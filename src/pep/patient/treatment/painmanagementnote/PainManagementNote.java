package pep.patient.treatment.painmanagementnote;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.patient.Patient;
import pep.patient.treatment.painmanagementnote.allergy.Allergy;
import pep.patient.treatment.painmanagementnote.clinicalnote.ClinicalNote;
import pep.patient.treatment.painmanagementnote.procedurenote.*;
import pep.patient.treatment.painmanagementnote.procedurenote.continuousperipheralnerveblock.ContinuousPeripheralNerveBlock;
import pep.patient.treatment.painmanagementnote.procedurenote.epiduralcatheter.EpiduralCatheter;
import pep.patient.treatment.painmanagementnote.procedurenote.ivpca.IvPca;
import pep.patient.treatment.painmanagementnote.procedurenote.singleperipheralnerveblock.SinglePeripheralNerveBlock;
import pep.patient.treatment.painmanagementnote.transfernote.TransferNote;
import pep.utilities.Arguments;
import pep.utilities.Driver;
import pep.utilities.ScreenShot;
import pep.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

/**
 *  This class represents various major subsections: allergies, procedure notes, clinical notes, and transfer notes
 *  and tries to organize the processing of them.
 */
public class PainManagementNote {
    private static Logger logger = Logger.getLogger(PainManagementNote.class.getName()); // multiple?
    public Boolean randomizeSection;
    public Boolean shoot;
    public List<Allergy> allergies;
    public List<ProcedureNote> procedureNotes;
    public List<ClinicalNote> clinicalNotes;
    public List<TransferNote> transferNotes;

    private static By patientTreatmentTabBy = By.cssSelector("a[href='/tmds/patientTreatment.html']");
    private static By painManagementNoteLinkBy = By.cssSelector("a[href='/bm-app/painManagement.html']");
    private static By painManagementNoteLink2By = By.cssSelector("a[href='/bm-app/painManagementNote.html']");
    private static By ssnField = By.id("ssn");
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//button[text()='Search For Patient']");
    private static By painManagementNoteSearchForPatientMessageLocatorBy = By.id("msg");
    private static By demographicTableBy = By.id("patient-demographics-container");
    private static By painManagementSearchForPatientSectionBy = By.id("patient-demographics-container");


    /**
     * This constructor merely sets up empty arrays as lists for each of the subsections.  They are arrays
     * because the user can just keep clicking on the submit button and create more, or somehow have an array
     * of these subsections.  Like multiple allergies.
     */
    public PainManagementNote() {
        if (Arguments.template) {
            this.allergies = Arrays.asList(new Allergy());
            this.procedureNotes = Arrays.asList(new ProcedureNote());
            this.clinicalNotes = Arrays.asList(new ClinicalNote());
            this.transferNotes = Arrays.asList(new TransferNote());
        }
        if (codeBranch != null && codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn");
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            painManagementNoteSearchForPatientMessageLocatorBy = By.xpath("//*[@id='j_id286']/table/tbody/tr/td/span");
            demographicTableBy = By.id("demographicTable");
            painManagementSearchForPatientSectionBy = By.id("patientSearchForm"); // looks right for TEST, but fails?  I changed the gold version
            painManagementNoteLinkBy = By.xpath("//li/a[@href='/bm-app/pain/painManagement.seam']");
        }
    }


    /**
     * This page contains Pain Management subsection management for Allergy, Procedure Notes, Clinical Note, and Transfer Note.
     * Procedure Notes itself has 4 parts.
     * If this patient is a random, then we want at least one of the 4 sections to be filled in.
     *
     * @param patient the patient to process
     * @return status for processing the subsections
     */
    public boolean process(Patient patient) {
        if (!Arguments.quiet)
            System.out.println("    Processing Pain Management Note for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, painManagementNoteLinkBy, painManagementNoteLink2By); // strange
        if (!navigated) {
            return false;
        }
        //
        // Before can do anything here, we need to insure the patient is registered/found.
        //
        try {
            Utilities.waitForVisibility(painManagementSearchForPatientSectionBy, 15, "PainManagementNote.process()");
        }
        catch (TimeoutException e) {
            logger.fine("Didn't see a Search For Patient section yet, so we may not be where we expect to be.  Nav failed even though says it succeeded?");
            return false;
        }
        catch (Exception e) {
            logger.fine("Did not see a Search For Patient section yet, so we may not be where we expect to be.  Nav failed even though says it succeeded?");
            return false;
        }

        boolean patientFound = isPatientFound(
                patient.patientSearch.ssn,
                patient.patientSearch.firstName,
                patient.patientSearch.lastName,
                patient.patientSearch.traumaRegisterNumber
        );
        if (!patientFound) {
            logger.fine("Cannot do a pain management note if patient not found.  Did Registration fail and wasn't detected?");
            logger.fine("Was looking for patient" + patient.patientSearch.firstName
                    + " " +    patient.patientSearch.lastName
                    + " " + patient.patientSearch.ssn
                    + " " +     patient.patientSearch.traumaRegisterNumber);
            return false;
        }
        //
        // The patient was found, so if this is page/section is meant to be randomized, select the sections to process.
        // Logic could be better here.
        //
        boolean doAllergy = false, doPn = false, doCn = false, doTn = false;
        if ((this.randomizeSection != null && this.randomizeSection)) {
            int percent = Utilities.random.nextInt(100);
            if (percent > 50) {
                doAllergy = true;
            }
            if (percent > 70) {
                doPn = true;
            }
            if (percent > 20) {
                doCn = true;
            }
            if (percent > 80) {
                doTn = true;
            }
            if (!doAllergy && !doPn && !doCn && !doTn) {
                doPn = true;
            }
        }
        if (this.procedureNotes != null) {
            doPn = true;
        }
        if (this.allergies != null) {
            doAllergy = true;
        }
        if (this.clinicalNotes != null) {
            doCn = true;
        }
        if (this.transferNotes != null) {
            doTn = true;
        }
        //
        // Process Allergies if JSON section was provided, or if should be randomized.
        //
        List<Allergy> allergies = this.allergies;
        if (allergies == null && (this.randomizeSection != null && this.randomizeSection) && doAllergy) {
            int nRandomAllergies = Utilities.random.nextInt(2) + 1;
            allergies = new ArrayList<Allergy>(nRandomAllergies);
            this.allergies = allergies;
            for (int ctr = 0; ctr < nRandomAllergies; ctr++) {
                Allergy allergy = new Allergy();
                allergy.randomizeSection = this.randomizeSection;
                allergy.shoot = this.shoot;
                this.allergies.add(allergy);
            }
        }
        if (allergies != null) {
            for (Allergy allergy : allergies) {
                if (allergy.randomizeSection == null) { // this should have been done before now.
                    allergy.randomizeSection = this.randomizeSection; // removed setting to false if null
                }
                if (allergy.shoot == null) { // this should have been done before now.
                    allergy.shoot = this.shoot;
                }
                boolean processSucceeded = allergy.process(patient);
                if (!processSucceeded && !Arguments.quiet) System.err.println("      ***Failed to process Allergy for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }
        //
        // If section is random, load up some procedure notes subsections to be processed.
        //
        List<ProcedureNote> procedureNotes = this.procedureNotes;
        if (procedureNotes == null && (this.randomizeSection != null && this.randomizeSection) && doPn) {
            int nRandomProcedureNotes = 1;  // TODO: Figure out the random thing later.  1 is right for now.
            procedureNotes = new ArrayList<ProcedureNote>(nRandomProcedureNotes);
            this.procedureNotes = procedureNotes;
            for (int ctr = 0; ctr < nRandomProcedureNotes; ctr++) {
                ProcedureNote procedureNote = new ProcedureNote();
                procedureNote.randomizeSection = this.randomizeSection;
                procedureNote.shoot = this.shoot;
                //
                // If section is random it would be good if we didn't repeat here.  If two, then two different ones, although
                // I think it's more likely that only one pain thing would happen per treatment.
                //
                int painSelection = Utilities.random.nextInt(4);
                switch (painSelection) {
                    case 0:
                        procedureNote.singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
                        procedureNote.singlePeripheralNerveBlock.randomizeSection = procedureNote.randomizeSection;
                        procedureNote.singlePeripheralNerveBlock.shoot = procedureNote.shoot;
                        break;
                    case 1:
                        procedureNote.continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
                        procedureNote.continuousPeripheralNerveBlock.randomizeSection = procedureNote.randomizeSection;
                        procedureNote.continuousPeripheralNerveBlock.shoot = procedureNote.shoot;
                        break;
                    case 2:
                        procedureNote.epiduralCatheter = new EpiduralCatheter();
                        procedureNote.epiduralCatheter.randomizeSection = procedureNote.randomizeSection;
                        procedureNote.epiduralCatheter.shoot = procedureNote.shoot;
                        break;
                    case 3:
                        procedureNote.ivPca = new IvPca();
                        procedureNote.ivPca.randomizeSection = procedureNote.randomizeSection;
                        procedureNote.ivPca.shoot = procedureNote.shoot;
                        break;
                }
                this.procedureNotes.add(procedureNote);
            }
        }
        int nErrors = 0;
        //
        // Process procedure notes if JSON section was provided, or generated above.
        //
        if (procedureNotes != null) {
            for (ProcedureNote procedureNote : procedureNotes) {
                if (procedureNote.randomizeSection == null) {
                    procedureNote.randomizeSection = this.randomizeSection;
                }
                if (procedureNote.shoot == null) {
                    procedureNote.shoot = this.shoot;
                }
                boolean processSucceeded = procedureNote.process(patient, this);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("      ***Failed to process Procedure Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Process clinical notes if JSON section was provided, or if should be randomized.
        //
        List<ClinicalNote> clinicalNotes = this.clinicalNotes;
        if (clinicalNotes == null && (this.randomizeSection != null && this.randomizeSection) && doCn) {
            int nRandomClinicalNotes = Utilities.random.nextInt(2) + 1;
            clinicalNotes = new ArrayList<>(nRandomClinicalNotes);
            this.clinicalNotes = clinicalNotes;
            for (int ctr = 0; ctr < nRandomClinicalNotes; ctr++) {
                ClinicalNote clinicalNote = new ClinicalNote();
                clinicalNote.randomizeSection = this.randomizeSection;
                clinicalNote.shoot = this.shoot;
                this.clinicalNotes.add(clinicalNote);
            }
        }
        if (clinicalNotes != null) {
            for (ClinicalNote clinicalNote : clinicalNotes) {
                if (clinicalNote.randomizeSection == null) {
                    clinicalNote.randomizeSection = this.randomizeSection;
                    clinicalNote.shoot = this.shoot;
                }

                boolean processSucceeded = clinicalNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to process Clinical Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }
        //
        // Process transfer notes if JSON section was provided, or if should be randomized.
        //
        List<TransferNote> transferNotes = this.transferNotes;
        if (transferNotes == null && (this.randomizeSection != null && this.randomizeSection == true) && doTn) {
            int nRandomTransferNotes = Utilities.random.nextInt(2) + 1;
            transferNotes = new ArrayList<>(nRandomTransferNotes);
            this.transferNotes = transferNotes;
            for (int ctr = 0; ctr < nRandomTransferNotes; ctr++) {
                TransferNote transferNote = new TransferNote();
                transferNote.randomizeSection = this.randomizeSection;
                transferNote.shoot = this.shoot;
                this.transferNotes.add(transferNote);
            }
        }
        if (transferNotes != null) {
            for (TransferNote transferNote : transferNotes) {
                if (transferNote.randomizeSection == null) {
                    transferNote.randomizeSection = this.randomizeSection;
                    transferNote.shoot = this.shoot;
                }
                boolean processSucceeded = transferNote.process(patient);
                if (!processSucceeded) {
                    nErrors++;
                    if (Arguments.verbose) System.err.println("      ***Failed to process Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000, "PainManagementNote, requested sleep for page.");
        }
        return (nErrors == 0);
    }

    /**
     * Determine if a particular patient has been registered and can therefore be found when doing a search.
     * There are currently 4 different methods with this name.  Perhaps they could be consolidated and put into Utilities.
     *
     * @param ssn The patient's social security number
     * @param firstName The patient's first name
     * @param lastName The patient's last name
     * @param traumaRegisterNumber The patient's trauma Register number
     * @return indication if patient was registered and therefore found in the system
     */
    boolean isPatientFound(String ssn, String firstName, String lastName, String traumaRegisterNumber) {
        Utilities.waitForPresence(ssnField, 3, "PainManagementNote.isPatientFound(), checking to see if on right page.");
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);

        logger.finest("PainManagementNote.isPatientFound(), here comes a click for search");
        Utilities.clickButton(searchForPatientButton);
        logger.finest("PainManagementNote.isPatientFound(), back from the click, here comes a wait for ajax finished");
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1 No: 1
        logger.finest("PainManagementNote.isPatientFound(), back from the wait for ajax finished, here comes a wait for visibility of message area");
        //
        // Either the patient was found or wasn't.  If we don't get advanced to the
        // page that has the demographic table on it, then it failed, and there's nothing that can be done.
        // If we get a message, like "There are no patients found.", then could report that to the user, but still have
        // to return null.  The only advantage to checking the failure is to return a message.
        //
        try {
            WebElement messageArea = Utilities.waitForVisibility(painManagementNoteSearchForPatientMessageLocatorBy, 2, "PainManagementNote.isPatientFound()");
            String message = messageArea.getText();
            if (message.equalsIgnoreCase("There are no patients found.")) {
                logger.fine("PainManagementNote.isPatientFound(), message says: " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("PainManagementNote.isPatientFound(), Prob okay???  Couldn't find a message about search, so a patient was probably (???) found.  Will check for more first.");
        }
        //
        // Check if there's a "Patient Demographics" tab or section, and if there is, we're okay.  But it's possible that the search results takes a long time.
        //
        try {
            logger.fine("PainManagementNote.isPatientFound(), now checking if there's a Patient Demographics section in the Pain Management Note.");
            Utilities.waitForVisibility(demographicTableBy, 15, "PainManagementNote.isPatientFound()");
        } catch (Exception e) {
            logger.severe("PainManagementNote.isPatientFound(), didn't find demographic table.  Exception: " + Utilities.getMessageFirstLine(e)); ScreenShot.shoot("SevereError");
            return false;
        }
        return true;
    }

}

