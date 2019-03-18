package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

//import java.util.logging.Logger;

public class BasalRateContinuousInfusion {
    //private static Logger logger = Logger.getLogger(BasalRateContinuousInfusion.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public String rate;
    public String medicationCentration;
    public String infusionStartTime;
    public String volumeToBeInfused;


    public BasalRateContinuousInfusion() {
        if (Arguments.template) {
            this.rate = "";
            this.medicationCentration = "";
            this.infusionStartTime = "";
            this.volumeToBeInfused = "";
        }
    }

}
