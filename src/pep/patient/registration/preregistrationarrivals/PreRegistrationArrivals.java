package pep.patient.registration.preregistrationarrivals;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;
import static pep.utilities.Utilities.getMessageFirstLine;


public class PreRegistrationArrivals {
    private static Logger logger = Logger.getLogger(PreRegistrationArrivals.class.getName());
    public Boolean random; // not sure we really want random for this page.  Randomly "arrive" a patient?, randomly remove a patient?
    public Boolean shoot;
    //public List<Arrival> arrivals = new ArrayList<>();
    public List<Arrival> arrivals; // these are specified in the JSON input file, and get loaded by GSON, right?

//    private static By patientRegistrationMenuLinkBy = By.xpath("//a[@href='/tmds/patientRegistrationMenu.html']");
    private static By patientRegistrationMenuLinkBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
//    private static By patientPreRegistrationArrivalsMenuLinkBy = By.xpath("//li/a[@href='/tmds/patientPreRegArrivals.html']"); // seems that this link changes after clicking on main menu link
    private static By patientPreRegistrationArrivalsMenuLinkBy = By.cssSelector("a[href='/tmds/patientPreRegArrivals.html']"); // seems that this link changes after clicking on main menu link
//    private static By updateButtonBy = By.xpath("//*[@id=\"patientPreRegArrivalForm\"]/table/tbody/tr[3]/td/input");
//    private static By updateButtonBy = By.xpath("//*[@id=\"patientPreRegArrivalForm\"]//input[@value='UPDATE']");
    private static By updateButtonBy = By.xpath("//input[@value='UPDATE']");
    private static By arrivalsTableBy = By.xpath("//*[@id=\"tr\"]/tbody");
    private static By preRegArrivalsFormBy = By.id("patientPreRegArrivalForm");


    public PreRegistrationArrivals() {
        if (Arguments.template) {
            this.arrivals = Arrays.asList(new Arrival());
        }
    }
    // This page has no Patient Search section at the top.  It contains a table/list of patients,
    // and each one has a Modify link, an Arrived check box and a "Remove" check box.
    //
    // The page also contains an "UPDATE" button.
    //
    // If you click on Modify link you go back to the Pre-registration page.
    // I think you have to check "Arrived" in order for that patient to be able to be accessed by
    // New Patient Reg.  However, you can access the patient with Update Patient, strangely.
    //
    // What this page will probably be used for is merely to check the "ARRIVED" box.  But we should
    // also support the "REMOVE" box.  I'm not sure we should support the Modify link, because the
    // user should just go directly to the Pre-registration page or Update Patient page if they want
    // to modify anything.
    //
    // The corresponding JSON input file section would contain what?  Two elements for the check boxes.
    // I think that's all that's required.  We could support the Modify link by creating an element
    // for that if necessary.
    //
    // But these elements are in a table, so the element selectors are more challenging.
    //
    // Plus, we have to have some patient identification information to know which row in the table
    // to work on.  The reasonable columns that could be used would include SSN (last 4), Last name,
    // First name.  But you could also use gender and flight date and flight number and rank.
    // Flight Date would seem to make the most easy match.
    //
    // So the JSON section could include any of those fields to be used for searching the table.
    // These elements can only be read, not clicked on, but selectors should still work for them.
    //
    // I think you have to search the
    // entire table, because you don't know if the provided fields would be a good enough match to insure
    // you have the patient.  For example, you don't want to just search on Gender, or the last 4 of SSN.
    // It should probably be a combination of ssn, last, first, and then optionally flight date.
    //
    // This is the element containing the rows:
    //      //*[@id="tr"]/tbody
    // under it (inside it) are the set of <tr> elements
    // Loop through them, applying the filters provided (ssn, last, first, whatever else)
    // when a match is found, save it to a list, along with the selectors for the two check boxes.
    // When done, check the list to see how many matches.
    // If none, exit.  If more than one, report and exit.  If exactly one, click its boxes.

    public boolean process(Patient patient) {
        // Report what's going on
        if (!Arguments.quiet) {
            System.out.print("  Processing Pre-Registration Arrivals ");

            StringBuffer forString = new StringBuffer();
            if (patient.patientSearch.firstName != null && !patient.patientSearch.firstName.isEmpty() && !patient.patientSearch.firstName.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(patient.patientSearch.firstName);
            }
            if (patient.patientSearch.lastName != null && !patient.patientSearch.lastName.isEmpty() && !patient.patientSearch.lastName.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(" " + patient.patientSearch.lastName);
            }
            if (patient.patientSearch.ssn != null && !patient.patientSearch.ssn.isEmpty() && !patient.patientSearch.ssn.equalsIgnoreCase("random")) { // prob don't want random here
                forString.append(" ssn:" + patient.patientSearch.ssn);
            }
            if (forString.length() > 0) {
                System.out.println("for " + forString.toString() + " ...");
            } else {
                System.out.println(" ...");
            }
        }
        // Navigate to the Pre-Registration Arrivals page
        Utilities.sleep(2555); // following line wrong????? // was 1555
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientPreRegistrationArrivalsMenuLinkBy);
        if (!navigated) {
            logger.fine("PreRegistrationArrivals.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2
        }
        // Check that the arrivals table is there
        WebElement arrivalsTable = null;
        try {
            // It's possible there is no table, because no one preregistered.  Need to account for that.  This doesn't.
            // Instead of sleep, maybe should do some other check to see if the table is done loading
            (new WebDriverWait(Driver.driver, 5)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(preRegArrivalsFormBy))); // experiment 12/12/18

            //(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(arrivalsTableBy)); // what is this? experiment 11/28/18 // not sure this helped.  Don't know that it hurt either
            Utilities.sleep(555); // hate to do it, and don't even know if this helps, but columns sometimes is 2 rather than 11
            //arrivalsTable = (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(arrivalsTableBy));
            arrivalsTable = (new WebDriverWait(driver, 10)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(arrivalsTableBy))); // was 30
        }
        catch (Exception e) {
            logger.severe("PreRegistrationArrivals.process(), could not get arrivals table.  Getting out, returning false.  Exception: " + Utilities.getMessageFirstLine(e));
            if (!Arguments.quiet) {
                System.out.println("    ***No patients in arrivals table.");
            }
            return false;
        }
        // Get all the rows (tr elements) into a list
        List<WebElement> arrivalsTableRows = null;
        try {
            //Utilities.sleep(555); // hate to do it, and don't even know if it helps, but sometimes the number of columns is 2 rather than 11
            arrivalsTableRows = arrivalsTable.findElements(By.cssSelector("tr"));
        }
        catch (Exception e) {
            logger.fine("PreRegistrationArrivals.process(), Couldn't get any rows from the table.  getting out, returning false.  Exception: " + getMessageFirstLine(e));
            return false; // no elements in table.
        }
        // There are three "lists" of things to consider:
        // 1. The user supplied one or more Arrival search criteria objects in the JSON file.  (Usually there'd be only one, but could click on multiple.)
        // 2. Each user-supplied Arrival search criteria object contains zero or more search filters, like ssn, or last name. (Usually just ssn, and/or first/last)
        // 3. The table contains zero or more patients that were pre-registered but not yet arrived.  (Usually we only want to arrive one of the patients.)
        //
        // This pseudo is not strictly followed.
        // And the following code is only lightly
        //
        // For each user supplied Arrival search criteria objects (usually 1)
        //   If the object does not contain either operation (arrive or remove), skip this object (uncommon, since doesn't make sense)
        //   For each row in the table
        //     Look at each column/value in the table row (6? and most have values) and each matching search filter in the Arrival search criteria objects (ssn, last, first)
        //     If the search filter has a value (not null, not missing, but random and blank okay)
        //       If the filter is "random", we say we have a match of that filter with the column/value - match=true
        //       else If the filter matches the column/value, we have a match for that column/value - match=true
        //       else match=false, so just loop to next row
        //     else, match=true (this means that if the user Arrival object contains no elements except "arrived", then all table elements get arrived!
        //     If match==false, skip row
        //     Else, check the boxes
        // This is slow when it comes to "arriving" a lot of patients.  For one, it's bearable.
        boolean clickedArrived = false;
        boolean clickedRemove = false;
        // For each user supplied Arrival search criteria objects (usually 1) check either arrived or remove on their row.
        for (Arrival userSuppliedArrivalFilter : arrivals) { // of course "arrivals" is an array of Arrival objects specified in JSON file
            // If the object does not contain either operation (arrive or remove), skip this object (uncommon, since doesn't make sense)
            if ((userSuppliedArrivalFilter.arrived == null || userSuppliedArrivalFilter.arrived == false)
                && (userSuppliedArrivalFilter.remove == null || userSuppliedArrivalFilter.remove == false)) {
                logger.fine("PreRegistrationArrivals.process(), No action specified in this particular user supplied arrival filter");
                continue;
            }
            // For each row in the table, examine each column element for matches or rejection, and after all, check either arrived or remove
            for (WebElement arrivalsTableRow : arrivalsTableRows) {
                List<WebElement> arrivalsTableColumns = null;
                try {
                    // do we need to do a Wait on the next line?
                    // It's failed about 3 times today 11/28/18, but works other times.  Getting "stale element"
                    Utilities.sleep(2555); // new 11/29/18  Really hate to set this so high, since we're in a loop.  But something strange is happening so trying this.
                    arrivalsTableColumns = arrivalsTableRow.findElements(By.cssSelector("td"));
                }
                catch (StaleElementReferenceException e) { // this happens sometimes.  Why?
                    logger.warning("Stale element exception for getting columns from " + arrivalsTableRow.getText() + " e: " + getMessageFirstLine(e));
                    continue;
                }
                catch (Exception e) {
                    logger.warning("Couldn't get columns.  e: " + getMessageFirstLine(e));
                    continue;
                }

                // I think there's a timing issue that occurs making arrivalsTableColumns, so see if this helps
                arrivalsTableColumns.get(0).getText();

                //System.out.println("PreRegistrationArrivals.process(), trying to get the first column element for the row, and it should be Modify: " + modifyLink);
                // the logic on these user supplied values is
                // 1.  If it's specified with a value (not null, not blank, not "random"), but doesn't match, then go to next row. (loop, continue)
                // This means:
                // 1.  If not specified (null or blank), it's not rejected.  go on to the next filter (go down)
                // 2.  If it's specified "random", it means "match anything", so go on to next filter. (go down)
                if (userSuppliedArrivalFilter.ssn != null && !userSuppliedArrivalFilter.ssn.isEmpty() && !userSuppliedArrivalFilter.ssn.equalsIgnoreCase("random")) {
                    String tableRowSsn = arrivalsTableColumns.get(2).getText();
                    if (!userSuppliedArrivalFilter.ssn.endsWith(tableRowSsn.substring(5))) {
                        continue;
                    }
                }

                if (userSuppliedArrivalFilter.rank != null && !userSuppliedArrivalFilter.rank.isEmpty() && !userSuppliedArrivalFilter.rank.equalsIgnoreCase("random")) {
                    String tableRowRank = arrivalsTableColumns.get(3).getText();
                    if (!userSuppliedArrivalFilter.rank.equalsIgnoreCase(tableRowRank)) {
                        continue;
                    }
                }

                if (userSuppliedArrivalFilter.last != null && !userSuppliedArrivalFilter.last.isEmpty() && !userSuppliedArrivalFilter.last.equalsIgnoreCase("random")) {
                    String tableRowLast = arrivalsTableColumns.get(4).getText();
                    if (!userSuppliedArrivalFilter.last.equalsIgnoreCase(tableRowLast)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.first != null && !userSuppliedArrivalFilter.first.isEmpty() && !userSuppliedArrivalFilter.first.equalsIgnoreCase("random")) {
                    String tableRowFirst = arrivalsTableColumns.get(5).getText();
                    if (!userSuppliedArrivalFilter.first.equalsIgnoreCase(tableRowFirst)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.gender != null && !userSuppliedArrivalFilter.gender.isEmpty() && !userSuppliedArrivalFilter.gender.equalsIgnoreCase("random")) {
                    String tableRowGender = arrivalsTableColumns.get(6).getText();
                    if (!userSuppliedArrivalFilter.gender.substring(0,1).equalsIgnoreCase(tableRowGender)) { // they abbreviate in the table
                        continue;
                    }
                }
                // Flight Date in the table has date and time and "hrs" as in "12/24/2018 1315 hrs".  That's the correct format.
                // But user supplied flightDate may only have the date, which is incomplete for a match.  Or maybe it's in the right format.
                // This has nothing to do with the Pre-Registration page's flight date and flight time.  I mean, we don't look at that info,
                // because it might not be available.  So, what's best?  Ignore time components in the table's Flight Date?  That's easiest.
                // Or if user didn't specify all time components (" 1300 hrs"), then just do a match on date?
                // Or should we parse here and compare available components?
                // Or should we just do a "startsWith" and if true, it's a match?
                // Let's do a "startsWith" for now.  That means we do a tableValue.startsWith(userValue).

                if (userSuppliedArrivalFilter.flightDate != null && !userSuppliedArrivalFilter.flightDate.isEmpty() && !userSuppliedArrivalFilter.flightDate.equalsIgnoreCase("random")) {
                    String tableRowFlightDate = arrivalsTableColumns.get(7).getText();
                    if (!tableRowFlightDate.startsWith(userSuppliedArrivalFilter.flightDate)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.flightNumber != null && !userSuppliedArrivalFilter.flightNumber.isEmpty() && !userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase("random")) {
                    String tableRowFlightNumber = arrivalsTableColumns.get(8).getText();
                    if (!userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase(tableRowFlightNumber)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.location != null &&
                        !userSuppliedArrivalFilter.location.isEmpty() &&
                        !userSuppliedArrivalFilter.location.equalsIgnoreCase("random")) {
                    String tableRowLocation = arrivalsTableColumns.get(9).getText();
                    if (tableRowLocation != null && !tableRowLocation.isEmpty()) { // table may not have a value for location
                        if (!userSuppliedArrivalFilter.location.equalsIgnoreCase(tableRowLocation)) {
                            continue;
                        }
                    }
                }

                // This row matches, so what operations were specified?
                // These row/col things might be reversed, or misnamed?
                // But in any case, the locators become xpaths, I think, even if use cssSelector()
                // Arrived and Remove are basically toggles.  Click one and the other one becomes unclicked
                if (userSuppliedArrivalFilter.arrived != null && userSuppliedArrivalFilter.arrived) {
                    // Index out of bounds exception next line.  Says "Index: 10, Size 2"  How can that be a size of 2?
                    WebElement tableRowArrivedElement = null;
                    try {
                        tableRowArrivedElement = arrivalsTableColumns.get(10); // 11? // wrap with try/catch?
                    }
                    catch (Exception e) {
                        logger.severe("PreRegistrationArrivals.process(), problem getting column 10 of this row of the arrivals table. e: " + getMessageFirstLine(e));
                        continue;
                    }
                    By arrivedCheckBoxForThisRowBy = By.cssSelector("input"); // find the checkbox associated with the tr/row
                    WebElement inputElement = tableRowArrivedElement.findElement(arrivedCheckBoxForThisRowBy);
                    if (!inputElement.isSelected()) { // don't wanna do a flip
                        inputElement.click();
                    }
                    clickedArrived = true;
                }
                if (userSuppliedArrivalFilter.remove != null && userSuppliedArrivalFilter.remove) {
                    WebElement tableRowRemoveElement = arrivalsTableColumns.get(11); // 12?
                    WebElement inputElement = tableRowRemoveElement.findElement(By.cssSelector("input")); // another input?
                    if (!inputElement.isSelected()) {
                        inputElement.click();
                    }
                    clickedRemove = true;
                }
            }
        }

        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("    Wrote screenshot file " + fileName);
        }

        // click on UPDATE button here if there were any changes?
        if (clickedArrived || clickedRemove) {
            try {
                WebElement updateButton = Driver.driver.findElement(updateButtonBy);
                updateButton.click(); // if a removal was checked, then there will be an alert
            } catch (Exception e) {
                logger.fine("PreRegistrationArrivals.process(), couldn't get or click update button.");
                return false;
            }
            // Handle alert if there was a remove
            if (clickedRemove) {
                try {
                    // Accept alert which is always there because it's part of the Login button.  It's the one that says "By clicking OK, I confirm ... privacy statement ..."
                    (new WebDriverWait(driver, 10)).until(ExpectedConditions.alertIsPresent());
                    WebDriver.TargetLocator targetLocator = driver.switchTo();
                    Alert someAlert = targetLocator.alert();
                    someAlert.accept(); // this thing causes a lot of stuff to happen: alert goes away, and new page comes into view, hopefully.
                } catch (TimeoutException e) {
                    logger.severe("TmdsPortal.doLoginPage(), Either alert wasn't present, or if it was couldn't accept it.  e: " + Utilities.getMessageFirstLine(e));
                    return false;
                }
            }
            if (!Arguments.quiet) {
                System.out.println("    Saved Pre-registration arrivals record for patient " +
                        (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                        (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                        (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
                );
            }
        }
        else {
            logger.info("PreRegistrationArrivals.process(), did not find any patients to arrive or remove from preregistration arrivals list");
            if (!Arguments.quiet) {
                System.err.println("    ***Didn't find any patients to arrive or remove from arrivals.");
            }
            return false; // right thing to do?  I think so.
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }
}

// I could pull this out into its own class, but it's not a big deal.  More of just a struct only used by PreRegistrationArrivals
// Instances of this class get filled in by GSON.
class Arrival {
    public String ssn; // this could be specified as "123456789" or "*****6789" or just "6789", but probably the first.
    public String rank;
    public String last;
    public String first;
    public String gender;
    public String arrivalDate;
    public String flightDate; // format should be: "11/11/2018 1300 hrs"
    public String flightNumber;
    public String location;
    public Boolean arrived;
    public Boolean remove;

    public Arrival() {
        if (Arguments.template) {
            this.ssn = "";
            this.rank = "";
            this.first = "";
            this.last = "";
            this.gender = "";
            this.arrivalDate = "";
            this.flightDate = "";
            this.flightNumber = "";
            this.location = "";
            this.arrived = false;
            this.remove = false;
        }
    }
}
