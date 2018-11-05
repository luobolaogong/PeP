package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.logging.Logger;

public class EmergencyContact {
  private static Logger logger = Logger.getLogger(EmergencyContact.class.getName());
    public Boolean random; // we're possibly missing something.  Where does this get set?
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

    public boolean process(Patient patient) {
        EmergencyContact emergencyContact = patient.patientRegistration.patientInformation.emergencyContact;

        if (!Arguments.quiet)
            System.out.println("    Processing Emergency Contact Information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        // Many of the following are bad guesses for random values
        try {
            emergencyContact.name = Utilities.processText(emergencyContactNameBy, emergencyContact.name, (Utilities.random.nextBoolean() ? Utilities.TextFieldType.NAME_FEMALE : Utilities.TextFieldType.NAME_MALE), emergencyContact.random, false);
            emergencyContact.relationship = Utilities.processText(relationshipBy, emergencyContact.relationship, Utilities.TextFieldType.RELATIONSHIP, emergencyContact.random, false);
            emergencyContact.address = Utilities.processText(mergencyContactAddressBy, emergencyContact.address, Utilities.TextFieldType.US_ADDRESS_NO_STATE, emergencyContact.random, false);
            emergencyContact.phoneNumber = Utilities.processText(emergencyContactPhoneNumberBy, emergencyContact.phoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, emergencyContact.random, false);
            emergencyContact.dateOfLastContactWithFamily = Utilities.processText(dateOfLastContactWithFamilyBy, emergencyContact.dateOfLastContactWithFamily, Utilities.TextFieldType.DATE, emergencyContact.random, false);
            emergencyContact.organDonor = Utilities.processDropdown(organDonorBy, emergencyContact.organDonor, emergencyContact.random, false);
        }
        catch (Exception e) {
            logger.fine("Not sure what could go wrong, but surely something could.");
            return false;
        }
        if (Arguments.sectionPause > 0) {
            Utilities.sleep(Arguments.sectionPause * 1000);
        }

        return true;

    }
}
