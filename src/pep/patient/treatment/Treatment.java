package pep.patient.treatment;

import pep.patient.Patient;
import pep.patient.treatment.behavioralhealthassessment.BehavioralHealthAssessment;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.patient.treatment.tbiassessment.TbiAssessment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.logging.Logger;

// There can be an array of these in an input JSON encounters file
public class Treatment {
    private static Logger logger = Logger.getLogger(Treatment.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
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
        //if (!Arguments.quiet) System.out.println("  Processing Treatment for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        // following is a quick test, untested
        if (!Arguments.quiet) System.out.println("  Processing Treatment for patient" +
                ((patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) ? (" " + patient.patientSearch.firstName) : "") +
                ((patient.patientSearch.lastName != null && !patient.patientSearch.lastName.isEmpty()) ? (" " + patient.patientSearch.lastName) : "") +
                " ssn:" + patient.patientSearch.ssn + " ...");

        if (treatment.random == null) { // nec?  Hopefully not any more.
            treatment.random = patient.random; // right?
        }
        if (treatment.shoot == null) { // nec?  Hopefully not any more.
            treatment.shoot = patient.shoot; // right?
        }
        // At this point treatment should not be null.  It may be essentially empty though, with "random:true"
        // I think this next percentage stuff is only used if the subsections are missing
        // and treatment random:true
        boolean doPm = false, doBh = false, doTbi = false;
        if (treatment.random != null && treatment.random) { // new
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
        }
        // following is new 10/19/18.  Looks wrong; if the JSON section is empty, but exists, and no random, then don't do it, right?  But can't easily check
        if (treatment.behavioralHealthAssessment != null) {
            doBh = true;
        }
        if (treatment.painManagementNote != null) {
            doPm = true;
        }
        if (treatment.tbiAssessment != null) {
            doTbi = true;
        }





        PainManagementNote painManagementNote = treatment.painManagementNote;
        int nErrors = 0;
        if (painManagementNote != null) {
            if (painManagementNote.random == null) { // Is this needed? I think so.
                //painManagementNote.random = (treatment.random == null) ? false : treatment.random;
                painManagementNote.random = treatment.random;
            }
            if (painManagementNote.shoot == null) { // Is this needed? I think so.
                painManagementNote.shoot = treatment.shoot;
            }
            boolean processSucceeded = painManagementNote.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.random != null && treatment.random) && doPm) {
                painManagementNote = new PainManagementNote();
                painManagementNote.random = treatment.random;
                painManagementNote.shoot = treatment.shoot;
                treatment.painManagementNote = painManagementNote;

                boolean processSucceeded = painManagementNote.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        // Do we need some time between pain management and behavioral health?????????????
        // check if BehavioralHealthAssessment is getting random inherited from parent correctly.
        BehavioralHealthAssessment behavioralHealthAssessment = treatment.behavioralHealthAssessment;
        if (behavioralHealthAssessment != null) { // fix this logic.  Maybe no random and no value, so just skip out
            if (behavioralHealthAssessment.random == null) { // Is this needed?
                behavioralHealthAssessment.random = treatment.random;
            }
            if (behavioralHealthAssessment.shoot == null) { // Is this needed?
                behavioralHealthAssessment.shoot = treatment.shoot;
            }
            boolean processSucceeded = behavioralHealthAssessment.process(patient); // does patient have the right SSN?  Inside can't continue because can't find the patient
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.random != null && treatment.random) && doBh) {
                behavioralHealthAssessment = new BehavioralHealthAssessment();
                behavioralHealthAssessment.random = treatment.random;
                behavioralHealthAssessment.shoot = treatment.shoot;
                treatment.behavioralHealthAssessment = behavioralHealthAssessment;
                boolean processSucceeded = behavioralHealthAssessment.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        TbiAssessment tbiAssessment = treatment.tbiAssessment;
        if (tbiAssessment != null) {
            if (tbiAssessment.random == null) { // Is this needed?
                tbiAssessment.random = treatment.random;
            }
            if (tbiAssessment.shoot == null) { // Is this needed?
                tbiAssessment.shoot = treatment.shoot;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a
            boolean processSucceeded = tbiAssessment.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.random != null && treatment.random) && doTbi) {
                tbiAssessment = new TbiAssessment();
                tbiAssessment.random = treatment.random;
                tbiAssessment.shoot = treatment.shoot;
                treatment.tbiAssessment = tbiAssessment;
                boolean processSucceeded = tbiAssessment.process(patient); // still kinda weird passing in treatment
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        return true; // huh?  Not affected by processSucceeded results?
    }
}
