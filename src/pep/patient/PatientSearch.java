package pep.patient;

import java.util.logging.Logger;

/**
 * This class holds the field values for a patient search form.  Consider changing the name to be Search,
 * and in the input files it would just be "search", rather than "patientSearch".  Fits better with "registration",
 * and "treatments" and "summaries"
 */
public class PatientSearch {
    private static Logger logger = Logger.getLogger(PatientSearch.class.getName());
    public Boolean sectionToBeRandomized;
    public String ssn;
    public String lastName;
    public String firstName;
    public String traumaRegisterNumber;

    public PatientSearch() {
        this.ssn = "";
        this.lastName = "";
        this.firstName = "";
        this.traumaRegisterNumber = "";
    }

}
