package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

import java.util.logging.Logger;

public class BasalRateContinuousInfusion {
    private static Logger logger = Logger.getLogger(BasalRateContinuousInfusion.class.getName());
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public String rate; // "mL/hr";
    public String medicationCentration; // "mg/mL";
    public String infusionStartTime; // "MM/DD/YYYY HHMM Z";
    public String volumeToBeInfused; // "mL";


    public BasalRateContinuousInfusion() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            this.rate = "";
            this.medicationCentration = "";
            this.infusionStartTime = "";
            this.volumeToBeInfused = "";
        }
    }

}
