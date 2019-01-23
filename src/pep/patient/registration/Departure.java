package pep.patient.registration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

// The Departure section of Update Patient (and only Update Patient?) can be complicated.  The fields that show up,
// and the interaction required is based on the Disposition value, and maybe the status of the patient
// up to this point.

public class Departure {
    private static Logger logger = Logger.getLogger(Departure.class.getName());
    public Boolean random; // true if want this section to be generated randomly -- change name to sectionToBeRandomized
    public Boolean shoot;
    public String departureDate;
    public String disposition;
    public String destination; // This was here, then I took it out, now putting back in.  Not sure in what environments it's not there
    public String dischargeNote;

    private static final By DEPARTURE_DATE_FIELD = By.id("formatDepartureDate");
    private static final By patientRegistrationDispositionBy = By.id("patientRegistration.disposition");
    private static final By patientRegistrationDischargeNoteBy = By.id("patientRegistration.dischargeNote"); // when would this be used?  Pops up somewhere?
    private static final By destinationBy = By.id("destinationSearch");

    public Departure() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.disposition = "";
            //this.destination = ""; // looks like this is available on TEST tier, also prob DEMO tier, but not GOLD, but only for certain dispositions, eg DISCH MED HOLD PENDING RETIREMENT
            this.departureDate = "";
            this.dischargeNote = "";  // Is this new or is it only on New Patient Reg. ?
        }
    }

    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
            if (!Arguments.quiet) System.out.println("    Processing Departure ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Departure for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        }


        // On Gold, if Departure's disposition has a value, then a departure date is required, and vice versa!
        // And if these fields have values, then Destination is required (I think), and when the record
        // is saved the patient's active status will change to inactive or closed.
        this.disposition = Utilities.processDropdown(patientRegistrationDispositionBy, this.disposition, this.random, false); // shouldn't be required
        this.departureDate = Utilities.processDate(DEPARTURE_DATE_FIELD, this.departureDate, this.random, false);

        // Looks like Role 3 and Role 4 on TEST tier have different fields.  For Role 3 there's a Discharge Note.  For Role 4 I guess there's
        // a destination field.

////        By destinationBy = By.id("destinationSearch");
//        this.destination = Utilities.processText(destinationBy, this.destination, Utilities.TextFieldType.TITLE, this.random, false);
//
//        // For Update Patient, this next field doesn't seem to appear on the form.  Does it only pop up if you choose "discharge"?  No, doesn't seem so.
//        // Possible Departure is used elsewhere?
//        // Removed 11/26/18
//        //this.dischargeNote = Utilities.processText(patientRegistrationDischargeNoteBy, this.dischargeNote, Utilities.TextFieldType.DISCHARGE_NOTE, this.random, false);

        // check for destinationBy, and also for patientregistrationDischargeNoteBy
        boolean thisIsRole4 = false;
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(destinationBy));
            thisIsRole4 = true;
        }
        catch (Exception e) {
            logger.finest("Departure.process(), did not find destination element, which is probably okay for a role 3 e: " + Utilities.getMessageFirstLine(e));
        }



        // I think maybe the following is for Role 4 because has disposition field, I think.
        if (thisIsRole4) {
            this.destination = Utilities.processText(destinationBy, this.destination, Utilities.TextFieldType.TITLE, this.random, false);
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

            if (this.destination == null && (this.departureDate != null || this.disposition != null)) {
                this.destination = Utilities.processText(destinationBy, this.destination, Utilities.TextFieldType.TITLE, this.random, true);
            }
        }
        else { // role 3
            this.dischargeNote = Utilities.processText(patientRegistrationDischargeNoteBy, this.dischargeNote, Utilities.TextFieldType.DISCHARGE_NOTE, this.random, false);
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
