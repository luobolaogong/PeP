package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

import java.util.logging.Logger;

public class PatientControlledBolusIvPca {
    private static Logger logger = Logger.getLogger(PatientControlledBolusIvPca.class.getName()); // two similar sections
    public Boolean sectionToBeRandomized;
    public Boolean shoot;
    public String dose; // "ml";
    public String lockout; // "minutes";
    public String medicationConcentration; // "mg/mL";
    public String volumeToBeInfused; // "mL";


    public PatientControlledBolusIvPca() {
        if (Arguments.template) {
            //this.sectionToBeRandomized = null; // don't want this showing up in template
            this.dose = "";
            this.lockout = "";
            this.medicationConcentration = "";
            this.volumeToBeInfused = "";
        }
    }

}
