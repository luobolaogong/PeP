package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

public class EmergencyContact {
    public Boolean random; // we're possibly missing something.  Where does this get set?
    public String emergencyContactName;
    public String mergencyContactAddress;
    public String dateOfLastContactWithFamily;
    public String relationship;
    public String emergencyContactPhoneNumber;
    public String organDonor;

    private static By emergencyContactNameBy = By.id("patientInfoBean.pnokName");
    private static By mergencyContactAddressBy = By.id("patientInfoBean.pnokAddress.addressLine1");
    private static By dateOfLastContactWithFamilyBy = By.id("dateOfLastContact");
    private static By relationshipBy = By.id("patientInfoBean.pnokRelationship");
    private static By emergencyContactPhoneNumberBy = By.id("patientInfoBean.pnokAddress.phone");
    private static By organDonorBy = By.id("patientInfoBean.organDonorInd");

    public boolean process(Patient patient) {
        EmergencyContact emergencyContact = patient.patientRegistration.patientInformation.emergencyContact;
        // Many of the following are bad guesses for random values
        // do address with state
        // do full name

        try {
            emergencyContact.emergencyContactName = Utilities.processText(emergencyContactNameBy, emergencyContact.emergencyContactName, (Utilities.random.nextBoolean() ? Utilities.TextFieldType.NAME_FEMALE : Utilities.TextFieldType.NAME_MALE), emergencyContact.random, false);
            emergencyContact.mergencyContactAddress = Utilities.processText(mergencyContactAddressBy, emergencyContact.mergencyContactAddress, Utilities.TextFieldType.US_ADDRESS_NO_STATE, emergencyContact.random, false);
            emergencyContact.dateOfLastContactWithFamily = Utilities.processText(dateOfLastContactWithFamilyBy, emergencyContact.dateOfLastContactWithFamily, Utilities.TextFieldType.DATE, emergencyContact.random, false);
            emergencyContact.relationship = Utilities.processText(relationshipBy, emergencyContact.relationship, Utilities.TextFieldType.RELATIONSHIP, emergencyContact.random, false);
            emergencyContact.emergencyContactPhoneNumber = Utilities.processText(emergencyContactPhoneNumberBy, emergencyContact.emergencyContactPhoneNumber, Utilities.TextFieldType.US_PHONE_NUMBER, emergencyContact.random, false);
            emergencyContact.organDonor = Utilities.processDropdown(organDonorBy, emergencyContact.organDonor, emergencyContact.random, false);
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Not sure what could go wrong, but surely something could.");
            return false;
        }
        return true;

    }
}
