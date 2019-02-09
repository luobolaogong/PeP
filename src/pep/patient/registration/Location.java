package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

public class Location {
    private static Logger logger = Logger.getLogger(Location.class.getName());
    public Boolean random;
    public Boolean shoot;
    public String treatmentStatus; // "option 1-3, required";
    public String roomNumberLocationInformation; // "text";
    public String treatmentLocation; // "text";
    public String administrativeNotes; // "text";

    private static final By LOCATION_TREATMENT_STATUS_DROPDOWN = By.id("patientRegistration.treatmentStatus");
    private static final By LOCATION_TREATMENT_LOCATION_DROPDOWN = By.id("patientRegistration.wardBilletingId");
    //private static final By LOCATION_ROOM_NUMBER_FIELD = By.xpath("//input[@name='patientRegistration.roomNumber']");
    //private static final By LOCATION_ADMIN_NOTES_FIELD = By.xpath("//textarea[@name='patientRegistration.notes']");


    private static By locationTreatmentStatusDropdownBy = By.id("patientRegistration.treatmentStatus");
    private static By locationRoomNumberFieldBy = By.id("patientRegistration.roomNumber");
    private static By locationTreatmentLocationDropdownBy = By.id("patientRegistration.wardBilletingId"); // verified
    private static By locationAdminNotesFieldBy = By.id("patientRegistration.notes");


    public Location() {
        if (Arguments.template) {
            this.treatmentStatus = "";
            this.roomNumberLocationInformation = "";
            this.treatmentLocation = "";
            this.administrativeNotes = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            locationTreatmentStatusDropdownBy = LOCATION_TREATMENT_STATUS_DROPDOWN;
            //locationRoomNumberFieldBy = LOCATION_ROOM_NUMBER_FIELD;
            locationTreatmentLocationDropdownBy = LOCATION_TREATMENT_LOCATION_DROPDOWN;
            //locationAdminNotesFieldBy = LOCATION_ADMIN_NOTES_FIELD;
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Location for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        Location location = null;
        if (patient.patientState == PatientState.PRE && patient.registration.preRegistration != null && patient.registration.preRegistration.location != null) {
            location = patient.registration.preRegistration.location;
        }
        else if (patient.patientState == PatientState.NEW && patient.registration.newPatientReg != null && patient.registration.newPatientReg.location != null) {
            location = patient.registration.newPatientReg.location;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.registration.updatePatient != null && patient.registration.updatePatient.location != null) {
            location = patient.registration.updatePatient.location;
        }


        location.treatmentStatus = Utilities.processDropdown(locationTreatmentStatusDropdownBy, location.treatmentStatus, location.random, true);
        if (location.treatmentStatus == null || location.treatmentStatus.isEmpty()) {
            logger.fine("location.treatmentStatus is " + location.treatmentStatus);
        }
        location.roomNumberLocationInformation = Utilities.processText(locationRoomNumberFieldBy, location.roomNumberLocationInformation, Utilities.TextFieldType.HHMM, location.random, false);

        location.administrativeNotes = Utilities.processText(locationAdminNotesFieldBy, location.administrativeNotes, Utilities.TextFieldType.LOCATION_ADMIN_NOTES, location.random, false);

        // Treatment Location depends on Treatment Status, which must be INPATIENT or OUTPATIENT, otherwise not a visible element.
        // If Treatment Status is Inpatient or Outpatient, then there will be values in dropdown,
        // otherwise not.
        if (location.treatmentStatus.equalsIgnoreCase("INPATIENT")
                || location.treatmentStatus.equalsIgnoreCase("OUTPATIENT")) {
            // Does this happen too soon, before dropdown gets populated?
            Utilities.sleep(1555, "Location.process(), will next process dropdown for treatment location"); // servers slow in populating dropdown
            location.treatmentLocation = Utilities.processDropdown(locationTreatmentLocationDropdownBy, location.treatmentLocation, location.random, false); // false on demo, on gold?
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }

        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Location");
        }
        return true; // what?  Only true returned?
    }

}
