package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

// Not true: This is only in the Update Patient page
// This is only available in a Role3 situation, not Role4.  True?  I think so.
// What does this section mean to the state of a patient?  If departure info is provided,
// does that mean the patient is no longer an open patient?  No longer a patient?
// ??????????????
public class Departure {
    public Boolean random; // true if want this section to be generated randomly
    public String departureDate; // "mm/dd/yyyy";
    public String disposition; // "option ???";
    public String destination; // "text";   ?????  Where is this?
    public String dischargeNote;

    private static final By DEPARTURE_DATE_FIELD = By.xpath("//input[@id='formatDepartureDate']");
    private static final By patientRegistrationDispositionBy = By.id("patientRegistration.disposition");
    private static final By patientRegistrationDischargeNoteBy = By.id("patientRegistration.dischargeNote");

    public Departure() {
        if (Arguments.template) {
            this.random = null;
            this.disposition = "";
            this.destination = "";
            this.departureDate = "";
            this.dischargeNote = "";  // Is this new or is it only on New Patient Reg. ?
        }
    }

    public boolean process(Patient patient) {
        if (patient.patientRegistration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {           if (!Arguments.quiet) System.out.println("    Processing Departure ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Departure for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");
        }

        // Something seems wrong here.  Not like Location, or InjuryIllness
        this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.random, true);
        this.dischargeNote = Utilities.processText(patientRegistrationDischargeNoteBy, this.dischargeNote, Utilities.TextFieldType.DISCHARGE_NOTE, this.random, false);
        if (this.disposition != null && !this.disposition.isEmpty()) {
            this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.random, true);
        }
        return true;
    }

}
