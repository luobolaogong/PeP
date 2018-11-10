package pep;

import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;

// TODO:
// Appears (though not sure) that an input file's element's value is "" (blank) or (probably also) null, that whether PeP provides a random value is based on a couple of things:
// If the section it is in has all its elements with "", then PeP skips the whole section.
// If the section has any value in it then it will turn every element in it to random.
// If the section is marked "random" then all required values get randoms.
public class Main {
    // some logging help at
    // https://examples.javacodegeeks.com/core-java/util/logging/java-util-logging-example/
    // also
    // www.ntu.edu.sg/home/ehchua/programming/java/javaLogging.html
    //
    // use fine for anything that is debugging at the top level of execution flow
    // user finer for stuff in loops and other places where you don't always need to see that much detail
    // Use paramaterized versions when can, as in rootLogger.log(Level.FINER, "processing[{0}]; {1}", new Opbect[]{i,list.get(i)});
    // The level is inherited from parent.  Hierarchy is based on the dot.  So pep.Pep is not the parent of pep.Main or pep.patient.Patient.  I don't get it.  Can do just "pep"?


    private static final LogManager logManager = LogManager.getLogManager();
    static {
        try {
            // You automatically get two loggers, somehow, perhaps when you create a LogManager.
            // Both are associated with console.  I don't know about file output.  Those are formatters, right?
            //
            // One is the root logger, which has the name "".  The second is a logger with the name
            // "global".  "The root logger is the parent of the global logger. The root logger is used
            // to propagate levels to child loggers and is used hold the handlers that can capture all
            // published log records. The global logger is just a named logger that has been reserved
            // for causal use. It is the System.out of the logging framework."
            //
            // I want to turn off logging from Selenium and everything other than PeP.  That would mean
            // I only want the loggers "pep", and everything descended from it.  I also want to set these
            // up using alogging.properties file.  Supposedly all loggers that start with "pep." will inherit
            // from "pep".
            //

            // Loading a config file appears not to create any loggers.
            logManager.readConfiguration(new FileInputStream("Resources/logging.properties"));
        }
        catch (IOException e) {
            //rootLogger.log(Level.SEVERE, "Error in loading log configuration", e);
            if (Arguments.debug) System.out.println("Error in loading log configuration" + e.getMessage());
        }
    }

    private static final Logger pepPackageLogger = Logger.getLogger("pep"); // logger for this package, but should inherit from rootLogger
    static final String version = "Prototype 11/03/2018";

    public static void main(String[] args) {
        System.out.println("pepPackageLogger name: " + pepPackageLogger.getName() + " level: " + pepPackageLogger.getLevel());
        pepPackageLogger.fine("This is a fine message for pepPackageLogger");

        // Make sure logging from Selenium and perhaps other Java stuff is turned off
        Logger seleniumRemoteLogger = Logger.getLogger("org.openqa.selenium.remote");
        seleniumRemoteLogger.setLevel(Level.OFF);
        Logger orgLogger = Logger.getLogger("org");
        orgLogger.setLevel(Level.OFF);


        try {
            SimpleFormatter  simpleFormatter = new SimpleFormatter();
            FileHandler fileHandler = new FileHandler("./someLogFile.log", true);
            fileHandler.setFormatter(simpleFormatter); // instead of XML
//            fileHandler.setLevel(Level.INFO);
//
//            rootLogger.addHandler(fileHandler);
//            rootLogger.setFilter(new LoggingTimingFilter()); // experiment
//
//            // how do you create your own level, as in rootLogger.timing("starting to create a patient");
//
//            //rootLogger.setUseParentHandlers(true);
//            //rootLogger.removeHandler(fileHandler);
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("severe error here, couldn't create handler? Ex: " + e.getMessage());
        }
        Pep pep = new Pep();

        // Load up the Arguments object using command line options and properties file and
        // environment variables.
        pep.loadAndProcessArguments(args);

        List<Patient> allPatients = Pep.loadPatients();

        if (allPatients.size() == 0) {
            System.out.println("No patient information processed.");
            System.out.println("Specify -usage option for help with command options.");
            System.exit(1);
        }
        Instant start = Instant.now();
        if (!Arguments.quiet) System.out.println("PeP " + version + ".  Started: " + (new Date()).toString()); // use java.time.Clock?

        // Initiate the browser, either headless or headed, either locally or remotely (grid)
        Driver.start(); // this is my Driver class
        //get login page first, then login
        boolean successful = TmdsPortal.getLoginPage(Arguments.tier);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS because could not get to the login page");
            Driver.driver.quit();
//            fileHandler.flush();
//            fileHandler.close();
            System.exit(1);
        }

        successful = TmdsPortal.doLoginPage(Arguments.user, Arguments.password);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS.");
            //return false;
            Driver.driver.quit();
//            fileHandler.flush();
//            fileHandler.close();
            System.exit(1);
        }


        // Should now be sitting on the main "page" that has the tabs and links.  The first tab is
        // "Patient Registration", and it is the default "page".  The links on the page are for that
        // default page.  The first link may be "Pre-patientRegistration", or it may be "New Patient Reg."
        // depending on what "role" you're associated with.  At this point we have not clicked on
        // any of the links or tabs.

        boolean processSucceeded = Pep.process(allPatients);
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to completely process all specified patients.");

        boolean successfulLogout = TmdsPortal.logoutFromTmds(); // this shuts down the browser too

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        //rootLogger.fine("Elapsed time in seconds: " + timeElapsed);
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
        //Driver.driver.quit(); // done in logout
//        fileHandler.flush();
//        fileHandler.close();
        System.exit(0);
    }
}
