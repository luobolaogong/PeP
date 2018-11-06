package pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock;

import pep.patient.Patient;
import pep.utilities.Arguments;

import java.util.logging.Logger;

public class PatientControlledBolusCpnb {
    private static Logger logger = Logger.getLogger(PatientControlledBolusCpnb.class.getName());
    public Boolean random; // true if want this section to be generated randomly
    public String volume; // "ml";
    public String lockout; // "minutes";


    public PatientControlledBolusCpnb() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.volume = "";
            this.lockout = "";
        }
    }

    public boolean process(Patient patient) {
        logger.fine("Why nothing?");
        return true;
    }

}
