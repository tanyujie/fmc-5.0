package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author nj
 * @date 2022/10/11 9:28
 */
public class ClassStruct {

    private List<Member> member;


    @XmlElement(name = "member")
    public List<Member> getMember() {
        return member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }
}
