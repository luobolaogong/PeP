package pep.patient.registration;

import pep.patient.registration.newpatient.NewPatientReg;
import pep.patient.registration.patientinformation.PatientInformation;
import pep.patient.registration.preregistration.PreRegistration;
import pep.patient.registration.preregistrationarrivals.PreRegistrationArrivals;
import pep.patient.registration.updatepatient.UpdatePatient;
import pep.utilities.Arguments;

import java.util.logging.Logger;

// Registration encompasses Pre-Registration, New Patient Registration, Patient Information, and Update Patient.
// There is no Registration page, and no Patient page; only navigation things.
// And each of these includes several sections, some of which are shared between these registrations such that
// the elements have the same locators.  There is no Registration class, but there should be.
// There's a Treatment class after all.
// Why wasn't this class called Registration?
public class Registration { // change this to Registration, if can
    private static Logger logger = Logger.getLogger(Registration.class.getName());
    public PreRegistration preRegistration;
    public PreRegistrationArrivals preRegistrationArrivals;
    public NewPatientReg newPatientReg;
    public PatientInformation patientInformation;  // change name to Information?
    public UpdatePatient updatePatient;

    public Registration() {
        if (Arguments.template) {
            preRegistration = new PreRegistration();
            preRegistrationArrivals = new PreRegistrationArrivals();
            newPatientReg = new NewPatientReg();
            patientInformation = new PatientInformation();
            updatePatient = new UpdatePatient();
        }
    }
    // Why do we have no process() method here?
}
