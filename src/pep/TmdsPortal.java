package pep;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static pep.utilities.Driver.driver;

public class TmdsPortal {

    private static By acceptButtonBy = By.xpath("//*[@id=\"myConsent\"]/div/button");
    private static By myLoginSectionBy = By.id("myLogin"); // this is supposed to be a visible part of the page, first.
    private static By userNameTextFieldBy = By.id("j_username");
    private static By passwordInputBy = By.name("j_password");
    private static By loginButtonBy = By.className("portlet-form-button");
    private static By loginMessageAreaBy = By.xpath("//*[@id=\"myLogin\"]/table[2]/tbody/tr/td[2]/span");
    private static By iFrameBy = By.id("portletFrame");
    private static By logoutLinkBy = By.linkText("Logout"); // interesting <a id="logout" href="/portal/sec/signout">Logout</a>

    // The login page has basically two parts, Consent, and Login, but only one part displays at a time.
    // Initially it's the Consent only, and the Login part is invisible.  But when you click on the ACCEPT button
    // the two parts reverse their visibility.  There is no AJAX/Server call that I know of.  We want to know
    // when the page visibility changes so that we can move on.
    //
    // The click() causes a call to a JavaScript getLogin(), which code is found on the page a little below
    // the button which causes the Document element with Id "myConsent" to suddenly have
    // style="display: none;" and the element with Id "myLogin" to have style="display: block;"
    // and the element with Id "j_username" to call focus().  These document elements are on the page
    // already, but are hidden or displayed, and this JavaScript just causes the changes.
    // All of this may take some time.
    //
    // We'd like to know when this javascript code completes, because if we move on to enter login info,
    // assuming we have the login text boxes ready and they're not there yet, or able to receive input
    // the Selenium code fails.
    //
    // But besides all that, we should not click the Accept button before it's there.  Usually this is
    // not an issue, but for slow connections we get to the click() before the button is visible.
    //
    static boolean getLoginPage(String tierUrl) {
        // Prior to this point there is only a blank page in a browser.
        // WHAT ABOUT "Concurrent Login Attempt Detected" ????????????
        try {
            // This next line is where the browser gets a new page, the first page.  Prior to this it's just blank.
            // Seems that it triggers a lot.
            // 1.  Loads https://demo-tmds.akimeka.com/portal/ index page with a GET, which does contain the Accept button, and the two main parts
            // 2.  css downloaded
            // 3.  Loads portal-login.js which contains function loginConfirm(_), and popup(url), and a checkBrowser() function.
            driver.get(tierUrl); // Issues a GET.  Sometimes has blocked, sometimes ripped through.  times out if server down.
        }
        catch (Exception e) {
            if (Arguments.debug)
                System.out.println("TmdsPortal.getLoginPage(), didn't get the tierUrl: " + tierUrl + " Exception: " + e.getMessage().split("\n"));
            return false;
        }

        // we should wait until the index page is completely loaded and the accept button is visible and clicable before trying to click on it
        boolean pageIsLoaded = Driver.driver.getPageSource().contains("javascript:getLogin");
        if (!pageIsLoaded) {
            if (Arguments.debug) System.out.println("Failed to load the page, or at least it didn't contain javascript:getLogin code");
            return false;
        }
        WebElement acceptButton = null;
            acceptButton = (new WebDriverWait(driver, 15)).until(ExpectedConditions.elementToBeClickable(acceptButtonBy));

         ExpectedCondition<Boolean> cond1 = ExpectedConditions.textToBe(acceptButtonBy, "ACCEPT");
         ExpectedCondition<WebElement> cond2 = ExpectedConditions.visibilityOfElementLocated(acceptButtonBy);
         ExpectedCondition<Boolean> cond4 = ExpectedConditions.attributeContains(acceptButtonBy, "onclick", "getLogin");
        (new WebDriverWait(driver, 15)).until(ExpectedConditions.and(cond1, cond2, cond4));

        acceptButton.click();

        // check that the click reversed the visibility before leaving this method.  But even if it isn't we'll leave
//        By myLoginSectionBy = By.id("myLogin"); // this is supposed to be a visible part of the page, first.
        (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(myLoginSectionBy));
        return true; // It's possible to get here without the acceptButton actually being clicked, and we'll be sitting on the same page with the button.  Why?

    }

    static boolean doLoginPage(String user, String password) {
        // Should we check here to see we're on the right page?  Often we're not.

        // This next stuff seems a bit strange.  We do a get of the loginNameInputField element and then we call
        // fillInTextFieldElement and pass in that element, rather than just calling fillInTextField() and pass in the
        // By for the element.  What's the advantage?  Most of the time we do the latter, but this method is the only
        // place where the former is done.  Is it to test we have the right page?  Probably.

        WebElement loginNameInputField = null;

        try {
            loginNameInputField = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(userNameTextFieldBy));
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), Couldn't get login text boxes to log in with.  Exception: " + e.getMessage());
            return false; // The last thing we see before getting here. : "see if there's a login name input box
        }
        try {
            Utilities.fillInTextFieldElement(loginNameInputField, user); // hey hold on.  Do we have an element or do we have a By?
            WebElement passwordInputElement = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(passwordInputBy));
            Utilities.fillInTextFieldElement(passwordInputElement, password);  // wait, do we have an element or a by?
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), ouldn't get login text boxes to log in with.  Exception: " + e.getMessage());
            return false; // The last thing we see before getting here. : "see if there's a login name input box
        }
        try {
            WebElement loginButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(loginButtonBy));
            loginButton.click();
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), Couldn't get login button and/or couldn't click it.  Exception: " + e.getMessage());
            return false;
        }

        try {
            // Accept alert which is always there because it's part of the Login button.  It's the one that says "By clicking OK, I confirm ... privacy statement ..."
            (new WebDriverWait(driver, 10)).until(ExpectedConditions.alertIsPresent());
            WebDriver.TargetLocator targetLocator = driver.switchTo();
            Alert someAlert = targetLocator.alert();
            someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), Either alert wasn't present, or if it was couldn't accept it.");
            return false;
        }

        // Check for login error.  If there's no error, there's no message
        try {
            WebElement loginButton = (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(loginMessageAreaBy));
            String loginErrorMessage = loginButton.getText();
            if (loginErrorMessage != null && !loginErrorMessage.isEmpty()) {
                System.err.println("Error logging in: " + loginErrorMessage);
                return false;
            }
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), No login error message.  Continuing on.");
        }

        // At this point we have a whole new page loaded.  The login stuff is gone.  The following stuff is just a check, I guess, that we
        // actually did leave the login page.  But I'm not 100% sure it's right.  Why switch to a new frame?
        try {
            (new WebDriverWait(driver, 30)).until(ExpectedConditions.refreshed(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iFrameBy)));
        }
        catch (TimeoutException e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), Timed out waiting for portletFrame.  Slow server?");
            return false;
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("TmdsPortal.doLoginPage(), Some other exception trying to get iFrame portletFrame: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean logoutFromTmds() {
        Driver.driver.switchTo().defaultContent(); // Wow, this is really important to get stuff on the outermost window or whatever
        // Is that why the menu links wouldn't work, because I didn't do a switchTo().defaultContent() ?
        try {
            WebElement logoutLink = (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.visibilityOfElementLocated(logoutLinkBy));
            logoutLink.click();
        }
        catch (Exception e) {
            System.out.println("Couldn't get logout link.  e: " + e.getMessage());
        }
        driver.quit();
        return true;
    }
}
