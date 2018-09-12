package pep.patient.registration;

import pep.utilities.Arguments;

public class PatientRegistration {
    public PreRegistration preRegistration;
    public NewPatientReg newPatientReg;
    public UpdatePatient updatePatient;
    public PatientInformation patientInformation;
    public PreRegistrationArrivals preRegistrationArrivals;

    public PatientRegistration() {
        if (Arguments.template) {
            preRegistration = new PreRegistration();
            newPatientReg = new NewPatientReg();
            updatePatient = new UpdatePatient();
            patientInformation = new PatientInformation();
            preRegistrationArrivals = new PreRegistrationArrivals();
        }
    }
}
