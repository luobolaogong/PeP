package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.Pep.isDemoTier;
import static pep.utilities.Driver.driver;

public class PatientInformation {
  private static Logger logger = Logger.getLogger(PatientInformation.class.getName());
    public Boolean random;
    public SelectedPatientInformation selectedPatientInformation;
    public PermanentHomeOfRecord permanentHomeOfRecord;
    public EmergencyContact emergencyContact;
    public ImmediateNeeds immediateNeeds;


    private static By  patientRegistrationMenuLinkBy = By.id("i4000");
    private static By  patientInformationPageLinkBy = By.xpath("//*[@id=\"links\"]//a[@href=\"/tmds/patientInformation.html\"]");
    public static By submitButtonByBy = By.xpath("//*[@id=\"patientInformationForm\"]/table[8]/tbody/tr/td/input");


    public PatientInformation() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.selectedPatientInformation = new SelectedPatientInformation(); // wrong of course
            this.permanentHomeOfRecord = new PermanentHomeOfRecord();
            this.emergencyContact = new EmergencyContact();
            this.immediateNeeds = new ImmediateNeeds();
        }
        if (isDemoTier) {
        }

    }

    // TMDS Patient Information causes names to go to upper case, and when this program reads it back, it updates the
    // patient search stuff.
    public boolean process(Patient patient) {
        boolean succeeded = true; // Why not start this out as true?  Innocent until proven otherwise

        // Is this right here?
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("  Processing Patient Information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }
        else {
            if (!Arguments.quiet)
                System.out.println("  Processing Patient Information ...");
        }

        // huh?  Don't do the thing with random here to inherit parent's random?
        if (this.random == null) {
            this.random = patient.random;
            // It really should be this one instead, I think:
            //this.random = patient.patientRegistration.random;
        }

        Utilities.sleep(555);
        // I think this next line fails because of the first link i4000
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientInformationPageLinkBy);
        //logger.fine("Navigated?: " + navigated);
        if (!navigated) {
            return false; // fails: level 4 demo: 1, gold 1
        }

        boolean proceedWithPatientInformation = isPatientFound(patient.patientSearch.ssn, patient.patientSearch.lastName, patient.patientSearch.firstName, patient.patientSearch.traumaRegisterNumber);

        if (proceedWithPatientInformation) {
            succeeded = doPatientInformation(patient);
        }
        else {
            return false;
        }
        if (Arguments.pagePause > 0) {
            Utilities.sleep(Arguments.pagePause * 1000);
        }
        return succeeded;
    }

    boolean isPatientFound(String ssn, String lastName, String firstName, String tramaRegisterNumber) {
        By ssnBy = By.id("ssn");
        By lastNameBy = By.id("lastName");
        By firstNameBy = By.id("firstName");
        By traumaRegisterNumberBy = By.id("registerNumber");
        By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]/table[2]/tbody/tr/td/table/tbody/tr[4]/td/input");

        try {
            // let's try to wait for ssn's field to show up before trying to do a find of it
            WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnBy));
            //WebElement ssnField = Driver.driver.findElement(ssnBy);
            ssnField.sendKeys(ssn);
            Driver.driver.findElement(lastNameBy).sendKeys(lastName);
            Driver.driver.findElement(firstNameBy).sendKeys(firstName);
            Driver.driver.findElement(traumaRegisterNumberBy).sendKeys(tramaRegisterNumber);
        }
        catch (Exception e) {
            logger.fine("PatientInformation.isPatientFound(), e: " + e.getMessage());
            return false;
        }

        // This click will only find patients at Role 4 if was created at Role 4.  Isn't that strange?  Is it right?
        Driver.driver.findElement(searchForPatientBy).click();

        try {
            By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // verified
            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy));
            String searchMessageAreaText = searchMessageArea.getText();
            if (searchMessageAreaText.equalsIgnoreCase("There are no patients found.")) {
                if (!Arguments.quiet) System.err.println("***Could not find patient.  No longer active?  Departed from facility?  Message says: " + searchMessageAreaText);
                return false; // could it be because the patient was departed? Yes!
            }
        }
        catch (Exception e) {
            logger.fine("PatientInformation.isPatientFound(), Prob okay.  Couldn't find a message about search, so a patient was probably found.");
        }


        return true;
    }

    boolean doPatientInformation(Patient patient) {
        boolean succeeded;
        succeeded = doSelectedPatientInformation(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doPermanentHomeOfRecord(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doEmergencyContact(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doImmediateNeeds(patient);
        if (!succeeded) {
            return false;
        }


        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Utilities.clickButton(submitButtonByBy); // Not AJAX, but does call something at /tmds/patientRegistration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
// unsure of following.  reports fail, but not?
        By savedMessageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[3]/tbody/tr[2]/td/table/tbody/tr/td[2]/span");
        By errorMessageBy = By.id("patientInformationForm.errors");

        ExpectedCondition<WebElement> savedMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
        ExpectedCondition<WebElement> errorMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);
        try {
            (new WebDriverWait(driver, 5)).until(ExpectedConditions.or(savedMessageVisibleCondition, errorMessageVisibleCondition));

        }
        catch (Exception e) {
            if (!Arguments.quiet) System.out.println("PatientInformation.doPatientInformation(), Couldn't wait for visible message. exception: " + e.getMessage());
            return false;
        }
        String message = null;
        try {
            WebElement savedMessageElement = driver.findElement(savedMessageBy);
            message = savedMessageElement.getText();
        }
        catch (Exception e) {
            if (!Arguments.quiet) System.out.println("PatientInformation.doPatientInformation(), couldn't get saved message. exception: " + e.getMessage());
        }

        try {
            WebElement errorMessageElement = driver.findElement(errorMessageBy);
            message = errorMessageElement.getText();
        }
        catch (Exception e) {
        }
        if (message == null || message.isEmpty()) {
            logger.fine("Huh?, no message at all for Patient Information save attempt?");
        }

        if (!message.contains("Record Saved")) {
            if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + message);
            return false;
        }
        return true;
    }

    boolean doSelectedPatientInformation(Patient patient) {
        if (selectedPatientInformation == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            selectedPatientInformation = new SelectedPatientInformation();
        }
        if (selectedPatientInformation.random == null) {
            selectedPatientInformation.random = ((this.random == null) ? false : this.random); // kinda test
        }
        boolean result = selectedPatientInformation.process(patient);
//        if (Arguments.sectionPause > 0) {
//            Utilities.sleep(Arguments.sectionPause * 1000);
//        }
        return result;
    }

    boolean doPermanentHomeOfRecord(Patient patient) {
        if (permanentHomeOfRecord == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            permanentHomeOfRecord = new PermanentHomeOfRecord();
        }
        if (permanentHomeOfRecord.random == null) {
            permanentHomeOfRecord.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = permanentHomeOfRecord.process(patient);
//        if (Arguments.sectionPause > 0) {
//            Utilities.sleep(Arguments.sectionPause * 1000);
//        }
        return result;
    }
    boolean doEmergencyContact(Patient patient) {
        if (emergencyContact == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            emergencyContact = new EmergencyContact();
        }
        if (emergencyContact.random == null) {
            emergencyContact.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = emergencyContact.process(patient);
//        if (Arguments.sectionPause > 0) {
//            Utilities.sleep(Arguments.sectionPause * 1000);
//        }
        return result;
    }
    boolean doImmediateNeeds(Patient patient) {
        if (immediateNeeds == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            immediateNeeds = new ImmediateNeeds();
        }
        if (immediateNeeds.random == null) {
            immediateNeeds.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = immediateNeeds.process(patient);
//        if (Arguments.sectionPause > 0) {
//            Utilities.sleep(Arguments.sectionPause * 1000);
//        }
        return result;
    }

}
