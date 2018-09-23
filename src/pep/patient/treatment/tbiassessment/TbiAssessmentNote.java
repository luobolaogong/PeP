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
import pep.utilities.Utilities;

// THIS ONE IS UNDER TbiAssessment and in that package

public class TbiAssessmentNote { // multiple?  Also, there's one below.  Duplicates are error prone
    public Boolean random; // true if want this section to be generated randomly
    public String assessmentType; // "option 1-3, required";
    public String assessmentDate; // "mm/dd/yyyy hhmm, required";
    public String noteTitle; // "text, required";
    public String maceTotalScore; // "text, required if assessmentType is mace";
    public String baseline; // "yes,no,unknown, required if assessmentType is anam";
    public String referral; // "yes,no, required";
    public String referralLocation; // "text, required if referral is yes";
    public String comments; // "text, required";

    public static By TBI_MACE_TOTAL_SCORE_FIELD = By
            .xpath("//label[.='MACE Total Score:']/../following-sibling::td/input");
    // prob wrong
    private static By createTbiAssessmentNoteLinkBy = By.xpath("//*[@id=\"tbiNotesContainer\"]/div[3]/a");

    private static By tbiPopupBy = By.id("tbi-popup");
    private static By assessmentTypeDropdownBy = By.id("tbiType");
    private static By noteTitleTextFieldBy = By.id("tbiNoteTitle");
    private static By assessmentDateTextFieldBy = By.id("tbiNoteDateString");
    private static By commentsTextAreaBy = By.id("commentsArea");
    private static By baselineYesRadioButtonLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[1]");
    private static By baselineNoRadioButtonLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[2]");
    private static By baselineUnknownRadioButtonLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[3]");
    private static By referralYesRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[1]");
    private static By referralNoRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[2]");

    private static By referralLocationFieldBy = By.id("referralLocation");
    private static By saveAssessmentButtonBy = By.xpath("//*[@id=\"tbiFormContainer\"]/div/button");
    private static By behavioralHealthAssessmentsH4By = By.xpath("/html/body/table/tbody/tr[2]/td/table/tbody/tr/td/h4");
    private static By tbiMaceTotalScoreFieldBy = By.id("tbiMaceScore");
    private static By messageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[3]");



    public TbiAssessmentNote() {
        if (Arguments.template) {
            //this.random = null;
            this.assessmentType = "";
            this.assessmentDate = "";
            this.noteTitle = "";
            this.maceTotalScore = "";
            this.baseline = "";
            this.referral = "";
            this.referralLocation = "";
            this.comments = "";
        }
        if (Pep.isDemoTier) {
            createTbiAssessmentNoteLinkBy = By.id("tbiAssessmentForm:j_id570"); // prob wrong
            tbiPopupBy = By.id("tbiModalFormCDiv"); // prob wrong
            assessmentTypeDropdownBy = By.id("tbiNoteForm:assessmentTypeDecorate:assessmentTypeSelect");
            assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
            noteTitleTextFieldBy = By.id("tbiNoteForm:assessmentNoteDecorate:assessmentTitle");
           // baselineYesRadioButtonLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[1]/label");


            baselineYesRadioButtonLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[1]/label");
            baselineNoRadioButtonLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[2]/label");
            baselineUnknownRadioButtonLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[3]/label");

            referralLocationFieldBy = By.id("tbiNoteForm:assessmentReferralLocationDecorate:assessmentReferralLocation");
            commentsTextAreaBy = By.id("tbiNoteForm:assessmentComments");
            tbiMaceTotalScoreFieldBy = TBI_MACE_TOTAL_SCORE_FIELD;
            referralYesRadioLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[1]/label");
            referralNoRadioLabelBy = By.xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[2]/label");
            saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment"); // not sure for demo tier
            messageAreaBy = By.xpath("//*[@id=\"tbiAssessmentForm:j_id553\"]/table/tbody/tr/td/span");

        }
    }

    // This is too long.  Break it into parts
    // I've changed the order of elements in this method.  Probably should do the same in BhAssessmentNote
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");

        // We don't need to do a navigation here as it was done in parent TbiAssessment, nor do we need to do a search

        // We're not on the TBI Assessment Note modal window yet.  Must click the "Create Note" link first
        //By bhCreateTbiAssessmentNoteLinkBy = By.id("bhAssessmentForm:j_id518"); // right? wrong.  How did this happen?  dev's changing it?

        try {
            WebElement bhCreateTbiAssessmentNoteLink = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createTbiAssessmentNoteLinkBy));
            bhCreateTbiAssessmentNoteLink.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for bhCreateTbiAssessmentNoteLink to show up.  Always.  Why? ");
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Exception either trying to get Webelement, or clicking on it: " + e.getMessage());
            return false;
        }


        // Now hopefully the TBI Assessment Note page has popped up.  It has a pulldown as first interactive element,
        // but maybe we should just check that the modal window is up first.
//        By tbiPopupBy = By.id("tbiModalFormCDiv");
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(tbiPopupBy));
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }

        // This next line causes a DOM rewrite which causes trouble to elements after this if we go too fast to them after something
        // is selected from this dropdown.  So until we can detect the DOM rewrite is finished, we have to put in a mandatory sleep.
        //this.assessmentType = Utilities.processDropdown(TBI_ASSESSMENT_TYPE_DROPDOWN, this.assessmentType, this.random, true);
        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.random, true);
        // MUST MUST MUST WAIT after this to give the freaking server time to respond and redo the DOM.

        // EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT
        //if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), doing a call to isFinishedAjax Does this work here????");
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        // The above doesn't seem to help, so will do a sleep
        Utilities.sleep(1008); // hate to do this haven't been able to get around this

        // Moved this section from below to here to help with the delay of Assessment Type
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(noteTitleTextFieldBy));
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for note title text field.");
            return false;
        }
        this.noteTitle = Utilities.processText(noteTitleTextFieldBy, this.noteTitle, Utilities.TextFieldType.TITLE, this.random, true);


        // If this fails again due to stale element reference, then do a close comparison between this TbiAssessmentNote.java and BhTbiAssessmentNote.java
        // Also see if the two can be merged somehow, because they are almost exactly the same.

        if (Arguments.date != null && (this.assessmentDate == null || this.assessmentDate.isEmpty())) {
            this.assessmentDate = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }

        // This next stuff has a ton of ugly calendar JS code behind it, and it's impossible to follow.
//        By assessmentDateTextFieldBy = By.id("tbiNoteForm:assessmentDateDecorate:assessmentDateInputDate");
        // this next wait stuff probably unnecessary.  The problem was identified that the first dropdown did an ajax call and redid the dom
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(assessmentDateTextFieldBy)));
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for assessment date text field.");
            return false;
        }
        this.assessmentDate = Utilities.processDateTime(assessmentDateTextFieldBy, this.assessmentDate, this.random, true); // wow, this is slow
        if (this.assessmentDate == null || this.assessmentDate.isEmpty()) {
            if (Arguments.debug) System.out.println("Assessment Date came back null or empty.  Why?");
            return false;
        }
        // The above is definitely failing because something causes the text to get wiped out.  I think it's that there
        // isn't enough time after entering an Assessment Type value and inputting the date.  So when Assessment Type comes
        // back from the server, it wipes out everything else in the text boxes.

        // Comments (moved from below to here, to give date more time)
        this.comments = Utilities.processText(commentsTextAreaBy, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.random, true);
        // take a look at the page before continuing on, and then after the save, is there any indicate it succeeded?  Next xpath is prob wrong


        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.random, true);
        }

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            this.baseline = Utilities.processRadiosByLabel(this.baseline, this.random, true, baselineYesRadioButtonLabelBy, baselineNoRadioButtonLabelBy, baselineUnknownRadioButtonLabelBy);
        }

        // following line differs between versions in BehavioralHealthAssesments.java and TraumaticBrainInjuryAssessments.java
        this.referral = Utilities.processRadiosByLabel(this.referral, this.random, true, referralYesRadioLabelBy, referralNoRadioLabelBy);
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(referralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.random, true);
        }

        WebElement saveAssessmentButton = null;
        try {
            saveAssessmentButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(saveAssessmentButtonBy));
            saveAssessmentButton.click(); // no ajax!
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Timed out waiting for saveAssessmentButton to be clickable.");
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Some kinda exception for finding and clicking on save assessment button");
            return false;
        }

        // If the Save Assessment button worked, then the TBI Assessment Note modal window should have gone away.
        // If it didn't then the next stuff will fail.  If it didn't should we try again somehow?  Probable failure
        // is the Assessment Date got wiped out because Assessment Type took too long.
        // This next check just sees if we're back to the Behavioral Health Assessments page after doing the TBI Note modal.
        // But we probably could have checked for the message "You have successfully created a TBI note!"

        try {
            Utilities.sleep(1555); // just another guess
            WebElement element = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
            String someTextMaybe = element.getText();
            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), Message area says " + someTextMaybe);
                // we could just return true here
            } else {
                if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), Failed in saving TBI Assessment Note.");
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), did not find evidence modal window was replaced by Beharioral Health Assessments page: " + e.getMessage());
            return false;
        }

//        // What the crud is this?  2nd effort to get a message?
//        try {
//            WebElement element = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(behavioralHealthAssessmentsH4By));
//            String someTextMaybe = element.getText();
//            if (someTextMaybe != null && someTextMaybe.contains("Traumatic Brain Injury Assessments")) { // strange but correct, I think.
//                if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), found something saying Behavioral Health Assessments.");
//            } else {
//                if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), Failed in saving TBI Assessment Note.");
//                return false;
//            }
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("TbiAssessmentNote.process(), did not find evidence modal window was replaced by Beharioral Health Assessments page: " + e.getMessage());
//            return false;
//        }
        return true;
    }
}
