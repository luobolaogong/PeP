package pep.utilities;

import org.openqa.selenium.NoSuchElementException;
import pep.utilities.lorem.Lorem;
import pep.utilities.lorem.LoremIpsum;
import org.openqa.grid.web.servlet.handler.WebDriverRequest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static pep.Pep.isDemoTier;
import static pep.Pep.isGoldTier;
import static pep.TmdsPortal.logoutFromTmds;
import static pep.utilities.AutomationUtils.findElement;

//public class Utilities extends AutomationUtils {
public class Utilities {
    private static Lorem lorem = LoremIpsum.getInstance(); // this is suspect.  Complicates.  Have is separate.

    public Utilities() {
    }

    // a little silly to do it this way, having lorem part of Utilities
    private static String getRandomLastName() {
        return lorem.getLastName();
    }

    private static String getRandomFirstNameMale() {
        return lorem.getFirstNameMale();
    }

    private static String getRandomFirstNameFemale() {
        return lorem.getFirstNameFemale();
    }

    private static String getRandomNameMale() { return lorem.getNameMale(); }

    private static String getRandomNameFemale() { return lorem.getNameFemale(); }

    private static String getRandomLatinFirstName() {
        return lorem.getTitle(1, 1);
    }

    private static String getRandomUsAddress() {
        return lorem.getUsAddress();
    }
    private static String getRandomUsAddressNoState() {
        return lorem.getUsAddressNoState();
    }
    private static String getRandomRelationship() {
        return lorem.getRelationship();
    }
    private static String getRandomTitleWords(int min, int max) {
        return lorem.getTitle(min, max);
    }

    private static String getAllergyName() {
        return lorem.getAllergyName();
    }

    private static String getAllergyReaction() {
        return lorem.getAllergyReaction();
    }

    private static String getUnitIdentificationCode() {
        return lorem.getUnitIdentificationCode();
    }
    private static String getUnitName() {
        return lorem.getUnitName();
    }
    private static String getUnitEmployer() {
        return lorem.getUnitEmployer();
    }

    private static String getCptCode() {
        return lorem.getCptCode();
    }

    private static String getIcd9Code() {
        return lorem.getIcd9Code();
    }

    private static String getIcd10Code() {
        return lorem.getIcd10Code();
    }

    private static String getInjuryIllnessAssessment() {
        return lorem.getInjuryIllnessAssessment();
    }

    private static String getInjuryIllnessAdmissionNote() {
        return lorem.getInjuryIllnessAdmissionNote();
    }

    private static String getDischargeNote() {
        return lorem.getDischargeNote();
    }

    private static String getCommentNoteComplication() {
        return lorem.getCommentNoteComplication();
    }

    private static String getPainManagementDissatisfiedComment() {
        return lorem.getPainManagementDissatisfiedComment();
    }

    private static String getPainManagementPlan() {
        return lorem.getPainManagementPlan();
    }

    private static String getBhNote() {
        return lorem.getBhNote();
    }

    private static String getBlockLocation() {
        return lorem.getBlockLocation();
    }

    private static String getTbiAssessmentNoteComment() {
        return lorem.getTbiAssessmentNoteComment();
    }

    private static String getLocationAdminNote() {
        return lorem.getLocationAdminNote();
    }


    public enum TextFieldType {
        PARAGRAPH,
        SHORT_PARAGRAPH,
        FIRST_NAME,
        FIRST_NAME_MALE,
        FIRST_NAME_FEMALE,
        NAME_MALE,
        NAME_FEMALE,
        LAST_NAME,
        TITLE,
        SSN,
        DOB,
        RELATIONSHIP,
        US_ADDRESS,
        US_ADDRESS_NO_STATE,
        US_PHONE_NUMBER,
        DATE,
        DATE_TIME,
        HHMM,
        THREE_OR_MORE,
        JPTA,
        CPT_CODES,
        ALLERGY_NAME,
        ALLERGY_REACTION,
        UNIT_IDENTIFICATION_CODE,
        UNIT_NAME,
        UNIT_EMPLOYER,
        DISCHARGE_NOTE,
        ICD9_CODE,
        CPT_CODE, // resolve the CPT_CODES use above
        ICD10_CODE,
        INJURY_ILLNESS_ASSESSMENT,
        INJURY_ILLNESS_ADMISSION_NOTE,
        COMMENTS_NOTES_COMPLICATIONS,
        PAIN_MGT_COMMENT_DISSATISFIED,
        PAIN_MGT_PLAN,
        BH_NOTE,
        BLOCK_LOCATION,
        TBI_ASSESSMENT_NOTE_COMMENT,
        LOCATION_ADMIN_NOTES
    }

    // Don't we need one of these for just unlimited text?  Whatever the user wants to put in?
    private static String genRandomValueText(TextFieldType textFieldType) {
        String randomValueText = null;
        switch (textFieldType) {
            case PARAGRAPH:
                randomValueText = Utilities.getRandomParagraphs(1, 1);
                break;
            case SHORT_PARAGRAPH:
                randomValueText = Utilities.getRandomWords(1, 20);
                break;
            case FIRST_NAME:
                randomValueText = Utilities.getRandomLatinFirstName();
                break;
            case FIRST_NAME_MALE:
                randomValueText = Utilities.getRandomFirstNameMale();
                break;
            case FIRST_NAME_FEMALE:
                randomValueText = Utilities.getRandomFirstNameFemale();
                break;
            case NAME_MALE:
                randomValueText = Utilities.getRandomFirstNameMale();
                break;
            case NAME_FEMALE:
                randomValueText = Utilities.getRandomFirstNameFemale();
                break;
            case LAST_NAME:
                randomValueText = Utilities.getRandomLastName();
                break;
            case SSN:
                randomValueText = Utilities.getRandomSsnLastFourByDate();
                break;
            case DOB:
                randomValueText = Utilities.getRandomDate(1950, 2000);
                break;
            case RELATIONSHIP:
                randomValueText = Utilities.getRandomRelationship();
                break;
            case US_ADDRESS:
                randomValueText = Utilities.getRandomUsAddress();
                break;
            case US_ADDRESS_NO_STATE:
                randomValueText = Utilities.getRandomUsAddressNoState();
                break;
            case US_PHONE_NUMBER:
                randomValueText = Utilities.getRandomUsPhoneNumber();
                break;
            case DATE:
                randomValueText = getCurrentDate();
                break;
            case DATE_TIME:
                randomValueText = getCurrentDateTime();
                break;
            case HHMM:
                randomValueText = getCurrentHourMinute();
                break;
            case TITLE:
                randomValueText = Utilities.getRandomTitleWords(1, 3);
                break;
            case THREE_OR_MORE: // should be THREE_OR_MORE_WORDS, but the following must be wrong then
                randomValueText = Utilities.getRandomTitleWords(1, 3);
                break;
            case JPTA:
                randomValueText = "455TH EMDG KANDAHAR (JPTA_AF17)";
                break;
//            case CPT_CODES:
//                randomValueText = Integer.toString(Utilities.random.nextInt(999)); // Used for searching.  improve later.  Maybe select randomly from list
//                break;
            case ALLERGY_NAME:
                randomValueText = Utilities.getAllergyName();
                break;
            case ALLERGY_REACTION:
                randomValueText = Utilities.getAllergyReaction();
                break;
            case UNIT_IDENTIFICATION_CODE:
                randomValueText = Utilities.getUnitIdentificationCode();
                break;
            case UNIT_NAME:
                randomValueText = Utilities.getUnitName();
                break;
            case UNIT_EMPLOYER:
                randomValueText = Utilities.getUnitEmployer();
                break;
            case DISCHARGE_NOTE:
                randomValueText = Utilities.getDischargeNote();
                break;
            case ICD10_CODE:
                randomValueText = Utilities.getIcd10Code();
                break;
            case CPT_CODE: // resolve the CPT_CODES above
                randomValueText = Utilities.getCptCode();
                break;
            case ICD9_CODE:
                randomValueText = Utilities.getIcd9Code();
                break;
            case INJURY_ILLNESS_ASSESSMENT:
                randomValueText = Utilities.getInjuryIllnessAssessment();
                break;
            case INJURY_ILLNESS_ADMISSION_NOTE:
                randomValueText = Utilities.getInjuryIllnessAdmissionNote();
                break;
            case COMMENTS_NOTES_COMPLICATIONS:
                randomValueText = Utilities.getCommentNoteComplication();
                break;
            case PAIN_MGT_COMMENT_DISSATISFIED:
                randomValueText = Utilities.getPainManagementDissatisfiedComment();
                break;
            case PAIN_MGT_PLAN:
                randomValueText = Utilities.getPainManagementPlan();
                break;
            case BH_NOTE:
                randomValueText = Utilities.getBhNote();
                break;
            case BLOCK_LOCATION:
                randomValueText = Utilities.getBlockLocation();
                break;
            case TBI_ASSESSMENT_NOTE_COMMENT:
                randomValueText = Utilities.getTbiAssessmentNoteComment();
                break;
            case LOCATION_ADMIN_NOTES:
                randomValueText = Utilities.getLocationAdminNote();
                break;
//            case PAIN_MGT_COMMENTS: // not satisfied
//                randomValueText = Utilities.getRandomWords(1, 20);
//                break;
//            case SPNB_COMMENTS:
//                randomValueText = Utilities.getRandomWords(1, 20);
//                break;
//            case TBI_ASSESSMENT_COMMENTS:
//                randomValueText = Utilities.getRandomWords(1, 20);
//                break;
//            case LEVEL_SPINE_CATHETER:
//                randomValueText = Utilities.getRandomWords(1, 20);
//                break;
            default:
                if (Arguments.debug) System.out.println("Unexpected text field type: " + textFieldType.toString());
                break;
        }
        return randomValueText;
    }

    // This is a pretty bad method because of the sleeps that seem necessary.  How to get around doing this?
    public static boolean myNavigate(By... linksBy) {
        if (Arguments.debug) System.out.println("Utilities.myNavigate()...");
        WebElement linkElement;
        for (By linkBy : linksBy) {
            if (Arguments.debug) System.out.println("Utilities.myNavigate(), linkBy: " + linkBy.toString());
            try { // this sleep stuff really needs to get fixed.
                Utilities.sleep(755); // new, and seems necessary when looping around to back here after some treatment stuff.  Possibly not long enough.  was 555
                linkElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(linkBy))); // not sure helps
                if (Arguments.debug) System.out.println("Utilities.myNavigate(), got the linkElement: " + linkElement.getText());
            } catch (Exception e) {
                if (Arguments.debug)
                    System.out.println("Utilities.myNavigate(), Couldn't access link using By: " + linkBy.toString() + "  Exception: ->" + e.getMessage() + "<-");
                return false;
            }
            try {
                Utilities.sleep(55); // just a test to see if this helps click not get a "is not clickable at point (62, 93)..." Happens right after "Processing Registration ..." so, right after start, but after previous patient, not initial
                if (Arguments.debug) System.out.println("Utilities.myNavigate(), clicking on the link element");
                linkElement.click();
                Utilities.sleep(555); // looks like the last link of the 3 (pain management note) can take a while to complete.  Maybe sleep should be at caller
            } catch (Exception e) {
                if (Arguments.debug)
                    System.out.println("Utilities.myNavigate(), could not click.  Exception: ->" + e.getMessage() + "<-");
                return false;
            }
        }
        if (Arguments.debug) System.out.println("Utilities.myNavigate(), succeeded, leaving.");
        return true;
    }


    // Using Actions() is interesting because maybe it makes the operations atomic, and if one part of it fails you're
    // back to where you were initially.  I'm not sure.
    public static void navSubMenus(By... links) {
        Actions builder = new Actions(Driver.driver);
        for (By link : links) {
            // Next line calls findElement() which calls waitUntilElementIsVisible which calls another one, and then calls
            // explicit wait.until() with an ExpectedCondition.visibilityOfElement(By) which returns a WebElement or null
            WebElement element = findElement(link);
            if (element == null) {
                //System.out.println(driver.getPageSource());
                throw new RuntimeException("Could not find element with locator: " + link.toString());
            }
            builder.moveToElement(element); // wow
        }

        builder.click();
        builder.perform();
    }


    public static void ajaxWait() {
        while (true) {
            Boolean ajaxIsComplete = null;
            try {
                ajaxIsComplete = (Boolean) ((JavascriptExecutor) Driver.driver).executeScript("return jQuery.active == 0");
            } catch (Exception e) {
                System.out.println("Utilities.ajaxWait(), exception caught: " + e.getMessage());
            }
            if (ajaxIsComplete) {
                break;
            }
            sleep(100);
        }
    }

    public void waitForAjax() throws InterruptedException {
        while (true) {
            Boolean ajaxIsComplete = (Boolean) ((JavascriptExecutor) Driver.driver).executeScript("return jQuery.active == 0");
            if (ajaxIsComplete) {
                break;
            }
            Thread.sleep(100);
        }
    }


    public static ExpectedCondition<Boolean> isFinishedAjax2() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                Boolean ajaxIsComplete = null;
                try {
                    ((JavascriptExecutor) Driver.driver).executeScript("return jQuery.active == 0");
                    return true;
                } catch (Exception e) {
                    System.out.println("Utilities.ajaxWait(), exception caught, possibly because no ajax on this page? : " + e.getMessage());
                    return true;
                }
            }
        };
    }


    // Wow, is this the only way?  Tad came up with this
    // You stick this after the explicit wait .until
    public static ExpectedCondition<Boolean> isFinishedAjax() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (driver instanceof JavascriptExecutor) {
                    try {
                        Boolean value =
                                (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0"); // wow
                        return value;
                    } catch (WebDriverException driverException) {
                        if (Arguments.debug)
                            System.out.println("--------------isFinishedAjax(), No ajax on this page.  returning true");
                        return true;// assuming there's no jQuery or ajax on this page.
                    }
                } else {
                    try {
//                        Thread.sleep(5000);
                        if (Arguments.debug) System.out.println("--------------isFinishedAjax(), gunna sleep");

                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        if (Arguments.debug)
                            System.out.println("--------------isFinishedAjax(), caught interrupted exception");

                    }
                    if (Arguments.debug) System.out.println("--------------isFinishedAjax(), returning true at end");
                    return true;
                }
            }
        };
    }


    // This method just has problems.  I don't trust the methods it calls.
    public static String processDropdown(By by, String value, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite = false;
        boolean hasCurrentValue = false;
        WebElement dropdownWebElement;
        try {
            dropdownWebElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get dropdownWebElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        Select select = new Select(dropdownWebElement); // fails here for originating camp, and other things
        WebElement optionSelected = select.getFirstSelectedOption();
        String currentValue = optionSelected.getText().trim(); // correct
        if (currentValue != null) { // probably has all options in this string.  Check
            hasCurrentValue = true;
            if (currentValue.isEmpty()) {
                hasCurrentValue = false;
            }
            else if (currentValue.contains("Select")) { // as in Select Gender, Select Race Select Branch Select Rank Select FMP
                hasCurrentValue = false;
            }
            else if (currentValue.contains("4XX.XX")) { //???
                hasCurrentValue = false;
            }
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = Utilities.getRandomDropdownOptionString(by);
                Utilities.selectDropdownOption(by, value);
            } else { // value is not "random"
                Utilities.selectDropdownOption(by, value); // this may fail when system is slow (ajax wait)
            }
        } else { // value is not specified
            if (required) { // field is required
                value = Utilities.getRandomDropdownOptionString(by); // this can fail if there are no options
                if (value == null || value.isEmpty()) { // added isEmpty()
                    if (Arguments.debug)
                        System.out.println("For some reason getRandomDropdownOptionString return null or an empty string.");
                    return null;
                }
                // Even though we just got a random value from the dropdown, we have to still have to make sure it's selected.
                Utilities.selectDropdownOption(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE??????????????????????
                if (sectionIsRandom) { // all this sectionIsRandom stuff could be automatically inherited if set up as classes that extend, like the tree I've drawn
                    value = Utilities.getRandomDropdownOptionString(by);
                    if (value != null) { // this is new because now returning null if problem in above.  Not sure at all.
                        Utilities.selectDropdownOption(by, value);
                    }
                } else { // section is not random
                    if (Arguments.debug)
                        System.out.println("In processDropdown(), the field is not required, and sectionIsRandom is " + sectionIsRandom + " so not doing anything with it.");
                }
            }
        }
        return value;
    }


    // If a field is required and no value was provided, but there's already a value
    // in the text field, don't overwrite it with random value.  this means we have to read the element's content.
    public static String processText(By by, String value, TextFieldType textFieldType, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processText(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        String currentValue = webElement.getAttribute("value").trim();

        if (currentValue != null) { //little awkward logic, but maybe okay if find other text values to reject
            hasCurrentValue = true;
            if (currentValue.isEmpty()) {
                hasCurrentValue = false;
            }
        }

        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = genRandomValueText(textFieldType);
                Utilities.fillInTextField(by, value);
            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                value = genRandomValueText(textFieldType);
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                if (Arguments.debug) System.out.println("This is a big change, and a big test.  If things stop working right, then uncomment this section");
            }
        }
        return value;
    }

    public static String getCurrentTextValue(By by) {
        // probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }

    // wrong
    public static String getCurrentDropdownValue(By by) {
        // probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }

    // wrong
    public static String getCurrentRadioValue(By by) {
        // probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }

    // A date may be specified as a field value, or it may come from the command line, or a properties file or
    // from the PatientsJson file.  But this method is called from methods that already know if the value
    // was specified or not.  The user can't specify a range.
    //
    // If it's "random" what date should be created?  There should be some range specified,
    // like "between 1950 and 2000".  In the JSON file rather than "random" it could be "random 1950-2000".  But
    // maybe the JSON file (or command line) date just says "random", or "now".
    // Let's assume the JSON file has date possibilities of
    // <missing>, "", "02/04/1954", "random", "now", or "random 1954-2000", or "random 02/04/1954-01/01/2000"
    // If it's <missing> or "", and required, then a date must be generated, and it will be today.
    // If it's <missing> or "", and not required, then skip it.
    // If it's "now" or "random", then specify today's date.
    // If it's "random 1950-2000", generate a date between those years, inclusive.
    // If it's "random 02/04/1954-01/01/2000", generate a date between those dates, inclusive.
    //
    // Java's LocalDate has parsing.  Does it support Period?  Can it convert to
    // How about where d1 and d2 are LocalDate, (copied from stackoverflow)...
    // int days = Days.daysBetween(d1, d2).toDays();
    // LocalDate randomDate = d1.addDays(ThreadLocalRandom.nextInt(days+1));

    public static String processDate(By by, String value, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        //String currentValue = webElement.getText().trim(); // Untested.  Wrong, I think.
        String currentValue = webElement.getAttribute("value").trim(); // InjuryDate comes back ""

        //if (currentValue != null) {
        if (currentValue != null && !currentValue.isEmpty()) { // isEmpty is new, does this screw things up?
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested, ever happen with Integer?
                hasCurrentValue = false;
            }
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }



        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDate();
                value = Utilities.fillInTextField(by, value); // here and below I just now 10/10/18 started capturing the return value
            } else if (value.startsWith("random")) {
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];

                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                value = Utilities.fillInTextField(by, value);
            } else { // value is not "random"
                value = Utilities.fillInTextField(by, value); // wow this thing doesn't return success/failure
                // do we need to check value for null here?
                if (value == null) { // new, added, untested
                    if (Arguments.debug)
                        System.out.println("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null; // fails: 8
                }

            }




        } else { // value is not specified
            if (required) { // field is required
                // logic could be improved
                value = getCurrentDate();
                String tempValue = Utilities.fillInTextField(by, value);
                if (tempValue == null) { // is this nec???????
                    if (Arguments.debug) System.out.println("Utilities.processDate(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else { // field is not required, but section may be specified as random, not sure this happens any more though
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) { // added extra check for safety, though probably this indicates a fault elsewhere
                    value = getCurrentDate();
                    value = Utilities.fillInTextField(by, value);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }

    public static String processDateTime(By dateTimeFieldBy, String value, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(dateTimeFieldBy));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + dateTimeFieldBy.toString() + " Exception: " + e.getMessage());
            return null;
        }
        String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        if (currentValue != null) {
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested, ever happen with Integer?
                hasCurrentValue = false;
            }
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


//        boolean shouldWriteNewValue = false;
//        boolean hasCurrentValue = false;
//        String currentValue = Utilities.getCurrentTextValue(dateTimeFieldBy);
//        if (currentValue != null) {
//            hasCurrentValue = true;
//        }
//        if (hasCurrentValue) {
//            if (sectionIsRandom) {
//                shouldWriteNewValue = true;
//            }
//        }
//        if (shouldWriteNewValue) {
//            System.out.println("We should write a new value.");
//        }
//        else {
//            System.out.println("We should not write a new value.");
//            return currentValue;
//        }

//        // Let's check that the field actually is available.  This method seems to fail if not on right page at the time, I think.
//        try {
//            // This next line has got to be wrong, not working or something.  It continues on when there is no such field
//            //WebElement dateTimeField = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(by));
//            if (Arguments.debug) System.out.println("Gunna check for a dateTimeField: " + dateTimeFieldBy);
//            WebElement dateTimeField = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.visibilityOfElementLocated(dateTimeFieldBy));
//            if (Arguments.debug) System.out.println("We got it????  dateTimeFiels is " + dateTimeField);
//        }
//        catch (Exception e) {
//            if (Arguments.debug) System.out.println("Cannot process date/time field if it isn't available.  Exception: " + e.getMessage());
//            return null; // failures, demo: 1, gold:1   Hey, but this is correct.  We should not be here in this method because there is no field on the page.
//        }




        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDateTime();
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
            } else if (value.startsWith("random")) {
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];

                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                String time = getRandomTime();
                //Utilities.automationUtils.waitUntilElementIsVisible(by); // totally new
                value = Utilities.fillInTextField(dateTimeFieldBy, value + " " + time);
            } else { // value is not "random"
                //Utilities.automationUtils.waitUntilElementIsVisible(by); // totally new
                Utilities.sleep(1555); // really hate to do it, but datetime is ALWAYS a problem, and usually blows up here.  Failed with 1555, failed with 2555  Because not on right page at time?
                if (Arguments.debug) System.out.println("Are we sitting in the right page to next try to do a date/time??????????????");
                //String theDateTimeString = Utilities.fillInTextField(dateTimeFieldBy, value); //
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (value == null) {
                    if (Arguments.debug)
                        System.out.println("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null; // fails: 8
                }
                if (Arguments.debug) System.out.println("In ProcessDateTime() Stuffed a date: " + value);
            }



        } else { // value is not specified
            if (required) { // field is required
                value = getCurrentDateTime();
                //Utilities.automationUtils.waitUntilElementIsVisible(by); // totally new
                String tempValue = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (tempValue == null) { // is this nec???????
                    if (Arguments.debug) System.out.println("Utilities.processDateTime(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else { // field is not required, but section may be specified as random, not sure this happens any more though
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) { // added extra check for safety, though probably this indicates a fault elsewhere
                    value = getCurrentDateTime();
                    //Utilities.automationUtils.waitUntilElementIsVisible(by); // totally new
                    value = Utilities.fillInTextField(dateTimeFieldBy, value);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        return value;
    }

    public static String processIntegerNumber(By by, String value, int minValue, int maxValue, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        //String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        String currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        if (currentValue != null && !currentValue.isEmpty()) { // isEmpty is new, does this screw things up?
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested, ever happen with Integer?
                hasCurrentValue = false;
            }
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                int intValue = random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                int intValue = random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom) {
                    int intValue = random.nextInt(maxValue - minValue) + minValue;
                    value = String.valueOf(intValue);
                    Utilities.fillInTextField(by, value);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }


    // Hey this is for a special kind of string of digits, like maybe SSN and not a real number, so watch out.  Use processIntegerNumber?
    public static String processStringOfDigits(By by, String value, int minDigits, int maxDigits, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite = false;
        boolean hasCurrentValue = false;
        WebElement webElement = null;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        //String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        String currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        //if (currentValue != null) { // we could check this for numeric, but unnec.
        if (currentValue != null && !currentValue.isEmpty()) { // we could check this for numeric, but unnec.
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested, ever happen?
                hasCurrentValue = false;
            }
        }

        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom) {
                    value = getRandomTwinNumber(minDigits, maxDigits);
                    Utilities.fillInTextField(by, value);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }


    // This was slapped together.  Based on processStringOfDigits, but that wasn't analyzed
    public static String processDoubleNumber(By by, String value, double minValue, double maxValue, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get webElement specified by " + by.toString() + " Exception: " + e.getMessage());
            return null;
        }
        String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        if (currentValue != null) { // we could check this for numeric, but unnec.
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested, ever happen with Integer?
                hasCurrentValue = false;
            }
        }

        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                double range = maxValue - minValue;
                double randomValue = random.nextDouble();
                value = String.format("%.2f", (minValue + (range * randomValue)));
                Utilities.fillInTextField(by, value);
            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                double range = maxValue - minValue;
                double randomValue = random.nextDouble();
                value = String.format("%.2f", (minValue + (range * randomValue)));
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom) {
                    double range = maxValue - minValue;
                    double randomValue = random.nextDouble();
                    value = String.format("%.2f", (minValue + (range * randomValue)));
                    Utilities.fillInTextField(by, value);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }


    public static String processRadiosByLabel(String value, Boolean sectionIsRandom, Boolean required, By... radiosByLabels) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing radio set on the page or not.  If any radio button in the set is checked, then the set has a current value
        boolean overwrite;
        boolean hasCurrentValue = false;

        for (By radioLabelBy : radiosByLabels) {
            String currentValue = Utilities.getCurrentRadioValue(radioLabelBy); // prob wrong
            if (currentValue != null) { // wrong
                hasCurrentValue =true;
                break;
            }
        }



        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }





//        boolean shouldWriteNewValue = false;
//        boolean hasCurrentValue = false;
//        String currentValue = Utilities.getCurrentRadioValue(radios[0]); // wrong, need to check all?  And String return? and text?
//        if (currentValue != null) {
//            hasCurrentValue = true;
//        }
//        if (hasCurrentValue) {
//            if (sectionIsRandom) {
//                shouldWriteNewValue = true;
//            }
//        }
//        if (shouldWriteNewValue) {
//            System.out.println("We should write a new value.");
//        }
//        else {
//            System.out.println("We should not write a new value.");
//            return currentValue;
//        }







        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomRadioLabel(radiosByLabels); // should check on this
                value = doRadioButtonByLabel(value, radiosByLabels);
            } else { // value is not "random"
                value = doRadioButtonByLabel(value, radiosByLabels); // garbage in, what happens?
            }
        } else { // value is not specified
            if (required) { // field is required
                value = getRandomRadioLabel(radiosByLabels); // should check on this
                value = doRadioButtonByLabel(value, radiosByLabels);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom) {
                    value = getRandomRadioLabel(radiosByLabels); // should check on this
                    value = doRadioButtonByLabel(value, radiosByLabels);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }

    public static String processRadiosByButton(String value, Boolean sectionIsRandom, Boolean required, By... radiosByButtons) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing radio set on the page or not.  If any radio button in the set is checked, then the set has a current value
        boolean overwrite;
        boolean hasCurrentValue = false;

        for (By radioButtonBy : radiosByButtons) {
            String currentValue = Utilities.getCurrentRadioValue(radioButtonBy); // prob wrong
            if (currentValue != null) { // wrong
                hasCurrentValue =true;
                break;
            }
        }



        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }








        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = doRadioButtonByButton(value, radiosByButtons);
            } else { // value is not "random"
                value = doRadioButtonByButton(value, radiosByButtons); // garbage in, what happens?
            }
        } else { // value is not specified
            if (required) { // field is required
                value = doRadioButtonByButton(value, radiosByButtons);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom) {
                    value = doRadioButtonByButton(value, radiosByButtons);
                } else { // section is not random
                    System.out.println("Do we ever get here?");
                }
            }
        }
        return value;
    }


    public static String getRandomRadioLabel(By... radioLabelByList) {
        int nRadioLabelBys = radioLabelByList.length;
        int randomRadioLabelIndex = random.nextInt(nRadioLabelBys);
        try {
            By radioLabelBy = radioLabelByList[randomRadioLabelIndex];
            WebElement radioLabelElement = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.presenceOfElementLocated(radioLabelBy));
            String radioLabelText = radioLabelElement.getText(); // stupid Baseline radio buttons, and Referral, comes back with "", "Unknown" has text ""
            if (radioLabelText.isEmpty()) {
                System.out.println("Utilities.getRandomRadioLabel(), selected radio " + radioLabelBy.toString() + " but corresponding label is blank, so how about returning 'Yes'?");
                radioLabelText = "Yes"; // hack that won't last
            }
            return radioLabelText;
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.getRandomRadioLabel(), couldn't get radio element " + randomRadioLabelIndex + " Exception: " + e.getMessage());
            return null;
        }
    }

    // total hack.  Needs to be looked at closely.  These radio By elements are supposed to be for their labels, not the buttons!!!
    // So only call this method if the By elements are labels and not buttons.
    public static String doRadioButtonByLabel(String value, By... radios) {
        //String radioLabelText = null;
        for (By radio : radios) {
            try {
                WebElement radioElement = (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.presenceOfElementLocated(radio));
                String radioLabelText = radioElement.getText(); // You can't do this if the DOM structure doesn't have a label inside the input element.  Gold doesn't.  At least in laterality of PNB in SPNB in ProcedureNotes.
                if (radioLabelText != null && radioLabelText.equalsIgnoreCase(value)) { // not good
                    //System.out.println("Found radio element that will now be clicked: " + radioLabelText);
                    radioElement.click();
                    return radioLabelText;
                } else {
                    //if (Arguments.debug) System.out.println("Utilities.doRadioButtonByLabel(), radioLabelText not what looking for: " + radioLabelText);
                    continue;
                }
            } catch (Exception e) {
                if (Arguments.debug)
                    System.out.println("Utilities.doRadioButtonByLabel(), didn't get radioElement, or its text: " + e.getMessage());
                continue;
            }
        }
        return null;
    }

    // It's clear now that Selenium does not support text nodes and so you cannot easily get to the text label following a radio button
    // when there is no accompanying <label> node.  With <label> you can get the Selenium WebElement and do a getText() to get the label
    // text, but since that doesn't exist, perhaps it's possible to get a button's parent node and then go a getText(), and parse the
    // results.  Trying that next.  It is very suspect.  Inefficient, because we loop through the radios and each time we get the
    // parent and then its text children and break them up and loop through them and see if the value passed in matches one of
    // those text children, and if so, ....
    // Why not skip the looping and just get the first radio and then its parent, and then its children, and then you've
    // got two parallel arrays (hopefully), and then go through the text children looking for a match, and then if there
    // is one, use its index as an index into the buttons, and click it and return the value.
    // index to get th
    public static String doRadioButtonByButton(String value, By... radios) {
        try {
            int nRadios = radios.length;
            if (value == null || value.equalsIgnoreCase("random") || value.isEmpty()) {
                int randomIndex = Utilities.random.nextInt(nRadios);

                // Why not wrap some of these in try/catch so we don't have to have so many if's?????????????????

                WebElement matchingRadioElement = (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.presenceOfElementLocated(radios[randomIndex]));
                // now get the matching label
                //String newValue = matchingRadioElement.getText(); // doesn't work
                WebElement parentElement = matchingRadioElement.findElement(By.xpath("parent::*"));
                String labelsString = parentElement.getText();
                String[] labels = null;
                String newValue = null;
                if (labelsString != null && !labelsString.isEmpty()) {
                    labels = labelsString.split(" ");
                    newValue = labels[randomIndex]; // hopefully right
                } else {
                    if (Arguments.debug) System.out.println("Something assumed about radio labels that isn't true.  Like what? " + labelsString);
                    System.out.println("And parent is " +  parentElement);
                    return null;
                }
                matchingRadioElement.click();
                return newValue;
            }
            WebElement firstRadioElement = (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.presenceOfElementLocated(radios[0]));
            WebElement parentElement = firstRadioElement.findElement(By.xpath("parent::*"));
            String labelsString = parentElement.getText();
            String[] labels = null;
            if (labelsString != null && !labelsString.isEmpty()) {
                labels = labelsString.split(" ");
            } else {
                if (Arguments.debug) System.out.println("Something assumed about radio labels that isn't true.  What? labelsString: " + labelsString);
                System.out.println("And parentElement: " + parentElement);
                return null;
            }

            for (int labelCtr = 0; labelCtr < labels.length; labelCtr++) {
                String label = labels[labelCtr];
                if (label.trim().equalsIgnoreCase(value)) {
                    WebElement matchingRadioElement = (new WebDriverWait(Driver.driver, 4)).until(ExpectedConditions.presenceOfElementLocated(radios[labelCtr]));
                    matchingRadioElement.click();
                    return label;
                }
            }
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.doRadioButtonByButton(), couldn't do radio button" + e.getMessage());
            return null;
        }
        return null; // right?

    }

    // This is for checkboxes (which are toggles) which means if the box is already checked you don't
    // want to click it again to try to set it.  Rather than clear first, do the XOR.
    // In the JSON file a Boolean can be true, false, null, or just missing, which is null.
    // If missing, it's null.  Then how do you specify "random"?  You can't.  We could make the
    // assumption that if it's null it means "random".  Oh, but that's the same as other fields
    // too.  Okay.
    public static Boolean processBoolean(By by, Boolean value, Boolean sectionIsRandom, Boolean required) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        if (sectionIsRandom) {
            if (Arguments.debug) System.out.println("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = (value != null);

        // Establish whether to overwrite existing checkbox on the page or not.  You can't tell by looking at the element whether
        // or not it has a value.  In a sense it always has a value, because no check means false.  So, just assume it has a value.
        boolean overwrite;
//        boolean hasCurrentValue = true;

        if (valueIsSpecified) {
            overwrite = true;
        }
//        else if (hasCurrentValue) {
//            overwrite = false;
//        }
        else if (!required && !sectionIsRandom) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            if (Arguments.debug) System.out.println("Don't go further because we don't want to overwrite.");
            return value;
        }



        if (valueIsSpecified) {
            WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
            WebElement checkBoxWebElement = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

            if (checkBoxWebElement != null) {
                boolean isChecked = checkBoxWebElement.isSelected(); // is this the right check to get the state?
                if (value != isChecked) {
                    checkBoxWebElement.click();
                }
            }
        } else { // value is not specified
            if (required) { // field is required
                value = random.nextBoolean();
                WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
                WebElement checkBoxWebElement = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

                if (checkBoxWebElement != null) {
                    boolean isChecked = checkBoxWebElement.isSelected();
                    if (value != isChecked) {
                        checkBoxWebElement.click();
                    }
                }
            } else { // field is not required
                if (sectionIsRandom) {
                    value = random.nextBoolean();
                    WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
                    WebElement checkBoxWebElement = wait.until(ExpectedConditions.visibilityOfElementLocated(by));


                    if (checkBoxWebElement != null) {
                        boolean isChecked = checkBoxWebElement.isSelected();
                        if (value != isChecked) {
                            checkBoxWebElement.click();
                        }
                    }
                }
            }
        }
        return value; // Don't change state
    }


    public static void clickButton(final By button) {
        try {
            WebElement buttonElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(button));
            buttonElement.click();
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.clickButton(), didn't get button to click, or couldn't click it: " + e.getMessage());
        }
    }

    public static boolean getCheckboxValue(final By locator) {
        final WebDriver driver = Driver.driver;
        WebElement checkbox = driver.findElement(locator);
        boolean isSelected = checkbox.isSelected();
        return isSelected;
    }


    public static void clickAlertAccept() {
        final WebDriver driver = Driver.driver;
        Alert possibleAlert = driver.switchTo().alert();
        if (possibleAlert == null) {
            logoutFromTmds();
        }
        (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                try {
                    Alert possibleAlert = driver.switchTo().alert();
                    if (possibleAlert == null) {
                        if (Arguments.debug) System.out.println("\tAlertAccept not available");
                        return false;
                    }
                    if (possibleAlert.getText().length() < 1) {
                        if (Arguments.debug)
                            System.out.println("\tIn Utilities.clickAlertAccept(), No text for this alert");
                        return false;
                    }
                } catch (Exception e) {
                    if (Arguments.debug)
                        System.out.println("\tIn Utilities.clickAlertAccept(), AlertAccept still not available.  Exception caught: " + e.getMessage());
                    return false;
                }
                return true;
            }
        });
        try {
            possibleAlert.accept(); // This can allow a "Concurrent Login Attempt Detected" page to appear because the login gets processed
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("\tIn Utilities.clickAlertAccept(), Could not accept the alertAccept.  Exception caught: " + e.getMessage());
            return;
        }
        return;
    }

    private static String getRandomParagraphs(int min, int max) {
        return lorem.getParagraphs(min, max);
    }

    private static String getRandomWords(int min, int max) {
        return lorem.getWords(min, max);
    }

    public static String fillInTextFieldElement(final WebElement element, String text) {
        try {
            element.clear(); // sometimes null pointer here.  Yes.  Why? Because times out and element comes back null?
        } catch (org.openqa.selenium.InvalidElementStateException e) {
            if (Arguments.debug)
                System.out.println("In fillInTextField(), Tried to clear element, got invalid state, Element is not interactable?  Continuing.");
            //return null;
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("fillInTextField(), SomekindaException.  couldn't do a clear, but will continue anyway: " + e.getMessage());
            return null;
        }
        try {
            element.sendKeys(text); // prob here "element is not attached to the page document"
        } catch (ElementNotVisibleException e) { // I think it fails because we're not on a page that has the field, but how got here, don't know.
            if (Arguments.debug)
                System.out.println("fillInTextField(), Could not sendKeys. Element not visible exception.  So, what's the element?: " + element.toString());
            return null;
        }
        return text;
    }

    // This is the worst freaking method with regard to timing failures.  What is so hard
    // about slapping some text into a text field?  Why are there always exceptions thrown?
    // What's wrong with that explicit wait, waiting for the presence of the field?  Obviously
    // it's caused by the calling method and not having the field ready, because of some AJAX
    // call, probably.  And why can't the waitForAjax method work?
    //
    // It's also possible that the field is not writable.  Marked readonly.
    public static String fillInTextField(final By field, String text) {
        if (text == null || text.isEmpty()) {
//            if (Arguments.debug && !(field.toString().contains("registerNumber") || field.toString().contains("patientSearchRegNum"))) // total hack - if field is transaction, don't mention this
//                System.out.println("Utilities.fillInTextField(), no text to fill anything in with.  returning null.  Can happen if pass in transaction number in search.  It's okay.");
            return null;
        }
        WebElement element = null;
        try { // this next line is where we fail.  Maybe it's because this text field comes right after some AJAX call, and we're not ready
            element = (new WebDriverWait(Driver.driver, 10))
                    .until(
                            ExpectedConditions.presenceOfElementLocated(field)); // This can timeout
            //ExpectedConditions.visibilityOfElementLocated(field)); // does this thing wait at all?
            String readonlyAttribute = element.getAttribute("readonly");
            if (readonlyAttribute != null) {
                if (readonlyAttribute.equalsIgnoreCase("true")) { // actually, in the html it says readonly="readonly" but for some reason comes back true
                    if (Arguments.debug)
                        System.out.println("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
                if (readonlyAttribute.equalsIgnoreCase("readonly")) {
                    if (Arguments.debug)
                        System.out.println("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
            }
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.fillInTextField(), could not get element: " + field.toString() + " Exception: " + e.getMessage());
            return null; // this happens a lot!!!  TimeoutException
        }
        if (Arguments.debug) System.out.println("Utilities.fillInTextField(), element is " + element);



        if (element == null) {
            System.out.println("How do we get a null element if there was no exception caught?");
            try {
                if (Arguments.debug) System.out.println("Let's try again...");
                element = (new WebDriverWait(Driver.driver, 10))
                        .until(
                                ExpectedConditions.presenceOfElementLocated(field)); // This can timeout
            }
            catch (Exception e) {
                if (Arguments.debug) System.out.println("2nd try didn't work either");
                return null;
            }
        }


        try {
            // This next line will throw an exception InvalidElementStateException .  Why?
            // Something wrong with the element.  It thinks it cannot be cleared.  Maybe the element is marked unwritable or something.
            // So, how about just not clearing it?
            element.clear();
        } catch (InvalidElementStateException e) {
            if (Arguments.debug)
                System.out.println("Utilities.fillInTextField(), Invalid Element State.  Could not clear element:, " + element + " Oh well.  Continuing");
            //return null;
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.fillInTextField(), could not clear element:, " + element + " Oh well.  Continuing.  Exception: " + e.getMessage());
            //return null;
        }

        try {
            // lets do a refresh because the clear can do something bad?
            if (Arguments.debug) System.out.println("Utilities.fillInTextField(), gunna refresh then wait for visibility of field: " + field);
            // This next line causes an error, and I think it's because we are NOT on the right page when we try to do this.
            element = (new WebDriverWait(Driver.driver, 10))
                    .until(ExpectedConditions.refreshed(
                            ExpectedConditions.visibilityOfElementLocated(field))); // does this thing wait at all?
            if (Arguments.debug) System.out.println("Utilities.fillInTextField(), waited for that field, and now gunna send text to it: " + text);
            element.sendKeys(text); // prob here "element is not attached to the page document"
            if (Arguments.debug) System.out.println("Success in sending text to that element."); // May be wront.  Maybe couldn't write.
        } catch (TimeoutException e) {
            if (Arguments.debug)
                System.out.println("Utilities.fillInTextField(), could not sendKeys " + text + " to it. Timed out");
            return null; // fails: 2
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.fillInTextField(), could not sendKeys " + text + " to it. Exception: " + e.getMessage());
            return null;
        }
        //System.out.println("Leaving fillInTextField(), with success I think.");
        return text; // probably should return the text that was sent in.
    }

    private static String getRandomDropdownOptionString(final By dropdownBy) {
        WebElement dropdownWebElement = null;
        try {
            // Crucial difference here between presenceOfElementLocated and visibilityOfElementLocated.  For TBI Assessment Note, must have visibilityOfElementLocated
            // why is this next line really slow for Arrival/Location. Status????
            dropdownWebElement = (new WebDriverWait(Driver.driver, 30)).until(ExpectedConditions.visibilityOfElementLocated(dropdownBy));
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " + e.getMessage());
            return null;
        }
        Select select = new Select(dropdownWebElement); // fails here for originating camp, and other things
        List<WebElement> optionElements = select.getOptions(); // strange it can get the options, but they are all stale?
        int size = optionElements.size();
        if (size < 2) { // Hey can't there ever be a dropdown with 1 option?
            if (Arguments.debug)
                System.out.println("This dropdown " + dropdownBy.toString() + " has no options.  Returning null");
            return null; // try again?
        }
        int randomOptionIndex = random.nextInt(size); // 0, 1, 2, 3  (if 4), but the first element in the list should not be chosen.  It is element 0
        randomOptionIndex = (randomOptionIndex == 0) ? 1 : randomOptionIndex; // Some dropdowns start with 0, but most do not. THIS IS FLAWED.  doesn't work for icd code set for example.
        //System.out.println("\tgetRandomDropdownOptionString, and randomOptionIndex is " + randomOptionIndex);
        //if (Arguments.debug) System.out.println("We'll use option number " + randomOptionIndex); // is option 1 bad??????????????  Failed with 1
        WebElement option = null;
        try {
            option = optionElements.get(randomOptionIndex); // optionElements is a list based on first is 0
        } catch (StaleElementReferenceException e) {
            if (Arguments.debug) System.out.println("Hmmm, stale element they say.  Must be optionElements");
            return null;
        } catch (Exception e) { // IndexOutOfBoundsException
            if (Arguments.debug)
                System.out.println("In Utilities.getRandomDropdownOptionString(), size: " + size + " driverUrl " + dropdownBy.toString());
            if (Arguments.debug) System.out.println("Exception caught in selecting dropdown option: " + e.getMessage());
            return null; // ?????????????????????????????
        }
        try {
            String optionString = option.getText();
            //if (Arguments.debug) System.out.println("getRandomDropdownOptionString is returning the string " + optionString);
            return optionString;
        } catch (Exception e) {
            if (Arguments.debug) System.out.println("Why the crap can't I getText() from the option?");
        }
        return null;
    }

    // why doesn't this return a value selected? Because it has to match exactly so you know anyway?  Well, success? failure?
    //public static void selectDropdownOption(final By dropdownBy, String optionString) {
    // This can fail after an effort to click on the Procedure Notes tab.
    private static String selectDropdownOption(final By dropdownBy, String optionString) {
        WebElement dropdownElement = null;
        try {
            dropdownElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(dropdownBy))); // this ExpectedConditions stuff is really powerful.  Look at it!!!!  Lots of things.
        } catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.selectDropdownOption(), couldn't get dropdown " + dropdownBy.toString() + " Exception: " + e.getMessage());
            return null;
        }
        try {
            //if (Arguments.debug) System.out.println("Utilities.selectDropdownOption(), here comes a new Select(dropdownElement)");
            Select select = new Select(dropdownElement); // fails, can through a Stale element reference: 1
            //if (Arguments.debug) System.out.println("Utilities.selectDropdownOption(), Will now do a selectByVisibleText with that option string");
            select.selectByVisibleText(optionString); // throws exception, stale:1  Why?  Because whatever called this method caused a DOM rewrite probably
            //if (Arguments.debug) System.out.println("Utilities.selectDropdownOption(), Back from calling selectByVisibleText with option " + optionString);
        } catch (StaleElementReferenceException e) {
            if (Arguments.debug)
                System.out.println("Utilities.selectDropdownOption(), Couldn't select option " + optionString + " Stale element reference, not attached to the page");
            return null;
        } catch (NoSuchElementException e) {
            if (Arguments.debug)
                System.out.println("Utilities.selectDropdownOption(), No such element exception: Couldn't select option " + optionString);
            return null;
        }
        catch (Exception e) {
            if (Arguments.debug)
                System.out.println("Utilities.selectDropdownOption(), Couldn't select option " + optionString + " Exception: " + e.getMessage());
            return null;
        }
        return optionString;
    }


    static private final String alphabetUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final String alphabetLower = "abcdefghijklmnopqrstuvwxyz";
    static private final String digits = "0123456789";

    public static Random random = new Random(System.currentTimeMillis()); // change to "randomGenerator" ?;

    public static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HHmm");
        TimeZone timeZone = TimeZone.getDefault(); // probably don't need to do this if gunna do next line
        dateFormat.setTimeZone(timeZone);
        String dateAndTime = dateFormat.format(new Date());
        return dateAndTime;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        //TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);
        String dateAndTime = dateFormat.format(new Date());
        return dateAndTime;
    }

    public static String getCurrentHourMinute() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        String dateAndTime = simpleDateFormat.format(new Date());
        return dateAndTime;
    }

    /**
     * Return a random upper or lower case letter
     *
     * @param isUpper
     * @return
     */
    public static char getRandomLetter(boolean isUpper) {
        if (isUpper) {
            return alphabetUpper.charAt(random.nextInt(26));
        }
        return alphabetLower.charAt(random.nextInt(26));
    }

    /**
     * A random number of specified length, that starts with a twin digit.
     */
    public static String getRandomTwinNumber(int minNDigits, int maxNDigits) {
        StringBuilder stringBuilder = new StringBuilder(maxNDigits); // names at least 3 char
        int nDigitsWanted = random.nextInt(maxNDigits + 1);
        if (nDigitsWanted < minNDigits) {
            nDigitsWanted = minNDigits;
        }
        char digit = digits.charAt(random.nextInt(10));
        stringBuilder.append(digit).append(digit); // append twins
        for (int ctr = 0; ctr < nDigitsWanted - 2; ctr++) {
            stringBuilder.append(digits.charAt(random.nextInt(10)));
        }
        return stringBuilder.toString();
    }


    // This is the version currently getting airplay.  The first 4 digits represent the time of day, and the last
    // four are the day and month.  The digit in the middle is just random.
    //
    // While this gives okay precision there's still a few problems.  The first is that if you run this app
    // in parallel you could get the same SSN for two different patients.  The second is that the number range
    // is pretty limited in that the first digit can only be 0, 1, or 2.  The third can only be 0-5.  The sixth
    // only 0 or 1.  The eighth only 0,1,2, or 3.  The goal is to be able to look at a SSN and tell when the
    // patient was created, find the patient using the last 4 digits, and be pretty sure there won't be duplicate
    // SSN's if the program is run simultaneously by different users or yourself or a process that forks off
    // lots of instances.
    //
    // We can improve things a little bit by making the 5th digit a random value 0-9, or the last digit of
    // milliseconds or microseconds since the epoch, assuming Time has that granularity.  Of course that still
    // makes it possible (10% chance) to have duplicates, but it's better than (minSec/10) which is about every 6
    // seconds there's a tick.
    //
    // There's probably a way to encode the time element using all 10 values per digit and still make it readable.
    // But I don't know what it would be right now.  I mean, it's easy to say "Oh, it 9:52am on June 29, so I'll look
    // for patients with SSN ending in 0629 and do the search and see what patients were generated after 0950.
    //
    // The last four digits are helpful in seeing what patients were created on a particular day of the year,
    // encoded MMDD.  So you can search easily, because the search allows for 4 digits or the entire SSN.
    // That leaves 5 digits.  We want something sequential so you can tell see an ordering of patients as
    // they were created, (even though the list isn't ordered when you look at it).  But that's not as important
    // as getting the granularity fine enough so that two random patients created at nearly the same time don't
    // get the same SSN.  Even if I used the 5 digits as the second in the day (max 86,400) it would be easy to
    // get duplicate SSN's if you're running this app in parallel.  Suppose people in a training class are running
    // this app to generate their own set of patients?  They'll get duplicates.  If it's just one copy of this
    // app running in the world at a time, then there's no problem, because it takes about a minute to generate
    // one random patient.
    //
    // Perhaps the easiest way is to forget about ordering and just generate a random 5 digit number.
    // What are the chances of getting a duplicate SSN that way?  It would have to happen within one
    // day's time.  I think it's a very small chance.  1 in 100,000.
    //
    // If you use number of microseconds since the start of the program, and then truncate somehow to fit
    // 5 digits, I don't think that's as good as random because the chance of duplication is much higher, plus
    // you don't really get an ordering of numbers.
    //
    public static String getRandomSsnLastFourByDate() {
        StringBuffer patternForSsn = new StringBuffer(9);
        int randomInt = Utilities.random.nextInt(100000);
        String formattedRandomInt = String.format("%05d", randomInt);
        patternForSsn.append(formattedRandomInt);
        patternForSsn.append("MMdd");
        DateFormat dateFormat = new SimpleDateFormat(patternForSsn.toString()); // This is just a way for me to more easily find patients by last 4 of SS
        String ssBasedOnDate = dateFormat.format(new Date());
        if (ssBasedOnDate.length() != 9) {
            if (Arguments.debug)
                System.out.println("!!!!!!!!!!!!!!!!!!!!!ssn " + ssBasedOnDate + " not 9 digits !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return ssBasedOnDate;
    }

    public static String getRandomUsPhoneNumber() {
        StringBuffer patternForUsPhoneNumber = new StringBuffer(12); // "999-999-9999"
        int areaCode = Utilities.random.nextInt(900) + 100;
        int middleThree = Utilities.random.nextInt(900) + 100;
        int lastFour = Utilities.random.nextInt(10000);
        String phoneNumber = String.format("%03d-%03d-%04d", areaCode, middleThree, lastFour);
        return phoneNumber;
    }
    // Assumes format mm/dd/yyyy
    private static String getRandomDateBetweenTwoDates(String startDateString, String endDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate startLocalDate = LocalDate.parse(startDateString, formatter);
        LocalDate endLocalDate = LocalDate.parse(endDateString, formatter);

        long days = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        LocalDate randomDate = startLocalDate.plusDays(random.nextInt((int) days + 1));

        String randomDateString = randomDate.format(formatter);
        return randomDateString;
    }

    /**
     * A random date between two dates, formatted as "mm/dd/yyyy".  Not doing any "double" stuff, like 11/22/1933
     *
     * @param minYear
     * @param maxYear
     * @return
     */
    private static String getRandomDate(int minYear, int maxYear) { // check logic
        GregorianCalendar calendar = new GregorianCalendar();

        int year = minYear;
        if (minYear != maxYear) {
            year = minYear + random.nextInt(maxYear - minYear);
        }

        calendar.set(calendar.YEAR, year);

        int maxDayOfYear = calendar.getActualMaximum(calendar.DAY_OF_YEAR); // could be off by a day
        int dayOfYear = random.nextInt(maxDayOfYear + 1); // could be off by a day

        calendar.set(calendar.DAY_OF_YEAR, dayOfYear);

        StringBuffer stringBuffer = new StringBuffer(10);
        stringBuffer.append(String.format("%02d", calendar.get(calendar.MONTH) + 1));
        stringBuffer.append("/");
        stringBuffer.append(String.format("%02d", calendar.get(calendar.DAY_OF_MONTH)));
        stringBuffer.append("/");
        stringBuffer.append(String.format("%02d", calendar.get(calendar.YEAR))); // ????? 02d?
        return stringBuffer.toString();
    }

    /**
     * This creates a string of form "HHMM".
     *
     * @return
     */
    private static String getRandomTime() {
        // 0-23, 0-59 formatted as two digits each
        int hours = random.nextInt(24);
        int mins = random.nextInt(60);
        return String.format("%02d%02d", hours, mins);
    }


    public static void sleep(int millis) {
        try {
            if (Arguments.debug) System.out.print(" " + millis + "ms ");
            Thread.sleep((int) (millis * Arguments.throttle));
        } catch (Exception e) {
            // ignore
        }
    }

    // Generate strings given a regular expression.
    // E.g.: \d{2,3}



}

