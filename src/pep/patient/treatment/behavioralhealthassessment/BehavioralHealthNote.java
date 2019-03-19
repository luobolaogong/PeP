package pep.patient.treatment.behavioralhealthassessment;

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
 * This class process a behavioral health note.
 */
class BehavioralHealthNote {
    private static Logger logger = Logger.getLogger(BehavioralHealthNote.class.getName());
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
    public String bhNoteType;

    private static By createNoteLinkBy = By.linkText("Create Note");
    private static By defaultTemplateUseTemplateLinkBy = By.xpath("//a[text()='Use Note Template']");
    private static By defaultTemplateBhNoteTypeDropdownBy = By.id("defaultNoteTypeId");
    private static By defaultTemplateSaveButtonBy = By.xpath("//div[@id=\'defaultTemplateContainer\']/descendant::button[text()=\'Save Note\']");
    private static By defaultTemplateNoteAreaBy = By.id("defaultNoteText");
    private static By noteTemplateBhNoteTypeDropdownBy = By.id("templateNoteTypeId");
    private static By noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//div[@id='noteTemplateContainer']/descendant::button[text()='Save Note']");
    private static By bhaBhnSuccessMessageAreaBy = By.xpath("//div[@id='bhNotesContainer']/preceding-sibling::div[1]"); // changed 10/6/18
    private static By problemMessageAreaBy = By.id("note-msg");
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

    public BehavioralHealthNote() {
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
            this.bhNoteType = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            createNoteLinkBy = By.id("bhAssessmentForm:j_id451");
            defaultTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");
            noteTemplateBhNoteTypeDropdownBy = By.xpath("//*[@id=\"createNoteForm:bhNoteTypeDecorator:validInput\"]/select");
            noteTemplateBhPopupSaveNoteForTemplateBy = By.xpath("//*[@id=\"createNoteForm:submitNote\"]");
            bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"bhAssessmentForm:j_id435\"]/table/tbody/tr/td/span");
            createNoteLinkBy = By.id("bhAssessmentForm:j_id451"); // verified on TEST, I suppose
            defaultTemplateUseTemplateLinkBy = By.id("createNoteForm:j_id720");
            serviceBy = By.id("createNoteForm:j_id730:serviceTextInput");
            attendingStaffBy = By.id("createNoteForm:j_id738:attendingStaffTextInput");
            workingDiagnosesBy = By.id("createNoteForm:j_id746:workingDiagnosisTextInput");
            careRenderedSinceLastUpdateBy = By.id("createNoteForm:j_id754:careRenderedTextInput");
            adlUpdateBy = By.id("createNoteForm:j_id762:adlUpdateTextInput");
            prognosisBy = By.id("createNoteForm:j_id770:prognosisTextInput");
            estimatedDischargeDateBy = By.id("createNoteForm:j_id779:j_id789InputDate");
            dateBy = By.id("createNoteForm:j_id790:j_id800InputDate");
            dispositionBy = By.id("createNoteForm:j_id802:dispositionTextInput");
            needsAndRequirementsBy = By.id("createNoteForm:j_id810:needsTextInput");
            defaultTemplateNoteAreaBy = By.id("createNoteForm:noteTextDecorator:noteTextInput");
            defaultTemplateSaveButtonBy = By.id("createNoteForm:submitNote");
        }
    }

    /**
     * Process the behavioral health note.
     *
     * Behavioral Health Note plays a game where the main section is made up of two parts where only one
     * is visible at a time, and they swap back and forth depending on whether you click on "Use Note Template"
     * or "Use Default Template".  These parts each include a "Save Button", and they are different buttons!
     * This also includes the "BH Note Type" dropdown element.  It probably also includes message areas.
     * You have to use the right version of the element for the template you select (default is default template).
     * @param patient The patient for this note
     * @return Success or failure at saving the note
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Behavioral Health Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        try { // get to next line too quickly?
            WebElement createNoteLinkElement = Utilities.waitForRefreshedClickability(createNoteLinkBy, 15, "BehavioralHealthNote.(), create note link"); // was 5, then 10
            createNoteLinkElement.click(); // ajax?
            boolean waitedForAjax = (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax());
            if (!waitedForAjax) {
                System.out.println("waitedForAjax is " + waitedForAjax);
            }
        }
        catch (TimeoutException e) {
            logger.severe("BehavioralHealthNote.process(), Timeout exception, couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false; // failed with timeout on gold: 1
        }
        catch (Exception e) {
            logger.severe("BehavioralHealthNote.process(), couldn't get link, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false; // failed with timeout on gold: 1
        }
// check logic
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
        // use one or the other, so
        if (!useNotesTemplate) {
            useDefaultTemplate = true;
        }

        Instant start = null;

        if (useNotesTemplate) {
            // open up the note template to get to the fields there
            try {
                WebElement useNoteTemplateLink = Utilities.waitForRefreshedClickability(defaultTemplateUseTemplateLinkBy, 6, "BehavioralHealthNote.(), use template link");
                useNoteTemplateLink.click();
            }
            catch (TimeoutException e) {
                logger.severe("BehavioralHealthNote.process(), Timeout exception, couldn't get Use Note Template link, or couldn't click on it.");
                return false;
            }
            catch (Exception e) {
                logger.fine("BehavioralHealthNote.process(), couldn't get Use Note Template link, or couldn't click on it.");
                return false;
            }

            try {

                // fill in the fields.
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
            }
            catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), couldn't find or fill in some element: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            this.bhNoteType = Utilities.processDropdown(noteTemplateBhNoteTypeDropdownBy, this.bhNoteType, this.randomizeSection, true);
            // What's this stuff?:
            //bhNotesTypeDropdownBy = bhNotesTypeDropdownForTemplateBy;
            //bhPopupSaveNoteBy = bhPopupSaveNoteForTemplateBy;

            if (this.shoot != null && this.shoot) {
                String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
                if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
            }


            // IF DO NOTE TEMPLATE DO IT HERE INSTEAD OF STUFF ABOVE
            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = Utilities.waitForRefreshedClickability(noteTemplateBhPopupSaveNoteForTemplateBy, 3, "BehavioralHealthNote.(), save note button");
                if (Arguments.pauseSave > 0) {
                    Utilities.sleep(Arguments.pauseSave * 1000, "");
                }
                start = Instant.now();
                popupSaveNoteElement.click(); //Does not cause AJAX.  Really?
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            }
            catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
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
                logger.finest("Done waiting");
            }
            catch (Exception e) {
                logger.fine("Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
                // continue, I guess.
            }


            try {
                //Utilities.sleep(3555, "BehavioralHealthNote"); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
                // next line new 10/19/18  refreshed
                //WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "classMethod"); // not sure
                WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy, 10, "BehavioralHealthNote.process()");
                String someTextMaybe = someElement.getText();
                if (someTextMaybe.contains("successfully")) {
                    logger.fine("BehavioralHealthNote.process(), saved note successfully.");
                }
                else if (someTextMaybe.contains("No records found for patient")) {
                    if (!Arguments.quiet) System.out.println("      ***Failed to save Behavioral Health Note.  Message: " + someTextMaybe);
                    return false;
                }
                else {
                    if (!Arguments.quiet) System.err.println("      ***Failed to save behavioral health note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " +
                            someTextMaybe);
                    return false;
                }
            }
            catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            if (!Arguments.quiet) {
                System.out.println("        Saved Behavioral Health Note for patient " +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
            }
            //timerLogger.fine("Behavioral Health Note note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            timerLogger.fine("Behavioral Health Note note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            if (Arguments.pausePage > 0) {
                Utilities.sleep(Arguments.pausePage * 1000, "BehavioralHealthNote, requested sleep for page.");
            }
            return true;

        }
        // Do we need to force this one, because the Note Template didn't succeed, or didn't have anything or something?
        if (useDefaultTemplate) { // Should be one or the other, because once we push the Save Note button, that's it.
            try {
                // get the sole "note" field/element to fill in
                Utilities.waitForVisibility(defaultTemplateNoteAreaBy, 1, "BehavioralHealthNote.(), note area");
                this.note = Utilities.processText(defaultTemplateNoteAreaBy, this.note, Utilities.TextFieldType.BH_NOTE, this.randomizeSection, true);
            } catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), wow, didn't find the text area.  Unlikely but it happens.");
                return false;
            }
            this.bhNoteType = Utilities.processDropdown(defaultTemplateBhNoteTypeDropdownBy, this.bhNoteType, this.randomizeSection, true);

            WebElement popupSaveNoteElement;
            try {
                popupSaveNoteElement = Utilities.waitForRefreshedClickability(defaultTemplateSaveButtonBy, 3, "BehavioralHealthNote.(), save button");
                if (Arguments.pauseSave > 0) {
                    Utilities.sleep(Arguments.pauseSave * 1000, "");
                }
                start = Instant.now();
                popupSaveNoteElement.click(); //Does not cause AJAX.  Really?
                (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            } catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), couldn't get Save Note button, or couldn't click on it: " + Utilities.getMessageFirstLine(e));
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
                logger.finest("Done waiting");
            } catch (Exception e) {
                logger.fine("Couldn't wait for staleness of popup save note element: " + popupSaveNoteElement.toString());
                // continue, I guess.
            }


            try {
                //Utilities.sleep(3555, "BehavioralHealthNote"); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
                //WebElement someElement = Utilities.waitForVisibility(bhaBhnSuccessMessageAreaBy, 10, "classMethod");
                // next line new 10/19/18  refreshed
                WebElement someElement = Utilities.waitForRefreshedVisibility(bhaBhnSuccessMessageAreaBy,  10, "BehavioralHealthNote.(), success message area"); // not sure
                String someTextMaybe = someElement.getText();
                if (someTextMaybe.contains("successfully")) {
                    logger.fine("BehavioralHealthNote.process(), saved note successfully.");
                } else if (someTextMaybe.contains("No records found for patient")) {
                    if (!Arguments.quiet)
                        System.out.println("***Could not save Behavioral Health Note.  Message: " + someTextMaybe);
                    return false;
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save behavioral health note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
            } catch (Exception e) {
                logger.severe("BehavioralHealthNote.process(), Didn't find message after save attempt: " + Utilities.getMessageFirstLine(e));
                WebElement someElement = Utilities.waitForRefreshedVisibility(problemMessageAreaBy,  10, "BehavioralHealthNote.(), problem message area");
                String someBadTextMaybe = someElement.getText();
                System.out.println("some bad text maybe: " + someBadTextMaybe);
                return false;
            }
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved Behavioral Health Note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        //timerLogger.fine("Behavioral Health Note note save for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        timerLogger.fine("Behavioral Health Note note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "behavioralHealthNote, requested sleep for page.");
        }
        return true;
    }
}
