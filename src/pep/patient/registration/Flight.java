package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Driver.driver;

/**
 * This class is part of a registration page, but only for Role 4(?)
 */
public class Flight {
    private static Logger logger = Logger.getLogger(Flight.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String arrivalDate;
    public String arrivalTime;
    public String flightNumber;
    public String originatingCamp;
    public String classification;
    public String precedenceType;
    public FlightCommentsSection flightCommentsSection;

    private static By FLIGHT_ARRIVAL_DATE_FIELD = By.id("formatArrivalDate"); // seems right
    private static By FLIGHT_ARRIVAL_TIME_FIELD = By.id("formatArrivalTime");
    private static By FLIGHT_NUMBER_FIELD = By.id("patientRegistration.flightNumber");
    private static By FLIGHT_ORIGINATING_CAMP_DROPDOWN = By.id("patientRegistration.origFacility");
    private static By FLIGHT_CLASSIFICATION_DROPDOWN = By.id("patientRegistration.classification");
    private static By FLIGHT_PRECEDENCE_TYPE_DROPDOWN = By.id("patientRegistration.precedenceType");
    private static By FLIGHT_SHOW_COMMENTS_BUTTON = By.xpath("//span[@id='showComments']/input");
    private static By FLIGHT_AMBULATORY_CHECKBOX = By.id("patientRegistration.specialEquipment1");
    private static By FLIGHT_ATTENDANT_CHECKBOX = By.id("patientRegistration.specialEquipment2");
    private static By FLIGHT_BATTER_SUPPORT_UNIT_CHECKBOX = By.id("patientRegistration.specialEquipment3");
    private static By FLIGHT_CARDIAC_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment4");
    private static By FLIGHT_CCATT_CHECKBOX = By.id("patientRegistration.specialEquipment5");
    private static By FLIGHT_CHEST_TUBE_CHECKBOX = By.id("patientRegistration.specialEquipment6");
    private static By FLIGHT_FOLEY_CHECKBOX = By.id("patientRegistration.specialEquipment7");
    private static By FLIGHT_INCUBATOR_CHECKBOX = By.id("patientRegistration.specialEquipment8");
    private static By FLIGHT_IV_CHECKBOX = By.id("patientRegistration.specialEquipment9");
    private static By FLIGHT_LFC_CHECKBOX = By.id("patientRegistration.specialEquipment10");
    private static By FLIGHT_LITTER_FOLDING_CHECKBOX = By.id("patientRegistration.specialEquipment11");
    private static By FLIGHT_MATTRESS_LITTER_CHECKBOX = By.id("patientRegistration.specialEquipment12");
    private static By FLIGHT_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment13");
    private static By FLIGHT_NG_TUBE_CHECKBOX = By.id("patientRegistration.specialEquipment14");
    private static By FLIGHT_ORTHOPEDIC_CHECKBOX = By.id("patientRegistration.specialEquipment15");
    private static By FLIGHT_OTHER_CHECKBOX = By.id("patientRegistration.specialEquipment16");
    private static By FLIGHT_OXYGEN_ANALYZER_CHECKBOX = By.id("patientRegistration.specialEquipment17");
    private static By FLIGHT_PULSE_OXIMETER_CHECKBOX = By.id("patientRegistration.specialEquipment18");
    private static By FLIGHT_PUMP_INTRAVENEOUS_CHECKBOX = By.id("patientRegistration.specialEquipment19");
    private static By FLIGHT_RESTRAINTS_CHECKBOX = By.id("patientRegistration.specialEquipment21");
    private static By FLIGHT_RESTRAINT_SET_CHECKBOX = By.id("patientRegistration.specialEquipment20");
    private static By FLIGHT_STRAPS_WEBBING_CHECKBOX = By.id("patientRegistration.specialEquipment22");
    private static By FLIGHT_STYKER_FRAME_CHECKBOX = By.id("patientRegistration.specialEquipment23");
    private static By FLIGHT_SUCTION_APPARATUS_CHECKBOX = By.id("patientRegistration.specialEquipment24");
    private static By FLIGHT_SUCTION_CHECKBOX = By.id("patientRegistration.specialEquipment25");
    private static By FLIGHT_TRACH_CHECKBOX = By.id("patientRegistration.specialEquipment26");
    private static By FLIGHT_TRACTION_APPLIANCE_CHECKBOX = By.id("patientRegistration.specialEquipment27");
    private static By FLIGHT_TRACTION_CHECKBOX = By.id("patientRegistration.specialEquipment28");
    private static By FLIGHT_VENT_CHECKBOX = By.id("patientRegistration.specialEquipment29");
    private static By FLIGHT_VITAL_SIGNS_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment30");


    public Flight() {
        if (Arguments.template) {
            this.arrivalDate = "";
            this.arrivalTime = "";
            this.flightNumber = "";
            this.originatingCamp = "";
            this.classification = "";
            this.precedenceType = "";
            this.flightCommentsSection = new FlightCommentsSection();
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            FLIGHT_NUMBER_FIELD = By.id("patientRegistration.flightNumber");
            FLIGHT_ORIGINATING_CAMP_DROPDOWN = By.id("patientRegistration.origFacility");
            FLIGHT_CLASSIFICATION_DROPDOWN = By.id("patientRegistration.classification");
            FLIGHT_PRECEDENCE_TYPE_DROPDOWN = By.id("patientRegistration.precedenceType");
            FLIGHT_AMBULATORY_CHECKBOX = By.id("patientRegistration.specialEquipment26");
            FLIGHT_ATTENDANT_CHECKBOX = By.id("patientRegistration.specialEquipment30");
            FLIGHT_BATTER_SUPPORT_UNIT_CHECKBOX = By.id("patientRegistration.specialEquipment15");
            FLIGHT_CARDIAC_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment13");
            FLIGHT_CCATT_CHECKBOX = By.id("patientRegistration.specialEquipment27");
            FLIGHT_CHEST_TUBE_CHECKBOX = By.id("patientRegistration.specialEquipment10");
            FLIGHT_FOLEY_CHECKBOX = By.id("patientRegistration.specialEquipment8");
            FLIGHT_INCUBATOR_CHECKBOX = By.id("patientRegistration.specialEquipment3");
            FLIGHT_IV_CHECKBOX = By.id("patientRegistration.specialEquipment5");
            FLIGHT_LFC_CHECKBOX = By.id("patientRegistration.specialEquipment29");
            FLIGHT_LITTER_FOLDING_CHECKBOX = By.id("patientRegistration.specialEquipment22");
            FLIGHT_MATTRESS_LITTER_CHECKBOX = By.id("patientRegistration.specialEquipment23");
            FLIGHT_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment7");
            FLIGHT_NG_TUBE_CHECKBOX = By.id("patientRegistration.specialEquipment2");
            FLIGHT_ORTHOPEDIC_CHECKBOX = By.id("patientRegistration.specialEquipment9");
            FLIGHT_OTHER_CHECKBOX = By.id("patientRegistration.specialEquipment12");
            FLIGHT_OXYGEN_ANALYZER_CHECKBOX = By.id("patientRegistration.specialEquipment19");
            FLIGHT_PULSE_OXIMETER_CHECKBOX = By.id("patientRegistration.specialEquipment16");
            FLIGHT_PUMP_INTRAVENEOUS_CHECKBOX = By.id("patientRegistration.specialEquipment21");
            FLIGHT_RESTRAINTS_CHECKBOX = By.id("patientRegistration.specialEquipment11");
            FLIGHT_RESTRAINT_SET_CHECKBOX = By.id("patientRegistration.specialEquipment25");
            FLIGHT_STRAPS_WEBBING_CHECKBOX = By.id("patientRegistration.specialEquipment24");
            FLIGHT_STYKER_FRAME_CHECKBOX = By.id("patientRegistration.specialEquipment14");
            FLIGHT_SUCTION_APPARATUS_CHECKBOX = By.id("patientRegistration.specialEquipment18");
            FLIGHT_SUCTION_CHECKBOX = By.id("patientRegistration.specialEquipment1");
            FLIGHT_TRACH_CHECKBOX = By.id("patientRegistration.specialEquipment6");
            FLIGHT_TRACTION_APPLIANCE_CHECKBOX = By.id("patientRegistration.specialEquipment20");
            FLIGHT_TRACTION_CHECKBOX = By.id("patientRegistration.specialEquipment4");
            FLIGHT_VENT_CHECKBOX = By.id("patientRegistration.specialEquipment28");
            FLIGHT_VITAL_SIGNS_MONITOR_CHECKBOX = By.id("patientRegistration.specialEquipment17");
        }
    }

    /**
     * Process the Flight section of a registration page.
     * @param patient The patient for this Flight section
     * @return Success or Failure at filling in the section
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Flight for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        Flight flight = null;
        if (patient.patientState == PatientState.PRE && patient.registration.preRegistration != null && patient.registration.preRegistration.flight != null) {
            flight = patient.registration.preRegistration.flight;
        }
        else if (patient.patientState == PatientState.NEW && patient.registration.newPatientReg != null && patient.registration.newPatientReg.flight != null) {
            flight = patient.registration.newPatientReg.flight;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.registration.updatePatient != null && patient.registration.updatePatient.flight != null) {
            flight = patient.registration.updatePatient.flight;
        }
        // CASF units do NOT have anything in the flight section except "Orig. Facility" (or originating camp)
        // So the following will fail.  But according to email from Des to Richard and Sean, and a conversation with Des today 2/13/19,
        // CASF designated MTFs/units should not be tested, and therefore the registration pages corresponding to CASF units should be ignored for now.
        // No FB case has been submitted yet regarding this, as far as I know.
        try {
            Utilities.waitForVisibility(FLIGHT_ARRIVAL_DATE_FIELD, 1, "Checking if Flight section is NOT CASF.  CASF does not have arrival date.");
        }
        catch (Exception e) {
            logger.info("The facility associated with the user is probably CASF, and therefore does not have Flight fields other than one.  Skipping the rest.");
            logger.severe("CASF designated MTF/unit detected.  PeP should not be used for this kind of facility.  You should exit.");
            return true;
        }
        try {
            flight.arrivalDate = Utilities.processDate(FLIGHT_ARRIVAL_DATE_FIELD, flight.arrivalDate, flight.randomizeSection, true);
            flight.arrivalTime = Utilities.processText(FLIGHT_ARRIVAL_TIME_FIELD, flight.arrivalTime, Utilities.TextFieldType.HHMM, flight.randomizeSection, true);
            if (flight.flightNumber == null || flight.flightNumber.isEmpty()) { // I think flight numbers are generally 4 digit (wing?) number, so should adjust
                flight.flightNumber = patient.patientSearch.ssn.substring(5, 9); // 4 digits.  Is this better than two letters followed by 3 digits?
            }
            flight.flightNumber = Utilities.processText(FLIGHT_NUMBER_FIELD, flight.flightNumber, Utilities.TextFieldType.HHMM, flight.randomizeSection, true);
            flight.originatingCamp = Utilities.processDropdown(FLIGHT_ORIGINATING_CAMP_DROPDOWN, flight.originatingCamp, flight.randomizeSection, true);
            flight.classification = Utilities.processDropdown(FLIGHT_CLASSIFICATION_DROPDOWN, flight.classification, flight.randomizeSection, true);
            flight.precedenceType = Utilities.processDropdown(FLIGHT_PRECEDENCE_TYPE_DROPDOWN, flight.precedenceType, flight.randomizeSection, true);
        }
        catch (Exception e) {
            logger.severe("Flight.process(), couldn't process the non-comments section of the Flight section.  Error: " + Utilities.getMessageFirstLine(e));
        }

        if (driver.findElement(FLIGHT_SHOW_COMMENTS_BUTTON).isDisplayed()) {
            driver.findElement(FLIGHT_SHOW_COMMENTS_BUTTON).click();
        }
        FlightCommentsSection flightCommentsSection = flight.flightCommentsSection;
        if (flightCommentsSection == null) {
            flightCommentsSection = new FlightCommentsSection();
            flight.flightCommentsSection = flightCommentsSection;
        }
        if (flightCommentsSection.randomizeSection == null) {
            flightCommentsSection.randomizeSection = flight.randomizeSection;
        }
        if (flightCommentsSection.shoot == null) {
            flightCommentsSection.shoot = flight.shoot;
        }
        try {
            flightCommentsSection.ambulatory = Utilities.processBoolean(FLIGHT_AMBULATORY_CHECKBOX, flightCommentsSection.ambulatory, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.attendant = Utilities.processBoolean(FLIGHT_ATTENDANT_CHECKBOX, flightCommentsSection.attendant, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.batterySupportUnit = Utilities.processBoolean(FLIGHT_BATTER_SUPPORT_UNIT_CHECKBOX, flightCommentsSection.batterySupportUnit, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.cardiacMonitor = Utilities.processBoolean(FLIGHT_CARDIAC_MONITOR_CHECKBOX, flightCommentsSection.cardiacMonitor, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.ccatt = Utilities.processBoolean(FLIGHT_CCATT_CHECKBOX, flightCommentsSection.ccatt, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.chestTube = Utilities.processBoolean(FLIGHT_CHEST_TUBE_CHECKBOX, flightCommentsSection.chestTube, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.foley = Utilities.processBoolean(FLIGHT_FOLEY_CHECKBOX, flightCommentsSection.foley, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.incubator = Utilities.processBoolean(FLIGHT_INCUBATOR_CHECKBOX, flightCommentsSection.incubator, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.iv = Utilities.processBoolean(FLIGHT_IV_CHECKBOX, flightCommentsSection.iv, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.lfc = Utilities.processBoolean(FLIGHT_LFC_CHECKBOX, flightCommentsSection.lfc, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.litterFolding = Utilities.processBoolean(FLIGHT_LITTER_FOLDING_CHECKBOX, flightCommentsSection.litterFolding, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.mattressLitter = Utilities.processBoolean(FLIGHT_MATTRESS_LITTER_CHECKBOX, flightCommentsSection.mattressLitter, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.monitor = Utilities.processBoolean(FLIGHT_MONITOR_CHECKBOX, flightCommentsSection.monitor, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.ngTube = Utilities.processBoolean(FLIGHT_NG_TUBE_CHECKBOX, flightCommentsSection.ngTube, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.orthopedic = Utilities.processBoolean(FLIGHT_ORTHOPEDIC_CHECKBOX, flightCommentsSection.orthopedic, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.other = Utilities.processBoolean(FLIGHT_OTHER_CHECKBOX, flightCommentsSection.other, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.oxygenAnalyzer9Volt = Utilities.processBoolean(FLIGHT_OXYGEN_ANALYZER_CHECKBOX, flightCommentsSection.oxygenAnalyzer9Volt, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.pulseOximeter = Utilities.processBoolean(FLIGHT_PULSE_OXIMETER_CHECKBOX, flightCommentsSection.pulseOximeter, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.pumpIntraveneousInfusion = Utilities.processBoolean(FLIGHT_PUMP_INTRAVENEOUS_CHECKBOX, flightCommentsSection.pumpIntraveneousInfusion, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.restraintSetWristsAndAnkle = Utilities.processBoolean(FLIGHT_RESTRAINT_SET_CHECKBOX, flightCommentsSection.restraintSetWristsAndAnkle, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.restraints = Utilities.processBoolean(FLIGHT_RESTRAINTS_CHECKBOX, flightCommentsSection.restraints, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.strapsWebbing = Utilities.processBoolean(FLIGHT_STRAPS_WEBBING_CHECKBOX, flightCommentsSection.strapsWebbing, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.stykerFrame = Utilities.processBoolean(FLIGHT_STYKER_FRAME_CHECKBOX, flightCommentsSection.stykerFrame, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.suction = Utilities.processBoolean(FLIGHT_SUCTION_CHECKBOX, flightCommentsSection.suction, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.suctionApparatusContinuousIntermittent = Utilities.processBoolean(FLIGHT_SUCTION_APPARATUS_CHECKBOX, flightCommentsSection.suction, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.trach = Utilities.processBoolean(FLIGHT_TRACH_CHECKBOX, flightCommentsSection.trach, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.traction = Utilities.processBoolean(FLIGHT_TRACTION_CHECKBOX, flightCommentsSection.traction, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.tractionApplianceCervicalInjury = Utilities.processBoolean(FLIGHT_TRACTION_APPLIANCE_CHECKBOX, flightCommentsSection.tractionApplianceCervicalInjury, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.vent = Utilities.processBoolean(FLIGHT_VENT_CHECKBOX, flightCommentsSection.vent, flightCommentsSection.randomizeSection, false);
            flightCommentsSection.vitalSignsMonitor = Utilities.processBoolean(FLIGHT_VITAL_SIGNS_MONITOR_CHECKBOX, flightCommentsSection.vitalSignsMonitor, flightCommentsSection.randomizeSection, false);
        }
        catch (Exception e) {
            logger.severe("Flight.process(), Some kind of error occurred when trying to do checkboxes for the flight comments section: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Flight");
        }
        return true;
    }

}
