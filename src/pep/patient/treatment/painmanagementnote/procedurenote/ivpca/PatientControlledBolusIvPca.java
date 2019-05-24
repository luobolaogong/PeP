package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

//import java.util.logging.Logger;

public class PatientControlledBolusIvPca {
    //private static Logger logger = Logger.getLogger(PatientControlledBolusIvPca.class.getName()); // two similar sections
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public String dose;
    public String lockout;
    public String medicationConcentration;
    public String volumeToBeInfused;


    public PatientControlledBolusIvPca() {
        if (Arguments.template) {
            this.dose = "";
            this.lockout = "";
            this.medicationConcentration = "";
            this.volumeToBeInfused = "";
        }
    }

}
