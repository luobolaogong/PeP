package pep.patient.registration.updatepatient;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.patient.registration.*;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;
import pep.patient.registration.Demographics;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import static pep.Main.timerLogger;

import static pep.patient.PatientState.UPDATE;
import static pep.utilities.Driver.driver;

public class UpdatePatient {
    private static Logger logger = Logger.getLogger(UpdatePatient.class.getName());
    public Boolean random;
    public Demographics demographics;

    // It will be Flight (level 4) or ArrivalLocsationSection (levels 1,2,3)
    public Flight flight;
    public ArrivalLocation arrivalLocation;

    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    private static By PATIENT_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static By SUBMIT_BUTTON = By.xpath("//input[@id='commit']");
    //private static By UPDATE_PATIENT_PAGE_LINK = By.xpath("//span/b/a[@href='/tmds/patientUpdate.html']");
    //private static By UPDATE_PATIENT_PAGE_LINK = By.xpath("//*[@id=\"nav\"]/li[1]/ul/li[3]/a");
    private static By UPDATE_PATIENT_PAGE_LINK = By.xpath("//li/a[@href='/tmds/patientUpdate.html']");
    //                                                xpath("//*[@id=\"nav\"]/li[1]/ul/li[4]/a")
    //                                                xpath("//*[@id=\"a_2\"]")

    //private static By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/span/table/tbody/tr/td");
    //private static By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/descendant::td[contains(text(),'Departure')][1]"); // a td element with text "Departure"
    private static By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/descendant::td[text()='Departure']"); // a td element with text "Departure"


    private static By flightSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table[2]/tbody/tr/td");
    private static By locationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table[5]/tbody/tr/td");

    private static By searchForPatientButton = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]//input[@value='Search For Patient']");

    private static By someStupidContinueButtonOnSensitiveInfoPopupBy = By.xpath("/html/body/table[2]/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[2]/td[1]/input"); // verified for gold & demo

    private static By firstNameField = By.id("firstName");
    private static By lastNameField = By.id("lastName");
    private static By ssnField = By.id("ssn");
    private static By traumaRegisterNumberField = By.id("registerNumber");

    private static By newPatientRole3RegSearchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // NEW patient????
    private static By errorMessagesBy                       = By.id("patientRegistrationSearchForm.errors"); // correct
    private static By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors"); // huh?  //*[@id="errors"]/ul/li


    //boolean skipRegistration;

    public UpdatePatient() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.arrivalLocation = new ArrivalLocation();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
            this.departure = new Departure();
        }
//        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
//            departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[7]"); // on demo
//        }
    }

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
            //if (!Arguments.quiet) System.out.println("  Processing Registration ...");
            if (!Arguments.quiet) System.out.println("  Processing Update Patient ...");
        } else {
            if (!Arguments.quiet)
                //System.out.println("  Processing Registration for patient " + patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName + " ...");
                System.out.println("  Processing Update Patient for patient " +
                        (patient.registration.updatePatient.demographics.firstName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.firstName)) +
                        (patient.registration.updatePatient.demographics.lastName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.lastName)) +
                        (patient.registration.updatePatient.demographics.ssn.isEmpty() ? "" : (" ssn:" + patient.registration.updatePatient.demographics.ssn)) + " ..."
                );
//              patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName + " ...");
        }

        // check out this stuff from here down/in.  Search for Update Patient isn't working now (11/5/18)
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, UPDATE_PATIENT_PAGE_LINK); // this last link often fails
        //logger.fine("Navigated?: " + navigated);
        if (!navigated) {
            return false;
        }
        // Hey, is it possible that we get back Sensitive Information?  I think so!!!!!!!
        PatientState patientState = getPatientStateFromUpdatePatientSearch(patient); // what if this generates a "Sensitive Information" popup window?
        if (patientState == UPDATE) {
            succeeded = doUpdatePatient(patient);
        }
        return succeeded;
    }


    // Unfortunately it looks like this method needs to be slightly different from the one in New Patient Reg
    PatientState getPatientStateFromUpdatePatientSearch(Patient patient) {
        boolean skipSearch = false;
        String firstName = null;
        String lastName = null;
        String ssn = null;
        String traumaRegisterNumber = null;

        // Let's see what we've got, if anything from the PatientSearch stuff.
        if (patient.patientSearch != null) {
            firstName = patient.patientSearch.firstName;
            lastName = patient.patientSearch.lastName;
            ssn = patient.patientSearch.ssn;
            traumaRegisterNumber = patient.patientSearch.traumaRegisterNumber;
        }

        PatientState patientState = null;

        // Not sure how worthwhile this is.  Even possible?  You can skip a search with UpdatePatient?  I don't think so.
        // Remove this section, right?
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            //logger.fine("Skipped patient search because processing a random patient, probably, and assuming no duplicates.");
            //return Pep.PatientStatus.NEW; // ???????????????
            return PatientState.NEW; // ???????????????
        }




        // what if this generates a "Sensitive Information" popup window?
        String searchResponseMessage = getUpdatePatientSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);

        if (searchResponseMessage.equalsIgnoreCase("Registered")) {
            return PatientState.UPDATE; // ????
        }

        if (searchResponseMessage.equalsIgnoreCase("No record found to update.")) { // something's wrong
            System.out.println("We want to update a patient, but the specified patient isn't found.");
            return PatientState.INVALID; // ?
        }

        if (searchResponseMessage.contains("There are no patients found.")) {
            if (!Arguments.quiet) System.err.println("    ***Error encountered for Update Patient: " + searchResponseMessage);
            logger.fine("This message of 'There are no patients found.' doesn't make sense if we jumped to Update Patient.");
            logger.fine("This is due to a bug in TMDS Update Patient page for a role 4, it seems.  Also role 3, Gold");
            logger.fine("UpdatePatient.getPatientStateFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            return PatientState.INVALID; // wrong.  what's better?
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            if (!Arguments.quiet) System.err.println("    ***Error encountered for Update Patient: " + searchResponseMessage);
            // "AATEST, AARON - 666701215 already has an open Registration record. Please update the patient via Patient Registration > Update Patient page."
            logger.fine("Prob should switch to either Update Patient or go straight to Treatments.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            // I think this happens when we're level 3, not 4.
            logger.fine("I think this happens when we're level 3, not 4.  No, happens with 4.  Can update here?  Won't complain later?");
            logger.fine("But For now we'll assume this means we just want to do Treatments.  No changes to registration info.  Later fix this.");
            if (!Arguments.quiet) System.out.println("    Skipping remaining Registration Processing for " + patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName + " ...");
            return PatientState.UPDATE; // I think.  Not sure.
        }
//        if (searchResponseMessage.startsWith("There are no patients found.")) {
//            logger.fine("Patient wasn't found, which means go ahead with New Patient Reg.");
//            return PatientState.NEW;
//        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            if (!Arguments.quiet) System.err.println("    ***Failed to accept search field because not alphanumeric.");
            return PatientState.INVALID;
        }
        logger.fine("What kinda message?: " + searchResponseMessage);
        return PatientState.INVALID; // or null better
    }


    // Update Patient should only update fields that are specified in the encounter.json file,
    // and will overwrite values if specified.  Maybe change this if deemed necessary, maybe supporting
    // -nooverwrite or -noupdate.  If change default to not overwrite anything, then maybe support -update
    // or -overwrite.
    boolean doUpdatePatient(Patient patient) {
        boolean succeeded;

        succeeded = doDemographicsSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doArrivalLocationSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doFlightSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doInjuryIllnessSection(patient);
        if (!succeeded) {
            return false;
        }
        succeeded = doLocationSection(patient);
        if (!succeeded) {
            return false;
        }
        // no DepartureSection for Role 4 with Gold???  There is with TEST tier. and it this will return true
        succeeded = doDepartureSection(patient); // not avail for 4?
        if (!succeeded) {
            return false;
        }

        // I think this next line does not block.  It takes about 4 seconds before the spinner stops and next page shows up.   Are all submit buttons the same?
        Instant start = Instant.now();
        Utilities.clickButton(SUBMIT_BUTTON); // Not AJAX, but does call something at /tmds/registration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
//        timerLogger.info("Update Patient save took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        // The above line will generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
        }
        catch (Exception e) {
            //logger.fine("UpdatePatient.doUpdatePatient(), No alert about duplicate SSN's.  Continuing...");
        }


        WebElement webElement;
        try { // throws wild exception that isn't caught until later??????????????????  This is due to getting to this next line before the alert has gone away or something.
            webElement = (new WebDriverWait(Driver.driver, 90)) // was 60.
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy))); // fails: 2
        }
        catch (Exception e) {
            logger.severe("updatePatient.process(), Failed to find error message area.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (someTextMaybe.contains("Patient's record has been created.")) { // unlikely, because we're in Update Patient, not New Patient Reg.
                logger.finer("updatePatient.process(), Message indicates patient's record was created: " + someTextMaybe);
            }
            else if (someTextMaybe.contains("Patient's record has been updated.")) {
                logger.fine("updatePatient.process(), Message indicates patient's record was updated: " + someTextMaybe);
                if (!Arguments.quiet) {
                    System.out.println("    Saved Update Patient record for patient " +
                            (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                            (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                            (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                    );
                }
            }
            else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) { // so for Role 4 "Pre-Registration" is all you can do here?
                logger.fine("updatePatient.process(), I guess this is okay for Role 4: " + someTextMaybe);
            }
            else {
                if (!Arguments.quiet) System.err.println("    ***Failed trying to save patient " + patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName +  " : " + someTextMaybe);
                return false; // "already has an open Pre-Registration record"? "Patient's Pre-Registration has been created.",  "Initial Diagnosis is required", failed slow 3G
            }
        }
        catch (Exception e) {
            logger.severe("updatePatient.process(), Failed to get message from message area.  Exception:  " + Utilities.getMessageFirstLine(e));
            return false;
        }

        logger.finer("updatePatient.process() I guess we got some kind of message, and now returning true.");

        timerLogger.info("Update Patient for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true; // success ??????????????????????????
    }

    // Hey, this section has changed or something.  The search for patient isn't working the same, it seems.  So we spin forever?
    boolean doDemographicsSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;

        // Demographics section must contain values in most fields, but could have been populated by now if patient info found (and patient had departed previously)
        Demographics demographics = updatePatient.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.random = (this.random == null) ? false : this.random; // new, and unnec bec below
            updatePatient.demographics = demographics;
        }
        if (demographics.random == null) {
            demographics.random = (this.random == null) ? false : this.random;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        return processSucceeded;
    }

    boolean doArrivalLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Do ArrivalLocation section, if it exists for this level/role
        try {
            By arrivalLocationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td");
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(arrivalLocationSectionBy)); // what?  No ExpectedConditions?
            ArrivalLocation arrivalLocation = updatePatient.arrivalLocation;
            if (arrivalLocation == null) {
                arrivalLocation = new ArrivalLocation();
                updatePatient.arrivalLocation = arrivalLocation;
            }
            if (arrivalLocation.random == null) {
                arrivalLocation.random = (this.random == null) ? false : this.random;
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
            logger.severe("Some kind of error with arrivalLocation section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    boolean doFlightSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Flight (only available in Level 4)
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(flightSectionBy));
            Flight flight = updatePatient.flight;
            if (flight == null) {
                flight = new Flight();
                updatePatient.flight = flight;
            }
            if (flight.random == null) {
                flight.random = (this.random == null) ? false : this.random; // can't let this be null
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
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Injury/Illness must also contain information.  Can't skip it.
        InjuryIllness injuryIllness = updatePatient.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            updatePatient.injuryIllness = injuryIllness;
        }
        if (injuryIllness.random == null) {
            injuryIllness.random = (this.random == null) ? false : this.random;
        }
        boolean processSucceeded = injuryIllness.process(patient); // contains required fields, so must do this.
        if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Location (for level 4 only?)  The following takes a bit of time.  Change to have xpath with string "Location"?
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(locationSectionBy));
            Location location = updatePatient.location;
            if (location == null) {
                location = new Location();
                //location.random = this.random; // new
                updatePatient.location = location;
            }
            if (location.random == null) {
                location.random = (this.random == null) ? false : this.random;
            }
            boolean processSucceeded = location.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //logger.fine("There's no location section, which is the case for levels/roles 1,2,3");
            return true;
        }
        catch (Exception e) {
            logger.severe("Some kind of (unlikely) error in location section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    boolean doDepartureSection(Patient patient) {
        UpdatePatient updatePatient = patient.registration.updatePatient;
        // Departure
        // If you do a Departure, the "record is closed" and the patient is no longer a patient.  That means you can't update
        // the patient with the Update Patient page.  However, the system allows you to add notes, it appears.
        // So, even if there are treatments to add for this patient, you can do a Departure at this time.
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(departureSectionBy));
            Departure departure = updatePatient.departure;
            if (departure == null) {
                departure = new Departure();
                //departure.random = this.random; // new
                updatePatient.departure = departure;
            }
            if (departure.random == null) {
                departure.random = (this.random == null) ? false : this.random;
            }
            if (departure.departureDate == null) {
                departure.departureDate = Arguments.date;
            }
            boolean processSucceeded = departure.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("    ***Failed to process departure for patient " +
                    (patient.registration.updatePatient.demographics.firstName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.firstName)) +
                    (patient.registration.updatePatient.demographics.lastName.isEmpty() ? "" : (" " + patient.registration.updatePatient.demographics.lastName)) +
                    (patient.registration.updatePatient.demographics.ssn.isEmpty() ? "" : (" ssn:" + patient.registration.updatePatient.demographics.ssn)) + " ..."
            );
            //patient.registration.updatePatient.demographics.firstName + " " + patient.registration.updatePatient.demographics.lastName);
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


    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    String getUpdatePatientSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {

        // Examine current list of windows before we do a search because that may cause Sensitive Information
        // window to appear which screws up Selenium locators.
        String mainWindowHandleBeforeClick = Driver.driver.getWindowHandle();
        Set<String> windowHandlesSetBeforeClick = Driver.driver.getWindowHandles();
        int nWindowHandlesBeforeClick = windowHandlesSetBeforeClick.size();

        // Do the search for a patient, which should be found if we want to do an update.  The only time it wouldn't
        // work is if the input encounter file wrongly identified the patient.
        String message = null; // next line fails, times out
        (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.presenceOfElementLocated(ssnField)); // was 3
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);

        WebElement searchButton = null;
        try {
            searchButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(searchForPatientButton));


            searchButton.click(); // yields "There are no patients found" on demo role 3, but role 4 works and ALWAYS comes up with "Sensitive Information" window



        }
        catch (Exception e) {
            logger.fine("UpdatePatient.getUpdatePatientSearchPatientResponse(), Couldn't get the search button or click on it.");
            return null;
        }

        // not at all sure this will work.  Fails:2
        try {
            logger.fine("Here comes a wait for a stale search button");
            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(searchButton));
        }
        catch (Exception e) {
            logger.fine("Exception caught while waiting for staleness of search button.");
        }



        logger.fine("Done trying on the staleness thing.  Now gunna sleep.");
        Utilities.sleep(2555); // was 2555 , then was 555, now 1555, now back to 2555.  Hate to do this, but the Sensitive Information window isn't showing up fast enough.  Maybe can do a watch for stale window or something?
        logger.fine("Done sleeping.");


        // Handle the possibility of a Sensitive Information window.  Following does work if wait long enough to start, I think.

        String mainWindowHandleAfterClick = Driver.driver.getWindowHandle(); // this may be the original window, not the Sensitive one
        Set<String> windowHandlesSetAfterClick = Driver.driver.getWindowHandles();
        int nWindowHandlesAfterClick = windowHandlesSetAfterClick.size();
        //String someStrangeWindowHandle = null;
        if (nWindowHandlesAfterClick != nWindowHandlesBeforeClick) { // we picked up a window due to the search.
            Iterator<String> newWindowHandlesIterator = windowHandlesSetAfterClick.iterator();
            while (newWindowHandlesIterator.hasNext()) {
                String windowHandleFromSetAfterClick = newWindowHandlesIterator.next();
                if (!mainWindowHandleAfterClick.equals(windowHandleFromSetAfterClick)) {
                    // The window handle in the new list is probably the Sensitive Window
                    // So switch to it and click it's Continue button
                    try {
                        logger.fine("Switching to window handle in the set, with iterator.");
                        Driver.driver.switchTo().window(windowHandleFromSetAfterClick);

                        logger.fine("Waiting for continue button to be clickable.");
                        WebElement continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
                        //System.out.println("Gunna click continue button.");
                        continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.

                        logger.fine("Gunna switch to main window after click");
                        // Now go back to the original window
                        Driver.driver.switchTo().window(mainWindowHandleAfterClick);
                        // At this point if we found the "main" window from the list, or just did a getWindow would we have the one we want for later?

                        //Driver.driver.switchTo().defaultContent(); // doesn't seem to help
                        logger.fine("Going to find a frame.");
                        WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
                        //System.out.println("Gunna switch to that frame");
                        Driver.driver.switchTo().frame(someFrame); // doesn't throw
                    }
                    catch (Exception e) {
                        logger.fine("e: " + Utilities.getMessageFirstLine(e));
                    }
                    break;
                }
            }
        }

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // There's a bug on DEMO where the search comes back with "There are no patients found."
        // when the patient is found when doing search on the New Patient Reg. page.
        // I think this is only for Role3 on both Demo and Gold
        // I think for Role4 we timeout on Demo and Gold.
        // Therefore, we will ignore this message for now, but not ignore it when this is fixed.
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Oh wow, this just happened on Gold role3

        // This this stuff.  A very bad method.
        // This one should work for Update Patient search, but not for New Patient Reg. search
        try {
            logger.fine("UpdatePatient.getUpdatePatientSearchPatientResponse(), here comes a wait for visibility of some error text, which probably isn't there.");
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 1))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errors\"]/ul/li"))); // hey, put this where it belongs.  works for gold, fails demo
            logger.fine("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();

            if (searchMessageText != null) {
                if (searchMessageText.equalsIgnoreCase("There are no patients found.")) {
                    logger.fine("Got this message 'There are no patients found.' which can happen for Role 3 Update Patient search ");
                    logger.fine("perhaps because the patient was transferred out?  Is this expected/correct?");
                    logger.fine("If this happens for Role 4, then there's some other problem.");
                    //return "Registered"; // REMOVE THIS WHEN THE BUG IS FIXED IN DEMO.  Can't have this here because can't update a patient that isn't found "No record found to update."
                }
                else {
                    logger.fine("The search for a patient in Update Patient yielded this message: " + searchMessageText);
                    logger.fine("Should that prohibit Update Patient from working?");
                }
                return searchMessageText;
            }
        }
        catch (TimeoutException e) { // probably means patient was found.
            logger.fine("Timed out waiting for visibility of a message for Update Patient search.  Got exception: " + Utilities.getMessageFirstLine(e));
            logger.fine("No message when patient is found.  I think different for New Patient Reg, which displays message.  Really?  When found?  Or just when not found?");
            logger.fine("For Role 4 Update Patient it seems the patient was found, even when there was a transfer.");
            message = "Registered"; // On Gold Role 4 this happens when there is a transfer, but on role 3 it says "no patients found", I think.
        }
        catch (Exception e) {
            logger.fine("Some kind of exception thrown when waiting for error message.  Got exception: " + Utilities.getMessageFirstLine(e));
        }
        return message;
    }
}

