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

import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;

public class TransferNote extends AbstractTransferNote { // multiple?
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
        if (!Arguments.quiet) System.out.println("      Processing Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        try {
            WebElement transferNoteTab = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.elementToBeClickable(transferNoteTabBy));
            transferNoteTab.click();
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this work?
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TransferNote.process(), couldn't get tab, and/or couldn't click on it.: " + e.getMessage());
            return false;
        }
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(transferSectionBy));
        }
        catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            return false; // fails: 1
        }

//        //Utilities.automationUtils.waitUntilElementIsVisible(By.xpath("//*[@id=\"painNoteForm:Transfer_lbl\"]")); // not sure this xpath indicates the tab has been selected
//        By painNoteFormTransferLblBy = By.id("painNoteForm:Transfer_lbl");
//        (new WebDriverWait(Driver.driver,1)).until(ExpectedConditions.visibilityOfElementLocated(painNoteFormTransferLblBy));



        if (Arguments.date != null && (this.transferNoteDateTime == null || this.transferNoteDateTime.isEmpty())) {
            this.transferNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
//        this.transferNoteDateTime = Utilities.processText(TN_TRANSFER_NOTE_DATE_TIME_FIELD, this.transferNoteDateTime, Utilities.TextFieldType.DATE_TIME, this.random, true);
        this.transferNoteDateTime = Utilities.processDateTime(tnTransferNoteDateTimeFieldBy, this.transferNoteDateTime, this.random, true);

        this.currentVerbalAnalogueScore = Utilities.processDropdown(tnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.random, true);

        this.verbalAnalogueScore = Utilities.processDropdown(tnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.random, true);

//        if (isDemoTier) {
//            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
//            if (this.satisfiedWithPainManagement.startsWith("No")) {
//                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
//            }
//        }
//        else if (isGoldTier) { // in Gold the comment is required.  Not sure about demo
//            this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
//            this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
//        }
        // Wow, comments are required whether satisfied with pain management or not.  When did this happen?
        // Also, for now there are two sections because of the radio button difference in structure between demo and gold
        if (isDemoTier) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
            this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
        }
        else if (isGoldTier) { // in Gold the comment is required.  Not sure about demo
            this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesBy, tnSatisfiedWithPainManagementNoBy);
            this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
        }

        // watch comments/text fields here.  In right order?
        this.painManagementPlan = Utilities.processText(tnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.random, true);

        this.commentsNotesComplications = Utilities.processText(tnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);


        // Looks like this next one requires some typing of at least 3 characters, and then there's a db lookup, and then you choose one from the list if there is one
        // Do this one later
        if (Arguments.debug) System.out.println("Here comes PainManagementNoteSection TN_DESTINATION_FACILITY_FIELD");
        this.destinationFacility = Utilities.processText(tnDestinationFacilityFieldBy, this.destinationFacility, Utilities.TextFieldType.JPTA, this.random, true);
        // I have no idea if this spinning thing is normal or not.  Probably if you don't satisfy it, you can't submit this transfer note
        //if (Arguments.debug) System.out.println("In Pain Management, Transfer Note, and don't know if the spinning facility field is normal or not.  I think okay.");

        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.elementToBeClickable(tnCreateNoteButton)); // was 3s
            //Utilities.clickButton(tnCreateNoteButton); // ajax?
            createNoteButton.click(); // ajax?
            //System.out.println("Here comes an isFinishedAjax in TransferNote.process()");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
            //System.out.println("back from calling isFinishedAjax");

        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TransferNote.process(), Could not get the create note button, or click on it.");
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
                if (Arguments.debug) System.out.println("TransferNote.process(), message indicates good results: " + message);
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  ": " + message);
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
//            if (isDemoTier) {
            // maybe next line needs to wait a bit.
                WebElement result = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
                String someTextMaybe = result.getText();
                if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                    if (Arguments.debug) System.out.println("Transfer Note successfully saved.");
                } else {
                    if (!Arguments.quiet)
                        System.err.println("***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " : " + someTextMaybe);
                    return false;
                }
//            }
//            else {
//                System.out.println("Looks like there's no message area for Clinical Note on Gold.  So, how do we assume success?");
//   //              Could check to see if the Clinical Note area is still visible.  Why not?  Isn't put into //*[@id="pain-note-message"]     ?
//     //            By the way, Pain Management Notes section does not show a DATE value for clinical notes.  Looks like a bug.
//                (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(transferSectionBy)); // maybe works
//            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("ClinicalNote.process() Probably timed out waiting for message after save note attempt");
            return false;
        }





//
//        // Check for confirmation of note saved
//
//        try {
//            result = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
//            String someTextMaybe = result.getText();
//            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
//                if (Arguments.debug) System.out.println("Great, did a transfer note.  We can go on.");
//            }
//            else {
//                if (Arguments.debug) System.out.println("Ooops, couldn't save this thing.");
//                return false;
//            }
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("TransferNote.process(), Couldn't get message area element.  Expected message of success or something else.");
//            return false;
//        }

        return true;
    }

}
