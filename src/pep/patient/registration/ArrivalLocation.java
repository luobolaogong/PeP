package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

public class ArrivalLocation {
    public Boolean random; // true if want this section to be generated randomly
    public String arrivalDate; // MM/DD/YYYY
    public String arrivalTime; // HHMM
    public String status; // dropdown, 3 choices
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

    // Hey, is this section available for a Role 1 CASF?  Doesn't look like it.  How about other roles?
    public boolean process(Patient patient) {
        if (patient.patientRegistration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Arrival/Location for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }
        // I think this is an example of why I'll need to use an Abstract class, because I won't know which registration page has the information.
        // Is it from NewPatientReg, or UpdatePatient, or ...?  It should just be patient.registration.arrivalLocation.  And if
        // classes are extended, or if interfaces are used, how does that affect the GSON direct loading?
        //ArrivalLocation arrivalLocation = patient.patientRegistration.newPatientReg.arrivalLocation; // should exist.  set just before calling
        ArrivalLocation arrivalLocation = null;
        if (patient.patientState == PatientState.NEW && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.arrivalLocation != null) {
            arrivalLocation = patient.patientRegistration.newPatientReg.arrivalLocation;
        }
        if (patient.patientState == PatientState.UPDATE && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.arrivalLocation != null) {
            arrivalLocation = patient.patientRegistration.updatePatient.arrivalLocation;
        }

        // Do we even have an Arrival/Location section for this Role?
        // Do we even have an Arrival/Location section for this Role?
        // Do we even have an Arrival/Location section for this Role?
        // Do we even have an Arrival/Location section for this Role?


        // This next one sometimes fails, and the element isn't set, which causes an error.  Don't know why.  It just hangs.  Times out
        arrivalLocation.status = Utilities.processDropdown(arrivalLocationStatusBy, arrivalLocation.status, arrivalLocation.random, true);
        if (Arguments.debug) System.out.println("ArrivalLocation.process(), did the arrival location status dropdown selection and chose " + arrivalLocation.status);

        //
        // Arrival date should be the value specified by the user on the command line, or properties file, or PatientsJson file,
        // or in the JSON file.
        // Hmmm, I guess the user could also specify a range, but that shouldn't be advertised.  That is "random 1950-1960"
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
        return true;
    }
}
