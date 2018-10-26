package pep.patient.registration;

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

import static javax.swing.text.html.CSS.getAttribute;
import static pep.utilities.Driver.driver;

public class SelectedPatientInformation {
    public Boolean random;
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


    // Selected Patient Information
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
    private static By optionOfRankDropdown = By.xpath("//*[@id=\"patientInfoBean.rankId\"]/option"); // guess

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
            this.mobOrder = null;
            this.deploymentOrder = null;
        }
    }
    
    public boolean process(Patient patient) {
        // I guess we're now requiring the use of the PatientSearch object

        // new 10/25/18
        if (!Arguments.quiet)
            System.out.println("    Processing Selected Patient Information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");


        SelectedPatientInformation selectedPatientInformation = patient.patientRegistration.patientInformation.selectedPatientInformation;
        // the above line is wrong?  That about "this"?
        // test:
//        if (selectedPatientInformation.random == null) {
//            selectedPatientInformation.random = (this.random == null) ? false : this.random; // huh?
//        }

        // Looks like a problem coming up.  The fields in this section are all required, pretty much.
        // If the user doesn't specify a value in the input file, then PeP will write a NEW random value in
        // and replace existing.  We do not want this.
        // This is a general problem throughout.  If a field has a value already because it was written in
        // by a previous page/section, then we should only override it if a new value is specified.
        // At least not ""/blank.  (maybe random)
        //
        // So the logic: Before writing, if a field already has a value, only write a new value if it was
        // specified with a non blank (or non-null) value.  If it says "random" or other value, write it.
        // This is the case whether the field is required or not.
        // Probably the things to consider:
        // Value exists?  Value is specified?  Section is marked random? Field/value is required?
        // The type of the field (dropdown, text, boolean, radio


        // It appears that arrival Date is not writable.  Its value comes from some other record.
        selectedPatientInformation.arrivalDate = Utilities.processDate(arrivalDateBy, selectedPatientInformation.arrivalDate, selectedPatientInformation.random, true); // true for test
        selectedPatientInformation.injuryDate = Utilities.processDate(injuryDateBy, selectedPatientInformation.injuryDate, selectedPatientInformation.random, true);
        selectedPatientInformation.gender = Utilities.processDropdown(genderBy, selectedPatientInformation.gender, selectedPatientInformation.random, true);
        selectedPatientInformation.lastName = Utilities.processText(lastNameBy, selectedPatientInformation.lastName, Utilities.TextFieldType.LAST_NAME, selectedPatientInformation.random, true);
        if (selectedPatientInformation.gender != null && selectedPatientInformation.gender.equalsIgnoreCase("Male")) {
            selectedPatientInformation.firstName = Utilities.processText(firstNameBy, selectedPatientInformation.firstName, Utilities.TextFieldType.FIRST_NAME_MALE, selectedPatientInformation.random, true);
        }
        else {
            selectedPatientInformation.firstName = Utilities.processText(firstNameBy, selectedPatientInformation.firstName, Utilities.TextFieldType.FIRST_NAME_FEMALE, selectedPatientInformation.random, true);
        }
        selectedPatientInformation.ssn = Utilities.processText(ssnBy, selectedPatientInformation.ssn, Utilities.TextFieldType.SSN, selectedPatientInformation.random, true);


        // Fill in PatientSearch if it was empty or had nulls.
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
        // No trauma number available from this section, so won't try to update patientSearch

        // Setting an FMP value can cause Sponsor SSN to be replaced, for example if it's "20 - Sponsor".  And choosing something else can erase what's already in the SponsorSsn
        selectedPatientInformation.fmp = Utilities.processDropdown(fmpBy, selectedPatientInformation.fmp, selectedPatientInformation.random, true);

        selectedPatientInformation.sponsorSsn = Utilities.processText(sponsorSsnBy, selectedPatientInformation.sponsorSsn, Utilities.TextFieldType.SSN, selectedPatientInformation.random, true); // sometimes erased

        selectedPatientInformation.dob = Utilities.processText(dobBy, selectedPatientInformation.dob, Utilities.TextFieldType.DOB, selectedPatientInformation.random, true);
        selectedPatientInformation.race = Utilities.processDropdown(raceBy, selectedPatientInformation.race, selectedPatientInformation.random, true);
        selectedPatientInformation.nation = Utilities.processDropdown(nationBy, selectedPatientInformation.nation, selectedPatientInformation.random, true);
        selectedPatientInformation.maritalStatus = Utilities.processDropdown(maritalStatusBy, selectedPatientInformation.maritalStatus, selectedPatientInformation.random, true);
        selectedPatientInformation.religiousPreference = Utilities.processDropdown(religiousPreferenceBy, selectedPatientInformation.religiousPreference, selectedPatientInformation.random, true);

        // moved from below to before fmp
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(sponsorSsnBy)));
        }
        catch (Exception e) {
            System.out.println("Didn't get a refresh of the sponsorSsn");
            return false;
        }

        // I think I improved this branch/rank stuff in Demographics, so take a look and compare later
        ExpectedCondition<WebElement> rankDropdownIsVisible = ExpectedConditions.visibilityOfElementLocated(rankBy);
        ExpectedCondition<List<WebElement>> rankDropdownOptionsMoreThanOne = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfRankDropdown, 1);

        int nOptions = 0;
        int loopCtr = 0;
        do {
            if (++loopCtr > 10) {
                break;
            }
            try {
                selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.random, true);
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("Prob don't need a try/catch around a processDropdown.");
            }
            try {
                (new WebDriverWait(driver, 15)).until(ExpectedConditions.refreshed(rankDropdownIsVisible));
                (new WebDriverWait(driver, 15)).until(rankDropdownIsVisible);
                (new WebDriverWait(driver, 15)).until(rankDropdownOptionsMoreThanOne);

                WebElement rankDropdown = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(rankBy)));
                Select rankSelect = new Select(rankDropdown);
                nOptions = rankSelect.getOptions().size();
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("SelectedPatientInformation.process(), Could not get rank dropdown, or select from it or get size.");
                continue;
            }
        } while (nOptions < 2);
        if (nOptions < 2) {
            if (Arguments.debug) System.out.println("Rank dropdown had this many options: " + nOptions + " and so this looks like failure.");
            return false;
        }
        selectedPatientInformation.rank = Utilities.processDropdown(rankBy, selectedPatientInformation.rank, selectedPatientInformation.random, true); // off by one?



        // Skip this next part (Branch/Rank) for now.  Rewrite it.  It's always a problem
        // Rank sucks.  Why?  Because the dropdown is slow to populate.  You can't choose a rank until you choose a branch.  Branch is
        // slow to populate rank.  How can you tell when it's done populating?
        // This next stuff to do branch and rank is really weird if you are not doing random.  Review to see if can streamline for when have actual values.
//        ExpectedCondition<WebElement> rankDropdownIsVisible = ExpectedConditions.visibilityOfElementLocated(rankBy);
//        By optionOfRankDropdown = By.xpath("//*[@id=\"patientInfoBean.rankId\"]/option[1]"); // this is not right, but here to get a compile
//        ExpectedCondition<List<WebElement>> rankDropdownOptionsMoreThanOne = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfRankDropdown, 1);
//
//        int nOptions = 0;
//        int loopCtr = 0;
//        do {
//            if (++loopCtr > 10) {
//                break;
//            }
//            try {
//                // doing this next line triggers a JS method call "getRank(...)" which causes the
//                // Rank dropdown to get new options.  //*[@id="patientRegistration.rank"]
//                // The number of options could change.  Before selecting an option on Branch, the
//                // Rank dropdown options is only one: "N/A".  But after a selection, the first one is always "Select Rank",
//                // followed by one or more others.  So, can't we just wait until children is greater than 1?
//                System.out.println("selectedPatientInformation.process(), 1");
//                selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.random, true);
//                System.out.println("selectedPatientInformation.process(), 2");
//            }
//            catch (Exception e) {
//                System.out.println("Prob don't need a try/catch around a processDropdown.");
//            }
//            try {
//                System.out.println("selectedPatientInformation.process(), 3");
//                (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(rankDropdownIsVisible, rankDropdownOptionsMoreThanOne));
//                System.out.println("selectedPatientInformation.process(), 4");
//
//                WebElement rankDropdown = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(rankBy)));
//                System.out.println("selectedPatientInformation.process(), 5");
//                Select rankSelect = new Select(rankDropdown);
//                System.out.println("selectedPatientInformation.process(), 6");
//                nOptions = rankSelect.getOptions().size();
//                System.out.println("selectedPatientInformation.process(), 7");
//
//            }
//            catch (Exception e) {
//                System.out.println("selectedPatientInformation.process(), 8");
//                if (Arguments.debug) System.out.println("selectedPatientInformation.process(), Could not get rank dropdown, or select from it or get size.");
//                continue;
//            }
//        } while (nOptions < 2);
//        System.out.println("selectedPatientInformation.process(), 9");
//        if (nOptions < 2) {
//            if (Arguments.debug) System.out.println("Rank dropdown had this many options: " + nOptions + " and so this looks like failure.");
//            return false;
//        }
//        if (Arguments.debug) System.out.println("selectedPatientInformation.process(), Just got a branch: " + selectedPatientInformation.branch + " and now will do rank.");
//        selectedPatientInformation.rank = Utilities.processDropdown(rankBy, selectedPatientInformation.rank, selectedPatientInformation.random, true); // off by one?







//        // This next part is wrong when you don't have a sponsor ssn value, but FMP 20 causes self ssn to go in.
//        // don't overwrite sponsor ssn if it's filled in because of FMP.  So review this section and fix if necessary
////        By sponsorSsnBy = By.id("patientRegistration.sponsorSsn");
//        try {
//            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(sponsorSsnBy)));
//        }
//        catch (Exception e) {
//            System.out.println("Didn't get a refresh of the sponsorSsn");
//            return false;
//        }
//        selectedPatientInformation.sponsorSsn = Utilities.processText(sponsorSsnBy, selectedPatientInformation.sponsorSsn, Utilities.TextFieldType.SSN, selectedPatientInformation.random, true); // sometimes erased



        selectedPatientInformation.patientCategory = Utilities.processDropdown(patientCategoryBy, selectedPatientInformation.patientCategory, selectedPatientInformation.random, true);

        // We don't want years of service more than their age minus about 20
        // This next part looks clumbsy, but wanna get through it for now
        if (selectedPatientInformation.yearsOfService == null || selectedPatientInformation.yearsOfService.isEmpty()) {
            By ageBy = By.id("age");
            try {
                WebElement age = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ageBy));
                String ageString = age.getAttribute("value");
                if (ageString != null && !ageString.isEmpty()) {
                    int ageInt = Integer.parseInt(ageString);
                    selectedPatientInformation.yearsOfService = String.valueOf((ageInt > 16) ? (ageInt - 16)/2 : 0);
                }
            } catch (Exception e) {
                if (Arguments.debug) System.out.println("SelectedPatientInformation.process(), coldn't get age.");
            }
        }
        //selectedPatientInformation.yearsOfService = Utilities.processStringOfDigits(selectedPatientInformation.yearsOfServiceBy, selectedPatientInformation.yearsOfService, 1,3, selectedPatientInformation.random, true);
        selectedPatientInformation.yearsOfService = Utilities.processIntegerNumber(selectedPatientInformation.yearsOfServiceBy, selectedPatientInformation.yearsOfService, 1,3, selectedPatientInformation.random, true);

        selectedPatientInformation.operation = Utilities.processDropdown(operationBy, selectedPatientInformation.operation, selectedPatientInformation.random, true);

        selectedPatientInformation.mobOrder = Utilities.processBoolean(mobOrderBy, selectedPatientInformation.mobOrder, selectedPatientInformation.random,false);
        selectedPatientInformation.deploymentOrder = Utilities.processBoolean(deploymentOrderBy, selectedPatientInformation.deploymentOrder, selectedPatientInformation.random,false);

        // NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW NEW
        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        if (selectedPatientInformation.ssn != null && !selectedPatientInformation.ssn.isEmpty()) {
            patient.patientSearch.ssn = selectedPatientInformation.ssn;
        }
        if (selectedPatientInformation.firstName != null && !selectedPatientInformation.firstName.isEmpty()) {
            patient.patientSearch.firstName = selectedPatientInformation.firstName;
        }
        if (selectedPatientInformation.lastName != null && !selectedPatientInformation.lastName.isEmpty()) {
            patient.patientSearch.lastName = selectedPatientInformation.lastName;
        }
        // There's no trauma register number in selected patient information section
        return true;
    }
}
