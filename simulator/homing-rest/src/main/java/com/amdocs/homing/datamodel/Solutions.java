package com.amdocs.homing.datamodel;

import java.util.List;

public class Solutions {

    private List<LicenseSolutions> licenseSolutions;

    private List<List<PlacementSolutions>> placementSolutions;

    public List<LicenseSolutions> getLicenseSolutions() {
        return licenseSolutions;
    }

    public void setLicenseSolutions(List<LicenseSolutions> licenseSolutions) {
        this.licenseSolutions = licenseSolutions;
    }

    public List<List<PlacementSolutions>> getPlacementSolutions() {
        return placementSolutions;
    }

    public void setPlacementSolutions(List<List<PlacementSolutions>> placementSolutions) {
        this.placementSolutions = placementSolutions;
    }

   /* @Override
    public String toString() {
        return Solutions.class.getSimpleName() + "[licenseSolutions = " + licenseSolutions
                + ", placementSolutions = " + placementSolutions + "]";
    }*/
}