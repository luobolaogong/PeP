package pep.patient.treatment.painmanagementnote.clinicalnote;

import org.openqa.selenium.*;
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


public class ClinicalNote {
    private static Logger logger = Logger.getLogger(ClinicalNote.class.getName()); // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String clinicalNoteDateTime = ""; // "mm/dd/yyyy hhmm z, required";
    public String adjunctMedications = ""; // "????";
    public String currentVerbalAnalogueScore = ""; // "option 1-11, required";
    public String verbalAnalogueScore = ""; // "option 1-11, required";
    //public String satisfiedWithPainManagement = "Yes"; // = y/n
    public String satisfiedWithPainManagement = ""; // = y/n
    public String commentsPainManagement = ""; // "text, required if not satisfied";
    public String painManagementPlan = ""; // "text";
    public String commentsNotesComplications = ""; // "text";

    // Clinical Note

    private static By CLINICAL_NOTE_TAB = By.linkText("Clinical Note"); // test 1/23/19
    private static By CN_CURRENT_VERBAL_ANALOGUE_SCORE_DROPDOWN = By.id("painNoteForm:currentVasDecorate:currentVas");
    private static By CN_VERBAL_ANALOGUE_SCORE_DROPDOWN = By.id("painNoteForm:vasDecorate:vas");
    private static By CN_PAIN_MANAGEMENT_PLAN_TEXTAREA = By.xpath("//textarea[@id='painNoteForm:painPlanDecorate:painPlan']");
    private static By CN_COMMENTS_TEXTAREA = By.xpath("//textarea[@id='painNoteForm:discontinueCommentsDecorate:comments']");
    private static By CN_DISCONTINUE_COMMENTS_TEXTAREA = By.xpath("//*[@id='painNoteForm:satisfiedCommentsDecorate:satisfiedComments']");
//    private static By clinicalNoteTabBy = By.xpath("//a[text()='Clinical Note']"); // going back and forth on these?  1/25/19 11:09am
    private static By clinicalNoteTabBy = By.linkText("Clinical Note");
    private static By clinicalSectionBy = By.id("clinicalNoteTabContainer"); // a visible version.  Do same for ProcedureNoteTabContainer, and transferNoteTabContainer
    private static By clinicalNoteDateTimeBy = By.id("clinicalPainNoteFormplacementDate");
    private static By cnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::select[@id='currentVas']");
    private static By cnVerbalAnalogueScoreDropdownBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::select[@id='vas']");
//    private static By cnSatisfiedWithPainManagementYesLabelBy = By.xpath("//*[@id='clinicalPainNoteForm']/div/table/tbody/tr[6]/td[2]/label[1]");
//    private static By cnSatisfiedWithPainManagementNoLabelBy = By.xpath("//*[@id='clinicalPainNoteForm']/div/table/tbody/tr[6]/td[2]/label[2]");
    private static By cnSatisfiedWithPainManagementYesLabelBy = By.xpath("//label[@for='satisfiedInd1']");
    private static By cnSatisfiedWithPainManagementNoLabelBy = By.xpath("//label[@for='satisfiedInd2']");
    private static By cnSatisfiedWithPainManagementYesButtonBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::input[@id='satisfiedInd1']");
    private static By cnSatisfiedWithPainManagementNoButtonBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::input[@id='satisfiedInd2']");
    private static By cnDiscontinueCommentsTextAreaBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='satisfiedComments']");
    private static By cnPainManagementPlanTextAreaBy  = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='painPlan']");
    private static By cnCommentsTextAreaBy            = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::textarea[@id='comments']");
    private static By createNoteThingBy = By.xpath("//form[@id='clinicalPainNoteForm']/descendant::button[text()='Create Note']");
    private static By messageAreaBy = By.id("pain-note-message");


    public ClinicalNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.clinicalNoteDateTime = "";
            this.adjunctMedications = "";
            this.currentVerbalAnalogueScore = "";
            this.verbalAnalogueScore = "";
            this.satisfiedWithPainManagement = ""; // was "Yes"
            this.commentsPainManagement = "";
            this.painManagementPlan = "";
            this.commentsNotesComplications = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            clinicalNoteTabBy = CLINICAL_NOTE_TAB;
            //clinicalSectionBy = By.id("painNoteForm:Clinical"); // seems correct, but this exists in the HTML whether visible or not
            clinicalSectionBy = By.xpath("//*[@id='painNoteForm:Clinical' and @style=';height:100%']"); // a visible version
            //clinicalSectionBy = By.xpath("//div[@id='painNoteForm:Clinical' and contains(@style,'display: none')]"); // a visible version.  Do same for ProcedureNoteTabContainer, and transferNoteTabContainer
            //clinicalSectionBy = By.xpath("//div[@id='painNoteForm:Clinical' and not(contains(@style,'none'))]"); // untested.  May be more general sol'n

            clinicalNoteDateTimeBy = By.id("painNoteForm:discontinueDateDecorate:placementDateInputDate");
            cnCurrentVerbalAnalogueScoreDropdownBy = CN_CURRENT_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            cnVerbalAnalogueScoreDropdownBy = CN_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            //cnSatisfiedWithPainManagementYesLabelBy = CN_SATISFIED_WITH_PAIN_MANAGEMENT_YES_RADIO_LABEL;
            cnSatisfiedWithPainManagementYesLabelBy = By.xpath("//*[@id='painNoteForm:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[1]/label");
            //cnSatisfiedWithPainManagementNoLabelBy = CN_SATISFIED_WITH_PAIN_MANAGEMENT_NO_RADIO_LABEL;
            cnSatisfiedWithPainManagementNoLabelBy = By.xpath("//*[@id='painNoteForm:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[2]/label");
            cnDiscontinueCommentsTextAreaBy = CN_DISCONTINUE_COMMENTS_TEXTAREA;
            cnPainManagementPlanTextAreaBy = CN_PAIN_MANAGEMENT_PLAN_TEXTAREA;
            cnCommentsTextAreaBy = CN_COMMENTS_TEXTAREA;
            createNoteThingBy = By.id("painNoteForm:createNote");
            messageAreaBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span");
            //messageAreaBy = By.id("clinical-note-message"); // wrong
        }
    }

    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Clinical Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //logger.fine("ClinicalNote.process() 1");
        try {
            //System.out.println("Here comes wait for element to be clickable with by: " + clinicalNoteTabBy);
            WebElement clinicalNoteTabElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.elementToBeClickable(clinicalNoteTabBy));
            //System.out.println("Here comes a click on the element");
            clinicalNoteTabElement.click(); // on Gold this will display something that has no content, as of 11/6/18
            //System.out.println("Here comes a wait for ajax");
            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // was 4
            //System.out.println("Done with ajax wait.");
        }
        catch (StaleElementReferenceException e) {
            logger.severe("clinicalNote.process(), couldn't get Clinical Note tab, and/or couldn't click it: Stale element reference: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("clinicalNote.process(), couldn't get tab, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        try {
            // There are three tables (each in its own td) associated with the tabs: Procedure Notes, Clinical Note, and Transfer Note,
            // all in the HTML, but only one table/td is being displayed at a time.  The other td's will have a
            // style="display: none;;height:100%" in it.  the one that is displayed has style=";height:100%"
            // So how do you know when this Clinical Note table is showing up?  You check for that td's style and see
            // if it has a "display: none" in it.
            //
            //(new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(clinicalSectionBy)); // new
            Utilities.waitForPresence(clinicalSectionBy, 1, "ClinicalNote.process()");
        }
        catch (Exception e) {
            logger.severe("ClinicalNote.process(), Could not wait for visibility of clinical section.  e: " + Utilities.getMessageFirstLine(e));
            return false; // fails: 1
        }

        if (Arguments.date != null && (this.clinicalNoteDateTime == null || this.clinicalNoteDateTime.isEmpty())) {
            this.clinicalNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        try {
            Utilities.waitForVisibility(clinicalNoteDateTimeBy, 4, "ClinicalNote.process()");
            //logger.fine("ClinicalNote.process() 8");
        }
        catch (Exception e) {
            logger.severe("ClinicalNote.process(), What, couldn't get clinical note date/time?");
        }
        Utilities.sleep(555); // hate to do this.  But tired of date/time screwing up.  However it very well could be that the problem is we're not on the right page


        // this next line usually fails because we're not on the clinical "page" -- the clinical tab was clicked, but nothing happened.
        this.clinicalNoteDateTime = Utilities.processDateTime(clinicalNoteDateTimeBy, this.clinicalNoteDateTime, this.random, true);

        this.currentVerbalAnalogueScore = Utilities.processDropdown(cnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.random, true);

        this.verbalAnalogueScore = Utilities.processDropdown(cnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.random, true);
        // Wow, since when can you add comments when Satisfied is Yes???  Now comments are always required for Clinical
        // And refactor this next part too.  Can be boiled down a lot.
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
//            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
//            this.commentsPainManagement = Utilities.processText(cnDiscontinueCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);

            //this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
            // next line prob not unique id
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true,
                    cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(cnDiscontinueCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
            }
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) {
            // this next line fails.  What the crap?  text area no longer exists if "yes" radio button selected?
//            this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, cnSatisfiedWithPainManagementYesButtonBy, cnSatisfiedWithPainManagementNoButtonBy);
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true,
                    cnSatisfiedWithPainManagementYesLabelBy, cnSatisfiedWithPainManagementNoLabelBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(cnDiscontinueCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
            }
        }

        // watch comments/text fields here.  In right order?
        logger.fine("ClinicalNote.process(), Here comes PainManagementNoteSection CN_PAIN_MANAGEMENT_PLAN_TEXTAREA");

        this.painManagementPlan = Utilities.processText(cnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.random, true);

        this.commentsNotesComplications = Utilities.processText(cnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);
        // above line doesn't do anything??????????????????????????????????????

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        Instant start = null;

        try { // check next line and single step through.  6 buttons with that text
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.elementToBeClickable(createNoteThingBy)); // was 3s

            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000);
            }
            start = Instant.now();
            // Next line can cause a Problem page!!!!!!!!!!!!!!!!!!!!!!!!
            createNoteButton.click(); // is there any message area on gold?  Yes if you go slow.   How about demo?

            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.severe("ClinicalNote.process(), Could not get the create note button, or click on it.");
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
                logger.severe("ClinicalNote.process(), Timed out waiting for message area to be visible.  e: " + Utilities.getMessageFirstLine(e));
                return false; // ???
            }
            catch (Exception e) {
                logger.severe("ClinicalNote.process(), Some kinda exception: " + Utilities.getMessageFirstLine(e));
                return false; // ???
            }
        }
        else { // this is for Gold!!!
            // Could check to see if the Clinical Note area is still visible
            // By the way, Pain Management Notes section does not show a DATE value for clinical notes.  Looks like a bug.
            try {
                logger.severe("ClinicalNote.process(), what are we waiting for?");
//                (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(clinicalSectionBy)); // maybe works
                Utilities.waitForInvisibility(clinicalSectionBy, 10, "ClinicalNote.process()"); // maybe works
                logger.severe("ClinicalNote.process(), done waiting for something waiting for?");
            }
            catch (Exception e) {
                logger.fine("Couldn't wait for clinical section to become invisible.");
            }
            // there may be an issue if previous "successfully" messages are still there from a previous save.  How do we know which one it's for?

            // this next stuff is a copy from above.  Just for test now.  If it works, then combine these perhaps
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
            catch (Exception e) {
                logger.fine("Couldn't wait for visibility of message area, probably.");
            }
        }

        if (!Arguments.quiet) {
            System.out.println("        Saved Clinical note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        //timerLogger.info("Clinical Note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        timerLogger.info("Clinical Note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true;
    }
}
