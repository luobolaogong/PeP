package pep.patient.registration;

import pep.patient.Patient;
import pep.patient.PatientSearch;
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
    // Kinda decided not to have a process() method in this class.
    // What it would do is currently being done elsewhere.  It could be cleaned up
    // and made more uniform later.

//    // This is just an idea.  I've not been using it:
//    public boolean process(Patient patient) {
//        boolean succeeded = false;
//        boolean hasNewPatientReg = patient.patientRegistration.newPatientReg != null;
//        boolean hasUpdatePatient = patient.patientRegistration.updatePatient != null;
//        boolean hasPreRegistration = patient.patientRegistration.preRegistration != null;
//        boolean hasPreRegistrationArrivals = patient.patientRegistration.preRegistrationArrivals != null;
//        boolean hasPatientInformation = patient.patientRegistration.patientInformation != null;
//        boolean hasSectionRandom = patient.random != null && patient.random == true;
//        if (patient.patientSearch == null) { // nec?
//            patient.patientSearch = new PatientSearch();
//            if (hasNewPatientReg) {
//                patient.patientSearch.firstName = patient.patientRegistration.newPatientReg.demographics.firstName;
//            }
//            else if (hasUpdatePatient) {
//                patient.patientSearch.firstName = patient.patientRegistration.updatePatient.demographics.firstName;
//            }
//            // could have others here
//        }
//        return true;
//    }

}
