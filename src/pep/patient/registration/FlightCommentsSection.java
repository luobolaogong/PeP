package pep.patient.registration;

import pep.utilities.Arguments;

public class FlightCommentsSection {
    Boolean random;
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
            //this.random = null; // don't want this showing up in template
            this.ambulatory = null;
            this.batterySupportUnit = null;
            this.ccatt = null;
            this.foley = null;
            this.iv = null;
            this.litterFolding = null;
            this.monitor = null;
            this.orthopedic = null;
            this.oxygenAnalyzer9Volt = null;
            this.pumpIntraveneousInfusion = null;
            this.restraints = null;
            this.stykerFrame = null;
            this.suctionApparatusContinuousIntermittent = null;
            this.traction = null;
            this.vent = null;
            this.attendant = null;
            this.cardiacMonitor = null;
            this.chestTube = null;
            this.incubator = null;
            this.lfc = null;
            this.mattressLitter = null;
            this.ngTube = null;
            this.other = null;
            this.pulseOximeter = null;
            this.restraintSetWristsAndAnkle = null;
            this.strapsWebbing = null;
            this.suction = null;
            this.trach = null;
            this.tractionApplianceCervicalInjury = null;
            this.vitalSignsMonitor = null;
        }
    }
}
