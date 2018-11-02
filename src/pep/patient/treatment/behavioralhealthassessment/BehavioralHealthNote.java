package pep.patient.treatment.behavioralhealthassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;

//class BehavioralHealthNote extends AbstractBehavioralHealthNote {
class BehavioralHealthNote {
    public Boolean random; // true if want this section to be generated randomly
    public String note; // "you can either do this, or have a Note Template with the following";

    public String service; // "free text";
    public String attendingStaff; // "free text";
    public String workingDiagnoses; // "free text";
    public String careRenderedSinceLastUpdate; // "free text";
    public String adlUpdate; // "free text";
    public String prognosis; // "free text";
    public String estimatedDischargeDate; // "MM/DD/YYYY";
    public String date; // "MM/DD/YYYY";
    public String disposition; // "free text";
    public String needsAndRequirements; // "free text";
    public String bhNoteType; // "option 1-2";

    private static By BH_NOTES_TYPE_DROPDOWN = By.xpath("//td[.='BH Note Type:']/../../../following-sibling::select");
    private static By BH_POPUP_SAVE_NOTE = By.xpath("//input[@value='Save Note']");

    private static By createNoteLinkBy = By.xpath("//*[@id=\"bhNotesContainer\"]/div[3]/a"); // verified

    private static By notesTextAreaBy = By.id("defaultNoteText");
    private static By bhNotesTypeDropdownBy = By.id("defaultNoteTypeId"); // verified
    private static By bhNotesTypeDropdownForTemplateBy = By.id("templateNoteTypeId");
    private static By bhPopupSaveNoteBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/div/button");
    private static By bhPopupSaveNoteForTemplateBy = By.xpath("//*[@id=\"noteTemplateContainer\"]/div/button");
    private static By bhaBhnSuccessMessageAreaBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[3]");


    public BehavioralHealthNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.note = "";
            this.service = "";
            this.attendingStaff = "";
            this.workingDiagnoses = "";
            this.careRenderedSinceLastUpdate = "";
            this.adlUpdate = "";
            this.prognosis = "";
            this.estimatedDischargeDate = "";
            this.date = "";
            this.disposition = "";
            this.needsAndRequirements = "";
            this.bhNoteType = "";
        }
        if (isDemoTier) {
            createNoteLinkBy = By.id("bhAssessmentForm:j_id451");
            notesTextAreaBy = By.id("createNoteForm:noteTextDecorator:noteTextInput");
            bhNotesTypeDropdownBy =  BH_NOTES_TYPE_DROPDOWN;
            bhPopupSaveNoteBy = BH_POPUP_SAVE_NOTE;
            bhaBhnSuccessMessageAreaBy = By.xpath("//*[@id=\"bhAssessmentForm:j_id435\"]/table/tbody/tr/td/span");

        }
    }

    // This method seems a bit off.  Check logic, and compare against similar.
    public boolean process(Patient patient, BehavioralHealthAssessment behavioralHealthAssessment) {
        if (!Arguments.quiet) System.out.println("      Processing Behavioral Health Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");

        try {
            WebElement createNoteLinkElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(createNoteLinkBy)); // was 5
            createNoteLinkElement.click(); // ajax?
            boolean whatever = (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax());
            if (!whatever) {
                System.out.println("whatever is " + whatever);
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), couldn't get link, and/or couldn't click on it.: " + e.getMessage());
            return false; // failed with timeout on gold: 1
        }

        // At this point we should have a modal window popped up, and it says "Behavioral Health Note" in the title.
        // It contains a text area, a dropdown, a Save Note button, and above all that there's a link "Use Note Template".


        if (!((this.service == null || this.service.isEmpty()) &&
                (this.attendingStaff == null || this.attendingStaff.isEmpty()) &&
                (this.workingDiagnoses == null || this.workingDiagnoses.isEmpty()) &&
                (this.careRenderedSinceLastUpdate == null || this.careRenderedSinceLastUpdate.isEmpty()) &&
                (this.adlUpdate == null || this.adlUpdate.isEmpty()) &&
                (this.prognosis == null || this.prognosis.isEmpty()) &&
                (this.estimatedDischargeDate == null || this.estimatedDischargeDate.isEmpty()) &&
                (this.date == null || this.date.isEmpty()) &&
                (this.disposition == null || this.disposition.isEmpty()) &&
                (this.needsAndRequirements == null || this.needsAndRequirements.isEmpty()))) {
            try {
                By useNoteTemplateLinkBy = By.xpath("//*[@id=\"defaultTemplateContainer\"]/span[2]/a");
                WebElement useNoteTemplateLink = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.elementToBeClickable(useNoteTemplateLinkBy));
                useNoteTemplateLink.click();
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), couldn't get Use Note Template link, or couldn't click on it.");
                return false;
            }

            try {
                By serviceBy = By.id("service");
                this.service = Utilities.processText(serviceBy, this.service, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                By attendingStaffBy = By.id("attendingStaff");
                this.attendingStaff = Utilities.processText(attendingStaffBy, this.attendingStaff, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                By workingDiagnosesBy = By.id("workingDiagnosis");
                this.workingDiagnoses = Utilities.processText(workingDiagnosesBy, this.workingDiagnoses, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                //By careRenderedSinceLastUpdateBy = By.id("careRenderedSinceLastUpdate");
                By careRenderedSinceLastUpdateBy = By.id("careRendered");
                this.careRenderedSinceLastUpdate = Utilities.processText(careRenderedSinceLastUpdateBy, this.careRenderedSinceLastUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                By adlUpdateBy = By.id("adlUpdated");
                this.adlUpdate = Utilities.processText(adlUpdateBy, this.adlUpdate, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                By prognosisBy = By.id("prognosis");
                this.prognosis = Utilities.processText(prognosisBy, this.prognosis, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
               // By estimatedDischargeDateBy = By.id("estimatedDischargeDate");
                By estimatedDischargeDateBy = By.id("estimatedDCString");
                this.estimatedDischargeDate = Utilities.processText(estimatedDischargeDateBy, this.estimatedDischargeDate, Utilities.TextFieldType.DATE, this.random, false);
                By dateBy = By.id("date");
                this.date = Utilities.processText(dateBy, this.date, Utilities.TextFieldType.DATE, this.random, false);
                By dispositionBy = By.id("disposition");
                this.disposition = Utilities.processText(dispositionBy, this.disposition, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
                By needsAndRequirementsBy = By.id("needsAndRequirements");
                this.needsAndRequirements = Utilities.processText(needsAndRequirementsBy, this.needsAndRequirements, Utilities.TextFieldType.SHORT_PARAGRAPH, this.random, false);
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), couldn't find or fill in some element: " + e.getMessage());
                return false;
            }
            bhNotesTypeDropdownBy = bhNotesTypeDropdownForTemplateBy;
            bhPopupSaveNoteBy = bhPopupSaveNoteForTemplateBy;
        }
        else {

            // AT THIS POINT WE'RE NOT SUPPORTING THE USE/SELECTION OF THAT LINK.
            // THIS SECTION IS IF YOU USE THE DEFAULT TEMPLATE  TODO: do the Note Template options

            try {
                (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(notesTextAreaBy));
                this.note = Utilities.processText(notesTextAreaBy, this.note, Utilities.TextFieldType.BH_NOTE, this.random, true);
            } catch (Exception e) {
                if (Arguments.debug)
                    System.out.println("BehavioralHealthNote.process(), wow, didn't find the text area.  Unlikely but it happens.");
                return false;
            }
        }
        this.bhNoteType = Utilities.processDropdown(bhNotesTypeDropdownBy, this.bhNoteType, this.random, true);

        // IF DO NOTE TEMPLATE DO IT HERE INSTEAD OF STUFF ABOVE

        WebElement popupSaveNoteElement;
        try {
            popupSaveNoteElement = (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.elementToBeClickable(bhPopupSaveNoteBy));
            popupSaveNoteElement.click(); //Does not cause AJAX.  Really?
            boolean whatever = (new WebDriverWait(Driver.driver, 8)).until(Utilities.isFinishedAjax()); // new
            if (!whatever) {
                System.out.println("whatever is " + whatever);
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), couldn't get Save Note button, or couldn't click on it: " + e.getMessage());
            return false;
        }

        // Check for success message after trying to save this Behavioral Health Note.
        // If successful, the modal window goes away and we're back to the Behavioral Health Assessments page, and there should
        // be a green message saying "Note saved successfully!".  But if the modal window failed, then there will be a message
        // there.  So, unless we get back to that page, there was an error and it wasn't saved, and we might as well just return false;
        //By behavioralHealthNoteMessageAreaBy = By.xpath("//*[@id=\"createNoteForm:noteTextDecorator:validInput\"]/table/tbody/tr/td/span");

        // Hey this seems to work for the popup window, and now don't have to wait 2555ms.  Try with other popups?  Like BH?
        if (Arguments.debug) System.out.println("Waiting for staleness of popup.");
        (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.stalenessOf(popupSaveNoteElement));
        if (Arguments.debug) System.out.println("Done waiting");


        try {
            //Utilities.sleep(3555); // Was 2555.  Seems there's no way to get around the need for a pause before we check for a message.  The AJAX thing does not work.
            //WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy));
            // next line new 10/19/18  refreshed
            WebElement someElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(bhaBhnSuccessMessageAreaBy)));
            String someTextMaybe = someElement.getText();
            if (someTextMaybe.contains("successfully")) {
                if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), saved note successfully.");
            }
            else if (someTextMaybe.contains("No records found for patient")) {
                if (!Arguments.quiet) System.out.println("***Could not save Behavioral Health Note.  Message: " + someTextMaybe);
                return false;
            }
            else {
                if (!Arguments.quiet) System.err.println("      ***Failed to save behavioral health note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " : " + someTextMaybe);
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("BehavioralHealthNote.process(), Didn't find message after save attempt: " + e.getMessage());
            return false;
        }
        if (Arguments.pagePause > 0) {
            Utilities.sleep(Arguments.pagePause * 1000);
        }
        return true;
    }
}
