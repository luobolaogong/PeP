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

    private static By TBI_MACE_TOTAL_SCORE_FIELD = By.xpath("//label[.='MACE Total Score:']/../following-sibling::td/input");
    private static By createTbiAssessmentNoteLinkBy = By.xpath("//div[@id=\"tbiNotesContainer\"]/descendant::a[text()='Create Note']");
    private static By tbiPopupBy = By.id("tbi-popup");
    private static By assessmentTypeDropdownBy = By.id("tbiType");
    private static By noteTitleTextFieldBy = By.id("tbiNoteTitle");
    private static By assessmentDateTextFieldBy = By.id("tbiNoteDateString");
    private static By commentsTextAreaBy = By.id("commentsArea");
    private static By baselineYesRadioButtonBy = By.id("baselineYes");
    private static By baselineNoRadioButtonBy = By.id("baselineNo");
    private static By baselineUnknownRadioButtonBy = By.id("baselineUnknown");
    private static By referralYesRadioButtonBy = By.id("referralYes");
    private static By referralNoRadioButtonBy = By.id("referralNo");
    private static By referralLocationFieldBy = By.id("referralLocation");
    private static By saveAssessmentButtonBy = By.xpath("//button[text()='Save Assessment']");
    private static By tbiMaceTotalScoreFieldBy = By.id("tbiMaceScore");
    private static By messageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[1]");

    public TbiAssessmentNote() {
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
            createTbiAssessmentNoteLinkBy = By.xpath("//*[@id='patientSummaryForm:tbi']/descendant::a");
            tbiPopupBy = By.id("tbiModalFormCDiv"); // prob wrong
            assessmentTypeDropdownBy = By.id("tbiNoteForm:assessmentTypeDecorate:assessmentTypeSelect");
            assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
            noteTitleTextFieldBy = By.id("tbiNoteForm:assessmentNoteDecorate:assessmentTitle");
            referralLocationFieldBy = By.id("tbiNoteForm:assessmentReferralLocationDecorate:assessmentReferralLocation");
            commentsTextAreaBy = By.id("tbiNoteForm:assessmentComments");
            tbiMaceTotalScoreFieldBy = TBI_MACE_TOTAL_SCORE_FIELD;
            messageAreaBy = By.xpath("//*[@id='patientSummaryForm:noteTabsPanel']/preceding-sibling::div[1]/descendant::span");
            saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment");
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("    Processing TBI Assessment Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Find and click the link for creating TBI Assessment Note
        //
        try {
            WebElement bhCreateTbiAssessmentNoteLink = Utilities.waitForRefreshedClickability(createTbiAssessmentNoteLinkBy, 15, "TbiAssessmentNote.process() link to click on to pup up the TBI Assessment Note"); // was 10
            bhCreateTbiAssessmentNoteLink.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for bhCreateTbiAssessmentNoteLink to show up.  Always.  Why? ");
            return false;
        }
        catch (Exception e) {
            logger.severe("Exception either trying to get Webelement, or clicking on it: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        WebElement tbiPopupElement;
        try {
            tbiPopupElement = Utilities.waitForPresence(tbiPopupBy, 10, "summary/TbiAssessmentNote.process()");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }
        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.randomizeSection, true);

        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        Utilities.sleep(1008, ""); // hate to do this haven't been able to get around this

        try {
            Utilities.waitForVisibility(noteTitleTextFieldBy, 10, "summary/TbiAssessmentNote.process()");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for note title text field.");
            return false;
        }
        if (this.noteTitle == null || this.noteTitle.isEmpty() || this.noteTitle.equalsIgnoreCase("random")) {
            this.noteTitle = patient.patientSearch.lastName + " " + this.assessmentType; // how about that?  better?
        }
        this.noteTitle = Utilities.processText(noteTitleTextFieldBy, this.noteTitle, Utilities.TextFieldType.TITLE, this.randomizeSection, true);

        if (Arguments.date != null && (this.assessmentDate == null || this.assessmentDate.isEmpty())) {
            this.assessmentDate = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        try {
            Utilities.waitForRefreshedVisibility(assessmentDateTextFieldBy,  10, "summary/TbiAssessmentNote.(), date");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for assessment date text field.");
            return false;
        }
        this.assessmentDate = Utilities.processDateTime(assessmentDateTextFieldBy, this.assessmentDate, this.randomizeSection, true); // wow, this is slow
        if (this.assessmentDate == null || this.assessmentDate.isEmpty()) {
            logger.fine("Assessment Date came back null or empty.  Why?");
            return false;
        }
        // The above is definitely failing because something causes the text to get wiped out.  I think it's that there
        // isn't enough time after entering an Assessment Type value and inputting the date.  So when Assessment Type comes
        // back from the server, it wipes out everything else in the text boxes.
        this.comments = Utilities.processText(commentsTextAreaBy, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.randomizeSection, true);

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.randomizeSection, true);
        }

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            this.baseline = Utilities.processRadiosByButton(this.baseline, this.randomizeSection, true, baselineYesRadioButtonBy, baselineNoRadioButtonBy, baselineUnknownRadioButtonBy);
        }

        this.referral = Utilities.processRadiosByButton(this.referral, this.randomizeSection, true,
                referralYesRadioButtonBy, referralNoRadioButtonBy);
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(referralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.randomizeSection, true);
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }
        //
        // Save the note.
        //
        Instant start = null;
        WebElement saveAssessmentButton = null;
        try {
            saveAssessmentButton = Utilities.waitForRefreshedClickability(saveAssessmentButtonBy, 10, "summary/TbiAssessmentNote.(), save assessment button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "");
            }
            start = Instant.now();
            saveAssessmentButton.click(); // no ajax!
        }
        catch (TimeoutException e) {
            logger.severe("Timed out waiting for saveAssessmentButton to be clickable."); ScreenShot.shoot("SevereError");
            return false;
        }
        catch (Exception e) {
            logger.severe("Some kinda exception for finding and clicking on save assessment button"); ScreenShot.shoot("SevereError");
            return false;
        }

        logger.finest("Waiting for staleness of TBI popup.");
        (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(tbiPopupElement));
        logger.finest("Done waiting for staleness of TBI popup element");

        // If the Save Assessment button worked, then the TBI Assessment Note modal window should have gone away.
        // If it didn't then the next stuff will fail.  If it didn't should we try again somehow?  Probable failure
        // is the Assessment Date got wiped out because Assessment Type took too long.
        // This next check just sees if we're back to the Behavioral Health Assessments page after doing the TBI Note modal.
        // But we probably could have checked for the message "You have successfully created a TBI note!"
        // By the way, this is different than tbiAssessmentNote, where there is no message "successfully created".

        try { // 2nd line failed 3/12/19
            //WebElement element = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy)); // changed from 1 to 5
            WebElement element = Utilities.waitForRefreshedVisibility(messageAreaBy,  5, "summary/TbiAssessmentNote.(), message area");
            String someTextMaybe = element.getText();
            if (someTextMaybe != null) {
                if (!someTextMaybe.contains("successfully")) {
                    if (!Arguments.quiet) System.out.println("      ***Failed to save TBI Assessment Note.  Message: " + someTextMaybe); // wrong message?  too long?
                    return false;
                }
            } else {
                logger.fine("Possibly couldn't wait for a refreshed element with visibility for the message area for trying to save TBI assessment note.");
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("TbiAssessmentNote.process(), did not find evidence modal window was replaced by Behavioral Health Assessments page: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("      Saved TBI Assessment note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }

        timerLogger.fine("TbiAssessmentNote save Assessment button click() took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "summary/TbiAssessmentNote");
        }
        return true;
    }
}
