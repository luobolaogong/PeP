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


    private static By BH_POPUP_SAVE_NOTE = By.xpath("//input[@value='Save Note']"); // huh?  Does this work?  You can do a button this way?

    //private static By createNoteLinkBy = By.xpath("//*[@id=\"bhNotesContainer\"]/div[3]/a"); // wrong, of course
    private static By createNoteLinkBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]/div[2]/a"); // correct but dangerous
    //private static By notesTextAreaBy = By.id("defaultNoteText"); // where is this?
    //private static By messageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]");

    // Behavioral Health Note plays a game where the main section is made up of two parts where only one
    // is visible at a time, and they swap back and forth depending on whether you click on "Use Note Template"
    // or "Use Default Template".  These parts each include a "Save Button", and they are different buttons!
    // This also includes the "BH Note Type" dropdown element.  It probably also includes message areas.
    // You have to use the right version of the element for the template you select (default is default template).
    //

    // Default Template:
    private static By defaultTemplateUseTemplateLinkBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/span[2]/a");
    private static By defaultTemplateBhNoteTypeDropdownBy = By.id("defaultNoteTypeId");
    private static By defaultTemplateSaveButtonBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/div/button");
    private static By defaultTemplateNoteAreaBy = By.id("defaultNoteText");

    // Note Template:
    private static By noteTemplateUseTemplateLinkBy = By.xpath("//*[@id=\"noteTemplateContainer\"]/span[2]/a"); // don't really need it, because default is other template, and we don't have to click to go back.
    private static By noteTemplateBhNoteTypeDropdownBy = By.id("templateNoteTypeId");
    private static By noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//*[@id=\"noteTemplateContainer\"]/div/button");

    private static By defaultPendingRtdRadioButtonBy = By.id("defaultPENDING RTD");
    private static By defaultPendingTransferRadioButtonBy = By.id("defaultPENDING TRANSFER");
    private static By defaultFollowUpApptRadioButtonBy = By.id("defaultFOLLOW UP APPT");
    private static By defaultPendingEvacRadioButtonBy = By.id("defaultPENDING EVAC");

    private static By templatePendingRtdRadioButtonBy = By.id("templatePENDING RTD");
    private static By templatePendingTransferRadioButtonBy = By.id("templatePENDING TRANSFER");
    private static By templateFollowUpApptRadioButtonBy = By.id("templateFOLLOW UP APPT");
    private static By templatePendingEvacRadioButtonBy = By.id("templatePENDING EVAC");

    // following is prob wrong
    //By defaultPendingRtdRadioLabelBy = By.id("PENDING RTD");
    private static By defaultPendingRtdRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[1]");
    //By defaultPendingTransferRadioLabelBy = By.id("PENDING TRANSFER");
    private static By defaultPendingTransferRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[2]");
    //By defaultFollowUpApptRadioLabelBy = By.id("FOLLOW UP APPT");
    private static By defaultFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"template-select-form\"]/label[3]");
    //By defaultPendingEvacRadioLabelBy = By.id("PENDING EVAC");
    private static By defaultPendingEvacRadioLabelBy = By.id("//*[@id=\"template-select-form\"]/label[4]");

    //By templatePendingRtdRadioLabelBy = By.id("PENDING RTD");
    private static By templatePendingRtdRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[1]");
    //By templatePendingTransferRadioLabelBy = By.id("PENDING TRANSFER");
    private static By templatePendingTransferRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[2]");
    //By templateFollowUpApptRadioLabelBy = By.id("FOLLOW UP APPT");
    private static By templateFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[3]");
    //By templatePendingEvacRadioLabelBy = By.id("PENDING EVAC");
    private static By templatePendingEvacRadioLabelBy = By.xpath("//*[@id=\"default-select-form\"]/label[4]");




    //private static By bhPopupSaveNoteBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/div/button");
    //private static By bhPopupSaveNoteBy = By.xpath("//*[@id=\"noteTemplateContainer\"]/div/button");
    private static By bhPopupSaveNoteBy = By.xpath("//button[text()='Save Note']");

    //private static By bhNotesTypeDropdownBy = By.id("defaultNoteTypeId"); // verified
    private static By bhNotesTypeDropdownForTemplateBy = By.id("templateNoteTypeId");


    //private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[3]");
    //private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[4]"); // changed 10/6/18
    //private static By bhaBhnSuccessMessageAreaBy = By.xpath("//div[@id='bhNotesContainer']/preceding-sibling::div[1]"); // changed 10/6/18
    //private static By bhaBhnSuccessMessageAreaBy = By.xpath("//div[@id='patient-demographics-tab']/preceding-sibling::div[1]"); // changed 10/6/18
    private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]"); // would like to improve this
    //private static By useNoteTemplateLinkBy = By.xpath("//*[@id=\"bhNotesContainer\"]/div[3]/a");




    private static By serviceBy = By.id("service");
    private static By attendingStaffBy = By.id("attendingStaff");
    private static By workingDiagnosesBy = By.id("workingDiagnosis");
    private static By careRenderedSinceLastUpdateBy = By.id("careRendered");
    private static By adlUpdateBy = By.id("adlUpdated");
    private static By prognosisBy = By.id("prognosis");
    //private static By estimatedDischargeDateBy = By.id("estimatedDCString");
    private static By estimatedDischargeDateBy = By.id("estimatedDate");
    private static By dateBy = By.id("date");
    private static By dispositionBy = By.id("disposition");
    private static By needsAndRequirementsBy = By.id("needsAndRequirements");

    private static By availableAtVaBy = By.xpath("//*[@id=\"createNoteForm:j_id808\"]/table/tbody/tr/td[1]/input");


    public FacilityTreatmentHistoryNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
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
            //createNoteLinkBy = By.id("bhAssessmentForm:j_id451");
            //notesTextAreaBy = By.id("createNoteForm:noteTextDecorator:noteTextInput");
            createNoteLinkBy = By.id("j_id839:j_id845"); // will probably work most of the time, but dangerous
            // This needs to be cleaned up.
            //bhNotesTypeDropdownBy =  BH_NOTES_TYPE_DROPDOWN;
            defaultTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");
            noteTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");


//bhPopupSaveNoteBy = BH_POPUP_SAVE_NOTE;

            //adlUpdateBy = By.id("createNoteForm:j_id762:adlUpdateTextInput");
            //attendingStaffBy = By.id("createNoteForm:j_id738:attendingStaffTextInput");
            bhPopupSaveNoteBy = By.id("createNoteForm:submitNote");
            //careRenderedSinceLastUpdateBy = By.id("createNoteForm:j_id754:careRenderedTextInput");
            //dateBy = By.id("createNoteForm:j_id790:j_id800InputDate");
            defaultTemplateNoteAreaBy = By.id("createNoteForm:noteTextDecorator:noteTextInput");
            defaultTemplateSaveButtonBy = By.id("createNoteForm:submitNote");
            defaultTemplateUseTemplateLinkBy = By.id("createNoteForm:j_id700");
            //dispositionBy = By.id("createNoteForm:j_id802:dispositionTextInput");
            //estimatedDischargeDateBy = By.id("createNoteForm:j_id779:j_id789InputDate");
            //needsAndRequirementsBy = By.id("createNoteForm:j_id810:needsTextInput");
            //prognosisBy = By.id("createNoteForm:j_id770:prognosisTextInput");
            //serviceBy = By.id("createNoteForm:j_id730:serviceTextInput");
            //workingDiagnosesBy = By.id("createNoteForm:j_id746:workingDiagnosisTextInput");


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



           defaultPendingRtdRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[1]/label");
           defaultPendingTransferRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[2]/label");
           defaultFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[3]/label");
           defaultPendingEvacRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[4]/label");

           templatePendingRtdRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[1]/label");
           templatePendingTransferRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[2]/label");
           templateFollowUpApptRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[3]/label");
           templatePendingEvacRadioLabelBy = By.xpath("//*[@id=\"createNoteForm:j_id809:careStatusSelect\"]/tbody/tr/td[4]/label");

            //bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"bhAssessmentForm:j_id435\"]/table/tbody/tr/td/span");

            bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"j_id830\"]/table/tbody/tr/td/span"); // would like to improve this

        }
    }

    // This method seems a bit off.  Check logic, and compare against similar.
    //public boolean process(Patient patient, BehavioralHealthAssessment behavioralHealthAssessment) {
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Facility Treatment History Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        try { // get to next line too quickly?
            WebElement createNoteLinkElement = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.elementToBeClickable(createNoteLinkBy)); // was 5, then 10
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

        if (!useNotesTemplate) {
            useDefaultTemplate = true;
        }
        Instant start = null;

        if (useNotesTemplate) {
            // open up the note template to get to the fields there
            try {
                WebElement useNoteTemplateLink = (new WebDriverWait(Driver.driver, 6)).until(ExpectedConditions.elementToBeClickable(defaultTemplateUseTemplateLinkBy));
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
                // fill in the fields.
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
                this.careStatus = Utilities.processRadiosByLabel(this.careStatus, this.random, false, defaultPendingRtdRadioLabelBy, defaultPendingTransferRadioLabelBy, defaultFollowUpApptRadioLabelBy, defaultPendingEvacRadioLabelBy);


                // For Seam code this next checkbox exists.  Looks like was removed in Spring code:
                if (codeBranch.equalsIgnoreCase("Seam")) {
                    try {
                        WebElement availableCheckBox = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(availableAtVaBy));
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
                popupSaveNoteElement = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.elementToBeClickable(noteTemplateBhPopupSaveNoteForTemplateBy));
                start = Instant.now();
                popupSaveNoteElement.click(); //Does not cause AJAX.  Really?
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
                return false;
            }

            // Check for success message after trying to save this Behavioral Health Note.
            // If successful, the modal window goes away and we're back to the Behavioral Health Assessments page, and there should
            // be a green message saying "Note saved successfully!".  But if the modal window failed, then there will be a message
            // there.  So, unless we get back to that page, there was an error and it wasn't saved, and we might as well just return false;

            // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
            logger.finest("FacilityTreatmentHistoryNote.process(), Waiting for staleness of popup."); // wow, next line throws a mean exception
            try {
                (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
                logger.finest("FacilityTreatmentHistoryNote.process(), Done waiting");
            }
            catch (Exception e) {
                logger.fine("FacilityTreatmentHistoryNote.process(), Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
                // continue, I guess.
            }


            try {
                //Utilities.sleep(3555); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
                // next line new 10/19/18  refreshed
                WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy))); // not sure
                String someTextMaybe = someElement.getText();
                if (someTextMaybe.contains("successfully")) {
                    logger.fine("FacilityTreatmentHistoryNote.process(), FacilityTreatmentHistoryNote.process(), saved note successfully.");
                }
                else if (someTextMaybe.contains("No records found for patient")) {
                    if (!Arguments.quiet) System.out.println("***Could not save Facility Treatment History Note.  Message: " + someTextMaybe);
                    return false;
                }
                else {
                    if (!Arguments.quiet) System.err.println("      ***Failed to save behavioral health note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " + someTextMaybe);
                    return false;
                }
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            if (!Arguments.quiet) {
                System.out.println("        Saved Facility Treatment History Note for patient " +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
            }
            timerLogger.info("Facility Treatment History Note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            if (Arguments.pausePage > 0) {
                Utilities.sleep(Arguments.pausePage * 1000);
            }
            return true;

        }
        // Do we need to force this one, because the Note Template didn't succeed, or didn't have anything or something?
        if (useDefaultTemplate) { // Should be one or the other, because once we push the Save Note button, that's it.
            try {
                // get the sole "note" field/element to fill in
                (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(defaultTemplateNoteAreaBy));
                this.note = Utilities.processText(defaultTemplateNoteAreaBy, this.note, Utilities.TextFieldType.BH_NOTE, this.random, true);
                this.careStatus = Utilities.processRadiosByLabel(this.careStatus, this.random, false, templatePendingRtdRadioLabelBy, templatePendingTransferRadioLabelBy, templateFollowUpApptRadioLabelBy, templatePendingEvacRadioLabelBy);

            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), wow, didn't find the text area.  Unlikely but it happens.");
                return false;
            }
            //this.bhNoteType = Utilities.processDropdown(defaultTemplateBhNoteTypeDropdownBy, this.bhNoteType, this.random, true);

            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.elementToBeClickable(defaultTemplateSaveButtonBy));
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
                //Utilities.sleep(3555); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
                // next line new 10/19/18  refreshed
                WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy))); // not sure
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
            System.out.println("        Saved Facility Treatment History Note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("Facility Treatment History Note save for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }
}

