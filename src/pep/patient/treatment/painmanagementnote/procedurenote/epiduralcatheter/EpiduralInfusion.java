package pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter;

import pep.patient.Patient;
import pep.utilities.Arguments;

import java.util.logging.Logger;

public class EpiduralInfusion {
    private static Logger logger = Logger.getLogger(EpiduralInfusion.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public String infusionRate; // "mL/hr";
    public String infusionMedication; // "option 1-3";
    public String concentration; // "percent";
    public String volumeToBeInfused; // "ml";


    public EpiduralInfusion() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.infusionRate = "";
            this.infusionMedication = "";
            this.concentration = "";
            this.volumeToBeInfused = "";
        }
    }

    public boolean process(Patient patient) {
        System.out.println("What's going on?  Why nothing here?");
        return true;
    }

}
