package pep.utilities;

// It's possible, I think to use org.openqa.selenium.json rather than have to load GSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import pep.patient.PatientsJson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.logging.Logger;

// Put this in PatientsJson ???  Probably
public class PatientJsonReader {
    private static Logger logger = Logger.getLogger(PatientJsonReader.class.getName());
    PatientJsonReader() {
    }

    //
    // This loads in the Patient JSON file and returns a list of Patient objects.
    // Right now this is based on the JSON file containing just an array of Patient
    // objects, and nothing more, like user name/password, tier, etc.  This may
    // be changed in the future so as to not requires so many params on command line
    // or require a properties file.  There could be some value in this because
    // a directory may exist with a bunch of JSON files and each one has a set
    // of patients, but each set might want to have a different user and tier
    // to simulate different MTFs and different permissions or something.
    // TODO: expand this so that a JSON file can have user/password/tier
    //
    public static PatientsJson getSourceJsonData(String patientsJsonUrl) {
        // Load the patient json data into a list of patients and return them.
        try {
            File patientJsonFile = new File(patientsJsonUrl);
            if (patientJsonFile.exists()) { // should be done way before now
                FileInputStream fileInputStream = new FileInputStream(patientJsonFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type patientSummaryRecordListType = new TypeToken<PatientsJson>() {}.getType(); // shouldn't call this patientSummaryRecordListType, because now we have a PatientSummary
                try {
                    PatientsJson patientsJson = gson.fromJson(inputStreamReader, patientSummaryRecordListType); // throws anything, complains if bad input?
                    // It's only at this time that "user", "password", "date", "tier" could be obtained from the input file.
                    // And if there are multiple files, then there could be different values for these things, which means
                    // that the program would have to logout and then log back in for the next patient.
                    // So even if the input file was loaded at the start, we'd still have to logout and log back in
                    // for each patient.  Also, the tier could be different.
                    // It might happen in the future that we have to run PeP with a patient that goes through different
                    // roles/levels, and to do that we'd have to have different users/passwords for each patient.
                    // So now I'm wondering how difficult it would be to do that, because it might be necessary in the future.
                    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    return patientsJson;
                }
                catch (JsonSyntaxException e) {
                    System.out.println("Check JSON file " + patientsJsonUrl + ".  There's probably a typo: " + e.getMessage());
                }
                catch (JsonIOException e) {
                    System.out.println("The json file wasn't found or there was some other IO error: " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Something wrong when calling fromJson: " + e.getMessage());
                }
            }
            else {
                logger.fine("No patient JSON file " + patientsJsonUrl + " found.");
                return null;
            }
        }
        catch(Exception e) {
            if (!Arguments.quiet) System.err.println("Couldn't parse JSON file " + patientsJsonUrl + ": " + e.getMessage());
            //System.exit(1);
        }
        return null; // should exit here?
    }

    public static boolean patientJsonFileExists(String patientsJsonUrl) {
        try {
            File patientJsonFile = new File(patientsJsonUrl);
            return patientJsonFile.exists();
        }
        catch (Exception e) {
            logger.fine("PatientJsonReader.patientJsonFileExists(), got an error: " + e.getMessage());
            return false;
        }
    }

    public static boolean isValidPatientJson(String patientsJsonUrl) {
        try {
            File patientJsonFile = new File(patientsJsonUrl);
            FileInputStream fileInputStream = new FileInputStream(patientJsonFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type patientSummaryRecordListType = new TypeToken<PatientsJson>() {
            }.getType();
            try {
                gson.fromJson(inputStreamReader, patientSummaryRecordListType); // throws anything, complains if bad input?
                return true;
            } catch (JsonSyntaxException e) {
                logger.fine("Check JSON file " + patientsJsonUrl + ".  There's probably a typo, like a missing comma.  Message: " + e.getMessage());
            } catch (JsonIOException e) {
                logger.fine("The json file wasn't found or there was some other IO error: " + e.getMessage());
            } catch (Exception e) {
                logger.fine("Something wrong when calling fromJson: " + e.getMessage());
            }
            return false;
        } catch (Exception e) {
            logger.severe("Couldn't parse JSON file " + patientsJsonUrl + ": " + e.getMessage());
            return false;
        }
    }
}

