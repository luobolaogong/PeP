package pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter;

import pep.utilities.Arguments;

//import java.util.logging.Logger;

public class BolusInjection {
    //private static Logger logger = Logger.getLogger(BolusInjection.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String bolusInjectionDate;
    public String bolusMedication;
    public String concentration;
    public String volume;


    public BolusInjection() {
        if (Arguments.template) {
            this.bolusInjectionDate = "";
            this.bolusMedication = "";
            this.concentration = "";
            this.volume = "";
        }
    }

}
