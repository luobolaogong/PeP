package pep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientsJson;
import pep.patient.registration.Registration;
import pep.patient.registration.newpatient.NewPatientReg;
import pep.patient.registration.patientinformation.PatientInformation;
import pep.patient.summary.Summary;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.PatientJsonReader;
import pep.utilities.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pep.Main.pepLogger;
import static pep.Main.timerLogger;


/**
 * This class contains code to drive the whole patient processing. It also contains
 * a lot of preparatory stuff like argument and properties processing, and logging setup.
 * This is prototype code and is not organized as well as it should be.
 */
public class Pep {
    private static Logger logger = Logger.getLogger(Pep.class.getName());
    public static Properties pepProperties; // could be used elsewhere in future

    static private final String SELENIUM_CHROME_DRIVER_ENV_VAR = "webdriver.chrome.driver";
    static private final String chromeDriverEnvVarName = "CHROME_DRIVER";
    static private final String WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver.exe";
    static private final String NON_WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver";
    static public int PAGE_LOAD_TIMEOUT_SECONDS = 30;
    static public int ELEMENT_TIMEOUT_SECONDS = 5;
    static public int SCRIPT_TIMEOUT_SECONDS = 10;

    public Pep() {
    }

    /**
     * This is really where the patient encounter processing starts.  All the rest in this class is
     * getting ready for that.  Process the list of patients we obtained by parsing the input JSON file.
     *
     * @param patients List of Patient objects to process
     * @return success or (partial) failure
     */
    boolean process(List<Patient> patients) {
        if (patients == null) {
            System.out.println("***No patients found in JSON file.");
            return false; // ???
        }
        int nErrors = 0;
        boolean success;
        for (Patient patient : patients) {
            if (patient.encounterFileUrl != null) {
                if (!Arguments.quiet) System.out.println("Processing Patient from encounter file " + patient.encounterFileUrl + " ...");
            }
            else {
                if (!Arguments.quiet) System.out.println("Processing Patient ...");
            }

            success = patient.process();

            if (success) {
                if (!Arguments.quiet) System.out.println("Processed Patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn))
                );
            }
            else { // new 2/5/19
                if (!Arguments.quiet) System.out.println("Error(s) encountered while processing Patient" +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn))
                );
                // we may be sitting on a page somewhere because of a failure.  So we should get back to initial page.
                //Driver.driver.get("https://demo-tmds.akimeka.com/portal"); // experiment 3/6/19  bad URL
                //By PATIENT_REGISTRATION_MENU_LINK = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
                //boolean navigated = Utilities.myNavigate(PATIENT_REGISTRATION_MENU_LINK);
            }

            if (Arguments.printEachPatientSummary) {
                printPatientJson(patient);
            }
            if (Arguments.writeEachPatientSummary) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(patient.patientSearch.firstName);
                stringBuilder.append(patient.patientSearch.lastName);
                stringBuilder.append(patient.patientSearch.ssn);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
                String hhMmSs = simpleDateFormat.format(new Date());
                stringBuilder.append(hhMmSs);
                stringBuilder.append(".json");
                writePatientJson(patient, stringBuilder.toString());
            }

            if (!success) {
                nErrors++;
            }

            if (Arguments.pausePatient > 0) {
                Utilities.sleep(Arguments.pausePatient * 1000, "Pep");
            }
        }
        if (Arguments.printAllPatientsSummary) {
            printPatientsJson(patients);
        }
        if (Arguments.writeAllPatientsSummary) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("AllPatientsSummary");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss");
            String hhMmSs = simpleDateFormat.format(new Date());
            stringBuilder.append(hhMmSs);
            stringBuilder.append(".json");

            PatientsJson patientsJson = new PatientsJson();
            patientsJson.patients = patients;
            writePatients(patientsJson, stringBuilder.toString());
        }
        return (nErrors == 0);
    }

    /**
     * Load up the Arguments object using command line options and properties file and
     * environment variables.
     * @param args The command line arguments to invoking PeP
     * @return success/failure
     */
    boolean loadAndProcessArguments(String[] args) {
        Arguments arguments = Arguments.processCommandLineArgs(args);
        if (arguments == null) {
            Arguments.showUsage();
            System.exit(1);
        }

        if (Arguments.debug) {
            System.out.println("User account name: " + System.getProperty("user.name"));
            System.out.println("Home dir:: " + System.getProperty("user.home"));
            System.out.println("Cur dir:: " + System.getProperty("user.dir"));
            System.out.println("OS name: " + System.getProperty("os.name"));
            System.out.println("OS version: " + System.getProperty("os.version"));
            System.out.println("Java class path: " + System.getProperty("java.class.path"));
            System.out.println("Computer name: " + System.getenv("COMPUTERNAME"));
        }

        doImmediateOptionsAndExit();

        pepProperties = loadPropertiesFile();
        if (pepProperties == null) {
            logger.finer("Pep.loadAndProcessArguments(), failed to load properties file.  Which is probably okay if there isn't one.  Handled later");
        }
        // This is temporary, used for gathering bad XPath locators:
        if (pepProperties != null) {
            String catchBys = pepProperties.getProperty("catchBys", "false");
            if (catchBys.equalsIgnoreCase("true")) {
                Main.catchBys = true;
            }
        }
        establishBrowserSize(pepProperties);
        establishLogging(pepProperties);
        establishPauses();
        boolean establishedServerTierBranch = establishServerTierBranch(pepProperties);
        if (!establishedServerTierBranch) {
            logger.info("Couldn't establish server/tier, branch.");
            return false;
        }
        boolean goodGridInfo = useGrid(pepProperties);
        if (!goodGridInfo) {
            System.out.println("Cannot use Grid.  Bad specification for hub.");
        }
        establishUserAndPassword(pepProperties); // make this return boolean
        establishDate(pepProperties); // make this return boolean
        boolean establishedDriver = establishDriver(pepProperties);
        if (!establishedDriver) {
            logger.severe("Pep.loadAndProcessArguments(), failed to establish driver.");
            if (!Arguments.quiet) System.out.println("Could not find chromedriver executable.");
            return false;
        }
        return true;
    }

    /**
     * Some arguments to PeP are meant to just give some status/response and then quit, rather than
     * to continue on processing patients, like version, help, usage and template.  All other
     * arguments are dropped.
     */
    private void doImmediateOptionsAndExit() {
        if (Arguments.version) {
            System.out.println("Version: " + Main.version);
            System.exit(0);
        }
        if (Arguments.help) {
            Arguments.showHelp();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.usage) {
            Arguments.showUsage();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.template) {
            Arguments.codeBranch = "Spring"; // remove later
            printTemplate();
            System.exit(0);
        }
    }

    /**
     * Load in values into Properties object, which may specify user, password, tier, date,
     * and driver.  Using a properties file is optional.  It could be specified as a command line argument,
     * or it could be identified by a file in the current directory called pep.properties,
     * or it could be specified as an environment variable with the name PEP_PROPS_URL (?)
     *
     * This doesn't have to do with priorities of the properties themselves.
     *
     * If there are conflicts with propterties from the properties file, and what is specified
     * on the command line, or environment variable, the command line properties win over
     * properties file properties, which win over environment variable properties.
     *
     * @return Properties object corresponding to parsed elements in the properties file
     */
    private Properties loadPropertiesFile() {
        File propFile;
        Properties properties;
        if (Arguments.propertiesUrl == null) {
            String currentDir = System.getProperty("user.dir");
            propFile = new File(currentDir, "pep.properties");
            logger.finer("Pep.loadPropertiesFile(), will try to use property file in current dir: " + propFile.getAbsolutePath());
        } else {
            propFile = new File(Arguments.propertiesUrl);
            logger.finer("Pep.loadPropertiesFile(), will try to use property file specified as argument: " + propFile.getAbsolutePath());
        }
        if (!propFile.exists()) {
            logger.finer("Pep.loadPropertiesFile(), property file does not exist at " + propFile.getAbsolutePath());
            return null;
        }
        properties = new Properties();
        try {
            logger.finer("Pep.loadPropertiesFile(), will try to load property file: " + propFile.getAbsolutePath());
            properties.load(new FileInputStream(propFile.getAbsoluteFile()));
        } catch (Exception e) {
            logger.severe("Pep.loadPropertiesFile(), Couldn't load properties file " + propFile.getAbsolutePath());
            return null;
        }
        return properties;
    }

    /**
     * To semi simulate a user slowness in interacting with the GUI, this method
     * sets pause times for different kinds of elements encountered in MSAT when
     * PeP processes them.  Values not read from Properties file, only command line currently.
     */
    private void establishPauses() {
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
            Arguments.pauseSave = Arguments.pauseElement;
        }
    }

    /**
     * Set the browser size if you don't want default full screen.
     * Not sure about -headless size for screenshots.
     * @param properties width and height values can be in properties
     */
    private void establishBrowserSize(Properties properties) {
        if (properties == null) {
            return;
        }
        String width = properties.getProperty("width"); // NPE
        if (width != null) {
            Arguments.width = Integer.parseInt(width);
        }
        String height = properties.getProperty("height");
        if (height != null) {
            Arguments.height = Integer.parseInt(height);
        }
    }

    /**
     * Establish logging levels and output site for two loggers - regular and timer.
     * Expects loggers to have previously been created, and values can be overridden here.
     * Arguments trumps Properties.
     * Need to work on when this can be established.  Should be early.
     * And there is confusion about logger names.  This method should be reviewed.
     * @param properties contains log level type, place
     */
    private void establishLogging(Properties properties) {
        if (properties == null) {
            return;
        }
        // Arguments is already set by the time we get here, I hope.  True?
        String logLevel = properties.getProperty("logLevel");
        if (Arguments.logLevel == null && logLevel != null) {
            Arguments.logLevel = logLevel;
        }
        String logUrl = properties.getProperty("logUrl");
        if (Arguments.logUrl == null && logUrl != null) {
            Arguments.logUrl = logUrl;
        }
        String logTimerLevel = properties.getProperty("logTimerLevel");
        if (Arguments.logTimerLevel == null && logTimerLevel != null) {
            Arguments.logTimerLevel = logTimerLevel;
        }
        String logTimerUrl = properties.getProperty("logTimerUrl"); // still coming out to console, how assign to file?
        if (Arguments.logTimerUrl == null && logTimerUrl != null) {
            Arguments.logTimerUrl = logTimerUrl;
        }

        try {
            if (pepLogger.getLevel() == null) { // pepLogger or peppepLogger?
                pepLogger.setLevel(Level.OFF);
            }
            if (Arguments.debug) { // hmm, override pepLogger level if already set?  Okay, because will get reset again below.  Not good logic
                pepLogger.setLevel(Level.FINE); // this thing seems to also set the level for pepLogger, even though set for pepLogger
            }
            else if (Arguments.verbose) { // new 12/18/18  // not sure want to do this.  verbose is for user, not developer, so they'll see info, warning, severe
                pepLogger.setLevel(Level.INFO);
            }
            if (Arguments.logLevel != null) { // this setting takes prcedence over -verbose or --debug
                pepLogger.setLevel(Level.parse(Arguments.logLevel)); // this appears to set the level for pepLogger (too), so affects any subsequent pepLogger messages
            }

            // nec?
            if (timerLogger.getLevel() == null) { // logger or pepLogger or Main.timerLogger????
                timerLogger.setLevel(Level.OFF);
            }
            if (Arguments.logTimerLevel != null) {
                timerLogger.setLevel(Level.parse(Arguments.logTimerLevel)); // if no name specified, it goes to stdout?
            }


            if (Arguments.logUrl != null) { // this is where to send logging output.  So remove any handler and add a file handler
                try {
                    StringBuilder logUrlAppendThisBuffer = new StringBuilder();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String dateTime = simpleDateFormat.format(new Date());
                    logUrlAppendThisBuffer.append(dateTime);
                    logUrlAppendThisBuffer.append(".log");
                    FileHandler fileHandler = new FileHandler(Arguments.logUrl + logUrlAppendThisBuffer.toString(), false);

                    Handler[] handlers = pepLogger.getHandlers();
                    for (Handler handler : handlers) {
                        pepLogger.removeHandler(handler); // this is getting skipped.  So output goes to both file and stderr
                    }
                    pepLogger.addHandler(fileHandler);
                } catch (Exception e) {
                    logger.severe("Arguments.processCommandLineArgs(), Couldn't do a file handler for logging");
                }

            }
            if (Arguments.logTimerUrl != null) { // remove any handlers for this logger and add a file handler
                try {
                    StringBuilder logTimerUrlAppendThisBuffer = new StringBuilder();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String dateTime = simpleDateFormat.format(new Date());
                    logTimerUrlAppendThisBuffer.append(dateTime);
                    logTimerUrlAppendThisBuffer.append(".log");
                    FileHandler fileHandler = new FileHandler(Arguments.logTimerUrl + logTimerUrlAppendThisBuffer.toString(), false);
// Not sure why I added the following, but it makes timing info come out when running PeP as a jar.
//                    if (Arguments.logTimerLevel == null || Arguments.logTimerLevel.equalsIgnoreCase("OFF")) { // new 1/7/19
//                        Arguments.logTimerLevel = "INFO";
//                        timerLogger.setLevel(Level.INFO); // right?
//                    }

                    Handler[] handlers = timerLogger.getHandlers();
                    for (Handler handler : handlers) {
                        timerLogger.removeHandler(handler);
                    }
                    timerLogger.addHandler(fileHandler);
                } catch (Exception e) {
                    logger.severe("Arguments.processCommandLineArgs(), Couldn't do a file handler for timer logging");
                }
            }
        }
        catch (Exception e) {
            if (!Arguments.quiet) System.out.println("Could not fully set up logging: " + Utilities.getMessageFirstLine(e));
        }
    }

    /**
     * Establish the web server address and the corresponding code branch (Seam or Spring for now).
     *
     * The user can specify a tier, or the webserver and a code branch.  Most of the time the branch is going to be the
     * Spring code branch, so it's the default.
     *
     * A "Tier" is a set of servers and software versions that go together.  So the "Gold" tier means a particular
     * web server, and database server, and the Spring code branch.  PeP only cares about the web server URL,
     * and currently also the branch.  (Soon that should go away.)
     *
     * A webserver can be designated in a few different ways.  This code tries to handle the possibilities.
     *
     * @param properties The set of properties that may include tier, or web server, or code branch
     * @return success or failure
     */
    private boolean establishServerTierBranch(Properties properties) {
        // Get tier/server/branch values from the properties file if any specified
        if (properties != null) {
            String propertiesWebServerUrl = properties.getProperty("server");
            if (propertiesWebServerUrl == null || propertiesWebServerUrl.isEmpty()) {
                propertiesWebServerUrl = properties.getProperty("webserver");
            }
            String propertiesTier = properties.getProperty("tier");
            String propertiesCodeBranch = properties.getProperty("branch");
            if ((Arguments.webServerUrl == null || Arguments.webServerUrl.isEmpty())) {
                Arguments.webServerUrl = propertiesWebServerUrl;
            }
            if ((Arguments.tier == null || Arguments.tier.isEmpty())) {
                Arguments.tier = propertiesTier;
            }
            if ((Arguments.codeBranch == null || Arguments.codeBranch.isEmpty())) {
                Arguments.codeBranch = propertiesCodeBranch;
            }
        }

        // Get tier/server/branch values from the command line, and let them override any properties
        if ((Arguments.tier != null && !Arguments.tier.isEmpty())) {
            if (Arguments.tier.equalsIgnoreCase("GOLD")) {
                Arguments.webServerUrl = "https://gold-tmds.akimeka.com";
                Arguments.codeBranch = "Spring";
            } else if (Arguments.tier.equalsIgnoreCase("DEMO")) {
                Arguments.webServerUrl = "https://demo-tmds.akimeka.com";
                Arguments.codeBranch = "Spring"; // was Seam.  Changed 1/11/19
            } else if (Arguments.tier.equalsIgnoreCase("TEST")) {
                Arguments.webServerUrl = "https://test-tmds.akimeka.com";
                Arguments.codeBranch = "Seam";
            } else if (Arguments.tier.equalsIgnoreCase("TRAIN")) {
                Arguments.webServerUrl = "https://train-tmds.akimeka.com";
                Arguments.codeBranch = "Seam";
            }
            else {
                logger.warning("Pep.establishServerTierBranch(), unknown tier specified: " + Arguments.tier);
                return false;
            }
            return true;
        }

        // Give up if there's nothing specified for webserver by now.
        if (Arguments.webServerUrl == null || Arguments.webServerUrl.isEmpty()) {
            System.out.println("***Neither web server nor tier specified.");
            return false;
        }

        // Webserver may have an address.  Check for special combinations.
        if (!Arguments.webServerUrl.startsWith("http")) {
            String IPADDRESS_PATTERN_withOptionalPortNumberDoesNotWorkWithINetAddress =
                    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                            "(:(\\d+))?$";

            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN_withOptionalPortNumberDoesNotWorkWithINetAddress);

            Matcher matcher = pattern.matcher(Arguments.webServerUrl);
            if (matcher.matches()) { // no domain name, strictly IP address with optional port
                // Handle optional port by pulling it off and restoring it later.
                String port = matcher.group(6);
                // Check for valid IP address:
                Arguments.webServerUrl = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3) + "." + matcher.group(4);
                try {
                    logger.finest("Checking IP address " + Arguments.webServerUrl);
                    InetAddress iNetAddress = InetAddress.getByName(Arguments.webServerUrl); // will not take port
                    boolean canReach = iNetAddress.isReachable(1000); // false
                    logger.finest("Can reach " + iNetAddress.getHostAddress() + " : " + canReach);
                    if (canReach) {
                        Arguments.webServerUrl = "http://" + Arguments.webServerUrl; // note: not https
                    } else {
                        logger.info("Pep.establishServerTierBranch(), could not reach webServerUrl: " + Arguments.webServerUrl + ", generated using iNetAddress: " + iNetAddress.toString());
                        if (!Arguments.quiet) System.out.println("Cannot reach address " + Arguments.webServerUrl);
                        return false;
                    }
                    // Now that we're done with INetAddress, we tack the port back on, if there was one
                    if (port != null) {
                        Arguments.webServerUrl += (":" + port);
                    }
                } catch (Exception e) {
                    logger.severe("Didn't do inetaddress right.  e: " + e.getMessage());
                }
            }
            else {
                // Whatever we have, it does not start with http.
                // It could still start with a number, like a valid IP address, for example 10.50.8:100,
                // but we can't use that.  We can only use something like "someName" or "someName:80"
                if (Arguments.webServerUrl.matches("[0-9].*")) {
                    System.out.println("Cannot use this webserver url: " + Arguments.webServerUrl);
                    return false;
                }
                logger.finest("We have a domain name (with no protocol), not an address.  Add http or https?  try http");
                Arguments.webServerUrl = "http://" + Arguments.webServerUrl; // note: we choose to do http which might work, although https might work, or be better
            }
        }

        // Insure the correct code branch based on value of webServerUrl.
        if (Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) { // hmm, maybe should call this tmdsVersion or tmdsRelease
            if (Arguments.webServerUrl.toLowerCase().contains("gold")) {
                Arguments.codeBranch = "Spring";
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("demo")) {
                Arguments.codeBranch = "Spring";
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("test")) {
                Arguments.codeBranch = "Seam"; // right?
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("train")) {
                Arguments.codeBranch = "Seam";
            }
            else {
                logger.fine("No branch directive specified.  Will assume Spring version of TMDS.");
                Arguments.codeBranch = "Spring"; // not best solution, but need something for now because code needs it
            }
        }

        logger.fine("Pep.establishServerTierBranch(), webserver: " + Arguments.webServerUrl + " tier: " + Arguments.tier + " branch: " + Arguments.codeBranch);
        if (Arguments.webServerUrl == null || Arguments.webServerUrl.isEmpty()) {
            logger.warning("Couldn't establish webserver URL");
            return false;
        }
        if (Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) {
            logger.fine("Couldn't establish codeBranch, which may be okay at some time in the future.");
            return false;
        }
        return true;
    }

    /**
     * Establish the grid hub url, using property file or command line.
     * If return true then the url is expected to be a grid hub that knows about grid nodes.
     * We are not handing a server situation, where Selenium can run in server mode.  (are we?)
     * This method is not highly exercised.
     *
     * @param properties The properties that may contain the property hub or grid
     * @return whether to use a grid/hub configuration rather than a specific webserver.
     */
    private boolean useGrid(Properties properties) {
        // also do hub and server here? yes?  Check the Grid document?
        if (Arguments.gridHubUrl == null) {
            String value = null;
            if (properties != null) {
                value = properties.getProperty("hub");
                if (value == null) {
                    value = properties.getProperty("grid");
                }
            }
            Arguments.gridHubUrl = value; // should tack on protocol and port and page, etc?
        }
        if (Arguments.gridHubUrl == null) {
            return true;
        }
        try { // http://www.AkimekaMapServerT7400:4444
            try {
                // Do a quick verify of the reachability of the hub address.
                InetAddress iNetAddress = InetAddress.getByName(Arguments.gridHubUrl); // will not take port
                boolean canReach = iNetAddress.isReachable(1000); // effective???
                if (!canReach) {
                    Arguments.gridHubUrl = null;
                    return false;
                }
            }
            catch (MalformedURLException e) {
                logger.warning("Bad url for hub.");
                Arguments.gridHubUrl = null;
                return false;
            }
            catch (Exception e) {
                logger.warning("Some hub address prob? e: " + e.getMessage());
                Arguments.gridHubUrl = null;
                return false;
            }

            URI uri = new URI(Arguments.gridHubUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            int port = uri.getPort();

            StringBuilder uriStringBuilder = new StringBuilder();

            String uriString = uri.toString();

            if (uriString.startsWith("http")) {
                uriStringBuilder.append(scheme); // could be https, perhaps
            } else {
                uriStringBuilder.append("http");
            }

            uriStringBuilder.append("://");

            if (path != null && host == null) {
                host = path;
            }

            if (host != null) {
                uriStringBuilder.append(host);
            } else {
                uriStringBuilder.append("localhost");
            }

            uriStringBuilder.append(":");

            if (port != -1) {
                uriStringBuilder.append(port);
            } else {
                uriStringBuilder.append("4444");
            }

            uriStringBuilder.append("/wd/hub");

            uriString = uriStringBuilder.toString();

            logger.info("URI: " + uriString);

            if (uriString.isEmpty()) {
                System.err.println("Bad URI for hub: " + Arguments.gridHubUrl);
                Arguments.gridHubUrl = null;
                return false;
            }
            Arguments.gridHubUrl = uriString;
        } catch (URISyntaxException e) {
            System.out.println("Hub URI prob: " + e.getReason());
            Arguments.gridHubUrl = null;
            return false;
        }
        return true;
    }

    /**
     * Establish the user and password values from either command line or properties file if no command line values.
     * @param properties the user and password might be in the properties
     */
    private void establishUserAndPassword (Properties properties){
        if (Arguments.user == null) {
            String value = null;
            if (properties != null) {
                value = properties.getProperty("user");
            }
            if (value == null) {
                System.err.println("***user required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.user = value;
        }

        if (Arguments.password == null) {
            String value = null;
            if (properties != null) {
                value = properties.getProperty("password");
            }
            if (value == null) {
                System.err.println("***password required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.password = value;
        }
    }

    /**
     * Establish a "date" from command line, or properties if not specified.  This date is due
     * to an early request that date fields should be filled in with a value the user could
     * initially specify.  The problem with that is that most fields get the current date and you
     * cannot overwrite it.
     * @param properties The properties that may include a date
     */
    private void establishDate(Properties properties){

        // Establish date for encounters
        if (Arguments.date == null) {
            String value = null;
            if (properties != null) {
                value = properties.getProperty("date");
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

    /**
     * Establish a WebDriver for Selenium to work on the local machine.
     *
     * If running remotely at server or hub we do not need the driver to sit on user's machine.
     * If running locally, insure we have a driver specified as a System property, since
     * that's what Selenium checks, although I suppose I could set it as a "binary" attribute.
     * Probably better.
     *
     * This assumes we're running locally, so look for the chromedriver to be specified and
     * stored, in this order:
     *
     * Arguments, then properties, then environment variables, then current directory.
     *
     * Each one of those could be a bad URL, and for now just check the most significant one.
     *
     * Also Windows and Linux are different in environment variables.  Selenium itself looks for the
     * environment variable "webdriver.chrome.driver", which is a bad name for an environment variable
     * since most Unix/Linux shells don't allow dots in a name.  So, we try to allow for Linux
     * environment variables by checking WEBDRIVER_CHROME_DRIVER too, or instead.
     *
     * @param properties This may contain an indication of which driver to use
     * @return success or failure
     */
    private boolean establishDriver(Properties properties) {
        File chromeDriverFile;
        String driverUrl = Arguments.driverUrl;
        // Check properties file
        if (driverUrl == null && properties != null) {
            driverUrl = properties.getProperty("chromedriver"); // should be CHROME_DRIVER?
        }
        // Check environment variable
        if (driverUrl == null) {
            driverUrl = System.getenv(chromeDriverEnvVarName); // Only works if IntelliJ/Eclipse was started by a shell that knows this variable.
        }
        // Check current directory
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

        // Set the system property for Selenium to use
        if (driverUrl != null) {
            chromeDriverFile = new File(driverUrl); // new
            if (chromeDriverFile.exists()) {
                System.setProperty(SELENIUM_CHROME_DRIVER_ENV_VAR, chromeDriverFile.getAbsolutePath());
                return true;
            }
        }
        // If none of the above worked, then maybe on Windows Selenium will find the executable because uses
        // had set webdriver.chrome.driver variable in ENV var.  Otherwise, error.
        else if (System.getenv(SELENIUM_CHROME_DRIVER_ENV_VAR) != null) {
            return true;
        }
        return false;
    }

    /**
     * This method uses GSON to parse a JSON file containing patient information -- registration
     * and treatments and summary, loading the whole file into a Java objects called PatientsJson.  It dumps
     * it right into that object directly.  No manual parsing.  So a PatientsJson object represents
     * the JSON file.  Then as a Java object its parts can be retrieved easily.  For example, to
     * get the patient's first name it would be something like patientsJson.registration.newPatientReg.demographics.firstName
     * and treatments are in an array.  Just handle it like arrays in regular Java object.  So
     * this method returns one Java object representing one JSON file, you'd think.  And there could
     * be an array of patients/encounters.
     *
     * TODO: Shorten this method
     *
     * @return a list of patient objects
     */
    List<Patient> loadEncounters() {
        PatientsJson patientsJson;
        List<Patient> patients = new ArrayList<>();
        if (Arguments.patientsJsonUrl != null) {
            for (String patientJsonUrl : Arguments.patientsJsonUrl) {
                // Skip missing or invalid JSON files
                boolean fileExists = PatientJsonReader.patientJsonFileExists(patientJsonUrl);
                if (!fileExists) {
                    if (!Arguments.quiet) System.err.println("Input patient encounter file " + patientJsonUrl + " cannot be found.  Check path.  Skipping it.");
                    continue;
                }
                boolean isValidJson = PatientJsonReader.isValidPatientJson(patientJsonUrl); // or reasonably valid.  It conforms.  But no schema.
                if (!isValidJson) {
                    if (!Arguments.quiet) System.err.println("***Bad input patient encounter file " + patientJsonUrl + "  Check content.  Skipping it.");
                    continue;
                }
                //
                // Load the patients from JSON.
                //
                patientsJson = PatientJsonReader.getSourceJsonData(patientJsonUrl);
                // check again if we have anything at all.
                if (patientsJson == null) {
                    if (!Arguments.quiet) System.err.println("Check JSON file.  No Patients JSON file found at " + patientJsonUrl);
                    continue;
                }
                //
                // Run through each patient in the array of patients
                //
                if (patientsJson.patients != null) {
                    for (Patient patient : patientsJson.patients) {
                        patient.encounterFileUrl = patientJsonUrl;
                        //
                        // If a PatientSearch object is missing, create one from preReg, or newReg, or maybe updateReg.
                        // This section is a bit sketchy.  What if already created, but firstName etc are null?
                        //
                        if (patient.patientSearch == null) {
                            patient.patientSearch = new PatientSearch();
                            if (patient.registration != null) {
                                if (patient.registration.preRegistration != null) {
                                    if (patient.registration.preRegistration.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.registration.preRegistration.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.registration.preRegistration.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.registration.preRegistration.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.registration.preRegistration.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }
                                else if (patient.registration.preRegistrationArrivals != null) {
                                    logger.fine("Pep.loadEncounters(), Should do something about setting up search stuff for prereg arrivals?");
                                }
                                else if (patient.registration.newPatientReg != null) {
                                    if (patient.registration.newPatientReg.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.registration.newPatientReg.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.registration.newPatientReg.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.registration.newPatientReg.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.registration.newPatientReg.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }
                                else if (patient.registration.patientInformation != null) {
                                    logger.fine("Pep.loadEncounters(), Should do something about setting up patientInformation search?");
                                }
                                else if (patient.registration.updatePatient != null) {
                                    if (patient.registration.updatePatient.demographics != null) {
                                        if (patient.patientSearch.firstName == null) {
                                            patient.patientSearch.firstName = patient.registration.updatePatient.demographics.firstName;
                                        }
                                        if (patient.patientSearch.lastName == null) {
                                            patient.patientSearch.lastName = patient.registration.updatePatient.demographics.lastName;
                                        }
                                        if (patient.patientSearch.ssn == null) {
                                            patient.patientSearch.ssn = patient.registration.updatePatient.demographics.ssn;
                                        }
                                        if (patient.patientSearch.traumaRegisterNumber == null) {
                                            patient.patientSearch.traumaRegisterNumber = patient.registration.updatePatient.demographics.traumaRegisterNumber;
                                        }
                                    }
                                }
                                // You can't have a patientInformation established and useful without first having new patient or
                                // prereg patient.  So don't bother with PatientInformation to get search data.
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
                patient.registration = new Registration(); // new, seems wrong.  Just to random=5  code from NPE's

                patient.registration.newPatientReg = new NewPatientReg();
                patient.registration.newPatientReg.random = true;
                patient.registration.newPatientReg.shoot = false; // If user does -random 5 then they want all images for all sections for all 5 patients?

                patient.registration.patientInformation = new PatientInformation();
                patient.registration.patientInformation.random = true;
                patient.registration.patientInformation.shoot = false;

                patient.treatments = Arrays.asList(new Treatment());
                patient.treatments.get(0).random = true; // hey, just the first one, huh?

                patient.summaries = Arrays.asList(new Summary());
                patient.summaries.get(0).random = true;

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

    /**
     * Print a starting template to standard out.  Could be redirected to a file as
     * a starting point for creating a patient input file.
     */
    private void printTemplate() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls().create();
        Gson gson = builder.create();

        PatientsJson patientsJson = new PatientsJson();
        Type patientsJsonTokenType = new TypeToken<PatientsJson>() {}.getType();
        String patientsJsonString = gson.toJson(patientsJson, patientsJsonTokenType);
        System.out.println(patientsJsonString);
    }

    /**
     * Write a single patient's resulting information to a file.
     * @param patient The patient information to write
     * @param outputPatientUrl The file URL to write to
     */
    private void writePatientJson(Patient patient, String outputPatientUrl) {
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
            logger.severe("Couldn't write file.  Exception: " + Utilities.getMessageFirstLine(e));
        }
    }

    /**
     * Write resulting patient information to a file.
     * @param patientsJson The set of patients as one JSON file
     * @param outputPatientsUrl The URL of the output file
     */
    private void writePatients(PatientsJson patientsJson, String outputPatientsUrl) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            logger.severe("Couldn't write file.  Exception: " + Utilities.getMessageFirstLine(e));
        }
    }

    /**
     * Print the resulting patients' JSON output.  This is all patients processed, as a group.
     * @param patients List of patients to print
     */
    private void printPatientsJson(List<Patient> patients) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type patientsTokenType = new TypeToken<ArrayList<Patient>>() {}.getType();
        String patientJsonString = gson.toJson(patients, patientsTokenType);
        System.out.println(patientJsonString);
    }

    /**
     * Print the resulting processed patient JSON output.  Not necessarily the same
     * as the patient's JSON input.
     * @param patient A single patient
     */
    private void printPatientJson(Patient patient) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type patientTokenType = new TypeToken<Patient>() {}.getType();
        String patientJsonString = gson.toJson(patient, patientTokenType);
        System.out.println(patientJsonString);
    }

}



