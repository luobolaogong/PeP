package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

/**
 * This class handles the Location section of a page.
 */
public class Location {
    private static Logger logger = Logger.getLogger(Location.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String treatmentStatus;
    public String roomNumberLocationInformation;
    public String treatmentLocation;
    public String administrativeNotes;

    private static final By LOCATION_TREATMENT_STATUS_DROPDOWN = By.id("patientRegistration.treatmentStatus");
    private static final By LOCATION_TREATMENT_LOCATION_DROPDOWN = By.id("patientRegistration.wardBilletingId");
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
            locationTreatmentLocationDropdownBy = LOCATION_TREATMENT_LOCATION_DROPDOWN;
        }
    }

    /**
     * Process the Location section of a page.
     * @param patient the patient for this location section
     * @return true if the processing of this section succeeded without error
     */
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

        location.treatmentStatus = Utilities.processDropdown(locationTreatmentStatusDropdownBy, location.treatmentStatus, location.randomizeSection, true);
        if (location.treatmentStatus == null || location.treatmentStatus.isEmpty()) {
            logger.fine("location.treatmentStatus is " + location.treatmentStatus);
        }
        location.roomNumberLocationInformation = Utilities.processText(locationRoomNumberFieldBy, location.roomNumberLocationInformation, Utilities.TextFieldType.HHMM, location.randomizeSection, false);
        location.administrativeNotes = Utilities.processText(locationAdminNotesFieldBy, location.administrativeNotes, Utilities.TextFieldType.LOCATION_ADMIN_NOTES, location.randomizeSection, false);

        // Treatment Location depends on Treatment Status, which must be INPATIENT or OUTPATIENT, otherwise not a visible element.
        // If Treatment Status is Inpatient or Outpatient, then there will be values in dropdown, otherwise not.
        if (location.treatmentStatus.equalsIgnoreCase("INPATIENT")
                || location.treatmentStatus.equalsIgnoreCase("OUTPATIENT")) {
            Utilities.sleep(1555, "Location.process(), will next process dropdown for treatment location");
            location.treatmentLocation = Utilities.processDropdown(locationTreatmentLocationDropdownBy, location.treatmentLocation, location.randomizeSection, false);
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }

        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Location");
        }
        return true;
    }
}
