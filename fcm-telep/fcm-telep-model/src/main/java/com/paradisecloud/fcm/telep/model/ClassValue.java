package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author nj
 * @date 2022/10/11 9:27
 */
public class ClassValue {
    private ClassStruct classStruct;


    @XmlElement(name = "struct")
    public ClassStruct getClassStruct() {
        return classStruct;
    }

    public void setClassStruct(ClassStruct classStruct) {
        this.classStruct = classStruct;
    }
}
