package pep.patient.registration;

// It's dangerous to use this class for both New Patient Reg., and Update Patient (or any other page)
// because the selectors can be different, for sure.
// Probably same true for flight and other sections of those pages.
// Dumb to assume the developers used the exact same code.

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.List;

import static pep.Pep.isDemoTier;
import static pep.utilities.Driver.driver;

public class Demographics { // shouldn't it be "Demographic"?  One patient == one demographic?
    public Boolean random; // true if want this section to be generated randomly
    public String lastName; // "text, required";
    public String firstName; // = "random"; // "text, required";
    public String ssn; // "text, required";
    public String fmp; // "option 1-52, required";
    public String sponsorSsn; // "text, required";
    public String dob; // "dd/mm/yyyy, required";
    public String gender; // "option 1-2, required";
    public String race; // "option 1-14, required";
    public String nation; // "option 1-64, required";
    public String branch; // "option 1-28, required";
    public String rank; // "option 1-?, required, depends on branch";
    public String unitEmployer; // "text";
    public String patientCategory; // "option 1-7, required";
    public String vipType; // "option 1-17";
    public String visitType; // "option 0-1";
    public String traumaRegisterNumber; // "text";
    public Boolean sensitiveRecord;

    // Why are these locators not working for UpdatePatient?????????? locators are based on New Patient Reg and not on UpdatePatient, so they cannot be used as is for Update Patient!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! At least for Demo.  Not sure about gold
//    private static final By PD_LAST_NAME_FIELD = By
//            .xpath("//input[@id='patientRegistration.lastName']");
    private static By PD_LAST_NAME_FIELD = By.id("patientRegistration.lastName");
    private static By PD_FIRST_NAME_FIELD = By
            .xpath("//input[@id='patientRegistration.firstName']");
    private static By PD_SSN_FIELD = By.xpath("//input[@id='patientRegistration.ssn']");
    private static By PD_FMP_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.sponsorFmp']");
    private static By PD_DOB_FIELD = By.xpath("//input[@id='formatDob']");
    private static By PD_AGE_FIELD = By.xpath("//input[@id='patientRegistration.age']");
    private static By PD_GENDER_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.gender']");
    private static By PD_RACE_DROPDOWN = By.xpath("//select[@id='patientRegistration.race']");
    private static By PD_RACE_DROPDOWN_TEXT = By
            .xpath("//select[@id='patientRegistration.race']/option");
    private static By PD_NATION_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.nationality']");
    private static By PD_UNIT_EMPLOYER_FIELD = By
            .xpath("//input[@id='patientRegistration.unitOrEmployer']");
    private static By PD_PATIENT_CATEGORY_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.patientCategory']");
    private static By PD_VIP_TYPE_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.vipType']");
    private static By PD_VISIT_TYPE_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.initVisitInd']");
    private static By PD_TRAUMA_REG_FIELD = By
            .xpath("//input[@id='patientRegistration.registrationNum']");
    private static By PD_SENSITIVE_RECORD_CHECKBOX = By
            .xpath("//input[@id='patientRegistration.sensitiveInd1']");
    private static By pdBranchDropdownBy = By.id("patientRegistration.branch");
    private static By pdRankDropdownBy = By.id("patientRegistration.rank"); // validated
    private static By optionOfRankDropdown = By.xpath("//*[@id=\"patientRegistration.rank\"]/option");
    private static By sponsorSsnBy = By.id("patientRegistration.sponsorSsn");


    public Demographics() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.lastName = "";
            this.firstName = "";
            this.ssn = "";
            this.fmp = "";
            this.sponsorSsn = "";
            this.dob = "";
            this.gender = "";
            this.race = "";
            this.nation = "";
            this.branch = "";
            this.rank = "";
            this.unitEmployer = "";
            this.patientCategory = "";
            this.vipType = "";
            this.visitType = "";
            this.traumaRegisterNumber = "";
            this.sensitiveRecord = null;
        }
        if (isDemoTier) {
            PD_PATIENT_CATEGORY_DROPDOWN = By.id("patientRegistration.patientCategory");
        }
    }

    // By the time we get here, there should be a patient with a patientRegistration object.  But that
    // patientRegistration object may not have a demographics object (I think).  So, create it if necessary,
    // and if created, set its random to the patientRegistration's random
    public boolean process(Patient patient) {
        // I guess we're now requiring the use of the PatientSearch object
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }
        else {
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics ...");
        }
        Demographics demographics = null;
        //Demographics demographics = patient.patientRegistration.newPatientReg.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        if (patient.patientState == PatientState.NEW && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.demographics != null) {
            demographics = patient.patientRegistration.newPatientReg.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        }
        if (patient.patientState == PatientState.UPDATE && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.demographics != null) {
            demographics = patient.patientRegistration.updatePatient.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        }
        // what else here?  patient info?  preregistration?

//        try {
//            WebElement lastNameFieldElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.id("patientRegistration.lastName")));
//        }
//        catch (Exception e) {
//            System.out.println("e: " + e.getMessage());
//        }

        demographics.gender = Utilities.processDropdown(PD_GENDER_DROPDOWN, demographics.gender, demographics.random, true);

        // moved sponsorSsn from below to top because it can be overwritten and don't want it to come right after fmp
        demographics.sponsorSsn = Utilities.processText(sponsorSsnBy, demographics.sponsorSsn, Utilities.TextFieldType.SSN, demographics.random, true); // sometimes erased



        demographics.lastName = Utilities.processText(PD_LAST_NAME_FIELD, demographics.lastName, Utilities.TextFieldType.LAST_NAME, demographics.random, true);
        if (demographics.gender != null && demographics.gender.equalsIgnoreCase("Male")) {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_MALE, demographics.random, true);
        }
        else {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_FEMALE, demographics.random, true);
        }
        demographics.ssn = Utilities.processText(PD_SSN_FIELD, demographics.ssn, Utilities.TextFieldType.SSN, demographics.random, true);

        // Fill in PatientSearch if it was empty or had nulls.
        if (patient.patientSearch == null) {
            patient.patientSearch = new PatientSearch();
        }
        if (patient.patientSearch.firstName == null || patient.patientSearch.firstName.isEmpty() || patient.patientSearch.firstName.equalsIgnoreCase("random")) {
            patient.patientSearch.firstName = demographics.firstName;
        }
        if (patient.patientSearch.lastName == null || patient.patientSearch.lastName.isEmpty() || patient.patientSearch.lastName.equalsIgnoreCase("random")) {
            patient.patientSearch.lastName = demographics.lastName;
        }
        if (patient.patientSearch.ssn == null || patient.patientSearch.ssn.isEmpty() || patient.patientSearch.ssn.equalsIgnoreCase("random")) {
            patient.patientSearch.ssn = demographics.ssn;
        }
        if (patient.patientSearch.traumaRegisterNumber == null || patient.patientSearch.traumaRegisterNumber.isEmpty() || patient.patientSearch.traumaRegisterNumber.equalsIgnoreCase("random")) {
            patient.patientSearch.traumaRegisterNumber = demographics.traumaRegisterNumber;
        }

        // Selecting an FMP dropdown option causes a JavaScript fmpCheck(...) method call that does an AJAX POST/request
        // from /tmds/patientRegistration/fmpCheck.html, with the purpose of "to obtain a Sponsor SSN",
        // which can take a while, but more importantly, when it is done it usually erases SponsorSsn!!!!!
        // It can also SET SponsorSSN, if for example you chose #20 "self", or "emergency".  So we have to wait a bit after selection.
        // Also I suppose an error might be detected if #20 is selected and you put in a different Ssn!
        // You can't do the AJAX text for this one, because there's no AJAX on the page, it says, even though that
        // JS method call does a "new Ajax.Request(...)"

        // Most of the time we'll want FMP to be 20 "Sponsor", so if we're going to do a random, let's weight it.  95% of the time.
        // FMP is required, so we don't care about section-random.  If a non-null, non-blank, non-random value was provided, use it.
        // Otherwise set it to 20 95% of the time.
        //if (demographics.random && (demographics.fmp == null || demographics.fmp.isEmpty() || demographics.fmp.equalsIgnoreCase("random"))) {
        if (demographics.fmp == null || demographics.fmp.isEmpty() || demographics.fmp.equalsIgnoreCase("random")) {
            if (Utilities.random.nextInt(100) < 95) {
                demographics.fmp = "20 - Sponsor";
            }
        }
        demographics.fmp = Utilities.processDropdown(PD_FMP_DROPDOWN, demographics.fmp, demographics.random, true);
// on previous line if fmp comes in as "" or "random" or null, then 90% of the time we should make is #20
        demographics.dob = Utilities.processText(PD_DOB_FIELD, demographics.dob, Utilities.TextFieldType.DOB, demographics.random, true);
        demographics.race = Utilities.processDropdown(PD_RACE_DROPDOWN, demographics.race, demographics.random, true);
        demographics.nation = Utilities.processDropdown(PD_NATION_DROPDOWN, demographics.nation, demographics.random, true);


        // Rank totally sucks.  Why?  Because the dropdown is slow to populate.  You can't choose a rank until you choose a branch.
        // Branch is slow to populate rank.  How can you tell when it's done populating?
        // This next stuff to do branch and rank is really weird if you are not doing random.  Review to see if can streamline for when have actual values.
        if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Here comes creation of expected conditions, one is rank dropdown is there, which it should always be, and the other is number of elements to be more than 1");
        ExpectedCondition<WebElement> rankDropdownIsVisible = ExpectedConditions.visibilityOfElementLocated(pdRankDropdownBy);
        ExpectedCondition<List<WebElement>> rankDropdownOptionsMoreThanOne = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfRankDropdown, 1);

        int nOptions = 0;
        int loopCtr = 0;
        do {
            if (++loopCtr > 10) {
                break;
            }
            try {
                // doing this next line triggers a JS method call "getRank(...)" which causes the
                // Rank dropdown to get new options.  //*[@id="patientRegistration.rank"]
                // The number of options could change.  Before selecting an option on Branch, the
                // Rank dropdown options is only one: "N/A".  But after a selection, the first one is always "Select Rank",
                // followed by one or more others.  So, can't we just wait until children is greater than 1?
                if (Arguments.debug) System.out.println("Demographics.process(), top of loop to do a dropdown for branch");
                demographics.branch = Utilities.processDropdown(pdBranchDropdownBy, demographics.branch, demographics.random, true);
                if (Arguments.debug) System.out.println("Demographics.process(), should now have a branch selected from the dropdown.  I guess it's " + demographics.branch);
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("Prob don't need a try/catch around a processDropdown.");
            }
            try {
//                if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Now waiting for rank dropdown to be visible and options be more than one.  Wait up to 15.  Wonder if I should ");
//                (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(rankDropdownIsVisible, rankDropdownOptionsMoreThanOne));

                if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Now waiting for rank dropdown to be visible, which should take no time.");
                (new WebDriverWait(driver, 15)).until(ExpectedConditions.refreshed(rankDropdownIsVisible));
                if (Arguments.debug) System.out.println("Demographics.process(), successfully waited for a refreshed rank dropdown to be Vislble");
                (new WebDriverWait(driver, 15)).until(rankDropdownIsVisible);
                if (Arguments.debug) System.out.println("Demographics.process(), successfully waited until the rank dropdown is visible.");
                (new WebDriverWait(driver, 15)).until(rankDropdownOptionsMoreThanOne);
                if (Arguments.debug) System.out.println("Demographics.process(), successfully waited until rank dropdown has more than one option.");




                if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Now waitting for rank dropdown to be clickable");
                WebElement rankDropdown = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(pdRankDropdownBy)));
                Select rankSelect = new Select(rankDropdown);
                nOptions = rankSelect.getOptions().size();
                if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Rank dropdown has this many options: " + nOptions);
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("Demographics.process(), Could not get rank dropdown, or select from it or get size.");
                continue;
            }
        } while (nOptions < 2);
        if (nOptions < 2) {
            if (Arguments.debug) System.out.println("Rank dropdown had this many options: " + nOptions + " and so this looks like failure.");
            return false;
        }
        if (Arguments.debug) System.out.println("Demographics.process(), Just got a branch: " + demographics.branch + " and now will do rank.");
        demographics.rank = Utilities.processDropdown(pdRankDropdownBy, demographics.rank, demographics.random, true); // off by one?
        if (Arguments.debug) System.out.println("Demographics.process(), rank sucks.  Finally got a rank: " + demographics.rank);

        // This next part is wrong when you don't have a sponsor ssn value, but FMP 20 causes self ssn to go in.
        // don't overwrite sponsor ssn if it's filled in because of FMP.  So review this section and fix if necessary
//        By sponsorSsnBy = By.id("patientRegistration.sponsorSsn");
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(sponsorSsnBy)));
        }
        catch (Exception e) {
            System.out.println("Didn't get a refresh of the sponsorSsn");
            return false;
        }
        // this next line could be a problem now that we're checking for existing values, because FMP 20 causes a value to go in.
        //demographics.sponsorSsn = Utilities.processText(sponsorSsnBy, demographics.sponsorSsn, Utilities.TextFieldType.SSN, demographics.random, true); // sometimes erased
        // moved the above line to near top of this section

        demographics.unitEmployer = Utilities.processText(PD_UNIT_EMPLOYER_FIELD, demographics.unitEmployer, Utilities.TextFieldType.UNIT_EMPLOYER, demographics.random, false);
        demographics.patientCategory = Utilities.processDropdown(PD_PATIENT_CATEGORY_DROPDOWN, demographics.patientCategory, demographics.random, true);
        demographics.vipType = Utilities.processDropdown(PD_VIP_TYPE_DROPDOWN, demographics.vipType, demographics.random, false);
        demographics.visitType = Utilities.processDropdown(PD_VISIT_TYPE_DROPDOWN, demographics.visitType, demographics.random, false);
        demographics.traumaRegisterNumber = Utilities.processStringOfDigits(PD_TRAUMA_REG_FIELD, demographics.traumaRegisterNumber, 3, 6, demographics.random, false);

        // What about "Sensitive Record" check box???  Not required
        demographics.sensitiveRecord = Utilities.processBoolean(PD_SENSITIVE_RECORD_CHECKBOX, demographics.sensitiveRecord, demographics.random, false);


        // NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW
        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        patient.patientSearch.ssn = demographics.ssn;
        patient.patientSearch.firstName = demographics.firstName;
        patient.patientSearch.lastName = demographics.lastName;
        patient.patientSearch.traumaRegisterNumber = demographics.traumaRegisterNumber;



        return true;
    }
}
