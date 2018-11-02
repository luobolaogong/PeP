package pep.patient.treatment.behavioralhealthassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;

public class BehavioralHealthAssessment { // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public BehavioralHealthNote behavioralHealthNote;
    public BhTbiAssessmentNote bhTbiAssessmentNote;
    public FileUpload fileUpload;

    private static By patientTreatmentTabBy = By.xpath("//*[@id=\"i4200\"]/span");
    private static By behavioralHealthLinkBy = By.id("a_1");
    private static By bhAssessmentsLinkBy = By.id("a_0");
    private static By ssnField = By.id("ssn"); // now not only does demo fail, but also test if you pass do a search for a ssn
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//*[@id=\"search-form\"]/div[2]/button");
    private static By patientDemographicsSectionBy = By.id("patient-demographics-container");

    private static By patientSearchMsgsBy = By.id("msg");


    public BehavioralHealthAssessment() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.behavioralHealthNote = new BehavioralHealthNote();
            this.bhTbiAssessmentNote = new BhTbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
        if (isDemoTier) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButtonBy = By.id("patientSearchGo");
            //patientSearchNoPatientsFoundArea = By.xpath("//*[@id=\"messages\"]/li");
            patientDemographicsSectionBy = By.id("j_id331"); // a section that has "Patient Demographics" tab and "Duplicate Patients" tab
            patientSearchMsgsBy = By.id("patientSearchMsgs");
        }
    }

    // change this to boolean success?
    // Behavioral Health Assessments page has a log of javascript in it that changes the visibility of components on the page.
    // It can be tricky.
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, behavioralHealthLinkBy, bhAssessmentsLinkBy);
        if (!navigated) {
            return false; // Why the frac????  Fails:3
        }

        // It seems we can get past the navigation without actually navigating fully.  True?  If so, why?

        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // valid here?  Mostly yes, sometimes no.

        boolean foundPatient = isPatientRegistered(patient);// Gotta check this.  Coming back false a lot
//        // The above seems to spin for a while and then return, but it's still spinning
        if (!foundPatient) {
            if (Arguments.debug) System.out.println("Can't Do BHA for a patient if can't find the patient.");
            if (Arguments.debug) System.out.println("Was looking for patient " + patient.patientSearch.firstName
                    + " " +    patient.patientSearch.lastName
                    + " " + patient.patientSearch.ssn
                    + " " +     patient.patientSearch.traumaRegisterNumber);

            return false;
        }

        // Logic is bad with regard to doing random.  We'll want at least one of the following, I think.
        // But we've got the random sections separate.  Clean up later.
        boolean wantFirstOne = Utilities.random.nextBoolean();

        int nErrors = 0;
        BehavioralHealthNote behavioralHealthNote = this.behavioralHealthNote;
        if (behavioralHealthNote != null) {
            if (behavioralHealthNote.random == null) { // Is this needed?
                behavioralHealthNote.random = (this.random == null) ? false : this.random;
            }
            boolean processSucceeded = behavioralHealthNote.process(patient, this);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet) System.err.println("      ***Failed to process Behavioral Health Note for patient "
                        + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            }
        }
        else {
            if (this.random && wantFirstOne) {
                behavioralHealthNote = new BehavioralHealthNote();
                behavioralHealthNote.random = (this.random == null) ? false : this.random;
                this.behavioralHealthNote = behavioralHealthNote;
                boolean processSucceeded = behavioralHealthNote.process(patient, this);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet) System.err.println("      ***Failed to process Behavioral Health Note for patient "
                            + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                }
            }
        }


        BhTbiAssessmentNote bhTbiAssessmentNote = this.bhTbiAssessmentNote;
        if (bhTbiAssessmentNote != null) {
            if (bhTbiAssessmentNote.random == null) { // Is this needed?
                bhTbiAssessmentNote.random = (this.random == null) ? false : this.random;
            }
            boolean processSucceeded = bhTbiAssessmentNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
            //return processSucceeded;
        }
        else {
            if (this.random && !wantFirstOne) {
                bhTbiAssessmentNote = new BhTbiAssessmentNote();
                bhTbiAssessmentNote.random = (this.random == null) ? false : this.random;
                this.bhTbiAssessmentNote = bhTbiAssessmentNote;
                boolean processSucceeded = bhTbiAssessmentNote.process(patient); // still kinda weird passing in treatment
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to process BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
                //return processSucceeded;
            }
        }
        if (nErrors > 0) {
            return false;
        }
        if (Arguments.pagePause > 0) {
            Utilities.sleep(Arguments.pagePause * 1000);
        }
        return true; // I know strange
    }

    // Why isn't this done like the other one that has all 4 params?
    boolean isPatientRegistered(Patient patient) {
        Utilities.fillInTextField(ssnField, patient.patientSearch.ssn);
        Utilities.fillInTextField(lastNameField, patient.patientSearch.lastName);
        Utilities.fillInTextField(firstNameField, patient.patientSearch.firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, patient.patientSearch.traumaRegisterNumber);
        // Why do we not get the button first and then click on it?
        Utilities.clickButton(searchForPatientButtonBy); // ajax.  We expect to see "Behavioral Health Assessments" if patient found.  No message area unless not found
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1

        // there could be an error from the search, but there shouldn't be if we'd done an initial search in Registration.  However, since now we can bypass
        // Registration pages and go straight to Treatments this may not have happened.  And so we could get a message like "There are no patients found."
        // If we wanted to check for errors, the area to look
        // would be the span with id "patientSearchMsgs"
        //*[@id="j_id286"]/table/tbody/tr/td/span   That one is for DEMO.  Not sure what it is in Gold at this time.
        try {
            WebElement patientSearchMsgsSpan = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(patientSearchMsgsBy)); // fails, which is okay
            String searchMessage = patientSearchMsgsSpan.getText();
            if (!searchMessage.isEmpty()) {
                if (Arguments.debug) System.out.println("BehavioralHealthAssessment.isPatientRegistered(), got a message back: " + searchMessage);
                if (searchMessage.equalsIgnoreCase("There are no patients found.")) {
                    return false;
                }
                return false;
            }
            else {
                if (Arguments.debug) System.out.println("Search message area was blank, which probably means we found the patient.  Can probably just return true here.");
            }
        }
        catch (Exception e) {
            //if (Arguments.debug) System.out.println("BehavioralHealthAssessment.isPatientRegistered(), no message found, so prob okay.  Continue.");
            //return false;
        }

        // Just to check that we did get to the page we expected, check for a portion of that page.
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(patientDemographicsSectionBy));
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Looks like didn't get the Behavioral Health Assessments page after the search: " + e.getMessage());
            return false;
        }
        return true;
    }
}
