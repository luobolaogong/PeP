package pep.patient;

import pep.utilities.Arguments;

import java.util.logging.Logger;

// Consider changing the name to be Search, and in the input files it would just be "search", rather than "patientSearch".  Fits better with "registration", and "treatments" and "summaries"
public class PatientSearch {
    private static Logger logger = Logger.getLogger(PatientSearch.class.getName());
    public Boolean random;
    public String ssn;
    public String lastName;
    public String firstName;
    public String traumaRegisterNumber;

    public PatientSearch() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template, but it will anyway I think
            this.ssn = "";
            this.lastName = "";
            this.firstName = "";
            this.traumaRegisterNumber = "";
        }
        // This is a test.  If okay, then remove the if template above.  We may want to do this to avoid npe's later when testing name for blank.
        this.ssn = "";
        this.lastName = "";
        this.firstName = "";
        this.traumaRegisterNumber = "";
    }

    public boolean process(Patient patient) {
        logger.fine("We should be consistent.  This is never called, right?");
        return true;
    }

}
