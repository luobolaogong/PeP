package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.PatientState;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;
import pep.utilities.lorem.LoremIpsum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;

// I don't know for sure, but it seems that each encounter, which is not a followup, has to have a new InjuryIllness section
//
// This represents a section in a Patient Registration page, which contains several sections, some of which are variable
// depending on the level.  The sections are generally divided by horizontal purplish lines.  However, I think it's more
// trouble than it's worth to divide InjuryIllness into subclasses based on these sections.
//
public class InjuryIllness {
    private static Logger logger = Logger.getLogger(InjuryIllness.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String operation;
    public String injuryNature;
    public String medicalService;
    public String mechanismOfInjury;
    public String patientCondition;
    public String diagnosisCodeSet;
    public String primaryDiagnosis;
    public String assessment;
    public List<String> additionalDiagnoses = new ArrayList<>(); // allocate here?  Is it done when input files loaded?  And what about random?

    public String cptCodeSearch;
    public String cptCode;
    public String procedureCodes;

    public Boolean receivedTransfusion;
    public Boolean transfusedWithUnlicensedBlood;

    public String admissionNote;
    public Boolean amputation;
    public Boolean headTrauma;
    public Boolean burns;
    public Boolean postTraumaticStressDisorder;
    public Boolean eyeTrauma;
    public Boolean spinalCordInjury;

    public String amputationCause;

    private static By II_MEDICAL_SERVICE_DROPDOWN = By
            .xpath("//select[@name='patientRegistration.medicalService']");

    // Injury/Illness Checkboxes
    private static By II_AMPUTATION_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare1'])");
    private static By II_BURNS_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare2'])");
    private static By II_EYE_TRAUMA_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare3'])");
    private static By II_HEAD_TRAUMA_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare4'])");
    private static By II_PTSD_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare5'])");
    private static By II_SPINAL_CORD_INJURY_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare6'])");

    private static By II_EXPLOSION_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[1]/label");
    private static By II_GSW_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[2]/label");
    private static By II_GRENADE_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[3]/label");
    private static By II_LAND_MINE_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[4]/label");
    private static By II_MVA_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[5]/label");
    private static By II_OTHER_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[6]/label");
    private static By injuryIllnessOperationDropdownBy = By.id("patientRegistration.operation");
    private static By injuryNatureDropdownBy = By.id("patientRegistration.injuryNature");
    private static By mechanismOfInjuryBy = By.id("patientRegistration.mechOfInjury");
    private static By patientConditionBy = By.id("patientRegistration.patientCondition");
    private static By diagnosisCodeSetDropdownBy = By.id("patientRegistration.codeType");

    // these next 3 are the same for demo tier
    private static By showAdditionalDiagnosesButtonBy = By.xpath("//*[@id=\"showAdditional\"]/tr/td/input");
    private static By additionalDiagnosisFieldBy = By.id("addtDiagnosisSearch");
    private static By additionalDiagnosisDropdownBy = By.id("patAddtDiagnoses");


    private static By primaryDiagnosisFieldBy = By.id("diagnosisSearch");
    private static By primaryDiagnosisDropdownBy = By.id("patientRegistration.diagnosis");
    private static By assessmentTextBoxBy = By.id("patientRegistration.assessment");
    private static By cptProcedureCodesTextBoxBy = By.id("cptCodesTextlist");
    private static By receivedTransfusionCheckBoxBy = By.id("patientRegistration.hasBloodTransfusion1");
    private static By admissionNoteLabelBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[4]/tbody/tr/td/table[7]/tbody/tr[2]/td/h4");
    private static By admissionNoteBy = By.id("patientRegistration.notes");
    private static By optionOfDiagnosisDropdown = By.xpath("//*[@id=\"patientRegistration.diagnosis\"]/option");


    public InjuryIllness() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.operation = "";
            this.injuryNature = "";
            this.medicalService = "";
            this.mechanismOfInjury = "";
            this.patientCondition = "";
            //this.acceptingPhysician = "";
            this.diagnosisCodeSet = "";
            this.primaryDiagnosis = "";
            this.assessment = "";
            //this.additionalDiagnoses = Arrays.asList(new String()); // causes the template to be "additionalDiagnoses": [""]
            //this.additionalDiagnoses = Arrays.asList(); // causes the template to be "additionalDiagnoses": []
            this.additionalDiagnoses = Collections.emptyList(); // causes the template to be "additionalDiagnoses": []
            this.cptCodeSearch = "";
            this.cptCode = "";
            this.procedureCodes = "";
            this.receivedTransfusion = false;
            this.transfusedWithUnlicensedBlood = false;
            this.admissionNote = "";
            this.amputation = false;
            this.headTrauma = false;
            this.burns = false;
            this.postTraumaticStressDisorder = false;
            this.eyeTrauma = false;
            this.spinalCordInjury = false;
            this.amputationCause = "";
        }
    }

    // This method is too long.  Break it out.
    // This method is too long.  Break it out.
    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
                if (!Arguments.quiet) System.out.println("    Processing Injury/Illness ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Injury/Illness for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        }

        InjuryIllness injuryIllness = null;
        if (patient.patientState == PatientState.PRE && patient.registration.preRegistration != null && patient.registration.preRegistration.injuryIllness != null) {
            injuryIllness = patient.registration.preRegistration.injuryIllness;
        }
        else if (patient.patientState == PatientState.NEW && patient.registration.newPatientReg != null && patient.registration.newPatientReg.injuryIllness != null) {
            injuryIllness = patient.registration.newPatientReg.injuryIllness;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.registration.updatePatient != null && patient.registration.updatePatient.injuryIllness != null) {
            injuryIllness = patient.registration.updatePatient.injuryIllness;
        }
// hey, if no value is provided for a mandatory field, should the value be supplied, or an error raised, or just leave it alone and let TMDS report error?
        // If not an exact match, then this should return null or throw exception or return special value like "error:<value>"
        injuryIllness.operation = Utilities.processDropdown(injuryIllnessOperationDropdownBy, injuryIllness.operation, injuryIllness.random, true);

        injuryIllness.injuryNature = Utilities.processDropdown(injuryNatureDropdownBy, injuryIllness.injuryNature, injuryIllness.random, true);

        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(II_MEDICAL_SERVICE_DROPDOWN));
            injuryIllness.medicalService = Utilities.processDropdown(II_MEDICAL_SERVICE_DROPDOWN, injuryIllness.medicalService, injuryIllness.random, true);
        }
        catch (TimeoutException e) {
            //logger.fine("There's no II_MEDICAL_SERVICE_DROPDOWN, which is the case for levels/roles 1,2,3");
        }

        // Mechanism of Injury dropdown isn't active unless Injury Nature indicates an injury rather than illness.
        // If inactive, it's not accessible, supposedly, but Selenium can make it happen!!!!  So we need to check for grayed out, and not just plow through.
        // Probably ought to check the logic here.  Whipped it together fast.
        try {
            WebElement mechanismOfInjuryElement = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(mechanismOfInjuryBy));
            String disabledAttribute = mechanismOfInjuryElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                logger.finer("InjuryIllness.process(), Didn't find disabled attribute, so not greyed out which means what?  Go ahead and use it.");
                try {
                    (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(mechanismOfInjuryBy));
                    injuryIllness.mechanismOfInjury = Utilities.processDropdown(mechanismOfInjuryBy, injuryIllness.mechanismOfInjury, injuryIllness.random, true);
                } catch (TimeoutException e) {
                    //logger.fine("InjuryIllness.process(), There's no mechanism of injury dropdown?, which is the case for levels/roles 1,2,3");
                }
            } else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    logger.fine("InjuryIllness.process(), Mechanism of Injury is grayed out.");
                }
            }
        }
        catch (Exception e) {
            logger.finest("Couldn't find Mechanism of Injury element.  Skipping it because it probably shouldn't be on the page for this role.");
        }



        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(patientConditionBy));
            injuryIllness.patientCondition = Utilities.processDropdown(patientConditionBy, injuryIllness.patientCondition, injuryIllness.random, true);
        }
        catch (Exception e) {
            logger.severe("prob timed out waiting for whether there was a patient condition dropdown or not, because there wasn't one.");
        }

        // Seems that "Accepting Physician" dropdown exists at levels 1,2,3, but not at level 4.  And for levels 1,2,3
        // nothing drops down if there are no physicians at the site.  For now, skipping this since it's optional.


        // the following assessment text box was the last part, but now it's first part of this section
        // Assessments doesn't show up in pre-registration's Injury/Illness, but it does in new patient reg and update patient.
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(assessmentTextBoxBy));
            injuryIllness.assessment = Utilities.processText(By.id("patientRegistration.assessment"), injuryIllness.assessment, Utilities.TextFieldType.INJURY_ILLNESS_ASSESSMENT, injuryIllness.random, false);
        }
        catch (TimeoutException e) {
            //logger.fine("No Assessment text box to enter assessment text.  Probably level 4.  Okay.");
            injuryIllness.assessment = null; // experiment.  blank?, null?  keep original value? 12/27/18
        }

        boolean forceToRequired = true;

        if (injuryIllness.diagnosisCodeSet == null || injuryIllness.diagnosisCodeSet.isEmpty() || injuryIllness.diagnosisCodeSet.equalsIgnoreCase("random")) {
            injuryIllness.diagnosisCodeSet = Utilities.random.nextBoolean() ? "ICD-9" : "ICD-10";
        }
        // get current value
        WebElement diagnosisCodeSetDropdown = Driver.driver.findElement(diagnosisCodeSetDropdownBy);
        Select select = new Select(diagnosisCodeSetDropdown);
        WebElement firstSelectedOption = select.getFirstSelectedOption();
        String currentOption = firstSelectedOption.getText();
        if (!injuryIllness.diagnosisCodeSet.equals(currentOption)) {
            injuryIllness.diagnosisCodeSet = Utilities.processDropdown(diagnosisCodeSetDropdownBy, injuryIllness.diagnosisCodeSet, injuryIllness.random, forceToRequired);
            try {
                Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
            }
            catch (TimeoutException e) {
                //logger.fine("InjuryIllness.process(), Timed out.  Didn't find an alert, which is probably okay.  Continuing.");
            }
            catch (Exception e) {
                //logger.fine("InjuryIllness.process(), Didn't find an alert, which is probably okay.  Continuing.");
            }
        }
        String diagnosisCode = processIcdDiagnosisCode(
                injuryIllness.diagnosisCodeSet,
                primaryDiagnosisFieldBy,
                primaryDiagnosisDropdownBy,
                injuryIllness.primaryDiagnosis,
                injuryIllness.random,
                forceToRequired); // should/could this ever be false?

        if (diagnosisCode == null) {
            if (!Arguments.quiet) System.err.println("***Could not process ICD diagnosis code for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return false;
        }
        this.primaryDiagnosis = diagnosisCode; // new 10/21/18

        // Additional Diagnoses is a list of diagnoses, and they are created in the same way as the primary diagnosis.
        // That is, you have to do 3 or more characters, and then a dropdown can be accessed.
        // But you have to click the Show Additional Diagnoses first.
        // I think this section needs to be reexamined.  Where are we allocating space?
        int nAdditionalDiagnoses = injuryIllness.additionalDiagnoses == null ? 0 : injuryIllness.additionalDiagnoses.size();
        if (nAdditionalDiagnoses > 0 && !injuryIllness.additionalDiagnoses.get(0).isEmpty()) {
            //WebElement showAdditionalDiagnosesButton = null;
            try {
                WebElement showAdditionalDiagnosesButton = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.elementToBeClickable(showAdditionalDiagnosesButtonBy));
                showAdditionalDiagnosesButton.click();
            }
            catch (Exception e) {
                logger.fine("Didn't find a Show Additional Diagnoses button.  Maybe because it says Hide instead.  We're going to continue on...");
            }
            try { // the results of doing this is not going back into the summary output json file.  Fix this.
                //List<String> updatedAdditionalDiagnoses = new ArrayList<String>(additionalDiagnoses);
                List<String> updatedAdditionalDiagnoses = new ArrayList<>();
                for (String additionalDiagnosisCode : additionalDiagnoses) {
                    Utilities.sleep(555); // new 12/06/18
                    String additionalDiagnosisFullString = processIcdDiagnosisCode(
                            injuryIllness.diagnosisCodeSet,
                            additionalDiagnosisFieldBy,
                            additionalDiagnosisDropdownBy,
                            additionalDiagnosisCode,
                            injuryIllness.random,
                            false); // was true

                    if (additionalDiagnosisFullString == null) {
                        if (!Arguments.quiet)
                            System.err.println("***Could not process ICD diagnosis code " + additionalDiagnosisCode + " for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        continue;
                    }
                    updatedAdditionalDiagnoses.add(additionalDiagnosisFullString);
                    //logger.fine("additionalDiagnosis: " + additionalDiagnosisCode);
                    //logger.fine("We should replace the additionalDiagnoses string with the full one, I think.  But do it later.");
                    //this.additionalDiagnoses.add(additionalDiagnosisFullString); // new 10/21/18, not sure at all. Cannot do this because we're looping on this collection
                }
                additionalDiagnoses = new ArrayList<String>(updatedAdditionalDiagnoses);
            }
            catch (StaleElementReferenceException e) { // why does this keep happening?
                logger.severe("Problem with processIcdDiagnosisCode.  Stale reference.  e: " + Utilities.getMessageFirstLine(e));
                return false;
            }
            catch (Exception e) {
                logger.severe("Problem with processIcdDiagnosisCode.  e: " + Utilities.getMessageFirstLine(e));
                return false;
            }
        }

        try {
            // Check if this Role has CPT section
            WebElement procedureCodesTextBox = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(cptProcedureCodesTextBoxBy));

            if (injuryIllness.procedureCodes != null && (injuryIllness.procedureCodes.isEmpty() || injuryIllness.procedureCodes.equalsIgnoreCase("random"))) {
                LoremIpsum loremIpsum = LoremIpsum.getInstance(); // not right way?

                int nCodes = Utilities.random.nextInt(5);
                StringBuffer codes = new StringBuffer();
                codes.append(loremIpsum.getCptCode());

                for (int ctr = 0; ctr < nCodes; ctr++) {
                    codes.append("," + loremIpsum.getCptCode());
                }
                injuryIllness.procedureCodes = codes.toString();

                procedureCodesTextBox.clear();
                procedureCodesTextBox.sendKeys(injuryIllness.procedureCodes);
            }
            else {
                // one or more CPT codes was specified in input file, so slam them in.  Next line prob wrong because changed CPT_CODES to CPT_CODE
                injuryIllness.procedureCodes = Utilities.processText(cptProcedureCodesTextBoxBy, injuryIllness.procedureCodes, Utilities.TextFieldType.CPT_CODE, injuryIllness.random, false);
            }


        }
        catch (TimeoutException e) {
            //logger.fine("Did not find CPT Procedure Codes text box, so maybe it doesn't exist at this level/role.  Continuing.");
        }

        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(receivedTransfusionCheckBoxBy));
            injuryIllness.receivedTransfusion = Utilities.processBoolean(receivedTransfusionCheckBoxBy, injuryIllness.receivedTransfusion, injuryIllness.random, false);
            if (injuryIllness.receivedTransfusion != null && injuryIllness.receivedTransfusion) {
                injuryIllness.transfusedWithUnlicensedBlood = Utilities.processBoolean(By.id("patientRegistration.hasUnlicensedBloodTransfusion1"), injuryIllness.transfusedWithUnlicensedBlood, injuryIllness.random, false);
            }
        }
        catch (TimeoutException e) {
            logger.finest("I guess there's no blood transfusion section or received transfusion button, so not role 1,2,3.  Okay.");
        }

        // There's an error in the web app regarding the identification of the Admission Note text box
        // and how it gets mixed up with Administrative Notes.  In a Level 4 instance, there is no
        // Admission Note text box.  In Level 1,2,3 there is, but if you use its identifier in Level 4
        // it gets written to Administrative Notes text box instead.  So, check first.
        try {
            WebElement admissionNoteLabel = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(admissionNoteLabelBy));
            String admissionNoteLabelText = admissionNoteLabel.getText();
            if (admissionNoteLabelText.contentEquals("Admission Note")) {
                //logger.fine("Found Admission Note Label so will try to add text to associated text box.");
                injuryIllness.admissionNote = Utilities.processText(admissionNoteBy, injuryIllness.admissionNote, Utilities.TextFieldType.INJURY_ILLNESS_ADMISSION_NOTE, injuryIllness.random, false);
            }
        }
        catch (Exception e) {
            logger.finest("Did not find Admission Note label on page, which means we can skip Admission Note.");
        }
        // Check these next booleans to see if they're working.  I'm getting false on all of them
        injuryIllness.amputation = Utilities.processBoolean(II_AMPUTATION_CHECKBOX, injuryIllness.amputation, injuryIllness.random, false);
        injuryIllness.headTrauma = Utilities.processBoolean(II_HEAD_TRAUMA_CHECKBOX, injuryIllness.headTrauma, injuryIllness.random, false);
        injuryIllness.burns = Utilities.processBoolean(II_BURNS_CHECKBOX, injuryIllness.burns, injuryIllness.random, false);
        injuryIllness.postTraumaticStressDisorder = Utilities.processBoolean(II_PTSD_CHECKBOX, injuryIllness.postTraumaticStressDisorder, injuryIllness.random, false);
        injuryIllness.eyeTrauma = Utilities.processBoolean(II_EYE_TRAUMA_CHECKBOX, injuryIllness.eyeTrauma, injuryIllness.random, false);
        injuryIllness.spinalCordInjury = Utilities.processBoolean(II_SPINAL_CORD_INJURY_CHECKBOX, injuryIllness.spinalCordInjury, injuryIllness.random, false);

        boolean amputationChecked = Utilities.getCheckboxValue(II_AMPUTATION_CHECKBOX);
        if (amputationChecked) { // amputation radios don't activate unless Amputation checkbox is checked
            injuryIllness.amputationCause = Utilities.processRadiosByLabel(injuryIllness.amputationCause, injuryIllness.random, false,
                    II_EXPLOSION_RADIO_BUTTON_LABEL,
                    II_GSW_RADIO_BUTTON_LABEL,
                    II_GRENADE_RADIO_BUTTON_LABEL,
                    II_LAND_MINE_RADIO_BUTTON_LABEL,
                    II_MVA_RADIO_BUTTON_LABEL,
                    II_OTHER_RADIO_BUTTON_LABEL
            ); // this was for demo and probably works.  need new one for gold
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000);
        }
        return true; // wow, this method doesn't ever return false?
    }

    public String processIcdDiagnosisCode(String codeSet, By icdTextField, By dropdownBy, String text, Boolean sectionIsRandom, Boolean required) {
        boolean valueIsSpecified = !(text == null || text.isEmpty() || text.equalsIgnoreCase("random"));
        String valueReturned = null;
        if (valueIsSpecified) {
            String[] pieces = text.split(" ");
            String searchString = pieces[0];
            String value = fillInIcdSearchTextField(icdTextField, searchString); // normally takes less than 1 second to get back from server to populate the dropdown
            if (value == null) {
                logger.fine("Utilities.processIcdDiagnosisCode(), was unable to fill in text field with text: " + searchString);
            }

            WebElement dropdownElement = null;

            Select select = null;
            try {
                int ctr = 0;
                do {
                    Utilities.sleep(777); // In decent server and network conditions I think it takes about a second to populate the dropdown
                    try {
                        // put a sleep of tenth sec here?
                        dropdownElement = (new WebDriverWait(Driver.driver, 5)).until( // was 4
                                ExpectedConditions.refreshed(
                                        ExpectedConditions.presenceOfElementLocated(dropdownBy)));
                    }
                    catch (Exception e3) {
                        logger.fine("InjuryIllness.processIcdDiagnosisCode(), tried waiting for refreshed presence of element for dropdown.  Exception: " + e3.getMessage());
                        ctr++;
                        continue;
                    }
                    // This next line prints out all the elements it could choose from
                    //logger.fine("InjuryIllness.process(), got dropdownElement: ->" + dropdownElement.getText() + "<-");

                    select = new Select(dropdownElement);
                    // Following logic could be improved.  I've seen a dropdown that has two options, but it reports as size 1.
                    int nOptions = select.getOptions().size(); // new
                    // assume the first element (0) is not to be chosen, so nOptions must be > 1.
                    // If it's 2, then choose element 1.  If it's 3, choose 1 or 2
                    int selectThisOption = 1;
                    if (nOptions > 1) {
                        selectThisOption = Utilities.random.nextInt(nOptions - 1) + 1;
                        //logger.fine("Will selection option #" + selectThisOption + " from dropdown: " + dropdownElement.toString());
                    }
                    try {
                        select.selectByIndex(selectThisOption); // first element is 0, throws when index is 1 and ...text is S06.0x1A, 290, S06.0X3A,
                    }
                    catch (Exception e2) {
                        logger.fine("\tInjuryIllness.processIcdDiagnosisCode(), index " + selectThisOption + " for dropdownBy: " + dropdownBy + ", text: " + text + ", exception: " + Utilities.getMessageFirstLine(e2));
                        ctr++;
                        continue;
                    }
                    WebElement firstSelectedOption = select.getFirstSelectedOption();
                    String firstSelectedOptionText = firstSelectedOption.getText();
                    if (!firstSelectedOptionText.contains("Select Diagnosis") && !firstSelectedOptionText.contains("NO DIAGNOSIS CODE")) {
                        break;
                    }
                    ctr++;
                } while (ctr < 20);
                //logger.fine("InjuryIllness.processIcdDiagnosisCode(), dropdown has this many options: " + select.getOptions().size());
                // just removed this.  Prob had this here because was failing too often.  //select.selectByIndex(1); // throws.  can fail here with NoSuchElementException, cannot locate option with index: 1
                valueReturned = select.getFirstSelectedOption().getText();
            }
            catch(Exception e) {
                logger.fine("InjuryIllness.processIcdDiagnosisCode(), text: " + text + " Couldn't select an option from dropdown: " + Utilities.getMessageFirstLine(e));
                return null;
            }
            //logger.fine("valueReturned by selecting an element in dropdown after search: " + valueReturned);
        }
        else { // value is not specified

            // REWRITE THIS SECTION

            int nOptionsRequiredForValidDropdownSelection = 1;
            ExpectedCondition<List<WebElement>> diagnosisCodesMoreThanEnough = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfDiagnosisDropdown, nOptionsRequiredForValidDropdownSelection);
            int loopCtr = 0;
            int maxLoops = 20;
            boolean moreThanEnoughCodes = false;
            do {
                if (++loopCtr > maxLoops) {
                    break; // indicates failure, I think
                }
                if (codeSet.equalsIgnoreCase("ICD-9")) {
                    LoremIpsum loremIpsum = LoremIpsum.getInstance(); // not right way
                    text = loremIpsum.getIcd9Code(); // test
                }
                if (codeSet.equalsIgnoreCase("ICD-10")) {
                    LoremIpsum loremIpsum = LoremIpsum.getInstance();
                    text = loremIpsum.getIcd10Code(); // test
                }
                String value = fillInIcdSearchTextField(icdTextField, text); // icdTextField corresponds to "searchElem" in the JS code in patientReg.html
                if (value == null) {
                    logger.fine("Utilities.processIcdDiagnosisCode(), unable to fill in text field with text: " + text);
                    continue;
                }
                try {
                    (new WebDriverWait(driver, 1 + loopCtr)).until(diagnosisCodesMoreThanEnough);
                    moreThanEnoughCodes = true;
                }
                catch (Exception e) {
                    logger.fine("Utilities.processIcdDiagnosisCode(), failed to get enough options in dropdown.");
                    continue; // fix up looping later, and get rid of stuff below to count options
                }
            } while (!moreThanEnoughCodes);
            // The problem is that this next line happens too soon, before the server returns the matches.
            valueReturned = Utilities.processDropdown(dropdownBy, null, sectionIsRandom, true); // valueReturned can be "4XX.Xx..." but the dropdown says "Select Diagnosis"
        }
        //logger.fine("processIcdDiagnosisCode(), Leaving processIcd10DiagnosisCode() and returning " + valueReturned);
        return valueReturned;
    }

    // Assume that when you enter some text in a search text field, it's going to cause a server request to populate an
    // associated dropdown.  For example ICD-9 and ICD-10.  This takes some time, and this method should perhaps
    // pause after the text is input to give the server a chance to populate the dropdown.  There's not much difference
    // between this method and fillInSearchField.
    private static String fillInIcdSearchTextField(final By textFieldBy, String text) {
        //logger.fine("In fillInIcdSearchTextField() with text: " + text + " and field: " + textFieldBy.toString());
        WebElement element = null;

        try {
            element = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(textFieldBy));
        }
        catch (Exception e) {
            logger.severe("Couldn't get the text field: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            //logger.fine("Utilities.fillInIcdSearchTextField(), going to clear element");
            Utilities.sleep(2555); // what the crap I hate to do this but what the crap why does this fail so often?
            element.clear(); // this fails often!!!!! "Element is not currently interactable and may not be manipulated"
        }
        catch (Exception e) { // invalid element state
            logger.fine("Utilities.fillInIcdSearchTextField(), failed to clear the element.: " + Utilities.getMessageFirstLine(e));
            return null; // Continue on for another loop somewhere?  Fails: 6 is this the right thing to do?  Go on anyway? failed when slow 3g
        }
        try {
            //logger.fine("Utilities.fillInIcdSearchTextField(), going to send the element this text: " + text);
            element.sendKeys(text); // this takes a half second to cause population of the dropdown.  Maybe longer.
        }
        catch (Exception e) {
            logger.fine("Utilities.fillInIcdSearchTextField(), either couldn't get the element, or couldn't clear it or couldn't sendKeys.: " + Utilities.getMessageFirstLine(e));
            return null;
        }

        Utilities.sleep(1555); // I hate doing this but don't know how to wait for dropdown to populate (was 750?)

        //logger.fine("Leaving Utilities.fillInIcdSearchTextField(), returning text: " + text);
        return text; // probably should return the text that was sent in.
    }
}
