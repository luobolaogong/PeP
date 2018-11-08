package pep.patient;

import pep.patient.registration.*;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I understand that each patient "encounter" requires a visit to the "New Patient Reg." page.  At that point you
 * are presented with sections, and the top section is a Search For Patient.  I guess it's best to first enter the
 * patient's search info there to see if the patient's demographic information is already in the system.  If you don't
 * do a search first, and just enter the information, and then click on Submit, there's a chance you'll get an error
 * message saying you have to use the Update Patient page.  After filling in the info on New Patient Reg. and submitting,
 * you go to Patient Treatment pages to enter info.  These are basically more detailed info, and notes or whatever that
 * gets added to this "encounter".  To finish the encounter, you go to the "Update Patient" page, and enter the
 * Departure information.  This is not handled by this app.  This app currently only sets departure info when
 * New Patient Reg. is acted upon.  We therefore assume there is no patient that's sitting around, open.  We don't
 * account for that.
 *
 * I guess the usual way to input data for a patient encounter is to first do a New Patient Reg, then any number of
 * Patient Treatment pages, and then you finish it off by going to Update Patient and doing Departure.  But we're not
 * doing it that way currently.
 *
 * That would mean that this Patient class should contain an array of Treatment objects because one
 * encounter can have multiple treatments.
 *
 * Each encounter starts off with a New patient.Patient Reg, and ends up with an Update patient.Patient.  The
 * only difference between the two is that Update patient.Patient has the Departure object, and that's how
 * you end the encounter.
 *
 */
public class Patient {
    private static Logger logger = Logger.getLogger(Patient.class.getName()); // this should inherit from the pepPackageLogger
    private static Logger pepPackageLogger = Logger.getLogger("pep");
    public Boolean random; // true if want everything to be generated randomly, but subclasses can override.
    public PatientSearch patientSearch;
    public PatientState patientState; // this is going into the weps and waps output.  Wish it wasn't.  How to stop that?
    public PatientRegistration patientRegistration;
    public List<Treatment> treatments; // Each encounter can have multiple treatments

    public Patient() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.patientSearch = new PatientSearch();
            this.patientState = null; // this doesn't keep it from going to template because of setting in GSON
            this.patientRegistration = new PatientRegistration();

            this.treatments = Arrays.asList(new Treatment());
        }
    }

    // What if in the JSON file there was a PatientSearch person defined, but there was no registration section defined?
    // Then skip registration stuff.  But what if there was a "random": true at the head of Patient section?
    // In this case I'd think that a random Registration should be done, and a patient search should be done before
    // any Registration should be done, and maybe do New Patient Reg first, and if found, then jump to Update Patient,
    // and it would all be random.
    // Same situation for Treatments.  If no Treatments section was specified, but PatientSearch had been defined,
    // then skip Treatments.  But if there was a '"random": true', then do a Treatment using PatientSearch.
    //
    // If the command line said "-random 5" then I think all specified JSON files should be ignored.
    // I think, because maybe the user is just saying "Hey, I just want to update treatment for a
    // patient who is already in the system.
    public boolean process() {
        //logger.setLevel(Level.ALL); // test
        logger.entering("Patient", "process");
        logger.info("Starting to process a patient for timing purposes");
        // Okay, so Boolean acts like boolean except that it can also hold the value null.  And if it is null then you'll
        // get an NPE if you do   if (this.random == true)  because you're saying   if (null == true) and that's an NPE.
        // So never do that.  Prevent NPE from happening when it's null by setting this.random = parent's value.
        // What is the parent's value in this particular class?
        //
        // We're talking about sections here.  A section can be marked "random":true, or "random":false, or "random":null
        // or have a missing "random" anything.
        //
        // So for a section if "random" is missing, you inherit from parent.
        // If "random":false, then only required fields in the section that are missing a value should be randomized.
        // If "random":true then the section should have random values for all fields without a specified value.
        // If "random":null then it's the same as missing, and you inherit from parent.


        // This is new, experimental:
        // The PatientSearch section in the input file may not exist, or it may be filled with nulls.
        // We rely on that structure being available.  We want to make sure it's there and
        // populated(?) with something(?) before we start into Demographics or other sections.
        // The complication is when this is for a random patient, and there is no name or ssn.
        // And there's something going on when patients are loaded, to have this, but what if no patients are loaded, like "random:5"?
        if (this.patientSearch == null) { // Maybe necessary, not sure.  Can happen if -random 5
            this.patientSearch = new PatientSearch(); // prob unnec
        }

        boolean success;
        int nErrors = 0;
        if (this.patientRegistration != null || this.random == true) {
            success = processRegistration(); // I guess this method updates a global variable nErrors, so we don't bother with status return
            if (!success) {
                nErrors++;
            }
        }
        else {
            logger.fine("No registration information."); // okay if not doing any registration stuff but only treatments
            //logger.fine("No registration information.");
        }

        // Hey, if registration was skipped, better still have something in PatientSearch if we want to do Treatments
        // We want to do treatments only if there's a Treatments structure, or if this patient is marked random:true
        // Also, the new patient registration may have failed, and if that's true, maybe shouldn't do treatments.  Or at least quickly get out of it.
        if (this.treatments != null || this.random == true) { // this this.random thing is throwing a NPE somehow

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
        logger.exiting("Patient", "process");

        if (nErrors > 0) {
            return false;
        }
        return true;
    }

    // There should be a Registration class, but there isn't.
    boolean processRegistration() {
        boolean success = true; // was false
        int nErrors = 0;
        // Process each section in the following order:
        // PreRegistration, PreRegistrationArrivals, NewPatient Reg, UpdatePatient, PatientInformation.
        // If no data in those sections, they should be null.  (Currently not true.)
        // After that do treatments.
        //
        // But what if the New Patient Reg section is filled in and the search reveals the patient
        // is already in the system (has open registration or whatever), then what do you do?  Skip
        // it and expect the user to put the information in the UpdatePatient section?  Or do we
        // just switch to Update and use that NewPatientReg info?
        //
        // Any advantage in doing the search first?  Well, guess what?  Can't do search without using a reg page.
        // So can't do it here even if it would help.
        //
        if (this.patientRegistration != null) {
            if (this.patientRegistration.preRegistration != null) {
                this.patientState = PatientState.PRE; // new.  May help with Demographics and others
                success = processPreRegistration();
                this.patientState = PatientState.PRE_ARRIVAL; // nec? right?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.patientRegistration.preRegistrationArrivals != null) {
                this.patientState = PatientState.PRE_ARRIVAL; // new.  May help with Demographics and others
                success = processPreRegistrationArrivals(); // what after this?  change state to nothing?
                this.patientState = PatientState.NEW; // nec? right?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.patientRegistration.newPatientReg != null || this.random) {
                this.patientState = PatientState.NEW; // new.  May help with Demographics and others
                success = processNewPatientReg();
                this.patientState = PatientState.UPDATE; // nec? right?  Prob wrong.
                if (!success) {
                    nErrors++;
                }
            }
            if (this.patientRegistration.updatePatient != null) {
                this.patientState = PatientState.UPDATE; // new.  May help with Demographics and others
                success = processUpdatePatient();
                //this.patientState = PatientState.NO_STATE; // nec?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.patientRegistration.patientInformation != null) {
                this.patientState = PatientState.INFO; // new.  May help with Demographics and others
                success = processPatientInformation();
                if (!success) {
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        return true; // yeah I know this isn't right
    }


    public boolean processPreRegistration() {
        int nErrors = 0;
        PreRegistration preRegistration = this.patientRegistration.preRegistration;
        if (preRegistration == null) {
            preRegistration = new PreRegistration();
            this.patientRegistration.preRegistration = preRegistration;

        }
        if (preRegistration.random == null) {
            preRegistration.random = (this.random == null) ? false : this.random;
        }

        // Currently assuming we want to go to "New Patient Reg." page... but this should be decided inside process()
        boolean processSucceeded = preRegistration.process(this);
        if (!processSucceeded) {
            if (!Arguments.quiet) System.err.print("  ***New Patient Registration process failed ");
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

    public boolean processPreRegistrationArrivals() {
        int nErrors = 0;
        // unsure of logic here
        PreRegistrationArrivals preRegistrationArrivals = this.patientRegistration.preRegistrationArrivals;
        if (preRegistrationArrivals == null) {
            preRegistrationArrivals = new PreRegistrationArrivals();
            this.patientRegistration.preRegistrationArrivals = preRegistrationArrivals;

        }
        // I doubt we want any random going on here.
        if (preRegistrationArrivals.random == null) {
            preRegistrationArrivals.random = (this.random == null) ? false : this.random;
        }

        boolean processSucceeded = preRegistrationArrivals.process(this);
        return processSucceeded;
    }

    public boolean processNewPatientReg() {
        int nErrors = 0;
        NewPatientReg newPatientReg = this.patientRegistration.newPatientReg;
        if (newPatientReg == null) {
            newPatientReg = new NewPatientReg();
            this.patientRegistration.newPatientReg = newPatientReg;

        }
        if (newPatientReg.random == null) {
            newPatientReg.random = (this.random == null) ? false : this.random;
        }

        // Currently assuming we want to go to "New Patient Reg." page... but this should be decided inside process()
        boolean processSucceeded = newPatientReg.process(this);
        if (!processSucceeded) {
            if (!Arguments.quiet) System.err.print("  ***New Patient Registration process failed ");
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

    public boolean processUpdatePatient() {
        UpdatePatient updatePatient = this.patientRegistration.updatePatient;
        if (updatePatient == null) {
            updatePatient = new UpdatePatient();
            this.patientRegistration.updatePatient = updatePatient;

        }
        if (updatePatient.random == null) {
            updatePatient.random = (this.random == null) ? false : this.random;
        }

        boolean processSucceeded = updatePatient.process(this);
        if (!processSucceeded) {
            System.err.print("***Update Patient process failed ");
            if (this != null // looks wrong
                    && this.patientRegistration != null
                    && this.patientRegistration.updatePatient.demographics != null
                    && this.patientRegistration.updatePatient.demographics.firstName != null
                    && !this.patientRegistration.updatePatient.demographics.firstName.isEmpty()) {
                System.err.print("for " + this.patientRegistration.updatePatient.demographics.firstName + " " + this.patientRegistration.updatePatient.demographics.lastName + " ");
            }
            System.err.println("possibly because no patient was found to update, or possibly due to an error in patient registration information, or a slow or down server.  Skipping...");
            return false;
        }
        return true;
    }

    public boolean processPatientInformation() {
        int nErrors = 0;
        PatientInformation patientInformation = this.patientRegistration.patientInformation;
        if (patientInformation == null) {
            patientInformation = new PatientInformation();
            this.patientRegistration.patientInformation = patientInformation;

        }
        if (patientInformation.random == null) {
            patientInformation.random = (this.random == null) ? false : this.random;
        }
// is the above unnecessary?  Doing it inside process() below?  I don't think it's the same, no.  Keep it.
        //
        boolean processSucceeded = patientInformation.process(this);
        if (!processSucceeded) {
            //if (!Arguments.quiet) System.err.print("***New Patient Registration process failed.");
            if (!Arguments.quiet) System.err.println("  ***Patient Information failed.");
            return false;
        }
        return true;

    }


    // We only get here if there is a Treatments structure, or if the patient is marked random:true
    // And I think that if we say "-random 5" on command line, then each patient is marked random:true.
    boolean processTreatments() {
        int nErrors = 0;

        // check logic.
        List<Treatment> treatments = this.treatments;
        // It's possible that there is no Treatments structure and we got here because Patient was random:true
        if (treatments == null && this.random) {
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
                    treatment.random = (this.random == null) ? false : this.random; // right?
                    treatments.add(treatment);
                }
                this.treatments = treatments;
            }
            else {
                logger.fine("Not gunna do any treatments because percent is too low: " + percent);
            }
        }
        boolean success = true; // fix logic, was false
        if (treatments != null && treatments.size() > 0) { // can simplify to (treatments.size() > 0)?  I think so
            // Yes, we should be able to get here even if this.random is false
            for (Treatment treatment : treatments) {
                //System.out.println("Pep.process(), here comes a Treatment out a total of " + treatments.size());
                success = treatment.process(this, treatment);
                if (!success) {
                    nErrors++;
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
}
