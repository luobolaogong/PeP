package pep.patient.treatment.behavioralhealthassessment;
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

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;


// THIS ONE IS UNDER BehavioralHealthAssessment and in that package.  But we should probably bump this up a level and share it between BehavioralHealthAssessment and TbiAssessment.  Or create an Abstract and add just a field or so more for the other one that's just one field bigger, I think.

// multiple? Also, there's one below.  Duplicates are error prone
public class BhTbiAssessmentNote {
    private static Logger logger = Logger.getLogger(BhTbiAssessmentNote.class.getName()); // multiple?  Also, there's one below.  Duplicates are error prone
    public Boolean random; // true if want this section to be generated randomly
    public String assessmentType; // "option 1-3, required";
    public String assessmentDate; // "mm/dd/yyyy hhmm, required";
    public String noteTitle; // "text, required";
    public String maceTotalScore; // "text, required if assessmentType is mace";
    public String baseline; // "yes,no,unknown, required if assessmentType is anam";
    //Boolean referral = false; // "yes,no, required";
    public String referral; // "yes,no, required";
    public String referralLocation; // "text, required if referral is yes";
    public String comments; // "text, required";

    private static By bhTbiAssessmentNotePopupBy = By.id("tbi-popup");

    private static By assessmentTypeDropdownBy = By.id("tbiType");
    private static By noteTitleTextFieldBy = By.id("tbiNoteTitle");

    private static By saveAssessmentButtonBy = By.xpath("//*[@id=\"tbiFormContainer\"]/div/button");

    private static By bhCreateTbiAssessmentNoteLinkBy = By.xpath("//*[@id=\"tbiNotesContainer\"]/div[3]/a");
    private static By assessmentDateTextFieldBy = By.id("tbiNoteDateString");
    private static By tbiMaceTotalScoreFieldBy = By.id("tbiMaceScore");
    private static By cnBaselineYesRadioButtonBy = By.id("baselineYes"); // not used because the radio in bhTbiAssessmentNote has an associate <label>
    private static By cnBaselineYesRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[1]");
    private static By cnBaselineNoRadioButtonBy = By.id("baselineNo"); // not used because the radio in bhTbiAssessmentNote has an associate <label>
    private static By cnBaselineNoRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[2]");
    private static By cnBaselineUnknownRadioButtonBy = By.id("//*[@id=\"baselineUnknown\"]"); // not used because the radio in bhTbiAssessmentNote has an associate <label>
    private static By cnBaselineUnknownRadioLabelBy = By.xpath("//*[@id=\"baselineRadios\"]/label[3]");
    private static By tbiReferralYesRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[1]");
    private static By tbiReferralNoRadioLabelBy = By.xpath("//*[@id=\"tbiFormContainer\"]/table/tbody/tr[6]/td[2]/label[2]");
    private static By tbiReferralLocationFieldBy = By.id("referralLocation");
    private static By tbiCommentsTextArea = By.id("commentsArea");
    private static By patientDemographicsContainerBy = By.id("patient-demographics-container");

    //private static By tbiAssessmentNoteMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[7]");
    //private static By tbiAssessmentNoteMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[6]");
    //private static By tbiAssessmentNoteMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[7]"); // why keep changing back and forth?
    //private static By tbiAssessmentNoteMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[8]"); // changed 11/6/18
    private static By tbiAssessmentNoteMessageAreaBy = By.xpath("//div[@id='tbiNotesContainer']/preceding-sibling::div[1]");



    public BhTbiAssessmentNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.assessmentType = "";
            this.assessmentDate = "";
            this.noteTitle = "";
            this.maceTotalScore = "";
            this.baseline = "";
            this.referral = "";
            this.referralLocation = "";
            this.comments = "";
        }
        if (codeBranch.equalsIgnoreCase("Seam")) {
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
            patientDemographicsContainerBy = By.id("bhAssessmentForm");
            tbiAssessmentNoteMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[7]"); // demo? gold? both?
        }
    }

    // BH Notes - create note popup  (this is the old DEMO tier way.)
    // TBI Assessments
    public static final By TBI_MACE_TOTAL_SCORE_FIELD = By
            .xpath("//label[.='MACE Total Score:']/../following-sibling::td/input");
    public static final By TBI_REFERRAL_YES_RADIO = By
            .xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[1]/label");
    public static final By TBI_REFERRAL_NO_RADIO = By
            .xpath("//*[@id=\"tbiNoteForm:assessmentReferralChoiceDecorate:assessmentReferralChoice\"]/tbody/tr/td[2]/label");

    public static final By TBI_REFERRAL_LOCATION_FIELD = By
            .xpath("//label[.='Referral Location:']/../following-sibling::td/input");
    public static final By TBI_COMMENTS_TEXTAREA = By
            .xpath("//textarea[@id='tbiNoteForm:assessmentComments']");
    // I'm trying something new here
    public static final By CN_BASLINE_YES_RADIO_BUTTON = By
            .xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[1]/label");
    public static final By CN_BASLINE_NO_RADIO_BUTTON = By
            .xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[2]/label");
    public static final By CN_BASLINE_UNKNOWN_RADIO_BUTTON = By
            .xpath("//*[@id=\"tbiNoteForm:assessmentBaselineDecorate:assessmentBaseline\"]/tbody/tr/td[3]/label");

    // This is too long and ugly.  Break it up.

    // I changed the order of elements in the very similar TbiHealthAssessmentNote class.  Should compare and change this one probably

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing BH TBI Assessment Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );


        // We don't have to do a navigate here because BehavioralHealthAssessments is the parent of
        // BhTbiAssessmentNote, and to get here we had to do the parent which include navigating and
        // patient search.  Same is true for TBI Assessments on this page

        // We're not on the TBI Assessment Note modal window yet.  Must click the "Create Note" link first
        try {
            WebElement bhCreateTbiAssessmentNoteLink = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(bhCreateTbiAssessmentNoteLinkBy));
            bhCreateTbiAssessmentNoteLink.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for bhCreateTbiAssessmentNoteLink to show up.");
            return false;
        }
        catch (Exception e) {
            logger.fine("Exception either trying to get Webelement, or clicking on it: " + e.getMessage());
            return false;
        }

        // Now hopefully the TBI Assessment Note page has popped up.  It has a pulldown as first interactive element,
        // but maybe we should just check that the modal window is up first.
        WebElement bhPopupElement;
        try {
            bhPopupElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(bhTbiAssessmentNotePopupBy));
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for tbiModelFormElement to show up.");
            return false;
        }


        // When a selection is made from the Assessment Type dropdown, an AJAX.Submit call is made,
        // which causes the server to return a "container"/table element that gets stuffed back
        // into the DOM, and that table element contains the date fields among other things.
        // So, if you do the date first and the selection later, the date gets wiped out.
        // The same thing happens if you select an element from the dropdown and then very quickly
        // put a date value into the Assessment Date field.
        // How do you keep that from happening?  Either insert a mandatory wait for some period of time
        // (.5 sec seems to be the normal (fast) time, but if server is slow could be long, perhaps)
        // or maybe you try to detect when that container/table gets restored.
        //
        this.assessmentType = Utilities.processDropdown(assessmentTypeDropdownBy, this.assessmentType, this.random, true);
        // MUST MUST MUST WAIT for this silly thing because of the AJAX call

        //logger.fine("BhTbiAssessmentNote.process(), doing a call to isFinishedAjax Does this work here????");
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // doesn't work in counterpart Tbi class had to add sleep
        // The above doesn't seem to help, so will do a sleep
        Utilities.sleep(508); // haven't been able to get around this.  Not absolutely sure this was necessary, but seemed to be in the Tbi version

        if (Arguments.date != null && (this.assessmentDate == null || this.assessmentDate.isEmpty())) {
            this.assessmentDate = Arguments.date + " " + Utilities.getCurrentHourMinute(); // this shouldn't take too long
        }

        // This next stuff has a ton of ugly calendar JS code behind it, and it's impossible to follow.
        // this next wait stuff probably unnecessary.  The problem was identified that the first dropdown did an ajax call and redid the dom
        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(assessmentDateTextFieldBy)));
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for assessment date text field.");
            return false;
        }
        this.assessmentDate = Utilities.processDateTime(assessmentDateTextFieldBy, this.assessmentDate, this.random, true);
        if (this.assessmentDate == null || this.assessmentDate.isEmpty()) {
            logger.fine("Assessment Date came back null or empty.  Why?");
            return false;
        }
        // doesn't dateTime cause a delay?  Is it checked for inside processDateTime()??

        try {
            (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(noteTitleTextFieldBy));
        }
        catch (TimeoutException e) {
            logger.fine("Timed out waiting for note title text field.");
            return false;
        }
        this.noteTitle = Utilities.processText(noteTitleTextFieldBy, this.noteTitle, Utilities.TextFieldType.TITLE, this.random, true);


        // I kinda cleaned up the stuff above, but not below.  This modal window thing is screwy probably because of the date, but maybe the
        // referral thing too if it causes a server call.
        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("MACE")) {
            this.maceTotalScore = Utilities.processIntegerNumber(tbiMaceTotalScoreFieldBy, this.maceTotalScore, 0, 30, this.random, true);
        }

        if (this.assessmentType != null && this.assessmentType.equalsIgnoreCase("ANAM")) {
            // We don't do processRadiosByButton() because the radios in bhTBI Assessment Note have <label> nodes
            this.baseline = Utilities.processRadiosByLabel(this.baseline, this.random, true, cnBaselineYesRadioLabelBy, cnBaselineNoRadioLabelBy, cnBaselineUnknownRadioLabelBy);
        }

        // following line differs between versions in BehavioralHealthAssesments.java and TraumaticBrainInjuryAssessments.java
        this.referral = Utilities.processRadiosByLabel(this.referral, this.random, true, tbiReferralYesRadioLabelBy, tbiReferralNoRadioLabelBy); // something wrong about these XPATHS or radios
        if (this.referral != null && this.referral.equalsIgnoreCase("yes")) {
            this.referralLocation = Utilities.processText(tbiReferralLocationFieldBy, this.referralLocation, Utilities.TextFieldType.TITLE, this.random, true);
        }

        // Comments
        this.comments = Utilities.processText(tbiCommentsTextArea, this.comments, Utilities.TextFieldType.TBI_ASSESSMENT_NOTE_COMMENT, this.random, true);


        WebElement saveAssessmentButton = null;
//        By saveAssessmentButtonBy = By.id("tbiNoteForm:submitAssessment");
        try {
            saveAssessmentButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(saveAssessmentButtonBy));
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

        // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
        logger.fine("Waiting for staleness of popup.");
        (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(bhPopupElement));
        logger.fine("Done waiting");

        // if the save succeeded, the modal window goes away.  There may be a message on the Behavioral Health Assessments
        // page indicating success.  Failure is indicated by the modal window still being there, with some kind of message.
        // So, we can either wait to see if the modal window is still there and check the message, or we can check that
        // we got back to the Behavioral Health Assessments page.
        // !!!!!!!!!!!!!!!!!!!!!!This is different than the TBI thing from the TBI Assessments. !!!!!!!!!!!!!!!!!!!!!!!!!1


        // Let's assume that the modal window went away and we're back to the Behavioral Health Assessments page, and let's
        // check for success.  Maybe not worth the effort.  Maybe better just to check that the BHA page is there, with
        // Patient Demographics section.
        try {
            //Utilities.sleep(2555); // seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work. // was 1555


            WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(tbiAssessmentNoteMessageAreaBy));


            String someTextMaybe = someElement.getText();
            if (someTextMaybe.contains("successfully")) {
                logger.fine("BhTbiAssessmentNote.process(), saved note successfully.");
                //return true; // new
            }
            else {
                logger.severe("      ***Failed to save BH TBI Assessment Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " + someTextMaybe);
                return false; // fails: 1
            }
        }
        catch (Exception e) {
            logger.fine("BhTbiAssessmentNote.process(), Didn't find message after save attempt: " + e.getMessage());
            return false; // fails: demo: 3
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }
}
