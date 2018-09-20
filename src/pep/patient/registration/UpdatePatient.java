package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
//import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class UpdatePatient {
    public Boolean random;
    public Demographics demographics;

    // It will be Flight (level 4) or ArrivalLocsationSection (levels 1,2,3)
    public Flight flight;
    public ArrivalLocation arrivalLocation;

    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    private static final By PATIENT_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static final By SUBMIT_BUTTON = By.xpath("//input[@id='commit']");
    private static final By UPDATE_PATIENT_PAGE_LINK = By.xpath("//span/b/a[@href='/tmds/patientUpdate.html']");

    private static final By departureSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/span/table/tbody/tr/td");
    private static final By flightSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table[2]/tbody/tr/td");
    private static final By locationSectionBy = By.xpath("//*[@id=\"patientRegForm\"]/table[5]/tbody/tr/td");

    private static final By searchForPatientButton = By.xpath("//*[@id=\"patientRegistrationSearchForm\"]//input[@value='Search For Patient']");

    private static final By someStupidContinueButtonOnSensitiveInfoPopupBy = By.xpath("/html/body/table[2]/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[2]/td[1]/input"); // verified for gold & demo

    private static final By firstNameField = By.id("firstName");
    private static final By lastNameField = By.id("lastName");
    private static final By ssnField = By.id("ssn");
    private static final By traumaRegisterNumberField = By.id("registerNumber");

    private static final By newPatientRole3RegSearchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // NEW patient????
    private static final By errorMessagesBy                       = By.id("patientRegistrationSearchForm.errors"); // correct
    private static final By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors"); // huh?  //*[@id="errors"]/ul/li


    //boolean skipRegistration;

    public UpdatePatient() {
        if (Arguments.template) {
            this.random = null;
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.arrivalLocation = new ArrivalLocation();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
            this.departure = new Departure();
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
        boolean succeeded = false;
        // We either got here because the default after logging in is this page, or perhaps we deliberately clicked on "Patient Registration" tab.
        if (patient.patientRegistration == null
                || patient.patientRegistration.updatePatient.demographics == null
                || patient.patientRegistration.updatePatient.demographics.firstName == null
                || patient.patientRegistration.updatePatient.demographics.firstName.isEmpty()
                || patient.patientRegistration.updatePatient.demographics.firstName.equalsIgnoreCase("random")
                || patient.patientRegistration.updatePatient.demographics.lastName == null
                || patient.patientRegistration.updatePatient.demographics.lastName.isEmpty()
                || patient.patientRegistration.updatePatient.demographics.lastName.equalsIgnoreCase("random")
                ) {
            if (!Arguments.quiet) System.out.println("  Processing Registration ...");
        } else {
            if (!Arguments.quiet)
                System.out.println("  Processing Registration for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
        }

        // Wow, at this point we're already sitting at New Patient Reg. page, if we came from Login.
        // If we didn't come here from Login, then we probably looped back here when we started a 2nd patient.
        // How do we know from looking at the JSON file whether we should be doing New or Update?  We can't,
        // unless we change the JSON structure.

        // In order to know whether to do a New Patient Reg., or an Update Patient, we need to do a search, and
        // to do a search we have to be on the New Patient Reg. page, or the Update Patient page.
        // (unless we want to user to specify -update or something like that, which would still require a search.
        // So might as well do the search on the New Patient Reg. page.
        boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, UPDATE_PATIENT_PAGE_LINK);
        if (Arguments.debug) System.out.println("Navigated?: " + navigated);
        if (!navigated) {
            return false;
        }

        // Okay, problem: PeP.PatientStatus vs PatientState (enum)
        //Pep.PatientStatus patientStatus = getPatientStatusFromUpdatePatientSearch(patient);
        PatientState patientStatus = getPatientStatusFromUpdatePatientSearch(patient);
        switch (patientStatus) {
            //case REGISTERED:
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
    //Pep.PatientStatus getPatientStatusFromUpdatePatientSearch(Patient patient) {
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


        //Pep.PatientStatus patientStatus = null;
        PatientState patientStatus = null;




//        // All this next stuff is merely to determine whether to skip searching for the patient.
//        // I don't know why.  And since it's pretty much required now that we have a PatientSearch
//        // object filled in then I don't see why we should skip.
//// this has to be wrong.  If we need this, it needs fixing.
//        if (demographics == null) { // totally new.  This is for the case where we have New Patient Reg info, but the patient is already in the system, so we want to do update, but don't have update info!
//            demographics = patient.patientRegistration.newPatientReg.demographics; // questionable thing to do.  Don't like this logic    NEW patientReg?????
//        }
//        // If patient has no name or ssn, then don't bother searching, and we assume that the random value generated will
//        // be unique.
//        if (demographics == null) {
//            skipSearch = true;
//        }
//        else if (demographics.firstName == null && demographics.lastName == null && demographics.ssn == null) {
//            skipSearch = true;
//        }
//        else if ((demographics.firstName.equalsIgnoreCase("random") || demographics.firstName.isEmpty())
//                && (demographics.lastName.equalsIgnoreCase("random") || demographics.lastName.isEmpty())
//                && (demographics.ssn.equalsIgnoreCase("random") || demographics.ssn.isEmpty())) {
//            skipSearch = true;
//        }
//        if (skipSearch) {
//            if (Arguments.debug) System.out.println("Skipped patient search because processing a random patient, probably, and assuming no duplicates.");
//            return Pep.PatientStatus.NEW; // ???????????????
//        }

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
        System.out.println("Hey, this assumes we have a PatientSearch section.");
        String searchResponseMessage = getUpdatePatientSearchPatientResponse(
                ssn,
                firstName,
                lastName,
                traumaRegisterNumber);
        // The following is wrong, based on what the upper method returns at this time
//        if (searchResponseMessage == null) {
//            System.out.println("Probably okay to proceed with New Patient Reg.");
//            return Pep.PatientStatus.NEW;
//        }
        if (searchResponseMessage.equalsIgnoreCase("Registered")) { // "registered"?  "open"?
            System.out.println("Probably okay to proceed with Update Patient Reg.");
            //return Pep.PatientStatus.REGISTERED; // experiment
            return PatientState.UPDATE; // ????
        }

        if (searchResponseMessage.equalsIgnoreCase("No record found to update.")) { // something's wrong
            System.out.println("We want to update a patient, but the specified patient isn't found.");
            //return Pep.PatientStatus.INVALID; // How about NOT_FOUND ?
            return PatientState.INVALID; // ?
        }

        if (searchResponseMessage.contains("There are no patients found.")) {
            if (Arguments.debug) System.out.println("This message of 'There are no patients found.' doesn't make sense if we jumped to Update Patient.");
            System.out.println("This is due to a bug in TMDS Update Patient page for a role 4, it seems.  Also role 3???????");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            System.out.println("UpdatePatient.getPatientStatusFromUpdatePatientSearch(), I think there's an error here.  The patient should have been found.  If continue on and try to Submit info in Update Patient, an alert will say that the info will go to a patient with a different SSN");
            //return Pep.PatientStatus.NEW; // While this is probably correct, it represents a mistake.  Why no patients found?
            //return Pep.PatientStatus.INVALID; // wrong.  what's better?
            return PatientState.INVALID; // wrong.  what's better?
            //return PatientStatus.REGISTERED; // This is because there's a bug in search for role3 in update patient search
        }
        if (searchResponseMessage.contains("already has an open Registration record.")) {
            // "AATEST, AARON - 666701215 already has an open Registration record. Please update the patient via Patient Registration > Update Patient page."
            // If this happens then the page is showing that message, but no other fields are filled in, it seems.  (Level 4 only.  Not level 3!)
            // But I've also seen it not return a message at all, and the Search fields go grey, and Demographics gets filled in.  (Level 3 not 4)
            if (Arguments.debug) System.out.println("Prob should switch to either Update Patient or go straight to Treatments.");
            if (!Arguments.quiet) System.out.println("  NOT! Skipping remaining Registration Processing for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
            //this.skipRegistration = true;
            //return true;
            //return false;
            //return Pep.PatientStatus.REGISTERED; // change to OPEN_REGISTRATION
            return PatientState.UPDATE; // change to OPEN_REGISTRATION
        }
        if (searchResponseMessage.startsWith("I think a patient was found")) { // , but for some reason does not have an open Registration record
            // I think this happens when we're level 3, not 4.
            if (Arguments.debug) System.out.println("I think this happens when we're level 3, not 4.  No, happens with 4.  Can update here?  Won't complain later?");
            if (Arguments.debug) System.out.println("But For now we'll assume this means we just want to do Treatments.  No changes to patientRegistration info.  Later fix this.");
            if (!Arguments.quiet) System.out.println("  Skipping remaining Registration Processing for " + patient.patientRegistration.updatePatient.demographics.firstName + " " + patient.patientRegistration.updatePatient.demographics.lastName + " ...");
            //this.skipRegistration = true;
            //return true; // patient is in the system, but should we do more patientRegistration?  If so, here, or in Update Patient?  Probably Update Patient
            //return Pep.PatientStatus.REGISTERED; // I think.  Not sure.
            return PatientState.UPDATE; // I think.  Not sure.
        }
        if (searchResponseMessage.startsWith("There are no patients found.")) {
            if (Arguments.debug) System.out.println("Patient wasn't found, which means go ahead with New Patient Reg.");
            //return true;
            //return Pep.PatientStatus.NEW;
            return PatientState.NEW;
        }
        if (searchResponseMessage.contains("must be alphanumeric")) {
            if (!Arguments.quiet) System.err.println("***Failed to accept search field because not alphanumeric.");
            //return false;
            //return Pep.PatientStatus.INVALID;
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
        //if (Arguments.debug) System.out.println("updatePatient.process() will now click submit button to register patient.");
        // Why do we not get the button first and then click on it?



        // !!!!!!!!!!!!!!!!!!!!!!!!
        // I think this next stuff should be looked at again.  What are all the possibilities?
        // I'm now getting "No record found to update." even though the patient should have been found.
        // !!!!!!!!!!!!!!!!!!!!!!!!



        // I think this next line actually blocks until the patient gets saved.  No, I don't think so.  It takes about 4 seconds before the spinner stops and next page shows up.   Are all submit buttons the same?
        Utilities.clickButton(SUBMIT_BUTTON); // Not AJAX, but does call something at /tmds/patientRegistration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner
        // The above line will generate an alert saying "The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
        // This happens even with Role 4 and doing Update Patient rather than New Patient Reg.  Therefore, what's the freaking difference between the two?
        // There is some diff, I think, but not sure what.
        // But if you get rid of the alert then the submit fails because it says "No record found to update".  What the heck?  Did I forget to do a search at the
        // start of Update Patient?????
        // check for alert
        try {
            Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Update Patient page, after click Submit, Didn't find an alert, which is probably okay.  " + e.getMessage() + " ... Continuing.");
        }



        //if (Arguments.debug) System.out.println("updatePatient.process() will now check for successful patient record creation, or other messages.  This seems to block okay.");

        WebElement webElement;
        try {
//            By errorMessagesBy = By.id("patientRegistrationSearchForm.errors"); // correct
            webElement = (new WebDriverWait(Driver.driver, 60)) // does this actually work, or does it just fly through?  I think it works.  Can take a long time on gold?
                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy))); // fails: 1
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
                return false; // Fails 5, "Patient's Pre-Registration has been created.",  "Initial Diagnosis is required", failed slow 3G
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("updatePatient.process(), Failed to get message from message area.  Exception:  " + e.getMessage());
            return false;
        }

        if (Arguments.debug) System.out.println("updatePatient.process() I guess we got some kind of message, and now returning true.");

        return true; // success ??????????????????????????
    }

    // WHAT'S WITH ALL THIS NEWPATIENTREG STUFF IN UPDATEPATIENT?????????????????????????????????????????????????????????????????

    boolean doDemographicsSection(Patient patient) {
        UpdatePatient updatePatient = patient.patientRegistration.updatePatient;

        // Demographics section must contain values in most fields, but could have been populated by now if patient info found (and patient had departed previously)
        Demographics demographics = updatePatient.demographics;
        if (demographics == null) {
            demographics = new Demographics();
            demographics.random = (this.random == null) ? false : this.random; // new, and unnec bec below
           // newPatientReg.demographics = demographics;
            updatePatient.demographics = demographics;
        }
        if (demographics.random == null) {
            demographics.random = (this.random == null) ? false : this.random;
        }
        boolean processSucceeded = demographics.process(patient); // demographics has required fields in it, so must do it
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process demographics for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
        // Arrival Location (only available in levels 3,2,1)  Change that xpath to contain "Arrival/Location"
        //if (Utilities.elementExistsShorterWait(By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr/td"), 1000) != null) {
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
            if (Arguments.debug) System.out.println("There's no departure section.  Is that right?  returning true");
            return true;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of error in departure section?: " + e.getMessage());
            return false;
        }
    }


//    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
//    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
//    String getUpdatePatientSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
//        String message = null;
//
//        Utilities.fillInTextField(ssnField, ssn);
//        Utilities.fillInTextField(lastNameField, lastName);
//        Utilities.fillInTextField(firstNameField, firstName);
//        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);
//
//
//        // When click on the "Search For Patient" button, a spinner "MB_window" appears for a while
//        // and when it's done the entire page appears to be rewritten.  If Selenium is searching for
//        // elements during that redraw it can get messed up.  The result of that search is possibly
//        // a changed page, but also maybe some search messages, like "There are no patients found."
//        // and another is something like "... already has an open Registration record. Please update
//        // the patient via Patient Registration > Update Patient page".  Plus if you try to register
//        // a patient who is already registered you get another message, possibly in the same place.
//        // These messages can be found by different XPaths.  One is
//        // By.xpath("//*[@id=\"errors\"]/ul/li") and another, I think, is
//        // By.xpath("//*[@id=\"patientRegistrationSearchForm.errors\"]")
//        //
//        // Then there's the question of how you can tell that the spinner is done.  You could guess
//        // that it will take no more than 5 seconds, but maybe that's not long enough.  What I
//        // think is now working is the presence and subsequent absence of the MB_window.
//        //
//        Utilities.clickButton(searchForPatientButton); // Not ajax
//        // For Role3, the same SSN, Last Name, First Name, same case, gives different results if doing Update Patient than if doing New Patient Reg.  Doesn't find with Update Patient.
//        // For Role4, it freaking works right for both New Patient Reg and Update Patient.
//        try {
//            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("MB_window"))); // was 2s
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Maybe too slow to get the spinner?  Continuing on is okay.");
//        }
//        (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window")));
//
//        // Now can check for messages, and if helpful check for grayed out search boxes.
//        // If patient was found then there will not be a message when go back to New Patient Reg page.
//        // Is that right?  If so, then can ignore the timeout exception.  There's probably a better way.
//        // This stuff is flakey.  sometimes happens for level 4, but usually level 3 I thought.
//
//        // This stuff is new:  check with role 3 and role 4  check against aaron too
//        try {
//            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
//                    .until(ExpectedConditions.visibilityOfElementLocated(newPatientRole3RegSearchMessageAreaBy));
//            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
//            String searchMessageText = searchMessage.getText();
//            if (searchMessageText != null) {
//                return searchMessageText;
//            }
//        }
//        catch (TimeoutException e) {
//            if (Arguments.debug) System.out.println("Timeout out waiting for visibility of a message when a patient is not found.  This is okay for Role3 New Patient Reg.  Got exception: " + e.getMessage());
//            if (Arguments.debug) System.out.println("Maybe just return a fake message like 'no message'?  But with level 4 get a message saying go to Update Patient.");
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
//        }
//
//
//
//        // This stuff is older, but I think it was working
//        // This one should work for New Patient Reg. search, but not for Update Patient search
//        try {
//            WebElement searchMessage = (new WebDriverWait(Driver.driver, 2)) // was 1s
//                    .until(ExpectedConditions.visibilityOfElementLocated(patientRegistrationSearchFormErrorsBy));
//            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
//            String searchMessageText = searchMessage.getText();
//            if (searchMessageText != null) {
//                return searchMessageText;
//            }
//        }
//        catch (TimeoutException e) {
//            if (Arguments.debug) System.out.println("Timeout out waiting for visibility of a message when a patient is actually found.  This is okay for Role3 New Patient Reg.  Got exception: " + e.getMessage());
//            if (Arguments.debug) System.out.println("Maybe just return a fake message like 'no message'?  But with level 4 get a message saying go to Update Patient.");
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
//        }
//
//        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
//
//        WebElement ssnTextBoxElement = null;
//        try {
//            ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(ssnField));
//            if (ssnTextBoxElement != null) {
//                //if (Arguments.debug) System.out.println("I guess ssnbox is available now");
//                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
//                if (ssnTextBoxAttribute != null) {
//                    if (Arguments.debug) System.out.println("ssnTextBoxAttribute: " + ssnTextBoxAttribute);
//                }
//                else {
//                    if (Arguments.debug) System.out.println("I guess there was no ssntextbox attribute");
//                }
//            }
//            else {
//                if (Arguments.debug) System.out.println("didn't get a ssnTextBoxelement for some unknown reason.");
//            }
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("I guess ssnbox wasn't available for some reason: " + e.getMessage());
//        }
//
//        if (ssnTextBoxElement == null) {
//            if (Arguments.debug) System.out.println("Didn't get an ssnTextBoxElement.");
//        }
//        else {
//            String disabledAttribute = ssnTextBoxElement.getAttribute("disabled");
//            if (disabledAttribute == null) {
//                if (Arguments.debug) System.out.println("Didn't find disabled attribute, so not greyed out which means what?  That 'There are no patients found.'?");
//            }
//            else {
//                if (disabledAttribute.equalsIgnoreCase("true")) {
//                    if (Arguments.debug) System.out.println("Grayed out."); // Next line right????????????
//                    return "I think a patient was found.";
//                }
//            }
//        }
//        return message;
//    }

    // Maybe this method should be changed to just return a patient status depending on the clues given from the search results.
    // Perhaps the most telling is if the search boxes get greyed out, rather than looking for messages.
    String getUpdatePatientSearchPatientResponse(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        System.out.println("\t\t\tHow many times are we going to come here, UpdatePatient.getUpdatePatientSearchPatientResponse(...)");
        String message = null;
        // Wow, this assumes we've got a Search For Patient section ready for input
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);

        String mainWindowHandleName = Driver.driver.getWindowHandle();
        Set<String> originalWindowHandles = Driver.driver.getWindowHandles();
        int nOriginalWindowHandles = originalWindowHandles.size();
        System.out.println("Original window: " + Driver.driver.getWindowHandle()); // CDwindow-AD6D6EC03E5665418216BF0A06083A71

        WebDriver saveThisDriver = Driver.driver;

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
        //
        WebElement searchButton = null;
        try {
            searchButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(searchForPatientButton));
            searchButton.click(); // yields "There are no patients found" on demo role 3, but role 4 works and ALWAYS comes up with "Sensitive Information" window
        }
        catch (Exception e) {
            System.out.println("Couldn't get the search button or click on it.");
        }
        // For the above searchButton.click(), a javascript window.open() call may be made.

        // We may get a "Sensitive Information" popup window with the "Continue"/"Cancel" buttons.
        // This currently always happens with Role4 even when patient's info isn't marked sensitive.
        // It may happen with Role3 too.
        // If it pops up, we have to click on "Continue" to get rid of it and continue on.
        // If it pops up, there will be another window listed when you do getWindowHandles()
        // The active window will have changed for one thing.
        // Supposedly  if we do a driver.getWindowHandle() and it's different than before, we have to take care of it.
        // (This stopped working for some reason.  Need to fix.)


        // see if some wait time will change the result on the next line.  Coming in as same as parent window without a wait.  No, waiting makes no diff
        //Driver.driver.navigate().refresh(); // helpful? No not helpful.

        Utilities.sleep(2555); // hate to do this, but the Sensitive Information window isn't showing up fast enough.  Maybe can do a watch for stale window or something?

        WebElement continueButton = null;
        String parent = Driver.driver.getWindowHandle();
        Set<String> windowHandles = Driver.driver.getWindowHandles();
        int nNewWindowHandles = windowHandles.size();
        if (nNewWindowHandles != nOriginalWindowHandles) {
            Iterator<String> iterator = windowHandles.iterator();
            while (iterator.hasNext()) {
                String child_window = iterator.next();
                if (!parent.equals(child_window)) {
                    Driver.driver.switchTo().window(child_window);
                    System.out.println("Switched to child window, and now title is " + Driver.driver.getTitle());
                    //Driver.driver.close();
                    continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
                    continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.
                    Driver.driver.switchTo().window(parent);
                    Driver.driver.switchTo().defaultContent(); // doesn't seem to help
                    WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
                    Driver.driver.switchTo().frame(someFrame); // doesn't throw
                    break;
                }
            }
        }
        // test code.  I think it shows there's a problem.  It doesn't get the text
        By lastNameFieldBy = By.id("patientRegistration.lastName");
        try {
            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
            String someText = lastNameField.getText();// why always blank?
            System.out.println("someText: " + someText); // Why is this always blank?
        }
        catch (Exception e) { // TimeoutException
            System.out.println("What the crud, tried to get last name: " + e.getMessage()); // "TMDS | Sensitive Information"  What?????????
        }



//        String possiblyNewWindowHandle = Driver.driver.getWindowHandle(); // possible getting here too fast or not fast enough????
//        if (possiblyNewWindowHandle.equalsIgnoreCase(mainWindowHandleName)) {
//            System.out.println("Nope, no Sensitive window popup.");
//        }
//        else {
//            System.out.println("Yep, got Sensitive window popup.");
//            WebElement continueButton = null;
//            String parent = Driver.driver.getWindowHandle();
//            Set<String>s1=Driver.driver.getWindowHandles();
//            Iterator<String> I1= s1.iterator();
//            while(I1.hasNext()) {
//                String child_window = I1.next();
//                if(!parent.equals(child_window)) {
//                    Driver.driver.switchTo().window(child_window);
//                    System.out.println("Switched to child window, and now title is " + Driver.driver.getTitle());
//                    //Driver.driver.close();
//                    continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
//                    // Next line destroys the current "Sensitive" window and we should have one window again, the main one.
//                    // However, the "driver" gets mixed up and cannot do a close(), which is necessary, because the window gets destroyed
//                    // by the click on the button.
//                    continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.
//                    //Driver.driver.switchTo().parentFrame(); // "Change focus to the parent context."
//
//                    //Driver.driver = saveThisDriver;
//                    //Driver.driver.close(); // wrong, "no such window: target window already closed", which means driver is now useless.
//                    break;
//                }
//            }
//            Driver.driver.switchTo().window(parent);
//            Driver.driver.switchTo().defaultContent(); // doesn't seem to help
//            WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
//            Driver.driver.switchTo().frame(someFrame); // doesn't throw
//
//            By lastNameFieldBy = By.id("patientRegistration.lastName");
//            try {
//                WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
//                String someText = lastNameField.getText();
//                System.out.println("someText: " + someText);
//            }
//            catch (Exception e) { // TimeoutException
//                System.out.println("What the crud, tried to get last name: " + e.getMessage()); // "TMDS | Sensitive Information"  What?????????
//            }
//
//        }



        // If it doesn't come up, then the Search For Patient button remains on the screen and does not go stale.
        // So we need to account for this.  If the Sensitive Information window comes up, we have to take care of it.
        // If it doesn't come up we ignore it.  But what do you check that doesn't require a timeout?
        // If we get a Sensitive Information popup window, then I think that the Update Patient window
        // pretty much goes blank, except the Update Patient words.
//        try {
//            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(searchButton)); // fails: demo role3: 2
//        }
//        catch (Exception e) {
//            System.out.println("UpdatePatient.getUpdatePatientSearchPatientResponse(), and there was no salteness of the searchButton");
//        }






//        WebElement continueButton = null;
//        String parent = Driver.driver.getWindowHandle();
//        Set<String>s1=Driver.driver.getWindowHandles();
//        Iterator<String> I1= s1.iterator();
//        while(I1.hasNext()) {
//            String child_window=I1.next();
//            if(!parent.equals(child_window)) {
//                Driver.driver.switchTo().window(child_window);
//                System.out.println("Switched to child window, and now title is " + Driver.driver.getTitle());
//                //Driver.driver.close();
//                continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
//                // Next line destroys the current "Sensitive" window and we should have one window again, the main one.
//                // However, the "driver" gets mixed up and cannot do a close(), which is necessary, because the window gets destroyed
//                // by the click on the button.
//                continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.
//                //Driver.driver.switchTo().parentFrame(); // "Change focus to the parent context."
//
//                //Driver.driver = saveThisDriver;
//                //Driver.driver.close(); // wrong, "no such window: target window already closed", which means driver is now useless.
//                break;
//            }
//        }




        //Driver.driver = saveThisDriver;
        //Driver.driver.close(); // no such window: target window already closed

        //parent=Driver.driver.getWindowHandle(); // causes no such window on next call to switch

//        Driver.driver.switchTo().window(parent);
//        Driver.driver.switchTo().defaultContent(); // doesn't seem to help
//        WebElement someFrame = Driver.driver.findElement(By.id("portletFrame"));
//        Driver.driver.switchTo().frame(someFrame); // doesn't throw
//
//        By lastNameFieldBy = By.id("patientRegistration.lastName");
//        try {
//            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
//            String someText = lastNameField.getText();
//            System.out.println("someText: " + someText);
//        }
//        catch (Exception e) { // TimeoutException
//            System.out.println("What the crud, tried to get last name: " + e.getMessage()); // "TMDS | Sensitive Information"  What?????????
//        }


//        Set<String> windowHandlesSet = Driver.driver.getWindowHandles();
//        Iterator<String> iterator = windowHandlesSet.iterator();
//        while(iterator.hasNext()) {
//            String someWindow = iterator.next();
//            Driver.driver.switchTo().window(someWindow);
//            //By lastNameFieldBy = By.id("patientRegistration.lastName");
//            try {
//                WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
//            }
//            catch (Exception e) { // TimeoutException
//                System.out.println(e.getMessage());
//            }
//        }




        //Driver.driver.close(); // completely shuts down the browser.  Not good.
        //Driver.driver.navigate().refresh(); // causes return to first page, patient registrtion

        //boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK, UPDATE_PATIENT_PAGE_LINK);



        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Nothing freaking works after that driver.switchTo().window(child) except stuff on child.
//        By updatePatientLink = By.xpath("//*[@id=\"a_3\"]");
//        boolean navigated = Utilities.myNavigate(updatePatientLink);
//        if (Arguments.debug) System.out.println("Navigated?: " + navigated);

        // Nothing wrong with follow code, but Selenium will not find the element.  Why?
        // Most of the examples show that you have to do a driver.close() but you can't do that after you've
        // clicked on the button which dismisses the window.
        //By lastNameFieldBy = By.id("patientRegistration.lastName");
//        try {
//            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
//        }
//        catch (Exception e) { // TimeoutException
//            System.out.println(e.getMessage());
//        }






















        // It is possible that the above click causes a Sensitive Information pop up window, and if so it needs dismissal.
        // But it's not an alert.  There are two buttons "Continue", and "Cancel".  We want "Continue".
        // This button belongs to a different window, and a different page.  We're not supposed to be in the big page
        // that has everything, and yet the locators seem to think we should be, provided by Chrome.
        // This one is much shorter.  I don't know how this is connected to the previous window.

        // Here comes a big hack.  First we assume there's going to be a popup window saying something about sensitivity
        // and so we wait for it by waiting for the previous page to go stale.  Then we get a set of window handles
        // that the webdriver knows about, and try to do a switch to that window so that the locators will work, and if
        // that doesn't throw an exception we look for the continue button and click on it if it exists,
        // which causes that window to go away and we're automatically back to the previous Update Patient window.

        // The first assumption may not always be true.  I don't know why it has always popped up.  Maybe it won't.



//        // This actually works, but maybe only by chance.  Not reliable probably.
//        Set<String> windowHandlesSet = Driver.driver.getWindowHandles();
//        int setSize = windowHandlesSet.size();
//        int ctr = 0;
//        for (String windowHandle : windowHandlesSet) {
//            ctr++;
//            if (ctr < setSize) {
//                continue;
//            }
//            Driver.driver.switchTo().window(windowHandle);
//        }
//        WebElement continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
//        continueButton.click(); // should make the fields go gray.  Anything else?

//        System.out.println(Driver.driver.getWindowHandle()); // same as before
//
//
//
//        //WebElement continueButton;
//        for (String popUpWindow : Driver.driver.getWindowHandles()) {
//            try {
//                if (popUpWindow.equals(mainWindowHandleName)) {
//                    continue;
//                }
//                // first time through the next line causes a switch to a window that only says Update Patient.  2nd time it brings up Sensitive Info popup window
//                Driver.driver.switchTo().window(popUpWindow); // slow first time because wrong window exception is thrown, and this causes the warning window to go away, but we didn't click continue yet
//                System.out.println("I guess a Driver is coupled with a window, and this driver now has a title of " + Driver.driver.getTitle());
//                System.out.println("The name of the window to switch to is " + popUpWindow);
//                System.out.println(Driver.driver.getWindowHandle()); // same as before, first time, second time through different
//                // next line, first time through, throws timeout exception because there's no continue button on it
//                continueButton = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(someStupidContinueButtonOnSensitiveInfoPopupBy));
//                // Next line should destroy current window and we should have one window again, the main one.
//                continueButton.click(); // causes Sensitive Info popup to go away, Update Patient returns, and makes the fields go gray.
//                break;
//            }
//            catch (NoSuchWindowException e) {
//                System.out.println("UpdatePatient.getUpdatePatientSearchPatientResponse(), No such window exception");
//            }
//            catch (Exception e) { // timeout, right?
//                 System.out.println("Some other exception: " + e.getMessage());
//            }
//        }
//        System.out.println(Driver.driver.getWindowHandle()); // same as the popup window
//
//        //System.out.println("This driver has a current url of " + Driver.driver.getCurrentUrl()); // fails
//
//        // There better be just one window
//        for (String someWindow : Driver.driver.getWindowHandles()) {
//            System.out.println("Handle: " + someWindow);
//            Driver.driver.switchTo().window(someWindow);
//        }
//        String anotherWindowString = Driver.driver.getWindowHandle();
//        System.out.println("anotherWindowString: " + anotherWindowString);
//        //Driver.driver.close(); // experiment.  Says no such window: target window already closed
//
//        // I think next line is nec but not sufficient to be able to get the main window's elements by locator
//        Driver.driver.switchTo().window(mainWindowHandleName); // appears not to do anything, but maybe did
//
//        // Do we need to give that window focus or something before Selenium knows about it?
//        System.out.println(Driver.driver.getWindowHandle());
//
//        // Test this bastard page now by seeing if can find the LastName field
//        //By lastNameFieldBy = By.id("patientRegistration.lastName");
//        try {
//            WebElement lastNameField = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(lastNameFieldBy));
//        }
//        catch (Exception e) { // TimeoutException
//            System.out.println(e.getMessage());
//        }

        // For Role3, the same SSN, Last Name, First Name, same case, gives different results if doing Update Patient than if doing New Patient Reg.  Doesn't find with Update Patient.
        // For Role4, it freaking works right for both New Patient Reg and Update Patient.


// Don't eliminate this code yet.  We may not always get that sensitive window popup, and maybe then this thing will work.  I doubt it though.
//        try {
//            System.out.println("Now gunna check for visibility of the MB_window.  Why?  Because couldn't find the Continue button?");
//            (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.visibilityOfElementLocated(By.id("MB_window")));  // was 2s
//        }
//        catch (Exception e) { // timeoutException
//            if (Arguments.debug) System.out.println("Maybe too slow to get the spinner?  Continuing on is okay.");
//        }
//        (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("MB_window")));





        // Now can check for messages, and if helpful check for grayed out search boxes.
        // If patient was found then there will not be a message when go back to New Patient Reg page.
        // Is that right?  If so, then can ignore the timeout exception.  There's probably a better way.
        // This stuff is flakey.  sometimes happens for level 4, but usually level 3 I thought.
        //By patientRegistrationSearchFormErrorsBy = By.id("patientRegistrationSearchForm.errors");

        // This one should work for Update Patient search, but not for New Patient Reg. search
        try {
            WebElement searchMessage = (new WebDriverWait(Driver.driver, 1))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errors\"]/ul/li"))); // hey, put this where it belongs.  works for gold
            if (Arguments.debug) System.out.println("getUpdatePatientSearchPatientResponse(), search message: " + searchMessage.getText());
            String searchMessageText = searchMessage.getText();

            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // There's a bug on DEMO where the search comes back with "There are no patients found."
            // when the patient is found when doing search on the New Patient Reg. page.
            // I think this is only for Role3 on both Demo and Gold
            // I think for Role4 we timeout on Demo.  Not sure Gold
            // Therefore, we will ignore this message for now, but not ignore it when this is fixed.
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            if (searchMessageText != null) {
                if (searchMessageText.equalsIgnoreCase("There are no patients found.")) {
                    System.out.println("There is a bug for Role 3 Update Patient search.  It says 'There are no patients found.' but the patient does exist.");
                    System.out.println("But what if the patient is new, and not found?  Would the message be correct?  And if on Update Patient page, then we shouldn't be on that page.");
                    System.out.println("So if we're on the Update Patient page we should get out.");
                    //return "Registered"; // REMOVE THIS WHEN THE BUG IS FIXED IN DEMO.  Can't have this here because can't update a patient that isn't found "No record found to update."
                }
                return searchMessageText;
            }
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for visibility of a message for Update Patient search.  Got exception: " + e.getMessage());
            if (Arguments.debug) System.out.println("This happens when patient is found.  I think different for New Patient Reg, which displays message.");
            if (Arguments.debug) System.out.println("Should we just return 'Patient found.' or something similar?");
            message = "Registered"; // experiment
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kind of exception thrown when waiting for error message.  Got exception: " + e.getMessage());
        }

//        // Now we could check the search text boxes to see if they got grayed out.  If so, it means a patient was found.
//        // Does this work?
//        WebElement ssnTextBoxElement = null;
//        try {
//            Driver.driver.switchTo().window(currentWindowHandle);
//
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            // why does next line throw TimeoutException???????????
//            ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(ssnField)); // id="ssn"
//            //ssnTextBoxElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(ssnField))); // id="ssn"
//            if (ssnTextBoxElement != null) {
//                //if (Arguments.debug) System.out.println("I guess ssnbox is available now");
//                String ssnTextBoxAttribute = ssnTextBoxElement.getAttribute("disabled");
//                if (ssnTextBoxAttribute != null) {
//                    if (Arguments.debug) System.out.println("ssnTextBoxAttribute 'disabled': " + ssnTextBoxAttribute);
//                }
//                else {
//                    if (Arguments.debug) System.out.println("I guess there was no ssntextbox attribute");
//                }
//            }
//            else {
//                if (Arguments.debug) System.out.println("didn't get a ssnTextBoxelement for some unknown reason.");
//            }
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("I guess ssnbox wasn't available for some reason: " + e.getMessage());
//        }
//
//        if (ssnTextBoxElement == null) {
//            if (Arguments.debug) System.out.println("Didn't get an ssnTextBoxElement.");
//        }
//        else {
//            String disabledAttribute = ssnTextBoxElement.getAttribute("disabled");
//            if (disabledAttribute == null) {
//                if (Arguments.debug) System.out.println("Didn't find disabled attribute, so not greyed out which means what?  That 'There are no patients found.'?");
//            }
//            else {
//                if (disabledAttribute.equalsIgnoreCase("true")) {
//                    if (Arguments.debug) System.out.println("Grayed out."); // Next line right????????????
//                    return "I think a patient was found."; // this is the one we want
//                }
//            }
//        }
        return message;
    }
}

