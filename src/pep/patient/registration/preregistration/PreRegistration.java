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
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Driver.driver;

// This only applies to Role 4, it seems.

public class PreRegistration {
    private static Logger logger = Logger.getLogger(PreRegistration.class.getName());
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public Demographics demographics;
    // It will be Flight (level 4) or ArrivalLocationSection (levels 1,2,3) ????
    public Flight flight;
    public InjuryIllness injuryIllness;
    public Location location;

    private static By PATIENT_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static By PATIENT_PRE_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientPreReg.html']");
    private static By ssnFieldBy = By.id("ssn");
    private static By lastNameFieldBy = By.id("lastName");
    private static By firstNameFieldBy = By.id("firstName");
    private static By registerNumberFieldBy = By.id("registerNumber");
    private static By searchForPatientButtonBy = By.xpath("//button[text()='Search For Patient']"); // test 1/25/19
    private static By messageArea1By = By.xpath("//*[@id='errors']/ul/li"); // "no patients found"?
    private static By messageArea2By = By.id("patientRegistrationSearchForm.errors"); // "... already has an open Registration record.  Please update ...Update Patient page."
    private static By messageArea3By = By.id("patientRegistrationSearchForm.errors"); // experiment
    private static By commitButtonBy = By.id("commit");

    // Not sure why these are here.  I think these sections always exist
    private static By flightSectionBy = By.id("formatArrivalDate"); // This is the first ID'd element in the section
//    private static By locationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[4]"); // not exactly same way did with new patient reg
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

    // If the user is not a level 4 then I think you can't do a Preregistration, and shouldn't be here.
    // So, how do you detect and skip?  We don't really know anything about the user, unless
    // it becomes a verifiable part of the input json file.
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

        Utilities.sleep(1555, "PreRegistration"); // was 555
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, PATIENT_PRE_REGISTRATION_MENU_LINK); // why does this second link fail?
        if (!navigated) {
            logger.fine("PreRegistration.process(), Failed to navigate!!!");
            return false;
        }

        // all this next stuff is to just see if we can do a Pre-Reg page with the patient
        // which we should know from what comes back from Search For Patient
        // What would the Search For Patient return at this point?  What are the options?
        // 1.  "XYZ already has an open Pre-Registration record.  Please update ...via Pre-registraion Arrivals page"
        // 2.  "There are no patients found."
        // 3.


        // Interesting that if you do a search for a patient that is in the system somewhere, it will bring up the patient in Pre-reg, fill in form.

        PatientState patientState = getPatientStateFromPreRegSearch(patient); // No longer: this sets skipRegistration true/false depending on if patient found
        // I think basically now any patientState other than PRE should cause a return of false
            switch (patientState) {
            case UPDATE: // we're in New Patient Reg, but TMDS said "xxx already has an open Registration record. Please update the patient via Patient Registration  Update Patient page."
                logger.fine("Should switch to Update Patient?  Not going to do that for now.");
                return false;
            case INVALID:
                return false;
            case PRE:
                succeeded = doPreRegistration(patient); // huh?  already here?
                break;
            case PRE_ARRIVAL: // new 12/30/18
                //succeeded = doPreRegistration(patient); // huh?  already here?
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
            return PatientState.PRE; // not .NEW  Not sure about this.  If "already has an open Reg..." then shouldn't do PRE.
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                if (!Arguments.quiet) System.err.println("    ***Search For Patient: " + searchResponseMessage);
            }
        }
        // Prob most of the following doesn't apply to PreRegistration
        if (searchResponseMessage.contains("There are no patients found.")) {
            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
            //return PatientState.NEW; // not sure
            return PatientState.PRE; // new
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            logger.severe("Patient already has an open registration record.  Use Update Patient instead.");
            return PatientState.UPDATE; // should we jump to Update?  What if there's something to do before Update?
            //return PatientState.PRE_ARRIVAL; // new 10/30/18  What???  Should be UPDATE, right?
        }
        if (searchResponseMessage.contains("already has an open Pre-Registration record.")) {
            logger.severe("Patient already has an open pre-registration record.  Use Pre-registration Arrivals page.");
            //return PatientState.UPDATE;
            return PatientState.PRE_ARRIVAL; // new 10/30/18
        }
        if (searchResponseMessage.contains("An error occurred while processing")) {
            logger.severe("Error with TMDS, but we will continue assuming new patient.  Message: " + searchResponseMessage);
            return PatientState.NEW; // Not invalid.  TMDS has a bug.
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            logger.fine("I think this happens when we're level 3, not 4.  Can update here?  Won't complain later?  Will not complain later");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            //return PatientState.NEW;
            return PatientState.PRE; // Does this mean the patient's record was previously closed?  If so, shouldn't we continue on?  Yes, I think so
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


        Utilities.clickButton(searchForPatientButtonBy); // Not ajax
// Removed 12/28/18 because I don't see a spinner at all.  Not sure why.  Finds the patient too fast?
//        // Hey, compare with the other spnner check in this file.  Does a stalenessOf rather than an invisibilityOf
//        try {
//            (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.id("MB_window"))); // was 2s, was 10s
//            logger.fine("PreReg.getPreRegSearchPatientResponse(), got a spinner window.  Now will try to wait until it goes away.");
//            // Next line can throw a timeout exception if the patient has a duplicate.  That is, same name and ssn.  Maybe even same trauma number.  Because selection list comes up. Peter Pptest 666701231
//            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window"))); // was after catch
//            logger.fine("PreReg.getPreRegSearchPatientResponse(), spinner window went away.");
//        }
//        catch (Exception e) {
//            logger.fine("Maybe too slow to get the spinner?  Continuing on is okay.");
//        }
// Also removed this next section on 12/28/18, prob shouldn't, but not seeing any messages when the patient is found.  What about when not?

        // Here's the problem I've been facing a long time now.  There is a general "message area" above the "Search For Patient" tab on the page
        // and it is where various messages are displayed.  One locator is used for the message "There are no patients found."  A different
        // locator is used for another message like "There's already a patient whatever..."  So I have to do a double locator and whatever one
        // shows up we wait for it.  I mean, wait just long enough for whichever one shows up.
        // But to complicate matters it seems that TEST and GOLD tiers have these reversed, possibly, so you can't name the variables descriptively.
        // Another problem is that it seems there are three possible results: not found, already open, or found but not yet arrived.
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
                logger.fine("message: " + message); // TEST: "there are no patients found"  GOLD: ?"... already has an open Registration record. Please update ... Update Patient page." What???? diff TEST and GOLD????
                return message; // GOLD: "already has an open Pre-Reg...Pre-registration Arrivals page."
            } catch (Exception e1) {
                logger.warning("Didn't get a message using locator " + messageArea2ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e1));
            }

            // check on the other condition.
            try {
                WebElement element = (new WebDriverWait(Driver.driver, 1)).until(messageArea1ExpectedCondition); // was 1
                message = element.getText();
                logger.fine("PreRegistration.getPreRegSearchPatientResponse(), Prob okay to procede with PreReg.  message: " + message); // TEST: "...already has an open Reg...Update Patient page.", GOLD:? "There are no patients found"
                return message; // TEST: "...already has an open Pre-Reg rec...Pre-reg Arrivals page.", or "...already has an open Reg rec.  Update Patient page.", GOLD: "There are no patients found."
            } catch (Exception e2) {
                logger.fine("Didn't get a message using locator " + messageArea1ExpectedCondition + " e: " + Utilities.getMessageFirstLine(e2));
            }
        }
        else {
                logger.fine("No exception but didn't get either condition met, which is unlikely.");
                // continue on
        }
        // I don't think we should get here.

//        try {
//            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
//                    .until(visibilityOfElementLocated(messageArea1By));
//                    //.until(visibilityOfElementLocated(someOtherPageErrorsAreaBy));
//            //String searchMessageText = searchMessage.getText();
//            message = searchMessage.getText();
//            //logger.fine("getPreRegSearchPatientResponse(), search message: " + searchMessageText);
//            logger.fine("getPreRegSearchPatientResponse(), search message: " + message);
//            if (message != null) {
//                return message;
//            }
//        }
//        catch (TimeoutException e) {
//            logger.fine("Timeout out waiting for visibility of a message when a patient is actually found.  This is okay for Role3 New Patient Reg.  Got exception: " + Utilities.getMessageFirstLine(e));
//        }
//        catch (Exception e) {
//            logger.fine("Some kind of exception thrown when waiting for error message.  Got exception: " + Utilities.getMessageFirstLine(e));
//        }

        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
        // I wonder why I couldn't do the same thing elsewhere, perhaps in UpdatePatient, or other places.  Just wouldn't work.  Programming mistake?
        // Rethink the following.  It isn't currently getting executed with some of my tests.  But still it's probably possible.
        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = Utilities.waitForPresence(ssnFieldBy, 10, "PreRegistration.(), ssn");
//            if (ssnTextBoxElement != null) {
//                //logger.fine("I guess ssnbox is available now");
//                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
//                if (ssnTextBoxAttribute != null) {
//                    logger.fine("ssnTextBoxAttribute disabled: " + ssnTextBoxAttribute);
//                }
//                else {
//                    logger.finer("No ssnTextBox attribute disabled");
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
            spinnerPopupWindow = Utilities.waitForVisibility(spinnerPopupWindowBy, 30, "PreRegistration.(), spinner popup window"); // was 15
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
        try { // This next line does work if you don't pause before hitting it.  But it has also failed.
            webElement = (new WebDriverWait(Driver.driver, 15)) //  was 140, was 10.  Can take a long time on gold
                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
                   // .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageArea1By))); // fails: 2
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageArea3By))); // fails: 3, but verifies
        }
        catch (Exception e) {
            logger.severe("preReg.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false; // why does this fail?  Seems gets to the above line too soon?  Stop at above line to see if works next time
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
        timerLogger.fine("PreRegistration Patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        return true; // success ??????????????????????????
    }

    boolean doDemographicsSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;

        Demographics demographics = preRegistration.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null // new, and unnec bec just below
            demographics.shoot = this.shoot; // new, and unnec bec just below
            preRegistration.demographics = demographics;
        }
        if (demographics.sectionToBeRandomized == null) {
            demographics.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
        }
        if (demographics.shoot == null) {
            demographics.shoot = this.shoot;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && Arguments.verbose) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        //if (Utilities.elementExistsShorterWait(By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"), 1000) != null) {
        return processSucceeded;
    }

    boolean doFlightSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Flight (only available in Level 4)
        try {
            Utilities.waitForPresence(flightSectionBy, 1, "PreRegistration.doFlightSection()"); // not sure why this is required.  Section is required.
            Flight flight = preRegistration.flight;
            if (flight == null) {
                flight = new Flight();
                preRegistration.flight = flight;
            }
            if (flight.sectionToBeRandomized == null) {
                flight.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null // can't let this be null
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
        if (injuryIllness.sectionToBeRandomized == null) {
            injuryIllness.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
        }
        if (injuryIllness.shoot == null) {
            injuryIllness.shoot = this.shoot;
        }
        boolean processSucceeded = injuryIllness.process(patient); // contains required fields, so must do this.
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        PreRegistration preRegistration = patient.registration.preRegistration;
        // Location (for level 4 only?)  The following takes a bit of time.  Change to have xpath with string "Location"?
        try {
            Utilities.waitForPresence(locationSectionBy, 1, "PreRegistration.doLocationSection()"); // not sure why need this.  The section is required.
            Location location = preRegistration.location;
            if (location == null) {
                location = new Location();
                //location.sectionToBeRandomized = this.sectionToBeRandomized; // new
                preRegistration.location = location;
            }
            if (location.sectionToBeRandomized == null) {
                location.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
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
            logger.severe("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

}
