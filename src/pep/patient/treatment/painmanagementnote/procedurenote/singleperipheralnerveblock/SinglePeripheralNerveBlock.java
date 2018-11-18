package pep.patient.treatment.painmanagementnote.procedurenote.singleperipheralnerveblock;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.or;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;

public class SinglePeripheralNerveBlock {
    private static Logger logger = Logger.getLogger(SinglePeripheralNerveBlock.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public String timeOfPlacement; // "MM/DD/YYYY HHMM Z, required";
    public String lateralityOfPnb; // "Left or Right, required"; // should have been spnb
    public String locationOfPnb; // "option 1-18, required"; // causes delay for some reason  // should have been spnb
    public String medication; // "option 1-4, required";
    public String concentration; // "percent, required";
    public String volume; // "ml, required";
    public String preProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String postProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String blockPurpose; // "option 1-3";
    public String commentsNotesComplications; // "text";
    public String wantAdditionalBlock; // = yes/no

    // I did these
    public static final By SPNB_LATERALITY_OF_PNB_RADIO_LEFT_LABEL = By.xpath("//*[@id=\"painNoteForm:primarySpnb:blockLateralityDecorate:blockLaterality\"]/tbody/tr/td[1]/label");
    public static final By SPNB_LATERALITY_OF_PNB_RADIO_RIGHT_LABEL = By.xpath("//*[@id=\"painNoteForm:primarySpnb:blockLateralityDecorate:blockLaterality\"]/tbody/tr/td[2]/label");
    public static final By SPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL = By
            .xpath("//*[@id=\"painNoteForm:primarySpnb:primarySpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[1]/label");
    public static final By SPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL = By
            .xpath("//*[@id=\"painNoteForm:primarySpnb:primarySpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[2]/label");

    private static By procedureNotesTabBy = By.xpath("//*[@id=\"procedureNoteTab\"]/a");
    private static By selectProcedureDropdownBy = By.id("procedureNoteTypeBox");
    private static By singlePeripheralSectionBy = By.id("singlePeripheralNerveBlockContainer");
    private static By spnbTimeOfPlacementBy = By.id("singlePeripheralPlacementDate1");
    private static By leftRadioButtonBy = By.id("blockLaterality1");
    private static By rightRadioButtonBy = By.id("blockLaterality2");
    private static By locationOfPnbDropdownBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::select[@id=\"blockLocation\"]");
    private static By medicationDropdownBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::select[@id=\"injectionMedication\"]");
    private static By concentrationFieldBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::input[@id=\"injectionConcentration\"]");
    private static By volumeFieldBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::input[@id=\"injectionQty\"]");
    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::select[@id=\"preProcVas\"]");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::select[@id=\"postProcVas\"]");
    private static By blockPurposeDropdownBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::select[@id=\"blockPurpose\"]"); // correct
    private static By commentsTextAreaBy = By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/descendant::textarea[@id=\"comments\"]");
    private static By yesRadioButtonBy = By.id("additionalBlockYes1");
    private static By noRadioButtonBy = By.id("additionalBlock1");
    private static By createNoteButtonBy = By.xpath("//*[@id=\"singlePeripheralNerveBlockContainer\"]/button[1]"); // correct
    private static By painManagementNoteMessageAreaBy = By.id("pain-note-message"); // works with role 4? verified to be correct id, but does it work?
    private static By problemOnTheServerMessageAreaBy = By.id("createNoteMsg"); // fails with role 4?
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // is this right?


    public SinglePeripheralNerveBlock() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.timeOfPlacement = "";
            this.lateralityOfPnb = "";// should have been spnb
            this.locationOfPnb = ""; // should have been spnb
            this.medication = "";
            this.concentration = "";
            this.volume = "";
            this.preProcedureVerbalAnalogueScore = "";
            this.postProcedureVerbalAnalogueScore = "";
            this.blockPurpose = "";
            this.commentsNotesComplications = "";
            this.wantAdditionalBlock = "";
        }
        if (isDemoTier) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // verified, and again
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

    // The dropdown for Select Procedure should have selected SPNB by this time
    // Wow this is a long one.  Break it up.
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("        Processing Single Peripheral Nerve Block for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        ;

        logger.fine("\tSinglePeripheralNerveBlock.process(), Will look for procedure notes tab, and then click on it");
        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try { // do this stuff again?  Didn't already do it?
            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            procedureNotesTabElement.click(); // what?  throws stale ref now?
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (StaleElementReferenceException e) { // fails: demo: 1
            logger.fine("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false; // if this fails again here I'm going to rewrite this piece of sh*t code because of f*ing selenium
        }
        catch (Exception e) {
            logger.fine("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false;
        }

        // The clickTab above restructures the DOM and if you go to the elements on the page too quickly
        // there are problems.  So check that the target section is refreshed.
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(procedureSectionBy)));
            logger.fine("SinglePeripheralNerveBlock.process(), I guess we found the procedure section.");
        }
        catch (Exception e) {
            logger.fine("SinglePeripheralNerveBlock.process(), Did not find the procedure section.  Exception caught: " + e.getMessage());
            return false;
        }

        String procedureNoteProcedure = "Single Peripheral Nerve Block";

        procedureNoteProcedure = Utilities.processDropdown(selectProcedureDropdownBy, procedureNoteProcedure, this.random, true); // true to go further, and do
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // another one?  Is there ajax on the page here?
        Utilities.sleep(1555); // nec?

        try {
            (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(singlePeripheralSectionBy));
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("SinglePeripheralNerveBlock.process(), timed out waiting for section after dropdown selection.");
            return false;
        }

        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }

        this.timeOfPlacement = Utilities.processDateTime(spnbTimeOfPlacementBy, this.timeOfPlacement, this.random, true); // fails often

        if (isGoldTier) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true,
                    By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/div/table/tbody/tr[2]/td[2]/label[1]"), // change the other radios that specified button to label later
                    By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/div/table/tbody/tr[2]/td[2]/label[2]"));
        }
        if (isDemoTier) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true, SPNB_LATERALITY_OF_PNB_RADIO_LEFT_LABEL, SPNB_LATERALITY_OF_PNB_RADIO_RIGHT_LABEL);
        }

        this.locationOfPnb = Utilities.processDropdown(locationOfPnbDropdownBy, this.locationOfPnb, this.random, true);
        this.medication = Utilities.processDropdown(medicationDropdownBy, this.medication, this.random, true);
        this.concentration = Utilities.processDoubleNumber(concentrationFieldBy, this.concentration, 0.01, 5.0, this.random, true);
        this.volume = Utilities.processDoubleNumber(volumeFieldBy, this.volume, 0, 25, this.random, true);
        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.random, true);
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.random, true);
        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.random, true);
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, true);
        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (isGoldTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByButton(this.wantAdditionalBlock, this.random, true, yesRadioButtonBy, noRadioButtonBy);
        }
        if (isDemoTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.random, true, SPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL, SPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL);
        }
        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            logger.fine("SinglePeripheralNerveBlock.process(), Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
        }

        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME

        // The next click can cause a "Sorry, there was a problem on the server." to show up underneath that dropdown for Single Peripheral Nerve Block.
        // How do you account for that?
        Instant start = null;
        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteButtonBy));

            start = Instant.now();
            createNoteButton.click(); // need to wait after this  // does this button work in Gold?????????????????????????????????????
//            timerLogger.info("Single Peripheral Nerve Block note save took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");

            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this help at all?  Seems not.  Blasts through?
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("SinglePeripheralNerveBlock.process(), failed to get get and click on the create note button(?).  Unlikely.  Exception: " + e.getMessage());
            return false;
        }

        // We need this sleep because of the table that gets populated and inserted prior to the message "Note successfully created!"
        // Otherwise we try to read it, and there's nothing there to read!
        // How do you know how long it takes to update that table?  What would trigger when it's finished?
        // A test to see if ajax is finished?
        Utilities.sleep(2555); // was 1555, which probably wasn't long enough.  maybe we need this when there is a table that gets inserted in front of the "Note successfully created!" message so we can read that message in time.



        // Check to see if the note was created okay
//       try { // this looks wrong
//            ExpectedCondition<WebElement> messageAreaExpectedCondition = ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy);
//            try {
//                // Might want to do a staleness on this.  That is, we may have a message hanging over from a previous operation
//                WebElement textArea = (new WebDriverWait(Driver.driver, 10)).until(messageAreaExpectedCondition);
//                String message = textArea.getText();
//                if (message.contains("successfully") || message.contains("sucessfully")) {
//                    return true; // If this doesn't work, and there are timing issues with the above, then try the stuff below too.
//                }
//            }
//            catch (Exception e) {
//                System.out.println("SPNB.proess(), didn't find a note message area, continuing.  Maybe okay? Exception: " + e.getMessage().substring(0,40)); // what?  Continuing on?
//            }

        // id="createNoteMsg" Can have the text Sorry, there was a problem on the server.
        ExpectedCondition<WebElement> problemOnTheServerMessageCondition = ExpectedConditions.visibilityOfElementLocated(problemOnTheServerMessageAreaBy);
        ExpectedCondition<WebElement> successfulMessageCondition = ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy);
        ExpectedCondition<Boolean> successOrServerProblem = ExpectedConditions.or(successfulMessageCondition, problemOnTheServerMessageCondition);
        try {
            boolean whatever = (new WebDriverWait(Driver.driver, 10)).until(successOrServerProblem);
        }
        catch (Exception e) {
            System.out.println("Didn't get either condition?");
            if (Arguments.debug) System.err.println("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + e.getMessage().substring(0,40));
            return false;
        }

        // At this point we should have one or the other message showing up (assuming a previous message was erased in time)
        // We'll check for the "Sorry, there was a problem on the server." message first
        try {
            WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 1)).until(problemOnTheServerMessageCondition);
            String message = problemOnTheServerElement.getText();
            if (message.contains("problem on the server")) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to save Single Peripheral Nerve Block note for " +
                            patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + ": " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("SPNB.process(), Exception caught while waiting fora message indicating a problem.  Maybe there was no problem.  Continuing...");
        }




        // Now we'll check for "successfully"
        try {
            //WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy));
            WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(successfulMessageCondition);
            //WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(successfulMessageCondition);
            String message = painManagementNoteMessageAreaElement.getText();
            if (!message.isEmpty() && (message.contains("successfully created") || message.contains("sucessfully created"))) { // yes, they haven't fixed the spelling on this yet
                //logger.fine("SinglePeripheralNerveBlock.process(), message indicates good results: " + message);
                //return true; // let it fall through to the end and return true there
                logger.finest("We're good.  fall through.");
            } else {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to save Single Peripheral Nerve Block note for " +
                            patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + ": " + message);
                //return false;
                WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 10)).until(problemOnTheServerMessageCondition);
                message = problemOnTheServerElement.getText();
                if (message.contains("problem on the server")) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to save Single Peripheral Nerve Block note for " +
                                patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + ": " + message);
                    return false;
                }

            }
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), exception caught but prob okay?: " + e.getMessage());
        }



//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.err.println("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + e.getMessage().substring(0,40));
//            return false;
//        }
        timerLogger.info("Single Peripheral Nerve Block note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }
}
