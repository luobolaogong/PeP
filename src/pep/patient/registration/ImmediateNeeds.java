package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * This class is part of PatientInformation
 */
public class ImmediateNeeds {
    private static Logger logger = Logger.getLogger(ImmediateNeeds.class.getName());

    public Boolean randomizeSection;
    public Boolean shoot;
    public Boolean skipSave;
    public Boolean identificationCard;
    public Boolean orders;
    public Boolean sensitiveItems;
    public Boolean accessToCash;
    public String boots;
    public String blouse;
    public String trousers;
    public String headgear;
    public Boolean speakToChaplain;
    public String administrativeNotes;

    public ImmediateNeeds() {
        if (Arguments.template) {
            this.identificationCard = false; // really?  Shows up in template?
            this.orders = false;
            this.sensitiveItems = false;
            this.accessToCash = false;
            this.boots = "";
            this.blouse = "";
            this.trousers = "";
            this.headgear = "";
            this.speakToChaplain = false;
            this.administrativeNotes = "";
        }
    }


    private static By identificationCardBy = By.id("patientInfoBean.needs.identificationCardAssistanceNeeded1");
    private static By ordersBy = By.id("patientInfoBean.needs.ordersAssistanceNeeded1");
    private static By sensitiveItemsBy = By.id("patientInfoBean.needs.sensitiveItemsAssistanceNeeded1");
    private static By accessToCashBy = By.id("patientInfoBean.needs.accessToMoneyAssistanceNeeded1");
    private static By bootsBy = By.id("patientInfoBean.needs.bootSize");
    private static By blouseBy = By.id("patientInfoBean.needs.blouseSize");
    private static By trousersBy = By.id("patientInfoBean.needs.trouserSize");
    private static By headgearBy = By.id("patientInfoBean.needs.headGearSize");
    private static By speakToChaplainBy = By.id("patientInfoBean.needs.chaplinAssistanceNeeded1");
    private static By administrativeNotesBy = By.id("patientInfoBean.notes");

    /**
     * Fill in the Immediate Needs section of Patient Information page.
     * @param patient The patient this is about
     * @return Success or failure at being able to fill in the fields
     */
    public boolean process(Patient patient) {
        ImmediateNeeds immediateNeeds = patient.registration.patientInformation.immediateNeeds;

        // new 10/25/18
        if (!Arguments.quiet)
            System.out.println("    Processing Immediate Needs at " + LocalTime.now() + " for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );

        try {
            immediateNeeds.identificationCard = Utilities.processBoolean(identificationCardBy, immediateNeeds.identificationCard, immediateNeeds.randomizeSection, false);
            immediateNeeds.orders = Utilities.processBoolean(ordersBy, immediateNeeds.orders, immediateNeeds.randomizeSection, false);
            immediateNeeds.sensitiveItems = Utilities.processBoolean(sensitiveItemsBy, immediateNeeds.sensitiveItems, immediateNeeds.randomizeSection, false);
            immediateNeeds.accessToCash = Utilities.processBoolean(accessToCashBy, immediateNeeds.accessToCash, immediateNeeds.randomizeSection, false);

            immediateNeeds.boots = Utilities.processIntegerNumber(bootsBy, immediateNeeds.boots, 4, 16, immediateNeeds.randomizeSection, false);
            immediateNeeds.blouse = Utilities.processIntegerNumber(blouseBy, immediateNeeds.blouse, 2, 18, immediateNeeds.randomizeSection, false);
            immediateNeeds.trousers = Utilities.processIntegerNumber(trousersBy, immediateNeeds.trousers, 24, 48, immediateNeeds.randomizeSection, false);
            immediateNeeds.headgear = Utilities.processIntegerNumber(headgearBy, immediateNeeds.headgear, 8, 20, immediateNeeds.randomizeSection, false);

            immediateNeeds.speakToChaplain = Utilities.processBoolean(speakToChaplainBy, immediateNeeds.speakToChaplain, immediateNeeds.randomizeSection, false);

            immediateNeeds.administrativeNotes = Utilities.processText(administrativeNotesBy, immediateNeeds.administrativeNotes, Utilities.TextFieldType.PARAGRAPH, immediateNeeds.randomizeSection, false);

        }
        catch (Exception e) {
            logger.fine("Not sure what could go wrong, but surely something could.");
            return false;
        }
        if (this.shoot != null && this.shoot) {
            String fileName = ScreenShot.shoot(this.getClass().getSimpleName());
            if (!Arguments.quiet) System.out.println("      Wrote screenshot file " + fileName);
        }
        if (Arguments.pauseSection > 0) {
            Utilities.sleep(Arguments.pauseSection * 1000, "ImmediateNeeds");
        }
        return true;
    }
}
