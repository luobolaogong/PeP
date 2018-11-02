package pep.patient.registration;

import org.apache.xpath.Arg;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.treatment.Treatment;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pep.utilities.AutomationUtils.findElement;
import static pep.utilities.Driver.driver;


public class PreRegistrationArrivals {
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

        Utilities.sleep(1555);
        boolean navigated = Utilities.myNavigate(patientRegistrationMenuLinkBy, patientPreRegistrationArrivalsMenuLinkBy);
        if (!navigated) {
            if (Arguments.debug) System.out.println("PreRegistrationArrivals.process(), Failed to navigate!!!");
            return false; // fails: level 4 demo: 1, gold 2
        }

        WebElement arrivalsTable = null;
        try {
            arrivalsTable = (new WebDriverWait(Driver.driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(arrivalsTableBy));
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("PreRegistrationArrivals.process(), could not get arrivals table.  Getting out, returning false.  Exception: " + arrivalsTableBy);
            return false;
        }

        List<WebElement> arrivalsTableRows = null;
        try {
            arrivalsTableRows = arrivalsTable.findElements(By.cssSelector("tr"));
        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("PreRegistrationArrivals.process(), Couldn't get any rows from the table.  getting out, returning false.  Exception:" + e.getMessage());
            return false; // no elements in table.
        }

        boolean clickedArrived = false;
        boolean clickedRemove = false;
        for (Arrival userSuppliedArrivalFilter : arrivals) {
            if (Arguments.debug) System.out.println("Looking at a user supplied arrival filter");
            // If the user didn't specify either checkbox, then skip this user supplied arrival filter
            if ((userSuppliedArrivalFilter.arrived == null || userSuppliedArrivalFilter.arrived == false)
                && (userSuppliedArrivalFilter.remove == null || userSuppliedArrivalFilter.remove == false)) {
                if (Arguments.debug) System.out.println("PreRegistrationArrivals.process(), No action specified in this particular user supplied arrival filter");
                continue;
            }
            // Go through each row in the table to see if the row matches all the supplied "filters", and if so, check a box the user specified.
            // following logic is wrong.
            for (WebElement arrivalsTableRow : arrivalsTableRows) {
                List<WebElement> arrivalsTableColumns = arrivalsTableRow.findElements(By.cssSelector("td"));  //*[@id="tr"]/tbody/tr[1]/td[3]    that's the ssn, index 3 of all rows

                boolean match = false;
                if (userSuppliedArrivalFilter.ssn != null && !userSuppliedArrivalFilter.ssn.isEmpty() && !userSuppliedArrivalFilter.ssn.equalsIgnoreCase("random")) {
                    String tableRowSsn = arrivalsTableColumns.get(2).getText();
                    if (userSuppliedArrivalFilter.ssn.endsWith(tableRowSsn.substring(5))) { // wrong
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.rank != null && !userSuppliedArrivalFilter.rank.isEmpty() && !userSuppliedArrivalFilter.rank.equalsIgnoreCase("random")) {
                    String tableRowRank = arrivalsTableColumns.get(3).getText();
                    if (userSuppliedArrivalFilter.rank.equalsIgnoreCase(tableRowRank)) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                System.out.println("user supplied last is " + userSuppliedArrivalFilter.last);
                //if (userSuppliedArrivalFilter.last != null && !userSuppliedArrivalFilter.last.isEmpty() && !userSuppliedArrivalFilter.last.equalsIgnoreCase("random")) {
                if (userSuppliedArrivalFilter.last != null && !userSuppliedArrivalFilter.last.isEmpty()) {
                    String tableRowLast = arrivalsTableColumns.get(4).getText();
                    System.out.println("tableRowLast is " + tableRowLast);
                    if (userSuppliedArrivalFilter.last.equalsIgnoreCase(tableRowLast) || userSuppliedArrivalFilter.last.equalsIgnoreCase("random")) { // added random 11/2/18
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.first != null && !userSuppliedArrivalFilter.first.isEmpty() && !userSuppliedArrivalFilter.first.equalsIgnoreCase("random")) {
                    String tableRowFirst = arrivalsTableColumns.get(5).getText();
                    if (userSuppliedArrivalFilter.first.equalsIgnoreCase(tableRowFirst)) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.gender != null && !userSuppliedArrivalFilter.gender.isEmpty() && !userSuppliedArrivalFilter.gender.equalsIgnoreCase("random")) {
                    String tableRowGender = arrivalsTableColumns.get(6).getText();
                    if (userSuppliedArrivalFilter.gender.equalsIgnoreCase(tableRowGender)) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.flightDate != null && !userSuppliedArrivalFilter.flightDate.isEmpty() && !userSuppliedArrivalFilter.flightDate.equalsIgnoreCase("random")) {
                    //if (arrival.flightDate.equalsIgnoreCase(flightDate)) {
                    //if (arrival.flightDate.substring(0,14).equalsIgnoreCase(flightDate.substring(0,14))) {
                    //System.out.println("->" + flightDate.substring(0,15) + "<-");
                    String tableRowFlightDate = arrivalsTableColumns.get(7).getText();
                    if (tableRowFlightDate.substring(0,15).startsWith(userSuppliedArrivalFilter.flightDate.substring(0,15))) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.flightNumber != null && !userSuppliedArrivalFilter.flightNumber.isEmpty() && !userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase("random")) {
                    String tableRowFlightNumber = arrivalsTableColumns.get(8).getText();
                    if (userSuppliedArrivalFilter.flightNumber.equalsIgnoreCase(tableRowFlightNumber)) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (userSuppliedArrivalFilter.location != null && !userSuppliedArrivalFilter.location.isEmpty() && !userSuppliedArrivalFilter.location.equalsIgnoreCase("random")) {
                    String tableRowLocation = arrivalsTableColumns.get(9).getText();
                    if (userSuppliedArrivalFilter.location.equalsIgnoreCase(tableRowLocation)) {
                        match = true;
                    } else {
                        continue;
                    }
                }
                if (!match) {
                    continue;
                }
                // This row matches, so what operations were specified?

                // Arrived and Remove are basically toggles.  Click one and the other one becomes unclicked
                if (userSuppliedArrivalFilter.arrived != null && userSuppliedArrivalFilter.arrived) {
                    //arrivedElement.clear();
                    WebElement tableRowArrivedElement = arrivalsTableColumns.get(10);
                    WebElement inputElement = tableRowArrivedElement.findElement(By.cssSelector("input"));
                    //System.out.println(tableRowArrivedElement.isSelected());
                    //System.out.println(inputElement.isSelected());
                    if (!inputElement.isSelected()) {
                        //tableRowArrivedElement.click(); // no, we cannot do a flip, if it was previoiusly checked
                        inputElement.click(); // no, we cannot do a flip, if it was previoiusly checked
                    }
                    clickedArrived = true;
                }
                if (userSuppliedArrivalFilter.remove != null && userSuppliedArrivalFilter.remove) {
                    //removeElement.clear();
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
                if (Arguments.debug) System.out.println("PreRegistrationArrivals.process(), couldn't get or click update button.");
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
