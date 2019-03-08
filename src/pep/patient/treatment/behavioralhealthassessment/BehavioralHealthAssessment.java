package pep.patient.treatment.behavioralhealthassessment;

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

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Utilities.getMessageFirstLine;

public class BehavioralHealthAssessment {
    private static Logger logger = Logger.getLogger(BehavioralHealthAssessment.class.getName()); // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public BehavioralHealthNote behavioralHealthNote;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    private static By patientTreatmentTabBy = By.cssSelector("a[href='/tmds/patientTreatment.html']"); // By.linkText(???)

    private static By behavioralHealthLinkBy = By.cssSelector("a[href='/bm-app/behavioralHealth.html']");
    private static By bhAssessmentsLinkBy = By.cssSelector("a[href='/bm-app/behavioralHealthAssessments.html']");
    private static By ssnField = By.id("ssn"); // now not only does demo fail, but also test if you pass do a search for a ssn
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//button[text()='Search For Patient']"); // right?
    private static By patientDemographicsSectionBy = By.id("patient-demographics-container");

    private static By patientSearchMsgsBy = By.id("msg");
//    private static By uploadANewFileTabBy = By.cssSelector("a[text()='Upload a New File']"); // Prob doesn't work  By.linkText(???)
    private static By uploadANewFileTabBy = By.linkText("Upload a New File"); // works?

    public BehavioralHealthAssessment() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.behavioralHealthNote = new BehavioralHealthNote();
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButtonBy = By.id("patientSearchGo");
            //patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li");
            patientDemographicsSectionBy = By.id("j_id331"); // a section that has "Patient Demographics" tab and "Duplicate Patients" tab
            patientSearchMsgsBy = By.id("patientSearchMsgs");
            bhAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/bh/behavioralHealthAssessments.seam']");
            uploadANewFileTabBy = By.xpath("//*[@id=\"tabAttachmentsForm:FileUpload_lbl\"]");
        }
    }

    // change this to boolean success?
    // Behavioral Health Assessments page has a log of javascript in it that changes the visibility of components on the page.
    // It can be tricky.
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Behavioral Health Assessment for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        // Are we navigating to the right page for TEST tier?????????????????????????????????
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, behavioralHealthLinkBy, bhAssessmentsLinkBy);
        if (!navigated) {
            return false; //  Fails:3
        }

        // It seems we can get past the navigation without actually navigating fully.  True?  If so, why?
        logger.finest("BehavioralHealthAssessment.process(), gunna try isFinishedAjax");
        //(new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // valid here?  Mostly yes, sometimes no:2  // removed 11/23/18
        logger.finest("BehavioralHealthAssessment.process(), was there isFinishedAjax?");
        boolean foundPatient = isPatientRegistered(patient);// Wow, this does not wait for spinner to stop.  Gotta check this.  Coming back false a lot
//        // The above seems to spin for a while and then return, but it's still spinning
        if (!foundPatient) {
            logger.fine("Can't Do BHA for a patient if can't find the patient.");
            logger.fine("Was looking for patient " + patient.patientSearch.firstName
                    + " " +    patient.patientSearch.lastName
                    + " " + patient.patientSearch.ssn
                    + " " +     patient.patientSearch.traumaRegisterNumber);

            logger.severe("In BehavioralHealthAssessment.process(), failed to find patient, returning false"); ScreenShot.shoot("SevereError");
            return false;
        }

        // Logic is bad with regard to doing random.  We'll want at least one of the following, I think.
        // But we've got the random sections separate.  Clean up later.
        boolean wantFirstOne = Utilities.random.nextBoolean();

        int nErrors = 0;
        BehavioralHealthNote behavioralHealthNote = this.behavioralHealthNote;
        if (behavioralHealthNote != null) {
            if (behavioralHealthNote.random == null) { // Is this needed?
                behavioralHealthNote.random = this.random; // removed setting to false if null
            }
            if (behavioralHealthNote.shoot == null) { // Is this needed?
                behavioralHealthNote.shoot = this.shoot;
            }
            boolean processSucceeded = behavioralHealthNote.process(patient, this);
            if (!processSucceeded) {
                nErrors++;
                if (Arguments.verbose) System.err.println("      ***Failed to process Behavioral Health Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn))
                );

            }
        }
        else {
            if ((this.random != null && this.random) && wantFirstOne) {
                behavioralHealthNote = new BehavioralHealthNote();
                behavioralHealthNote.random = this.random; // removed setting to false if null
                behavioralHealthNote.shoot = this.shoot;
                this.behavioralHealthNote = behavioralHealthNote;
                boolean processSucceeded = behavioralHealthNote.process(patient, this);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("      ***Failed to process Behavioral Health Note for patient" +
                            (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                            (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                            (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn))
                    );
                }
            }
        }


        TbiAssessmentNote tbiAssessmentNote = this.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.random == null) { // Is this needed?
                tbiAssessmentNote.random = this.random; // removed setting to false if null
            }
            if (tbiAssessmentNote.shoot == null) { // Is this needed?
                tbiAssessmentNote.shoot = this.shoot;
            }
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (Arguments.verbose)
                    System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
            //return processSucceeded;
        }
        else {
            if (this.random != null && this.random && !wantFirstOne) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.random = this.random; // removed setting to false if null
                tbiAssessmentNote.shoot = this.shoot;
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient); // still kinda weird passing in treatment
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose)
                        System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
                //return processSucceeded;
            }
        }


        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null && fileUpload.fullFilePath != null && !fileUpload.fullFilePath.isEmpty()) {
            if (fileUpload.shoot == null) { // Is this needed?
                fileUpload.shoot = this.shoot;
            }

            // NO, NO, NO, nav there from here first.  Not in FileUpload

            try {
                WebElement uploadANewFileTabElement = Utilities.waitForVisibility(uploadANewFileTabBy, 5, "BehavioralHealthAssessment.process()");
                uploadANewFileTabElement.click(); // element not visible
            }
            catch (Exception e) {
                logger.severe("Couldn't get Upload a New File tab or click on it.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }





            boolean processSucceeded = fileUpload.process(patient);
            if (!processSucceeded) {
                //nErrors++;
                if (Arguments.verbose)
                    System.err.println("      ***Failed to process BH TBI Assessment Note file upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                return false;
            }
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "BehavioralHealthAssessment, requested sleep for page.");
        }
        return (nErrors == 0);
    }

    // Why isn't this done like the other one that has all 4 params?
    boolean isPatientRegistered(Patient patient) {
        Utilities.sleep(2000, "BehavioralHealthAssessment.isPatientRegistered()"); // desperate attempt.  Remove later when have sol'n
        try {
            Utilities.waitForPresence(ssnField, 3, "\"BehavioralHealthAssessment.isPatientRegistered()");
        }
        catch (Exception e) {
            logger.warning("BehavioralHealthAssessment.isPatientRegistered(), What happened to presence of ssnField? e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        try {
            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn); // gunna generate a stale element reference?
            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
        }
        catch (Exception e) {
            logger.warning("BehavioralHealthAssessment.isPatientRegistered(), couldn't fill in fields for search, I guess.  message: " + getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        // Why do we not get the button first and then click on it?   Wow, we got here fast after the last save!!!
        Utilities.clickButton(searchForPatientButtonBy); // ajax.  We expect to see "Behavioral Health Assessments" if patient found.  No message area unless not found
        logger.finest("BehavioralHealthAssessment.isPatientregistered(), gunna wait for isFinishedAjax");
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1
        logger.finest("BehavioralHealthAssessment.isPatientregistered(), waited for isFinishedAjax");

        // there could be an error from the search, but there shouldn't be if we'd done an initial search in Registration.  However, since now we can bypass
        // Registration pages and go straight to Treatments this may not have happened.  And so we could get a message like "There are no patients found."
        // If we wanted to check for errors, the area to look
        // would be the span with id "patientSearchMsgs"
        //*[@id="j_id286"]/table/tbody/tr/td/span   That one is for DEMO.  Not sure what it is in Gold at this time.
        try {
            WebElement patientSearchMsgsSpan = Utilities.waitForPresence(patientSearchMsgsBy, 3, "BehavioralHealthAssessment.isPatientRegistered()"); // fails, which is okay
            String searchMessage = patientSearchMsgsSpan.getText();
            if (!searchMessage.isEmpty()) { // I guess we assume that any message indicates an error.
                logger.fine("BehavioralHealthAssessment.isPatientRegistered(), got a message back: " + searchMessage);
                //if (searchMessage.equalsIgnoreCase("There are no patients found.")) {
                //    return false;
                //}
                return false; // huh?
            }
            else {
                logger.fine("Search message area was blank, which probably means we found the patient.  Can probably just return true here.");
            }
        }
        catch (Exception e) {
            logger.finest("BehavioralHealthAssessment.isPatientRegistered(), no message found, so prob okay.  Continue.");
            //return false;
        }

        // Just to check that we did get to the page we expected, check for a portion of that page.
        try {
            Utilities.waitForVisibility(patientDemographicsSectionBy, 10, "\"BehavioralHealthAssessment.isPatientRegistered()");
        }
        catch (TimeoutException e) {
            logger.finest("Is this true?: Looks like didn't get the Behavioral Health Assessments page after the search: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        return true;
    }
}
