package pep.patient.registration;

import org.openqa.selenium.By;

public class PermanentHomeOfRecord {

    // Permanent Home of Record, Home and Deployed Unit Information
    public static By permanentHomeOfRecordAddressBy = By.id("patientInfoBean.homeOfRecordAddress.city");
    public static By permanentHomeOfRecordStateBy = By.id("patientInfoBean.homeOfRecordAddress.state");
    public static By homePhoneNumberBy = By.id("patientInfoBean.homeOfRecordAddress.phone");
    public static By mosSpecialtyPositionTitleBy = By.id("patientInfoBean.mosAfsc");
    public static By homeUnitNameBy = By.id("patientInfoBean.stateSidePoc.pocUnitName");
    public static By homeUnitUicBy = By.id("patientInfoBean.stateSidePoc.pocUic");
    public static By homeUnitPocNameBy = By.id("patientInfoBean.stateSidePoc.pocRankName");
    public static By homeUnitAddressBy = By.id("patientInfoBean.stateSidePoc.pocAddress");
    public static By homeUnitStateBy = By.id("patientInfoBean.stateSidePoc.state");
    public static By homeUitPocPhoneNumberBy = By.id("patientInfoBean.stateSidePoc.pocPhone");
    public static By deployedUnitNameBy = By.id("patientInfoBean.deployedPoc.pocUnitName");
    public static By deployedUnitUicBy = By.id("patientInfoBean.deployedPoc.pocUic");
    public static By deployedUnitPocNameBy = By.id("patientInfoBean.deployedPoc.pocRankName");
    public static By deployedUnitPocPhoneNumberBy = By.id("patientInfoBean.deployedPoc.pocPhone");
    public static By mobilizationStationBy = By.id("patientInfoBean.nationalGuardReserveAddress.postalCode");
    public static By mobilizationStateBy = By.id("patientInfoBean.nationalGuardReserveAddress.state");

}
