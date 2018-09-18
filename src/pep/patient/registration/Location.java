package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;

public class Location {
    public Boolean random;
    public String treatmentStatus; // "option 1-3, required";
    public String roomNumberLocationInformation; // "text";
    public String treatmentLocation; // "text";
    public String administrativeNotes; // "text";

    private static final By LOCATION_TREATMENT_STATUS_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.treatmentStatus']");
    private static final By LOCATION_TREATMENT_LOCATION_DROPDOWN = By
            .xpath("//select[@id='patientRegistration.wardBilletingId']");
    private static final By LOCATION_ROOM_NUMBER_FIELD = By
            .xpath("//input[@name='patientRegistration.roomNumber']");
    private static final By LOCATION_ADMIN_NOTES_FIELD = By
            .xpath("//textarea[@name='patientRegistration.notes']");


    private static By locationTreatmentStatusDropdownBy = By.id("patientRegistration.treatmentStatus");
    private static By locationRoomNumberFieldBy = By.id("patientRegistration.roomNumber");
    private static By locationTreatmentLocationDropdownBy = By.id("patientRegistration.wardBilletingId"); // verified
    private static By locationAdminNotesFieldBy = By.id("patientRegistration.notes");


    public Location() {
        if (Arguments.template) {
            this.random = null;
            this.treatmentStatus = "";
            this.roomNumberLocationInformation = "";
            this.treatmentLocation = "";
            this.administrativeNotes = "";
        }
        if (isDemoTier) {
            locationTreatmentStatusDropdownBy = LOCATION_TREATMENT_STATUS_DROPDOWN;
            locationRoomNumberFieldBy = LOCATION_ROOM_NUMBER_FIELD;
            locationTreatmentLocationDropdownBy = LOCATION_TREATMENT_LOCATION_DROPDOWN;
            locationAdminNotesFieldBy = LOCATION_ADMIN_NOTES_FIELD;
        }
    }

    // this is a level 4 only
    public boolean process(Patient patient) { // test on demo
        if (!Arguments.quiet) System.out.println("    Processing Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");
        //Location location = patient.patientRegistration.newPatientReg.location;
        Location location = null;
        if (patient.patientState == PatientState.NEW_REGISTRATION && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.location != null) {
            location = patient.patientRegistration.newPatientReg.location;
        }
        if (patient.patientState == PatientState.UPDATE_REGISTRATION && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.location != null) {
            location = patient.patientRegistration.updatePatient.location;
        }




        // The treatment location dropdown options are dependent upon Treatment Status.  So we can have a timing issue, again.
        location.treatmentStatus = Utilities.processDropdown(locationTreatmentStatusDropdownBy, location.treatmentStatus, location.random, true); // check on demo tier

        location.roomNumberLocationInformation = Utilities.processText(locationRoomNumberFieldBy, location.roomNumberLocationInformation, Utilities.TextFieldType.HHMM, location.random, false);

        location.administrativeNotes = Utilities.processText(locationAdminNotesFieldBy, location.administrativeNotes, Utilities.TextFieldType.LOCATION_ADMIN_NOTES, location.random, false);

        // next one is tricky.  Depends on Treatment Status, and maybe on the tier, or level/role
        // Further, not sure when it has values and when it doesn't.  Not required, though.  Or is it sometimes?  Maybe when INPATIENT?
        // If Treatment Status is Inpatient or Outpatient, then there will be values in dropdown,
        // otherwise not.
        if (location.treatmentStatus.equalsIgnoreCase("INPATIENT")
                || location.treatmentStatus.equalsIgnoreCase("OUTPATIENT")) {
            // Does this happen too soon, before dropdown gets populated?
            Utilities.sleep(1555); // servers slow in populating dropdown
            location.treatmentLocation = Utilities.processDropdown(locationTreatmentLocationDropdownBy, location.treatmentLocation, location.random, true); // false?
        }
        return true;
    }

}
