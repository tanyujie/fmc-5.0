package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/11 9:27
 */
public class ClassParam {

    private List<ClassValue> classValue;

    @XmlElement(name = "value")

    public List<ClassValue> getClassValue() {
        return classValue;
    }

    public void setClassValue(List<ClassValue> classValue) {
        this.classValue = classValue;
    }
}
