package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

public class BasalRateContinuousInfusion {
    public Boolean random; // true if want this section to be generated randomly
    public String rate; // "mL/hr";
    public String medicationCentration; // "mg/mL";
    public String infusionStartTime; // "MM/DD/YYYY HHMM Z";
    public String volumeToBeInfused; // "mL";


    public BasalRateContinuousInfusion() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.rate = "";
            this.medicationCentration = "";
            this.infusionStartTime = "";
            this.volumeToBeInfused = "";
        }
    }

}
