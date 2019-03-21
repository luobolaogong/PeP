package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

/**
 * This class is part of PatientInformation
 */
public class EmergencyContact {
    private static Logger logger = Logger.getLogger(EmergencyContact.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String name;
    public String address;
    public String dateOfLastContactWithFamily;
    public String relationship;
    public String phoneNumber;
    public String organDonor;

    public EmergencyContact() {
        if (Arguments.template) {
            this.name = "";
            this.address = "";
            this.dateOfLastContactWithFamily = "";
            this.relationship = "";
            this.phoneNumber = "";
            this.organDonor = "";
        }
    }

    private static By emergencyContactNameBy = By.id("patientInfoBean.pnokName");
    private static By mergencyContactAddressBy = By.id("patientInfoBean.pnokAddress.addressLine1");
    private static By dateOfLastContactWithFamilyBy = By.id("dateOfLastContact");
    private static By relationshipBy = By.id("patientInfoBean.pnokRelationship");
    private static By emergencyContactPhoneNumberBy = By.id("patientInfoBean.pnokAddress.phone");
    private static By organDonorBy = By.id("patientInfoBean.organDonorInd");

    /**
     * Process the emergency contact part of the PatientInformation page
     * @param patient The patient
     * @return Success or Failure of being able to add emergency contact info to the page
     */
    public boolean process(Patient patient) {
        EmergencyContact emergencyContact = patient.registration.patientInformation.emergencyContact;

        if (!Arguments.quiet)
            System.out.println("    Processing Emergency Contact Information for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        // Many of the following are bad guesses for random values
        try {
            emergencyContact.name = Utilities.processText(emergencyContactNameBy, emergencyContact.name, (Utilities.random.nextBoolean() ? Utilities.TextFieldType.NAME_FEMALE : Utilities.TextFieldType.NAME_MALE), emergencyContact.randomizeSection, false);
            emergencyContact.relationship = Utilities.processText(relationshipBy, emergencyContact.relationship, Utilities.TextFieldType.RELATIONSHIP, emergencyContact.randomizeSection, false);
            emergencyContact.address = Utilities.processText(mergencyContactAddressBy, emergencyContact.address, Utilities.TextFieldType.US_ADDRESS_NO_STATE, emergencyContact.randomizeSection, false);
            emergencyContact.phoneNumber = Utilities.processText(emergencyContactPhoneNumberBy, emergencyContact.phoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, emergencyContact.randomizeSection, false);
            emergencyContact.dateOfLastContactWithFamily = Utilities.processText(dateOfLastContactWithFamilyBy, emergencyContact.dateOfLastContactWithFamily, Utilities.TextFieldType.DATE, emergencyContact.randomizeSection, false);
            emergencyContact.organDonor = Utilities.processDropdown(organDonorBy, emergencyContact.organDonor, emergencyContact.randomizeSection, false);
        }
        catch (Exception e) {
            logger.fine("Not sure what could go wrong, but surely something could.");
            return false;
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "EmergencyContact");
        }
        return true;
    }
}
