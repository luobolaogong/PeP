package pep.patient.registration.updatepatient;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.patient.registration.*;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;
import pep.patient.registration.Demographics;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pep.Main.timerLogger;

import static pep.patient.PatientState.UPDATE;
import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Driver.driver;

/**
 * This class represents the Update Patient registration page, and the sections that belongs to it, some of which are shared with other registration pages.
 */
public class UpdatePatient {
    private static Logger logger = Logger.getLogger(UpdatePatient.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public Demographics demographics;
    public Flight flight;
    public ArrivalLocation arrivalLocation;
    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    private static By PATIENT_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static By SUBMIT_BUTTON = By.id("commit");
    private static By UPDATE_PATIENT_PAGE_LINK = By.cssSelector("a[href='/tmds/patientUpdate.html']"); // this often fails on TEST, but it's valid.  It's jumping to Patient Info on role 3!!!!!
    private static By arrivalLocationTabBy = By.xpath("//td[text()='Arrival/Location']"); // new 2/12/19
    private static By flightTabBy = By.xpath("//td[text()='Flight']"); // new 2/12/19
    private static By departureSectionBy = By.xpath("//td[text()='Departure']");
    private static By locationSectionBy = By.xpath("//td[text()='Location']");
    private static By searchForPatientButton = By.xpath("//input[@value='Search For Patient']");
    private static By someStupidContinueButtonOnSensitiveInfoPopupBy = By.xpath("//input[@class='button-normal']");
    private static By firstNameField = By.id("firstName");
    private static By lastNameField = By.id("lastName");
    private static By ssnField = By.id("ssn");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By errorMessagesBy = By.id("patientRegistrationSearchForm.errors"); // correct
    private static By errorsSearchMessageBy = By.xpath("//*[@id='errors']/ul/li");
    private static By arrivalLocationSectionBy = By.xpath("//*[@id='patientRegForm']/table/tbody/tr/td[2]/table[2]/tbody/tr/td");

    public UpdatePatient() {
        if (Arguments.template) {
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.arrivalLocation = new ArrivalLocation();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
            this.departure = new Departure();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            // nothing special
        }
    }

    /**
     * This method merely tries to navigate to Update Patient page and check if patient is ready to be updated.
     * @param patient The patient
     * @return true if page could be navigated to and the update patient method returns true
     */
    public boolean process(Patient patient) {
        boolean succeeded = false;
        if (patient.registration == null
                || patient.registration.updatePatient.demographics == null
                || patient.registration.updatePatient.demographics.firstName == null
                || patient.registration.updatePatient.demographics.firstName.isEmpty()
                || patient.registration.updatePatient.demographics.firstName.equalsIgnoreCase("random")
                || patient.registration.updatePatient.demographics.lastName == null
                || patient.registration.updatePatient.demographics.lastName.isEmpty()
                || patient.registration.updatePatient.demographics.lastName.equalsIgnoreCase("random")
                ) {
            //if (!Arguments.quiet) System.out.println("  Processing Registration at " + LocalTime.now() + " ...");
            if (!Arguments.quiet) System.out.println("  Processing Update Patient at " + LocalTime.now() + " ...");
        } else {
            if (!Arguments.quiet)
                System.out.println("  Processing Update Patient at " + LocalTime.now() + " for patient" +
                        (patient.registration.updatePatient.demographics.firstName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.firstName)) +
                        (patient.registration.updatePatient.demographics.lastName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.lastName)) +
                        (patient.registration.updatePatient.demographics.ssn.isEmpty() ? "" : (" ssn:" + patient.registration.updatePatient.demographics.ssn)) + " ..."
                );
        }
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, UPDATE_PATIENT_PAGE_LINK); // this last link often fails
        if (!navigated) {
            return false;
        }
        try { // execution gets to next line before navigation is done.  I assume that's why we've got this next wait
            Utilities.waitForVisibility(By.id("patientRegForm"), 5, "UpdatePatient.process()"); // new 1/17/19
        }
        catch (Exception e) {
            logger.severe("UpdatePatient.process(), couldn't wait for visibility of patient form.  e: " + Utilities.getMessageFirstLine(e));
        }
        PatientState patientState = getPatientStateFromUpdatePatientSearch(patient); // what does this do???  I think this is what can bring up the Sensitive Inforation page that doesn't get dismissed!!!!

        // !!!!!!!!!!!!!!!!!!!!!Before we get here, if there had been a sensitive information page, it should have been dismissed
        // Is that right???????????????????????????????????????????????????????????????????????????????
        if (patientState == UPDATE) {
            // Possible that WE HAVE A SENSITIVE INFORMATION PAGE SHOWING A THIS TIME!  How handled?
            logger.fine("Do not continue on, to do an update patient if we're sitting at a Sensitive Information page!");
            // the sensitive info page should have gone away by this point.  Set that flag to test.
            // !!!!!!!!!!!! Hey, we should not proceed if we're sitting at a Sensitive Information page
            // So how do we detect this?  What causes this not to get resolved?
            // !!!!!!!!!!!! This next thing will fail if we have an unresolved Sensitive Information page
            succeeded = doUpdatePatient(patient);
        }
        return succeeded;
    }

    /**
     * Do a search for the patient, and return an indication of the current "state" of the patient.  Could be
     * the patient can be updated.  Maybe not.  This is slightly different version from something similar in
     * New Patient Reg., particularly related to sensitive info popup.
     * @param patient The patient in question
     * @return PatientState has different possible values, one of which is Update capable
     */
    private PatientState getPatientStateFromUpdatePatientSearch(Patient patient) {
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
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            return PatientState.NEW;
        }
        String searchResponseMessage = getUpdatePatientSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);
        if (searchResponseMessage == null) {
            return PatientState.INVALID;
        }
        if (searchResponseMessage.equalsIgnoreCase("Registered")) {
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.equalsIgnoreCase("No record found to update.")) { // something's wrong
            System.out.println("We want to update a patient, but the specified patient isn't found.");
            return PatientState.INVALID;
        }
        if (searchResponseMessage.contains("There are no patients found.")) {
            //if (!Arguments.info) System.err.println("    ***Error encountered for Update Patient: " + searchResponseMessage);
            logger.fine("This message of 'There are no patients found.' doesn't make sense if we jumped to Update Patient.");
            logger.fine("This is due to a bug in TMDS Update Patient page for a role 4, it seems.  Also role 3, Gold");
            logger.fine("UpdatePatient.getPatientStateFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
//            return PatientState.INVALID;
            return PatientState.UPDATE; // changing to UPDATE assuming the bug was fixed.
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            if (!Arguments.quiet) System.err.println("    ***Error encountered for Update Patient: " + searchResponseMessage);
            logger.fine("Prob should switch to either Update Patient or go straight to Treatments.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) {
            logger.fine("I think this happens when we're level 3, not 4.  No, happens with 4.  Can update here?  Won't complain later?");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            if (!Arguments.quiet) System.out.println("    Skipping remaining Registration Processing for " + patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName + " at " + LocalTime.now() + " ...");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            if (!Arguments.quiet) System.err.println("    ***Failed to accept search field because not alphanumeric.");
            return PatientState.INVALID;
        }
        logger.fine("What kinda message?: " + searchResponseMessage);
        return PatientState.INVALID;
    }


    /**
     * Process the different parts of the Update Patient registration page, by calling the process() methods on them.
     * @param patient The patient to do the update on
     * @return success or failure depending on the success or failure of the sections of this page
     */
    boolean doUpdatePatient(Patient patient) {
        //
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Hey, we should not proceed if we are not on the right page.  We may be on a Sensitive Information page.  Yes!!! This happens!!!
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        boolean succeeded;
        succeeded = doDemographicsSection(patient); // this will fail if sensitive inf
        if (!succeeded) {
            return false; // Hey, maybe it was okay?
        }
        try {
            Utilities.waitForVisibility(arrivalLocationTabBy, 1, "UpdatePatient.doUpdatePatient(), checking for arrival/location tab");
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
            Utilities.waitForVisibility(flightTabBy, 1, "UpdatePatient.doUpdatePatient(), checking for flight sectino tab.");
            logger.finest("Got a FlightTab");
            succeeded = doFlightSection(patient); // what about later when do doFlightSection?????
            if (!succeeded) {
                logger.fine("NewPatientReg.doNewPatientReg(), doFlightSection() failed.");
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("Didn't find Flight tab.  Possible if Role is 3 and Seam code.  But for Roles 3 & 4 Spring there is a Flight section");
        }
//        succeeded = doArrivalLocationSection(patient);
//        if (!succeeded) {
//            return false;
//        }
//        succeeded = doFlightSection(patient);
//        if (!succeeded) {
//            return false;
//        }
        succeeded = doInjuryIllnessSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doLocationSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doDepartureSection(patient);
        if (!succeeded) {
            return false;
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        if (!this.skipSave) {
            Instant start = Instant.now();
            Utilities.clickButton(SUBMIT_BUTTON);
            // The above line may generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
            try {
                (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
                WebDriver.TargetLocator targetLocator = driver.switchTo();
                Alert someAlert = targetLocator.alert();
                someAlert.accept();
            } catch (Exception e) {
                logger.fine("UpdatePatient.doUpdatePatient(), No alert.  Continuing...");
            }
            WebElement webElement;
            try { // did we get here a little bit too soon?  Does it matter?
                webElement = Utilities.waitForRefreshedVisibility(errorMessagesBy, 90, "UpdatePatient.doUpdatePatient(), waiting for error messages area.");
            } catch (Exception e) {
                logger.severe("updatePatient.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            try {
                String someTextMaybe = webElement.getText();
                if (someTextMaybe.contains("Patient's record has been created.")) { // unlikely, because we're in Update Patient, not New Patient Reg.
                    logger.finer("updatePatient.process(), Message indicates patient's record was created: " + someTextMaybe);
                } else if (someTextMaybe.contains("Patient's record has been updated.")) {
                    logger.fine("updatePatient.process(), Message indicates patient's record was updated: " + someTextMaybe);
                    if (!Arguments.quiet) {
                        System.out.println("    Saved Update Patient record at " + LocalTime.now() + " for patient" +
                                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                        );
                    }
                } else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) {
                    logger.fine("updatePatient.process(), I guess this is okay for Role 4: " + someTextMaybe);
                } else {
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed trying to save patient " + patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName + " : " + someTextMaybe);
                    return false;
                }
            } catch (Exception e) {
                logger.severe("updatePatient.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
                return false;
            }
            logger.finer("updatePatient.process() I guess we got some kind of message, and now returning true.");
            timerLogger.fine("Update Patient for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " saved in " + ((Duration.between(start, Instant.now()).toMillis()) / 1000.0) + "s");
        }
        else {
            if (!Arguments.quiet) {
                System.out.println("    Not saving Update Patient info.");
            }
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "UpdatePatient, requested sleep for page.");
        }
        return true;
    }

    /**
     * This method merely calls Demographics.process().
     * @param patient The patient for the demographics info
     * @return
     */
    boolean doDemographicsSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        Demographics demographics = updatePatient.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.randomizeSection = this.randomizeSection;
            demographics.shoot = this.shoot;
            updatePatient.demographics = demographics;
        }
        if (demographics.randomizeSection == null) {
            demographics.randomizeSection = this.randomizeSection;
        }
        if (demographics.shoot == null) {
            demographics.shoot = this.shoot;
        }
        // Next line can fail if there's a sensitive record.  Not a problem for NewRegistration, I think, just Update Patient
        boolean processSucceeded = demographics.process(patient);
        if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    /**
     * This method merely calls ArrivalLocation.process().  Not sure if this should be here.  New Patient Reg only?
     * @param patient The patient for this arrival info
     * @return success or failure at calling ArrivalLocation.process()
     */
    boolean doArrivalLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        try {
            Utilities.waitForPresence(arrivalLocationSectionBy, 1, "UpdatePatient.(), arrival location section");
            ArrivalLocation arrivalLocation = updatePatient.arrivalLocation;
            if (arrivalLocation == null) {
                arrivalLocation = new ArrivalLocation();
                updatePatient.arrivalLocation = arrivalLocation;
            }
            if (arrivalLocation.randomizeSection == null) {
                arrivalLocation.randomizeSection = this.randomizeSection;
            }
            if (arrivalLocation.shoot == null) {
                arrivalLocation.shoot = this.shoot;
            }
            if (arrivalLocation.arrivalDate == null) {
                arrivalLocation.arrivalDate = Arguments.date;
            }
            boolean processSucceeded = arrivalLocation.process(patient);
            if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process arrival/Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            // No arrivalLocation section probably
            return true;
        }
        catch (Exception e) {
            logger.severe("Some kind of error with arrivalLocation section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * This method merely calls Flight.process().  Flight is only available for a Role 4
     * @param patient The patient for this flight section
     * @return success or failure at calling Flight.process()
     */
    boolean doFlightSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        try {
            Utilities.waitForPresence(flightTabBy, 1, "UpdatePatient.doFlightSection() waiting for flight section tab");
            Flight flight = updatePatient.flight;
            if (flight == null) {
                flight = new Flight();
                updatePatient.flight = flight;
            }
            if (flight.randomizeSection == null) {
                flight.randomizeSection = this.randomizeSection;
            }
            if (flight.shoot == null) {
                flight.shoot = this.shoot;
            }
            boolean processSucceeded = flight.process(patient);
            if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process flight for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            // There's no flight section, which is the case for levels/roles 1,2,3
            return true; // small hack
        }
        catch (Exception e) {
            logger.severe("Some kind of error in flight section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * This method calls InjuryIllness.process()
     * @param patient The patient for this section
     * @return success or failure of performing InjuryIllness.process()
     */
    boolean doInjuryIllnessSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Injury/Illness must also contain information.  Can't skip it.
        InjuryIllness injuryIllness = updatePatient.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            updatePatient.injuryIllness = injuryIllness;
        }
        if (injuryIllness.randomizeSection == null) {
            injuryIllness.randomizeSection = this.randomizeSection;
        }
        if (injuryIllness.shoot == null) {
            injuryIllness.shoot = this.shoot;
        }
        boolean processSucceeded = injuryIllness.process(patient);
        if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    /**
     * Do the location section of the Update Patient page.
     * @param patient The patient for this location info
     * @return the result of calling location.process()
     */
    boolean doLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        try {
            Utilities.waitForPresence(locationSectionBy, 1, "UpdatePatient.doLocationSection()");
            Location location = updatePatient.location;
            if (location == null) {
                location = new Location();
                updatePatient.location = location;
            }
            if (location.randomizeSection == null) {
                location.randomizeSection = this.randomizeSection;
            }
            if (location.shoot == null) {
                location.shoot = this.shoot;
            }
            boolean processSucceeded = location.process(patient);
            if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            // There's no location section, which is the case for levels/roles 1,2,3
            return true;
        }
        catch (Exception e) {
            logger.severe("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * This method merely calls the process() method for Departure information in the input file.
     * If you do a Departure, the "record is closed" and the patient is no longer a patient.  That means you can't update
     * the patient with the Update Patient page.  However, the system allows you to add notes, it appears.
     * @param patient The patient this is for
     * @return success or failure of the Departure.process() call
     */
    boolean doDepartureSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        try {
            Utilities.waitForPresence(departureSectionBy, 1, "UpdatePatient.doDepartureSection()");
            Departure departure = updatePatient.departure;
            if (departure == null) {
                departure = new Departure();
                updatePatient.departure = departure;
            }
            if (departure.randomizeSection == null) {
                departure.randomizeSection = this.randomizeSection;
            }
            if (departure.shoot == null) {
                departure.shoot = this.shoot;
            }
            if (departure.departureDate == null) {
                departure.departureDate = Arguments.date;
            }
            boolean processSucceeded = departure.process(patient);
            if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process departure for patient" +
                    (patient.registration.updatePatient.demographics.firstName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.firstName)) +
                    (patient.registration.updatePatient.demographics.lastName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.lastName)) +
                    (patient.registration.updatePatient.demographics.ssn.isEmpty() ? "" : (" ssn:" + patient.registration.updatePatient.demographics.ssn)) + " ..."
            );
            return processSucceeded;
        }
        catch (TimeoutException e) {
            logger.finest("There's no departure section????  That seems wrong.  Prob shouldn't get here.  returning true");  // it does get here level 3
            return true;
        }
        catch (Exception e) {
            logger.severe("Some kind of error in departure section?: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Do a search for a patient based on the params, and return the resulting string response
     * @param ssn of the patient
     * @param firstName of the patient
     * @param lastName of the patient
     * @param traumaRegisterNumber of the patient
     * @return the response message to the search
     */
    private String getUpdatePatientSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        // A search may cause Sensitive Information window to appear which screws up Selenium locators.
        // Get the number of windows before click to compare with after click.
        int nWindowHandlesBeforeClick = Driver.driver.getWindowHandles().size(); // 1, if no Sensitive Information window??

        try {
            Utilities.waitForRefreshedVisibility(ssnField,  5, "UpdatePatient.getUpdatePatientSearchPatientResponse(), ssn");
        }
        catch (Exception e) {
            logger.severe("UpdatePatient.getUpdatePatientSearchPatientResponse(), couldn't get the ssn field.  But will continue on.  e: " + Utilities.getMessageFirstLine(e));
        }
        Utilities.sleep(2555, "UpdatePatient.getUpdatePatientSearchPatientResponse(), about to fill in ssn, last, first, trauma");
        //
        // Load up the search fields
        //
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);
        //
        // Find and click the search button
        //
        WebElement searchButton = null;
        try {
            searchButton = Utilities.waitForRefreshedClickability(searchForPatientButton, 5, "UpdatePatient.(), search for patient button");
            searchButton.click(); // can cause sensitive info popup
        }
        catch (Exception e) {
            logger.fine("UpdatePatient.getUpdatePatientSearchPatientResponse(), Couldn't get the search button or click on it.");
            return null;
        }
        try { // If pause here long enough, no errors, I think.  So, now removing stop, and let's see.
            logger.fine("Here comes a wait for a stale search button");
//            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(searchButton)); // add to Utilities.
            Utilities.waitForStaleness(searchButton, 5, "UpdatePatient.getUpdatePatientSearchPatientResponse(), waiting for stale search button.");
        }
        catch (Exception e) {
            logger.fine("Exception caught while waiting for staleness of search button."); // fails:2
        }
//Level currentLevel = logger.getLevel();
        //logger.setLevel(Level.FINE);
        logger.fine("Done trying on the staleness thing.  May have failed.  Now gunna sleep long time."); // I think the following sleep may not be effective.  We get here same time Sensitive Info windows pops up
        Utilities.sleep(4555, "UpdatePatient, getUpdatePatientSearchPatientResponse(), gunna get window handle and try to handle sensitive record, waiting until continue button shows up, maybe"); // was 2555 , then was 555, now 1555, now back to 2555.  Hate to do this, but the Sensitive Information window isn't showing up fast enough.  Maybe can do a watch for stale window or something?
        logger.fine("Done sleeping.");
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Handle the possibility of a Sensitive Information window.  We get to the next line WAY before the patient is found and analyzed if there's sensitivity
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String mainWindowHandleAfterClick = Driver.driver.getWindowHandle();
        Set<String> windowHandlesSetAfterClick = Driver.driver.getWindowHandles();
        int nWindowHandlesAfterClick = windowHandlesSetAfterClick.size(); // it's 2 if there is a Sensitive Information page showing, I think.
        if (nWindowHandlesAfterClick != nWindowHandlesBeforeClick) { // we picked up a window due to the search button click, which may be a "Sensitive Information" window.
            Iterator<String> newWindowHandlesIterator = windowHandlesSetAfterClick.iterator();
            while (newWindowHandlesIterator.hasNext()) {
                String windowHandleFromSetAfterClick = newWindowHandlesIterator.next();
                if (mainWindowHandleAfterClick.equals(windowHandleFromSetAfterClick)) {
                    continue;
                }
                try {
                    logger.fine("Switching to window handle in the set, with iterator.");
                    Driver.driver.switchTo().window(windowHandleFromSetAfterClick); // nec?
                    logger.fine("Waiting for continue button to be clickable.");
                    WebElement continueButton = Utilities.waitForRefreshedClickability(someStupidContinueButtonOnSensitiveInfoPopupBy, 5, "UpdatePatient.(), continue button on sensitive info");

                    // This click is a big deal.  Does it get rid of the sensitive info window or not????  Get here too fast?
                    continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.


                    logger.fine("Gunna switch to main window after click");
                    Driver.driver.switchTo().window(mainWindowHandleAfterClick);
                    logger.fine("Going to find a frame.");
                    WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
                    Driver.driver.switchTo().frame(someFrame); // is this nec?
                }
                catch (Exception e) {
                    logger.finer("e: " + Utilities.getMessageFirstLine(e));
                }
                break; // why exit the loop here?  Assuming Sensitive Information window was removed?
            }
        }
        //
        // Locate the search message response text if there is one, and return it.  No message is there, ever?
        //
        try {
            logger.fine("UpdatePatient.getUpdatePatientSearchPatientResponse(), here comes a wait for visibility of some error text, which probably isn't there.");
            WebElement searchMessageWebElement = Utilities.waitForVisibility(errorsSearchMessageBy, 1, "UpdatePatient.getUpdatePatientSearchPatientResponse(), waiting for errors search message area");
            logger.fine("getUpdatePatientSearchPatientResponse(), search message: " + searchMessageWebElement.getText());
            String searchMessageText = searchMessageWebElement.getText();

            if (searchMessageText != null) {
                if (searchMessageText.equalsIgnoreCase("There are no patients found.")) {
                    logger.fine("Got this message 'There are no patients found.' which can happen for Role 3 Update Patient search ");
                    logger.fine("perhaps because the patient was transferred out?  Is this expected/correct?");
                    logger.fine("If this happens for Role 4, then there's some other problem.");
                }
                else {
                    logger.fine("The search for a patient in Update Patient yielded this message: " + searchMessageText);
                    logger.fine("Should that prohibit Update Patient from working?");
                }
//logger.setLevel(currentLevel);
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for visibility of a message for Update Patient search.  Got exception: " + Utilities.getMessageFirstLine(e));
            logger.fine("No message when patient is found.  I think different for New Patient Reg, which displays message.  Really?  When found?  Or just when not found?");
            logger.fine("For Role 4 Update Patient it seems the patient was found, even when there was a transfer.");
//logger.setLevel(currentLevel);
            return "Registered";  // bad way to find out registered?
        }
        catch (Exception e) {
            logger.finer("Some kind of exception thrown when waiting for error message.  Got exception: " + Utilities.getMessageFirstLine(e));
        }
//logger.setLevel(currentLevel);
        return null;
    }
}

