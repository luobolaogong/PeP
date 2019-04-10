package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * This class handles the Permanent Home of Record
 */
public class PermanentHomeOfRecord {
    private static Logger logger = Logger.getLogger(PermanentHomeOfRecord.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
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

    public PermanentHomeOfRecord() {
        if (Arguments.template) {
            this.permanentHomeOfRecordAddress = "";
            this.permanentHomeOfRecordState = "";
            this.homePhoneNumber = "";
            this.mosSpecialtyPositionTitle = "";
            this.homeUnitName = "";
            this.homeUnitUic = "";
            this.homeUnitPocName = "";
            this.homeUnitAddress = "";
            this.homeUnitState = "";
            this.homeUnitPocPhoneNumber = "";
            this.deployedUnitName = "";
            this.deployedUnitUic = "";
            this.deployedUnitPocName = "";
            this.deployedUnitPocPhoneNumber = "";
            this.mobilizationStation = "";
            this.mobilizationState = "";
        }
    }

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

    /**
     * Process the Permanent Home Record for this patient
     * @param patient The patient for this record
     * @return Success or Failure os saving this record
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet)
            System.out.println("    Processing Permanent Home of Record at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        PermanentHomeOfRecord permanentHomeOfRecord = patient.registration.patientInformation.permanentHomeOfRecord;

        // Doing this next section randomly doesn't make good sense.  no address, but has state, for example
        // Many of the following are bad guesses for random values
        try {
            permanentHomeOfRecord.permanentHomeOfRecordAddress = Utilities.processText(permanentHomeOfRecordAddressBy, permanentHomeOfRecord.permanentHomeOfRecordAddress, Utilities.TextFieldType.US_ADDRESS_NO_STATE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.permanentHomeOfRecordState = Utilities.processDropdown(permanentHomeOfRecordStateBy, permanentHomeOfRecord.permanentHomeOfRecordState, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homePhoneNumber = Utilities.processText(homePhoneNumberBy, permanentHomeOfRecord.homePhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.mosSpecialtyPositionTitle = Utilities.processText(mosSpecialtyPositionTitleBy, permanentHomeOfRecord.mosSpecialtyPositionTitle, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitName = Utilities.processText(homeUnitNameBy, permanentHomeOfRecord.homeUnitName, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitUic = Utilities.processText(homeUnitUicBy, permanentHomeOfRecord.homeUnitUic, Utilities.TextFieldType.UNIT_IDENTIFICATION_CODE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitPocName = Utilities.processText(homeUnitPocNameBy, permanentHomeOfRecord.homeUnitPocName, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitAddress = Utilities.processText(homeUnitAddressBy, permanentHomeOfRecord.homeUnitAddress, Utilities.TextFieldType.US_ADDRESS_NO_STATE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitState = Utilities.processDropdown(homeUnitStateBy, permanentHomeOfRecord.homeUnitState, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.homeUnitPocPhoneNumber = Utilities.processText(homeUitPocPhoneNumberBy, permanentHomeOfRecord.homeUnitPocPhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.deployedUnitName = Utilities.processText(deployedUnitNameBy, permanentHomeOfRecord.deployedUnitName, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.deployedUnitUic = Utilities.processText(deployedUnitUicBy, permanentHomeOfRecord.deployedUnitUic, Utilities.TextFieldType.UNIT_IDENTIFICATION_CODE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.deployedUnitPocName = Utilities.processText(deployedUnitPocNameBy, permanentHomeOfRecord.deployedUnitPocName, Utilities.TextFieldType.TITLE, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.deployedUnitPocPhoneNumber = Utilities.processText(deployedUnitPocPhoneNumberBy, permanentHomeOfRecord.deployedUnitPocPhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.mobilizationStation = Utilities.processText(mobilizationStationBy, permanentHomeOfRecord.mobilizationStation, Utilities.TextFieldType.UNIT_NAME, permanentHomeOfRecord.randomizeSection, false);
            permanentHomeOfRecord.mobilizationState = Utilities.processDropdown(mobilizationStateBy, permanentHomeOfRecord.mobilizationState, permanentHomeOfRecord.randomizeSection, false);
        }
        catch (Exception e) {
            logger.fine("Not sure what could go wrong, but surely something could: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "PermanentHomeOfRecord");
        }
        return true;
    }
}
