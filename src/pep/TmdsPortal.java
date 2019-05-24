package pep;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pep.Main.timerLogger;
import static pep.utilities.Driver.driver;

/**
 * This class is for logging into and out of TMDS
 * The login page has basically two parts, Consent, and Login, but only one part displays at a time.
 * Initially it's the Consent only, and the Login part is invisible.  But when you click on the ACCEPT button
 * the two parts reverse their visibility, due to the attached JavaScript.  This may take some time.
 *
 * We'd like to know when this javascript code completes, because if we move on to enter login info,
 * assuming we have the login text boxes ready and they're not there yet, or able to receive input
 * the Selenium code fails.
 *
 * But besides all that, we should not click the Accept button before it's there.  Usually this is
 * not an issue, but for slow connections we get to the click() before the button is visible.
 *
 * Each Patient can have a user associated with it.  If there is one, PeP will login with that person,
 * logging out first if necessary.
 */
public class TmdsPortal {
    private static Logger logger = Logger.getLogger(TmdsPortal.class.getName());

    private static By acceptButtonBy = By.cssSelector("button");
    private static By myLoginSectionBy = By.id("myLogin");
    private static By userNameTextFieldBy = By.id("j_username");
    private static By passwordInputBy = By.name("j_password");
    private static By loginButtonBy = By.cssSelector("input[value='Login']");
    private static By loginMessageAreaBy = By.className("warntext");
    private static By iFrameBy = By.id("portletFrame");
    private static By logoutLinkBy = By.linkText("Logout");

    /**
     * Check for the existence of the login page and then click the accept button.
     * Prior to this point there is only a blank page in a browser.
     * @param webServerUrl
     * @return
     */
    public static boolean getLoginPage(String webServerUrl) { // changed to public 2/8/19
        try {
            // This next line is where the browser issues a GET, and all this happens:
            // 1.  Loads https://demo-tmds.akimeka.com/portal/ index page with a GET, which does contain the Accept button, and the two main parts
            // 2.  css downloaded
            // 3.  Loads portal-login.js which contains function loginConfirm(_), and popup(url), and a checkBrowser() function.
            driver.get(webServerUrl); //  Times out if server down.
        } catch (Exception e) {
            logger.severe("TmdsPortal.getLoginPage(), didn't get the webserver Url: " + webServerUrl + ", Exception: " + Utilities.getMessageFirstLine(e));
            ScreenShot.shoot("SevereError");
            return false;
        }

        boolean pageIsLoaded = Driver.driver.getPageSource().contains("javascript:getLogin"); // what's this?
        if (!pageIsLoaded) {
            logger.fine("Failed to load the page, or at least it didn't contain javascript:getLogin code");
            return false;
        }
        WebElement acceptButton = null;
        try {
            acceptButton = Utilities.waitForRefreshedClickability(acceptButtonBy, 15, "TmdsPortal.getLoginPage()");
        } catch (Exception e) {
            logger.severe("TmdsPortal.getLoginPage(), couldn't get acceptButton: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        // The following is overkill but interesting
        ExpectedCondition<Boolean> cond1 = ExpectedConditions.textToBe(acceptButtonBy, "ACCEPT");
        ExpectedCondition<WebElement> cond2 = ExpectedConditions.visibilityOfElementLocated(acceptButtonBy);
        ExpectedCondition<Boolean> cond4 = ExpectedConditions.attributeContains(acceptButtonBy, "onclick", "getLogin");
        try {
            (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(cond1, cond2, cond4));
        } catch (Exception e) {
            if (!Arguments.quiet) System.out.println("Could not login.");
            logger.severe("Could not login.");
            return false;
        }
        acceptButton.click();

        // check that the click reversed the visibility before leaving this method.  But even if it isn't we'll leave
        Utilities.waitForVisibility(myLoginSectionBy, 10, "TmdsPortal.getLoginPage()");
        return true; // It's possible to get here without the acceptButton actually being clicked

    }

    /**
     * Fill in login credentials, click login button, check for error.
     * @param user The TMDS user name
     * @param password The password for the user
     * @return success or failure of login attempt
     */
    public static boolean doLoginPage(String user, String password) { // changed to public 2/8/19
        WebElement loginNameInputField = null;

        try {
            loginNameInputField = Utilities.waitForVisibility(userNameTextFieldBy, 20, "TmdsPortal.doLoginPage(), checking for user name field.");
        } catch (Exception e) {
            logger.severe("TmdsPortal.doLoginPage(), Couldn't get login text boxes to log in with.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        try {
            fillInTextFieldElement(loginNameInputField, user);
            WebElement passwordInputElement = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(passwordInputBy));
            fillInTextFieldElement(passwordInputElement, password);
        } catch (Exception e) {
            logger.severe("TmdsPortal.doLoginPage(), couldn't get login text boxes to log in with.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        Instant start;
        try {
            WebElement loginButton = Utilities.waitForVisibility(loginButtonBy, 10, "TmdsPortal.doLoginPage(), checking for login button.");

            if (Arguments.pauseSave > 0) {
                Utilities.sleep(Arguments.pauseSave * 1000, "TmdsPortal.doLoginPage(),sleeping as requested");
            }
            start = Instant.now();
            loginButton.click();
        } catch (TimeoutException e) {
            logger.severe("TmdsPortal.doLoginPage(), Couldn't get login button and/or couldn't click it.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }

        // Accept alert which is always there because it's part of the Login button.  It's the one that says "By clicking OK, I confirm ... privacy statement ..."
        try {
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.alertIsPresent()); // add method Utilities.waitForAlert() or something
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept(); // alert goes away, and new page comes into view.
        } catch (TimeoutException e) {
            logger.severe("TmdsPortal.doLoginPage(), Either alert wasn't present, or if it was couldn't accept it."); ScreenShot.shoot("SevereError");
            return false;
        }

        // At this point we may get a "Change Password" page, or a "Concurrent Login Attempt Detected"  These could cause failure.
        try {
            WebElement loginButton = Utilities.waitForPresence(loginMessageAreaBy, 1, "TmdsPortal.doLoginPage()"); // 1/25/19
            String loginErrorMessage = loginButton.getText();
            if (loginErrorMessage != null && !loginErrorMessage.isEmpty()) {
                System.err.println("***Error logging in: " + loginErrorMessage);
                return false;
            }
        } catch (Exception e) {
            logger.finer("TmdsPortal.doLoginPage(), No login error message.  Continuing on.");
        }
        // At this point we have a whole new page loaded.  The login stuff is gone.  Now sure why doing following.
        try {
            (new WebDriverWait(driver, 30)).until(ExpectedConditions.refreshed(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iFrameBy)));
        } catch (TimeoutException e) {
            logger.severe("TmdsPortal.doLoginPage(), Timed out waiting for portletFrame.  Slow server?"); ScreenShot.shoot("SevereError");
            return false;
        } catch (Exception e) {
            logger.severe("TmdsPortal.doLoginPage(), Some other exception trying to get iFrame portletFrame: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        timerLogger.info("TmdsPortal.doLoginPage(), loginButton.click() took " + ((Duration.between(start, Instant.now()).toMillis()) / 1000.0) + "s");
        return true;
    }
    /**
     * Given a WebElement that can take a text value, this method attempts to stuff it with the provided text.
     * @param element The text element, not the locator for it
     * @param text the text to stuff
     * @return the string that was used to fill in the field/element.
     */
    private static String fillInTextFieldElement(final WebElement element, String text) {
        try {
            element.clear(); // null pointer
        } catch (org.openqa.selenium.InvalidElementStateException e) {
            logger.warning("In fillInTextField(), Tried to clear element, got invalid state, Element is not interactable?  Continuing.");
        } catch (Exception e) {
            logger.warning("fillInTextField(), SomekindaException.  couldn't do a clear, but will continue anyway: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            element.sendKeys(text); // prob here "element is not attached to the page document"
        } catch (ElementNotVisibleException e) { // fails because we're not on a page that has the field, usually
            logger.warning("fillInTextField(), Could not sendKeys. Element not visible exception.  So, what's the element?: " + element.toString());
            return null;
        }
        return text;
    }

    /**
     * Logout from TMDS
     * @return success or failure
     */
    public static boolean logoutFromTmds() {
        // Is that why the menu links wouldn't work, because I didn't do a switchTo().defaultContent() ?
        try {
            Driver.driver.switchTo().defaultContent(); // Wow, this is really important to get stuff on the outermost window or whatever
            WebElement logoutLink = Utilities.waitForVisibility(logoutLinkBy, 5, "TmdsPortal.logoutFromTmds");
            logoutLink.click();
            driver.quit(); // should first close the logger file descriptors?
            return true;
        } catch (Exception e) {
            logger.severe("Couldn't get logout link.  e: " + Utilities.getMessageFirstLine(e));
        }
        driver.quit(); // should first close the logger file descriptors?
        return false;
    }

    /**
     * Switch from current logged in user to a different user by logging out and logging in with the
     * new credentials as found in a properties file.  The properties file would be the same one
     * (default name "pep.properties") used to store the properties tier, user, password, and others.
     * <p>
     * There could be multiple users each with their passwords, and then at the start of processing a
     * patient, if a new user is specified, as with the JSON encounter input file line
     * "user": "autopepr0001", the associated password would be found as a property value associated
     * with that user.  The properties file would just be lines that contained the user name and password,
     * as in
     * autopepr0001=somePassword!
     * autopepr0002=someOtherPassword!
     * autopepr0003=yetAnotherPassword!
     * <p>
     * The benefit of this would be the "longitudinal" concept of following
     * a patient from one "Role" facility to another.  Each of those steps requires a new user which
     * requires a new login.
     * <p>
     * Yes, this same thing could be done by invoking PeP with a new user.
     *
     * @param newUser
     * @return
     */
    public static boolean switchUsers(String newUser) {
        String password = null;
        if (Pep.pepProperties != null) {
            password = Pep.pepProperties.getProperty(newUser);
            if (password == null) {
                return false;
            }
        }

        try {
            Driver.driver.switchTo().defaultContent(); // Wow, this is really important to get stuff on the outermost window or whatever
            By logoutLinkBy = By.linkText("Logout");
            WebElement logoutLink = Utilities.waitForVisibility(logoutLinkBy, 5, "TmdsPortal.logoutFromTmds");
            logoutLink.click();
        } catch (Exception e) {
            logger.severe("Couldn't get logout link.  e: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        try {
            By logBackInLinkBy = By.linkText("click here.");
            WebElement clickHereLink = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(logBackInLinkBy));
            clickHereLink.click();
        } catch (Exception e) {
            System.out.println("Failed.");
            return false;
        }

        By acceptButtonBy = By.cssSelector("button");
        By myLoginSectionBy = By.id("myLogin");
        WebElement acceptButton = null;
        try {
            acceptButton = Utilities.waitForRefreshedClickability(acceptButtonBy, 15, "TmdsPortal.getLoginPage()");
        } catch (Exception e) {
            logger.severe("TmdsPortal.getLoginPage(), couldn't get acceptButton: " + Utilities.getMessageFirstLine(e));
            return false;
        }
        // The following seems overkill to me, but it's quite interesting anyway
        ExpectedCondition<Boolean> cond1 = ExpectedConditions.textToBe(acceptButtonBy, "ACCEPT");
        ExpectedCondition<WebElement> cond2 = ExpectedConditions.visibilityOfElementLocated(acceptButtonBy);
        ExpectedCondition<Boolean> cond4 = ExpectedConditions.attributeContains(acceptButtonBy, "onclick", "getLogin");
        try {
            (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(cond1, cond2, cond4));
        } catch (Exception e) {
            if (!Arguments.quiet) System.out.println("Could not login.");
            logger.severe("Could not login.");
            return false;
        }
        acceptButton.click();

        // check that the click reversed the visibility before leaving this method.  But even if it isn't we'll leave
        Utilities.waitForVisibility(myLoginSectionBy, 10, "TmdsPortal.getLoginPage()");
        boolean someResult = TmdsPortal.doLoginPage(newUser, password);
        return someResult;
    }


}
