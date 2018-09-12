package pep.patient;

import pep.patient.registration.PatientRegistration;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;

import java.util.Arrays;

public class PatientSearch {
    Boolean random;
    public String ssn;
    public String lastName;
    public String firstName;
    public String traumaRegisterNumber;

    public PatientSearch() {
        if (Arguments.template) {
            this.random = null;
            this.ssn = "";
            this.lastName = "";
            this.firstName = "";
            this.traumaRegisterNumber = "";
        }
    }

    public boolean process(Patient patient) {
        if (Arguments.debug) System.out.println("We should be consistent.  This is never called, right?");
        return true;
    }

}
