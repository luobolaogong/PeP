package pep.utilities;

import org.openqa.selenium.NoSuchElementException;
import pep.Main;
import pep.patient.Patient;
import pep.utilities.lorem.Lorem;
import pep.utilities.lorem.LoremIpsum;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static pep.TmdsPortal.logoutFromTmds;

/**
 * This class holds all the common utility methods: generate random values for different field types,
 * process different field types, do navigation, and wait for field/elements signal readiness.
 *
 * Please note:
 * If a section is marked random, (as in Boolean sectionIsRandom) then all elements
 * in that section are required to have values.
 *
 * If a field is required and no value was provided, but there's already a value
 * in the field, the existing value is not overwritten with random value.
 */
public class Utilities {
    private static Logger logger = Logger.getLogger(Utilities.class.getName());
    private static Lorem lorem = LoremIpsum.getInstance(); // ?
    public static Random random = new Random(System.currentTimeMillis()); // change to "randomGenerator" or "randomize"?

    public Utilities() {
    }

    private static String getRandomLastName() {
        return lorem.getLastName();
    }
    private static String getRandomFirstNameMale() {
        return lorem.getFirstNameMale();
    }
    private static String getRandomFirstNameFemale() {
        return lorem.getFirstNameFemale();
    }
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
    private static String getEcSpineLevel() {
        return lorem.getEcSpineLevel();
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
        //CPT_CODES,
        ALLERGY_NAME,
        ALLERGY_REACTION,
        UNIT_IDENTIFICATION_CODE,
        UNIT_NAME,
        UNIT_EMPLOYER,
        DISCHARGE_NOTE,
        ICD9_CODE,
        CPT_CODE,
        EC_SPINE_LEVEL,
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

    /**
     * Get a random value for an input field type, and return it.
     * @param textFieldType The type of value expected
     * @return a string that should work for the input field.
     */
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
            case THREE_OR_MORE: // this is unused
                randomValueText = Utilities.getRandomTitleWords(3, 10);
                break;
            case JPTA:
                randomValueText = "455TH EMDG KANDAHAR (JPTA_AF17)";
                break;
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
            case EC_SPINE_LEVEL:
                randomValueText = Utilities.getEcSpineLevel();
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
            default:
                logger.fine("Unexpected text field type: " + textFieldType.toString());
                break;
        }
        return randomValueText;
    }
    /**
     * Navigate to a new page using the navigation tabs/bar and maybe submenus.
     *
     * This capability is crucial to moving around the webapp, and it should work from wherever you happen
     * to be sitting at the time, and not dependent upon the current context.  It should work as an escape
     * method, and not be hindered because the current context doesn't allow it.
     *
     * Being able to navigate to different pages is very important, but there have been problems doing this.
     * One problem is that dropdowns have not been working right.
     *
     * This one uses the Actions class, because the simpler more direct way isn't working because of a bug in the nav stuff.
     * Let's assume that this is only working with that silly pseudo menu system where there are tabs which when you
     * hover over them they show "submenu" elements, but if you click on it, the submenu disappears, the page changes,
     * and then a half second later the submenu appears again, thus making it impossible to click on the links in
     * the page.  So, we hover over the tab, then move to a submenu option, and then either click, or hover over it
     * and go to the subsubmenu item and click.  We don't click on the links in the page.
     *
     * This capability needs to be expanded so you can navigate away from wherever you're at whenever you want,
     * to wherever would be reasonable to go to.  So this method needs to be looked at, and maybe more methods written.
     *
     * @param linksBy a series of By elements representing different parts of an expanding set of navigation links
     * @return success or failure (t/f) at being able to navigate
     */
    public static boolean myNavigate(By... linksBy) {
        WebElement linkElement;
        Actions actions = new Actions(Driver.driver);
        for (By linkBy : linksBy) {
            if (pep.Main.catchBys) System.out.println(linkBy.toString() + "\tUtilities.myNavigate()");
            logger.finest("Utilities.myNavigate(), looking for linkBy: " + linkBy.toString());
            try {
                linkElement = Utilities.waitForRefreshedClickability(linkBy, 5, "Utilities.myNavigate(), waiting for " + linkBy.toString());
            } catch (Exception e) {
                logger.warning("Utilities.myNavigate(), Couldn't access link using By: " + linkBy.toString() + "  Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("warningError");
                return false; // fails: 2 due to PainManagementNote, EncounteredProblem: 1
            }
            try {
                actions.moveToElement(linkElement).build().perform();
            } catch (StaleElementReferenceException e) {
                logger.warning("Utilities.myNavigate(), Stale reference when trying to use linkElement, could not click on linkBy: " + linkBy.toString() + " Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("warningError");
                return false; // fails:1
            } catch (Exception e) {
                logger.warning("Utilities.myNavigate(), could not click on linkBy: " + linkBy.toString() + " Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("warningError");
                return false;
            }
        }
        // actions.click().perform();
        try {
            Actions clickActionIGuess = actions.click();
            if (clickActionIGuess != null) {
                clickActionIGuess.perform();
            }
        }
        catch (Exception e) {
            System.out.println("Utilities.myNavigate(), could not get the click button Actions object, or call perform on it.");
            return false;
        }
        return true;
    }

    /**
     * Wait until an AJAX operation completes.  This is not tested conclusively.  Not sure being used correctly either.
     * @return ExpectedCondition that's a boolean
     */
    public static ExpectedCondition<Boolean> isFinishedAjax() {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (driver instanceof JavascriptExecutor) {
                    try {
                        Boolean value =
                                (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0"); // wow
                        return value;
                    } catch (WebDriverException driverException) {
                        logger.fine("--------------Utilities.isFinishedAjax(), No ajax on this page.  Don't know who called.  returning true");
                        return true;// assuming there's no jQuery or ajax on this page.
                    }
                } else {
                    try {
                        logger.fine("--------------isFinishedAjax(), gunna sleep");

                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        logger.fine("--------------isFinishedAjax(), caught interrupted exception");

                    }
                    logger.fine("--------------isFinishedAjax(), returning true at end");
                    return true;
                }
            }
        };
    }

    /**
     * This method processes a dropdown element.
     * @param dropdownBy the locator of the dropdown element
     * @param value the value to select from the options in the dropdown.  May be "random"
     * @param sectionIsRandom whether the section is marked random or not
     * @param required whether the field value is required or not
     * @return the string of the selected dropdown option
     */
    public static String processDropdown(By dropdownBy, String value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.processDropdown()");
        if ((value == null || value.isEmpty()) && required == true) {
            logger.fine("Utilities.processDropdown(), Will generate a dropdown value for element " + dropdownBy.toString());
        }
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement dropdownWebElement;
        try {
            //dropdownWebElement = Utilities.waitForVisibility(dropdownBy, 15, "Utilities.processDropdown()");
            // Next line is new 4/30/19
            dropdownWebElement = Utilities.waitForRefreshedVisibility(dropdownBy, 15, "Utilities.processDropdown()");
        } catch (Exception e) {
            logger.warning("Utilities.processDropdown(), Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " +Utilities.getMessageFirstLine(e)); ScreenShot.shoot("warningError");
            return null; // Fails:16
        }
        Select select = new Select(dropdownWebElement);
        WebElement optionSelected = select.getFirstSelectedOption();
        String currentValue = optionSelected.getText().trim();
        if (!currentValue.isEmpty()) {
            hasCurrentValue = true;
            if (currentValue.contains("Select")) {
                hasCurrentValue = false;
                currentValue = "";
            } else if (currentValue.contains("4XX.XX")) {
                hasCurrentValue = false;
                currentValue = "";
            } else if (currentValue.contains("USA") && (value == null || value.isEmpty())) {
                hasCurrentValue = false;
                currentValue = "";
            } else if (currentValue.contains("Enter BH Note Type")) {
                hasCurrentValue = false;
                currentValue = "";
            } else if (currentValue.contains("Initial Visit") && (value == null || value.isEmpty())) {
                hasCurrentValue = false;
                currentValue = "";
            }
        }
        else {
            currentValue = null;
        }
        if (valueIsSpecified) {
            overwrite = true;
        } else if (hasCurrentValue) {
            overwrite = false;
        } else {
            overwrite = required; // not sure.  Maybe used to be else if (!required && (sectionIsRandom == null || !sectionIsRandom)) overwrite = false else overwrite = true
        }
        if (!overwrite) {
            if (currentValue == null || currentValue.isEmpty()) {
                return value;
            }
            return currentValue;
        }
        //
        // Write the value if there is one, or if "random" specified, otherwise write a value if the
        // field is required.
        //
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = Utilities.getRandomDropdownOptionString(dropdownBy);
                Utilities.selectDropdownOption(dropdownBy, value);
                if (value == null) {
                    value = currentValue;
                }

            } else {
                Utilities.selectDropdownOption(dropdownBy, value);
            }
        } else {
            value = Utilities.getRandomDropdownOptionString(dropdownBy);
            if (value == null || value.isEmpty()) {
                logger.fine("For some reason getRandomDropdownOptionString return null or an empty string.");
                //return null;
                return value;
            }
            // Even though we just got a random value from the dropdown, we have to still have to make sure it's selected.
            Utilities.selectDropdownOption(dropdownBy, value);
            if (Arguments.verbose) {
                System.out.println("      **Random dropdown value generated: " + value);
            }
        }
        if (Arguments.pauseDropdown > 0) {
            Utilities.sleep(Arguments.pauseDropdown * 1000, "Utilities");
        }
        return value;
    }

    /**
     * This method processes a date field, either with a specific date, or a random date.  This method needs to be reviewed
     * because it has evolved without much review, and it takes time to sort this out.
     *
     * Here are notes that show things have changed and are not current:
     *
     * A date may be specified as a field value, or it may come from the command line, or a properties file or
     * from the PatientsJson file.  But this method is called from methods that already know if the value
     * was specified or not.  The user can't specify a range.
     *
     * If it's "random" what date should be created?  There should be some range specified,
     * like "between 1950 and 2000".  In the JSON file rather than "random" it could be "random 1950-2000".  But
     * maybe the JSON file (or command line) date just says "random", or "now".
     * Let's assume the JSON file has date possibilities of
     * <missing>, "", "02/04/1954", "random", "now", or "random 1954-2000", or "random 02/04/1954-01/01/2000"
     * If it's <missing> or "", and required, then a date must be generated, and it will be today.
     * If it's <missing> or "", and not required, then skip it.
     * If it's "now" or "random", then specify today's date.
     * If it's "random 1950-2000", generate a date between those years, inclusive.
     * If it's "random 02/04/1954-01/01/2000", generate a date between those dates, inclusive.
     *
     * Java's LocalDate has parsing.  Does it support Period?  Can it convert to
     * How about where d1 and d2 are LocalDate, (copied from stackoverflow)...
     * int days = Days.daysBetween(d1, d2).toDays();
     * LocalDate randomDate = d1.addDays(ThreadLocalRandom.nextInt(days+1));
     * @param by the field where the date will be entered
     * @param value a date value, or "random", or "now" or "random <range>"
     * @param sectionIsRandom whether or not the section is marked to be randomized
     * @param required whether the date field is required to be filled in or not
     * @return the date string that was written into the field
     */
    public static String processDate(By by, String value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processDate()");
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page or not
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by)); // could convert to shorter
        } catch (Exception e) {
            logger.warning("Utilities.processDate(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("warningError");
            return null;
        }
        String currentValue = webElement.getAttribute("value").trim();
        if (!currentValue.isEmpty()) {
            hasCurrentValue = true;
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else {
            overwrite = required;// not sure.  Maybe used to be else if (!required && (sectionIsRandom == null || !sectionIsRandom)) overwrite = false else overwrite = true
        }
        if (!overwrite) {
            if (currentValue.isEmpty()) {
                return value;
            }
            return currentValue;
        }
        //
        // Enter a date value into the field, generating a random one, possibly within limits, if necessary.
        //
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDate();
                value = Utilities.fillInTextField(by, value);
                if (value == null) {
                    value = currentValue;
                }
            } else if (value.startsWith("random")) { // "random <lower-upper>"
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];
                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                value = Utilities.fillInTextField(by, value);
                if (Arguments.verbose) {
                    System.out.println("      **Random date value generated: " + value);
                }
            } else {
                value = Utilities.fillInTextField(by, value);
                if (value == null) {
                    logger.fine("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null;
                }
            }
        } else {
            if (required) { // IDE says always true, but Boolean value false can be passed in.
                value = getCurrentDate();
                String tempValue = Utilities.fillInTextField(by, value);
                if (tempValue == null) {
                    logger.fine("Utilities.processDate(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getCurrentDate();
                    value = Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random date value generated: " + value);
            }
        }
        if (Arguments.pauseDate > 0) {
            Utilities.sleep(Arguments.pauseDate * 1000, "Utilities");
        }
        return value;
    }

    /**
     * This method processes a date/time field.  So it adds time to the date.
     * This should be looked at closer, and probably use processDate().
     * @param dateTimeFieldBy
     * @param value
     * @param sectionIsRandom
     * @param required
     * @return
     */
    public static String processDateTime(By dateTimeFieldBy, String value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(dateTimeFieldBy + "\tUtilities.processDateTime()");
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(dateTimeFieldBy));
        } catch (Exception e) {
            logger.warning("Utilities.processDateTime(), Did not get webElement specified by " + dateTimeFieldBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("warningError");
            return null; // should we throw the exception?  Getting all kinds of db connection problems.
        }
        String currentValue = webElement.getAttribute("value").trim();

//        if (currentValue != null && !currentValue.isEmpty()) {
//            hasCurrentValue = true;
//            if (currentValue.isEmpty()) {
//                hasCurrentValue = false;
//            }
//        }
        if (!currentValue.isEmpty()) {
            hasCurrentValue = true;
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            if (currentValue.isEmpty()) {
                return null;
            }
            return currentValue;
        }
        //
        // Enter a date-time value into the field, generating a random one, possibly within limits, if necessary.
        //
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDateTime();
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

            } else if (value.startsWith("random")) { // "random <lower-upper>"
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];
                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                String time = getRandomTime();
                value = Utilities.fillInTextField(dateTimeFieldBy, value + " " + time);
            } else {
                Utilities.sleep(1555, "Utilities"); // following often fails.  Pause helps?
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (value == null) {
                    logger.fine("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null;
                }
            }
        } else {
            if (required) { // Not sure this is always true.  IDE error diagnosis?
                value = getCurrentDateTime();
                String tempValue = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (tempValue == null) {
                    logger.fine("Utilities.processDateTime(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getCurrentDateTime();
                    value = Utilities.fillInTextField(dateTimeFieldBy, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random date/time value generated: " + value); // new 12/27/18
            }
        }
        //(new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // removing 4/18/19  Is this because of a different selenium version??????
        if (Arguments.pauseDate > 0) {
            Utilities.sleep(Arguments.pauseDate * 1000, "Utilities");
        }
        return value;
    }

    /**
     * Enter an integer value at the location specified.  If the value is meant to be a random value, make it within the min/max values
     * @param by the location
     * @param value an integer value (as a string), or the word "random", or "", or null, meaning random.
     * @param minValue min value if generated randomly
     * @param maxValue max value if generated randomly
     * @param sectionIsRandom boolean value indicating whether this section is to be generated randomly
     * @param required boolean value if the value is required or not.
     * @return the string representing the integer input into the field
     */
    public static String processIntegerNumber(By by, String value, int minValue, int maxValue, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processIntegerNumber()");
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page or not
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.warning("Utilities.processIntegerNumber(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        String currentValue = webElement.getAttribute("value").trim();

        if (currentValue != null && !currentValue.isEmpty()) {
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            if (currentValue.isEmpty()) {
                return null;
            }
            return currentValue;
        }
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
                if (value == null) {
                    value = currentValue;
                }

            } else {
                Utilities.fillInTextField(by, value);
            }
        } else {
            if (required) {
                int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                    value = String.valueOf(intValue);
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random integer generated: " + value);
            }
        }
        return value;
    }


    // Hey this is for a special kind of string of digits, like maybe SSN and not a real number, so watch out.  Use processIntegerNumber?

    /**
     * This method stuffs a string of digits into a text field.  If random, then the length will be within a range,
     * and start with a twin number, like 22 or 33.
     * @param by
     * @param value
     * @param minDigits
     * @param maxDigits
     * @param sectionIsRandom
     * @param required
     * @return
     */
    public static String processStringOfDigits(By by, String value, int minDigits, int maxDigits, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processStringOfDigits()");
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page or not
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.warning("Utilities.processStringOfDigits(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        String currentValue = webElement.getAttribute("value").trim();

        if (!currentValue.isEmpty()) {
            hasCurrentValue = true;
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else {
            overwrite = required;// not sure.  Maybe used to be else if (!required && (sectionIsRandom == null || !sectionIsRandom)) overwrite = false else overwrite = true
        }
        if (!overwrite) {
            if (currentValue.isEmpty()) {
                return null;
            }
            return currentValue;
        }
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
                if (value == null) {
                    value = currentValue;
                }

            } else {
                Utilities.fillInTextField(by, value);
            }
        } else {
            if (required) {
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getRandomTwinNumber(minDigits, maxDigits);
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random digits generated: " + value);
            }
        }
        return value;
    }

    /**
     * Enter a value into a field requiring a double number.
     * @param by The locator for the element
     * @param value The value to enter.  Numeric or string "random".
     * @param minValue The minimum value, inclusive for a random value
     * @param maxValue The maximum value, inclusive for a random value
     * @param sectionIsRandom If the section is marked random
     * @param required if the field requires a value
     * @return the resulting double value, as a string
     */
    public static String processDoubleNumber(By by, String value, double minValue, double maxValue, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processDoubleNumber()");
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page.
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.warning("Utilities.processDoubleNumber(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        String currentValue;
        currentValue = webElement.getAttribute("value").trim();
        if (currentValue != null && !currentValue.isEmpty()) {
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            if (currentValue.isEmpty()) {
                return null;
            }
            return currentValue;
        }
        //
        // Enter the value, generating one if necessary within specified min/max values
        //
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                double randomValue = random.nextDouble();
                double range = maxValue - minValue;
                value = String.format("%.2f", (minValue + (range * randomValue)));
                if (value == null) {
                    value = currentValue;
                }
                Utilities.fillInTextField(by, value);
            } else {
                Utilities.fillInTextField(by, value);
            }
        } else {
            if (required) {
                double range = maxValue - minValue;
                double randomValue = random.nextDouble();
                value = String.format("%.2f", (minValue + (range * randomValue)));
                Utilities.fillInTextField(by, value);
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    double range = maxValue - minValue;
                    double randomValue = random.nextDouble();
                    value = String.format("%.2f", (minValue + (range * randomValue)));
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random double number generated: " + value);
            }
        }
        return value;
    }

    /**
     * Select a radio button from a set, specified by the label locator associated with the button,
     * rather than by a button locator.  Some radios are easier to select by label and others by button.
     * This method calls a "doRadioButtonByLabel" to help separate the work.
     * Some radio button labels values can be a truncation of the actual label, if there is a dash " - " in the label.
     * For example "No - Please explain in comments" can be represented in the input file as just "No"
     * @param value The string value matching the radio button desired
     * @param sectionIsRandom whether the section is marked random
     * @param required whether one of the radio buttons is required to be selected
     * @param radiosByLabels a list of radio button label locators
     * @return a string matching the radio button label selected
     */
    public static String processRadiosByLabel(String value, Boolean sectionIsRandom, Boolean required, By... radiosByLabels) {
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomRadioLabel(radiosByLabels);
                value = doRadioButtonByLabel(value, radiosByLabels);
                logger.finest("Utilities.processRadiosByLabel(), value is " + value);
            } else {
                value = doRadioButtonByLabel(value, radiosByLabels);
            }
        } else {
            if (required) {
                value = getRandomRadioLabel(radiosByLabels);
                value = doRadioButtonByLabel(value, radiosByLabels);
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getRandomRadioLabel(radiosByLabels);
                    value = doRadioButtonByLabel(value, radiosByLabels);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random radio generated: " + value);
            }
        }
        if (Arguments.pauseRadio > 0) {
            Utilities.sleep(Arguments.pauseRadio * 1000, "Utilities");
        }
        return value;
    }


    /**
     * Select a radio button from a set, specified by the button label, but with locators for buttons instead of labels.
     * Some radios are easier to select by label and others by button.  This method calls a "doRadioButtonByButton"
     * to help separate the work.
     *
     * You need both the <input> element so you can show the click, and the <label> element so you know what to match.
     * And you need to get the whole list of radio elements in the set. And we cannot be certain what the grouping
     * of elements is.
     *
     * In one case a <td> has a set of <span> elements, and each <span> has one input element and one label element.
     * In other cases a <td> has a set of input/label pairs.  So in the former you can't do a "parent" to get the
     * entire set (which is what this assumes, I think).  And you cannot assume that a label has only one word,
     * which is also what this currently assumes.  So, until there's consistency in how radios are organized on
     * a page, there has to be at least two ways to handle them.
     *
     * THIS ASSUMES RADIOS ARE ALWAYS A SET of <input>:<label> PAIRS. If the button has a unique ID it MAY be more
     * solid than doing an xpath.  The ID is associated with an <input> and isn't the label, so you have to find
     * the matching label and its label text to get the match.  But the <label> requires a xpath
     * @param value The string value matching the specified radio button's label
     * @param sectionIsRandom whether the section is marked random
     * @param required whether one of the radio buttons is required to be selected
     * @param radiosByButtons a list of radio button locators
     * @return a string matching the radio button label selected
     */
    public static String processRadiosByButton(String value, Boolean sectionIsRandom, Boolean required, By... radiosByButtons) {
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = doRadioButtonByButton(value, radiosByButtons);
            } else { // value is not "random"
                value = doRadioButtonByButton(value, radiosByButtons);
            }
        } else {
            if (required) {
                value = doRadioButtonByButton(value, radiosByButtons);
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = doRadioButtonByButton(value, radiosByButtons);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random radio value generated: " + value);
            }
        }
        if (Arguments.pauseRadio > 0) {
            Utilities.sleep(Arguments.pauseRadio * 1000, "Utilities");
        }
        return value;
    }
    /**
     * Find the matching radio button, by label, and click on that label, which acts like clicking on the button.
     * @param value the text of a radio button label to match so the button can be selected
     * @param radioLabels a list of locators for the radio labels
     * @return the full radio label text
     */
    private static String doRadioButtonByLabel(String value, By... radioLabels) {
        for (By radioLabel : radioLabels) {
            if (pep.Main.catchBys) System.out.println(radioLabel.toString() + "\tUtilities.doRadioButtonByLabel()");
            try {
                WebElement radioElement = Utilities.waitForPresence(radioLabel, 4, "Utilities.doRadioButtonByLabel()");
                // You can't do the following if the DOM structure doesn't have a label inside the input element.
                // For example, at least previously, laterality of PNB in SPNB in ProcedureNotes.
                String radioLabelText = radioElement.getText();
                // Compare all the words, even though that makes the user type more than one word if there is, like "PENDING TRANSFER".
                // Otherwise that would assume radio labels in a set are unique in first word, which they aren't, like "PENDING EVAC".
                // Exception case: you can type in "No", when the option is "No - explain".  We'll stop comparing at the " - ".
                if (radioLabelText != null) {
                    int radioLabelTextLength = radioLabelText.length(); // test stuff
                    int nCharsToMatch = radioLabelText.indexOf(" - ");
                    if (nCharsToMatch == -1) {
                        nCharsToMatch = radioLabelTextLength;
                    }
                    if (radioLabelText.regionMatches(true, 0, value, 0, nCharsToMatch)) {
                        // The following assumes you can click on labels and the associated button will show the click.
                        // Not true if duplicate ID's, as in some cases like values in @for attribute
                        radioElement.click();
                        return radioLabelText;
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                logger.fine("Utilities.doRadioButtonByLabel(), didn't get radioElement, or its text: " + Utilities.getMessageFirstLine(e));
            }
        }
        return null; // will probably be interpreted as failure, up the chain.  Check.
    }


    /**
     * Find the matching radio button, and click on it. The way this is attempted is prone to error.
     * Selenium does not support text nodes and so you cannot easily get to the text label following
     * a radio button when there is no accompanying <label> node.  With <label> you can get the
     * Selenium WebElement and do a getText() to get the label text, but since that doesn't exist,
     * perhaps it's possible to get a button's parent node and then go a getText(), and parse the
     * results.
     * This method has bad logic and should be rewritten.
     * @param value the text of a radio button label to match so the button can be selected
     * @param radios a list of locators for the radio buttons
     * @return the full radio label text
     */
    private static String doRadioButtonByButton(String value, By... radios) {
        try {
            int nRadios = radios.length;
            if (value == null || value.equalsIgnoreCase("random") || value.isEmpty()) {
                int randomIndex = Utilities.random.nextInt(nRadios);
                WebElement matchingRadioElement = Utilities.waitForPresence(radios[randomIndex], 4, "classMethod");
                //
                // Get the matching label by getting the parent of the button and assuming the parent
                // has children organized a certain way.  This can cause problems. For example,
                // the one in Amputation Cause has a set of spans.
                //
                WebElement parentElement = matchingRadioElement.findElement(By.xpath("parent::*"));
                String labelsString = parentElement.getText();
                String[] labels;
                String newValue;
                if (labelsString != null && !labelsString.isEmpty()) {
                    labels = labelsString.split(" "); // Assumes labels can only be one word, which is wrong
                    newValue = labels[randomIndex];
                } else {
                    logger.fine("Something assumed about radio labels that isn't true.  like an association of button with label that is clearly defined for all. " + labelsString);
                    logger.fine("And parent is " +  parentElement);
                    return null;
                }
                matchingRadioElement.click();
                return newValue;
            }
            // the following expects radio labels are a single word, and that they are all organized under a single parent
            WebElement firstRadioElement = Utilities.waitForPresence(radios[0], 4, "classMethod");
            WebElement parentElement = firstRadioElement.findElement(By.xpath("parent::*"));
            String labelsString = parentElement.getText();
            String[] labels;
            if (labelsString != null && !labelsString.isEmpty()) {
                labels = labelsString.split(" ");
            } else {
                logger.fine("Something assumed about radio labels that isn't true.  What? labelsString: " + labelsString);
                logger.fine("And parentElement: " + parentElement);
                return null;
            }
            for (int labelCtr = 0; labelCtr < labels.length; labelCtr++) {
                String label = labels[labelCtr];
                if (label.trim().equalsIgnoreCase(value)) {
                    if (pep.Main.catchBys) System.out.println(radios[labelCtr].toString() + "\tUtilities.doRadioButtonByButton()");
                    WebElement matchingRadioElement = Utilities.waitForPresence(radios[labelCtr], 4, "classMethod");
                    matchingRadioElement.click();
                    return label;
                }
            }
        } catch (Exception e) {
            logger.warning("Utilities.doRadioButtonByButton(), couldn't do radio button" + Utilities.getMessageFirstLine(e));
            return null;
        }
        return null;
    }
    /**
     * Process a text field by entering the specified value into it, or a random value.  The kind of random value depends
     * on the TextFieldType, which might be a name, or address, or title, or relationship, or several other things.
     * Warning: We don't want to return from this method until the element is finished somehow, because
     * sometimes the server will process the text somehow, and if return too soon things get messed up all over.
     * So whatever method calls this needs to watch for this, until it can be figured out what's going on.
     * @param textFieldBy The locator for the text field element
     * @param value The value to enter into the field, or the word "random"
     * @param textFieldType If random value is to be generated, then this says what kind is needed.
     * @param sectionIsRandom Whether the section's text elements need to be filled with random values
     * @param required Whether the field/element needs a value for the page to be saved
     * @return The text value entered
     */
    public static String processText(By textFieldBy, String value, TextFieldType textFieldType, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(textFieldBy.toString() + "\tUtilities.processText()");

        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());
        //
        // Establish whether to overwrite existing value for this element on the page or not
        //
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 5)).until(visibilityOfElementLocated(textFieldBy));
        } catch (Exception e) {
            logger.warning("Utilities.processText(), Did not get webElement specified by " + textFieldBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return value;
        }
        String currentValue = webElement.getAttribute("value").trim();
        //if (currentValue != null && !currentValue.isEmpty()) { // supposedly currentValue will never be null
        if (!currentValue.isEmpty()) {
            hasCurrentValue = true;
        }
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (hasCurrentValue) {
            overwrite = false;
        }
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            //If field is optional, and no value is specified, and no value is in the element,
            // do we want the output JSON file to show the field and have it be blank, or not?  I think not.
            // Perhaps not putting the field into the output JSON is better than putting it in with a blank value.
            if (currentValue.isEmpty()) {
                return value;
            }
            return currentValue;
        }
        //
        // Fill in the field/element with a value, if specified, or if required.
        //
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = genRandomValueText(textFieldType);
                if (value == null) {
                    value = currentValue;
                }
                Utilities.fillInTextField(textFieldBy, value);

            } else {
                Utilities.fillInTextField(textFieldBy, value);
            }
        } else {
            if (required) {
                value = genRandomValueText(textFieldType);
                Utilities.fillInTextField(textFieldBy, value);
                if (Arguments.verbose) {
                    System.out.println("      **Random text value generated: " + value);
                }
            }
        }
        if (Arguments.pauseText > 0) {
            Utilities.sleep(Arguments.pauseText * 1000, "Utilities");
        }
        return value;
    }

    /**
     * This is for checkboxes (which are toggles) which means if the box is already checked you don't
     * want to click it again to try to set it.  Rather than clear first, essentially do the XOR.
     * In the JSON file a Boolean can be true, false, null, or just missing, which is null.
     * If missing, it's null.  Then how do you specify "random"?  You can't.  We could make the
     * assumption that if it's null it means "random".  Oh, but that's the same as other fields
     * too.  Okay.
     *
     * @param by
     * @param value
     * @param sectionIsRandom
     * @param required
     * @return the value of the checkbox.
     */
    public static Boolean processBoolean(By by, Boolean value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processBoolean()");
        if (sectionIsRandom != null && sectionIsRandom && !required) {
            required = true;
        }
        boolean valueIsSpecified = (value != null);
        //
        // Establish whether to overwrite existing checkbox on the page or not.
        // You can't tell by looking at the element whether or not it has a value.
        // In a sense it always has a value, because no check means false.
        //
        boolean overwrite;
        if (valueIsSpecified) {
            overwrite = true;
        }
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true;
        }
        if (!overwrite) {
            //if (value == null) System.out.println("IDE: I told you so, it's null.");
            return value; // IDE says always null, but I don't believe it.  Seems to be true though
        }
        //
        // Now fill in the field/element with a value.
        //
        if (valueIsSpecified) {
            WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
            WebElement checkBoxWebElement = wait.until(visibilityOfElementLocated(by));
            if (checkBoxWebElement != null) {
                boolean isChecked = checkBoxWebElement.isSelected();
                if (value != isChecked) {
                    checkBoxWebElement.click(); // check for exception?
                }
            }
        } else {
            if (required) {
                value = random.nextBoolean();
                WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
                WebElement checkBoxWebElement = wait.until(visibilityOfElementLocated(by));
                if (checkBoxWebElement != null) {
                    boolean isChecked = checkBoxWebElement.isSelected();
                    if (value != isChecked) {
                        checkBoxWebElement.click();
                    }
                }
                if (Arguments.verbose) {
                    System.out.println("      **Random checkbox value generated: " + value);
                }
            } else {
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = random.nextBoolean();
                    WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
                    WebElement checkBoxWebElement = wait.until(visibilityOfElementLocated(by));
                    if (checkBoxWebElement != null) {
                        boolean isChecked = checkBoxWebElement.isSelected();
                        if (value != isChecked) {
                            checkBoxWebElement.click();
                        }
                    }
                }
            }
        }
        if (Arguments.pauseCheckbox > 0) {
            Utilities.sleep(Arguments.pauseCheckbox * 1000, "Utilities");
        }
        return value;
    }

//    public static String getCurrentTextValue(By by) {
//        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentTextValue()");
//        try {
//            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
//            String currentValue = textField.getText();
//            return currentValue;
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }
//
//    // wrong
//    public static String getCurrentDropdownValue(By by) {
//        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentDropdownValue()");
//// probably want to wrap this with an explicit wait and try
//        try {
//            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
//            String currentValue = textField.getText();
//            return currentValue;
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }
//
//    // wrong
//    public static String getCurrentRadioValue(By by) {
//        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentRadioValue()");
//// probably want to wrap this with an explicit wait and try
//        try {
//            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
//            String currentValue = textField.getText();
//            return currentValue;
//        }
//        catch (Exception e) {
//            return null;
//        }
//    }


    /**
     * This is a support method to get a random radio label from a list of radio labels.
     * @param radioLabelByList A list of locators for radio button labels
     * @return the randomly selected label from the list of radios
     */
    private static String getRandomRadioLabel(By... radioLabelByList) {
        int nRadioLabelBys = radioLabelByList.length;
        int randomRadioLabelIndex = Utilities.random.nextInt(nRadioLabelBys);
        try {
            By radioLabelBy = radioLabelByList[randomRadioLabelIndex];
            if (pep.Main.catchBys) System.out.println(radioLabelBy.toString() + "\tUtilities.getRandomRadioLabel()");
            WebElement radioLabelElement = Utilities.waitForPresence(radioLabelBy, 2, "Utilities.getRandomRadioLabel()");
            // Baseline radio buttons, and Referral, comes back with "", "Unknown" has text ""
            String radioLabelText = radioLabelElement.getText();
            if (radioLabelText.isEmpty()) {  // "Baseline", "Referral", "Unknown" radios may come back with ""
                logger.fine("Utilities.getRandomRadioLabel(), selected radio " + radioLabelBy.toString() + " but corresponding label is blank, so how about returning 'Yes'?");
                radioLabelText = "Yes"; // hack for now
            }
            return radioLabelText;
        } catch (Exception e) {
            logger.warning("Utilities.getRandomRadioLabel(), couldn't get radio element " + randomRadioLabelIndex + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
    }



    // this method waits 10 sec max to find a clickable element.  If this method isn't used it's because there are other things to consider when clicking something.

    /**
     * Click the button identified by the locator.
     * @param button the locator for the button
     */
    public static void clickButton(final By button) {
        if (Main.catchBys) System.out.println(button.toString() + "\tUtilities.clickButton()");
        try {
            WebElement buttonElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(button));
            buttonElement.click();

        } catch (Exception e) {
            logger.warning("Utilities.clickButton(), didn't get button to click, or couldn't click it: " + Utilities.getMessageFirstLine(e));
        }
    }

    /**
     * Get the value of the checkbox identified by the locator.  Looks like there's only one caller of this method/
     * @param locator the checkbox locator
     * @return whether the value is selected
     */
    public static boolean getCheckboxValue(final By locator) {
        if ( Main.catchBys) System.out.println(locator.toString() + "\tUtilities.getCheckboxValue()");
        final WebDriver driver = Driver.driver;
        WebElement checkbox = driver.findElement(locator);
        boolean isSelected = checkbox.isSelected();
        return isSelected;
    }

//    /**
//     *
//     */
//    public static void clickAlertAccept() {
//        final WebDriver driver = Driver.driver;
//        Alert possibleAlert = driver.switchTo().alert();
//        if (possibleAlert == null) {
//            logoutFromTmds(); // what?
//        }
//        (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver d) {
//                try {
//                    Alert possibleAlert = driver.switchTo().alert();
//                    if (possibleAlert == null) {
//                        logger.fine("\tAlertAccept not available");
//                        return false;
//                    }
//                    if (possibleAlert.getText().length() < 1) {
//                        logger.warning("\tIn Utilities.clickAlertAccept(), No text for this alert");
//                        return false;
//                    }
//                } catch (Exception e) {
//                    logger.warning("\tIn Utilities.clickAlertAccept(), AlertAccept still not available.  Exception caught: " + Utilities.getMessageFirstLine(e));
//                    return false;
//                }
//                return true;
//            }
//        });
//        try {
//            possibleAlert.accept(); // This can allow a "Concurrent Login Attempt Detected" page to appear because the login gets processed
//        } catch (Exception e) {
//            logger.warning("\tIn Utilities.clickAlertAccept(), Could not accept the alertAccept.  Exception caught: " + Utilities.getMessageFirstLine(e));
//            return;
//        }
//        return;
//    }

    /**
     * Get one or more random paragraphs.  This is Lorem stuff, so improve it later to make it more specific to its purpose.
     * @param min the minimum number of paragraphs to return
     * @param max the maximum number of paragraphs to return
     * @return the paragraphs, as a single string
     */
    private static String getRandomParagraphs(int min, int max) {
        return lorem.getParagraphs(min, max);
    }

    /**
     * Get one or more random words.  This is Lorem stuff, so improve it later to make it more specific to its purpose.
     * @param min the minimum number of words to return
     * @param max the maximum number of words to return
     * @return the words, as a string.
     */
    private static String getRandomWords(int min, int max) {
        return lorem.getWords(min, max);
    }



    /**
     * This method is called a lot, and it fails a lot due to timing issues.
     * It's also possible that the field is not writable.  Marked readonly.
     * @param field the text field locator
     * @param text the text to input
     * @return the text that got input
     */
    public static String fillInTextField(final By field, String text) {
        if (pep.Main.catchBys) System.out.println(field.toString() + "\tUtilities.fillInTextField()");
        if (text == null || text.isEmpty()) {
            return null;
        }
        WebElement element;
        try {
            element = Utilities.waitForRefreshedPresence(field,  10, "Utilities.fillInTextField()");
            String readonlyAttribute = element.getAttribute("readonly");
            // if readonly, don't bother trying to change the value
            if (readonlyAttribute != null) {
                if (readonlyAttribute.equalsIgnoreCase("true")) {
                    logger.fine("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
                if (readonlyAttribute.equalsIgnoreCase("readonly")) {
                    logger.fine("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
            }
        } catch (StaleElementReferenceException e) {
           logger.warning("Utilities.fillInTextField(), Stale Element Reference.  Could not get element: " + Utilities.getMessageFirstLine(e));
            return null;
        } catch (Exception e) {
            logger.warning("Utilities.fillInTextField(), could not get element: " + field.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }

        if (element == null) { // never happens, according to IDE, which is prob right
            System.out.println("How do we get a null element if there was no exception caught?");
            try {
                logger.finer("Let's try again...");
                element = (new WebDriverWait(Driver.driver, 10))
                        .until(
                                ExpectedConditions.presenceOfElementLocated(field));
            }
            catch (Exception e) {
                logger.finer("2nd try didn't work either");
                return null;
            }
        }
        try {
            element.clear();
        } catch (InvalidElementStateException e) {
            logger.warning("Utilities.fillInTextField(), Invalid Element State.  Could not clear element:, " + element + " Oh well.  Continuing");
        } catch (StaleElementReferenceException e) {
            logger.warning("Utilities.fillInTextField(), Stale Element References.  Could not clear element:, " + element + " Oh well.  Continuing.  Exception: " + Utilities.getMessageFirstLine(e));
        } catch (Exception e) {
            logger.fine("Utilities.fillInTextField(), could not clear element:, " + element + " Oh well.  Continuing.  Exception: " + Utilities.getMessageFirstLine(e));
        }
        try {
            element = Utilities.waitForRefreshedVisibility(field, 10, "Utilities.fillInTextField(), text: " + text + " field: " + field.toString());
//            element = (new WebDriverWait(Driver.driver, 10))
//                    .until(ExpectedConditions.refreshed(
//                            visibilityOfElementLocated(field)));
            element.sendKeys(text); // prob here "element is not attached to the page document"
        } catch (TimeoutException e) {
            logger.warning("Utilities.fillInTextField(), could not sendKeys " + text + " to it. Timed out.  e: " + Utilities.getMessageFirstLine(e));
            return null;
        } catch (StaleElementReferenceException e) {
            logger.warning("Utilities.fillInTextField(), Stale ref.  Could not sendKeys " + text + " to it. e: " + Utilities.getMessageFirstLine(e));
            return null;
        } catch (Exception e) {
            logger.warning("Utilities.fillInTextField(), could not do a sendKeys " + text + " to it. Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        return text;
    }

    /**
     * Get and return a random dropdown option string for the specified dropdown
     * @param dropdownBy the locator for a dropdown
     * @return the randomly chosen dropdown option string
     */
    private static String getRandomDropdownOptionString(final By dropdownBy) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.getRandomDropdownOptionString()");

        WebElement dropdownWebElement;
        try {
            // Crucial difference here between presenceOfElementLocated and visibilityOfElementLocated.
            // For TBI Assessment Note, must have visibilityOfElementLocated
            // why is this next line really slow for Arrival/Location?
            dropdownWebElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(dropdownBy));
        } catch (Exception e) {
            logger.warning("Utilities.getRandomDropdownOptionString(), Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        Select select = new Select(dropdownWebElement); // fails here for originating camp, and other things
        List<WebElement> optionElements = select.getOptions(); // strange it can get the options, but they are all stale?
        int size = optionElements.size();
        if (size < 2) {
            logger.warning("This dropdown " + dropdownBy.toString() + " has no options.  Returning null");
            return null; // try again?
        }
        int randomOptionIndex = Utilities.random.nextInt(size);
        // Some dropdowns start with 0, but most do not. Flawed.  Doesn't work for icd code set for example.
        randomOptionIndex = (randomOptionIndex == 0) ? 1 : randomOptionIndex;
        WebElement option;
        try {
            option = optionElements.get(randomOptionIndex); // optionElements is a list based on first is 0
        } catch (StaleElementReferenceException e) {
            logger.fine("Hmmm, stale element they say.  Must be optionElements");
            return null;
        } catch (Exception e) {
            logger.warning("In Utilities.getRandomDropdownOptionString(), size: " + size + " driverUrl " + dropdownBy.toString());
            logger.fine("Exception caught in selecting dropdown option: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            String optionString = option.getText();
            return optionString;
        } catch (Exception e) {
            logger.fine("Why the crap can't I getText() from the option?");
        }
        return null;
    }

    /**
     * Select an option from a dropdown element.
     * @param dropdownBy the locator for the dropdown element
     * @param optionString a string to select the option.
     * @return the string representing the selection option of the dropdown
     */
    private static String selectDropdownOption(final By dropdownBy, String optionString) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.selectDropdownOption()");
        WebElement dropdownElement;
        try {
            dropdownElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(visibilityOfElementLocated(dropdownBy)));
        } catch (Exception e) {
            logger.warning("Utilities.selectDropdownOption(), couldn't get dropdown " + dropdownBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            Select select = new Select(dropdownElement);
            select.selectByVisibleText(optionString);
        } catch (StaleElementReferenceException e) {
            logger.fine("Utilities.selectDropdownOption(), Couldn't select option " + optionString + " Stale element reference, not attached to the page");
            return null;
        } catch (NoSuchElementException e) {
            logger.fine("Utilities.selectDropdownOption(), No such element exception: Couldn't select option " + optionString);
            return null;
        }
        catch (Exception e) {
            logger.fine("Utilities.selectDropdownOption(), Couldn't select option " + optionString + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        return optionString;
    }

    /**
     * Get the current date and time and return it in format MM/dd/yyyy HHmm
     * @return the current date in the format MM/dd/yyy HHmm
     */
    private static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HHmm");
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);
        String dateAndTime = dateFormat.format(new Date());
        return dateAndTime;
    }

    /**
     * Get the current date and return it in form MM/dd/yyyy
     * @return the date in format MM/dd/yyyy
     */
    private static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);
        String dateAndTime = dateFormat.format(new Date());
        return dateAndTime;
    }

    /**
     * Get the current hour and minute
     * @return the current hour and minute in format HHmm
     */
    public static String getCurrentHourMinute() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        String dateAndTime = simpleDateFormat.format(new Date());
        return dateAndTime;
    }

    static private final String alphabetUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final String alphabetLower = "abcdefghijklmnopqrstuvwxyz";
    static private final String digits = "0123456789";
//    /**
//     * Return a random upper or lower case letter
//     * @param isUpper
//     * @return
//     */
//    public static char getRandomLetter(boolean isUpper) {
//        if (isUpper) {
//            return alphabetUpper.charAt(random.nextInt(26));
//        }
//        return alphabetLower.charAt(random.nextInt(26));
//    }

    /**
     * A random number of specified length, that starts with a twin digit.
     */
    public static String getRandomTwinNumber(int minNDigits, int maxNDigits) {
        StringBuilder stringBuilder = new StringBuilder(maxNDigits); // names at least 3 char
        int nDigitsWanted = Utilities.random.nextInt(maxNDigits + 1);
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



    /**
     * Produce a random SSN with the last 4 digits representing the day of year.
     *
     * The goal is to be able to look at a SSN and tell the day of the year a patient was created, and find
     * the patient using the last 4 digits, and be pretty sure there won't be duplicate SSN's if the program
     * is run simultaneously by different users or yourself or a process that forks off lots of instances.
     * The last four digits are encoded MMDD.
     *
     * You can't get both precision and range with 5 digits plus 4 for date.  We need at least timing down to the tenth of
     * a second to avoid duplicate SSN's when running multiple PePs at once, and that leaves 4 digits,
     * which is as most 10,000 seconds.  But a day is over 86,000 seconds, and so it would cycle 8 times
     * in a day (every 14.4 minutes).  So, you'd lose sequence comparison, and have a (very small) chance
     * of duplicates.  Instead, if you used a random number, you'd have no sequence, and you'd have a
     * smaller chance of duplicates.  Sequence isn't as important as no duplicates.  So, random 5 digits is
     * still the best decision.
     *
     * @return a ssn in string format with last 4 indicating date
     */
    private static String getRandomSsnLastFourByDate() {
        int randomInt = Utilities.random.nextInt(100000);
        String formattedRandomInt = String.format("%05d", randomInt);
        StringBuilder patternForSsn = new StringBuilder(9);
        patternForSsn.append(formattedRandomInt);
        patternForSsn.append("MMdd");
        DateFormat dateFormat = new SimpleDateFormat(patternForSsn.toString()); // This is just a way for me to more easily find patients by last 4 of SS
        String ssBasedOnDate = dateFormat.format(new Date());
        if (ssBasedOnDate.length() != 9) {
            logger.warning("ssn " + ssBasedOnDate + " not 9 digits !");
        }
        return ssBasedOnDate;
    }

    /**
     * Generate a random US phone number
     * @return the random US phone number as a string with dash separators
     */
    private static String getRandomUsPhoneNumber() {
        //StringBuffer patternForUsPhoneNumber = new StringBuffer(12); // "999-999-9999"
        int areaCode = Utilities.random.nextInt(900) + 100;
        int middleThree = Utilities.random.nextInt(900) + 100;
        int lastFour = Utilities.random.nextInt(10000);
        String phoneNumber = String.format("%03d-%03d-%04d", areaCode, middleThree, lastFour);
        return phoneNumber;
    }

    /**
     * Create a random date between two dates formatted as MM/dd/yyyy
     * @param startDateString start date formatted as MM/dd/yyyy
     * @param endDateString end date formatted as MM/dd/yyyy
     * @return the random date in MM/dd/yyyy
     */
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
     * Create a random date between two year-date boundaries, inclusive, formatted as "mm/dd/yyyy"
     * Currently only being used for a person's birth date.
     * Probably could have done this easier with LocalDate and ChronoUnit, or whatever like getRandomDateBetweenTwoDates()
     *
     * @param minYear minimum year, inclusive
     * @param maxYear maximum year, inclusive
     * @return string representing the random date
     */
    private static String getRandomDate(int minYear, int maxYear) { // check logic
        GregorianCalendar calendar = new GregorianCalendar();

        int year = minYear;
        if (minYear != maxYear) {
            year = minYear + Utilities.random.nextInt(maxYear - minYear);
        }

        calendar.set(calendar.YEAR, year);

        int maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        int dayOfYear = Utilities.random.nextInt(maxDayOfYear + 1);

        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        //
        // Convert the year and day of year to formatted MM/DD/YYYY
        //
        StringBuilder randomDateWithinBounds = new StringBuilder(10);
        randomDateWithinBounds.append(String.format("%02d", calendar.get(Calendar.MONTH) + 1));
        randomDateWithinBounds.append("/");
        randomDateWithinBounds.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        randomDateWithinBounds.append("/");
        randomDateWithinBounds.append(String.format("%02d", calendar.get(Calendar.YEAR))); // %04d ??????
        return randomDateWithinBounds.toString();
    }

    /**
     * Generate a string representing the hour and minute of a day in the form "HHMM".
     * Hour is 0 to 23, and minute is 0-59
     *
     * @return string representing the hour and minute in HHMM format
     */
    private static String getRandomTime() {
        int hours = Utilities.random.nextInt(24);
        int mins = Utilities.random.nextInt(60);
        return String.format("%02d%02d", hours, mins);
    }

    /**
     * Return an exception message limited in length to one line.  Some exceptions, particularly Selenium exceptions
     * are long and multi-lined, which pollutes the output when you want to know the exception message.
     * @param e The exception
     * @return A string representing the shortened exception's message
     */
    public static String getMessageFirstLine(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            return "<no exception message>";
        }
        int indexOfLineEnd = message.indexOf("\n");
        if (indexOfLineEnd > 0) {
            message = message.substring(0, indexOfLineEnd);
        }
        return message;
    }


    /**
     * Wait for an element of the page to be present, though not necessarily visible.
     * @param elementBy The element locator
     * @param secondsToWait The number of seconds to wait for the element to become present
     * @param message The message to display in a log message, for debug purposes
     * @return the WebElement that was returned by waiting for it
     * @throws TimeoutException indicating the element did not become present within the waiting period
     */
    public static WebElement waitForPresence(By elementBy, int secondsToWait, String message) throws TimeoutException {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for presence.");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(presenceOfElementLocated(elementBy));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForPresence() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for an element to be clickable.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static WebElement waitForClickability(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for clickability");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(ExpectedConditions.elementToBeClickable(elementBy));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForClickability() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for an element to be refreshed and then clickable.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static WebElement waitForRefreshedClickability(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for clickability");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(elementBy)));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForClickability() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for an element to become visible.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static WebElement waitForVisibility(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for visibility.");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(visibilityOfElementLocated(elementBy));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForVisibility() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for an element to become invisible.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static void waitForInvisibility(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for invisibility.");
        try {
            (new WebDriverWait(Driver.driver, secondsToWait)).until(ExpectedConditions.invisibilityOfElementLocated(elementBy));
             return;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForInvisibility() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e; // fails:2 unhandled inspector error, whatever that is
        }
    }

    /**
     * Wait for an element to be refreshed and then have a presence, though not necessarily visible.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static WebElement waitForRefreshedPresence(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for refreshed presence.");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(refreshed(ExpectedConditions.presenceOfElementLocated(elementBy)));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForRefreshedPresence() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for an element to be refreshed and then visible.
     * @param elementBy The element locator
     * @param secondsToWait how long to wait
     * @param message the message to display in a log
     * @return the element that was waited for
     */
    public static WebElement waitForRefreshedVisibility(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for refreshed visibility.");
        try {
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(refreshed(ExpectedConditions.visibilityOfElementLocated(elementBy)));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForRefreshedVisibility() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Wait for the element to go stale, up to the number of seconds specified, and output a log message message
     * @param elementBy The element to wait to go stale
     * @param secondsToWait The number of seconds to wait
     * @param message The message for the log
     */
    public static void waitForStaleness(WebElement elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for staleness of element.");
        try {
            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.stalenessOf(elementBy));
        }
        catch (TimeoutException e) {
            logger.fine("Utilities.waitForStaleness() caught timeout exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForStaleness() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    /**
     * Sleep for the requested number of milliseconds, and provide logging info
     * @param millis number of milliseconds to sleep
     * @param comment A comment to help determine where the sleep occurred.
     */
    public static void sleep(int millis, String comment) {
        try {
            Main.timerLogger.fine("sleeping " + millis + " ms * " + Arguments.throttle + " " + comment);
            Thread.sleep((int) (millis * Arguments.throttle));
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * This is an attempt to consolidate the various "isPatientFound" methods used to execute the Search For Patient section and button press.
     * But it appears this is not possible without also adding parameters to locate the elements.  They are not universal, unfortunately.
     * So this method will go unused for now.
     * @param patient
     * @return
     */
    public static boolean isPatientFound(Patient patient) {
        return isPatientFound(patient.patientSearch.ssn, patient.patientSearch.firstName, patient.patientSearch.lastName, patient.patientSearch.traumaRegisterNumber);
    }

    /**
     * This method does a search for the patient.  If found then the patient was previously registered.
     * There are currently 4 different methods with this name.  This was an attempt to consolidate them.  Turns out the By locators are not universal,
     * and would require By values to be passed in to work.  So this method is currently unused.
     *
     * @param ssn of the patient for searching
     * @param firstName of the patient for searching
     * @param lastName of the patient for searching
     * @param traumaRegisterNumber of the patient for searching
     * @return true if patient has been registered, and is found.  false otherwise.
     */
    public static boolean isPatientFound(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        By ssnField = By.id("ssn");
        By lastNameField = By.id("lastName");
        By firstNameField = By.id("firstName");
        By traumaRegisterNumberField = By.id("registerNumber");
        By searchForPatientButton = By.xpath("//button[text()='Search For Patient']");
        By painManagementNoteSearchForPatientMessageLocatorBy = By.id("msg");
        By demographicTableBy = By.id("patient-demographics-container");

        Utilities.sleep(555, "Utilities.isPatientFound(), desperate attempt.  Remove later when have solution.");

        try {
            Utilities.waitForVisibility(ssnField, 3, "PainManagementNote.isPatientFound(), checking to see if on right page.");
            Utilities.waitForClickability(searchForPatientButton, 3, "Summary.process() waiting for clickability which should indicate we can enter values into the fields");
        }
        catch (Exception e) {
            logger.severe("Utilities.isPatientFound(), Couldn't get ssnField, or search button.  Continue on or return false? e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
        }
        try {
            Utilities.fillInTextField(ssnField, ssn);
            Utilities.fillInTextField(lastNameField, lastName);
            Utilities.fillInTextField(firstNameField, firstName);
            Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);
        }
        catch (Exception e) {
            logger.severe("Utilities.isPatientFound(), could not fill in one or more fields.  e: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
         }

        Utilities.sleep(3155, "Utilities.isPatientFound(), sleeping before clicking search button.  Not sure this is nec, except maybe for TbiAssessment use.");
        Utilities.clickButton(searchForPatientButton);
        (new WebDriverWait(Driver.driver, 10)).until(Utilities.isFinishedAjax());

        //
        // Either the patient was found or wasn't.  If we don't get advanced to the
        // page that has the demographic table on it, then it failed, and there's nothing that can be done.
        // If we get a message, like "There are no patients found.", then could report that to the user, but still have
        // to return null.  The only advantage to checking the failure is to return a message.
        //
        try {
            WebElement messageArea = Utilities.waitForVisibility(painManagementNoteSearchForPatientMessageLocatorBy, 3, "PainManagementNote.isPatientFound()");
            String message = messageArea.getText();
            if (!message.isEmpty()) {
                if (message.equalsIgnoreCase("There are no patients found.")) {
                    logger.fine("PainManagementNote.isPatientFound(), message says: " + message);
                    return false;
                }
                if (message.equalsIgnoreCase("There were no records found.")) {
                    logger.fine("PainManagementNote.isPatientFound(), message says: " + message);
                    return false;
                }
                return false;
            }
            else {
                logger.fine("Utilities.isPatientFound(), no message found, so maybe okay to continue.");
            }
        }
        catch (Exception e) {
            logger.fine("Utilities.isPatientFound(), Prob okay???  Couldn't find a message about search, so a patient was probably (???) found.  Will check for more clues firest.");
        }
        //
        // Check if there's a "Patient Demographics" tab or section, and if there is, we're okay.  But it's possible that the search results takes a long time.
        //
        try {
            logger.fine("Utilities.isPatientFound(), now checking if there's a Patient Demographics section in the Pain Management Note.");
            Utilities.waitForVisibility(demographicTableBy, 15, "PainManagementNote.isPatientFound()");
        } catch (Exception e) {
            logger.severe("PainManagementNote.isPatientFound(), didn't find demographic table.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        return true;

    }
}
