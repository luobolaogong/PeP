package pep.patient;

import pep.TmdsPortal;
import pep.patient.registration.*;
import pep.patient.registration.newpatient.NewPatientReg;
import pep.patient.registration.patientinformation.PatientInformation;
import pep.patient.registration.preregistration.PreRegistration;
import pep.patient.registration.preregistrationarrivals.PreRegistrationArrivals;
import pep.patient.registration.updatepatient.UpdatePatient;
import pep.patient.summary.Summary;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * This class handles the processing subtasks of patient registration, treatment, and summary pages.
 * A Patient object knows about its registration, treatments, and summaries.  It also knows if a new user was specified
 * for use over whatever the previous user was.
 * This class should probably be reorganized.
 */
public class Patient {
    private static Logger logger = Logger.getLogger(Patient.class.getName()); // this should inherit from the pepPackageLogger
    public Boolean random; // true if want everything (including optional field values?) to be generated randomly, but subclasses can override.
    public Boolean shoot;
    public String user; // optional new user, with associated password in properties file
    public PatientSearch patientSearch;
    public PatientState patientState;
    public Registration registration;
    public List<Treatment> treatments;
    public List<Summary> summaries; // Each encounter can have multiple Summary "Notes" (TBI and FacilityTreatmentHistory)
    public String encounterFileUrl;

    public Patient() {
        if (Arguments.template) {
            this.patientSearch = new PatientSearch();
            this.patientState = null; // unfortunately goes to template because of setting in GSON
            this.registration = new Registration();
            this.treatments = Arrays.asList(new Treatment());
            this.summaries = Arrays.asList(new Summary());
        }
    }

    /**
     * Process registration, treatment, and summary information, and switch user if specified.
     * @return success or failure of this or subcalls
     */
    public boolean process() {
        if (this.patientSearch == null) { // Can happen if -random 5
            this.patientSearch = new PatientSearch();
        }
        // If there's an optional user specified, it means it's time to switch users by logging
        // out and logging back in with the new user's credentials.  The user name and password would be in
        // a properties file read at startup.
        if (this.user != null) {
            boolean success = TmdsPortal.switchUsers(this.user);
            if (!success) {
                logger.info("User swap attempted but failed.");
                if (!Arguments.quiet) {
                    System.out.println("User swap failed.  Could not login as " + this.user + "  Skipping this patient.");
                    return false;
                }
            }
            else {
                if (!Arguments.quiet) {
                    System.out.println("Switched TMDS user to " + this.user);
                    logger.fine("Switched TMDS user: " + this.user);
                }
            }
        }

        // Process registration if the registration object was created by GSON reading the input JSON file and determining
        // that the JSON object contained specified information in those sections, or the section is marked random which means we
        // at least want the required fields in the registration page to have values.
        boolean success;
        int nErrors = 0;
        if (this.registration != null || (this.random != null && this.random == true)) {
            success = processRegistration(); // I guess this method updates a global variable nErrors, so we don't bother with status return
            if (!success) {
                nErrors++;
            }
        }
        else {
            logger.fine("No registration information."); // okay if not doing any registration stuff but only treatments
            //logger.fine("No registration information.");
        }

        // Process treatments if the treatments object was created by GSON reading the input JSON file and determining
        // that the JSON object contained specified information in those sections, or the section is marked random which means we
        // at least want the required fields in the registration page to have values.
        if (this.treatments != null || (this.random != null && this.random == true)) {
            if (this.patientSearch == null) {
                logger.fine("No patient search for this patient.  Not going to look for it in a registration.  We cannot continue with Treatments.");
                return false;
            }
            if (this.patientSearch.firstName == null
                    && this.patientSearch.lastName == null
                    && this.patientSearch.ssn == null
                    && this.patientSearch.traumaRegisterNumber == null) {
                logger.fine("Can't continue with Treatment information without a patient.");
                return false;
            }
            if (
                    (this.patientSearch.firstName == null || this.patientSearch.firstName.isEmpty()) &&
                            (this.patientSearch.lastName == null || this.patientSearch.lastName.isEmpty()) &&
                            (this.patientSearch.ssn == null || this.patientSearch.ssn.isEmpty()) &&
                            (this.patientSearch.traumaRegisterNumber == null || this.patientSearch.traumaRegisterNumber.isEmpty())
            ) {
                logger.fine("Not even one element we can possibly use.  Not continuing with Treatments");
                return false;
            }

            success = processTreatments(); // I guess this method updates a global variable nErrors, so we don't bother with status return
            if (!success) {
                nErrors++;
            }
        }

        // Process summaries if the summaries object was created by GSON reading the input JSON file and determining
        // that the JSON object contained specified information in those sections, or the section is marked random which means we
        // at least want the required fields in the registration page to have values.
        if (this.summaries != null || (this.random != null && this.random == true)) { // this this.random thing is throwing a NPE somehow

            if (this.patientSearch == null) {
                logger.fine("No patient search for this patient.  Not going to look for it in a registration.  We cannot continue with Treatments.");
                return false;
            }
            if ((this.patientSearch.firstName == null || this.patientSearch.firstName.isEmpty())
                    && (this.patientSearch.lastName == null || this.patientSearch.lastName.isEmpty())
                    && (this.patientSearch.ssn == null || this.patientSearch.ssn.isEmpty())
                    && (this.patientSearch.traumaRegisterNumber == null || this.patientSearch.traumaRegisterNumber.isEmpty())) {
                logger.fine("Can't continue with Patient Summary information without a specified patient.");
                return false;
            }

            success = processSummaries(); // I guess this method updates a global variable nErrors, so we don't bother with status return
            if (!success) {
                nErrors++;
            }
        }
        return (nErrors > 0);
    }

    /**
     * Process one or more of the types of registration depending on what the user specified in the input file.
     * Some combinations would not make sense so PatientState is used to help make sense.  There should
     * probably have been a Registration class with subclasses for the types.  The order of these types
     * is somewhat logical in the order a patient might progress.
     * @return success or failure of registration efforts in the type of registration.
     */
    boolean processRegistration() {
        boolean success;
        int nErrors = 0;
        if (this.registration != null) {
            if (this.registration.preRegistration != null) {
                this.patientState = PatientState.PRE;
                success = processPreRegistration();
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.preRegistrationArrivals != null) {
                this.patientState = PatientState.PRE_ARRIVAL;
                success = processPreRegistrationArrivals();
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.newPatientReg != null || (this.random != null && this.random == true)) {
                this.patientState = PatientState.NEW;
                success = processNewPatientReg();
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.updatePatient != null) {
                this.patientState = PatientState.UPDATE;
                success = processUpdatePatient();
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.patientInformation != null) {
                this.patientState = PatientState.INFO;
                success = processPatientInformation();
                if (!success) {
                    nErrors++;
                }
            }
        }
        return (nErrors > 0);
    }

    /**
     * Process a pre-registration page.
     * @return success or failure
     */
    private boolean processPreRegistration() {
        PreRegistration preRegistration = this.registration.preRegistration;
        if (preRegistration == null) {
            preRegistration = new PreRegistration();
            this.registration.preRegistration = preRegistration;

        }
        if (preRegistration.random == null) {
            preRegistration.random = this.random;
        }
        if (preRegistration.shoot == null) {
            preRegistration.shoot = this.shoot;
        }

        boolean processSucceeded = preRegistration.process(this);
        if (!processSucceeded) {
            if (Arguments.verbose) System.err.print("    ***Pre-registration processing failed "); // changed indentation to 2 more.  Right?
            if (this.patientSearch != null
                    && this.patientSearch.firstName != null && !this.patientSearch.firstName.isEmpty()
                    && this.patientSearch.lastName != null && !this.patientSearch.lastName.isEmpty()
                    && this.patientSearch.ssn != null && !this.patientSearch.ssn.isEmpty()) {
                System.err.println("for " + this.patientSearch.firstName + " " + this.patientSearch.lastName + " ssn:" + this.patientSearch.ssn);
            }
            else {
                System.err.println();
            }
            return false;
        }
        return true;

    }

    /**
     * Process a pre-registration arrivals page.
     * @return success or failure
     */
    private boolean processPreRegistrationArrivals() {
        PreRegistrationArrivals preRegistrationArrivals = this.registration.preRegistrationArrivals;
        if (preRegistrationArrivals == null) {
            preRegistrationArrivals = new PreRegistrationArrivals();
            this.registration.preRegistrationArrivals = preRegistrationArrivals;
        }
        if (preRegistrationArrivals.random == null) {
            preRegistrationArrivals.random = this.random;
        }
        if (preRegistrationArrivals.shoot == null) {
            preRegistrationArrivals.shoot = this.shoot;
        }

        boolean processSucceeded = preRegistrationArrivals.process(this);
        return processSucceeded;
    }

    /**
     * Process a new patient registration page.
     * @return success or failure
     */
    private boolean processNewPatientReg() {
        NewPatientReg newPatientReg = this.registration.newPatientReg;
        if (newPatientReg == null) {
            newPatientReg = new NewPatientReg();
            this.registration.newPatientReg = newPatientReg;
        }
        if (newPatientReg.random == null) {
            newPatientReg.random = this.random;
        }
        if (newPatientReg.shoot == null) {
            newPatientReg.shoot = this.shoot;
        }

        boolean processSucceeded = newPatientReg.process(this);
        if (!processSucceeded) {
            if (this.patientSearch != null) {
                System.err.println("    ***New Patient Registration processing failed for patient" +
                        (this.patientSearch.firstName.isEmpty() ? "" : (" " + this.patientSearch.firstName)) +
                        (this.patientSearch.lastName.isEmpty() ? "" : (" " + this.patientSearch.lastName)) +
                        (this.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + this.patientSearch.ssn))
                );
            }
            else {
                System.err.println();
            }
            return false;
        }
        return true;
    }

    /**
     * Process an update patient page.
     * @return success or failure
     */
    private boolean processUpdatePatient() {
        UpdatePatient updatePatient = this.registration.updatePatient;
        if (updatePatient == null) {
            updatePatient = new UpdatePatient();
            this.registration.updatePatient = updatePatient;

        }
        if (updatePatient.random == null) {
            updatePatient.random = this.random;
        }
        if (updatePatient.shoot == null) {
            updatePatient.shoot = this.shoot;
        }
        boolean processSucceeded = updatePatient.process(this);
        if (!processSucceeded) {
            System.err.print("  ***Update Patient processing failed ");
            if (this != null // this should be reworked
                    && this.registration != null
                    && this.registration.updatePatient.demographics != null
                    && this.registration.updatePatient.demographics.firstName != null
                    && !this.registration.updatePatient.demographics.firstName.isEmpty()
                    && !this.registration.updatePatient.demographics.firstName.equalsIgnoreCase("random")
                    && this.registration.updatePatient.demographics.lastName != null
                    && !this.registration.updatePatient.demographics.lastName.isEmpty()
                    && !this.registration.updatePatient.demographics.lastName.equalsIgnoreCase("random")
            ) {
                System.err.print("for " + this.registration.updatePatient.demographics.firstName + " " + this.registration.updatePatient.demographics.lastName + " ");
            }
            System.err.println();
            return false;
        }
        return true;
    }

    /**
     * Process the patient information page.
     * @return success or failure
     */
    private boolean processPatientInformation() {
        PatientInformation patientInformation = this.registration.patientInformation;
        if (patientInformation == null) {
            patientInformation = new PatientInformation();
            this.registration.patientInformation = patientInformation;

        }
        if (patientInformation.random == null) {
            patientInformation.random = this.random; // removed setting to false if null
        }
        if (patientInformation.shoot == null) {
            patientInformation.shoot = this.shoot;
        }
        // Is it possible that the patient was departed?????
        boolean processSucceeded = patientInformation.process(this);
        if (!processSucceeded) {
            if (Arguments.verbose) System.err.println("  ***Patient Information failed.");
            return false;
        }
        return true;

    }


    /**
     * Start the processing of treatment pages.
     * @return success or failure
     */
    private boolean processTreatments() {
        int nErrors = 0;

        List<Treatment> treatments = this.treatments;
        if (treatments == null && (this.random != null && this.random == true)) {
            // Doing a random number of 0 to 3 for the number of treatments is getting 0 way too often.  Most of the time (50%)
            // we'll want 1 treatment.  Sometimes (40%) 2.  Less rarely (10%) 0.  3 is too many.
            int nTreatments;
            int percent = Utilities.random.nextInt(100);
            if (percent > 60) {
                nTreatments = 1;
            }
            else if (percent > 30) {
                nTreatments = 2;
            }
            else if (percent > 10) {
                nTreatments = 0;
            }
            else {
                nTreatments = 3;
            }
            if (nTreatments > 0) {
                treatments = new ArrayList<Treatment>(nTreatments);
                for (int ctr = 0; ctr < nTreatments; ctr++) {
                    Treatment treatment = new Treatment();
                    treatment.random = this.random;
                    treatment.shoot = this.shoot;
                    treatments.add(treatment);
                }
                this.treatments = treatments;
            }
            else {
                logger.fine("Not gunna do any treatments because percent is too low: " + percent);
            }
        }
        boolean success = true;
        if (treatments != null && treatments.size() > 0) { // can simplify to (treatments.size() > 0)?  I think so
            for (Treatment treatment : treatments) {
                success = treatment.process(this, treatment);
                if (!success) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("  ***Failed to process Treatment for " + this.patientSearch.firstName + " " + this.patientSearch.lastName + " ssn:" + this.patientSearch.ssn);
                }
            }
        }
        else {
            logger.fine("Did not to treatments.  Why?  treatments: " + treatments);
        }
        if (nErrors > 0) {
            success = false;
        }

        return success;
    }

    /**
     * Process the summaries page.
     * @return success or failure
     */
    private boolean processSummaries() {
        int nErrors = 0;

        List<Summary> summaries = this.summaries;
        if (summaries == null && (this.random != null && this.random == true)) {
            // Doing a random number of 0 to 3 for the number of summaries is getting 0 way too often.  Most of the time (50%)
            // we'll want 1 summary.  Sometimes (40%) 2.  Less rarely (10%) 0.  3 is too many.
            int nSummaries;
            int percent = Utilities.random.nextInt(100);
            if (percent > 60) {
                nSummaries = 1;
            }
            else if (percent > 20) {
                nSummaries = 2;
            }
            else {
                nSummaries = 3;
            }
            if (nSummaries > 0) {
                summaries = new ArrayList<Summary>(nSummaries);
                for (int ctr = 0; ctr < nSummaries; ctr++) {
                    Summary summary = new Summary();
                    summary.random = this.random;
                    summary.shoot = this.shoot;
                    summaries.add(summary);
                }
                this.summaries = summaries;
            }
            else {
                logger.fine("Not gunna do any summaries because percent is too low: " + percent);
            }
        }
        boolean success = true;
        if (summaries != null && summaries.size() > 0) { // can simplify to (summaries.size() > 0)?  I think so or just summaries != null ?
            for (Summary summary : summaries) {
                success = summary.process(this, summary);
                if (!success) {
                    nErrors++;
                }
            }
        }
        else {
            logger.fine("Did not to summaries.  Why?  summaries: " + summaries);
        }
        if (nErrors > 0) {
            success = false;
        }

        return success;
    }

}
