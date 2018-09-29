package pep.patient;

import pep.utilities.Arguments;

import java.util.Arrays;
import java.util.List;

/**
 * This looks like it represents a single JSON file, which can hold multiple patients, and was not
 * meant to hold all patients from all JSON files.  But if we use that concept, then allowing a
 * particular user/password/tier/date to be specified per JSON file then we'd need to log out and log
 * back in for every user/password/tier processed when a directory of JSON files is processed, and
 * that's not the way things work right now, although I guess it might be a powerful concept.
 * What has been happening, I think, is that somehow all patients in all JSON files in a directory
 * have been accumulated into one list and then processed.  That's not working now, suddenly, and I
 * don't know what I've done.
 */
public class PatientsJson {
    // most of the following will probably be removed, except the list of patients.  This will simplify logic.
    // Unless we're going to process a list of PatientsJson objects, and logout/in with each one, we
    // should probably just have this stuff at a global/app level, and get it from command line or
    // properties file.
//    public String user;
//    public String password;
//    public String tier; // demo-tmds.akimeka.com or https://demo-tmds.akimeka.com or just demo (test,gold,...)
//    public String date;
//    public Integer random; // I'm not at all sure why this field is here.  Try to remove later.
    public List<Patient> patients;

    public PatientsJson() {
        if (Arguments.template) {
            //this.random = null; // don't want this to show up in template
            //            this.user = null; // don't want this to show up in template
            //            this.password = null; // don't want this to show up in template
            //            this.tier = null; // demo-tmds.akimeka.com or test-tmds.akimeka.com
            //            this.date = null; // taking this out of the template also
            //            this.random = null; // don't want this to show up in template
            this.patients = Arrays.asList(new Patient());
        }
    }

    //public void process(List<Patient> patients) {
    public boolean process(List<Patient> patients) {
        if (Arguments.debug) System.out.println("In PatientsJson, but don't expect will ever get here");
//        if (this.date != null) {
//            Arguments.date = this.date;
//        }
        return true;
    }
}