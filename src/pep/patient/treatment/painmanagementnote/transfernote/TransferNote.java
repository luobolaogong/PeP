package pep.patient.treatment.painmanagementnote.transfernote;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
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
 * This class handles the Transfer Note, which is under Treatment, Pain Management
 */
public class TransferNote {
    private static Logger logger = Logger.getLogger(TransferNote.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public String transferNoteDateTime;
    public String adjunctMedications;
    public String currentVerbalAnalogueScore;
    public String verbalAnalogueScore;
    public String satisfiedWithPainManagement;
    public String commentsPainManagement;
    public String painManagementPlan;
    public String commentsNotesComplications;
    public String destinationFacility;

    private static By transferNoteTabBy = By.linkText("Transfer Note");
    private static By transferSectionBy = By.id("transferPainNoteForm");
    private static By tnSatisfiedWithPainManagementYesButtonBy = By.xpath("//*[@id='transferPainNoteForm']/div/table/tbody/tr[6]/td[2]/label[1]"); // can't simplify because two of these
    private static By tnSatisfiedWithPainManagementNoButtonBy = By.xpath("//*[@id='transferPainNoteForm']/div/table/tbody/tr[6]/td[2]/label[2]");
    private static By tnSatisfiedWithPainManagementCommentsTextAreaBy = By.xpath("//*[@id='transferPainNoteForm']/descendant::textarea[@id='satisfiedComments']");
    private static By tnPainManagementPlanTextAreaBy = By.xpath("//*[@id='transferPainNoteForm']/descendant::textarea[@id='painPlan']");
    private static By tnCommentsTextAreaBy = By.xpath("//*[@id='transferPainNoteForm']/descendant::textarea[@id='comments']");
    private static By tnDestinationFacilityFieldBy = By.id("destinationMtfIdDesc");
    //private static By tnCreateNoteButton = By.xpath("//*[@id='transferPainNoteForm']/descendant::button[text()='Create Note']");
    private static By tnCreateNoteButton = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[13]/td[2]/button[1]");
    private static By tnTransferNoteDateTimeFieldBy = By.id("transferPainNoteFormplacementDate");
    private static By tnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id='transferPainNoteForm']/descendant::select[@id='currentVas']");
    private static By tnVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id='transferPainNoteForm']/descendant::select[@id='vas']");
    private static By messageAreaBy = By.id("pain-note-message");
    private static By satisfiedYesButtonBy = By.xpath("//form[@id='transferPainNoteForm']/descendant::input[@id='satisfiedInd1']");
    private static By satisfiedNoButtonBy = By.xpath("//form[@id='transferPainNoteForm']/descendant::input[@id='satisfiedInd2']");



    public TransferNote() {
        if (Arguments.template) {
            this.transferNoteDateTime = "";
            this.adjunctMedications = "";
            this.currentVerbalAnalogueScore = "";
            this.verbalAnalogueScore = "";
            this.satisfiedWithPainManagement = "";
            this.commentsPainManagement = "";
            this.painManagementPlan = "";
            this.commentsNotesComplications = "";
            this.destinationFacility = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            transferNoteTabBy = By.xpath("//td[@id='painNoteForm:Transfer_lbl']");
            transferSectionBy = By.id("painNoteForm:Transfer");
            // The following all come from AbstractTransferNote, and there are probably better ways
            tnSatisfiedWithPainManagementYesButtonBy = By.xpath("//*[@id='painNoteForm:transferDecorate:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[1]/label");;
            tnSatisfiedWithPainManagementNoButtonBy = By.xpath("//*[@id='painNoteForm:transferDecorate:satisfiedIndDecorate:satisfiedInd']/tbody/tr/td[2]/label");;
            tnSatisfiedWithPainManagementCommentsTextAreaBy = By.xpath("//label[.='Comments:']/../following-sibling::td/textarea");
            tnPainManagementPlanTextAreaBy = By.xpath("//textarea[@id='painNoteForm:transferDecorate:painPlanDecorate:painPlan']");
            tnCommentsTextAreaBy = By.xpath("//textarea[@id='painNoteForm:transferDecorate:painPlanDecorate:painPlan']");
            tnDestinationFacilityFieldBy = By.id("painNoteForm:transferDecorate:destinationFacility:facilityText");
            tnCreateNoteButton = By.id("painNoteForm:transferDecorate:createNote");
            tnTransferNoteDateTimeFieldBy = By.id("painNoteForm:transferDecorate:discontinueDateDecorate:placementDateInputDate");
            tnCurrentVerbalAnalogueScoreDropdownBy = By.id("painNoteForm:transferDecorate:currentVasDecorate:currentVas");
            tnVerbalAnalogueScoreDropdownBy = By.id("painNoteForm:transferDecorate:vasDecorate:vas");
            messageAreaBy = By.xpath("//*[@id='painNoteForm:j_id1200']/table/tbody/tr/td/span");
        }
    }

    /**
     * Process treatment note.
     * @param patient The patient for which this transfer note applies
     * @return success or failure at saving the note
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Transfer Note at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Click on Transfer Note tab.
        //
        try {
            //System.out.println("Here comes a wait for refreshed clickability of transferNoteTab");
            WebElement transferNoteTab = Utilities.waitForRefreshedClickability(transferNoteTabBy, 5, "TransferNote.() transfer note tab");
            //System.out.println("Here comes a click on that transfer note tab");
            transferNoteTab.click(); // are we on the right page for this?  Does this fail?
        }
        catch (Exception e) { // "unhandled inspector error"
            logger.severe("TransferNote.process(), couldn't get tab, and/or couldn't click on it.  Couldn't find patient record?: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        Utilities.sleep(555, "TransferNote.process(), the next waitForPresence doesn't seem to help.");
        try { // this next line probably often fails.  ARE WE ON THE RIGHT PAGE????????????????????????????
            Utilities.waitForPresence(transferSectionBy, 1, "TransferNote.process()");
        }
        catch (Exception e) {
            logger.severe("Exception caught: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails: 1
        }
        //
        // Fill in Transfer Note fields.  But are we ready?  Are we on the right page?  Did the click above work?????  Yes:1 No:1
        // Probably should check that the page is ready though, before going on!!!!!!!!!!!!
        //
        if (Arguments.date != null && (this.transferNoteDateTime == null || this.transferNoteDateTime.isEmpty())) {
            this.transferNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        // Next line can fail, but we don't catch it!  We're probably not on the right page!
        this.transferNoteDateTime = Utilities.processDateTime(tnTransferNoteDateTimeFieldBy, this.transferNoteDateTime, this.randomizeSection, true);
        this.currentVerbalAnalogueScore = Utilities.processDropdown(tnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.randomizeSection, true);
        this.verbalAnalogueScore = Utilities.processDropdown(tnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.randomizeSection, true);
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.randomizeSection, true, tnSatisfiedWithPainManagementYesButtonBy, tnSatisfiedWithPainManagementNoButtonBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.randomizeSection, true);
            }
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // in Gold the comment is required.  Not sure about demo
            // Not calling processRadiosByButton or processRadiosByLabel, because doesn't work for Transfer Note prob because duplicate ID's for labels
            // and the javascript value for "for" is not unique.  Rolling my own here, assuming it's a required choice:
            if (this.satisfiedWithPainManagement == null || this.satisfiedWithPainManagement.isEmpty() || this.satisfiedWithPainManagement.equalsIgnoreCase("random")) {
                if (Utilities.random.nextBoolean()) {
                    this.satisfiedWithPainManagement = "Yes";
                }
                else {
                    this.satisfiedWithPainManagement = "No";
                    //Driver.driver.findElement(satisfiedNoButtonBy).click();
                }
            }
            try {
                if (this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                    WebElement yesButton = Utilities.waitForPresence(satisfiedYesButtonBy, 3, "TransferNote.process()");
                    yesButton.click();
                } else {
                    WebElement noButton = Utilities.waitForPresence(satisfiedNoButtonBy, 3, "TransferNote.process()");
                    noButton.click(); // this will trigger a change to the page.  Watch it.
                }
            }
            catch (Exception e) {
                logger.severe("Couldn't click on radio button for Satisfied with Pain Management. e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }


            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.randomizeSection, true);
            }
        }

        this.painManagementPlan = Utilities.processText(tnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.randomizeSection, true);
        this.commentsNotesComplications = Utilities.processText(tnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.randomizeSection, false);

        // Destination facility requires some typing of at least 3 characters, and then there's a db lookup,
        // and then you choose one from the list if there is one
        logger.fine("Here comes PainManagementNoteSection TN_DESTINATION_FACILITY_FIELD");
        this.destinationFacility = Utilities.processText(tnDestinationFacilityFieldBy, this.destinationFacility, Utilities.TextFieldType.JPTA, this.randomizeSection, true);

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }
        //
        // Save the note.
        //
        Instant start = null;
        try {
            WebElement createNoteButton = Utilities.waitForRefreshedClickability(tnCreateNoteButton, 30, "TransferNote.() create note button"); // was 3s
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "TransferNote");
            }
            start = Instant.now();


            // This next line is now triggering a "You Have Encountered a Problem" page!!!!!!
            if (!this.skipSave) {
                createNoteButton.click();
            }

            Utilities.sleep(1555, "TransferNote.process(), clicked on createNoteButton, but takes a while");

            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // Hmmm, not removing 4/18/19. FailursIfStop: 0 Removed 5/8/19, and added 1 sec to the sleep above.
        }
        catch (Exception e) {
            logger.fine("TransferNote.process(), Could not get the create note button, or click on it.");
            return false;
        }
        //
        // Get confirmation of note saved.  Check logic.  Prob don't need both waits below.
        //
        WebElement messageAreaElement;
        try {
            Utilities.sleep(1555, "TransferNote"); // seems nec.  Was 555
            messageAreaElement = Utilities.waitForRefreshedVisibility(messageAreaBy, 10, "TransferNote.() message area");
        }
        catch (Exception e) {
                logger.severe("TransferNote.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false; // fails:
        }
        String message;
        try {
            message = messageAreaElement.getText();
        }
        catch (Exception e) {
            logger.severe("TransferNote.process(), exception caught trying to get message text: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails:
        }
        try {
            if (message.isEmpty()) {
                logger.finest("Wow, message is blank even though did refresh. so we'll wait for several seconds and try it again.");
                Utilities.sleep(8555, "TransferNote"); // some kind of wait seems nec
                messageAreaElement = Utilities.waitForVisibility(messageAreaBy, 10, "TransferNote.process()");
                message = messageAreaElement.getText();
            }
            if (message.contains("successfully created") || message.contains("sucessfully created")) {
                logger.finest("TransferNote.process(), message indicates good results: " + message);
            }
            else {
                if (!Arguments.quiet) System.err.println("      ***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " message: " + message);
                return false;
            }
        }
        catch (StaleElementReferenceException e) {
            logger.severe("TransferNote.process(), Stale Element.  exception message: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        catch (TimeoutException e) {
            logger.severe("TransferNote.process(), timeout exception caught waiting for message.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails: 2, Problem Page:2
        }
        catch (Exception e) {
            logger.severe("TransferNote.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails: 2, Problem Page:2
        }
        // Need following?
        try {
                WebElement result = Utilities.waitForVisibility(messageAreaBy, 10, "TransferNote.process()");
                String someTextMaybe = result.getText();
                if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                    logger.fine("Transfer Note successfully saved.");
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
        }
        catch (Exception e) {
            logger.fine("ClinicalNote.process() Probably timed out waiting for message after save note attempt");
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved Transfer Note at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("Transfer Note saved in " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "TransferNote, requested sleep for page.");
        }
        return true;
    }
}
