package pep.patient.treatment.tbiassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

// What about TraumaticBrainInjuryAssessment??????????????????

public class TbiAssessment {
    private static Logger logger = Logger.getLogger(TbiAssessment.class.getName()); // watch out for duplication or recursion
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    // fix these. they are for demo
    private static By ssnField = By.id("ssn"); // now not only does demo fail, but also test if you pass do a search for a ssn
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//*[@id=\"search-form\"]/div[2]/button");
    private static By patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li"); // wrong, I'd guess.

    private static By patientDemographicsSectionBy = By.id("patient-demographics-container");
    // This is for demo but also seems to work for gold
    //By patientTreatmentTabBy = By.xpath("//*[@id=\"i4200\"]/span"); // fix to match tbi not bh
    private static By patientTreatmentTabBy = By.xpath("//li/a[@href='/tmds/patientTreatment.html']");
    //By tbiAssessmentsLinkBy = By.id("a_2");
    private static By tbiAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/tbiAssessments.html']");
    //private static By uploadANewFileTabBy = By.id("tabAttachmentsForm:FileUpload_lbl");
    private static By uploadANewFileTabBy = By.xpath("//*[@id=\"uploadTab\"]/a");

    public TbiAssessment() {
        if (Arguments.template) {
            //this.random = null;
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li");
            patientDemographicsSectionBy = By.id("demoTab");
            tbiAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/tbi/tbiAssessments.seam']");
            uploadANewFileTabBy = By.xpath("//*[@id=\"tabAttachmentsForm:FileUpload_lbl\"]");
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing TBI Assessment for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");

        Utilities.sleep(555); // just a guess to see if can cut down on error that tbiAssessmentsLinkBy fails
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, tbiAssessmentsLinkBy); // link fails?  Not clickable?
        //logger.fine("Navigated?: "+ navigated);
        if (!navigated) {
            return false; // Why the frac????  Fails:3
        }
        //(new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // valid here?  I guess so, I guess not:3

        // This one always takes a long time.  Why?  And even when found patient eventually, looks like didn't wait long enough
        boolean foundPatient = isPatientRegistered(patient);// Is this super slow? 4s As in Super super super slow?  30 sec or something?
        // The above seems to spin for a while and then return, but it's still spinning
        if (!foundPatient) {
            logger.fine("Can't Do TBI assessment if you don't have a patient that matches the SSN");
            return false; // fails: demo: 1
        }

        // Copied from BehavioralHealthAssessment.java.  Logic is bad with regard to doing random.  We'll want at least one of the following, I think.
        // But we've got the random sections separate.  Clean up later.
        boolean wantFirstOne = Utilities.random.nextBoolean();

        TbiAssessmentNote tbiAssessmentNote = this.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.random == null) { // Is this needed?
                tbiAssessmentNote.random = (this.random == null) ? false : this.random;
            }
            if (tbiAssessmentNote.shoot == null) { // Is this needed?
                tbiAssessmentNote.shoot = (this.shoot == null) ? false : this.shoot;
            }
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("      ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
            //return processSucceeded;
        }
        else {
            if (this.random && wantFirstOne) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.random = (this.random == null) ? false : this.random;
                tbiAssessmentNote.shoot = (this.shoot == null) ? false : this.shoot;
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient);
                if (!processSucceeded && !Arguments.quiet) System.err.println("      ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                //return processSucceeded;
            }
        }

        // why is this next section not in a FileUpload class???  Oh, this is a check before going to that class
        // Does this section make sense with all this random stuff?  Random file name?
        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null && fileUpload.fullFilePath != null && !fileUpload.fullFilePath.isEmpty()) {
            if (fileUpload.shoot == null) { // Is this needed?
                fileUpload.shoot = (this.shoot == null) ? false : this.shoot;
            }

            try {
                WebElement uploadANewFileTabElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(uploadANewFileTabBy));
                uploadANewFileTabElement.click(); // element not visible
            }
            catch (Exception e) {
                logger.severe("Couldn't get Upload a New File tab or click on it.  e: " + Utilities.getMessageFirstLine(e));
                return false;
            }



            boolean processSucceeded = fileUpload.process(patient);
            if (!processSucceeded) {
                //nErrors++;
                if (!Arguments.quiet)
                    System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                return false;
            }
        }


        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true; // I know strange
    }

    // This is copied from BehavioralHealthAssessment.java
    boolean isPatientRegistered(Patient patient) {
        try {
            logger.finer("TbiAssessment.isPatientRegistered(), will now wait for ssn field to be visible");
            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnField));
            logger.finer("TbiAssessment.isPatientRegistered(), waited for ssn field to be visible");
        }
        catch (Exception e) {
            logger.severe("TbiAssessment.isPatientRegistered(), could not find ssn field");
            // now what?  Return false?
        }
        try {
            logger.finer("TbiAssessment.isPatientRegistered(), will try to fill in ssnField");
            Utilities.fillInTextField(ssnField, patient.patientSearch.ssn); // should check for existence
            logger.finer("TbiAssessment.isPatientRegistered(), will try to fill in lastNameField");
            Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
            logger.finer("TbiAssessment.isPatientRegistered(), will try to fill in firstNameField");
            Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
            logger.finer("TbiAssessment.isPatientRegistered(), will try to fill in traumaReg");
            Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
        }
        catch (Exception e) {
            logger.severe("TbiAssessment.isPatientRegistered(), could not fill in one or more fields.  e: " + Utilities.getMessageFirstLine(e));
            // now what?  return false?
            return false;  // new 11/19/18
        }
        // why do we not get the button first and then call click on it?
        Utilities.clickButton(searchForPatientButton); // ajax.  We expect to see "Behavioral Health Assessments" if patient found.  No message area unless not found
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1

        By patientSearchMsgsBy = By.xpath("//*[@id=\"j_id402\"]/table/tbody/tr/td/span"); // new demo
        try {
            WebElement patientSearchMsgsSpan = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(patientSearchMsgsBy)); // fails, which is okay
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
            //logger.fine("TbiAssessment.isPatientRegistered(), no message found, so prob okay.  Continue.");
            //return false;
        }

        // Just to check that we did get to the page we expected, check for a portion of that page.
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(patientDemographicsSectionBy));
        }
        catch (TimeoutException e) {
            logger.fine("Looks like didn't get the Behavioral Health Assessments page after the search: " + Utilities.getMessageFirstLine(e));
            return false; // fails: demo: 2
        }
        return true;
    }
}
