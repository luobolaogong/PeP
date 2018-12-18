package pep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pep.patient.Patient;
import pep.patient.PatientSearch;
import pep.patient.PatientsJson;
import pep.patient.registration.Registration;
import pep.patient.registration.newpatient.NewPatientReg;
import pep.patient.registration.patientinformation.PatientInformation;
import pep.patient.summary.Summary;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.PatientJsonReader;
import pep.utilities.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.security.Permission;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    static private final String SELENIUM_CHROME_DRIVER_ENV_VAR = "webdriver.chrome.driver"; // expected environment variable name if one is to be used
    //static private final String WIN_CHROME_DRIVER_ENV_VAR = "webdriver.chrome.driver"; // expected environment variable name if one is to be used
    //static private final String NON_WIN_CHROME_DRIVER_ENV_VAR = "webdriver_chrome_driver"; // expected environment variable name if one is to be used
    static private final String chromeDriverEnvVarName = "CHROME_DRIVER"; // expected environment variable name if one is to be used (Win and Linux)
    static private final String WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver.exe";
    static private final String NON_WIN_CHROME_DRIVER_EXECUTABLE_NAME = "chromedriver";
    static public int PAGE_LOAD_TIMEOUT_SECONDS = 30; // was 60
    static public int ELEMENT_TIMEOUT_SECONDS = 5; // new 12/13/18
    static public int SCRIPT_TIMEOUT_SECONDS = 10;

    //static public boolean isSpringCode; // was isGoldTier
    //static public boolean isSeamCode; // was isDemoTier

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
    //void loadAndProcessArguments(String[] args) {
    boolean loadAndProcessArguments(String[] args) {
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

        if (Arguments.debug) {
            System.out.println("User account name: " + System.getProperty("user.name"));
            System.out.println("Home dir:: " + System.getProperty("user.home"));
            System.out.println("Cur dir:: " + System.getProperty("user.dir"));
            System.out.println("OS name: " + System.getProperty("os.name"));
            System.out.println("OS version: " + System.getProperty("os.version"));
            System.out.println("Java class pth: " + System.getProperty("java.class.path"));
            System.out.println("Computer name: " + System.getenv("COMPUTERNAME"));
        }

        doImmediateOptionsAndExit();

        Properties pepProperties = loadPropertiesFile(); // did this work?  If not, don't pass it in later or handle null
        if (pepProperties == null) {
            logger.finer("Pep.loadAndProcessArguments(), failed to load properties file.  Which is probably okay if there isn't one.  Handled later");
        }
        // some of the following things could be defined in the input JSON file
        // But we don't load them yet.  Why?  Because maybe we don't know where those files are?
        establishPauses();
        boolean establishedServerTierBranch = establishServerTierBranch(pepProperties);
        if (!establishedServerTierBranch) {
            logger.severe("Pep.loadAndProcessArguments(), failed.  Check webserverURL");
            // what now?
            return false;
        }
        useGrid(pepProperties); // make this return boolean
        establishUserAndPassword(pepProperties); // make this return boolean
        establishDate(pepProperties); // make this return boolean
        boolean establishedDriver = establishDriver(pepProperties);
        if (!establishedDriver) {
            logger.severe("Pep.loadAndProcessArguments(), failed to establish driver.");
            return false;
        }
        return true;
    }

    //void doImmediateOptionsAndExit(Arguments Arguments) {
    void doImmediateOptionsAndExit() {
        if (Arguments.version) {
            System.out.println("Version: " + Main.version);
            System.exit(0);
        }
        if (Arguments.help) {
            Arguments.showHelp();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.usage == true) {
            Arguments.showUsage();
            System.exit(0); // should shut down driver first?
        }
        if (Arguments.template) {
            // prob get rid of next line????????????????????????????????????
            Arguments.codeBranch = "Spring"; // hack to help clean up template results, because of demo stuff in constructors
            printTemplate();
            System.exit(0);
        }
    }

    // What's the order of choosing a properties file?  1: Argument, 2: cur dir with name "pep.properties", 3: env var with name PEP_PROPS_URL(?)
    Properties loadPropertiesFile() {
        // load in values into Properties object, which may specify user, password, tier, date, and driver
        File propFile;
        Properties properties = null;
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
            propFile = null; // kinda silly to continue on.
            return null;
        }
        if (propFile != null) {
            properties = new Properties();
            try {
                logger.finer("Pep.loadPropertiesFile(), will try to load property file: " + propFile.getAbsolutePath());
                properties.load(new FileInputStream(propFile.getAbsoluteFile()));
            } catch (Exception e) {
                logger.severe("Pep.loadPropertiesFile(), Couldn't load properties file " + propFile.getAbsolutePath());
                return null;
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






    //void establishServerTierBranch(Properties properties) {
    // This needs to handle errors, and return status
    //void establishServerTierBranch(Properties properties) {
    boolean establishServerTierBranch(Properties properties) {
        // Here's my new thinking, as of 12/14/18:
        // "Tier" means one of the sets of servers (web server, db server) and software version (Spring, Seam, whatever in the future).
        // It's shorthand for those set of machines and software.  However, it mostly means the web server.  When you say "gold tier",
        // it means the server associated with http://gold-tmds.akimeka.com.  Even though that represents some front end thing that's
        // not really the web server, we can think of it as the webserver.  Associated with a tier (and web server) is a "code branch".
        // which represents a "version" of TMDS, like the Seam version, or the Spring version.  This is all put together from a table
        // of associations between "tier" and server and branch.
        //
        // PeP uses Selenium, and WebDriver, and WebDriver needs a URL to connect to.  I don't know if that's a full protocol thing
        // like http://gold-tmds.akimeka.com or perhaps even an address and possibly port, like 10.5.4.135:80 or whatever.
        // But you need one or either of those.
        //
        // The way to do it is this:
        // 0.  Set tier, server, and branch according to properties file if provided.  Then read command line args.
        // 1.  If "-tier" is provided, translate that into the server url that WebDriver needs, and set the branch.  Use a table?
        // 2.  Else, get the value of "-server" (web server URL or address and port)
        // 3.  And if "-branch" is given, set it.  Else, assume branch is Spring
        //
        if (properties != null) {
            //String propertiesWebServerUrl = properties.getProperty("webserverurl"); // npe?
            // Hey, the thing is, by the time we get here the values have already been set for logLevel, I think.
            if (Arguments.logLevel == null || Arguments.logLevel.isEmpty()) {
                String logLevelPropValue = properties.getProperty("logLevel"); // experimental.  "loglevel" better?  And how set at this point?
                if (logLevelPropValue != null) {
                    //logger.getParent().setLevel(Level.parse(logLevelPropValue)); // one or the other of these, I think
                    logger.setLevel(Level.parse(logLevelPropValue));
                }
            }





            String propertiesWebServerUrl = properties.getProperty("server");
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
        if ((Arguments.tier != null && !Arguments.tier.isEmpty())) { // not sure need that second check for empty
            if (Arguments.tier.equalsIgnoreCase("GOLD")) {
                Arguments.webServerUrl = "https://gold-tmds.akimeka.com";
                Arguments.codeBranch = "Spring";
            } else if (Arguments.tier.equalsIgnoreCase("DEMO")) {
                Arguments.webServerUrl = "https://demo-tmds.akimeka.com";
                Arguments.codeBranch = "Seam";
            } else if (Arguments.tier.equalsIgnoreCase("TEST")) {
                Arguments.webServerUrl = "https://test-tmds.akimeka.com";
                //Arguments.codeBranch = "Spring"; // right?
                Arguments.codeBranch = "Seam"; // try this.  Test is a mix of seam and spring???????????
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
        // Arguments.webServerUrl is either specified or not. If not it's an error, because we don't want to assume Gold tier.
        // If it is specified, it might not be correct, or if it is correct it might be in various formats,
        // like IP address, or partial URL.  We should allow full URL, but also partial, like "apple.com", or "apple.com:8080"
        // or "apple".  And we should also allow an IP address as in http://192.168.1.1 or http://192.168.1.1:8080,
        // or just 192.168.1.1 or just 192.168.1.1:8080
        // What does Selenium WebDriver.get() take?  It says "It's best to use a fully qualified URL" That's for the String
        // version.  It also takes a real URL.  So maybe turn the webServerUrl string into a URL and test it, and then
        // convert it back to a string.  Maybe.  Test it how?  Maybe URL.openConnection() ?
        //
        // A real URL can take an address, as in http://192.168.1.1.  It can take a port too.
        // Also, I'm not sure I can just add http://www to the front of something that doesn't have a protocol
        if (Arguments.webServerUrl == null || Arguments.webServerUrl.isEmpty()) {
            System.out.println("***Neither web server nor tier specified.");
            return false;
        }


        //Arguments.webServerUrl = "http://10.50.11.220"; // okay
        //Arguments.webServerUrl = "10.50.11.220"; // okay
        //Arguments.webServerUrl = "http://10.50.11.220:80"; // okay, fortunately
        //Arguments.webServerUrl = "10.50.11.220:80"; // okay if only add http not https
        //Arguments.webServerUrl = "https://10.50.11.220"; // work on this one next
        //Arguments.webServerUrl = "http://gold-tmds.akimeka.com"; // works
        //Arguments.webServerUrl = "https://gold-tmds.akimeka.com"; // works
        //Arguments.webServerUrl = "http://gold-tmds.akimeka.com:80"; // works
        //Arguments.webServerUrl = "gold-tmds.akimeka.com:80"; // success!  What?




        // If address has a port, take it off first to test validity using InetAddress, then tack it back on.
        // A port starts with ":", but http/https is followed by a ":".  So split and see how many parts.
//        String[] parts = Arguments.webServerUrl.split(":");
//        String addressNoPort = null;
//        String port = null;
//        int nParts = parts.length;
//        if (nParts == 1) {
//            addressNoPort = parts[0];
//        }
//        else if (nParts == 2) {
//            if (parts[0].startsWith("http")) {
//                addressNoPort = parts[1];
//            }
//        }
//        else {
//            if (parts[0].startsWith("http")) {
//                addressNoPort = parts[1];
//                port = parts[2];
//            }
//            else {
//                addressNoPort = parts[0];
//                port = parts[1];
//            }
//        }
//
//        Arguments.webServerUrl = addressNoPort; // for now




        // I'm not sure that InetAddress whatever is the best way to verify an IP address.
        // One problem is that it doesn't do parsing of a URL.  If the URL has a port in it,
        // INetAddress doesn't work.  I mean you have to pull off the port.  That requires some
        // parsing.

        // This is an attempt to verify an IP address.  It does not handle optional ports (":4443")
        // WebDriver.get() can use a URL like http://10.50.11.220, but not https://10.50.11.220, I think.
        // The protocol is always required with WebDriver.get().


        // I'm not sure we need to bother trying to do a name lookup to convert an address to "gold-tmds.akimeka.com".
        // It is slow and untested.
//        String IPADDRESS_PATTERN =
//                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
//                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";



        // Here's the logic:
        // The user has specified a server url or address or combination.  Here are the only ones that work with WebDriver.get():
        //
        // https://<ipAddress>
        // http://<ipAddress>
        // http://<ipAddress>:<port>
        //
        // https://<domain name>
        // http://<domain name>
        // http://<domain name>:<port>
        //
        // <domain name>:<port>
        //
        // Examples:
        // "http://10.50.11.220"
        // "http://10.50.11.220:80"
        // "http://gold-tmds.akimeka.com"
        // "http://gold-tmds.akimeka.com:80"
        // "gold-tmds.akimeka.com:80"
        // "https://10.50.11.220"
        // "https://gold-tmds.akimeka.com"
        //
        // So, we allow the user to supply as a argument value the following, which we DO NOT fix or check for validity:
        // http:<anything>
        // https:<anything>
        //
        // We do allow the user to supply as a argument value the following, which we try to fix:
        // <ipAddress> (but we don't know whether to add http or https.  Try http)
        // <ipAddress>:<port> (we add "http://")
        // <domain name>  (but we don't know whether to add http or https.  Try http)
        // <domain name>:<port> (either add "http://" or nothing)
        //
        // Since we don't mess with anything that starts with http or https, we should assume they're legal and pass them through.
        // While it's possible that an IP address can be converted to a domain or computer name, we don't do that, because we don't need to.
        // But we could try to check the IP address to see if it's valid.  Not sure it's worth the effort though, since we could let it blow up later on.
        // I guess we'll parse the value to see if it's an address or a domain name.
        // If it's an address then we check validity, and add "http://"
        // If it's a domain name without or without a port we add "http://" because WebDriver.get() requires a protocol, I think.
        // (Check "gold-tmds.akimeka.com:80")

        if (!Arguments.webServerUrl.startsWith("http")) {
            // check if IP address:
            String IPADDRESS_PATTERN_withOptionalPortNumberDoesNotWorkWithINetAddress =
                    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                            ":(\\d*)$";

            //Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN_withOptionalPortNumberDoesNotWorkWithINetAddress);

            Matcher matcher = pattern.matcher(Arguments.webServerUrl);
            if (matcher.matches()) { // no domain name, strictly IP address with optional port
                // Handle optional port by pulling it off and restoring it later.
                String port = matcher.group(5);
                // Check for valid IP address:
                Arguments.webServerUrl = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3) + "." + matcher.group(4);
                try {
                    logger.finest("Checking IP address " + Arguments.webServerUrl);
                    //System.out.println("Checking IP address " + addressNoPort);
                    InetAddress iNetAddress = InetAddress.getByName(Arguments.webServerUrl); // will not take port
                    //InetAddress iNetAddress = InetAddress.getByName(addressNoPort); // will not take port
                    // getting the host name is slow, and for the Gold IP address it just comes back with "10.50.11.220"
                    //String canonicalHostName = iNetAddress.getCanonicalHostName();
                    //String someHostAddress = iNetAddress.getHostAddress(); //"10.5.4.135"
                    boolean canReach = iNetAddress.isReachable(1000); // false
                    System.out.println("Can reach " + iNetAddress.getHostAddress() + " : " + canReach);
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
                logger.finest("We have a domain name (with no protocol), not an address.  Add http or https?  try http");
                // Check "gold-tmds.akimeka.com:80" ??
                if (!Arguments.webServerUrl.contains(":")) {
                    Arguments.webServerUrl = "http://" + Arguments.webServerUrl; // note: we choose to do http which might work, although https might work, or be better
                }
                else {
                    logger.finest("It contained a port, so we don't add a protocol, just to see what happens.");
                }
            }
//            // webServerUrl has a value, check if valid.
//            // Looks like URL requires a protocol ("http://") so add it if doesn't start with protocol
//            if (!Arguments.webServerUrl.toLowerCase().startsWith("http")) {
//                System.out.println("Adding https:// to start of " + Arguments.webServerUrl);
//                Arguments.webServerUrl = "https://" + Arguments.webServerUrl; // this may be wrong.  Don't know about http or https
//            }

// Not sure need to test the connection here
//            // Now test the connection no matter what we ended up with?
//            try {
//                // Do some minimal tests
//                URL testUrl = new URL(Arguments.webServerUrl); // okay: http://www.apple.com, http://17.142.160.59, http://17.142.160.59:80,
//                URLConnection urlConnection = testUrl.openConnection();
//                urlConnection.connect(); // this throws if the URL is bad.  Cannot handle https://10.50.11.220:80, but can handle http://10.50.11.220
//                //Permission permission = urlConnection.getPermission();
//                //String permissionString = permission.toString();
//                //Object content = urlConnection.getContent();
//                //int contentLength = urlConnection.getContentLength();
//                //permission.
//            } catch (MalformedURLException e) { // fails because no protocol: apple.com, www.apple.com, 17.142.160.59, localhost
//                System.out.println("Pep.establishServerTierBranch(), malformed url exception due to " + Arguments.webServerUrl + " e: " + e.getMessage());
//                return false;
//            }
////        catch (java.security.cert.CertificateException e) { // connection refused: http://localhost (I'm not running a server), http://apple
////        //catch (java.security.cert.CertificateException e) { // connection refused: http://localhost (I'm not running a server), http://apple
////            System.out.println(e.getMessage());
////            return false;
////        }
//            catch (Exception e) { // CertificateException "No subject alternative names matching IP address 10.50.11.220 found",  connection refused: http://localhost (I'm not running a server), http://apple
//                System.out.println("Pep.establishserverTierBranch(), e: " + e.getMessage());
//                return false;
//            }

        }






        // not sure how best to handle this.
        if (Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) { // hmm, maybe should call this tmdsVersion or tmdsRelease
            if (Arguments.webServerUrl.toLowerCase().contains("gold")) {
                Arguments.codeBranch = "Spring";
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("demo")) {
                Arguments.codeBranch = "Seam";
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("test")) {
                Arguments.codeBranch = "Seam"; // right?
            }
            else if (Arguments.webServerUrl.toLowerCase().contains("train")) {
                Arguments.codeBranch = "Seam";
            }
            else {
                logger.warning("No branch directive specified.  Will assume Spring version of TMDS.");
                Arguments.codeBranch = "Spring"; // not best solution, but need something for now because code needs it
            }
        }



        //        if (properties != null) {
//            // This next section is kinda one way to do this properties stuff, but it conflicts below with the way I was doing it before.  Logic is shaky.
//            String propertiesWebServerUrl = properties.getProperty("webServerUrl"); // npe
//            String propertiesTier = properties.getProperty("tier");
//            String propertiesCodeBranch = properties.getProperty("codeBranch");
//            if ((webServerUrl == null || webServerUrl.isEmpty())) {
//                webServerUrl = propertiesWebServerUrl;
//            }
//            if ((tier == null || tier.isEmpty())) {
//                tier = propertiesTier;
//            }
//            if ((codeBranch == null || codeBranch.isEmpty())) {
//                codeBranch = propertiesCodeBranch;
//            }
//        }
//        // This used to be the following, when was just one thing, "tier":
//        // We give the option of specifying a tier name like "demo", or a host like "demo-tmds.akimeka.com"
//        // or even a URI like  "https://demo-tmds.akimeka.com" or "https://demo-tmds.akimeka.com/portal"
//        // Looks like maybe we need to strip of "/portal" if want to use tier in a page get (not a good thing to do, I think)
//        /*
//         * Regarding "tier" and code technology (Seam/Spring) and webserver address...  These have all been combined so that
//         * if you specified a tier name, like "gold", or a url like "http://tmds-gold.akimeka", or variation,
//         * then execution of code would branch at various places to account for differences between seam and spring.
//         *
//         * That's not good enough now because we should handle other webservers, and we should be able to
//         * independently specify the code technology (seam or spring), for example when I have to support
//         * "localhost" as my webserver, I want to change whether my webserver is running seam or spring.
//         *
//         * Eventually this code technology difference will go away, but for now it's staying in.
//         *
//         * The most important piece of information is "webServerUrl" in order to bring up the app.  That must be supported
//         * as a full URL ("http://gold-tmds.akimeka.com") and maybe (prob not) abbreviations of the full URL
//         * like "tmds-gold".  "Tier" is just a convenience/shorthand term for a webServerUrl.  So, if Tier is
//         * specified it will expand to commonly accepted full URL. And codeTech could be assumed from webServerUrl
//         * or Tier, but could also be specified as an override.
//         *
//         * CodeTech could be a boolean "codeBranch" (otherwise Spring is assumed).
//         *
//         * The simplest thing to do would be require webServerUrl and codeTech, and forget about tier.  But we'll allow tier.
//         * All 3 should have values, either assumed or inferred or set.
//         */
//        // Hey what if the webServerUrl is just an address like 10.5.4.135 ? or 10.5.4.135:8080  ?  Do we allow that?
//        // Url's are not addresses, but addresses can be part of Url's.  That is http://10.5.4.135  So if we get an address,
//        // we can just put http;// in front of it, or https:// in front of it and then treat it as a URI/URL.
//        // Maybe we should just tack on "http://" to the front of anything that starts with a number?  Or maybe if it
//        // fits the pattern of x.x.x.x where x is any number 0-255.  Or maybe we should use InetAddress to get a host name
//        // and substitute it.
//        // We want to support "localhost", "127.0.0.1", "192.168.1.1" "apple.com", http://10.5.4.135:8080
//        //
//        // New 12/13/18:
//        //
//        // PeP needs to access a web server service.  like xxxxxxxxxxxxx
//        // "Tier" has to do with the shorthand tier names often known as "gold", "demo", "test", ... and these have
//        // corresponding webserver name URL's, like "gold-tmds.akimeka.com" or "http://gold-tmds.akimeka.com", etc.  So "-tier gold"
//        // allows the user to use a shorthand for a webserver URL like "gold-tmds.akimeka.com" or "http://gold-tmds.akimeka.com".
//        // But PeP should also be able to use a webserver IP address like "10.5.4.135" or "10.5.4.135:80", or perhaps "localhost"?,
//        // or perhaps "http://10.5.4.135:8800", and these are not "tiers".
//        //
//        // But what PeP needs is a webserver URL like or a webserver IP address
//        if (Arguments.webServerUrl != null && !Arguments.webServerUrl.isEmpty()) { // using isEmpty but isBlank cold be used for a change)
////            try {
////                InetAddress iNetAddress = InetAddress.getByName(Arguments.webServerUrl); // will not take port
////                String someHostAddress = iNetAddress.getHostAddress();
////                boolean canReach = iNetAddress.isReachable(1000);
////                String canHostName = iNetAddress.getCanonicalHostName();
////                String hostName = iNetAddress.getHostName();
////                System.out.println(hostName);
////                Arguments.webServerUrl = hostName;
////            }
////            catch (Exception e) {
////                System.out.println("Bad address?");
////            }
//            // Check that the URL is valid.
//            // The following stuff assumes a lot that should not be assumed.  Rewrite.  What kinds of things do we want to support?
//            try {
//                URI uri = new URI(Arguments.webServerUrl); // assumed to be HTTP URL // should be new URL()?
//                String uriString = null;
//                String scheme = uri.getScheme();
//                String host = uri.getHost();
//                String path = uri.getPath();
//                int port = uri.getPort(); // new, for experiment
//                if (scheme != null && host != null && path != null) {
//                    uriString = scheme + "://" + host + path;
//                    if (uriString.contains("/")) {
//                        uriString.substring(0, uriString.indexOf("/"));
//                    }
//                } else if (scheme != null && host != null && path == null) {
//                    uriString = scheme + "://" + host;
//                } else if (scheme == null && host == null && path != null) {
//                    // at this point we've got path of either "test", or "test-tmds.akimeka.com"
//                    if (!path.contains("-")) { // test
//                        uriString = "https://" + path + "-tmds.akimeka.com"; // we cannot do this.  This is just for a few hosts
//                    } else { //   test-tmds.akimeka.com
//                        uriString = "https://" + path;
//                    }
//                }
//                //logger.info("web server URI: " + uriString);
//                if (uriString == null || uriString.isEmpty()) {
//                    System.err.println("Bad URI for host or tier: " + Arguments.webServerUrl);
//                    System.out.println("Use -usage option for help with command options.");
//                    System.exit(1);
//                }
//                Arguments.webServerUrl = uriString;
//            } catch (URISyntaxException e) {
//                System.out.println("webserver URI prob: " + e.getReason());
//                System.out.println("webserver URI prob: " + Utilities.getMessageFirstLine(e));
//            }
//
//
//
//            // check, check check next time
//
//
//            if (Arguments.tier == null || Arguments.tier.isEmpty()) {
//                String value = null;
//                if (properties != null) {
//                    value = (String) properties.getProperty("tier");
//                }
//                if (value == null) {
//                    if (Arguments.webServerUrl.toLowerCase().contains("gold")) {
//                        Arguments.tier = "GOLD"; // caps?
//                    } else if (Arguments.webServerUrl.toLowerCase().contains("demo")) {
//                        Arguments.tier = "DEMO"; // caps?
//                    } else if (Arguments.webServerUrl.toLowerCase().contains("test")) {
//                        Arguments.tier = "TEST"; // caps?
//                    } else if (Arguments.webServerUrl.toLowerCase().contains("train")) {
//                        Arguments.tier = "TRAIN"; // caps?
//                    } else if (Arguments.webServerUrl.toLowerCase().contains("localhost")) { // just a guess
//                        Arguments.tier = "DEV"; // Wild guess
//                    }
//                }
//
//                if (Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) {
//                    if (Arguments.tier.equalsIgnoreCase("GOLD")) { // npe
//                        Arguments.codeBranch = "Spring";
//                    }
//                    else if (Arguments.tier.equalsIgnoreCase("DEMO")) {
//                        Arguments.codeBranch = "Seam";
//                    }
//                    else if (Arguments.tier.equalsIgnoreCase("TEST")) {
//                        Arguments.codeBranch = "Seam";
//                    }
//                    else if (Arguments.tier.equalsIgnoreCase("TRAIN")) {
//                        Arguments.codeBranch = "Seam";
//                    }
//                    else {
//                        Arguments.codeBranch = "Spring";
//                    }
//                }
//            }
//            else { // tier is specified
//                if (Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) {
//                    if (Arguments.webServerUrl.toLowerCase().contains("gold")) {
//                        Arguments.codeBranch = "Spring";
//                    }
//                    else if (Arguments.webServerUrl.toLowerCase().contains("demo")) {
//                        Arguments.codeBranch = "Seam";
//                    }
//                    else if (Arguments.tier.equalsIgnoreCase("test")) {
//                        Arguments.codeBranch = "Spring"; // right?
//                    }
//                    else if (Arguments.tier.equalsIgnoreCase("train")) {
//                        Arguments.codeBranch = "Seam";
//                    }
//                    else {
//                        Arguments.codeBranch = "Spring";
//                    }
//                }
//            }
//        }
//        else { // no webserver url specified
////            if (Arguments.tier == null || Arguments.tier.isEmpty()) {
////                if (!Arguments.quiet) {
////                    System.out.println("Cannot access TMDS because no webserver URL or tier specified.");
////                }
////                return;
////            }
//            if (Arguments.tier != null) {
//                if (Arguments.tier.equalsIgnoreCase("GOLD")) {
//                    Arguments.webServerUrl = "https://gold-tmds.akimeka.com";
//                } else if (Arguments.tier.equalsIgnoreCase("DEMO")) {
//                    Arguments.webServerUrl = "https://demo-tmds.akimeka.com";
//                } else if (Arguments.tier.equalsIgnoreCase("TEST")) {
//                    Arguments.webServerUrl = "https://test-tmds.akimeka.com";
//                } else if (Arguments.tier.equalsIgnoreCase("TRAIN")) {
//                    Arguments.webServerUrl = "https://train-tmds.akimeka.com";
//                }
//            }
//            //else {
//            //    System.out.println("Shouldn't get here.");
//            //}
//            if ((Arguments.codeBranch == null || Arguments.codeBranch.isEmpty()) && Arguments.tier != null) {
//            //if (Arguments.codeBranch != null) {
//                if (Arguments.tier.equalsIgnoreCase("GOLD")) {
//                    Arguments.codeBranch = "Spring";
//                }
//                else if (Arguments.tier.equalsIgnoreCase("DEMO")) {
//                    Arguments.codeBranch = "Seam";
//                }
//                else if (Arguments.tier.equalsIgnoreCase("TEST")) {
//                    Arguments.codeBranch = "Seam";
//                }
//                else if (Arguments.tier.equalsIgnoreCase("TRAIN")) {
//                    Arguments.codeBranch = "Seam";
//                }
//                else {
//                    Arguments.codeBranch = "Spring";
//                }
//            }
//        }
//
//        // Here is experimentation without taking time to think.  What if after the above these values are not set?
//        if (properties != null) {
//            // This next section is kinda one way to do this properties stuff, but it conflicts below with the way I was doing it before.  Logic is shaky.
//            //String propertiesWebServerUrl = properties.getProperty("webServerUrl"); // npe
//            String propertiesWebServerUrl = properties.getProperty("webserverurl"); // npe
//            String propertiesTier = properties.getProperty("tier");
//            String propertiesCodeBranch = properties.getProperty("codebranch");
//            if ((Arguments.webServerUrl == null || Arguments.webServerUrl.isEmpty())) {
//                Arguments.webServerUrl = propertiesWebServerUrl;
//            }
//            if ((Arguments.tier == null || Arguments.tier.isEmpty())) {
//                Arguments.tier = propertiesTier;
//            }
//            if ((Arguments.codeBranch == null || Arguments.codeBranch.isEmpty())) {
//                Arguments.codeBranch = propertiesCodeBranch;
//            }
//        }
        logger.info("Pep.establishServerTierBranch(), webserver: " + Arguments.webServerUrl + " tier: " + Arguments.tier + " branch: " + Arguments.codeBranch);
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

    void useGrid(Properties properties) {
        // also do hub and server here? yes
        if (Arguments.gridHubUrl == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.getProperty("hub");
                if (value == null) {
                    value = (String) properties.getProperty("grid");
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
                System.out.println("Hub URI prob: " + Utilities.getMessageFirstLine(e));
            }
        }
    }

    void establishUserAndPassword (Properties properties){
        if (Arguments.user == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.getProperty("user");
            }
            if (value == null) {
                System.err.println("***user required");
                System.out.println("Use -usage option for help with command options.");
                System.exit(1);
            }
            Arguments.user = value;
        }

        // Establish password
        if (Arguments.password == null) {
            String value = null;
            if (properties != null) {
                value = (String) properties.getProperty("password");
            }
            if (value == null) {
                System.err.println("***password required");
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
                value = (String) properties.getProperty("date");
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

    //void establishDriver(Properties properties) {
    boolean establishDriver(Properties properties) {
        File chromeDriverFile = null;
        // If running remotely at server or hub we do not need the driver to sit on user's machine.
        // If running locally, insure we have a driver specified as a System property, since
        // that's what Selenium checks, although I suppose I could set it as a "binary" attribute.
        // Probably better.
        //
        // This assumes we're running locally, so look for the chromedriver to be specified and
        // stored, in this order:
        //
        // Arguments, then properties, then environment variables, then current directory.
        //
        // Each one of those could be a bad URL, I guess and so do we check the file at each level,
        // or just make it simple and check the first one specified and that's it?  I think the latter.
        //
        // Also Windows and Linux are different in environment variables.  Selenium itself looks for the
        // environment variable "webdriver.chrome.driver", which is a stupid name for an environment variable
        // since most Unix/Linux shells don't allow dots in a name.  So, we try to allow for Linux
        // environment variables by checking WEBDRIVER_CHROME_DRIVER too, or instead.
        //
        String driverUrl = Arguments.driverUrl;

        // No? then properties file?  (Probably should name it CHROME_DRIVER rather than chromedriver, just to match
        if (driverUrl == null && properties != null) {
            driverUrl = properties.getProperty("chromedriver");
            //driverUrl = properties.getProperty("CHROME_DRIVER");
        }

        // No?  Then env variable?
        if (driverUrl == null) {
//            driverUrl = System.getenv(WIN_CHROME_DRIVER_ENV_VAR);
//            if (driverUrl == null) {
//                driverUrl = System.getenv(NON_WIN_CHROME_DRIVER_ENV_VAR); // logic right?  If on Linux then WIN_CHROME_DRIVER_ENV_VAR won't be set?
//            }
            //driverUrl = System.getenv("WEBDRIVER_CHROME_DRIVER");
            //driverUrl = System.getenv("CHROME_DRIVER"); // This only works if IntelliJ was started by a shell that knows this variable.
            driverUrl = System.getenv(chromeDriverEnvVarName); // This only works if IntelliJ was started by a shell that knows this variable.
            //driverUrl = System.getProperty("CHROME_DRIVER");
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
        // Now set the system property for Selenium to use
        if (driverUrl != null) {
            chromeDriverFile = new File(driverUrl); // new
            if (chromeDriverFile.exists()) {
                System.setProperty(SELENIUM_CHROME_DRIVER_ENV_VAR, chromeDriverFile.getAbsolutePath());
            }
        }
        // If none of the above worked, then maybe on Windows Selenium will find the executable because use had set webdriver.chrome.driver variable in ENV vars
        // Otherwise, error.  But we want to return true or false, so check here for that wonderful environment variable, which only
        // works on Windows.
//        if (System.getProperty(SELENIUM_CHROME_DRIVER_ENV_VAR) == null) { // experimental
//            return false;
//        }
        if (System.getenv(SELENIUM_CHROME_DRIVER_ENV_VAR) == null) {
            return false;
        }
        return true;
    }

    /**
     * This method uses GSON to parse a JSON file containing patient information -- registration
     * and treatments, loading the whole file into a Java objects called PatientsJson.  It dumps
     * it right into that object directly.  No manual parsing.  So a PatientsJson object represents
     * the JSON file.  Then as a Java object its parts can be retrieved easily.  For example, to
     * get the patient's first name it would be something like patientsJson.registration.newPatientReg.demographics.firstName
     * and treatments are in an array.  Just handle it like arrays in regular Java object.  So
     * this method returns one Java object representing one JSON file, you'd think.
     * But what about directories containing several JSON files?????????????????????
     * and it returns a PatientsJson object which is the result of parsing the JSON files.........
     * And what about "-random 5" on the command line?  Do 5 randoms and then the other specifieds?
     *
     * The logic in this method is overly complex.  Refactor
     * @return
     */
    static List<Patient> loadEncounters() { // shouldn't be called loadEncounters.  Instead, loadEncounterFiles or loadEncounters
        PatientsJson patientsJson = null;
        List<Patient> patients = new ArrayList<Patient>(); // I think I just added this.  Not sure how it affects the template output.  Check
        if (Arguments.patientsJsonUrl != null) {
            for (String patientJsonUrl : Arguments.patientsJsonUrl) {

                boolean fileExists = PatientJsonReader.patientJsonFileExists(patientJsonUrl);
                if (!fileExists) {
                    if (!Arguments.quiet) System.err.println("Input patient encounter file " + patientJsonUrl + " cannot be found.  Check path.  Skipping it.");
                    continue;
                }
                // How do you tell GSON that some sections are required, like the patientSearch area?
                // I think it gets complicated.  So it's best just to check the results
                boolean isValidJson = PatientJsonReader.isValidPatientJson(patientJsonUrl);
                if (!isValidJson) {
                    if (!Arguments.quiet) System.err.println("Bad input patient encounter file " + patientJsonUrl + "  Check content.  Skipping it.");
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
                        patient.encounterFileUrl = patientJsonUrl; // new 11/19/18
                        // Create PatientSearch objects if missing, based on contents of Demographics.
                        //
                        // We could reject any patient object that didn't contain a PatientSearch object.  That would be easiest.
                        // If we want to help the user, we could create one from NewPatientReg, or UpdatePatientReg, or PatientInfo
                        // The logic would be "If PatientSearch missing, create one from NewPatientSearch, and if that was missing,
                        // create it from UpdatePatient, and if that was missing create it from PatientInfo, and if that was missing,
                        // reject the patient.
                        if (patient.patientSearch == null) { // what if already created, but firstName etc are null?
                            patient.patientSearch = new PatientSearch(); // probably do this earlier, maybe when Registration is added.
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

                                // preregistration arrival has a need for search because need to find the patient in the list that is presented.
                                // However, we wouldn't be using the PatientSearch class to help out.  If nothing is specified in the JSON input
                                // file for this page, then we'd maybe want to get it from PreRegistration section, and if not there, then other
                                // places.  But for now we should assume the user will fill in the fields for PreRegistrationArrivals, and these
                                // are the fields we're interested in: SSN (last 4), Last name,
                                // First name, gender, flight date, flight number, & rank.
                                //
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
//                                // below is kinda strange.  Fix later.
//                                else if (patient.registration.patientInformation != null) {
//                                    if (Arguments.debug) {
//                                        System.out.println("PeP.loadEncounters(), Skipping patient, missing patient information object.");
//                                    }
//                                }
//                                else if (patient.registration.preRegistration != null) {
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
                patient.registration = new Registration(); // new, seems wrong.  Just to random=5  code from NPE's

                // just now 10/15/18 adding the following few lines.  Experimental.
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



                //patient.registration.process(patient); // totally new, totally untested, experimental mostly to make things more uniform, but also for PatientSearch support

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
            logger.severe("Couldn't write file.  Exception: " + Utilities.getMessageFirstLine(e));
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
            logger.severe("Couldn't write file.  Exception: " + Utilities.getMessageFirstLine(e));
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
    // contained optional sections: PatientSearch, Registration, and Treatments.  PatientSearch
    // is a section used in the Registration pages.  There are 5 of those.  The JSON file
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
            if (patient.encounterFileUrl != null) {
                if (!Arguments.quiet) System.out.println("Processing Patient from encounter file " + patient.encounterFileUrl + " ...");
            }
            else {
                if (!Arguments.quiet) System.out.println("Processing Patient ...");
            }

            // A patient is represented by the top section in the input json/encounter file
            // and you can say "random":false, or "random":true, or "random":null, or nothing.
            // If you have nothing, then patient.random is null.  And we have to fix that.
            if (patient.random == null) { // totally new
                patient.random = false; // totally new 9/22/18
            }
            if (patient.shoot == null) { // totally new
                patient.shoot = false; // totally new 12/10/18
            }

            success = patient.process();

            if (success) {
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
            //if (Arguments.writeEachPatientSummary && patient.registration != null) {
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



