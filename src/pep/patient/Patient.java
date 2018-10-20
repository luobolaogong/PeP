package pep.patient;

import org.apache.xpath.Arg;
import pep.Pep;
import pep.patient.registration.NewPatientReg;
import pep.patient.registration.PatientInformation;
import pep.patient.registration.PatientRegistration;
//import pep.patient.registration.Registration;
import pep.patient.registration.UpdatePatient;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

            //this.patientRegistration.process(this);


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
        //System.out.println("Hey, at this point I think maybe this.random should not be null, because tests don't work");
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
            if (Arguments.debug) System.out.println("No registration information.");
        }

        // Hey, if registration was skipped, better still have something in PatientSearch
//        System.out.println("Missing PatientSearch values even though did PatientInformation, (and New Pat Reg failed)");
//        System.out.println("Should stop here to check patientSearch.  Can't continue without it.");

        if (this.patientSearch == null) {
            if (Arguments.debug) System.out.println("No patient search for this patient.  Not going to look for it in a registration.  We cannot continue with Treatments.");
            return false;
        }
        if (this.patientSearch.firstName == null && this.patientSearch.lastName == null && this.patientSearch.ssn == null && this.patientSearch.traumaRegisterNumber == null) {
            if (Arguments.debug) System.out.println("Can't continue with Treatment information without a patient.");
            return false;
        }
        if (
            (this.patientSearch.firstName == null || this.patientSearch.firstName.isEmpty()) &&
            (this.patientSearch.lastName == null || this.patientSearch.lastName.isEmpty()) &&
            (this.patientSearch.ssn == null || this.patientSearch.ssn.isEmpty()) &&
            (this.patientSearch.traumaRegisterNumber == null || this.patientSearch.traumaRegisterNumber.isEmpty())
        ) {
            if (Arguments.debug) System.out.println("Not even one element we can possibly use.  Not continuing with Treatments");
            return false; // causes a miss in WriteEachPatient.  Doesn't happen
        }

        // We want to do treatments only if there's a Treatments structure, or if this patient is marked random:true
        if (this.treatments != null || this.random == true) { // this this.random thing is throwing a NPE somehow
            success = processTreatments(); // I guess this method updates a global variable nErrors, so we don't bother with status return
            if (!success) {
                nErrors++;
            }
        }
        else {
            if (Arguments.debug) System.out.println("Did not process Treatments because treatments: " + this.treatments + " random: " + this.random);
        }
        if (nErrors > 0) {
            return false;
        }
        return true;
    }

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
        return true;
    }

    public boolean processPreRegistrationArrivals() {
        return true;
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
            if (!Arguments.quiet) System.err.print("***New Patient Registration process failed ");
//            if (this != null // looks wrong
//                    && this.patientRegistration != null
//                    && this.patientRegistration.newPatientReg.demographics != null
//                    && this.patientRegistration.newPatientReg.demographics.firstName != null
//                    && !this.patientRegistration.newPatientReg.demographics.firstName.isEmpty()) {
//                System.err.println("for " + this.patientRegistration.newPatientReg.demographics.firstName + " " + this.patientRegistration.newPatientReg.demographics.lastName + " ");
//            }
            if (this.patientSearch != null
                    && this.patientSearch.firstName != null && !this.patientSearch.firstName.isEmpty()
                    && this.patientSearch.lastName != null && !this.patientSearch.lastName.isEmpty()
                    && this.patientSearch.ssn != null && !this.patientSearch.ssn.isEmpty()) {
                System.err.println("for " + this.patientSearch.firstName + " " + this.patientSearch.lastName + " ssn:" + this.patientSearch.ssn);
            }
            else {
                System.err.println();
            }

            //if (!Arguments.quiet) System.err.println("possibly due to an error in patient registration information, or a slow or down server.  Skipping...");
            return false;
        }
        //else {
        //    System.out.println("process succeeded.");
        //}
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
            System.err.print("***Uupdate Patient process failed ");
            if (this != null // looks wrong
                    && this.patientRegistration != null
                    && this.patientRegistration.updatePatient.demographics != null
                    && this.patientRegistration.updatePatient.demographics.firstName != null
                    && !this.patientRegistration.updatePatient.demographics.firstName.isEmpty()) {
                System.err.print("for " + this.patientRegistration.updatePatient.demographics.firstName + " " + this.patientRegistration.updatePatient.demographics.lastName + " ");
            }
            System.err.println("possibly because no patient was found to update, or possibly due to an error in patient registration information, or a slow or down server.  Skipping...");
            //nErrors++;
            //continue;
            return false;
        }

//        if (!Arguments.quiet) {
//            System.out.println("Processed Patient: " +
//                    this.patientRegistration.updatePatient.demographics.firstName + " " +
//                    this.patientRegistration.updatePatient.demographics.lastName + ", dob: " +
//                    this.patientRegistration.updatePatient.demographics.dob + ", ssn:" +
//                    this.patientRegistration.updatePatient.demographics.ssn);
//        }

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
            if (!Arguments.quiet) System.err.print("***New Patient Registration process failed.");
            return false;
        }
        return true;

    }


    // We only get here if there is a Treatments structure, or if the patient is marked random:true
    // And I think that if we say "-random 5" on command line, then each patient is marked random:true.
    boolean processTreatments() {
        int nErrors = 0;

        // We're checking for PatientSearch before getting here.  So following is commented out
        // Probably shouldn't go very far if we don't have PatientSearch information.  It may be in any of the registration
        // sections, but we should not have to go there looking for it.  A registration page will update PatientSearch,
        // but it's possible that registration pages fail, or they were not specified.
        //
//        if (this.patientSearch == null) {
//            if (Arguments.debug) System.out.println("No patient search for this patient.  Not going to look for it in a registration.  We cannot continue with Treatments.");
//            return false;
//        }
////        if (this.patientRegistration == null) {
////            System.out.println("No PatientRegistration.  Possibly means no patientSearch, unless it was provided at the top.");
////        }
////        if (this.patientRegistration == null && this.patientSearch == null) {
////            System.out.println("No PatientRegistration or patient search.");
////            this.patientSearch = new PatientSearch();
////        }
//        // We need at least one element in the Patient Search to do Treatments.
//        // maybe do this before enter into Treatments
//        if (this.patientSearch.firstName == null && this.patientSearch.lastName == null && this.patientSearch.ssn == null && this.patientSearch.traumaRegisterNumber == null) {
//            if (Arguments.debug) System.out.println("Can't continue with Treatment information without a patient.");
//            return false;
//        }
//        if (
//                (this.patientSearch.firstName == null || this.patientSearch.firstName.isEmpty()) &&
//                (this.patientSearch.lastName == null || this.patientSearch.lastName.isEmpty()) &&
//                (this.patientSearch.ssn == null || this.patientSearch.ssn.isEmpty()) &&
//                (this.patientSearch.traumaRegisterNumber == null || this.patientSearch.traumaRegisterNumber.isEmpty())
//        ) {
//            if (Arguments.debug) System.out.println("Not even one element we can possibly use.  Not continuing with Treatments");
//            return false;
//        }
        // }
        // There can be a long wait required after patientRegistration is submitted and before treatments can be done.
        // 8 sec?

        // Treatments aren't required.  If the JSON file has no treatments, then patient.treatments is null.
        // But if patient.random is true, then we need to create a random number of treatments to fill in.
        // Each Treatment can have a number of "Notes".  If the JSON file contains a note section, even if it's
        // empty, it means do it.  Don't skip it.
        //
        // "Patient Treatment" is a tab, and in it there are three
        // areas that allow you to add as many notes as you want per area.  A Treatment is
        // a set of notes.  An encounter can have multiple treatments.  (And an encounter is pretty
        // much a patient record.  When I use the term Patient, it's really a patient record.  A person
        // can have multiple patient records over time.  A patient record represents a period of time that
        // care is given, or the patient is watched.  When finished, the person is no longer a patient, and
        // therefore a "Patient" goes away.  Anyway, I guess we'll allow multiple treatments per patient, even
        // though they're all just notes, and each of those three areas can be used to generate a note.  You
        // don't have to get out of that area in the web page to create a new note.  But you can get out and
        // then get back in.

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
                if (Arguments.debug) System.out.println("Not gunna do any treatments because percent is too low: " + percent);
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
            if (Arguments.debug) System.out.println("Did not to treatments.  Why?  treatments: " + treatments);
        }
        if (nErrors > 0) {
            success = false;
        }

        return success;
    }
}

//// Let's assume a patient can be in only one state at a time,
//// and yet the input file for a patient may contain info for more than one
//// state, because there's data for PreRegistration, NewRegistration,
//// UpdatePatient, PatientInfo, and PreRegistrationArrivals.
//// When we're doing Demographics, we don't know if the data comes from
//// NewRegistration, or UpdatePatient.
//enum PatientState {
//    PRE_REGISTRATION,
//    NEW_REGISTRATION,
//    UPDATE_REGISTRATION,
//    PATIENT_INFO,
//    PRE_REGISTRATION_ARRIVALS
//}
