package pep.patient.summary;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;

import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import javax.xml.xpath.XPath;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

// Summary is navigated to via the nav bar, by clicking on "Patient Summary" which is //a[@href='/bm-app/patientSummary.html']
// and then finding a patient through the Patient Search thing.  Then on that Patient Summary page there are three things
// a user might click on in order to create more info to add to the patient: "Facility Treatment History - Create Note",
// TBI Assessments - Create Note", and "Upload a New File".
// The first Create Note is very much like BehavioralHealthNote, except it has some additional radio buttons at the bottom.
// The second Create Note looks identical to TbiAssessmentNote or TbiAssessmentNote, but note those were slightly different in how they worked.
// The "Upload a New File" looks exactly like the others.
//
// So essentially, Summary is related to or should be under Patient Treatments, but it isn't under Treatment.
// There are 3 areas PeP deals with: Registration, Treatment, and Summary.  And the JSON file looks like this:
// { "patients":[{"patientSearch":{},"registration":{},"treatments":[],"summaries":[]}]}
// Even though the stuff in a Summary is stuff related to treatments, it's separate, partially because of the name "Summary".
//
// Note there's no need to have the "Assessment" layer.  We don't need to go from Patient Summary to BehavioralHealthAssessment to BehavioralHealthNote.
// So we can skip that middle layer.
//
// Summary would be similar to Treatment (or Registration), except that its children would be a BehavioralHealthNote and TbiAssessmentNote
// and skip the middle "Assessment" layer.
//
// So this will be patterned after Treatment, and rip out the stuff not needed
//
// There can be an array of these in an input JSON encounters file
public class Summary {

    private static Logger logger = Logger.getLogger(Summary.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public FacilityTreatmentHistoryNote facilityTreatmentHistoryNote;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;
    // next line is often problematic
//    private static By patientSummaryTabBy = By.xpath("//li/a[@href='/bm-app/patientSummary.html']");
    private static By patientSummaryTabBy = By.cssSelector("a[href='/bm-app/patientSummary.html']");
    private static By ssnField = By.id("ssn"); // now not only does demo fail, but also test if you pass do a search for a ssn
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//button[text()='Search For Patient']"); // works
    //private static By patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li"); // wrong, I'd guess.

    private static By patientDemographicsSectionBy = By.id("patient-demographics-tab");
//    private static By uploadANewFileTabBy = By.xpath("//*[@id=\"uploadTab\"]/a"); // By.linkText
    private static By uploadANewFileTabBy = By.linkText("Upload a New File");
//    private static By uploadANewFileTabBy = By.linkText("//*[@id=\"uploadTab\"]/a"); // By.linkText
//    private static By patientSearchMsgsBy = By.xpath("//*[@id=\"j_id402\"]/table/tbody/tr/td/span"); // new demo
    private static By patientSearchMsgsBy = By.className("warntext");
    //private static By searchForPatientBy = By.xpath("//button[text()='Search For Patient']"); // 1/29/19

    public Summary() {
        if (Arguments.template) {
            //this.random = null;
            this.facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            patientSummaryTabBy = By.xpath("//li/a[@href='/bm-app/summary/patientSummary.seam']");
            //patientSummaryTabBy = By.xpath("//*[@id=\"nav\"]/li[3]/a/span");
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            patientDemographicsSectionBy = By.id("demographicsTabPanel");
            uploadANewFileTabBy = By.id("tabAttachmentsForm:FileUpload_lbl");
        }
    }

    // This method seems way too long
    public boolean process(Patient patient, Summary summary) {  // this is weird
        if (!Arguments.quiet) System.out.println("  Processing Patient Summary for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");

        if (summary.random == null) { // nec?  Hopefully not any more.
            summary.random = patient.random; // right?
        }
        if (summary.shoot == null) { // nec?  Hopefully not any more.
            summary.shoot = patient.shoot; // right?
        }

        // experiment.  Want here?
        // There's a problem here, I think.  It gets the wrong thing?  Says couldn't access link by using that xpath
        boolean navigated = Utilities.myNavigate(patientSummaryTabBy); // speed problem here?
        if (!navigated) {
            return false;
        }
        boolean foundPatient = isPatientRegistered(patient);
        // The above seems to spin for a while and then return, but it's still spinning
        if (!foundPatient) {
            logger.fine("Can't Do TBI assessment if you don't have a patient that matches the SSN");
            return false; // fails: demo: 1
        }

        boolean wantFirstOne = Utilities.random.nextBoolean();

        // Not sure of the value of doing this random stuff, where Summary is specified as random, but
        // neither of the two/three sections are provided.  That is, there's no facilityTreatmentHistoryNote section
        // or no tbiAssessmentNote section.  (Or fileUpload section.)
        //
        // This kind of logic maybe should also be in Registration, where if Registration is marked random
        // and there are no subsections like NewPatientReg, or PatientInformation, then some of the time one set
        // is done, and another time another set is done.  1: PreRegistration, PreRegistrationArrivals.  2: New Patient Reg
        // 3: #1 then Update Patient.  4: #2 then Update Patient.  5: #1 then Patient Information, etc, etc.
        //
        // At this point summary should not be null.  It may be essentially empty though, with "random:true"
        // I think this next percentage stuff is only used if the subsections are missing
        // and summary random:true
        boolean doFacilityTreatmentHistoryNote = false, doPsTbiAssessmentNote = false, doFileUpload = false;
        if ((summary.random != null && summary.random)) { // new
            int percent = Utilities.random.nextInt(100);

            if (percent > 75) {
                doFacilityTreatmentHistoryNote = true;
            }
            if (percent > 80) {
                doPsTbiAssessmentNote = true;
            }
            if (percent > 90) {
                doFileUpload = true;
            }
            if (!doFacilityTreatmentHistoryNote && !doPsTbiAssessmentNote && !doFileUpload) {
                doFacilityTreatmentHistoryNote = true;
            }
        }
        if (summary.facilityTreatmentHistoryNote != null) {
            doFacilityTreatmentHistoryNote = true;
        }
        if (summary.tbiAssessmentNote != null) {
            doPsTbiAssessmentNote = true;
        }
        if (summary.fileUpload != null) {
            doFileUpload = true;
        }


        int nErrors = 0;
        FacilityTreatmentHistoryNote facilityTreatmentHistoryNote = summary.facilityTreatmentHistoryNote;
        if (facilityTreatmentHistoryNote != null) { // fix this logic.  Maybe no random and no value, so just skip out
            if (facilityTreatmentHistoryNote.random == null) { // Is this needed?
                facilityTreatmentHistoryNote.random = summary.random;
            }
            if (facilityTreatmentHistoryNote.shoot == null) { // Is this needed?
                facilityTreatmentHistoryNote.shoot = summary.shoot;
            }
            // should we click on the link before calling process?  I kinda think so, to establish a pattern, but in this case it's probably no biggie
            logger.finest("Here comes a call to facilityTreatmetHistory.process.  Are we ready?");
            boolean processSucceeded = facilityTreatmentHistoryNote.process(patient); // does patient have the right SSN?  Inside can't continue because can't find the patient
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("      ***Failed to process Patient Summary Facility Treatment History Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((summary.random != null && summary.random) && doFacilityTreatmentHistoryNote) {
                facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
                facilityTreatmentHistoryNote.random = summary.random;
                facilityTreatmentHistoryNote.shoot = summary.shoot;
                summary.facilityTreatmentHistoryNote = facilityTreatmentHistoryNote;
                boolean processSucceeded = facilityTreatmentHistoryNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary Facility Treatment History Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        TbiAssessmentNote tbiAssessmentNote = summary.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.random == null) { // Is this needed?
                tbiAssessmentNote.random = summary.random;
            }
            if (tbiAssessmentNote.shoot == null) { // Is this needed?
                tbiAssessmentNote.shoot = summary.shoot;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((summary.random != null && summary.random) && doPsTbiAssessmentNote) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.random = summary.random;
                tbiAssessmentNote.shoot = summary.shoot;
                summary.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient); // still kinda weird passing in summary
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        FileUpload fileUpload = summary.fileUpload;
        if (fileUpload != null) {
            if (fileUpload.random == null) { // Is this needed?
                fileUpload.random = summary.random;
            }
            if (fileUpload.shoot == null) { // Is this needed?
                fileUpload.shoot = summary.shoot;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a

            // hey nav to the FileUpload page from here, not go there and then nav
            //Driver.driver.findElement(uploadANewFileTabBy).click();
            try {
                WebElement fileUploadTab = Utilities.waitForVisibility(uploadANewFileTabBy, 3, "Summary.process()");
                fileUploadTab.click();
            }
            catch (Exception e) {
                logger.severe("Summary.process(), couldn't find or click on file upload tab.");
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process File Upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);

            }

            boolean processSucceeded = fileUpload.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment file upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((summary.random != null && summary.random) && doFileUpload) {
                fileUpload = new FileUpload();
                fileUpload.random = summary.random;
                fileUpload.shoot = summary.shoot;
                summary.fileUpload = fileUpload;
                // NO, NO, NO, don't nav there, do it here first.

                try {
                    WebElement uploadANewFileTabElement = Utilities.waitForVisibility(uploadANewFileTabBy, 5, "Summary.() upload file tab");
                    uploadANewFileTabElement.click(); // element not visible
                }
                catch (Exception e) {
                    logger.severe("Couldn't get Upload a New File tab or click on it.  e: " + Utilities.getMessageFirstLine(e));
                    return false;
                }




                boolean processSucceeded = fileUpload.process(patient); // still kinda weird passing in summary
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary file upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        if (nErrors > 0) {
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "Summary, requested sleep for page.");
        }
        return true; // huh?  Not affected by processSucceeded results?

    }


    boolean isPatientRegistered(Patient patient) {
        Utilities.sleep(1000, ".isPatientRegistered()"); // desperate attempt.  Remove later when have sol'n
        // This next stuff won't work if timing is off, especially if a new patient was just created.  So we wait for something.
        // Waiting for SSN text field to be able to take a value doesn't work.  Waiting for button clickability does.
        try {
            Utilities.waitForClickability(searchForPatientButton, 3, "Summary.process() waiting for clickability which should indicate we can enter values into the fields");
        }
        catch (Exception e) {
            logger.severe("Summary.isPatientRegistered(), Couldn't get search button.  Continue on or return false? e: " + Utilities.getMessageFirstLine(e));
        }
        try {
            // ready for this to be filled in?
            logger.finer("Summary.isPatientRegistered(), will try to fill in ssnField");
            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn); // should check for existence
            logger.finer("Summary.isPatientRegistered(), will try to fill in lastNameField");
            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
            logger.finer("Summary.isPatientRegistered(), will try to fill in firstNameField");
            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
            logger.finer("Summary.isPatientRegistered(), will try to fill in traumaReg");
            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
            //System.out.println("\t\tDid any fields get filled in?  I doubt it unless we wait before.");
        }
        catch (Exception e) {
            logger.severe("Summary.isPatientRegistered(), could not fill in one or more fields.  e: " + Utilities.getMessageFirstLine(e));
            // now what?  return false?
            return false;  // new 11/19/18
        }
        Utilities.clickButton(searchForPatientButton); // ajax.  We expect to see "Behavioral Health Assessments" if patient found.  No message area unless not found
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1


        // following must be changed at some time.  For Spring code it looks like there is no message provided when find patient.
        try {
            WebElement patientSearchMsgsSpan = Utilities.waitForPresence(patientSearchMsgsBy, 3, "Summary.isPatientRegistered()"); // fails, which is okay
            String searchMessage = patientSearchMsgsSpan.getText();
            if (!searchMessage.isEmpty()) {
                logger.fine("BehavioralHealthAssessment.isPatientRegistered(), got a message back: " + searchMessage);
                if (searchMessage.equalsIgnoreCase("There are no patients found.")) {
                    return false;
                }
                return false;
            }
            else {
                logger.fine("Search message area was blank, which probably means we found the patient.  Can probably just return true here.");
            }
        }
        catch (Exception e) {
            // It's possible there are no messages, in which case we can probably assume a patient was found, and so we continue.
            logger.finest("Summary.isPatientRegistered(), no message found, so prob okay.  Continue.");
            //return false;
        }

        // Just to check that we did get to the page we expected, check for a portion of that page.
        try {
            //By patientDemographicsTabBy = By.xpath("//div[@id='patient-demographics-tab']/a[text()='Patient Demographics']");
            //Utilities.waitForVisibility(patientDemographicsSectionBy, 15, "Summary.isPatientRegistered()"); // was 10
            By patientDemographicsTabBy = By.linkText("Patient Demographics");
            Utilities.waitForVisibility(patientDemographicsTabBy, 15, "Summary.isPatientRegistered()"); // was 10
        }
        catch (TimeoutException e) {
            logger.severe("Looks like didn't get the Behavioral Health Assessments page after the search: " + Utilities.getMessageFirstLine(e));
            return false; // fails: demo: 2
        }
        return true;
    }

}