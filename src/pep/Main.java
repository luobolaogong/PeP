package pep;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This Main class may as well represent PeP with regard to comments.
 * This version of PeP is a prototype version.  It's purpose initially was to see how well
 * Selenium could be used to generate and enter Demographics information for a newly
 * registered patient, and the initial test case was a set of Double Alphabet patients.
 * This was put together quickly, but immediately thereafter the role expanded to do more
 * and more, and PeP was never really designed.  But it should be.
 *
 * One of the big decisions early on was to allow for page element values to be specified
 * in an input file (in JSON format), and the generation of random data for fields
 * that the user didn't care to specify in that input file.  There are so many fields that
 * the user may not be interested in specifying, and requiring it would just burden the user.
 * The same was the case when an input file is not specified, but one or more patients
 * needed to be created.
 *
 * These decisions have had a big impact on how the code has evolved over time, and there
 * is a lot of code that handles this, and when PeP is redesigned, these concepts need to
 * be better formalized and implemented.
 *
 * This class contains main(), but in addition to instantiating Pep, first logging is set up,
 * and then input arguments/params are evaluated, and encounter files are read/loaded.
 * If those go okay then TMDS is logged into and then the patient encounters are processed,
 * and finally TMDS is logged out of.  This is not the best organization, but this is
 * prototoype code.
 */
public class Main {
    static final String version = "Prototype 3/22/2019 09:55";
    public static boolean catchBys = false; // Temporary, for finding and eliminating XPaths.

    // Logging definitely needs to be fixed.  Perhaps replaced with a logging system that
    // makes more sense.  For now, using Java Util Logging.
    //
    // Two "userContext" loggers are automatically created when you create a LogManager.
    // One is the root logger, named "".  The other is the global logger, named "global".
    // The root logger may be used to propagate settings of parent to child, and is the
    // parent of the global logger.  The LogManager has a level of INFO initially
    // and a ConsoleHandler.level of INFO
    private static final LogManager logManager = LogManager.getLogManager();
    // This static block is meant to run before other log things get set up, so as
    // to set up the logManager loggers according to the Resources/logging.properties file.
    // Hopefully that file becomes part of the pep.jar file somehow.
    static {
        // THESE ARE TESTS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // When can't get properties out of the jar file in production mode, then we get info messages showing up.
        // and they're not formatted correctly, so see if can fix those here.
        // Hopefully these can be overwritten if a log properties file is read in.
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n"); // seems to work
//        System.setProperty("java.util.logging.SimpleFormatter.level", "SEVERE"); // complete guess
//        System.setProperty("java.util.logging.ConsoleHandler.level", "ALL"); // not sure this works
//        System.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
//        System.setProperty("java.util.logging.FileHandler.level", "ALL");
//        System.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
//
//        System.setProperty(".level", "SEVERE");
//        System.setProperty("timer.level", "SEVERE");
//        System.setProperty("timer.handlers", "java.util.logging.ConsoleHandler");
//        System.setProperty("timer.useParentHandlers", "false");
//        System.setProperty("pep.level", "SEVERE");
//        System.setProperty("pep.handlers", "java.util.logging.ConsoleHandler");
//        System.setProperty("pep.useParentHandlers", "false");



//        System.out.println("In Main.static block to set up logging.");
//        System.out.println("Where am I?");
//        String curDir = System.getProperty("user.dir");
//        System.out.println("curDir is " + curDir);
//        Path currentRelativePath = Paths.get("");
//        String s = currentRelativePath.toAbsolutePath().toString();
//        System.out.println("Current relative path is: " + s);
//
//        String anotherPath = Paths.get(".").toAbsolutePath().normalize().toString();
//        System.out.println("Another path: " + anotherPath);
//
//        try {
//            Files.list(new File(anotherPath).toPath())
//                    .limit(10)
//                    .forEach(path -> {
//                        System.out.println(path);
//                    });
//        } catch (Exception e) {
//            System.out.println("oops");
//        }
//
//        URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println("\n\nBased on the location of this class, what is this: " + location.getFile());
//
//        System.out.println("How about finding logging.properties here...? " + location.getPath());
//        System.out.println("How about finding logging.properties here...? " + location.getFile());
////        URL loggingPropertiesUrl = Main.class.getResource("logging.properties");
//        //URL loggingPropertiesUrl = Main.class.getResource("pep.jar");
//        //System.out.println("What's the path ?: " + loggingPropertiesUrl.getPath());
//        File theJarFileIThink = new File(location.getFile());
//
//
//
//        String theLoggingPropertiesFilePath = location.getPath() + "../logging.properties";
//        System.out.println("theLoggingPropertiesFilePath: " + theLoggingPropertiesFilePath);
//
//
//
//
//        try {
////            Files.list(new File(location.getFile()).toPath())
//            Files.list(theJarFileIThink.toPath())
//                    .limit(10)
//                    .forEach(path -> {
//                        System.out.println("Maybe jar file content: " + path);
//                    });
//        } catch (Exception e) {
//            System.out.println("oops");
//        }
//
//        String cwd = new File("").getAbsolutePath();
//        System.out.println("And one more: " + cwd);

//        String loggingPropertiesUrl = "Resources/logging.properties"; // or get from Arguments.loggingPropertiesUrl

//        InputStream in = this.getClass().getResourceAsStream("/logging.properties");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));




        //URL loggingPropertiesJarFileUrl = Main.class.getResource("/logging.properties");
        //String loggingPropertiesJarFilePath = loggingPropertiesJarFileUrl.getFile();
        //System.out.println("Okay, is this the file name I want when run as a jar?: " + loggingPropertiesJarFilePath);
//        URL loggingPropertiesJarFileUrl = Main.class.getResource("logging.properties");
//        String loggingPropertiesJarFilePath = loggingPropertiesJarFileUrl.getFile();
//        System.out.println("Okay, is this the file name I want when run as a jar?: " + loggingPropertiesJarFilePath);




        // Try to find logging.properties in the Resources folder, then if not there try the current folder,
        // and if not there, check conf/logging.properties, I think.
        String theLoggingPropertiesFilePath = "Resources/logging.properties"; // or get from Arguments.loggingPropertiesUrl
        //String theLoggingPropertiesFilePath = "logging.properties"; // or get from Arguments.loggingPropertiesUrl
        FileInputStream loggingPropertiesFileInputStream = null;
        try {
            loggingPropertiesFileInputStream = new FileInputStream(theLoggingPropertiesFilePath);
            //loggingPropertiesFileInputStream = new FileInputStream(loggingPropertiesJarFilePath);
            //System.out.println("Was able to convert the file to an input stream.");
        }
        catch (Exception e) {
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!Couldn't get a file input stream for " + theLoggingPropertiesFilePath);
        }

        if (loggingPropertiesFileInputStream == null) {
            theLoggingPropertiesFilePath = "logging.properties"; // or get from Arguments.loggingPropertiesUrl
            //String theLoggingPropertiesFilePath = "logging.properties"; // or get from Arguments.loggingPropertiesUrl
            loggingPropertiesFileInputStream = null;
            try {
                loggingPropertiesFileInputStream = new FileInputStream(theLoggingPropertiesFilePath);
                //loggingPropertiesFileInputStream = new FileInputStream(loggingPropertiesJarFilePath);
                //System.out.println("Was able to convert the file to an input stream.");
            } catch (Exception e) {
                //System.out.println("!!!!!!!!!!!!!!!!!!!!!Still Couldn't get a file input stream for " + theLoggingPropertiesFilePath);
            }
        }

        // Try conf/logging.properties if didn't get Resources/logging.properties
        try {
            //System.out.println("loggingPropertiesFileInputStream is " + loggingPropertiesFileInputStream);
            if (loggingPropertiesFileInputStream == null) {
                //System.out.println("!!!!!!!!!!!Couldn't get the logging.properties file, so reading in conf/logging.properties if can.");
                logManager.readConfiguration(); // looks for conf/logging.properties in Java installation directory
                //System.out.println("!!!!!Have no idea if could read conf/logging.properties.");
            } else {
                //System.out.println("!!!!!!!Will now try to read the logging.properties file.");
                logManager.readConfiguration(loggingPropertiesFileInputStream);
            }
        } catch (IOException e) {
            //System.out.println("!!!!!!!!!!!!!!!!!!Some kinda problem loading loading properties.");
            if (Arguments.debug) System.out.println("Error in loading log configuration " + Utilities.getMessageFirstLine(e));
        }




        //System.out.println("What does logManager say?  " + logManager.getProperty("pep.utilities.Arguments.level"));
        // Turn off other automatic loggers so they don't confuse things
        Logger seleniumRemoteLogger = Logger.getLogger("org.openqa.selenium.remote");
        seleniumRemoteLogger.setLevel(Level.OFF);
        Logger orgLogger = Logger.getLogger("org");
        orgLogger.setLevel(Level.OFF);
    }



    public static final Logger pepLogger = Logger.getLogger("pep"); // child loggers inhereit from this one
    public static final Logger timerLogger = Logger.getLogger("timer");

    private static By patientRegistrationNavMenuBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");

    /**
     * Instantiate Pep, load/process arguments and properties, load encounters, start the
     * Selenium WebDriver, login, process patient encounters, and log out.
     * @param args Command line arguments, as described in the class Arguments
     */
    public static void main(String[] args) {
        Pep pep = new Pep();

        boolean loadedAndProcessedArguments = pep.loadAndProcessArguments(args);
        if (!loadedAndProcessedArguments) {
            pepLogger.severe("Main.main(), could not load and process arguments.");
            System.out.println("Cannot start PeP.");
            System.out.println("Specify -usage option for help with command options.");
            System.exit(1);
        }

        List<Patient> allPatients = pep.loadEncounters(); // maybe Patient should be changed to Encounter
        if (allPatients.size() == 0) {
            System.out.println("No patient information processed.");
            System.out.println("Specify -usage option for help with command options.");
            System.exit(1);
        }

        Instant start = Instant.now();
        if (!Arguments.quiet) System.out.println("PeP " + version + ".  Started: " + (new Date()).toString() + ", accessing web server " + Arguments.webServerUrl); // use java.time.Clock?

        // Initiate the browser and ChromeDriver.
        Driver.start();

        // Get login page then login
        boolean successful = TmdsPortal.getLoginPage(Arguments.webServerUrl);
        if (!successful) {
            if (!Arguments.quiet) System.err.println("***Could not log in to TMDS because could not get to the login page");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
            System.exit(1);
        }
        successful = TmdsPortal.doLoginPage(Arguments.user, Arguments.password);
        if (!successful) {
            if (!Arguments.quiet) System.err.println("***Could not log in to TMDS.");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
            System.exit(1);
        }

        // Check that we're past the login page, which could have encountered errors, like
        // "Concurrent Login Attempt Detected"
        try {
            Utilities.waitForVisibility(patientRegistrationNavMenuBy, 5, "Main.main(), waiting for nav menus to indicate login okay.");
        }
        catch (Exception e) {
            if (!Arguments.quiet) System.err.println("***Could not log in to TMDS.  There could be various reasons for this.  Concurrent login? Connection refused?");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
            System.exit(1);
        }

        // Start processing patients.
        boolean processSucceeded = pep.process(allPatients);
        int nPatients = allPatients.size();
        if (!processSucceeded && Arguments.verbose) System.err.println("***Failed to fully process patient" + (nPatients > 1 ? "s" : "") + ".");

        // Done processing patients so logout and shut down the browser and driver.
        TmdsPortal.logoutFromTmds();

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
        System.exit(0);
    }
}
