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
    public Demographics demographics;

    // It will be Flight (level 4) or ArrivalLocsationSection (levels 1,2,3)
    public Flight flight;
    public ArrivalLocation arrivalLocation;

    public InjuryIllness injuryIllness;
    public Location location;
    public Departure departure;

    static final By SUBMIT_BUTTON = By.xpath("//input[@id='commit']");

    //boolean skipRegistration;

    public PreRegistrationArrivals() {
        if (Arguments.template) {
            this.random = null;
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.arrivalLocation = new ArrivalLocation();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
            this.departure = new Departure();
        }
    }
}