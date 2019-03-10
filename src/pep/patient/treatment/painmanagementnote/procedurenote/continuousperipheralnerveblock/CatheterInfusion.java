package pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock;

import pep.utilities.Arguments;

import java.util.logging.Logger;

public class CatheterInfusion {
    private static Logger logger = Logger.getLogger(CatheterInfusion.class.getName());
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public String infusionRate; // "mL/hr";
    public String infusionMedication; // "option 1-3";
    public String concentration; // "percent";
    public String volumeToBeInfused; // "ml";


    public CatheterInfusion() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            this.infusionRate = "";
            this.infusionMedication = "";
            this.concentration = "";
            this.volumeToBeInfused = "";
        }
    }

}
