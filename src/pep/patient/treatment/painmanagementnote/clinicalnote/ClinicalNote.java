package pep.patient.treatment.painmanagementnote.clinicalnote;

import org.openqa.selenium.*;
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

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

/**
 * This class is one of the parts of a pain management note, and it is processed by this class.
 */
public class ClinicalNote {
    private static Logger logger = Logger.getLogger(ClinicalNote.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String clinicalNoteDateTime = "";
    public String adjunctMedications = "";
    public String currentVerbalAnalogueScore = "";
    public String verbalAnalogueScore = "";
    public String satisfiedWithPainManagement = "";
    public String commentsPainManagement = "";
    public String painManagementPlan = "";
    public String commentsNotesComplications = "";

    private static By CLINICAL_NOTE_TAB = By.linkText("Clinical Note");
    private static By CN_CURRENT_VERBAL_ANALOGUE_SCORE_DROPDOWN = By.id("painNoteForm:currentVasDecorate:currentVas");
    private static By CN_VERBAL_ANALOGUE_SCORE_DROPDOWN = By.id("painNoteForm:vasDecorate:vas");
    private static By CN_PAIN_MANAGEMENT_PLAN_TEXTAREA = By.xpath("//textarea[@id='painNoteForm:painPlanDecorate:painPlan']");
    private static By CN_COMMENTS_TEXTAREA = By.xpath("//textarea[@id='painNoteForm:discontinueCommentsDecorate:comments']");
    private static By CN_DISCONTINUE_COMMENTS_TEXTAREA = By.xpath("//*[@id='painNoteForm:satisfiedCommentsDecorate:satisfiedComments']");
    private static By clinicalNoteTabBy = By.linkText("Clinical Note");
    private static By clinicalSectionBy = By.id("clinicalNoteTabContainer");
    private static By clinicalNoteDateTimeBy = By.id("clinicalPainNoteFormplacementDate");
    private static By cnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::select[@id='currentVas']");
    private static By cnVerbalAnalogueScoreDropdownBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::select[@id='vas']");
    private static By cnSatisfiedWithPainManagementYesLabelBy = By.xpath("//label[@for='satisfiedInd1']");
    private static By cnSatisfiedWithPainManagementNoLabelBy = By.xpath("//label[@for='satisfiedInd2']");
    private static By cnDiscontinueCommentsTextAreaBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='satisfiedComments']");
    private static By cnPainManagementPlanTextAreaBy  = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='painPlan']");
    private static By cnCommentsTextAreaBy            = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='comments']");
    private static By createNoteThingBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::button[text()='Create Note']");
    private static By messageAreaBy = By.id("pain-note-message");


    public ClinicalNote() {
        if (Arguments.template) {
            this.clinicalNoteDateTime = "";
            this.adjunctMedications = "";
            this.currentVerbalAnalogueScore = "";
            this.verbalAnalogueScore = "";
            this.satisfiedWithPainManagement = "";
            this.commentsPainManagement = "";
            this.painManagementPlan = "";
            this.commentsNotesComplications = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            clinicalNoteTabBy = CLINICAL_NOTE_TAB;
            clinicalSectionBy = By.xpath("//*[@id='painNoteForm:Clinical' and @style=';height:100%']");
            clinicalNoteDateTimeBy = By.id("painNoteForm:discontinueDateDecorate:placementDateInputDate");
            cnCurrentVerbalAnalogueScoreDropdownBy = CN_CURRENT_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            cnVerbalAnalogueScoreDropdownBy = CN_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            cnSatisfiedWithPainManagementYesLabelBy = By.xpath("//*[@id='painNoteForm:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[1]/label");
            cnSatisfiedWithPainManagementNoLabelBy = By.xpath("//*[@id='painNoteForm:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[2]/label");
            cnDiscontinueCommentsTextAreaBy = CN_DISCONTINUE_COMMENTS_TEXTAREA;
            cnPainManagementPlanTextAreaBy = CN_PAIN_MANAGEMENT_PLAN_TEXTAREA;
            cnCommentsTextAreaBy = CN_COMMENTS_TEXTAREA;
            createNoteThingBy = By.id("painNoteForm:createNote");
            messageAreaBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span");
        }
    }

    /**
     * Process a clinical note for the patient
     * @param patient The patient for this note
     * @return Success or failure in saving the record
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Clinical Note at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Make sure we're at the right place before filling fields with values.
        //
        try {
            WebElement clinicalNoteTabElement = Utilities.waitForRefreshedClickability(clinicalNoteTabBy, 30, "ClinicalNote.process() clinical note tab");
            clinicalNoteTabElement.click();
            // need next line?  Removing 5/2/19
//            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // was 4 // wow, not removing 4/18/19? Fails when stopped: 0 n
        }
        catch (StaleElementReferenceException e) {
            logger.severe("clinicalNote.process(), couldn't get Clinical Note tab, and/or couldn't click it: Stale element reference: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        catch (Exception e) {
            logger.severe("clinicalNote.process(), couldn't get tab, and/or couldn't click on it.  Patient not found?: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails: 3
        }

        try {
            Utilities.waitForPresence(clinicalSectionBy, 1, "ClinicalNote.process()");
        }
        catch (Exception e) {
            logger.severe("ClinicalNote.process(), Could not wait for visibility of clinical section.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        if (Arguments.date != null && (this.clinicalNoteDateTime == null || this.clinicalNoteDateTime.isEmpty())) {
            this.clinicalNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        try {
            Utilities.waitForVisibility(clinicalNoteDateTimeBy, 4, "ClinicalNote.process()"); // fails:1 failsIfStop:0
        }
        catch (Exception e) {
            logger.severe("ClinicalNote.process(), What, couldn't get clinical note date/time?");
            ScreenShot.shoot("SevereError");
        }
        Utilities.sleep(555, "ClinicalNote");
        //
        // Fill fields with values.
        //
        this.clinicalNoteDateTime = Utilities.processDateTime(clinicalNoteDateTimeBy, this.clinicalNoteDateTime, this.randomizeSection, true);
        this.currentVerbalAnalogueScore = Utilities.processDropdown(cnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.randomizeSection, true);
        this.verbalAnalogueScore = Utilities.processDropdown(cnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.randomizeSection, true);
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.randomizeSection, true,
                    cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(cnDiscontinueCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.randomizeSection, true);
            }
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.randomizeSection, true,
                    cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(cnDiscontinueCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.randomizeSection, true);
            }
        }
        logger.fine("ClinicalNote.process(), Here comes PainManagementNoteSection CN_PAIN_MANAGEMENT_PLAN_TEXTAREA");
        this.painManagementPlan = Utilities.processText(cnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.randomizeSection, true);
        this.commentsNotesComplications = Utilities.processText(cnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.randomizeSection, false);


        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }
        //
        // Save the Note
        //
        Instant start;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(createNoteThingBy, 30, "ClinicalNote.process() create note button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "ClinicalNote");
            }
            start = Instant.now();
            createNoteButton.click();
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // hmmm not removing 4/18/19?
        }
        catch (Exception e) { // got a "You Have Encountered a Problem" page.  Because of DB?
            logger.severe("ClinicalNote.process(), Could not get the create note button, or click on it."); ScreenShot.shoot("SevereError");
            return false;
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            try {
                WebElement result = Utilities.waitForVisibility(messageAreaBy, 10, "ClinicalNote.process()");
                String someTextMaybe = result.getText();
                if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                    logger.fine("Clinical Note successfully saved.");
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save Clinical Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
            }
            catch (TimeoutException e) {
                logger.severe("ClinicalNote.process(), Timed out waiting for message area to be visible.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false; // ???
            }
            catch (Exception e) {
                logger.severe("ClinicalNote.process(), Some kinda exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false; // ???
            }
        }
        else {
            // removing following 5/8/19, as a test because get error about unexpected whatever exception, but will keep the sleep
//            try {
//                logger.finest("ClinicalNote.process(), what are we waiting for?");
//                Utilities.sleep(555, "ClinicalNote.process(), after save operation this next wait doesn't wait?");
//                Utilities.waitForInvisibility(clinicalSectionBy, 10, "ClinicalNote.process()"); // maybe works? fails:1
//                logger.finest("ClinicalNote.process(), done waiting for something waiting for?");
//            }
//            catch (Exception e) {
//                logger.fine("Couldn't wait for clinical section to become invisible.");
//            }
            Utilities.sleep(1555, "ClinicalNote.process(), after save operation this next wait doesn't wait?");
            try { // perhaps next line fails for some reason, but actually did create a clinical note
                WebElement result = Utilities.waitForVisibility(messageAreaBy, 10, "ClinicalNote.process()");
                String someTextMaybe = result.getText();
                if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                    logger.fine("Clinical Note successfully saved.");
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save Clinical Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
            }
            catch (Exception e) {
                logger.fine("Couldn't wait for visibility of message area, probably.");
            }
        }

        if (!Arguments.quiet) {
            System.out.println("        Saved Clinical note at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("Clinical Note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ClinicalNote");
        }
        return true;
    }
}
