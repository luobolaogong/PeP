package pep.patient.treatment.behavioralhealthassessment;

import pep.utilities.Arguments;

import java.util.logging.Logger;

public class FileUpload {
  private static Logger logger = Logger.getLogger(FileUpload.class.getName()); // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public String fullFilePath; // "select from file system";
    public String fileDescription; // "text";


    public FileUpload() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.fullFilePath = "";
            this.fileDescription = "";
        }
    }

}
