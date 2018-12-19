package pep.patient.registration.preregistration;

import org.openqa.selenium.*;
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
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Driver.driver;

// This only applies to Role 4, it seems.

public class PreRegistration {
    private static Logger logger = Logger.getLogger(PreRegistration.class.getName());
    public Boolean random;
    public Boolean shoot;
    public Demographics demographics;
    // It will be Flight (level 4) or ArrivalLocationSection (levels 1,2,3) ????
    public Flight flight;
    public InjuryIllness injuryIllness;
    public Location location;

    private static By PATIENT_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static By PATIENT_PRE_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/patientPreReg.html']"); // This used to work reliably, now something changed, and only if the menu disappears(?)
    private static By ssnFieldBy = By.id("ssn");
    private static By lastNameFieldBy = By.id("lastName");
    private static By firstNameFieldBy = By.id("firstName");
    private static By registerNumberFieldBy = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr[4]/td/input");
    private static By pageErrorsAreaBy = By.id("patientRegistrationSearchForm.errors");
    private static By someOtherPageErrorsAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
    private static By commitButtonBy = By.id("commit");

    // Not sure why these are here.  I think these sections always exist
    private static By flightSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[2]"); // not exactly same way did with new patient reg
    private static By injuryIllnessSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[3]"); // not exactly same way did with new patient reg
    private static By locationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[4]"); // not exactly same way did with new patient reg
    //private static By submitErrorsBy = By.id("patientRegistrationSearchForm.errors");


    public PreRegistration() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
        }
    }

    public boolean process(Patient patient) {
        boolean succeeded = false; // true?

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

        Utilities.sleep(1555); // was 555
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, PATIENT_PRE_REGISTRATION_MENU_LINK); // why does this second link fail?
        //logger.fine("Navigated?: " + navigated);
        if (!navigated) {
            logger.fine("PreRegistration.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2
        }


        // all this next stuff is to just see if we can do a Pre-Reg page with the patient
        // which we should know from what comes back from Search For Patient
        // What would the Search For Patient return at this point?  What are the options?
        // 1.  "XYZ already has an open Pre-Registration record.  Please update ...via Pre-registraion Arrivals page"
        // 2.  "There are no patients found."
        // 3.




        PatientState patientState = getPatientStateFromPreRegSearch(patient); // No longer: this sets skipRegistration true/false depending on if patient found
        switch (patientState) {
            case UPDATE: // we're in New Patient Reg, but TMDS said "xxx already has an open Registration record. Please update the patient via Patient Registration  Update Patient page."
                logger.fine("Should switch to Update Patient?  Not going to do that for now.");
                return false;
            case INVALID:
                return false;
            case PRE:
                succeeded = doPreRegistration(patient); // huh?  already here?
                break;
            default:
                logger.fine("What status? " + patientState);
                break;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return succeeded;
    }

    PatientState getPatientStateFromPreRegSearch(Patient patient) { // change name to getPatientStateFromPreRegSearch ????

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
        //PatientState patientStatus = null;

        // Not sure how worthwhile this is
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            return PatientState.PRE; // ???????????????
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
        // Getting here too soon?
        String searchResponseMessage = getPreRegSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);

        if (searchResponseMessage == null) { // does this happen for Pre-Reg?
            logger.fine("Probably okay to proceed with Pre-registration.");
            //return Pep.PatientStatus.NEW;
            return PatientState.PRE; // not .NEW
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                if (!Arguments.quiet) System.err.println("    Search For Patient: " + searchResponseMessage);
            }
        }
        // Prob most of the following doesn't apply to PreRegistration
        if (searchResponseMessage.contains("There are no patients found.")) {
            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.NEW; // not sure
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            logger.severe("***Patient already has an open registration record.  Use Update Patient instead.");
            //return PatientState.UPDATE;
            return PatientState.PRE_ARRIVAL; // new 10/30/18
        }
        if (searchResponseMessage.contains("already has an open Pre-Registration record.")) {
            logger.severe("***Patient already has an open pre-registration record.  Use Pre-registration Arrivals page.");
            //return PatientState.UPDATE;
            return PatientState.PRE_ARRIVAL; // new 10/30/18
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
        //return patientStatus;
        return PatientState.PRE_ARRIVAL;// new 10/30/18
    }

    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    // Not sure this stuff applies so much for PreRegistration.  Should review this method.
    String getPreRegSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        String message = null;
        (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(ssnFieldBy));
        Utilities.fillInTextField(ssnFieldBy, ssn);
        Utilities.fillInTextField(lastNameFieldBy, lastName);
        Utilities.fillInTextField(firstNameFieldBy, firstName);
        Utilities.fillInTextField(registerNumberFieldBy, traumaRegisterNumber);


        Utilities.clickButton(searchForPatientButtonBy); // Not ajax
        // Hey, compare with the other spnner check in this file.  Does a stalenessOf rather than an invisibilityOf
        try {
            (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.id("MB_window"))); // was 2s, was 10s
            logger.fine("PreReg.getPreRegSearchPatientResponse(), got a spinner window.  Now will try to wait until it goes away.");
            // Next line can throw a timeout exception if the patient has a duplicate.  That is, same name and ssn.  Maybe even same trauma number.  Because selection list comes up. Peter Pptest 666701231
            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window"))); // was after catch
            logger.fine("PreReg.getPreRegSearchPatientResponse(), spinner window went away.");
        }
        catch (Exception e) {
            logger.fine("Maybe too slow to get the spinner?  Continuing on is okay.");
        }

        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
                    .until(visibilityOfElementLocated(pageErrorsAreaBy));
                    //.until(visibilityOfElementLocated(someOtherPageErrorsAreaBy));
            String searchMessageText = searchMessage.getText();
            logger.fine("getPreRegSearchPatientResponse(), search message: " + searchMessageText);
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
            ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(ssnFieldBy));
            if (ssnTextBoxElement != null) {
                //logger.fine("I guess ssnbox is available now");
                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
                if (ssnTextBoxAttribute != null) {
                    logger.fine("ssnTextBoxAttribute: " + ssnTextBoxAttribute);
                }
                else {
                    logger.fine("I guess there was no ssntextbox attribute");
                }
            }
            else {
                logger.fine("didn't get a ssnTextBoxelement for some unknown reason.");
            }
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

    boolean doPreRegistration(Patient patient) {
        boolean succeeded;
        // I think that the returns of the following sections should not be counted as errors if the sections don't exist.
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
            return false; // never happens because always returns true
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Instant start = Instant.now();
        Utilities.clickButton(commitButtonBy); // Hey, the button click will come back before the patient is actually saved.

        // The above line may generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        // following is new:
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            // TMDS New Patient Reg page will allert that there's already a patient with that SSN.  If you click accept on the alert
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
            spinnerPopupWindow = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(spinnerPopupWindowBy)); // was 15
        }
        catch (Exception e) {
            logger.fine("Couldn't wait for visibility of spinner.  Will continue.  Exception: " + Utilities.getMessageFirstLine(e));
        }
        try {
            if (spinnerPopupWindow != null) { // new 11/4/18 because spinner window coming back null for some reason.  Check why
                (new WebDriverWait(Driver.driver, 180)).until(ExpectedConditions.stalenessOf(spinnerPopupWindow)); // do invisibilityOfElementLocated instead of staleness?
            }
        }
        catch (WebDriverException e) {
            logger.fine("Got a WebDriverException, whatever that was from, while trying to wait for spinnerWindow.  Bec running in grid?  Exception: " + Utilities.getMessageFirstLine(e));
        }
//        catch (TimeoutException e) {
//            logger.fine("Couldn't wait for staleness of spinner window.  Exception: " + Utilities.getMessageFirstLine(e));
//        }
        catch (Exception e) {
            logger.fine("Some other exception in PreReg.doPreReg(): " + Utilities.getMessageFirstLine(e));
        }


        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 4)) //  was 140.  Can take a long time on gold
                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(pageErrorsAreaBy))); // fails: 2
        }
        catch (Exception e) {
            logger.severe("preReg.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (someTextMaybe.contains("has been created.")) { // works for Pre-reg, and New Patient reg.  Update Patient too?
            }
            else if (someTextMaybe.contains("Patient's record has been updated.")) { // unlikely because we're in New Patient Reg., not Update Patient
            }
            else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) { // so for Role 4 "Pre-Registration" is all you can do here?
            }
            else {
                if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe + " fmp: " + patient.registration.preRegistration.demographics.fmp + " sometextmaybe: " + someTextMaybe);
                return false;
            }
        }
        catch (TimeoutException e) { // hey this should be impossible.
            logger.severe("preReg.process(), Failed to get message from message area.  TimeoutException: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("preReg.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("    Saved Pre-registration record for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("PreRegistration Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true; // success ??????????????????????????
    }

    boolean doDemographicsSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;

        Demographics demographics = preRegistration.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.random = (this.random == null) ? false : this.random; // new, and unnec bec just below
            demographics.shoot = (this.shoot == null) ? false : this.shoot; // new, and unnec bec just below
            preRegistration.demographics = demographics;
        }
        if (demographics.random == null) {
            demographics.random = (this.random == null) ? false : this.random;
        }
        if (demographics.shoot == null) {
            demographics.shoot = (this.shoot == null) ? false : this.shoot;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        //if (Utilities.elementExistsShorterWait(By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"), 1000) != null) {
        return processSucceeded;
    }

    boolean doFlightSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Flight (only available in Level 4)
        try {
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(flightSectionBy)); // not sure why this is required.  Section is required.
            Flight flight = preRegistration.flight;
            if (flight == null) {
                flight = new Flight();
                preRegistration.flight = flight;
            }
            if (flight.random == null) {
                flight.random = (this.random == null) ? false : this.random; // can't let this be null
            }
            if (flight.shoot == null) {
                flight.shoot = (this.shoot == null) ? false : this.shoot; // can't let this be null
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
            logger.severe("Some kind of error in flight section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    boolean doInjuryIllnessSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Injury/Illness must also contain information.  Can't skip it.
        //(new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(injuryIllnessSectionBy)); // now sure why can skip this, when others don't

        InjuryIllness injuryIllness = preRegistration.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            preRegistration.injuryIllness = injuryIllness;
        }
        if (injuryIllness.random == null) {
            injuryIllness.random = (this.random == null) ? false : this.random;
        }
        if (injuryIllness.shoot == null) {
            injuryIllness.shoot = (this.shoot == null) ? false : this.shoot;
        }
        boolean processSucceeded = injuryIllness.process(patient); // contains required fields, so must do this.
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Location (for level 4 only?)  The following takes a bit of time.  Change to have xpath with string "Location"?
        try {
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(locationSectionBy)); // not sure why need this.  The section is required.
            Location location = preRegistration.location;
            if (location == null) {
                location = new Location();
                //location.random = this.random; // new
                preRegistration.location = location;
            }
            if (location.random == null) {
                location.random = (this.random == null) ? false : this.random;
            }
            if (location.shoot == null) {
                location.shoot = (this.shoot == null) ? false : this.shoot;
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
            logger.severe("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

}
