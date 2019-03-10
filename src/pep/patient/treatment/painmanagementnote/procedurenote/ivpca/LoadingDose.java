package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;


import pep.utilities.Arguments;

import java.util.logging.Logger;

public class LoadingDose {
    private static Logger logger = Logger.getLogger(LoadingDose.class.getName());
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public String dose; // "mg";


    public LoadingDose() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            this.dose = "";
        }
    }

}
