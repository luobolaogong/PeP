package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static pep.Pep.isDemoTier;
import static pep.utilities.Driver.driver;

public class PatientInformation {
    public Boolean random;
    public SelectedPatientInformation selectedPatientInformation;
    public PermanentHomeOfRecord permanentHomeOfRecord;
    public EmergencyContact emergencyContact;
    public ImmediateNeeds immediateNeeds;


    //private static By  patientRegistrationMenuLinkBy = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    private static By  patientRegistrationMenuLinkBy = By.id("i4000");

    //private static By  patientInformationPageLinkBy = By.xpath("//*[@id=\"links\"]/li/span/b/a[@href=\"/tmds/patientInformation.html\"]");
    private static By  patientInformationPageLinkBy = By.xpath("//*[@id=\"links\"]//a[@href=\"/tmds/patientInformation.html\"]");
    //private static By  patientInformationPageLinkBy = By.partialLinkText("/tmds/patientInformation.html");
    //private static By  patientInformationPageLinkBy = By.id("a_3"); // for a Role 4
    //private static By  patientInformationPageLinkBy = By.id("a_2"); // for a Role 3 (if Role 4 this one is Update Patient)


    public static By submitButtonByBy = By.xpath("//*[@id=\"patientInformationForm\"]/table[8]/tbody/tr/td/input");


    public PatientInformation() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.selectedPatientInformation = new SelectedPatientInformation(); // wrong of course
            this.permanentHomeOfRecord = new PermanentHomeOfRecord();
            this.emergencyContact = new EmergencyContact();
            this.immediateNeeds = new ImmediateNeeds();
        }
        if (isDemoTier) {
        }

    }


    public boolean process(Patient patient) {
        boolean succeeded = true; // Why not start this out as true?  Innocent until proven otherwise

        // Is this right here?
        if (patient.patientSearch != null && patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty()) { // npe
            if (!Arguments.quiet)
                System.out.println("    Processing Patient Information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn + " ...");
        }
        else {
            if (!Arguments.quiet)
                System.out.println("    Processing Patient Information ...");
        }

        // huh?  Don't do the thing with random here to inherit parent's random?
        if (this.random == null) {
            this.random = patient.random;
            // It really should be this one instead, I think:
            //this.random = patient.patientRegistration.random;
        }

        Utilities.sleep(555);
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientInformationPageLinkBy);
        if (Arguments.debug) System.out.println("Navigated?: " + navigated);
        if (!navigated) {
            return false; // fails: level 4 demo: 1, gold 1
        }

        boolean proceedWithPatientInformation = isPatientFound(patient.patientSearch.ssn, patient.patientSearch.lastName, patient.patientSearch.firstName, patient.patientSearch.traumaRegisterNumber);

        if (proceedWithPatientInformation) {
            succeeded = doPatientInformation(patient);
            return succeeded;
        }
        else {
            return false;
        }
    }

    boolean isPatientFound(String ssn, String lastName, String firstName, String tramaRegisterNumber) {
        By ssnBy = By.id("ssn");
        By lastNameBy = By.id("lastName");
        By firstNameBy = By.id("firstName");
        By traumaRegisterNumberBy = By.id("registerNumber");
        By searchForPatientBy = By.xpath("//*[@id=\"patientInfoSearchForm\"]/table[2]/tbody/tr/td/table/tbody/tr[4]/td/input");

        try {
            // let's try to wait for ssn's field to show up before trying to do a find of it
            WebElement ssnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(ssnBy));
            //WebElement ssnField = Driver.driver.findElement(ssnBy);
            ssnField.sendKeys(ssn);
            Driver.driver.findElement(lastNameBy).sendKeys(lastName);
            Driver.driver.findElement(firstNameBy).sendKeys(firstName);
            Driver.driver.findElement(traumaRegisterNumberBy).sendKeys(tramaRegisterNumber);
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("PatientInformation.isPatientFound(), e: " + e.getMessage());
            return false;
        }

        // This click will only find patients at Role 4 if was created at Role 4.  Isn't that strange?  Is it right?
        Driver.driver.findElement(searchForPatientBy).click();

        // The above click causes a major change to the DOM, including the selectors/locators for the search elements.
        // This isn't a new navigtion, I don't think.
        // We have to do something to get the updated DOM before we can get to any of the fields.
        // Selenium is supposed to automatically update the DOM, but it's not what I'm seeing, I think.
        //
        // For this method all we need to know is whether we advanced to the real Patient Information page
        // from the previous one that only contained Search For Patient section.
        // After clicking the button I think there are three possibilities: Not found, one found, multiple found.
        // If multiple found then we have a popup window.  If one found, we just advance and the new Search For
        // Patient section is all grayed out.  If none found then we have a message and the fields are still there,
        // with the same locators, and not grayed out.


        //
        // Maybe it's simpler just to check for the message and forget about grayed out fields.
        // Either way, we'd have to wait for a response.



        // Or you can see if there's new stuff showing up below the search area.
        // Maybe you can even wait for stuff to go stale, I don't know.

        // Since these search fields get rewritten, maybe we just wait until they go
        // stale, and maybe that would signal that a patient was found.  Maybe.
        // Check that, and also check what happens if a patient was not found.  Still goes
        // stale?  If so, don't use that method.

        // Probably the best way to tell is if the fields go gray.  But let's try both:
        // wait for the original ssn field to go stale, and then get the new one and check for
        // gray.
//        try {
//            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf((ssnField)));
//        }
//        catch (TimeoutException e) {
//            System.out.println("Waited for staleness, but never went stale, meaning what?  Yes can get here even if patient found");
//            By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
//            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy));
//            String searchMessageAreaText = searchMessageArea.getText();
//            System.out.println("Message: " + searchMessageAreaText);
//            return false;
//        }
//        catch (Exception e) {
//            System.out.println("Waited for staleness, but never went stale, meaning what?  Yes can get here even if patient found");
//            By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
//            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy));
//            String searchMessageAreaText = searchMessageArea.getText();
//            System.out.println("Message: " + searchMessageAreaText);
//            return false;
//        }

//        // Check on new SSN field and see if it went gray
//        By differentSsnBy = By.xpath("//*[@id=\"patientInformationForm\"]/table[2]/tbody/tr/td/table/tbody/tr[2]/td[1]/input");
//        WebElement newSsnField = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.presenceOfElementLocated(differentSsnBy));
//        String disabledAttribute = newSsnField.getAttribute("disabled");
//        if (disabledAttribute != null) {
//            if (disabledAttribute.equalsIgnoreCase("true")) {
//                System.out.println("Probably patient was found.");
//                return true;
//            }
//        }
//        else {
//            System.out.println("Probably patient was not found.");
//            By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li");
//            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy));
//            String searchMessageAreaText = searchMessageArea.getText();
//            System.out.println("Message: " + searchMessageAreaText);
//            return false;
//        }

        // If there are no patients found, then there will be a <div id="errors", and under it a <ul> <li>There are nopatients found.<li>,/ul></div><br>
        // But if a single patient is found, then there's no message
        // And if more than one patient is found, then probably brings up something where you can choose the patient

       // (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // maybe this will help
        try {
            By searchMessageAreaBy = By.xpath("//*[@id=\"errors\"]/ul/li"); // verified
            WebElement searchMessageArea = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(searchMessageAreaBy));
            String searchMessageAreaText = searchMessageArea.getText();
            if (searchMessageAreaText.equalsIgnoreCase("There are no patients found.")) {
                if (!Arguments.quiet) System.err.println("***Could not find patient.  No longer active?  Departed from facility?  Message says: " + searchMessageAreaText);
                return false; // could it be because the patient was departed? Yes!
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("PatientInformation.isPatientFound(), Prob okay.  Couldn't find a message about search, so a patient was probably found.");
        }


        return true;
    }

    boolean doPatientInformation(Patient patient) {

        //By errorMessagesBy = By.id("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        boolean succeeded;
        succeeded = doSelectedPatientInformation(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doPermanentHomeOfRecord(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doEmergencyContact(patient);
        if (!succeeded) {
            return false;
        }

        succeeded = doImmediateNeeds(patient);
        if (!succeeded) {
            return false;
        }


        // The next line doesn't block until the patient gets saved.  It generally takes about 4 seconds before the spinner stops
        // and next page shows up.   Are all submit buttons the same?
        Utilities.clickButton(submitButtonByBy); // Not AJAX, but does call something at /tmds/patientRegistration/ssnCheck.htmlthis takes time.  It can hang too.  Causes Processing request spinner

        //if (Arguments.debug) System.out.println("patientInformation.process() will now check for successful patient record creation, or other messages.  This seems to block okay.");
//        try {
//            By spinnerPopupWindowBy = By.id("MB_window");
//            // This next line assumes execution gets to it before the spinner goes away.
//            // Also the next line can throw a WebDriverException due to an "unexpected alert open: (Alert text : The SSN you have provided is already associated with a different patient.  Do you wish to continue?"
//            if (Arguments.debug) System.out.println("Waiting for visibility of spinner");
//            WebElement spinnerPopupWindow = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(spinnerPopupWindowBy)); // was 15
//            if (Arguments.debug) System.out.println("Waiting for staleness of spinner");
//            (new WebDriverWait(Driver.driver, 180)).until(ExpectedConditions.stalenessOf(spinnerPopupWindow)); // do invisibilityOfElementLocated instead of staleness?
//            if (Arguments.debug) System.out.println("We're good.");
//        }
//        catch (TimeoutException e) {
//            if (Arguments.debug) System.out.println("Couldn't wait long enough, probably, for whatever to be saved.: " + e.getMessage());
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Some other exception in PatientInformation.doPatientInformation(): " + e.getMessage());
//        }

        By savedMessageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[3]/tbody/tr[2]/td/table/tbody/tr/td[2]/span");
        By errorMessageBy = By.id("patientInformationForm.errors");

        ExpectedCondition<WebElement> savedMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(savedMessageBy);
        ExpectedCondition<WebElement> errorMessageVisibleCondition = ExpectedConditions.visibilityOfElementLocated(errorMessageBy);
        try {
            (new WebDriverWait(driver, 5)).until(ExpectedConditions.or(savedMessageVisibleCondition, errorMessageVisibleCondition));

        }
        catch (Exception e) {
            if (!Arguments.quiet) System.out.println("?????");
            return false;
        }
        String message = null;
        try {
            WebElement savedMessageElement = driver.findElement(savedMessageBy);
            message = savedMessageElement.getText();
        }
        catch (Exception e) {
        }

        try {
            WebElement errorMessageElement = driver.findElement(errorMessageBy);
            message = errorMessageElement.getText();
        }
        catch (Exception e) {
        }
        if (message == null || message.isEmpty()) {
            if (Arguments.debug) System.out.println("Huh?, no message at all for Patient Information save attempt?");
        }

        if (!message.contains("Record Saved")) {
            if (!Arguments.quiet) System.err.println("***Failed trying to save patient information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + message);
            return false;
        }
        return true;
//        WebElement webElement;
//        try {
//            webElement = (new WebDriverWait(Driver.driver, 10))
//                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessageBy));
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("patientInformation.process(), Failed to find error message area.  Exception: " + e.getMessage());
//            return false;
//        }




//        webElement = (new WebDriverWait(Driver.driver, 140)) //  Can take a long time on gold
//                //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
//                .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(savedMessageBy))); // fails: 2
//
//        WebElement webElement;
//        try {
//            webElement = (new WebDriverWait(Driver.driver, 140)) //  Can take a long time on gold
//                    //                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessagesBy)); // fails: 2
//                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(savedMessageBy))); // fails: 2
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("patientInformation.process(), Failed to find error message area.  Exception: " + e.getMessage());
//            return false;
//        }
        // following is wrong, bec came from new patient reg
//        try {
//            String someTextMaybe = webElement.getText();
//            if (someTextMaybe.contains("Record Saved")) {
//                if (Arguments.debug) System.out.println("patientInformation.process(), Message indicates record was saved: " + someTextMaybe);
//            }
//            else {
//                if (!Arguments.quiet) System.err.println("***Failed trying to save patient information for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  " : " + someTextMaybe);
//                return false; // Fails 7, "Patient's Pre-Registration has been created.",  "Initial Diagnosis is required", failed slow 3G
//            }
//        }
//        catch (TimeoutException e) { // hey this should be impossible.
//            if (Arguments.debug) System.out.println("patientInformation.process(), Failed to get message from message area.  TimeoutException: " + e.getMessage());
//            return false;
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("patientInformation.process(), Failed to get message from message area.  Exception:  " + e.getMessage());
//            return false;
//        }
//
//        if (Arguments.debug) System.out.println("patientInformation.process() I guess we got some kind of message, and now returning true.");
//
//        return true; // success ??????????????????????????
    }

    boolean doSelectedPatientInformation(Patient patient) {
//        SelectedPatientInformation selectedPatientInformation = new SelectedPatientInformation();
//// wait, the above creates a new SelectedPatientInformation object, but then in process() one is retrieved????
//        // new:  Oh, looks like this is done inside process()
//        if (selectedPatientInformation.random == null) {
//            selectedPatientInformation.random = (this.random == null) ? false : this.random;
//        }
        if (selectedPatientInformation == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            selectedPatientInformation = new SelectedPatientInformation();
        }
        if (selectedPatientInformation.random == null) {
            selectedPatientInformation.random = ((this.random == null) ? false : this.random); // kinda test
        }
        // npe next line
        boolean result = selectedPatientInformation.process(patient);
        return result;
    }

    boolean doPermanentHomeOfRecord(Patient patient) {
//        PermanentHomeOfRecord permanentHomeOfRecord = new PermanentHomeOfRecord();
        // new:Oh, looks like this is done inside process()
//        if (permanentHomeOfRecord.random == null) {
//            permanentHomeOfRecord.random = (this.random == null) ? false : this.random;
//        }
        if (permanentHomeOfRecord == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            permanentHomeOfRecord = new PermanentHomeOfRecord();
        }
        if (permanentHomeOfRecord.random == null) {
            permanentHomeOfRecord.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = permanentHomeOfRecord.process(patient);
        return result;
    }
    boolean doEmergencyContact(Patient patient) {
//        EmergencyContact emergencyContact = new EmergencyContact();
        // new:Oh, looks like this is done inside process()
//        if (emergencyContact.random == null) {
//            emergencyContact.random = (this.random == null) ? false : this.random;
//        }
        if (emergencyContact == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            emergencyContact = new EmergencyContact();
        }
        if (emergencyContact.random == null) {
            emergencyContact.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = emergencyContact.process(patient);
        return result;
    }
    boolean doImmediateNeeds(Patient patient) {
        //ImmediateNeeds immediateNeeds = new ImmediateNeeds();
        // new: Oh, looks like this is done inside process()
//        if (immediateNeeds.random == null) {
//            immediateNeeds.random = (this.random == null) ? false : this.random;
//        }
        if (immediateNeeds == null) { // how can this happen?  Maybe if all of PatientInformation is marked "random": true
            immediateNeeds = new ImmediateNeeds();
        }
        if (immediateNeeds.random == null) {
            immediateNeeds.random = ((this.random == null) ? false : this.random); // kinda test
        }

        boolean result = immediateNeeds.process(patient);
        return result;
    }

}
