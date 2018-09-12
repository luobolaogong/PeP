package pep.utilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.util.Arrays;
import java.util.List;

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
 */
public class Arguments {
    @Parameter(names = {"-tier", "-host", "-t"}, required = false, order = 0,
            description = "Tier/Host to use, e.g. \"-tier demo\", or \"-host demo-tmds.akimeka.com\", or \"-t https://web01-tmds-test.tmdsmsat.akiproj.com/\"")
    public static String tier; // can be in properties file

    @Parameter(names = {"-user", "-u"}, required = false, order = 1,
            description = "User login. e.g. \"-user reed1234\"")
    public static String user; // can be in properties file

    @Parameter(names = {"-password", "-p"}, required = false, order = 2,
            description = "User password. e.g. \"-password ChangeMe!\"")
    public static String password;  // can be in properties file, or can be prompted for.

    @Parameter(names = {"-date"}, required = false, order = 3,
            description = "The date to be used for encounters and treatments.  Format: mm/dd/yyyy, e.g. \"-date 02/04/2018\"")
    public static String date; // can be in properties file, right?  = Date.from(Instant.now()).toString(); // format this

    @Parameter(names = {"-pat", "-patient", "-patients", "-enc", "-encounter", "-encounters"}, required = false, variableArity = true, order = 4,
            description = "Locations of one or more Patient source input files. e.g. \"-patients C:/data/patients.json\"")
    public static List<String> patientsJsonUrl;

    // If a patient directory is specified, then all .json files in it are loaded
    @Parameter(names = {"-patDir", "-patsDir", "-encDir"}, required = false, arity = 1, order = 5,
            description = "Directory containing patient source input files, e.g. \"-patsdir C:/data/in/patients\"")
    public static String patientsJsonDir; // change to patientsInDir

    @Parameter(names = {"-properties", "-props"}, required = false, order = 6,
            description = "Location of a properties file, e.g. \"-properties C:/data/patientgenerator.properties\"")
    public static String propertiesUrl; // = "patientgenerator.properties"; // reasonable name?


    // What are our options?  external server, grid, headless.
    // Should be able to run headless no matter in a grid, or server.
    // Is server a reasonable option if we already have a built in server?  Maybe.  Client machine may be slow.  Don't want to use chromedriver.exe
    // If specify grid you need to specify
    // the hub address if it is remote.  Of course it could be local too.  Should allow for
    // default hub address to be localhost?

    @Parameter(names = {"-headless"}, required = false, arity = 0, hidden = false, order = 7,
            description = "Headless mode")
    public static boolean headless = false; // add to properties file?

    @Parameter(names = {"-grid", "-hub"}, required = false, arity = 1, hidden = false, order = 8,
            description = "Run in a remote Selenium grid environment with specified hub address (for parallel processing), e.g. \"-hub 10.5.4.168\"")
    public static String gridHubUrl; // add to properties file?

    @Parameter(names = {"-server", "-ss", "-seleniumServer"}, required = false, arity = 1, hidden = false, order = 9,
            description = "Run using a remote Selenium server with specified address")
    public static String serverUrl; // add to properties file?


//
//    @Parameter(names = {"-peps", "-threads"}, required = false, arity = 1, hidden = false, order = 99,
//            description = "Run at most this many instances of PeP from this instance.  Supposedly to help out with -grid so that client need not fire off 100 PeP processes.")
//    public static int maxPeps; // add to properties file?



    @Parameter(names = {"-driver", "-d"}, required = false, order = 10,
            description = "Location of the Chrome Driver executable, e.g. \"-driver C:/drivers/chromedriver.exe\" ")
    public static String driverUrl; // can be in properties file, right?  If not specified on command line, check env vars, current dir, etc.

    @Parameter(names = {"-template"}, required = false, arity = 0, order = 11,
            description = "Print a JSON template, then exit.  e.g. \"-template\"")
    public static boolean template;


    // This flag means "even if a patient is already in the system somehow, and has patientRegistration info, update it with anything found
    @Parameter(names = {"-update", "-up", "-updatepatient", "-updatepatients"}, required = false, arity = 0, order = 12,
            description = "Update patient patientRegistration information specified")
    public static boolean updatepatients = false; // should probably add to properties file



    @Parameter(names = {"-writeEachPatientSummary", "-weps"}, required = false, arity = 0, order = 12,
            description = "Write individual summary JSON file for each processed patient.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeEachPatientSummary = false; // add to properties file?  change to patientsOutDir

    @Parameter(names = {"-writeAllPatientsSummary", "-waps"}, required = false, arity = 0, order = 13,
            description = "Write as single summary JSON file all processed patients.  Writes to <outpatdir> if specified, otherwise current directory.")
    public static boolean writeAllPatientsSummary = false; // add to properties file?  change to patientsOutDir

    // If a patient output directory is specified, then all created .json files are written there
    @Parameter(names = {"-outDir", "-outPatDir", "-opd"}, required = false, arity = 1, order = 14,
            description = "Directory for created patients' summaries, as in \"-outpatdir C:/data/out/patients\"")
    public static String patientsJsonOutDir; // change to patientsOutDir

    @Parameter(names = {"-random", "--random"}, required = false, order = 15,
            description = "Create n random patients, e.g. \"-random 20\"")
    public static int random = 0; // add to properties file?

    @Parameter(names = {"-version"}, arity = 0, hidden = false, order = 16,
            description = "Print version of this tool then exit.")
    public static boolean version = false;

    @Parameter(names = {"-verbose", "--verbose"}, required = false, arity = 0, hidden = false, order = 17,
            description = "Run in verbose mode, showing more console output.")
    public static boolean verbose = false;

    @Parameter(names = {"-quiet"}, arity = 0, hidden = true, order = 18,
            description = "Run in quiet mode, limiting most console output.")
    public static boolean quiet = false;

    @Parameter(names = {"-help", "--help", "-h"}, required = false, help = true, order = 19,
            description = "Show this message then exit.")
    public static boolean help = false;

    @Parameter(names = {"-usage", "--usage"}, required = false, order = 20,
            description = "Show showUsage options then exit.")
    public static boolean usage = false;



    // Hiddens next

    @Parameter(names = {"-printEachPatientSummary", "-peps"}, hidden = true, required = false, arity = 0,
            description = "Print to the console each patient's summary as a JSON string.")
    public static boolean printEachPatientSummary = false; // change to patientsOutDir

    @Parameter(names = {"-printAllPatientsSummary", "-paps"}, hidden = true, required = false, arity = 0,
            description = "Print to the console all patient's summaries as defined in the JSON input file, as a JSON string.")
    public static boolean printAllPatientsSummary = false; // change to patientsOutDir


    @Parameter(names = {"--log", "-log"}, required = false, hidden = true,
            description = "Produce a log file of output, at location specified, e.g. \"--log C:/logs/log20180531\"")
    public static String logUrl; // = "patientgenerator.log"; // reasonable name?

    @Parameter(names = {"--throttle"}, required = false, hidden = true,
            description = "Change the length of embedded sleep time by some factor.  2 means sleep twice as long. e.g. \"--throttle 2\"")
    public static double throttle = 1.0;




    @Parameter(names = "--debug", required = false, arity = 0, hidden = true,
            description = "Debug mode")
    public static boolean debug = false;


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
            String errorMessage = e.getMessage();
            // Hack to make certain error message more appropriate for users.
            // could use regular expression and Pattern, but will go simple:
            if (errorMessage.contains("but no main parameter")) {
                String[] errorMessageParts = errorMessage.split("\'");
                errorMessage = "Unknown command line argument \'" + errorMessageParts[1] + "\'";
//            if (errorMessage.contains(" but no main")) {
//                errorMessage.substring()
//            }
            }
            if (!quiet) System.err.println("Error: " + errorMessage);
            //e.usage();
            //showUsage();
            //System.exit(1);
            return null; // ???
        }
        return arguments;
    }

    /**
     * Provides some help in the form of identification, purpose, sources of further help, and some handy usage options.
     */
    public static void showHelp() {
        System.out.println();
        System.out.println("You are running the TMDS Patient Encounter Generator (PeP).  Its purpose is to enter patient");
        System.out.println("data into a tier's database using the TMDS web application's patient web pages.");
        System.out.println();
        System.out.println("Common usage: java -jar pep.jar -encounter <file.json>");
        System.out.println();
        System.out.println("Use the -usage option to see a full list of command line arguments.");
        System.out.println();
        System.out.println("Please see the User Guide and other documentation in the Documentation folder.");
    }

    public static void showUsage() {
        jCommander.usage();
    }

}

