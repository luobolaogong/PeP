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

/**
 * This class represents the handling of the Pre-Registration pag
 */
public class PreRegistrationArrivals {
    private static Logger logger = Logger.getLogger(PreRegistrationArrivals.class.getName());
    public Boolean randomizeSection;
    public Boolean shoot;
    public List<Arrival> arrivals;

    private static By patientRegistrationMenuLinkBy = By.cssSelector("a[href='/tmds/patientRegistrationMenu.html']");
    private static By patientPreRegistrationArrivalsMenuLinkBy = By.cssSelector("a[href='/tmds/patientPreRegArrivals.html']");
    private static By updateButtonBy = By.xpath("//input[@value='UPDATE']");
    private static By arrivalsTableBy = By.xpath("//*[@id='tr']/tbody");
    private static By preRegArrivalsFormBy = By.id("patientPreRegArrivalForm");

    public PreRegistrationArrivals() {
        if (Arguments.template) {
            this.arrivals = Arrays.asList(new Arrival());
        }
    }

    /**
     * Process each row in the table and mark them as specified in the input file, and then execute.
     * This page has no Patient Search section at the top.  It contains a table/list of patients,
     * and each one has a Modify link, an Arrived check box and a "Remove" check box.
     *
     * The page also contains an "UPDATE" button.
     *
     * If you click on Modify link you go back to the Pre-registration page.
     * I think you have to check "Arrived" in order for that patient to be able to be accessed by
     * New Patient Reg.  However, you can access the patient with Update Patient, strangely.
     *
     * What this page will probably be used for is merely to check the "ARRIVED" box.  But we should
     * also support the "REMOVE" box.  I'm not sure we should support the Modify link, because the
     * user should just go directly to the Pre-registration page or Update Patient page if they want
     * to modify anything.
     *
     * The corresponding JSON input file section would contain what?  Two elements for the check boxes.
     * I think that's all that's required.  We could support the Modify link by creating an element
     * for that if necessary.
     *
     * But these elements are in a table, so the element selectors are more challenging.
     *
     * Plus, we have to have some patient identification information to know which row in the table
     * to work on.  The reasonable columns that could be used would include SSN (last 4), Last name,
     * First name.  But you could also use gender and flight date and flight number and rank.
     * Flight Date would seem to make the most easy match.
     *
     * So the JSON section could include any of those fields to be used for searching the table.
     * These elements can only be read, not clicked on, but selectors should still work for them.
     *
     * I think you have to search the
     * entire table, because you don't know if the provided fields would be a good enough match to insure
     * you have the patient.  For example, you don't want to just search on Gender, or the last 4 of SSN.
     * It should probably be a combination of ssn, last, first, and then optionally flight date.
     *
     * This is the element containing the rows:
     *      //*[@id="tr"]/tbody
     * under it (inside it) are the set of <tr> elements
     * Loop through them, applying the filters provided (ssn, last, first, whatever else)
     * when a match is found, save it to a list, along with the selectors for the two check boxes.
     * When done, check the list to see how many matches.
     * If none, exit.  If more than one, report and exit.  If exactly one, click its boxes.
     * @param patient The patient this belongs to.
     * @return success or failure at doing the processing
     */
    public boolean process(Patient patient) {
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
        //
        // Navigate to the Pre-Registration Arrivals page and check that the arrivals table is there
        //
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientPreRegistrationArrivalsMenuLinkBy);
        if (!navigated) {
            logger.fine("PreRegistrationArrivals.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2
        }

        WebElement arrivalsTable;
        try {
            // It's possible there is no table, because no one preregistered.  Need to account for that.  This doesn't.
            // Instead of sleep, maybe should do some other check to see if the table is done loading
            Utilities.sleep(555, "PreRegistrationArrivals.process(), gunna wait 5 sec max for the form, I think"); // 3/12/19
            Utilities.waitForRefreshedVisibility(preRegArrivalsFormBy,  5, "PreRegistrationArrivals.(), form"); // experiment 12/12/18
            Utilities.sleep(555, "PreRegistrationArrivals.process(), waiting before check on arrivals table");
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
        boolean clickedArrived = false;
        boolean clickedRemove = false;
        // For each user supplied Arrival search criteria objects (usually 1) check either arrived or remove on their row.
        for (Arrival userSuppliedArrivalFilter : arrivals) { // of course "arrivals" is an array of Arrival objects specified in JSON file
            if ((userSuppliedArrivalFilter.arrived == null || !userSuppliedArrivalFilter.arrived)
                && (userSuppliedArrivalFilter.remove == null || !userSuppliedArrivalFilter.remove)) {
                logger.fine("PreRegistrationArrivals.process(), No action specified in this particular user supplied arrival filter");
                continue;
            }
            // For each row in the table, examine each column element for matches or rejection, and after all, check either arrived or remove
            for (WebElement arrivalsTableRow : arrivalsTableRows) {
                List<WebElement> arrivalsTableColumns = null;
                try {
                    Utilities.sleep(2555, "PreRegistrationArrivals.process(), waiting before checking for td in arrivals table");
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
                arrivalsTableColumns.get(0).getText();
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
                // This has nothing to do with the Pre-Registration page's flight date and flight time.  We don't look at that info,
                // because it might not be available.
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
                    if (tableRowLocation != null && !tableRowLocation.isEmpty()) {
                        if (!userSuppliedArrivalFilter.location.equalsIgnoreCase(tableRowLocation)) {
                            continue;
                        }
                    }
                }
                // This row matches. Arrived and Remove are basically toggles.  Click one and the other one becomes unclicked
                if (userSuppliedArrivalFilter.arrived != null && userSuppliedArrivalFilter.arrived) {
                    WebElement tableRowArrivedElement;
                    try {
                        tableRowArrivedElement = arrivalsTableColumns.get(10);
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
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "PreRegistrationArrivals.process(), requested sleep for page.");
        }
        return true;
    }
}

class Arrival {
    public String ssn;
    public String rank;
    public String last;
    public String first;
    public String gender;
    public String arrivalDate;
    public String flightDate;
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
