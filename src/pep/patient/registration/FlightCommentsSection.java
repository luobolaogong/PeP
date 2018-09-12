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
            this.random = null;
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
