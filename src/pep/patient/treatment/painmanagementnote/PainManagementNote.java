package pep.patient.treatment.painmanagementnote;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pep.Pep;
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
import pep.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static pep.utilities.Arguments.codeBranch;

// A PainManagementNote PAGE consists of a list of Allergy objects, a list of ProcedureNote objects, a list of ClinitcalNote objects,
// and a list of TransferNote objects.  The ProcedureNote objects can have subtypes (nerve blocks, catheter, ivpca).  All of
// these objects get listed in the section of this page called "Pain Management Notes".  So, a PainManagementNote page
// shows a list of PainManagementNote parts. The stuff is probably badly named, and the page organization seems wrong.
//
public class PainManagementNote {
    private static Logger logger = Logger.getLogger(PainManagementNote.class.getName()); // multiple?
    public Boolean random; // true if want this section to be generated randomly
    public List<Allergy> allergies; // just keep clicking "Add Allergy" on the page for multiple
    public List<ProcedureNote> procedureNotes; // just keep clicking "Create Note"
    public List<ClinicalNote> clinicalNotes; // keep clicking Create Note
    public List<TransferNote> transferNotes; // keep clicking Create Note

    // prob wrong:
    //private static By patientTreatmentTabBy = By.xpath("//*[@id=\"i4200\"]/span"); // verified.  This is the tab for "Patient Treatment"
    //private static By patientTreatmentTabBy = By.partialLinkText("Treatment"); // better?
    private static By patientTreatmentTabBy = By.xpath("//li/a[@href='/tmds/patientTreatment.html']"); // best?
    //private static By painManagementNoteLinkBy = By.id("a_0"); // if menu, not link then //*[@id="nav"]/li[2]/ul/li[1]/a
    //private static By painManagementNoteLinkBy = By.linkText("Pain Management");
    private static By painManagementNoteLinkBy = By.xpath("//*[@id=\"nav\"]/li[2]/ul/li[1]/a");
    //private static By painManagementNoteLink2By = By.id("a_0");
    //private static By painManagementNoteLink2By = By.id("Pain&nbsp;Management&nbsp;Note");
    private static By painManagementNoteLink2By = By.xpath("//*[@id=\"nav\"]/li[2]/ul/li[1]/ul/li[1]/a");


    private static By ssnField = By.id("ssn");
    private static By lastNameField = By.id("lastName");
    private static By firstNameField = By.id("firstName");
    private static By traumaRegisterNumberField = By.id("registerNumber");
    private static By searchForPatientButton = By.xpath("//*[@id=\"search-form\"]/div[2]/button");
    private static By painManagementNoteSearchForPatientMessageLocatorBy = By.id("msg");
    private static By demographicTableBy = By.id("patient-demographics-container"); // I've changed this back and forth a couple of times on 9/20/18
    //private static By painManagementSearchForPatientSectionBy = By.id("search-Form"); // for gold of course.  how about demo?
    private static By painManagementSearchForPatientSectionBy = By.id("patient-demographics-container");



    public PainManagementNote() {
        if (Arguments.template) {
            //this.random = null; // don't want this showing up in template
            this.allergies = Arrays.asList(new Allergy());
            this.procedureNotes = Arrays.asList(new ProcedureNote());
            this.clinicalNotes = Arrays.asList(new ClinicalNote());
            this.transferNotes = Arrays.asList(new TransferNote());
        }
        if (codeBranch.equalsIgnoreCase("Seam")) {
            ssnField = By.id("patientSearchSsn"); // now not only does demo fail, but also test if you pass do a search for a ssn
            lastNameField = By.id("patientSearchLastName");
            firstNameField = By.id("patientSearchFirstName");
            traumaRegisterNumberField = By.id("patientSearchRegNum");
            searchForPatientButton = By.id("patientSearchGo");
            painManagementNoteSearchForPatientMessageLocatorBy = By.xpath("//*[@id=\"j_id286\"]/table/tbody/tr/td/span");
            demographicTableBy = By.id("demographicTable");
            painManagementSearchForPatientSectionBy = By.id("patientSearchForm"); // check this.  I changed the gold version
        }
    }

    //    This page contains these significant sections with regard to entering new patient information:
    //    Add Allergy, Procedure Notes, Clinical Note, Transfer Note.  (I'm not sure that "Pain
    //    Management Notes" allows you to enter information.)
    // If this patient is a random, then we want at least one of the 4 sections to be filled in.  Chances
    // of allergy info: 40, procedure notes: 60, clinical note 50, transfer note 30.
    // THIS NEEDS SERIOUS LOOKING AT.  It's still generating a grundle of duplicate things.
    // THIS IS THE COMPLICATED ONE.

    public boolean process(Patient patient) {
        if (!Arguments.quiet)
            System.out.println("    Processing Pain Management Note for patient" +
                    (patient.patientSearch.firstName.isEmpty() ? "" : (" " + patient.patientSearch.firstName)) +
                    (patient.patientSearch.lastName.isEmpty() ? "" : (" " + patient.patientSearch.lastName)) +
                    (patient.patientSearch.ssn.isEmpty() ? "" : (" ssn:" + patient.patientSearch.ssn)) + " ..."
            );
        // Watch this next line.  Can use link text in the By's?
        boolean navigated = Utilities.myNavigate(patientTreatmentTabBy, painManagementNoteLinkBy, painManagementNoteLink2By);
        if (!navigated) {
            return false; // Why????  Fails:1
        }
        // At this point what should we be seeing?  We're going to wait for the visibility of some form: By.id("search-Form")  which is there
        try { // following line fails on gold, role3, role4
            //(new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(painManagementSearchForPatientSectionBy))); // was 20s
            (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.visibilityOfElementLocated(painManagementSearchForPatientSectionBy)); // was 20s
        }
        catch (TimeoutException e) {
            logger.fine("Wow, didn't see a Search For Patient section yet, so we may not be where we expect to be.  Nav failed even though says it succeeded?");
            return false; // fails: 3 11/5/18, 11/7/18
        }
        catch (Exception e) {
            logger.fine("Wow, didn't see a Search For Patient section yet, so we may not be where we expect to be.  Nav failed even though says it succeeded?");
            return false;
        }

        // Wanna check here to see that at least one value exists before call search????????????????????????????????????????????????????????????????????????????????
        boolean patientFound = isPatientRegistered(
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
            return false; // Fails: demo: Role4: 2   Why?
        }
        logger.fine("????????????????????????????????????????????????????????????Did we really get past this search for the patient?  And why do we call it isPatientRegistered?");
        // This next stuff is only for doing sections if they are nonexistent because not listed in JSON.
        // If the sections exist in the JSON then we don't use this stuff.
        // The entire logic regarding "random" should be reviewed and redone, and cleaned.
        boolean doAllergy = false, doPn = false, doCn = false, doTn = false;
        if (this.random) { // this is totally new
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
        // This next stuff is totally new
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
//        if (this.procedureNotes != null) {
//            doPn = true;
//        }




        List<Allergy> allergies = this.allergies;
        if (allergies == null && this.random && doAllergy) {
            int nRandomAllergies = Utilities.random.nextInt(2) + 1;
            allergies = new ArrayList<Allergy>(nRandomAllergies); // Doesn't put anything in this.  Must allocate
            this.allergies = allergies;
            for (int ctr = 0; ctr < nRandomAllergies; ctr++) {
                Allergy allergy = new Allergy();
                allergy.random = (this.random == null) ? false : this.random;
                this.allergies.add(allergy);
            }
        }
        if (allergies != null) {
            for (Allergy allergy : allergies) {
                // this is new
                if (allergy.random == null) { // this should have been done before now.
                    allergy.random = (this.random == null) ? false : this.random;
                }

                boolean processSucceeded = allergy.process(patient, this); // too bad this doesn't return a reason for failure.  Need to report it at same time as following line
                if (!processSucceeded && !Arguments.quiet) System.err.println("      ***Failed to process Allergy for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
            }
        }

        // Check logic.  If random then one thing.  But what if not random?  Don't set up nRandom and all that
        // CHECK THIS LOGIC  WAY TOO MANY THINGS GENERATED WHEN PATIENT IS RANDOM
        List<ProcedureNote> procedureNotes = this.procedureNotes;
        // is this the right freaking logic?  This doPn thing?
        if (procedureNotes == null && this.random && doPn) {
            int nRandomProcedureNotes = 1;  // let's figure out the random thing later.  1 is right for now.
            procedureNotes = new ArrayList<ProcedureNote>(nRandomProcedureNotes); // right way to allocate?
            this.procedureNotes = procedureNotes;
            for (int ctr = 0; ctr < nRandomProcedureNotes; ctr++) { // we've got an array situation, so losing all but last?
                ProcedureNote procedureNote = new ProcedureNote();
                procedureNote.random = (this.random == null) ? false : this.random;

                // Probably only need to do one of the following four, but at least one if we're doing random
                // But it would be good if we didn't repeat here.  If two, then two different ones, although
                // I think it's more likely that only one pain thing would happen per treatment.
                int painSelection = Utilities.random.nextInt(4);
                switch (painSelection) {
                    case 0:
                        procedureNote.singlePeripheralNerveBlock = new SinglePeripheralNerveBlock();
                        procedureNote.singlePeripheralNerveBlock.random = (procedureNote.random == null) ? false : procedureNote.random;
                        break;
                    case 1:
                        procedureNote.continuousPeripheralNerveBlock = new ContinuousPeripheralNerveBlock();
                        procedureNote.continuousPeripheralNerveBlock.random = (procedureNote.random == null) ? false : procedureNote.random;
                        break;
                    case 2:
                        procedureNote.epiduralCatheter = new EpiduralCatheter();
                        procedureNote.epiduralCatheter.random = (procedureNote.random == null) ? false : procedureNote.random;
                        break;
                    case 3:
                        procedureNote.ivPca = new IvPca(); // linking it to its parent procedureNote
                        procedureNote.ivPca.random = (procedureNote.random == null) ? false : procedureNote.random;
                        break;
                }
                this.procedureNotes.add(procedureNote);
            }
        }
        int nErrors = 0;
        if (procedureNotes != null) {
            for (ProcedureNote procedureNote : procedureNotes) {
                if (procedureNote.random == null) { // this should have been done before now.
                    procedureNote.random = (this.random == null) ? false : this.random;
                }
                boolean processSucceeded = procedureNote.process(patient, this); // watch out for npe inside
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Procedure Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to process Procedure Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        List<ClinicalNote> clinicalNotes = this.clinicalNotes;
        if (clinicalNotes == null && this.random && doCn) {// error in logic here?  Allows sections to go without propagating random?
            int nRandomClinicalNotes = Utilities.random.nextInt(2) + 1;
            clinicalNotes = new ArrayList<ClinicalNote>(nRandomClinicalNotes); // actually allocates each one, or just an empty list capable of some Allergy objects?
            this.clinicalNotes = clinicalNotes;
            for (int ctr = 0; ctr < nRandomClinicalNotes; ctr++) {
                ClinicalNote clinicalNote = new ClinicalNote();
                clinicalNote.random = (this.random == null) ? false : this.random;
                this.clinicalNotes.add(clinicalNote);
            }
        } // hey, what about inheriting random from parent for clinical note?
        if (clinicalNotes != null) {
            for (ClinicalNote clinicalNote : clinicalNotes) {
                // This if is new
                if (clinicalNote.random == null) {
                    clinicalNote.random = (this.random == null) ? false : this.random;
                }
                logger.fine("Hey, are we ready to start into clinical note now?  Did the previous thing finish?  Looks like it.");
                boolean processSucceeded = clinicalNote.process(patient);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Clinical Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to process Clinical Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }


        List<TransferNote> transferNotes = this.transferNotes;
        // Changing the logic here too, so keep this around a bit
        if (transferNotes == null && this.random && doTn) { // error in logic here?  Allows sections to go without propagating random?
            int nRandomTransferNotes = Utilities.random.nextInt(2) + 1; // Isn't it unlikely there'd be more than 1?
            transferNotes = new ArrayList<TransferNote>(nRandomTransferNotes); // actually allocates each one, or just an empty list capable of some Allergy objects?
            this.transferNotes = transferNotes;
            for (int ctr = 0; ctr < nRandomTransferNotes; ctr++) {
                TransferNote transferNote = new TransferNote();
                transferNote.random = (this.random == null) ? false : this.random;
                this.transferNotes.add(transferNote);
            }
        }
        if (transferNotes != null) {
            for (TransferNote transferNote : transferNotes) {
                // before we call process, has transferNote.random been set for all elements?
                // This if is new
                if (transferNote.random == null) {
                    transferNote.random = (this.random == null) ? false : this.random;
                }

                boolean processSucceeded = transferNote.process(patient, this);
                //if (!processSucceeded && !Arguments.quiet) System.err.println("***Failed to process Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                if (!processSucceeded) {
                    nErrors++;
                    if (!Arguments.quiet)
                        System.err.println("      ***Failed to process Transfer Note for " + patient.patientSearch.firstName + " " + patient.patientSearch.lastName + " ssn:" + patient.patientSearch.ssn);
                }
            }
        }

        if (nErrors > 0) {
            return false;
        }
        if (Arguments.pausePage > 0) {
            Utilities.sleep(Arguments.pausePage * 1000);
        }
        return true;
    }

    boolean isPatientRegistered(String ssn, String firstName, String lastName, String traumaRegisterNumber) { // next line can take 13s when servers slow
        (new WebDriverWait(Driver.driver, 3)).until(ExpectedConditions.presenceOfElementLocated(ssnField));

        // Also need to make sure that at least one of the following 4 values exists.
        Utilities.fillInTextField(ssnField, ssn);
        Utilities.fillInTextField(lastNameField, lastName);
        Utilities.fillInTextField(firstNameField, firstName);
        Utilities.fillInTextField(traumaRegisterNumberField, traumaRegisterNumber);

        // Before click we're at a page and all it has on it is search stuff.  Nope not true.  New Patient Reg has Patient Demographics and more
        Utilities.clickButton(searchForPatientButton); // Yes, A4J.AJAX.Submit() call.  We need ajax wait?

        // Does the above do a spinner?  MB_Whatever?  If so, handle it like in UpdatePatient?

        // Wow, we did a search on a new patient, and found someone, and the form got filled in!!!!!!!!!!!!!!!!!!
        (new WebDriverWait(Driver.driver, 4)).until(Utilities.isFinishedAjax()); // doesn't block?  No message about no ajax on page.  Yes there is:1 No: 1

        // Either the patient was found or wasn't.  If we don't get advanced to the
        // page that has the demographic table on it, then it failed, and there's nothing that can be done.
        // If we get a message, like "There are no patients found.", then could report that to the user, but still have
        // to return null.  The only advantage to checking the failure is to return a message.  We could instead just check
        // that there's a demographics table now showing up.
        try {
            WebElement messageArea = (new WebDriverWait(Driver.driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(painManagementNoteSearchForPatientMessageLocatorBy));
            String message = messageArea.getText();
            if (message.equalsIgnoreCase("There are no patients found.")) {
                System.out.println("PainManagementNote.isPatientRegistered(), message says: " + message);
                return false;
            }
        }
        catch (Exception e) {
            logger.fine("PainManagementNote.isPatientRegistered(), Prob okay???  Couldn't find a message about search, so a patient was probably (???) found.  Will check for more first.");
        }
        // Check if there's a "Patient Demographics" tab or section, and if there is, we're okay.  But it's possible that the search results takes a long time.
        // Changed 9/20/18.  Will change this to be a regFormBy or something rather than demographicTableBy
        try {
            logger.fine("PainManagementNote.isPatientRegistered(), now checking if there's a Patient Demographics section in the Pain Management Note.");
            (new WebDriverWait(Driver.driver, 15)).until(ExpectedConditions.visibilityOfElementLocated(demographicTableBy));
        } catch (Exception e) {
            logger.severe("PainManagementNote.isPatientRegistered(), didn't find demographic table.  Exception: " + e.getMessage().substring(0,60));
            return false; // fails: 5
        }
        return true;
    }

}

