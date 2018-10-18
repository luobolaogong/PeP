package pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;
import static pep.utilities.Utilities.isFinishedAjax;

public class ContinuousPeripheralNerveBlock {
    public Boolean random; // true if want this section to be generated randomly
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


    private static By CPNB_LOCATION_OF_CPNB_DROPDOWN = By
            .xpath("//label[.='Location of CPNB:']/../following-sibling::td/select");

    // I did these
    private static By CPNB_CATHETER_TUNNELED_RADIO_YES_LABEL = By
            .xpath("//*[@id=\"painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_TUNNELED_RADIO_NO_LABEL = By
            .xpath("//*[@id=\"painNoteForm:primaryCpnb:catheterTunneledIndDecorate:catheterTunneledInd\"]/tbody/tr/td[2]/label");



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
    private static By CPNB_BOLUS_MEDICATION_DROPDOWN = By
            .xpath("//label[.='Bolus Medication:']/../following-sibling::td/select");
    private static By CPNB_BOLUS_CONCENTRATION_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_BOLUS_VOLUME_FIELD =
            By.xpath("//label[.='Bolus Medication:']/../../../../../../../../following-sibling::tr[2]/td/div/div/table/tbody/tr/td/input");

    // I did these
    private static By CPNB_CATHETER_INFUSION_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:InfusionFields:infusionInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_CATHETER_INFUSION_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:InfusionFields:infusionInd\"]/tbody/tr/td[2]/label");


    private static By CPNB_CI_INFUSION_RATE_FIELD = By
            .xpath("//label[.='Infusion Rate:']/../following-sibling::td/input");
    private static By CPNB_CI_INFUSION_MEDICATION_DROPDOWN = By
            .xpath("//label[.='Infusion Medication:']/../following-sibling::td/select");
    private static By CPNB_CI_CONCENTRATION_FIELD =
            By.xpath("//label[.='Infusion Medication:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By CPNB_CI_VOLUME_FIELD = By
            .xpath("//label[.='Volume to be Infused:']/../following-sibling::td/input");

    // I did these
    private static By CPNB_PCB_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_PCB_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[2]/label");

    // I did these
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[1]/label");
    private static By CPNB_ADDITIONAL_BLOCK_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:primaryCpnb:primaryCpnbDecorator:secondaryBlockInd\"]/tbody/tr/td[2]/label");

    private static By EC_PCEB_VOLUME_FIELD =
            By.xpath("//label[.='Lockout:']/../../../../../../../../preceding-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    private static By EC_PCEB_LOCKOUT_FIELD = By
            .xpath("//label[.='Lockout:']/../following-sibling::td/input");

    private static By messageAreaForCreatingNoteBy = By.id("pain-note-message");
    private static By sorryThereWasAProblemOnTheServerBy = By.id("createNoteMsg");
    private static By procedureNotesTabBy = By.xpath("//*[@id=\"procedureNoteTab\"]/a");
    private static By procedureSectionBy = By.id("procedureNoteTabContainer"); // is this right?
    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");
    private static By timeOfPlacementFieldBy = By.id("continuousPeripheralPlacementDate1");
    private static By leftRadioButtonBy = By.id("blockLaterality7");
    private static By rightRadioButtonBy = By.id("blockLaterality8");
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
    private static By cpnbCiInfusionMedicationBy = By.id("infusionMedication");
    private static By cpnbCiConcentrationFieldBy = By.id("infusionConcentration");
    private static By cpnbCiVolumeFieldBy = By.id("infusionQty");
    private static By cpnbPcbRadioButtonYesBy = By.id("pcaInd1");
    private static By cpnbPcbRadioButtonNoBy = By.id("pcaInd2");
    private static By cpnbPcbRadioLabelYesBy = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[1]/label");
    private static By cpnbPcbRadioLabelNoBy = By.xpath("//*[@id=\"painNoteForm:primaryCpnb:pcaIndDecorate:pcaInd\"]/tbody/tr/td[2]/label");

    private static By ecPcebVolumeFieldBy = By.id("pcaQty");
    private static By ecPcebLocoutFieldBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"pcaLockout\"]");
    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"preProcVas\"]");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"postProcVas\"]");
    private static By blockPurposeDropdownBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::select[@id=\"blockPurpose\"]");
    private static By commentsTextAreaBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::textarea[@id=\"comments\"]");
    private static By cpnbAdditionalBlockRadioYesBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"additionalBlockYes1\"]");
    private static By cpnbAdditionalBlockRadioNoBy = By.xpath("//*[@id=\"continuousPeripheralPainNoteForm1\"]/descendant::input[@id=\"additionalBlock3\"]");
    private static By createNoteButtonBy = By.xpath("//*[@id=\"continuousPeripheralNerveBlockContainer\"]/button[1]"); // verified on gold

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
        if (isDemoTier) {
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
            ecPcebVolumeFieldBy = EC_PCEB_VOLUME_FIELD;
            ecPcebLocoutFieldBy = EC_PCEB_LOCKOUT_FIELD;
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
        if (!Arguments.quiet) System.out.println("        Processing Continuous Peripheral Nerve Block for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        //if (Arguments.debug) System.out.println("CPNB.process() 1");
        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try {
            //if (Arguments.debug) System.out.println("CPNB.process() 2");

            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            //if (Arguments.debug) System.out.println("CPNB.process() 3");
            procedureNotesTabElement.click();
            //if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), 1 doing a call to isFinishedAjax");
            //if (Arguments.debug) System.out.println("CPNB.process() 4");
            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false;
        }
        //if (Arguments.debug) System.out.println("CPNB.process() 5");
        if (Arguments.debug) System.out.println("ContinuousPeriperalNerveBlock.process, and will next look for procedure section.");

        // The clickTab above restructures the DOM and if you go to the elements on the page too quickly
        // there are problems.  So check that the target section is refreshed.
        try {
            //if (Arguments.debug) System.out.println("CPNB.process() 6");
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(procedureSectionBy)));
            //if (Arguments.debug) System.out.println("CPNB.process() 7");
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), I guess we found the procedure section.");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), Did not find the procedure section.  Exception caught: " + e.getMessage());
            return false;
        }

        // At this point we should have the Select Procedure dropdown
        try {
            //if (Arguments.debug) System.out.println("CPNB.process() 8");
            (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.presenceOfElementLocated(dropdownForSelectProcedureBy));
            //if (Arguments.debug) System.out.println("CPNB.process() 9");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // new.  nec? Utiliities.isFinished???
            //if (Arguments.debug) System.out.println("CPNB.process() 10");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process() timed out waiting for dropdownForSelectProcedure");
            return false;
        }

        // Making a selection on this dropdown causes the DOM to change, so again we can't go to the next elements too quickly.
        //if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), will now process procedure dropdown.");
        //if (Arguments.debug) System.out.println("CPNB.process() 11");
        String procedureNoteProcedure = "Continuous Peripheral Nerve Block";


        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL
        // MY GUESS IS THAT THIS NEXT DROPDOWN ISN'T WORKING SOMETIMES AND THEREFORE WHEN WE ASSUME WE'RE ON THE CPNB SECTION, WE FAIL


        Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.random, true); // true to go further, and do

        //if (Arguments.debug) System.out.println("CPNB.process() 12");
        Utilities.sleep(1555); // hate to do this, but I'm not confident that isFinishedAjax works.  was 755
        //if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), 2 doing a call to isFinishedAjax");
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());

        // Okay, this is where we should have the CPNB section showing.  All that stuff above was a bunch of crap to see if we could get here!  Sheesh.
        if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), Looking for the time of placement input.");
        try {
            //if (Arguments.debug) System.out.println("CPNB.process() 13");
//            (new WebDriverWait(Driver.driver, 10)).until(
//                    ExpectedConditions.presenceOfElementLocated(timeOfPlacementFieldBy));
            (new WebDriverWait(Driver.driver, 10)).until(
                    ExpectedConditions.visibilityOfElementLocated(timeOfPlacementFieldBy)); // we'll try visibility rather than presence, because of this stupid way things are hidden but present
            //if (Arguments.debug) System.out.println("CPNB.process() 14");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNeverBlock.process(), Could not find timeOfPlacementField");
            return false;
        }

        // Hopefully now we have all the elements in the section ready to be used.  But still there are lots
        // of timing issues coming up.  Most of the radio buttons cause AJAX calls.

        // This next section can be repeated if the user specifies another block near the end
        //if (Arguments.debug) System.out.println("CPNB.process() 15");
        if (Arguments.date != null && (this.timeOfPlacement == null || this.timeOfPlacement.isEmpty())) {
            this.timeOfPlacement = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
// next line often fails !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //if (Arguments.debug) System.out.println("CPNB.process()16 This next line can cause a failure.  And I'm pretty sure it's because we're not on the right page.");
        this.timeOfPlacement = Utilities.processDateTime(timeOfPlacementFieldBy, this.timeOfPlacement, this.random, true); // fails often
        //if (Arguments.debug) System.out.println("CPNB.process() 17");

        if (isGoldTier) {
            this.lateralityOfPnb = Utilities.processRadiosByButton(this.lateralityOfPnb, this.random, true, leftRadioButtonBy, rightRadioButtonBy);
        }
        if (isDemoTier) {
            this.lateralityOfPnb = Utilities.processRadiosByLabel(this.lateralityOfPnb, this.random, true, CPNB_LATERALITY_OF_CPNB_RADIO_LEFT_LABEL, CPNB_LATERALITY_OF_CPNB_RADIO_RIGHT_LABEL);
        }


        // This next one also does an AJAX call, though I don't know why.  It does seem to take about 0.5 seconds to return
        this.locationOfPnb = Utilities.processDropdown(locationOfCpnbDropdownBy, this.locationOfPnb, this.random, true);

        if (isGoldTier) {
            this.isCatheterTunneled = Utilities.processRadiosByButton(this.isCatheterTunneled, this.random, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        if (isDemoTier) {
            this.isCatheterTunneled = Utilities.processRadiosByLabel(this.isCatheterTunneled, this.random, true, cpnbCatheterTunneledRadioYesBy, cpnbCatheterTunneledRadioNoBy);
        }
        // Not sure the following is necessary or right.  Check
        if (this.isCatheterTestDosed == null || this.isCatheterTestDosed.isEmpty() || this.isCatheterTestDosed.equalsIgnoreCase("random")) {
            this.isCatheterTestDosed = "Yes";
        }

        if (isGoldTier) {
            this.isCatheterTestDosed = Utilities.processRadiosByButton(this.isCatheterTestDosed, this.random, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }
        if (isDemoTier) {
            this.isCatheterTestDosed = Utilities.processRadiosByLabel(this.isCatheterTestDosed, this.random, true, cpnbCatheterTestDosedRadioYesBy, cpnbCatheterTestDosedRadioNoBy);
        }

        if (isGoldTier) {
            this.isBolusInjection = Utilities.processRadiosByButton(this.isBolusInjection, this.random, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }
        if (isDemoTier) {
            this.isBolusInjection = Utilities.processRadiosByLabel(this.isBolusInjection, this.random, true, cpnbBolusInjectionRadioYesBy, cpnbBolusInjectionRadioNoBy);
        }

        if (this.isBolusInjection != null && this.isBolusInjection.equalsIgnoreCase("Yes")) {

            BolusInjection bolusInjection = this.bolusInjection;
            if (bolusInjection == null) {
                bolusInjection = new BolusInjection();
                this.bolusInjection = bolusInjection; // new
            }
            if (bolusInjection.random == null) {
                bolusInjection.random = (this.random == null) ? false : this.random;
            }

            bolusInjection.bolusInjectionDate = Utilities.processText(cpnbBolusInjectionDateFieldBy, bolusInjection.bolusInjectionDate, Utilities.TextFieldType.DATE_TIME, this.random, true);

            bolusInjection.bolusMedication = Utilities.processDropdown(cpnbBolusInjectionMedicationBy, bolusInjection.bolusMedication, this.random, true);

            bolusInjection.concentration = Utilities.processDoubleNumber(cpnbBolusConcentrationFieldBy, bolusInjection.concentration, 0.01, 5.0, this.random, true);

            bolusInjection.volume = Utilities.processDoubleNumber(cpnbBolusVolumeFieldBy, bolusInjection.volume, 0, 50, this.random, true);  // what's reasonable amount?
        }

        // Even though the values are right, sometimes the radio button doesn't get registered, I think.

        if (isGoldTier) {
            this.isCatheterInfusion = Utilities.processRadiosByButton(this.isCatheterInfusion, this.random, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        if (isDemoTier) {
            this.isCatheterInfusion = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.random, true, cpnbCatheterInfusionRadioYesBy, cpnbCatheterInfusionRadioNoBy);
        }
        //this.isCatheterInfusion = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.random, true, CPNB_CATHETER_INFUSION_RADIO_YES_LABEL, CPNB_CATHETER_INFUSION_RADIO_NO_LABEL);
        if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), just did catheter infusion radio button, now an isFinishedAjax...");
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new test
        if (this.isCatheterInfusion != null && this.isCatheterInfusion.equalsIgnoreCase("Yes")) {

            CatheterInfusion catheterInfusion = this.catheterInfusion;
            if (catheterInfusion == null) {
                catheterInfusion = new CatheterInfusion();
                this.catheterInfusion = catheterInfusion; // new
            }
            if (catheterInfusion.random == null) {
                catheterInfusion.random = (this.random == null) ? false : this.random;
            }

            catheterInfusion.infusionRate = Utilities.processDoubleNumber(cpnbCiInfusionRateFieldBy, catheterInfusion.infusionRate, 0.0, 20.0, this.random, true);
            catheterInfusion.infusionMedication = Utilities.processDropdown(cpnbCiInfusionMedicationBy, catheterInfusion.infusionMedication, this.random, true);
            catheterInfusion.concentration = Utilities.processDoubleNumber(cpnbCiConcentrationFieldBy, catheterInfusion.concentration, 0.01, 5.0, this.random, true);

            catheterInfusion.volumeToBeInfused = Utilities.processDoubleNumber(cpnbCiVolumeFieldBy, catheterInfusion.volumeToBeInfused, 0.0, 1000.0, this.random, true);
        }

        if (isGoldTier) { // what's with isCatheterInfusion????????????????????????
            this.isPatientContolledBolus = Utilities.processRadiosByButton(this.isCatheterInfusion, this.random, true, cpnbPcbRadioButtonYesBy, cpnbPcbRadioButtonNoBy);
        }
        if (isDemoTier) {
            this.isPatientContolledBolus = Utilities.processRadiosByLabel(this.isCatheterInfusion, this.random, true, cpnbPcbRadioLabelYesBy, cpnbPcbRadioLabelNoBy);
        }

        if (this.isPatientContolledBolus != null && this.isPatientContolledBolus.equalsIgnoreCase("Yes")) {

            // This isn't like the others above.  What's wrong?
            if (this.patientControlledBolus == null) {
                this.patientControlledBolus = new PatientControlledBolusCpnb();
            }
            if (this.patientControlledBolus.random == null) {
                this.patientControlledBolus.random = (this.random == null) ? false : this.random;
            }

            this.patientControlledBolus.volume = Utilities.processDoubleNumber(ecPcebVolumeFieldBy, this.patientControlledBolus.volume, 0, 25, this.patientControlledBolus.random, true);
            this.patientControlledBolus.lockout = Utilities.processDoubleNumber(ecPcebLocoutFieldBy, this.patientControlledBolus.lockout, 1, 60, this.patientControlledBolus.random, true);
        }

        this.preProcedureVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.preProcedureVerbalAnalogueScore, this.random, true);

        this.postProcedureVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.postProcedureVerbalAnalogueScore, this.random, true);

        this.blockPurpose = Utilities.processDropdown(blockPurposeDropdownBy, this.blockPurpose, this.random, false); // was required:true

        //if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), just did a block purpose, though it's not required, and here comes an isFinishedAjax...");
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????

        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);
        //if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), just did a commentsNotesComplications, required: true, and here comes a isFinishedAjax");
        (new WebDriverWait(Driver.driver, 5)).until(Utilities.isFinishedAjax()); // helpful?????


        this.wantAdditionalBlock = "No"; // forcing this because not ready to loop
        if (isGoldTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByButton(this.wantAdditionalBlock, this.random, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }
        if (isDemoTier) {
            this.wantAdditionalBlock = Utilities.processRadiosByLabel(this.wantAdditionalBlock, this.random, true, cpnbAdditionalBlockRadioYesBy, cpnbAdditionalBlockRadioNoBy);
        }

        if (this.wantAdditionalBlock != null && this.wantAdditionalBlock.equalsIgnoreCase("Yes")) {
            if (Arguments.debug) System.out.println("Want to add another Single Periph Nerve Block for this patient.  But not going to at this time.");
        }
        // Why do we not get the button first and then click on it?
        //Utilities.clickButton(createNoteButtonBy); // Yes, makes ajax call


        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME


        //WebElement saveResultTextElement = null;
        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteButtonBy));

            // Not sure the following exists on ContinuousPeripheralNerveBlock, but seems to exist on IVPCA.
            //saveResultTextElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy));

            // FOLLOWING IS BUG ON GOLD  but not Demo if you go slow enough
            // The following does not cause the form to go back to initial state on gold
            createNoteButton.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this help at all?  Seems not.  Blasts through?
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.err.println("ContinuousPeripheralNerveBlock.process(), failed to get get and click on the create note button(?).  Unlikely.  Exception: " + e.getMessage());
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("ContinuousPeripheralNerveBlock.process(), failed to get get and click on the create note button(?).  Unlikely.  Exception: " + e.getMessage());
            return false;
        }





        // We need this sleep because of the table that gets populated and inserted prior to the message "Note successfully created!"
        // Otherwise we try to read it, and there's nothing there to read!
        // How do you know how long it takes to update that table?  What would trigger when it's finished?
        // A test to see if ajax is finished?
        Utilities.sleep(1555); // maybe we need this when there is a table that gets inserted in front of the "Note successfully created!" message so we can read that message in time.

        // There's a bug on Gold for CPNB and SPNB, and Epidural, and IvPca, I think, where you can't save because get message "Sorry, there was a problem on the server."
        WebElement messageElement = null;
        try {
            messageElement = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy)); // make sure this works.  Changed from above
            String someTextMaybe = messageElement.getText();
            System.out.println("someTextMaybe1: " + someTextMaybe);
            messageElement = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.visibilityOfElementLocated(sorryThereWasAProblemOnTheServerBy)); // make sure this works.  Changed from above
            someTextMaybe = messageElement.getText();
            System.out.println("someTextMaybe2: " + someTextMaybe);
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), couldn't get message area after trying to create note.: " + e.getMessage());
            return false; // fails: 8
        }

        try {
            String someTextMaybe = messageElement.getText();
            if (someTextMaybe.contains("successfully") || someTextMaybe.contains("sucessfully")) {
                if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process() successfully saved the note.");
            }
            else {
                // This is not always true.  It can save, but something else failed.  Maybe just a timing issue?  Or part of the page isn't there.
                if (!Arguments.quiet) System.err.println("***Failed to save Continuous Peripheral Nerve Block note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " : " + someTextMaybe);
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ContinuousPeripheralNerveBlock.process(), couldn't get message from message area, after trying to save note.: " + e.getMessage());
            return false;
        }
        return true;
    }
}
