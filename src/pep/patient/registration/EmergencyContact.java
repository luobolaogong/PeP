package pep.patient.registration;

import org.openqa.selenium.By;

public class EmergencyContact {
    // Emergency Contact
    public static By emergencyContactNameBy = By.id("patientInfoBean.pnokName");
    public static By mergencyContactAddressBy = By.id("patientInfoBean.pnokAddress.addressLine1");
    public static By dateOfLastContactWithFamilyBy = By.id("dateOfLastContact");
    public static By relationshipBy = By.id("patientInfoBean.pnokRelationship");
    public static By emergencyContactPhoneNumberBy = By.id("patientInfoBean.pnokAddress.phone");
    public static By organDonorBy = By.id("patientInfoBean.organDonorInd");

}
