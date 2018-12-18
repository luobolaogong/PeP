package pep.patient;

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

//import static pep.utilities.LoggingTimer.timerLogger;

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
    //private static Logger pepPackageLogger = Logger.getLogger("pep");
    public Boolean random; // true if want everything to be generated randomly, but subclasses can override.
    public Boolean shoot;
    public PatientSearch patientSearch;
    public PatientState patientState; // this is going into the weps and waps output.  Wish it wasn't.  How to stop that?
    public Registration registration; // name was changed from PatientRegistration
    public List<Treatment> treatments; // Each encounter can have multiple treatments
    public List<Summary> summaries; // Each encounter can have multiple Summary "Notes" (TBI and FacilityTreatmentHistory)
    public String encounterFileUrl; // new 11/19/18

    public Patient() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.patientSearch = new PatientSearch();
            //this.patientState = null; // this doesn't keep it from going to template because of setting in GSON
            this.registration = new Registration();
            this.treatments = Arrays.asList(new Treatment());
            this.summaries = Arrays.asList(new Summary());
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
        // Next line essentially useless because I modified the formatter.  The class and method do not emit.  Just the word "ENTRY" and possibly "[FINER]"
        // logger.entering("Patient", "process"); // emits "ENTRY" doesn't say anything about "Patient" or "process", prob because I modified the formatter and only emits when level is FINER or FINEST or ALL// Okay, so Boolean acts like boolean except that it can also hold the value null.  And if it is null then you'll


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
        if (this.registration != null || this.random == true) {
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

        if (this.summaries != null || this.random == true) { // this this.random thing is throwing a NPE somehow

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
        if (this.registration != null) {
            if (this.registration.preRegistration != null) {
                this.patientState = PatientState.PRE; // new.  May help with Demographics and others
                success = processPreRegistration();
                //this.patientState = PatientState.PRE_ARRIVAL; // nec? right?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.preRegistrationArrivals != null) {
                this.patientState = PatientState.PRE_ARRIVAL; // new.  May help with Demographics and others
                success = processPreRegistrationArrivals(); // what after this?  change state to nothing?
                //this.patientState = PatientState.NEW; // nec? right?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.newPatientReg != null || this.random) {
                this.patientState = PatientState.NEW; // new.  May help with Demographics and others
                success = processNewPatientReg();
                //this.patientState = PatientState.UPDATE; // nec? right?  Prob wrong.
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.updatePatient != null) {
                this.patientState = PatientState.UPDATE; // new.  May help with Demographics and others
                success = processUpdatePatient();
                //this.patientState = PatientState.NO_STATE; // nec?
                if (!success) {
                    nErrors++;
                }
            }
            if (this.registration.patientInformation != null) {
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
        PreRegistration preRegistration = this.registration.preRegistration;
        if (preRegistration == null) {
            preRegistration = new PreRegistration();
            this.registration.preRegistration = preRegistration;

        }
        if (preRegistration.random == null) {
            preRegistration.random = (this.random == null) ? false : this.random;
        }
        if (preRegistration.shoot == null) {
            preRegistration.shoot = (this.shoot == null) ? false : this.shoot;
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
        // unsure of logic here.  This PreRegistrationArrivals needs to be attached to a real Registration.  Ditto for PreRegistration
        PreRegistrationArrivals preRegistrationArrivals = this.registration.preRegistrationArrivals;
        if (preRegistrationArrivals == null) {
            preRegistrationArrivals = new PreRegistrationArrivals();
            this.registration.preRegistrationArrivals = preRegistrationArrivals;

        }
        // I doubt we want any random going on here.
        if (preRegistrationArrivals.random == null) {
            preRegistrationArrivals.random = (this.random == null) ? false : this.random;
        }
        if (preRegistrationArrivals.shoot == null) {
            preRegistrationArrivals.shoot = (this.shoot == null) ? false : this.shoot;
        }

        boolean processSucceeded = preRegistrationArrivals.process(this);
        return processSucceeded;
    }

    public boolean processNewPatientReg() {
        NewPatientReg newPatientReg = this.registration.newPatientReg;
        if (newPatientReg == null) {
            newPatientReg = new NewPatientReg();
            this.registration.newPatientReg = newPatientReg;

        }
        if (newPatientReg.random == null) {
            newPatientReg.random = (this.random == null) ? false : this.random;
        }
        if (newPatientReg.shoot == null) {
            newPatientReg.shoot = (this.shoot == null) ? false : this.shoot;
        }

        // Currently assuming we want to go to "New Patient Reg." page... but this should be decided inside process()
        boolean processSucceeded = newPatientReg.process(this);
        if (!processSucceeded) {
//            if (!Arguments.quiet) System.err.print("  ***New Patient Registration process failed ");
//            if (this.patientSearch != null
//                    && this.patientSearch.firstName != null && !this.patientSearch.firstName.isEmpty()
//                    && this.patientSearch.lastName != null && !this.patientSearch.lastName.isEmpty()
//                    && this.patientSearch.ssn != null && !this.patientSearch.ssn.isEmpty()) {
//                System.err.println("for " + this.patientSearch.firstName + " " + this.patientSearch.lastName + " ssn:" + this.patientSearch.ssn);

            if (this.patientSearch != null) {
                System.err.println("  ***New Patient Registration process failed for " +
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

    public boolean processUpdatePatient() {
        UpdatePatient updatePatient = this.registration.updatePatient;
        if (updatePatient == null) {
            updatePatient = new UpdatePatient();
            this.registration.updatePatient = updatePatient;

        }
        if (updatePatient.random == null) {
            updatePatient.random = (this.random == null) ? false : this.random;
        }
        if (updatePatient.shoot == null) {
            updatePatient.shoot = (this.shoot == null) ? false : this.shoot;
        }
        boolean processSucceeded = updatePatient.process(this);
        if (!processSucceeded) {
            System.err.print("  ***Update Patient process failed ");
            if (this != null // looks wrong
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
            //System.err.println("possibly because no patient was found to update, or possibly due to an error in patient registration information, or a slow or down server.  Skipping...");
            return false;
        }
        return true;
    }

    public boolean processPatientInformation() {
        int nErrors = 0;
        PatientInformation patientInformation = this.registration.patientInformation;
        if (patientInformation == null) {
            patientInformation = new PatientInformation();
            this.registration.patientInformation = patientInformation;

        }
        if (patientInformation.random == null) {
            patientInformation.random = (this.random == null) ? false : this.random;
        }
        if (patientInformation.shoot == null) {
            patientInformation.shoot = (this.shoot == null) ? false : this.shoot;
        }
// is the above unnecessary?  Doing it inside process() below?  I don't think it's the same, no.  Keep it.
        //
        // Seems that if Update Patient is performed before doing Patient Information, Patient Information's Search For Patient cannot find the patient.
        // At least some of the time, if not often, or if not always.  Not sure.  Doesn't seem to be a timing thing.
        // And yet without changing later, doing a search by hand for Patient Information, will work.
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
                    treatment.shoot = (this.shoot == null) ? false : this.shoot; // right?
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

    boolean processSummaries() {
        int nErrors = 0;

        // check logic.
        List<Summary> summaries = this.summaries;
        // It's possible that there is no Summaries structure and we got here because Patient was random:true
        if (summaries == null && this.random) {
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
                    summary.random = (this.random == null) ? false : this.random; // right?
                    summary.shoot = (this.shoot == null) ? false : this.shoot; // right?
                    summaries.add(summary);
                }
                this.summaries = summaries;
            }
            else {
                logger.fine("Not gunna do any summaries because percent is too low: " + percent);
            }
        }
        boolean success = true; // fix logic, was false
        if (summaries != null && summaries.size() > 0) { // can simplify to (summaries.size() > 0)?  I think so or just summaries != null ?
            // Yes, we should be able to get here even if this.random is false
            for (Summary summary : summaries) {
                //System.out.println("Pep.process(), here comes a Summary out a total of " + summaries.size());
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
