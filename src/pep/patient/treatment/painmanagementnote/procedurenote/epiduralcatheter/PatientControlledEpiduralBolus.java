package pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter;

import pep.patient.Patient;
import pep.utilities.Arguments;

public class PatientControlledEpiduralBolus {
    public Boolean random; // true if want this section to be generated randomly
    public String volume; // "ml";
    public String lockout; // "minutes";


    public PatientControlledEpiduralBolus() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.volume = "";
            this.lockout = "";
        }
    }
    public boolean process(Patient patient) {
        System.out.println("What's going on?  Why nothing here?");
        return true;
    }
}
