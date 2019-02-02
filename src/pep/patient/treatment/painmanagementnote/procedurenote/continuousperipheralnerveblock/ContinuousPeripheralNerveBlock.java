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
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

public class ContinuousPeripheralNerveBlock {
    private static Logger logger = Logger.getLogger(ContinuousPeripheralNerveBlock.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String timeOfPlacement; // "MM/DD/YYYY HHMM Z, required";
    public String lateralityOfPnb; // "Left or Right, required";
    public String locationOfPnb; // "option 1-18, required"; // causes delay for some reason

    public String isCatheterTunneled;
    public String isCatheterTestDosed;
    public String isBolusInjection; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS

    public BolusInjection bolusInjection;

    public String isCatheterInfusion; // = yes/no // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public CatheterInfusion catheterInfusion; // = new CatheterInfusion();

    public String isPatientContolledBolus; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public PatientControlledBolusCpnb patientControlledBolus; // two similar classes that had same name, but in different classes.  fix later.  different packages.

    public String preProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String postProcedureVerbalAnalogueScore; // "option 1-11, required";
    public String blockPurpose; // "option 1-3";
    public String commentsNotesComplications; // "text";
    public String wantAdditionalBlock; // = yes/no

    // I did these
    private static By CPNB_LATERALITY_OF_CPNB_RADIO_LEFT_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:blockLateralityDecorate:blockLaterality\"]/tbody/tr/td[1]/label");
    private static By CPNB_LATERALITY_OF_CPNB_RADIO_RIGHT_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:blockLateralityDecorate:blockLaterality\"]/tbody/tr/td[2]/label");

    private static By CPNB_LOCATION_OF_CPNB_DROPDOWN = By.xpath("//label[.='Location of CPNB:']/../following-sibling::td/select");

    // I did these
    private static By CPNB_CATHETER_TUNNELED_RADIO_YES_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_TUNNELED_RADIO_NO_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd\"]/tbody/tr/td[2]/label");

    // I did these
    private static By CPNB_CATHETER_TEST_DOSED_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:testDoseIndDecorate:testDoseInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_TEST_DOSED_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:testDoseIndDecorate:testDoseInd\"]/tbody/tr/td[2]/label");

    // Bolus Injection

    private static By CPNB_BOLUS_INJECTION_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:injectionIndDecorate:injectionInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_BOLUS_INJECTION_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:injectionIndDecorate:injectionInd\"]/tbody/tr/td[2]/label");


    private static By CPNB_BOLUS_INJECTION_DATE_FIELD =
            By.xpath("//label[.='Bolus Injection Date:']/../../../../../following-sibling::td/span/input[1]");
    private static By CPNB_BOLUS_MEDICATION_DROPDOWN = By.xpath("//label[.='Bolus Medication:']/../following-sibling::td/select");
    private static By CPNB_BOLUS_CONCENTRATION_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_BOLUS_VOLUME_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[2]/td/div/div/table/tbody/tr/td/input");

    // I did these
    private static By CPNB_CATHETER_INFUSION_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:InfusionFields:infusionInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_INFUSION_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:InfusionFields:infusionInd\"]/tbody/tr/td[2]/label");


    private static By CPNB_CI_INFUSION_RATE_FIELD = By.xpath("//label[.='Infusion Rate:']/../following-sibling::td/input");
    private static By CPNB_CI_INFUSION_MEDICATION_DROPDOWN = By.xpath("//label[.='Infusion Medication:']/../following-sibling::td/select");
    private static By CPNB_CI_CONCENTRATION_FIELD = By.xpath("//label[.='Infusion Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_CI_VOLUME_FIELD = By.xpath("//label[.='Volume to be Infused:']/../following-sibling::td/input");

    // I did these
    private static By CPNB_PCB_RADIO_YES_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_PCB_RADIO_NO_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[2]/label");

    // I did these
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[2]/label");


    //private static By messageAreaForCreatingNoteBy = By.id("pain-note-message"); // verified
    private static By messageAreaForCreatingNoteBy = By.xpath("//div[@id='procedureNoteTab']/preceding-sibling::div[1]"); // new 10/19/20

    private static By sorryThereWasAProblemOnTheServerBy = By.id("createNoteMsg"); // verified
    private static By procedureNotesTabBy = By.linkText("Procedure Notes"); // 1/23/19
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // is this right?
    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");
    private static By timeOfPlacementFieldBy = By.id("continuousPeripheralPlacementDate1");
//    private static By leftRadioButtonBy = By.id("blockLaterality7");
//    private static By rightRadioButtonBy = By.id("blockLaterality8");
    private static By leftRadioButtonLabelBy = By.xpath("//label[@for='blockLaterality7']");
    private static By rightRadioButtonLabelBy = By.xpath("//label[@for='blockLaterality8']");
    private static By locationOfCpnbDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"blockLocation\"]");
    private static By cpnbCatheterTunneledRadioYesBy = By.id("catheterTunneledInd1");
    private static By cpnbCatheterTunneledRadioNoBy = By.id("catheterTunneledInd2");
    private static By cpnbCatheterTestDosedRadioYesBy = By.id("testDoseInd1");
    private static By cpnbCatheterTestDosedRadioNoBy = By.id("testDoseInd2");
    private static By cpnbBolusInjectionRadioYesBy = By.id("injectionInd1");
    private static By cpnbBolusInjectionRadioNoBy = By.id("injectionInd2");
    private static By cpnbBolusInjectionDateFieldBy = By.id("continuousPeripheralInjectionDate1");
    private static By cpnbBolusInjectionMedicationBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"injectionMedication\"]");
    private static By cpnbBolusConcentrationFieldBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"injectionConcentration\"]");
    private static By cpnbBolusVolumeFieldBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"injectionQty\"]");
    private static By cpnbCatheterInfusionRadioYesBy = By.id("infusionInd1");
    private static By cpnbCatheterInfusionRadioNoBy = By.id("infusionInd2");
    private static By cpnbCiInfusionRateFieldBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"infusionRate\"]");
//    private static By cpnbCiInfusionMedicationBy = By.id("infusionMedication");
    private static By cpnbCiInfusionMedicationBy = By.xpath("//tr[@id='cpnbInfusionMedication1']/td/select[@id='infusionMedication']");
//    private static By cpnbCiConcentrationFieldBy = By.id("infusionConcentration");
    private static By cpnbCiConcentrationFieldBy = By.xpath("//tr[@id='cpnbInfusionConcentration1']/td/input[@id='infusionConcentration']");
//    private static By cpnbCiVolumeFieldBy = By.id("infusionQty");
    private static By cpnbCiVolumeFieldBy = By.xpath("//tr[@id='cpnbInfusionVolume1']/td/input[@id='infusionQty']");
    private static By cpnbPcbRadioButtonYesBy = By.id("pcaInd1");
    private static By cpnbPcbRadioButtonNoBy = By.id("pcaInd2");
    private static By cpnbPcbRadioLabelYesBy = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[1]/label");
    private static By cpnbPcbRadioLabelNoBy = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[2]/label");

    //private static By ecPcebVolumeFieldBy = By.id("pcaQty");
    private static By ecPcebVolumeFieldBy = By.xpath("//tr[@id='pcbVolume1']/td/input[@id=\"pcaQty\"]");
    private static By ecPcebLocoutFieldBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"pcaLockout\"]");
    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"preProcVas\"]");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"postProcVas\"]");
    private static By blockPurposeDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"blockPurpose\"]");
    private static By commentsTextAreaBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::textarea[@id=\"comments\"]");
    private static By cpnbAdditionalBlockRadioYesBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"additionalBlockYes1\"]");
    private static By cpnbAdditionalBlockRadioNoBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"additionalBlock3\"]");
//    private static By createNoteButtonBy = By.xpath("//*[@id=\"continuousPeripheralNerveBlockContainer\"]/button[1]"); // verified on gold, and again, but doesn't work?
    private static By createNoteButtonBy = By.xpath("//div[@id=\"continuousPeripheralNerveBlockContainer\"]/button[text()='Create Note']"); // verified on gold, and again, but doesn't work?

    public ContinuousPeripheralNerveBlock() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
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
            messageAreaForCreatingNoteBy = By.xpath("//*[@id=\"painNoteForm:j_id1200\"]/table/tbody/tr/td/span"); // correct for demo tier
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

    // This method covers a section of a page that has a lot of AJAX elements, so timing is a real issue because we don't
    // know how long the server will take to return and update the DOM.
    // And is it possible that we're not even showing the section of the page and yet we blast through it mostly????
    // Yeah, this page hasn't opened up completely or something by the time we try to access date/time.
    // So, the tab element click isn't working, I think.
    // This method is way too long.  Break it out.
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("        Processing Continuous Peripheral Nerve Block for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

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

        // At this point we should have the Select Procedure dropdown
        try {
            Utilities.waitForPresence(dropdownForSelectProcedureBy, 2, "ContinuousPeripheralNerveBlock.process()");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // new.  nec? Utiliities.isFinished???
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNerveBlock.process() timed out waiting for dropdownForSelectProcedure");
            return false;
        }

        // Making a selection on this dropdown causes the DOM to change, so again we can't go to the next elements too quickly.
        String procedureNoteProcedure = "Continuous Peripheral Nerve Block";

        Utilities.sleep(555, "CPNB"); // spnb usually fails at the next line, so trying a sleep there, but will put one here too for consistency
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL.  I agree.
        Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.random, true); // true to go further, and do
// the above line probably isn't a good idea.  What's the need for random?  How do you handle that inside processDropdown?
        Utilities.sleep(1555, "CPNB"); // hate to do this, but I'm not confident that isFinishedAjax works.  was 755
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());

        // Okay, this is where we should have the CPNB section showing.  All that stuff above was a bunch of crap to see if we could get here!  Sheesh.
        logger.fine("ContinuousPeripheralNerveBlock.process(), Looking for the time of placement input.");
        try {
            (new WebDriverWait(Driver.driver, 10)).until(
                    visibilityOfElementLocated(timeOfPlacementFieldBy)); // we'll try visibility rather than presence, because of this stupid way things are hidden but present
        }
        catch (Exception e) {
            logger.fine("ContinuousPeripheralNeverBlock.process(), Could not find timeOfPlacementField");
            return false;
        }

        // Hopefully now we have all the elements in the section ready to be used.  But still there are lots
        // of timing issues coming up.  Most of the radio buttons cause AJAX calls.

        // This next section can be repeated if the user specifies another block near the end
        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.timeOfPlacement = Utilities.processDateTime(timeOfPlacementFieldBy, this.timeOfPlacement, this.random, true); // fails often

        // All the following radio buttons in this section can go by Button rather than Label, but only because of the structure, and there are no multiword labels
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
//            this.lateralityOfPnb = Utilities.processRadiosByButton(this.lateralityOfPnb, this.random, true, leftRadioButtonBy, rightRadioButtonBy);
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true, leftRadioButtonLabelBy, rightRadioButtonLabelBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true, CPNB_LATERALITY_OF_CPNB_RADIO_LEFT_LABEL, CPNB_LATERALITY_OF_CPNB_RADIO_RIGHT_LABEL);
        }

        // This next one also does an AJAX call, though I don't know why.  It does seem to take about 0.5 seconds to return
        this.locationOfPnb = Utilities.processDropdown(locationOfCpnbDropdownBy, this.locationOfPnb, this.random, true);

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterTunneled = Utilities.processRadiosByButton(this.isCatheterTunneled, this.random, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterTunneled = Utilities.processRadiosByLabel(this.isCatheterTunneled, this.random, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        // I believe catheter must be test dosed in order to save this note.  So if not specified, or "random", set to Yes
        if (this.isCatheterTestDosed == null || this.isCatheterTestDosed.isEmpty() || this.isCatheterTestDosed.equalsIgnoreCase("random")) {
            this.isCatheterTestDosed = "Yes";
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterTestDosed = Utilities.processRadiosByButton(this.isCatheterTestDosed, this.random, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterTestDosed = Utilities.processRadiosByLabel(this.isCatheterTestDosed, this.random, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isBolusInjection = Utilities.processRadiosByButton(this.isBolusInjection, this.random, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isBolusInjection = Utilities.processRadiosByLabel(this.isBolusInjection, this.random, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }

        if (this.isBolusInjection != null && this.isBolusInjection.equalsIgnoreCase("Yes")) {

            BolusInjection bolusInjection = this.bolusInjection;
            if (bolusInjection == null) {
                bolusInjection = new BolusInjection();
                this.bolusInjection = bolusInjection; // new
            }
            if (bolusInjection.random == null) {
                bolusInjection.random = this.random; // removed setting to false if null
            }
            if (bolusInjection.shoot == null) {
                bolusInjection.shoot = this.shoot;
            }

            bolusInjection.bolusInjectionDate = Utilities.processText(cpnbBolusInjectionDateFieldBy, bolusInjection.bolusInjectionDate, Utilities.TextFieldType.DATE_TIME, this.random, true);

            bolusInjection.bolusMedication = Utilities.processDropdown(cpnbBolusInjectionMedicationBy, bolusInjection.bolusMedication, this.random, true);

            bolusInjection.concentration = Utilities.processDoubleNumber(cpnbBolusConcentrationFieldBy, bolusInjection.concentration, 0.1, 5.0, this.random, true);

            bolusInjection.volume = Utilities.processDoubleNumber(cpnbBolusVolumeFieldBy, bolusInjection.volume, 0, 50, this.random, true);  // what's reasonable amount?
        }

        // Even though the values are right, sometimes the radio button doesn't get registered, I think.

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isCatheterInfusion = Utilities.processRadiosByButton(this.isCatheterInfusion, this.random, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isCatheterInfusion = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.random, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new test
        if (this.isCatheterInfusion != null && this.isCatheterInfusion.equalsIgnoreCase("Yes")) {

            CatheterInfusion catheterInfusion = this.catheterInfusion;
            if (catheterInfusion == null) {
                catheterInfusion = new CatheterInfusion();
                this.catheterInfusion = catheterInfusion; // new
            }
            if (catheterInfusion.random == null) {
                catheterInfusion.random = this.random; // removed setting to false if null
            }
            if (catheterInfusion.shoot == null) {
                catheterInfusion.shoot = this.shoot;
            }

            catheterInfusion.infusionRate = Utilities.processDoubleNumber(cpnbCiInfusionRateFieldBy, catheterInfusion.infusionRate, 0.0, 20.0, this.random, true);
            catheterInfusion.infusionMedication = Utilities.processDropdown(cpnbCiInfusionMedicationBy, catheterInfusion.infusionMedication, this.random, true);
            catheterInfusion.concentration = Utilities.processDoubleNumber(cpnbCiConcentrationFieldBy, catheterInfusion.concentration, 0.1, 5.0, this.random, true);

            catheterInfusion.volumeToBeInfused = Utilities.processDoubleNumber(cpnbCiVolumeFieldBy, catheterInfusion.volumeToBeInfused, 0.0, 1000.0, this.random, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // what's with isCatheterInfusion????????????????????????
            this.isPatientContolledBolus = Utilities.processRadiosByButton(this.isCatheterInfusion, this.random, true, cpnbPcbRadioButtonYesBy, cpnbPcbRadioButtonNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isPatientContolledBolus = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.random, true, cpnbPcbRadioLabelYesBy, cpnbPcbRadioLabelNoBy);
        }

        if (this.isPatientContolledBolus != null && this.isPatientContolledBolus.equalsIgnoreCase("Yes")) {

            // This isn't like the others above.  What's wrong?
            if (this.patientControlledBolus == null) {
                this.patientControlledBolus = new PatientControlledBolusCpnb();
            }
            if (this.patientControlledBolus.random == null) {
                this.patientControlledBolus.random = this.random; // removed setting to false if null
            }
            if (this.patientControlledBolus.shoot == null) {
                this.patientControlledBolus.shoot = this.shoot;
            }

            this.patientControlledBolus.volume = Utilities.processDoubleNumber(ecPcebVolumeFieldBy, this.patientControlledBolus.volume, 0, 25, this.patientControlledBolus.random, true);
            this.patientControlledBolus.lockout = Utilities.processDoubleNumber(ecPcebLocoutFieldBy, this.patientControlledBolus.lockout, 1, 60, this.patientControlledBolus.random, true);
        }

        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.random, true);

        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.random, true);

        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.random, false); // was required:true

        //logger.fine("ContinuousPeripheralNerveBlock.process(), just did a block purpose, though it's not required, and here comes an isFinishedAjax...");
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????

        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);
        //logger.fine("ContinuousPeripheralNerveBlock.process(), just did a commentsNotesComplications, required: true, and here comes a isFinishedAjax");
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????


        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.wantAdditionalBlock = Utilities.processRadiosByButton(this.wantAdditionalBlock, this.random, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.random, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }

        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            logger.fine("Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote screenshot file " + fileName);
        }

        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        Instant start = null;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(createNoteButtonBy, 10, "ContinuousPeripheralNerveBlock.(), create note button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "CPNB");
            }
            start = Instant.now();
            logger.fine("Heer comes a click.");
            createNoteButton.click();
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this help at all?  Seems not.  Blasts through?
            //logger.fine("ajax finished");
        }
        catch (TimeoutException e) {
            logger.severe("ContinuousPeripheralNerveBlock.process(), failed to get and click on the create note button(?).  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("ContinuousPeripheralNerveBlock.process(), failed to get and click on the create note button(?).  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }


        // The following is totally weird stuff.  How many messages are possible?  And how is this being handled in SPNB?


        Utilities.sleep(555, "CPNB"); // do we need this?  Seemed necessary in SPNB, but 6555 ms

        // Possible that we can get a message "Sorry, there was a problem on the server."
        // If so, it would be located by //*[@id="createNoteMsg"]
        // How long before it shows up, I don't know.


        ExpectedCondition<WebElement> problemOnTheServerMessageCondition = ExpectedConditions.visibilityOfElementLocated(sorryThereWasAProblemOnTheServerBy);
        ExpectedCondition<WebElement> successfulMessageCondition = ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy);
        ExpectedCondition<Boolean> successOrServerProblem = ExpectedConditions.or(successfulMessageCondition, problemOnTheServerMessageCondition);
        try {
            boolean whatever = (new WebDriverWait(Driver.driver, 10)).until(successOrServerProblem);
        }
        catch (Exception e) {
            //System.out.println("Didn't get either condition?");
            logger.severe("SinglePeripheralNerveBlock.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        // At this point we should have one or the other message showing up (assuming a previous message was erased in time)
        // We'll check for the "Sorry, there was a problem on the server." message first
        try {
            logger.fine("gunna wait until message for problem on server.  Looks like not doing the 'or' thing here.");
            Utilities.sleep(4555, "CPNB"); // may be essential.  Wow that's a long time.
            WebElement problemOnTheServerElement = (new WebDriverWait(Driver.driver, 4)).until(problemOnTheServerMessageCondition); // was 1
            String message = problemOnTheServerElement.getText();
            if (message.contains("problem on the server")) {
                if (!Arguments.quiet)
                    System.err.println("        ***Failed to save Continuous Peripheral Nerve Block note for " +
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
                if (message.contains("successfully created") || message.contains("sucessfully created")) { // yes, they haven't fixed the spelling on this yet
                    logger.finest("We're good.  fall through.");
                    //return true; // fix the logic so we can exit at the end of this method
                    weAreGood = true;
                }
                else {
                    if (!Arguments.quiet)
                        System.err.println("        ***Failed to save Continuous Peripheral Nerve Block note for " +
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
                        System.err.println("        ***Failed to save Continuous Peripheral Nerve Block note for " +
                                patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + message);
                    return false;
                }


            } catch (Exception e) {
                logger.warning("ContinuousPeripheralNerveBlock.process(), exception caught but prob okay?: " + Utilities.getMessageFirstLine(e));
            }
        }
        if (!Arguments.quiet) {
            System.out.println("          Saved Continuous Peripheral Nerve Block note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        //timerLogger.info("Continuous Peripheral Nerve Block save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        timerLogger.info("Continuous Peripheral Nerve Block saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "CPNB");
        }
        return true;
    }
}
