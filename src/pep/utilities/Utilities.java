package pep.utilities;

import org.openqa.selenium.NoSuchElementException;
import pep.Main;
import pep.utilities.lorem.Lorem;
import pep.utilities.lorem.LoremIpsum;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static pep.TmdsPortal.logoutFromTmds;

public class Utilities {
    private static Logger logger = Logger.getLogger(Utilities.class.getName());
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
        CPT_CODE, // resolve the CPT_CODES use above
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
                logger.fine("Unexpected text field type: " + textFieldType.toString());
                break;
        }
        return randomValueText;
    }

    // This one uses the Actions class.
    // Let's assume that this is only working with that silly pseudo menu system where there are tabs which when you
    // hover over them they show "submenu" elements, but if you click on it, the submenu disappears, the page changes,
    // and then a half second later the submenu appears again, thus making it impossible to click on the links in
    // the page.  So, we hover over the tab, then move to a submenu option, and then either click, or hover over it
    // and go to the subsubmenu item and click.  We don't click on the links in the page.
    public static boolean myNavigate(By... linksBy) { // no longer working right.  Menu dropdowns don't disappear
        WebElement linkElement = null;
        // Supposedly we can chain these up using Actions
        Actions actions = new Actions(Driver.driver); // this stuff is a temporary hack, until Richard fixes menus
        for (By linkBy : linksBy) {
            if (pep.Main.catchBys) System.out.println(linkBy.toString() + "\tUtilities.myNavigate()");
            logger.finest("Utilities.myNavigate(), looking for linkBy: " + linkBy.toString());
            try { // this sleep stuff really needs to get fixed.
                //linkElement = Driver.driver.findElement(linkBy); // see if this also fails with css selector
                linkElement = Utilities.waitForRefreshedClickability(linkBy, 5, "Utilities.myNavigate(), waiting for " + linkBy.toString()); // new 11/23/18
            } catch (Exception e) {
                logger.severe("Utilities.myNavigate(), Couldn't access link using By: " + linkBy.toString() + "  Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false; // might be okay to return false if user doesn't have access to the nav option
            }
            try {
                //logger.finest("Utilities.myNavigate(), Here comes an actions.moveToElement then build and perform");
                actions.moveToElement(linkElement).build().perform();
            } catch (StaleElementReferenceException e) {
                logger.severe("Utilities.myNavigate(), Stale reference when trying to use linkElement, could not click on linkBy: " + linkBy.toString() + " Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            } catch (Exception e) {
                logger.severe("Utilities.myNavigate(), could not click on linkBy: " + linkBy.toString() + " Exception: " + getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
                return false;
            }
        }
        //logger.finest("Utilities.myNavigate(), Here comes an actions.click.perform");
        actions.click().perform();
        //actions.click().build().perform();
        //logger.fine("Utilities.myNavigate(), succeeded, leaving and returning true.");
        return true;
    }


//    public static void ajaxWait() {
//        while (true) {
//            Boolean ajaxIsComplete = null;
//            try {
//                ajaxIsComplete = (Boolean) ((JavascriptExecutor) Driver.driver).executeScript("return jQuery.active == 0");
//            } catch (Exception e) {
//                System.out.println("Utilities.ajaxWait(), exception caught: " + Utilities.getMessageFirstLine(e));
//            }
//            if (ajaxIsComplete) {
//                break;
//            }
//            Thread.sleep(100);
//        }
//    }

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
                    System.out.println("Utilities.ajaxWait(), exception caught, possibly because no ajax on this page? : " + Utilities.getMessageFirstLine(e));
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
                        logger.fine("--------------Utilities.isFinishedAjax(), No ajax on this page.  Don't know who called.  returning true");
                        return true;// assuming there's no jQuery or ajax on this page.
                    }
                } else {
                    try {
//                        Thread.sleep(5000);
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


    // This method just has problems.  I don't trust the methods it calls.
    // On patient category I get a stale element.  Seems to be related to speed.  So maybe I should add code to handle that kind of thing
    public static String processDropdown(By dropdownBy, String value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.processDropdown()");

//        // EXPERIMENTAL:
//        WebElement dropdownWebElement;
//        try {
//            dropdownWebElement = (new WebDriverWait(Driver.driver, 1)).until(ExpectedConditions.presenceOfElementLocated(dropdownBy));
//            (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.stalenessOf(dropdownWebElement));
//        } catch (Exception e) {
//            logger.severe("Utilities.processDropdown(), Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " +Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
//            return null;
//        }




        // New: Taking position that if section is marked random, then all elements are required to have values.  Good idea?
        //boolean originalRequired = required == true;
        if ((value == null || value.isEmpty()) && required == true) {
            logger.fine("Utilities.processDropdown(), Will generate a dropdown value for element " + dropdownBy.toString());
        }
        // questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) { // test!!!!!!!!!!!!!!!!!!!!!!!
        if (sectionIsRandom != null && sectionIsRandom && !required) { // test!!!!!!!!!!!!!!!!!!!!!!!
           // logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true; // could also set value to "random" to do the same?
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        // This section needs to be revisited.  Logic needs fixing.
        boolean overwrite = false;
        boolean hasCurrentValue = false;
        WebElement dropdownWebElement;
        try {
//            dropdownWebElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(dropdownBy));
            dropdownWebElement = Utilities.waitForVisibility(dropdownBy, 15, "Utilities.processDropdown()"); // okay? // was 30
        } catch (Exception e) {
            logger.severe("Utilities.processDropdown(), Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " +Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return null;
        }
        Select select = new Select(dropdownWebElement); // fails here for originating camp, and other things
        WebElement optionSelected = select.getFirstSelectedOption();
        String currentValue = optionSelected.getText().trim(); // correct, but can be associated with a stale element for Gender for New Patient Reg.
        if (currentValue != null && !currentValue.isEmpty()) {
            hasCurrentValue = true;
            if (currentValue.contains("Select")) { // as in Select Gender, Select Race Select Branch Select Rank Select FMP
                hasCurrentValue = false;
                currentValue = ""; // be careful, new.  I think it should be null, but not sure
            } else if (currentValue.contains("4XX.XX")) { //???
                hasCurrentValue = false;
                currentValue = "";// be careful, new
            } else if (currentValue.contains("USA") && (value == null || value.isEmpty())) { //???
                hasCurrentValue = false;
                currentValue = "";// be careful, new
            } else if (currentValue.contains("Enter BH Note Type")) {
                hasCurrentValue = false;
                currentValue = "";// be careful, new
            } else if (currentValue.contains("Initial Visit") && (value == null || value.isEmpty())) { // new 12/29/18
                hasCurrentValue = false;
                currentValue = "";// be careful, new
            }
        }
        else {
            currentValue = null;// be careful, new
        }
        if (valueIsSpecified) {
            overwrite = true;
        } else if (hasCurrentValue) {
            overwrite = false;
        } else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        } else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
//            //logger.fine("Don't go further because we don't want to overwrite.");
//            return value;
            // I'm really not sure whether we should return null or "".
            // If return null then when the JSON output is generated, no element shows up, and that can be problems for a spreadsheet.
            // If blank, then the JSON gets a "", which means something.  I forget what.  random?  Keep forgetting.
            if (currentValue == null || currentValue.isEmpty()) { // new as of 10/20/18
                //return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
                //return ""; // This is a dangerous change, because I've not researched it.  changed 12/13/18
                return value; // This is a dangerous change, because I've not researched it.  changed 12/13/18
            }
            return currentValue;

        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = Utilities.getRandomDropdownOptionString(dropdownBy);
                Utilities.selectDropdownOption(dropdownBy, value);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

            } else { // value is not "random"
                Utilities.selectDropdownOption(dropdownBy, value); // this may fail when system is slow (ajax wait)
            }
        } else { // value is not specified
            if (required) { // field is required
                value = Utilities.getRandomDropdownOptionString(dropdownBy); // this can fail if there are no options
                if (value == null || value.isEmpty()) { // added isEmpty()
                    logger.fine("For some reason getRandomDropdownOptionString return null or an empty string.");
                    //return null;
                    return value; // dangerous 12/13/18
                }
                // Even though we just got a random value from the dropdown, we have to still have to make sure it's selected.
                Utilities.selectDropdownOption(dropdownBy, value);
                if (Arguments.verbose) {
                    System.out.println("      **Random dropdown value generated: " + value);
                }
            } else { // field is not required
                if (sectionIsRandom != null && sectionIsRandom) { // all this sectionIsRandom stuff could be automatically inherited if set up as classes that extend, like the tree I've drawn
                    value = Utilities.getRandomDropdownOptionString(dropdownBy);
                    if (value != null) { // this is new because now returning null if problem in above.  Not sure at all.
                        Utilities.selectDropdownOption(dropdownBy, value);
                    }
                    if (Arguments.verbose) {
                        System.out.println("      **Random dropdown value generated: " + value);
                    }
                } else { // section is not random
                    logger.fine("In processDropdown(), the field is not required, and sectionIsRandom is " + sectionIsRandom + " so not doing anything with it.");
                }
            }
        }
        if (Arguments.pauseDropdown > 0) {
            Utilities.sleep(Arguments.pauseDropdown * 1000, "Utilities");
        }
        return value;
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
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processDate()");

        // New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
       // if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty()); // what about "random"?

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by)); // could convert to shorter
        } catch (Exception e) {
            logger.severe("Utilities.processDate(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return null;
        }
        //String currentValue = webElement.getText().trim(); // Untested.  Wrong, I think.
        String currentValue = webElement.getAttribute("value").trim(); // InjuryDate comes back ""

        //if (currentValue != null) {
        if (currentValue != null && !currentValue.isEmpty()) { // isEmpty is new, does this screw things up?
            hasCurrentValue = true;
            if (currentValue.isEmpty()) { // this is new, untested
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
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            //return value;
            if (currentValue.isEmpty()) { // new as of 10/20/18
                //return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
                //return ""; // dangerous.  12/13/18.  Maybe return value instead?
                return value; // dangerous.  12/13/18.  Maybe return value instead?
            }
            return currentValue;

        }



        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDate();
                value = Utilities.fillInTextField(by, value); // Possible that this comes back as null if value couldn't be changed because unwritablefield
                if (value == null) { // new 10/26/18
                    value = currentValue;
                }
            } else if (value.startsWith("random")) {
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];

                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                value = Utilities.fillInTextField(by, value);
                if (Arguments.verbose) {
                    System.out.println("      **Random date value generated: " + value); // new 12/27/18
                }
            } else { // value is not "random"
                value = Utilities.fillInTextField(by, value); // wow this thing doesn't return success/failure
                // do we need to check value for null here?
                if (value == null) { // new, added, untested
                    logger.fine("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null; // fails: 8  hey, should this be ""?
                }

            }




        } else { // value is not specified
            if (required) { // field is required
                // logic could be improved
                value = getCurrentDate();
                String tempValue = Utilities.fillInTextField(by, value);
                if (tempValue == null) { // is this nec???????
                    logger.fine("Utilities.processDate(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else { // field is not required, but section may be specified as random, not sure this happens any more though
                // Yes, we can get here
                if (sectionIsRandom != null && sectionIsRandom) { // added extra check for safety, though probably this indicates a fault elsewhere
                    value = getCurrentDate();
                    value = Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random date value generated: " + value); // new 12/27/18   Is it right to do this here?
            }
        }
        if (Arguments.pauseDate > 0) {
            Utilities.sleep(Arguments.pauseDate * 1000, "Utilities");
        }
        return value;
    }

    public static String processDateTime(By dateTimeFieldBy, String value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(dateTimeFieldBy + "\tUtilities.processDateTime()");
// New: Taking position that if section is marked random, then all elements are required to have values
        // questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(dateTimeFieldBy));
        } catch (Exception e) {
            logger.severe("Utilities.processDateTime(), Did not get webElement specified by " + dateTimeFieldBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return null;
        }
        //String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        String currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        if (currentValue != null && !currentValue.isEmpty()) {
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            //return value;
            if (currentValue.isEmpty()) { // new as of 10/20/18
                return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
            }
            return currentValue;

        }

        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random") || value.equalsIgnoreCase("now")) {
                value = getCurrentDateTime();
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

            } else if (value.startsWith("random")) {
                String[] randomWithRange = value.split(" ");
                String range = randomWithRange[1];
                String[] rangeValues = range.split("-");
                // At this point we've supposedly got either 1950-2000 or 02/04/1954-01/01/2000
                String lowerYear = rangeValues[0];
                String upperYear = rangeValues[1];

                value = getRandomDateBetweenTwoDates(lowerYear, upperYear);
                String time = getRandomTime();
                value = Utilities.fillInTextField(dateTimeFieldBy, value + " " + time);
            } else { // value is not "random"
                Utilities.sleep(1555, "Utilities"); // really hate to do it, but datetime is ALWAYS a problem, and usually blows up here.  Failed with 1555, failed with 2555  Because not on right page at time?
                //logger.fine("Are we sitting in the right page to next try to do a date/time??????????????");
                //String theDateTimeString = Utilities.fillInTextField(dateTimeFieldBy, value); //
                value = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (value == null) {
                    logger.fine("Utilities.processDateTime(), could not stuff datetime because fillInTextField failed.  text: " + value);
                    return null; // fails: 8
                }
                //logger.fine("In ProcessDateTime() Stuffed a date: " + value);
            }



        } else { // value is not specified
            if (required) { // field is required
                value = getCurrentDateTime();
                String tempValue = Utilities.fillInTextField(dateTimeFieldBy, value);
                if (tempValue == null) { // is this nec???????
                    logger.fine("Utilities.processDateTime(), couldn't stuff date because fillInTextField failed.  Value: " + value);
                }
                else {
                    value = tempValue;
                }
            } else { // field is not required, but section may be specified as random, not sure this happens any more though
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) { // added extra check for safety, though probably this indicates a fault elsewhere
                    value = getCurrentDateTime();
                    value = Utilities.fillInTextField(dateTimeFieldBy, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random date/time value generated: " + value); // new 12/27/18
            }
        }
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax());
        if (Arguments.pauseDate > 0) {
            Utilities.sleep(Arguments.pauseDate * 1000, "Utilities");
        }
        return value;
    }

    public static String processIntegerNumber(By by, String value, int minValue, int maxValue, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processIntegerNumber()");
// New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.severe("Utilities.processIntegerNumber(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            //return value;
            if (currentValue.isEmpty()) { // new as of 10/20/18
                return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
            }
            return currentValue;

        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                value = String.valueOf(intValue);
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) {
                    int intValue = Utilities.random.nextInt(maxValue - minValue) + minValue;
                    value = String.valueOf(intValue);
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random integer generated: " + value); // new 12/27/18
            }
        }
        return value;
    }


    // Hey this is for a special kind of string of digits, like maybe SSN and not a real number, so watch out.  Use processIntegerNumber?
    public static String processStringOfDigits(By by, String value, int minDigits, int maxDigits, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processStringOfDigits()");
// New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite = false;
        boolean hasCurrentValue = false;
        WebElement webElement = null;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.severe("Utilities.processStringOfDigits(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            //return value;
            if (currentValue.isEmpty()) { // new as of 10/20/18
                return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
            }
            return currentValue;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

            } else { // value is not "random"
                Utilities.fillInTextField(by, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                value = getRandomTwinNumber(minDigits, maxDigits);
                Utilities.fillInTextField(by, value);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getRandomTwinNumber(minDigits, maxDigits);
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random digits generated: " + value); // new 12/27/18
            }
        }
        return value;
    }


    // This was slapped together.  Based on processStringOfDigits, but that wasn't analyzed
    public static String processDoubleNumber(By by, String value, double minValue, double maxValue, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processDoubleNumber()");
// New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.severe("Utilities.processDoubleNumber(), Did not get webElement specified by " + by.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        String currentValue = webElement.getText().trim(); // I added trim.  Untested.
        currentValue = webElement.getAttribute("value").trim(); // which of these two is correct??????

        if (currentValue != null && !currentValue.isEmpty()) { // we could check this for numeric, but unnec.
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            //return value;
            if (currentValue.isEmpty()) { // new as of 10/20/18
                return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
            }
            return currentValue;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                double range = maxValue - minValue;
                double randomValue = random.nextDouble();
                value = String.format("%.2f", (minValue + (range * randomValue)));
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }

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
                if (sectionIsRandom != null && sectionIsRandom) {
                    double range = maxValue - minValue;
                    double randomValue = random.nextDouble();
                    value = String.format("%.2f", (minValue + (range * randomValue)));
                    Utilities.fillInTextField(by, value);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random double number generated: " + value); // new 12/27/18
            }
        }
        return value;
    }

    // This first part is wrong, fix it.
    public static String processRadiosByLabel(String value, Boolean sectionIsRandom, Boolean required, By... radiosByLabels) {
        // New: Taking position that if section is marked random, then all elements are required to have values
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) { // wow, so if the section is random, then this element must get a value.  A bit much?  Maybe this should mean "some nonrequired elements will be forced to have a value"
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = getRandomRadioLabel(radiosByLabels); // should check on this
                value = doRadioButtonByLabel(value, radiosByLabels); // Need to click the button
                logger.finest("Utilities.processRadiosByLabel(), value is " + value);
            } else { // value is not "random"
                value = doRadioButtonByLabel(value, radiosByLabels); // garbage in, what happens?  And can we truncate the value?  "No - Please explain in comments" to "No"
            }
        } else { // value is not specified
            if (required) { // field is required
                value = getRandomRadioLabel(radiosByLabels); // should check on this
                value = doRadioButtonByLabel(value, radiosByLabels);
            } else { // field is not required
                // DO WE EVER GET HERE????????????   Yes!!!  Happens with status radio buttons for summary patient facility treatment history, ... management note.  This doesn't work same as text boxes for random unrequired
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = getRandomRadioLabel(radiosByLabels); // should check on this  Ever get here???????????
                    value = doRadioButtonByLabel(value, radiosByLabels);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random radio generated: " + value); // new 12/27/18
            }
        }
        if (Arguments.pauseRadio > 0) {
            Utilities.sleep(Arguments.pauseRadio * 1000, "Utilities");
        }
        return value;
    }

    // Is there a reason we do radios by button rather than label?
    // You need both the <input> element so you can show the click, and the <label> element so you know what to match.
    // And you need to get the whole list of radio elements in the set.
    // And we cannot be certain what the grouping of elements is.  In one
    // case a <td> has a set of <span> elements, and each <span> has one input element and one label element.
    // In other cases a <td> has a set of input/label pairs.  So in the former you can't do a "parent" to get the
    // entire set (which is what this assumes, I think).  And you cannot assume that a label has only one word,
    // which is also what this currently assumes.
    //
    // When doing radios, what would be easier to process, a list of <input> elements, or a list of <label> elements?
    // THIS ASSUMES RADIOS ARE ALWAYS A SET of <input>:<label> PAIRS.  Is this correct?
    //
    // What's the order of ding things?  Get the set of labels first?  Build a map of <input> to <label>
    //
    // If the button has a unique ID it MAY be more solid than doing an xpath.  The ID is associated with an <input> and isn't the label, so you have to find the matching label and its label text to get the match.  But the <label> requires a xpath
    public static String processRadiosByButton(String value, Boolean sectionIsRandom, Boolean required, By... radiosByButtons) {
        // New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) { // and I made this random too, so half the time when section is random and not required, we make it required.  Watch out.
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // The logic in this section is kinda bad
        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = doRadioButtonByButton(value, radiosByButtons); // check for "random" or something?
            } else { // value is not "random"
                value = doRadioButtonByButton(value, radiosByButtons); // garbage in, what happens? // what?  same either way
            }
        } else { // value is not specified
            if (required) { // field is required
                value = doRadioButtonByButton(value, radiosByButtons);
            } else { // field is not required
                // DO WE EVER GET HERE????????????
                if (sectionIsRandom != null && sectionIsRandom) {
                    value = doRadioButtonByButton(value, radiosByButtons);
                }
            }
            if (Arguments.verbose) {
                System.out.println("      **Random radio value generated: " + value); // new 12/27/18
            }
        }
        if (Arguments.pauseRadio > 0) {
            Utilities.sleep(Arguments.pauseRadio * 1000, "Utilities");
        }
        return value;
    }


    // If a field is required and no value was provided, but there's already a value
    // in the text field, don't overwrite it with random value.  this means we have to read the element's content.
    // What should be returned when there's an error?  The original value?

    // We don't want to return from this method until the element is finished somehow.  Sometimes TMDS server will process the text somehow, and if return too soon things get messed up all over.

    public static String processText(By textFieldBy, String value, TextFieldType textFieldType, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(textFieldBy.toString() + "\tUtilities.processText()");

        // New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18 to remove nextBoolean, and again 12/28/18 to test sectionIsRandom for null
            //logger.fine("Utilities.processText(), Forcing element to be required because section is marked random.");
            required = true;
        }
        boolean valueIsSpecified = !(value == null || value.isEmpty());

        // Establish whether to overwrite existing value for this element on the page or not
        boolean overwrite;
        boolean hasCurrentValue = false;
        WebElement webElement;
        try {
            webElement = (new WebDriverWait(Driver.driver, 5)).until(visibilityOfElementLocated(textFieldBy)); // was 30 way too long for text fields that aren't first or after some long ajax thing
        } catch (Exception e) {
            logger.severe("Utilities.processText(), Did not get webElement specified by " + textFieldBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            //return null; // null, or value?
            return value; // new 10/19/18, is this better?  Can this be okay sometimes?
        }
        String currentValue = webElement.getAttribute("value").trim();

        // Set boolean representing field/element has a currentValue
        // currentValue could be null or blank or text.  If null or blank
        // then boolean gets true.  Otherwise false.
        if (currentValue != null && !currentValue.isEmpty()) { //little awkward logic, but maybe okay if find other text values to reject
            hasCurrentValue = true;
//            if (currentValue.isEmpty()) {
//                hasCurrentValue = false;
//            }
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
            //logger.fine("Don't go further because we don't want to overwrite.");
            //If field is optional, and no value is specified, and no value is in the element, do we want the output JSON file to show the field and have it be blank, or not?  I think not.
            if (currentValue.isEmpty()) { // perhaps not putting the field into the output JSON is better than putting it in with a blank value.
                //logger.fine("Utilities.processText(), won't overwrite, but currentValue is empty.  Returning null which means JSON output won't show this field");
                //return null; // This has consequences for -weps and -waps, because null doesn't get put into output JSON file I don't think
                return value; // dangerous.  12/13/18
            }
            //logger.fine("Utilities.processText(), won't overwrite, returning current value: ->" + currentValue + "<-");
            return currentValue;
        }


        if (valueIsSpecified) {
            if (value.equalsIgnoreCase("random")) {
                value = genRandomValueText(textFieldType);
                if (value == null) { // new 10/26/18, experimental, not sure
                    value = currentValue;
                }
                Utilities.fillInTextField(textFieldBy, value);

            } else { // value is not "random"
                Utilities.fillInTextField(textFieldBy, value);
            }
        } else { // value is not specified
            if (required) { // field is required
                value = genRandomValueText(textFieldType);
                Utilities.fillInTextField(textFieldBy, value);
                if (Arguments.verbose) {
                    System.out.println("      **Random text value generated: " + value); // new 12/27/18
                }
            } //else { // field is not required
                //logger.fine("This is a big change, and a big test.  If things stop working right, then uncomment this section");
            //}
        }
        if (Arguments.pauseText > 0) {
            Utilities.sleep(Arguments.pauseText * 1000, "Utilities");
        }
        return value;
    }
    // This is for checkboxes (which are toggles) which means if the box is already checked you don't
    // want to click it again to try to set it.  Rather than clear first, do the XOR.
    // In the JSON file a Boolean can be true, false, null, or just missing, which is null.
    // If missing, it's null.  Then how do you specify "random"?  You can't.  We could make the
    // assumption that if it's null it means "random".  Oh, but that's the same as other fields
    // too.  Okay.
    public static Boolean processBoolean(By by, Boolean value, Boolean sectionIsRandom, Boolean required) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.processBoolean()");
// New: Taking position that if section is marked random, then all elements are required to have values
// questionable:
        //if (sectionIsRandom && !required && Utilities.random.nextBoolean()) {
        if (sectionIsRandom != null && sectionIsRandom && !required) { // changed 12/27/18
            //logger.fine("Utilities.processXXX(), Forcing element to be required because section is marked random.");
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
        else if (!required && (sectionIsRandom == null || !sectionIsRandom)) {
            overwrite = false;
        }
        else {
            overwrite = true; // whittled down to either required or section is random
        }
        if (!overwrite) {
            //logger.fine("Don't go further because we don't want to overwrite.");
            return value;
        }



        if (valueIsSpecified) {
            WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
            WebElement checkBoxWebElement = wait.until(visibilityOfElementLocated(by));

            if (checkBoxWebElement != null) {
                boolean isChecked = checkBoxWebElement.isSelected(); // is this the right check to get the state?  I don't think so
                if (value != isChecked) {
                    checkBoxWebElement.click(); // can this throw?
                }
            }
        } else { // value is not specified
            if (required) { // field is required
                value = random.nextBoolean();
                WebDriverWait wait = new WebDriverWait(Driver.driver, 10);
                WebElement checkBoxWebElement = wait.until(visibilityOfElementLocated(by));

                if (checkBoxWebElement != null) {
                    boolean isChecked = checkBoxWebElement.isSelected(); // does this work?
                    if (value != isChecked) {
                        checkBoxWebElement.click();
                    }
                }
                if (Arguments.verbose) {
                    System.out.println("      **Random checkbox value generated: " + value); // new 12/27/18
                }
            } else { // field is not required
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
        return value; // Don't change state
    }

    public static String getCurrentTextValue(By by) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentTextValue()");
// probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }

    // wrong
    public static String getCurrentDropdownValue(By by) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentDropdownValue()");
// probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }

    // wrong
    public static String getCurrentRadioValue(By by) {
        if (pep.Main.catchBys) System.out.println(by.toString() + "\tUtilities.getCurrentRadioValue()");
// probably want to wrap this with an explicit wait and try
        try {
            WebElement textField = (new WebDriverWait(Driver.driver, 2)).until(visibilityOfElementLocated(by));
            String currentValue = textField.getText();
            return currentValue;
        }
        catch (Exception e) {
            return null;
        }
    }



    public static String getRandomRadioLabel(By... radioLabelByList) {
        int nRadioLabelBys = radioLabelByList.length;
//        int randomRadioLabelIndex = Utilities.random.nextInt(nRadioLabelBys); // why does this keep returning 0?
        int randomRadioLabelIndex = Utilities.random.nextInt(nRadioLabelBys);
        try {
            By radioLabelBy = radioLabelByList[randomRadioLabelIndex];
            if (pep.Main.catchBys) System.out.println(radioLabelBy.toString() + "\tUtilities.getRandomRadioLabel()"); // not sure next line should be done that way with waitForPresence
           // next line fails
            WebElement radioLabelElement = Utilities.waitForPresence(radioLabelBy, 2, "Utilities.getRandomRadioLabel()");
            String radioLabelText = radioLabelElement.getText(); // Baseline radio buttons, and Referral, comes back with "", "Unknown" has text ""
            if (radioLabelText.isEmpty()) {
                logger.fine("Utilities.getRandomRadioLabel(), selected radio " + radioLabelBy.toString() + " but corresponding label is blank, so how about returning 'Yes'?");
                radioLabelText = "Yes"; // hack that won't last
            }
            return radioLabelText;
        } catch (Exception e) {
            logger.severe("Utilities.getRandomRadioLabel(), couldn't get radio element " + randomRadioLabelIndex + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
    }

    // total hack.  Needs to be looked at closely.  These radio By elements are supposed to be for their labels, not the buttons!!!
    // So only call this method if the By elements are labels and not buttons.
    public static String doRadioButtonByLabel(String value, By... radios) {
        //String radioLabelText = null;
        for (By radioBy : radios) {
            if (pep.Main.catchBys) System.out.println(radioBy.toString() + "\tUtilities.doRadioButtonByLabel()");

            try { // prob shouldn't have converted next line.  Did it accidentally
                WebElement radioElement = Utilities.waitForPresence(radioBy, 4, "Utilities.doRadioButtonByLabel()");
                String radioLabelText = radioElement.getText(); // You can't do this if the DOM structure doesn't have a label inside the input element.  Gold doesn't.  At least in laterality of PNB in SPNB in ProcedureNotes.
                // Compare all the words, even though that makes the user type more than one word if there is, like "PENDING TRANSFER".
                // Otherwise that would assume radio labels in a set are unique in first word, which they aren't, like "PENDING EVAC".
                // Exception case: you can type in "No", when the option is "No - explain".  We'll stop comparing at the " - ".
                if (radioLabelText != null) {
                    int radioLabelTextLength = radioLabelText.length(); // test stuff
                    int nCharsToMatch = radioLabelText.indexOf(" - ");
                    if (nCharsToMatch == -1) {
                        nCharsToMatch = radioLabelTextLength;
                    }
                    if (radioLabelText.regionMatches(true, 0, value, 0, nCharsToMatch)) { // experiment
                        // next line has wrong element to click on some times, I think, possibly because duplicate elements on page for label?  Used to be able to click on the label and the button would respond.  No longer.  At least not in Transfer Note
                        radioElement.click(); // THIS ASSUMES YOU CAN CLICK ON LABELS AND BUTTON WILL SHOW THE CLICK.  NOT TRUE IF DUPLICATE ID'S IN SOME CASES LIKE VALUES IN @for ATTRIBUTE
                        return radioLabelText;
                    }
                } else {
                    //logger.fine("Utilities.doRadioButtonByLabel(), radioLabelText not what looking for: " + radioLabelText);
                    continue;
                }

            } catch (Exception e) {
                logger.fine("Utilities.doRadioButtonByLabel(), didn't get radioElement, or its text: " + Utilities.getMessageFirstLine(e));
                continue; // maybe this should be a break?  We've got a bad locator.
            }
        }
        return null; // what does this mean, failure, right?
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
    // This must change.  Fix this.  Logic er
    public static String doRadioButtonByButton(String value, By... radios) {
        try {
            int nRadios = radios.length;
            if (value == null || value.equalsIgnoreCase("random") || value.isEmpty()) {
                int randomIndex = Utilities.random.nextInt(nRadios);

                // Why not wrap some of these in try/catch so we don't have to have so many if's?????????????????
                // Not sure should be calling waitForPresence, which is what I wrote to handle non Utilities methods
                WebElement matchingRadioElement = Utilities.waitForPresence(radios[randomIndex], 4, "classMethod");
                // now get the matching label
                // Wow, this next line gets the parent of the button?  This assumes the parent has children organized
                // in a certain way.  I don't think it's universal.  The one in Amputation Cause has a set of spans.
                // a child that has a label child
                WebElement parentElement = matchingRadioElement.findElement(By.xpath("parent::*"));
                String labelsString = parentElement.getText();
                String[] labels = null;
                String newValue = null;
                if (labelsString != null && !labelsString.isEmpty()) {
                    labels = labelsString.split(" "); // this is way faulty logic.  Assumes labels can only be one word
                    newValue = labels[randomIndex]; // wrong!!!!!!!!!!!!!!!!!!!!!!
                } else {
                    logger.fine("Something assumed about radio labels that isn't true.  like an association of button with label that is clearly defined for all. " + labelsString);
                    logger.fine("And parent is " +  parentElement);
                    return null;
                }
                matchingRadioElement.click();
                return newValue;
            }
            // the following is faulty logic.  Expects radio labels are a single word, and that they are all organized under a single parent
            WebElement firstRadioElement = Utilities.waitForPresence(radios[0], 4, "classMethod");
            WebElement parentElement = firstRadioElement.findElement(By.xpath("parent::*"));
            String labelsString = parentElement.getText();
            String[] labels = null;
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
            logger.severe("Utilities.doRadioButtonByButton(), couldn't do radio button" + Utilities.getMessageFirstLine(e));
            return null;
        }
        return null; // right?

    }


    // this method waits 10 sec max to find a clickable element.  If this method isn't used it's because there are other things to consider when clicking something.
    public static void clickButton(final By button) {
        if (Main.catchBys) System.out.println(button.toString() + "\tUtilities.clickButton()");

        try {
            WebElement buttonElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.elementToBeClickable(button));
            buttonElement.click();

        } catch (Exception e) {
            logger.warning("Utilities.clickButton(), didn't get button to click, or couldn't click it: " + Utilities.getMessageFirstLine(e));
        }
    }

    public static boolean getCheckboxValue(final By locator) {
        if ( Main.catchBys) System.out.println(locator.toString() + "\tUtilities.getCheckboxValue()");
        final WebDriver driver = Driver.driver;
        WebElement checkbox = driver.findElement(locator);
        boolean isSelected = checkbox.isSelected();
        return isSelected;
    }


    public static void clickAlertAccept() {
        final WebDriver driver = Driver.driver;
        Alert possibleAlert = driver.switchTo().alert();
        if (possibleAlert == null) {
            logoutFromTmds(); // what?
        }
        (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                try {
                    Alert possibleAlert = driver.switchTo().alert();
                    if (possibleAlert == null) {
                        logger.fine("\tAlertAccept not available");
                        return false;
                    }
                    if (possibleAlert.getText().length() < 1) {
                        logger.warning("\tIn Utilities.clickAlertAccept(), No text for this alert");
                        return false;
                    }
                } catch (Exception e) {
                    logger.severe("\tIn Utilities.clickAlertAccept(), AlertAccept still not available.  Exception caught: " + Utilities.getMessageFirstLine(e));
                    return false;
                }
                return true;
            }
        });
        try {
            possibleAlert.accept(); // This can allow a "Concurrent Login Attempt Detected" page to appear because the login gets processed
        } catch (Exception e) {
            logger.severe("\tIn Utilities.clickAlertAccept(), Could not accept the alertAccept.  Exception caught: " + Utilities.getMessageFirstLine(e));
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
            logger.warning("In fillInTextField(), Tried to clear element, got invalid state, Element is not interactable?  Continuing.");
            //return null;
        } catch (Exception e) {
            logger.severe("fillInTextField(), SomekindaException.  couldn't do a clear, but will continue anyway: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            element.sendKeys(text); // prob here "element is not attached to the page document"
        } catch (ElementNotVisibleException e) { // I think it fails because we're not on a page that has the field, but how got here, don't know.
            logger.severe("fillInTextField(), Could not sendKeys. Element not visible exception.  So, what's the element?: " + element.toString());
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
    // This method is a total mess with so many exceptions possible.  Perhaps the problem is switching contexts
    // somehow.
    //
    // This method has serious problems.
    //
    // It's also possible that the field is not writable.  Marked readonly.
    public static String fillInTextField(final By field, String text) {
        if (pep.Main.catchBys) System.out.println(field.toString() + "\tUtilities.fillInTextField()");
        if (text == null || text.isEmpty()) {
//            if (Arguments.debug && !(field.toString().contains("registerNumber") || field.toString().contains("patientSearchRegNum"))) // total hack - if field is transaction, don't mention this
//                System.out.println("Utilities.fillInTextField(), no text to fill anything in with.  returning null.  Can happen if pass in transaction number in search.  It's okay.");
            return null;
        }

        WebElement element = null;
        try { // this next line is where we fail.  Maybe it's because this text field comes right after some AJAX call, and we're not ready
            //element = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(field)); // This can timeout
            element = Utilities.waitForRefreshedPresence(field,  10, "Utilities.fillInTextField()"); // oops, didn't mean to do this
            //ExpectedConditions.visibilityOfElementLocated(field)); // does this thing wait at all?
            String readonlyAttribute = element.getAttribute("readonly");
            if (readonlyAttribute != null) {
                if (readonlyAttribute.equalsIgnoreCase("true")) { // actually, in the html it says readonly="readonly" but for some reason comes back true
                    logger.fine("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
                if (readonlyAttribute.equalsIgnoreCase("readonly")) { // I don't think this happens.  would be "true", maybe?
                    logger.fine("Hey, this field is read only, so why bother trying to change it?");
                    return null;
                }
            }
        } catch (StaleElementReferenceException e) {
           logger.severe("Utilities.fillInTextField(), Stale Element Reference.  Could not get element: " + Utilities.getMessageFirstLine(e));
            return null;
        } catch (Exception e) {
            logger.severe("Utilities.fillInTextField(), could not get element: " + field.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null; // this happens a lot!!!  TimeoutException 10/21/18:1
        }
        //logger.fine("Utilities.fillInTextField(), element is " + element);



        if (element == null) {
            System.out.println("How do we get a null element if there was no exception caught?");
            try {
                logger.finer("Let's try again...");
                element = (new WebDriverWait(Driver.driver, 10))
                        .until(
                                ExpectedConditions.presenceOfElementLocated(field)); // This can timeout
            }
            catch (Exception e) {
                logger.finer("2nd try didn't work either");
                return null;
            }
        }


        try {
            // This next line will throw an exception InvalidElementStateException .  Why?
            // Something wrong with the element.  It thinks it cannot be cleared.  Maybe the element is marked unwritable or something.
            // So, how about just not clearing it?
            element.clear();
        } catch (InvalidElementStateException e) {
            logger.warning("Utilities.fillInTextField(), Invalid Element State.  Could not clear element:, " + element + " Oh well.  Continuing");
            //return null;
        } catch (StaleElementReferenceException e) {
            logger.warning("Utilities.fillInTextField(), Stale Element References.  Could not clear element:, " + element + " Oh well.  Continuing.  Exception: " + Utilities.getMessageFirstLine(e));
            //return null;
        } catch (Exception e) {
            logger.fine("Utilities.fillInTextField(), could not clear element:, " + element + " Oh well.  Continuing.  Exception: " + Utilities.getMessageFirstLine(e));
            //return null;
        }

        try {
            // lets do a refresh because the clear can do something bad?
            //logger.fine("Utilities.fillInTextField(), gunna refresh then wait for visibility of field: " + field);
            // This next line causes an error, and I think it's because we are NOT on the right page when we try to do this.
            element = (new WebDriverWait(Driver.driver, 10))
                    .until(ExpectedConditions.refreshed(
                            visibilityOfElementLocated(field))); // does this thing wait at all?
            //logger.fine("Utilities.fillInTextField(), waited for that field, and now gunna send text to it: " + text);
            element.sendKeys(text); // prob here "element is not attached to the page document"
            //logger.fine("Success in sending text to that element."); // May be wront.  Maybe couldn't write.
        } catch (TimeoutException e) {
            logger.severe("Utilities.fillInTextField(), could not sendKeys " + text + " to it. Timed out.  e: " + Utilities.getMessageFirstLine(e));
            return null; // fails: 2
        } catch (StaleElementReferenceException e) {
            logger.severe("Utilities.fillInTextField(), Stale ref.  Could not sendKeys " + text + " to it. e: " + Utilities.getMessageFirstLine(e));
            return null; // fails: 3 1/30/19, 1/31/19
        } catch (Exception e) {
            logger.severe("Utilities.fillInTextField(), could not sendKeys " + text + " to it. Exception: " + Utilities.getMessageFirstLine(e));
            return null; //  could not sendKeys 222261224 to it. Exception: unknown error: unhandled inspector error: {"code":-32000,"message":"Cannot find context with specified id"}
        }
        return text;
    }

    private static String getRandomDropdownOptionString(final By dropdownBy) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.getRandomDropdownOptionString()");

        WebElement dropdownWebElement = null;
        try {
            // Crucial difference here between presenceOfElementLocated and visibilityOfElementLocated.  For TBI Assessment Note, must have visibilityOfElementLocated
            // why is this next line really slow for Arrival/Location. Status????
            dropdownWebElement = (new WebDriverWait(Driver.driver, 30)).until(visibilityOfElementLocated(dropdownBy));
        } catch (Exception e) {
            logger.severe("Utilities.getRandomDropdownOptionString(), Did not get dropdownWebElement specified by " + dropdownBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        Select select = new Select(dropdownWebElement); // fails here for originating camp, and other things
        List<WebElement> optionElements = select.getOptions(); // strange it can get the options, but they are all stale?
        int size = optionElements.size();
        if (size < 2) { // Hey can't there ever be a dropdown with 1 option?
            logger.warning("This dropdown " + dropdownBy.toString() + " has no options.  Returning null");
            return null; // try again?
        }
        int randomOptionIndex = Utilities.random.nextInt(size); // 0, 1, 2, 3  (if 4), but the first element in the list should not be chosen.  It is element 0
        randomOptionIndex = (randomOptionIndex == 0) ? 1 : randomOptionIndex; // Some dropdowns start with 0, but most do not. THIS IS FLAWED.  doesn't work for icd code set for example.
        //System.out.println("\tgetRandomDropdownOptionString, and randomOptionIndex is " + randomOptionIndex);
        //logger.fine("We'll use option number " + randomOptionIndex); // is option 1 bad??????????????  Failed with 1
        WebElement option = null;
        try {
            option = optionElements.get(randomOptionIndex); // optionElements is a list based on first is 0
        } catch (StaleElementReferenceException e) {
            logger.fine("Hmmm, stale element they say.  Must be optionElements");
            return null;
        } catch (Exception e) { // IndexOutOfBoundsException
            logger.warning("In Utilities.getRandomDropdownOptionString(), size: " + size + " driverUrl " + dropdownBy.toString());
            logger.fine("Exception caught in selecting dropdown option: " + Utilities.getMessageFirstLine(e));
            return null; // ?????????????????????????????
        }
        try {
            String optionString = option.getText();
            //logger.fine("getRandomDropdownOptionString is returning the string " + optionString);
            return optionString;
        } catch (Exception e) {
            logger.fine("Why the crap can't I getText() from the option?");
        }
        return null;
    }

    // why doesn't this return a value selected? Because it has to match exactly so you know anyway?  Well, success? failure?
    //public static void selectDropdownOption(final By dropdownBy, String optionString) {
    // This can fail after an effort to click on the Procedure Notes tab.
    private static String selectDropdownOption(final By dropdownBy, String optionString) {
        if (pep.Main.catchBys) System.out.println(dropdownBy.toString() + "\tUtilities.selectDropdownOption()");
        WebElement dropdownElement = null;
        try {
            dropdownElement = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(visibilityOfElementLocated(dropdownBy))); // this ExpectedConditions stuff is really powerful.  Look at it!!!!  Lots of things.
        } catch (Exception e) {
            logger.severe("Utilities.selectDropdownOption(), couldn't get dropdown " + dropdownBy.toString() + " Exception: " + Utilities.getMessageFirstLine(e));
            return null;
        }
        try {
            //logger.fine("Utilities.selectDropdownOption(), here comes a new Select(dropdownElement)");
            Select select = new Select(dropdownElement); // fails, can through a Stale element reference: 1
            // Next line is Selenium, and it only does exact matches.  No ignore case, no close match, etc.
            select.selectByVisibleText(optionString); // throws exception, stale:1  Why?  Because whatever called this method caused a DOM rewrite probably
            //logger.fine("Utilities.selectDropdownOption(), Back from calling selectByVisibleText with option " + optionString);
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


    // This is the version currently getting airplay.  The first 4 digits represent the time of day, and the last
    // four are the day and month.  The digit in the middle is just random.
    //
    // While this gives okay precision there's still a few problems.  The first is that if you run this app
    // in parallel you could get the same SSN for two different patients.  The second is that the number range
    // is pretty limited in that the first digit can only be 0, 1, or 2.  The third can only be 0-5.  The sixth
    // only 0 or 1.  The eighth only 0,1,2, or 3.
    //
    // The goal is to be able to look at a SSN and tell when the
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
    // You can get current time down to the millisecond, supposedly, but probably no more than 10ms granularity.
    // So, that's 100 clicks per second, and probably good enough when multiple PePs are running.  So,
    // if we have 5 digits to fill, why not just take the last 6 digits of ms time (since epoch) and cut off
    // the 6th digit, and use the remaining 5 to fill?  Perhaps technically those digits could be used to
    // get a time stamp (knowing the day), it really wouldn't be used for anything other than just an ordering
    // of numbers so you could tell the order in which SSN's were created.  This might have some value.
    //
    // Perhaps the easiest way is to forget about ordering and just generate a random 5 digit number.
    // What are the chances of getting a duplicate SSN that way?  It would have to happen within one
    // day's time.  I think it's a very small chance.  1 in 100,000.
    //
    // If you use the number of microseconds since the start of the program, and then truncate somehow to fit
    // 5 digits, I don't think that's as good as random because the chance of duplication is much higher, plus
    // you don't really get an ordering of numbers.
    //
    // You can't get both precision and range with 5 digits plus 4 for date.  We need at least timing down to the tenth of
    // a second to avoid duplicate SSN's when running multiple PePs at once, and that leaves 4 digits,
    // which is as most 10,000 seconds.  But a day is over 86,000 seconds, and so it would cycle 8 times
    // in a day (every 14.4 minutes).  So, you'd lose sequence comparison, and have a (very small) chance
    // of duplicates.  Instead, if you used a random number, you'd have no sequence, and you'd have a
    // smaller chance of duplicates.  Sequence isn't as important as no duplicates.  So, random 5 digits is
    // still the best decision.
    public static String getRandomSsnLastFourByDate() {
        StringBuffer patternForSsn = new StringBuffer(9);
        int randomInt = Utilities.random.nextInt(100000);
        String formattedRandomInt = String.format("%05d", randomInt);
        patternForSsn.append(formattedRandomInt);
        patternForSsn.append("MMdd");
        DateFormat dateFormat = new SimpleDateFormat(patternForSsn.toString()); // This is just a way for me to more easily find patients by last 4 of SS
        String ssBasedOnDate = dateFormat.format(new Date());
        if (ssBasedOnDate.length() != 9) {
            logger.warning("!!!!!!!!!!!!!!!!!!!!!ssn " + ssBasedOnDate + " not 9 digits !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return ssBasedOnDate;

        // The following is hacky and bad design:
        // there are 86400 seconds per day, and 1440 minutes per day, and 24 hours per day.
        // HHMMSSMMDD is 10 digits and only gets down to the second.
        //
        //
//        StringBuffer patternForSsn = new StringBuffer(9);
//        Calendar cal = Calendar.getInstance();
//        long millis = cal.getTimeInMillis();
//        String millisString = String.valueOf(millis); // 13 chars long.  Always will be?
//        String millisSubString = millisString.substring(6,11);
//        int millisSubInt = Integer.parseInt(millisSubString);
//        String formattedMillis = String.format("%05d", millisSubInt);
//        patternForSsn.append(formattedMillis);
//        patternForSsn.append("MMdd");
//        DateFormat dateFormat = new SimpleDateFormat(patternForSsn.toString()); // This is just a way for me to more easily find patients by last 4 of SS
//        String ssBasedOnDate = dateFormat.format(new Date());
//        //System.out.println("ssBasedOnDate: " + ssBasedOnDate);
//        if (ssBasedOnDate.length() != 9) {
//            logger.warning("!!!!!!!!!!!!!!!!!!!!!ssn " + ssBasedOnDate + " not 9 digits !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        }
//        return ssBasedOnDate; // 68535xxxx
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
            year = minYear + Utilities.random.nextInt(maxYear - minYear);
        }

        calendar.set(calendar.YEAR, year);

        int maxDayOfYear = calendar.getActualMaximum(calendar.DAY_OF_YEAR); // could be off by a day
        int dayOfYear = Utilities.random.nextInt(maxDayOfYear + 1); // could be off by a day

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
        int hours = Utilities.random.nextInt(24);
        int mins = Utilities.random.nextInt(60);
        return String.format("%02d%02d", hours, mins);
    }

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
    // untested, prob off by 1 or other prob
    public static String getMessageFirstLineTruncated(Exception e, int maxChars) {
        String message = getMessageFirstLine(e);
        if (message.length() > maxChars) {
            message = message.substring(0, maxChars);
        }
        return message;
    }

    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
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
//    public static WebElement waitForPresence(By elementBy, int secondsToWait, String message) throws TimeoutException {
//        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for presence.");
//        try {
//            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(presenceOfElementLocated(elementBy));
//            return webElement;
//        }
//        catch (TimeoutException e) {
//            throw e;
//        }
////        return webElement;
//        //return null;
//    }
    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
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
    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
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
    //            WebElement createNoteButton = (new WebDriverWait(Driver.driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(createNoteButtonBy)));

    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
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
    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
    public static void waitForInvisibility(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for invisibility.");
        try {
            (new WebDriverWait(Driver.driver, secondsToWait)).until(ExpectedConditions.invisibilityOfElementLocated(elementBy)); // maybe works
             return;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForInvisibility() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }
    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
    public static WebElement waitForRefreshedPresence(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for refreshed presence.");
        //WebElement webElement = Utilities.waitForRefreshedPresence(elementBy,  secondsToWait, "classMethod");
        try {
        WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(refreshed(ExpectedConditions.presenceOfElementLocated(elementBy)));
        return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForRefreshedPresence() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }
    // This is experimental.  Maybe not used because don't know what method this comes from, so can't report it,
    // and don't know if should display any error messages.
    public static WebElement waitForRefreshedVisibility(By elementBy, int secondsToWait, String message) {
        if (pep.Main.catchBys) System.out.println(elementBy.toString() + " - " + message + " Waiting " + secondsToWait + " sec for refreshed visibility.");
        try {
            //WebElement webElement = Utilities.waitForRefreshedVisibility(elementBy,  secondsToWait, "classMethod");
            WebElement webElement = (new WebDriverWait(Driver.driver, secondsToWait)).until(refreshed(ExpectedConditions.visibilityOfElementLocated(elementBy)));
            return webElement;
        }
        catch (Exception e) {
            logger.fine("Utilities.waitForRefreshedVisibility() caught exception and will throw it. e: " + Utilities.getMessageFirstLine(e));
            throw e;
        }
    }

    public static void sleep(int millis, String comment) {
        try {
            //if (Arguments.debug) System.out.print(" " + millis + "ms ");
            Main.timerLogger.fine("sleeping " + millis + " ms * " + Arguments.throttle + " " + comment);
            Thread.sleep((int) (millis * Arguments.throttle)); // may be changing this later, because throttling by sleep not as effective
        } catch (Exception e) {
            // ignore
        }
    }


}

/*

Patient Registration
Pre-registration
New Patient Reg.
Update Patient
Patient Information
Pre-registration Arrivals
Mission Summary
Update Mission Info
Patient Treatment
Pain Management
Pain Management Note
Pain Management Report
Behavioral Health
BH Assessments
Reports
BH Patient Report
BH Case Review Conference
TBI Assessments
Patient Summary
MTD
Patient Management
Attendant Management
MTD Reports
MTD Active Reports
MTD Outprocessed Report
MTD Admin
Vouchers
Voucher Management
Voucher Report
Reports
Active Patient Reports
Active Patient Roster
Average Number of Days
Inpatient Report
Outpatient Report
Daily Reports
Mission Summary
Patient Arrivals
OIF/OEF Daily Report
Patients By Service
Current
New
Liaison Report
Originating Location Report
Air Transport Reports
Air Transport Report
Air Transport History
Historical Reports
Archived Patients
Task Forces
My Task Forces
Task Force Report
Joint Trauma Registry
Case Review Conference
Trauma Report
MTBI Reports
MTBI Diagnosis Report
MTBI Assessment Report
My Patient List
Joint Legacy Viewer
Guidelines/Info
CENTCOM JTTS CPG
JPMRC Info
JLV Training Materials
Links
Preferences
Account Information
Change Password
Request Additional Access


 */