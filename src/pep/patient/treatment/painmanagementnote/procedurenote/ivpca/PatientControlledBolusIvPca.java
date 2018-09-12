package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;

import pep.utilities.Arguments;

public class PatientControlledBolusIvPca { // two similar sections
    public Boolean random; // true if want this section to be generated randomly
    public String dose; // "ml";
    public String lockout; // "minutes";
    public String medicationConcentration; // "mg/mL";
    public String volumeToBeInfused; // "mL";


    public PatientControlledBolusIvPca() {
        if (Arguments.template) {
            this.random = null;
            this.dose = "";
            this.lockout = "";
            this.medicationConcentration = "";
            this.volumeToBeInfused = "";
        }
    }

}
