package pep.patient.registration;

// It's dangerous to use this class for both New Patient Reg., and Update Patient (or any other page)
// because the selectors can be different, for sure.
// Probably same true for flight and other sections of those pages.
// Dumb to assume the developers used the exact same code.

import org.apache.xpath.Arg;
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
    public String lastName;
    public String firstName;
    public String ssn;
    public String fmp;
    public String sponsorSsn;
    public String dob;
    public String gender;
    public String race;
    public String nation;
    public String branch;;
    public String rank;
    public String unitEmployer;
    public String patientCategory;
    public String vipType;
    public String visitType;
    public String traumaRegisterNumber;
    public Boolean sensitiveRecord;

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
            this.sensitiveRecord = false;
        }
        if (isDemoTier) {
            PD_PATIENT_CATEGORY_DROPDOWN = By.id("patientRegistration.patientCategory");
        }
    }

    public boolean process(Patient patient) {
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }
        else {
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics ...");
        }
        Demographics demographics = null;
        if (patient.patientState == PatientState.PRE && patient.patientRegistration.preRegistration != null && patient.patientRegistration.preRegistration.demographics != null) {
            demographics = patient.patientRegistration.preRegistration.demographics;
        }
        else if (patient.patientState == PatientState.NEW && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.demographics != null) {
            demographics = patient.patientRegistration.newPatientReg.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        }
        else if (patient.patientState == PatientState.UPDATE && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.demographics != null) {
            demographics = patient.patientRegistration.updatePatient.demographics; // must exist, right?    Why NewPatient?  UpdatePatient?
        }
        // what else here?  patient info?  preregistration?

        demographics.gender = Utilities.processDropdown(PD_GENDER_DROPDOWN, demographics.gender, demographics.random, true);


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
        // Most of the time (I was told "99%") we'll want FMP to be 20 "Sponsor", so if we're going to do a random, we weight it.
        // FMP is required, so we don't care about section-random.  If a non-null, non-blank, non-random value was provided, use it.
        //if (demographics.random && (demographics.fmp == null || demographics.fmp.isEmpty() || demographics.fmp.equalsIgnoreCase("random"))) {
        if (demographics.fmp == null || demographics.fmp.isEmpty() || demographics.fmp.equalsIgnoreCase("random")) {
            if (Utilities.random.nextInt(100) < 95) {
                demographics.fmp = "20 - Sponsor";
            }
        }
        demographics.fmp = Utilities.processDropdown(PD_FMP_DROPDOWN, demographics.fmp, demographics.random, true);
        // For DOB, TMDS requires format MM/DD/YYYY, and you need leading 0's if MM or DD is less than 10.  So to help out users, we should add the 0's
        demographics.dob = Utilities.processText(PD_DOB_FIELD, demographics.dob, Utilities.TextFieldType.DOB, demographics.random, true);
        demographics.race = Utilities.processDropdown(PD_RACE_DROPDOWN, demographics.race, demographics.random, true);
        demographics.nation = Utilities.processDropdown(PD_NATION_DROPDOWN, demographics.nation, demographics.random, true);


        // this probably doesn't help because a refresh is done in processText, probably.  The problem is the fmp setting:
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(sponsorSsnBy)));
        }
        catch (Exception e) {
            System.out.println("Didn't get a refresh of the sponsorSsn");
            //return false;
        }

        // Branch is slow to populate rank.
        ExpectedCondition<WebElement> rankDropdownIsVisible = ExpectedConditions.visibilityOfElementLocated(pdRankDropdownBy);
        ExpectedCondition<List<WebElement>> rankDropdownOptionsMoreThanOne = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfRankDropdown, 1);

        int nOptions = 0;
        int loopCtr = 0;
        do {
            if (++loopCtr > 10) {
                break;
            }
            try {
                demographics.branch = Utilities.processDropdown(pdBranchDropdownBy, demographics.branch, demographics.random, true);
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("Prob don't need a try/catch around a processDropdown.");
            }
            try {
                (new WebDriverWait(driver, 15)).until(ExpectedConditions.refreshed(rankDropdownIsVisible));
                (new WebDriverWait(driver, 15)).until(rankDropdownIsVisible);
                (new WebDriverWait(driver, 15)).until(rankDropdownOptionsMoreThanOne);
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
        demographics.rank = Utilities.processDropdown(pdRankDropdownBy, demographics.rank, demographics.random, true); // off by one?
        demographics.unitEmployer = Utilities.processText(PD_UNIT_EMPLOYER_FIELD, demographics.unitEmployer, Utilities.TextFieldType.UNIT_EMPLOYER, demographics.random, false);
        demographics.patientCategory = Utilities.processDropdown(PD_PATIENT_CATEGORY_DROPDOWN, demographics.patientCategory, demographics.random, true);
        demographics.vipType = Utilities.processDropdown(PD_VIP_TYPE_DROPDOWN, demographics.vipType, demographics.random, false);
        demographics.visitType = Utilities.processDropdown(PD_VISIT_TYPE_DROPDOWN, demographics.visitType, demographics.random, false);
        demographics.traumaRegisterNumber = Utilities.processStringOfDigits(PD_TRAUMA_REG_FIELD, demographics.traumaRegisterNumber, 3, 6, demographics.random, false);
        // What about "Sensitive Record" check box???  Not required
        demographics.sensitiveRecord = Utilities.processBoolean(PD_SENSITIVE_RECORD_CHECKBOX, demographics.sensitiveRecord, demographics.random, false);

        demographics.sponsorSsn = Utilities.processText(sponsorSsnBy, demographics.sponsorSsn, Utilities.TextFieldType.SSN, demographics.random, true); // sometimes erased
        // Here comes a hack because above processText isn't working right, I think:
        if (demographics.sponsorSsn == null || demographics.sponsorSsn.isEmpty()) {
            if (Arguments.debug) System.out.println("Hack: setting sponsorssn to ssn!!!!!!!!!!!!!!!!!!!!!!!!!1");
            demographics.sponsorSsn = demographics.ssn;
        }

        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        patient.patientSearch.ssn = demographics.ssn;
        patient.patientSearch.firstName = demographics.firstName; // don't do this if it's just a case difference
        patient.patientSearch.lastName = demographics.lastName;
        patient.patientSearch.traumaRegisterNumber = demographics.traumaRegisterNumber;

        if (Arguments.sectionPause > 0) {
            Utilities.sleep(Arguments.sectionPause * 1000);
        }

        return true;
    }
}
