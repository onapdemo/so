package com.amdocs.homing.datamodel;

import java.util.List;

public class Solution {

    private String cloudOwner;

    private List<String> identifiers;

    private String identifierType;

    public String getCloudOwner() {
        return cloudOwner;
    }

    public void setCloudOwner(String cloudOwner) {
        this.cloudOwner = cloudOwner;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    @Override
    public String toString() {
        return Solution.class.getSimpleName() + "[cloudOwner = " + cloudOwner + ", identifiers = " + identifiers
                + ", identifierType = " + identifierType + "]";
    }
}