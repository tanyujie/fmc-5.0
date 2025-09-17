package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author nj
 * @date 2022/10/11 9:25
 */
public class ParamClass {

    private ClassParam classParam;

    @XmlElement(name = "param")
    public ClassParam getClassParam() {
        return classParam;
    }

    public void setClassParam(ClassParam classParam) {
        this.classParam = classParam;
    }
}
