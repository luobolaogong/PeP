package pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter;

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
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

public class EpiduralCatheter {
    private static Logger logger = Logger.getLogger(EpiduralCatheter.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String timeOfPlacement;
    public String levelOfSpineCatheterIsPlaced;
    public String isCatheterTestDosed;
    public String isBolusInjection;  // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public BolusInjection bolusInjection;
    public String isEpiduralInfusion;  // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public EpiduralInfusion epiduralInfusion;
    public String isPatientControlledEpiduralBolus; // = yes/no  PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public PatientControlledEpiduralBolus patientControlledEpiduralBolus;
    public String preProcedureVerbalAnalogueScore;
    public String postProcedureVerbalAnalogueScore;
    public String blockPurpose;
    public String commentsNotesComplications;

    private static By procedureNotesTabBy = By.linkText("Procedure Notes"); // 1/23/19
    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");
    private static By ecTimeOfPlacementBy = By.id("epiduralCatheterPlacementDate");
    private static By ecLevelFieldBy = By.id("levelSpine");

    private static By catheterTestDosedYesLabelBy = By.id("testDoseInd7");
    private static By catheterTestDosedNoLabelBy =  By.id("testDoseInd8");

    private static By messageAreaForCreatingNoteBy = By.id("pain-note-message"); // verified, and again, and doesn't seem to work.  Why?????????
    private static By noteSuccessfullyCreatedBy = By.xpath("//*[@id='pain-note-message']"); // new 1/9/19

    private static By ecBolusInjectionRadioYes = By.id("injectionInd7");
    private static By ecBolusInjectionRadioNo = By.id("injectionInd8");

    private static By ecBolusInjectinDateFieldBy = By.id("epiduralCatheterInjectionDate");
    private static By ecBolusMedicationDropdownBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::select[@id='injectionMedication']");
    private static By ecBolusConcentrationFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='injectionConcentration']");
    private static By ecBolusVolumeFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='injectionQty']");

    private static By ecEpiduralInfusionRadioYesBy = By.id("infusionInd7");
    private static By ecEpiduralInfusionRadioNoBy = By.id("infusionInd8");

    private static By ecEiInfusionRateFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='infusionRate']");
    private static By ecEiInfusionMedicationDropdownBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::select[@id='infusionMedication']");
    private static By ecEiConcentrationFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='infusionConcentration']");
    private static By ecEiVolumeFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='infusionQty']");

    private static By ecPcebRadioYesBy = By.id("pcaInd7");
    private static By ecPcebRadioNoBy = By.id("pcaInd8");

    // these should be changed to By.id("pcaQty"); (or similar) in the future when they get unique ID's.  spnb, cpnb, ivpca and this class share a lot of id's currently.
    private static By ecPcebVolumeFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='pcaQty']");
    private static By ecPcebLockoutFieldBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::input[@id='pcaLockout']");

    private static By ecPreVerbalScoreDropdownBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::select[@id='preProcVas']");
    private static By ecPostVerbalScoreDropdownBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::select[@id='postProcVas']");
    private static By ecBlockPurposeDropdownBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::select[@id='blockPurpose']");
    private static By ecCommentsTextAreaBy = By.xpath("//*[@id='epiduralCatheterPainNoteForm']/descendant::textarea[@id='comments']");


    private static By ecCreateNoteButtonBy = By.xpath("//form[@id='epiduralCatheterPainNoteForm']/descendant::button[text()='Create Note']"); // verified

    private static By EC_BOLUS_INJECTION_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[1]/label");
    private static By EC_BOLUS_INJECTION_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[2]/label");


    public EpiduralCatheter() {
        if (Arguments.template) {
            //this.randomizeSection = null; // don't want this showing up in template
            this.timeOfPlacement = "";
            this.levelOfSpineCatheterIsPlaced = "";
            this.isCatheterTestDosed = "";
            this.isBolusInjection = "";
            this.bolusInjection = new BolusInjection();
            this.isEpiduralInfusion = "";
            this.epiduralInfusion = new EpiduralInfusion();
            this.isPatientControlledEpiduralBolus = "";
            this.patientControlledEpiduralBolus = new PatientControlledEpiduralBolus();
            this.preProcedureVerbalAnalogueScore = "";
            this.postProcedureVerbalAnalogueScore = "";
            this.blockPurpose = "";
            this.commentsNotesComplications = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {

            By EC_BOLUS_INJECTION_DATE_FIELD =
                    By.xpath("//label[.='Bolus Injection Date:']/../../../../../following-sibling::td/span/input[1]");
            By EC_BOLUS_MEDICATION_DROPDOWN = By.xpath("//label[.='Bolus Medication:']/../following-sibling::td/select");
            By EC_BOLUS_CONCENTRATION_FIELD =
                    By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
            By EC_BOLUS_VOLUME_FIELD =
                    By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[2]/td/div/div/table/tbody/tr/td/input");
            // Epidural Infusion

            // I did this:
            By EC_EPIDURAL_INFUSION_RADIO_YES_LABEL =
                    By.xpath("//*[@id='painNoteForm:InfusionFields:infusionInd']/tbody/tr/td[1]/label");
            By EC_EPIDURAL_INFUSION_RADIO_NO_LABEL =
                    By.xpath("//*[@id='painNoteForm:InfusionFields:infusionInd']/tbody/tr/td[2]/label");


            By EC_EI_INFUSION_RATE_FIELD = By.xpath("//label[.='Infusion Rate:']/../following-sibling::td/input");
            By EC_EI_INFUSION_MEDICATION_DROPDOWN = By.xpath("//label[.='Infusion Medication:']/../following-sibling::td/select");
            By EC_EI_CONCENTRATION_FIELD =
                    By.xpath("//label[.='Infusion Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
            By EC_EI_VOLUME_FIELD = By.xpath("//label[.='Volume to be Infused:']/../following-sibling::td/input");

            By EC_PCEB_RADIO_YES_LABEL =
                    By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[1]/label");
            By EC_PCEB_RADIO_NO_LABEL =
                    By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[2]/label");

            By EC_PRE_VERBAL_SCORE_DROPDOWN =
                    By.xpath("//label[contains(text(),'Pre-Procedure Verbal Analogue Score')]/../following-sibling::td/select");
            By EC_POST_VERBAL_SCORE_DROPDOWN =
                    By.xpath("//label[contains(text(),'Post-Procedure Verbal Analogue Score')]/../following-sibling::td/select");
            By EC_BLOCK_PURPOSE_DROPDOWN = By.xpath("//label[.='Block Purpose:']/../following-sibling::td/select");
            By EC_CREATE_NOTE_BUTTON = By.id("painNoteForm:createNoteButton");

            By EC_COMMENTS_TEXTAREA = By.xpath("//label[.='Comments/Notes/Complications:']/../following-sibling::td/textarea");
            // Procedure Notes
            By PN_SELECT_PROCEDURE_DROPDOWN = By.id("painNoteForm:selectProcedure");

            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // this is the tab
            dropdownForSelectProcedureBy = PN_SELECT_PROCEDURE_DROPDOWN; //By.id("painNoteForm:selectProcedure");
            ecTimeOfPlacementBy = By.id("painNoteForm:placementDateDecorate:placementDateInputDate");
            ecLevelFieldBy = By.id("painNoteForm:levelSpineDecorate:levelSpine"); // validated, but often fails
            catheterTestDosedYesLabelBy = By.xpath("//*[@id='painNoteForm:testDoseIndDecorate:testDoseInd']/tbody/tr/td[1]/label");
            catheterTestDosedNoLabelBy =  By.xpath("//*[@id='painNoteForm:testDoseIndDecorate:testDoseInd']/tbody/tr/td[2]/label");
            ecBolusInjectionRadioYes = EC_BOLUS_INJECTION_RADIO_YES_LABEL;
            ecBolusInjectionRadioNo = EC_BOLUS_INJECTION_RADIO_NO_LABEL;
            ecBolusInjectinDateFieldBy = EC_BOLUS_INJECTION_DATE_FIELD;
            ecBolusMedicationDropdownBy = EC_BOLUS_MEDICATION_DROPDOWN;
            ecBolusConcentrationFieldBy = EC_BOLUS_CONCENTRATION_FIELD; // this is wacko stuff
            ecBolusVolumeFieldBy = EC_BOLUS_VOLUME_FIELD;
            ecEpiduralInfusionRadioYesBy = EC_EPIDURAL_INFUSION_RADIO_YES_LABEL;
            ecEpiduralInfusionRadioNoBy = EC_EPIDURAL_INFUSION_RADIO_NO_LABEL;
            ecEiInfusionRateFieldBy = EC_EI_INFUSION_RATE_FIELD;
            ecEiInfusionMedicationDropdownBy = EC_EI_INFUSION_MEDICATION_DROPDOWN;
            ecEiConcentrationFieldBy = EC_EI_CONCENTRATION_FIELD;
            ecEiVolumeFieldBy = EC_EI_VOLUME_FIELD;
            ecPcebRadioYesBy = EC_PCEB_RADIO_YES_LABEL;
            ecPcebRadioNoBy = EC_PCEB_RADIO_NO_LABEL;
            //ecPcebVolumeFieldBy = EC_PCEB_VOLUME_FIELD;
            ecPcebVolumeFieldBy = By.id("painNoteForm:PCEAVolumeFields:pcaQty");
            //ecPcebLockoutFieldBy = EC_PCEB_VOLUME_FIELD;
            ecPcebLockoutFieldBy = By.id("painNoteForm:PCEAVLockoutFields:pcaLockout");

            ecPreVerbalScoreDropdownBy = EC_PRE_VERBAL_SCORE_DROPDOWN;
            ecPostVerbalScoreDropdownBy = EC_POST_VERBAL_SCORE_DROPDOWN;
            ecBlockPurposeDropdownBy = EC_BLOCK_PURPOSE_DROPDOWN;
            ecCommentsTextAreaBy = EC_COMMENTS_TEXTAREA;
            ecCreateNoteButtonBy = EC_CREATE_NOTE_BUTTON;
            messageAreaForCreatingNoteBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span");
        }

    }

    /**
     * Process this kind of procedure note that is part of a Pain Management Note, for the specified patient,
     * by filling in the values and saving them.
     * @param patient The patient for whom this procedure note applies
     * @return Success of Failure
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("        Processing Epidural Catheter for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Find and click on the procedure notes tab, and wait for it to finish before trying to add any values to the fields.
        // There are often timing problems in this section.  This should be reviewed.
        //
        try {
            logger.fine("EpiduralCatheter.process() gunna wait for visibility of procedure notes tab.");
            WebElement procedureNotesTabElement = Utilities.waitForRefreshedVisibility(procedureNotesTabBy,  10, "EpiduralCatheter.(), procedure Notes tab");
            logger.fine("EpiduralCatheter.process() got the tab, gunna click it.");
            procedureNotesTabElement.click();
            logger.fine("EpiduralCatheter.process() clicked the tab, gunna wait for ajax to finish");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
            logger.fine("EpiduralCatheter.process() ajax done, gunna sleep");
            Utilities.sleep(555, "EpiduralCatheter");
            logger.fine("EpiduralCatheter.process() done sleeping.");
        }
        catch (StaleElementReferenceException e) {
            logger.severe("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Stale element ref exception."); ScreenShot.shoot("SevereError");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        catch (Exception e) {
            logger.severe("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        String procedureNoteProcedure = "Epidural Catheter";
        Utilities.sleep(555, "EpiduralCatheter");
        procedureNoteProcedure = Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.randomizeSection, true);
        Utilities.sleep(555, "EpiduralCatheter");


        //
        // Start filling in the fields.
        //
        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        try {
            Utilities.waitForVisibility(ecTimeOfPlacementBy, 3, "EpiduralCatheter.process()");
            this.timeOfPlacement = Utilities.processDateTime(ecTimeOfPlacementBy, this.timeOfPlacement, this.randomizeSection, true); // fails often
        }
        catch (Exception e) {
            logger.fine("EpiduralCatheter.process(), didn't get the Time of Placement text box.");
            return false;
        }
        Utilities.sleep(1555, "EpiduralCatheter"); // was 555
        this.levelOfSpineCatheterIsPlaced = Utilities.processText(ecLevelFieldBy, this.levelOfSpineCatheterIsPlaced, Utilities.TextFieldType.EC_SPINE_LEVEL, this.randomizeSection, true);

        // The catheter has to be test dosed in order to continue, so if not specified, or if set to "random", it must be set to Yes
        if (this.isCatheterTestDosed == null || this.isCatheterTestDosed.isEmpty() || this.isCatheterTestDosed.equalsIgnoreCase("random")) {
            this.isCatheterTestDosed = "Yes";
        }
        // All the following radio buttons can be done by Button rather than Label because of structure and no multi-word labels.
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // next line is failing now
            this.isCatheterTestDosed = Utilities.processRadiosByButton(this.isCatheterTestDosed, this.randomizeSection, true, catheterTestDosedYesLabelBy, catheterTestDosedNoLabelBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) { // next line causes problem with Epidural Catheter "Catheter test dosed" No.  Yes is okay.
            this.isCatheterTestDosed = Utilities.processRadiosByLabel(this.isCatheterTestDosed, this.randomizeSection, true, catheterTestDosedYesLabelBy, catheterTestDosedNoLabelBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isBolusInjection = Utilities.processRadiosByButton(this.isBolusInjection, this.randomizeSection, true, ecBolusInjectionRadioYes, ecBolusInjectionRadioNo);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isBolusInjection = Utilities.processRadiosByLabel(this.isBolusInjection, this.randomizeSection, true, EC_BOLUS_INJECTION_RADIO_YES_LABEL, EC_BOLUS_INJECTION_RADIO_NO_LABEL);
        }
        if (this.isBolusInjection != null &&this.isBolusInjection.equalsIgnoreCase("Yes")) { // npe next line
            // maybe make this look like others above, later
            if (this.bolusInjection == null) {
                this.bolusInjection = new BolusInjection();
            }
            if (this.bolusInjection.randomizeSection == null) {
                this.bolusInjection.randomizeSection = this.randomizeSection; // removed setting to false if null
            }
            if (this.bolusInjection.shoot == null) {
                this.bolusInjection.shoot = this.shoot;
            }

            this.bolusInjection.bolusInjectionDate = Utilities.processText(ecBolusInjectinDateFieldBy, this.bolusInjection.bolusInjectionDate, Utilities.TextFieldType.DATE_TIME, this.randomizeSection, true);
            this.bolusInjection.bolusMedication = Utilities.processDropdown(ecBolusMedicationDropdownBy, this.bolusInjection.bolusMedication, this.randomizeSection, true);
            this.bolusInjection.concentration = Utilities.processDoubleNumber(ecBolusConcentrationFieldBy, this.bolusInjection.concentration, 0.1, 5.0, this.randomizeSection, true);
            this.bolusInjection.volume = Utilities.processDoubleNumber(ecBolusVolumeFieldBy, this.bolusInjection.volume, 0, 25, this.randomizeSection, true);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isEpiduralInfusion = Utilities.processRadiosByLabel(this.isEpiduralInfusion, this.randomizeSection, true, ecEpiduralInfusionRadioYesBy, ecEpiduralInfusionRadioNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isEpiduralInfusion = Utilities.processRadiosByButton(this.isEpiduralInfusion, this.randomizeSection, true, ecEpiduralInfusionRadioYesBy, ecEpiduralInfusionRadioNoBy);
        }
        if (this.isEpiduralInfusion != null && this.isEpiduralInfusion.equalsIgnoreCase("Yes")) {
            // maybe fix like above, later
            if (this.epiduralInfusion == null) {
                this.epiduralInfusion = new EpiduralInfusion();
            }
            if (this.epiduralInfusion.randomizeSection == null) {
                this.epiduralInfusion.randomizeSection = this.randomizeSection; // removed setting to false if null
            }
            if (this.epiduralInfusion.shoot == null) {
                this.epiduralInfusion.shoot = this.shoot;
            }
            EpiduralInfusion epiduralInfusion = this.epiduralInfusion;

            epiduralInfusion.infusionRate = Utilities.processDoubleNumber(ecEiInfusionRateFieldBy, epiduralInfusion.infusionRate, 0, 5, epiduralInfusion.randomizeSection, true);
            epiduralInfusion.infusionMedication = Utilities.processDropdown(ecEiInfusionMedicationDropdownBy, epiduralInfusion.infusionMedication, epiduralInfusion.randomizeSection, true);
            epiduralInfusion.concentration = Utilities.processDoubleNumber(ecEiConcentrationFieldBy, epiduralInfusion.concentration, 0.1, 5.0, epiduralInfusion.randomizeSection, true);
            epiduralInfusion.volumeToBeInfused = Utilities.processDoubleNumber(ecEiVolumeFieldBy, epiduralInfusion.volumeToBeInfused, 0, 25, epiduralInfusion.randomizeSection, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isPatientControlledEpiduralBolus = Utilities.processRadiosByLabel(this.isPatientControlledEpiduralBolus, this.randomizeSection, true, ecPcebRadioYesBy, ecPcebRadioNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isPatientControlledEpiduralBolus = Utilities.processRadiosByButton(this.isPatientControlledEpiduralBolus, this.randomizeSection, true, ecPcebRadioYesBy, ecPcebRadioNoBy);
        }
        if (this.isPatientControlledEpiduralBolus != null && this.isPatientControlledEpiduralBolus.equalsIgnoreCase("Yes")) { // npe on next line

            // make like above later
            if (this.patientControlledEpiduralBolus == null) {
                this.patientControlledEpiduralBolus = new PatientControlledEpiduralBolus();
            }
            if (this.patientControlledEpiduralBolus.randomizeSection == null) {
                this.patientControlledEpiduralBolus.randomizeSection = this.randomizeSection; // removed setting to false if null // check this one
            }
            if (this.patientControlledEpiduralBolus.shoot == null) {
                this.patientControlledEpiduralBolus.shoot = this.shoot; // check this one
            }

            PatientControlledEpiduralBolus patientControlledEpiduralBolus = this.patientControlledEpiduralBolus;
            this.patientControlledEpiduralBolus.volume = Utilities.processDoubleNumber(ecPcebVolumeFieldBy, patientControlledEpiduralBolus.volume, 0, 5, this.randomizeSection, true);
            this.patientControlledEpiduralBolus.lockout = Utilities.processDoubleNumber(ecPcebLockoutFieldBy, patientControlledEpiduralBolus.lockout, 0, 60, this.randomizeSection, true);
        }

        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(ecPreVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(ecPostVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.blockPurpose = Utilities.processDropdown(ecBlockPurposeDropdownBy, this.blockPurpose, this.randomizeSection, true);

        this.commentsNotesComplications = Utilities.processText(ecCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.randomizeSection, false);

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote screenshot file " + fileName);
        }

        //
        // Save the note.
        //
        Instant start = null;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(ecCreateNoteButtonBy, 10, "EpiduralCatheter.(), create note button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "EpiduralCatheter");
            }
            start = Instant.now();
            createNoteButton.click();
        }
        catch (Exception e) {
            logger.warning("EpiduralCatheter.process(), couldn't get or click on the createNoteButton: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
        }

        try {
            WebElement messageArea = Utilities.waitForPresence(messageAreaForCreatingNoteBy, 10, "EpiduralCatheter.process()");
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.stalenessOf(messageArea)); // was 3, 10, 15
        }
        catch (Exception e) {
            logger.finest("EpiduralCatheter.process(), couldn't wait for or get message area and then wait for staleness. Continuing.  E: " + Utilities.getMessageFirstLine(e));
        }
        WebElement result = null;
        try {
            result = Utilities.waitForVisibility(messageAreaForCreatingNoteBy, 15, "EpiduralCatheter.process()"); // was 3, 10
        }
        catch (TimeoutException e) {
            logger.severe("EpiduralCatheter.process(), Timeout exception, couldn't get message result from trying to save note.  E: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // fails: demo: 3 gold: 1  no problem if wait long enough
        }
        catch (Exception e) {
            System.out.println("E: " + e.getMessage());
            logger.severe("EpiduralCatheter.process(), couldn't get message result from trying to save note.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // fails: demo: 3 gold: 1  no problem if wait long enough
        }
        String someTextMaybe = result.getText(); // often get "" here
        if (someTextMaybe.contains("successfully") || someTextMaybe.contains("sucessfully")) {
            logger.fine("EpiduralCatheter.process() successfully saved the note.");
        }
        else {
            if (!Arguments.quiet) System.err.println("          ***Failed to save Epidural Catheter note for "
                    + patient.patientSearch.firstName + " "
                    + patient.patientSearch.lastName
                    + " ssn:" + patient.patientSearch.ssn
                    +  (someTextMaybe.isEmpty() ? "" : " message: " + someTextMaybe));
            logger.warning("EpiduralCatheter.process(), Failed to save Epidural Catheter.  Was there a failure listed on the page? message: ->" + someTextMaybe + "<-");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // fails: 2  due to dates, failed due to missing level of spine text
        }
        if (!Arguments.quiet) {
            System.out.println("          Saved Epidural Catheter note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.fine("Epidural Catheter note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "EpiduralCatheter");
        }
        return true;
    }
}
