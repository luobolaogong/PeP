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

/**
 * This class processes the Facility Treatment History Note that is part of the Summary page.
 * This is similar to BehavioralHealthNote.
 */
public class FacilityTreatmentHistoryNote {
    private static Logger logger = Logger.getLogger(FacilityTreatmentHistoryNote.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String note;

    public String service;
    public String attendingStaff;
    public String workingDiagnoses;
    public String careRenderedSinceLastUpdate;
    public String adlUpdate;
    public String prognosis;
    public String estimatedDischargeDate;
    public String date;
    public String disposition;
    public String needsAndRequirements;
    private String careStatus;
    private Boolean availableAtVa;


    private static By createNoteLinkBy = By.linkText("Create Note");
    private static By defaultTemplateUseTemplateLinkBy = By.linkText("Use Note Template");
    private static By defaultTemplateSaveButtonBy = By.xpath("//div[@id=\"defaultTemplateContainer\"]/div/button[text()='Save Note']");
    private static By defaultTemplateNoteAreaBy = By.id("defaultNoteText");
    private static By noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//div[@id='noteTemplateContainer']/div/button[text()='Save Note']");
    private static By defaultPendingRtdRadioLabelBy = By.xpath("//label[@for='templatePENDING RTD']");
    private static By defaultPendingTransferRadioLabelBy = By.xpath("//label[@for='templatePENDING TRANSFER']");
    private static By defaultFollowUpApptRadioLabelBy = By.xpath("//label[@for='templateFOLLOW UP APPT']");
    private static By defaultPendingEvacRadioLabelBy = By.xpath("//label[@for='templatePENDING EVAC']");
    private static By templatePendingRtdRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[1]");
    private static By templatePendingTransferRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[2]");
    private static By templateFollowUpApptRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[3]");
    private static By templatePendingEvacRadioButtonBy = By.xpath("//*[@id=\"default-select-form\"]/label[4]");

    private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[2]/div[9]/div[2]");
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
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            createNoteLinkBy = By.id("j_id839:j_id845");
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
            bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"j_id830\"]/table/tbody/tr/td/span"); // would like to improve this
        }
    }

    //

    /**
     * Process this Facility Treatment History Note.
     * This Note is made up of two parts where only one is visible at a time, and they swap back and forth
     * depending on whether you click on "Use Note Template" or "Use Default Template".  These parts each
     * include a "Save Button", and they are different buttons!  This also includes the "BH Note Type"
     * dropdown element.  It probably also includes message areas. You have to use the right version of the
     * element for the template you select (default is default template).
     * @param patient The patient is the one for this facility treatment history note
     * @return Success or Failure at processing the this note.
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing Facility Treatment History Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Find and click the link for creating this Note
        // Forgot to copy down the section random value stuff here?
        //
        try {
            WebElement createNoteLinkElement = Utilities.waitForRefreshedClickability(createNoteLinkBy, 15, "FacilityTreatmentHistoryNote.(), create note link");
            createNoteLinkElement.click(); // ajax?
            (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.severe("FacilityTreatmentHistoryNote.process(), Timeout exception, couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.fine("FacilityTreatmentHistoryNote.process(), couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false; // failed with timeout on gold: 1
        }

        // At this point we should have a modal window popped up, and it says "Facility Treatment History Note" in the title(?)
        // It contains a text area, a dropdown, a Save Note button, and above all that there's a link "Use Note Template".
        // You can use that link, or not.  If you do, then a new window pops up with the template to fill in.
        // The deciding factor in whether to use the note template is if the fields were defined in the input file.
        // If the input field "note" value is specified, then use the Default Template.
        boolean useDefaultTemplate = (this.note != null && !this.note.isEmpty());
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
        // Two ways to do this popup, either with default template, or note template!
        // Note template is the one with the bunch of text fields.  Default is one text field.
        // Both have radio buttons and a save button at the bottom.
        if (useNotesTemplate) {
            try {
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
                this.service = Utilities.processText(serviceBy, this.service, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.attendingStaff = Utilities.processText(attendingStaffBy, this.attendingStaff, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.workingDiagnoses = Utilities.processText(workingDiagnosesBy, this.workingDiagnoses, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.careRenderedSinceLastUpdate = Utilities.processText(careRenderedSinceLastUpdateBy, this.careRenderedSinceLastUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.adlUpdate = Utilities.processText(adlUpdateBy, this.adlUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.prognosis = Utilities.processText(prognosisBy, this.prognosis, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.estimatedDischargeDate = Utilities.processText(estimatedDischargeDateBy, this.estimatedDischargeDate, Utilities.TextFieldType.DATE, this.randomizeSection, false);
                this.date = Utilities.processText(dateBy, this.date, Utilities.TextFieldType.DATE, this.randomizeSection, false);
                this.disposition = Utilities.processText(dispositionBy, this.disposition, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.needsAndRequirements = Utilities.processText(needsAndRequirementsBy, this.needsAndRequirements, Utilities.TextFieldType.SHORT_PARAGRAPH, this.randomizeSection, false);
                this.careStatus = Utilities.processRadiosByLabel(this.careStatus, this.randomizeSection, false,
                        defaultPendingRtdRadioLabelBy,
                        defaultPendingTransferRadioLabelBy,
                        defaultFollowUpApptRadioLabelBy,
                        defaultPendingEvacRadioLabelBy);
                if (codeBranch.equalsIgnoreCase("Seam")) {
                    try {
                        Utilities.waitForVisibility(availableAtVaBy, 3, "FacilityTreatmentHistoryNote.(), available checkbox");
                        this.availableAtVa = Utilities.processBoolean(availableAtVaBy, this.availableAtVa, this.randomizeSection, false);
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
            //
            // Save the note template version
            //
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
            try {
                (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
                logger.finest("FacilityTreatmentHistoryNote.process(), Done waiting");
            }
            catch (Exception e) {
                logger.fine("FacilityTreatmentHistoryNote.process(), Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
            }

            try {
                Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "FacilityTreatmentHistoryNote.(), success message area"); // not sure
            }
            catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            if (!Arguments.quiet) {
                System.out.println("      Saved Facility Treatment History Note for patient " +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
            }
            timerLogger.fine("Facility Treatment History Note took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            if (Arguments.pausePage > 0) {
                Utilities.sleep(Arguments.pausePage * 1000, "FacilityTreatmentHistoryNote, requested sleep for page.");
            }
            return true;

        }

        if (useDefaultTemplate) {
            try {
                Utilities.waitForVisibility(defaultTemplateNoteAreaBy, 1, "FacilityTreatmentHistoryNote.(), note area");
                this.note = Utilities.processText(defaultTemplateNoteAreaBy, this.note, Utilities.TextFieldType.BH_NOTE, this.randomizeSection, true);
                this.careStatus = Utilities.processRadiosByButton(this.careStatus, this.randomizeSection, false,
                        templatePendingRtdRadioButtonBy,
                        templatePendingTransferRadioButtonBy,
                        templateFollowUpApptRadioButtonBy,
                        templatePendingEvacRadioButtonBy);

            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), wow, didn't find the text area.  Unlikely but it happens.");
                return false;
            }
            //
            // Save the default template version
            //
            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = Utilities.waitForRefreshedClickability(defaultTemplateSaveButtonBy, 3, "FacilityTreatmentHistoryNote.(), save button");
                if (Arguments.pauseSave > 0) {
                    Utilities.sleep(Arguments.pauseSave * 1000, "");
                }
                start = Instant.now();
                popupSaveNoteElement.click();
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            } catch (Exception e) {
                logger.severe("FacilityTreatmentHistoryNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            //
            // Check for success message after trying to save this Behavioral Health Note.
            // If successful, the modal window goes away and we're back to the Behavioral Health Assessments page, and there should
            // be a green message saying "Note saved successfully!".  But if the modal window failed, then there will be a message
            // there.  So, unless we get back to that page, there was an error and it wasn't saved, and we just return false;
            //
            logger.finest("Waiting for staleness of popup.");
            try {
                (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
                logger.finest("FacilityTreatmentHistoryNote.process(), Done waiting");
            } catch (Exception e) {
                logger.fine("FacilityTreatmentHistoryNote.process(), Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
            }
            try {
                WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "FacilityTreatmentHistoryNote.(), success message area");
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
        timerLogger.fine("Facility Treatment History Note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "FacilityTreatmentHistoryNote, requested sleep for page.");
        }
        return true;
    }
}

