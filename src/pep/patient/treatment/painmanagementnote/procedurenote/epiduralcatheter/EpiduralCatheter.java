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
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String timeOfPlacement; // "MM/DD/YYYY HHMM Z, required";
    public String levelOfSpineCatheterIsPlaced; // "text";
    public String isCatheterTestDosed; // = z yes/no
    public String isBolusInjection;  // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public BolusInjection bolusInjection;
    public String isEpiduralInfusion;  // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public EpiduralInfusion epiduralInfusion;
    public String isPatientControlledEpiduralBolus; // = yes/no  PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public PatientControlledEpiduralBolus patientControlledEpiduralBolus;
    public String preProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String postProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String blockPurpose; // "option 1-3";
    public String commentsNotesComplications; // "text";

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

    // On gold:  <button type="submit" style="background-color: #990000">Create Note</button>

//    public static final By EC_LEVEL_FIELD = By
//            .xpath("//label[.='Level of spine catheter is placed:']/../following-sibling::td/input");

    // I did these:
    public static final By EC_BOLUS_INJECTION_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[1]/label");
    public static final By EC_BOLUS_INJECTION_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[2]/label");

    public static final By EC_BOLUS_INJECTION_DATE_FIELD =
            By.xpath("//label[.='Bolus Injection Date:']/../../../../../following-sibling::td/span/input[1]");
    public static final By EC_BOLUS_MEDICATION_DROPDOWN = By.xpath("//label[.='Bolus Medication:']/../following-sibling::td/select");
    public static final By EC_BOLUS_CONCENTRATION_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    public static final By EC_BOLUS_VOLUME_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[2]/td/div/div/table/tbody/tr/td/input");
    // Epidural Infusion

    // I did this:
    public static final By EC_EPIDURAL_INFUSION_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:InfusionFields:infusionInd']/tbody/tr/td[1]/label");
    public static final By EC_EPIDURAL_INFUSION_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:InfusionFields:infusionInd']/tbody/tr/td[2]/label");


    public static final By EC_EI_INFUSION_RATE_FIELD = By.xpath("//label[.='Infusion Rate:']/../following-sibling::td/input");
    public static final By EC_EI_INFUSION_MEDICATION_DROPDOWN = By.xpath("//label[.='Infusion Medication:']/../following-sibling::td/select");
    public static final By EC_EI_CONCENTRATION_FIELD =
            By.xpath("//label[.='Infusion Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    public static final By EC_EI_VOLUME_FIELD = By.xpath("//label[.='Volume to be Infused:']/../following-sibling::td/input");

    public static final By EC_PCEB_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[1]/label");
    public static final By EC_PCEB_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[2]/label");

//    public static final By EC_PCEB_VOLUME_FIELD =
//            By.xpath("//label[.='Lockout:']/../../../../../../../../preceding-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
//    public static final By EC_PCEB_LOCKOUT_FIELD = By
//            .xpath("//label[.='Lockout:']/../following-sibling::td/input");
    public static final By EC_PRE_VERBAL_SCORE_DROPDOWN =
            By.xpath("//label[contains(text(),'Pre-Procedure Verbal Analogue Score')]/../following-sibling::td/select");
    public static final By EC_POST_VERBAL_SCORE_DROPDOWN =
            By.xpath("//label[contains(text(),'Post-Procedure Verbal Analogue Score')]/../following-sibling::td/select");
    public static final By EC_BLOCK_PURPOSE_DROPDOWN = By.xpath("//label[.='Block Purpose:']/../following-sibling::td/select");
    public static final By EC_CREATE_NOTE_BUTTON = By.id("painNoteForm:createNoteButton");

    public static final By EC_COMMENTS_TEXTAREA = By.xpath("//label[.='Comments/Notes/Complications:']/../following-sibling::td/textarea");
    // Procedure Notes
    public static final By PN_SELECT_PROCEDURE_DROPDOWN = By.id("painNoteForm:selectProcedure");


    public EpiduralCatheter() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
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
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl"); // this is the tab
            dropdownForSelectProcedureBy = PN_SELECT_PROCEDURE_DROPDOWN; //By.id("painNoteForm:selectProcedure");
            ecTimeOfPlacementBy = By.id("painNoteForm:placementDateDecorate:placementDateInputDate");
            //ecLevelFieldBy = EC_LEVEL_FIELD;
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

    // Perhaps this method and the other 3 should start with navigation from the very top rather than assume we're sitting somewhere.
    public boolean process(Patient patient) { // lots of problems with timing here, I think.
        if (!Arguments.quiet) System.out.println("        Processing Epidural Catheter for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );



        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try {
            logger.fine("EpiduralCatheter.process() gunna wait for visibility of procedure notes tab.");
            //WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            WebElement procedureNotesTabElement = Utilities.waitForRefreshedVisibility(procedureNotesTabBy,  10, "EpiduralCatheter.(), procedure Notes tab");
            logger.fine("EpiduralCatheter.process() got the tab, gunna click it.");
            procedureNotesTabElement.click();
            logger.fine("EpiduralCatheter.process() clicked the tab, gunna wait for ajax to finish");
           // Utilities.sleep(1002); // Hate to do this, but how do you find out when AJAX is done?
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
            logger.fine("EpiduralCatheter.process() ajax done, gunna sleep");
            Utilities.sleep(555); // hate to do this, but I lack faith in isFinishedAjax()
            logger.fine("EpiduralCatheter.process() done sleeping.");
        }
        catch (StaleElementReferenceException e) {
            logger.severe("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Stale element ref exception.");
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        catch (Exception e) {
            logger.severe("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        // Following is strange.  Why not use the value from JSON file for the Select Procedure dropdown?
        String procedureNoteProcedure = "Epidural Catheter";
        Utilities.sleep(555); // spnb usually fails at the next line, so trying a sleep there, but will put one here too for consistency
        procedureNoteProcedure = Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.random, true); // true to go further, and do
        //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // prob no ajax // removed 11/24/18
        Utilities.sleep(555); // hate to do this, but I lack faith in isFinishedAjax()

        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        try {
            Utilities.waitForVisibility(ecTimeOfPlacementBy, 3, "EpiduralCatheter.process()");
            this.timeOfPlacement = Utilities.processDateTime(ecTimeOfPlacementBy, this.timeOfPlacement, this.random, true); // fails often
        }
        catch (Exception e) {
            logger.fine("EpiduralCatheter.process(), didn't get the Time of Placement text box.");
            return false;
        }
        // I'll guess that it's the date processing that's taking too long, and it wipes out the text in level of spine
        // So until I find a better way, here comes a sleep.  Possibly this may need to be done for the other 3 procedure notes.
        Utilities.sleep(555);
        // Perhaps L1 through L4? // failures: 1 11/26/18, 1 12/12/18  Possibly a speed issue.  processText cannot look up random values fast enough?
        this.levelOfSpineCatheterIsPlaced = Utilities.processText(ecLevelFieldBy, this.levelOfSpineCatheterIsPlaced, Utilities.TextFieldType.EC_SPINE_LEVEL, this.random, true);

        // The catheter has to be test dosed in order to continue, so if not specified, or if set to "random", it must be set to Yes
        // This is new, I don't know if correct.  Check it.
        if (this.isCatheterTestDosed == null || this.isCatheterTestDosed.isEmpty() || this.isCatheterTestDosed.equalsIgnoreCase("random")) {
            this.isCatheterTestDosed = "Yes";
        }
// wow, I think rather than this.random it should be this.isRandom.  It's a boolean, isn't it?  Or maybe Boolean.  Big change throughout the code.
        // All the following radio buttons can be done by Button rather than Label because of structure and no multi-word labels.
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // next line is failing now
            this.isCatheterTestDosed = Utilities.processRadiosByButton(this.isCatheterTestDosed, this.random, true, catheterTestDosedYesLabelBy, catheterTestDosedNoLabelBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) { // next line causes problem with Epidural Catheter "Catheter test dosed" No.  Yes is okay.
            this.isCatheterTestDosed = Utilities.processRadiosByLabel(this.isCatheterTestDosed, this.random, true, catheterTestDosedYesLabelBy, catheterTestDosedNoLabelBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isBolusInjection = Utilities.processRadiosByButton(this.isBolusInjection, this.random, true, ecBolusInjectionRadioYes, ecBolusInjectionRadioNo);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isBolusInjection = Utilities.processRadiosByLabel(this.isBolusInjection, this.random, true, EC_BOLUS_INJECTION_RADIO_YES_LABEL, EC_BOLUS_INJECTION_RADIO_NO_LABEL);
        }
        if (this.isBolusInjection != null &&this.isBolusInjection.equalsIgnoreCase("Yes")) { // npe next line
            // maybe make this look like others above, later
            if (this.bolusInjection == null) {
                this.bolusInjection = new BolusInjection();
            }
            if (this.bolusInjection.random == null) {
                this.bolusInjection.random = this.random; // removed setting to false if null
            }
            if (this.bolusInjection.shoot == null) {
                this.bolusInjection.shoot = this.shoot;
            }

            this.bolusInjection.bolusInjectionDate = Utilities.processText(ecBolusInjectinDateFieldBy, this.bolusInjection.bolusInjectionDate, Utilities.TextFieldType.DATE_TIME, this.random, true);
            this.bolusInjection.bolusMedication = Utilities.processDropdown(ecBolusMedicationDropdownBy, this.bolusInjection.bolusMedication, this.random, true);
            this.bolusInjection.concentration = Utilities.processDoubleNumber(ecBolusConcentrationFieldBy, this.bolusInjection.concentration, 0.1, 5.0, this.random, true);
            this.bolusInjection.volume = Utilities.processDoubleNumber(ecBolusVolumeFieldBy, this.bolusInjection.volume, 0, 25, this.random, true);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isEpiduralInfusion = Utilities.processRadiosByLabel(this.isEpiduralInfusion, this.random, true, ecEpiduralInfusionRadioYesBy, ecEpiduralInfusionRadioNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isEpiduralInfusion = Utilities.processRadiosByButton(this.isEpiduralInfusion, this.random, true, ecEpiduralInfusionRadioYesBy, ecEpiduralInfusionRadioNoBy);
        }
        if (this.isEpiduralInfusion != null && this.isEpiduralInfusion.equalsIgnoreCase("Yes")) {
            // maybe fix like above, later
            if (this.epiduralInfusion == null) {
                this.epiduralInfusion = new EpiduralInfusion();
            }
            if (this.epiduralInfusion.random == null) {
                this.epiduralInfusion.random = this.random; // removed setting to false if null
            }
            if (this.epiduralInfusion.shoot == null) {
                this.epiduralInfusion.shoot = this.shoot;
            }
            EpiduralInfusion epiduralInfusion = this.epiduralInfusion;

            epiduralInfusion.infusionRate = Utilities.processDoubleNumber(ecEiInfusionRateFieldBy, epiduralInfusion.infusionRate, 0, 5, epiduralInfusion.random, true);
            epiduralInfusion.infusionMedication = Utilities.processDropdown(ecEiInfusionMedicationDropdownBy, epiduralInfusion.infusionMedication, epiduralInfusion.random, true);
            epiduralInfusion.concentration = Utilities.processDoubleNumber(ecEiConcentrationFieldBy, epiduralInfusion.concentration, 0.1, 5.0, epiduralInfusion.random, true);
            epiduralInfusion.volumeToBeInfused = Utilities.processDoubleNumber(ecEiVolumeFieldBy, epiduralInfusion.volumeToBeInfused, 0, 25, epiduralInfusion.random, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isPatientControlledEpiduralBolus = Utilities.processRadiosByLabel(this.isPatientControlledEpiduralBolus, this.random, true, ecPcebRadioYesBy, ecPcebRadioNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isPatientControlledEpiduralBolus = Utilities.processRadiosByButton(this.isPatientControlledEpiduralBolus, this.random, true, ecPcebRadioYesBy, ecPcebRadioNoBy);
        }
        if (this.isPatientControlledEpiduralBolus != null && this.isPatientControlledEpiduralBolus.equalsIgnoreCase("Yes")) { // npe on next line

            // make like above later
            if (this.patientControlledEpiduralBolus == null) {
                this.patientControlledEpiduralBolus = new PatientControlledEpiduralBolus();
                //this.patientControlledEpiduralBolus = new PatientControlledEpiduralBolus();
            }
            if (this.patientControlledEpiduralBolus.random == null) {
                this.patientControlledEpiduralBolus.random = this.random; // removed setting to false if null // check this one
            }
            if (this.patientControlledEpiduralBolus.shoot == null) {
                this.patientControlledEpiduralBolus.shoot = this.shoot; // check this one
            }

            PatientControlledEpiduralBolus patientControlledEpiduralBolus = this.patientControlledEpiduralBolus;
            this.patientControlledEpiduralBolus.volume = Utilities.processDoubleNumber(ecPcebVolumeFieldBy, patientControlledEpiduralBolus.volume, 0, 5, this.random, true);
            this.patientControlledEpiduralBolus.lockout = Utilities.processDoubleNumber(ecPcebLockoutFieldBy, patientControlledEpiduralBolus.lockout, 0, 60, this.random, true);
        }

        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(ecPreVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.random, true);
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(ecPostVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.random, true);
        this.blockPurpose = Utilities.processDropdown(ecBlockPurposeDropdownBy, this.blockPurpose, this.random, true);

        this.commentsNotesComplications = Utilities.processText(ecCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote screenshot file " + fileName);
        }

        // PROBLEM AREAS COMING.
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.
        // THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME

        Instant start = null;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(ecCreateNoteButtonBy, 10, "EpiduralCatheter.(), create note button");

            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000);
            }
            start = Instant.now();
            createNoteButton.click(); // Can take a long time
        }
        catch (Exception e) { // what kind of exception?  Trap the specific one.
            logger.warning("EpiduralCatheter.process(), couldn't get or click on the createNoteButton: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            // what now, continue on??????????????????????????????
        }



        // somewhere around here it's possible that TMDS will error with "You Have Encountered a Problem" page.



        // We expect a message area, but it doesn't get a value until after the save, which takes a long time.
        // We have to wait on something to happen before we check the string in the message area.
        // At the same time, we can't go too slow (like single stepping in debug) or we won't get a stalenessOf.
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
            logger.severe("EpiduralCatheter.process(), Timeout exception, couldn't get message result from trying to save note.  E: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false; // fails: demo: 3 gold: 1  no problem if wait long enough
        }
        catch (Exception e) {
            System.out.println("E: " + e.getMessage());
            logger.severe("EpiduralCatheter.process(), couldn't get message result from trying to save note.: " + Utilities.getMessageFirstLine(e));
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
        timerLogger.info("Epidural Catheter note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }
}
