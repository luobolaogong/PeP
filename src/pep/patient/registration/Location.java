package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.Pep.isSeamCode;

public class Location {
    private static Logger logger = Logger.getLogger(Location.class.getName());
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
            //this.random = null; // don't want this showing up in template
            this.treatmentStatus = "";
            this.roomNumberLocationInformation = "";
            this.treatmentLocation = "";
            this.administrativeNotes = "";
        }
        if (isSeamCode) {
            locationTreatmentStatusDropdownBy = LOCATION_TREATMENT_STATUS_DROPDOWN;
            locationRoomNumberFieldBy = LOCATION_ROOM_NUMBER_FIELD;
            locationTreatmentLocationDropdownBy = LOCATION_TREATMENT_LOCATION_DROPDOWN;
            locationAdminNotesFieldBy = LOCATION_ADMIN_NOTES_FIELD;
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Location for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        Location location = null;
        if (patient.patientState == PatientState.PRE && patient.patientRegistration.preRegistration != null && patient.patientRegistration.preRegistration.location != null) {
            location = patient.patientRegistration.preRegistration.location;
        }
        else if (patient.patientState == PatientState.NEW && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.location != null) {
            location = patient.patientRegistration.newPatientReg.location;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.location != null) {
            location = patient.patientRegistration.updatePatient.location;
        }


        location.treatmentStatus = Utilities.processDropdown(locationTreatmentStatusDropdownBy, location.treatmentStatus, location.random, true);

        location.roomNumberLocationInformation = Utilities.processText(locationRoomNumberFieldBy, location.roomNumberLocationInformation, Utilities.TextFieldType.HHMM, location.random, false);

        location.administrativeNotes = Utilities.processText(locationAdminNotesFieldBy, location.administrativeNotes, Utilities.TextFieldType.LOCATION_ADMIN_NOTES, location.random, false);

        // Treatment Location depends on Treatment Status, which must be INPATIENT or OUTPATIENT, otherwise not a visible element.
        // If Treatment Status is Inpatient or Outpatient, then there will be values in dropdown,
        // otherwise not.
        if (location.treatmentStatus.equalsIgnoreCase("INPATIENT")
                || location.treatmentStatus.equalsIgnoreCase("OUTPATIENT")) {
            // Does this happen too soon, before dropdown gets populated?
            Utilities.sleep(1555); // servers slow in populating dropdown
            location.treatmentLocation = Utilities.processDropdown(locationTreatmentLocationDropdownBy, location.treatmentLocation, location.random, false); // false on demo, on gold?
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true; // what?  Only true returned?
    }

}
