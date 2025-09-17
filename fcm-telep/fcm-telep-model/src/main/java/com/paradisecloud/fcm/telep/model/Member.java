package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author nj
 * @date 2022/10/10 17:46
 */
public class Member {

   private String name;

   private Value value;

   @XmlElement(name = "name")
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }


   @XmlElement(name = "value")
   public Value getValue() {
      return value;
   }

   public void setValue(Value value) {
      this.value = value;
   }
}
