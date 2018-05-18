package com.amdocs.homing.datamodel;

import java.util.List;

public class PlacementSolutions {

    private String resourceModuleName;

    private String serviceResourceId;

    private List<AssignmentInfo> assignmentInfo;

    private Solution solution;

    public String getResourceModuleName() {
        return resourceModuleName;
    }

    public void setResourceModuleName(String resourceModuleName) {
        this.resourceModuleName = resourceModuleName;
    }

    public String getServiceResourceId() {
        return serviceResourceId;
    }

    public void setServiceResourceId(String serviceResourceId) {
        this.serviceResourceId = serviceResourceId;
    }

    public List<AssignmentInfo> getAssignmentInfo() {
        return assignmentInfo;
    }

    public void setAssignmentInfo(List<AssignmentInfo> assignmentInfo) {
        this.assignmentInfo = assignmentInfo;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    /*@Override
    public String toString() {
        return PlacementSolutions.class.getSimpleName() + "[resourceModuleName = " + resourceModuleName
                + ", serviceResourceId = " + serviceResourceId + ", assignmentInfo = " + assignmentInfo
                + ", solution = " + solution + "]";
    }*/
}
