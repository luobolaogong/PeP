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
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;

public class SelectedPatientInformation {
    private static Logger logger = Logger.getLogger(SelectedPatientInformation.class.getName());
    public Boolean random;
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
    //private static By optionOfRankDropdown = By.xpath("//*[@id=\"patientInfoBean.rankId\"]/option"); // guess

    private static By patientCategoryBy = By.id("patientInfoBean.patientCategory"); // looks right
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
    
    public boolean process(Patient patient) {
        // I guess we're now requiring the use of the PatientSearch object
        // Before going further we should verify we're on the right page.  The Search may not have worked.  This does happen!!!
        // Why does this happen?  Possibly the patient has been "departed"?
        // But really, this should be done before we get here.
        // new 10/25/18
        if (!Arguments.quiet)
            System.out.println("    Processing Selected Patient Information for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );



        SelectedPatientInformation selectedPatientInformation = patient.registration.patientInformation.selectedPatientInformation;

        // Shouldn't continue on unless we've found a patient.  The error is that Search For Patient doesn't work

        // This Branch stuff was below, but making a change to Branch causes Rank and Patient Category to get their dropdown options updated.
        // which takes time (2 sec?) and this can cause havoc.  So, this will help a bit, maybe.  Hopefully putting this here will not
        // screw up the following fields if we go fast.  Probably it will!
        try {
            // A change of branch will cause a reset of Patient Category which can take a long time. (at least 1 sec?)
            selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.random, true);
        }
        catch (Exception e) {
            logger.fine("Prob don't need a try/catch around a processDropdown.");
        }



        // It appears that arrival Date is not writable?  Its value comes from some other record.
        selectedPatientInformation.arrivalDate = Utilities.processDate(arrivalDateBy, selectedPatientInformation.arrivalDate, selectedPatientInformation.random, false);
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
//check demographics to see how I did this
//        selectedPatientInformation.sponsorSsn = Utilities.processText(sponsorSsnBy, selectedPatientInformation.sponsorSsn, Utilities.TextFieldType.SSN, selectedPatientInformation.random, true); // sometimes erased

        selectedPatientInformation.dob = Utilities.processText(dobBy, selectedPatientInformation.dob, Utilities.TextFieldType.DOB, selectedPatientInformation.random, true);
        selectedPatientInformation.race = Utilities.processDropdown(raceBy, selectedPatientInformation.race, selectedPatientInformation.random, true);
        selectedPatientInformation.nation = Utilities.processDropdown(nationBy, selectedPatientInformation.nation, selectedPatientInformation.random, true);
        selectedPatientInformation.maritalStatus = Utilities.processDropdown(maritalStatusBy, selectedPatientInformation.maritalStatus, selectedPatientInformation.random, true);
        selectedPatientInformation.religiousPreference = Utilities.processDropdown(religiousPreferenceBy, selectedPatientInformation.religiousPreference, selectedPatientInformation.random, true);

        // This next stuff was in the loop below.  Pulled it out 1/16/19
        // And now, 1/23/19 moving it to the top.
//        try {
//            // A change of branch will cause a reset of Patient Category which can take a long time. (at least 1 sec?)
//            selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.random, true);
//        }
//        catch (Exception e) {
//            logger.fine("Prob don't need a try/catch around a processDropdown.");
//        }




//        // Wow, this next stuff looks extensive, and it's just to handle rank?
//        // Looping because of timing problems?  There are better ways, like waiting until the first option in the dropdown gets refreshed or something.
//        ExpectedCondition<WebElement> rankDropdownIsVisible = ExpectedConditions.visibilityOfElementLocated(rankBy);
//        ExpectedCondition<List<WebElement>> rankDropdownOptionsMoreThanOne = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfRankDropdown, 1);
//        int nOptions = 0;
//        int loopCtr = 0;
//        do {
//            if (++loopCtr > 10) {
//                break;
//            }
////            try {
////                // A change of branch will cause a reset of Patient Category which can take a long time. (at least 1 sec?)
////                selectedPatientInformation.branch = Utilities.processDropdown(branchBy, selectedPatientInformation.branch, selectedPatientInformation.random, true);
////            }
////            catch (Exception e) {
////                logger.fine("Prob don't need a try/catch around a processDropdown.");
////            }
//            try {
//                (new WebDriverWait(driver, 15)).until(ExpectedConditions.refreshed(rankDropdownIsVisible));
//                (new WebDriverWait(driver, 15)).until(rankDropdownIsVisible);
//                (new WebDriverWait(driver, 15)).until(rankDropdownOptionsMoreThanOne);
//
//                WebElement rankDropdown = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(rankBy)));
//                Select rankSelect = new Select(rankDropdown);
//                nOptions = rankSelect.getOptions().size();
//            }
//            catch (Exception e) {
//                logger.fine("SelectedPatientInformation.process(), Could not get rank dropdown, or select from it or get size.");
//                continue;
//            }
//        } while (nOptions < 2);
//        if (nOptions < 2) {
//            logger.fine("Rank dropdown had this many options: " + nOptions + " and so this looks like failure.");
//            return false;
//        }

        // Handle Rank here, but don't do it in a loop like above.
        //logger.fine("SelectedPatientInformation.process(), Skipping rank for now.  It needs to be done same as Patient Category");

        // Utilities.sleep(555); // added 1/4/19, because the next dropdown with random seems to fail sometimes
        // The problem with Patient Category is that a change of military branch will cause a reset and repopulation
        // of Patient Category options, which is slow.  If branch changes we have to wait until Patient Category is reset.
        // If branch value does not change from the previous value, then we don't have to wait at all.  But we must first
        // process Branch, otherwise Patient Category would get reset to "Select Patient Category" and that would prevent
        // the page from being saved.  Ditto for Rank.
        //
        // If we were looking at PatientCategory before processing Branch above, we'd see some options related to that Branch.
        // (And there would have to be a Branch selected, because previously in patient registration somewhere it was set.)
        // The Branch value may not get changed in this Patient Information/Selected Patient Information class/page, which would mean
        // that the options would not change.  But if Branch changed, then the options would change.
        // We should always be able to get the WebElement for the dropdown.  That's not a problem.
        // We should always be able to get a value of it's first option too.
        // But we don't know if it will get removed and something will replace it, and we don't know how long to wait to know.
        // If it will get removed and something replaces it, we want to wait until that happens, using a stale reference or something.
        // So, we can't wait for staleness to know when to proceed. XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // You know this same thing happens in New Patient Reg, right? In Demographics?
        // Very experimental:
//        try {
//            WebElement patientCategoryDropdown = (new WebDriverWait(driver, 2)).until(ExpectedConditions.presenceOfElementLocated(patientCategoryBy));
////            By firstOptionInDropdownBy = By.xpath("//*[@id=\"patientInfoBean.patientCategory\"]/option[1]"); // works
//            //By firstOptionInDropdownBy = By.xpath("//option[text()='Select Patient Category']"); // prob works
//            By firstOptionInDropdownBy = By.xpath("//select[@id='patientInfoBean.patientCategory']/option[text()='Select Patient Category']"); // prob works
//            WebElement firstOptionInDropdown = (new WebDriverWait(driver, 2)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(firstOptionInDropdownBy)));
//            (new WebDriverWait(driver, 2)).until(ExpectedConditions.stalenessOf(firstOptionInDropdown));
//            (new WebDriverWait(driver, 2)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(firstOptionInDropdownBy)));
//        }
//        catch (Exception e) {
//            logger.severe("SelectedPatientInformation.process(), Patient Category should go stale because Branch causes a reset, but couldn't wait. e: " + Utilities.getMessageFirstLine(e));
//        }

//        selectedPatientInformation.patientCategory = Utilities.processDropdown(patientCategoryBy, selectedPatientInformation.patientCategory, selectedPatientInformation.random, true);
// so what happened above?  Did it finish?  Did it work?  The page will fail if the value isn't set.  (Or was it overwritten somehow?)
        // Do we continue on too soon, and somehow we don't get a value?  Should we wait for a change to occur?

        // We don't want years of service more than their age minus about 20
        // This next part looks clumsy, but wanna get through it for now
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
        selectedPatientInformation.yearsOfService = Utilities.processIntegerNumber(selectedPatientInformation.yearsOfServiceBy, selectedPatientInformation.yearsOfService, 1,3, selectedPatientInformation.random, true);

        selectedPatientInformation.operation = Utilities.processDropdown(operationBy, selectedPatientInformation.operation, selectedPatientInformation.random, true);

        selectedPatientInformation.mobOrder = Utilities.processBoolean(mobOrderBy, selectedPatientInformation.mobOrder, selectedPatientInformation.random,false);
        selectedPatientInformation.deploymentOrder = Utilities.processBoolean(deploymentOrderBy, selectedPatientInformation.deploymentOrder, selectedPatientInformation.random,false);

        // kinda doubt I need this
        try {
            Utilities.waitForRefreshedPresence(sponsorSsnBy,  10, "SelectedPatientInformation.(), sponsor ssn");
        }
        catch (Exception e) {
            logger.severe("Didn't get a refresh of the sponsorSsn");
            return false;
        }

        // Wow, moved rank and patient category way down here after doing all that looping and testing above
        selectedPatientInformation.rank = Utilities.processDropdown(rankBy, selectedPatientInformation.rank, selectedPatientInformation.random, true); // off by one?
        selectedPatientInformation.patientCategory = Utilities.processDropdown(patientCategoryBy, selectedPatientInformation.patientCategory, selectedPatientInformation.random, true);
        // And moved sponsorSsn down here because FMP can modify it.
        selectedPatientInformation.sponsorSsn = Utilities.processText(sponsorSsnBy, selectedPatientInformation.sponsorSsn, Utilities.TextFieldType.SSN, selectedPatientInformation.random, true); // sometimes erased

        // It's possible that the patient got name, ssn, id changed in this method, so we should update:
        if (selectedPatientInformation.ssn != null && !selectedPatientInformation.ssn.isEmpty()) {
            patient.patientSearch.ssn = selectedPatientInformation.ssn;
        }
        if (selectedPatientInformation.firstName != null && !selectedPatientInformation.firstName.isEmpty()) {
            //patient.patientSearch.firstName = selectedPatientInformation.firstName;
            if (!patient.patientSearch.firstName.equalsIgnoreCase(selectedPatientInformation.firstName)) {
                patient.patientSearch.firstName = selectedPatientInformation.firstName;
            }
        }
        if (selectedPatientInformation.lastName != null && !selectedPatientInformation.lastName.isEmpty()) {
            if (!patient.patientSearch.lastName.equalsIgnoreCase(selectedPatientInformation.lastName)) {
                patient.patientSearch.lastName = selectedPatientInformation.lastName;
            }
        }
        // There's no trauma register number in selected patient information section
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }
}
