package pep.patient.treatment.painmanagementnote.transfernote;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

//public class TransferNote extends AbstractTransferNote {
public class TransferNote {
    private static Logger logger = Logger.getLogger(TransferNote.class.getName()); // multiple?
    public Boolean random;
    public Boolean shoot;
    public String transferNoteDateTime; // "mm/dd/yyyy hhmm z, required";
    public String adjunctMedications;
    public String currentVerbalAnalogueScore;
    public String verbalAnalogueScore;
    //public String satisfiedWithPainManagement = ""; // why did I do this?
    public String satisfiedWithPainManagement;
    public String commentsPainManagement;
    public String painManagementPlan;
    public String commentsNotesComplications;
    public String destinationFacility;

    private static By transferNoteTabBy = By.xpath("//*[@id=\"transferNoteTab\"]/a");
    private static By transferSectionBy = By.id("transferPainNoteForm");
    //private static By tnSatisfiedWithPainManagementYesButtonBy = By.id("satisfiedInd3"); // Does this keep changing?

    private static By tnSatisfiedWithPainManagementYesButtonBy = By.id("satisfiedInd1"); // no can do.  There are two of these
    //private static By tnSatisfiedWithPainManagementNoButtonBy = By.id("satisfiedInd4");
    private static By tnSatisfiedWithPainManagementNoButtonBy = By.id("satisfiedInd2");

    private static By tnSatisfiedWithPainManagementYesLabelBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[6]/td[2]/label[1]");
    private static By tnSatisfiedWithPainManagementNoLabelBy =  By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[6]/td[2]/label[2]");

    private static By tnSatisfiedWithPainManagementCommentsTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"satisfiedComments\"]");
    private static By tnPainManagementPlanTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"painPlan\"]");
    private static By tnCommentsTextAreaBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::textarea[@id=\"comments\"]");
    private static By tnDestinationFacilityFieldBy = By.id("destinationMtfIdDesc");
    //private static By tnCreateNoteButton = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[13]/td[2]/button[1]");
    private static By tnCreateNoteButton = By.xpath("//*[@id=\"transferPainNoteForm\"]//button[text()='Create Note']");
    private static By tnTransferNoteDateTimeFieldBy = By.id("transferPainNoteFormplacementDate");
    private static By tnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::select[@id=\"currentVas\"]");
    private static By tnVerbalAnalogueScoreDropdownBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/descendant::select[@id=\"vas\"]");
    private static By messageAreaBy = By.id("pain-note-message"); // this should work but never does?????



    public TransferNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.transferNoteDateTime = "";
            this.adjunctMedications = "";
            this.currentVerbalAnalogueScore = "";
            this.verbalAnalogueScore = "";
            this.satisfiedWithPainManagement = ""; // was "Yes"
            this.commentsPainManagement = "";
            this.painManagementPlan = "";
            this.commentsNotesComplications = "";
            this.destinationFacility = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            transferNoteTabBy = By.xpath("//td[@id='painNoteForm:Transfer_lbl']");
            transferSectionBy = By.id("painNoteForm:Transfer");
            // The following all come from AbstractTransferNote, and there are probably better ways
            tnSatisfiedWithPainManagementYesButtonBy = By.xpath("//*[@id=\"painNoteForm:transferDecorate:satisfiedIndDecorate:satisfiedInd\"]/tbody/tr/td[1]/label");;
            tnSatisfiedWithPainManagementNoButtonBy = By.xpath("//*[@id=\"painNoteForm:transferDecorate:satisfiedIndDecorate:satisfiedInd\"]/tbody/tr/td[2]/label");;
            tnSatisfiedWithPainManagementCommentsTextAreaBy = By.xpath("//label[.='Comments:']/../following-sibling::td/textarea");
            tnPainManagementPlanTextAreaBy = By.xpath("//textarea[@id='painNoteForm:transferDecorate:painPlanDecorate:painPlan']");
            tnCommentsTextAreaBy = By.xpath("//textarea[@id='painNoteForm:transferDecorate:painPlanDecorate:painPlan']");;
            tnDestinationFacilityFieldBy = By.xpath("//input[@id='painNoteForm:transferDecorate:destinationFacility:facilityText']");;
            tnCreateNoteButton = By.xpath("//input[@id='painNoteForm:transferDecorate:createNote']");;
            tnTransferNoteDateTimeFieldBy = By.xpath("//input[@id='painNoteForm:transferDecorate:discontinueDateDecorate:placementDateInputDate']");;
            tnCurrentVerbalAnalogueScoreDropdownBy = By.xpath("//select[@id='painNoteForm:transferDecorate:currentVasDecorate:currentVas']");;
            tnVerbalAnalogueScoreDropdownBy = By.xpath("//select[@id='painNoteForm:transferDecorate:vasDecorate:vas']");;
            messageAreaBy = By.xpath("//*[@id=\"painNoteForm:j_id1200\"]/table/tbody/tr/td/span");
        }
    }

    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Transfer Note for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        try { // sometimes next line needs a bit more time?
            WebElement transferNoteTab = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(transferNoteTabBy));
            transferNoteTab.click();
            (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax()); // does this work?
        }
        catch (Exception e) {
            logger.severe("TransferNote.process(), couldn't get tab, and/or couldn't click on it.: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(transferSectionBy));
        }
        catch (Exception e) {
            logger.severe("Exception caught: " + Utilities.getMessageFirstLine(e));
            return false; // fails: 1
        }

        if (Arguments.date != null && (this.transferNoteDateTime == null || this.transferNoteDateTime.isEmpty())) {
            this.transferNoteDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.transferNoteDateTime = Utilities.processDateTime(tnTransferNoteDateTimeFieldBy, this.transferNoteDateTime, this.random, true);

        this.currentVerbalAnalogueScore = Utilities.processDropdown(tnCurrentVerbalAnalogueScoreDropdownBy, this.currentVerbalAnalogueScore, this.random, true);

        this.verbalAnalogueScore = Utilities.processDropdown(tnVerbalAnalogueScoreDropdownBy, this.verbalAnalogueScore, this.random, true);

        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesButtonBy, tnSatisfiedWithPainManagementNoButtonBy);
            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
            }
        }
        else if (codeBranch != null && codeBranch.equalsIgnoreCase("Spring")) { // in Gold the comment is required.  Not sure about demo

            // The following, I think, no longer works on Gold:
            //            this.satisfiedWithPainManagement = Utilities.processRadiosByButton(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesButtonBy, tnSatisfiedWithPainManagementNoButtonBy);
            //            //this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesLabelBy, tnSatisfiedWithPainManagementNoLabelBy);
            //            //this.satisfiedWithPainManagement = Utilities.processRadiosByLabel(this.satisfiedWithPainManagement, this.random, true, tnSatisfiedWithPainManagementYesLabelBy, tnSatisfiedWithPainManagementNoLabelBy);
            // Not calling processRadiosByButton or processRadiosByLabel, because doesn't work for Transfer Note
            // Rolling my own here, assuming it's a required choice:
            //By satisfiedYesButtonBy = By.xpath("//*[@id=\"satisfiedInd1\"][1]"); // can't make this work
            By satisfiedYesButtonBy = By.xpath("//form[@id='transferPainNoteForm']/descendant::*[@id='satisfiedInd1']");
            //By satisfiedNoButtonBy = By.xpath("//*[@id=\"satisfiedInd2\"][1]");
            By satisfiedNoButtonBy = By.xpath("//form[@id='transferPainNoteForm']/descendant::*[@id='satisfiedInd2']");
            By satisfiedYesLabelBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[6]/td[2]/label[1]");
            By satisfiedNoLabelBy = By.xpath("//*[@id=\"transferPainNoteForm\"]/div/table/tbody/tr[6]/td[2]/label[2]");
            if (this.satisfiedWithPainManagement == null || this.satisfiedWithPainManagement.isEmpty() || this.satisfiedWithPainManagement.equalsIgnoreCase("random")) {
                if (Utilities.random.nextBoolean()) {
                    this.satisfiedWithPainManagement = "Yes";
                    //Driver.driver.findElement(satisfiedYesButtonBy).click();
                }
                else {
                    this.satisfiedWithPainManagement = "No";
                    //Driver.driver.findElement(satisfiedNoButtonBy).click();
                }
            }
            try {
                if (this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                    //WebElement yesButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(satisfiedYesButtonBy));
                    WebElement yesButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(satisfiedYesButtonBy));
                    //WebElement yesButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(satisfiedYesLabelBy));
                    //WebElement yesButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(satisfiedYesLabelBy));
                    yesButton.click();
                } else {
                    //WebElement noButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(satisfiedNoButtonBy));
                    WebElement noButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(satisfiedNoButtonBy));
                    //WebElement noButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(satisfiedNoLabelBy));
                    //WebElement noButton = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.visibilityOfElementLocated(satisfiedNoLabelBy));
                    noButton.click();
                }
            }
            catch (Exception e) {
                logger.severe("Couldn't click on radio button for Satisfied with Pain Management. e: " + Utilities.getMessageFirstLine(e));
                return false;
            }

            if (this.satisfiedWithPainManagement != null && !this.satisfiedWithPainManagement.equalsIgnoreCase("Yes")) {
                this.commentsPainManagement = Utilities.processText(tnSatisfiedWithPainManagementCommentsTextAreaBy, this.commentsPainManagement, Utilities.TextFieldType.PAIN_MGT_COMMENT_DISSATISFIED, this.random, true);
            }
        }

        this.painManagementPlan = Utilities.processText(tnPainManagementPlanTextAreaBy, this.painManagementPlan, Utilities.TextFieldType.PAIN_MGT_PLAN, this.random, true);

        this.commentsNotesComplications = Utilities.processText(tnCommentsTextAreaBy, this.commentsNotesComplications, Utilities.TextFieldType.COMMENTS_NOTES_COMPLICATIONS, this.random, false);


        // Looks like this next one requires some typing of at least 3 characters, and then there's a db lookup, and then you choose one from the list if there is one
        // Do this one later
        logger.fine("Here comes PainManagementNoteSection TN_DESTINATION_FACILITY_FIELD");
        this.destinationFacility = Utilities.processText(tnDestinationFacilityFieldBy, this.destinationFacility, Utilities.TextFieldType.JPTA, this.random, true);

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        Instant start = null;
        try {
            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.elementToBeClickable(tnCreateNoteButton)); // was 3s
            start = Instant.now();

            createNoteButton.click(); // I think this can take a while
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        }
        catch (Exception e) {
            logger.fine("TransferNote.process(), Could not get the create note button, or click on it.");
            return false;
        }

        // Fix the remainder of this method later.  I just want to get it to work for now.
        // On gold it appears there is no message for success, and the same form is displayed.
        // copied from SPNB
        // This really needs to be examined, because it fails too often
        try {
            // I don't know how to make this wait long enough, but it does seem like a timing issue, so sleep
            Utilities.sleep(555); // seems nec
            //WebElement messageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
            WebElement messageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageAreaBy)));
            String message = messageAreaElement.getText();
            if (message.isEmpty()) {
                logger.finest("Wow, message is blank even though did refresh. so we'll wait for several seconds and try it again.");
                Utilities.sleep(8555); // some kind of wait seems nec
                messageAreaElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(messageAreaBy));
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
            logger.severe("TransferNote.process(), Stale Element.  exception message: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        catch (Exception e) {
            logger.severe("TransferNote.process(), exception caught waiting for message.: " + Utilities.getMessageFirstLine(e));
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
                        System.err.println("      ***Failed to save Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " message: " + someTextMaybe);
                    return false;
                }
        }
        catch (Exception e) {
            logger.fine("ClinicalNote.process() Probably timed out waiting for message after save note attempt");
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved Transfer Note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.info("Transfer Note save for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }
}
