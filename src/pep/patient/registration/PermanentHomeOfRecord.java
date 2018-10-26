package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

public class PermanentHomeOfRecord {
    public Boolean random; // true if want this section to be generated randomly
    public String permanentHomeOfRecordAddress;
    public String permanentHomeOfRecordState;
    public String homePhoneNumber;
    public String mosSpecialtyPositionTitle;
    public String homeUnitName;
    public String homeUnitUic;
    public String homeUnitPocName;
    public String homeUnitAddress;
    public String homeUnitState;
    public String homeUnitPocPhoneNumber;
    public String deployedUnitName;
    public String deployedUnitUic;
    public String deployedUnitPocName;
    public String deployedUnitPocPhoneNumber;
    public String mobilizationStation;
    public String mobilizationState;

    // Permanent Home of Record, Home and Deployed Unit Information
    private static By permanentHomeOfRecordAddressBy = By.id("patientInfoBean.homeOfRecordAddress.city");
    private static By permanentHomeOfRecordStateBy = By.id("patientInfoBean.homeOfRecordAddress.state"); // validated
    private static By homePhoneNumberBy = By.id("patientInfoBean.homeOfRecordAddress.phone");
    private static By mosSpecialtyPositionTitleBy = By.id("patientInfoBean.mosAfsc");
    private static By homeUnitNameBy = By.id("patientInfoBean.stateSidePoc.pocUnitName");
    private static By homeUnitUicBy = By.id("patientInfoBean.stateSidePoc.pocUic");
    private static By homeUnitPocNameBy = By.id("patientInfoBean.stateSidePoc.pocRankName");
    private static By homeUnitAddressBy = By.id("patientInfoBean.stateSidePoc.pocAddress");
    private static By homeUnitStateBy = By.id("patientInfoBean.stateSidePoc.state");
    private static By homeUitPocPhoneNumberBy = By.id("patientInfoBean.stateSidePoc.pocPhone");
    private static By deployedUnitNameBy = By.id("patientInfoBean.deployedPoc.pocUnitName");
    private static By deployedUnitUicBy = By.id("patientInfoBean.deployedPoc.pocUic");
    private static By deployedUnitPocNameBy = By.id("patientInfoBean.deployedPoc.pocRankName");
    private static By deployedUnitPocPhoneNumberBy = By.id("patientInfoBean.deployedPoc.pocPhone");
    private static By mobilizationStationBy = By.id("patientInfoBean.nationalGuardReserveAddress.postalCode");
    private static By mobilizationStateBy = By.id("patientInfoBean.nationalGuardReserveAddress.state");

    // Hey, before we get here is this.random set?
    public boolean process(Patient patient) {

        // new 10/25/18
        if (!Arguments.quiet)
            System.out.println("    Processing Permanent Home of Record for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        PermanentHomeOfRecord permanentHomeOfRecord = patient.patientRegistration.patientInformation.permanentHomeOfRecord;

        // Doing this next section randomly doesn't make good sense.  no address, but has state, for example
        // Many of the following are bad guesses for random values
        try { // why the next one is getting skipped?
            permanentHomeOfRecord.permanentHomeOfRecordAddress = Utilities.processText(permanentHomeOfRecordAddressBy, permanentHomeOfRecord.permanentHomeOfRecordAddress, Utilities.TextFieldType.US_ADDRESS_NO_STATE, permanentHomeOfRecord.random, false);
            // next line npe
            permanentHomeOfRecord.permanentHomeOfRecordState = Utilities.processDropdown(permanentHomeOfRecordStateBy, permanentHomeOfRecord.permanentHomeOfRecordState, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homePhoneNumber = Utilities.processText(homePhoneNumberBy, permanentHomeOfRecord.homePhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.mosSpecialtyPositionTitle = Utilities.processText(mosSpecialtyPositionTitleBy, permanentHomeOfRecord.mosSpecialtyPositionTitle, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitName = Utilities.processText(homeUnitNameBy, permanentHomeOfRecord.homeUnitName, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitUic = Utilities.processText(homeUnitUicBy, permanentHomeOfRecord.homeUnitUic, Utilities.TextFieldType.UNIT_IDENTIFICATION_CODE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitPocName = Utilities.processText(homeUnitPocNameBy, permanentHomeOfRecord.homeUnitPocName, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitAddress = Utilities.processText(homeUnitAddressBy, permanentHomeOfRecord.homeUnitAddress, Utilities.TextFieldType.US_ADDRESS_NO_STATE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitState = Utilities.processDropdown(homeUnitStateBy, permanentHomeOfRecord.homeUnitState, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.homeUnitPocPhoneNumber = Utilities.processText(homeUitPocPhoneNumberBy, permanentHomeOfRecord.homeUnitPocPhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.deployedUnitName = Utilities.processText(deployedUnitNameBy, permanentHomeOfRecord.deployedUnitName, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.deployedUnitUic = Utilities.processText(deployedUnitUicBy, permanentHomeOfRecord.deployedUnitUic, Utilities.TextFieldType.UNIT_IDENTIFICATION_CODE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.deployedUnitPocName = Utilities.processText(deployedUnitPocNameBy, permanentHomeOfRecord.deployedUnitPocName, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.deployedUnitPocPhoneNumber = Utilities.processText(deployedUnitPocPhoneNumberBy, permanentHomeOfRecord.deployedUnitPocPhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.mobilizationStation = Utilities.processText(mobilizationStationBy, permanentHomeOfRecord.mobilizationStation, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.random, false);
            permanentHomeOfRecord.mobilizationState = Utilities.processDropdown(mobilizationStateBy, permanentHomeOfRecord.mobilizationState, permanentHomeOfRecord.random, false);
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Not sure what could go wrong, but surely something could: " + e.getMessage());
            return false;
        }
        return true;
    }
}
