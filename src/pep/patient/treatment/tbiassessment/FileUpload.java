//package pep.patient.treatment.tbiassessment;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import pep.patient.Patient;
//import pep.utilities.Arguments;
//import pep.utilities.Driver;
//
//import java.util.logging.Logger;
//// This may be identical to the FileUpload in BehavioralHealthAssessment.java
////
//// "Upload a New File" is a dialog, and not a popup like Create Note.  It has three elements on it.
//// First is "Full File Path", which is an element with a "Choose File" button, and a text area to specify the file path,
//// and if you click on the button or the path area then an OS specific (windows) file selection popup appears where you can scroll
//// around and click on a file, and then click "Open", etc.  Or you could just enter your file path into the field in the popup
//// that says "File name" and then click the "Open" button.  But this is not a TMDS window, and Selenium has no access to it,
//// and even if it did, these buttons and fields could be different for whatever OS version you're running.
////Instead we can just enter text into the field without clicking and popping it up.  Otherwise this will be hard.
////
//// It appears that you have to provide a full path URL for the file, and TMDS software, or Windows software (which?)
//// strips off the path and just shows the file.  How strange.
//public class FileUpload {
//    private static Logger logger = Logger.getLogger(FileUpload.class.getName()); // multiple?
//    public Boolean random; // true if want this section to be generated randomly
//    public String fullFilePath; // "select from file system";
//    public String fileDescription; // "text";
//
//
//    public FileUpload() {
//        if (Arguments.template) {
//            //this.random = null; // don't want this showing up in template
//            this.fullFilePath = "";
//            this.fileDescription = "";
//        }
//    }
//    public boolean process(Patient patient) {
//        if (!Arguments.quiet) System.out.println("      Processing BH TBI Assessment File Upload for patient" +
//                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
//                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
//                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
//        );
//        // check how I did this stuff elsewhere, wherever.  Like clicking on Create Note
//        By uploadANewFileTabBy = By.xpath("//*[@id=\"uploadTab\"]/a");
//        By fullFilePathInputFieldBy = By.id("uploadFile"); // This thing is an "input" element, but it triggers a file input popup
//        By fileDescriptionBy = By.id("fileDescription");
//        By uploadButtonBy = By.xpath("//*[@id=\"attachmentForm\"]/div[2]/table/tbody/tr[3]/td[2]/input");
//        //By messageBy = By.xpath("/html/body/table/tbody/tr[1]/td/table[4]/tbody/tr/td/div/div[11]");
//        By messageBy = By.xpath("//*[@id=\"attachmentsContainer\"]/preceding-sibling::div[1]");
//
//
//        Driver.driver.findElement(uploadANewFileTabBy).click(); // element not visible
//        WebElement fullFilePathInputField = Driver.driver.findElement(fullFilePathInputFieldBy);
//        fullFilePathInputField.sendKeys(this.fullFilePath); // can generate an exception WebDriverException  because file not found
//        Driver.driver.findElement(fileDescriptionBy).sendKeys(this.fileDescription);
//        Driver.driver.findElement(uploadButtonBy).click();
//        WebElement messageElement = Driver.driver.findElement(messageBy);
//        String message = messageElement.getText();
//        System.out.println("message: " + message);
//        if (message.contains("successfully")) {
//            System.out.println("Wow, file was successfully uploaded, I guess.  Message: " + message);
//            return true;
//        }
//        else {
//            System.out.println("Did not save file.  Message: " + message);
//            return false;
//        }
//    }
//}
////package pep.patient.treatment.tbiassessment;
////
////import pep.patient.Patient;
////import pep.utilities.Arguments;
////
////import java.util.logging.Logger;
////
////public class FileUpload {
////    private static Logger logger = Logger.getLogger(FileUpload.class.getName()); // multiple?
////    public Boolean random; // true if want this section to be generated randomly
////    public String fullFilePath; // "select from file system";
////    public String fileDescription; // "text";
////
////
////    public FileUpload() {
////        if (Arguments.template) {
////            //this.random = null;
////            this.fullFilePath = "";
////            this.fileDescription = "";
////        }
////    }
////    public boolean process(Patient patient) {
////        if (!Arguments.quiet) System.out.println("      Processing BH TBI Assessment File Upload for patient" +
////                (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
////                (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
////                (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
////        );
////        return true;
////    }
////
////}
