package pep;

import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.LoggingFormatter;

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
    // some logging help at
    // https://examples.javacodegeeks.com/core-java/util/logging/java-util-logging-example/
    // also
    // www.ntu.edu.sg/home/ehchua/programming/java/javaLogging.html
    //
    // use fine for anything that is debugging at the top level of execution flow
    // user finer for stuff in loops and other places where you don't always need to see that much detail
    // Use paramaterized versions when can, as in rootLogger.log(Level.FINER, "processing[{0}]; {1}", new Opbect[]{i,list.get(i)});
    // The level is inherited from parent.  Hierarchy is based on the dot.  So pep.Pep is not the parent of pep.Main or pep.patient.Patient.  I don't get it.  Can do just "pep"?
    //private final static Logger rootLogger = Logger.getLogger(Main.class.getName());
    private static final Logger rootLogger = Logger.getLogger(""); // root rootLogger
    static final String version = "Prototype 11/03/2018";

    public static void main(String[] args) {
        Handler consoleHandler = null;
        Handler fileHandler = null;
        Formatter simpleFormatter = new SimpleFormatter();
        Formatter pepLoggingFormatter = new LoggingFormatter();
        try {

            //LogRecord logRecord = new LogRecord(Level.FINE, "%4$s: %5$s [%1$tc]%n");
            //simpleFormatter.format(logRecord);
            //rootLogger.addHandler(consoleHandler);
            //rootLogger.addHandler(fileHandler);
            //Handler[] handlers = rootLogger.getHandlers();

            consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(pepLoggingFormatter); // simpleFormatter instead of XML
            consoleHandler.setLevel(Level.ALL);
            rootLogger.addHandler(consoleHandler);

            //rootLogger.setLevel(Level.ALL); // what's diff between setting the level for the handler this rootLogger uses?
            fileHandler = new FileHandler("./someLogFile.log", true);
            fileHandler.setFormatter(simpleFormatter); // instead of XML
            fileHandler.setLevel(Level.INFO);
            rootLogger.addHandler(fileHandler);

            rootLogger.setLevel(Level.ALL);
            //rootLogger.setUseParentHandlers(true);
            //rootLogger.removeHandler(fileHandler);
        }
        catch (Exception e) {
            rootLogger.severe("severe error here, couldn't create handler? Ex: " + e.getMessage());
        }
        rootLogger.finest("finest: Logger name: " + rootLogger.getName());
        rootLogger.finer("finer: Logger name: " + rootLogger.getName());
        rootLogger.fine("fine: Logger name: " + rootLogger.getName());
        rootLogger.info("info: Logger name: " + rootLogger.getName());
        rootLogger.warning("warning: This is a warning");
        rootLogger.severe("severe: This is a severe message");
        rootLogger.config("config: this is a config message, and for some reason it doesn't come out unless rootLogger is somehow configured for this.");
        //rootLogger.setLevel(Level.WARNING); // children don't get this, I guess.  And what children?

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
            fileHandler.flush();
            fileHandler.close();
            System.exit(1);
        }

        successful = TmdsPortal.doLoginPage(Arguments.user, Arguments.password);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS.");
            //return false;
            Driver.driver.quit();
            fileHandler.flush();
            fileHandler.close();
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
        fileHandler.flush();
        fileHandler.close();
        System.exit(0);
    }
}
