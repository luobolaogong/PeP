package pep.patient.registration;

import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.utilities.Arguments;

// Registration encompasses Pre-Registration, New Patient Registration, Patient Information, and Update Patient.
// And each of these includes several sections, some of which are shared between these registrations such that
// the elements have the same locators.  There is no Registration class, but there should be.
// There's a Treatment class after all.
// Why wasn't this class called Registration?
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
