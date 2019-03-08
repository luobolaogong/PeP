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
import java.util.logging.Logger;

import static pep.Main.pepLogger;

/**
 * Sets up Selenium to run ChromeDriver as a WebDriver that either runs the browser locally or remotely, and if remotely
 * then makes sure it can be accessed as either a server instance, or a Grid with a Hub and Nodes.  All this is done
 * only when start() is called, which sets up the driver options, and then instantiates ChromeDriver, or RemoteWebDriver.
 *
 * Driver is ChromeDriver, but Selenium controls how ChromeDriver is used.  For example, to get ChromeDriver
 * to load a web page you have to use the Selenium method WebDriver.get().
 *
 * The URL for the driver can be on command line, property file, environment variable, exist in the local directory
 * with a standard name, or possibly get it out of the jar file (not currently), in that order, basically.
 * Whatever URL is found, we try to use it.  If that cannot be done, we quit.
 *
 * If a remote server or grid system is used, it must be started up by hand.  Instructions on how to do that are in
 * the PeP User Manual.  If we wanted a program to start it up, we'd probably want to use  ChromeDriverService as in:
 *  service = new ChromeDriverService.Builder()
 *           .usingDriverExecutable(new File("path/to/my/chromedriver"))
 *           .usingAnyFreePort()
 *           .build();
 *  service.start();
 */
public class Driver {
    private static Logger logger = Logger.getLogger(Driver.class.getName());
    public static WebDriver driver;

    public Driver() {
        System.out.println("This constructor is never called.  PeP was always experimental and not designed.");
    }

    /**
     * Set up the Selenium WebDriver, which is ChromeDriver, either headless or not, as a server, or not, in a grid, or not.
     * Set driver options first though.
     */
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
        }

        // options.setBinary("pathToChromeDriverExecutableOrFile");  // would this be a better idea than using System env?

        // Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF); // helps keep things less verbose
        System.setProperty("webdriver.chrome.silentOutput", "true"); // does get rid of some output at start

        // Appears that gridHubUrl and seleniumServer do not act the same from the PeP client, although the server is started up the same.

        // If grid/hub specified, then start PeP up using RemoteWebDriver with the hub address to communicate with the hub (and nodes)
        if (Arguments.gridHubUrl != null) {
            String hub = Arguments.gridHubUrl;
            try {
                // Would it make sense to modify these values based on server or network speeds?  User could apply a factor to the defaults or something.
                logger.fine("Driver.start(), creating new RemoteWebDriver with hub " + hub);
                driver = new RemoteWebDriver(new URL(hub), chromeDriverOptions); // takes a while.  Causes Chrome browser to start up with blank page

                logger.fine("Driver.start(), setting page load timeout to " + Pep.PAGE_LOAD_TIMEOUT_SECONDS);
                driver.manage().timeouts().pageLoadTimeout(Pep.PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all page loads, not just login

                logger.fine("Driver.start(), setting script timeout to " + Pep.SCRIPT_TIMEOUT_SECONDS);
                driver.manage().timeouts().setScriptTimeout(Pep.SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // Not sure it helps or hurts.

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
        // else start it up to talk with a simple remote server which is running ChromeDriver and Selenium standalone.
        else if (Arguments.seleniumServerUrl != null) {
            try {
                logger.fine("Driver.start(), creating new RemoteWebDriver with server " + Arguments.webServerUrl);
                URL seleniumServer = new URL(Arguments.seleniumServerUrl);

                driver = new RemoteWebDriver(seleniumServer, chromeDriverOptions); // need xxx/wd/hub at the end.  Otherwise it returns the contents of the default selenium server page.
                logger.fine("Driver.start(), created new RemoteWebDriver with server " + Arguments.webServerUrl);
            } catch (MalformedURLException e) {
                if (!Arguments.quiet) System.err.println("Couldn't connect to server at " + Arguments.webServerUrl + " Exception: " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (UnreachableBrowserException e) {
                logger.severe("Couldn't get to browser.  " + Utilities.getMessageFirstLine(e) + " Exiting...");
                System.exit(1);
            } catch (Exception e) {
                logger.severe("Something happened and couldn't connect.  Exiting... e: " + e.getMessage());
                System.exit(1);
            }
        }
        // If neither of those two were specified, then start it up with a local WebDriver and local Selenium jar (standalone server is okay for this)
        else {
            try {
                // Start up the browser headed or headless, locally, with blank page.  Takes a few seconds.
                logger.finer("Driver.start(), creating a new ChromeDriver with  " + chromeDriverOptions.toString());
                if (Arguments.verbose) System.out.println("Driver.start(), creating a new ChromeDriver with  " + chromeDriverOptions.toString());
                driver = new ChromeDriver(chromeDriverOptions); // starts up browser.  Doesn't go to any page.
                if (Arguments.verbose) System.out.println("Driver.start(), setting pageLoadTimeout of " + Pep.PAGE_LOAD_TIMEOUT_SECONDS + " seconds");
                driver.manage().timeouts().pageLoadTimeout(Pep.PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all page loads, not just login page
                // It's possible the following slow things down unnecessarily.  Not sure.  Experiment again.
                if (Arguments.verbose) System.out.println("Driver.start(), setting implicit wait timeout of " + Pep.ELEMENT_TIMEOUT_SECONDS + " seconds");
                driver.manage().timeouts().implicitlyWait(Pep.ELEMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects all implicit wait elements
                if (Arguments.verbose) System.out.println("Driver.start(), setting scriptTimeout of " + Pep.SCRIPT_TIMEOUT_SECONDS + " seconds");
                driver.manage().timeouts().setScriptTimeout(Pep.SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // affects those asynchronous script waits new 12/13/18
                // no need to try to clear cache.  Everything's cleared when selenium starts up chromedriver, I think.
                if (Arguments.verbose) System.out.println("Driver.start(), No need to try to clear cache.");
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
    }
}
