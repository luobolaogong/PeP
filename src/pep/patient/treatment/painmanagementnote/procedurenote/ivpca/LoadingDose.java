package pep.patient.treatment.painmanagementnote.procedurenote.ivpca;


import pep.utilities.Arguments;

public class LoadingDose {
    public Boolean random; // true if want this section to be generated randomly
    public String dose; // "mg";


    public LoadingDose() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.dose = "";
        }
    }

}
