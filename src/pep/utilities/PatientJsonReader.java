package pep.utilities;

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

/**
 * This class reads and validates the patient encounter input file, which is a JSON file.
 * GSON is used because it just loads a JSON file right into the full object structure representing
 * the whole set of pages, doing the object instantiation as necessary so that the elements are
 * available immediately.  Very cool.
 *
 * This class should probably have been part of PatientsJson
 */
public class PatientJsonReader {
    private static Logger logger = Logger.getLogger(PatientJsonReader.class.getName());

    PatientJsonReader() {
    }

    /**
     * Load a PatientsJson structure from the JSON input file.  This structure is a List of Patient objects
     *
     * @param patientsJsonUrl The URL for the input file
     * @return PatientsJson object which contains a list of Patient objects
     */
    public static PatientsJson getSourceJsonData(String patientsJsonUrl) {
        try {
            File patientJsonFile = new File(patientsJsonUrl);
            if (patientJsonFile.exists()) { // should be done way before now
                FileInputStream fileInputStream = new FileInputStream(patientJsonFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type patientsJsonListType = new TypeToken<PatientsJson>() {}.getType();
                try {
                    PatientsJson patientsJson = gson.fromJson(inputStreamReader, patientsJsonListType);
                    return patientsJson;
                }
                catch (JsonSyntaxException e) {
                    System.out.println("Check JSON file " + patientsJsonUrl + ".  There's probably a typo: " + Utilities.getMessageFirstLine(e));
                }
                catch (JsonIOException e) {
                    System.out.println("The json file wasn't found or there was some other IO error: " + Utilities.getMessageFirstLine(e));
                }
                catch (Exception e) {
                    System.err.println("Something wrong when calling fromJson: " + Utilities.getMessageFirstLine(e));
                }
            }
            else {
                logger.fine("No patient JSON file " + patientsJsonUrl + " found.");
                return null;
            }
        }
        catch(Exception e) {
            if (!Arguments.quiet) System.err.println("Couldn't parse JSON file " + patientsJsonUrl + ": " + Utilities.getMessageFirstLine(e));
            //System.exit(1);
        }
        return null; // should exit here?
    }

    /**
     * Check that the input patient encounter json file exists.
     * @param patientsJsonUrl The URL for the file
     * @return indication if exists
     */
    public static boolean patientJsonFileExists(String patientsJsonUrl) {
        try {
            File patientJsonFile = new File(patientsJsonUrl);
            return patientJsonFile.exists();
        }
        catch (Exception e) {
            logger.fine("PatientJsonReader.patientJsonFileExists(), got an error: " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }

    /**
     * Check that the patient encounter input JSON file is valid JSON.
     * @param patientsJsonUrl The URL of the input file
     * @return indication if it's valid JSON.  There's no schema.
     */
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
                logger.fine("Check JSON file " + patientsJsonUrl + ".  There's probably a typo, like a missing comma.  Message: " + Utilities.getMessageFirstLine(e));
                System.out.println("Bad input file.  JsonSyntaxException caught.  Check commas and such.  Exception Message: " + e.getMessage());
            } catch (JsonIOException e) {
                logger.fine("The json file wasn't found or there was some other IO error: " + Utilities.getMessageFirstLine(e));
            } catch (Exception e) {
                logger.fine("Something wrong when calling fromJson: " + Utilities.getMessageFirstLine(e));
            }
            return false;
        } catch (Exception e) {
            logger.severe("Couldn't parse JSON file " + patientsJsonUrl + ": " + Utilities.getMessageFirstLine(e));
            return false;
        }
    }
}

