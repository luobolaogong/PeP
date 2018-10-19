package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Pep.isDemoTier;
import static pep.utilities.Driver.driver;

public class NewPatientReg {
    public Boolean random;
    public Demographics demographics;

    // It will be Flight (level 4) or ArrivalLocsationSection (levels 1,2,3)
    public Flight flight;
    public ArrivalLocation arrivalLocation;

    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;


    private static By  PATIENT_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static By  NEW_PATIENT_REG_PAGE_LINK = By.xpath("//span/b/a[@href='/tmds/patientReg.html']"); // this can fail

    private static By  arrivalLocationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td");
    private static By  departureSectionBy       = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/span/table/tbody/tr/td");
    private static By  flightSectionBy          = By.xpath("//*[@id=\"patientRegForm\"]/table[2]/tbody/tr/td");
    private static By  locationSectionBy        = By.xpath("//*[@id=\"patientRegForm\"]/table[5]/tbody/tr/td");


    private static By  firstNameField = By.id("firstName");
    private static By  lastNameField = By.id("lastName");
    private static By  ssnField = By.id("ssn");
    private static By  traumaRegisterNumberField = By.id("registerNumber");

    private static By  newPatientRole3RegSearchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
    private static By  errorMessagesBy                       = By.id("patientRegistrationSearchForm.errors"); // correct for demo tier
    private static By  patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors"); // huh?  //*[@id="errors"]/ul/li

    private static By  searchForPatientButton = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]//input[@value='Search For Patient']");

    private static By  SUBMIT_BUTTON = By.xpath("//input[@id='commit']");

    //boolean skipRegistration;

    public NewPatientReg() {
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
            departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/div[7]"); // right?
        }

    }

    // Process the big thing called Registration.  Registration encompasses Pre-Registration, New Patient Registration,
    // and Update Patient.  And each of these includes several sections, most of which are shared between these registrations.
    // This method initially assumed there was only one: New Patient Registration.  It is now being changed to do more.
    //
    // Where and how should we decide between New Patient Reg, and Update Patient?
    // We are given an Encounters.json file, but there's nothing in it that says "Do New Patient",
    // or "Do Update Patient".  We could add a field but the JSON file should represent what's actually in
    // the pages.  Perhaps we could have an added layer, as in PatientRegistration {PreRegistration{}, NewPatientReg{}, UpdatePatient{}}
    // Probably do that.
    // Or perhaps we could use a command line argument: -new, -update, -pre
    // Or we could infer from the data provided.  That is, if we search for the patient is he/she found?
    // And if found is the record "open", or whatever?
    //
    // As to navigation, we want to mimic what a user would do -- clicking on elements, rather than
    // typing in a URL to get to a particular page.  One reason why is that our pages are pages within
    // pages, and we need to keep the outer pages, because the outer page has a logout link.  So,
    // we have to simulate clicking.
    //
    // And if we simulate clicking, what do we click on?  tab/menu, and their submenu elements?  Or do we
    // click on links?  I don't think it matters.  Whatever works.
    //
    // But the problem is that these menus and links have to be located by a mechanism that works for different
    // levels/roles.  A level 3 "New Patient Reg" element has a different "id" than a level 4!!!!!  So we cannot
    // use By.id("") for these!!!  Looks like instead I have to use silly complex xpaths that contain text
    // attributes saying "New Patient Reg."  Or I could keep a state variable that indicates level/role.
    //
    public boolean process(Patient patient) {
        boolean succeeded = false; // Why not start this out as true?  Innocent until proven otherwise
        // We either got here because the default after logging in is this page, or perhaps we deliberately clicked on "Patient Registration" tab.
        if (patient.patientRegistration == null
                || patient.patientRegistration.newPatientReg.demographics == null
                || patient.patientRegistration.newPatientReg.demographics.firstName == null
                || patient.patientRegistration.newPatientReg.demographics.firstName.isEmpty()
                || patient.patientRegistration.newPatientReg.demographics.firstName.equalsIgnoreCase("random")
                || patient.patientRegistration.newPatientReg.demographics.lastName == null
                || patient.patientRegistration.newPatientReg.demographics.lastName.isEmpty()
                || patient.patientRegistration.newPatientReg.demographics.lastName.equalsIgnoreCase("random")
                ) {
            if (!Arguments.quiet) System.out.println("  Processing New Patient Registration ...");
        } else {
            if (!Arguments.quiet)
                System.out.println("  Processing New Patient Registration for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }

        // Wow, at this point we're already sitting at New Patient Reg. page, if we came from Login.
        // If we didn't come here from Login, then we probably looped back here when we started a 2nd patient.
        // How do we know from looking at the JSON file whether we should be doing New or Update?  We can't,
        // unless we change the JSON structure.

        // In order to know whether to do a New Patient Reg., or an Update Patient, we need to do a search, and
        // to do a search we have to be on the New Patient Reg. page, or the Update Patient page.
        // (unless we want to user to specify -update or something like that, which would still require a search.
        // So might as well do the search on the New Patient Reg. page.

        // are we getting here too soon????????????
        Utilities.sleep(1555); // was 555, but first time invoked in morning, it fails.  It's the 2nd link, not the first one that fails.  Not enough time between links?
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, NEW_PATIENT_REG_PAGE_LINK);
        //if (Arguments.debug) System.out.println("Navigated?: " + navigated);
        if (!navigated) {
            return false; // fails: level 4 demo: 1, gold 2
        }
        //
        // We should probably use PatientSearch information from the JSON file rather than dip into patient demographics?
        //
        //Pep.PatientStatus patientStatus = getPatientStatusFromNewPatientRegSearch(patient); // No longer: this sets skipRegistration true/false depending on if patient found
        PatientState patientStatus = getPatientStatusFromNewPatientRegSearch(patient); // No longer: this sets skipRegistration true/false depending on if patient found
        switch (patientStatus) {
            //case REGISTERED:
            case UPDATE: // we're in New Patient Reg, but TMDS said "xxx already has an open Registration record. Please update the patient via Patient Registration  Update Patient page."
                if (Arguments.debug) System.out.println("Should switch to Update Patient?  Not going to do that for now.");
                // WOW WE'RE GOING TO FLIP NOW OVER TO UPDATE PATIENT!!!!!! rather than exit out
                //succeeded = patient.processUpdatePatient(); // should we try this?  I don't think so.
                return false;
                //break;
            case INVALID:
                return false;
            case NEW:
                //if (Arguments.debug) System.out.println("Continue on, I guess.  Do New Patient Reg.");
                succeeded = doNewPatientReg(patient);
                break;
            default:
                if (Arguments.debug) System.out.println("What status? " + patientStatus);
                break;
        }
        return succeeded;
    }




    boolean doNewPatientReg(Patient patient) {
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
            return false; // never happens because always returns true
        }
        // there is no DepartureSection for Role 4, and it this will return true
        succeeded = doDepartureSection(patient);
        if (!succeeded) {
            return false;
        }
        //if (Arguments.debug) System.out.println("newPatientReg.process() will now click submit button to register patient.");

        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Utilities.clickButton(SUBMIT_BUTTON); // Not AJAX, but does call something at /tmds/patientRegistration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
        // The above line may generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        // following is new:
        try {
            (new WebDriverWait(driver, 2)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
        }
        catch (Exception e) {
            //if (Arguments.debug) System.out.println("No alert about duplicate SSN's.  Continuing...");
        }


        //if (Arguments.debug) System.out.println("newPatientReg.process() will now check for successful patient record creation, or other messages.  This seems to block okay.");
        try {
            By spinnerPopupWindowBy = By.id("MB_window");
            // This next line assumes execution gets to it before the spinner goes away.
            // Also the next line can throw a WebDriverException due to an "unexpected alert open: (Alert text : The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
            if (Arguments.debug) System.out.println("Waiting for visibility of spinner");
            WebElement spinnerPopupWindow = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.visibilityOfElementLocated(spinnerPopupWindowBy)); // was 15
            if (Arguments.debug) System.out.println("Waiting for staleness of spinner");
            (new WebDriverWait(Driver.driver, 180)).until(ExpectedConditions.stalenessOf(spinnerPopupWindow)); // do invisibilityOfElementLocated instead of staleness?
            //if (Arguments.debug) System.out.println("We're good.");
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!Couldn't wait long enough, probably, for new patient to be saved.: " + e.getMessage());
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some other exception in NewPatientReg.doNewPatientReg(): " + e.getMessage());
        }
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 140)) //  Can take a long time on gold
                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy))); // fails: 2
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("newPatientReg.process(), Failed to find error message area.  Exception: " + e.getMessage());
            return false;
        }
        try {
            String someTextMaybe = webElement.getText();
            if (someTextMaybe.contains("Patient's record has been created.")) {
                //if (Arguments.debug) System.out.println("newPatientReg.process(), Message indicates patient's record was created: " + someTextMaybe);
            }
            else if (someTextMaybe.contains("Patient's record has been updated.")) { // unlikely because we're in New Patient Reg., not Update Patient
                //if (Arguments.debug) System.out.println("newPatientReg.process(), Message indicates patient's record was updated: " + someTextMaybe);
            }
            else if (someTextMaybe.contains("Patient's Pre-Registration has been created.")) { // so for Role 4 "Pre-Registration" is all you can do here?
                //if (Arguments.debug) System.out.println("newPatientReg.process(), I guess this is okay for Role 4: " + someTextMaybe);
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed trying to save patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe);
                return false; // Fails 7, "Patient's Pre-Registration has been created.",  "Initial Diagnosis is required", failed slow 3G
            }
        }
        catch (TimeoutException e) { // hey this should be impossible.
            if (Arguments.debug) System.out.println("newPatientReg.process(), Failed to get message from message area.  TimeoutException: " + e.getMessage());
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("newPatientReg.process(), Failed to get message from message area.  Exception:  " + e.getMessage());
            return false;
        }

        //if (Arguments.debug) System.out.println("newPatientReg.process() I guess we got some kind of message, and now returning true.");
        //if (Arguments.debug) System.out.println("Do we need to update PatientSearch now?");
        return true; // success ??????????????????????????
    }


    // This was meant to show the various states a patient or person could be in, but it's not clear what is needed yet.
// A person becomes a patient.  They could be preregistered, they could be admitted, they could be inpatient or outpatient,
// They could be 'departed'.  Their patientRegistration could get updated.  I don't know this stuff yet.

    PatientState getPatientStatusFromNewPatientRegSearch(Patient patient) {

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
            // probably ought to merge these fields with what's in Demographics somehow.  For search purposes
            // PatientSearch.xxx should trump demographics.xxx value
            // We have to decide how important or required PatientSearch should be.
        }
        if (patient.patientSearch == null) {
            skipSearch = true; // not quite right.  patientSearch is still optional
        }

        //Pep.PatientStatus patientStatus = null;
        PatientState patientStatus = null;

        // Not sure how worthwhile this is
        if ((firstName == null || firstName.equalsIgnoreCase("random") || firstName.isEmpty())
                && (lastName == null || lastName.equalsIgnoreCase("random") || lastName.isEmpty())
                && (ssn == null || ssn.equalsIgnoreCase("random") || ssn.isEmpty())) {
            skipSearch = true;
        }
        if (skipSearch) {
            //if (Arguments.debug) System.out.println("Skipped patient search because either processing a random patient or PatientSearch section not provided.");
            //return Pep.PatientStatus.NEW; // ???????????????
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
            if (Arguments.debug) System.out.println("Probably okay to proceed with New Patient Reg.");
            //return Pep.PatientStatus.NEW;
            return PatientState.NEW;
        }
        if (!Arguments.quiet) {
            if (!searchResponseMessage.contains("grayed out") && !searchResponseMessage.contains("There are no patients found")) {
                if (!Arguments.quiet) System.err.println("    Search For Patient: " + searchResponseMessage);
            }
        }
        if (searchResponseMessage.contains("There are no patients found.")) {
            if (Arguments.debug) System.out.println("There are no patients found message comes up with Demo role 4.  Don't know about others");
            //return Pep.PatientStatus.NEW; // totally not sure.  And this seems to happen for Update Patient which does not make sense.  Why no patients found?
            return PatientState.NEW; // not sure
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            // If this happens then the page is showing that message, but no other fields are filled in, it seems.  (Level 4 only.  Not level 3!)
            // But I've also seen it not return a message at all, and the Search fields go grey, and Demographics gets filled in.  (Level 3 not 4)
            if (Arguments.debug) System.err.println("***Patient already has an open registration record.  Use Update Patient instead.");
            return PatientState.UPDATE;
        }
        if (searchResponseMessage.startsWith("Search fields grayed out.")) { // , but for some reason does not have an open Registration record
            // I think this happens when we're role 3, not 4.  Oh, happens with role 4 too.  Bettie Bbtest.  Why?  Because the record was closed earlier?
            if (Arguments.debug) System.out.println("I think this happens when we're level 3, not 4.  Can update here?  Won't complain later?");
            if (Arguments.debug) System.out.println("But For now we'll assume this means we just want to do Treatments.  No changes to patientRegistration info.  Later fix this.");
            return PatientState.NEW; // Does this mean the patient's record was previously closed?  If so, shouldn't we continue on?
        }
        if (searchResponseMessage.startsWith("There are no patients found.")) {
            if (Arguments.debug) System.out.println("Patient wasn't found, which means go ahead with New Patient Reg.");
            return PatientState.NEW;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            return PatientState.INVALID;
        }
        if (Arguments.debug) System.out.println("What kinda message?: " + searchResponseMessage);
        return patientStatus;
    }


    boolean doDemographicsSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;

        // Demographics section must contain values in most fields, but could have been populated by now if patient info found (and patient had departed previously)
        Demographics demographics = newPatientReg.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.random = (this.random == null) ? false : this.random; // new, and unnec bec just below
            newPatientReg.demographics = demographics;
        }
        if (demographics.random == null) {
            demographics.random = (this.random == null) ? false : this.random;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        //if (Utilities.elementExistsShorterWait(By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"), 1000) != null) {
        return processSucceeded;
    }

    // Hey, is this section available for a Role 1 CASF?  And others too?  Which roles don't?
    boolean doArrivalLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;
        // Do ArrivalLocation section, if it exists for this level/role
        try {
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(arrivalLocationSectionBy));
            ArrivalLocation arrivalLocation = newPatientReg.arrivalLocation;
            if (arrivalLocation == null) {
                arrivalLocation = new ArrivalLocation();
                newPatientReg.arrivalLocation = arrivalLocation;
            }
            if (arrivalLocation.random == null) {
                arrivalLocation.random = (this.random == null) ? false : this.random;
            }
            if (arrivalLocation.arrivalDate == null) {
                arrivalLocation.arrivalDate = Arguments.date;
            }
            boolean processSucceeded = arrivalLocation.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process arrival/Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("No arrivalLocation section.  Okay.");
            return true;
        }
        catch (Exception e) {
            System.out.println("Some kind of error with arrivalLocation section: " + e.getMessage());
            return false;
        }
    }

    boolean doFlightSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;
        // Flight (only available in Level 4)
        try {
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(flightSectionBy));
            Flight flight = newPatientReg.flight;
            if (flight == null) {
                flight = new Flight();
                newPatientReg.flight = flight;
            }
            if (flight.random == null) {
                flight.random = (this.random == null) ? false : this.random; // can't let this be null
            }
            boolean processSucceeded = flight.process(patient); // flight has required fields in it, so must do it
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process flight for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("There's no flight section, which is the case for levels/roles 1,2,3");
            return true; // a little hack here
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of error in flight section: " + e.getMessage());
            return false;
        }
    }

    boolean doInjuryIllnessSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;
        // Injury/Illness must also contain information.  Can't skip it.
        InjuryIllness injuryIllness = newPatientReg.injuryIllness;
        if (injuryIllness == null) {
            injuryIllness = new InjuryIllness();
            newPatientReg.injuryIllness = injuryIllness;
        }
        if (injuryIllness.random == null) {
            injuryIllness.random = (this.random == null) ? false : this.random;
        }
        boolean processSucceeded = injuryIllness.process(patient); // contains required fields, so must do this.
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process injury/illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
        return processSucceeded;
    }

    boolean doLocationSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;
        // Location (for level 4 only?)  The following takes a bit of time.  Change to have xpath with string "Location"?
        try {
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(locationSectionBy));
            Location location = newPatientReg.location;
            if (location == null) {
                location = new Location();
                //location.random = this.random; // new
                newPatientReg.location = location;
            }
            if (location.random == null) {
                location.random = (this.random == null) ? false : this.random;
            }
            boolean processSucceeded = location.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("There's no location section, which is the case for levels/roles 1,2,3");
            return true;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of (unlikely) error in location section: " + e.getMessage());
            return false;
        }
    }

    boolean doDepartureSection(Patient patient) {
        NewPatientReg newPatientReg = patient.patientRegistration.newPatientReg;
        // Departure
        // If you do a Departure, the "record is closed" and the patient is no longer a patient.  That means you can't update
        // the patient with the Update Patient page.  However, the system allows you to add notes, it appears.
        // So, even if there are treatments to add for this patient, you can do a Departure at this time.
        try {
//            By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/span/table/tbody/tr/td");
            (new WebDriverWait(Driver.driver, 1)).until(presenceOfElementLocated(departureSectionBy));
            Departure departure = newPatientReg.departure;
            if (departure == null) {
                departure = new Departure();
                //departure.random = this.random; // new
                newPatientReg.departure = departure;
            }
            if (departure.random == null) {
                departure.random = (this.random == null) ? false : this.random;
            }
//            if (departure.departureDate == null) {
//                departure.departureDate = Arguments.date;
//            }
            boolean processSucceeded = departure.process(patient);
            if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process departure for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return processSucceeded;
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("There's no departure section.  That doesn't seem right.  Is it?  returning true");
            return true;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of error in departure section?: " + e.getMessage());
            return false;
        }
    }



    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    String getNewPatientRegSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        String message = null;
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);


        // When click on the "Search For Patient" button, a spinner "MB_window" appears for a while
        // and when it's done the entire page appears to be rewritten.  If Selenium is searching for
        // elements during that redraw it can get messed up.  The result of that search is possibly
        // a changed page, but also maybe some search messages, like "There are no patients found."
        // and another is something like "... already has an open Registration record. Please update
        // the patient via Patient Registration > Update Patient page".  Plus if you try to register
        // a patient who is already registered you get another message, possibly in the same place.
        // These messages can be found by different XPaths.  One is
        // By.xpath("//*[@id=\"errors\"]/ul/li") and another, I think, is
        // By.xpath("//*[@id=\"patientRegistrationSearchForm.errors\"]")
        //
        // Then there's the question of how you can tell that the spinner is done.  You could guess
        // that it will take no more than 5 seconds, but maybe that's not long enough.  What I
        // think is now working is the presence and subsequent absence of the MB_window.
        //        // Why do we not get the button first and then click on it?
        Utilities.clickButton(searchForPatientButton); // Not ajax
        // For Role3, the same SSN, Last Name, First Name, same case, gives different results if doing Update Patient than if doing New Patient Reg.  Doesn't find with Update Patient.
        // For Role4, it freaking works right for both New Patient Reg and Update Patient.
        // Hey, compare with the other spnner check in this file.  Does a stalenessOf rather than an invisibilityOf
        try {
//            (new WebDriverWait(Driver.driver, 20)).until(visibilityOfElementLocated(By.id("MB_window"))); // was 2s, was 10s
            (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.id("MB_window"))); // was 2s, was 10s
            if (Arguments.debug) System.out.println("NewPatientReg.getNewPatientRegSearchPatientResponse(), got a spinner window.  Now will try to wait until it goes away.");
            // Next line can throw a timeout exception if the patient has a duplicate.  That is, same name and ssn.  Maybe even same trauma number.  Because selection list comes up. Peter Pptest 666701231
            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window"))); // was after catch
            if (Arguments.debug) System.out.println("NewPatientReg.getNewPatientRegSearchPatientResponse(), spinner window went away.");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Maybe too slow to get the spinner?  Continuing on is okay.");
        }
//        (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window")));
        // Now can check for messages, and if helpful check for grayed out search boxes.  Do both, or is one good enough, or better?
        // If patient was found then there will not be a message when go back to New Patient Reg page.
        // Is that right?  If so, then can ignore the timeout exception.  There's probably a better way.
        // This stuff is flakey.  sometimes happens for level 4, but usually level 3 I thought.

        // This stuff is new:  check with role 3 and role 4  check against aaron too
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 3)) // was 1s
                    .until(visibilityOfElementLocated(newPatientRole3RegSearchMessageAreaBy));
            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();
            if (searchMessageText != null) {
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timeout out waiting for visibility of a message when a patient is not found.  This is okay for Role3 New Patient Reg.  Got exception: " + e.getMessage());
            if (Arguments.debug) System.out.println("Maybe just return a fake message like 'no message'?  But with level 4 get a message saying go to Update Patient.");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
        }

        // This stuff is older, but I think it was working
        // This one should work for New Patient Reg. search, but not for Update Patient search
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
                    .until(visibilityOfElementLocated(patientRegistrationSearchFormErrorsBy));
            if (Arguments.debug) System.out.println("getNewPatientRegSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();
            if (searchMessageText != null) {
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timeout out waiting for visibility of a message when a patient is actually found.  This is okay for Role3 New Patient Reg.  Got exception: " + e.getMessage());
            if (Arguments.debug) System.out.println("Maybe just return a fake message like 'no message'?  But with level 4 get a message saying go to Update Patient.");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
        }

        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
        // I wonder why I couldn't do the same thing elsewhere, perhaps in UpdatePatient, or other places.  Just wouldn't work.  Programming mistake?
        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(ssnField));
            if (ssnTextBoxElement != null) {
                //if (Arguments.debug) System.out.println("I guess ssnbox is available now");
                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
                if (ssnTextBoxAttribute != null) {
                    if (Arguments.debug) System.out.println("ssnTextBoxAttribute: " + ssnTextBoxAttribute);
                }
                else {
                    if (Arguments.debug) System.out.println("I guess there was no ssntextbox attribute");
                }
            }
            else {
                if (Arguments.debug) System.out.println("didn't get a ssnTextBoxelement for some unknown reason.");
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("I guess ssnbox wasn't available for some reason: " + e.getMessage());
        }

        if (ssnTextBoxElement == null) {
            if (Arguments.debug) System.out.println("Didn't get an ssnTextBoxElement.");
        }
        else {
            String disabledAttribute = ssnTextBoxElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                if (Arguments.debug) System.out.println("Didn't find disabled attribute, so not greyed out which means what?  That 'There are no patients found.'?");
            }
            else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    if (Arguments.debug) System.out.println("Grayed out."); // Next line right????????????
                    return "Search fields grayed out.";
                }
            }
        }
        return message;
    }

    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    String getUpdatePatientSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {

        String message = null;

        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);


        // When click on the "Search For Patient" button, a spinner "MB_window" appears for a while
        // and when it's done the entire page appears to be rewritten.  If Selenium is searching for
        // elements during that redraw it can get messed up.  The result of that search is possibly
        // a changed page, but also maybe some search messages, like "There are no patients found."
        // and another is something like "... already has an open Registration record. Please update
        // the patient via Patient Registration > Update Patient page".  Plus if you try to register
        // a patient who is already registered you get another message, possibly in the same place.
        // These messages can be found by different XPaths.  One is
        // By.xpath("//*[@id=\"errors\"]/ul/li") and another, I think, is
        // By.xpath("//*[@id=\"patientRegistrationSearchForm.errors\"]")
        //
        // So I still don't understand patient state, or how searches report that state.
        //
        // Then there's the question of how you can tell that the spinner is done.  You could guess
        // that it will take no more than 5 seconds, but maybe that's not long enough.  What I
        // think is now working is the presence and subsequent absence of the MB_window.
        //        // Why do we not get the button first and then click on it?
        Utilities.clickButton(searchForPatientButton); // Not ajax
        // For Role3, the same SSN, Last Name, First Name, same case, gives different results if doing Update Patient than if doing New Patient Reg.  Doesn't find with Update Patient.
        // For Role4, it freaking works right for both New Patient Reg and Update Patient.
        try {
            (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(By.id("MB_window")));
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Maybe too slow to get the spinner?  Continuing on is okay.");
        }
        (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window")));

        // Now can check for messages, and if helpful check for grayed out search boxes.
        // If patient was found then there will not be a message when go back to New Patient Reg page.
        // Is that right?  If so, then can ignore the timeout exception.  There's probably a better way.
        // This stuff is flakey.  sometimes happens for level 4, but usually level 3 I thought.
        //By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors");

        // This one should work for Update Patient search, but not for New Patient Reg. search
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 1))
                    .until(visibilityOfElementLocated(By.xpath("//*[@id=\"errors\"]/ul/li")));
            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();
            // There's a bug on DEMO where the search comes back with "There are no patients found."
            // when the patient is found when doing search on the New Patient Reg. page.
            // Therefore, we will ignore this message for now, but not ignore it when this is fixed.
            if (searchMessageText != null) {
                if (searchMessageText.equalsIgnoreCase("There are no patients found.")) {
                    System.out.println("There is a bug for Role 3 Update Patient search.  It says 'There are no patients found.' but the patient does exist.");
                    System.out.println("But what if the patient is new, and not found?  Would the message be correct?  And if on Update Patient page, then we shouldn't be on that page.");
                    System.out.println("So if we're on the Update Patient page we should get out.");
                }
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timeout out waiting for visibility of a message for Update Patient search.  Got exception: " + e.getMessage());
            if (Arguments.debug) System.out.println("This happens when patient is found.  With level 4 get a message saying go to Update Patient.  With level 3, nothing");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
        }

        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.

        WebElement ssnTextBoxElement = null;
        try {
            ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(ssnField));
            if (ssnTextBoxElement != null) {
                //if (Arguments.debug) System.out.println("I guess ssnbox is available now");
                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
                if (ssnTextBoxAttribute != null) {
                    if (Arguments.debug) System.out.println("ssnTextBoxAttribute: " + ssnTextBoxAttribute);
                }
                else {
                    if (Arguments.debug) System.out.println("I guess there was no ssntextbox attribute");
                }
            }
            else {
                if (Arguments.debug) System.out.println("didn't get a ssnTextBoxelement for some unknown reason.");
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("I guess ssnbox wasn't available for some reason: " + e.getMessage());
        }

        if (ssnTextBoxElement == null) {
            if (Arguments.debug) System.out.println("Didn't get an ssnTextBoxElement.");
        }
        else {
            String disabledAttribute = ssnTextBoxElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                if (Arguments.debug) System.out.println("Didn't find disabled attribute, so not greyed out which means what?  That 'There are no patients found.'?");
            }
            else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    if (Arguments.debug) System.out.println("Grayed out."); // Next line right????????????
                    return "Search fields grayed out.";
                }
            }
        }
        return message;
    }
}

