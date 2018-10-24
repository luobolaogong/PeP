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
import pep.utilities.Utilities;
import pep.utilities.lorem.LoremIpsum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pep.utilities.Driver.driver;

// I don't know for sure, but it seems that each encounter, which is not a followup, has to have a new InjuryIllness section
//
// This represents a section in a Patient Registration page, which contains several sections, some of which are variable
// depending on the level.  The sections are generally divided by horizontal purplish lines.  However, I think it's more
// trouble than it's worth to divide InjuryIllness into subclasses based on these sections.
//
public class InjuryIllness {
    public Boolean random; // true if want this section to be generated randomly
    public String operation; // "option 1-12, required";
    public String injuryNature; // "option 1-5, required";
    public String medicalService; // "option 1-44, required";
    public String mechanismOfInjury;
    public String patientCondition;
    //public String acceptingPhysician;
    public String diagnosisCodeSet; // "option 1-2"; icd9 or icd10
    public String primaryDiagnosis; // "based on search, dropdown";  Should this be considered a "Code", as in "200.31"?  Or a search string like "333"? or a full long string?
    public String assessment; // only levels 1,2,3
    //public String additionalDiagnoses; // for now we'll only allow one, because don't know how to do multiple yet
    //public List<String> additionalDiagnoses; // for now we'll only allow one, because don't know how to do multiple yet
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
            this.receivedTransfusion = null;
            this.transfusedWithUnlicensedBlood = null;
            this.admissionNote = "";
            this.amputation = null;
            this.headTrauma = null;
            this.burns = null;
            this.postTraumaticStressDisorder = null;
            this.eyeTrauma = null;
            this.spinalCordInjury = null;
            this.amputationCause = "";
        }
    }

    // This section is different depending on level.  In level 4 we have "Medical Service".  In levels 1,2,3 there's an Assessment text box.
    // In level 4 there's a "Patient Condition" dropdown.  In levels 1,2,3 there's the CPT Procedure section and Blood Transfusion section
    // and Admission Note text box.  The rest is the same.  So, this gets complicated.
    // This method is too long.  Break it out.
    // This method is too long.  Break it out.
    public boolean process(Patient patient) {
        //if (!Arguments.quiet) System.out.println("    Processing Injury/Illness ...");
        //if (patient.patientRegistration == null || patient.patientRegistration.newPatientReg.demographics == null || patient.patientRegistration.newPatientReg.demographics.firstName == null || patient.patientRegistration.newPatientReg.demographics.firstName.isEmpty()) {
        if (patient.patientRegistration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
                if (!Arguments.quiet) System.out.println("    Processing Injury/Illness ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Injury/Illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }

        //if (Arguments.debug) System.out.println("In InjuryIllness");

        //InjuryIllness injuryIllness = patient.patientRegistration.newPatientReg.injuryIllness;
        InjuryIllness injuryIllness = null;
        if (patient.patientState == PatientState.NEW && patient.patientRegistration.newPatientReg != null && patient.patientRegistration.newPatientReg.injuryIllness != null) {
            injuryIllness = patient.patientRegistration.newPatientReg.injuryIllness;
        }
        if (patient.patientState == PatientState.UPDATE && patient.patientRegistration.updatePatient != null && patient.patientRegistration.updatePatient.injuryIllness != null) {
            injuryIllness = patient.patientRegistration.updatePatient.injuryIllness;
        }



        injuryIllness.operation = Utilities.processDropdown(injuryIllnessOperationDropdownBy, injuryIllness.operation, injuryIllness.random, true);

        injuryIllness.injuryNature = Utilities.processDropdown(injuryNatureDropdownBy, injuryIllness.injuryNature, injuryIllness.random, true);

        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(II_MEDICAL_SERVICE_DROPDOWN));
            // we don't get here unless the dropdown exists.  Would have thrown timeout exception otherwise
            injuryIllness.medicalService = Utilities.processDropdown(II_MEDICAL_SERVICE_DROPDOWN, injuryIllness.medicalService, injuryIllness.random, true);
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("There's no II_MEDICAL_SERVICE_DROPDOWN, which is the case for levels/roles 1,2,3");
        }

        // Mechanism of Injury dropdown isn't active unless Injury Nature indicates an injury rather than illness.
        // If inactive, it's not accessible, supposedly, but Selenium can make it happen!!!!  So we need to check for grayed out, and not just plow through.
        // Probably ought to check the logic here.  Whipped it together fast.
        try {
            WebElement mechanismOfInjuryElement = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(mechanismOfInjuryBy));
            String disabledAttribute = mechanismOfInjuryElement.getAttribute("disabled");
            if (disabledAttribute == null) {
                if (Arguments.debug)
                    System.out.println("InjuryIllness.process(), Didn't find disabled attribute, so not greyed out which means what?  Go ahead and use it.");
                try {
                    (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(mechanismOfInjuryBy));
                    injuryIllness.mechanismOfInjury = Utilities.processDropdown(mechanismOfInjuryBy, injuryIllness.mechanismOfInjury, injuryIllness.random, true);
                } catch (TimeoutException e) {
                    //if (Arguments.debug) System.out.println("InjuryIllness.process(), There's no mechanism of injury dropdown?, which is the case for levels/roles 1,2,3");
                }
            } else {
                if (disabledAttribute.equalsIgnoreCase("true")) {
                    if (Arguments.debug) System.out.println("InjuryIllness.process(), Mechanism of Injury is grayed out.");
                }
            }
        }
        catch (Exception e) {
            //if (Arguments.debug) System.out.println("Couldn't determine Mechanism of Injury element.  So, skip it.");
        }



        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(patientConditionBy));
            injuryIllness.patientCondition = Utilities.processDropdown(patientConditionBy, injuryIllness.patientCondition, injuryIllness.random, true);
        }
        catch (Exception e) {
            System.out.println("prob timed out waiting for whether there was a patient condition dropdown or not, because there wasn't one.");
        }

        // Seems that "Accepting Physician" dropdown exists at levels 1,2,3, but not at level 4.  And for levels 1,2,3
        // nothing drops down if there are no physicians at the site.  For now, skipping this since it's optional.


        // the following assessment text box was the last part, but now it's first part of this section
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(assessmentTextBoxBy));
            injuryIllness.assessment = Utilities.processText(By.xpath("//*[@id=\"patientRegistration.assessment\"]"), injuryIllness.assessment, Utilities.TextFieldType.INJURY_ILLNESS_ASSESSMENT, injuryIllness.random, false);
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("No Assessment text box to enter assessment text.  Probably level 4.  Okay.");
        }

        // Looks like ICD-9 is the default code set, so if you don't change it, there's no alert that comes up.
        // But if you do change it to ICD-10 there is an alert warning about losing info somehow.
        // Maybe even the same alert if you select ICD-9 even when it's already set to ICD-9.

        // Got a problem here.  If this is for an UpdatePatient state, then the previous value may have been 9 or 10
        // and if we set a different state then we get an alert saying "Selecting a new Diagnosis Code Set value
        // will clear all diagnoses associated with this record.  Do you wish to continue?"
        // We cannot assume that the current state is ICD-9.
        // So, we need to read the current value and compare it with the new value
        boolean forceToRequired = true; // next line always, always, always always a problem

        // next line new
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
                //if (Arguments.debug) System.out.println("InjuryIllness.process(), Timed out.  Didn't find an alert, which is probably okay.  Continuing.");
            }
            catch (Exception e) {
                //if (Arguments.debug) System.out.println("InjuryIllness.process(), Didn't find an alert, which is probably okay.  Continuing.");
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
        //if (Arguments.debug) System.out.println("diagnosisCode: " + diagnosisCode);

        // Additional Diagnoses is a list of diagnoses, and they are created in the same way as the primary diagnosis.
        // That is, you have to do 3 or more characters, and then a dropdown can be accessed.
        // But you have to click the Show Additional Diagnoses first.
        //if (Arguments.debug) System.out.println("!!!!!!!!Skipping additional diagnoses for now!!!!!!!!");

        // I think this section needs to be reexamined.  Where are we allocating space?
        int nAdditionalDiagnoses = injuryIllness.additionalDiagnoses == null ? 0 : injuryIllness.additionalDiagnoses.size();
        if (nAdditionalDiagnoses > 0 && !injuryIllness.additionalDiagnoses.get(0).isEmpty()) {
            //WebElement showAdditionalDiagnosesButton = null;
            try {
                WebElement showAdditionalDiagnosesButton = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.elementToBeClickable(showAdditionalDiagnosesButtonBy));
                showAdditionalDiagnosesButton.click();
            }
            catch (Exception e) {
                //if (Arguments.debug) System.out.println("Didn't find a Show Additional Diagnoses button.  Maybe because it says Hide instead.  We're going to continue on...");
            }
            try {
                for (String additionalDiagnosisCode : additionalDiagnoses) {
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
                    //if (Arguments.debug) System.out.println("additionalDiagnosis: " + additionalDiagnosisCode);
                    System.out.println("We should replace the additionalDiagnoses string with the full one, I think.  But do it later.");
                    //this.additionalDiagnoses.add(additionalDiagnosisFullString); // new 10/21/18, not sure at all. Cannot do this because we're looping on this collection
                }
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("Couldn't get or click on the Show Additional Diagnoses button");
                return false;
            }
        }



        // Handle CPT Procedure. (Levels/Roles 1,2,3)
        // The interface has 4 elements: code search, code, search button, and text box for procedure codes.
        // The first three parts are to assist the user in finding the code to put into the list.
        // If you know the code to put into the list, you can just add it to the list and you don't need the other 3 elements.
        //
        // The first part is a text box where you enter a search string, and then you click on the Search CPT button, and that
        // puts a list of possible selections into the dropdown.  Then you select it from the dropdown which puts the corresponding code
        // into the list.  So, just get the codes from the JSON file and add them to the list, with comma separators and no spaces.
        // The list would be a List, but for now we'll just use a String, and make sure it is of the form "code,code,code,code..."
        //
        // To do this randomly there are a couple of ways -- a hard but more uniform way, and an easy but very limited way.
        //
        // A CPT Code consists of a number and related text, like "54125 - AMPUTATION OF ...".  They appear to all start with a
        // number.  You enter a search string in the text search box and then you click on the "Search CPT" button
        // and that causes the CPT Code dropdown to populate with a list of CPT codes that contain the string.
        // The search string can be anything that might match the entire CPT Code, numbers and text.  So, you could
        // enter "54125", or "AMPUTATION", or just "5".  Once the dropdown has been populated you select one of the options.
        // The option selected causes the Procedure Codes text box to have the numeric code added to it, with a comma
        // prefix if there's already a code before it.
        //
        // If we want random values, then going through the initial 3 elements
        // becomes a pain because of the AJAX.  You'd enter a single digit for search, click the Search CPT button, wait
        // for the dropdown to populate, select a random option, and then possibly loop back to do more.
        // There's ample room for failure.
        //
        // The easy but limited way is to just have a limited set of known correct codes and string them together and enter them.
        //
        // When the input encounter JSON file is used (not random), and a value is specified, the value will be for the actual
        // procedure code, and it will be one string, like "10040,82000,14300".  That makes it easy and we just skip
        // the first 3 elements, and just slap that string in.
        //
        // The CPT codes entered into the text box are validated when the Submit button is pressed, and if illegal
        // values are detected, the patient isn't registered.
        //
        //
        try {
            // Check if this Role has CPT section
            //if (Arguments.debug) System.out.println("InjuryIllness.process(), checking for CPT section by checking visibility of cpt procedure codes text box...");
            WebElement procedureCodesTextBox = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(cptProcedureCodesTextBoxBy));
            // If we get here, it means there is a Procedure Codes text box to put codes into.
            //if (Arguments.debug) System.out.println("InjuryIllness.process(), looks like CPT section exists.");
//            // No CPT code was provided
//            if (injuryIllness.procedureCodes == null || injuryIllness.procedureCodes.isEmpty()) {
//                LoremIpsum loremIpsum = LoremIpsum.getInstance(); // not right way?
//
//                int nCodes = Utilities.random.nextInt(5);
//                StringBuffer codes = new StringBuffer();
//                codes.append(loremIpsum.getCptCode());
//
//                for (int ctr = 0; ctr < nCodes; ctr++) {
//                    codes.append("," + loremIpsum.getCptCode());
//                }
//                injuryIllness.procedureCodes = codes.toString();
//
//                procedureCodesTextBox.clear();
//                procedureCodesTextBox.sendKeys(injuryIllness.procedureCodes);
//            }
//            else {
//                // one or more CPT codes was specified in input file, so slam them in.
//                injuryIllness.procedureCodes = Utilities.processText(cptProcedureCodesTextBoxBy, injuryIllness.procedureCodes, Utilities.TextFieldType.CPT_CODES, injuryIllness.random, false);
//            }


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
            //if (Arguments.debug) System.out.println("Did not find CPT Procedure Codes text box, so maybe it doesn't exist at this level/role.  Continuing.");
        }

        // Handle Blood Transfusion checkbox for levels 1,2,3
        try {
//            By receivedTransfusionCheckBoxBy = By.id("patientRegistration.hasBloodTransfusion1");
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(receivedTransfusionCheckBoxBy));
            injuryIllness.receivedTransfusion = Utilities.processBoolean(receivedTransfusionCheckBoxBy, injuryIllness.receivedTransfusion, injuryIllness.random, false);
            if (injuryIllness.receivedTransfusion != null && injuryIllness.receivedTransfusion) {
                injuryIllness.transfusedWithUnlicensedBlood = Utilities.processBoolean(By.id("patientRegistration.hasUnlicensedBloodTransfusion1"), injuryIllness.transfusedWithUnlicensedBlood, injuryIllness.random, false);
            }
        }
        catch (TimeoutException e) {
            //if (Arguments.debug) System.out.println("I guess there's no blood transfusion section or received transfusion button, so not role 1,2,3.  Okay.");
        }

        // There's an error in the web app regarding the identification of the Admission Note text box
        // and how it gets mixed up with Administrative Notes.  In a Level 4 instance, there is no
        // Admission Note text box.  In Level 1,2,3 there is, but if you use its identifier in Level 4
        // it gets written to Administrative Notes text box instead.  So, check first.
        try {
            WebElement admissionNoteLabel = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(admissionNoteLabelBy));
            String admissionNoteLabelText = admissionNoteLabel.getText();
            if (admissionNoteLabelText.contentEquals("Admission Note")) {
                //if (Arguments.debug) System.out.println("Found Admission Note Label so will try to add text to associated text box.");
                injuryIllness.admissionNote = Utilities.processText(admissionNoteBy, injuryIllness.admissionNote, Utilities.TextFieldType.INJURY_ILLNESS_ADMISSION_NOTE, injuryIllness.random, false);
            }
        }
        catch (Exception e) {
            //if (Arguments.debug) System.out.println("Did not find Admission Note label on page, which means we can skip Admission Note.");
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
        return true; // wow, this method doesn't ever return false?
    }

    // Both ICD9 and ICD10 diagnoses can only be entered into TMDS through the diagnosis dropdown, but
    // that dropdown is useless without first specifying a search string.  The search string cannot
    // contain any spaces, I believe.  So if an ICD code is provided, truncate it at the first space
    // and put that into the search field and then wait for the dropdown to populate, then click the
    // first entry, and hopefully that's the right one, although it might not be if there are others
    // with that same initial search string.
    //
    // If no value is specified then the diagnosis should be randomly selected, and so something
    // has to be put into the search field to populate the dropdown, and the first element should be
    // selected from the dropdown.  What random search string should be used?  If ICD9, the diagnoses
    // all start with 3 digits.  For ICD10 they start with a letter followed by two digits.  So
    // such search strings can be generated easily enough, and if there is no match, then we do it
    // again until there's a match.
    //
    // Seems that this way of selecting a random ICD-10 code is not the smartest way because a lot
    // of random search strings don't yield anything.
    //
    public String processIcdDiagnosisCode(String codeSet, By icdTextField, By dropdownBy, String text, Boolean sectionIsRandom, Boolean required) {
        boolean valueIsSpecified = !(text == null || text.isEmpty() || text.equalsIgnoreCase("random"));
        String valueReturned = null;
        if (valueIsSpecified) {
            String[] pieces = text.split(" ");
            String searchString = pieces[0];
            String value = fillInIcdSearchTextField(icdTextField, searchString); // normally takes less than 1 second to get back from server to populate the dropdown
            if (value == null) {
                if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), was unable to fill in text field with text: " + searchString);
            }

            WebElement dropdownElement = null;

            Select select = null;
            try {
                int ctr = 0;
                do {
                    //if (Arguments.debug) System.out.println("top of loop, ctr == " + ctr);
                    Utilities.sleep(777); // In decent server and network conditions I think it takes about a second to populate the dropdown
                    try {
                        // put a sleep of tenth sec here?
                        dropdownElement = (new WebDriverWait(Driver.driver, 5)).until( // was 4
                                ExpectedConditions.refreshed(
                                        ExpectedConditions.presenceOfElementLocated(dropdownBy)));
                    }
                    catch (Exception e3) {
                        if (Arguments.debug) System.out.println("InjuryIllness.processIcdDiagnosisCode(), tried waiting for refreshed presence of element for dropdown.  Exception: " + e3.getMessage());
                        ctr++;
                        continue;
                    }
                    // This next line prints out all the elements it could choose from
                    //if (Arguments.debug) System.out.println("InjuryIllness.process(), got dropdownElement: ->" + dropdownElement.getText() + "<-");

                    select = new Select(dropdownElement);
                    // Following logic could be improved.  I've seen a dropdown that has two options, but it reports as size 1.
                    int nOptions = select.getOptions().size(); // new
                    // assume the first element (0) is not to be chosen, so nOptions must be > 1.
                    // If it's 2, then choose element 1.  If it's 3, choose 1 or 2
                    int selectThisOption = 1;
                    if (nOptions > 1) {
                        selectThisOption = Utilities.random.nextInt(nOptions - 1) + 1;
                        //if (Arguments.debug) System.out.println("Will selection option #" + selectThisOption + " from dropdown: " + dropdownElement.toString());
                    }
                    try {
                        select.selectByIndex(selectThisOption); // first element is 0, throws when index is 1 and ...text is S06.0x1A, 290, S06.0X3A,
                    }
                    catch (Exception e2) {
                        if (Arguments.debug) System.out.println("\tInjuryIllness.processIcdDiagnosisCode(), index " + selectThisOption + " for dropdownBy: " + dropdownBy + ", text: " + text + ", exception: " + e2.getMessage().substring(0,35));
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
                //if (Arguments.debug) System.out.println("InjuryIllness.processIcdDiagnosisCode(), dropdown has this many options: " + select.getOptions().size());
                // just removed this.  Prob had this here because was failing too often.  //select.selectByIndex(1); // throws.  can fail here with NoSuchElementException, cannot locate option with index: 1
                valueReturned = select.getFirstSelectedOption().getText();
            }
            catch(Exception e) {
                if (Arguments.debug) System.out.println("InjuryIllness.processIcdDiagnosisCode(), text: " + text + " Couldn't select an option from dropdown: " + e.getMessage());
                return null;
            }
            //if (Arguments.debug) System.out.println("valueReturned by selecting an element in dropdown after search: " + valueReturned);
        }
        else { // value is not specified

            // REWRITE THIS CRAP
            // REWRITE THIS CRAP
            // REWRITE THIS CRAP
            // REWRITE THIS CRAP
            // REWRITE THIS CRAP
            // REWRITE THIS CRAP

            // this entire section should be rewritten now that I've removed the guessing game.  Shouldn't need to loop.  But leave in for now.
//            By optionOfDiagnosisDropdown = By.xpath("//*[@id=\"patientRegistration.diagnosis\"]/option");
            //int nOptionsRequiredForValidDropdownSelection = 3;
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
                    //text = String.format("%c%02d", Utilities.getRandomLetter(true), Utilities.random.nextInt(100));
                    LoremIpsum loremIpsum = LoremIpsum.getInstance();
                    text = loremIpsum.getIcd10Code(); // test
                }
//                if (codeSet.equalsIgnoreCase("ICD-9")) {
//                    //System.out.println("Why is this never the case now that we get an icd 9?");
//                    text = String.format("%03d", Utilities.random.nextInt(1000));
//                }
                // don't need to do the following if we can grab a random code from lorem.  In that case there would be 2 elements in the dropdown
                //if (Arguments.debug) System.out.println("Here comes a filling of the search text field box with the search string: " + text);
                String value = fillInIcdSearchTextField(icdTextField, text); // icdTextField corresponds to "searchElem" in the JS code in patientReg.html
                if (value == null) {
                    if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), unable to fill in text field with text: " + text);
                    continue;
                }
//                // following is wrong.  Supposed to check the results
//                if (value.equalsIgnoreCase("NO DIAGNOSIS CODE IN MEDICAL RECORD")) {
//                    if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), Maybe this happens because of timing.  Can't get a value: " + text);
//                    continue;
//                }

                try {
                    (new WebDriverWait(driver, 1 + loopCtr)).until(diagnosisCodesMoreThanEnough);
                    moreThanEnoughCodes = true;
                }
                catch (Exception e) {
                    if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), failed to get enough options in dropdown.");
                    continue; // fix up looping later, and get rid of stuff below to count options
                }
            } while (!moreThanEnoughCodes);
            // The problem is that this next line happens too soon, before the server returns the matches.
            valueReturned = Utilities.processDropdown(dropdownBy, null, sectionIsRandom, true); // valueReturned can be "4XX.Xx..." but the dropdown says "Select Diagnosis"
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // works here?  No, no ajax on page
            //if (Arguments.debug) System.out.println("processIcdDiagnosisCode(), valueReturned in processing diagnosis code: " + valueReturned);
            // new, untested, and probably wrong.  No, this doesn't work because sometimes the value doesn't come back.
//            if (valueReturned.equalsIgnoreCase("NO DIAGNOSIS CODE IN MEDICAL RECORD")) {
//                if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), Maybe this happens because of timing.  Can't get a value: " + text);
//                return null;
//            }
        }
        //if (Arguments.debug) System.out.println("processIcdDiagnosisCode(), Leaving processIcd10DiagnosisCode() and returning " + valueReturned);
        return valueReturned;
    }

    // Assume that when you enter some text in a search text field, it's going to cause a server request to populate an
    // associated dropdown.  For example ICD-9 and ICD-10.  This takes some time, and this method should perhaps
    // pause after the text is input to give the server a chance to populate the dropdown.  There's not much difference
    // between this method and fillInSearchField.
    private static String fillInIcdSearchTextField(final By textFieldBy, String text) {
        //if (Arguments.debug) System.out.println("In fillInIcdSearchTextField() with text: " + text + " and field: " + textFieldBy.toString());
        WebElement element = null;

        try {
            element = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(textFieldBy));
        }
        catch (Exception e) {
            System.out.println("Couldn't get the text field: " + e.getMessage());
            return null;
        }
        try {
            //if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), going to clear element");
            Utilities.sleep(1555); // what the crap I hate to do this but what the crap why does this fail so often?
            element.clear(); // this fails often!!!!! "Element is not currently interactable and may not be manipulated"
        }
        catch (Exception e) { // invalid element state
            if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), failed to clear the element.: ->" + e.getMessage().substring(0,60) + "<-");
            return null; // Fails: 4 is this the right thing to do?  Go on anyway? failed when slow 3g
        }
        try {
            //if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), going to send the element this text: " + text);
            element.sendKeys(text); // this takes a half second to cause population of the dropdown.  Maybe longer.
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), either couldn't get the element, or couldn't clear it or couldn't sendKeys.: " + e.getMessage());
            return null;
        }

        Utilities.sleep(1555); // I hate doing this but don't know how to wait for dropdown to populate (was 750?)

        //if (Arguments.debug) System.out.println("Leaving Utilities.fillInIcdSearchTextField(), returning text: " + text);
        return text; // probably should return the text that was sent in.
    }
}
