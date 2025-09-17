package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author nj
 * @date 2022/10/11 13:49
 */
@XmlRootElement(name = "methodResponse")
public class FailMethodResponse {

    private ClassFault classFault;

    @XmlElement(name="fault")
    public ClassFault getClassFault() {
        return classFault;
    }

    public void setClassFault(ClassFault classFault) {
        this.classFault = classFault;
    }
}
