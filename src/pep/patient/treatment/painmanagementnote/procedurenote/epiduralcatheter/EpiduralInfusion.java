package pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter;

//import pep.patient.Patient;
import pep.utilities.Arguments;

import java.util.logging.Logger;

public class EpiduralInfusion {
    //private static Logger logger = Logger.getLogger(EpiduralInfusion.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public String infusionRate;
    public String infusionMedication;
    public String concentration;
    public String volumeToBeInfused;


    public EpiduralInfusion() {
        if (Arguments.template) {
            this.infusionRate = "";
            this.infusionMedication = "";
            this.concentration = "";
            this.volumeToBeInfused = "";
        }
    }

//    public boolean process(Patient patient) {
//        System.out.println("What's going on?  Why nothing here?");
//        return true;
//    }

}
