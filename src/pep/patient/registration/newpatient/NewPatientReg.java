package pep.patient.registration.newpatient;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.patient.registration.Demographics; // don't need this if use registration.*
import pep.patient.registration.*;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Driver.driver;

/**
 * This class handles the New Patient Registration page, which is composed of several sections, some of which are
 * shared with other registration pages.
 */
public class NewPatientReg {
    private static Logger logger = Logger.getLogger(NewPatientReg.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Demographics demographics;
    public Flight flight;
    public ArrivalLocation arrivalLocation;
    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    private static final By NEW_PATIENT_REG_PAGE_LINK = By.cssSelector("a[href='/tmds/patientReg.html']");
    private static final By patientRegistrationMenuLinkBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static By arrivalLocationTabBy = By.xpath("//td[text()='Arrival/Location']"); // new 2/12/19
    private static final By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/descendant::td[text()='Departure']");
    private static By flightTabBy = By.xpath("//td[text()='Flight']");
    private static final By locationSectionBy = By.id("patientRegistration.treatmentStatus");
    private static final By firstNameField = By.id("firstName");
    private static final By lastNameField = By.id("lastName");
    private static final By ssnField = By.id("ssn");
    private static final By traumaRegisterNumberField = By.id("registerNumber");
    private static final By newPatientRole3RegSearchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
    private static final By errorMessagesBy  = By.id("patientRegistrationSearchForm.errors");
    private static final By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors");
    private static final By searchForPatientButton = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]/descendant::input[@value='Search For Patient']");
    private static final By SUBMIT_BUTTON = By.id("commit");

    public NewPatientReg() {
        if (Arguments.template) {
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.arrivalLocation = new ArrivalLocation();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
            this.departure = new Departure();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            arrivalLocationTabBy = By.xpath("//td[text()='Arrival/Location']");
            flightTabBy = By.xpath("//td[text()='Flight']");
        }
    }

    /**
     * This method navigates to the patient registration page then gets the patient state,
     * and if correct, calls doNewPatientReg()
     * @param patient The patient
     * @return success/true if doNewPatientReg succeeded, false otherwise
     */
    public boolean process(Patient patient) {
        boolean succeeded = false; // true?
        if (patient.registration == null
                || patient.registration.newPatientReg.demographics == null
                || patient.registration.newPatientReg.demographics.firstName == null
                || patient.registration.newPatientReg.demographics.firstName.isEmpty()
                || patient.registration.newPatientReg.demographics.firstName.equalsIgnoreCase("random")
                || patient.registration.newPatientReg.demographics.lastName == null
                || patient.registration.newPatientReg.demographics.lastName.isEmpty()
                || patient.registration.newPatientReg.demographics.lastName.equalsIgnoreCase("random")
                ) {
            if (!Arguments.quiet) System.out.println("  Processing New Patient Registration at " + LocalTime.now() + " ...");
        } else {
            if (!Arguments.quiet)
                System.out.println("  Processing New Patient Registration at " + LocalTime.now() + " for patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
        }

        Utilities.sleep(1555, "NewPatientReg.process(), waiting so navigation doesn't start too soon.");
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, NEW_PATIENT_REG_PAGE_LINK);
        if (!navigated) {
            logger.fine("NewPatientReg.process(), Failed to navigate!!!");
            return false;
        }
        PatientState patientState = getPatientStateFromNewPatientRegSearch(patient);
        switch (patientState) {
            case UPDATE:
                logger.fine("Should switch to Update Patient?  Not going to do that for now.");
                return false;
            case INVALID:
                return false;
            case NEW:
                succeeded = doNewPatientReg(patient);
                break;
            default:
                logger.fine("What status? " + patientState);
                break;
        }
        return succeeded;
    }

    /**
     * This method farms out the processing of the sections to the "doXXXXXSection" methods, and
     * then clicks the Submit button.
     * @param patient The patient for this New Patient page
     * @return success or failure, depending on the subsections
     */
    private boolean doNewPatientReg(Patient patient) {
        boolean succeeded;
        //
        // call the different "doXXXSection" methods
        //
        succeeded = doDemographicsSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doDemographicsSection() failed.");
            return false;
        }
        try {
            Utilities.waitForRefreshedVisibility(arrivalLocationTabBy, 1, "Waiting for arrival location tab to be visible");
            succeeded = doArrivalLocationSection(patient);
            if (!succeeded) {
                logger.fine("NewPatientReg.doNewPatientReg(), doArrivalLocationSection() failed.");
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("Didn't find an arrivalLocationTab.  Possible if Role 4 and Seam or Spring code, or role 3 and Spring");
        }
        try {
            Utilities.waitForVisibility(flightTabBy, 1, "Checking for flight tab's existence.");
            succeeded = doFlightSection(patient);
            if (!succeeded) {
                logger.fine("NewPatientReg.doNewPatientReg(), doFlightSection() failed.");
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("Didn't find Flight tab.  Possible if Role is 3 and Seam code.  But for Roles 3 & 4 Spring there is a Flight section");
        }
        succeeded = doInjuryIllnessSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doInjuryIllnessSection() failed.");
            return false;
        }
        succeeded = doLocationSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doLocationSection() failed.");
            return false;
        }
        succeeded = doDepartureSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doDepartureSection() failed.");
            return false;
        }

        //
        // Take screenshot, and/or pause, as directed.
        //
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSave > 0) {
            Utilities.sleep(Arguments.pauseSave * 1000, "NewPatientReg");
        }
        //
        // Click the Submit button
        //
        Instant start = Instant.now();
        Utilities.clickButton(SUBMIT_BUTTON);
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept();
        }
        catch (Exception e) {
            //logger.fine("No alert about duplicate SSN's.  Continuing...");
        }
        //
        // Handle messages from the submit.
        //
        logger.finest("Waiting for refreshed visibility of error message, max wait is 140 sec.  time is " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 140))
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)));
        }
        catch (Exception e) {
            logger.fine("newPatientReg.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false; // fails: 2
        }
        try {
            String someTextMaybe = webElement.getText();
            if (!(someTextMaybe.contains("Patient's record has been created.") ||
                someTextMaybe.contains("Patient's record has been updated.") ||
                someTextMaybe.contains("Patient's Pre-Registration has been created."))) {
                if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe + " fmp: " + patient.registration.newPatientReg.demographics.fmp + " " + someTextMaybe);
                return false;
            }
        }
        catch (TimeoutException e) {
            logger.fine("newPatientReg.process(), Failed to get message from message area.  TimeoutException: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.fine("newPatientReg.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
            return false;
        }
        //
        // No errors, so report and return true.
        //
        if (!Arguments.quiet) {
            System.out.println("    Saved New Patient record at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "NewPatientReg, requested sleep for page.");
        }
        timerLogger.info("New Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true;
    }

    /**
     * Determine the patient state, and return it.  Probably similar to other methods: New, Pre, Update.
     * Need a state transition diagram, because the resulting state is dependent upon the current state
     * even though you get the same search responses.
     * @param patient the patient for which we determine PatientState
     * @return PatientState, for the patient
     */
    private PatientState getPatientStateFromNewPatientRegSearch(Patient patient) {
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
            return PatientState.NEW;
        }

        // The results differ if the patient is "found", and you're level 3 or 4.
        // If known, and level 3, it fills in the patient Demographics, and no message.
        // If known and level 4 it does not fill in the demographics, and the message is
        // "... already has an open Registration ... > Update Patient page"
        String searchResponseMessage = getNewPatientRegSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);
        if (searchResponseMessage == null) {
            logger.fine("Probably okay to proceed with New Patient Reg.");
            return PatientState.NEW;
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                if (!Arguments.quiet) System.err.println("    ***Search For Patient: " + searchResponseMessage);
            }
        }
        if (searchResponseMessage.contains("There are no patients found.")) {
            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.NEW;
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            logger.severe("***Patient already has an open registration record.  Use Update Patient instead.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.contains("An error occurred while processing")) {
            logger.severe("***Error with TMDS, but we will continue assuming new patient.  Message: " + searchResponseMessage);
            return PatientState.NEW;
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) {
            logger.fine("I think this happens when we're level 3, not 4.  Can update here?  Won't complain later?");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            return PatientState.NEW;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            return PatientState.INVALID;
        }
        logger.fine("What kinda message?: " + searchResponseMessage);
        return PatientState.INVALID;
    }


    /**
     * Do this section of the page by calling its process() method
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doDemographicsSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        Demographics demographics = newPatientReg.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.randomizeSection = this.randomizeSection;
            demographics.shoot = this.shoot;
            newPatientReg.demographics = demographics;
        }
        if (demographics.randomizeSection == null) {
            demographics.randomizeSection = this.randomizeSection;
        }
        if (demographics.shoot == null) {
            demographics.shoot = this.shoot;
        }
        boolean processSucceeded = demographics.process(patient);
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    /**
     * Do this section of the page, if it exists, by calling its process() method
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doArrivalLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        try {
            Utilities.waitForPresence(arrivalLocationTabBy, 1, "NewPatientReg.doArrivalLocationSection()");
            ArrivalLocation arrivalLocation = newPatientReg.arrivalLocation;
            if (arrivalLocation == null) {
                arrivalLocation = new ArrivalLocation();
                newPatientReg.arrivalLocation = arrivalLocation;
            }
            if (arrivalLocation.randomizeSection == null) {
                arrivalLocation.randomizeSection = this.randomizeSection; // removed setting to false if null
            }
            if (arrivalLocation.shoot == null) {
                arrivalLocation.shoot = this.shoot;
            }
            if (arrivalLocation.arrivalDate == null) {
                arrivalLocation.arrivalDate = Arguments.date;
            }
            boolean processSucceeded = arrivalLocation.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process arrival/Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            return true;
        }
        catch (Exception e) {
            System.out.println("Some kind of error with arrivalLocation section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Do this section of the page, if it exists, by calling its process() method
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doFlightSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        try {
            Utilities.waitForPresence(flightTabBy, 1, "NewPatientReg.doFlightSection()");
            Flight flight = newPatientReg.flight;
            if (flight == null) {
                flight = new Flight();
                newPatientReg.flight = flight;
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
            return true;
        }
        catch (Exception e) {
            logger.fine("Some kind of error in flight section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Do this section of the page by calling its process() method
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doInjuryIllnessSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        InjuryIllness injuryIllness = newPatientReg.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            newPatientReg.injuryIllness = injuryIllness;
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
     * Do this section of the page, if it exists, by calling its process() method
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        try {
            Utilities.waitForPresence(locationSectionBy, 1, "NewPatientReg.doLocationSection()");
            Location location = newPatientReg.location;
            if (location == null) {
                location = new Location();
                newPatientReg.location = location;
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
            return true; // this is okay
        }
        catch (StaleElementReferenceException e) {
            logger.fine("Stale reference exception in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.fine("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Do the departure section of the page, if it exists, by calling its process() method
     *
     * If you do a Departure, the "record is closed" and the patient is no longer a patient.  That means you can't update
     * the patient with the Update Patient page.  However, the system allows you to add notes, it appears.
     * So, even if there are treatments to add for this patient, you can do a Departure at this time.
     *
     * @param patient The patient for this new patient registration page
     * @return success or failure (true or false) depending on the results of calling process() on the section
     */
    private boolean doDepartureSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        try {
            Utilities.waitForPresence(departureSectionBy, 1, "NewPatientReg.doDepartureSection()");
            Departure departure = newPatientReg.departure;
            if (departure == null) {
                departure = new Departure();
                newPatientReg.departure = departure;
            }
            if (departure.randomizeSection == null) {
                departure.randomizeSection = this.randomizeSection;
            }
            if (departure.shoot == null) {
                departure.shoot = this.shoot;
            }
            boolean processSucceeded = departure.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process departure for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            return true; // strange way to show that there is no departure section
        }
        catch (Exception e) {
            logger.fine("Some kind of error in departure section?: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Search for a patient and return the response
     * @param ssn for searching for the patient
     * @param firstName for searching for the patient
     * @param lastName for searching for the patient
     * @param traumaRegisterNumber for searching for the patient
     * @return string representing the patient's search results
     */
    private String getNewPatientRegSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        //
        // Insure we have a search section available to fill in and then click.
        //
        try {
            Utilities.waitForPresence(ssnField, 3, "NewPatientReg.(), ssn");
        }
        catch (Exception e) {
            logger.severe("NewPatientReg.getNewPatientRegSearchPatientResponse(), couldn't get the ssn field.");
            return null;
        }
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);
        Utilities.clickButton(searchForPatientButton);
        //
        // Handle popup spinner window
        //
        try {
            Utilities.waitForVisibility(By.id("MB_window"), 20, "NewPatientReg.(), mb window");
            logger.fine("NewPatientReg.getNewPatientRegSearchPatientResponse(), got a spinner window.  Now will try to wait until it goes away.");
            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window")));
            logger.fine("NewPatientReg.getNewPatientRegSearchPatientResponse(), spinner window went away.");
        }
        catch (Exception e) {
            logger.fine("Maybe too slow to get the spinner?  Continuing on is okay.");
        }
        //
        // find and return (success) message
        //
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 3))
                    .until(visibilityOfElementLocated(newPatientRole3RegSearchMessageAreaBy));
            logger.fine("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();
            if (searchMessageText != null) {
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            logger.fine("Timeout out waiting for visibility of a message when a patient is not found.  This is okay for Role3 New Patient Reg.  Got exception: " + Utilities.getMessageFirstLine(e));
        }
        catch (Exception e) {
            logger.fine("Some kind of exception thrown when waiting for error message.  Got exception: " + Utilities.getMessageFirstLine(e));
        }
        //
        // If didn't get regular message, find and return error message
        //
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2))
                    .until(visibilityOfElementLocated(patientRegistrationSearchFormErrorsBy));
            logger.fine("getNewPatientRegSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();
            if (searchMessageText != null) {
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            logger.fine("Timeout out waiting for visibility of a message when a patient is actually found.  This is okay for Role3 New Patient Reg.  Got exception: " + Utilities.getMessageFirstLine(e));
            logger.fine("Maybe just return a fake message like 'no message'?  But with level 4 get a message saying go to Update Patient.");
        }
        catch (Exception e) {
            logger.fine("Some kind of exception thrown when waiting for error message.  Got exception: " + Utilities.getMessageFirstLine(e));
        }
        //
        // If didn't get success or failure message, check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
        //
        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = Utilities.waitForPresence(ssnField, 10, "NewPatientReg.(), ssn");
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
        return null;
    }
}

