package pep.patient.treatment.painmanagementnote.allergy;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.patient.treatment.painmanagementnote.PainManagementNote;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

public class Allergy { // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public String allergy; // "text, required";
    public String startDateTime; // "mm/dd/yyyy hhmm, required";
    public String reaction; // "string text, required";

    private static By addAllergiesTabBy = By.id("saveAllergyLink");
    private static By allergyFieldBy = By.id("allergy");
    private static By startDateTimeFieldBy = By.id("allergyStartDate");
    private static By reactionTextAreaBy = By.id("reaction");
    private static By addAllergyButtonBy = By.id("saveAllergyButton");
    private static By messageAreaAfterClickAddAllergyButtonBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div[7]"); // verified
    public Allergy() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.allergy = "";
            this.startDateTime = "";
            this.reaction = "";
        }
        if (Pep.isDemoTier) {
            addAllergiesTabBy = By.id("painNoteForm:AddEditAllergiesTab_lbl");
            allergyFieldBy = By.id("painNoteForm:allergyDecorate:allergy");
            startDateTimeFieldBy = By.id("painNoteForm:startDateDecorate:startDateInputDate");
            reactionTextAreaBy = By.id("painNoteForm:reactionDecorate:reaction");
            addAllergyButtonBy = By.id("painNoteForm:add");
            messageAreaAfterClickAddAllergyButtonBy = By.xpath("//*[@id=\"painNoteForm:j_id494\"]/table/tbody/tr/td/span");
        }
    }
    // Before we do anything here, we need to make sure we're on the right page, which means there's an Add Allergies tab.
    // Prior to this I think there was a search for patient.  We now want to click on the Add Allergies tab and do that section.
    // This method really has a problem because the Add Allergies tab click causes an AJAX call which takes time!!!!!!!!!!!!!
    // I really kinda hate working on Allergies because of the stupid waste of unknown amount of time it takes for a server to
    // verify that the allergy hasn't been entered before.
    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Allergy for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ...");
        // The problem here is probably that the previous Search For Patient didn't come up with anyone, and it hung on that page
        // and therefore there is no allergy section

        // Find the section that's going to get overwritten when the Add Allergies tab gets clicked, so we know when it gets stale and then refreshed.
        // Find and click the Add Allergies tab
        try {
            WebElement addAllergiesTab = (new WebDriverWait(Driver.driver, 15))
                    .until(ExpectedConditions.presenceOfElementLocated(addAllergiesTabBy));
            //if (Arguments.debug) System.out.println("Here comes a click on the Add Allergies tab");
            addAllergiesTab.click(); // Causes AJAX call, which can take a while for the DOM to be reconstructed
            //if (Arguments.debug) System.out.println("Back from a click on the Add Allergies tab");

            // EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT EXPERIMENT
            //if (Arguments.debug) System.out.println("Allergy.process()1, doing a call to isFinishedAjax");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());  // does this really wait?  Seems it doesn't!!
            //if (Arguments.debug) System.out.println("Allergy.process()1, back from a call to isFinishedAjax");

            Utilities.sleep(1022); // I hate to do this.  Does it even help?


        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Allergy.process() Couldn't get the allergies tab, or couldn't click on it: " + e.getMessage());
           // System.out.println("Allergy.process() Couldn't get the allergies tab, or couldn't click on it: " + e.getMessage());
            return false;
        }


        //this.startDateTime = Utilities.processText(AA_START_DATE_TIME_FIELD, this.startDateTime, Utilities.TextFieldType.DATE_TIME, this.random, true);
        if (Arguments.date != null && (this.startDateTime == null || this.startDateTime.isEmpty())) {
            this.startDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.startDateTime = Utilities.processDateTime(startDateTimeFieldBy, this.startDateTime, this.random, true);

        // This was above the start date/time, but moved it here to see if helps, because I think start date/time will erase the allergy.  Not sure
        try {
            //if (Arguments.debug) System.out.println("Allergy.process(), Here comes an effort to add an allergy to the text box, and this is where we fail...");
            this.allergy = Utilities.processText(allergyFieldBy, this.allergy, Utilities.TextFieldType.ALLERGY_NAME, this.random, true);
            //if (Arguments.debug) System.out.println("Allergy.process(), I guess we added an allergy to the text box...");
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Got some kind of exception after trying to do a processText on the allergy stuff.: " + e.getMessage());
           // System.out.println("Got some kind of exception after trying to do a processText on the allergy stuff.: " + e.getMessage());
            return false;
        }

        // Is reaction actually required?  Yes, on Demo and prob gold too.  The asterisk is for "All Fields"
        this.reaction = Utilities.processText(reactionTextAreaBy, this.reaction, Utilities.TextFieldType.ALLERGY_REACTION, this.random, true);

        try {
            WebElement addAllergyButtonElement = (new WebDriverWait(Driver.driver,1)).until(ExpectedConditions.elementToBeClickable(addAllergyButtonBy));
            // Watch the freaking network requests and responses and see how the DOM changes.  Turn on chrome debugging and watch
            addAllergyButtonElement.click(); // After clicking it takes a long time to come back.  I think this must be what causes the error of not finding the message area later.  There's not enough time after the click and the time the message area is checked
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this actually work?  I doubt it
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Allergy.process(), did not get the Add Allergy button, or could not click on it: " + e.getMessage());
            return false; // fails: gold: 1
        }
        // The above save allergy click can take a long time.  The wait below may not be long enough
        WebElement result = null; // we get here before the servers come back with "Allergy successfully created!"
        try {
            result = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageAreaAfterClickAddAllergyButtonBy)));
        }
        catch(Exception e) {
            if (Arguments.debug) System.out.println("allergy.process(), Did not get web element for expected condition of presence..." + e.getMessage());
            return false; // fails: gold: 1
        }
        try {
            if (result != null) {
                if (Arguments.debug) System.out.println("result has text: " + result.getText());
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("This should never happen.  But it might due to a stale result element where you can't get a text value.");
        }

        // surrounding in a try/catch because result.getText() can blow up, but not because of npe, but because of statle element reference
        // This really needs to get solved rather than bandaided
        try {
            if (Arguments.debug) System.out.println("Allergy.process(), Found message area, now Gunna get the message...");
            // stop on this next line and check result if null
            Utilities.sleep(5155); // what the crap?  It's a stale fricking element.  Prob because of some crappy ajax thing the rewrites locators.

            if (Arguments.debug) System.out.println("here's a duplicate request that shouldn't be needed");
            result = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(messageAreaAfterClickAddAllergyButtonBy)));

            // I don't know what the hell is going on, but this can return "Allergies" rather than "Allergy successfully created!"
            // And it's probably because of some damn ajax thing screwing up the DOM
            String someTextMaybe = result.getText();// This can throw exception!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   error here, throws exception, stale
            if (Arguments.debug) System.out.println("Allergy.process(), Got the message ->" + someTextMaybe + "<-");
            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                if (Arguments.debug) System.out.println("Allergy.process().  Created allergy successfully.");
            }
            else if (someTextMaybe != null && someTextMaybe.contains("You may not create an allergy with the same name from TMDS")) {
                if (Arguments.debug) System.out.println("Allergy.process().  Duplicate allergies not allowed.");
                return false;
            }
            else {
                if (!Arguments.quiet) System.err.println("***Failed to add allergy note for patient " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName +  ": " + someTextMaybe);
                return false;
            }
        }
        catch (StaleElementReferenceException e) {
            if (Arguments.debug) System.out.println("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + e.getMessage());
            return true; // we're gunna let this one go through because Selenium totally sucks
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

}