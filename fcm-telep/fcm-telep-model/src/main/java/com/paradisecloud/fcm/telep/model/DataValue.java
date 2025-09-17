package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author nj
 * @date 2022/10/11 17:50
 */
public class DataValue {

    private ClassParam classParam;

    @XmlElement(name="data")
    public ClassParam getClassParam() {
        return classParam;
    }

    public void setClassParam(ClassParam classParam) {
        this.classParam = classParam;
    }
}
