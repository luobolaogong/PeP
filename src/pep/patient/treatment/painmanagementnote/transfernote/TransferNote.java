package pep.patient.treatment.painmanagementnote.transfernote;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;

public class TransferNote extends AbstractTransferNote {
    private static Logger logger = Logger.getLogger(TransferNote.class.getName()); // multiple?
    public Boolean random;
    public String transferNoteDateTime; // "mm/dd/yyyy hhmm z, required";
    public String adjunctMedications;
    public String currentVerbalAnalogueScore;
    public String verbalAnalogueScore;
    //public String satisfiedWithPainManagement = "Yes";
    public String satisfiedWithPainManagement = "";
    public String commentsPainManagement;
    public String painManagementPlan;
    public String commentsNotesComplications;
    public String destinationFacility;

    private static By transferNoteTabBy = By.xpath("//*[@id=\"transferNoteTab\"]/a");
    private static By transferSectionBy = By.id("transferPainNoteForm");//*[@id="transferPainNoteForm"]
    private static  By tnSatisfiedWithPainManagementYesBy = By.id("satisfiedInd3");
    private static By tnSatisfiedWithPainManagementNoBy = By.id("satisfiedInd4");
    private static By tnSatisfiedWithPainManagementCommentsTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"satisfiedComments\"]");
    private static By tnPainManagementPlanTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"painPlan\"]");
    private static By tnCommentsTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"comments\"]");
    private static By tnDestinationFacilityFieldBy = By.id("destinationMtfIdDesc");
    private static By tnCreateNoteButton = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[13]/td[2]/button[1]");
    private static By tnTransferNoteDateTimeFieldBy = By.id("transferPainNoteFormplacementDate");
    private static By tnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::select[@id=\"currentVas\"]");
    private static By tnVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::select[@id=\"vas\"]");
    private static By messageAreaBy = By.id("pain-note-message");



    public TransferNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.transferNoteDateTime = "";
            this.adjunctMedications = "";
            this.currentVerbalAnalogueScore = "";
            this.verbalAnalogueScore = "";
            this.satisfiedWithPainManagement = "Yes";
            this.commentsPainManagement = "";
            this.painManagementPlan = "";
            this.commentsNotesComplications = "";
            this.destinationFacility = "";
        }
        if (isDemoTier) {
            transferNoteTabBy = TRANSFER_NOTE_TAB;
            transferSectionBy = By.id("painNoteForm:Transfer");
            tnSatisfiedWithPainManagementYesBy = TN_SATISFIED_WITH_PAIN_MANAGEMENT_YES_RADIO_LABEL;
            tnSatisfiedWithPainManagementNoBy = TN_SATISFIED_WITH_PAIN_MANAGEMENT_NO_RADIO_LABEL;
            tnSatisfiedWithPainManagementCommentsTextAreaBy = TN_SATISFIED_WITH_PAIN_MANAGEMENT_COMMENTS_TEXTAREA;
            tnPainManagementPlanTextAreaBy = TN_PAIN_MANAGEMENT_PLAN_TEXTAREA;
            tnCommentsTextAreaBy = TN_PAIN_MANAGEMENT_PLAN_TEXTAREA;
            tnDestinationFacilityFieldBy =TN_DESTINATION_FACILITY_FIELD;
            tnCreateNoteButton = TN_CREATE_NOTE_BUTTON;
            tnTransferNoteDateTimeFieldBy = TN_TRANSFER_NOTE_DATE_TIME_FIELD;
            tnCurrentVerbalAnalogueScoreDropdownBy = TN_CURRENT_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            tnVerbalAnalogueScoreDropdownBy = TN_VERBAL_ANALOGUE_SCORE_DROPDOWN;
            messageAreaBy = By.xpath("//*[@id=\"painNoteForm:j_id1200\"]/table/tbody/tr/td/span");
        }
    }

    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Transfer Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        try {
            WebElement transferNoteTab = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.elementToBeClickable(transferNoteTabBy));
            transferNoteTab.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this work?
        }
        catch (Exception e) {
            logger.fine("TransferNote.process(), couldn't get tab, and/or couldn't click on it.: " + e.getMessage());
            return false;
        }
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(transferSectionBy));
        }
        catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            return false; // fails: 1
        }

        if (Arguments.date != null && (this.transferNoteDateTime == null || this.transferNoteDateTime.isEmpty())) {
            this.transferNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.transferNoteDateTime = Utilities.processDateTime(tnTransferNoteDateTimeFieldBy, this.transferNoteDateTime, this.random, true);

        this.currentVerbalAnalogueScore = Utilities.processDropdown(tnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.random, true);

        this.verbalAnalogueScore = Utilities.processDropdown(tnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.random, true);

        if (isDemoTier) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
            this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
        }
        else if (isGoldTier) { // in Gold the comment is required.  Not sure about demo
            this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
            if (!this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
            }
        }

        this.painManagementPlan = Utilities.processText(tnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.random, true);

        this.commentsNotesComplications = Utilities.processText(tnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);


        // Looks like this next one requires some typing of at least 3 characters, and then there's a db lookup, and then you choose one from the list if there is one
        // Do this one later
        logger.fine("Here comes PainManagementNoteSection TN_DESTINATION_FACILITY_FIELD");
        this.destinationFacility = Utilities.processText(tnDestinationFacilityFieldBy, this.destinationFacility, Utilities.TextFieldType.JPTA, this.random, true);

        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.elementToBeClickable(tnCreateNoteButton)); // was 3s
            createNoteButton.click(); // ajax?
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("TransferNote.process(), Could not get the create note button, or click on it.");
            return false;
        }

        // Fix the remainder of this method later.  I just want to get it to work for now.

        // copied from SPNB
        try {
            // Seems that the next two conditions really do not work.  There is no waiting.  So, adding a sleep
            Utilities.sleep(1555);
            ExpectedCondition<WebElement> messageAreaExpectedCondition = ExpectedConditions.presenceOfElementLocated(messageAreaBy);
            //ExpectedCondition<Boolean> messageAreaSaysSuccessfullyCreated = ExpectedConditions.textToBePresentInElementLocated(messageAreaBy, "Note successfully created!");
            try {
                WebElement textArea = (new WebDriverWait(Driver.driver, 10)).until(messageAreaExpectedCondition);
                String message = textArea.getText();
                if (message.contains("successfully")) {
                    //Boolean textIsPresent = (new WebDriverWait(Driver.driver, 10)).until(messageAreaSaysSuccessfullyCreated); // fails
                   // if (Arguments.debug)
                   //     System.out.println("Wow, so the expected text was there!: " + textArea.getText());
                    return true; // If this doesn't work, and there are timing issues with the above, then try the stuff below too.
                }
                else {
                    return false;
                }
            }
            catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }

            WebElement messageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
            String message = messageAreaElement.getText();
            if (message.contains("successfully created") || message.contains("sucessfully created")) {
                //logger.fine("TransferNote.process(), message indicates good results: " + message);
            }
            else {
                if (!Arguments.quiet) System.err.println("      ***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  ": " + message);
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.err.println("TransferNote.process(), exception caught waiting for message.: " + e.getMessage());
            return false;
        }

        // We don't need both of these above and below

        // I think the following is wrong.  I think not waiting long enough for messageAreaBy

        try {
                WebElement result = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
                String someTextMaybe = result.getText();
                if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                    logger.fine("Transfer Note successfully saved.");
                } else {
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " : " + someTextMaybe);
                    return false;
                }
        }
        catch (Exception e) {
            logger.fine("ClinicalNote.process() Probably timed out waiting for message after save note attempt");
            return false;
        }
        if (Arguments.pagePause > 0) {
            Utilities.sleep(Arguments.pagePause * 1000);
        }
        return true;
    }
}
