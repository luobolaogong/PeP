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

/**
 * This class represents the Patient Information page that is part of the Registration grouping, and is used to
 * augment a registered patient's information.  It's composed of 4 different parts, each represented by a class
 * and this class' process() method farms out processing to those classes.
 */
public class PatientInformation {
    private static Logger logger = Logger.getLogger(PatientInformation.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public SelectedPatientInformation selectedPatientInformation;
    public PermanentHomeOfRecord permanentHomeOfRecord;
    public EmergencyContact emergencyContact;
    public ImmediateNeeds immediateNeeds;

    private static final By patientRegistrationMenuLinkBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static final By patientInformationPageLinkBy = By.cssSelector("a[href='/tmds/patientInformation.html']");
    private static final By submitButtonBy = By.xpath("//input[@value='Submit']");
    private static final By searchMessageAreaBy = By.xpath("//*[@id='errors']/ul/li");
    private static final By ssnBy = By.id("ssn");
    private static final By lastNameBy = By.id("lastName");
    private static final By firstNameBy = By.id("firstName");
    private static final By traumaRegisterNumberBy = By.id("registerNumber");
    private static final By searchForPatientBy = By.cssSelector("input[value='Search For Patient']");
    private static final By savedMessageBy = By.xpath("//span[@class='warntext']");
    private static final By errorMessageBy = By.id("patientInformationForm.errors");

    public PatientInformation() {
        if (Arguments.template) {
            this.selectedPatientInformation = new SelectedPatientInformation();
            this.permanentHomeOfRecord = new PermanentHomeOfRecord();
            this.emergencyContact = new EmergencyContact();
            this.immediateNeeds = new ImmediateNeeds();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
        }
    }


    /**
     * Process the patient information page.  Entering info on this page causes names to go to upper case,
     * and when this program reads it back, it updates the patient search stuff.
     * @param patient The patient for this page
     * @return success or failure at doing the parts of the page
     */
    public boolean process(Patient patient) {
        boolean succeeded;
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
        if (this.randomizeSection == null) {
            this.randomizeSection = patient.randomizeSection;
        }
        //
        // This next section is very time sensitive, or perhaps "state" sensitive, as in the patient already departed?
        // Or the patient's name was change?  Or something else?  This section needs analysis.
        //
        Utilities.sleep(555, "PatientInformation.process(), about to do navigation");
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientInformationPageLinkBy);
        if (!navigated) {
            return false;
        }
        Utilities.sleep(1555, "PatientInformation.process(), about to wait for ssn");
        try {
            logger.finest("PatientInformation.process(), here comes a wait for visibility of ssn field.");
            Utilities.waitForVisibility(By.id("ssn"), 10, "PatientInformation.process()"); // was 5
            logger.finest("PatientInformation.process(), back from waiting for visibility of ssn field.");
        }
        catch (Exception e) {
            logger.severe("PatientInformation.process(), could not wait for ssn text field to appear.  This is bad. e: " + Utilities.getMessageFirstLine(e));
        }

        Utilities.sleep(1555, "PatientInformation.process(), want to wait a bit before calling isPatientFound.");
        //boolean proceedWithPatientInformation = isPatientFound(patient.patientSearch.ssn, patient.patientSearch.lastName, patient.patientSearch.firstName, patient.patientSearch.traumaRegisterNumber);
        boolean proceedWithPatientInformation = Utilities.isPatientFound(patient.patientSearch.ssn, patient.patientSearch.lastName, patient.patientSearch.firstName, patient.patientSearch.traumaRegisterNumber);
        if (proceedWithPatientInformation) {
            succeeded = doPatientInformation(patient);
        }
        else {
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "PatientInformation, requested sleep for page.");
        }
        return succeeded;
    }

//    /**
//     * There are currently 4 different methods with this name.  Perhaps they could be consolidated and put into Utilities.
//     * @param ssn of the patient
//     * @param lastName of the patient
//     * @param firstName of the patient
//     * @param traumaRegisterNumber of the patient
//     * @return true if patient registered and found, false otherwise
//     */
//    boolean isPatientFound(String ssn, String lastName, String firstName, String traumaRegisterNumber) {
//        //try {
//        // let's try to wait for ssn's field to show up before trying to do a find of it
//        //logger.finest("gunna wait for visibility of ssn field");
//        // WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnBy));
//        // Something happens to mess this up.  If you get here too fast then even though you get a WebElement,
//        // it goes stale before you can sendKeys to it.
//        logger.finest("PatientInformation.isPatientFound(), got values for ssn, last, first, trauma: " + ssn + " " + lastName + " " + firstName + " " + traumaRegisterNumber);
//
//        try {
//            Utilities.waitForClickability(searchForPatientBy, 5, "Summary.process() waiting for clickability which should indicate we can enter values into the fields");
//        }
//        catch (Exception e) {
//            logger.severe("PatientInformation.isPatientFound() couldn't wait long enough for patient search button to be clickable.");
//            return false; // careful, maybe just didn't wait long enough?
//        }
//        try {
//            Utilities.waitForRefreshedVisibility(ssnBy, 5, "Summary.process() waiting for refreshed visibility for ssn");
//        }
//        catch (Exception e) {
//            logger.severe("PatientInformation.isPatientFound() couldn't wait long enough for ssn's refreshed visibility.");
//            return false; // careful, maybe just didn't wait long enough?
//        }
//        // since the above tests don't seem to work, here comes a sleep
//        Utilities.sleep(2555, "PatientInformation.isPatientFound(), about to wait for visibility of ssn"); //do this next line thing elsewhere too?  was 555
//
//        WebElement webElement = null;
//        if (ssn != null && !ssn.isEmpty()) {
//            logger.finest("PatientInformation.isPatientFound(), will call sendKeys with ssn " + ssn);
//            try {
//                webElement = Utilities.waitForRefreshedVisibility(ssnBy, 5, "PatientInformation.isPatientFound()");
//                webElement.sendKeys(ssn);
//            }
//            catch (Exception e) {
//                logger.severe("PatientInformation.isPatientFound(), ssnField: " + webElement + " value: " + ssn + " e: " + Utilities.getMessageFirstLine(e));
//                return false;
//            }
//        }
//        if (lastName != null && !lastName.isEmpty()) {
//            logger.finest("PatientInformation.isPatientFound(), will call sendKeys for last name " + lastName);
//            try {
//                webElement = Driver.driver.findElement(lastNameBy);
//                webElement.sendKeys(lastName);
//            }
//            catch (Exception e) {
//                logger.severe("PatientInformation.isPatientFound(), lastName: " + webElement + " value: " + lastName + " e: " + e.getMessage());
//                return false;
//            }
//        }
//        if (firstName != null && !firstName.isEmpty()) {
//            logger.finest("PatientInformation.isPatientFound(), will call sendKeys for first name " + firstName);
//            try {
//                webElement = Driver.driver.findElement(firstNameBy);
//                webElement.sendKeys(firstName);
//            }
//            catch (Exception e) {
//                logger.severe("PatientInformation.isPatientFound(), firstName: " + webElement + " value: " + firstName + " e: " + e.getMessage());
//                return false;
//            }
//        }
//        if (traumaRegisterNumber != null && !traumaRegisterNumber.isEmpty()) {
//            logger.finest("PatientInformation.isPatientFound(), will call sendKeys with trauma " + traumaRegisterNumber);
//            try {
//                webElement = Driver.driver.findElement(traumaRegisterNumberBy);
//                webElement.sendKeys(traumaRegisterNumber);
//            }
//            catch (Exception e) {
//                logger.severe("PatientInformation.isPatientFound(), traumaRegisterNumber: " + webElement + " value: " + traumaRegisterNumber + " e: " + e.getMessage());
//                return false;
//            }
//        }
//        // Do we get here too quickly to handle a click on the button?
//
//        // The following search fails if Update Patient was executed just before this, MAYBE.
//        try {
//            WebElement searchForPatientButton = Utilities.waitForRefreshedClickability(searchForPatientBy, 5, "PatientInformation.(), search for patient button");
//            searchForPatientButton.click();
//        }
//        catch (Exception e) {
//            logger.severe("Couldn't find or click on Search For Patient button.");
//            return false;
//        }
//        // Looks like maybe cannot do Patient Information if the patient was departed.
//        // What's a fast way to check if we can move on, or if there's a failure?
//        // If there's a failure because patient not found, then I think we don't get the "Selected Patient Information" section,
//        // and I think we get an error message. If we do get it, then no failure.  So, could do a "joint" wait for either
//        // condition, and when one is caught, check which one instead of waiting around for a timeout.
//
//        // Hey, even if you step through this stuff, it can fail.  Sometimes it doesn't fail.
//        // Following works when stepping through  Takes 5 seconds to get to the page after the last thing is clicked, wherever that is
//        ExpectedCondition<WebElement> condition1 = ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy);
//        ExpectedCondition<WebElement> condition2 = ExpectedConditions.visibilityOfElementLocated(By.id("arrivalDate")); // this is the Arrival Date text box, first ID I can find.
//        ExpectedCondition<Boolean> eitherCondition = ExpectedConditions.or(condition2, condition1);
//        try {
//            logger.finest("PatientInformation.isPatientFound(), here comes a wait for either condition, message or arrivalDate field");
//            (new WebDriverWait(Driver.driver, 10)).until(eitherCondition);
//            logger.finest("PatientInformation.isPatientFound(), back from waiting for either condition, message or arrivalDate field");
//        }
//        catch (Exception e) {
//            logger.info("PatientInformation.isPatientFound(), failed to get either condition.  Continuing. e: " + Utilities.getMessageFirstLine(e));
//        }
//
//        try {
//            WebElement condition2Element = Utilities.waitForVisibility(By.id("arrivalDate"), 1, "PatientInformation.isPatientFound()");
//        }
//        catch (Exception e) {
//            try {
//                WebElement condition1Element = Utilities.waitForVisibility(searchMessageAreaBy, 1, "PatientInformation.isPatientFound()");
//                String message = condition1Element.getText();
//                if (message.contains("There are no patients found.")) {
//                    logger.warning("PatientInformation.isPatientFound(), Failed to find patient.");
//                    return false;
//                }
//            } catch (Exception e2) {
//                logger.finest("PatientInformation.isPatientFound(), failed to wait for condition 1.  Continuing... e: " + Utilities.getMessageFirstLine(e2));
//            }
//        }
//        // do we need to wait a bit to let the page catch up?
//        return true;  // Is it possible there could be an error?
//    }

    /**
     *
     * @param patient
     * @return
     */
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
        if (Arguments.pauseSave > 0) {
            Utilities.sleep(Arguments.pauseSave * 1000, "PatientInformation");
        }
        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Instant start = Instant.now();
        Utilities.clickButton(submitButtonBy); // Not AJAX, but does call something at /tmds/registration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner

        ExpectedCondition<WebElement> messageArea1ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
        ExpectedCondition<WebElement> messageArea2ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);
        ExpectedCondition<Boolean> oneOrTheOtherCondition = ExpectedConditions.or(messageArea1ExpectedCondition, messageArea2ExpectedCondition);
        boolean gotOneOrTheOther = false;
        try {
            gotOneOrTheOther = (new WebDriverWait(Driver.driver, 15)).until(oneOrTheOtherCondition);
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
                logger.fine("Maybe okay: Didn't get a message using locator " + messageArea2ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e1));
            }

            // check on the other condition.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea1ExpectedCondition); // was 1
                message = element.getText();
                logger.fine("PreRegistration.getPreRegSearchPatientResponse(), Prob okay to procede with PreReg.  message: " + message); // TEST: "...already has an open Reg...Update Patient page.", GOLD:? "There are no patients found"
                //return message; // TEST: "...already has an open Pre-Reg rec...Pre-reg Arrivals page.", or "...already has an open Reg rec.  Update Patient page.", GOLD: "There are no patients found."
            } catch (Exception e2) {
                logger.fine("Maybe okay??? Didn't get a message using locator " + messageArea1ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e2));
            }
        }
        else {
            logger.fine("No exception but didn't get either condition met, which is unlikely.");
        }
        if (message == null || message.isEmpty()) {
            logger.fine("Huh?, no message at all for Patient Information save attempt?");
        }
        else if (!message.contains("Record Saved")) {
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
        timerLogger.fine("Patient Information for Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true;
    }

    /**
     * Take care of this section of Patient Information page by calling process() on it.
     * @param patient the patient for this section
     * @return the result of calling process() on the object for this section
     */
    boolean doSelectedPatientInformation(Patient patient) {
        if (selectedPatientInformation == null) {
            selectedPatientInformation = new SelectedPatientInformation();
        }
        if (selectedPatientInformation.randomizeSection == null) {
            selectedPatientInformation.randomizeSection = (this.randomizeSection);
        }
        if (selectedPatientInformation.shoot == null) {
            selectedPatientInformation.shoot = (this.shoot);
        }
        boolean result = selectedPatientInformation.process(patient);
        return result;
    }

    /**
     * Take care of this section of Patient Information page by calling process() on it.
     * @param patient the patient for this section
     * @return the result of calling process() on the object for this section
     */
    boolean doPermanentHomeOfRecord(Patient patient) {
        if (permanentHomeOfRecord == null) {
            permanentHomeOfRecord = new PermanentHomeOfRecord();
        }
        if (permanentHomeOfRecord.randomizeSection == null) {
            permanentHomeOfRecord.randomizeSection = (this.randomizeSection);
        }
        if (permanentHomeOfRecord.shoot == null) {
            permanentHomeOfRecord.shoot = (this.shoot);
        }

        boolean result = permanentHomeOfRecord.process(patient);
        return result;
    }

    /**
     * Take care of this section of Patient Information page by calling process() on it.
     * @param patient the patient for this section
     * @return the result of calling process() on the object for this section
     */
    boolean doEmergencyContact(Patient patient) {
        if (emergencyContact == null) {
            emergencyContact = new EmergencyContact();
        }
        if (emergencyContact.randomizeSection == null) {
            emergencyContact.randomizeSection = (this.randomizeSection);
        }
        if (emergencyContact.shoot == null) {
            emergencyContact.shoot = (this.shoot);
        }

        boolean result = emergencyContact.process(patient);
        return result;
    }

    /**
     * Take care of this section by calling process() on it.
     * @param patient the patient for this section
     * @return
     */
    boolean doImmediateNeeds(Patient patient) {
        if (immediateNeeds == null) {
            immediateNeeds = new ImmediateNeeds();
        }
        if (immediateNeeds.randomizeSection == null) {
            immediateNeeds.randomizeSection = (this.randomizeSection);
        }
        if (immediateNeeds.shoot == null) {
            immediateNeeds.shoot = (this.shoot);
        }
        boolean result = immediateNeeds.process(patient);
        return result;
    }

}
