package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author nj
 * @date 2022/10/11 13:57
 */
@XmlRootElement(name = "methodResponse")
public class MethodResponse {


    private ClassFault classFault;

    private ParamClass paramClass;

    @XmlElement(name = "params")
    public ParamClass getParamClass() {
        return paramClass;
    }

    public void setParamClass(ParamClass paramClass) {
        this.paramClass = paramClass;
    }


    @XmlElement(name="fault")
    public ClassFault getClassFault() {
        return classFault;
    }

    public void setClassFault(ClassFault classFault) {
        this.classFault = classFault;
    }
}
