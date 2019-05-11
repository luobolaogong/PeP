package pep.patient.registration.preregistration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.patient.registration.Demographics;
import pep.patient.registration.Flight;
import pep.patient.registration.InjuryIllness;
import pep.patient.registration.Location;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Driver.driver;
/**
 * This class takes care of the Pre-Registration page, which consists of sections Demographics,
 * Flight, Injury/Illness, and Location.  This page is only accessible to Role 4 facilities and
 * their users.(?)
 */
public class PreRegistration {
    private static Logger logger = Logger.getLogger(PreRegistration.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Demographics demographics;
    public Flight flight;
    public InjuryIllness injuryIllness;
    public Location location;

    private static By PATIENT_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static By PATIENT_PRE_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientPreReg.html']");
    private static By ssnFieldBy = By.id("ssn");
    private static By lastNameFieldBy = By.id("lastName");
    private static By firstNameFieldBy = By.id("firstName");
    private static By registerNumberFieldBy = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//button[text()='Search For Patient']");
    private static By messageArea1By = By.xpath("//*[@id='errors']/ul/li");
    private static By messageArea2By = By.id("patientRegistrationSearchForm.errors");
    private static By messageArea3By = By.id("patientRegistrationSearchForm.errors");
    private static By commitButtonBy = By.id("commit");

    private static By flightSectionBy = By.id("formatArrivalDate");
    private static By locationSectionBy = By.xpath("//td[text()='Location']");

    public PreRegistration() {
        if (Arguments.template) {
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
        }
        if (Arguments.codeBranch.equalsIgnoreCase("Seam")) {
            messageArea1By = By.xpath("//*[@id=\"patientRegistrationSearchForm.errors\"]");
            messageArea2By = By.xpath("//*[@id=\"errors\"]/ul/li");
            messageArea3By = By.xpath("//*[@id=\"patientRegistrationSearchForm.errors\"]");
        }
    }

    /**
     * Process this pre-registration page, by calling doPreRegistration, after navigating to the page
     * and insuring the patient can have a preregistration.
     * If the user is not a level 4 then I think you can't do a Preregistration, and shouldn't be here.
     * @param patient the patient in question
     * @return success or failure depending on doPreRegistration(), and if the patient can do a pre-registration
     */
    public boolean process(Patient patient) {
        boolean succeeded;
        if (!Arguments.quiet) {
            System.out.print("  Processing Pre-Registration ");
            StringBuffer forString = new StringBuffer();
            if (patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty() && !patient.patientSearch.firstName.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(patient.patientSearch.firstName);
            }
            if (patient.patientSearch.lastName != null && !patient.patientSearch.lastName.isEmpty() && !patient.patientSearch.lastName.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(" " + patient.patientSearch.lastName);
            }
            if (patient.patientSearch.ssn != null && !patient.patientSearch.ssn.isEmpty() && !patient.patientSearch.ssn.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(" ssn:" + patient.patientSearch.ssn);
            }
            if (forString.length() > 0) {
                System.out.println("for " + forString.toString() + " ...");
            } else {
                System.out.println(" ...");
            }
        }
        //
        // Navigate to the pre-registration page.
        //
        Utilities.sleep(1555, "PreRegistration"); // was 555
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, PATIENT_PRE_REGISTRATION_MENU_LINK);
        if (!navigated) {
            logger.fine("PreRegistration.process(), Failed to navigate!!!");
            return false;
        }
        // all this next stuff is to just see if we can do a Pre-Reg page with the patient
        // which we should know from what comes back from Search For Patient
        PatientState patientState = getPatientStateFromPreRegSearch(patient);
            switch (patientState) {
            case UPDATE:
                logger.fine("Should switch to Update Patient?  Not going to do that for now.");
                return false;
            case INVALID:
                return false;
            case PRE:
                // we're good so do it
                succeeded = doPreRegistration(patient);
                break;
            case PRE_ARRIVAL:
                return false;
            default:
                logger.fine("What status? " + patientState);
                return false; // new 12/30/18
                //break;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "PreRegistration, requested sleep for page.");
        }
        return succeeded;
    }

    /**
     * Determine the patient state, and return it.  Similar to other methods: New, Pre, Update.
     * Need a state transition diagram, because the resulting state is dependent upon the current state
     * even though you get the same search responses.
     * This method returns a PatientState object representing the patient's state in the system, which is based on the text
     * message which is returned by doing a patient search.  So it takes two methods to get this done.
     * @param patient The patient in question
     * @return the PatientState object
     */
    private PatientState getPatientStateFromPreRegSearch(Patient patient) {
        boolean skipSearch = false;
        String firstName = null;
        String lastName = null;
        String ssn = null;
        String traumaRegisterNumber = null;

        if (patient.patientSearch != null) {
            firstName = patient.patientSearch.firstName;
            lastName = patient.patientSearch.lastName;
            ssn = patient.patientSearch.ssn;
            traumaRegisterNumber = patient.patientSearch.traumaRegisterNumber;
        }
        if (patient.patientSearch == null) {
            skipSearch = true;
        }
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            return PatientState.PRE;
        }

        String searchResponseMessage = getPreRegSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);
        if (searchResponseMessage == null) {
            logger.fine("Probably okay to proceed with Pre-registration.");
            return PatientState.PRE;
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                System.err.println("    ***Search For Patient: " + searchResponseMessage);
            }
        }
        // Prob most of the following doesn't apply to PreRegistration
        if (searchResponseMessage.contains("There are no patients found.")) {
            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.PRE;
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            logger.severe("Patient already has an open registration record.  Use Update Patient instead.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.contains("already has an open Pre-Registration record.")) {
            logger.severe("Patient already has an open pre-registration record.  Use Pre-registration Arrivals page.");
            return PatientState.PRE_ARRIVAL;
        }
        if (searchResponseMessage.contains("An error occurred while processing")) {
            logger.severe("Error with TMDS, but we will continue assuming new patient.  Message: " + searchResponseMessage);
            return PatientState.NEW; // Not invalid.  TMDS has a bug.
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            logger.fine("I think this happens when we're level 3, not 4.  Can update here?  Won't complain later?  Will not complain later");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            return PatientState.PRE;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            return PatientState.INVALID;
        }
        logger.fine("What kinda message?: " + searchResponseMessage);
        return PatientState.PRE_ARRIVAL;
    }

    /**
     * This method returns the string message resulting from doing a search for a patient on this pre-registration page.
     * Obtaining that message is more complicated than it needed to be had the page been constructed more logically.
     * @param ssn of the patient searched for
     * @param firstName of the patient searched for
     * @param lastName of the patient searched for
     * @param traumaRegisterNumber of the patient searched for
     * @return The string result of doing a patient search
     */
    String getPreRegSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        String message = null;
        try {
            logger.finest("PreRegistration.getPreRegSearchPatientResponse(), going to wait for the ssnField to be present.  Hmm should be visible or clickable?");
            Utilities.waitForPresence(ssnFieldBy, 5, "PreRegistration.(), ssn");
        }
        catch (Exception e) {
            System.out.println("Couldn't get ssn field in PreRegistration.getPreRegSearchPatientResponse(), why? e: " + Utilities.getMessageFirstLine(e));
        }
        Utilities.fillInTextField(ssnFieldBy, ssn);
        Utilities.fillInTextField(lastNameFieldBy, lastName);
        Utilities.fillInTextField(firstNameFieldBy, firstName);
        Utilities.fillInTextField(registerNumberFieldBy, traumaRegisterNumber);

        Utilities.clickButton(searchForPatientButtonBy);
        // There is a general "message area" above the "Search For Patient" tab on the page and it is where various messages are displayed.
        // One locator is used for the message "There are no patients found."  A different locator is used for another message like
        // "There's already a patient whatever..."  So do a double locator and wait just long enough for whichever one shows up.
        // There are three possible results: not found, already open, or found but not yet arrived.
        // The first two have messages.  The last one probably doesn't, and it's signalled by the Search For Patient text input boxes being grayed out.
        // But handling the grayed out field is more complicated, and so it should be done only if the first two fail.
        //
        ExpectedCondition<WebElement> messageArea1ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(messageArea1By);
        ExpectedCondition<WebElement> messageArea2ExpectedCondition = ExpectedConditions.visibilityOfElementLocated(messageArea2By);
        ExpectedCondition<Boolean> oneOrTheOtherCondition = ExpectedConditions.or(messageArea1ExpectedCondition, messageArea2ExpectedCondition);
        boolean gotOneOrTheOther = false;
        try {
            gotOneOrTheOther = (new WebDriverWait(Driver.driver, 10)).until(oneOrTheOtherCondition);
            logger.finer("result of waiting for one or the other: " + gotOneOrTheOther);
        }
        catch (Exception e) {
            logger.fine("Didn't get either condition met. So check for rayed out?  return null? e: " + Utilities.getMessageFirstLine(e));
            // continue on, we might need to check gray ssn box
        }

        if (gotOneOrTheOther) {
            // At this point we should have one or the other message showing up (assuming a previous message was erased in time)
            // I don't know how to find out which one got the result without doing another wait, but it shouldn't take long now.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea2ExpectedCondition); // was 1
                message = element.getText();
                logger.fine("message: " + message); // "there are no patients found"  "... already has an open Registration record. Please update ... Update Patient page."
                return message; // "already has an open Pre-Reg...Pre-registration Arrivals page."
            } catch (Exception e1) {
                logger.warning("Didn't get a message using locator " + messageArea2ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e1));
            }
            // check on the other condition.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea1ExpectedCondition);
                message = element.getText();
                logger.fine("PreRegistration.getPreRegSearchPatientResponse(), Prob okay to procede with PreReg.  message: " + message); // "...already has an open Reg...Update Patient page.", "There are no patients found"
                return message; // "...already has an open Pre-Reg rec...Pre-reg Arrivals page.", or "...already has an open Reg rec.  Update Patient page.", "There are no patients found."
            } catch (Exception e2) {
                logger.fine("Didn't get a message using locator " + messageArea1ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e2));
            }
        }
        else {
                logger.fine("No exception but didn't get either condition met, which is unlikely.");
                // continue on
        }
        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = Utilities.waitForPresence(ssnFieldBy, 10, "PreRegistration.(), ssn");
        }
        catch (Exception e) {
            logger.fine("I guess ssnbox wasn't available for some reason: " + Utilities.getMessageFirstLine(e));
        }

        if (ssnTextBoxElement == null) {
            logger.fine("Didn't get an ssnTextBoxElement.");
        }
        else {
            String disabledAttribute = ssnTextBoxElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                logger.fine("Didn't find disabled attribute, so not greyed out which means what?  That 'There are no patients found.'?");
            }
            else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    logger.fine("Grayed out.");
                    return "Search fields grayed out.";
                }
            }
        }
        return message;
    }

    /**
     * Process the pre-registration page for the patient, which consists of several sections
     * @param patient The patient for this page
     * @return success or failure of the sections that make up this page
     */
    boolean doPreRegistration(Patient patient) {
        boolean succeeded;
        succeeded = doDemographicsSection(patient);
        if (!succeeded) {
            logger.fine("PreReg.doPreReg(), doDemographicsSection() failed.");
            return false;
        }
        succeeded = doFlightSection(patient);
        if (!succeeded) {
            logger.fine("PreReg.doPreReg(), doFlightSection() failed.");
            return false;
        }
        succeeded = doInjuryIllnessSection(patient);
        if (!succeeded) {
            logger.fine("PreReg.doPreReg(), doInjuryIllnessSection() failed.");
            return false;
        }
        succeeded = doLocationSection(patient);
        if (!succeeded) {
            logger.fine("PreReg.doPreReg(), doLocationSection() failed.");
            return false;
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }
        Instant start = Instant.now();
        Utilities.clickButton(commitButtonBy);
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept();
        }
        catch (Exception e) {
            // No alert about duplicate SSN's.  Continue
        }

        // This section has timing challenges
        WebElement spinnerPopupWindow = null;
        try {
            By spinnerPopupWindowBy = By.id("MB_window");
            spinnerPopupWindow = Utilities.waitForVisibility(spinnerPopupWindowBy, 30, "PreRegistration.(), spinner popup window"); // was 15
        }
        catch (Exception e) {
            logger.fine("Couldn't wait for visibility of spinner.  Will continue.  Exception: " + Utilities.getMessageFirstLine(e));
        }
        try {
            if (spinnerPopupWindow != null) {
                (new WebDriverWait(Driver.driver, 180)).until(ExpectedConditions.stalenessOf(spinnerPopupWindow)); // do invisibilityOfElementLocated instead of staleness?
            }
        }
        catch (WebDriverException e) {
            logger.fine("Got a WebDriverException, whatever that was from, while trying to wait for spinnerWindow.  Bec running in grid?  Exception: " + Utilities.getMessageFirstLine(e));
        }
        catch (Exception e) {
            logger.fine("Some other exception in PreReg.doPreReg(): " + Utilities.getMessageFirstLine(e));
        }

        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 15)) //  Can take a long time
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageArea3By))); // fails: 4, but verifies
        }
        catch (Exception e) {
            logger.severe("preReg.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (!(someTextMaybe.contains("has been created.") ||
                someTextMaybe.contains("Patient's record has been updated.") ||
                someTextMaybe.contains("Patient's Pre-Registration has been created."))) {
                if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe + " fmp: " + patient.registration.preRegistration.demographics.fmp + " " + someTextMaybe);
                return false;
            }
        }
        catch (TimeoutException e) {
            logger.severe("preReg.process(), Failed to get message from message area.  TimeoutException: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("preReg.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("    Saved Pre-registration record at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("PreRegistration Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true;
    }

    /**
     * Setup, then call Demographics.process()
     * @param patient The patient
     * @return success or failure of calling Flight.process()
     */
    boolean doDemographicsSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        Demographics demographics = preRegistration.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.randomizeSection = this.randomizeSection;
            demographics.shoot = this.shoot;
            preRegistration.demographics = demographics;
        }
        if (demographics.randomizeSection == null) {
            demographics.randomizeSection = this.randomizeSection;
        }
        if (demographics.shoot == null) {
            demographics.shoot = this.shoot;
        }
        boolean processSucceeded = demographics.process(patient);
        if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    /**
     * Setup, then call Flight.process()
     * @param patient The patient
     * @return success or failure of calling Flight.process()
     */
    boolean doFlightSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Flight (only available in Level 4)
        try {
            Utilities.waitForPresence(flightSectionBy, 1, "PreRegistration.doFlightSection()");
            Flight flight = preRegistration.flight;
            if (flight == null) {
                flight = new Flight();
                preRegistration.flight = flight;
            }
            if (flight.randomizeSection == null) {
                flight.randomizeSection = this.randomizeSection;
            }
            if (flight.shoot == null) {
                flight.shoot = this.shoot;
            }
            boolean processSucceeded = flight.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process flight for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            // There's no flight section, which is the case for levels/roles 1,2,3
            return true;
        }
        catch (Exception e) {
            logger.severe("Some kind of error in flight section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Setup, then call InjuryIllness.process()
     * @param patient The patient
     * @return the success or failure of calling InjuryIllness.process()
     */
    boolean doInjuryIllnessSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        InjuryIllness injuryIllness = preRegistration.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            preRegistration.injuryIllness = injuryIllness;
        }
        if (injuryIllness.randomizeSection == null) {
            injuryIllness.randomizeSection = this.randomizeSection;
        }
        if (injuryIllness.shoot == null) {
            injuryIllness.shoot = this.shoot;
        }
        boolean processSucceeded = injuryIllness.process(patient);
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    /**
     * Setup, and then call Location.process()
     * @param patient The patient
     * @return success or failure of calling Location.process()
     */
    boolean doLocationSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        try {
            Utilities.waitForPresence(locationSectionBy, 1, "PreRegistration.doLocationSection()"); // not sure why need this.  The section is required.
            Location location = preRegistration.location;
            if (location == null) {
                location = new Location();
                preRegistration.location = location;
            }
            if (location.randomizeSection == null) {
                location.randomizeSection = this.randomizeSection;
            }
            if (location.shoot == null) {
                location.shoot = this.shoot;
            }
            boolean processSucceeded = location.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            return true;
        }
        catch (StaleElementReferenceException e) {
            logger.fine("Stale reference exception in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

}
