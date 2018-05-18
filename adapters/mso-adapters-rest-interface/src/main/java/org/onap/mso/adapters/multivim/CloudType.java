package org.onap.mso.adapters.multivim;

import javax.xml.bind.annotation.XmlEnumValue;

public enum CloudType {

    @XmlEnumValue("azure")
    AZURE("azure"),
    @XmlEnumValue("openstack")
    OPENSTACK("openstack");

    String cloudType;

    CloudType(final String cloudType){
        this.cloudType = cloudType;
    }
}
