package pep.patient.treatment.tbiassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Utilities.getMessageFirstLine;
/**
 * This class is used to upload a file specified by the user, and not selected from a file system popup select modal window.
 * This class is similar to other FileUpload classes in PeP.  I think there are slight differences because not all
 * TMDS FileUpload sections are the same between the three or more pages that have such a section.
 *
 * "Upload a New File" is a dialog, and not a popup like Create Note.  It has three elements on it.
 * First is "Full File Path", which is an element with a "Choose File" button, and a text area to specify the file path,
 * and if you click on the button or the path area then an OS specific (windows) file selection popup appears where you can scroll
 * around and click on a file, and then click "Open", etc.  Or you could just enter your file path into the field in the popup
 * that says "File name" and then click the "Open" button.  But this is not a TMDS window, and Selenium has no access to it,
 * and even if it did, these buttons and fields could be different for whatever OS version you're running.
 * Instead we can just enter text into the field without clicking and popping it up.
 *
 * It appears that you have to provide a full path URL for the file, and TMDS software, or Windows software (which?)
 * strips off the path and just shows the file.  How strange.
 */
public class FileUpload {
    private static Logger logger = Logger.getLogger(FileUpload.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;

    public String fullFilePath; // "select from file system";
    public String fileDescription; // "text";

    private static By fullFilePathInputFieldBy = By.id("uploadFile");
    private static By fileDescriptionBy = By.id("fileDescription");
    private static By uploadButtonBy = By.xpath("//input[@value='Upload']");
    private static By messageBy = By.xpath("//*[@id=\"attachmentsContainer\"]/preceding-sibling::div[1]"); // another bad one that will be fixed when devs add ID's to messages

    public FileUpload() {
        if (Arguments.template) {
            this.fullFilePath = "";
            this.fileDescription = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            fullFilePathInputFieldBy = By.id("tabAttachmentsForm:j_id718:j_id728");
            fileDescriptionBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id732:j_id733\"]/table/tbody/tr/td[2]/textarea");
            uploadButtonBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id746:j_id747\"]/table/tbody/tr/td[2]/input");
            messageBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id683\"]/table/tbody/tr/td/span");
        }
    }
    public boolean process(Patient patient) {
        if (this.fullFilePath == null || this.fullFilePath.isEmpty()) {
            logger.finest("FileUpload.process(), no file to upload.  Returning true.");
            return true;
        }
        if (!Arguments.quiet) System.out.println("      Processing File Upload at " + LocalTime.now() + " for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );

        logger.finer("FileUpload, file path: " + this.fullFilePath + " description: " + this.fileDescription);

        Utilities.sleep(555, "tbiassessment/FileUpload"); // don't know if this helps, but values are not getting input
        try {
            WebElement fullFilePathInputField = Utilities.waitForVisibility(fullFilePathInputFieldBy, 2, "tbiassessment/FileUpload.() full file path input field");
            fullFilePathInputField.sendKeys(this.fullFilePath); // can generate an exception WebDriverException  because file not found
        }
        catch (Exception e) {
            logger.severe("Couldn't add file URL to input field.  e: " + getMessageFirstLine(e)); // off by one? ScreenShot.shoot("SevereError");

            if (!Arguments.quiet) System.err.println("        ***Failed to upload file for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn) +
                            " Check file path: " + this.fullFilePath)
            );
            return false;
        }

        try {
            WebElement fileDescriptionElement = Driver.driver.findElement(fileDescriptionBy);
            fileDescriptionElement.sendKeys(this.fileDescription);
        }
        catch (Exception e) { // got a stale.  Get's to above too fast?
            logger.severe("Couldn't add upload file description.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        try {
            WebElement uploadButtonElement = Driver.driver.findElement(uploadButtonBy);
            if (this.skipSave) {
                if (!Arguments.quiet) System.out.println("    Not saving file.");
                return true;
            }
            uploadButtonElement.click();
        }
        catch (Exception e) {
            logger.severe("Failure clicking or trying to find button to click for file upload.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        try {
            WebElement messageElement = Utilities.waitForVisibility(messageBy, 5, "tbiassessment/FileUpload.(), message area");
            String message = messageElement.getText();
            logger.finer("message: " + message);
            if (message.contains("successfully")) {
                logger.fine("The file was successfully uploaded.  Message: " + message);
                System.out.println("        Uploaded file " + this.fullFilePath + " at " + LocalTime.now());
                return true;
            }
            else {
                logger.warning("Did not save file.  Message: " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("No message for file save?  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false; // fails:1, "Cannot find context with specified id"  From the wait, or from the getText()?
        }
    }
}
