package pep.patient.treatment;

import pep.patient.Patient;
import pep.patient.treatment.behavioralhealthassessment.BehavioralHealthAssessment;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.patient.treatment.tbiassessment.TbiAssessment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

public class Treatment {
    public Boolean random; // true if want this section to be generated randomly
    public PainManagementNote painManagementNote;
    public BehavioralHealthAssessment behavioralHealthAssessment;
    public TbiAssessment tbiAssessment;


    public Treatment() {
        if (Arguments.template) {
            //this.random = random;
            this.painManagementNote = new PainManagementNote();
            this.behavioralHealthAssessment = new BehavioralHealthAssessment();
            this.tbiAssessment = new TbiAssessment();
        }
    }

    // This might come in as a random patient.  A treatment can consist of a number of things,
    // like PM, or BH, or TBI or combination, and we should have at least one of those if we have
    // a treatment.  And PM can be 4 or more things, I think, and if we have a PM then we'll want at least one
    // of those 4.  If a section or subsection exists, then it needs to be processed even if it's empty.
    // If a subsection doesn't exist, but the section is random:true, then we randomly choose which
    // subsections to process.  By the time this method is called, there should be a Treatment section.
    //
    public boolean process(Patient patient, Treatment treatment) {
//        if (!Arguments.quiet) System.out.println("  Processing Treatment for " + patient.patientRegistration.newPatientReg.demographics.firstName + " " + patient.patientRegistration.newPatientReg.demographics.lastName + " ...");
        if (!Arguments.quiet) System.out.println("  Processing Treatment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");

        if (treatment.random == null) { // nec?  Hopefully not any more.
            treatment.random = (patient.random == null) ? false : patient.random; // right?
        }

        // I think this next percentage stuff is only used if the subsections are missing
        // and treatment random:true
        boolean doPm = false, doBh = false, doTbi = false;
        int percent = Utilities.random.nextInt(100);
        if (percent > 25) {
            doPm = true;
        }
        if (percent > 75) {
            doBh = true;
        }
        if (percent > 80) {
            doTbi = true;
        }
        if (!doPm && !doBh && !doTbi) {
            doPm = true;
        }

        PainManagementNote painManagementNote = treatment.painManagementNote;
        int nErrors = 0;
        if (painManagementNote != null) {
            if (painManagementNote.random == null) { // Is this needed? I think so.
                painManagementNote.random = (treatment.random == null) ? false : treatment.random;
            }
            boolean processSucceeded = painManagementNote.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Pain Management Note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("***Failed to process Pain Management Note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            }
        }
        else {
            if (treatment.random && doPm) {
                painManagementNote = new PainManagementNote();
                painManagementNote.random = (treatment.random == null) ? false : treatment.random;
                treatment.painManagementNote = painManagementNote;

                boolean processSucceeded = painManagementNote.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Pain Management Note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process Pain Management Note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                }
            }
        }

        // Do we need some time between pain management and behavioral health?????????????
        // check if BehavioralHealthAssessment is getting random inherited from parent correctly.
        BehavioralHealthAssessment behavioralHealthAssessment = treatment.behavioralHealthAssessment;
        if (behavioralHealthAssessment != null) { // fix this logic.  Maybe no random and no value, so just skip out
            if (behavioralHealthAssessment.random == null) { // Is this needed?
                behavioralHealthAssessment.random = (treatment.random == null) ? false : treatment.random;
            }
            boolean processSucceeded = behavioralHealthAssessment.process(patient); // does patient have the right SSN?  Inside can't continue because can't find the patient
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("***Failed to process Behavioral Health Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            }
        }
        else {
            if (treatment.random && doBh) {
                behavioralHealthAssessment = new BehavioralHealthAssessment();
                behavioralHealthAssessment.random = (treatment.random == null) ? false : treatment.random;
                treatment.behavioralHealthAssessment = behavioralHealthAssessment;
                boolean processSucceeded = behavioralHealthAssessment.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process Behavioral Health Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                }
            }
        }

        TbiAssessment tbiAssessment = treatment.tbiAssessment;
        if (tbiAssessment != null) {
            if (tbiAssessment.random == null) { // Is this needed?
                tbiAssessment.random = (treatment.random == null) ? false : treatment.random;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a
            boolean processSucceeded = tbiAssessment.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("***Failed to process TBI Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            }
        }
        else {
            if (treatment.random && doTbi) {
                tbiAssessment = new TbiAssessment();
                tbiAssessment.random = (treatment.random == null) ? false : treatment.random;
                treatment.tbiAssessment = tbiAssessment;
                boolean processSucceeded = tbiAssessment.process(patient); // still kinda weird passing in treatment
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process TBI Assessment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        return true; // huh?  Not affected by processSucceeded results?
    }
}
