package pep.patient.treatment.painmanagementnote.allergy;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.Main.timerLogger;
import static pep.utilities.Arguments.codeBranch;

/**
 * This class handles the creating and saving of an allergy for a patient.
 * A patient can have more than one allergy, but not duplicate ones.
 * An allergy note is under the category of Pain Management Notes.
 */
public class Allergy {
    private static Logger logger = Logger.getLogger(Allergy.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String allergy;
    public String startDateTime;
    public String reaction;

    private static By addAllergiesTabBy = By.id("saveAllergyLink");
    private static By allergyFieldBy = By.id("allergy");
    private static By startDateTimeFieldBy = By.id("allergyStartDate");
    private static By reactionTextAreaBy = By.id("reaction");
    private static By addAllergyButtonBy = By.id("saveAllergyButton");

    private static By messageAreaAfterClickAddAllergyButtonBy = By.xpath("//div[contains(text(),'Allergy successfully created')]");
    public Allergy() {
        if (Arguments.template) {
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

    /**
     * Process the allergy.
     * @param patient The patient this allergy note is for
     * @return Success or failure of adding the allergy note for the patient
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet) System.out.println("      Processing Allergy at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );
        //
        // Check we have an allergies tab and click it to add an allergy.
        // Prior to this there was a search for patient which may have failed some how, like failing to find the patient.
        // It takes a while for a server to verify that the allergy hasn't been entered before.
        //
        try {
            logger.finest("Allergy.process(), here comes a wait for presence of add allergies tab.");  // Why the heck are we sitting at a PainManagement Search For Patient page??????
            WebElement addAllergiesTab = Utilities.waitForRefreshedClickability(addAllergiesTabBy, 15, "Allergy.process() allergies tab"); // was 10
            logger.finest("Allergy.process(), here comes a click on allergies tab.");
            addAllergiesTab.click();
            logger.finest("Allergy.process(), here comes a wait for ajax to be finished.");
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
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
        //
        // Add field values for Allergy.  There are only a few.
        //
        Utilities.sleep(555, "Allergy.process(), before doing a save of Allergy record.  Keep from getting stale element"); // new 12/6/18 because get stale element
        if (Arguments.date != null && (this.startDateTime == null || this.startDateTime.isEmpty())) {
            this.startDateTime = Arguments.date + " " + Utilities.getCurrentHourMinute();
        }
        this.startDateTime = Utilities.processDateTime(startDateTimeFieldBy, this.startDateTime, this.randomizeSection, true);

        try {
            this.allergy = Utilities.processText(allergyFieldBy, this.allergy, Utilities.TextFieldType.ALLERGY_NAME, this.randomizeSection, true);
        }
        catch (Exception e) {
            logger.fine("Got some kind of exception after trying to do a processText on the allergy stuff.: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        this.reaction = Utilities.processText(reactionTextAreaBy, this.reaction, Utilities.TextFieldType.ALLERGY_REACTION, this.randomizeSection, true);
        //
        // Take a screenshot if interested.
        //
        Utilities.sleep(555, "Allergy.process(), about to try to process allergy reaction and click the save button"); // give the server some time to finish entering Reaction information before taking a screen shot (?????, "Allergy", "Allergy");
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        //
        // Save the allergy record.
        //
        Instant start = null;
        try {
            WebElement addAllergyButtonElement = Utilities.waitForRefreshedClickability(addAllergyButtonBy, 1, "Allergy.process() add allergy button");
            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "Allergy");
            }
            start = Instant.now();
            addAllergyButtonElement.click(); // After clicking it takes a long time to come back.  I think this must be what causes the error of not finding the message area later.  There's not enough time after the click and the time the message area is checked
            (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // does this actually work?  I doubt it
        }
        catch (Exception e) {
            logger.fine("Allergy.process(), did not get the Add Allergy button, or could not click on it: " + Utilities.getMessageFirstLine(e));
            return false; // fails: gold: 1
        }
        //
        // Check that it was saved.
        //
        Utilities.sleep(2555, "Allergy.process(), done adding/saving the allergy, about ti try waiting for message of success.");// will this keep it from failing below?
        WebElement result;
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
        try {
            logger.fine("Allergy.process(), Found message area, now Gunna get the message...");
            Utilities.sleep(1155, "Allergy.process() about ti wait for message area with button? or get text?"); //

            logger.fine("here's a duplicate request that shouldn't be needed");
            result = Utilities.waitForRefreshedVisibility(messageAreaAfterClickAddAllergyButtonBy,  15, "Allergy.process() message area");

            String someTextMaybe = result.getText();
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
                return false;
            }
        }
        catch (StaleElementReferenceException e) {
            logger.severe("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return true;
        }
        catch (Exception e) {
            logger.severe("Allergy.process(), did not find message area after clicking Add Allergy button.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        if (!Arguments.quiet) {
            System.out.println("        Saved Allergy note at " + LocalTime.now() + " for patient" +
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