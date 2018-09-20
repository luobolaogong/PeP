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
    public String acceptingPhysician;
    public String diagnosisCodeSet; // "option 1-2"; icd9 or icd10
    public String primaryDiagnosis; // "based on search, dropdown";
    public String assessment; // only levels 1,2,3
    public String additionalDiagnoses; // for now we'll only allow one, because don't know how to do multiple yet

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

    private static final By II_MEDICAL_SERVICE_DROPDOWN = By
            .xpath("//select[@name='patientRegistration.medicalService']");

    // Injury/Illness Checkboxes
    private static final By II_AMPUTATION_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare1'])");
    private static final By II_BURNS_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare2'])");
    private static final By II_EYE_TRAUMA_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare3'])");
    private static final By II_HEAD_TRAUMA_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare4'])");
    private static final By II_PTSD_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare5'])");
    private static final By II_SPINAL_CORD_INJURY_CHECKBOX = By
            .xpath("(//input[@id='patientRegistration.enablingCare6'])");

    private static final By II_EXPLOSION_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[1]/label");
    private static final By II_GSW_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[2]/label");
    private static final By II_GRENADE_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[3]/label");
    private static final By II_LAND_MINE_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[4]/label");
    private static final By II_MVA_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[5]/label");
    private static final By II_OTHER_RADIO_BUTTON_LABEL = By
            .xpath("//*[@id=\"patientRegForm\"]//table/tbody/tr[3]/td/span[6]/label");
    private static final By injuryIllnessOperationDropdownBy = By.id("patientRegistration.operation");
    private static final By injuryNatureDropdownBy = By.id("patientRegistration.injuryNature");
    private static final By mechanismOfInjuryBy = By.id("patientRegistration.mechOfInjury");
    private static final By patientConditionBy = By.id("patientRegistration.patientCondition");
    private static final By diagnosisCodeSetDropdownBy = By.id("patientRegistration.codeType");
    private static final By primaryDiagnosisFieldBy = By.id("diagnosisSearch");
    private static final By primaryDiagnosisDropdownBy = By.id("patientRegistration.diagnosis");
    private static final By assessmentTextBoxBy = By.id("patientRegistration.assessment");
    private static final By cptProcedureCodesTextBoxBy = By.id("cptCodesTextlist");
    private static final By receivedTransfusionCheckBoxBy = By.id("patientRegistration.hasBloodTransfusion1");
    private static final By admissionNoteLabelBy = By.xpath("//*[@id=\"patientRegForm\"]/table/tbody/tr/td[2]/table[4]/tbody/tr/td/table[7]/tbody/tr[2]/td/h4");
    private static final By admissionNoteBy = By.id("patientRegistration.notes");
    private static final By optionOfDiagnosisDropdown = By.xpath("//*[@id=\"patientRegistration.diagnosis\"]/option");


    public InjuryIllness() {
        if (Arguments.template) {
            this.random = null;
            this.operation = "";
            this.injuryNature = "";
            this.medicalService = "";
            this.mechanismOfInjury = "";
            this.patientCondition = "";
            this.acceptingPhysician = "";
            this.diagnosisCodeSet = "";
            this.primaryDiagnosis = "";
            this.assessment = "";
            this.additionalDiagnoses = "";
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

    // This section is different depending on level.  In level 4 we have "Medical Service".  In levels 1,2,3 there's an Assessment text box.
    // In level 4 there's a "Patient Condition" dropdown.  In levels 1,2,3 there's the CPT Procedure section and Blood Transfusion section
    // and Admission Note text box.  The rest is the same.  So, this gets complicated.
    // This method is too long.  Break it out.
    public boolean process(Patient patient) {
        //if (!Arguments.quiet) System.out.println("    Processing Injury/Illness ...");
        //if (patient.patientRegistration == null || patient.patientRegistration.newPatientReg.demographics == null || patient.patientRegistration.newPatientReg.demographics.firstName == null || patient.patientRegistration.newPatientReg.demographics.firstName.isEmpty()) {
        if (patient.patientRegistration == null || patient.patientSearch == null || patient.patientSearch.firstName == null) {
                if (!Arguments.quiet) System.out.println("    Processing Injury/Illness ...");
        }
        else {
            if (!Arguments.quiet) System.out.println("    Processing Injury/Illness for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");
        }

        if (Arguments.debug) System.out.println("In InjuryIllness");

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
            if (Arguments.debug) System.out.println("There's no II_MEDICAL_SERVICE_DROPDOWN, which is the case for levels/roles 1,2,3");
        }

        // Mechanism of Injury dropdown isn't active unless Injury Nature indicates an injury rather than illness.
        // If inactive, it's not accessible.  There should be a way to check.
        try {
            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(mechanismOfInjuryBy));
            injuryIllness.mechanismOfInjury = Utilities.processDropdown(mechanismOfInjuryBy, injuryIllness.mechanismOfInjury, injuryIllness.random, true);
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("There's no mechanism of injury dropdown?, which is the case for levels/roles 1,2,3");
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
            if (Arguments.debug) System.out.println("No Assessment text box to enter assessment text.  Probably level 4.  Okay.");
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

//        injuryIllness.diagnosisCodeSet = Utilities.random.nextBoolean() ? "ICD-9" : "ICD-10";
//        injuryIllness.diagnosisCodeSet = Utilities.processDropdown(diagnosisCodeSetDropdownBy, injuryIllness.diagnosisCodeSet, injuryIllness.random, forceToRequired);
//        // Hey, we only need to do this next part if the code set changes from what it was.  Starts out as ICD-9, but is that always the case when we get here?  Probably.
//        if (injuryIllness.diagnosisCodeSet != null && injuryIllness.diagnosisCodeSet.equalsIgnoreCase("ICD-10")) {
//            try {
//                Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
//            }
//            catch (Exception e) {
//                if (Arguments.debug) System.out.println("InjuryIllness.process(), Didn't find an alert, which is probably okay.  Continuing.");
//            }
//        }
        injuryIllness.diagnosisCodeSet = Utilities.random.nextBoolean() ? "ICD-9" : "ICD-10";
        // get current value
        //By diagnosisCodeSetDropdownBy = By.id("patientRegistration.codeType");
        WebElement diagnosisCodeSetDropdown = Driver.driver.findElement(diagnosisCodeSetDropdownBy);
        Select select = new Select(diagnosisCodeSetDropdown);
        WebElement firstSelectedOption = select.getFirstSelectedOption();
        String currentOption = firstSelectedOption.getText();
        if (!injuryIllness.diagnosisCodeSet.equals(currentOption)) {
            injuryIllness.diagnosisCodeSet = Utilities.processDropdown(diagnosisCodeSetDropdownBy, injuryIllness.diagnosisCodeSet, injuryIllness.random, forceToRequired);
            try {
                Driver.driver.switchTo().alert().accept(); // this can fail? "NoAlertPresentException"
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("InjuryIllness.process(), Didn't find an alert, which is probably okay.  Continuing.");
            }
        }



        // Always and forever this is a problem child
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
//        processIcdDiagnosisCode(
//                injuryIllness.diagnosisCodeSet,
//                primaryDiagnosisFieldBy,
//                primaryDiagnosisDropdownBy,
//                injuryIllness.primaryDiagnosis,
//                injuryIllness.random,
//                forceToRequired);
        String diagnosisCode = processIcdDiagnosisCode(
                injuryIllness.diagnosisCodeSet,
                primaryDiagnosisFieldBy,
                primaryDiagnosisDropdownBy,
                injuryIllness.primaryDiagnosis,
                injuryIllness.random,
                forceToRequired);

        if (diagnosisCode == null) {
            if (!Arguments.quiet) System.err.println("***Could not process ICD diagnosis code for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName);
            return false;
        }
        if (Arguments.debug) System.out.println("diagnosisCode: " + diagnosisCode);
        // Moving following to up above because assessment text can accidentally partially get put into the primary diagnosis search box.  wierdly.  because of stupid way primary diagnosis is done
//        System.out.println("Hey, what is the diagnosisCode if later it says it was needed and didn't get one????????????????????????: " + diagnosisCode);
//        try {
//            (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(assessmentTextBoxBy));
//            injuryIllness.assessment = Utilities.processText(By.xpath("//*[@id=\"patientRegistration.assessment\"]"), injuryIllness.assessment, Utilities.TextFieldType.INJURY_ILLNESS_ASSESSMENT, injuryIllness.random, false);
//        }
//        catch (TimeoutException e) {
//            if (Arguments.debug) System.out.println("No Assessment text box to enter assessment text.  Probably level 4.  Okay.");
//        }

            // Additional Diagnoses.  This is not ready.  Have to do 3 or more characters, and then a dropdown can be accessed.
        // Plus, this is a list.  Can have multiple.
        // Plus you have to click the Show Additional Diagnoses first.
        // Currently not allowing list.
        // Can we skip that part, and just enter a string if the user provides it?  Cannot use "random", because that would
        // required three characters to allow the input into a dropdown.
        if (Arguments.debug) System.out.println("!!!!!!!!Skipping additional diagnoses for now!!!!!!!!");


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
        try {
            WebElement procedureCodesTextBox = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(cptProcedureCodesTextBoxBy));
            // The following is for random.  MOVE THIS INTO LOREM
            if (injuryIllness.procedureCodes == null || injuryIllness.procedureCodes.isEmpty()) {
                // The following codes all have "9" in them, and should be all the code that contain "9", and should be valid codes.
                String[] list = {"82009","29440","20693","59000","24920","24930","24925","24931",
                        "24900","26951","26952","25900","25905","25909","25907","26910","27590","27591",
                        "27592","27596","27594","1390","1392","1920","1916","1490","1922","1190","190",
                        "192","932","938","920","930","936","934","928","926","924","910","916","912",
                        "914","918","948","942","950","952","940","944","902","904","906","75898","75790",
                        "27889","86689","44960","44955","20692","20690","29035","29046","29040","29044",
                        "29450","29131","29130","29000","29325","29305","29105","29345","29355","29358",
                        "29505","29435","29015","29010","29126","29125","29405","29425","29515","29025",
                        "29020","29075","29085","29058","29055","29065","27479","22595","22590","29888",
                        "29889","29898","29897","29895","29894","29830","29838","29837","29836","29835",
                        "29879","29877","29886","29885","29871","29874","29875","29876","29884","29880",
                        "29881","29883","29882","29823","29826","29821","29820","29825","29819","29800",
                        "29804","29840","29846","29843","29847","29845","29844","50390","69105","69100",
                        "19101","19100","40490","64795","58900","62269","49180","21925","21920","86079",
                        "20962","20902","20900","85097","19364","19366","19357","31629","35549","35509",
                        "38794","67950","20910","20912","89050","59325","59320","82390","15819","59515",
                        "75984","74291","82489","82495","24940","28495","28490","21494","21493","25690",
                        "50920","50930","51920","51925","51900","69700","85293","85292","69930","44393",
                        "44389","44391","44390","44392","45379","33692","33694","72193","72192","72194",
                        "70491","70492","72129","57292","57291","30903","30901","30905","30906","42971",
                        "42970","42972","42961","42960","42962","59012","26499","19355","67911","28290",
                        "28294","28299","61519","61490","61539","61790","61791","62190","62192","82595",
                        "46937","46938","89060","46942","59160","51980","51596","51590","51595","52290",
                        "1996","69222","69220","69725","69720","24495","69960","19342","59414","29590",
                        "46935","46934","46936","46924","46916","46910","46917","46922","82649","45905",
                        "45910","35092","35091","27598","27295","23920","23921","25920","25924","25922",
                        "72295","33940","33930","69020","69005","69000","49020","49060","49040","89105",
                        "89100","69090","20975","20974","95827","95819","88349","58974","69806","69805",
                        "74329","29848","51960","54901","54900","43269","43219","82679","82690","82696",
                        "69405","69400","69401","65093","65091","66986","67966","69554","69550","69552",
                        "69540","69140","69120","69110","26390","19260","19272","19271","26596","19120",
                        "40819","19112","28090","28092","39200","39220","64792","64790","50290","49215",
                        "49200","49201","22900","15922","15920","15839","15946","15940","15941","15944",
                        "15945","15936","15937","15931","15933","15934","15935","15956","15958","15951",
                        "15952","15953","27619","21930","49010","49000","61559","59412","66984","20922",
                        "20920","89125","69820","59020","59050","59025","85390","58970","30930","20973",
                        "20970","20972","20969","82759","58976","82926","82928","89135","89140","82941",
                        "82938","78891","78890","82943","82946","82955","82960","82950","82953","82951",
                        "82952","82963","82965","82975","82977","82979","82980","82985","28298","28292",
                        "28297","28293","28296","33945","33935","83069","75889","75891","83491","59350",
                        "59100","39545","19340","86329","69710","21079","44900","26990","26991","23931",
                        "23930","64590","26992","23935","59840","59850","59851","59852","49400","62292",
                        "62291","62290","27095","27093","50394","50690","19030","11901","11900","95830",
                        "59200","33970","66985","49421","49420","49425","11960","71090","11975","21497",
                        "27290","55980","55970","79300","23900","79440","66983","79200","41009","79420",
                        "50395","50392","50393","78190","25915","69905","69910","69801","69802","63290",
                        "63198","63199","63196","63197","63194","63195","63190","63191","59151","59150",
                        "58960","31590","31529","31579","27395","27394","27393","88309","30920","46946",
                        "46945","37609","42890","50590","78195","72196","19325","19324","76096","76091",
                        "76090","50396","42409","19140","19240","19160","19162","19220","19200","19180",
                        "19182","69670","69502","69505","69511","19316","19020","39400","39000","39010",
                        "69440","20950","85549","95834","23397","78469","69620","69420","69421","64898",
                        "64897","64896","64895","64893","64892","64891","64890","64902","64901","64905",
                        "64907","64719","19110","19350","49255","58943","58940","19370","27519","27792",
                        "24579","21495","25695","21339","21395","21390","27179","27259","21490","76519",
                        "76529","26494","26496","26492","23190","28119","21209","25393","25392","25391",
                        "25390","21198","28309","27709","69300","58925","42509","88329","51597","47490",
                        "75982","75940","28496","57289","19371","78291","49080","49081","69530","42953",
                        "31395","31390","42950","1990","36490","36491","36489","54390","78191","32940",
                        "32960","59430","19396","33960","27495","23490","23491","25490","25492","25491",
                        "24498","78990","79900","33910","33915","78596","78591","78594","78593","19000",
                        "19001","77409","69155","69150","21935","27329","27079","27049","72190","72069",
                        "72090","73090","73592","70190","73590","73092","74249","76098","75989","79030",
                        "79035","79000","79001","79400","79100","79020","95852","69320","21159","69310",
                        "67975","67973","67974","67971","21249","21195","21193","21196","21194","21179",
                        "21139","19318","21295","21296","67909","45900","1995","22849","27097","21029",
                        "69205","69200","69210","65930","67938","65900","45915","42809","28193","28192",
                        "28190","27090","27091","65920","19328","33971","66940","66920","66930","19330",
                        "49085","26392","11971","69970","29705","29710","29715","69711","11976","20694",
                        "50559","49002","49570","49525","49550","49560","49505","49500","49540","26590",
                        "67904","67903","67908","67902","67901","67906","67900","63709","67917","67914",
                        "67915","67923","67924","67921","67922","53449","49605","49606","49610","49600",
                        "29720","33779","65290","69666","49555","49565","69667","49590","49580","35190",
                        "35189","39530","39520","39540","39541","27659","26591","39501","39503","39502",
                        "27695","27409","27698","11970","62194","36493","42892","58950","58951","58952",
                        "42894","32900","69535","69840","69601","69603","69604","69605","25449","49426",
                        "19380","69662","64595","15829","59510","59400","76092","31595","87197","26479",
                        "70390","36469","31090","44369","49220","69660","69661","69650","61795","51792",
                        "29540","29260","29280","29520","29530","29240","29200","29550","29580","24935",
                        "59525","15879","77790","77789","61692","61690","59130","59140","59135","59136",
                        "59120","59121","87190","69745","69740","64859","67935","67930","42900","27696",
                        "49900","64809","25119","86593","11951","11952","11920","11921","11922","11954",
                        "11950","20924","26489","26498","26449","25295","25290","27392","27391","27390",
                        "77290","32905","32906","32095","34490","84479","20926","69955","75970","75961",
                        "75894","75896","26497","27692","27690","27098","27691","75964","75968","35459",
                        "75962","75966","75978","69501","25927","25931","27397","59812","59820","59821",
                        "59830","84490","69610","69676","69450","69632","69633","69631","69636","69637",
                        "69635","69645","69644","69643","69642","69641","69436","69433","76946","76948",
                        "76932","76942","76930","76950","76986","76970","49250","1999","78499","84999",
                        "21299","76499","78099","78299","78799","78199","78999","78399","21499","78699",
                        "22999","49999","31299","46999","47999","19499","33999","29799","77799","68399",
                        "41899","39599","60699","43499","15999","69399","67999","58999","27599","28899",
                        "25999","26989","38999","24999","69949","44799","68899","31599","27899","40799",
                        "47399","32999","55899","44899","39499","77399","69799","20999","21899","64999",
                        "30999","67399","67599","42299","48999","27299","42999","67299","45999","42699",
                        "23929","17999","22899","43999","69979","77299","77499","41599","31899","53899",
                        "36299","37799","40899","79999","78599","88399","76999","81099","43239","50951",
                        "50955","50957","50959","50961","50970","50974","50976","50978","50980","50972",
                        "50900","59870","59410","75893","69424","63091","63090","69950","69915","67039",
                        "51795","51797","58920","29740","29750","29730",};
                StringBuffer codes = new StringBuffer(list.length);
                codes.append(list[Utilities.random.nextInt(list.length)]);
                int nCodes = Utilities.random.nextInt(5);
                for (int ctr = 0; ctr < nCodes; ctr++) {
                    codes.append("," + list[Utilities.random.nextInt(list.length)]);
                }
                injuryIllness.procedureCodes = codes.toString();
                //WebElement procedureCodesTextBox = Utilities.automationUtils.waitUntilElementIsVisible(cptProcedureCodesTextBoxBy);
                procedureCodesTextBox.clear();
                procedureCodesTextBox.sendKeys(injuryIllness.procedureCodes);
            }
            else {
                injuryIllness.procedureCodes = Utilities.processText(cptProcedureCodesTextBoxBy, injuryIllness.procedureCodes, Utilities.TextFieldType.CPT_CODES, injuryIllness.random, false);
            }

        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("Did not find CPT Procedure Codes text box, so maybe it doesn't exist at this level/role.  Continuing.");
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
            if (Arguments.debug) System.out.println("I guess there's no blood transfusion section or received transfusion button, so not role 1,2,3.  Okay.");
        }

        // There's an error in the web app regarding the identification of the Admission Note text box
        // and how it gets mixed up with Administrative Notes.  In a Level 4 instance, there is no
        // Admission Note text box.  In Level 1,2,3 there is, but if you use its identifier in Level 4
        // it gets written to Administrative Notes text box instead.  So, check first.
        try {
            WebElement admissionNoteLabel = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(admissionNoteLabelBy));
            String admissionNoteLabelText = admissionNoteLabel.getText();
            if (admissionNoteLabelText.contentEquals("Admission Note")) {
                if (Arguments.debug) System.out.println("Found Admission Note Label so will try to add text to associated text box.");
                injuryIllness.admissionNote = Utilities.processText(admissionNoteBy, injuryIllness.admissionNote, Utilities.TextFieldType.INJURY_ILLNESS_ADMISSION_NOTE, injuryIllness.random, false);
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Did not find Admission Note label on page, which means we can skip Admission Note.");
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
        return true;
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
    public String processIcdDiagnosisCode(String codeSet, By icdTextField, By dropdown, String text, Boolean sectionIsRandom, Boolean required) {
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
                    if (Arguments.debug) System.out.println("top of loop, ctr == " + ctr);
                    Utilities.sleep(777); // In decent server and network conditions I think it takes about a second to populate the dropdown
                    try {
                        // put a sleep of tenth sec here?
                        dropdownElement = (new WebDriverWait(Driver.driver, 4)).until(
                                ExpectedConditions.refreshed(
                                        ExpectedConditions.presenceOfElementLocated(dropdown)));
                    }
                    catch (Exception e3) {
                        if (Arguments.debug) System.out.println("Ex3: " + e3.getMessage());
                        ctr++;
                        continue;
                    }
                    if (Arguments.debug) System.out.println("InjuryIllness.process(), got dropdownElement: ->" + dropdownElement.getText() + "<-");

                    select = new Select(dropdownElement);
                    try {
                        select.selectByIndex(1); // first element is 0
                    }
                    catch (Exception e2) {
                        if (Arguments.debug) System.out.println("Ex2: " + e2.getMessage());
                        ctr++;
                        continue;
                    }
                    WebElement firstSelectedOption = select.getFirstSelectedOption();
                    String firstSelectedOptionText = firstSelectedOption.getText();
                    if (Arguments.debug) System.out.println("first: " + firstSelectedOptionText);
                    if (!firstSelectedOptionText.contains("Select Diagnosis") && !firstSelectedOptionText.contains("NO DIAGNOSIS CODE")) {
                        break;
                    }
                    ctr++;
                } while (ctr < 20);
                if (Arguments.debug) System.out.println("InjuryIllness.processIcdDiagnosisCode(), dropdown has this many options: " + select.getOptions().size());
                select.selectByIndex(1); // throws.  can fail here with NoSuchElementException, cannot locate option with index: 1
                valueReturned = select.getFirstSelectedOption().getText();
            }
            catch(Exception e) {
                if (Arguments.debug) System.out.println("InjuryIllness.processIcdDiagnosisCode(), couldn't select an option from dropdown: " + e.getMessage());
                return null;
            }
            if (Arguments.debug) System.out.println("valueReturned by selecting first element in dropdown after search: " + valueReturned);
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
                if (Arguments.debug) System.out.println("Here comes a filling of the search text field box with the search string: " + text);
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
            valueReturned = Utilities.processDropdown(dropdown, null, sectionIsRandom, true); // valueReturned can be "4XX.Xx..." but the dropdown says "Select Diagnosis"
            //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // works here?  No, no ajax on page
            if (Arguments.debug) System.out.println("processIcdDiagnosisCode(), valueReturned in processing diagnosis code: " + valueReturned);
            // new, untested, and probably wrong.  No, this doesn't work because sometimes the value doesn't come back.
//            if (valueReturned.equalsIgnoreCase("NO DIAGNOSIS CODE IN MEDICAL RECORD")) {
//                if (Arguments.debug) System.out.println("Utilities.processIcdDiagnosisCode(), Maybe this happens because of timing.  Can't get a value: " + text);
//                return null;
//            }
        }
        if (Arguments.debug) System.out.println("processIcdDiagnosisCode(), Leaving processIcd10DiagnosisCode() and returning " + valueReturned);
        return valueReturned;
    }

    // Assume that when you enter some text in a search text field, it's going to cause a server request to populate an
    // associated dropdown.  For example ICD-9 and ICD-10.  This takes some time, and this method should perhaps
    // pause after the text is input to give the server a chance to populate the dropdown.  There's not much difference
    // between this method and fillInSearchField.
    private static String fillInIcdSearchTextField(final By textFieldBy, String text) {
        if (Arguments.debug) System.out.println("In fillInIcdSearchTextField() with text: " + text + " and field: " + textFieldBy.toString());
        WebElement element = null;

        try {
            element = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(textFieldBy));
        }
        catch (Exception e) {
            System.out.println("Couldn't get the text field: " + e.getMessage());
            return null;
        }
        try {
            if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), going to clear element");
            Utilities.sleep(1555); // what the crap I hate to do this but what the crap why does this fail so often?
            element.clear(); // this fails often!!!!! "Element is not currently interactable and may not be manipulated"
        }
        catch (Exception e) { // invalid element state
            if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), failed to clear the element.: ->" + e.getMessage() + "<-");
            return null; // Fails: 4 is this the right thing to do?  Go on anyway? failed when slow 3g
        }
        try {
            if (Arguments.debug)
                System.out.println("Utilities.fillInIcdSearchTextField(), going to send the element this text: " + text);
            element.sendKeys(text); // this takes a half second to cause population of the dropdown.  Maybe longer.
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Utilities.fillInIcdSearchTextField(), either couldn't get the element, or couldn't clear it or couldn't sendKeys.: " + e.getMessage());
            return null;
        }

        Utilities.sleep(1555); // I hate doing this but don't know how to wait for dropdown to populate (was 750?)

        if (Arguments.debug) System.out.println("Leaving Utilities.fillInIcdSearchTextField(), returning text: " + text);
        return text; // probably should return the text that was sent in.
    }
}
