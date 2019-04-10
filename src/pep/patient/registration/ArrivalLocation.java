package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

public class ArrivalLocation {
    private static Logger logger = Logger.getLogger(ArrivalLocation.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String arrivalDate;
    public String arrivalTime;
    public String status;
    public String pointOfInjury;
    public String originatingCamp;

    private static final By FLIGHT_ARRIVAL_DATE_FIELD = By.id("formatArrivalDate");
    private static final By FLIGHT_ARRIVAL_TIME_FIELD = By.id("formatArrivalTime");
    private static final By FLIGHT_ORIGINATING_CAMP_DROPDOWN = By.id("patientRegistration.origFacility");
    private static final By flightOriginatingCampDropdownBy = By.id("patientRegistration.pointOfInjury");
    private static final By arrivalLocationStatusBy = By.id("patientRegistration.wardBilletingId");


    public ArrivalLocation() {
        if (Arguments.template) {
            this.arrivalDate = "";
            this.arrivalTime = "";
            this.status = "";
            this.pointOfInjury = "";
            this.originatingCamp = "";
        }
    }

    /**
     * Process an Arrival Location section that's part of New Registration, or Update Patient.
     * @param patient The patient that's being registered
     * @return Success or Failure at filling in this section, although currently always returning success/true
     */
    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location at " + LocalTime.now() + " ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        }
        ArrivalLocation arrivalLocation = null;
        if (patient.patientState == PatientState.NEW && patient.registration.newPatientReg != null && patient.registration.newPatientReg.arrivalLocation != null) {
            arrivalLocation = patient.registration.newPatientReg.arrivalLocation;
        }
        if (patient.patientState == PatientState.UPDATE && patient.registration.updatePatient != null && patient.registration.updatePatient.arrivalLocation != null) {
            arrivalLocation = patient.registration.updatePatient.arrivalLocation;
        }
        arrivalLocation.status = Utilities.processDropdown(arrivalLocationStatusBy, arrivalLocation.status, arrivalLocation.randomizeSection, true);
        if (Arguments.date != null && (arrivalLocation.arrivalDate == null || arrivalLocation.arrivalDate.isEmpty())) {
            arrivalLocation.arrivalDate = Arguments.date;
        }
        arrivalLocation.arrivalDate = Utilities.processDate(FLIGHT_ARRIVAL_DATE_FIELD, arrivalLocation.arrivalDate, arrivalLocation.randomizeSection, true);
        arrivalLocation.arrivalTime = Utilities.processText(FLIGHT_ARRIVAL_TIME_FIELD, arrivalLocation.arrivalTime, Utilities.TextFieldType.HHMM, arrivalLocation.randomizeSection, true);

        // one of the following two is necessary.  Probably shouldn't have both, but the page logic doesn't prevent it
        // The problem is, at least one is required.  If we're here with random set, then we can just choose one of the two.
        // If either is specified, do it.  If neither are specified do one of them.
        if (arrivalLocation.pointOfInjury == null && arrivalLocation.originatingCamp == null) {
            if (Utilities.random.nextBoolean()) { // PointOfInjury or OriginalCamp is all that's required
                arrivalLocation.pointOfInjury = Utilities.processText(flightOriginatingCampDropdownBy, arrivalLocation.pointOfInjury, Utilities.TextFieldType.TITLE, arrivalLocation.randomizeSection, true);
            } else {
                arrivalLocation.originatingCamp = Utilities.processDropdown(FLIGHT_ORIGINATING_CAMP_DROPDOWN, arrivalLocation.originatingCamp, arrivalLocation.randomizeSection, true);
            }
        }
        else {
            if (arrivalLocation.pointOfInjury != null) {
                arrivalLocation.pointOfInjury = Utilities.processText(flightOriginatingCampDropdownBy, arrivalLocation.pointOfInjury, Utilities.TextFieldType.TITLE, arrivalLocation.randomizeSection, true);
            }
            if (arrivalLocation.originatingCamp != null) {
                arrivalLocation.originatingCamp = Utilities.processDropdown(FLIGHT_ORIGINATING_CAMP_DROPDOWN, arrivalLocation.originatingCamp, arrivalLocation.randomizeSection, true);
            }
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ArrivalLocation");
        }
        return true;
    }
}
