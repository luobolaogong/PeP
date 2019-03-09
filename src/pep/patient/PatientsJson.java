package pep.patient;

import pep.utilities.Arguments;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used for is to hold a List of Patient objects. It probably had other plans, perhaps related to PatientJsonReader.
 */
public class PatientsJson {
    public List<Patient> patients;

    public PatientsJson() {
        if (Arguments.template) {
            this.patients = Arrays.asList(new Patient());
        }
    }
}