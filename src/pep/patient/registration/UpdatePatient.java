package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.Iterator;
import java.util.Set;

import static pep.Pep.isDemoTier;
import static pep.utilities.Driver.driver;

public class UpdatePatient {
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
    private static By UPDATE_PATIENT_PAGE_LINK = By.xpath("//span/b/a[@href='/tmds/patientUpdate.html']");

    private static By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/span/table/tbody/tr/td");
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
        if (isDemoTier) {
            departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[7]"); // on demo
        }
    }

    public boolean process(Patient patient) {
        boolean succeeded = false;
        if (patient.patientRegistration == null
                || patient.patientRegistration.updatePatient.demographics == null
                || patient.patientRegistration.updatePatient.demographics.firstName == null
                || patient.patientRegistration.updatePatient.demographics.firstName.isEmpty()
                || patient.patientRegistration.updatePatient.demographics.firstName.equalsIgnoreCase("random")
                || patient.patientRegistration.updatePatient.demographics.lastName == null
                || patient.patientRegistration.updatePatient.demographics.lastName.isEmpty()
                || patient.patientRegistration.updatePatient.demographics.lastName.equalsIgnoreCase("random")
                ) {
            //if (!Arguments.quiet) System.out.println("  Processing Registration ...");
            if (!Arguments.quiet) System.out.println("  Processing Update Patient ...");
        } else {
            if (!Arguments.quiet)
                //System.out.println("  Processing Registration for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
                System.out.println("  Processing Update Patient for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
        }

        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, UPDATE_PATIENT_PAGE_LINK);
        if (Arguments.debug) System.out.println("Navigated?: " + navigated);
        if (!navigated) {
            return false;
        }

        PatientState patientStatus = getPatientStatusFromUpdatePatientSearch(patient); // what if this generates a "Sensitive Information" popup window?
        switch (patientStatus) {
            case UPDATE:
                if (Arguments.debug) System.out.println("Patient previously registered and now we'll do an update, if that makes sense.");
                patient.patientState = PatientState.UPDATE; // right????????????????
                // Are we always sitting on the Update Patient page at this point?  If so do we have to go through another search?
                succeeded = doUpdatePatient(patient);
                break;
            case INVALID:
                patient.patientState = PatientState.NO_STATE; // wrong of course
                return false;
            case NEW:
                if (Arguments.debug) System.out.println("This better not happen in UpdatePatient.  can't find the patient.");
                patient.patientState = PatientState.NEW; // right????????????????
                //succeeded = doNewPatientReg(patient);
                break;
            default:
                if (Arguments.debug) System.out.println("What status? " + patientStatus);
                break;
        }
        return succeeded;
    }


    // Unfortunately it looks like this method needs to be slightly different from the one in New Patient Reg
    PatientState getPatientStatusFromUpdatePatientSearch(Patient patient) {
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

        PatientState patientStatus = null;

        // Not sure how worthwhile this is
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            if (Arguments.debug) System.out.println("Skipped patient search because processing a random patient, probably, and assuming no duplicates.");
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
            if (Arguments.debug) System.out.println("This message of 'There are no patients found.' doesn't make sense if we jumped to Update Patient.");
            if (!Arguments.quiet) System.out.println("This is due to a bug in TMDS Update Patient page for a role 4, it seems.  Also role 3, Gold");
            if (!Arguments.quiet) System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            return PatientState.INVALID; // wrong.  what's better?
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            // "AATEST, AARON - 666701215 already has an open Registration record. Please update the patient via Patient Registration > Update Patient page."
            if (Arguments.debug) System.out.println("Prob should switch to either Update Patient or go straight to Treatments.");
            if (!Arguments.quiet) System.out.println("  NOT! Skipping remaining Registration Processing for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            // I think this happens when we're level 3, not 4.
            if (Arguments.debug) System.out.println("I think this happens when we're level 3, not 4.  No, happens with 4.  Can update here?  Won't complain later?");
            if (Arguments.debug) System.out.println("But For now we'll assume this means we just want to do Treatments.  No changes to patientRegistration info.  Later fix this.");
            if (!Arguments.quiet) System.out.println("  Skipping remaining Registration Processing for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
            return PatientState.UPDATE; // I think.  Not sure.
        }
        if (searchResponseMessage.startsWith("There are no patients found.")) {
            if (Arguments.debug) System.out.println("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.NEW;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            if (!Arguments.quiet) System.err.println("***Failed to accept search field because not alphanumeric.");
            return PatientState.INVALID;
        }
        if (Arguments.debug) System.out.println("What kinda message?: " + searchResponseMessage);
        return patientStatus;
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
        // there is no DepartureSection for Role 4, and it this will return true
        succeeded = doDepartureSection(patient);
        if (!succeeded) {
            return false;
        }

        // I think this next line does not block.  It takes about 4 seconds before the spinner stops and next page shows up.   Are all submit buttons the same?
        Utilities.clickButton(SUBMIT_BUTTON); // Not AJAX, but does call something at /tmds/patientRegistration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
        if (Arguments.debug) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Hey the submit in the update patient search thing could cause two unexpected things to happen: Sensitive Info popup window, and message of patient not found.");
        // The above line will generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        // This happens even with Role 4 and doing Update Patient rather than New Patient Reg.  Therefore, what's the freaking difference between the two?
        // There is some diff, I think, but not sure what.
        // But if you get rid of the alert then the submit fails because it says "No record found to update".  What the heck?  Did I forget to do a search at the
        // start of Update Patient?????
        // check for alert
//        try {
////            Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
//            Utilities.sleep(1555); // Added this because the wait down below causes a bad exception to be thrown and burps on the screen
//            Alert duplicateSsnAlert = Driver.driver.switchTo().alert();
//            duplicateSsnAlert.accept();
//        }
//        catch (TimeoutException e) { // huh?
//            if (Arguments.debug) System.out.println("Update Patient page, after click Submit, Timed out, Didn't find an alert, which is probably okay... Continuing.");
//        }
//        catch (NoAlertPresentException e) { // wrong?  It was there?
//            if (Arguments.debug) System.out.println("Update Patient page, after click Submit, No alert present exception... Continuing.");
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Update Patient page, after click Submit, Didn't find an alert, which is probably okay.  " + e.getMessage() + " ... Continuing.");
//        }
        // following is new 9/25/18 as replacement for above
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("UpdatePatient.doUpdatePatient(), No alert about duplicate SSN's.  Continuing...");
        }


        WebElement webElement;
        try { // throws wild exception that isn't caught until later??????????????????  This is due to getting to this next line before the alert has gone away or something.
            webElement = (new WebDriverWait(Driver.driver, 60)) // does this actually work, or does it just fly through?  I think it works.  Can take a long time on gold?
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy))); // fails: 2
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("updatePatient.process(), Failed to find error message area.  Exception: " + e.getMessage());
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (someTextMaybe.contains("Patient's record has been created.")) { // unlikely, because we're in Update Patient, not New Patient Reg.
                if (Arguments.debug) System.out.println("updatePatient.process(), Message indicates patient's record was created: " + someTextMaybe);
            }
            else if (someTextMaybe.contains("Patient's record has been updated.")) {
                if (Arguments.debug) System.out.println("updatePatient.process(), Message indicates patient's record was updated: " + someTextMaybe);
            }
            else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) { // so for Role 4 "Pre-Registration" is all you can do here?
                if (Arguments.debug) System.out.println("updatePatient.process(), I guess this is okay for Role 4: " + someTextMaybe);
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed trying to save patient " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName +  ": " + someTextMaybe);
                return false; // Fails 6, "Patient's Pre-Registration has been created.",  "Initial Diagnosis is required", failed slow 3G
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("updatePatient.process(), Failed to get message from message area.  Exception:  " + e.getMessage());
            return false;
        }

        if (Arguments.debug) System.out.println("updatePatient.process() I guess we got some kind of message, and now returning true.");

        return true; // success ??????????????????????????
    }

    boolean doDemographicsSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;

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
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process demographics for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        return processSucceeded;
    }

    boolean doArrivalLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;
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
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process arrival/Location for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("No arrivalLocation section.  Okay.");
            return true;
        }
        catch (Exception e) {
            System.out.println("Some kind of error with arrivalLocation section: " + e.getMessage());
            return false;
        }
    }

    boolean doFlightSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;
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
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process flight for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("There's no flight section, which is the case for levels/roles 1,2,3");
            return true; // a little hack here
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of error in flight section: " + e.getMessage());
            return false;
        }
    }

    boolean doInjuryIllnessSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;
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
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process injury/illness for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;
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
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Location for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("There's no location section, which is the case for levels/roles 1,2,3");
            return true;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of (unlikely) error in location section: " + e.getMessage());
            return false;
        }
    }

    boolean doDepartureSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;
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
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process departure for patient " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("There's no departure section????  That seems wrong.  Prob shouldn't get here.  returning true");  // it does get here level 3
            return true;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of error in departure section?: " + e.getMessage());
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
        String message = null;
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
            if (Arguments.debug) System.out.println("UpdatePatient.getUpdatePatientSearchPatientResponse(), Couldn't get the search button or click on it.");
            return null;
        }

        // not at all sure this will work.  Fails:2
        try {
            if (Arguments.debug) System.out.println("Here comes a wait for a stale search button");
            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(searchButton));
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Exception caught while waiting for staleness of search button.");
        }



        if (Arguments.debug) System.out.println("Done trying on the staleness thing.  Now gunna sleep.");
        Utilities.sleep(555); // was 2555hate to do this, but the Sensitive Information window isn't showing up fast enough.  Maybe can do a watch for stale window or something?
        if (Arguments.debug) System.out.println("Done sleeping.");
        // Handle the possibility of a Sensitive Information window

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
                        if (Arguments.debug) System.out.println("Switching to window handle in the set, with iterator.");
                        Driver.driver.switchTo().window(windowHandleFromSetAfterClick);

                        if (Arguments.debug) System.out.println("Waiting for continue button to be clickable.");
                        WebElement continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
                        //System.out.println("Gunna click continue button.");
                        continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.

                        if (Arguments.debug) System.out.println("Gunna switch to main window after click");
                        // Now go back to the original window
                        Driver.driver.switchTo().window(mainWindowHandleAfterClick);
                        // At this point if we found the "main" window from the list, or just did a getWindow would we have the one we want for later?

                        //Driver.driver.switchTo().defaultContent(); // doesn't seem to help
                        if (Arguments.debug) System.out.println("Going to find a frame.");
                        WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
                        //System.out.println("Gunna switch to that frame");
                        Driver.driver.switchTo().frame(someFrame); // doesn't throw

//                        // test code.  I think it shows there's a problem.  It doesn't get the text
//                        By lastNameFieldBy = By.id("patientRegistration.lastName"); // verified on gold level 4
//                        try {
//                            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy)); // fails
//                            String someText = lastNameField.getAttribute("value");// why always blank?
//                        }
//                        catch (TimeoutException e) { // TimeoutException
//                            System.out.println("What the crud, timed out tried to get last name"); // "TMDS | Sensitive Information"  What?????????
//                        }
//                        catch (Exception e) { // TimeoutException
//                            System.out.println("What the crud, tried to get last name: " + e.getMessage()); // "TMDS | Sensitive Information"  What?????????
//                        }


//                        System.out.println("Gunna switch back to original window before click");
//                        // Now go back to the original window????
//                        Driver.driver.switchTo().window(mainWindowHandleBeforeClick); // causes probs
//                        //Driver.driver.switchTo().window(someStrangeWindowHandle);
                    }
                    catch (Exception e) {
                        if (Arguments.debug) System.out.println("e: " + e.getMessage());
                    }
                    break;
                }
//                else {
//                    someStrangeWindowHandle = windowHandleFromSetAfterClick;
//                }
            }
        }
//        // test code.  I think it shows there's a problem.  It doesn't get the text
//        By lastNameFieldBy = By.id("patientRegistration.lastName"); // verified on gold level 4
//        try {
//            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy)); // fails
//            //String someText = lastNameField.getText();// why always blank?
//            String someText = lastNameField.getAttribute("value");// why always blank?
//            System.out.println("someText: " + someText); // Why is this always blank?
//        }
//        catch (TimeoutException e) { // TimeoutException
//            System.out.println("Timeout 4"); // "TMDS | Sensitive Information"  What?????????
//        }
//        catch (Exception e) { // TimeoutException
//            System.out.println("What the crud, tried to get last name: " + e.getMessage()); // "TMDS | Sensitive Information"  What?????????
//        }


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
            if (Arguments.debug) System.out.println("UpdatePatient.getUpdatePatientSearchPatientResponse(), here comes a wait for visibility of some error text, which probably isn't there.");
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 1))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errors\"]/ul/li"))); // hey, put this where it belongs.  works for gold, fails demo
            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();

            if (searchMessageText != null) {
                if (searchMessageText.equalsIgnoreCase("There are no patients found.")) {
                    if (Arguments.debug) System.out.println("Got this message 'There are no patients found.' which can happen for Role 3 Update Patient search ");
                    if (Arguments.debug) System.out.println("perhaps because the patient was transferred out?  Is this expected/correct?");
                    if (Arguments.debug) System.out.println("If this happens for Role 4, then there's some other problem.");
                    //return "Registered"; // REMOVE THIS WHEN THE BUG IS FIXED IN DEMO.  Can't have this here because can't update a patient that isn't found "No record found to update."
                }
                else {
                    if (Arguments.debug) System.out.println("The search for a patient in Update Patient yielded this message: " + searchMessageText);
                    if (Arguments.debug) System.out.println("Should that prohibit Update Patient from working?");
                }
                return searchMessageText;
            }
        }
        catch (TimeoutException e) { // probably means patient was found.
            if (Arguments.debug) System.out.println("Timed out waiting for visibility of a message for Update Patient search.  Got exception: " + e.getMessage());
            if (Arguments.debug) System.out.println("No message when patient is found.  I think different for New Patient Reg, which displays message.  Really?  When found?  Or just when not found?");
            if (Arguments.debug) System.out.println("For Role 4 Update Patient it seems the patient was found, even when there was a transfer.");
            message = "Registered"; // On Gold Role 4 this happens when there is a transfer, but on role 3 it says "no patients found", I think.
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
        }
        return message;
    }
}

