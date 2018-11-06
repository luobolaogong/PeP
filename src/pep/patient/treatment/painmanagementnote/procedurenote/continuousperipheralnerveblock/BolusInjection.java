package pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock;

import pep.utilities.Arguments;

import java.util.logging.Logger;

public class BolusInjection {
    private static Logger logger = Logger.getLogger(BolusInjection.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public String bolusInjectionDate; // "MM/DD/YYYY HHMM Z";
    public String bolusMedication; // "option 1-3";
    public String concentration; // "percent";
    public String volume; // "ml";


    public BolusInjection() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.bolusInjectionDate = "";
            this.bolusMedication = "";
            this.concentration = "";
            this.volume = "";
        }
    }

}
