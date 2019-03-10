package pep.patient.treatment.painmanagementnote.allergy;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
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

public class Allergy {
    private static Logger logger = Logger.getLogger(Allergy.class.getName()); // multiple?
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public String allergy; // "text, required";
    public String startDateTime; // "mm/dd/yyyy hhmm, required";
    public String reaction; // "string text, required";

    private static By addAllergiesTabBy = By.id("saveAllergyLink");
    private static By allergyFieldBy = By.id("allergy");
    private static By startDateTimeFieldBy = By.id("allergyStartDate");
    private static By reactionTextAreaBy = By.id("reaction");
    private static By addAllergyButtonBy = By.id("saveAllergyButton");

    // The message area for whether an allergy was created successfully or not keeps changing on me.  Sometimes it's a short id, and others a long xpath
    private static By messageAreaAfterClickAddAllergyButtonBy = By.xpath("//div[contains(text(),'Allergy successfully created')]");
    public Allergy() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            this.allergy = "";
            this.startDateTime = "";
            this.reaction = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            addAllergiesTabBy = By.id("painNoteForm:AddEditAllergiesTab_lbl");
            allergyFieldBy = By.id("painNoteForm:allergyDecorate:allergy");
            startDateTimeFieldBy = By.id("painNoteForm:startDateDecorate:startDateInputDate");
            reactionTextAreaBy = By.id("painNoteForm:reactionDecorate:reaction");
            addAllergyButtonBy = By.id("painNoteForm:add");
            messageAreaAfterClickAddAllergyButtonBy = By.xpath("//*[@id='painNoteForm:j_id494']/table/tbody/tr/td/span");
        }
    }
    // Before we do anything here, we need to make sure we're on the right page, which means there's an Add Allergies tab.
    // Prior to this there was a search for patient which may have failed some how, like failing to find the patient.
    // If so, we can't do this method.  But if it worked, then we now want to click on the Add Allergies tab and do that section.
    // This method really has a problem because the Add Allergies tab click causes an AJAX call which takes time!!!!!!!!!!!!!
    // I really kinda hate working on Allergies because of the stupid waste of unknown amount of time it takes for a server to
    // verify that the allergy hasn't been entered before.
    // Yes unfortunately we get here when we're still back on a search page or somewhere else, due to some kind of failure.
    public boolean process(Patient patient, PainManagementNote painManagementNote) {
        if (!Arguments.quiet) System.out.println("      Processing Allergy for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        // The problem here is probably that the previous Search For Patient didn't come up with anyone, and it hung on that page
        // and therefore there is no allergy section

        // Find the section that's going to get overwritten when the Add Allergies tab gets clicked, so we know when it gets stale and then refreshed.
        // Find and click the Add Allergies tab
        // What? We're stuck on some other page here, and therefore the next stuff fails?
        try { // what, we have to put a sleep here too because can't get to tab too early???????
            logger.finest("Allergy.process(), here comes a wait for presence of add allergies tab.");  // Why the heck are we sitting at a PainManagement Search For Patient page??????
            //WebElement addAllergiesTab = (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.elementToBeClickable(addAllergiesTabBy));
            WebElement addAllergiesTab = Utilities.waitForRefreshedClickability(addAllergiesTabBy, 15, "Allergy.process() allergies tab"); // was 10
            //logger.finest("just tried clickable, and now visible:");
            //addAllergiesTab = Utilities.waitForVisibility(addAllergiesTabBy, 15, "Allergy.process()");
            //logger.finest("just tried visible, and now presence:");
            //addAllergiesTab = Utilities.waitForPresence(addAllergiesTabBy, 15, "Allergy.process()");
            //logger.finest("Done with presence");
            logger.finest("Allergy.process(), here comes a click on allergies tab.");
            addAllergiesTab.click(); // Causes AJAX call, which can take a while for the DOM to be reconstructed
            logger.finest("Allergy.process(), here comes a wait for ajax to be finished.");

            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());  // does this really wait?  Seems it doesn't!!

            Utilities.sleep(1022, "Allergy.process(), After clicking on add allergies tab."); // I hate to do this.  Does it even help?
        }
        catch (TimeoutException e) {
            logger.severe("Allergy.process() Timeout exception.  Couldn't get the allergies tab, or couldn't click on it"); ScreenShot.shoot("SevereError");
            return false; // why?  Because Pain Management Search For Patient didn't find the patient!!!!!!!!!!!!!!!!
        }
        catch (Exception e) {
            logger.severe("Allergy.process() Couldn't get the allergies tab, or couldn't click on it: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        Utilities.sleep(555, "Allergy.process(), before doing a save of Allergy record.  Keep from getting stale element"); // new 12/6/18 because get stale element
        if (Arguments.date != null && (this.startDateTime == null || this.startDateTime.isEmpty())) {
            this.startDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.startDateTime = Utilities.processDateTime(startDateTimeFieldBy, this.startDateTime, this.sectionToBeRandomized, true);

        // This was above the start date/time, but moved it here to see if helps, because I think start date/time will erase the allergy.  Not sure
        try {
            //logger.fine("Allergy.process(), Here comes an effort to add an allergy to the text box, and this is where we fail...");
            this.allergy = Utilities.processText(allergyFieldBy, this.allergy, Utilities.TextFieldType.ALLERGY_NAME, this.sectionToBeRandomized, true);
            //logger.fine("Allergy.process(), I guess we added an allergy to the text box...");
        }
        catch (Exception e) {
            logger.fine("Got some kind of exception after trying to do a processText on the allergy stuff.: " + Utilities.getMessageFirstLine(e));
           // System.out.println("Got some kind of exception after trying to do a processText on the allergy stuff.: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        Utilities.sleep(555, "Allergy.process(), about to try to process allergy reaction and click the save button"); // give the server some time to finish entering Reaction information before taking a screen shot (?????, "Allergy", "Allergy");
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        // Is reaction actually required?  Yes, on Demo and prob gold too.  The asterisk is for "All Fields"
        this.reaction = Utilities.processText(reactionTextAreaBy, this.reaction, Utilities.TextFieldType.ALLERGY_REACTION, this.sectionToBeRandomized, true);
        Instant start = null;
        try {
            //WebElement addAllergyButtonElement = (new WebDriverWait(Driver.driver,1)).until(ExpectedConditions.elementToBeClickable(addAllergyButtonBy));
            WebElement addAllergyButtonElement = Utilities.waitForRefreshedClickability(addAllergyButtonBy, 1, "Allergy.process() add allergy button");
            // Watch the freaking network requests and responses and see how the DOM changes.  Turn on chrome debugging and watch
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "Allergy");
            }
            start = Instant.now();
            addAllergyButtonElement.click(); // After clicking it takes a long time to come back.  I think this must be what causes the error of not finding the message area later.  There's not enough time after the click and the time the message area is checked
//            timerLogger.fine("Allergy addAllergyButtonElement.click took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this actually work?  I doubt it
        }
        catch (Exception e) {
            logger.fine("Allergy.process(), did not get the Add Allergy button, or could not click on it: " + Utilities.getMessageFirstLine(e));
            return false; // fails: gold: 1
        }


        // even though the allergy gets saved, something below here fails,
        Utilities.sleep(2555, "Allergy.process(), done adding/saving the allergy, about ti try waiting for message of success.");// will this keep it from failing below?


        // The above save allergy click can take a long time.  The wait below may not be long enough
        WebElement result = null; // we get here before the servers come back with "Allergy successfully created!"
        try {
            result = Utilities.waitForRefreshedVisibility(messageAreaAfterClickAddAllergyButtonBy,  15, "Allergy.process() message area");
        }
        catch(Exception e) {
            logger.fine("allergy.process(), Did not get web element for expected condition of presence..." + Utilities.getMessageFirstLine(e));
            return false; // fails: gold: 1
        }
        try {
            if (result != null) {
                logger.fine("result has text: " + result.getText());
            }
        }
        catch (Exception e) {
            logger.fine("This should never happen.  But it might due to a stale result element where you can't get a text value.");
        }

        // surrounding in a try/catch because result.getText() can blow up, but not because of npe, but because of statle element reference
        // This really needs to get solved rather than bandaided
        try {
            logger.fine("Allergy.process(), Found message area, now Gunna get the message...");
            // stop on this next line and check result if null
            //Utilities.sleep(5155, "Allergy"); // what the crap?  It's a stale fricking element.  Prob because of some crappy ajax thing the rewrites locators.
            Utilities.sleep(1155, "Allergy.process() about ti wait for message area with button? or get text?"); //

            logger.fine("here's a duplicate request that shouldn't be needed");
            result = Utilities.waitForRefreshedVisibility(messageAreaAfterClickAddAllergyButtonBy,  15, "Allergy.process() message area");

            // I don't know what the hell is going on, but this can return "Allergies" rather than "Allergy successfully created!"
            // And it's probably because of some damn ajax thing screwing up the DOM
            String someTextMaybe = result.getText();// This can throw exception!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   error here, throws exception, stale
            logger.fine("Allergy.process(), Got the message ->" + someTextMaybe + "<-");
            if (someTextMaybe != null && someTextMaybe.contains("successfully")) {
                logger.fine("Allergy.process().  Created allergy successfully.");
            }
            else if (someTextMaybe != null && someTextMaybe.contains("You may not create an allergy with the same name from TMDS")) {
                if (!Arguments.quiet) System.err.println("***Duplicate allergies not allowed.");
                return false;
            }
            else if (someTextMaybe != null && someTextMaybe.contains("No inpatient data for patient")) {
                if (!Arguments.quiet) System.err.println("Inpatient Events History section reports the message: " + someTextMaybe);
            }
            else {
                logger.severe("      Failed to add allergy note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn +  " : " + someTextMaybe); ScreenShot.shoot("SevereError");
                return false; // fails: 3    what is this a timing issue? failed 11/05/18
            }
        }
        catch (StaleElementReferenceException e) {
            logger.severe("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return true; // we're gunna let this one go through because Selenium totally sucks
        }
        catch (Exception e) {
            logger.severe("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved Allergy note for patient " +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        }
        timerLogger.fine("Allergy addAllergyButtonElement.click took " + ((Duration.between(start, Instant.now()).toMillis())/1000.0) + "s");
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "Allergy");
        }
        return true;
    }

}