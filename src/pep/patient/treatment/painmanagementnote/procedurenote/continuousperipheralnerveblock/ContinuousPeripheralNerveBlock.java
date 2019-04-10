package pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock;

import org.openqa.selenium.By;
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

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

/**
 * This class represents one kind of procedure note, that is part of a Pain Management Note,
 * and is used to process the note by filling in the field values and saving it.
 */
public class ContinuousPeripheralNerveBlock {
    private static Logger logger = Logger.getLogger(ContinuousPeripheralNerveBlock.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String timeOfPlacement;
    public String lateralityOfPnb;
    public String locationOfPnb;

    public String isCatheterTunneled;
    public String isCatheterTestDosed;
    public String isBolusInjection; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS

    public BolusInjection bolusInjection;

    public String isCatheterInfusion; // = yes/no // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public CatheterInfusion catheterInfusion;

    public String isPatientContolledBolus; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public PatientControlledBolusCpnb patientControlledBolus;

    public String preProcedureVerbalAnalogueScore;
    public String postProcedureVerbalAnalogueScore;
    public String blockPurpose;
    public String commentsNotesComplications;
    public String wantAdditionalBlock;

    private static By CPNB_LATERALITY_OF_CPNB_RADIO_LEFT_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:blockLateralityDecorate:blockLaterality']/tbody/tr/td[1]/label");
    private static By CPNB_LATERALITY_OF_CPNB_RADIO_RIGHT_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:blockLateralityDecorate:blockLaterality']/tbody/tr/td[2]/label");
    private static By CPNB_LOCATION_OF_CPNB_DROPDOWN = By.xpath("//label[.='Location of CPNB:']/../following-sibling::td/select");
    private static By CPNB_CATHETER_TUNNELED_RADIO_YES_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd']/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_TUNNELED_RADIO_NO_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd']/tbody/tr/td[2]/label");
    private static By CPNB_CATHETER_TEST_DOSED_RADIO_YES_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:testDoseIndDecorate:testDoseInd']/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_TEST_DOSED_RADIO_NO_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:testDoseIndDecorate:testDoseInd']/tbody/tr/td[2]/label");

    // Bolus Injection
    private static By CPNB_BOLUS_INJECTION_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:primaryCpnb:injectionIndDecorate:injectionInd']/tbody/tr/td[1]/label");
    private static By CPNB_BOLUS_INJECTION_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:primaryCpnb:injectionIndDecorate:injectionInd']/tbody/tr/td[2]/label");


    private static By CPNB_BOLUS_INJECTION_DATE_FIELD =
            By.xpath("//label[.='Bolus Injection Date:']/../../../../../following-sibling::td/span/input[1]");
    private static By CPNB_BOLUS_MEDICATION_DROPDOWN = By.xpath("//label[.='Bolus Medication:']/../following-sibling::td/select");
    private static By CPNB_BOLUS_CONCENTRATION_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_BOLUS_VOLUME_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[2]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_CATHETER_INFUSION_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:primaryCpnb:InfusionFields:infusionInd']/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_INFUSION_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:primaryCpnb:InfusionFields:infusionInd']/tbody/tr/td[2]/label");


    private static By CPNB_CI_INFUSION_RATE_FIELD = By.xpath("//label[.='Infusion Rate:']/../following-sibling::td/input");
    private static By CPNB_CI_INFUSION_MEDICATION_DROPDOWN = By.xpath("//label[.='Infusion Medication:']/../following-sibling::td/select");
    private static By CPNB_CI_CONCENTRATION_FIELD = By.xpath("//label[.='Infusion Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_CI_VOLUME_FIELD = By.xpath("//label[.='Volume to be Infused:']/../following-sibling::td/input");
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd']/tbody/tr/td[1]/label");
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL = By.xpath("//*[@id='painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd']/tbody/tr/td[2]/label");


    private static By messageAreaForCreatingNoteBy = By.xpath("//div[@id='procedureNoteTab']/preceding-sibling::div[1]"); // new 10/19/20

    private static By sorryThereWasAProblemOnTheServerBy = By.id("createNoteMsg"); // verified
    private static By procedureNotesTabBy = By.linkText("Procedure Notes"); // 1/23/19
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // is this right?
    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");
    private static By timeOfPlacementFieldBy = By.id("continuousPeripheralPlacementDate1");
    private static By leftRadioButtonLabelBy = By.xpath("//label[@for='blockLaterality7']");
    private static By rightRadioButtonLabelBy = By.xpath("//label[@for='blockLaterality8']");
    private static By locationOfCpnbDropdownBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::select[@id='blockLocation']");
    private static By cpnbCatheterTunneledRadioYesBy = By.id("catheterTunneledInd1");
    private static By cpnbCatheterTunneledRadioNoBy = By.id("catheterTunneledInd2");
    private static By cpnbCatheterTestDosedRadioYesBy = By.id("testDoseInd1");
    private static By cpnbCatheterTestDosedRadioNoBy = By.id("testDoseInd2");
    private static By cpnbBolusInjectionRadioYesBy = By.id("injectionInd1");
    private static By cpnbBolusInjectionRadioNoBy = By.id("injectionInd2");
    private static By cpnbBolusInjectionDateFieldBy = By.id("continuousPeripheralInjectionDate1");
    private static By cpnbBolusInjectionMedicationBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::select[@id='injectionMedication']");
    private static By cpnbBolusConcentrationFieldBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='injectionConcentration']");
    private static By cpnbBolusVolumeFieldBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='injectionQty']");
    private static By cpnbCatheterInfusionRadioYesBy = By.id("infusionInd1");
    private static By cpnbCatheterInfusionRadioNoBy = By.id("infusionInd2");
    private static By cpnbCiInfusionRateFieldBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='infusionRate']");
    private static By cpnbCiInfusionMedicationBy = By.xpath("//tr[@id='cpnbInfusionMedication1']/td/select[@id='infusionMedication']");
    private static By cpnbCiConcentrationFieldBy = By.xpath("//tr[@id='cpnbInfusionConcentration1']/td/input[@id='infusionConcentration']");
    private static By cpnbCiVolumeFieldBy = By.xpath("//tr[@id='cpnbInfusionVolume1']/td/input[@id='infusionQty']");
    private static By cpnbPcbRadioButtonYesBy = By.id("pcaInd1");
    private static By cpnbPcbRadioButtonNoBy = By.id("pcaInd2");
    private static By cpnbPcbRadioLabelYesBy = By.xpath("//*[@id='painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd']/tbody/tr/td[1]/label");
    private static By cpnbPcbRadioLabelNoBy = By.xpath("//*[@id='painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd']/tbody/tr/td[2]/label");

    private static By ecPcebVolumeFieldBy = By.xpath("//tr[@id='pcbVolume1']/td/input[@id='pcaQty']");
    private static By ecPcebLocoutFieldBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='pcaLockout']");
    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::select[@id='preProcVas']");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::select[@id='postProcVas']");
    private static By blockPurposeDropdownBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::select[@id='blockPurpose']");
    private static By commentsTextAreaBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::textarea[@id='comments']");
    private static By cpnbAdditionalBlockRadioYesBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='additionalBlockYes1']");
    private static By cpnbAdditionalBlockRadioNoBy = By.xpath("//*[@id='continuousPeripheralPainNoteForm1']/descendant::input[@id='additionalBlock3']");
    private static By createNoteButtonBy = By.xpath("//div[@id='continuousPeripheralNerveBlockContainer']/descendant::button[text()='Create Note']"); // verified on gold, and again, but doesn't work?

    public ContinuousPeripheralNerveBlock() {
        if (Arguments.template) {
            //this.randomizeSection = null; // don't want this showing up in template
            this.timeOfPlacement = "";
            this.lateralityOfPnb = "";
            this.locationOfPnb = "";
            this.isCatheterTunneled = "";
            this.isCatheterTestDosed = "";
            this.isBolusInjection = "";
            this.bolusInjection = new BolusInjection();
            this.isCatheterInfusion = "";
            this.catheterInfusion = new CatheterInfusion();
            this.isPatientContolledBolus = "";
            this.patientControlledBolus = new PatientControlledBolusCpnb();
            this.preProcedureVerbalAnalogueScore = "";
            this.postProcedureVerbalAnalogueScore = "";
            this.blockPurpose = "";
            this.commentsNotesComplications = "";
            this.wantAdditionalBlock = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            messageAreaForCreatingNoteBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span"); // correct for demo tier
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl");
            procedureSectionBy = By.id("painNoteForm:Procedure");
            dropdownForSelectProcedureBy = By.id("painNoteForm:selectProcedure");
            timeOfPlacementFieldBy = By.id("painNoteForm:primaryCpnb:placementDateDecorate:placementDateInputDate");
            locationOfCpnbDropdownBy = CPNB_LOCATION_OF_CPNB_DROPDOWN; // fix later
            cpnbCatheterTunneledRadioYesBy = CPNB_CATHETER_TUNNELED_RADIO_YES_LABEL;
            cpnbCatheterTunneledRadioNoBy = CPNB_CATHETER_TUNNELED_RADIO_NO_LABEL;
            cpnbCatheterTestDosedRadioYesBy = CPNB_CATHETER_TEST_DOSED_RADIO_YES_LABEL;
            cpnbCatheterTestDosedRadioNoBy = CPNB_CATHETER_TEST_DOSED_RADIO_NO_LABEL;
            cpnbBolusInjectionRadioYesBy = CPNB_BOLUS_INJECTION_RADIO_YES_LABEL;
            cpnbBolusInjectionRadioNoBy = CPNB_BOLUS_INJECTION_RADIO_NO_LABEL;
            cpnbBolusInjectionDateFieldBy = CPNB_BOLUS_INJECTION_DATE_FIELD;
            cpnbBolusInjectionMedicationBy = CPNB_BOLUS_MEDICATION_DROPDOWN;
            cpnbBolusConcentrationFieldBy = CPNB_BOLUS_CONCENTRATION_FIELD;
            cpnbBolusVolumeFieldBy = CPNB_BOLUS_VOLUME_FIELD;
            cpnbCatheterInfusionRadioYesBy = CPNB_CATHETER_INFUSION_RADIO_YES_LABEL;
            cpnbCatheterInfusionRadioNoBy = CPNB_CATHETER_INFUSION_RADIO_NO_LABEL;
            cpnbCiInfusionRateFieldBy = CPNB_CI_INFUSION_RATE_FIELD;
            cpnbCiInfusionMedicationBy = CPNB_CI_INFUSION_MEDICATION_DROPDOWN;
            cpnbCiConcentrationFieldBy = CPNB_CI_CONCENTRATION_FIELD;
            cpnbCiVolumeFieldBy = CPNB_CI_VOLUME_FIELD;
            ecPcebVolumeFieldBy = By.xpath("//label[.='Lockout:']/../../../../../../../../preceding-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
            ecPcebLocoutFieldBy = By.xpath("//label[.='Lockout:']/../following-sibling::td/input");
            preVerbalScoreDropdownBy = By.id("painNoteForm:primaryCpnb:preProcVasDecorate:preProcVas");
            postVerbalScoreDropdownBy = By.id("painNoteForm:primaryCpnb:postProcVasDecorate:postProcVas");
            blockPurposeDropdownBy = By.id("painNoteForm:primaryCpnb:blockPurposeDecorate:blockPurpose");
            commentsTextAreaBy = By.id("painNoteForm:primaryCpnb:commentsDecorate:comments");
            cpnbAdditionalBlockRadioYesBy = CPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL;
            cpnbAdditionalBlockRadioNoBy = CPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL;
            createNoteButtonBy = By.id("painNoteForm:createNoteButton"); // verified
        }
    }

    /**
     * Process this kind of procedure note for the specified patient, by filling in the values and saving them
     * @param patient The patient for whom this procedure note applies
     * @return Success of Failure
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("        Processing Continuous Peripheral Nerve Block at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        //
        // Find and click on the procedure notes tab, and wait for it to finish, and check to see if we did get where we expect to,
        // before trying to add any values to the fields.
        // There are often timing problems in this section.  This should be reviewed.
        //
        try {
            WebElement procedureNotesTabElement = Utilities.waitForVisibility(procedureNotesTabBy, 10, "ContinuousPeripheralNerveBlock.process()");
            procedureNotesTabElement.click();
            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }
        logger.fine("ContinuousPeriperalNerveBlock.process, and will next look for procedure section.");

        // The clickTab above restructures the DOM and if you go to the elements on the page too quickly
        // there are problems.  So check that the target section is refreshed.
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(visibilityOfElementLocated(procedureSectionBy)));
            logger.fine("ContinuousPeripheralNerveBlock.process(), I guess we found the procedure section.");
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNerveBlock.process(), Did not find the procedure section.  Exception caught: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        try {
            Utilities.waitForPresence(dropdownForSelectProcedureBy, 2, "ContinuousPeripheralNerveBlock.process()");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNerveBlock.process() timed out waiting for dropdownForSelectProcedure");
            return false;
        }

        String procedureNoteProcedure = "Continuous Peripheral Nerve Block";

        Utilities.sleep(555, "CPNB");
        Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.randomizeSection, true);
        Utilities.sleep(1555, "CPNB"); // hate to do this, but I'm not confident that isFinishedAjax works.  was 755
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());
        logger.fine("ContinuousPeripheralNerveBlock.process(), Looking for the time of placement input.");
        try {
            (new WebDriverWait(Driver.driver, 10)).until(visibilityOfElementLocated(timeOfPlacementFieldBy));
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNeverBlock.process(), Could not find timeOfPlacementField");
            return false;
        }

        //
        // Start filling in the fields.
        //
        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.timeOfPlacement = Utilities.processDateTime(timeOfPlacementFieldBy, this.timeOfPlacement, this.randomizeSection, true); // fails often
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.randomizeSection, true, leftRadioButtonLabelBy, rightRadioButtonLabelBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.randomizeSection, true, CPNB_LATERALITY_OF_CPNB_RADIO_LEFT_LABEL, CPNB_LATERALITY_OF_CPNB_RADIO_RIGHT_LABEL);
        }
        // This next one also does an AJAX call, though I don't know why.  It does seem to take about 0.5 seconds to return
        this.locationOfPnb = Utilities.processDropdown(locationOfCpnbDropdownBy, this.locationOfPnb, this.randomizeSection, true);
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterTunneled = Utilities.processRadiosByButton(this.isCatheterTunneled, this.randomizeSection, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterTunneled = Utilities.processRadiosByLabel(this.isCatheterTunneled, this.randomizeSection, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        // I believe catheter must be test dosed in order to save this note.  So if not specified, or "random", set to Yes
        if (this.isCatheterTestDosed == null || this.isCatheterTestDosed.isEmpty() || this.isCatheterTestDosed.equalsIgnoreCase("random")) {
            this.isCatheterTestDosed = "Yes";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterTestDosed = Utilities.processRadiosByButton(this.isCatheterTestDosed, this.randomizeSection, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterTestDosed = Utilities.processRadiosByLabel(this.isCatheterTestDosed, this.randomizeSection, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isBolusInjection = Utilities.processRadiosByButton(this.isBolusInjection, this.randomizeSection, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isBolusInjection = Utilities.processRadiosByLabel(this.isBolusInjection, this.randomizeSection, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }
        if (this.isBolusInjection != null && this.isBolusInjection.equalsIgnoreCase("Yes")) {
            BolusInjection bolusInjection = this.bolusInjection;
            if (bolusInjection == null) {
                bolusInjection = new BolusInjection();
                this.bolusInjection = bolusInjection; // new
            }
            if (bolusInjection.randomizeSection == null) {
                bolusInjection.randomizeSection = this.randomizeSection; // removed setting to false if null
            }
            if (bolusInjection.shoot == null) {
                bolusInjection.shoot = this.shoot;
            }

            bolusInjection.bolusInjectionDate = Utilities.processText(cpnbBolusInjectionDateFieldBy, bolusInjection.bolusInjectionDate, Utilities.TextFieldType.DATE_TIME, this.randomizeSection, true);

            bolusInjection.bolusMedication = Utilities.processDropdown(cpnbBolusInjectionMedicationBy, bolusInjection.bolusMedication, this.randomizeSection, true);

            bolusInjection.concentration = Utilities.processDoubleNumber(cpnbBolusConcentrationFieldBy, bolusInjection.concentration, 0.1, 5.0, this.randomizeSection, true);

            bolusInjection.volume = Utilities.processDoubleNumber(cpnbBolusVolumeFieldBy, bolusInjection.volume, 0, 50, this.randomizeSection, true);  // what's reasonable amount?
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterInfusion = Utilities.processRadiosByButton(this.isCatheterInfusion, this.randomizeSection, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterInfusion = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.randomizeSection, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new test
        if (this.isCatheterInfusion != null && this.isCatheterInfusion.equalsIgnoreCase("Yes")) {
            CatheterInfusion catheterInfusion = this.catheterInfusion;
            if (catheterInfusion == null) {
                catheterInfusion = new CatheterInfusion();
                this.catheterInfusion = catheterInfusion; // new
            }
            if (catheterInfusion.randomizeSection == null) {
                catheterInfusion.randomizeSection = this.randomizeSection;
            }
            if (catheterInfusion.shoot == null) {
                catheterInfusion.shoot = this.shoot;
            }
            catheterInfusion.infusionRate = Utilities.processDoubleNumber(cpnbCiInfusionRateFieldBy, catheterInfusion.infusionRate, 0.0, 20.0, this.randomizeSection, true);
            catheterInfusion.infusionMedication = Utilities.processDropdown(cpnbCiInfusionMedicationBy, catheterInfusion.infusionMedication, this.randomizeSection, true);
            catheterInfusion.concentration = Utilities.processDoubleNumber(cpnbCiConcentrationFieldBy, catheterInfusion.concentration, 0.1, 5.0, this.randomizeSection, true);
            catheterInfusion.volumeToBeInfused = Utilities.processDoubleNumber(cpnbCiVolumeFieldBy, catheterInfusion.volumeToBeInfused, 0.0, 1000.0, this.randomizeSection, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // what's with isCatheterInfusion????????????????????????
            this.isPatientContolledBolus = Utilities.processRadiosByButton(this.isCatheterInfusion, this.randomizeSection, true, cpnbPcbRadioButtonYesBy, cpnbPcbRadioButtonNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isPatientContolledBolus = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.randomizeSection, true, cpnbPcbRadioLabelYesBy, cpnbPcbRadioLabelNoBy);
        }
        if (this.isPatientContolledBolus != null && this.isPatientContolledBolus.equalsIgnoreCase("Yes")) {
            if (this.patientControlledBolus == null) {
                this.patientControlledBolus = new PatientControlledBolusCpnb();
            }
            if (this.patientControlledBolus.randomizeSection == null) {
                this.patientControlledBolus.randomizeSection = this.randomizeSection; // removed setting to false if null
            }
            if (this.patientControlledBolus.shoot == null) {
                this.patientControlledBolus.shoot = this.shoot;
            }
            this.patientControlledBolus.volume = Utilities.processDoubleNumber(ecPcebVolumeFieldBy, this.patientControlledBolus.volume, 0, 25, this.patientControlledBolus.randomizeSection, true);
            this.patientControlledBolus.lockout = Utilities.processDoubleNumber(ecPcebLocoutFieldBy, this.patientControlledBolus.lockout, 1, 60, this.patientControlledBolus.randomizeSection, true);
        }
        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.randomizeSection, true);
        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.randomizeSection, false); // was required:true
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.randomizeSection, false);
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????

        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.wantAdditionalBlock = Utilities.processRadiosByButton(this.wantAdditionalBlock, this.randomizeSection, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.randomizeSection, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }

        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            logger.fine("Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
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
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(createNoteButtonBy, 10, "ContinuousPeripheralNerveBlock.(), create note button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "CPNB");
            }
            start = Instant.now();
            logger.fine("Here comes a click of crete note button for CPNB");
            createNoteButton.click();
        }
        catch (TimeoutException e) {
            logger.severe("ContinuousPeripheralNerveBlock.process(), failed to get and click on the create note button.  Timed out.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        catch (Exception e) {
            logger.severe("ContinuousPeripheralNerveBlock.process(), failed to get and click on the create note button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        // We may need this sleep because of the table that gets populated and inserted prior to the message "Note successfully created!"
        // Otherwise we try to read it, and there's nothing there to read!
        Utilities.sleep(555, "CPNB"); // do we need this?  Seemed necessary in SPNB, but 6555 ms

        ExpectedCondition<WebElement> problemOnTheServerMessageCondition = ExpectedConditions.visibilityOfElementLocated(sorryThereWasAProblemOnTheServerBy);
        ExpectedCondition<WebElement> successfulMessageCondition = ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy);
        ExpectedCondition<Boolean> successOrServerProblem = ExpectedConditions.or(successfulMessageCondition, problemOnTheServerMessageCondition);
        try {
            (new WebDriverWait(Driver.driver, 10)).until(successOrServerProblem);
        }
        catch (Exception e) {
            logger.severe("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        // We'll check for the "Sorry, there was a problem on the server." message first
        try {
            logger.fine("gunna wait until message for problem on server.  Looks like not doing the 'or' thing here.");
            Utilities.sleep(4555, "CPNB"); // may be essential.  Wow that's a long time.
            WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 4)).until(problemOnTheServerMessageCondition); // was 1
            String message = problemOnTheServerElement.getText();
            if (message.contains("problem on the server")) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to save Continuous Peripheral Nerve Block Note for " +
                            patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.finest("CPNB.process(), Exception caught while waiting for a message indicating a problem.  Maybe there was no problem.  Continuing...  e: " + Utilities.getMessageFirstLine(e));
        }

        // Now we'll check for "successfully"
        boolean weAreGood = false;
        try {
            WebElement painManagementNoteMessageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(successfulMessageCondition);
            String message = painManagementNoteMessageAreaElement.getText();
            if (!message.isEmpty()) {
                if (message.contains("successfully created") || message.contains("sucessfully created")) {
                    logger.finest("We're good.  fall through.");
                    //return true; // fix the logic so we can exit at the end of this method
                    weAreGood = true;
                }
                else {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to save Continuous Peripheral Nerve Block Note for " +
                                patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                    return false;
                }
            }
            else {
                logger.finer("The CPNB messages is empty.  What does that mean?  Will try for a message elsewhere now.  This message is unlikely.");
            }
        }
        catch (Exception e) {
            logger.fine("Didn't find a successful message condition, or couldn't get its text.  Will now check other areas for error messages...");
        }
        if (!weAreGood) {
            try {
                WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 10)).until(problemOnTheServerMessageCondition);
                String message = problemOnTheServerElement.getText();
                if (message.contains("problem on the server")) {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to save Continuous Peripheral Nerve Block Note for " +
                                patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                    return false;
                }


            } catch (Exception e) {
                logger.info("ContinuousPeripheralNerveBlock.process(), exception caught but prob okay?: " + Utilities.getMessageFirstLine(e));
            }
        }
        if (!Arguments.quiet) {
            System.out.println("          Saved Continuous Peripheral Nerve Block Note at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.fine("Continuous Peripheral Nerve Block saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "CPNB");
        }
        return true;
    }
}
