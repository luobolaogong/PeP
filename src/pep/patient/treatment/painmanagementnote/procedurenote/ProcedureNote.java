package pep.patient.treatment.painmanagementnote.procedurenote;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock.ContinuousPeripheralNerveBlock;
import pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter.EpiduralCatheter;
import pep.patient.treatment.painmanagementnote.procedurenote.ivpca.IvPca;
import pep.patient.treatment.painmanagementnote.procedurenote.singleperipheralnerveblock.SinglePeripheralNerveBlock;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

/**
 * This class is just an organizer for the 4 different kinds of procedure notes.
 * This is a single ProcedureNote which would be part of a List of such, carried in PainManagementNote
 */
public class ProcedureNote {
    private static Logger logger = Logger.getLogger(ProcedureNote.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public SinglePeripheralNerveBlock singlePeripheralNerveBlock;
    public ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock;
    public EpiduralCatheter epiduralCatheter;
    public IvPca ivPca;
    public Boolean additionalBlock;

    private static By procedureNotesTabBy = By.linkText("Procedure Notes");
    private static By procedureSectionBy = By.id("procedureNoteTabContainer");

    public ProcedureNote() {
        if (Arguments.template) {
            this.singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            this.continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            this.epiduralCatheter = new EpiduralCatheter();
            this.ivPca = new IvPca();
            this.additionalBlock = null;
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // correct?
            procedureSectionBy = By.id("painNoteForm:Procedure");
        }

    }

    /**
     * Loop through the list of painManagementNote's ProcedureNotes, calling process() on each.
     * @param patient The Patient for which the pain management note applies
     * @param painManagementNote The pain management note that has a list of procedure notes.
     * @return Failure or success at processing the procedure notes for the pain management note
     */
    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Procedure Note at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Get to the procedure notes tab and click on it, which enables the choice of 4 types of procedures,
        // then wait for that "Select Procedure" dropdown to appear.
        //
        try {
            logger.finest("ProcedureNote.process(), here comes a wait for visibility of procedure notes tab.");
            WebElement procedureNotesTabElement = Utilities.waitForVisibility(procedureNotesTabBy, 30, "ProcedureNote.process()");
            logger.finest("ProcedureNote.process(), here comes a click on procedure notes tab.");
            procedureNotesTabElement.click();
            procedureNotesTabElement.click(); // experiment 5/6/19
            logger.finest("ProcedureNote.process(), gunna wait for ajax to finish.");
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does not cause prob if stop first
        }
        catch (Exception e) {
            logger.fine("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }

        // removing next section to see if makes any difference
//        try { // next line fails when do first start up?
//            Utilities.waitForRefreshedVisibility(procedureSectionBy,  30, "ProcedureNote.() procedure section");
//        }
//        catch (Exception e) {
//            logger.severe("ProcedureNote.process(), Exception caught: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
//            return false; // fails:4
//        }
        //
        // If a procedure object is specified, then process it by selecting that process in the dropdown.
        // ProcedureNotes is a list, so loop through them.  Call the appropriate "process" method, which then calls process() on that object.
        // Not sure we're handling "Additional Block"
        //
        int nErrors = 0;
        if (painManagementNote.procedureNotes != null) {
            for (ProcedureNote procedureNote : painManagementNote.procedureNotes) {
                if (procedureNote.singlePeripheralNerveBlock != null) {
                    boolean processSucceeded = processSinglePeripheralNerveBlock(patient);
                    if (!processSucceeded) {
                        if (Arguments.verbose) System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.continuousPeripheralNerveBlock != null) {
                    boolean processSucceeded = processContinuousPeripheralNerveBlock(patient);
                    if (!processSucceeded) {
                        if (Arguments.verbose) System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.epiduralCatheter != null) {
                    boolean processSucceeded = processEpiduralCatheter(patient);
                    if (!processSucceeded) {
                        if (Arguments.verbose) System.err.println("        ***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.ivPca != null) {
                    boolean processSucceeded = processIvPca(patient);
                    if (!processSucceeded) {
                        if (Arguments.verbose) System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
            }
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "TransferNote, requested sleep for page.");
        }
        return (nErrors == 0);
    }

    // I know these next four methods should be handled as one, using inheritance.

    /**
     * Set up the procedure note prior to calling process() on it, and then call it.
     * @param patient The patient to act upon
     * @return Success or Failure
     */
    boolean processSinglePeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        SinglePeripheralNerveBlock singlePeripheralNerveBlock = this.singlePeripheralNerveBlock;
        if (singlePeripheralNerveBlock != null) {
            if (singlePeripheralNerveBlock.randomizeSection == null) {
                singlePeripheralNerveBlock.randomizeSection = this.randomizeSection;
            }
            if (singlePeripheralNerveBlock.shoot == null) {
                singlePeripheralNerveBlock.shoot = this.shoot;
            }
            if (singlePeripheralNerveBlock.skipSave == null) {
                singlePeripheralNerveBlock.skipSave = this.skipSave;
            }
            boolean processSucceeded = singlePeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            singlePeripheralNerveBlock.randomizeSection = this.randomizeSection;
            singlePeripheralNerveBlock.shoot = this.shoot;
            singlePeripheralNerveBlock.skipSave = this.skipSave;
            this.singlePeripheralNerveBlock = singlePeripheralNerveBlock;
            if (this.randomizeSection != null && this.randomizeSection) {
                boolean processSucceeded = singlePeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    /**
     * Set up the procedure note prior to calling process() on it, and then call it.
     * @param patient The patient to act upon
     * @return Success or Failure
     */
    boolean processContinuousPeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock = this.continuousPeripheralNerveBlock;
        if (continuousPeripheralNerveBlock != null) {
            if (continuousPeripheralNerveBlock.randomizeSection == null) {
                continuousPeripheralNerveBlock.randomizeSection = this.randomizeSection;
            }
            if (continuousPeripheralNerveBlock.shoot == null) {
                continuousPeripheralNerveBlock.shoot = this.shoot;
            }
            if (continuousPeripheralNerveBlock.skipSave == null) {
                continuousPeripheralNerveBlock.skipSave = this.skipSave;
            }
            boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            continuousPeripheralNerveBlock.randomizeSection = this.randomizeSection;
            continuousPeripheralNerveBlock.shoot = this.shoot;
            continuousPeripheralNerveBlock.skipSave = this.skipSave;
            this.continuousPeripheralNerveBlock = continuousPeripheralNerveBlock;
            if (this.randomizeSection != null && this.randomizeSection) {
                boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    /**
     * Set up the procedure note prior to calling process() on it, and then call it.
     * @param patient The patient to act upon
     * @return Success or Failure
     */
    boolean processEpiduralCatheter(Patient patient) {
        int nErrors = 0;
        EpiduralCatheter epiduralCatheter = this.epiduralCatheter;
        if (epiduralCatheter != null) {
            if (epiduralCatheter.randomizeSection == null) {
                epiduralCatheter.randomizeSection = this.randomizeSection;
            }
            if (epiduralCatheter.shoot == null) {
                epiduralCatheter.shoot = this.shoot;
            }
            if (epiduralCatheter.skipSave == null) {
                epiduralCatheter.skipSave = this.skipSave;
            }
            boolean processSucceeded = epiduralCatheter.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            epiduralCatheter = new EpiduralCatheter();
            epiduralCatheter.randomizeSection = this.randomizeSection;
            epiduralCatheter.shoot = this.shoot;
            epiduralCatheter.skipSave = this.skipSave;
            this.epiduralCatheter = epiduralCatheter;
            if (this.randomizeSection != null && this.randomizeSection) {
                boolean processSucceeded = epiduralCatheter.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    /**
     * Set up the procedure note prior to calling process() on it, and then call it.
     * @param patient The patient to act upon
     * @return Success or Failure
     */
    boolean processIvPca(Patient patient) {
        int nErrors = 0;
        IvPca ivPca = this.ivPca;
        if (ivPca != null) {
            if (ivPca.randomizeSection == null) {
                ivPca.randomizeSection = this.randomizeSection;
            }
            if (ivPca.shoot == null) {
                ivPca.shoot = this.shoot;
            }
            if (ivPca.skipSave == null) {
                ivPca.skipSave = this.skipSave;
            }
            boolean processSucceeded = ivPca.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            ivPca = new IvPca();
            ivPca.randomizeSection = this.randomizeSection;
            ivPca.shoot = this.shoot;
            ivPca.skipSave = this.skipSave;
            this.ivPca = ivPca;
            if (this.randomizeSection != null && this.randomizeSection) {
                boolean processSucceeded = ivPca.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

}
