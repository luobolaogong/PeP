package pep.patient.registration;

import pep.utilities.Arguments;

public class PreRegistration {
    public Boolean random;
    public Demographics demographics;
    // It will be Flight (level 4) or ArrivalLocationSection (levels 1,2,3) ????
    public Flight flight;
    public InjuryIllness injuryIllness;
    public Location location;

    public PreRegistration() {
        if (Arguments.template) {
            this.random = null;
            this.demographics = new Demographics();
            this.flight = new Flight();
            this.injuryIllness = new InjuryIllness();
            this.location = new Location();
        }
    }

}
