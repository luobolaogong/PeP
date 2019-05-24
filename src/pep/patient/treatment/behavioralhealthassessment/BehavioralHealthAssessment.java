package pep.patient.treatment.behavioralhealthassessment;

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
import static pep.utilities.Utilities.getMessageFirstLine;

/**
 * This class takes care of the different parts of what makes a Behavioral Health Assessment: Behavioral Health Note,
 * TBI Assessment Note, and File Upload.
 */
public class BehavioralHealthAssessment {
    private static Logger logger = Logger.getLogger(BehavioralHealthAssessment.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public BehavioralHealthNote behavioralHealthNote;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    private static By patientTreatmentTabBy = By.cssSelector("a[href='/tmds/patientTreatment.html']");
    private static By behavioralHealthLinkBy = By.cssSelector("a[href='/bm-app/behavioralHealth.html']");
    private static By bhAssessmentsLinkBy = By.cssSelector("a[href='/bm-app/behavioralHealthAssessments.html']");
    private static By ssnField = By.id("ssn");
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//button[text()='Search For Patient']"); // right?
    private static By patientDemographicsSectionBy = By.id("patient-demographics-container");
    private static By patientSearchMsgsBy = By.id("msg");
    private static By uploadANewFileTabBy = By.linkText("Upload a New File"); // works?

    public BehavioralHealthAssessment() {
        if (Arguments.template) {
            this.behavioralHealthNote = new BehavioralHealthNote();
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn");
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButtonBy = By.id("patientSearchGo");
            patientDemographicsSectionBy = By.id("j_id331");
            patientSearchMsgsBy = By.id("patientSearchMsgs");
            bhAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/bh/behavioralHealthAssessments.seam']");
            uploadANewFileTabBy = By.xpath("//*[@id=\"tabAttachmentsForm:FileUpload_lbl\"]");
        }
    }

    /**
     * Handle the different parts of Behavioral Health Assessment, by calling their process() methods.
     * @param patient The patient for the asseessment
     * @return Success or Failure for the parts as a whole
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Behavioral Health Assessment at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        // Next line has caused a "You Have Encountered a Problem" page
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, behavioralHealthLinkBy, bhAssessmentsLinkBy);
        if (!navigated) {
            return false; // fails: 1
        }

        logger.finest("BehavioralHealthAssessment.process(), gunna try isFinishedAjax");
        logger.finest("BehavioralHealthAssessment.process(), was there isFinishedAjax?");
        boolean foundPatient = isPatientRegistered(patient);
        if (!foundPatient) {
            logger.fine("Can't Do BHA for a patient if can't find the patient.");
            logger.fine("Was looking for patient " + patient.patientSearch.firstName
                    + " " +    patient.patientSearch.lastName
                    + " " + patient.patientSearch.ssn
                    + " " +     patient.patientSearch.traumaRegisterNumber);

            logger.severe("In BehavioralHealthAssessment.process(), failed to find patient, returning false"); ScreenShot.shoot("SevereError");
            return false;
        }

        boolean wantFirstOne = Utilities.random.nextBoolean();
        int nErrors = 0;
        //
        // Handle behavioral health note
        //
        BehavioralHealthNote behavioralHealthNote = this.behavioralHealthNote;
        if (behavioralHealthNote != null) {
            if (behavioralHealthNote.randomizeSection == null) {
                behavioralHealthNote.randomizeSection = this.randomizeSection;
            }
            if (behavioralHealthNote.shoot == null) { // Is this needed?
                behavioralHealthNote.shoot = this.shoot;
            }
            if (behavioralHealthNote.skipSave == null) { // Is this needed?
                behavioralHealthNote.skipSave = this.skipSave;
            }
            boolean processSucceeded = behavioralHealthNote.process(patient);
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
            if ((this.randomizeSection != null && this.randomizeSection) && wantFirstOne) {
                behavioralHealthNote = new BehavioralHealthNote();
                behavioralHealthNote.randomizeSection = this.randomizeSection;
                behavioralHealthNote.shoot = this.shoot;
                behavioralHealthNote.skipSave = this.skipSave;
                this.behavioralHealthNote = behavioralHealthNote;
                boolean processSucceeded = behavioralHealthNote.process(patient);
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
        //
        // Handle TBI Assessment Note
        //
        TbiAssessmentNote tbiAssessmentNote = this.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.randomizeSection == null) {
                tbiAssessmentNote.randomizeSection = this.randomizeSection;
            }
            if (tbiAssessmentNote.shoot == null) { // Is this needed?
                tbiAssessmentNote.shoot = this.shoot;
            }
            if (tbiAssessmentNote.skipSave == null) { // Is this needed?
                tbiAssessmentNote.skipSave = this.skipSave;
            }
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (Arguments.verbose)
                    System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if (this.randomizeSection != null && this.randomizeSection && !wantFirstOne) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.randomizeSection = this.randomizeSection;
                tbiAssessmentNote.shoot = this.shoot;
                tbiAssessmentNote.skipSave = this.skipSave;
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose)
                        System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Handle File Upload
        //
        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null && fileUpload.fullFilePath != null && !fileUpload.fullFilePath.isEmpty()) {
            if (fileUpload.shoot == null) { // Is this needed?
                fileUpload.shoot = this.shoot;
            }
            if (fileUpload.skipSave == null) { // Is this needed?
                fileUpload.skipSave = this.skipSave;
            }
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

    /**
     * See if the patient is registered.
     * There's no good reason for making this method different in signature than the other isPatientRegistered methods
     * @param patient The patient to search for
     * @return True is the patient is registered and found
     */
    boolean isPatientRegistered(Patient patient) {
        Utilities.sleep(2000, "BehavioralHealthAssessment.isPatientRegistered()"); // desperate attempt.  Remove later when have sol'n
        try {
            Utilities.waitForPresence(ssnField, 3, "\"BehavioralHealthAssessment.isPatientRegistered()");
        }
        catch (Exception e) {
            logger.warning("BehavioralHealthAssessment.isPatientRegistered(), What happened to presence of ssnField? e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        try {
            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn);
            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
        }
        catch (Exception e) {
            logger.warning("BehavioralHealthAssessment.isPatientRegistered(), couldn't fill in fields for search, I guess.  message: " + getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        Utilities.clickButton(searchForPatientButtonBy);
        logger.finest("BehavioralHealthAssessment.isPatientregistered(), gunna wait for isFinishedAjax");
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());
        logger.finest("BehavioralHealthAssessment.isPatientregistered(), waited for isFinishedAjax");

        try {
            WebElement patientSearchMsgsSpan = Utilities.waitForPresence(patientSearchMsgsBy, 3, "BehavioralHealthAssessment.isPatientRegistered()"); // fails, which is okay
            String searchMessage = patientSearchMsgsSpan.getText();
            if (!searchMessage.isEmpty()) {
                logger.fine("BehavioralHealthAssessment.isPatientRegistered(), got a message back: " + searchMessage);
                return false;
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
