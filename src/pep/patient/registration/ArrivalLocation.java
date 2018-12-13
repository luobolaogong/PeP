package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

public class ArrivalLocation {
    private static Logger logger = Logger.getLogger(ArrivalLocation.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String arrivalDate;
    public String arrivalTime;
    public String status;
    public String pointOfInjury;
    // or
    public String originatingCamp;

    private static final By FLIGHT_ARRIVAL_DATE_FIELD = By.xpath("//input[@id='formatArrivalDate']");
    private static final By FLIGHT_ARRIVAL_TIME_FIELD = By.xpath("//input[@id='formatArrivalTime']");
    private static final By FLIGHT_ORIGINATING_CAMP_DROPDOWN = By.xpath("//select[@id='patientRegistration.origFacility']");
    private static final By flightOriginatingCampDropdownBy = By.id("patientRegistration.pointOfInjury");
    private static final By arrivalLocationStatusBy = By.id("patientRegistration.wardBilletingId");


    public ArrivalLocation() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.arrivalDate = "";
            this.arrivalTime = "";
            this.status = "";
            this.pointOfInjury = "";
            this.originatingCamp = "";
        }
    }

    // Is this section available for a Role 1 CASF?  Doesn't look like it.  How about other roles?
    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location for patient" +
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

        // Do we even have an Arrival/Location section for this Role?

        // This next one sometimes fails, and the element isn't set, which causes an error.  Don't know why.  It just hangs.  Times out
        arrivalLocation.status = Utilities.processDropdown(arrivalLocationStatusBy, arrivalLocation.status, arrivalLocation.random, true);

        //
        // Arrival date should be the value specified by the user on the command line, or properties file, or PatientsJson file,
        // or in the JSON file.
        // Can date still take a range?
        //
        if (Arguments.date != null && (arrivalLocation.arrivalDate == null || arrivalLocation.arrivalDate.isEmpty())) {
            arrivalLocation.arrivalDate = Arguments.date;
        }
        arrivalLocation.arrivalDate = Utilities.processDate(FLIGHT_ARRIVAL_DATE_FIELD, arrivalLocation.arrivalDate, arrivalLocation.random, true);
        arrivalLocation.arrivalTime = Utilities.processText(FLIGHT_ARRIVAL_TIME_FIELD, arrivalLocation.arrivalTime, Utilities.TextFieldType.HHMM, arrivalLocation.random, true);

        // one of the following two is necessary.  Probably shouldn't have both, but the page logic doesn't prevent it
        // The problem is, at least one is required.  If we're here with random set, then we can just choose one of the two.
        // If either is specified, do it.  If neither are specified do one of them.
        // Not 100% sure of the logic here.  New as of 10/02/18:
        if (arrivalLocation.pointOfInjury == null && arrivalLocation.originatingCamp == null) {
            if (Utilities.random.nextBoolean()) { // PointOfInjury or OriginalCamp is all that's required
                arrivalLocation.pointOfInjury = Utilities.processText(flightOriginatingCampDropdownBy, arrivalLocation.pointOfInjury, Utilities.TextFieldType.TITLE, arrivalLocation.random, true);
            } else {
                arrivalLocation.originatingCamp = Utilities.processDropdown(FLIGHT_ORIGINATING_CAMP_DROPDOWN, arrivalLocation.originatingCamp, arrivalLocation.random, true);
            }
        }
        else {
            if (arrivalLocation.pointOfInjury != null) {
                arrivalLocation.pointOfInjury = Utilities.processText(flightOriginatingCampDropdownBy, arrivalLocation.pointOfInjury, Utilities.TextFieldType.TITLE, arrivalLocation.random, true);
            }
            if (arrivalLocation.originatingCamp != null) {
                arrivalLocation.originatingCamp = Utilities.processDropdown(FLIGHT_ORIGINATING_CAMP_DROPDOWN, arrivalLocation.originatingCamp, arrivalLocation.random, true);
            }
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }

        return true;
    }
}
