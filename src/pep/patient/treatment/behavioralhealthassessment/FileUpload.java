package pep.patient.treatment.behavioralhealthassessment;

import pep.utilities.Arguments;

public class FileUpload { // multiple?
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
