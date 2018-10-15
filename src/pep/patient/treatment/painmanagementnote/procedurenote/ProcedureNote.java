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
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;

public class ProcedureNote {
    public Boolean random; // true if want this section to be generated randomly
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
        if (isDemoTier) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // correct for Demo?
            procedureSectionBy = By.id("painNoteForm:Procedure");
        }

    }

    // When this method is called, where are we sitting?  What section of a web page is showing?
    boolean processSinglePeripheralNerveBlock(Patient patient) {
        boolean processSucceeded = false;
        SinglePeripheralNerveBlock singlePeripheralNerveBlock = this.singlePeripheralNerveBlock;
        if (singlePeripheralNerveBlock != null) {
            if (singlePeripheralNerveBlock.random == null) {
                singlePeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            }
            processSucceeded = singlePeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        else {
            singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
            singlePeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            this.singlePeripheralNerveBlock = singlePeripheralNerveBlock; // new
            if (this.random) { // nec?
                processSucceeded = singlePeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        return processSucceeded;
    }

    // Where are we sitting right now when this is called?
    boolean processContinuousPeripheralNerveBlock(Patient patient) {
        ContinuousPeripheralNerveBlock continuousPeripheralNerveBlock = this.continuousPeripheralNerveBlock;
        if (continuousPeripheralNerveBlock != null) {
            if (continuousPeripheralNerveBlock.random == null) {
                continuousPeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            }
            boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                return false;
            }
        }
        else {
            continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
            continuousPeripheralNerveBlock.random = (this.random == null) ? false : this.random;
            this.continuousPeripheralNerveBlock = continuousPeripheralNerveBlock;
            if (this.random) { // nec? don't think so
                boolean processSucceeded = continuousPeripheralNerveBlock.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    return false;
                }
            }
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
            boolean processSucceeded = epiduralCatheter.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            epiduralCatheter = new EpiduralCatheter();
            epiduralCatheter.random = (this.random == null) ? false : this.random;
            this.epiduralCatheter = epiduralCatheter; // new
            if (this.random) { // nec?
                boolean processSucceeded = epiduralCatheter.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
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
            boolean processSucceeded = ivPca.process(patient);
            if (!processSucceeded) {
                if (!Arguments.quiet)
                    System.err.println("***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                nErrors++;
            }
        }
        else {
            ivPca = new IvPca();
            ivPca.random = (this.random == null) ? false : this.random;
            this.ivPca = ivPca; // new, possibly wrong???????????????????????  Array situation?
            if (this.random) { // prob unnec
                boolean processSucceeded = ivPca.process(patient);
                if (!processSucceeded) {
                    if (!Arguments.quiet)
                        System.err.println("***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                    nErrors++;
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        return true;
    }

   // I think currently a PainManagementNote can hold at most one ProcedureNotes object.  That
    // ProcedureNotes object can have multiple ProcedureNote objects.  So, looks like we currently
    // support lists within lists.
    // Perhaps this method should start out with a navigation from the very top, and not assume we're sitting somewhere
    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Procedure Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        // on gold the page may not completely display, and there will be no Procedure Notes tab to click on!!!!!!!!!!!
        // Seems to only happen for patients that already had some kind of pain management stuff created, like IvPca.

        try {
            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            procedureNotesTabElement.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
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
            System.out.println("Exception caught: " + e.getMessage());
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
                        if (Arguments.debug) System.out.println("***Failed to process Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.continuousPeripheralNerveBlock != null) {
                    boolean processSucceeded = processContinuousPeripheralNerveBlock(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug) System.out.println("***Failed to process Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.epiduralCatheter != null) {
                    boolean processSucceeded = processEpiduralCatheter(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug)
                            System.out.println("***Failed to process Epidural Catheter for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
                if (procedureNote.ivPca != null) {
                    boolean processSucceeded = processIvPca(patient);
                    if (!processSucceeded) {
                        if (Arguments.debug)
                            System.out.println("***Failed to process IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        nErrors++;
                    }
                }
            }
        }
        if (nErrors > 0) {
            return false;
        }
        return true;
    }
}
