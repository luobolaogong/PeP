package pep.utilities;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import pep.Pep;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pep.Main.pepLogger;

// what is ChromeDriverService?


// Probably not the best name.  Should be PePSeleniumWebDriver or something
public class Driver {
    private static Logger logger = Logger.getLogger(Driver.class.getName());
    public static WebDriver driver;

    // Driver is ChromeDriver, but Selenium controls how ChromeDriver is used.  For example, to get ChromeDriver
    // to load a web page you have to use the Selenium method WebDriver.get().
    //
    // The URL for the driver can be on command line, property file, environment variable, exists in local directory
    // with a standard name, or possibly get it out of the jar file.  That's the order of importance.
    // Command line trumps.  Whatever URL is found, we try to use it.  But if the highest priority URL doesn't work,
    // then we try the one in the jar.  But if that cannot be done, we quit.
    // At this point there may be a value set if on command line or in property file and the command line trumps.
    // If no URL at this point then check the current directory, and then environment variable.  I mean,
    // there may or may not be an environment variable, and there may or may not be a file in the current dir with the default name.
    // Which name trumps, env var, or cur dir?  Env var trumps.  So check env var first and if value specified, then use it.
    // Otherwise do cur dir.
    // If after that there's still no URL, then try to get it out of the jar file.
    // Take a look at http://www.seleniumeasy.com/selenium-tutorials/accessing-shadow-dom-elements-with-webdriver

    // At the point this is called, have we checked the properties file?  Yes.

    // Having a constructor do this is kind of silly.  All this does is set the system file for Selenium WebDriver.
    public Driver() {
    }

    // Seems this could be a static method
    // Whether we have a local WebDriver or a RemoteWebDriver, we need to start it.  I don't like the name, but does it really start something?
    public static void start() {
        ChromeOptions chromeDriverOptions = new ChromeOptions();
        if (Arguments.headless) {
            // Chrome has a bug in headless mode when running on Win7.  Runs very slowly.
            // These two arguments help solve this, as indicated in https://github.com/Codeception/CodeceptJS/issues/561
            chromeDriverOptions.addArguments("--proxy-server='direct://'");
            chromeDriverOptions.addArguments("--proxy-bypass-list=*");
            chromeDriverOptions.addArguments("--proxy-bypass-list=*");
            chromeDriverOptions.addArguments("--disable-gpu"); // experiment

            if (Arguments.width != null && Arguments.height != null) {
                chromeDriverOptions.addArguments("--window-size=" + Arguments.width + "x" + Arguments.height); // a compromise default size.  Can be overridden on command line or properties file?
            }
            else {
                chromeDriverOptions.addArguments("--window-size=1500x2000"); // a compromise default size.  Can be overridden on command line or properties file?
                // can be changed later with? driver.manage().window().setSize(new Dimension(1024,768));
            }
            //chromeDriverOptions.addArguments("--start-maximized"); // seems not to work here
            //chromeDriverOptions.addArguments("--start-fullscreen"); // seems not to work here
            chromeDriverOptions.setHeadless(true);

            // another idea, from https://developers.google.com/web/updates/2017/04/headless-chrome
            // chromeCapabilities.set('chromeOptions', {args: ['--headless']});
        }
        else {
            chromeDriverOptions.addArguments("disable-infobars"); // want this?  doesn't work unless you have the "stand-alone" chrome web driver.  Can't just have client
            //chromeDriverOptions.addArguments("--window-size=1500x2600");
            //chromeDriverOptions.addArguments("--start-maximized"); // for sure want this?
            if (Arguments.width != null && Arguments.height != null) {
                // Different syntax for non-headless
                chromeDriverOptions.addArguments("--window-size=" + Arguments.width + "," + Arguments.height); // how about supporting properties file too?
                // ?can later changes size with: driver.manage().window().setSize(new Dimension(1024,768));
            }
            else {
                chromeDriverOptions.addArguments("--start-fullscreen"); // for sure want this?
            }
            //chromeDriverOptions.setBinary("Users/Rob/WebDriver/ChromeDriver/2.40/chromedriver.exe"); // doesn't seem to work
        }

        // options.setBinary("pathToChromeDriverExecutableOrFile");  // would this be a better idea than using System env?

//        Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF); // helps keep things less verbose
        System.setProperty("webdriver.chrome.silentOutput", "true"); // does get rid of some output at start

        //Logger.getLogger(Pep.class.getName()).setLevel(Level.ALL); // test
        //Logger.getLogger("pep").setLevel(Level.ALL); // test

        // Connect to WebDriver (chromedriver)
        // What is that RemoteManagement stuff?  Need it?
        if (Arguments.gridHubUrl != null) {
            // probably should check if the hub is actually up first
            String hub = Arguments.gridHubUrl; // for now, expect full url
            try {
                logger.fine("Driver.start(), creating new RemoteWebDriver with hub " + hub);
                driver = new RemoteWebDriver(new URL(hub), chromeDriverOptions); // takes a while.  Causes Chrome browser to start up with blank page
                logger.fine("Driver.start(), setting page load timeout to " + Pep.PAGE_LOAD_TIMEOUT_SECONDS);
                driver.manage().timeouts().pageLoadTimeout(Pep.PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all page loads, not just login
                logger.fine("Driver.start(), setting script timeout to " + Pep.SCRIPT_TIMEOUT_SECONDS);
                driver.manage().timeouts().setScriptTimeout(Pep.SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // new.  Not sure it helps or hurts anything yet.  Some scripts maybe take a while.  Check for script success somewhere?
                logger.fine("Driver.start(), created new RemoteWebDriver with hub " + hub);
            } catch (MalformedURLException e) {
                pepLogger.severe("Couldn't contact hub at " + hub + " Exiting...");
                System.exit(1);
            } catch (UnreachableBrowserException e) {
                pepLogger.severe("Couldn't get to browser.  " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (SessionNotCreatedException e) {
                pepLogger.severe("Session wasn't created.  " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (Exception e) {
                pepLogger.severe("Something happened and couldn't connect.\n" + Utilities.getMessageFirstLine(e) + "\nExiting...");
                System.exit(1);
            }
        }
        else if (Arguments.webServerUrl != null) {
            //options.addArguments("role=standalone"); // wrong of course
            try {
                logger.fine("Driver.start(), creating new RemoteWebDriver with server " + Arguments.webServerUrl);
                driver = new RemoteWebDriver(new URL(Arguments.webServerUrl), chromeDriverOptions); // hangs
                logger.fine("Driver.start(), created new RemoteWebDriver with server " + Arguments.webServerUrl);
            } catch (MalformedURLException e) {
                if (!Arguments.quiet) System.err.println("Couldn't connect to server at " + Arguments.webServerUrl + " Exception: " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (UnreachableBrowserException e) {
                logger.severe("Couldn't get to browser.  " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (Exception e) {
                logger.severe("Something happened and couldn't connect.  " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            }
        }
        else {
            try {
                // Start up the browser headed or headless, locally, with blank page.  Takes a few seconds.
                driver = new ChromeDriver(chromeDriverOptions); // starts up ChromeDriver.  Doesn't connect with anything.
                driver.manage().timeouts().pageLoadTimeout(Pep.PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all page loads, not just login page
                // It's possible the following slow things down unnecessarily.  Not sure.  Experiment again.
                driver.manage().timeouts().implicitlyWait(Pep.ELEMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all implicit wait elements
                driver.manage().timeouts().setScriptTimeout(Pep.SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects those asynchronous script waits new 12/13/18
                // no need to try to clear cache.  Everything's cleared when selenium starts up chromedriver, I think.
            }
            catch (IllegalStateException e) {
                if (!Arguments.quiet) System.err.println("Selenium did not find webdriver (chromedriver) executable.  Not specified on command line, in properties file, environment variable or found in current directory.");
                if (!Arguments.quiet) System.out.println("Use -usage option for help with command opt.");
                logger.severe("Selenium did not find chrome driver executable.  Was not found from environment variable webdriver.chrome.driver: " + System.getProperty("webdriver.chrome.driver"));
                System.exit(1);
            }
            catch (Exception e) {
                logger.severe("Exception in Driver.start: " + Utilities.getMessageFirstLine(e));
                System.exit(1);
            }
        }
        // How about doing this just to remember it can be used later to get out of jams
//        Driver.driver.switchTo().defaultContent(); // "Selects either the first frame on the page, or the main document when a page contains iframes."
//        Driver.driver.switchTo().parentFrame(); // "Change focus to the parent context."
//        Driver.driver.switchTo().frame(String); // "Select a frame by its name or ID."
        return;
    }
}
