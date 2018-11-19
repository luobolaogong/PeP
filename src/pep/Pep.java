package pep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientsJson;
import pep.patient.registration.NewPatientReg;
import pep.patient.registration.PatientInformation;
import pep.patient.registration.PatientRegistration;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.PatientJsonReader;
import pep.utilities.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

//import static pep.Main.timerLogger;
import static pep.Main.timerLogger;
import static pep.utilities.Arguments.showHelp;
//import static pep.utilities.LoggingTimer.timerLogger;

/**
 * This class just contains code to drive the whole patient processing.  Shouldn't contain specific patient stuff.
 *
 * Pep now (may in the future) extends Thread so that maybe it can be made to run in a thread concurrently with
 * other instances in order to save memory when trying to run in a Grid environment.
 *
 * Here's a really rough "railroad" map through TMDS pages for patients:
 * Grammar  ::= ( ('Pre-Registration'* 'Pre-Registration Arrivals') | 'New Patient Reg.') ('Patient Info' | 'Pain Management' | 'Behavioral Health' | 'Tramatic Brain Injury')* 'Update Patient'+ 'Transfer Out'
 * See http://www.bottlecaps.de/rr/ui
 * Also, this is way cool:
 * https://github.com/tabatkins/railroad-diagrams
 */
public class Pep {
    private static Logger logger = Logger.getLogger(Pep.class.getName());
    static private final String TIER = "pep.encounter.tier"; // expected environment variable name if one is to be used

    static private final String CHROME_DRIVER_ENV_VAR = "webdriver.chrome.driver"; // expected environment variable name if one is to be used
    static private final String WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver.exe";
    static private final String NON_WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver";
    static public int PAGE_LOAD_TIMEOUT_SECONDS = 60;
    static public int SCRIPT_TIMEOUT_SECONDS = 10;

    static public boolean isGoldTier;
    static public boolean isDemoTier;
    static public boolean isTestTier; // just an idea.  None of this should be this way

    public Pep() {
    }

    // What's different between this and PatientStatus?
//    public enum PatientStatus {
//        INVALID,
//        NEW,
//        REGISTERED,
//        PREREGISTERED,
//        UPDATED,
//        DEPARTED
//    }

    // From PatientState:
//    PRE_REGISTRATION, (this is only for those going to Landstuhl (LRMC) supposedly.  Maybe not strictly)
//    NEW_REGISTRATION,
//    UPDATE_REGISTRATION,
//    PATIENT_INFO,
//    PRE_REGISTRATION_ARRIVALS,
//    NO_STATE
    // IN_TRANSIT ?????????????  This is only for PRE_REGISTRATION
    // OPEN_PRE_REGISTRATION ??????????
    // ARRIVED  ??????? which means now you can enter medical encounter info
    // ACTIVE  (one who has an open registration record at a MTF)
    // OPEN_REGISTRATION_RECORD (assigned either inpatient or outpatient treatment status)
    // INPATIENT
    // OUTPATIENT
    // NO_PATIENTS_FOUND


    /**
     * Process command line arguments and load patients.
     * @param args
     * @return
     */
    void loadAndProcessArguments(String[] args) {
//        System.out.println("In pep.Pep.loadAndProcessArguments(), This logger is ->" + logger.getName() + "<-");
//        System.out.println("This logger level is " + logger.getLevel() + " and if it's null then that probably means it inherits.");
//        logger.fine("This is a logger.fine message to say starting to load and process arguments");
//        logger.finest("logger.finest: this class Logger name: ->" + logger.getName() + "<-");
//        logger.finer("logger.finer: pep package Logger name: ->" + logger.getName());
//        logger.fine("logger.fine: this class Logger name: ->" + logger.getName() + "<-");
//        logger.info("logger.info: pep package Logger name: ->" + logger.getName() + "<-");
//        logger.warning("logger.warning: This is a timing warning: ->" + logger.getName() + "<-");
//        logger.severe("logger.severe: This is a severe message: ->" + logger.getName() + "<-");
//        logger.config("logger.config: this is a config message, and for some reason it doesn't come out unless logger is somehow configured for this.");

        Arguments arguments = Arguments.processCommandLineArgs(args);
        if (arguments == null) {
            Arguments.showUsage();
            System.exit(1);
        }

        doImmediateOptionsAndExit();

        Properties properties = loadPropertiesFile();

        // some of the following things could be defined in the input JSON file
        // But we don't load them yet.  Why?  Because maybe we don't know where those files are?
        establishPauses();
        establishTier(properties);
        useGrid(properties);
        establishUserAndPassword(properties);
        establishDate(properties);
        establishDriver(properties); // shouldn't this return success/failure?
    }

    //void doImmediateOptionsAndExit(Arguments Arguments) {
    void doImmediateOptionsAndExit() {
        if (Arguments.version) {
            System.out.println("Version: " + Main.version);
            System.exit(0);
        }
        if (Arguments.help) {
            showHelp();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.usage == true) {
            Arguments.showUsage();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.template) {
            isDemoTier = false; // hack to help clean up template results, because of demo stuff in constructors
            printTemplate();
            System.exit(0);
        }
    }

    Properties loadPropertiesFile() {
        // load in values into Properties object, which may specify user, password, tier, date, and driver
        File propFile;
        Properties properties = null;
        if (Arguments.propertiesUrl == null) {
            String currentDir = System.getProperty("user.dir");
            propFile = new File(currentDir, "pep.properties");
        } else {
            propFile = new File(Arguments.propertiesUrl);
        }
        if (!propFile.exists()) {
            propFile = null;
        }
        if (propFile != null) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(propFile.getAbsoluteFile()));
            } catch (Exception e) {
                logger.severe("Couldn't load properties file " + propFile.getAbsolutePath());
            }
        }
        return properties;
    }
    void establishPauses() {
        if (Arguments.pauseAll > 0) {
            Arguments.pausePatient = Arguments.pauseAll;
            Arguments.pausePage = Arguments.pauseAll;
            Arguments.pauseSection = Arguments.pauseAll;
            Arguments.pauseElement = Arguments.pauseAll;
        }
        if (Arguments.pauseElement > 0) {
            Arguments.pauseText = Arguments.pauseElement;
            Arguments.pauseDropdown = Arguments.pauseElement;
            Arguments.pauseRadio = Arguments.pauseElement;
            Arguments.pauseCheckbox = Arguments.pauseElement;
            Arguments.pauseDate = Arguments.pauseElement;
        }
    }
    void establishTier(Properties properties){
        // Establish Tier.
        // We give the option of specifying a tier name like "demo", or a host like "demo-tmds.akimeka.com"
        // or even a URI like  "https://demo-tmds.akimeka.com" or "https://demo-tmds.akimeka.com/portal"
        // Looks like maybe we need to strip of "/portal" if want to use tier in a page get (not a good thing to do, I think)
        if (Arguments.tier == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.get("tier");
            }
            if (value == null) {
                System.err.println("tier required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.tier = value;
        }
        if (Arguments.tier.contains("/portal")) {
            Arguments.tier = Arguments.tier.substring(0, Arguments.tier.indexOf("/portal"));
        }

        try {
            URI uri = new URI(Arguments.tier);
            String uriString = null;
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            if (scheme != null && host != null && path != null) {
                uriString = scheme + "://" + host + path;
                if (uriString.contains("/")) {
                    uriString.substring(0, uriString.indexOf("/"));
                }
            } else if (scheme != null && host != null && path == null) {
                uriString = scheme + "://" + host;
            } else if (scheme == null && host == null && path != null) {
                // at this point we've got path of either "test", or "test-tmds.akimeka.com"
                if (!path.contains("-")) { // test
                    uriString = "https://" + path + "-tmds.akimeka.com";
                } else { //   test-tmds.akimeka.com
                    uriString = "https://" + path;
                }
            }
            logger.info("Tier URI: " + uriString);
            if (uriString == null || uriString.isEmpty()) {
                System.err.println("Bad URI for host or tier: " + Arguments.tier);
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.tier = uriString;
        } catch (URISyntaxException e) {
            System.out.println("URI prob: " + e.getReason());
            System.out.println("URI prob: " + e.getMessage());
        }
        // THIS IS A TEMPORARY HACK
        // THIS IS A TEMPORARY HACK
        // THIS IS A TEMPORARY HACK
        // THIS IS A TEMPORARY HACK
        // Currently, DEMO and GOLD tiers are producing different DOM elements, and to handle both
        // tiers we'll temporarily set a global variable to use as branching mechanism.
        if (Arguments.tier.toLowerCase().contains("gold")) {
            logger.info("This is gold tier (" + Arguments.tier + ") with user " + Arguments.user);
            this.isGoldTier = true;
        }
        else if (Arguments.tier.toLowerCase().contains("test")) {
            logger.info("This is test tier (" + Arguments.tier + ") with user " + Arguments.user);
            this.isGoldTier = true; // of course wrong
        }
        else {
            this.isDemoTier = true;
            logger.info("This is demo tier (" + Arguments.tier + ") with user " + Arguments.user);
        }
        // and what about training, and test, and other tiers?

        if (Arguments.tier == null && properties != null) {
            Arguments.tier = properties.getProperty("tier");        // this can't happen, right?
        }

    }

    void useGrid(Properties properties) {
        // also do hub and server here? yes
        if (Arguments.gridHubUrl == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.get("hub");
                if (value == null) {
                    value = (String) properties.get("grid");
                }
            }
            Arguments.gridHubUrl = value;
        } else {
            // gridHubUrl could be a machine name, or localhost, or IP address.  It could have a port.  It could have scheme.  It shouldn't include "/wd/hub"
            // What the result should look like is "<scheme>://<host>:<port>/wd/hub"
            // Reasonable possibilities:
            // "<host>"
            // "<host>:<port>"
            // "<scheme>://<host>:<port>"
            // So we need to fill in the blanks.
            // If too hard, just limit to these format
            // http://10.5.4.168
            // http://10.5.4.168:4444
            try { // http://www.AkimekaMapServerT7400:4444
                URI uri = new URI(Arguments.gridHubUrl); // AkimekaMapServerT7400, http://AkimekaMapServerT7400, http://AkimekaMapServerT7400:4444, AkimekaMapServerT7400:4444
                String scheme = uri.getScheme(); // null, http, http, AkimekaMapServerT7400
                String host = uri.getHost(); // null, AkimekaMapServerT7400, AlimekaMapServerT7400, null
                String path = uri.getPath(); // AkimekaMapServerT7400, "", "", null
                int port = uri.getPort(); // -1, -1, 4444, -1

                StringBuffer uriStringBuffer = new StringBuffer();

                String uriString = uri.toString();

                if (uriString.startsWith("http")) {
                    uriStringBuffer.append(scheme); // could be https, perhaps
                } else {
                    uriStringBuffer.append("http");
                }

                uriStringBuffer.append("://");

                // this is hack code for now, this whole try catch
                if (path != null && host == null) {
                    host = path;
                }

                if (host != null) {
                    uriStringBuffer.append(host);
                } else {
                    uriStringBuffer.append("localhost");
                }

                uriStringBuffer.append(":");

                if (port != -1) {
                    uriStringBuffer.append(port);
                } else {
                    uriStringBuffer.append("4444");
                }

                uriStringBuffer.append("/wd/hub");

                uriString = uriStringBuffer.toString();

                logger.info("URI: " + uriString);

                if (uriString == null || uriString.isEmpty()) {
                    System.err.println("Bad URI for hub: " + Arguments.gridHubUrl);
                    System.out.println("Use -usage option for help with command options.");
                    System.exit(1);
                }
                Arguments.gridHubUrl = uriString;
            } catch (URISyntaxException e) {
                System.out.println("Hub URI prob: " + e.getReason());
                System.out.println("Hub URI prob: " + e.getMessage());
            }
        }
    }

    void establishUserAndPassword (Properties properties){
        if (Arguments.user == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.get("user");
            }
            if (value == null) {
                System.err.println("user required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.user = value;
        }

        // Establish password
        if (Arguments.password == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.get("password");
            }
            if (value == null) {
                System.err.println("password required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.password = value;
        }
    }

    void establishDate(Properties properties){

        // Establish date for encounters
        if (Arguments.date == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.get("date");
            }
            if (value == null) {
                if (Arguments.verbose) System.out.println("No date specified.  Date will be current date.");
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                TimeZone timeZone = TimeZone.getDefault();
                dateFormat.setTimeZone(timeZone);
                value = dateFormat.format(new Date());
            }
            Arguments.date = value;
        }
    }

    void establishDriver(Properties properties) {
        File chromeDriverFile = null;
        // If running remotely at server or hub we do not need the driver to sit on user's machine.
        // If running locally, insure we have a driver specified as a System property, since
        // that's what Selenium checks, although I suppose I could set it as a "binary" attribute.
        // Probably better.

        // This assumes we're running locally, so look for the chromedriver to be specified and
        // stored in Arguments, then properties then environment variables, then current directory.
        // Each one of those could be a bad URL, I guess and so do we check the file at each level,
        // or just make it simple and check the first one specified and that's it?  I think the latter.

        // In Args?
        String driverUrl = Arguments.driverUrl;

        // No? then properties file?
        if (driverUrl == null && properties != null) {
            driverUrl = properties.getProperty("chromedriver");
        }

        // No?  Then env variable?
        if (driverUrl == null) {
            driverUrl = System.getenv(CHROME_DRIVER_ENV_VAR);
        }

        // No? Then current dir?
        if (driverUrl == null) {
            String currentDirUrl = System.getProperty("user.dir");
            chromeDriverFile = new File(currentDirUrl, WIN_CHROME_DRIVER_EXECUTABLE_NAME);
            if (chromeDriverFile.exists()) {
                driverUrl = chromeDriverFile.getAbsolutePath();
            } else {
                chromeDriverFile = new File(currentDirUrl, NON_WIN_CHROME_DRIVER_EXECUTABLE_NAME);
                if (chromeDriverFile.exists()) {
                    driverUrl = chromeDriverFile.getAbsolutePath();
                }
            }
        }

        if (driverUrl != null) {
            chromeDriverFile = new File(driverUrl); // new
            if (chromeDriverFile.exists()) {
                System.setProperty(CHROME_DRIVER_ENV_VAR, chromeDriverFile.getAbsolutePath());
            }
        }
    }

    /**
     * This method uses GSON to parse a JSON file containing patient information -- patientRegistration
     * and treatments, loading the whole file into a Java objects called PatientsJson.  It dumps
     * it right into that object directly.  No manual parsing.  So a PatientsJson object represents
     * the JSON file.  Then as a Java object its parts can be retrieved easily.  For example, to
     * get the patient's first name it would be something like patientsJson.patientRegistration.newPatientReg.demographics.firstName
     * and treatments are in an array.  Just handle it like arrays in regular Java object.  So
     * this method returns one Java object representing one JSON file, you'd think.
     * But what about directories containing several JSON files?????????????????????
     * and it returns a PatientsJson object which is the result of parsing the JSON files.........
     * And what about "-random 5" on the command line?  Do 5 randoms and then the other specifieds?
     * @return
     */
    static List<Patient> loadPatients() {
        PatientsJson patientsJson = null;
        List<Patient> patients = new ArrayList<Patient>(); // I think I just added this.  Not sure how it affects the template output.  Check
        if (Arguments.patientsJsonUrl != null) {
            for (String patientJsonUrl : Arguments.patientsJsonUrl) {

                boolean fileExists = PatientJsonReader.patientJsonFileExists(patientJsonUrl);
                if (!fileExists) {
                    if (!Arguments.quiet) System.err.println("Input patient file " + patientJsonUrl + " cannot be found.  Check path.  Skipping it.");
                    continue;
                }
                // How do you tell GSON that some sections are required, like the patientSearch area?
                // I think it gets complicated.  So it's best just to check the results
                boolean isValidJson = PatientJsonReader.isValidPatientJson(patientJsonUrl);
                if (!isValidJson) {
                    if (!Arguments.quiet) System.err.println("Bad input patient file " + patientJsonUrl + "  Check content.  Skipping it.");
                    continue;
                }
                //
                // Load the patients from JSON.  We're not really using the class now for much more than holding the Patient list, I think.
                //
                patientsJson = PatientJsonReader.getSourceJsonData(patientJsonUrl);

                // Fix following logic.  And above too.
                if (patientsJson == null) {
                    if (!Arguments.quiet) System.err.println("Check JSON file.  No Patients JSON file found at " + patientJsonUrl);
                    continue;
                }
                if (patientsJson.patients != null) {
                    for (Patient patient : patientsJson.patients) {
                        // Create PatientSearch objects if missing, based on contents of Demographics.
                        //
                        // We could reject any patient object that didn't contain a PatientSearch object.  That would be easiest.
                        // If we want to help the user, we could create one from NewPatientReg, or UpdatePatientReg, or PatientInfo
                        // The logic would be "If PatientSearch missing, create one from NewPatientSearch, and if that was missing,
                        // create it from UpdatePatient, and if that was missing create it from PatientInfo, and if that was missing,
                        // reject the patient.
                        if (patient.patientSearch == null) { // what if already created, but firstName etc are null?
                            patient.patientSearch = new PatientSearch(); // probably do this earlier, maybe when PatientRegistration is added.
                            if (patient.patientRegistration != null) {
                                if (patient.patientRegistration.preRegistration != null) {
                                    if (patient.patientRegistration.preRegistration.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.patientRegistration.preRegistration.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.patientRegistration.preRegistration.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.patientRegistration.preRegistration.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.patientRegistration.preRegistration.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }

                                // preregistration arrival has a need for search because need to find the patient in the list that is presented.
                                // However, we wouldn't be using the PatientSearch class to help out.  If nothing is specified in the JSON input
                                // file for this page, then we'd maybe want to get it from PreRegistration section, and if not there, then other
                                // places.  But for now we should assume the user will fill in the fields for PreRegistrationArrivals, and these
                                // are the fields we're interested in: SSN (last 4), Last name,
                                // First name, gender, flight date, flight number, & rank.
                                //
                                else if (patient.patientRegistration.preRegistrationArrivals != null) {
                                    logger.fine("Pep.loadPatients(), Should do something about setting up search stuff for prereg arrivals?");
                                }

                                else if (patient.patientRegistration.newPatientReg != null) {
                                    if (patient.patientRegistration.newPatientReg.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.patientRegistration.newPatientReg.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.patientRegistration.newPatientReg.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.patientRegistration.newPatientReg.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.patientRegistration.newPatientReg.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }



                                else if (patient.patientRegistration.patientInformation != null) {
                                    logger.fine("Pep.loadPatients(), Should do something about setting up patientInformation search?");
                                }

                                else if (patient.patientRegistration.updatePatient != null) {
                                    if (patient.patientRegistration.updatePatient.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.patientRegistration.updatePatient.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.patientRegistration.updatePatient.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.patientRegistration.updatePatient.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.patientRegistration.updatePatient.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }
//                                // below is kinda strange.  Fix later.
//                                else if (patient.patientRegistration.patientInformation != null) {
//                                    if (Arguments.debug) {
//                                        System.out.println("PeP.loadPatients(), Skipping patient, missing patient information object.");
//                                    }
//                                }
//                                else if (patient.patientRegistration.preRegistration != null) {
//                                    if (!Arguments.quiet) {
//                                        System.err.println("Skipping patient, missing identification.");
//                                    }
//                                }
//                                else {
//                                    if (!Arguments.quiet) {
//                                        System.err.println("Skipping patient, missing identification.");
//                                    }
//                                }
                            }
                        }
                    }
                    patients.addAll(patientsJson.patients);
                }
            }
        }

        // If no patient json files specified, but --random 5 is, then we generate randoms
        // rather than check current dir for patient json files.
        // But why not both?  And why not also allow specified files?
        if (Arguments.random > 0 && patients.size() == 0) {
            for (int ctr = 0; ctr < Arguments.random; ctr++) {
                Patient patient = new Patient();
                //patient.random = true; // wow, doesn't this mean do everything, all sections, pages, and elements?
                patient.patientRegistration = new PatientRegistration(); // new, seems wrong.  Just to random=5  code from NPE's

                // just now 10/15/18 adding the following few lines.  Experimental.
                patient.patientRegistration.newPatientReg = new NewPatientReg();
                patient.patientRegistration.newPatientReg.random = true;
                patient.patientRegistration.patientInformation = new PatientInformation();
                patient.patientRegistration.patientInformation.random = true;
                patient.patientRegistration.newPatientReg.random = true;
                patient.treatments = Arrays.asList(new Treatment());
                patient.treatments.get(0).random = true;



                //patient.patientRegistration.process(patient); // totally new, totally untested, experimental mostly to make things more uniform, but also for PatientSearch support

                patients.add(patient);
            }
        }

        // If a patient file was specified, and it has at least one patient,
        // don't also do a directory of patient files, at least for now.
        if (Arguments.patientsJsonDir == null && Arguments.patientsJsonUrl == null) {
            Arguments.patientsJsonDir = System.getProperty("user.dir");
        }
        if (patients.size() == 0 && Arguments.patientsJsonDir != null) { // if we picked up patients already, then don't do this
            logger.fine("Will look for JSON files in " + Arguments.patientsJsonDir);
            File dir = new File(Arguments.patientsJsonDir);
            File[] files = dir.listFiles((dir1, name) -> name.endsWith(".json")); // a lambda for fun
            if (files != null) {
                logger.fine("nFiles: " + files.length);
                for (File file : files) {
                    String filePath = file.getPath();
                    logger.fine("File: " + filePath); // "D:\tmp\Patients\PorterHahn.json"
                    if (!PatientJsonReader.isValidPatientJson(file.getAbsolutePath())) {
                        if (!Arguments.quiet)
                            System.out.println("Patient JSON file \" " + file.getAbsolutePath() + " is invalid.  Skipping it.");
                        continue;
                    }
                    patientsJson = PatientJsonReader.getSourceJsonData(filePath); // takes .5 sec
                    // Fix following logic.  And above too.
                    if (patientsJson == null) {
                        if (!Arguments.quiet)
                            System.err.println("Check JSON file.  No Patients JSON file found at " + filePath);
                        continue;
                    }
                    if (patientsJson.patients != null) {
                        // Not sure why this is here
//                        for (Patient patient : patientsJson.patients) {
//                            if (patient.random == null && patientsJson.random != null && patientsJson.random > 0) {
//                                patient.random = true;
//                            }
//                        }
                        patients.addAll(patientsJson.patients);
                    } else {
                        logger.warning("Maybe there are no patients in the JSON file.  (And maybe no JSON file!)  But also maybe the JSON file just says random?");
                    }
                }
            }
            else {
                logger.fine("No patient input files found.");
            }
        }
        return patients;
    }

    static public void printTemplate() {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        PatientsJson patientsJson = new PatientsJson();
        Type patientsJsonTokenType = new TypeToken<PatientsJson>() {}.getType();
        String patientsJsonString = gson.toJson(patientsJson, patientsJsonTokenType);
        System.out.println(patientsJsonString);
    }

    static public void writePatientJson(Patient patient, String outputPatientUrl) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type patientTokenType = new TypeToken<Patient>() {}.getType();

        // This should probably be done at startup, and check if dir is writable, etc.
        String outputDirString = Arguments.patientsJsonOutDir;
        if (outputDirString == null || outputDirString.isEmpty()) {
            outputDirString = System.getProperty("user.dir");
        }
        try {
            File outputPatientJsonFile = new File(outputDirString, outputPatientUrl);
            FileOutputStream fileOutputStream = new FileOutputStream(outputPatientJsonFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write("{\"patients\":["); // make it so it can be read in as a json file
            gson.toJson(patient, patientTokenType, outputStreamWriter);
            outputStreamWriter.write("]}"); // so it can be read in
            outputStreamWriter.flush();
        }
        catch (Exception e) {
            logger.severe("Couldn't write file.  Exception: " + e.getMessage());
        }
    }

    //static void writePatients(List<Patient> patients, String outputPatientsUrl) {
    static public void writePatients(PatientsJson patientsJson, String outputPatientsUrl) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //Type patientsTokenType = new TypeToken<List<Patient>>() {}.getType(); // right?  made mistake?
        Type patientsTokenType = new TypeToken<PatientsJson>() {}.getType(); // right?  made mistake?

        // This should probably be done at startup, and check if dir is writable, etc.
        String outputDirString = Arguments.patientsJsonOutDir;
        if (outputDirString == null || outputDirString.isEmpty()) {
            outputDirString = System.getProperty("user.dir");
        }
        try {
            File outputPatientFile = new File(outputDirString, outputPatientsUrl);
            FileOutputStream fileOutputStream = new FileOutputStream(outputPatientFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            gson.toJson(patientsJson, patientsTokenType, outputStreamWriter);
            outputStreamWriter.flush();
        }
        catch (Exception e) {
            logger.severe("Couldn't write file.  Exception: " + e.getMessage());
        }
    }
    /**
     * This prints the resulting patients JSON output.  This should be the content of the output file.
     *
     * @param patients
     */
    static public void printPatientsJson(List<Patient> patients) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type patientsTokenType = new TypeToken<ArrayList<Patient>>() {}.getType();
        String patientJsonString = gson.toJson(patients, patientsTokenType);
        System.out.println(patientJsonString);
    }
    /**
     * This prints the resulting patients JSON output.  This should be the content of the output file.
     */
    static public void printPatientJson(Patient patient) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type patientTokenType = new TypeToken<Patient>() {}.getType();
        String patientJsonString = gson.toJson(patient, patientTokenType);
        System.out.println(patientJsonString);
    }

    // Time to start processing the list of patients we obtained by parsing the input JSON file.
    // That file (probably) would have contained one or more patients.  Each patient would have
    // contained optional sections: PatientSearch, PatientRegistration, and Treatments.  PatientSearch
    // is a section used in the PatientRegistration pages.  There are 5 of those.  The JSON file
    // can handle all 5 at once, but usually probably only one or two of those would be used.
    // This method doesn't process individual patients.  So it's the Patient class that must
    // determine which of the 5 to process.
    static boolean process(List<Patient> patients) {
        if (patients == null) {
            System.out.println("***No patients found in JSON file.");
            return false; // ???
        }
        int nErrors = 0;
        boolean success;
        for (Patient patient : patients) {
            if (!Arguments.quiet) System.out.println("Processing Patient ...");

            // A patient is represented by the top section in the input json/encounter file
            // and you can say "random":false, or "random":true, or "random":null, or nothing.
            // If you have nothing, then patient.random is null.  And we have to fix that.
            if (patient.random == null) { // totally new
                patient.random = false; // totally new 9/22/18
            }

            success = patient.process();

            if (success) {
//                if (!Arguments.quiet) System.out.println("Processed Patient: " +
//                        patient.patientSearch.firstName + " " +
//                        patient.patientSearch.lastName + " ssn:" +
//                        patient.patientSearch.ssn);
                if (!Arguments.quiet) System.out.println("Processed Patient:" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn))
                        );
            }


            // why are there two places for this?  One in Patient I think
            if (Arguments.printEachPatientSummary) {
                Pep.printPatientJson(patient);
            }
            if (Arguments.writeEachPatientSummary) {
                // Don't do the following unless there's something to write
                StringBuffer stringBuffer = new StringBuffer();
                // Maybe we should require patient search information in the JSON file
                stringBuffer.append(patient.patientSearch.firstName);
                stringBuffer.append(patient.patientSearch.lastName);
                stringBuffer.append(patient.patientSearch.ssn);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
                String hhMmSs = simpleDateFormat.format(new Date());
                stringBuffer.append(hhMmSs);
                stringBuffer.append(".json");
                Pep.writePatientJson(patient, stringBuffer.toString());
            }


            if (!success) {
                nErrors++;
            }

            if (Arguments.pausePatient > 0) {
                Utilities.sleep(Arguments.pausePatient * 1000);
            }
        }
        if (Arguments.printAllPatientsSummary) {
            printPatientsJson(patients);
        }
        if (Arguments.writeAllPatientsSummary) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("AllPatientsSummary");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss");
            String hhMmSs = simpleDateFormat.format(new Date());
            stringBuffer.append(hhMmSs);
            stringBuffer.append(".json");

            //writePatients(patients, stringBuffer.toString());
            PatientsJson patientsJson = new PatientsJson();
            patientsJson.patients = patients;
//            patientsJson.user = Arguments.user;
//            patientsJson.password = Arguments.password;
//            patientsJson.date = Arguments.date;
//            patientsJson.tier = Arguments.tier;
            writePatients(patientsJson, stringBuffer.toString());
        }
        if (nErrors > 0) {
            logger.fine("Errors occurred.  Probably more than " + nErrors);
            return false;
        }
        return true;
    }
}



