package pep.patient;


/**
 * TMDS/PeP Records and Patient States
 *
 * Related to records, a person may be in one of three patient states: Open Pre-Registration record,
 * Open Registration record, or Closed record.
 *
 * A person becomes a patient upon either the creation of an Open Pre-Registration record,
 * or the creation of an Open Registration Record.  An Open Registration Record is an “Active” record.
 * Therefore, you’re a patient if you have an “active” or open registration record.
 *
 * A person stops being a patient upon the closing or deleting of a registration record.
 * Perhaps this could be called an “inactive” registration record.
 *
 * A Patient moves from having an Open Pre-Reg record to having an Open Reg record by the action of “arrival”.
 * However, a patient may have an Open Reg record without first having an Open Pre-Registration record.
 *
 * A patient moves from having an Open Registration record to a Closed record by the action of “departure”.
 *
 * Pep only needs to be concerned with Open Registration patients because it only needs to work with
 * the three pages New Patient Registration, Update Patient, and Patient Information.
 *
 * PeP input files contain sections for New Patient Registration, Update Patient, and Patient Information.
 * They also contain a Patient Search section that does not contain patient information.  Additionally,
 * PeP input files contain “Treatment” sections, which are notes and assessments.  These sections correspond
 * with the sections and pages in the TMDS interface.
 *
 * PeP Registration Pages
 *
 * There are five TMDS registration “pages”: Pre-Registration, Pre-Registration Arrival,
 * New Patient Registration, Update Patient, and Patient Information.  Only the last three
 * are involved with PeP.
 *
 * The Pre-Registration page can be used to enter patient info.  For PeP it need not be used
 * and therefore is not supported.
 *
 * The PreReg  Arrival page merely controls the disposition of the Pre Reg record either by
 * removing it by marking it “”remove”, or advancing it by marking the patient as “arrived”.
 * This page is also unnecessary for PeP and is therefore not supported.
 *
 * The New Patient Registration page is the first page that PeP deals with, and thus a person
 * becomes a patient when the New Patient Registration page page is used to create patient information.
 *
 * The Patient Information page allows for supplementing patient information.  It can be used
 * after New Patient Registration page
 *
 * The Update Patient page is used to update patient information and to “depart” a patient.
 * It can only be used after the New Patient Registration page has been used to create an Open Registration record
 *
 * If PeP is directed to create a new patient because the input file contains
 * New Patient Registration information, and the patient already has an open registration record,
 * it will not try to do it.  Instead it will/may do an Update Patient operation, or indicate an error.
 * If PeP is directed to update a patient’s record because the input file contains an
 * Update Patient section, and the patient does not have an open registration record,
 * it will not try to do it.  Instead it may do a New Patient Registration operation or indicate an error.
 *
 * If PeP is directed to operate on a Patient Information page because the input file contains
 * a Patient Information section, and the patient does not have an open registration record,
 * it will not try to do it, and will indicate an error.
 *
 *
 * PeP Patient States: New, Updated, Closed
 *
 * For PeP the patient states are New, Updated, and Closed, and that is the mandatory
 * order of these states.  Only Updated patients can become Closed, and only New patients
 * can be Updated.
 *
 * PeP can be used to create a new patient by creating an Open Registration record,
 * and it can be used to update a new patient that has an Open Registration record,
 * and it can be used to close the Open Registration record by departing the patient.
 *
 * PeP Operation States: New, Update, Info
 *
 * Per patient, PeP advances through operation states in the order: New, Update, and Info,
 * corresponding to the pages New Patient Registration, Update Patient, and Patient Info.
 * If the input file contains New Patient Registration information or Update Patient information
 * or Patient Info then the corresponding pages are manipulated, and the contents saved.
 *
 * For the sake of internal PeP code, I think the states will be: New, Update, Info,
 * Closed, and Invalid.
 *
 * If all 5 registration pages are to be manipulated by PeP, then this state list will
 * be expanded to include Pre and PreArrival
 */
public enum PatientState {
    NO_STATE,
    PRE,
    PRE_ARRIVAL,
    NEW,
    UPDATE,
    INFO,
    CLOSED,
    INVALID
}
