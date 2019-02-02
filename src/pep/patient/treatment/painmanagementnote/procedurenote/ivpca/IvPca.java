package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
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

public class IvPca {
    private static Logger logger = Logger.getLogger(IvPca.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
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
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[1]/label");
    private static By IV_LOADING_DOSE_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:injectionIndDecorate:injectionInd']/tbody/tr/td[2]/label");

    private static By IV_LOADING_DOSE_DOSE_FIELD =
            By.xpath("//label[.='Loading Dose:']/../../../../../../../../following-sibling::tr[1]/td/div/div/table/tbody/tr/td/input");
    // Patient Controlled Bolus (PCB)

    private static By IV_PCB_RADIO_YES_LABEL =
            By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[1]/label");
    private static By IV_PCB_RADIO_NO_LABEL =
            By.xpath("//*[@id='painNoteForm:pcaIndDecorate:pcaInd']/tbody/tr/td[2]/label");

    // I did this:
    private static By IV_BR_RADIO_YES_LABEL = By.xpath("//*[@id='painNoteForm:basalRateContinuousInfusionDecorate:infusionInd']/tbody/tr/td[1]/label");
    private static By IV_BR_RADIO_NO_LABEL = By.xpath("//*[@id='painNoteForm:basalRateContinuousInfusionDecorate:infusionInd']/tbody/tr/td[2]/label");

    private static By IV_BR_RATE_FIELD = By.xpath(".//*[@id='painNoteForm:basalRateFields:infusionRate']");
    private static By IV_BR_MEDICATION_CONCENTRATION_FIELD = By.xpath("//div[@id='painNoteForm:basalConcentrationFields']/div/table/tbody/tr/td/input");
    private static By IV_BR_INFUSION_START_TIME_FIELD = By.id("painNoteForm:basalDateFields:infusionDateInputDate");
    private static By IV_BR_VOLUME_FIELD = By.xpath(".//*[@id='painNoteForm:basalVolumeFields:infusionQty']");

    private static By procedureNotesTabBy = By.linkText("Procedure Notes"); // 1/23/19

    private static By procedureSectionBy = By.id("procedureNoteTabContainer");

    private static By dropdownForSelectProcedureBy = By.id("procedureNoteTypeBox");

    private static By pcaStartTimeBy = By.id("ivPcaPainNoteFormPlacementDate");

    private static By medicationDropdownBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::select[@id='injectionMedication']");

    private static By pcbDoseFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='pcaQty']");
//    private static By pcbLockoutFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='pcaLockout']");
    private static By pcbLockoutFieldBy = By.xpath("//tr[@id='ivpcaPcaLockout']/td/input[@id='pcaLockout']"); // just to prove these can be shorter
    private static By medicationConcentrationFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='pcaConcentration']");
    private static By volumeFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='pcaVolume']");

    private static By preVerbalScoreDropdownBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::select[@id='preProcVas']");
    private static By postVerbalScoreDropdownBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::select[@id='postProcVas']");

    private static By commentsTextAreaBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::textarea[@id='comments']");

    private static By createNoteButtonBy = By.xpath("//form[@id='ivPcaPainNoteForm']/descendant::button[text()='Create Note']");

    // keep bouncing back between the following two
    private static By messageAreaForCreatingNoteBy = By.xpath("//*[@id='procedureNoteTab']/preceding-sibling::div[@id='pain-note-message']"); // should be same as By.id("ivPcaPainNoteForm.errors"

    private static By ivLoadingDoseRadioButtonYesBy = By.id("injectionInd9");
    private static By ivLoadingDoseRadioButtonNoBy = By.id("injectionInd10");
    private static By ivLoadingDoseRadioLabelYesBy = IV_LOADING_DOSE_RADIO_YES_LABEL;
    private static By ivLoadingDoseRadioLabelNoBy = IV_LOADING_DOSE_RADIO_NO_LABEL;

    private static By ivLoadingDoseDoseFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='injectionQty']");

    private static By ivPcbRadioButtonYesBy = By.id("pcaInd9"); // they made a mistake in naming this, but prob works
    private static By ivPcbRadioButtonNoBy = By.id("pcaInd10"); // they made a mistake in naming this, but prob works
    private static By ivPcbRadioLabelYesBy = IV_PCB_RADIO_YES_LABEL;
    private static By ivPcbRadioLabelNoBy = IV_PCB_RADIO_NO_LABEL;

    private static By ivBrRadioYesBy = By.id("infusionInd9");
    private static By ivBrRadioNoBy = By.id("infusionInd10");

    private static By ivBrRateFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='infusionRate']");
    private static By ivBrMedicationConcentrationFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='infusionConcentration']");
    private static By ivBrInfusionStartTimeFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='ivPcaInfusionPainNoteFormPlacementDate']");
    private static By ivBrVolumeFieldBy = By.xpath("//*[@id='ivPcaPainNoteForm']/descendant::input[@id='infusionQty']");

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
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
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
            messageAreaForCreatingNoteBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span"); // looks wrong, but kinda verifies.  But it's not always there, it seems
            //createNoteButtonBy = By.xpath("//*[@id='ivPcaPainNoteForm']/div/table/tbody/tr[18]/td[2]/button[1]");
        }
    }

    // I don't think the system allows for more than one active IV PCA procedure to exist.  So if there's already
    // one, then don't call this, or just get out.
    // This method is really long.  Break it out!
    public boolean process(Patient patient) { // here's #1 in IvPca
        if (!Arguments.quiet) System.out.println("        Processing IV PCA for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );


        // We assume that the tab exists and we don't have to check anything.  Don't know if that's right though.
        // One thing is certain though, when you click on the tab there's going to be an AJAX.Submit call, and
        // that takes time.
        try {
            WebElement procedureNotesTabElement = Utilities.waitForVisibility(procedureNotesTabBy, 10, "IvPca.process()");
            procedureNotesTabElement.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("ProcedureNote.process(), failed to get the Procedure Notes tab and click it.  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            String fileName = ScreenShot.shoot("Error-" + this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote error screenshot file " + fileName);
            return false;
        }

        try {
            Utilities.waitForRefreshedVisibility(procedureSectionBy,  10, "IvPca.(), procedure section");
        }
        catch (Exception e) {
            logger.severe("IvPca.process(), Did not find the procedure section.  Exception caught: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        // Selecting a procedure from the "Select Procedure" dropdown causes a server request/response so as to
        // populate the form with the right fields.  This takes time.  So after the selection we have to
        // wait for the stuff to come back or we could miss the elements and cause errors.
        // So how do we know when the stuff has come back?  Wait until the current stuff gets removed and then
        // wait until the new stuff comes in?

        try {
            Utilities.waitForPresence(dropdownForSelectProcedureBy, 2, "IvPca.process()");
        }
        catch (Exception e) {
            logger.fine("IvPca.process(), exception while waiting for dropdownForSelectProcedureBy: " + Utilities.getMessageFirstLine(e));
        }


        String procedureNoteProcedure = "IV PCA";
        Utilities.sleep(1555, "IvPca");
        try {

            Utilities.waitForPresence(dropdownForSelectProcedureBy, 10, "IvPca.process()");
            procedureNoteProcedure = Utilities.processDropdown(dropdownForSelectProcedureBy, procedureNoteProcedure, this.random, true); // set true to go further
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // new
            Utilities.sleep(1055, "IvPca"); // See if this helps.  Hate to do it  Often get error can't do date because couldn't fillInTextField.
        }
        catch (Exception e) {
            logger.severe("Could not get IVPCA procedure dropdown.");
            return false;
        }

        if (Arguments.date != null && (this.pcaStartTime == null || this.pcaStartTime.isEmpty())) {
            this.pcaStartTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        // Date/Time stuff causes a server request/response, I think, or at least some kind of JavaScript thing, which takes time
        // And so you can't go too fast after this next call.
        this.pcaStartTime = Utilities.processDateTime(pcaStartTimeBy, this.pcaStartTime, this.random, true); // fails often
        // There's no AJAX with this datetime element and the dropdown, or after the process
        //(new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // I didn't see any AJAX call, but maybe exists deeper // removed 11/23/18

        try {
            Utilities.waitForVisibility(medicationDropdownBy, 4, "IvPca.process()");
            this.medication = Utilities.processDropdown(medicationDropdownBy, this.medication, this.random, true); // npe: 2
        }
        catch (Exception e) {
            logger.fine("IvPca.process(), couldn't get medication dropdown.  Required, so will bomb out here.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isLoadingDose = Utilities.processRadiosByLabel(this.isLoadingDose, this.random, true, ivLoadingDoseRadioLabelYesBy, ivLoadingDoseRadioLabelNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // what is this.isLoadingDose value?
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
                this.loadingDose.random = this.random; // removed setting to false if null
            }
            if (this.loadingDose.shoot == null) {
                this.loadingDose.shoot = this.shoot;
            }

            this.loadingDose.dose = Utilities.processDoubleNumber(ivLoadingDoseDoseFieldBy, this.loadingDose.dose, 0, 25, this.random, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isPatientControlledBolus = Utilities.processRadiosByLabel(this.isPatientControlledBolus, this.random, true, ivPcbRadioLabelYesBy, ivPcbRadioLabelNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
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
                this.patientControlledBolus.random = this.random; // removed setting to false if null
            }
            if (this.patientControlledBolus.shoot == null) {
                this.patientControlledBolus.shoot = this.shoot;
            }
            this.patientControlledBolus.dose = Utilities.processDoubleNumber(pcbDoseFieldBy, this.patientControlledBolus.dose, 0, 25, this.random, true);
            this.patientControlledBolus.lockout = Utilities.processDoubleNumber(pcbLockoutFieldBy, this.patientControlledBolus.lockout, 0, 60, this.random, true);
            this.patientControlledBolus.medicationConcentration = Utilities.processDoubleNumber(medicationConcentrationFieldBy, this.patientControlledBolus.medicationConcentration, 0.1, 50, this.random, true);
            this.patientControlledBolus.volumeToBeInfused = Utilities.processDoubleNumber(volumeFieldBy, this.patientControlledBolus.volumeToBeInfused, 0, 20, this.random, true);
        }

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.isBasalRateContinuousInfusion = Utilities.processRadiosByLabel(this.isBasalRateContinuousInfusion, this.random, true, ivBrRadioYesBy, ivBrRadioNoBy);
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.isBasalRateContinuousInfusion = Utilities.processRadiosByButton(this.isBasalRateContinuousInfusion, this.random, true, ivBrRadioYesBy, ivBrRadioNoBy);
        }
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // new
        if (this.isBasalRateContinuousInfusion != null && this.isBasalRateContinuousInfusion.equalsIgnoreCase("Yes")) { // npe next line

            // following correct?  If so, need to do it for similar subsections
            if (this.basalRateContinuousInfusion == null) {
                this.basalRateContinuousInfusion = new BasalRateContinuousInfusion();
            }
            if (this.basalRateContinuousInfusion.random == null) {
                this.basalRateContinuousInfusion.random = this.random; // removed setting to false if null
            }
            if (this.basalRateContinuousInfusion.shoot == null) {
                this.basalRateContinuousInfusion.shoot = this.shoot;
            }

            this.basalRateContinuousInfusion.rate = Utilities.processDoubleNumber(ivBrRateFieldBy, this.basalRateContinuousInfusion.rate, 0, 20, this.random, true);

            this.basalRateContinuousInfusion.medicationCentration = Utilities.processDoubleNumber(ivBrMedicationConcentrationFieldBy, this.basalRateContinuousInfusion.medicationCentration, 0.1, 50, this.random, true);

            this.basalRateContinuousInfusion.infusionStartTime = Utilities.processText(ivBrInfusionStartTimeFieldBy, this.basalRateContinuousInfusion.infusionStartTime, Utilities.TextFieldType.DATE_TIME, this.random, true);
            // Does this next one not work?  Calendar date/time probably takes time.

            this.basalRateContinuousInfusion.volumeToBeInfused = Utilities.processDoubleNumber(ivBrVolumeFieldBy, this.basalRateContinuousInfusion.volumeToBeInfused, 0, 20, this.random, true);
        }
        // check the by for the dropdowns following
        this.prePcaVerbalAnalogueScore = Utilities.processDropdown(preVerbalScoreDropdownBy, this.prePcaVerbalAnalogueScore, this.random, true);
        this.postPcaVerbalAnalogueScore = Utilities.processDropdown(postVerbalScoreDropdownBy, this.prePcaVerbalAnalogueScore, this.random, true);
        // This next line takes 10 seconds to complete.  Really?
        this.commentsNotesComplications = Utilities.processText(commentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);


//        if (this.commentsNotesComplications != null && !this.commentsNotesComplications.isEmpty()) {
//            Utilities.sleep(2555, "IvPca"); // this is just a first guess to see if long enough to wait so createNoteButton doesn't blow up with a Problem page.
//        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("          Wrote screenshot file " + fileName);
        }

        // The problem with failing on Gold is that execution gets here before it's ready.  Something in the previous lines take s a very
        // long time to complete.

        // ALL THIS NEXT STUFF SHOULD BE COMPARED TO THE OTHER THREE PAIN SECTIONS.  THEY SHOULD ALL WORK THE SAME, AND SO THE CODE SHOULD BE THE SAME
        Instant start = null;
        try {
            // Maybe next line also fails on gold
            logger.fine("IvPca.process(), waiting for createNoteButton to be clickable.");
//            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(createNoteButtonBy)));
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(createNoteButtonBy, 10, "IvPca.process() getting create button");
           // WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteButtonBy));

            // This next click can cause a lot to happen on the server.  It will probably cause an update to a table, and the new
            // info to be sent from the server to the client can take a while.

            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "IvPca");
            }
            start = Instant.now();
            createNoteButton.click(); // need to wait after this  // does this button work in Gold?????????????????????????????????????
//            timerLogger.info("Epidural Catheter note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
// wait here a while to see if helps
            Utilities.sleep(355, "IvPca");
            (new WebDriverWait(Driver.driver, 60)).until(ExpectedConditions.stalenessOf(createNoteButton)); // new 11/19/18

            logger.fine("IvPca.process(), waiting for ajax to finish.");

            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this help at all?  Seems not.  Blasts through? // removed 11/24/18
           // logger.fine("IvPca.process(), ajax is finished");

        }
        catch (TimeoutException e) {
            logger.severe("IvPca.process(), failed to get and click on the create note button(?).  Unlikely.  TimeoutException");
            return false;
        }
        catch (Exception e) {
            logger.severe("IvPca.process(), failed to get and click on the create note button(?).  Unlikely.  Exception: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        Utilities.sleep(5555, "IvPca"); // maybe we need this when there is a table that gets inserted in front of the "Note successfully created!" message so we can read that message in time.

        // In this area, we can get an error message that "An active IV PCA procedure already exists", which we're
        // not currently looked at.  Maybe before, but not now.  Maybe should be.  comes from //*[@id="ivPcaPainNoteForm.errors"]
        // Maybe this isn't the best way to check for success, because I don't see any message and it seems to have saved
        try {
            WebElement saveResultTextElement = null;

            // Might want to do a staleness on this.  That is, we may have a message hanging over from a previous operation
            // Also, I'd bet that the success message is in one node, and failure message in another, like "An active IV PCA procedure already exists"
            saveResultTextElement = Utilities.waitForVisibility(messageAreaForCreatingNoteBy, 5, "IvPca.process()");
            String someTextMaybe = saveResultTextElement.getText();
            if (someTextMaybe == null || someTextMaybe.isEmpty()) {
                logger.fine("\t\tSo let's try it again.");
                saveResultTextElement = Utilities.waitForVisibility(messageAreaForCreatingNoteBy, 5, "IvPca.process()"); // why not try again?
                someTextMaybe = saveResultTextElement.getText();
                logger.fine("\t\tNow the text is this: ->" + someTextMaybe + "<-");
                logger.fine("\t\tIs it null? " + ((someTextMaybe == null) ? "yes" : "No"));
            }
            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                logger.fine("IvPca.process() successfully saved the note.");
            }
            else {
//                if (!Arguments.quiet) System.err.println("        ***Failed to save IV PCA note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " + someTextMaybe);
                if (!Arguments.quiet) System.err.println("        ***Failed to save IV PCA note for "
                        + patient.patientSearch.firstName + " "
                        + patient.patientSearch.lastName
                        + " ssn:" + patient.patientSearch.ssn
                        +  ((someTextMaybe == null || someTextMaybe.isEmpty()) ? "" : (" message: " + someTextMaybe)));
                return false; // Active IVPCA already exists?  because sections of the page get deleted???
            }
        }
        catch (Exception e) {
            logger.fine("IvPca.process(), couldn't get message result from trying to save note.: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("          Saved IV PCA note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        //timerLogger.info("IvPca note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        timerLogger.info("IvPca note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "IvPca");
        }
        return true;
    }
}
