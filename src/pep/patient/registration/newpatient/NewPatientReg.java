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

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Driver.driver;

// Registration encompasses Pre-Registration, New Patient Registration, Patient Information, and Update Patient.
// And each of these includes several sections, some of which are shared between these registrations such that
// the elements have the same locators.  There is no Registration class, only a Registration class.

public class NewPatientReg {
    private static Logger logger = Logger.getLogger(NewPatientReg.class.getName());
    public Boolean random;
    public Boolean shoot;
    public Demographics demographics;

    public Flight flight;
    public ArrivalLocation arrivalLocation;

    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    private static By NEW_PATIENT_REG_PAGE_LINK = By.cssSelector("a[href='/tmds/patientReg.html']");

    private static By patientRegistrationMenuLinkBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");

    private static By arrivalLocationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"); // I've not seen this tab/section for a long time
    private static By departureSectionBy       = By.xpath("//*[@id=\"patientRegForm\"]/descendant::td[text()='Departure']");
    private static By flightSectionBy          = By.id("formatArrivalDate"); // this is the first ID'd element in the section
    private static By locationSectionBy        = By.id("patientRegistration.treatmentStatus"); // first ID'd element in the section
    private static By firstNameField = By.id("firstName");
    private static By lastNameField = By.id("lastName");
    private static By ssnField = By.id("ssn");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By newPatientRole3RegSearchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // what the crud?  Diff between role3/role4?  Rename remove Role3
    private static By errorMessagesBy                       = By.id("patientRegistrationSearchForm.errors"); // correct for demo tier
    private static By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors"); // huh?  //*[@id="errors"]/ul/li
    private static By searchForPatientButton = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]/descendant::input[@value='Search For Patient']");
    private static By SUBMIT_BUTTON = By.id("commit");

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
        }

    }

    public boolean process(Patient patient) {
        boolean succeeded = false; // true?
        // We either got here because the default after logging in is this page, or perhaps we deliberately clicked on "Patient Registration" tab.
        if (patient.registration == null
                || patient.registration.newPatientReg.demographics == null
                || patient.registration.newPatientReg.demographics.firstName == null
                || patient.registration.newPatientReg.demographics.firstName.isEmpty()
                || patient.registration.newPatientReg.demographics.firstName.equalsIgnoreCase("random")
                || patient.registration.newPatientReg.demographics.lastName == null
                || patient.registration.newPatientReg.demographics.lastName.isEmpty()
                || patient.registration.newPatientReg.demographics.lastName.equalsIgnoreCase("random")
                ) {
            if (!Arguments.quiet) System.out.println("  Processing New Patient Registration ...");
        } else {
            if (!Arguments.quiet)
                System.out.println("  Processing New Patient Registration for patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
        }

        Utilities.sleep(1555); // was 555
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, NEW_PATIENT_REG_PAGE_LINK);
        //logger.fine("Navigated?: " + navigated);
        if (!navigated) {
            logger.fine("NewPatientReg.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2, test: 1
        }
        // seems like the page doesn't get updated fast enough
        PatientState patientState = getPatientStateFromNewPatientRegSearch(patient); // No longer: this sets skipRegistration true/false depending on if patient found
        switch (patientState) {
            case UPDATE: // we're in New Patient Reg, but TMDS said "xxx already has an open Registration record. Please update the patient via Patient Registration  Update Patient page."
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

    boolean doNewPatientReg(Patient patient) {
        boolean succeeded;
        // I think that the returns of the following sections should not be counted as errors if the sections don't exist.
        succeeded = doDemographicsSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doDemographicsSection() failed.");
            return false;
        }

        // Does New Patient Reg have an Arrival Location Section?
        succeeded = doArrivalLocationSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doArrivalLocationSection() failed.");
            return false;
        }

        succeeded = doFlightSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doFlightSection() failed.");
            return false;
        }

        succeeded = doInjuryIllnessSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doInjuryIllnessSection() failed.");
            return false;
        }

        succeeded = doLocationSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doLocationSection() failed.");
            return false; // never happens because always returns true
        }
        // there is no DepartureSection for Role 4, and this will return true
        succeeded = doDepartureSection(patient);
        if (!succeeded) {
            logger.fine("NewPatientReg.doNewPatientReg(), doDepartureSection() failed.");
            return false;
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        if (Arguments.pauseSave > 0) {
            Utilities.sleep(Arguments.pauseSave * 1000);
        }
        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Instant start = Instant.now();
        Utilities.clickButton(SUBMIT_BUTTON); // Not AJAX, but does call something at /tmds/registration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
        // The above line may generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        // following is new:
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            // TMDS New Patient Reg page will alert that there's already a patient with that SSN.  If you click accept on the alert
            // another patient with that SSN will be created.  Do we want to do that?  If we're doing random, then no.
            // But this wouldn't happen if we first searched for the patient, and if there was a match generate a new one, if random.
            // Still hoping that won't run across too many duplicates.
            // "OK" == accept(), "Cancel" == dismiss()
            someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
            //someAlert.dismiss(); // Opposite of accept?  And if we do, what happens?
        }
        catch (Exception e) {
            //logger.fine("No alert about duplicate SSN's.  Continuing...");
        }

        // problem area
        WebElement spinnerPopupWindow = null;
        try {
            By spinnerPopupWindowBy = By.id("MB_window");
            // This next line assumes execution gets to it before the spinner goes away.
            // Also the next line can throw a WebDriverException due to an "unexpected alert open: (Alert text : The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
            spinnerPopupWindow = Utilities.waitForVisibility(spinnerPopupWindowBy, 30, "NewPatientReg.doNewPatientReg()"); // was 15
        }
        catch (Exception e) {
            logger.fine("Couldn't wait for visibility of spinner.  Will continue.  Exception: " + Utilities.getMessageFirstLine(e));
        }
        try {
            (new WebDriverWait(Driver.driver, 180)).until(ExpectedConditions.stalenessOf(spinnerPopupWindow)); // do invisibilityOfElementLocated instead of staleness?
        }
        catch (TimeoutException e) {
            logger.fine("Couldn't wait for staleness of spinner window.  Exception: " + Utilities.getMessageFirstLine(e));
        }
        catch (Exception e) {
            logger.fine("Some other exception in NewPatientReg.doNewPatientReg(): " + Utilities.getMessageFirstLine(e)); // prob short message
        }


        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 140)) //  Can take a long time on gold
                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy))); // fails: 2
        }
        catch (Exception e) {
            logger.fine("newPatientReg.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (someTextMaybe.contains("Patient's record has been created.")) {
            }
            else if (someTextMaybe.contains("Patient's record has been updated.")) { // unlikely because we're in New Patient Reg., not Update Patient
            }
            else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) { // so for Role 4 "Pre-Registration" is all you can do here?
            }
            else {
                if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe + " fmp: " + patient.registration.newPatientReg.demographics.fmp + " sometextmaybe: " + someTextMaybe);
                return false;
            }
        }
        catch (TimeoutException e) { // hey this should be impossible.
            logger.fine("newPatientReg.process(), Failed to get message from message area.  TimeoutException: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.fine("newPatientReg.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("    Saved New Patient record for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        timerLogger.info("New Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true; // success ??????????????????????????
    }

    // This was meant to show the various states a patient or person could be in, but it's not clear what is needed yet.
    // A person becomes a patient.  They could be preregistered, they could be admitted, they could be inpatient or outpatient,
    // They could be 'departed'.  Their registration could get updated.  I don't know this stuff yet.

    PatientState getPatientStateFromNewPatientRegSearch(Patient patient) { // change name to state

        boolean skipSearch = false;
        String firstName = null;
        String lastName = null;
        String ssn = null;
        String traumaRegisterNumber = null;

        // Let's see what we've got, if anything from the PatientSearch stuff.  This doesn't make sense.  Just experimenting
        if (patient.patientSearch != null) {
            firstName = patient.patientSearch.firstName;
            lastName = patient.patientSearch.lastName;
            ssn = patient.patientSearch.ssn;
            traumaRegisterNumber = patient.patientSearch.traumaRegisterNumber;
        }
        if (patient.patientSearch == null) {
            skipSearch = true; // not quite right.  patientSearch is still optional
        }

        //Pep.PatientStatus patientStatus = null;
        PatientState patientStatus = PatientState.INVALID;

        // Not sure how worthwhile this is
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            return PatientState.NEW; // ???????????????
        }

        // Here comes the big search (easy to miss).
        // The results differ if the patient is "found", and you're level 3 or 4.
        // If known, and level 3, it fills in the patient Demographics, and no message.
        // If known and level 4 it does not fill in the demographics, and the message is
        // "... already has an open Registration ... > Update Patient page"
        // Why is that?
        // If level 3, and patient is found can you add or modify information?
        // And if you do that, can you save the changes, or does it then say "You gotta do this in Update Patient"?
        // And if the patient is NOT found, there's this message:  "There are no patients found." (For both levels 3 and 4)

        // The problem is that these SearchForPatient sections give different responses depending on the page they're on.
        // So maybe we can do New Patient Reg search first, and if that doesn't work for some reason, do the Update Patient search.

        String searchResponseMessage = getNewPatientRegSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);

        if (searchResponseMessage == null) {
            logger.fine("Probably okay to proceed with New Patient Reg.");
            //return Pep.PatientStatus.NEW;
            return PatientState.NEW;
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                if (!Arguments.quiet) System.err.println("    ***Search For Patient: " + searchResponseMessage);
            }
        }
        if (searchResponseMessage.contains("There are no patients found.")) {
            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.NEW; // not sure
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            logger.severe("***Patient already has an open registration record.  Use Update Patient instead.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.contains("An error occurred while processing")) {
            logger.severe("***Error with TMDS, but we will continue assuming new patient.  Message: " + searchResponseMessage);
            return PatientState.NEW; // Not invalid.  TMDS has a bug.
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            logger.fine("I think this happens when we're level 3, not 4.  Can update here?  Won't complain later?");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            return PatientState.NEW; // Does this mean the patient's record was previously closed?  If so, shouldn't we continue on?
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            return PatientState.INVALID;
        }
        logger.fine("What kinda message?: " + searchResponseMessage);
        return patientStatus;
    }


    boolean doDemographicsSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;

        Demographics demographics = newPatientReg.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.random = this.random; // removed setting to false if null // new, and unnec bec just below
            demographics.shoot = this.shoot; // new, and unnec bec just below
            newPatientReg.demographics = demographics;
        }
        if (demographics.random == null) {
            demographics.random = this.random; // removed setting to false if null
        }
        if (demographics.shoot == null) {
            demographics.shoot = this.shoot;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        //if (Utilities.elementExistsShorterWait(By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"), 1000) != null) {
        return processSucceeded;
    }

    // Hey, is this section available for a Role 1 CASF?  And others too?  Which roles don't?
    boolean doArrivalLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        // Do ArrivalLocation section, if it exists for this level/role
        try {
            //(new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(arrivalLocationSectionBy));
            Utilities.waitForPresence(arrivalLocationSectionBy, 1, "NewPatientReg.doArrivalLocationSection()");
            ArrivalLocation arrivalLocation = newPatientReg.arrivalLocation;
            if (arrivalLocation == null) {
                arrivalLocation = new ArrivalLocation();
                newPatientReg.arrivalLocation = arrivalLocation;
            }
            if (arrivalLocation.random == null) {
                arrivalLocation.random = this.random; // removed setting to false if null
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
            //logger.fine("No arrivalLocation section.  Okay.");
            return true;
        }
        catch (Exception e) {
            System.out.println("Some kind of error with arrivalLocation section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    boolean doFlightSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        // Flight (only available in Level 4)
        try {
            //(new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(flightSectionBy));
            Utilities.waitForPresence(flightSectionBy, 1, "NewPatientReg.doFlightSection()");
            Flight flight = newPatientReg.flight;
            if (flight == null) {
                flight = new Flight();
                newPatientReg.flight = flight;
            }
            if (flight.random == null) {
                flight.random = this.random; // removed setting to false if null // can't let this be null
            }
            if (flight.shoot == null) {
                flight.shoot = this.shoot; // can't let this be null
            }
            boolean processSucceeded = flight.process(patient); // flight has required fields in it, so must do it
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process flight for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //logger.fine("There's no flight section, which is the case for levels/roles 1,2,3");
            return true; // a little hack here
        }
        catch (Exception e) {
            logger.fine("Some kind of error in flight section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    boolean doInjuryIllnessSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        // Injury/Illness must also contain information.  Can't skip it.
        InjuryIllness injuryIllness = newPatientReg.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            newPatientReg.injuryIllness = injuryIllness;
        }
        if (injuryIllness.random == null) {
            injuryIllness.random = this.random; // removed setting to false if null
        }
        if (injuryIllness.shoot == null) {
            injuryIllness.shoot = this.shoot;
        }
        boolean processSucceeded = injuryIllness.process(patient); // contains required fields, so must do this.
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        // Location (for level 4 only?)  The following takes a bit of time.  Change to have xpath with string "Location"?
        try {
//            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(locationSectionBy)); // was 1s
            Utilities.waitForPresence(locationSectionBy, 1, "NewPatientReg.doLocationSection()");
            Location location = newPatientReg.location;
            if (location == null) {
                location = new Location();
                //location.random = this.random; // new
                newPatientReg.location = location;
            }
            if (location.random == null) {
                location.random = this.random; // removed setting to false if null
            }
            if (location.shoot == null) {
                location.shoot = this.shoot;
            }
            boolean processSucceeded = location.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded; // this is always true because location.process() always returns true.
        }
        catch (TimeoutException e) {
            //logger.fine("There's no location section, which is the case for levels/roles 1,2,3");
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
    // Is there a Departure section for New Patient Reg?  Maybe in a role 3?  Maybe in old Seam version?
    // Maybe this section can be removed in this file.
    boolean doDepartureSection(Patient patient) {
        NewPatientReg newPatientReg = patient.registration.newPatientReg;
        // Departure
        // If you do a Departure, the "record is closed" and the patient is no longer a patient.  That means you can't update
        // the patient with the Update Patient page.  However, the system allows you to add notes, it appears.
        // So, even if there are treatments to add for this patient, you can do a Departure at this time.
        try { // fix this next By to shorter/better when we do have a departure section.  Does this ever happen for New Patient?  I don't think so
            Utilities.waitForPresence(departureSectionBy, 1, "NewPatientReg.doDepartureSection()");
            Departure departure = newPatientReg.departure;
            if (departure == null) {
                departure = new Departure();
                //departure.random = this.random; // new
                newPatientReg.departure = departure;
            }
            if (departure.random == null) {
                departure.random = this.random; // removed setting to false if null
            }
            if (departure.shoot == null) {
                departure.shoot = this.shoot;
            }
            boolean processSucceeded = departure.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process departure for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //logger.fine("There's no departure section.  That doesn't seem right.  Is it?  returning true");
            return true; // strange way to show that there is no departure section
        }
        catch (Exception e) {
            logger.fine("Some kind of error in departure section?: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    String getNewPatientRegSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        String message = null;
        try {
            Utilities.waitForPresence(ssnField, 3, "classMethod");
        }
        catch (Exception e) {
            logger.severe("NewPatientReg.getNewPatientRegSearchPatientResponse(), couldn't get the ssn field.");
            return null;
        }
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);


        Utilities.clickButton(searchForPatientButton); // Not ajax
        // Hey, compare with the other spnner check in this file.  Does a stalenessOf rather than an invisibilityOf
        try {
            Utilities.waitForVisibility(By.id("MB_window"), 20, "classMethod"); // was 2s, was 10s
            logger.fine("NewPatientReg.getNewPatientRegSearchPatientResponse(), got a spinner window.  Now will try to wait until it goes away.");
            // Next line can throw a timeout exception if the patient has a duplicate.  That is, same name and ssn.  Maybe even same trauma number.  Because selection list comes up. Peter Pptest 666701231
            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window"))); // was after catch
            logger.fine("NewPatientReg.getNewPatientRegSearchPatientResponse(), spinner window went away.");
        }
        catch (Exception e) {
            logger.fine("Maybe too slow to get the spinner?  Continuing on is okay.");
        }
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 3)) // was 1s
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

        // This one should work for New Patient Reg. search, but not for Update Patient search
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
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

        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
        // I wonder why I couldn't do the same thing elsewhere, perhaps in UpdatePatient, or other places.  Just wouldn't work.  Programming mistake?
        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = Utilities.waitForPresence(ssnField, 10, "classMethod");
//            if (ssnTextBoxElement != null) {
//                //logger.fine("I guess ssnbox is available now");
//                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
//                if (ssnTextBoxAttribute != null) {
//                    logger.fine("ssnTextBoxAttribute: " + ssnTextBoxAttribute);
//                }
//                else {
//                    logger.fine("I guess there was no ssntextbox attribute");
//                }
//            }
//            else {
//                logger.fine("didn't get a ssnTextBoxelement for some unknown reason.");
//            }
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
                    logger.fine("Grayed out."); // Next line right????????????
                    return "Search fields grayed out.";
                }
            }
        }
        return message;
    }
}

