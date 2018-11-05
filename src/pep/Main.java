package pep;

import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

// TODO:
// Appears (though not sure) that an input file's element's value is "" (blank) or (probably also) null, that whether PeP provides a random value is based on a couple of things:
// If the section it is in has all its elements with "", then PeP skips the whole section.
// If the section has any value in it then it will turn every element in it to random.
// If the section is marked "random" then all required values get randoms.
public class Main {
    // some logging help at https://examples.javacodegeeks.com/core-java/util/logging/java-util-logging-example/
    // also www.ntu.edu.sg/home/ehchua/programming/java/javaLogging.html   check this out.  Also for formatting output?
    // use fine for anything that is debugging at the top level of execution flow
    // user finer for stuff in loops and other places where you don't always need to see that much detail
    // Use paramaterized versions when can, as in logger.log(Level.FINER, "processing[{0}]; {1}", new Opbect[]{i,list.get(i)});
    // The level is inherited from parent.  Hierarchy is based on the dot.  So pep.Pep is not the parent of pep.Main or pep.patient.Patient.  I don't get it.  Can do just "pep"?
  private final static Logger logger = Logger.getLogger(Main.class.getName());
    static final String version = "Prototype 11/03/2018";

    public static void main(String[] args) {
        Handler consoleHandler = null;
        Handler fileHandler = null;
        Formatter simpleFormatter = null;
        try {
            consoleHandler = new ConsoleHandler();
            fileHandler = new FileHandler("./someLogFile.log", true);
            simpleFormatter = new SimpleFormatter();
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
            consoleHandler.setFormatter(simpleFormatter);
            fileHandler.setFormatter(simpleFormatter); // maybe this makes it no longer XML, by default.
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            logger.config("Configuration done.");
            logger.removeHandler(fileHandler);
        }
        catch (Exception e) {
            logger.severe("severe error here, couldn't create handler?");
        }
        logger.fine("Logger name: " + logger.getName());
        logger.warning("This is a warning");
        logger.config("this is a config message, and for some reason it doesn't come out unless logger is somehow configured for this.");
        logger.severe("This is a severe message");
        logger.setLevel(Level.WARNING); // children don't get this, I guess.  And what children?
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
            System.exit(1);
        }

        successful = TmdsPortal.doLoginPage(Arguments.user, Arguments.password);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS.");
            //return false;
            Driver.driver.quit();
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
        //logger.fine("Elapsed time in seconds: " + timeElapsed);
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
        //Driver.driver.quit(); // done in logout
        System.exit(0);
    }
}
