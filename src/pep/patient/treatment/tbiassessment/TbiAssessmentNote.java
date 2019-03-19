package pep.patient.treatment.tbiassessment;

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

// THIS ONE IS UNDER TbiAssessment and in that package

/**
 * This class holds and processes Traumatic Brain Injury information.  It's a note that is part of TbiAssessment.
 */
public class TbiAssessmentNote {
    private static Logger logger = Logger.getLogger(TbiAssessmentNote.class.getName()); // multiple?  Also, there's one below.  Duplicates are error prone
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
    private static By createTbiAssessmentNoteLinkBy = By.xpath("//div[@id='tbiNotesContainer']/descendant::a[text()='Create Note']");


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
    private static By successMessageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[1]"); // I know this is bad, but wait until devs fix messages to have ID's
    private static By errorMessageAreaBy = By.id("tbi-note-msg");

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
            createTbiAssessmentNoteLinkBy = By.id("tbiAssessmentForm:j_id570");
            tbiPopupBy = By.id("tbiModalFormCDiv"); // prob wrong
            assessmentTypeDropdownBy = By.id("tbiNoteForm:assessmentTypeDecorate:assessmentTypeSelect");
            assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
            noteTitleTextFieldBy = By.id("tbiNoteForm:assessmentNoteDecorate:assessmentTitle");
            referralLocationFieldBy = By.id("tbiNoteForm:assessmentReferralLocationDecorate:assessmentReferralLocation");
            commentsTextAreaBy = By.id("tbiNoteForm:assessmentComments");
            tbiMaceTotalScoreFieldBy = TBI_MACE_TOTAL_SCORE_FIELD;
            saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment");
            successMessageAreaBy = By.xpath("//*[@id='tbiAssessmentForm:j_id553']/table/tbody/tr/td/span");
        }
    }

    /**
     * Process the TBI Assessment Note for the patient.
     * TODO: break this into parts
     * @param patient The patient to process the TBI Assessment Note for
     * @return success or failure of processing
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing TBI Assessment Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Get and click on the link for the modal popup window to create the note, and then check it popped up.
        //
        try { // next line fails, just started 3/5/19.  Bug submitted
            WebElement bhCreateTbiAssessmentNoteLink = Utilities.waitForRefreshedClickability(createTbiAssessmentNoteLinkBy, 15, "TbiAssessmentNote.process()"); // was 10
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
            tbiPopupElement = Utilities.waitForPresence(tbiPopupBy, 10, "tbiassessment/TbiAssessmentNote.process");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }
        //
        // Start processing the popup modal window.
        //
        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.randomizeSection, true);

        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        Utilities.sleep(1008, "tbiassessment/TbiAssessment"); // hate to do this haven't been able to get around this

        try {
            Utilities.waitForVisibility(noteTitleTextFieldBy, 10, "tbiassessment/TbiAssessmentNote.process");
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

        if (Arguments.date != null && (this.assessmentDate == null || this.assessmentDate.isEmpty())) {
            this.assessmentDate = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }

        // This next stuff has a ton of ugly calendar JS code behind it, and it's impossible to follow.
        // this next wait stuff probably unnecessary.  The problem was identified that the first dropdown did an ajax call and redid the dom
        try {
            Utilities.waitForRefreshedVisibility(assessmentDateTextFieldBy,  10, "tbiassessment/TbiAssessmentNote.process()");
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

        // Comments (moved from below to here, to give date more time)
        // Comments have been limited to 60 characters, which is pretty short.
        this.comments = Utilities.processText(commentsTextAreaBy, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.randomizeSection, true);
        // take a look at the page before continuing on, and then after the save, is there any indicate it succeeded?  Next xpath is prob wrong

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.randomizeSection, true);
        }

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            this.baseline = Utilities.processRadiosByButton(this.baseline, this.randomizeSection, true,   // //input[@id='baselineUnknown']
                    baselineYesRadioButtonBy, baselineNoRadioButtonBy, baselineUnknownRadioButtonBy);
        }

        // following line differs between versions in BehavioralHealthAssesments.java and TraumaticBrainInjuryAssessments.java
        this.referral = Utilities.processRadiosByButton(this.referral, this.randomizeSection, true, referralYesRadioButtonBy, referralNoRadioButtonBy);
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(referralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.randomizeSection, true);
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        //
        // Save the field values
        //
        Instant start;
        WebElement saveAssessmentButton;
        try {
            saveAssessmentButton = Utilities.waitForRefreshedClickability(saveAssessmentButtonBy, 10, "TbiAssessmentNote.process()"); // was 10
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "tbiassessment/TbiAssessment");
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
        // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
        logger.finest("Waiting for staleness of popup.");
        try {
            (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(tbiPopupElement));
        }
        catch (Exception e) {
            logger.warning("TbiAssessmentNote.process(), couldn't wait for staleness of TBI Popup Element.  Continuing.");
        }
        //
        // If the Save Assessment button worked, then the TBI Assessment Note modal window should have gone away.
        // If it didn't then the next stuff will fail.  So check to see if we're back to the Behavioral Health Assessments page
        // after doing the TBI Note modal.  To do that, check for a success or a failure message.
        //
        // Looks like we've got two possible messages.  One is on the modal window in big red letters in By.id("tbi-note-msg")
        // and the other is back to the TBI Assessment page (not modal) where we check
        // By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[text()='You have successfully created a TBI Assessment Note!']")
        // So, if you got back to that page, you're probably okay, and if you want, you can check the "successfully" message.
        // But if you didn't get back to that page, then you're on the modal and you can check for the tbi-note-msg element and get
        // the message and then return null.
        //
        // The following is a bad implementation of the logic.  We should probably us a Selenium "or" for the conditions and then work out
        // which one we got.
        try {
            WebElement element = Utilities.waitForRefreshedVisibility(successMessageAreaBy, 5, "TBiAssessmentNote.process()");
            String someTextMaybe = element.getText();
            if (someTextMaybe != null) {
                if (!someTextMaybe.contains("successfully")) {
                    element = Utilities.waitForRefreshedVisibility(errorMessageAreaBy,  5, "tbiassessment/TbiAssessmentNote.process() message area");
                    someTextMaybe = element.getText();
                    if (!Arguments.quiet) System.out.println("      ***Failed to save TBI Assessment Note.  Message: " + someTextMaybe); // text too long?  Wrong message?
                    return false;
                }
            } else {
                element = Utilities.waitForRefreshedVisibility(errorMessageAreaBy, 5, "tbiassessment/TBiAssessmentNote.process()");
                someTextMaybe = element.getText();
                if (!Arguments.quiet) System.out.println("      ***Failed to save TBI Assessment Note.  Message: " + someTextMaybe); // text too long?  Wrong message?
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("TbiAssessmentNote.process(), did not find evidence that modal window was replaced by Behavioral Health Assessments page: " + Utilities.getMessageFirstLine(e));
            ScreenShot.shoot("TbiAssessmentNoteFailure");
            return false;
        }
        //
        // Must have gotten a "success" message, so report saved, and return true:
        //
        if (!Arguments.quiet) {
            System.out.println("        Saved TBI Assessment note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }

        timerLogger.fine("TbiAssessmentNote save Assessment button click() took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "tbiassessment/TbiAssessment");
        }
        return true;
    }
}
