package pep.patient.summary;

import pep.patient.Patient;
import pep.patient.treatment.FileUpload;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

import java.util.logging.Logger;

// Summary is navigated to via the nav bar, by clicking on "Patient Summary" which is
// //*[@id="nav"]/li[3]/a/span or possibly this: //*[@id="nav"]/li[3]/a or perhaps something like //a[@href='/bm-app/patientSummary.html']
// and then finding a patient through the Patient Search thing.  Then on that Patient Summary page there are three things
// a user might click on in order to create more info to add to the patient: "Facility Treatment History - Create Note",
// TBI Assessments - Create Note", and "Upload a New File".
// The first Create Note is very much like BehavioralHealthNote, except it has some additional radio buttons at the bottom.
// The second Create Note looks identical to TbiAssessmentNote or TbiAssessmentNote, but note those were slightly different in how they worked.
// The "Upload a New File" looks exactly like the others.
//
// Note there's no need to have the "Assessment" layer.  We don't need to go from Patient Summary to BehavioralHealthAssessment to BehavioralHealthNote.
// So we can skip that middle layer.
//
// Summary would be similar to Treatment (or Registration), except that its children would be a BehavioralHealthNote and TbiAssessmentNote
// and skip the middle "Assessment" layer.
//
// So this will be patterned after Treatment, and rip out the stuff not needed
//
// There can be an array of these in an input JSON encounters file
public class Summary {

    private static Logger logger = Logger.getLogger(Summary.class.getName());

    public Boolean random; // true if want this section to be generated randomly
    public FacilityTreatmentHistoryNote facilityTreatmentHistoryNote;
    public TbiAssessmentNote tbiAssessmentNote;
    public FileUpload fileUpload;

    public Summary() {
        if (Arguments.template) {
            //this.random = null;
            this.facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
            this.tbiAssessmentNote = new TbiAssessmentNote();
            this.fileUpload = new FileUpload();
        }
    }

    public boolean process(Patient patient, Summary summary) {  // this is weird
        if (!Arguments.quiet) System.out.println("    Processing Patient Summary for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ...");

        if (summary.random == null) { // nec?  Hopefully not any more.
            summary.random = (patient.random == null) ? false : patient.random; // right?
        }
        // Not sure of the value of doing this random stuff, where Summary is specified as random, but
        // neither of the two/three sections are provided.  That is, there's no facilityTreatmentHistoryNote section
        // or no tbiAssessmentNote section.  (Or fileUpload section.)
        //
        // This kind of logic maybe should also be in Registration, where if Registration is marked random
        // and there are no subsections like NewPatientReg, or PatientInformation, then some of the time one set
        // is done, and another time another set is done.  1: PreRegistration, PreRegistrationArrivals.  2: New Patient Reg
        // 3: #1 then Update Patient.  4: #2 then Update Patient.  5: #1 then Patient Information, etc, etc.
        //
        // At this point summary should not be null.  It may be essentially empty though, with "random:true"
        // I think this next percentage stuff is only used if the subsections are missing
        // and summary random:true
        boolean doFacilityTreatmentHistoryNote = false, doPsTbiAssessmentNote = false, doFileUpload = false;
        if (summary.random) { // new
            int percent = Utilities.random.nextInt(100);
            if (percent > 75) {
                doFacilityTreatmentHistoryNote = true;
            }
            if (percent > 80) {
                doPsTbiAssessmentNote = true;
            }
            if (percent > 90) {
                doFileUpload = true;
            }
            if (!doFacilityTreatmentHistoryNote && !doPsTbiAssessmentNote && !doFileUpload) {
                doFacilityTreatmentHistoryNote = true;
            }
        }
        if (summary.facilityTreatmentHistoryNote != null) {
            doFacilityTreatmentHistoryNote = true;
        }
        if (summary.tbiAssessmentNote != null) {
            doPsTbiAssessmentNote = true;
        }
        if (summary.fileUpload != null) {
            doFileUpload = true;
        }


        int nErrors = 0;
        FacilityTreatmentHistoryNote facilityTreatmentHistoryNote = summary.facilityTreatmentHistoryNote;
        if (facilityTreatmentHistoryNote != null) { // fix this logic.  Maybe no random and no value, so just skip out
            if (facilityTreatmentHistoryNote.random == null) { // Is this needed?
                facilityTreatmentHistoryNote.random = (summary.random == null) ? false : summary.random;
            }
            boolean processSucceeded = facilityTreatmentHistoryNote.process(patient); // does patient have the right SSN?  Inside can't continue because can't find the patient
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process Patient Summary Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if (summary.random && doFacilityTreatmentHistoryNote) {
                facilityTreatmentHistoryNote = new FacilityTreatmentHistoryNote();
                facilityTreatmentHistoryNote.random = (summary.random == null) ? false : summary.random;
                summary.facilityTreatmentHistoryNote = facilityTreatmentHistoryNote;
                boolean processSucceeded = facilityTreatmentHistoryNote.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Behavioral Health Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        TbiAssessmentNote tbiAssessmentNote = summary.tbiAssessmentNote;
        if (tbiAssessmentNote != null) {
            if (tbiAssessmentNote.random == null) { // Is this needed?
                tbiAssessmentNote.random = (summary.random == null) ? false : summary.random;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a
            boolean processSucceeded = tbiAssessmentNote.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if (summary.random && doPsTbiAssessmentNote) {
                tbiAssessmentNote = new TbiAssessmentNote();
                tbiAssessmentNote.random = (summary.random == null) ? false : summary.random;
                summary.tbiAssessmentNote = tbiAssessmentNote;
                boolean processSucceeded = tbiAssessmentNote.process(patient); // still kinda weird passing in summary
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        FileUpload fileUpload = summary.fileUpload;
        if (fileUpload != null) {
            if (fileUpload.random == null) { // Is this needed?
                fileUpload.random = (summary.random == null) ? false : summary.random;
            }
            // Hmmmm, that nav link to get to the page is this:        //*[@id="nav"]/li[2]/ul/li[3]/a
            boolean processSucceeded = fileUpload.process(patient);
            //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            if (!processSucceeded) {
                nErrors++;
                if (!Arguments.quiet)
                    System.err.println("    ***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            if (summary.random && doFileUpload) {
                fileUpload = new FileUpload();
                fileUpload.random = (summary.random == null) ? false : summary.random;
                summary.fileUpload = fileUpload;
                boolean processSucceeded = fileUpload.process(patient); // still kinda weird passing in summary
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("    ***Failed to process Patient Summary TBI Assessment for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true; // huh?  Not affected by processSucceeded results?


    }

}
