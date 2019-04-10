package pep.patient.summary;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;

import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;


/**
 * This class handles the Summary page.
 *
 * Summary is navigated to via the nav bar, by clicking on "Patient Summary" which is //a[@href='/bm-app/patientSummary.html']
 * and then finding a patient through the Patient Search thing.  Then on that Patient Summary page there are three things
 * a user might click on in order to create more info to add to the patient: "Facility Treatment History - Create Note",
 * TBI Assessments - Create Note", and "Upload a New File".
 *
 * The first Create Note is very much like BehavioralHealthNote, except it has some additional radio buttons at the bottom.
 *
 * The second Create Note looks identical to TbiAssessmentNote or TbiAssessmentNote, but note those were slightly different in how they worked.
 *
 * The "Upload a New File" looks exactly like the others.
 *
 * There can be an array of these in an input JSON encounters file
 */
public class Summary {
    private static Logger logger = Logger.getLogger(Summary.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    private FacilityTreatmentHistoryNote facilityTreatmentHistoryNote;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    private static By patientSummaryTabBy = By.cssSelector("a[href='/bm-app/patientSummary.html']");
    private static By ssnField = By.id("ssn");
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//button[text()='Search For Patient']");
    private static By uploadANewFileTabBy = By.linkText("Upload a New File");
    private static By patientSearchMsgsBy = By.className("warntext");
    private static By patientDemographicsTabBy = By.linkText("Patient Demographics");

    public Summary() {
        if (Arguments.template) {
            this.facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            patientSummaryTabBy = By.xpath("//li/a[@href='/bm-app/summary/patientSummary.seam']");
            ssnField = By.id("patientSearchSsn");
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            uploadANewFileTabBy = By.id("tabAttachmentsForm:FileUpload_lbl");
        }
    }

    /**
     * Because Summary is composed of three main parts, Facility Treatment History Note, TBI Assessment Note,
     * and File Upload, and this method handles them in turn by calling their process() methods.
     * @param patient The patient this Summary is for
     * @return Success or Failure at processing the parts
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("  Processing Patient Summary at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");
        if (this.randomizeSection == null) {
            this.randomizeSection = patient.randomizeSection;
        }
        if (this.shoot == null) {
            this.shoot = patient.shoot;
        }
        boolean navigated = Utilities.myNavigate(patientSummaryTabBy);
        if (!navigated) {
            return false;
        }
//        boolean foundPatient = isPatientFound(patient);
        boolean foundPatient = Utilities.isPatientFound(patient);
        if (!foundPatient) {
            logger.fine("Can't Do TBI assessment if you don't have a patient that matches the SSN");
            return false;
        }
        //
        // Account for the possibility that this section was marked to be randomized.
        //
        // logic of following should be examined closer
        boolean doFacilityTreatmentHistoryNote = false, doPsTbiAssessmentNote = false, doFileUpload = false;
        if ((this.randomizeSection != null && this.randomizeSection)) {
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
        if (this.facilityTreatmentHistoryNote != null) {
            doFacilityTreatmentHistoryNote = true;
        }
        if (this.tbiAssessmentNote != null) {
            doPsTbiAssessmentNote = true;
        }
        if (this.fileUpload != null) {
            doFileUpload = true;
        }


        int nErrors = 0;
        //
        // Handle Facility Treatment Note part of this Summary page.
        //
        FacilityTreatmentHistoryNote facilityTreatmentHistoryNote = this.facilityTreatmentHistoryNote;
        if (facilityTreatmentHistoryNote != null) {
            if (facilityTreatmentHistoryNote.randomizeSection == null) {
                facilityTreatmentHistoryNote.randomizeSection = this.randomizeSection;
            }
            if (facilityTreatmentHistoryNote.shoot == null) {
                facilityTreatmentHistoryNote.shoot = this.shoot;
            }
            logger.finest("Here comes a call to facilityTreatmetHistory.process.  Are we ready?");
            boolean processSucceeded = facilityTreatmentHistoryNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("      ***Failed to process Patient Summary Facility Treatment History Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((this.randomizeSection != null && this.randomizeSection) && doFacilityTreatmentHistoryNote) {
                facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
                facilityTreatmentHistoryNote.randomizeSection = this.randomizeSection;
                facilityTreatmentHistoryNote.shoot = this.shoot;
                this.facilityTreatmentHistoryNote = facilityTreatmentHistoryNote;
                boolean processSucceeded = facilityTreatmentHistoryNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary Facility Treatment History Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Handle TBI Assessment Note part of this Summary page.
        //
        TbiAssessmentNote tbiAssessmentNote = this.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.randomizeSection == null) {
                tbiAssessmentNote.randomizeSection = this.randomizeSection;
            }
            if (tbiAssessmentNote.shoot == null) { // Is this needed?
                tbiAssessmentNote.shoot = this.shoot;
            }
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((this.randomizeSection != null && this.randomizeSection) && doPsTbiAssessmentNote) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.randomizeSection = this.randomizeSection;
                tbiAssessmentNote.shoot = this.shoot;
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient); // still kinda weird passing in summary
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Handle File Upload part of this Summary page.
        //
        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null) {
            if (fileUpload.randomizeSection == null) {
                fileUpload.randomizeSection = this.randomizeSection;
            }
            if (fileUpload.shoot == null) {
                fileUpload.shoot = this.shoot;
            }
            try {
                WebElement fileUploadTab = Utilities.waitForVisibility(uploadANewFileTabBy, 3, "Summary.process()");
                fileUploadTab.click();
            }
            catch (Exception e) {
                logger.severe("Summary.process(), couldn't find or click on file upload tab."); ScreenShot.shoot("SevereError");
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
            if ((this.randomizeSection != null && this.randomizeSection) && doFileUpload) {
                fileUpload = new FileUpload();
                fileUpload.randomizeSection = this.randomizeSection;
                fileUpload.shoot = this.shoot;
                this.fileUpload = fileUpload;
                try {
                    WebElement uploadANewFileTabElement = Utilities.waitForVisibility(uploadANewFileTabBy, 5, "Summary.process() upload file tab");
                    uploadANewFileTabElement.click();
                }
                catch (Exception e) {
                    logger.severe("Couldn't get Upload a New File tab or click on it.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                    return false;
                }
                boolean processSucceeded = fileUpload.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary file upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Take screenshot if desired, and return with status.
        //
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "Summary, requested sleep for page.");
        }
        return (nErrors == 0);
    }


//    /**
//     * Determine if a particular patient has been registered and can therefore be found when doing a search.
//     * There are currently 4 different methods with this name.  Perhaps they could be consolidated and put into Utilities.
//     * @param patient The patient to search for
//     * @return true if found, false otherwise
//     */
//    boolean isPatientFound(Patient patient) {
//        Utilities.sleep(1555, "Summary.isPatientFound()");
//        //
//        // Check for a search button.  Then try to fill in the search fields, and if can do that, then click on the button.
//        //
//        try {
//            Utilities.waitForClickability(searchForPatientButton, 3, "Summary.process() waiting for clickability which should indicate we can enter values into the fields");
//        }
//        catch (Exception e) {
//            logger.severe("Summary.isPatientFound(), Couldn't get search button.  Continue on or return false? e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
//        }
//        try {
//            // ready for this to be filled in?
//            logger.finer("Summary.isPatientFound(), will try to fill in ssnField");
//            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn);
//            logger.finer("Summary.isPatientFound(), will try to fill in lastNameField");
//            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
//            logger.finer("Summary.isPatientFound(), will try to fill in firstNameField");
//            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
//            logger.finer("Summary.isPatientFound(), will try to fill in traumaReg");
//            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
//        }
//        catch (Exception e) {
//            logger.severe("Summary.isPatientFound(), could not fill in one or more fields.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
//            return false;
//        }
//        Utilities.clickButton(searchForPatientButton);
//        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());
//        //
//        // Check for message from the search.  Possible there's no message when the patient is found.  This could be improved so as to not waste time.
//        //
//        try {
//            WebElement patientSearchMsgsSpan = Utilities.waitForPresence(patientSearchMsgsBy, 3, "Summary.isPatientFound()"); // fails, which is okay
//            String searchMessage = patientSearchMsgsSpan.getText();
//            if (!searchMessage.isEmpty()) {
//                logger.fine("BehavioralHealthAssessment.isPatientFound(), got a message back: " + searchMessage);
//                if (searchMessage.equalsIgnoreCase("There are no patients found.")) {
//                    return false;
//                }
//                return false;
//            }
//            else {
//                logger.fine("Search message area was blank, which probably means we found the patient.  Can probably just return true here.");
//            }
//        }
//        catch (Exception e) {
//            logger.finest("Summary.isPatientFound(), no message found, so prob okay.  Continue.");
//        }
//
//        try {
//            Utilities.waitForVisibility(patientDemographicsTabBy, 15, "Summary.isPatientFound()");
//        }
//        catch (TimeoutException e) {
//            logger.severe("Looks like didn't get the Behavioral Health Assessments page after the search: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
//            return false;
//        }
//        return true;
//    }

}