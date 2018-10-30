package pep.patient.registration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.Utilities;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class PreRegistrationArrivals {
    public Boolean random;
    public String firstName;
    public String lastName;
    public String ssnLast4;
    public String arrivalDate;
    public String gender;
    public String flightDate;
    public String flightNumber;
    public String rank;

    public PreRegistrationArrivals() {
        if (Arguments.template) {
            this.firstName = "";
            this.lastName = "";
            this.ssnLast4 = "";
            this.arrivalDate = "";
            this.gender = "";
            this.flightDate = "";
            this.flightNumber = "";
            this.rank = "";
        }
    }
}