package pep.patient.treatment;

import pep.patient.Patient;
import pep.patient.treatment.behavioralhealthassessment.BehavioralHealthAssessment;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.patient.treatment.tbiassessment.TbiAssessment;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

// There can be an array of these in an input JSON encounters file

/**
 * This class holds treatment information for a patient.  A treatment may include pain management, behavioral health, or traumatic brain injury.
 * A patient can have more than one treatment, and so this goes in an array.
 * The logic of this class could be simplified.
 */
public class Treatment {
    private static Logger logger = Logger.getLogger(Treatment.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public PainManagementNote painManagementNote;
    public BehavioralHealthAssessment behavioralHealthAssessment;
    public TbiAssessment tbiAssessment;


    public Treatment() {
        if (Arguments.template) {
            this.painManagementNote = new PainManagementNote();
            this.behavioralHealthAssessment = new BehavioralHealthAssessment();
            this.tbiAssessment = new TbiAssessment();
        }
    }

    /**
     * Process a Treatment object, which comes from the input file, which could be marked "random".
     * A treatment can consist of a number of things, like PM, or BH, or TBI or combination, and we should
     * have at least one of those if we have a treatment.  And PM can be 4 or more things, I think, and if
     * we have a PM then we'll want at least one of those 4.  If a section or subsection exists, then it
     * needs to be processed even if it's empty.  If a subsection doesn't exist, but the section is random:true,
     * then we randomly choose which subsections to process.  By the time this method is called, there should
     * be a Treatment section.
     * @param patient The patient the treatment is for
     * @param treatment The treatment for the patient
     * @return success or failure of the entire treatment processing
     */
    public boolean process(Patient patient, Treatment treatment) {
        if (!Arguments.quiet) System.out.println("  Processing Treatment at " + LocalTime.now() + " for patient" +
                ((patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) ? (" " + patient.patientSearch.firstName) : "") +
                ((patient.patientSearch.lastName != null && !patient.patientSearch.lastName.isEmpty()) ? (" " + patient.patientSearch.lastName) : "") +
                " ssn:" + patient.patientSearch.ssn + " ...");

        // maybe unnecessary:
        if (treatment.randomizeSection == null) {
            treatment.randomizeSection = patient.randomizeSection;
        }
        if (treatment.shoot == null) {
            treatment.shoot = patient.shoot;
        }

        // If Treatment section is marked "random", then set percentages of probable occurrances so can process accordingly.
        boolean doPm = false, doBh = false, doTbi = false;
        if (treatment.randomizeSection != null && treatment.randomizeSection) {
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
        // Override if values actually specified:
        if (treatment.behavioralHealthAssessment != null) {
            doBh = true;
        }
        if (treatment.painManagementNote != null) {
            doPm = true;
        }
        if (treatment.tbiAssessment != null) {
            doTbi = true;
        }


        // Handle Pain Management
        PainManagementNote painManagementNote = treatment.painManagementNote;
        int nErrors = 0;
        if (painManagementNote != null) { // check logic for this section
            if (painManagementNote.randomizeSection == null) {
                painManagementNote.randomizeSection = treatment.randomizeSection;
            }
            if (painManagementNote.shoot == null) {
                painManagementNote.shoot = treatment.shoot;
            }
            boolean processSucceeded = painManagementNote.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (Arguments.verbose) System.err.println("    ***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.randomizeSection != null && treatment.randomizeSection) && doPm) {
                painManagementNote = new PainManagementNote();
                painManagementNote.randomizeSection = treatment.randomizeSection;
                painManagementNote.shoot = treatment.shoot;
                treatment.painManagementNote = painManagementNote;

                boolean processSucceeded = painManagementNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("    ***Failed to process Pain Management Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        // Handle Behavioral Health
        BehavioralHealthAssessment behavioralHealthAssessment = treatment.behavioralHealthAssessment;
        if (behavioralHealthAssessment != null) {
            if (behavioralHealthAssessment.randomizeSection == null) {
                behavioralHealthAssessment.randomizeSection = treatment.randomizeSection;
            }
            if (behavioralHealthAssessment.shoot == null) {
                behavioralHealthAssessment.shoot = treatment.shoot;
            }
            boolean processSucceeded = behavioralHealthAssessment.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (Arguments.verbose) System.err.println("    ***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.randomizeSection != null && treatment.randomizeSection) && doBh) {
                behavioralHealthAssessment = new BehavioralHealthAssessment();
                behavioralHealthAssessment.randomizeSection = treatment.randomizeSection;
                behavioralHealthAssessment.shoot = treatment.shoot;
                treatment.behavioralHealthAssessment = behavioralHealthAssessment;
                boolean processSucceeded = behavioralHealthAssessment.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("    ***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        // Handle Traumatic Brain Injury
        TbiAssessment tbiAssessment = treatment.tbiAssessment;
        if (tbiAssessment != null) {
            if (tbiAssessment.randomizeSection == null) {
                tbiAssessment.randomizeSection = treatment.randomizeSection;
            }
            if (tbiAssessment.shoot == null) {
                tbiAssessment.shoot = treatment.shoot;
            }
            boolean processSucceeded = tbiAssessment.process(patient);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet) System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if ((treatment.randomizeSection != null && treatment.randomizeSection) && doTbi) {
                tbiAssessment = new TbiAssessment();
                tbiAssessment.randomizeSection = treatment.randomizeSection;
                tbiAssessment.shoot = treatment.shoot;
                treatment.tbiAssessment = tbiAssessment;
                boolean processSucceeded = tbiAssessment.process(patient); // still kinda weird passing in treatment
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        return (nErrors == 0);
    }
}
