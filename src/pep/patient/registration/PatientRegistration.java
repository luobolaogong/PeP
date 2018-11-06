package pep.patient.registration;

import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.utilities.Arguments;

import java.util.logging.Logger;

// Registration encompasses Pre-Registration, New Patient Registration, Patient Information, and Update Patient.
// And each of these includes several sections, some of which are shared between these registrations such that
// the elements have the same locators.  There is no Registration class, but there should be.
// There's a Treatment class after all.
// Why wasn't this class called Registration?
public class PatientRegistration {
    private static Logger logger = Logger.getLogger(PatientRegistration.class.getName());
    public PreRegistration preRegistration;
    public PreRegistrationArrivals preRegistrationArrivals;
    public NewPatientReg newPatientReg;
    public PatientInformation patientInformation;
    public UpdatePatient updatePatient;

    public PatientRegistration() {
        if (Arguments.template) {
            preRegistration = new PreRegistration();
            preRegistrationArrivals = new PreRegistrationArrivals();
            newPatientReg = new NewPatientReg();
            patientInformation = new PatientInformation();
            updatePatient = new UpdatePatient();
        }
    }
}
