package pep.patient.treatment.tbiassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
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

public class TbiAssessmentNote {
    private static Logger logger = Logger.getLogger(TbiAssessmentNote.class.getName()); // multiple?  Also, there's one below.  Duplicates are error prone
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String assessmentType; // "option 1-3, required";
    public String assessmentDate; // "mm/dd/yyyy hhmm, required";
    public String noteTitle; // "text, required";
    public String maceTotalScore; // "text, required if assessmentType is mace";
    public String baseline; // "yes,no,unknown, required if assessmentType is anam";
    public String referral; // "yes,no, required";
    public String referralLocation; // "text, required if referral is yes";
    public String comments; // "text, required";

    private static By TBI_MACE_TOTAL_SCORE_FIELD = By.xpath("//label[.='MACE Total Score:']/../following-sibling::td/input");
    private static By createTbiAssessmentNoteLinkBy = By.xpath("//div[@id='tbiNotesContainer']/descendant::a[text()='Create Note']"); // easier if use style?:   By.xpath("//li/a[@href='/bm-app/pain/painManagement.seam']");


    private static By tbiPopupBy = By.id("tbi-popup");
    private static By assessmentTypeDropdownBy = By.id("tbiType");
    private static By noteTitleTextFieldBy = By.id("tbiNoteTitle");
    private static By assessmentDateTextFieldBy = By.id("tbiNoteDateString");
    private static By commentsTextAreaBy = By.id("commentsArea");
    private static By baselineYesRadioButtonLabelBy = By.xpath("//div[@id='baselineRadios']/label[text()='Yes']"); // these 3 seem to work
    private static By baselineNoRadioButtonLabelBy = By.xpath("//div[@id='baselineRadios']/label[text()='No']");
    private static By baselineUnknownRadioButtonLabelBy = By.xpath("//div[@id='baselineRadios']/label[text()='Unknown']");
    private static By baselineYesRadioButtonBy = By.id("baselineYes");
    private static By baselineNoRadioButtonBy = By.id("baselineNo");
    private static By baselineUnknownRadioButtonBy = By.id("baselineUnknown");
    private static By referralYesRadioButtonBy = By.id("referralYes");
    private static By referralNoRadioButtonBy = By.id("referralNo");

    private static By referralLocationFieldBy = By.id("referralLocation");
    private static By saveAssessmentButtonBy = By.xpath("//button[text()='Save Assessment']"); // prob works
    //private static By saveAssessmentButtonBy = By.cssSelector("button[text()='Save Assessment']"); // prob doesn't work
    private static By tbiMaceTotalScoreFieldBy = By.id("tbiMaceScore");
    private static By successMessageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[1]"); // I know this is bad, but wait until devs fix messages to have ID's
    private static By errorMessageAreaBy = By.id("tbi-note-msg"); // 1/28/19
//    private static By messageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[text()='You have successfully created a TBI Assessment Note!']"); // experimental

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

            createTbiAssessmentNoteLinkBy = By.id("tbiAssessmentForm:j_id570"); // prob wrong
            tbiPopupBy = By.id("tbiModalFormCDiv"); // prob wrong
            assessmentTypeDropdownBy = By.id("tbiNoteForm:assessmentTypeDecorate:assessmentTypeSelect");
            assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
            noteTitleTextFieldBy = By.id("tbiNoteForm:assessmentNoteDecorate:assessmentTitle");
           // baselineYesRadioButtonLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline']/tbody/tr/td[1]/label");


            baselineYesRadioButtonLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline']/tbody/tr/td[1]/label");
            baselineNoRadioButtonLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline']/tbody/tr/td[2]/label");
            baselineUnknownRadioButtonLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline']/tbody/tr/td[3]/label");

            referralLocationFieldBy = By.id("tbiNoteForm:assessmentReferralLocationDecorate:assessmentReferralLocation");
            commentsTextAreaBy = By.id("tbiNoteForm:assessmentComments");
            tbiMaceTotalScoreFieldBy = TBI_MACE_TOTAL_SCORE_FIELD;
            //referralYesRadioLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice']/tbody/tr/td[1]/label");
            //referralNoRadioLabelBy = By.xpath("//*[@id='tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice']/tbody/tr/td[2]/label");
            saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment"); // not sure for demo tier
            successMessageAreaBy = By.xpath("//*[@id='tbiAssessmentForm:j_id553']/table/tbody/tr/td/span");
        }
    }

    // This is too long.  Break it into parts
    // I've changed the order of elements in this method.  Probably should do the same in BhAssessmentNote
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing TBI Assessment Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        // We don't need to do a navigation here as it was done in parent TbiAssessment, nor do we need to do a search

        // We're not on the TBI Assessment Note modal window yet.  Must click the "Create Note" link first
        try {
            //WebElement bhCreateTbiAssessmentNoteLink = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.elementToBeClickable(createTbiAssessmentNoteLinkBy)); // was 10
            WebElement bhCreateTbiAssessmentNoteLink = Utilities.waitForRefreshedClickability(createTbiAssessmentNoteLinkBy, 15, "TbiAssessmentNote.process()"); // was 10
            bhCreateTbiAssessmentNoteLink.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for bhCreateTbiAssessmentNoteLink to show up.  Always.  Why? ");
            return false;
        }
        catch (Exception e) {
            logger.severe("Exception either trying to get Webelement, or clicking on it: " + Utilities.getMessageFirstLine(e));
            return false;
        }


        // Now hopefully the TBI Assessment Note page has popped up.  It has a pulldown as first interactive element,
        // but maybe we should just check that the modal window is up first.
        WebElement tbiPopupElement;
        try {
            tbiPopupElement = Utilities.waitForPresence(tbiPopupBy, 10, "tbiassessment/TbiAssessmentNote.process");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }

        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.random, true);

        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        Utilities.sleep(1008, "tbiassessment/TbiAssessment"); // hate to do this haven't been able to get around this

        try {
            Utilities.waitForVisibility(noteTitleTextFieldBy, 10, "tbiassessment/TbiAssessmentNote.process");
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for note title text field.");
            return false;
        }
        // We may want to generate a title based on the comments for this thing.  Perhaps the first 3 words of the comments.  Better than Latin?
        if (this.noteTitle == null || this.noteTitle.isEmpty() || this.noteTitle.equalsIgnoreCase("random")) {
            this.noteTitle = patient.patientSearch.lastName + " " + this.assessmentType; // how about that?  better?
        }
        this.noteTitle = Utilities.processText(noteTitleTextFieldBy, this.noteTitle, Utilities.TextFieldType.TITLE, this.random, true);

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
        this.assessmentDate = Utilities.processDateTime(assessmentDateTextFieldBy, this.assessmentDate, this.random, true); // wow, this is slow
        if (this.assessmentDate == null || this.assessmentDate.isEmpty()) {
            logger.fine("Assessment Date came back null or empty.  Why?");
            return false;
        }
        // The above is definitely failing because something causes the text to get wiped out.  I think it's that there
        // isn't enough time after entering an Assessment Type value and inputting the date.  So when Assessment Type comes
        // back from the server, it wipes out everything else in the text boxes.

        // Comments (moved from below to here, to give date more time)
        // Looks like comments are no limited to 60 characters, which is pretty short.
        this.comments = Utilities.processText(commentsTextAreaBy, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.random, true);
        // take a look at the page before continuing on, and then after the save, is there any indicate it succeeded?  Next xpath is prob wrong

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.random, true);
        }

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            //this.baseline = Utilities.processRadiosByLabel(this.baseline, this.random, true,
                    //baselineYesRadioButtonLabelBy, baselineNoRadioButtonLabelBy, baselineUnknownRadioButtonLabelBy);
            // This next line returns without clicking a radio button.  Trace it through.
            this.baseline = Utilities.processRadiosByButton(this.baseline, this.random, true,   // //input[@id='baselineUnknown']
                    baselineYesRadioButtonBy, baselineNoRadioButtonBy, baselineUnknownRadioButtonBy);
        }

        // following line differs between versions in BehavioralHealthAssesments.java and TraumaticBrainInjuryAssessments.java
//        this.referral = Utilities.processRadiosByLabel(this.referral, this.random, true, referralYesRadioLabelBy, referralNoRadioLabelBy);
        // Seems that if you have a radio button that has an ID then use button.  Otherwise use Label???
        this.referral = Utilities.processRadiosByButton(this.referral, this.random, true, referralYesRadioButtonBy, referralNoRadioButtonBy);
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(referralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.random, true);
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }


        Instant start = null;
        WebElement saveAssessmentButton = null;
        try {
            //saveAssessmentButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(saveAssessmentButtonBy));
            saveAssessmentButton = Utilities.waitForRefreshedClickability(saveAssessmentButtonBy, 10, "TbiAssessmentNote.process()"); // was 10
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "tbiassessment/TbiAssessment");
            }
            start = Instant.now();
            saveAssessmentButton.click(); // no ajax!
        }
        catch (TimeoutException e) {
            logger.severe("Timed out waiting for saveAssessmentButton to be clickable.");
            return false;
        }
        catch (Exception e) {
            logger.severe("Some kinda exception for finding and clicking on save assessment button");
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

        // If the Save Assessment button worked, then the TBI Assessment Note modal window should have gone away.
        // If it didn't then the next stuff will fail.  If it didn't should we try again somehow?  Possible failure
        // is the Assessment Date got wiped out because Assessment Type took too long.  Or, the server failed, which happens for some reason.
        // This next check just sees if we're back to the Behavioral Health Assessments page after doing the TBI Note modal.
        // But we probably could have checked for the message "You have successfully created a TBI note!"
        // By the way, this is different than tbiAssessmentNote, where there is no message "successfully created".
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
            //WebElement element = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy)); // changed from 1 to 5
//            WebElement element = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(successMessageAreaBy)));
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
                //logger.fine("Possibly couldn't wait for a refreshed element with visibility for the message area for trying to save TBI assessment note.");
                //element = Utilities.waitForRefreshedVisibility(errorMessageAreaBy,  5, "classMethod");
                element = Utilities.waitForRefreshedVisibility(errorMessageAreaBy, 5, "TBiAssessmentNote.process()");
                someTextMaybe = element.getText();
                if (!Arguments.quiet) System.out.println("      ***Failed to save TBI Assessment Note.  Message: " + someTextMaybe); // text too long?  Wrong message?
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("TbiAssessmentNote.process(), did not find evidence modal window was replaced by Behavioral Health Assessments page: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved TBI Assessment note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }

        timerLogger.info("TbiAssessmentNote save Assessment button click() took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "tbiassessment/TbiAssessment");
        }
        return true;
    }
}
