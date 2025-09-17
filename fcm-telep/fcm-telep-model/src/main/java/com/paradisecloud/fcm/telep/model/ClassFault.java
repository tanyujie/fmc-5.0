package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author nj
 * @date 2022/10/11 13:50
 */
public class ClassFault {

    private ClassValue classValue;

    @XmlElement(name = "value")
    public ClassValue getClassValue() {
        return classValue;
    }

    public void setClassValue(ClassValue classValue) {
        this.classValue = classValue;
    }
}
