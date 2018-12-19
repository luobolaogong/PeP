package pep.utilities;

import com.google.common.base.Predicate;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Any convenience code that would be used by both a Page class and Test class
 * 
 * @author tdicks
 *
 */
public class AutomationUtils {
    private static Logger logger = Logger.getLogger(AutomationUtils.class.getName());

  protected ResourceBundle testResources = ResourceBundle
      .getBundle("com.akimeka.test.automated_test");

  protected WebDriver driver;
  protected static Wait<WebDriver> wait;
  protected static Wait<WebDriver> shortWait;

  public AutomationUtils() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   * @param selectLocator Element locator for the Drop down
   * @param optionText Visible text of the option you want to select
   */
  public void select(By selectLocator, String optionText) {
    if (optionText != null && selectLocator != null) {
      try {

        this.wait.until(Utilities.isFinishedAjax());
        Select select = this.waitUntilOptionIsSelectable(selectLocator, optionText);
        select.selectByVisibleText(optionText);
      } catch (StaleElementReferenceException elementReferenceException) {
        this.wait.until(Utilities.isFinishedAjax());
        this.select(selectLocator, optionText); // wait for the select to be re-rendered

      }
    }
  }



  protected ExpectedCondition<Boolean> isFinishedAjax() {

    return new ExpectedCondition<Boolean>() {

      public Boolean apply(WebDriver driver) {
        if (driver instanceof JavascriptExecutor) {
          try {
            Boolean value =
                (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0");
            return value;
          } catch (WebDriverException driverException) {
            return true;// assuming there's no jQuery or ajax on this page.
          }
        } else {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException ie) {

          }
          return true;
        }
      }
    };
  }


  /**
   * This method will eventually cause a WebDriver asynchronous call, and it's causing problems
   * @param elmentLocator tell the webdriver to wait until a particular element is visible
   * @return web element waiting to locate or null if it is not found
   */
  public static WebElement waitUntilElementIsVisible(By elmentLocator) { // this did not work for me when trying to do service/rank dropdown
    WebElement webElement = waitUntilElementIsVisible(elmentLocator, false);
    return webElement;
  }


  /**
   * 
   * @param elementLocator tell the webdriver to wait until a particular element is visible
   * @param wantShortWait use the long wait or the short wait
   * @return web element waiting to locate or null if it is not found
   */

  public static WebElement waitUntilElementIsVisible(By elementLocator, boolean wantShortWait) {
    try {
      //System.out.println("In waitUntilElementIsVisible() shortWait: " + shortWait);
      if (wantShortWait) {
        WebElement webElement = shortWait.until(visibilityOfElement(elementLocator));
        return webElement;
      } else {
        WebElement webElement = wait.until(visibilityOfElement(elementLocator)); // go in, slow if can't find.  npe?
        return webElement;
      }
    } catch (TimeoutException timeoutException) {
      System.out.println("AutomationUtils.waitUntilElementIsVisible(), Timed out waiting for: " + elementLocator.toString());
      timeoutException.getMessage(); //timeoutException.printStackTrace();
      return null;
    }
  }


  private static ExpectedCondition<WebElement> visibilityOfElement(final By locator) {
    return new ExpectedCondition<WebElement>() {

      public WebElement apply(WebDriver driver) {
        try {
          WebElement toReturn = driver.findElement(locator); // can go in?
          //System.out.println("In AutomationUtils.apply() which is a callback inside visibilityOfElement(By), and locator is " + locator.toString());
          if (toReturn.isDisplayed()) {
            //System.out.println("toReturn is " + toReturn.toString());
            return toReturn;
          }
          System.out.println("AutomationUtils.visibilityOfElement.apply() Didn't find element " + locator.toString() + " so will return null");
          return null;
        } catch (StaleElementReferenceException elementReferenceException) {
          System.out.println("AutomationUtils.visibilityOfElement.apply() Caught a StaleElementReferenceException while looking for element " + locator.toString() + " so returning null");
          return null;
        }
      }
    };
  }


  protected Select waitUntilOptionIsSelectable(By by, String opt) {
    return this.wait.until(optionIsSelectable(by, opt));
  }

  private ExpectedCondition<Select> optionIsSelectable(final By selectLocator,
                                                       final String optionText) {
    return new ExpectedCondition<Select>() {

      public Select apply(WebDriver driver) {
        try {
          WebElement element = findElement(selectLocator);
          Select select = new Select(element);
          List<WebElement> options = select.getOptions();

          for (WebElement opt : options) {
            if (optionText.equals(opt.getText())) {
              return select;
            }
          }
          return null;
        } catch (StaleElementReferenceException elementReferenceException)// the select drop down is
                                                                          // being swapped out
        {
          return null;
        }
      }
    };
  }


  /**
   * 
   * @param elementLocator locator for element you're looking for
   * @return the element after waiting for it to appear
   */
  public static WebElement findElement(By elementLocator) {
    return waitUntilElementIsVisible(elementLocator);
  }
}
