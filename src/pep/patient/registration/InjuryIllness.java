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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;


/**
 * This represents a section in a Patient Registration page, which contains several sections, some of which are variable
 * depending on the level.  The sections are generally divided by horizontal purplish lines.
 * Injury/Illness is a big section of a registration page (New Patient, Pre-Registration, and Update)
 */
public class InjuryIllness {
    private static Logger logger = Logger.getLogger(InjuryIllness.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String operation;
    public String injuryNature;
    public String medicalService;
    public String mechanismOfInjury;
    public String patientCondition;
    public String diagnosisCodeSet;
    public String primaryDiagnosis;
    public String assessment;
    public List<String> additionalDiagnoses = new ArrayList<>();

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

    private static By II_MEDICAL_SERVICE_DROPDOWN = By.id("patientRegistration.medicalService");
    private static By II_AMPUTATION_CHECKBOX = By.id("patientRegistration.enablingCare1");
    private static By II_BURNS_CHECKBOX = By.id("patientRegistration.enablingCare2");
    private static By II_EYE_TRAUMA_CHECKBOX = By.id("patientRegistration.enablingCare3");
    private static By II_HEAD_TRAUMA_CHECKBOX = By.id("patientRegistration.enablingCare4");
    private static By II_PTSD_CHECKBOX = By.id("patientRegistration.enablingCare5");
    private static By II_SPINAL_CORD_INJURY_CHECKBOX = By.id("patientRegistration.enablingCare6");
    private static By II_EXPLOSION_RADIO_BUTTON_LABEL = By.xpath("//label[text()='Explosion']");
    private static By II_GSW_RADIO_BUTTON_LABEL = By.xpath("//label[text()='GSW']");
    private static By II_GRENADE_RADIO_BUTTON_LABEL = By.xpath("//label[text()='Grenade']");
    private static By II_LAND_MINE_RADIO_BUTTON_LABEL = By.xpath("//label[text()='Land Mine']");
    private static By II_MVA_RADIO_BUTTON_LABEL = By.xpath("//label[text()='MVA']");
    private static By II_OTHER_RADIO_BUTTON_LABEL = By.xpath("//label[text()='Other']");
    private static By injuryIllnessOperationDropdownBy = By.id("patientRegistration.operation");
    private static By injuryNatureDropdownBy = By.id("patientRegistration.injuryNature");
    private static By mechanismOfInjuryBy = By.id("patientRegistration.mechOfInjury");
    private static By patientConditionBy = By.id("patientRegistration.patientCondition");
    private static By diagnosisCodeSetDropdownBy = By.id("patientRegistration.codeType");
    private static By showAdditionalDiagnosesButtonBy = By.cssSelector("input[value='Show Additional Diagnoses']");
    private static By additionalDiagnosisFieldBy = By.id("addtDiagnosisSearch");
    private static By additionalDiagnosisDropdownBy = By.id("patAddtDiagnoses");
    private static By primaryDiagnosisFieldBy = By.id("diagnosisSearch");
    private static By primaryDiagnosisDropdownBy = By.id("patientRegistration.diagnosis");
    private static By assessmentTextBoxBy = By.id("patientRegistration.assessment");
    private static By cptProcedureCodesTextBoxBy = By.id("cptCodesTextlist");
    private static By receivedTransfusionCheckBoxBy = By.id("patientRegistration.hasBloodTransfusion1");
    private static By optionOfDiagnosisDropdown = By.xpath("//select[@id='patientRegistration.diagnosis']/option");


    public InjuryIllness() {
        if (Arguments.template) {
            this.operation = "";
            this.injuryNature = "";
            this.medicalService = "";
            this.mechanismOfInjury = "";
            this.patientCondition = "";
            this.diagnosisCodeSet = "";
            this.primaryDiagnosis = "";
            this.assessment = "";
            this.additionalDiagnoses = Collections.emptyList();
            this.cptCode = "";
            this.procedureCodes = "";
            this.receivedTransfusion = false;
            this.transfusedWithUnlicensedBlood = false;
            this.admissionNote = ""; // Is this thing ever used?
            this.amputation = false;
            this.headTrauma = false;
            this.burns = false;
            this.postTraumaticStressDisorder = false;
            this.eyeTrauma = false;
            this.spinalCordInjury = false;
            this.amputationCause = "";
        }
    }

    /**
     * Process the entire Injury/Illness section of a registration page.
     * There is no save operation for this section.  This method is long and complicated because
     * some of the elements have to communicate with the server. Timing issues are rampant.
     *
     * @param patient The Patient for this Injury/Illness section
     * @return Success or Failure at entering data into the section
     */
    public boolean process(Patient patient) {
        if (patient.registration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
                if (!Arguments.quiet) System.out.println("    Processing Injury/Illness at " + LocalTime.now() + " ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Injury/Illness at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        }
        //
        // Inherit the state stuff from parent.
        //
        InjuryIllness injuryIllness = null;
        if (patient.patientState == PatientState.PRE && patient.registration != null && patient.registration.preRegistration != null && patient.registration.preRegistration.injuryIllness != null) {
            injuryIllness = patient.registration.preRegistration.injuryIllness;
        }
        else if (patient.patientState == PatientState.NEW && patient.registration != null && patient.registration.newPatientReg != null && patient.registration.newPatientReg.injuryIllness != null) {
            injuryIllness = patient.registration.newPatientReg.injuryIllness;
        }
        else if (patient.patientState == PatientState.UPDATE && patient.registration != null && patient.registration.updatePatient != null && patient.registration.updatePatient.injuryIllness != null) {
            injuryIllness = patient.registration.updatePatient.injuryIllness;
        }
        if (injuryIllness == null) { // new.  Does this make sense?  3/20/19
            System.out.println("Does this ever happen?");
            return false;
        }
        //
        // Start filling in fields
        //
        injuryIllness.operation = Utilities.processDropdown(injuryIllnessOperationDropdownBy, injuryIllness.operation, injuryIllness.randomizeSection, true);
        injuryIllness.injuryNature = Utilities.processDropdown(injuryNatureDropdownBy, injuryIllness.injuryNature, injuryIllness.randomizeSection, true);
        try {
            Utilities.waitForPresence(II_MEDICAL_SERVICE_DROPDOWN, 1, "InjuryIllness.process()");
            injuryIllness.medicalService = Utilities.processDropdown(II_MEDICAL_SERVICE_DROPDOWN, injuryIllness.medicalService, injuryIllness.randomizeSection, true);
        }
        catch (TimeoutException e) {
            // This happens for roles 1,2,3
        }

        // Mechanism of Injury dropdown isn't active unless Injury Nature indicates an injury rather than illness.
        try {
            WebElement mechanismOfInjuryElement = Utilities.waitForPresence(mechanismOfInjuryBy, 1, "InjuryIllness.process()");
            String disabledAttribute = mechanismOfInjuryElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                logger.finer("InjuryIllness.process(), Didn't find disabled attribute, so not greyed out which means what?  Go ahead and use it.");
                try {
                    Utilities.waitForPresence(mechanismOfInjuryBy, 1, "InjuryIllness.(), mechanism of injury");
                    injuryIllness.mechanismOfInjury = Utilities.processDropdown(mechanismOfInjuryBy, injuryIllness.mechanismOfInjury, injuryIllness.randomizeSection, true);
                } catch (TimeoutException e) {
                    logger.finest("InjuryIllness.process(), There's no mechanism of injury dropdown?, which is the case for levels/roles 1,2,3");
                }
            } else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    logger.fine("InjuryIllness.process(), Mechanism of Injury is grayed out.");
                }
            }
        }
        catch (Exception e) {
            logger.finest("Couldn't find Mechanism of Injury element.  Skipping it because it probably shouldn't be on the page for this role. e: " + Utilities.getMessageFirstLine(e));
        }
        try {
            Utilities.waitForPresence(patientConditionBy, 1, "InjuryIllness.process()");
            injuryIllness.patientCondition = Utilities.processDropdown(patientConditionBy, injuryIllness.patientCondition, injuryIllness.randomizeSection, true);
        }
        catch (Exception e) {
            logger.severe("prob timed out waiting for whether there was a patient condition dropdown or not, because there wasn't one."); ScreenShot.shoot("SevereError");
        }
        // "Accepting Physician" dropdown exists at levels 1,2,3, but not at level 4.  And for levels 1,2,3
        // nothing drops down if there are no physicians at the site.  For now, skipping this since it's optional.
        // Assessments doesn't show up in pre-registration's Injury/Illness, but it does in new patient reg and update patient.
        // No assessments box for Role 4 in New Patient Reg for Seam code.
        try {
            Utilities.waitForVisibility(assessmentTextBoxBy, 1, "InjuryIllness.process(), checking on assessment text box.");
            injuryIllness.assessment = Utilities.processText(By.id("patientRegistration.assessment"), injuryIllness.assessment, Utilities.TextFieldType.INJURY_ILLNESS_ASSESSMENT, injuryIllness.randomizeSection, false);
        }
        catch (TimeoutException e) {
            injuryIllness.assessment = null;
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
            injuryIllness.diagnosisCodeSet = Utilities.processDropdown(diagnosisCodeSetDropdownBy, injuryIllness.diagnosisCodeSet, injuryIllness.randomizeSection, forceToRequired);
            try {
                Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
            }
            catch (TimeoutException e) {
                logger.finest("InjuryIllness.process(), Timed out.  Didn't find an alert, which is probably okay.  Continuing.");
            }
            catch (Exception e) {
                logger.finest("InjuryIllness.process(), Didn't find an alert, which is probably okay.  Continuing.");
            }
        }
        String diagnosisCode = processIcdDiagnosisCode(
                injuryIllness.diagnosisCodeSet,
                primaryDiagnosisFieldBy,
                primaryDiagnosisDropdownBy,
                injuryIllness.primaryDiagnosis,
                injuryIllness.randomizeSection);

        if (diagnosisCode == null) {
            if (!Arguments.quiet) System.err.println("***Could not process ICD diagnosis code for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            return false;
        }
        this.primaryDiagnosis = diagnosisCode; // new 10/21/18
        // Additional Diagnoses is a list of diagnoses, and they are created in the same way as the primary diagnosis.
        // That is, you have to do 3 or more characters, and then a dropdown can be accessed.
        // But you have to click the Show Additional Diagnoses first.
        int nAdditionalDiagnoses = injuryIllness.additionalDiagnoses == null ? 0 : injuryIllness.additionalDiagnoses.size();
        if (nAdditionalDiagnoses > 0 && !injuryIllness.additionalDiagnoses.get(0).isEmpty()) {
            try {
                WebElement showAdditionalDiagnosesButton = Utilities.waitForRefreshedClickability(showAdditionalDiagnosesButtonBy, 1, "InjuryIllness.(), show additional diagnoses button");
                showAdditionalDiagnosesButton.click();
            }
            catch (Exception e) {
                logger.fine("Didn't find a Show Additional Diagnoses button.  Maybe because it says Hide instead.  We're going to continue on...");
            }
            try {
                List<String> updatedAdditionalDiagnoses = new ArrayList<>();
                for (String additionalDiagnosisCode : additionalDiagnoses) {
                    Utilities.sleep(555, "InjuryIllness, process(), in loop for additional diagnosis codes."); // new 12/06/18
                    String additionalDiagnosisFullString = processIcdDiagnosisCode(
                            injuryIllness.diagnosisCodeSet,
                            additionalDiagnosisFieldBy,
                            additionalDiagnosisDropdownBy,
                            additionalDiagnosisCode,
                            injuryIllness.randomizeSection);

                    if (additionalDiagnosisFullString == null) {
                        if (!Arguments.quiet)
                            System.err.println("***Could not process ICD diagnosis code " + additionalDiagnosisCode + " for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                        continue;
                    }
                    updatedAdditionalDiagnoses.add(additionalDiagnosisFullString);
                }
                additionalDiagnoses = new ArrayList<String>(updatedAdditionalDiagnoses);
            }
            catch (StaleElementReferenceException e) {
                logger.severe("Problem with processIcdDiagnosisCode.  Stale reference.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }
            catch (Exception e) {
                logger.severe("Problem with processIcdDiagnosisCode.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }
        }
        try {
            WebElement procedureCodesTextBox = Utilities.waitForVisibility(cptProcedureCodesTextBoxBy, 1, "InjuryIllness.process()");
            if (injuryIllness.procedureCodes != null && (injuryIllness.procedureCodes.isEmpty() || injuryIllness.procedureCodes.equalsIgnoreCase("random"))) {
                LoremIpsum loremIpsum = LoremIpsum.getInstance();
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
                injuryIllness.procedureCodes = Utilities.processText(cptProcedureCodesTextBoxBy, injuryIllness.procedureCodes, Utilities.TextFieldType.CPT_CODE, injuryIllness.randomizeSection, false);
            }
        }
        catch (TimeoutException e) {
            logger.finest("Did not find CPT Procedure Codes text box, so maybe it doesn't exist at this level/role.  Continuing.");
        }

        try {
            Utilities.waitForPresence(receivedTransfusionCheckBoxBy, 1, "InjuryIllness.process()");
            injuryIllness.receivedTransfusion = Utilities.processBoolean(receivedTransfusionCheckBoxBy, injuryIllness.receivedTransfusion, injuryIllness.randomizeSection, false);
            if (injuryIllness.receivedTransfusion != null && injuryIllness.receivedTransfusion) {
                injuryIllness.transfusedWithUnlicensedBlood = Utilities.processBoolean(By.id("patientRegistration.hasUnlicensedBloodTransfusion1"), injuryIllness.transfusedWithUnlicensedBlood, injuryIllness.randomizeSection, false);
            }
        }
        catch (TimeoutException e) {
            logger.finest("No blood transfusion section or received transfusion button, so not role 1,2,3.  Okay.");
        }
        injuryIllness.amputation = Utilities.processBoolean(II_AMPUTATION_CHECKBOX, injuryIllness.amputation, injuryIllness.randomizeSection, false);
        injuryIllness.headTrauma = Utilities.processBoolean(II_HEAD_TRAUMA_CHECKBOX, injuryIllness.headTrauma, injuryIllness.randomizeSection, false);
        injuryIllness.burns = Utilities.processBoolean(II_BURNS_CHECKBOX, injuryIllness.burns, injuryIllness.randomizeSection, false);
        injuryIllness.postTraumaticStressDisorder = Utilities.processBoolean(II_PTSD_CHECKBOX, injuryIllness.postTraumaticStressDisorder, injuryIllness.randomizeSection, false);
        injuryIllness.eyeTrauma = Utilities.processBoolean(II_EYE_TRAUMA_CHECKBOX, injuryIllness.eyeTrauma, injuryIllness.randomizeSection, false);
        injuryIllness.spinalCordInjury = Utilities.processBoolean(II_SPINAL_CORD_INJURY_CHECKBOX, injuryIllness.spinalCordInjury, injuryIllness.randomizeSection, false);

        boolean amputationChecked = Utilities.getCheckboxValue(II_AMPUTATION_CHECKBOX);
        if (amputationChecked) {
            injuryIllness.amputationCause = Utilities.processRadiosByLabel(injuryIllness.amputationCause, injuryIllness.randomizeSection, false,
                    II_EXPLOSION_RADIO_BUTTON_LABEL,
                    II_GSW_RADIO_BUTTON_LABEL,
                    II_GRENADE_RADIO_BUTTON_LABEL,
                    II_LAND_MINE_RADIO_BUTTON_LABEL,
                    II_MVA_RADIO_BUTTON_LABEL,
                    II_OTHER_RADIO_BUTTON_LABEL
            );
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "InjuryIllness");
        }
        return true;
    }

    /**
     * Handle the ICD Diagnosis field elements on the page.  This is a bit tricky due to the interaction with the server and the
     * need to handle randomly created values.
     *
     * @param codeSet
     * @param icdTextFieldBy
     * @param dropdownBy
     * @param textValue
     * @param sectionIsRandom
     * @return the string resulting from a match with the textValue passed in
     */
    public String processIcdDiagnosisCode(String codeSet, By icdTextFieldBy, By dropdownBy, String textValue, Boolean sectionIsRandom) {
        boolean valueIsSpecified = !(textValue == null || textValue.isEmpty() || textValue.equalsIgnoreCase("random"));
        String valueReturned;
        if (valueIsSpecified) {
            //
            // Fill in the text search field, which causes a dropdown to be populated.  The string may contain more than one
            // word, and if so, just use the first one.
            //
            String[] pieces = textValue.split(" ");
            String firstWordTextValueForSearch = pieces[0];
            boolean filledInTextField = fillInIcdSearchTextField(icdTextFieldBy, firstWordTextValueForSearch);
            if (!filledInTextField) {
                logger.fine("Utilities.processIcdDiagnosisCode(), was unable to fill in text field with text: " + firstWordTextValueForSearch);
            }
            //
            // Check the dropdown that got populated by filling in some text in a search field.
            //
            WebElement dropdownElement;
            Select select = null;
            try {
                int ctr = 0;
                do {
                    Utilities.sleep(777, "InjuryIllness");
                    try {
                        dropdownElement = (new WebDriverWait(Driver.driver, 5)).until(
                                ExpectedConditions.refreshed(
                                        ExpectedConditions.presenceOfElementLocated(dropdownBy)));
                    }
                    catch (Exception e3) {
                        logger.fine("InjuryIllness.processIcdDiagnosisCode(), tried waiting for refreshed presence of element for dropdown.  Exception: " + e3.getMessage());
                        ctr++;
                        continue;
                    }
                    select = new Select(dropdownElement);
                    int nOptions = select.getOptions().size();
                    int selectThisOption = 1;
                    if (nOptions > 1) {
                        selectThisOption = Utilities.random.nextInt(nOptions - 1) + 1;
                    }
                    try {
                        select.selectByIndex(selectThisOption);
                    }
                    catch (Exception e2) {
                        logger.fine("\tInjuryIllness.processIcdDiagnosisCode(), index " + selectThisOption + " for dropdownBy: " + dropdownBy + ", text: " + textValue + ", exception: " + Utilities.getMessageFirstLine(e2));
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
                valueReturned = select.getFirstSelectedOption().getText();
            }
            catch(Exception e) {
                logger.fine("InjuryIllness.processIcdDiagnosisCode(), text: " + textValue + " Couldn't select an option from dropdown: " + Utilities.getMessageFirstLine(e));
                return null;
            }
        }
        else {
            // REWRITE THIS SECTION
            int nOptionsRequiredForValidDropdownSelection = 1;
            ExpectedCondition<List<WebElement>> diagnosisCodesMoreThanEnough = ExpectedConditions.numberOfElementsToBeMoreThan(optionOfDiagnosisDropdown, nOptionsRequiredForValidDropdownSelection);
            int loopCtr = 0;
            int maxLoops = 20;
            boolean moreThanEnoughCodes = false;
            do {
                if (++loopCtr > maxLoops) {
                    break;
                }
                if (codeSet.equalsIgnoreCase("ICD-9")) {
                    LoremIpsum loremIpsum = LoremIpsum.getInstance();
                    textValue = loremIpsum.getIcd9Code();
                }
                if (codeSet.equalsIgnoreCase("ICD-10")) {
                    LoremIpsum loremIpsum = LoremIpsum.getInstance();
                    textValue = loremIpsum.getIcd10Code();
                }
                boolean filledInTextField = fillInIcdSearchTextField(icdTextFieldBy, textValue);
                if (!filledInTextField) {
                    logger.fine("Utilities.processIcdDiagnosisCode(), unable to fill in text field with text: " + textValue);
                    continue;
                }
                try {
                    (new WebDriverWait(driver, 1 + loopCtr)).until(diagnosisCodesMoreThanEnough);
                    moreThanEnoughCodes = true;
                }
                catch (Exception e) {
                    logger.fine("Utilities.processIcdDiagnosisCode(), failed to get enough options in dropdown.");
                    continue;
                }
            } while (!moreThanEnoughCodes);
            valueReturned = Utilities.processDropdown(dropdownBy, null, sectionIsRandom, true);
        }
        return valueReturned;
    }


    /**
     * This method fills in a text search field, first clearing out any previous value.  No button is pressed.  But
     * entering the info does cause a server request which populates an associated dropdown, which takes time.
     *
     * @param textFieldBy
     * @param text
     * @return Success or failure in entering in a search text field
     */
    private static boolean fillInIcdSearchTextField(final By textFieldBy, String text) {
        WebElement element;
        try {
            element = Utilities.waitForPresence(textFieldBy, 10, "InjuryIllness.fillInIcdSearchTextField()");
        }
        catch (Exception e) {
            logger.severe("Couldn't get the text field: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        try {
            Utilities.sleep(2555, "InjuryIllness.fillInIcdSearchTextField() waiting before calling clear()");
            element.clear();
        }
        catch (Exception e) {
            logger.fine("Utilities.fillInIcdSearchTextField(), failed to clear the element.: " + Utilities.getMessageFirstLine(e));
            //System.out.println("How many times does this have to fail?");
            return false; // just try again, right?
        }
        try {
            element.sendKeys(text);
        }
        catch (Exception e) {
            logger.fine("Utilities.fillInIcdSearchTextField(), either couldn't get the element, or couldn't clear it or couldn't sendKeys.: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        Utilities.sleep(1555, "InjuryIllness.fillInIcdSearchTextField() will next return the text " + text);
        return true;
    }
}
