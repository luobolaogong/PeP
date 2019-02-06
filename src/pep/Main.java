package pep;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.*;
//
// THIS IS PROTOTYPE CODE.
//
public class Main {
    static final String version = "Prototype 2/4/2019";
    public static boolean catchBys = false; // Temporary, for finding and eliminating XPaths.

    // Two loggers are automatically created when you create a LogManager.
    // One is the root logger, named "".  The other is the global logger, named "global"
    // which is kinda like System.out.  The root logger may be used to propagate settings
    // of parent to child, and is the parent of the global logger.
    private static final LogManager logManager = LogManager.getLogManager();
    static {
        try {
            String loggingPropertiesUrl = "Resources/logging.properties"; // or get from Arguments.loggingPropertiesUrl
            FileInputStream loggingPropertiesFileInputStream = new FileInputStream(loggingPropertiesUrl);
            if (loggingPropertiesFileInputStream != null) {
                logManager.readConfiguration(loggingPropertiesFileInputStream);
            } else {
                logManager.readConfiguration(); // looks for conf/logging.properties in the Java installation directory
            }
        } catch (IOException e) {
            if (Arguments.debug) System.out.println("Error in loading log configuration " + Utilities.getMessageFirstLine(e));
        }
        Logger seleniumRemoteLogger = Logger.getLogger("org.openqa.selenium.remote");
        seleniumRemoteLogger.setLevel(Level.OFF);
        Logger orgLogger = Logger.getLogger("org");
        orgLogger.setLevel(Level.OFF);
    }
    public static final Logger pepLogger = Logger.getLogger("pep"); // child loggers inhereit from this one
    public static final Logger timerLogger = Logger.getLogger("timer");

    private static By patientRegistrationNavMenuBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");


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
//        boolean processSucceeded = Pep.process(allPatients);
        boolean processSucceeded = pep.process(allPatients);
        int nPatients = allPatients.size();
        if (!processSucceeded && Arguments.verbose) System.err.println("***Failed to completely process patient" + (nPatients > 1 ? "s" : "") + ".");

        // Done processing patients so logout and shut down the browser and driver.
        TmdsPortal.logoutFromTmds();

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
        System.exit(0);
    }
}
