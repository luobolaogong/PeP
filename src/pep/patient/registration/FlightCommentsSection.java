package pep.patient.registration;

import pep.utilities.Arguments;

import java.util.logging.Logger;

/**
 * This is the comments section of the Flight section of a registration page (New, Update, Pre-reg)
 */
public class FlightCommentsSection {
    private static Logger logger = Logger.getLogger(FlightCommentsSection.class.getName());
    Boolean randomizeSection;
    Boolean shoot;
    Boolean ambulatory;
    Boolean batterySupportUnit;
    Boolean ccatt;
    Boolean foley;
    Boolean iv;
    Boolean litterFolding;
    Boolean monitor;
    Boolean orthopedic;
    Boolean oxygenAnalyzer9Volt;
    Boolean pumpIntraveneousInfusion;
    Boolean restraints;
    Boolean stykerFrame;
    Boolean suctionApparatusContinuousIntermittent;
    Boolean traction;
    Boolean vent;
    Boolean attendant;
    Boolean cardiacMonitor;
    Boolean chestTube;
    Boolean incubator;
    Boolean lfc;
    Boolean mattressLitter;
    Boolean ngTube;
    Boolean other;
    Boolean pulseOximeter;
    Boolean restraintSetWristsAndAnkle;
    Boolean strapsWebbing;
    Boolean suction;
    Boolean trach;
    Boolean tractionApplianceCervicalInjury;
    Boolean vitalSignsMonitor;

    public FlightCommentsSection() {
        if (Arguments.template) {
            //this.randomizeSection = null; // don't want this showing up in template
            this.ambulatory = false;
            this.batterySupportUnit = false;
            this.ccatt = false;
            this.foley = false;
            this.iv = false;
            this.litterFolding = false;
            this.monitor = false;
            this.orthopedic = false;
            this.oxygenAnalyzer9Volt = false;
            this.pumpIntraveneousInfusion = false;
            this.restraints = false;
            this.stykerFrame = false;
            this.suctionApparatusContinuousIntermittent = false;
            this.traction = false;
            this.vent = false;
            this.attendant = false;
            this.cardiacMonitor = false;
            this.chestTube = false;
            this.incubator = false;
            this.lfc = false;
            this.mattressLitter = false;
            this.ngTube = false;
            this.other = false;
            this.pulseOximeter = false;
            this.restraintSetWristsAndAnkle = false;
            this.strapsWebbing = false;
            this.suction = false;
            this.trach = false;
            this.tractionApplianceCervicalInjury = false;
            this.vitalSignsMonitor = false;
        }
    }
}
