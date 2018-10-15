package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

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

public class IvPca {
    public Boolean random; // true if want this section to be generated randomly
    public String pcaStartTime; // "MM/DD/YYYY HHMM Z";
    public String medication; // "option 1-3";
    public String isLoadingDose; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public LoadingDose loadingDose;
    public String isPatientControlledBolus; // = y/no // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public PatientControlledBolusIvPca patientControlledBolus;
    public String isBasalRateContinuousInfusion; // PROBABLY DON'T NEED THIS.  DECIDE BASED ON IF OBJECT EXISTS
    public BasalRateContinuousInfusion basalRateContinuousInfusion;
    public String prePcaVerbalAnalogueScore; // "option 0-11";
    public String postPcaVerbalAnalogueScore; // "option 0-11";
    public String commentsNotesComplications; // "text";

    // I did this:
    private static By IV_LOADING_DOSE_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:injectionIndDecorate:injectionInd\"]/tbody/tr/td[1]/label");
    private static By IV_LOADING_DOSE_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:injectionIndDecorate:injectionInd\"]/tbody/tr/td[2]/label");

    private static By IV_LOADING_DOSE_DOSE_FIELD =
            By.xpath("//label[.='Loading Dose:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    // Patient Controlled Bolus (PCB)

    private static By IV_PCB_RADIO_YES_LABEL =
            By.xpath("//*[@id=\"painNoteForm:pcaIndDecorate:pcaInd\"]/tbody/tr/td[1]/label");
    private static By IV_PCB_RADIO_NO_LABEL =
            By.xpath("//*[@id=\"painNoteForm:pcaIndDecorate:pcaInd\"]/tbody/tr/td[2]/label");

    // I did this:
    private static By IV_BR_RADIO_YES_LABEL = By
            .xpath("//*[@id=\"painNoteForm:basalRateContinuousInfusionDecorate:infusionInd\"]/tbody/tr/td[1]/label");
    private static By IV_BR_RADIO_NO_LABEL = By
            .xpath("//*[@id=\"painNoteForm:basalRateContinuousInfusionDecorate:infusionInd\"]/tbody/tr/td[2]/label");

    private static By IV_BR_RATE_FIELD = By
            .xpath(".//*[@id='painNoteForm:basalRateFields:infusionRate']");
    private static By IV_BR_MEDICATION_CONCENTRATION_FIELD = By
            .xpath("//div[@id='painNoteForm:basalConcentrationFields']/div/table/tbody/tr/td/input");
    private static By IV_BR_INFUSION_START_TIME_FIELD = By
            .xpath("//input[@id='painNoteForm:basalDateFields:infusionDateInputDate']");
    private static By IV_BR_VOLUME_FIELD = By
            .xpath(".//*[@id='painNoteForm:basalVolumeFields:infusionQty']");

    private static By procedureNotesTabBy = By.xpath("//*[@id=\"procedureNoteTab\"]/a");

    private static By procedureSectionBy = By.id("procedureNoteTabContainer");

    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");

    private static By pcaStartTimeBy = By.id("ivPcaPainNoteFormPlacementDate");

    private static By medicationDropdownBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::select[@id=\"injectionMedication\"]");

    private static By pcbDoseFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"pcaQty\"]");
    private static By pcbLockoutFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"pcaLockout\"]");
    private static By medicationConcentrationFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"pcaConcentration\"]");
    private static By volumeFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"pcaVolume\"]");

    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::select[@id=\"preProcVas\"]");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::select[@id=\"postProcVas\"]");

    //private static By commentsTextAreaBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::select[@id=\"comments\"]");
    private static By commentsTextAreaBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::textarea[@id=\"comments\"]");

    //private static By commentsTextAreaBy = By.xpath("//*[@id=\"epiduralCatheterPainNoteForm\"]/descendant::textarea[@id=\"comments\"]");


    //private static By createNoteButtonBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/div/table/tbody/tr[18]/td[2]/button[1]");
    private static By createNoteButtonBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/div/table/tbody/tr[19]/td[2]/button[1]");

    private static By messageAreaForCreatingNoteBy = By.id("pain-note-message"); // verified on gold, and again, and again
    //private static By messageAreaForCreatingNoteBy = By.xpath("//*[@id=\"pain-note-message\"]"); // we'll try this one this time.  Makes no difference.

    private static By ivLoadingDoseRadioButtonYesBy = By.id("injectionInd9");
    private static By ivLoadingDoseRadioButtonNoBy = By.id("injectionInd10");
    private static By ivLoadingDoseRadioLabelYesBy = IV_LOADING_DOSE_RADIO_YES_LABEL;
    private static By ivLoadingDoseRadioLabelNoBy = IV_LOADING_DOSE_RADIO_NO_LABEL;

    private static By ivLoadingDoseDoseFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"injectionQty\"]");

    private static By ivPcbRadioButtonYesBy = By.id("pcaInd9"); // they made a mistake in naming this, but prob works
    private static By ivPcbRadioButtonNoBy = By.id("pcaInd10"); // they made a mistake in naming this, but prob works
    private static By ivPcbRadioLabelYesBy = IV_PCB_RADIO_YES_LABEL;
    private static By ivPcbRadioLabelNoBy = IV_PCB_RADIO_NO_LABEL;

    private static By ivBrRadioYesBy = By.id("infusionInd9");
    private static By ivBrRadioNoBy = By.id("infusionInd10");

    private static By ivBrRateFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"infusionRate\"]");
    private static By ivBrMedicationConcentrationFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"infusionConcentration\"]");
    private static By ivBrInfusionStartTimeFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"ivPcaInfusionPainNoteFormPlacementDate\"]");
    private static By ivBrVolumeFieldBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/descendant::input[@id=\"infusionQty\"]");

    public IvPca() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.pcaStartTime = "";
            this.medication = "";
            this.isLoadingDose = "";
            this.loadingDose = new LoadingDose();
            this.isPatientControlledBolus = "";
            this.patientControlledBolus = new PatientControlledBolusIvPca();
            this.isBasalRateContinuousInfusion = "";
            this.basalRateContinuousInfusion = new BasalRateContinuousInfusion();
            this.prePcaVerbalAnalogueScore = "";
            this.postPcaVerbalAnalogueScore = "";
            this.commentsNotesComplications = "";
        }
        if (isDemoTier) {
            procedureNotesTabBy = By.id("painNoteForm:Procedure_lbl");
            procedureSectionBy = By.id("painNoteForm:Procedure");
            dropdownForSelectProcedureBy = By.id("painNoteForm:selectProcedure");
            pcaStartTimeBy = By.id("painNoteForm:placementDateDecorate:placementDateInputDate");
            medicationDropdownBy = By.id("painNoteForm:selectedInjectionMedicationDecorate:selectedInjectionMedication");
            ivLoadingDoseDoseFieldBy = IV_LOADING_DOSE_DOSE_FIELD;
            pcbDoseFieldBy = By.id("painNoteForm:PCADoseFields:pcaQty");
            pcbLockoutFieldBy = By.id("painNoteForm:PCALockoutFields:pcaLockout");
            medicationConcentrationFieldBy = By.id("painNoteForm:PCAConcentrationFields:pcaConcentration");
            volumeFieldBy = By.id("painNoteForm:PCAVolumeFields:pcaVolume");
            ivBrRadioYesBy = IV_BR_RADIO_YES_LABEL;
            ivBrRadioNoBy = IV_BR_RADIO_NO_LABEL;
            ivBrRateFieldBy = IV_BR_RATE_FIELD;
            ivBrMedicationConcentrationFieldBy = IV_BR_MEDICATION_CONCENTRATION_FIELD;
            ivBrInfusionStartTimeFieldBy = IV_BR_INFUSION_START_TIME_FIELD;
            ivBrVolumeFieldBy = IV_BR_VOLUME_FIELD;
            preVerbalScoreDropdownBy = By.id("painNoteForm:preProcVasDecorate:preProcVas");
            postVerbalScoreDropdownBy = By.id("painNoteForm:postProcVasDecorate:postProcVas");
            commentsTextAreaBy = By.id("painNoteForm:commentsDecorate:comments");
            createNoteButtonBy = By.id("painNoteForm:createNoteButton"); // verified on demo
            messageAreaForCreatingNoteBy = By.xpath("//*[@id=\"painNoteForm:j_id1200\"]/table/tbody/tr/td/span"); // looks wrong, but kinda verifies.  But it's not always there, it seems
            //createNoteButtonBy = By.xpath("//*[@id=\"ivPcaPainNoteForm\"]/div/table/tbody/tr[18]/td[2]/button[1]");
        }
    }

    // I don't think the system allows for more than one active IV PCA procedure to exist.  So if there's already
    // one, then don't call this, or just get out.
    // This method is really long.  Break it out!
    public boolean process(Patient patient) { // here's #1 in IvPca
        //if (!Arguments.quiet) System.out.println("        Processing IV PCA ...");
        if (!Arguments.quiet) System.out.println("        Processing IV PCA for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try {
            WebElement procedureNotesTabElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(procedureNotesTabBy));
            procedureNotesTabElement.click();
            //Utilities.sleep(1002); // Hate to do this, but how do you find out when AJAX is done?
            // EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT
            //if (Arguments.debug) System.out.println("IvPca.process(), doing a call to isFinishedAjax");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
            //Utilities.ajaxWait();
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + e.getMessage());
            return false;
        }


//        By procedureSectionBy = By.id("painNoteForm:Procedure");
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(procedureSectionBy)));
        }
        catch (Exception e) {
            System.out.println("IvPca.process(), Did not find the procedure section.  Exception caught: " + e.getMessage());
            return false;
        }

        // Selecting a procedure from the "Select Procedure" dropdown causes a server request/response so as to
        // populate the form with the right fields.  This takes time.  So after the selection we have to
        // wait for the stuff to come back or we could miss the elements and cause errors.
        // So how do we know when the stuff has come back?  Wait until the current stuff gets removed and then
        // wait until the new stuff comes in?

        try {
            (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.presenceOfElementLocated(dropdownForSelectProcedureBy));
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // nec?
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("IvPca.process(), exception while waiting for dropdownForSelectProcedureBy: " + e.getMessage());
        }


        String procedureNoteProcedure = "IV PCA";
        Utilities.sleep(1555);
        try {

            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(dropdownForSelectProcedureBy));
            procedureNoteProcedure = Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.random, true); // set true to go further
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // new
            Utilities.sleep(1055); // See if this helps.  Hate to do it  Often get error can't do date because couldn't fillInTextField.
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Could not get IVPCA procedure dropdown.");
            return false;
        }

        if (Arguments.date != null && (this.pcaStartTime == null || this.pcaStartTime.isEmpty())) {
            this.pcaStartTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        // Date/Time stuff causes a server request/response, I think, or at least some kind of JavaScript thing, which takes time
        // And so you can't go too fast after this next call.
        this.pcaStartTime = Utilities.processDateTime(pcaStartTimeBy, this.pcaStartTime, this.random, true); // fails often
        // There's no AJAX with this datetime element and the dropdown, or after the process
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // I didn't see any AJAX call, but maybe exists deeper

        try {
            (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.visibilityOfElementLocated(medicationDropdownBy));
            this.medication = Utilities.processDropdown(medicationDropdownBy, this.medication, this.random, true); // npe: 2
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("IvPca.process(), couldn't get medication dropdown.  Required, so will bomb out here.  Exception: " + e.getMessage());
            return false;
        }

        if (isDemoTier) {
            this.isLoadingDose = Utilities.processRadiosByLabel(this.isLoadingDose, this.random, true, ivLoadingDoseRadioLabelYesBy, ivLoadingDoseRadioLabelNoBy);
        }
        else if (isGoldTier) { // what is this.isLoadingDose value?
            this.isLoadingDose = Utilities.processRadiosByButton(this.isLoadingDose, this.random, true, ivLoadingDoseRadioButtonYesBy, ivLoadingDoseRadioButtonNoBy);
        }
        if (this.isLoadingDose != null && this.isLoadingDose.equalsIgnoreCase("Yes")) {
            // need to allocate here?
            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new

            // following correct?  If so, need to do it for similar subsections
            if (this.loadingDose == null) {
                this.loadingDose = new LoadingDose();
            }
            if (this.loadingDose.random == null) {
                this.loadingDose.random = (this.random == null) ? false : this.random;
            }

            this.loadingDose.dose = Utilities.processDoubleNumber(ivLoadingDoseDoseFieldBy, this.loadingDose.dose, 0, 25, this.random, true);
        }

        if (isDemoTier) {
            this.isPatientControlledBolus = Utilities.processRadiosByLabel(this.isPatientControlledBolus, this.random, true, ivPcbRadioLabelYesBy, ivPcbRadioLabelNoBy);
        }
        else if (isGoldTier) {
            this.isPatientControlledBolus = Utilities.processRadiosByButton(this.isPatientControlledBolus, this.random, true, ivPcbRadioButtonYesBy, ivPcbRadioButtonNoBy);
        }
        (new WebDriverWait(Driver.driver, 15)).until(Utilities.isFinishedAjax()); // new
        if (this.isPatientControlledBolus != null && this.isPatientControlledBolus.equalsIgnoreCase("Yes")) {
            // need to allocate here?

            // following correct?  If so, need to do it for similar subsections
            if (this.patientControlledBolus == null) { // what?  We just checked that it wasn't null
                this.patientControlledBolus = new PatientControlledBolusIvPca(); // right?  Yeah
            }
            if (this.patientControlledBolus.random == null) {
                this.patientControlledBolus.random = (this.random == null) ? false : this.random;
            }
            this.patientControlledBolus.dose = Utilities.processDoubleNumber(pcbDoseFieldBy, this.patientControlledBolus.dose, 0, 25, this.random, true);
            this.patientControlledBolus.lockout = Utilities.processDoubleNumber(pcbLockoutFieldBy, this.patientControlledBolus.lockout, 0, 60, this.random, true);
            this.patientControlledBolus.medicationConcentration = Utilities.processDoubleNumber(medicationConcentrationFieldBy, this.patientControlledBolus.medicationConcentration, 0.01, 50, this.random, true);
            this.patientControlledBolus.volumeToBeInfused = Utilities.processDoubleNumber(volumeFieldBy, this.patientControlledBolus.volumeToBeInfused, 0, 20, this.random, true);
        }

        if (isDemoTier) {
            this.isBasalRateContinuousInfusion = Utilities.processRadiosByLabel(this.isBasalRateContinuousInfusion, this.random, true, ivBrRadioYesBy, ivBrRadioNoBy);
        }
        else if (isGoldTier) {
            this.isBasalRateContinuousInfusion = Utilities.processRadiosByButton(this.isBasalRateContinuousInfusion, this.random, true, ivBrRadioYesBy, ivBrRadioNoBy);
        }
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new
        if (this.isBasalRateContinuousInfusion != null && this.isBasalRateContinuousInfusion.equalsIgnoreCase("Yes")) { // npe next line

            // following correct?  If so, need to do it for similar subsections
            if (this.basalRateContinuousInfusion == null) {
                this.basalRateContinuousInfusion = new BasalRateContinuousInfusion();
            }
            if (this.basalRateContinuousInfusion.random == null) {
                this.basalRateContinuousInfusion.random = (this.random == null) ? false : this.random;
            }

            this.basalRateContinuousInfusion.rate = Utilities.processDoubleNumber(ivBrRateFieldBy, this.basalRateContinuousInfusion.rate, 0, 20, this.random, true);

            this.basalRateContinuousInfusion.medicationCentration = Utilities.processDoubleNumber(ivBrMedicationConcentrationFieldBy, this.basalRateContinuousInfusion.medicationCentration, 0.01, 50, this.random, true);

            this.basalRateContinuousInfusion.infusionStartTime = Utilities.processText(ivBrInfusionStartTimeFieldBy, this.basalRateContinuousInfusion.infusionStartTime, Utilities.TextFieldType.DATE_TIME, this.random, true);
            // Does this next one not work?  Calendar date/time probably takes time.

            this.basalRateContinuousInfusion.volumeToBeInfused = Utilities.processDoubleNumber(ivBrVolumeFieldBy, this.basalRateContinuousInfusion.volumeToBeInfused, 0, 20, this.random, true);
        }
        // check the by for the dropdowns following
        this.prePcaVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.prePcaVerbalAnalogueScore, this.random, true);
        this.prePcaVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.prePcaVerbalAnalogueScore, this.random, true);
        // This next line takes 10 seconds to complete
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);

        // The problem with failing on Gold is that execution gets here before it's ready.  Something in the previous lines take s a very
        // long time to complete.
        //Utilities.clickButton(createNoteButtonBy); // Fails on Gold??????  can cause a message "An active IV PCA procedure already exists", and it won't save.



        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME

        // something below here failed on DEMO, role3

//*[@id="painNoteForm:j_id1200"]/table/tbody/tr/td/span
        // It appears on Demo there is no such message area for save operations??????????????, but there is on gold.
        // If that's the case we have to have different code for this section for demo and gold.
        // Why do we have to have such idiotic HTML code generated for this app?????????????????????
        //WebElement saveResultTextElement = null;
        try {
            // Next line fails on demo but only if you get here too fast!!!!!!!!!!!!!!!!!!!!!
            // Maybe next line also fails on gold
            if (Arguments.debug) System.out.println("IvPca.process(), waiting for createNoteButton to be clickable.");
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteButtonBy));

            // I believe there's a problem here (or below) on Gold.

            // Actually, I think it saves the note, but after that something goes wrong and part of the page goes missing, because there's a table
            // with the tab "Pain Management Notes", but nothing under the table.
            // Actually, even more of the page can go missing.  Nothing under Allergies too.

            // Does this one also cause the sections below Allergies to go blank, and thus cannot check message and cannot do clinical and Transfer notes?????


            // Next line fails on demo, even if you go slow.  Just changed this to invisibility.  Prob won't work
//            System.out.println("IvPca.process(), waiting for invisibility of message area, which may be dumb.");
//            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.invisibilityOfElementLocated(messageAreaForCreatingNoteBy));

            if (Arguments.debug) if (Arguments.debug) System.out.println("IvPca.process(), clicking on createNoteButton");

            // This next click can cause a lot to happen on the server.  It will probably cause an update to a table, and the new
            // info to be sent from the server to the client can take a while.
            createNoteButton.click(); // need to wait after this  // does this button work in Gold?????????????????????????????????????
            //if (Arguments.debug) System.out.println("IvPca.process(), doing a call to isFinishedAjax");
            if (Arguments.debug) System.out.println("IvPca.process(), waiting for ajax to finish.");

            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this help at all?  Seems not.  Blasts through?
            if (Arguments.debug) System.out.println("IvPca.process(), ajax is finished");

        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.err.println("IvPca.process(), failed to get get and click on the create note button(?).  Unlikely.  TimeoutException");
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("IvPca.process(), failed to get get and click on the create note button(?).  Unlikely.  Exception: " + e.getMessage());
            return false;
        }

        // We need this sleep because of the table that gets populated and inserted prior to the message "Note successfully created!"
        // Otherwise we try to read it, and there's nothing there to read!
        // How do you know how long it takes to update that table?  What would trigger when it's finished?
        // A test to see if ajax is finished?
        Utilities.sleep(1555); // maybe we need this when there is a table that gets inserted in front of the "Note successfully created!" message so we can read that message in time.


        // Maybe this isn't the best way to check for success, because I don't see any message and it seems to have saved
        try {
            WebElement saveResultTextElement = null;

//            System.out.println("In IvPca.process(), waiting for staleness of saveResultTextElement, which may be a bad idea.");
//            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(saveResultTextElement)); // hey, this wasn't set, so it's bound to fail
            if (Arguments.debug) System.out.println("In IvPca.process(), waiting for visibility of messageAreaForCreatingNote");
            //saveResultTextElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy));
            //saveResultTextElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy))); // line above has been coming back with blank response
            saveResultTextElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy));
            if (Arguments.debug) System.out.println("In IvPca.process(),maybe got some text, and so will save it.");
            String someTextMaybe = saveResultTextElement.getText();
            if (Arguments.debug) System.out.println("\t\t!!!!!!!!!!!!!!!!!!!!Hey what the hell text is in the results text element??????????!!!!!!!!!!!!!!!!!!!!!!!!: " + someTextMaybe);
            if (someTextMaybe == null || someTextMaybe.isEmpty()) {
                if (Arguments.debug) System.out.println("\t\tSo let's try it a fucking again.");
                saveResultTextElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaForCreatingNoteBy)); // why not try again?
                someTextMaybe = saveResultTextElement.getText();
                if (Arguments.debug) System.out.println("\t\tNow the text is this: ->" + someTextMaybe + "<-");
                if (Arguments.debug) System.out.println("\t\tIs it null? " + ((someTextMaybe == null) ? "yes" : "No"));
            }
            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                if (Arguments.debug) System.out.println("IvPca.process() successfully saved the note.");
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed to save IV PCA note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " : " + someTextMaybe);
                return false; // fails gold role3:2 role4:3    because sections of the page get deleted???
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("IvPca.process(), couldn't get message result from trying to save note.: " + e.getMessage());
            return false;
        }
        return true;
    }
}
