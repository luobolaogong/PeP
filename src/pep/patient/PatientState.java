package pep.patient;

// Let's assume a patient can be in only one state at a time,
// and yet the input file for a patient may contain info for more than one
// state, because there's data for PreRegistration, NewRegistration,
// UpdatePatient, PatientInfo, and PreRegistrationArrivals.
// When we're doing Demographics, we don't know if the data comes from
// NewRegistration, or UpdatePatient.
public enum PatientState {
    PRE_REGISTRATION,
    NEW_REGISTRATION,
    UPDATE_REGISTRATION,
    PATIENT_INFO,
    PRE_REGISTRATION_ARRIVALS,
    NO_STATE
}
