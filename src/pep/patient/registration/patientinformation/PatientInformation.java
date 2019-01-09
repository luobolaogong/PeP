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
    //public static By submitButtonBy = By.xpath("//*[@id=\"patientInformationForm\"]/table[8]/tbody/tr/td/input"); // wow, much better, if this works
    public static By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // could be more than one error in the list,  We assume the first one is good enough // verified (on test tier too?)

    public static By ssnBy = By.id("ssn");
    public static By lastNameBy = By.id("lastName");
    public static By firstNameBy = By.id("firstName");
    public static By traumaRegisterNumberBy = By.id("registerNumber");
    //public static By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]/table[2]/tbody/tr/td/table/tbody/tr[4]/td/input");
    public static By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]//input[@value='Search For Patient']");

    public static By savedMessageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[3]/tbody/tr[2]/td/table/tbody/tr/td[2]/span"); // verified on TEST, my DEV.  Not much can do about this ugly xpath.  Give it an id!
    //public static By savedMessageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[3]/tbody/tr[2]/td/table/tbody/tr/td[2]/span"); // Seems okay on GOLD too  Not much can do about this ugly xpath.  Give it an id!
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
        Utilities.sleep(555); // 12/30/18
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

    // What do we do here if the patient is random?  Well, it should still have values here, I think
    boolean isPatientFound(String ssn, String lastName, String firstName, String traumaRegisterNumber) {
        //try {
            // let's try to wait for ssn's field to show up before trying to do a find of it
            logger.finest("gunna wait for visibility of ssn field");
           // WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnBy));
            // Something happens to mess this up.  If you get here too fast then even though you get a WebElement,
            // it goes stale before you can sendKeys to it.
            logger.finest("PatientInformation.isPatientFound(), got values for ssn, last, first, trauma: " + ssn + " " + lastName + " " + firstName + " " + traumaRegisterNumber);
            Utilities.sleep(555);

            WebElement webElement = null;
            if (ssn != null && !ssn.isEmpty()) {
                logger.finest("PatientInformation.isPatientFound(), will call sendKeys with ssn " + ssn);
                try {
                    webElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(ssnBy)));
                    webElement.sendKeys(ssn); // this fails!!!!!!!!!!!!!!!!!!!
                }
                catch (Exception e) {
                    logger.severe("PatientInformation.isPatientFound(), ssnField: " + webElement + " value: " + ssn + " e: " + e.getMessage());
                    return false;
                }
            }
            if (lastName != null && !lastName.isEmpty()) {
                logger.finest("PatientInformation.isPatientFound(), will call sendKeys for last name " + lastName);
                try {
                    webElement = Driver.driver.findElement(lastNameBy);
                    webElement.sendKeys(lastName);
                }
                catch (Exception e) {
                    logger.severe("PatientInformation.isPatientFound(), lastName: " + webElement + " value: " + lastName + " e: " + e.getMessage());
                    return false;
                }
            }
            if (firstName != null && !firstName.isEmpty()) {
                logger.finest("PatientInformation.isPatientFound(), will call sendKeys for first name " + firstName);
                try {
                    webElement = Driver.driver.findElement(firstNameBy);
                    webElement.sendKeys(firstName);
                }
                catch (Exception e) {
                    logger.severe("PatientInformation.isPatientFound(), firstName: " + webElement + " value: " + firstName + " e: " + e.getMessage());
                    return false;
                }
            }
            if (traumaRegisterNumber != null && !traumaRegisterNumber.isEmpty()) {
                logger.finest("PatientInformation.isPatientFound(), will call sendKeys with trauma " + traumaRegisterNumber);
                try {
                    webElement = Driver.driver.findElement(traumaRegisterNumberBy);
                    webElement.sendKeys(traumaRegisterNumber);
                }
                catch (Exception e) {
                    logger.severe("PatientInformation.isPatientFound(), traumaRegisterNumber: " + webElement + " value: " + traumaRegisterNumber + " e: " + e.getMessage());
                    return false;
                }
            }
//        }
//        catch (StaleElementReferenceException e) { // fails: 1 11/17/18
//            logger.fine("PatientInformation.isPatientFound(), Stale Element: " + Utilities.getMessageFirstLine(e));
//            return false;
//        }
//        catch (Exception e) {
//            logger.severe("PatientInformation.isPatientFound(), e: " + Utilities.getMessageFirstLine(e));
//            return false;
//        }

        // The following search fails if Update Patient was executed just before this, MAYBE.
        try {
            WebElement searchForPatientButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(searchForPatientBy));
            searchForPatientButton.click();
        }
        catch (Exception e) {
            logger.severe("Couldn't find or click on Search For Patient button.");
            return false;
        }
        // Looks like maybe cannot do Patient Information if the patient was departed.
        try {
            // something failing on next line.  Check, stop.  Yup, keeps failing
            Utilities.sleep(2555); // maybe too long, but too many failures, so trying 2555
            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy)); // was 2
            String searchMessageAreaText = searchMessageArea.getText();
            if (searchMessageAreaText.equalsIgnoreCase("There are no patients found.")) {
                if (!Arguments.quiet) System.err.println("    ***Could not find patient to process Patient Information.  No longer active?  Departed from facility?  Message says: " + searchMessageAreaText);
                return false; // could it be because the patient was departed? Yes!
            }
        }
        catch (Exception e) {
            logger.fine("PatientInformation.isPatientFound(), Prob okay.  Couldn't find a message about search, so a patient was probably found.");
        }


        return true;  // Is it possible there could be an error?
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
        //ExpectedCondition<WebElement> savedMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
       // ExpectedCondition<WebElement> errorMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);

        // removed following 12/24/18
//        try {
//            (new WebDriverWait(driver, 30)).until(ExpectedConditions.or(savedMessageVisibleCondition, errorMessageVisibleCondition)); // was 5
//        }
//        catch (Exception e) {
//            logger.severe("PatientInformation.doPatientInformation(), Couldn't wait for visible message. exception: " + Utilities.getMessageFirstLine(e));
//            return false;
//        }

        // new 12/31/18
        ExpectedCondition<WebElement> messageArea1ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
        ExpectedCondition<WebElement> messageArea2ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);
        ExpectedCondition<Boolean> oneOrTheOtherCondition = ExpectedConditions.or(messageArea1ExpectedCondition, messageArea2ExpectedCondition);
        boolean gotOneOrTheOther = false;
        try {
            gotOneOrTheOther = (new WebDriverWait(Driver.driver, 15)).until(oneOrTheOtherCondition); // was 10.  how can this fail?
            logger.finer("result of waiting for one or the other: " + gotOneOrTheOther);
        }
        catch (Exception e) {
            logger.fine("Didn't get either condition met. So check for grayed out?  return null? e: " + Utilities.getMessageFirstLine(e));
            // continue on, we might need to check gray ssn box
        }

        String message = null;

        if (gotOneOrTheOther) {
            // At this point we should have one or the other message showing up (assuming a previous message was erased in time)
            // I don't know how to find out which one got the result without doing another wait, but it shouldn't take long now.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea2ExpectedCondition); // was 1
                message = element.getText();
                logger.fine("message: " + message); // TEST: "there are no patients found"  GOLD: ?"... already has an open Registration record. Please update ... Update Patient page." What???? diff TEST and GOLD????
                //return message; // GOLD: "already has an open Pre-Reg...Pre-registration Arrivals page."
            } catch (Exception e1) {
                logger.warning("Didn't get a message using locator " + messageArea2ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e1));
            }

            // check on the other condition.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea1ExpectedCondition); // was 1
                message = element.getText();
                logger.fine("PreRegistration.getPreRegSearchPatientResponse(), Prob okay to procede with PreReg.  message: " + message); // TEST: "...already has an open Reg...Update Patient page.", GOLD:? "There are no patients found"
                //return message; // TEST: "...already has an open Pre-Reg rec...Pre-reg Arrivals page.", or "...already has an open Reg rec.  Update Patient page.", GOLD: "There are no patients found."
            } catch (Exception e2) {
                logger.fine("Didn't get a message using locator " + messageArea1ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e2));
            }
        }
        else {
            logger.fine("No exception but didn't get either condition met, which is unlikely.");
            // continue on
        }




















// removed 12/31/18
//        String message = null;
//        try {
//            WebElement savedMessageElement = driver.findElement(savedMessageBy); // not the most safe way, but seems to work
//            message = savedMessageElement.getText();
//            logger.finest("PatientInformation.doPatientInformation(), saved message: " + message);
//        }
//        catch (Exception e) {
//            logger.finest("PatientInformation.doPatientInformation(), couldn't get saved message. Continuing... exception: " + Utilities.getMessageFirstLine(e));
//        }






        // hey, if message is "Record Saved", then don't need to do anything else.  Seems to work on TEST tier.  But I deliberately didn't change the name/ssn/gender, with "random"


// removed following on 12/24/18
//        try {
//            WebElement errorMessageElement = driver.findElement(errorMessageBy);
//            message = errorMessageElement.getText();
//            logger.finest("PatientInformation.doPatientInformation(), error message: " + message);
//        }
//        catch (Exception e) {
//            logger.finest("PatientInformation.doPatientInformation(), couldn't get error message.  Continuing.");
//        }




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
            selectedPatientInformation.random = (this.random); // kinda test
        }
        if (selectedPatientInformation.shoot == null) {
            selectedPatientInformation.shoot = (this.shoot); // kinda test
        }
        boolean result = selectedPatientInformation.process(patient);
        return result;
    }

    boolean doPermanentHomeOfRecord(Patient patient) {
        if (permanentHomeOfRecord == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            permanentHomeOfRecord = new PermanentHomeOfRecord();
        }
        if (permanentHomeOfRecord.random == null) {
            permanentHomeOfRecord.random = (this.random); // kinda test
        }
        if (permanentHomeOfRecord.shoot == null) {
            permanentHomeOfRecord.shoot = (this.shoot); // kinda test
        }

        boolean result = permanentHomeOfRecord.process(patient);
        return result;
    }
    boolean doEmergencyContact(Patient patient) {
        if (emergencyContact == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            emergencyContact = new EmergencyContact();
        }
        if (emergencyContact.random == null) {
            emergencyContact.random = (this.random); // kinda test
        }
        if (emergencyContact.shoot == null) {
            emergencyContact.shoot = (this.shoot); // kinda test
        }

        boolean result = emergencyContact.process(patient);
        return result;
    }
    boolean doImmediateNeeds(Patient patient) {
        if (immediateNeeds == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            immediateNeeds = new ImmediateNeeds();
        }
        if (immediateNeeds.random == null) {
            immediateNeeds.random = (this.random); // kinda test
        }
        if (immediateNeeds.shoot == null) {
            immediateNeeds.shoot = (this.shoot); // kinda test
        }

        boolean result = immediateNeeds.process(patient);
//        if (Arguments.pauseSection > 0) {
//            Utilities.sleep(Arguments.pauseSection * 1000);
//        }
        return result;
    }

}
