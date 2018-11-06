package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.logging.Logger;

public class Departure {
    private static Logger logger = Logger.getLogger(Departure.class.getName());
    public Boolean random; // true if want this section to be generated randomly -- change name to sectionToBeRandomized
    public String departureDate;
    public String disposition;
    //public String destination;
    public String dischargeNote;

    private static final By DEPARTURE_DATE_FIELD = By.xpath("//input[@id='formatDepartureDate']");
    private static final By patientRegistrationDispositionBy = By.id("patientRegistration.disposition");
    private static final By patientRegistrationDischargeNoteBy = By.id("patientRegistration.dischargeNote");

    public Departure() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.disposition = "";
            //this.destination = "";
            this.departureDate = "";
            this.dischargeNote = "";  // Is this new or is it only on New Patient Reg. ?
        }
    }

    public boolean process(Patient patient) {
        if (patient.patientRegistration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Departure ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Departure for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }

        // If Departure's disposition has a value, then a departure date is required, and if these fields
        // have values, then when the record is saved the patient's active status will change to inactive or closed.
        // Maybe it's also true that if you have a departure date you also need to have a disposition.
        // So either one requires the other.
        this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.random, false); // shouldn't be required
        this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.random, false);
        this.dischargeNote = Utilities.processText(patientRegistrationDischargeNoteBy, this.dischargeNote, Utilities.TextFieldType.DISCHARGE_NOTE, this.random, false);

        // Fix any discrepancy, because to save this page these fields have to work together.
        boolean hasDispositionFieldValue = this.disposition != null && !this.disposition.isEmpty() && !this.disposition.equalsIgnoreCase("Select Disposition");
        boolean hasDepartureDate = this.departureDate != null && !this.departureDate.isEmpty();
        // If either disposition or departure is provided, make sure have the other one.
        // If both exist, do nothing extra.  If neither exist, do nothing extra.
        if (hasDispositionFieldValue && !hasDepartureDate) {
            this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.random, true); // force true, right?
        }
        if (!hasDispositionFieldValue && hasDepartureDate) {
            this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.random, true); // force true, right?
        }

        if (Arguments.sectionPause > 0) {
            Utilities.sleep(Arguments.sectionPause * 1000);
        }

        return true;
    }

}
