package pep.patient.treatment.tbiassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

/**
 * This class holds the TBI assessment note and file upload section
 */
public class TbiAssessment {
    private static Logger logger = Logger.getLogger(TbiAssessment.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    private static By ssnField = By.id("ssn");
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//button[text()='Search For Patient']"); // works

    private static By patientDemographicsSectionBy = By.id("patient-demographics-container");
    private static By patientTreatmentTabBy = By.cssSelector("a[href='/tmds/patientTreatment.html']");
    private static By tbiAssessmentsLinkBy = By.cssSelector("a[href='/bm-app/tbiAssessments.html']");
    private static By uploadANewFileTabBy = By.linkText("Upload a New File");
    private static By patientSearchMsgsBy = By.className("warntext"); // guess.  right? "There were no records found."

    public TbiAssessment() {
        if (Arguments.template) {
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            patientDemographicsSectionBy = By.id("demoTab");
            tbiAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/tbi/tbiAssessments.seam']");
            uploadANewFileTabBy = By.xpath("//*[@id='tabAttachmentsForm:FileUpload_lbl']");
        }
    }

    /**
     * Process the TBI assessment section, which is composed of the note and file upload.  The actual processing is the note is done in TbiAssessmentNote.
     * @param patient The patient for which the note applies
     * @return success or failure
     */
    public boolean process(Patient patient) {
        int nErrors = 0;
        if (!Arguments.quiet) System.out.println("    Processing TBI Assessment for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");

        Utilities.sleep(555, "TmdsPortal"); // just a guess to see if can cut down on error that tbiAssessmentsLinkBy fails
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, tbiAssessmentsLinkBy); // link fails?  Not clickable?
        if (!navigated) {
            return false; // Fails:3
        }

        // Timing issues here.  Following seems to spin for a while then return, but still spinning.  Super slow?
        boolean foundPatient = isPatientFound(patient);
//        boolean foundPatient = Utilities.isPatientFound(patient);
        if (!foundPatient) {
            logger.warning("Can't Do TBI assessment if you don't have a patient that matches the SSN");
            return false; // fails: demo: 1
        }
        //
        // Process the note
        //
        TbiAssessmentNote tbiAssessmentNote = this.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.randomizeSection == null) {
                tbiAssessmentNote.randomizeSection = this.randomizeSection;
            }
            if (tbiAssessmentNote.shoot == null) {
                tbiAssessmentNote.shoot = this.shoot;
            }
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose) System.err.println("      ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            boolean wantFirstOne = Utilities.random.nextBoolean();
            if ((this.randomizeSection != null && this.randomizeSection == true) && wantFirstOne) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.randomizeSection = this.randomizeSection; // removed setting to false if null
                tbiAssessmentNote.shoot = this.shoot;
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet) System.err.println("      ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        //
        // Process the file upload.
        //
        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null && fileUpload.fullFilePath != null && !fileUpload.fullFilePath.isEmpty()) {
            if (fileUpload.shoot == null) { // Is this needed?
                fileUpload.shoot = this.shoot;
            }

            try {
                WebElement uploadANewFileTabElement = Utilities.waitForVisibility(uploadANewFileTabBy, 5, "TbiAssessment.process()");
                uploadANewFileTabElement.click(); // element not visible
            }
            catch (Exception e) {
                logger.severe("Couldn't get Upload a New File tab or click on it.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }

            boolean processSucceeded = fileUpload.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet) System.err.println("      ***Failed to process BH TBI Assessment file upload for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }

        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "TbiAssessment, requested sleep for page.");
        }
        return (nErrors == 0);
    }

    // This is copied from BehavioralHealthAssessment.java

    /**
     * This method does a search for the patient.  If found then the patient was previously registered.
     * There are timing issues going on here that need attention.
     * There are currently 4 different methods with this name.  Perhaps they could be consolidated and put into Utilities.
     * @param patient The patient to search for
     * @return true if the patient had been registered and found.  False otherwise.
     */
    boolean isPatientFound(Patient patient) {
        Utilities.sleep(1555, "TbiAssessment.isPatientFound()"); // desperate attempt.  Remove later when have sol'n
        try {
            logger.finer("TbiAssessment.isPatientFound(), will now wait for ssn field to be visible");
            Utilities.waitForVisibility(ssnField, 5, "TbiAssessment.isPatientFound() waiting for visibility of ssn");
            logger.finer("TbiAssessment.isPatientFound(), waited for ssn field to be visible");
        }
        catch (Exception e) {
            logger.severe("TbiAssessment.isPatientFound(), could not find ssn field but will continue anyway, just in case."); ScreenShot.shoot("SevereError");
            // now what?  Return false?
        }
        try {
            logger.finer("TbiAssessment.isPatientFound(), will try to fill in ssnField");
            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn); // should check for existence
            logger.finer("TbiAssessment.isPatientFound(), will try to fill in lastNameField");
            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
            logger.finer("TbiAssessment.isPatientFound(), will try to fill in firstNameField");
            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
            logger.finer("TbiAssessment.isPatientFound(), will try to fill in traumaReg");
            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
        }
        catch (Exception e) {
            logger.severe("TbiAssessment.isPatientFound(), could not fill in one or more fields.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        // Perhaps it's the save of the patient registration that causes this need?
        Utilities.sleep(3155, "TbiAssessment.isPatientFound(), waiting before click Search For Patient button");
        Utilities.clickButton(searchForPatientButton); // ajax.  We expect to see "Behavioral Health Assessments" if patient found.  No message area unless not found
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1

        try { // this is a slow way to check for errors from the previous search.  We time out in 6 seconds if there was no error.  Dumb.  Fix this later to search for both possibilities and act on the first one
            WebElement patientSearchMsgsSpan = Utilities.waitForPresence(patientSearchMsgsBy, 4, "TbiAssessment.isPatientFound()"); // was 2, 1/25/19
            String searchMessage = patientSearchMsgsSpan.getText();
            if (!searchMessage.isEmpty()) {
                logger.fine("BehavioralHealthAssessment.isPatientFound(), got a message back: " + searchMessage);
                if (searchMessage.equalsIgnoreCase("There are no patients found.")) { // why put this here?
                    return false;
                }
                if (searchMessage.equalsIgnoreCase("There were no records found.")) { // why put this here?
                    return false;
                }
                return false;
            }
            else {
                logger.fine("Search message area was blank, which probably means we found the patient.  Can probably just return true here.");
            }
        }
        catch (Exception e) {
            logger.fine("TbiAssessment.isPatientFound(), no message found, so prob okay.  Continue.");
            //return false;
        }
        // This is strange.  Demographics here?
        // Just to check that we did get to the page we expected, check for a portion of that page.  What????????????????? demographics on TBI Assessment?????????
        try {
            Utilities.waitForVisibility(patientDemographicsSectionBy, 10, "TbiAssessment.isPatientFound()");
        }
        catch (TimeoutException e) {
            logger.fine("Looks like didn't get the Behavioral Health Assessments page after the search: " + Utilities.getMessageFirstLine(e));
            return false; // fails: demo: 2
        }
        return true;
    }
}
