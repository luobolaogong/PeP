package pep.patient.registration;

import org.openqa.selenium.By;
import pep.patient.Patient;
import pep.utilities.Arguments;
import pep.utilities.Utilities;

public class ImmediateNeeds {

    public Boolean random;
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

    public boolean process(Patient patient) {
        ImmediateNeeds immediateNeeds = patient.patientRegistration.patientInformation.immediateNeeds;
        // Many of the following are bad guesses for random values
        // do address with state
        // do full name

        // test:
        if (immediateNeeds.random == null) {
            immediateNeeds.random = (this.random == null) ? false : this.random;
        }

        try {
            immediateNeeds.identificationCard = Utilities.processBoolean(identificationCardBy, immediateNeeds.identificationCard, immediateNeeds.random, false);
            immediateNeeds.orders = Utilities.processBoolean(ordersBy, immediateNeeds.orders, immediateNeeds.random, false);
            immediateNeeds.sensitiveItems = Utilities.processBoolean(sensitiveItemsBy, immediateNeeds.sensitiveItems, immediateNeeds.random, false);
            immediateNeeds.accessToCash = Utilities.processBoolean(accessToCashBy, immediateNeeds.accessToCash, immediateNeeds.random, false);

//            immediateNeeds.boots = Utilities.processText(bootsBy, immediateNeeds.boots, Utilities.TextFieldType.TITLE, immediateNeeds.random, false);
//            immediateNeeds.blouse = Utilities.processText(blouseBy, immediateNeeds.blouse, Utilities.TextFieldType.TITLE, immediateNeeds.random, false);
//            immediateNeeds.trousers = Utilities.processText(trousersBy, immediateNeeds.trousers, Utilities.TextFieldType.TITLE, immediateNeeds.random, false);
//            immediateNeeds.headgear = Utilities.processText(headgearBy, immediateNeeds.headgear, Utilities.TextFieldType.TITLE, immediateNeeds.random, false);
            immediateNeeds.boots = Utilities.processIntegerNumber(bootsBy, immediateNeeds.boots, 4, 16, immediateNeeds.random, false);
            immediateNeeds.blouse = Utilities.processIntegerNumber(blouseBy, immediateNeeds.blouse, 2, 18, immediateNeeds.random, false);
            immediateNeeds.trousers = Utilities.processIntegerNumber(trousersBy, immediateNeeds.trousers, 24, 48, immediateNeeds.random, false);
            immediateNeeds.headgear = Utilities.processIntegerNumber(headgearBy, immediateNeeds.headgear, 8, 20, immediateNeeds.random, false);

            immediateNeeds.speakToChaplain = Utilities.processBoolean(speakToChaplainBy, immediateNeeds.speakToChaplain, immediateNeeds.random, false);

            immediateNeeds.administrativeNotes = Utilities.processText(administrativeNotesBy, immediateNeeds.administrativeNotes, Utilities.TextFieldType.SHORT_PARAGRAPH, immediateNeeds.random, false);

        }
        catch (Exception e) {
            if (Arguments.debug) System.out.println("Not sure what could go wrong, but surely something could.");
            return false;
        }
        return true;
    }
}
