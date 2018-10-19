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

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;

public class SinglePeripheralNerveBlock {
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
    //private static By painManagementNoteMessageAreaBy = By.id("pain-note-message");
    private static By painManagementNoteMessageAreaBy = By.id("createNoteMsg");
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
        if (!Arguments.quiet) System.out.println("        Processing Single Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        if (Arguments.debug) System.out.println("\tSinglePeripheralNerveBlock.process(), Will look for procedure notes tab, and then click on it");
        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try { // do this stuff again?  Didn't already do it?
            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            procedureNotesTabElement.click(); // what?  throws stale ref now?
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (StaleElementReferenceException e) { // fails: demo: 1
            if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false; // if this fails again here I'm going to rewrite this piece of sh*t code because of f*ing selenium
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false;
        }
        // That click above does cause an AJAX call, I believe.

        // The clickTab above restructures the DOM and if you go to the elements on the page too quickly
        // there are problems.  So check that the target section is refreshed.
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(procedureSectionBy)));
            if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), I guess we found the procedure section.");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Did not find the procedure section.  Exception caught: " + e.getMessage());
            return false;
        }

        //if (Arguments.debug) System.out.println("Here comes a select procedure dropdown.");
        // !!!!!!!!!!!!!! The following dropdown triggers an AJAX call, so you have to wait after it's done
        String procedureNoteProcedure = "Single Peripheral Nerve Block";

        // Next line is a problem often, stale element reference: 2
        // I think it's because of the click on the Procedure Notes tab, and there hasn't been enough time.
        procedureNoteProcedure = Utilities.processDropdown(selectProcedureDropdownBy, procedureNoteProcedure, this.random, true); // true to go further, and do
        //if (Arguments.debug) System.out.println("Just did a select procedure dropdown.");
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // another one?  Is there ajax on the page here?
        Utilities.sleep(1555); // nec?
        // We really do need to check we're on the right page before proceeding further

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

        //if (Arguments.debug) System.out.println("Here comes a processDateTime");
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // The reason this next line often fails is because we're not on the right page because the
        // "Select Procedure" dropdown didn't work, or wasn't done.
        this.timeOfPlacement = Utilities.processDateTime(spnbTimeOfPlacementBy, this.timeOfPlacement, this.random, true); // fails often
        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Just did a processDateTime");

        // I doubt this helps either
        //Utilities.automationUtils.waitUntilElementIsVisible(By.xpath("//*[@id=\"painNoteForm:primarySpnb:blockLateralityDecorate:blockLaterality\"]/tbody/tr"));

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a processing of radios for laterality.");

        if (isGoldTier) {
            //this.lateralityOfPnb = Utilities.processRadiosByButton(this.lateralityOfPnb, this.random, true, leftRadioButtonBy, rightRadioButtonBy);
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true,
                    By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/div/table/tbody/tr[2]/td[2]/label[1]"), // change the other radios that specified button to label later
                    By.xpath("//*[@id=\"singlePeripheralPainNoteForm1\"]/div/table/tbody/tr[2]/td[2]/label[2]"));
        }
        if (isDemoTier) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true, SPNB_LATERALITY_OF_PNB_RADIO_LEFT_LABEL, SPNB_LATERALITY_OF_PNB_RADIO_RIGHT_LABEL);
        }
        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a dropdown for location.");

        this.locationOfPnb = Utilities.processDropdown(locationOfPnbDropdownBy, this.locationOfPnb, this.random, true);

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a dropdown for medication.");
        this.medication = Utilities.processDropdown(medicationDropdownBy, this.medication, this.random, true);

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a concentration.");
        this.concentration = Utilities.processDoubleNumber(concentrationFieldBy, this.concentration, 0.01, 5.0, this.random, true);

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a volume.");
        this.volume = Utilities.processDoubleNumber(volumeFieldBy, this.volume, 0, 25, this.random, true);

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a dropdown for pre verbal score.");
        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.random, true);

        //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a dropdown for post verbal score.");
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.random, true);


        if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a dropdown for block purpose.");
        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.random, true);


        if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes comments: " + this.commentsNotesComplications);
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, true);
        //if (Arguments.debug) System.out.println("After comments: " + this.commentsNotesComplications);


        // add another block?  A radio button choice here will cause an AJAX call which takes time.  So potential for problems.
        if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Here comes a process radios for want additional block.");
        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (isGoldTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByButton(this.wantAdditionalBlock, this.random, true, yesRadioButtonBy, noRadioButtonBy);
        }
        if (isDemoTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.random, true, SPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL, SPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL);
        }
        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
        }

        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME


        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteButtonBy));
            //System.out.println("The following currently does not cause the form to go back to initial state on Gold!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!????????????????????????????????????????!!!!!!!!!!!!!!!!!!!!!!!!!!!??????????????????????");
            createNoteButton.click(); // need to wait after this  // does this button work in Gold?????????????????????????????????????
           // if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), doing a call to isFinishedAjax");
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
        Utilities.sleep(1555); // maybe we need this when there is a table that gets inserted in front of the "Note successfully created!" message so we can read that message in time.



        // Check to see if the note was created okay
        try {
            ExpectedCondition<WebElement> messageAreaExpectedCondition = ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy);
            //ExpectedCondition<Boolean> messageAreaSaysSuccessfullyCreatedSpelledWrong = ExpectedConditions.textToBePresentInElementLocated(painManagementNoteMessageAreaBy, "Note(s) sucessfully created!");
            try {
                WebElement textArea = (new WebDriverWait(Driver.driver, 10)).until(messageAreaExpectedCondition);
                //Boolean textIsPresent = (new WebDriverWait(Driver.driver, 10)).until(messageAreaSaysSuccessfullyCreatedSpelledWrong);
                //if (Arguments.debug) System.out.println("Wow, so the expected text was there!: " + textArea.getText());
                return true; // If this doesn't work, and there are timing issues with the above, then try the stuff below too.
            }
            catch (Exception e) {
                System.out.println("Exception: " + e.getMessage()); // what?  Continuing on?
            }

            WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(painManagementNoteMessageAreaBy));
            String message = painManagementNoteMessageAreaElement.getText();
            if (message.contains("successfully created") || message.contains("sucessfully created")) {
                //if (Arguments.debug) System.out.println("SinglePeripheralNerveBlock.process(), message indicates good results: " + message);
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed to save Single Peripheral Nerve Block note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  ": " + message);
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + e.getMessage());
            return false;
        }
        return true;
    }
}
