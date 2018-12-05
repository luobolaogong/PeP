package pep;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.*;

// Sometimes with IntelliJ something goes wrong with the run configuration, and the Main class cannot be found.
// I think the solution is to do File > Project Structure > Modules > Project Settings > Sources > Add Content Root.
// Maybe need to delete the previous one and redo it?
//
// Fix up the artifact jar contents so not including a bunch of unwanted stuff.  There's a section on the File > Projec structure > Modules > add content root,
// and then delete the stuff on the right you don't want in the jar.
//
// Note to remember:  With Selenium xpaths and finding elements with text, do something like:
//     By.xpath("//*[@id=\"patientRegForm\"]/descendant::td[text()='Departure']"); // a td element with text "Departure"
// The key thing to remember is you can do this: element[text()='someString']

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

    // I guess the reason we're doing this here is because when main() starts, we want any created loggers to get their
    // properties from the logger.properties file.  Seems like this manager could just go before the creation of the first
    // logger.  Later try to move it into main().
    private static final LogManager logManager = LogManager.getLogManager();
    static {
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
        // In my IntelliJ development environment I have the logging properties file under Resources
        // and I wanted to put it into the executable jar, but I think that's not working.  Why?
        //
        // The user can specify the logging properties file with a definition on the command line, as in
        // java -Djava.util.logging.config.file=MyLogging.properties  That's a good enough option.
        // Don't need to augment Arguments to specify a logging.properties file.
        // But I suppose we could allow the user to create a logging.properties file and stick it in
        // the current directory / Resources subdir.???
        //
        // The following code needs to be run BEFORE any loggers are created.
        // So fix this up later.

        // Loading a config file appears not to create any loggers.
        try {
            String loggingPropertiesUrl = "Resources/logging.properties"; // or get from Arguments.loggingPropertiesUrl
            FileInputStream loggingPropertiesFileInputStream = new FileInputStream(loggingPropertiesUrl);
            if (loggingPropertiesFileInputStream != null) {
                logManager.readConfiguration(loggingPropertiesFileInputStream);
            } else {
                logManager.readConfiguration(); // looks for conf/logging.properties in the Java installation directory
            }
        } catch (IOException e) {
            //rootLogger.log(Level.SEVERE, "Error in loading log configuration", e);
            if (Arguments.debug) System.out.println("Error in loading log configuration " + e.getMessage());
        }
        Logger seleniumRemoteLogger = Logger.getLogger("org.openqa.selenium.remote");
        seleniumRemoteLogger.setLevel(Level.OFF);
        Logger orgLogger = Logger.getLogger("org");
        orgLogger.setLevel(Level.OFF);
    }
    // This should be done in a PepLogger class.  Actually, I'm not sure pepLogger is nec, except in Arguments where sets the parent logger for all descendants of pep package
    public static final Logger pepLogger = Logger.getLogger("pep"); // logger for this package, but should inherit from rootLogger, and most all other loggers inhereit from this one
    // Why don't we do a global timerLogger here?
    //public static final Logger timerLogger = Logger.getLogger("pep.utilities.LoggingTimer");
    public static final Logger timerLogger = Logger.getLogger("timer");
    static final String version = "Prototype 11/30/2018";
    static private By dashboardBy = By.id("dashboardnav");
    static private By portletContainerBy = By.id("portlet-container");
    static private By patientRegistrationNavMenuBy = By.id("i4000");

    public static void main(String[] args) {
        // Make sure logging from Selenium and perhaps other Java stuff is turned off
//        Logger seleniumRemoteLogger = Logger.getLogger("org.openqa.selenium.remote");
//        seleniumRemoteLogger.setLevel(Level.OFF);
//        Logger orgLogger = Logger.getLogger("org");
//        orgLogger.setLevel(Level.OFF);



//        Properties javaProps = System.getProperties();
//        Enumeration propNamesEnum = javaProps.propertyNames();
//        while (propNamesEnum.hasMoreElements()) {
//            String propName = (String) propNamesEnum.nextElement();
//            String propValue = javaProps.getProperty(propName);
//            System.out.println("Java Prop name: " + propName + " value: " + propValue);
//        }
//        Map<String,String> envVars = System.getenv();
//        Set<String> keys = envVars.keySet();
//        for (String key : keys) {
//            System.out.println("Env Key: " + key + " Env Value: " + envVars.get(key));
//        }

        Pep pep = new Pep();

        // Load up the Arguments object using command line options and properties file and
        // environment variables.
        boolean loadedAndProcessedArguments = pep.loadAndProcessArguments(args);
        if (!loadedAndProcessedArguments) {
            pepLogger.severe("Main.main(), could not load and process arguments.");
            // do what?
            System.out.println("Couldn't start PeP.  Check webserver address and ChromeDriver location.");
            System.out.println("Specify -usage option for help with command options.");
            System.exit(1);
        }

        List<Patient> allPatients = Pep.loadEncounters(); // maybe Patient should be changed to Encounter

        if (allPatients.size() == 0) {
            System.out.println("No patient information processed.");
            System.out.println("Specify -usage option for help with command options.");
            System.exit(1);
        }
        Instant start = Instant.now();
        if (!Arguments.quiet) System.out.println("PeP " + version + ".  Started: " + (new Date()).toString() + ", accessing web server " + Arguments.webServerUrl); // use java.time.Clock?

        // Initiate the browser, either headless or headed, either locally or remotely (grid)
        Driver.start(); // this is my Driver class
        //get login page first, then login
        //boolean successful = TmdsPortal.getLoginPage(Arguments.tier);
        boolean successful = TmdsPortal.getLoginPage(Arguments.webServerUrl);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS because could not get to the login page");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
            // pepLogger.getHandlers().flush; // not sure where to do something like this
//            fileHandler.flush();
//            fileHandler.close();
            System.exit(1);
        }

        successful = TmdsPortal.doLoginPage(Arguments.user, Arguments.password);
        if (!successful) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS.");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
//            fileHandler.flush();
//            fileHandler.close();
            System.exit(1);
        }


        // Should now be sitting on the main "page" that has the tabs and links.  The first tab is
        // "Patient Registration", and it is the default "page".  The links on the page are for that
        // default page.  The first link may be "Pre-registration", or it may be "New Patient Reg."
        // depending on what "role" you're associated with.  At this point we have not clicked on
        // any of the links or tabs.
        // It's also possible that we could be sitting on a page that says "Change Password", but we can ignore.
        // And it's possible we could be seeing the "Concurrent Login Attempt Detected" page, which we
        // can't go past.


//        if (notRightPage) {
//            System.out.println("Not on right page.");
//        }
        try {
           //Driver.driver.switchTo().defaultContent(); // Wow, this is really important to get stuff on the outermost window or whatever

            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(patientRegistrationNavMenuBy));
            //(new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.presenceOfElementLocated(patientRegistrationNavMenuBy));
            //(new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(patientRegistrationNavMenuBy)));
            //(new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(whatever));
        }
        catch (Exception e) {
            if (!Arguments.quiet) System.out.println("Could not log in to TMDS.  There could be various reasons for this.  Connection refused?  Possible concurrent login?");
            TmdsPortal.logoutFromTmds(); // test that prob doesn't work
            Driver.driver.quit();
            System.exit(1);
        }

        boolean processSucceeded = Pep.process(allPatients);
        if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to completely process all specified patients.");

        boolean successfulLogout = TmdsPortal.logoutFromTmds(); // this shuts down the browser too

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        //rootLogger.fine("Elapsed time in seconds: " + timeElapsed);
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
       // Driver.driver.quit(); // done in logout, above, right?
//        fileHandler.flush(); // can we do these in logoutFromTmds?
//        fileHandler.close();
        System.exit(0);
    }
}
