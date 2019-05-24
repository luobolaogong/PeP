package pep.patient.treatment.painmanagementnote.procedurenote.singleperipheralnerveblock;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

/**
 * This class represents one kind of procedure note, that is part of a Pain Management Note,
 * and is used to process the note by filling in the field values and saving it.
 */
public class SinglePeripheralNerveBlock {
    private static Logger logger = Logger.getLogger(SinglePeripheralNerveBlock.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public String timeOfPlacement;
    public String lateralityOfPnb;
    public String locationOfPnb;
    public String medication;
    public String concentration;
    public String volume;
    public String preProcedureVerbalAnalogueScore;
    public String postProcedureVerbalAnalogueScore;
    public String blockPurpose;
    public String commentsNotesComplications;
    public String wantAdditionalBlock;

    private static By procedureNotesTabBy = By.linkText("Procedure Notes");
    private static By selectProcedureDropdownBy = By.id("procedureNoteTypeBox");
    private static By singlePeripheralSectionBy = By.id("singlePeripheralNerveBlockContainer");
    private static By spnbTimeOfPlacementBy = By.id("singlePeripheralPlacementDate1");
    private static By locationOfPnbDropdownBy = By.id("blockLocation");
    private static By medicationDropdownBy = By.id("injectionMedication");
    private static By concentrationFieldBy = By.id("injectionConcentration");
    private static By volumeFieldBy = By.id("injectionQty");
    private static By preVerbalScoreDropdownBy = By.id("preProcVas");
    private static By postVerbalScoreDropdownBy = By.id("postProcVas");
    private static By blockPurposeDropdownBy = By.id("blockPurpose");
    private static By commentsTextAreaBy = By.id("comments");

    private static By yesRadioLabelBy = By.xpath("//label[text()='Yes']");
    private static By noRadioLabelBy = By.xpath("//label[text()='No']");
    private static By createNoteButtonBy =   By.xpath("//div[@id='singlePeripheralNerveBlockContainer']/descendant::button[text()='Create Note']");
    private static By painManagementNoteMessageAreaBy = By.id("pain-note-message");
    private static By problemOnTheServerMessageAreaBy = By.id("createNoteMsg");
    private static By procedureSectionBy = By.id("procedureNoteTabContainer");
    private static By lateralityLeftBy = By.xpath("//label[text()='Left']");
    private static By lateralityRightBy = By.xpath("//label[text()='Right']");


    public SinglePeripheralNerveBlock() {
        if (Arguments.template) {
            this.timeOfPlacement = "";
            this.lateralityOfPnb = "";
            this.locationOfPnb = "";
            this.medication = "";
            this.concentration = "";
            this.volume = "";
            this.preProcedureVerbalAnalogueScore = "";
            this.postProcedureVerbalAnalogueScore = "";
            this.blockPurpose = "";
            this.commentsNotesComplications = "";
            this.wantAdditionalBlock = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl");
            selectProcedureDropdownBy = By.id("painNoteForm:selectProcedure");
            singlePeripheralSectionBy = By.id("painNoteForm:j_id1224");
            spnbTimeOfPlacementBy = By.id("painNoteForm:primarySpnb:placementDateDecorate:placementDateInputDate");//*[@id="singlePeripheralPlacementDate1"]
            locationOfPnbDropdownBy = By.id("painNoteForm:primarySpnb:blockLocationDecorate:blockLocation");
            medicationDropdownBy = By.id("painNoteForm:primarySpnb:injectionMedicationDecorate:injectionMedication");
            concentrationFieldBy = By.id("painNoteForm:primarySpnb:injectionConcentrationDecorate:injectionConcentration");
            volumeFieldBy = By.id("painNoteForm:primarySpnb:injectionQtyDecorate:injectionQty");
            preVerbalScoreDropdownBy = By.id("painNoteForm:primarySpnb:preProcVasDecorate:preProcVas");
            postVerbalScoreDropdownBy = By.id("painNoteForm:primarySpnb:postProcVasDecorate:postProcVas");
            blockPurposeDropdownBy = By.id("painNoteForm:primarySpnb:blockPurposeDecorate:blockPurpose");
            commentsTextAreaBy = By.id("painNoteForm:primarySpnb:commentsDecorate:comments");
            createNoteButtonBy = By.id("painNoteForm:createNoteButton");
            painManagementNoteMessageAreaBy = By.xpath("//*[@id=\"painNoteForm:j_id1200\"]/table/tbody/tr/td/span");
            procedureSectionBy = By.id("painNoteForm:Procedure");
        }

    }

    /**
     * Process this kind of procedure note for the specified patient, by filling in the values and saving them
     * @param patient The patient for whom this procedure note applies
     * @return Success of Failure
     */
    public boolean process(Patient patient) {
        By SPNB_LATERALITY_OF_PNB_RADIO_LEFT_LABEL = By.xpath("//*[@id='painNoteForm:primarySpnb:blockLateralityDecorate:blockLaterality']/tbody/tr/td[1]/label");
        By SPNB_LATERALITY_OF_PNB_RADIO_RIGHT_LABEL = By.xpath("//*[@id='painNoteForm:primarySpnb:blockLateralityDecorate:blockLaterality']/tbody/tr/td[2]/label");
        By SPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL = By.xpath("//*[@id='painNoteForm:primarySpnb:primarySpnbDecorator:secondaryBlockInd']/tbody/tr/td[1]/label");
        By SPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL = By.xpath("//*[@id='painNoteForm:primarySpnb:primarySpnbDecorator:secondaryBlockInd']/tbody/tr/td[2]/label");


        if (!Arguments.quiet) System.out.println("        Processing Single Peripheral Nerve Block at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Find and click on the procedure notes tab, and wait for it to finish before trying to add any values to the fields.
        // There are often timing problems in this section.  This should be reviewed.
        //
        logger.fine("\tSinglePeripheralNerveBlock.process(), Will look for procedure notes tab, and then click on it");
        try {
            WebElement procedureNotesTabElement = Utilities.waitForVisibility(procedureNotesTabBy, 10, "SinglePeripheralNerveBlock.process()");
            procedureNotesTabElement.click();
            procedureNotesTabElement.click(); // how about a second click?  Help make it appear?
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // screws stuff up now?  If super slow, no
        }
        catch (StaleElementReferenceException e) {
            logger.fine("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        catch (Exception e) {
            logger.fine("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // fails after "Note(s) successfully created!", I think.
        }
        // The clickTab above restructures the DOM and if you go to the elements on the page too quickly
        // there are problems.  So check that the target section is refreshed.
        try {
            Utilities.waitForRefreshedVisibility(procedureSectionBy,  10, "SinglePeripheralNerveBlock, procedure section");
            logger.fine("SinglePeripheralNerveBlock.process(), I guess we found the procedure section.");
        }
        catch (Exception e) {
            logger.fine("SinglePeripheralNerveBlock.process(), Did not find the procedure section.  Exception caught: " + Utilities.getMessageFirstLine(e));
            return false; // fails:1 (at first startup)  Sometimes it's just not there.  Maybe you have to click twice?
        }
        // What's diff between previous and next section?
        String procedureNoteProcedure = "Single Peripheral Nerve Block";
        Utilities.sleep(1555, "SPNB.process(), about to do dropdown to select procedure");
        try {
            procedureNoteProcedure = Utilities.processDropdown(selectProcedureDropdownBy, procedureNoteProcedure, this.randomizeSection, true);
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), unable to select procedure note procedure. e: " + e.getMessage()); ScreenShot.shoot("SevereError");
            return false;
        }
        if (procedureNoteProcedure == null) {
            logger.severe("SinglePeripheralNerveBlock.process(), unable to get procedure Note Procedure.  Got null back."); ScreenShot.shoot("SevereError");
            return false;
        }
        //(new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // does this screw stuff up now too?
        Utilities.sleep(3555, "SPNB.process(), about to wait for spnb section to show up"); // nec?  Perhaps essential for now.  Was 2555

        try {
//            Utilities.waitForVisibility(singlePeripheralSectionBy, 2, "SinglePeripheralNerveBlock.process()");
            // Next line new 4/30/19
            Utilities.waitForRefreshedVisibility(singlePeripheralSectionBy, 2, "SinglePeripheralNerveBlock.process()");
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), timed out waiting for section after dropdown selection."); ScreenShot.shoot("SevereError");
            return false;
        }


        //
        // Start filling in the fields.
        //
        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.timeOfPlacement = Utilities.processDateTime(spnbTimeOfPlacementBy, this.timeOfPlacement, this.randomizeSection, true);
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.randomizeSection, true, lateralityLeftBy, lateralityRightBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.randomizeSection, true, SPNB_LATERALITY_OF_PNB_RADIO_LEFT_LABEL, SPNB_LATERALITY_OF_PNB_RADIO_RIGHT_LABEL);
        }

        this.locationOfPnb = Utilities.processDropdown(locationOfPnbDropdownBy, this.locationOfPnb, this.randomizeSection, true);
        this.medication = Utilities.processDropdown(medicationDropdownBy, this.medication, this.randomizeSection, true);
        this.concentration = Utilities.processDoubleNumber(concentrationFieldBy, this.concentration, 0.1, 5.0, this.randomizeSection, true);
        this.volume = Utilities.processDoubleNumber(volumeFieldBy, this.volume, 0, 25, this.randomizeSection, true);
        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.randomizeSection, true);
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.randomizeSection, false);
        Utilities.sleep(555, "SPNB.process(), will do add additional block stuff");
        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.randomizeSection, true, yesRadioLabelBy, noRadioLabelBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.randomizeSection, true, SPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL, SPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL);
        }
        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            logger.fine("SinglePeripheralNerveBlock.process(), Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote screenshot file " + fileName);
        }
        //
        // Save the note.
        //
        Instant start;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(createNoteButtonBy, 10, "SinglePeripheralNerveBlock.(), create note button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "SPNB");
            }
            start = Instant.now();
            if (!this.skipSave) {
                createNoteButton.click();
            }
        }
        catch (TimeoutException e) {
            logger.severe("SinglePeripheralNerveBlock.process(), failed to get and click on the create note button.  Timed out.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), failed to get and click on the create note button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        // We need this sleep because of the table that gets populated and inserted prior to the message "Note successfully created!"
        // Otherwise we try to read it, and there's nothing there to read!
        Utilities.sleep(6555, "SPNB.process(), will check 2 conditions next for messages");

        ExpectedCondition<WebElement> problemOnTheServerMessageCondition = ExpectedConditions.visibilityOfElementLocated(problemOnTheServerMessageAreaBy);
        ExpectedCondition<WebElement> successfulMessageCondition = ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy);
        ExpectedCondition<Boolean> successOrServerProblem = ExpectedConditions.or(successfulMessageCondition, problemOnTheServerMessageCondition);
        try {
            (new WebDriverWait(Driver.driver, 10)).until(successOrServerProblem);
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            timerLogger.fine("Exception 1 while processing " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " after " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            return false;
        }
        // Check for "Sorry, there was a problem on the server." message.
        try {
            WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 4)).until(problemOnTheServerMessageCondition); // was 1
            String message = problemOnTheServerElement.getText();
            if (message.contains("problem on the server")) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to save Single Peripheral Nerve Block Note for " +
                            patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                timerLogger.fine("Problem on the server for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " after " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
                return false; // wow, the message no longer displays, but it's still there.
            }
        }
        catch (Exception e) {
            logger.finest("SPNB.process(), Maybe no problem, because we were checking on the server problem.  Continuing... e: " + Utilities.getMessageFirstLine(e));
        }

        // Now we'll check for "successfully".  Compare with CPNB, because that looks more complete.
        try {
            WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(successfulMessageCondition);
            String message = painManagementNoteMessageAreaElement.getText();
            if (!message.isEmpty() && (message.contains("successfully created") || message.contains("sucessfully created"))) {
                logger.finest("SPNB.process(), message indicates success.  Fall through.");
            } else {
                WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 10)).until(problemOnTheServerMessageCondition);
                message = problemOnTheServerElement.getText();
                if (message.contains("problem on the server")) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to save Single Peripheral Nerve Block Note for " +
                                patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                    timerLogger.fine("Problem on the server while processing " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " after " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
                    return false;
                }
                else {
                    logger.finest("Didn't find message saying problem on the server.  So if this problem goes away, fix the code.");
                }
            }
        }
        catch (Exception e) {
            logger.info("SinglePeripheralNerveBlock.process(), exception caught but prob okay?: " + Utilities.getMessageFirstLine(e));
        }
        //
        // Report the results
        //
        timerLogger.info("Single Peripheral Nerve Block note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (!Arguments.quiet) {
            System.out.println("          Saved Single Peripheral Nerve Block note at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "SPNB");
        }
        return true;
    }
}
