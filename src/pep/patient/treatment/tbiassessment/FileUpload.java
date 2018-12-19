package pep.patient.treatment.tbiassessment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;
import static pep.utilities.Utilities.getMessageFirstLine;
// This may be identical to the other FlightUpload.java file under tbiAssessment somewhere

// "Upload a New File" is a dialog, and not a popup like Create Note.  It has three elements on it.
// First is "Full File Path", which is an element with a "Choose File" button, and a text area to specify the file path,
// and if you click on the button or the path area then an OS specific (windows) file selection popup appears where you can scroll
// around and click on a file, and then click "Open", etc.  Or you could just enter your file path into the field in the popup
// that says "File name" and then click the "Open" button.  But this is not a TMDS window, and Selenium has no access to it,
// and even if it did, these buttons and fields could be different for whatever OS version you're running.
//Instead we can just enter text into the field without clicking and popping it up.  Otherwise this will be hard.
//
// It appears that you have to provide a full path URL for the file, and TMDS software, or Windows software (which?)
// strips off the path and just shows the file.  How strange.
public class FileUpload {
    private static Logger logger = Logger.getLogger(FileUpload.class.getName()); // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public Boolean shoot;
    public String fullFilePath; // "select from file system";
    public String fileDescription; // "text";

    private static By uploadANewFileTabBy = By.xpath("//*[@id=\"uploadTab\"]/a");
    private static  By fullFilePathInputFieldBy = By.id("uploadFile"); // This thing is an "input" element, but it triggers a file input popup
    private static By fileDescriptionBy = By.id("fileDescription");
    private static By uploadButtonBy = By.xpath("//*[@id=\"attachmentForm\"]/div[2]/table/tbody/tr[3]/td[2]/input");
    //By messageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[11]");
    private static  By messageBy = By.xpath("//*[@id=\"attachmentsContainer\"]/preceding-sibling::div[1]");



    public FileUpload() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.fullFilePath = "";
            this.fileDescription = "";
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            //uploadANewFileTabBy = By.id("tabAttachmentsForm:FileUpload_lbl");
            //fullFilePathInputFieldBy = By.id("uploadFile");
           // fullFilePathInputFieldBy = By.id("tabAttachmentsForm:j_id666:j_id676");
            fullFilePathInputFieldBy = By.id("tabAttachmentsForm:j_id718:j_id728");
           //fileDescriptionBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id680:j_id681\"]/table/tbody/tr/td[2]/textarea");
           fileDescriptionBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id732:j_id733\"]/table/tbody/tr/td[2]/textarea");
           //uploadButtonBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id694:j_id695\"]/table/tbody/tr/td[2]/input");
           uploadButtonBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id746:j_id747\"]/table/tbody/tr/td[2]/input");
            //messageBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id631\"]/table/tbody/tr/td/span");
            messageBy = By.xpath("//*[@id=\"tabAttachmentsForm:j_id683\"]/table/tbody/tr/td/span");
        }
    }
    public boolean process(Patient patient) {
        if (this.fullFilePath == null || this.fullFilePath.isEmpty()) {
            logger.finest("FileUpload.process(), no file to upload.  Returning true.");
            return true;
        }
        if (!Arguments.quiet) System.out.println("      Processing File Upload for patient" +
                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
        );


        logger.finer("FileUpload, file path: " + this.fullFilePath + " description: " + this.fileDescription);

        Utilities.sleep(555); // don't know if this helps, but values are not getting input
        try {
            WebElement fullFilePathInputField = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(fullFilePathInputFieldBy));
            fullFilePathInputField.sendKeys(this.fullFilePath); // can generate an exception WebDriverException  because file not found
        }
        catch (Exception e) {
            logger.severe("Couldn't add file URL to input field.  e: " + getMessageFirstLine(e)); // off by one?

            if (!Arguments.quiet) System.err.println("        ***Failed to upload file for patient " +
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
            logger.severe("Couldn't add upload file description.  e: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("        Wrote screenshot file " + fileName);
        }

        try {
            WebElement uplodadButtonElement = Driver.driver.findElement(uploadButtonBy);
            uplodadButtonElement.click();
        }
        catch (Exception e) {
            logger.severe("Failure clicking or trying to find button to click for file upload.  e: " + Utilities.getMessageFirstLine(e));
            return false;
        }

        try {
           // WebElement messageElement = Driver.driver.findElement(messageBy);
            WebElement messageElement = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(messageBy));
            String message = messageElement.getText();
            logger.finer("message: " + message);
            if (message.contains("successfully")) {
                logger.info("The file was successfully uploaded.  Message: " + message);
                System.out.println("        Uploaded file " + this.fullFilePath);
                return true;
            }
            else {
                logger.warning("Did not save file.  Message: " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.severe("No message for file save?  e: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }
}
