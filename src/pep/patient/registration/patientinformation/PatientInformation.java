package pep.patient.registration.patientinformation;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.registration.EmergencyContact;
import pep.patient.registration.ImmediateNeeds;
import pep.patient.registration.PermanentHomeOfRecord;
import pep.patient.registration.SelectedPatientInformation;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Driver.driver;

public class PatientInformation {
    private static Logger logger = Logger.getLogger(PatientInformation.class.getName());
    public Boolean random;
    public Boolean shoot;
    public SelectedPatientInformation selectedPatientInformation;
    public PermanentHomeOfRecord permanentHomeOfRecord;
    public EmergencyContact emergencyContact;
    public ImmediateNeeds immediateNeeds;


    private static By patientRegistrationMenuLinkBy = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static By patientInformationPageLinkBy = By.xpath("//li/a[@href='/tmds/patientInformation.html']");
    public static By submitButtonBy = By.xpath("//input[@value=\"Submit\"]"); // wow, much better, if this works
    public static By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // could be more than one error in the list,  We assume the first one is good enough // verified (on test tier too?)

    public static By ssnBy = By.id("ssn");
    public static By lastNameBy = By.id("lastName");
    public static By firstNameBy = By.id("firstName");
    public static By traumaRegisterNumberBy = By.id("registerNumber");
    //public static By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]/table[2]/tbody/tr/td/table/tbody/tr[4]/td/input");
    public static By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]//input[@value='Search For Patient']");

    public static By savedMessageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[3]/tbody/tr[2]/td/table/tbody/tr/td[2]/span"); // verified on TEST.  Not much can do about this ugly xpath.  Give it an id!
    public static By errorMessageBy = By.id("patientInformationForm.errors");

    public PatientInformation() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.selectedPatientInformation = new SelectedPatientInformation(); // wrong of course
            this.permanentHomeOfRecord = new PermanentHomeOfRecord();
            this.emergencyContact = new EmergencyContact();
            this.immediateNeeds = new ImmediateNeeds();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
        }

    }

    // TMDS Patient Information causes names to go to upper case, and when this program reads it back, it updates the
    // patient search stuff.
    public boolean process(Patient patient) {
        boolean succeeded = true; // Why not start this out as true?  Innocent until proven otherwise
// Let's just sit and wait here for a while to see if it makes any difference in being able to find a patient that exists
        // Is this right here?  Don't continue with PatientInformation if we didn't find the patient!!!!!
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("  Processing Patient Information for patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );

        }
        else {
            if (!Arguments.quiet)
                System.out.println("  Processing Patient Information ...");
        }

        // huh?  Don't do the thing with random here to inherit parent's random?
        if (this.random == null) {
            this.random = patient.random;
            // It really should be this one instead, I think:
            //this.random = patient.registration.random;
        }

        Utilities.sleep(555);
        // I think this next line fails because of the first link i4000
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientInformationPageLinkBy);
        //logger.fine("Navigated?: " + navigated);
        if (!navigated) {
            return false; // fails: level 4 demo: 1, gold 1
        }
        // If next line happens too soon, the search doesn't work.  So I put a little wait in it.
        // Or is there perhaps a problem after doing a Patient Update and some fields changed?
        // Or is it because the patient hasn't arrived yet?  Most likely this is the case.  A PreReg was done, and Arrivals wasn't done to mark the patient arrived.
        boolean proceedWithPatientInformation = isPatientFound(patient.patientSearch.ssn, patient.patientSearch.lastName, patient.patientSearch.firstName, patient.patientSearch.traumaRegisterNumber);
        // If try to do Patient Information when the patient has been "departed" through Update Patient, you won't find the patient.
        // Is it possible that we get back a true on isPatientFound when SearchForPatient never worked?
        if (proceedWithPatientInformation) {
            succeeded = doPatientInformation(patient);
        }
        else {
            return false; // Why?  no patients found because Update Patient changed the patient's name?
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return succeeded;
    }

    boolean isPatientFound(String ssn, String lastName, String firstName, String tramaRegisterNumber) {

        try {
            // let's try to wait for ssn's field to show up before trying to do a find of it
            logger.finest("gunna wait for visibility of ssn field");
           // WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnBy));
            // Something happens to mess this up.  If you get here too fast then even though you get a WebElement,
            // it goes stale before you can sendKeys to it.

            Utilities.sleep(555);

            WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(ssnBy)));
            logger.finest("gunna send keys " + ssn);
            ssnField.sendKeys(ssn); // this fails!!!!!!!!!!!!!!!!!!!111


            logger.finest("gunna try to send last name to element last name");
            Driver.driver.findElement(lastNameBy).sendKeys(lastName);
            logger.finest("gunna try to send first name to element first name");
            Driver.driver.findElement(firstNameBy).sendKeys(firstName);
            logger.finest("gunna try to send trauma to element trauma");
            Driver.driver.findElement(traumaRegisterNumberBy).sendKeys(tramaRegisterNumber);
            logger.finest("sent them all");
        }
        catch (StaleElementReferenceException e) { // fails: 1 11/17/18
            logger.fine("PatientInformation.isPatientFound(), Stale Element: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("PatientInformation.isPatientFound(), e: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        // The following search fails if Update Patient was executed just before this, MAYBE.
        try {
            WebElement searchForPatientButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(searchForPatientBy));
            searchForPatientButton.click();
        }
        catch (Exception e) {
            logger.severe("Couldn't find or click on Search For Patient button.");
            return false;
        }
        try {
            // something failing on next line.  Check, stop.
            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy)); // was 2
            String searchMessageAreaText = searchMessageArea.getText();
            if (searchMessageAreaText.equalsIgnoreCase("There are no patients found.")) {
                if (!Arguments.quiet) System.err.println("    ***Could not find patient to process Patient Information.  No longer active?  Departed from facility?  Message says: " + searchMessageAreaText);
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

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Instant start = Instant.now();
        Utilities.clickButton(submitButtonBy); // Not AJAX, but does call something at /tmds/registration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner



// unsure of following.  reports fail, but not?
        // kinda cool how this is done.  Does it work reliably?  If so, do it elsewhere
        ExpectedCondition<WebElement> savedMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
        ExpectedCondition<WebElement> errorMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);
        try {
            (new WebDriverWait(driver, 30)).until(ExpectedConditions.or(savedMessageVisibleCondition, errorMessageVisibleCondition)); // was 5
        }
        catch (Exception e) {
            logger.severe("PatientInformation.doPatientInformation(), Couldn't wait for visible message. exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }



        String message = null;
        try {
            WebElement savedMessageElement = driver.findElement(savedMessageBy); // not the most safe way, but seems to work
            message = savedMessageElement.getText();
            logger.finest("PatientInformation.doPatientInformation(), saved message: " + message);
        }
        catch (Exception e) {
            logger.finest("PatientInformation.doPatientInformation(), couldn't get saved message. Continuing... exception: " + Utilities.getMessageFirstLine(e));
        }
        // hey, if message is "Record Saved", then don't need to do anything else.  Seems to work on TEST tier.  But I deliberately didn't change the name/ssn/gender, with "random"



        try {
            WebElement errorMessageElement = driver.findElement(errorMessageBy);
            message = errorMessageElement.getText();
            logger.finest("PatientInformation.doPatientInformation(), error message: " + message);
        }
        catch (Exception e) {
            logger.finest("PatientInformation.doPatientInformation(), couldn't get error message.  Continuing.");
        }




        if (message == null || message.isEmpty()) {
            logger.fine("Huh?, no message at all for Patient Information save attempt?");
        }

        if (!message.contains("Record Saved")) {
            if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + message);
            return false;
        }


        if (!Arguments.quiet) {
            System.out.println("    Saved Patient Information record for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("Patient Information for Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true;
    }

    boolean doSelectedPatientInformation(Patient patient) {
        if (selectedPatientInformation == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            selectedPatientInformation = new SelectedPatientInformation();
        }
        if (selectedPatientInformation.random == null) {
            selectedPatientInformation.random = ((this.random == null) ? false : this.random); // kinda test
        }
        if (selectedPatientInformation.shoot == null) {
            selectedPatientInformation.shoot = ((this.shoot == null) ? false : this.shoot); // kinda test
        }
        boolean result = selectedPatientInformation.process(patient);
        return result;
    }

    boolean doPermanentHomeOfRecord(Patient patient) {
        if (permanentHomeOfRecord == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            permanentHomeOfRecord = new PermanentHomeOfRecord();
        }
        if (permanentHomeOfRecord.random == null) {
            permanentHomeOfRecord.random = ((this.random == null) ? false : this.random); // kinda test
        }
        if (permanentHomeOfRecord.shoot == null) {
            permanentHomeOfRecord.shoot = ((this.shoot == null) ? false : this.shoot); // kinda test
        }

        boolean result = permanentHomeOfRecord.process(patient);
        return result;
    }
    boolean doEmergencyContact(Patient patient) {
        if (emergencyContact == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            emergencyContact = new EmergencyContact();
        }
        if (emergencyContact.random == null) {
            emergencyContact.random = ((this.random == null) ? false : this.random); // kinda test
        }
        if (emergencyContact.shoot == null) {
            emergencyContact.shoot = ((this.shoot == null) ? false : this.shoot); // kinda test
        }

        boolean result = emergencyContact.process(patient);
        return result;
    }
    boolean doImmediateNeeds(Patient patient) {
        if (immediateNeeds == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            immediateNeeds = new ImmediateNeeds();
        }
        if (immediateNeeds.random == null) {
            immediateNeeds.random = ((this.random == null) ? false : this.random); // kinda test
        }
        if (immediateNeeds.shoot == null) {
            immediateNeeds.shoot = ((this.shoot == null) ? false : this.shoot); // kinda test
        }

        boolean result = immediateNeeds.process(patient);
//        if (Arguments.pauseSection > 0) {
//            Utilities.sleep(Arguments.pauseSection * 1000);
//        }
        return result;
    }

}
