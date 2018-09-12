package pep.patient.registration;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.List;

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

    private static final By PD_LAST_NAME_FIELD = By
            .xpath("//input[@id='patientRegistration.lastName']");
    private static final By PD_FIRST_NAME_FIELD = By
            .xpath("//input[@id='patientRegistration.firstName']");
    private static final By PD_SSN_FIELD = By.xpath("//input[@id='patientRegistration.ssn']");
    private static final By PD_FMP_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.sponsorFmp']");
    private static final By PD_DOB_FIELD = By.xpath("//input[@id='formatDob']");
    private static final By PD_AGE_FIELD = By.xpath("//input[@id='patientRegistration.age']");
    private static final By PD_GENDER_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.gender']");
    private static final By PD_RACE_DROPDOWN = By.xpath("//select[@id='patientRegistration.race']");
    private static final By PD_RACE_DROPDOWN_TEXT = By
            .xpath("//select[@id='patientRegistration.race']/option");
    private static final By PD_NATION_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.nationality']");
    private static final By PD_UNIT_EMPLOYER_FIELD = By
            .xpath("//input[@id='patientRegistration.unitOrEmployer']");
    private static final By PD_PATIENT_CATEGORY_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.patientCategory']");
    private static final By PD_VIP_TYPE_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.vipType']");
    private static final By PD_VISIT_TYPE_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.initVisitInd']");
    private static final By PD_TRAUMA_REG_FIELD = By
            .xpath("//input[@id='patientRegistration.registrationNum']");
    private static final By PD_SENSITIVE_RECORD_CHECKBOX = By
            .xpath("//input[@id='patientRegistration.sensitiveInd1']");
    private static final By pdBranchDropdownBy = By.id("patientRegistration.branch");
    private static final By pdRankDropdownBy = By.id("patientRegistration.rank");
    private static final By optionOfRankDropdown = By.xpath("//*[@id=\"patientRegistration.rank\"]/option");
    private static final By sponsorSsnBy = By.id("patientRegistration.sponsorSsn");


    public Demographics() {
        if (Arguments.template) {
            this.random = null;
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
            this.sensitiveRecord = false;
        }
    }

    // By the time we get here, there should be a patient with a patientRegistration object.  But that
    // patientRegistration object may not have a demographics object (I think).  So, create it if necessary,
    // and if created, set its random to the patientRegistration's random
    public boolean process(Patient patient) {
       // if (!Arguments.quiet) System.out.println("    Processing Demographics ...");
        if (patient.patientRegistration == null
                || patient.patientRegistration.newPatientReg.demographics == null
                || patient.patientRegistration.newPatientReg.demographics.firstName == null
                || patient.patientRegistration.newPatientReg.demographics.firstName.isEmpty()
                || patient.patientRegistration.newPatientReg.demographics.firstName.equalsIgnoreCase("random")
                || patient.patientRegistration.newPatientReg.demographics.lastName == null
                || patient.patientRegistration.newPatientReg.demographics.lastName.isEmpty()
                || patient.patientRegistration.newPatientReg.demographics.lastName.equalsIgnoreCase("random")
                ) {
            if (!Arguments.quiet) System.out.println("    Processing Demographics ...");
        }
        else {
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");
        }
        Demographics demographics = patient.patientRegistration.newPatientReg.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        demographics.gender = Utilities.processDropdown(PD_GENDER_DROPDOWN, demographics.gender, demographics.random, true);
        demographics.lastName = Utilities.processText(PD_LAST_NAME_FIELD, demographics.lastName, Utilities.TextFieldType.LAST_NAME, demographics.random, true);
        if (demographics.gender != null && demographics.gender.equalsIgnoreCase("Male")) {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_MALE, demographics.random, true);
        }
        else {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_FEMALE, demographics.random, true);
        }
        demographics.ssn = Utilities.processText(PD_SSN_FIELD, demographics.ssn, Utilities.TextFieldType.SSN, demographics.random, true);

        // Fill in PatientSearch if it was empty or had nulls
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
        demographics.fmp = Utilities.processDropdown(PD_FMP_DROPDOWN, demographics.fmp, demographics.random, true);

        demographics.dob = Utilities.processText(PD_DOB_FIELD, demographics.dob, Utilities.TextFieldType.DOB, demographics.random, true);
        demographics.race = Utilities.processDropdown(PD_RACE_DROPDOWN, demographics.race, demographics.random, true);
        demographics.nation = Utilities.processDropdown(PD_NATION_DROPDOWN, demographics.nation, demographics.random, true);



        // This next stuff to do branch and rank is really weird if you are not doing random.  Review to see if can streamline for when have actual values.
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
                demographics.branch = Utilities.processDropdown(pdBranchDropdownBy, demographics.branch, demographics.random, true);

                (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(rankDropdownIsVisible, rankDropdownOptionsMoreThanOne));

                WebElement rankDropdown = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(pdRankDropdownBy)));
                Select rankSelect = new Select(rankDropdown);
                nOptions = rankSelect.getOptions().size();
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
        demographics.sponsorSsn = Utilities.processText(sponsorSsnBy, demographics.sponsorSsn, Utilities.TextFieldType.SSN, demographics.random, true); // sometimes erased

        demographics.unitEmployer = Utilities.processText(PD_UNIT_EMPLOYER_FIELD, demographics.unitEmployer, Utilities.TextFieldType.UNIT_EMPLOYER, demographics.random, false);
        demographics.patientCategory = Utilities.processDropdown(PD_PATIENT_CATEGORY_DROPDOWN, demographics.patientCategory, demographics.random, true);
        demographics.vipType = Utilities.processDropdown(PD_VIP_TYPE_DROPDOWN, demographics.vipType, demographics.random, false);
        demographics.visitType = Utilities.processDropdown(PD_VISIT_TYPE_DROPDOWN, demographics.visitType, demographics.random, false);
        demographics.traumaRegisterNumber = Utilities.processNumber(PD_TRAUMA_REG_FIELD, demographics.traumaRegisterNumber, 3, 6, demographics.random, false);

        // What about "Sensitive Record" check box???  Not required
        demographics.sensitiveRecord = Utilities.processBoolean(PD_SENSITIVE_RECORD_CHECKBOX, demographics.sensitiveRecord, demographics.random, false);
        return true;
    }
}
