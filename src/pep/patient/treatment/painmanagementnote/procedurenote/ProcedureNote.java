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
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public SinglePeripheralNerveBlock singlePeripheralNerveBlock;
    public ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock;
    public EpiduralCatheter epiduralCatheter;
    public IvPca ivPca;
    public Boolean additionalBlock; // Should this be a String yes/no ?  means do another ProcedureNote

    private static By procedureNotesTabBy = By.xpath("//*[@id=\"procedureNoteTab\"]/a");
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // looks right.  Only one.  Yellow are surrounding the dropdown.  But not here sometimes.

    // This is a single ProcedureNote from a List of such, carried in PainManagementNote
    // A single ProcedureNote can have sub classes.  So, we handle each one.
    public ProcedureNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            //this.procedure = ""; // this is used, right, from JSON we have strings like IvPca?  CHECK ON THIS
            this.singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            this.continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            this.epiduralCatheter = new EpiduralCatheter();
            this.ivPca = new IvPca();
            this.additionalBlock = null; // yes/no?  ""?
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // correct for Demo?
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

            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            logger.finest("ProcedureNote.process(), here comes a click on procedure notes tab.");
            procedureNotesTabElement.click();
            logger.finest("ProcedureNote.process(), gunna wait for ajax to finish.");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            return false; // times out currently because Pain Management Note is screwed up due to some table error and so some of the page doesn't show up
        }

        // Supposedly we clicked on the the "Procedure Notes" tab, and as a result we've got a single dropdown saying "Select Type".
        // That dropdown is in a part of a "<tbody>" that surrounds the "Select Procedure" dropdown.  Initially that's all that's in it.
        // Maybe it contains more than that later after you select something.  So this next part is just to confirm we've got
        // at least the dropdown, I guess.  Seems rather strange to do this rather than see if the dropdown is there.
        try {
            (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(procedureSectionBy)));
        }
        catch (Exception e) {
            logger.severe("ProcedureNote.(), Exception caught: " + Utilities.getMessageFirstLine(e));
            return false; // fails:1
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
                        logger.fine("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.continuousPeripheralNerveBlock != null) {
                    boolean processSucceeded = processContinuousPeripheralNerveBlock(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug) System.out.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.epiduralCatheter != null) {
                    boolean processSucceeded = processEpiduralCatheter(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug) System.out.println("        ***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.ivPca != null) {
                    boolean processSucceeded = processIvPca(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug) System.out.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }

    // When this method is called, where are we sitting?  What section of a web page is showing?
    boolean processSinglePeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        boolean processSucceeded = false;
        SinglePeripheralNerveBlock singlePeripheralNerveBlock = this.singlePeripheralNerveBlock;
        if (singlePeripheralNerveBlock != null) {
            if (singlePeripheralNerveBlock.random == null) {
                singlePeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            }
            if (singlePeripheralNerveBlock.shoot == null) {
                singlePeripheralNerveBlock.shoot = (this.shoot == null) ? false : this.shoot;
            }
            processSucceeded = singlePeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            singlePeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            singlePeripheralNerveBlock.shoot = (this.shoot == null) ? false : this.shoot;
            this.singlePeripheralNerveBlock = singlePeripheralNerveBlock; // new
            if (this.random) { // nec?
                processSucceeded = singlePeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
//        if (this.shoot != null && this.shoot) {
//            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
//            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
//        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in SPNB?
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return processSucceeded;
    }

    // Where are we sitting right now when this is called?
    boolean processContinuousPeripheralNerveBlock(Patient patient) {
        int nErrors = 0;
        ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock = this.continuousPeripheralNerveBlock;
        if (continuousPeripheralNerveBlock != null) {
            if (continuousPeripheralNerveBlock.random == null) {
                continuousPeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            }
            if (continuousPeripheralNerveBlock.shoot == null) {
                continuousPeripheralNerveBlock.shoot = (this.shoot == null) ? false : this.shoot;
            }
            boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
                //return false;
            }
        }
        else {
            continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            continuousPeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            continuousPeripheralNerveBlock.shoot = (this.shoot == null) ? false : this.shoot;
            this.continuousPeripheralNerveBlock = continuousPeripheralNerveBlock;
            if (this.random) { // nec? don't think so
                boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                    //return false;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
//        if (this.shoot != null && this.shoot) {
//            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
//            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
//        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in CPNB?
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }

    // Where are we sitting right now when this is called?
    boolean processEpiduralCatheter(Patient patient) {
        int nErrors = 0; // this is silly, I know.  We're not looping.  Just return true or false rather than nError++;
        EpiduralCatheter epiduralCatheter = this.epiduralCatheter;
        if (epiduralCatheter != null) {
            if (epiduralCatheter.random == null) {
                epiduralCatheter.random = (this.random == null) ? false : this.random;
            }
            if (epiduralCatheter.shoot == null) {
                epiduralCatheter.shoot = (this.shoot == null) ? false : this.shoot;
            }
            boolean processSucceeded = epiduralCatheter.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            epiduralCatheter = new EpiduralCatheter();
            epiduralCatheter.random = (this.random == null) ? false : this.random;
            epiduralCatheter.shoot = (this.shoot == null) ? false : this.shoot;
            this.epiduralCatheter = epiduralCatheter; // new
            if (this.random) { // nec?
                boolean processSucceeded = epiduralCatheter.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to process epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
//        if (this.shoot != null && this.shoot) {
//            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
//            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
//        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in EpiduralCatheter?
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }

    boolean processIvPca(Patient patient) { // seems silly
        int nErrors = 0; // this is silly, I know.  We're not looping.  Just return true or false rather than nError++;
        IvPca ivPca = this.ivPca;
        if (ivPca != null) {
            if (ivPca.random == null) {
                ivPca.random = (this.random == null) ? false : this.random;
            }
            if (ivPca.shoot == null) {
                ivPca.shoot = (this.shoot == null) ? false : this.shoot;
            }
            boolean processSucceeded = ivPca.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else { // why?  Only get here if there's no ivPca object, and we're doing random????
            ivPca = new IvPca();
            ivPca.random = (this.random == null) ? false : this.random;
            ivPca.shoot = (this.shoot == null) ? false : this.shoot;
            this.ivPca = ivPca; // new, possibly wrong???????????????????????  Array situation?
            if (this.random) { // prob unnec
                boolean processSucceeded = ivPca.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
//        if (this.shoot != null && this.shoot) {
//            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
//            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
//        }
        if (Arguments.pauseSection > 0) { // is this right here?  Should be in IvPca?
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }

}
