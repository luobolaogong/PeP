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


    public TbiAssessment() {
        if (Arguments.template) {
            //this.random = null;
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li");
            patientDemographicsSectionBy = By.id("demoTab");
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing TBI Assessment for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");

        // This is for demo but also seems to work for gold
        //By patientTreatmentTabBy = By.xpath("//*[@id=\"i4200\"]/span"); // fix to match tbi not bh
        By patientTreatmentTabBy = By.xpath("//li/a[@href='/tmds/patientTreatment.html']");
        //By tbiAssessmentsLinkBy = By.id("a_2");
        By tbiAssessmentsLinkBy = By.xpath("//li/a[@href='/bm-app/tbiAssessments.html']");

        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, tbiAssessmentsLinkBy);
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
                this.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient);
                if (!processSucceeded && !Arguments.quiet) System.err.println("      ***Failed to process TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                //return processSucceeded;
            }
        }

        // Do file upload now
        FileUpload fileUpload = this.fileUpload;
        if (fileUpload != null) {
            System.out.println("Finish this off later for file upload, here and in BehavioralHealthAssessment");
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
            logger.severe("TbiAssessment.isPatientRegistered(), could not fill in one or more fields.  e: " + e.getMessage());
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
            logger.fine("Looks like didn't get the Behavioral Health Assessments page after the search: " + e.getMessage());
            return false; // fails: demo: 2
        }
        return true;
    }
}
