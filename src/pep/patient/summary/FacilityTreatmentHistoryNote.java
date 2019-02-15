package pep.patient.summary;

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

// This FacilityTreatmentHistoryNote is similar to BehavioralHealthNote, so it should be patterned after that.

public class FacilityTreatmentHistoryNote {
    private static Logger logger = Logger.getLogger(FacilityTreatmentHistoryNote.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String note; // "you can either do this, or have a Note Template with the following";

    public String service; // "free text";
    public String attendingStaff; // "free text";
    public String workingDiagnoses; // "free text";
    public String careRenderedSinceLastUpdate; // "free text";
    public String adlUpdate; // "free text";
    public String prognosis; // "free text";
    public String estimatedDischargeDate; // "MM/DD/YYYY";
    public String date; // "MM/DD/YYYY";
    public String disposition; // "free text";
    public String needsAndRequirements; // "free text";
    public String careStatus; // radio button set
    public Boolean availableAtVa; // only available on Seam?

//    private static By createNoteLinkBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]/div[2]/a"); // correct but dangerous
    private static By createNoteLinkBy = By.linkText("Create Note"); // correct but dangerous

    // Behavioral Health Note plays a game where the main section is made up of two parts where only one
    // is visible at a time, and they swap back and forth depending on whether you click on "Use Note Template"
    // or "Use Default Template".  These parts each include a "Save Button", and they are different buttons!
    // This also includes the "BH Note Type" dropdown element.  It probably also includes message areas.
    // You have to use the right version of the element for the template you select (default is default template).
    //

    // Default Template:
//    private static By defaultTemplateUseTemplateLinkBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/span[2]/a");
    private static By defaultTemplateUseTemplateLinkBy = By.linkText("Use Note Template");
    private static By defaultTemplateBhNoteTypeDropdownBy = By.id("defaultNoteTypeId");
    //private static By defaultTemplateSaveButtonBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/div/button");
    private static By defaultTemplateSaveButtonBy = By.xpath("//div[@id=\"defaultTemplateContainer\"]/div/button[text()='Save Note']");
    private static By defaultTemplateNoteAreaBy = By.id("defaultNoteText");

    // Note Template:
    private static By noteTemplateBhNoteTypeDropdownBy = By.id("templateNoteTypeId");
    //private static By noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//*[@id=\"noteTemplateContainer\"]/div/button");
    private static By noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//div[@id='noteTemplateContainer']/div/button[text()='Save Note']");

    // following is prob wrong
//    private static By defaultPendingRtdRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[1]");
//    private static By defaultPendingTransferRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[2]");
//    private static By defaultFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[3]");
//    private static By defaultPendingEvacRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[4]");
//    private static By defaultPendingRtdRadioLabelBy = By.xpath("//label[@for='templatePENDING RTD'][text()='PENDING RTD']");
//    private static By defaultPendingTransferRadioLabelBy = By.xpath("//label[@for='templatePENDING TRANSFER'][text()='PENDING TRANSFER']");
//    //private static By defaultPendingTransferRadioLabelBy = By.xpath("//form[@id='template-select-form']/label[text()='PENDING TRANSFER']");
//    private static By defaultFollowUpApptRadioLabelBy = By.xpath("//label[@for='templatePENDING TRANSFER'][text()='FOLLOW UP APPT']");
//    private static By defaultPendingEvacRadioLabelBy = By.xpath("//label[@for='templatePENDING TRANSFER'][text()='PENDING EVAC']");
    private static By defaultPendingRtdRadioLabelBy = By.xpath("//label[@for='templatePENDING RTD']");
    private static By defaultPendingTransferRadioLabelBy = By.xpath("//label[@for='templatePENDING TRANSFER']");
    private static By defaultFollowUpApptRadioLabelBy = By.xpath("//label[@for='templateFOLLOW UP APPT']");
    private static By defaultPendingEvacRadioLabelBy = By.xpath("//label[@for='templatePENDING EVAC']");

    private static By defaultPendingRtdRadioButtonBy = By.id("templatePENDING RTD");
    private static By defaultPendingTransferRadioButtonBy = By.id("templatePENDING TRANSFER");
    private static By defaultFollowUpApptRadioButtonBy = By.id("templateFOLLOW UP APPT");
    private static By defaultPendingEvacRadioButtonBy = By.id("templatePENDING EVAC");

//    private static By templatePendingRtdRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[1]");
//    private static By templatePendingTransferRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[2]");
//    private static By templateFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[3]");
//    private static By templatePendingEvacRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[4]");

    private static By templatePendingRtdRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[1]");
    private static By templatePendingTransferRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[2]");
    private static By templateFollowUpApptRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[3]");
    private static By templatePendingEvacRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[4]");

    //private static By bhPopupSaveNoteBy = By.xpath("//button[text()='Save Note']");
// ID's on the Patient Summary page are not unique!  id="patient-demographics-tab" for example
    // Having a hard time finding a better xpath on the following:
    private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]"); // would like to improve this
//    private static By bhaBhnSuccessMessageAreaBy = By.xpath("//div[starts-with(text(),'You have successfully')]");
    // Okay, the above is a breaththrough for me.  I didn't know how to use those functions like starts-with().  This should be used elsewhere!!!
//    private static By bhaBhnSuccessMessageAreaBy = By.xpath("//div[text()='You have successfully created a Patient Treatment Management Note!']");
    private static By serviceBy = By.id("service");
    private static By attendingStaffBy = By.id("attendingStaff");
    private static By workingDiagnosesBy = By.id("workingDiagnosis");
    private static By careRenderedSinceLastUpdateBy = By.id("careRendered");
    private static By adlUpdateBy = By.id("adlUpdated");
    private static By prognosisBy = By.id("prognosis");
    private static By estimatedDischargeDateBy = By.id("estimatedDate");
    private static By dateBy = By.id("date");
    private static By dispositionBy = By.id("disposition");
    private static By needsAndRequirementsBy = By.id("needsAndRequirements");

    private static By availableAtVaBy = By.xpath("//*[@id=\"createNoteForm:j_id808\"]/table/tbody/tr/td[1]/input");


    public FacilityTreatmentHistoryNote() {
        if (Arguments.template) {
            this.note = "";
            this.service = "";
            this.attendingStaff = "";
            this.workingDiagnoses = "";
            this.careRenderedSinceLastUpdate = "";
            this.adlUpdate = "";
            this.prognosis = "";
            this.estimatedDischargeDate = "";
            this.date = "";
            this.disposition = "";
            this.needsAndRequirements = "";
            //this.bhNoteType = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            createNoteLinkBy = By.id("j_id839:j_id845"); // will probably work most of the time, but dangerous
            defaultTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");
            noteTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");
            //bhPopupSaveNoteBy = By.id("createNoteForm:submitNote");
            defaultTemplateNoteAreaBy = By.id("createNoteForm:noteTextDecorator:noteTextInput");
            defaultTemplateSaveButtonBy = By.id("createNoteForm:submitNote");
            defaultTemplateUseTemplateLinkBy = By.id("createNoteForm:j_id700");
            adlUpdateBy = By.id("createNoteForm:j_id742:adlUpdateTextInput");
            attendingStaffBy = By.id("createNoteForm:j_id718:attendingStaffTextInput");
            careRenderedSinceLastUpdateBy = By.id("createNoteForm:j_id734:careRenderedTextInput");
            dateBy = By.id("createNoteForm:j_id770:j_id780InputDate");
            dispositionBy = By.id("createNoteForm:j_id782:dispositionTextInput");
            estimatedDischargeDateBy = By.id("createNoteForm:j_id759:j_id769InputDate");
            needsAndRequirementsBy = By.id("createNoteForm:j_id790:needsTextInput");
            prognosisBy = By.id("createNoteForm:j_id750:prognosisTextInput");
            serviceBy = By.id("createNoteForm:j_id710:serviceTextInput");
            workingDiagnosesBy = By.id("createNoteForm:j_id726:workingDiagnosisTextInput");
            noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//*[@id=\"createNoteForm:submitNote\"]");
//            defaultPendingRtdRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[1]/label");
//            defaultPendingTransferRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[2]/label");
//            defaultFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[3]/label");
//            defaultPendingEvacRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[4]/label");
//            templatePendingRtdRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[1]/label");
//            templatePendingTransferRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[2]/label");
//            templateFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[3]/label");
//            templatePendingEvacRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[4]/label");
            bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"j_id830\"]/table/tbody/tr/td/span"); // would like to improve this
        }
    }

    // This method seems a bit off.  Check logic, and compare against similar.
    //public boolean process(Patient patient, BehavioralHealthAssessment behavioralHealthAssessment) {
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Facility Treatment History Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );


        // Did I forget to copy down the section random value?


        try { // get to next line too quickly?
            WebElement createNoteLinkElement = Utilities.waitForRefreshedClickability(createNoteLinkBy, 15, "FacilityTreatmentHistoryNote.(), create note link"); // was 5, then 10
            //WebElement createNoteLinkElement = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.presenceOfElementLocated(createNoteLinkBy)); // was 5, then 10
            createNoteLinkElement.click(); // ajax?
            (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.severe("FacilityTreatmentHistoryNote.process(), Timeout exception, couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false; // failed with timeout on gold: 1
        }
        catch (Exception e) {
            logger.fine("FacilityTreatmentHistoryNote.process(), couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false; // failed with timeout on gold: 1
        }

        // At this point we should have a modal window popped up, and it says "Behavioral Health Note" in the title.
        // It contains a text area, a dropdown, a Save Note button, and above all that there's a link "Use Note Template".
        // You can use that link, or not.  If you do, then a new window pops up with the template to fill in.
        // The deciding factor in whether to use the note template is if the fields were defined in the input file.
        // If the input field "note" value is specified, then use the Default Template.
        boolean useDefaultTemplate = (this.note != null && !this.note.isEmpty());  // can use?  Make simpler?
        // because of "random", both of these could be true.  We do the NotesTemplate one, in that case, and DefaultTemplate gets skipped
        boolean useNotesTemplate = ((!((this.service == null || this.service.isEmpty()) &&
                (this.attendingStaff == null || this.attendingStaff.isEmpty()) &&
                (this.workingDiagnoses == null || this.workingDiagnoses.isEmpty()) &&
                (this.careRenderedSinceLastUpdate == null || this.careRenderedSinceLastUpdate.isEmpty()) &&
                (this.adlUpdate == null || this.adlUpdate.isEmpty()) &&
                (this.prognosis == null || this.prognosis.isEmpty()) &&
                (this.estimatedDischargeDate == null || this.estimatedDischargeDate.isEmpty()) &&
                (this.date == null || this.date.isEmpty()) &&
                (this.disposition == null || this.disposition.isEmpty()) &&
                (this.needsAndRequirements == null || this.needsAndRequirements.isEmpty()))));
        // Do one or the other
        if (!useNotesTemplate) {
            useDefaultTemplate = true;
        }
        Instant start = null;


        // Two ways to do this popup, either with default template, or note template!
        // Note template is the one with the bunch of text fields.  Default is one text field.
        // Both have radio buttons and a save button at the bottom.  Later break this long
        // method into two parts.
        if (useNotesTemplate) {
            // open up the note template to get to the fields there
            try {
                //WebElement useNoteTemplateLink = (new WebDriverWait(Driver.driver, 6)).until(ExpectedConditions.elementToBeClickable(defaultTemplateUseTemplateLinkBy));
                WebElement useNoteTemplateLink = Utilities.waitForClickability(defaultTemplateUseTemplateLinkBy, 6, "FacilityTreatmentHistoryNote.process()");
                useNoteTemplateLink.click();
            }
            catch (TimeoutException e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), Timeout exception, couldn't get Use Note Template link, or couldn't click on it.");
                return false;
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't get Use Note Template link, or couldn't click on it.");
                return false;
            }

            try {
                // FOLLOWING COMMENTED OUT ONLY FOR TESTING AN ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // fill in the fields.  How can this section fail if none of the fields are required?  Only by not filling in any.
                this.service = Utilities.processText(serviceBy, this.service, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.attendingStaff = Utilities.processText(attendingStaffBy, this.attendingStaff, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.workingDiagnoses = Utilities.processText(workingDiagnosesBy, this.workingDiagnoses, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.careRenderedSinceLastUpdate = Utilities.processText(careRenderedSinceLastUpdateBy, this.careRenderedSinceLastUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.adlUpdate = Utilities.processText(adlUpdateBy, this.adlUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.prognosis = Utilities.processText(prognosisBy, this.prognosis, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.estimatedDischargeDate = Utilities.processText(estimatedDischargeDateBy, this.estimatedDischargeDate, Utilities.TextFieldType.DATE, this.random, false);
                this.date = Utilities.processText(dateBy, this.date, Utilities.TextFieldType.DATE, this.random, false);
                this.disposition = Utilities.processText(dispositionBy, this.disposition, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                this.needsAndRequirements = Utilities.processText(needsAndRequirementsBy, this.needsAndRequirements, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                //this.careStatus = Utilities.processRadiosByButton(this.careStatus, this.random, false, defaultPendingRtdRadioButtonBy, defaultPendingTransferRadioButtonBy, defaultFollowUpApptRadioButtonBy, defaultPendingEvacRadioButtonBy);
                // This next thing doesn't click a button
                // This radio button set is organized differently than others.  The set is in a <form>
                // IS IT TRUE THAT THE FOLLOWING ALWAYS RETURNS THE FIRST RADIO BUTTON????????????????????????????????????????????????????
                this.careStatus = Utilities.processRadiosByLabel(this.careStatus, this.random, false,
                        defaultPendingRtdRadioLabelBy,
                        defaultPendingTransferRadioLabelBy,
                        defaultFollowUpApptRadioLabelBy,
                        defaultPendingEvacRadioLabelBy);


//                this.careStatus = Utilities.processRadiosByButton(this.careStatus, this.random, false,
//                        defaultPendingRtdRadioButtonBy,
//                        defaultPendingTransferRadioButtonBy,
//                        defaultFollowUpApptRadioButtonBy,
//                        defaultPendingEvacRadioButtonBy);

                // For Seam code this next checkbox exists.  Looks like was removed in Spring code:
                if (codeBranch.equalsIgnoreCase("Seam")) {
                    try {
                        WebElement availableCheckBox = Utilities.waitForVisibility(availableAtVaBy, 3, "FacilityTreatmentHistoryNote.(), available checkbox");
                        this.availableAtVa = Utilities.processBoolean(availableAtVaBy, this.availableAtVa, this.random, false);
                    } catch (Exception e) {
                        logger.severe("FacilityTreatmentHistoryNote.process(), didn't find availableAtVA checkbox, because this is Spring code.  Seam had it.");
                    }
                }
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't find or fill in some element: " + Utilities.getMessageFirstLine(e));
                return false;
            }

            if (this.shoot != null && this.shoot) {
                String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
                if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
            }

            // stuff below is not same as the similar popup page.  No bhNoteType.  But there are radio buttons.
            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = Utilities.waitForRefreshedClickability(noteTemplateBhPopupSaveNoteForTemplateBy, 3, "FacilityTreatmentHistoryNote.(), save note button");
                if (Arguments.pauseSave > 0) {
                    Utilities.sleep(Arguments.pauseSave * 1000, "");
                }
                start = Instant.now();
                popupSaveNoteElement.click(); // this is slow.  Over 2 seconds.
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
                return false;
            }

            // Check for success message after trying to save this Behavioral Health Note.
            // If successful, the modal window goes away and we're back to the previous page, and there should
            // be a green message saying success.  But if the modal window failed, then there will be a message
            // there.  So, unless we get back to that page, there was an error and it wasn't saved, and we might as well just return false.

            // Seems to me rather than do what I've been doing (wait for staleness of popup and then find the success message,
            // OR

            // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
            try {
                (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
                logger.finest("FacilityTreatmentHistoryNote.process(), Done waiting");
            }
            catch (Exception e) {
                logger.fine("FacilityTreatmentHistoryNote.process(), Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
                // continue, I guess.
            }

            try {
                //Utilities.sleep(3555, "FacilityTreatmentHistoryNote"); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
                // next line new 10/19/18  refreshed
                // 1/30/19 changed the By to look for a message element that had text that included "You have successfully" and so if
                // it is not found, then the requirements of the popup were not met, and the popup is still there and this will fail.
                // So, we don't really need to look for any more "successfully" text like is done in other places.
                // We can just return false.
                WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "FacilityTreatmentHistoryNote.(), success message area"); // not sure
//                String someTextMaybe = someElement.getText();
//                if (someTextMaybe.contains("successfully")) {
//                    logger.fine("FacilityTreatmentHistoryNote.process(), FacilityTreatmentHistoryNote.process(), saved note successfully.");
//                }
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));

                // maybe now look for the element //*[@id="note-msg"]  and get it's message and report it????

                return false;
            }
            if (!Arguments.quiet) {
                System.out.println("      Saved Facility Treatment History Note for patient " +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
            }
            //timerLogger.info("Facility Treatment History Note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            timerLogger.info("Facility Treatment History Note took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            if (Arguments.pausePage > 0) {
                Utilities.sleep(Arguments.pausePage * 1000, "FacilityTreatmentHistoryNote, requested sleep for page.");
            }
            return true;

        }

        // Now using default template...
        // Do we need to force this one, because the Note Template didn't succeed, or didn't have anything or something?
        if (useDefaultTemplate) { // Should be one or the other, because once we push the Save Note button, that's it.
            try {
                // get the sole "note" field/element to fill in
                Utilities.waitForVisibility(defaultTemplateNoteAreaBy, 1, "FacilityTreatmentHistoryNote.(), note area");
                this.note = Utilities.processText(defaultTemplateNoteAreaBy, this.note, Utilities.TextFieldType.BH_NOTE, this.random, true);
                //this.careStatus = Utilities.processRadiosByLabel(this.careStatus, this.random, false, templatePendingRtdRadioLabelBy, templatePendingTransferRadioLabelBy, templateFollowUpApptRadioLabelBy, templatePendingEvacRadioLabelBy);
                this.careStatus = Utilities.processRadiosByButton(this.careStatus, this.random, false,
                        templatePendingRtdRadioButtonBy,
                        templatePendingTransferRadioButtonBy,
                        templateFollowUpApptRadioButtonBy,
                        templatePendingEvacRadioButtonBy);

            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), wow, didn't find the text area.  Unlikely but it happens.");
                return false;
            }
            //this.bhNoteType = Utilities.processDropdown(defaultTemplateBhNoteTypeDropdownBy, this.bhNoteType, this.random, true);

            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = Utilities.waitForRefreshedClickability(defaultTemplateSaveButtonBy, 3, "FacilityTreatmentHistoryNote.(), save button");
                if (Arguments.pauseSave > 0) {
                    Utilities.sleep(Arguments.pauseSave * 1000, "");
                }
                start = Instant.now();
                popupSaveNoteElement.click(); //Does not cause AJAX.  Really?
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
                return false;
            }

            // Check for success message after trying to save this Behavioral Health Note.
            // If successful, the modal window goes away and we're back to the Behavioral Health Assessments page, and there should
            // be a green message saying "Note saved successfully!".  But if the modal window failed, then there will be a message
            // there.  So, unless we get back to that page, there was an error and it wasn't saved, and we might as well just return false;

            // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
            logger.finest("Waiting for staleness of popup."); // wow, next line throws a mean exception
            try {
                (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
                logger.finest("FacilityTreatmentHistoryNote.process(), Done waiting");
            } catch (Exception e) {
                logger.fine("FacilityTreatmentHistoryNote.process(), Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
                // continue, I guess.
            }


            try {
                //Utilities.sleep(3555, "FacilityTreatmentHistoryNote"); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
                // next line new 10/19/18  refreshed
                WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "FacilityTreatmentHistoryNote.(), success message area"); // not sure
                String someTextMaybe = someElement.getText();
                if (someTextMaybe.contains("successfully")) {
                    logger.fine("FacilityTreatmentHistoryNote.process(), saved note successfully.");
                } else if (someTextMaybe.contains("No records found for patient")) {
                    if (!Arguments.quiet)
                        System.out.println("***Could not save Facility Treatment History Note.  Message: " + someTextMaybe);
                    return false;
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save Facility Treatment History note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));
                return false;
            }
        }
        if (!Arguments.quiet) {
            System.out.println("      Saved Facility Treatment History Note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        //timerLogger.info("Facility Treatment History Note save for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        timerLogger.info("Facility Treatment History Note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "FacilityTreatmentHistoryNote, requested sleep for page.");
        }
        return true;
    }
}

