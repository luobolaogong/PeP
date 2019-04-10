package pep.patient.treatment.behavioralhealthassessment;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;


// THIS ONE IS UNDER BehavioralHealthAssessment and in that package.  There are differences with tbiassessment/TbiAssessmentNote

/**
 * This class holds and processes Traumatic Brain Injury information.  It's a note that is part of TbiAssessment.
 */
public class TbiAssessmentNote {
    private static Logger logger = Logger.getLogger(TbiAssessmentNote.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String assessmentType;
    public String assessmentDate;
    public String noteTitle;
    public String maceTotalScore;
    public String baseline;
    public String referral;
    public String referralLocation;
    public String comments;

    private static By bhTbiAssessmentNotePopupBy = By.id("tbi-popup");
    private static By assessmentTypeDropdownBy = By.id("tbiType");
    private static By noteTitleTextFieldBy = By.id("tbiNoteTitle");
    private static By saveAssessmentButtonBy = By.xpath("//*[@id=\"tbiFormContainer\"]/div/button");
    private static By bhCreateTbiAssessmentNoteLinkBy = By.xpath("//*[@id=\"tbiNotesContainer\"]/div[3]/a");
    private static By assessmentDateTextFieldBy = By.id("tbiNoteDateString");
    private static By tbiMaceTotalScoreFieldBy = By.id("tbiMaceScore");
    private static By cnBaselineYesRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[1]");
    private static By cnBaselineNoRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[2]");
    private static By cnBaselineUnknownRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[3]");
    private static By tbiReferralYesRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[1]");
    private static By tbiReferralNoRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[2]");
    private static By tbiReferralLocationFieldBy = By.id("referralLocation");
    private static By tbiCommentsTextArea = By.id("commentsArea");
    private static By messageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[1]");


    TbiAssessmentNote() {
        if (Arguments.template) {
            this.assessmentType = "";
            this.assessmentDate = "";
            this.noteTitle = "";
            this.maceTotalScore = "";
            this.baseline = "";
            this.referral = "";
            this.referralLocation = "";
            this.comments = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            bhCreateTbiAssessmentNoteLinkBy = By.id("bhAssessmentForm:j_id518");
            bhTbiAssessmentNotePopupBy =  By.id("tbiModalFormCDiv");
            assessmentTypeDropdownBy = By.id("tbiNoteForm:assessmentTypeDecorate:assessmentTypeSelect");
            assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
            noteTitleTextFieldBy = By.id("tbiNoteForm:assessmentNoteDecorate:assessmentTitle");
            tbiMaceTotalScoreFieldBy = TBI_MACE_TOTAL_SCORE_FIELD;
            cnBaselineYesRadioLabelBy = CN_BASLINE_YES_RADIO_BUTTON;
            cnBaselineNoRadioLabelBy = CN_BASLINE_NO_RADIO_BUTTON;
            cnBaselineUnknownRadioLabelBy = CN_BASLINE_UNKNOWN_RADIO_BUTTON;
            tbiReferralYesRadioLabelBy = TBI_REFERRAL_YES_RADIO;
            tbiReferralNoRadioLabelBy = TBI_REFERRAL_NO_RADIO;
            tbiReferralLocationFieldBy = TBI_REFERRAL_LOCATION_FIELD;
            tbiCommentsTextArea = TBI_COMMENTS_TEXTAREA;
            saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment");
            messageAreaBy = By.xpath("//*[@id=\"bhAssessmentForm:j_id435\"]/table/tbody/tr/td/span");
        }
    }

    private static final By TBI_MACE_TOTAL_SCORE_FIELD = By.xpath("//label[.='MACE Total Score:']/../following-sibling::td/input");
    private static final By TBI_REFERRAL_YES_RADIO = By.xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[1]/label");
    private static final By TBI_REFERRAL_NO_RADIO = By.xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[2]/label");
    private static final By TBI_REFERRAL_LOCATION_FIELD = By.xpath("//label[.='Referral Location:']/../following-sibling::td/input");
    private static final By TBI_COMMENTS_TEXTAREA = By.xpath("//textarea[@id='tbiNoteForm:assessmentComments']");
    private static final By CN_BASLINE_YES_RADIO_BUTTON = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[1]/label");
    private static final By CN_BASLINE_NO_RADIO_BUTTON = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[2]/label");
    private static final By CN_BASLINE_UNKNOWN_RADIO_BUTTON = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[3]/label");

    /**
     * Process the TBI Assessment Note that is part of Behavioral Health Assessment.
     * @param patient The patient for this TBI Assessment Note
     * @return Success or Failure in saving the note
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing BH TBI Assessment Note at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Get and click on the link for the modal popup window to create the note, and then check it popped up.
        //
        try {
            WebElement bhCreateTbiAssessmentNoteLink = Utilities.waitForRefreshedClickability(bhCreateTbiAssessmentNoteLinkBy, 10, "behavioralhealthassessment/TbiAssessmentNote.(), create note link");
            bhCreateTbiAssessmentNoteLink.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for bhCreateTbiAssessmentNoteLink to show up.");
            return false;
        }
        catch (Exception e) {
            logger.fine("Exception either trying to get Webelement, or clicking on it: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        WebElement bhPopupElement;
        try {
            bhPopupElement = Utilities.waitForPresence(bhTbiAssessmentNotePopupBy, 10, "behavioralhealthassessment/TbiAssessmentNote.(), assessment note popup");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }
        //
        // Start processing the popup modal window.
        // When a selection is made from the Assessment Type dropdown, an AJAX.Submit call is made,
        // which causes the server to return a "container"/table element that gets stuffed back
        // into the DOM, and that table element contains the date fields among other things.
        // So, if you do the date first and the selection later, the date gets wiped out.
        // The same thing happens if you select an element from the dropdown and then very quickly
        // put a date value into the Assessment Date field.
        //
        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.randomizeSection, true);
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        Utilities.sleep(508, "behavioralhealthassessment/TbiAssessmentNote"); // haven't been able to get around this.  Not absolutely sure this was necessary, but seemed to be in the Tbi version

        if (Arguments.date != null && (this.assessmentDate == null || this.assessmentDate.isEmpty())) {
            this.assessmentDate = Arguments.date + " " + Utilities.getCurrentHourMinute(); // this shouldn't take too long
        }
        try {
            Utilities.waitForRefreshedVisibility(assessmentDateTextFieldBy,  10, "behavioralhealthassessment/TbiAssessmentNote.(), assessment Date");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for assessment date text field.");
            return false;
        }
        this.assessmentDate = Utilities.processDateTime(assessmentDateTextFieldBy, this.assessmentDate, this.randomizeSection, true);
        if (this.assessmentDate == null || this.assessmentDate.isEmpty()) {
            logger.fine("Assessment Date came back null or empty.  Why?");
            return false;
        }
        try {
            Utilities.waitForVisibility(noteTitleTextFieldBy, 10, "behavioralhealthassessment/TbiAssessmentNote.(), note title text field");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for note title text field.");
            return false;
        }
        // If want a random title, generate one patient's name and assessment type, for now.  Don't know what a good title would be.  No latin.
        if (this.noteTitle == null || this.noteTitle.isEmpty() || this.noteTitle.equalsIgnoreCase("random")) {
            this.noteTitle = patient.patientSearch.lastName + " " + this.assessmentType;
        }
        this.noteTitle = Utilities.processText(noteTitleTextFieldBy, this.noteTitle, Utilities.TextFieldType.TITLE, this.randomizeSection, true);
        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.randomizeSection, true);
        }
        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            this.baseline = Utilities.processRadiosByLabel(this.baseline, this.randomizeSection, true, cnBaselineYesRadioLabelBy, cnBaselineNoRadioLabelBy, cnBaselineUnknownRadioLabelBy);
        }
        this.referral = Utilities.processRadiosByLabel(this.referral, this.randomizeSection, true, tbiReferralYesRadioLabelBy, tbiReferralNoRadioLabelBy); // something wrong about these XPATHS or radios
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(tbiReferralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.randomizeSection, true);
        }
        this.comments = Utilities.processText(tbiCommentsTextArea, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.randomizeSection, true);
        //
        // Save the field values
        //
        Instant start;
        WebElement saveAssessmentButton;
        try {
            saveAssessmentButton = Utilities.waitForRefreshedClickability(saveAssessmentButtonBy, 10, "behavioralhealthassessment/TbiAssessmentNote.(), save button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "");
            }
            start = Instant.now();
            saveAssessmentButton.click(); // no ajax
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for saveAssessmentButton to be clickable.");
            return false;
        }
        catch (Exception e) {
            logger.fine("Some kinda exception for finding and clicking on save assessment button");
            return false;
        }
        logger.finest("Waiting for staleness of popup.");
        (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(bhPopupElement));
        logger.finest("Done waiting");
        //
        // If the save succeeded, the modal window goes away.  There may be a message on the Behavioral Health Assessments
        // page indicating success.  Failure is indicated by the modal window still being there, with some kind of message.
        // So, we can either wait to see if the modal window is still there and check the message, or we can check that
        // we got back to the Behavioral Health Assessments page.
        // !!!!!!!!!!!!!!!!!!!!!!This is different than the TBI thing from the TBI Assessments. !!!!!!!!!!!!!!!!!!!!!!!!!
        // Let's assume that the modal window went away and we're back to the Behavioral Health Assessments page, and let's
        // check for success.  Maybe not worth the effort.  Maybe better just to check that the BHA page is there, with
        // Patient Demographics section.
        try {
            WebElement someElement = Utilities.waitForVisibility(messageAreaBy, 5, "behavioralhealthassessment/TbiAssessmentNote.(), message area"); // was 10
            String someTextMaybe = someElement.getText();
            if (someTextMaybe.contains("successfully")) {
                logger.fine("TbiAssessmentNote.process(), saved note successfully.");
            }
            else {
                logger.severe("      ***Failed to save BH TBI Assessment Note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " + someTextMaybe);
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("TbiAssessmentNote.process(), Didn't find message after save attempt, probably because Seam tiers don't do it.  Continuing.  e: " + Utilities.getMessageFirstLine(e));
        }
        //
        // Must have gotten a "success" message, so report saved, and return true:
        //
        if (!Arguments.quiet) {
            System.out.println("        Saved Behavioral Health TBI Assessment Note at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.fine("Behavioral Health Note note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "behavioralhealthassessment/TbiAssessmentNote, requested sleep for page.");
        }
        return true;
    }
}
