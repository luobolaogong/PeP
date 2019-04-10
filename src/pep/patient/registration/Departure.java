package pep.patient.registration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * This class handles the departure section of New Patient and Update Patient registration pages.  This section has
 * some fields that change based on values selected in other fields on the page.
 */
public class Departure {
    private static Logger logger = Logger.getLogger(Departure.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String departureDate;
    public String disposition;
    public String destination;
    public String dischargeNote;

    private static final By DEPARTURE_DATE_FIELD = By.id("formatDepartureDate");
    private static final By patientRegistrationDispositionBy = By.id("patientRegistration.disposition");
    private static final By patientRegistrationDischargeNoteBy = By.id("patientRegistration.dischargeNote");
    private static final By destinationBy = By.id("destinationSearch");

    public Departure() {
        if (Arguments.template) {
            this.disposition = "";
            this.departureDate = "";
            this.dischargeNote = "";
        }
    }

    /**
     * Process this tricky departure section of the registration page.
     * @param patient The patient
     * @return Success or Failure of filling in the fields.  Actually currently it always returns true
     */
    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Departure at " + LocalTime.now() + " ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Departure at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        // On Gold, if Departure's disposition has a value, then a departure date is required, and vice versa!
        // And if these fields have values, then Destination is required (I think), and when the record
        // is saved the patient's active status will change to inactive or closed, and then certain pages won't
        // work because patient cannot be located.
        this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.randomizeSection, false); // shouldn't be required
        this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.randomizeSection, false);

        boolean thisIsRole4 = false;
        try {
            Utilities.waitForVisibility(destinationBy, 1, "Departure.process()");
            thisIsRole4 = true;
        }
        catch (Exception e) {
            logger.finest("Departure.process(), did not find destination element, which is probably okay for a role 3 e: " + Utilities.getMessageFirstLine(e));
        }
        if (thisIsRole4) {
            this.destination = Utilities.processText(destinationBy, this.destination, Utilities.TextFieldType.TITLE, this.randomizeSection, false);
            boolean hasDispositionFieldValue = this.disposition != null && !this.disposition.isEmpty() && !this.disposition.equalsIgnoreCase("Select Disposition");
            boolean hasDepartureDate = this.departureDate != null && !this.departureDate.isEmpty();
            if (hasDispositionFieldValue && !hasDepartureDate) {
                this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.randomizeSection, true); // force true, right?
            }
            if (!hasDispositionFieldValue && hasDepartureDate) {
                this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.randomizeSection, true); // force true, right?
            }
            if (this.destination == null && (this.departureDate != null || this.disposition != null)) {
                this.destination = Utilities.processText(destinationBy, this.destination, Utilities.TextFieldType.TITLE, this.randomizeSection, true);
            }
        }
        else {
            this.dischargeNote = Utilities.processText(patientRegistrationDischargeNoteBy, this.dischargeNote, Utilities.TextFieldType.DISCHARGE_NOTE, this.randomizeSection, false);
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Departure");
        }
        if (this.destination != null && !this.destination.isEmpty()) {
            logger.info("Hey, is this patient being departed to " + this.destination);
        }
        return true;
    }
}
