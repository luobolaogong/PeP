package pep.patient.registration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;


import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static pep.utilities.Arguments.codeBranch;

/**
 * This class handles the Demographics section of different registration pages, although it's
 * a bit risky to assume that the locators/selectors are the same.
 */
public class Demographics { // shouldn't it be "Demographic"?  One patient == one demographic?
    private static Logger logger = Logger.getLogger(Demographics.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
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

    private static final By PD_LAST_NAME_FIELD = By.id("patientRegistration.lastName");
    private static final By PD_FIRST_NAME_FIELD = By.id("patientRegistration.firstName");
    private static final By PD_SSN_FIELD = By.id("patientRegistration.ssn");
    private static final By PD_FMP_DROPDOWN = By.id("patientRegistration.sponsorFmp");
    private static final By PD_DOB_FIELD = By.id("formatDob");
    private static final By PD_GENDER_DROPDOWN = By.id("patientRegistration.gender");
    private static final By PD_RACE_DROPDOWN = By.id("patientRegistration.race");
    private static final By PD_NATION_DROPDOWN = By.id("patientRegistration.nationality");
    private static final By PD_UNIT_EMPLOYER_FIELD = By.id("patientRegistration.unitOrEmployer");
    private static final By PD_VIP_TYPE_DROPDOWN = By.id("patientRegistration.vipType");
    private static final By PD_VISIT_TYPE_DROPDOWN = By.id("patientRegistration.initVisitInd");
    private static final By PD_TRAUMA_REG_FIELD = By.id("patientRegistration.registrationNum");
    private static final By PD_SENSITIVE_RECORD_CHECKBOX = By.id("patientRegistration.sensitiveInd1");
    private static final By pdBranchDropdownBy = By.id("patientRegistration.branch");
    private static final By pdRankDropdownBy = By.id("patientRegistration.rank"); // validated
    private static final By sponsorSsnBy = By.id("patientRegistration.sponsorSsn");
    private static By PD_PATIENT_CATEGORY_DROPDOWN = By.id("patientRegistration.patientCategory"); // 12/12/18

    public Demographics() {
        if (Arguments.template) {
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
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            PD_PATIENT_CATEGORY_DROPDOWN = By.id("patientRegistration.patientCategory");
        }
    }
    /**
     * Process the Demographics section of a registration page.
     * @param patient The patient for this demographics info
     * @return Success or Failure at filling in the fields.  Currently always returning true
     */
    public boolean process(Patient patient) {
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("    Processing Demographics at " + LocalTime.now() + " for patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Demographics at " + LocalTime.now() + " ...");
        }
        //
        // Copy from parent
        //
        Demographics demographics = null;
        if (patient.patientState == PatientState.PRE && patient.registration.preRegistration != null && patient.registration.preRegistration.demographics != null) {
            demographics = patient.registration.preRegistration.demographics;
        }
        else if (patient.patientState == PatientState.NEW && patient.registration.newPatientReg != null && patient.registration.newPatientReg.demographics != null) {
            demographics = patient.registration.newPatientReg.demographics;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.registration.updatePatient != null && patient.registration.updatePatient.demographics != null) {
            demographics = patient.registration.updatePatient.demographics;
        }
        // What good is the following?  Hitting the branch stuff too soon?
        //
        // Test the first field to see if it's available.  Do we have "Sensitive Information" page here?  YES WE CAN BE SITTING AT A Sensitive Information PAGE RIGHT NOW!
        // !!!!!!!! In Update Patient (not New Patient Reg) we can fail because of a Sensitive Information alert.  Verified 5/8/19
        // !!!!!! Before checking for fields on the expected page, should probably see if we dismissed the Sensitive Information page?
        //
        try { // what?  Sensitive Information page again? // next sleep does NOT help, even if 12555
            Utilities.sleep(555, "Demographics.process(), when doing Update Patient, waiting for Demographics last name field often fails.  Trying a sleep to fix that.  Prob wrong fix.");
            Utilities.waitForRefreshedVisibility(PD_LAST_NAME_FIELD,  15, "Demographics.(), last name field"); // added 11/20/18, was 10
        }
        catch (TimeoutException e) {
            logger.info("Timed out waiting for visibility of element " + PD_LAST_NAME_FIELD);
            System.out.println("Prob should just return false now, because probably got a Sensitive Information page.");
            return false;
        }
        catch (Exception e) { // fails:2
            logger.info("Some kind of exception waiting for visibility of element " + PD_LAST_NAME_FIELD);
            System.out.println("Prob  got a Sensitive Information page.");
            return false;
        }

        //
        // Fill in fields.
        //
        // Moved branch from below to here since a change to the value will cause a reset of rank and patient category dropdown options, and we want
        // to give those fields more time and eliminate the need for all that special looping and testing.
        demographics.branch = Utilities.processDropdown(pdBranchDropdownBy, demographics.branch, demographics.randomizeSection, true);
        Utilities.sleep(2555, "Demographics.process(), when doing Update Patient, waiting for Demographics last name field often fails.  Trying a sleep to fix that.");
        demographics.lastName = Utilities.processText(PD_LAST_NAME_FIELD, demographics.lastName, Utilities.TextFieldType.LAST_NAME, demographics.randomizeSection, true);
        try {
            Utilities.waitForVisibility(PD_GENDER_DROPDOWN, 5, "Demographics.process()"); // new 11/20/18
        }
        catch (Exception e) {
            logger.severe("Demographics.process(), failed to see gender dropdown. Continuing.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        demographics.gender = Utilities.processDropdown(PD_GENDER_DROPDOWN, demographics.gender, demographics.randomizeSection, true);
        if (demographics.gender != null && demographics.gender.equalsIgnoreCase("Male")) {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_MALE, demographics.randomizeSection, true);
        }
        else {
            demographics.firstName = Utilities.processText(PD_FIRST_NAME_FIELD, demographics.firstName, Utilities.TextFieldType.FIRST_NAME_FEMALE, demographics.randomizeSection, true);
        }
        demographics.ssn = Utilities.processText(PD_SSN_FIELD, demographics.ssn, Utilities.TextFieldType.SSN, demographics.randomizeSection, true);

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
        if (demographics.fmp == null || demographics.fmp.isEmpty() || demographics.fmp.equalsIgnoreCase("random")) {
            if (Utilities.random.nextInt(100) < 95) {
                demographics.fmp = "20 - Sponsor";
            }
        }
        demographics.fmp = Utilities.processDropdown(PD_FMP_DROPDOWN, demographics.fmp, demographics.randomizeSection, true);
        demographics.dob = Utilities.processText(PD_DOB_FIELD, demographics.dob, Utilities.TextFieldType.DOB, demographics.randomizeSection, true);
        demographics.race = Utilities.processDropdown(PD_RACE_DROPDOWN, demographics.race, demographics.randomizeSection, true);
        demographics.nation = Utilities.processDropdown(PD_NATION_DROPDOWN, demographics.nation, demographics.randomizeSection, true);
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(presenceOfElementLocated(sponsorSsnBy)));
        }
        catch (Exception e) {
            logger.fine("Didn't get a refresh of the sponsorSsn");
        }
        Utilities.sleep(555, "Demographics.process(), about to process unit employer text.  Seems to get stale ref if don't sleep.");
        demographics.unitEmployer = Utilities.processText(PD_UNIT_EMPLOYER_FIELD, demographics.unitEmployer, Utilities.TextFieldType.UNIT_EMPLOYER, demographics.randomizeSection, false);
        demographics.vipType = Utilities.processDropdown(PD_VIP_TYPE_DROPDOWN, demographics.vipType, demographics.randomizeSection, false);
        demographics.visitType = Utilities.processDropdown(PD_VISIT_TYPE_DROPDOWN, demographics.visitType, demographics.randomizeSection, false);
        demographics.traumaRegisterNumber = Utilities.processStringOfDigits(PD_TRAUMA_REG_FIELD, demographics.traumaRegisterNumber, 3, 6, demographics.randomizeSection, false);
        try {
            demographics.sensitiveRecord = Utilities.processBoolean(PD_SENSITIVE_RECORD_CHECKBOX, demographics.sensitiveRecord, demographics.randomizeSection, false);
        } catch (Exception e) {
            logger.severe("Demographics.process(), couldn't do sensitiveRecord. e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        try {
            demographics.rank = Utilities.processDropdown(pdRankDropdownBy, demographics.rank, demographics.randomizeSection, true);
        } catch (Exception e) {
            logger.severe("Demographics.process(), couldn't process rank. e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        demographics.patientCategory = Utilities.processDropdown(PD_PATIENT_CATEGORY_DROPDOWN, demographics.patientCategory, demographics.randomizeSection, true);
        demographics.sponsorSsn = Utilities.processText(sponsorSsnBy, demographics.sponsorSsn, Utilities.TextFieldType.SSN, demographics.randomizeSection, true);
        if (demographics.sponsorSsn == null || demographics.sponsorSsn.isEmpty()) {
            logger.fine("Hack: setting sponsorssn to ssn!!!!!!!!!!!!!!!!!!!!!!!!!1");
            demographics.sponsorSsn = demographics.ssn;
        }
        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        patient.patientSearch.ssn = demographics.ssn;
        patient.patientSearch.firstName = demographics.firstName; // don't do this if it's just a case difference
        patient.patientSearch.lastName = demographics.lastName;
        patient.patientSearch.traumaRegisterNumber = demographics.traumaRegisterNumber;
        //
        // Do screenshot if desired, and pause too.
        //
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Demographics");
        }
        return true; // fix above stuff to return false if bad error.
    }
}
