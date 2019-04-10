package pep.patient.registration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * This class handles extra patient information that is not in Demographics.  Most fields on the page are optional.
 */
public class SelectedPatientInformation {
    private static Logger logger = Logger.getLogger(SelectedPatientInformation.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String arrivalDate;
    public String injuryDate;
    public String lastName;
    public String firstName;
    public String ssn;
    public String fmp;
    public String sponsorSsn;
    public String dob;
    public String gender;
    public String race;
    public String nation;
    public String maritalStatus;
    public String religiousPreference;
    public String branch;
    public String rank;
    public String patientCategory;
    public String yearsOfService;
    public String operation;
    public Boolean mobOrder;
    public Boolean deploymentOrder;

    private static By arrivalDateBy = By.id("arrivalDate");
    private static By injuryDateBy = By.id("dateOfInjury");
    private static By lastNameBy = By.id("patientInfoBean.patientLastName");
    private static By firstNameBy = By.id("patientFirstName");
    private static By ssnBy = By.id("displaySsn");
    private static By fmpBy = By.id("sponsorFmp");
    private static By sponsorSsnBy = By.id("displaySponsorSsn");
    private static By dobBy = By.id("dob");
    private static By genderBy = By.id("patientInfoBean.gender");
    private static By raceBy = By.id("raceId");
    private static By nationBy = By.id("nationId");
    private static By maritalStatusBy = By.id("maritalStatusId");
    private static By religiousPreferenceBy = By.id("religionId");
    private static By branchBy = By.id("branchOfService");
    private static By rankBy = By.id("patientInfoBean.rankId");
    private static By patientCategoryBy = By.id("patientInfoBean.patientCategory");
    private static By yearsOfServiceBy = By.id("yearsOfService");
    private static By operationBy = By.id("patientInfoBean.areaOfOperationId");
    private static By mobOrderBy = By.id("patientInfoBean.mobOrder1");
    private static By deploymentOrderBy = By.id("patientInfoBean.deploymentOrder1");

    public SelectedPatientInformation() {
        if (Arguments.template) {
            this.arrivalDate = "";
            this.injuryDate = "";
            this.lastName = "";
            this.firstName = "";
            this.ssn = "";
            this.fmp = "";
            this.sponsorSsn = "";
            this.dob = "";
            this.gender = "";
            this.race = "";
            this.nation = "";
            this.maritalStatus = "";
            this.religiousPreference = "";
            this.branch = "";
            this.rank = "";
            this.patientCategory = "";
            this.yearsOfService = "";
            this.operation = "";
            this.mobOrder = false;
            this.deploymentOrder = false;
        }
    }

    /**
     * Process this Selected Patient Information page.
     * @param patient The patient in question
     * @return Success or Failure in saving the page
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet)
            System.out.println("    Processing Selected Patient Information at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        SelectedPatientInformation selectedPatientInformation = patient.registration.patientInformation.selectedPatientInformation;
        // Branch was below, but making a change to Branch causes Rank and Patient Category to get their dropdown options updated.
        // which takes time (2 sec?) and this can cause havoc.
        try {
            // A change of branch will cause a reset of Patient Category which can take a long time. (at least 1 sec?)
            selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.randomizeSection, true);
        }
        catch (Exception e) {
            logger.fine("Prob don't need a try/catch around a processDropdown.");
        }
        selectedPatientInformation.arrivalDate = Utilities.processDate(arrivalDateBy, selectedPatientInformation.arrivalDate, selectedPatientInformation.randomizeSection, false);
        selectedPatientInformation.injuryDate = Utilities.processDate(injuryDateBy, selectedPatientInformation.injuryDate, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.gender = Utilities.processDropdown(genderBy, selectedPatientInformation.gender, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.lastName = Utilities.processText(lastNameBy, selectedPatientInformation.lastName, Utilities.TextFieldType.LAST_NAME, selectedPatientInformation.randomizeSection, true);
        if (selectedPatientInformation.gender != null && selectedPatientInformation.gender.equalsIgnoreCase("Male")) {
            selectedPatientInformation.firstName = Utilities.processText(firstNameBy, selectedPatientInformation.firstName, Utilities.TextFieldType.FIRST_NAME_MALE, selectedPatientInformation.randomizeSection, true);
        }
        else {
            selectedPatientInformation.firstName = Utilities.processText(firstNameBy, selectedPatientInformation.firstName, Utilities.TextFieldType.FIRST_NAME_FEMALE, selectedPatientInformation.randomizeSection, true);
        }
        selectedPatientInformation.ssn = Utilities.processText(ssnBy, selectedPatientInformation.ssn, Utilities.TextFieldType.SSN, selectedPatientInformation.randomizeSection, true);

        if (patient.patientSearch == null) {
            patient.patientSearch = new PatientSearch();
        }
        if (patient.patientSearch.firstName == null || patient.patientSearch.firstName.isEmpty() || patient.patientSearch.firstName.equalsIgnoreCase("random")) {
            patient.patientSearch.firstName = selectedPatientInformation.firstName;
        }
        if (patient.patientSearch.lastName == null || patient.patientSearch.lastName.isEmpty() || patient.patientSearch.lastName.equalsIgnoreCase("random")) {
            patient.patientSearch.lastName = selectedPatientInformation.lastName;
        }
        if (patient.patientSearch.ssn == null || patient.patientSearch.ssn.isEmpty() || patient.patientSearch.ssn.equalsIgnoreCase("random")) {
            patient.patientSearch.ssn = selectedPatientInformation.ssn;
        }
        // Setting an FMP value can cause Sponsor SSN to be replaced, for example if it's "20 - Sponsor".  And choosing something else can erase what's already in the SponsorSsn
        selectedPatientInformation.fmp = Utilities.processDropdown(fmpBy, selectedPatientInformation.fmp, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.dob = Utilities.processText(dobBy, selectedPatientInformation.dob, Utilities.TextFieldType.DOB, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.race = Utilities.processDropdown(raceBy, selectedPatientInformation.race, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.nation = Utilities.processDropdown(nationBy, selectedPatientInformation.nation, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.maritalStatus = Utilities.processDropdown(maritalStatusBy, selectedPatientInformation.maritalStatus, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.religiousPreference = Utilities.processDropdown(religiousPreferenceBy, selectedPatientInformation.religiousPreference, selectedPatientInformation.randomizeSection, true);

        if (selectedPatientInformation.yearsOfService == null || selectedPatientInformation.yearsOfService.isEmpty()) {
            By ageBy = By.id("age");
            try {
                WebElement age = Utilities.waitForVisibility(ageBy, 5, "SelectedPatientInformation.(), age");
                String ageString = age.getAttribute("value");
                if (ageString != null && !ageString.isEmpty()) {
                    int ageInt = Integer.parseInt(ageString);
                    selectedPatientInformation.yearsOfService = String.valueOf((ageInt > 16) ? (ageInt - 16)/2 : 0);
                }
            } catch (Exception e) {
                logger.fine("SelectedPatientInformation.process(), couldn't get age.");
            }
        }
        selectedPatientInformation.yearsOfService = Utilities.processIntegerNumber(selectedPatientInformation.yearsOfServiceBy, selectedPatientInformation.yearsOfService, 1,3, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.operation = Utilities.processDropdown(operationBy, selectedPatientInformation.operation, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.mobOrder = Utilities.processBoolean(mobOrderBy, selectedPatientInformation.mobOrder, selectedPatientInformation.randomizeSection,false);
        selectedPatientInformation.deploymentOrder = Utilities.processBoolean(deploymentOrderBy, selectedPatientInformation.deploymentOrder, selectedPatientInformation.randomizeSection,false);
        try {
            Utilities.waitForRefreshedPresence(sponsorSsnBy,  10, "SelectedPatientInformation.(), sponsor ssn");
        }
        catch (Exception e) {
            logger.severe("Didn't get a refresh of the sponsorSsn"); ScreenShot.shoot("SevereError");
            return false;
        }
        selectedPatientInformation.rank = Utilities.processDropdown(rankBy, selectedPatientInformation.rank, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.patientCategory = Utilities.processDropdown(patientCategoryBy, selectedPatientInformation.patientCategory, selectedPatientInformation.randomizeSection, true);
        selectedPatientInformation.sponsorSsn = Utilities.processText(sponsorSsnBy, selectedPatientInformation.sponsorSsn, Utilities.TextFieldType.SSN, selectedPatientInformation.randomizeSection, true);

        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        if (selectedPatientInformation.ssn != null && !selectedPatientInformation.ssn.isEmpty()) {
            patient.patientSearch.ssn = selectedPatientInformation.ssn;
        }
        if (selectedPatientInformation.firstName != null && !selectedPatientInformation.firstName.isEmpty()) {
            if (!patient.patientSearch.firstName.equalsIgnoreCase(selectedPatientInformation.firstName)) {
                patient.patientSearch.firstName = selectedPatientInformation.firstName;
            }
        }
        if (selectedPatientInformation.lastName != null && !selectedPatientInformation.lastName.isEmpty()) {
            if (!patient.patientSearch.lastName.equalsIgnoreCase(selectedPatientInformation.lastName)) {
                patient.patientSearch.lastName = selectedPatientInformation.lastName;
            }
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "SelectedPatientInformation");
        }
        return true;
    }
}
