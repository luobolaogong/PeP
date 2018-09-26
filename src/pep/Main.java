package pep;

import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class Main {
    static final String version = "Prototype 9/25/2018";

    public static void main(String[] args) {
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
        //if (Arguments.debug) System.out.println("Elapsed time in seconds: " + timeElapsed);
        if (!Arguments.quiet) System.out.println("Ended: " + (new Date()).toString() + " (" + timeElapsed + "s)");
        //Driver.driver.quit(); // done in logout
        System.exit(0);
    }
}
