package pep.utilities;

// Consider replacing JCommander with JOptSimple.  https://pholser.github.io/jopt-simple
// Both require the existence of a library.
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

/**
 * This class handles the command line arguments/options, but it also is used throughout the program
 * to reference those as directives.  For example, "--debug" is related to the global static
 * value Arguments.debug which is used throughout the program.
 *
 * Because most users don't have a Unix background, most of the options are changed from double dash to
 * single.
 *
 * Regarding "tier" and code technology (Seam/Spring) and webserver address...  These have all been combined so that
 * if you specified a tier name, like "gold", or a url like "http://tmds-gold.akimeka", or variation,
 * then execution of code would branch at various places to account for differences between seam and spring.
 * Eventually this code technology difference will go away, but for now it's staying in.
 *
 */
public class Arguments {
    private static Logger logger = Logger.getLogger(Arguments.class.getName());

    @Parameter(names = {"-user", "-u"}, required = false, order = 1,
            description = "User login. e.g. \"-user autopepr0004\"")
    public static String user; // can be in properties file

    @Parameter(names = {"-password", "-pass", "-p"}, required = false, order = 2,
            description = "User password. e.g. \"-password ChangeMe\"")
    public static String password;  // can be in properties file, or can be prompted for.


    @Parameter(names = {"-properties", "-props", "-prop"}, required = false, arity = 1, order = 3,
            description = "Location of a properties file, e.g. \"-properties C:/data/pep.properties\"")
    public static String propertiesUrl; // = "patientgenerator.properties"; // reasonable name?


    // Consider changing this such that if a directory is specified rather than a file, you process all files in the dir
    @Parameter(names = {"-encounter", "-enc", "-encounters", "-encs"}, required = false, variableArity = true, order = 4,
            description = "Locations of one or more patient encounter input files. e.g. \"-encounter C:/data/patients.json\"")
    public static List<String> patientsJsonUrl;

    // If a patient directory is specified, then all .json files in it are loaded
    @Parameter(names = {"-encounterDirectory", "-encDir"}, required = false, arity = 1, order = 5,
            description = "Directory containing patient encounter input files, e.g. \"-encDir C:/data/patients\"")
    public static String patientsJsonDir; // change to patientsInDir

    @Parameter(names = {"-random"}, required = false, arity = 1, order = 6,
            description = "Create n random patient encounters, e.g. \"-random 20\"")
    public static int random = 0; // add to properties file?


    @Parameter(names = {"-template"}, required = false, arity = 0, order = 7,
            description = "Print a JSON template, then exit.  e.g. \"-template\"")
    public static boolean template = false;


    @Parameter(names = {"-version"}, arity = 0, hidden = false, order = 8,
            description = "Print version of this tool then exit.")
    public static boolean version = false;

    @Parameter(names = {"-help", "--help", "-h"}, required = false, help = true, order = 9,
            description = "Show this message then exit.")
    public static boolean help = false;

    @Parameter(names = {"-usage", "--usage"}, required = false, order = 10,
            description = "Show showUsage options then exit.")
    public static boolean usage = false;


    @Parameter(names = {"-headless"}, required = false, arity = 0, hidden = false, order = 11,
            description = "Headless mode")
    public static boolean headless = false; // add to properties file?



    @Parameter(names = {"-server", "-webserver", "-ws"}, required = false, arity = 1, order = 12,
            description = "Webserver to connect with to access TMDS.  Related to tier.  One or other required.  \"-server demo-tmds.akimeka.com\", or \"-ws localhost\"")
    public static String webServerUrl; // can be in properties file, and in the encounter input files (does that work?)

    // TMDS code branched when it went from Seam framework to Spring framework.  One consequence was that the
    // xpaths/locators were changed for the Spring branch.  Some tiers (TRAIN) still run the Seam branch
    // while others run Spring (GOLD, ...).  We still need to handle both.  The user doesn't care or want to know
    // whether the tier they are using is Seam or Spring.  So usually this argument will not be set by the
    // user.  But it could be, if they know if a special tier or webserverURL has some special code that needs
    // to be branched on, in PeP.  In the future, maybe some other framework will be used.  Therefore
    // it will not be a boolean "codeBranch", but rather a string like "Seam", or "Spring", "Angular" or whatever.
    // So this argument will just give an indication of what special branches should be taken in PeP code.
    // It's something that is used to make a code branching decision.  There could be more than one at a time.
    // So, call it "branch".
    @Parameter(names = {"-branch"}, required = false, arity = 1, order = 13,
            description = "The name of a branch to take in PeP code when TMDS has different code depending on the version.  'Seam', 'Spring' (default), ... e.g. \"-branch Seam\"")
    public static String codeBranch; // can be in properties file?

    @Parameter(names = {"-hub", "-grid"}, required = false, arity = 1, hidden = false, order = 14,
            description = "Run in a remote Selenium grid environment with specified hub address (for parallel processing), e.g. \"-hub 10.5.4.168\"")
    public static String gridHubUrl; // add to properties file?

    @Parameter(names = {"-seleniumServer"}, required = false, arity = 1, hidden = false, order = 15,
            description = "Run using a remote Selenium server with specified address")
    public static String seleniumServerUrl; // add to properties file?

    @Parameter(names = {"-driver", "-d"}, required = false, arity = 1, order = 16,
            description = "Location of the Chrome Driver executable, e.g. \"-driver C:/drivers/chromedriver.exe\" ")
    public static String driverUrl; // can be in properties file, right?  If not specified on command line, check env vars, current dir, etc.


    @Parameter(names = {"-tier", "-t"}, required = false, arity = 1, order = 17,
            description = "The tier which is related to the webServerUrl.  Related to webserver.  One or other required. e.g. \"-tier gold\"")
    public static String tier; // can be in properties file



    // This should be tested
    @Parameter(names = {"-date"}, required = false, order = 18,
            description = "The date to be used for certain encounters and treatments.  Format: mm/dd/yyyy, e.g. \"-date 02/04/2018\"")
    public static String date; // can be in properties file, right?  = Date.from(Instant.now()).toString(); // format this



    @Parameter(names = {"-writeEachPatientSummary", "-weps"}, required = false, arity = 0, order = 19,
            description = "Write individual summary JSON file for each processed patient.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeEachPatientSummary = false; // add to properties file?  change to patientsOutDir

    @Parameter(names = {"-writeAllPatientsSummary", "-waps"}, required = false, arity = 0, order = 20,
            description = "Write as single summary JSON file all processed patients.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeAllPatientsSummary = false; // add to properties file?  change to patientsOutDir

    // If a patient output directory is specified, then all created .json files are written there
    @Parameter(names = {"-outDir", "-outPatDir", "-opd"}, required = false, arity = 1, order = 21,
            description = "Directory for created patients' summaries, as in \"-outpatdir C:/data/out/patients\"")
    public static String patientsJsonOutDir; // change to patientsOutDir



    @Parameter(names = {"-shootDir", "-sdir"}, required = false, arity = 1, order = 22,
            description = "Directory for created screen shots, as in \"-shootDir C:/data/out/screenshots\"")
    public static String shootDir;

    @Parameter(names = {"-width"}, required = false, arity = 1, order = 23,
            description = "Width of TMDS, as in \"-width 1200\"  To be used with -height, and usually with -headless for screen shots")
    public static Integer width;

    @Parameter(names = {"-height"}, required = false, arity = 1, order = 24,
            description = "Height of TMDS, as in \"-height 2000\"  To be used with -width, and usually with -headless for screen shots")
    public static Integer height;





    // The following are hidden.  Some may be described in the user manual, so will stay single dash

    @Parameter(names = {"-verbose"}, required = false, arity = 0, hidden = true, order = 25,
            description = "Run in verbose mode, showing more console output.")
    public static boolean verbose = false;

    @Parameter(names = {"-quiet"}, arity = 0, hidden = true, order = 26,
            description = "Run in quiet mode, limiting most console output.")
    public static boolean quiet = false;




    @Parameter(names = {"-throttle"}, required = false, arity = 1, hidden = true,
            description = "Change the length of embedded sleep time by some factor.  2 means sleep twice as long. e.g. \"--throttle 2\"")
    public static double throttle = 1.0;

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

    @Parameter(names = {"-pauseSave"}, required = false, hidden = true, arity = 1,
            description = "Cause a pause of X seconds before clicking on a button that would cause a save operation. e.g. \"-pauseSave 10\"")
    public static int pauseSave = 0;



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

    @Parameter(names = "--logLevel", required = false, arity = 1, hidden = true,
            description = "Set logging level.  Values are ALL, SEVERE, WARNING, INFO, FINE, FINER, FINEST, CONFIG, OFF.  Default is OFF")
    //public static String logLevel = "OFF"; // don't want to set default here, I think, because want to test against null later
    public static String logLevel;

    @Parameter(names = {"--logTimerLevel", "--timerLogLevel"}, required = false, arity = 1, hidden = true,
            description = "Set timer logging level.  Values are ALL, SEVERE, WARNING, INFO, FINE, FINER, FINEST, CONFIG, OFF, which isn't very logical.  Default is OFF")
    //public static String logTimerLevel = "OFF";
    public static String logTimerLevel;

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
     * @param argsFromCommandLine The command line arguments
     * @return Arguments parsed and processed from command line
     */
    public static Arguments processCommandLineArgs(String[] argsFromCommandLine) {
        Arguments arguments = new Arguments(); // uses everything above
        jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        jCommander.setProgramName(INVOCATION_NO_ARGS);
        jCommander.setCaseSensitiveOptions(false);
        jCommander.setAllowAbbreviatedOptions(false);
        try {
            jCommander.parse(argsFromCommandLine);
        }
        catch (ParameterException e) {
            if (!quiet) System.out.println("Arguments on command line: " + Arrays.toString(argsFromCommandLine));
            if (!quiet) System.out.println("Parsing command line arguments yielded this error: \"" + e.getMessage() + "\"");
            if (debug) System.err.println("Command line argument error: " + e.getMessage());
            return null;
        }
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

