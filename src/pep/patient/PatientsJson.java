package pep.patient;

import pep.utilities.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class probably had other plans.  Probably related to PatientJsonReader.  Looks like the only thing this is
 * used for is to hold a List of Patient objects.
 */
public class PatientsJson {
    private static Logger logger = Logger.getLogger(PatientsJson.class.getName());
    public List<Patient> patients;

    public PatientsJson() {
        if (Arguments.template) {
            this.patients = Arrays.asList(new Patient());
        }
    }

    public boolean process(List<Patient> patients) {
        logger.fine("In PatientsJson, but don't expect will ever get here");
        return true;
    }
}