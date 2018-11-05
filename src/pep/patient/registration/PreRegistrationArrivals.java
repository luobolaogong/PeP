package pep.patient.registration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Driver.driver;


public class PreRegistrationArrivals {
  private static Logger logger = Logger.getLogger(PreRegistrationArrivals.class.getName());
    public Boolean random; // not sure we really want random for this page.  Randomly "arrive" a patient?, randomly remove a patient?
    //public List<Arrival> arrivals = new ArrayList<>();
    public List<Arrival> arrivals; // these are specified in the JSON input file, and get loaded by GSON, right?

    private static By patientRegistrationMenuLinkBy = By.xpath("//li/a[@href='/tmds/patientRegistrationMenu.html']");
    //private static By PATIENT_PRE_REGISTRATION_MENU_LINK = By.xpath("//li/a[@href='/tmds/preReg.html']");  // only valid before clicking on main menu link, I think
    private static By patientPreRegistrationArrivalsMenuLinkBy = By.id("a_4"); // seems that this link changes after clicking on main menu link
    private static By updateButtonBy = By.xpath("//*[@id=\"patientPreRegArrivalForm\"]/table/tbody/tr[3]/td/input");
    private static By arrivalsTableBy = By.xpath("//*[@id=\"tr\"]/tbody");

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
        Utilities.sleep(1555);
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientPreRegistrationArrivalsMenuLinkBy);
        if (!navigated) {
            logger.fine("PreRegistrationArrivals.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2
        }
        // Check that the arrivals table is there
        WebElement arrivalsTable = null;
        try {
            arrivalsTable = (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(arrivalsTableBy));
        }
        catch (Exception e) {
            logger.fine("PreRegistrationArrivals.process(), could not get arrivals table.  Getting out, returning false.  Exception: " + arrivalsTableBy);
            return false;
        }
        // Get all the rows (tr elements) into a list
        List<WebElement> arrivalsTableRows = null;
        try {
            arrivalsTableRows = arrivalsTable.findElements(By.cssSelector("tr"));
        }
        catch (Exception e) {
            logger.fine("PreRegistrationArrivals.process(), Couldn't get any rows from the table.  getting out, returning false.  Exception:" + e.getMessage());
            return false; // no elements in table.
        }
        // There are three "lists" of things to consider:
        // 1. The user supplied one or more Arrival search criteria objects in the JSON file.  (I'd think usually there'd be only one.)
        // 2. Each user supplied Arrival search criteria object contains zero or more search filters, like ssn, or last name. (Usually ssn, first, last)
        // 3. The table contains zero or more patients that were pre-registered but not yet arrived.  (Usually we only want to arrive one of the patients.)
        //
        // This pseudo is not strictly followed.
        // And the following code is not heavily tested at all.
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
        //
        boolean clickedArrived = false;
        boolean clickedRemove = false;
        // For each user supplied Arrival search criteria objects (usually 1
        for (Arrival userSuppliedArrivalFilter : arrivals) {
            // If the object does not contain either operation (arrive or remove), skip this object (uncommon, since doesn't make sense)
            if ((userSuppliedArrivalFilter.arrived == null || userSuppliedArrivalFilter.arrived == false)
                && (userSuppliedArrivalFilter.remove == null || userSuppliedArrivalFilter.remove == false)) {
                logger.fine("PreRegistrationArrivals.process(), No action specified in this particular user supplied arrival filter");
                continue;
            }
            // For each row in the table
            for (WebElement arrivalsTableRow : arrivalsTableRows) {
                List<WebElement> arrivalsTableColumns = arrivalsTableRow.findElements(By.cssSelector("td"));  //*[@id="tr"]/tbody/tr[1]/td[3]    that's the ssn, index 3 of all rows

                if (userSuppliedArrivalFilter.ssn != null) {
                    String tableRowSsn = arrivalsTableColumns.get(2).getText();
                    if (!userSuppliedArrivalFilter.ssn.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.ssn.endsWith(tableRowSsn.substring(5))) {
                        continue;
                    }
                }

                if (userSuppliedArrivalFilter.rank != null) {
                    String tableRowRank = arrivalsTableColumns.get(3).getText();
                    if (!userSuppliedArrivalFilter.rank.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.rank.equalsIgnoreCase(tableRowRank)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.last != null) {
                    String tableRowLast = arrivalsTableColumns.get(4).getText();
                    if (!userSuppliedArrivalFilter.last.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.last.equalsIgnoreCase(tableRowLast)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.first != null) {
                    String tableRowFirst = arrivalsTableColumns.get(5).getText();
                    if (!userSuppliedArrivalFilter.first.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.first.equalsIgnoreCase(tableRowFirst)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.gender != null) {
                    String tableRowGender = arrivalsTableColumns.get(6).getText();
                    if (!userSuppliedArrivalFilter.gender.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.gender.equalsIgnoreCase(tableRowGender)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.flightDate != null) {
                    String tableRowFlightDate = arrivalsTableColumns.get(7).getText();
                    String flightDate = (userSuppliedArrivalFilter.flightDate.length() < 16) ? userSuppliedArrivalFilter.flightDate : userSuppliedArrivalFilter.flightDate.substring(0,15);
                    String tableFlightDate = (tableRowFlightDate.length() < 16) ? tableRowFlightDate : tableRowFlightDate.substring(0,15);
                    if (!flightDate.equalsIgnoreCase("random")
                            && !tableFlightDate.startsWith(flightDate)) {
//                    if (!userSuppliedArrivalFilter.flightDate.equalsIgnoreCase("random")
//                            && !tableRowFlightDate.substring(0,15).startsWith(userSuppliedArrivalFilter.flightDate.substring(0,15))) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.flightNumber != null) {
                    String tableRowFlightNumber = arrivalsTableColumns.get(8).getText();
                    if (!userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase(tableRowFlightNumber)) {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.location != null) {
                    String tableRowLocation = arrivalsTableColumns.get(9).getText();
                    if (!userSuppliedArrivalFilter.location.equalsIgnoreCase("random")
                        && !userSuppliedArrivalFilter.location.equalsIgnoreCase(tableRowLocation)) {
                        continue;
                    }
                }

                // This row matches, so what operations were specified?

                // Arrived and Remove are basically toggles.  Click one and the other one becomes unclicked
                if (userSuppliedArrivalFilter.arrived != null && userSuppliedArrivalFilter.arrived) {
                    WebElement tableRowArrivedElement = arrivalsTableColumns.get(10);
                    WebElement inputElement = tableRowArrivedElement.findElement(By.cssSelector("input"));
                    if (!inputElement.isSelected()) { // don't wanna do a flip
                        inputElement.click();
                    }
                    clickedArrived = true;
                }
                if (userSuppliedArrivalFilter.remove != null && userSuppliedArrivalFilter.remove) {
                    WebElement tableRowRemoveElement = arrivalsTableColumns.get(11);
                    WebElement inputElement = tableRowRemoveElement.findElement(By.cssSelector("input"));
                    if (!inputElement.isSelected()) {
                        inputElement.click();
                    }
                    clickedRemove = true;
                }
            }
        }
        // click on UPDATE button here if there were any changes?
        if (clickedArrived || clickedRemove) {
            try {
                WebElement updateButton = Driver.driver.findElement(updateButtonBy);
                updateButton.click(); // if a removal was checked, then there will be an alert
            }
            catch(Exception e) {
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
                    if (Arguments.debug)
                        System.out.println("TmdsPortal.doLoginPage(), Either alert wasn't present, or if it was couldn't accept it.");
                    return false;
                }
            }
        }
        if (Arguments.pagePause > 0) {
            Utilities.sleep(Arguments.pagePause * 1000);
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
    public String flightDate;
    public String flightNumber;
    public String location;
    public Boolean arrived;
    public Boolean remove;

    public Arrival() {
        if (Arguments.template) {
            this.ssn = null;
            this.rank = null;
            this.first = null;
            this.last = null;
            this.gender = null;
            this.arrivalDate = null;
            this.flightDate = null;
            this.flightNumber = null;
            this.location = null;
            this.arrived = false;
            this.remove = false;
        }
    }
}
