package pep.utilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.*;

import static pep.Main.pepLogger;
import static pep.Main.timerLogger;
import static pep.utilities.Utilities.getMessageFirstLine;

//import static pep.utilities.LoggingTimer.timerLogger;

/**
 * Consider replacing JCommander with JOptSimple.  https://pholser.github.io/jopt-simple/
 * Both require the existence of a library.
 *
 * This class handles the command line arguments/options, but it also is used throughout the program
 * to reference those as directives.  For example, "--debug" is related to the global static
 * value Arguments.debug which is used throughout the program.
 *
 *
 * Because most users don't have a Unix background, I'm changing the options from double dash to
 * single.
 *
 * Also, this program also will now support running against a server, which may be remote, and also it
 * will support running in a hub/node grid environment, where the hub may be remote.
 *
 * Currently this program has been running with an internal server, which starts up when this app
 * starts, and shuts down when this app ends, and there's no connection information required.
 * This server is part of the jar "selenium-server-standalone-3.13.0.jar", which also contains the
 * WebDriver and other Selenium API code, which uses the executable (server) chromedriver for
 * running on Chrome.  This is the only implementation of WebDriver used by this app at this time.
 *
 * How do you run this against a server, and why would you, and if you do, where does the browser run?
 *
 * For the hub/node/grid, the user needs to specify the address of the hub.
 *
 *
 * Regarding "tier" and code technology (Seam/Spring) and webserver address...  These have all been combined so that
 * if you specified a tier name, like "gold", or a url like "http://tmds-gold.akimeka", or variation,
 * then execution of code would branch at various places to account for differences between seam and spring.
 *
 * That's not good enough now because we should handle other webservers, and we should be able to
 * independently specify the code technology (seam or spring), for example when I have to support
 * "localhost" as my webserver, I want to change whether my webserver is running seam or spring.
 *
 * Eventually this code technology difference will go away, but for now it's staying in.
 *
 * The most important piece of information is "webServerUrl" in order to bring up the app.  That must be supported
 * as a full URL ("http://tmds-gold.akimeka.com") and maybe (prob not) abbreviations of the full URL
 * like "tmds-gold".  "Tier" is just a convenience/shorthand term for a webServerUrl.  So, if Tier is
 * specified it will expand to commonly accepted full URL. And codeTech could be assumed from webServerUrl
 * or Tier, but could also be specified as an override.
 *
 * CodeTech could be a boolean "codeBranch" (otherwise Spring is assumed).
 *
 * The simplest thing to do would be require webServerUrl and codeTech, and forget about tier.  But we'll allow tier.
 * All 3 should have values, either assumed or inferred or set.
 *
 *
 *
 */
public class Arguments {
    private static Logger logger = Logger.getLogger(Arguments.class.getName());


    // should change name from host to server or webserver.  Actually tier should be "gold", "demo", ..., server should be the url of the web server
    // we want to be able to say "-tier demo", which would expand to a URL demo-tmds.akimeka.com, but also allow -server demo-tmds.akimeka.com or a variation
    // There's also a relationship with the code, whether we have Seam or Spring.  PeP should set that to an enum or something, with values SEAM or SPRING
    // Right now there's a bunch of if (codeBranch) and if (isSpring).  That wouldn't preclude both.  So, maybe an enum rather than two booleans.
//    @Parameter(names = {"-tier", "-host", "-t", "-webserver"}, required = false, order = 0,
//            description = "Tier/Host to use, e.g. \"-tier demo\", or \"-host demo-tmds.akimeka.com\", or \"-t https://web01-tmds-test.tmdsmsat.akiproj.com/\"")
//    public static String tier; // can be in properties file, and in the encounter input files (does that work?)

    // following 3 are experimental
    @Parameter(names = {"-server", "-webserver", "-ws"}, required = false, arity = 1, order = 0,
            description = "Webserver to connect with to access TMDS.  Related to tier.  One or other required.  \"-server demo-tmds.akimeka.com\", or \"-ws localhost\"")
    public static String webServerUrl; // can be in properties file, and in the encounter input files (does that work?)

    @Parameter(names = {"-tier", "-t"}, required = false, arity = 1, order = 1,
            description = "The tier which is related to the webServerUrl.  Related to webserver.  One or other required. e.g. \"-tier gold\"")
    public static String tier; // can be in properties file


    @Parameter(names = {"-user", "-u"}, required = false, order = 2,
            description = "User login. e.g. \"-user autopepr0004\"")
    public static String user; // can be in properties file

    @Parameter(names = {"-password", "-pass", "-p"}, required = false, order = 3,
            description = "User password. e.g. \"-password ChangeMe\"")
    public static String password;  // can be in properties file, or can be prompted for.

    @Parameter(names = {"-properties", "-props", "-prop"}, required = false, arity = 1, order = 4,
            description = "Location of a properties file, e.g. \"-properties C:/data/pep.properties\"")
    public static String propertiesUrl; // = "patientgenerator.properties"; // reasonable name?




    // Consider changing this such that if a directory is specified rather than a file, you process all files in the dir
    @Parameter(names = {"-encounter", "-enc", "-encounters", "-encs"}, required = false, variableArity = true, order = 5,
            description = "Locations of one or more patient encounter input files. e.g. \"-encounter C:/data/patients.json\"")
    public static List<String> patientsJsonUrl;

    // If a patient directory is specified, then all .json files in it are loaded
    @Parameter(names = {"-encounterDirectory", "-encDir"}, required = false, arity = 1, order = 6,
            description = "Directory containing patient encounter input files, e.g. \"-encDir C:/data/patients\"")
    public static String patientsJsonDir; // change to patientsInDir

    @Parameter(names = {"-random"}, required = false, arity = 1, order = 7,
            description = "Create n random patient encounters, e.g. \"-random 20\"")
    public static int random = 0; // add to properties file?


    @Parameter(names = {"-driver", "-d"}, required = false, arity = 1, order = 8,
            description = "Location of the Chrome Driver executable, e.g. \"-driver C:/drivers/chromedriver.exe\" ")
    public static String driverUrl; // can be in properties file, right?  If not specified on command line, check env vars, current dir, etc.

    @Parameter(names = {"-template"}, required = false, arity = 0, order = 9,
            description = "Print a JSON template, then exit.  e.g. \"-template\"")
    public static boolean template = false;

    // What are our options?  external server, grid, headless.
    // Should be able to run headless no matter in a grid, or server.
    // Is server a reasonable option if we already have a built in server?  Maybe.  Client machine may be slow.  Don't want to use chromedriver.exe
    // If specify grid you need to specify
    // the hub address if it is remote.  Of course it could be local too.  Should allow for
    // default hub address to be localhost?

    @Parameter(names = {"-headless"}, required = false, arity = 0, hidden = false, order = 10,
            description = "Headless mode")
    public static boolean headless = false; // add to properties file?

    @Parameter(names = {"-hub", "-grid"}, required = false, arity = 1, hidden = false, order = 11,
            description = "Run in a remote Selenium grid environment with specified hub address (for parallel processing), e.g. \"-hub 10.5.4.168\"")
    public static String gridHubUrl; // add to properties file?

    @Parameter(names = {"-seleniumServer"}, required = false, arity = 1, hidden = false, order = 12,
            description = "Run using a remote Selenium server with specified address")
    public static String seleniumServerUrl; // add to properties file?











    // This needs to be tested
    @Parameter(names = {"-date"}, required = false, order = 13,
            description = "The date to be used for encounters and treatments.  Format: mm/dd/yyyy, e.g. \"-date 02/04/2018\"")
    public static String date; // can be in properties file, right?  = Date.from(Instant.now()).toString(); // format this


    // TMDS code branched when it went from Seam framework to Spring framework.  One consequence was that the
    // xpaths/locators were changed for the Spring branch.  Some tiers (DEMO, TRAIN) still run the Seam branch
    // while others run Spring (GOLD, ...).  We still need to handle both.  The user doesn't care or want to know
    // whether the tier they are using is Seam or Spring.  So usually this argument will not be set by the
    // user.  But it could be, if they know if a special tier or webserverURL has some special code that needs
    // to be branched on, in PeP.  In the future, maybe some other silly framework will be used.  Therefore
    // it will not be a boolean "codeBranch", but rather a string like "Seam", or "Spring", "Angular" or whatever.
    // So this argument will just give an indication of what special branches should be taken in PeP code.
    // It doesn't even have to be a "framework".  It could be an "api".  Could be "birthdaySurprise".  It's
    // something that is used to make a code branching decision.  There could be more than one at a time.
    // So, call it "branch".  But if a String then we have to do a compare rather than just a boolean if.  So
    // maybe in the future I'll create a boolean for each one, like isSeam, isSpring, isWhatever.
    @Parameter(names = {"-branch"}, required = false, arity = 1, order = 14,
            description = "The name of a branch to take in PeP code when TMDS has different code depending on the version.  'Seam', 'Spring' (default), ... e.g. \"-branch Seam\"")
    public static String codeBranch; // can be in properties file?



//
//    @Parameter(names = {"-peps", "-threads"}, required = false, arity = 1, hidden = false, order = 99,
//            description = "Run at most this many instances of PeP from this instance.  Supposedly to help out with -grid so that client need not fire off 100 PeP processes.")
//    public static int maxPeps; // add to properties file?




    // Maybe do this kind of thing later, but probably want to allow -template to just provide a URL
//    @Parameter(names = {"-templateUrl"}, required = false, arity = 1, order = 11,
//            description = "Send results of -template to the file specified by this templateUrl value.  e.g. \"-templateUrl myTemplate.json\"")
//    public static String templateUrl;

// Possibly later:
//    // This flag means "even if a patient is already in the system somehow, and has registration info, update it with anything found
//    @Parameter(names = {"-update", "-up", "-updatepatient", "-updatepatients"}, required = false, arity = 0, order = 12,
//            description = "Update patient registration information specified")
//    public static boolean updatepatients = false; // should probably add to properties file



    @Parameter(names = {"-writeEachPatientSummary", "-weps"}, required = false, arity = 0, order = 15,
            description = "Write individual summary JSON file for each processed patient.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeEachPatientSummary = false; // add to properties file?  change to patientsOutDir

    @Parameter(names = {"-writeAllPatientsSummary", "-waps"}, required = false, arity = 0, order = 16,
            description = "Write as single summary JSON file all processed patients.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeAllPatientsSummary = false; // add to properties file?  change to patientsOutDir

    // If a patient output directory is specified, then all created .json files are written there
    @Parameter(names = {"-outDir", "-outPatDir", "-opd"}, required = false, arity = 1, order = 17,
            description = "Directory for created patients' summaries, as in \"-outpatdir C:/data/out/patients\"")
    public static String patientsJsonOutDir; // change to patientsOutDir



    @Parameter(names = {"-shootDir", "-sdir"}, required = false, arity = 1, order = 18,
            description = "Directory for created screen shots, as in \"-shootDir C:/data/out/screenshots\"")
    public static String shootDir;

    @Parameter(names = {"-width"}, required = false, arity = 1, order = 19,
            description = "Width of TMDS, as in \"-width 1200\"  To be used with -height, and usually with -headless for screen shots")
    public static Integer width;

    @Parameter(names = {"-height"}, required = false, arity = 1, order = 19,
            description = "Height of TMDS, as in \"-height 2000\"  To be used with -width, and usually with -headless for screen shots")
    public static Integer height;

// probably add option to go full screen, or max, or min, or perhaps specific size?







    @Parameter(names = {"-version"}, arity = 0, hidden = false, order = 20,
            description = "Print version of this tool then exit.")
    public static boolean version = false;

    @Parameter(names = {"-help", "--help", "-h"}, required = false, help = true, order = 21,
            description = "Show this message then exit.")
    public static boolean help = false;

    @Parameter(names = {"-usage", "--usage"}, required = false, order = 22,
            description = "Show showUsage options then exit.")
    public static boolean usage = false;




    // The following are hidden.  Some may be described in the user manual, so will stay single dash



    @Parameter(names = {"-verbose"}, required = false, arity = 0, hidden = true, order = 23,
            description = "Run in verbose mode, showing more console output.")
    public static boolean verbose = false;

    @Parameter(names = {"-quiet"}, arity = 0, hidden = true, order = 24,
            description = "Run in quiet mode, limiting most console output.")
    public static boolean quiet = false;

    @Parameter(names = {"-throttle"}, required = false, arity = 1, hidden = true,
            description = "Change the length of embedded sleep time by some factor.  2 means sleep twice as long. e.g. \"--throttle 2\"")
    public static double throttle = 1.0;

    // We may want to introduce some throttling options, to simulate real users, or to accomodate slow servers.
    // We could put in pauses in different places, like between pages, or between sections of a page,
    // or after elements like dropdowns, or text input, or checkboxes, or radios.  And for text input
    // we could even simulate character speed for text fields.  And we could have an overall "throttle"
    // handle all of these with various coefficients.
    //
    // Prior to now, throttle was just scaling the amount of sleeps.  But sleeps are there because I
    // couldn't figure out a better way to handle timing issues using Selenium's Wait capabilities.
    // These are becoming less, I think, as I learn more about Selenium.
    // -pausePatient x, -pausePage, -pauseSection, -pauseElement


    @Parameter(names = {"-pauseAll"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds for patients, pages, sections, elements. e.g. \"-pauseAll 5\"")
    public static int pauseAll = 0;

    @Parameter(names = {"-pausePatient"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finishing a patient. e.g. \"-pausePatient 15\"")
    public static int pausePatient = 0;

    @Parameter(names = {"-pausePage"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finishing a page submit. e.g. \"-pausePage 10\"")
    public static int pausePage = 0;

    @Parameter(names = {"-pauseSection"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a section. e.g. \"-pauseSection 5\"")
    public static int pauseSection = 0;

    @Parameter(names = {"-pauseElement"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a text and dropdown, and radio and checkbox and date elements. e.g. \"-pauseElement 1\"")
    public static int pauseElement = 0;

    @Parameter(names = {"-pauseText"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a text element. e.g. \"-pauseText 4\"")
    public static int pauseText = 0;

    @Parameter(names = {"-pauseDropdown"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a dropdown element. e.g. \"-pauseDropdown 3\"")
    public static int pauseDropdown = 0;

    @Parameter(names = {"-pauseRadio"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a radio element. e.g. \"-pauseRadio 2\"")
    public static int pauseRadio = 0;

    @Parameter(names = {"-pauseCheckbox"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a checkbox element. e.g. \"-pauseCheckbox 1\"")
    public static int pauseCheckbox = 0;

    @Parameter(names = {"-pauseDate"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds after finish processing a date element. e.g. \"-pauseDate 3\"")
    public static int pauseDate = 0;



    @Parameter(names = {"-printEachPatientSummary", "-peps"}, hidden = true, required = false, arity = 0,
            description = "Print to the console each patient's summary as a JSON string.")
    public static boolean printEachPatientSummary = false; // change to patientsOutDir

    @Parameter(names = {"-printAllPatientsSummary", "-paps"}, hidden = true, required = false, arity = 0,
            description = "Print to the console all patient's summaries as defined in the JSON input file, as a JSON string.")
    public static boolean printAllPatientsSummary = false; // change to patientsOutDir




    // Following are "double dash --", so should be hidden and not talked about in manuals

    @Parameter(names = "--debug", required = false, arity = 0, hidden = true,
            description = "Debug mode")
    public static boolean debug = false;

    // Add arguments for timing, like "-logTimes" or "-timeDB" which would cause log statements to issue for any record saves,
    // so that we can get a handle on DB save times to see if they are blocking.  Or something that measures server response
    // times, like .... ?  Or how about "-timePatient", or "-timeTreatments", or "-timeRegistration" ?  I doubt it.

    // Should probably try to make this one an enum
    @Parameter(names = "--logLevel", required = false, arity = 1, hidden = true,
            description = "Set logging level.  Values are ALL, SEVERE, WARNING, INFO, FINE, FINER, FINEST, CONFIG, OFF.  Default is OFF")
    //public static String logLevel = "OFF"; // don't want to set default here, I think, because want to test against null later
    public static String logLevel;

    @Parameter(names = "--logTimerLevel", required = false, arity = 1, hidden = true,
            description = "Set timer logging level.  Values are ALL, SEVERE, WARNING, INFO, FINE, FINER, FINEST, CONFIG, OFF, which isn't very logical.  Default is OFF")
    public static String logTimerLevel = "OFF";

    @Parameter(names = "--logUrl", required = false, arity = 1, hidden = true,
            description = "log file URL.  e.g.  --logUrl C:/temp/pep.log")
    public static String logUrl; // don't want to set default here, I think, because want to test against null later? = "pep.log";

    @Parameter(names = "--logTimerUrl", required = false, arity = 1, hidden = true,
            description = "log timer file URL.  e.g.  --logTimerUrl C:/temp/peptiming.log")
    public static String logTimerUrl; // don't want to set default here, I think, because want to test against null later? = "pep.log";





    private static final String INVOCATION_NO_ARGS = "java -jar pep.jar"; // Automated Tmds UI Patient Encounter Generator
    private static JCommander jCommander;

    /**
     * Parse command line options.  That's it.
     *
     * @param argsFromCommandLine
     * @return
     */
    public static Arguments processCommandLineArgs(String[] argsFromCommandLine) {
        Arguments arguments = new Arguments(); // now jCommandArgs knows what it can expect
        jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        jCommander.setProgramName(INVOCATION_NO_ARGS);
        jCommander.setCaseSensitiveOptions(false);
        jCommander.setAllowAbbreviatedOptions(false); // was true
        try {
            jCommander.parse(argsFromCommandLine); // This can throw an exception though the Java docs don't show it
        }
        catch (ParameterException e) {
            //System.out.println(jCommander.getUnknownOptions().toString());
            // Seems to be shortcoming of JCommander.  How do you identify the error in the command line options?
            // This exception has an associated message which gives a clue, but the string isn't appropriate to
            // show the user.  It looks like
            // "Was passed main parameter 'fraggle' but no main parameter was defined in your arg class"
            // But it could also be:
            // "Expected a value after parameter -patient"
            // which is a reasonable string to present the user.
            // Whatever else could it be?
            // Well, I could just parse out the 'fraggle' and identify it as the problem.
            // But still not sure what else it could say.
            // So, I'm considering changing to JOptSimple.
            //

            if (!quiet) System.out.println("Arguments on command line: " + Arrays.toString(argsFromCommandLine));
            if (debug) System.err.println("Command line argument error: " + e.getMessage());
            return null; // ???
        }

        // Why don't I just make pepLogger global?
        //Logger pepLogger = logger.getParent().getParent(); // "pep"

        // "--debug" by itself means set logLevel for pepLogger to ALL.
        // "--logLevel XXX" by itself means set logLevel for pepLogger to XXX
        // "timerLogger XXX" means set logLevel for timerLogger to XXX
        // "--logUrl XXX" means send both pepLogger and timerlogger to XXX
        // The order of processing the arguments is as above, so if there was "--debug --logLevel OFF"
        // then logging is off.
        // The logging.properties file is hopefully read before these arguments are processed,
        // meaning that arguments override properties where they overlap.  However, the properties can set
        // levels for individual classes and packages
        //
        // Seems that pepLogger and logger are the same thing.
        try {
            if (Arguments.debug) {
                pepLogger.setLevel(Level.FINE); // this thing seems to also set the level for logger, even though set for pepLogger
            }
            else if (Arguments.verbose) { // new 12/18/18
                pepLogger.setLevel(Level.INFO);
            }
            else {
                pepLogger.setLevel(Level.SEVERE);
            }

            if (Arguments.logLevel != null) {
                pepLogger.setLevel(Level.parse(Arguments.logLevel)); // this appears to set the level for logger (too), so affects any subsequent logger messages
            }
            if (Arguments.logTimerLevel != null) {
                timerLogger.setLevel(Level.parse(Arguments.logTimerLevel));
            }


            if (Arguments.logUrl != null) { // this is where to send logging output.  So remove any handler and add a file handler
                try {
                    StringBuffer logUrlAppendThisBuffer = new StringBuffer();
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
                    // Should we append a patient name to this file?  No because could be doing more than one patient.  Prob by date/time
                    //FileHandler fileHandler = new FileHandler(Arguments.logTimerUrl, true);
                    StringBuffer logTimerUrlAppendThisBuffer = new StringBuffer();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String dateTime = simpleDateFormat.format(new Date());
                    logTimerUrlAppendThisBuffer.append(dateTime);
                    logTimerUrlAppendThisBuffer.append(".log");
                    FileHandler fileHandler = new FileHandler(Arguments.logTimerUrl + logTimerUrlAppendThisBuffer.toString(), false);
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

        // will do tier, webServerUrl, codeBranch in PeP class, I think.

        return arguments; // this is just strange.  This class is Arguments
    }

    /**
     * Provides some help in the form of identification, purpose, sources of further help, and some handy usage options.
     */
    public static void showHelp() {
        System.out.println();
        System.out.println("You are running the TMDS Patient Encounter Processor (PeP).  Its purpose is to enter patient");
        System.out.println("data into a database using the TMDS web application's patient web pages.");
        System.out.println();
        System.out.println("Common usage: java -jar pep.jar -encounter <file.json>");
        System.out.println();
        System.out.println("Use the -usage option to see a full list of command line arguments.");
        System.out.println();
        System.out.println("Please see the User Guide for more information.");
    }

    public static void showUsage() {
        jCommander.usage();
    }

}

