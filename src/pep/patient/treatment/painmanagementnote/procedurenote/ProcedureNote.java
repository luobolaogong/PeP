package pep.patient.treatment.painmanagementnote.procedurenote;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;


public class ProcedureNote {
    private static Logger logger = Logger.getLogger(ProcedureNote.class.getName());
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public SinglePeripheralNerveBlock singlePeripheralNerveBlock;
    public ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock;
    public EpiduralCatheter epiduralCatheter;
    public IvPca ivPca;
    public Boolean additionalBlock; // Should this be a String yes/no ?  means do another ProcedureNote

    private static By procedureNotesTabBy = By.linkText("Procedure Notes"); // new! 1/23/19 Does it work??????????????
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // looks right.  Only one.  Yellow are surrounding the dropdown.  But not here sometimes.

    // This is a single ProcedureNote from a List of such, carried in PainManagementNote
    // A single ProcedureNote can have sub classes.  So, we handle each one.
    public ProcedureNote() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            //this.procedure = ""; // this is used, right, from JSON we have strings like IvPca?  CHECK ON THIS
            this.singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            this.continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            this.epiduralCatheter = new EpiduralCatheter();
            this.ivPca = new IvPca();
            this.additionalBlock = null; // yes/no?  ""?
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // correct?
            procedureSectionBy = By.id("painNoteForm:Procedure");
        }

    }

    // Perhaps this method should start out with a navigation from the very top, and not assume we're sitting somewhere
    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Procedure Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );


        // on gold the page may not completely display, and there will be no Procedure Notes tab to click on!!!!!!!!!!!
        // Seems to only happen for patients that already had some kind of pain management stuff created, like IvPca.

        try {
            logger.finest("ProcedureNote.process(), here comes a wait for visibility of procedure notes tab.");
            // may be getting here too soon, because the search can take a long time
            WebElement procedureNotesTabElement = Utilities.waitForVisibility(procedureNotesTabBy, 30, "ProcedureNote.process()");
            logger.finest("ProcedureNote.process(), here comes a click on procedure notes tab.");
            procedureNotesTabElement.click();
            logger.finest("ProcedureNote.process(), gunna wait for ajax to finish.");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // times out currently because Pain Management Note is screwed up due to some table error and so some of the page doesn't show up
        }

        // Supposedly we clicked on the the "Procedure Notes" tab, and as a result we've got a single dropdown saying "Select Type".
        // That dropdown is in a part of a "<tbody>" that surrounds the "Select Procedure" dropdown.  Initially that's all that's in it.
        // Maybe it contains more than that later after you select something.  So this next part is just to confirm we've got
        // at least the dropdown, I guess.  Seems rather strange to do this rather than see if the dropdown is there.
        try {
            Utilities.waitForRefreshedVisibility(procedureSectionBy,  30, "ProcedureNote.() procedure section");
        }
        catch (Exception e) {
            logger.severe("ProcedureNote.process(), Exception caught: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails:1  Can seem to fail here because nothing shows up under Procedure Notes in Pain Managment Note page.  Happens sometimes.  Speed related prob
        }

        // Problem: User should not have to say the name of the procedure like "IvPca", but rather just create the JSON
        // object of IvPca.  If the object is specified, then that's the procedure.  So this next call to processDropdown
        // should be based on the procedure object that exists next.  And if there's more than one, you just keep doing them.
        // ProcedureNote IS in a List, so maybe we can handle this.  The JSON file should have array notation to handle
        // multiple ProcedureNote objects.  Does it?

        // Do we handle the array here, or is it up above in PainManagementNote?  Where do you handle multiple
        // ProcedureNotes, as in when you click the button?  We don't need/want the user to click the "Additional Block"
        // If there's an array, then handle them.  Where is the array?
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
                // getting here too fast?
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

    // When this method is called, where are we sitting?  What section of a web page is showing?
    boolean processSinglePeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        boolean processSucceeded = false;
        SinglePeripheralNerveBlock singlePeripheralNerveBlock = this.singlePeripheralNerveBlock;
        if (singlePeripheralNerveBlock != null) {
            if (singlePeripheralNerveBlock.sectionToBeRandomized == null) {
                singlePeripheralNerveBlock.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            }
            if (singlePeripheralNerveBlock.shoot == null) {
                singlePeripheralNerveBlock.shoot = this.shoot;
            }
            processSucceeded = singlePeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            singlePeripheralNerveBlock.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            singlePeripheralNerveBlock.shoot = this.shoot;
            this.singlePeripheralNerveBlock = singlePeripheralNerveBlock; // new
            if (this.sectionToBeRandomized) { // nec?
                processSucceeded = singlePeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in SPNB?
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    // Where are we sitting right now when this is called?
    boolean processContinuousPeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock = this.continuousPeripheralNerveBlock;
        if (continuousPeripheralNerveBlock != null) {
            if (continuousPeripheralNerveBlock.sectionToBeRandomized == null) {
                continuousPeripheralNerveBlock.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            }
            if (continuousPeripheralNerveBlock.shoot == null) {
                continuousPeripheralNerveBlock.shoot = this.shoot;
            }
            boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
                //return false;
            }
        }
        else {
            continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            continuousPeripheralNerveBlock.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            continuousPeripheralNerveBlock.shoot = this.shoot;
            this.continuousPeripheralNerveBlock = continuousPeripheralNerveBlock;
            if (this.sectionToBeRandomized) { // nec? don't think so
                boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                    //return false;
                }
            }
        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in CPNB?
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    // Where are we sitting right now when this is called?
    boolean processEpiduralCatheter(Patient patient) {
        int nErrors = 0; // this is silly, I know.  We're not looping.  Just return true or false rather than nError++;
        EpiduralCatheter epiduralCatheter = this.epiduralCatheter;
        if (epiduralCatheter != null) {
            if (epiduralCatheter.sectionToBeRandomized == null) {
                epiduralCatheter.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            }
            if (epiduralCatheter.shoot == null) {
                epiduralCatheter.shoot = this.shoot;
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
            epiduralCatheter.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            epiduralCatheter.shoot = this.shoot;
            this.epiduralCatheter = epiduralCatheter; // new
            if (this.sectionToBeRandomized) { // nec?
                boolean processSucceeded = epiduralCatheter.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in EpiduralCatheter?
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

    boolean processIvPca(Patient patient) { // seems silly
        int nErrors = 0; // this is silly, I know.  We're not looping.  Just return true or false rather than nError++;
        IvPca ivPca = this.ivPca;
        if (ivPca != null) {
            if (ivPca.sectionToBeRandomized == null) {
                ivPca.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            }
            if (ivPca.shoot == null) {
                ivPca.shoot = this.shoot;
            }
            boolean processSucceeded = ivPca.process(patient);
            if (!processSucceeded) {
                if (Arguments.verbose)
                    System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else { // why?  Only get here if there's no ivPca object, and we're doing random????
            ivPca = new IvPca();
            ivPca.sectionToBeRandomized = this.sectionToBeRandomized; // removed setting to false if null
            ivPca.shoot = this.shoot;
            this.ivPca = ivPca; // new, possibly wrong???????????????????????  Array situation?
            if (this.sectionToBeRandomized) { // prob unnec
                boolean processSucceeded = ivPca.process(patient);
                if (!processSucceeded) {
                    if (Arguments.verbose)
                        System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in IvPca?
            Utilities.sleep(Arguments.pauseSection * 1000, "ProcedureNote");
        }
        return (nErrors == 0);
    }

}
