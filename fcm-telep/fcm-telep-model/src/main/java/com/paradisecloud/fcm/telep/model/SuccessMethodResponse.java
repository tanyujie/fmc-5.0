package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author nj
 * @date 2022/10/10 17:13
 */

@XmlRootElement(name = "methodResponse")
public class SuccessMethodResponse implements Serializable {


  private ParamClass paramClass;

  @XmlElement(name = "params")
  public ParamClass getParamClass() {
    return paramClass;
  }

  public void setParamClass(ParamClass paramClass) {
    this.paramClass = paramClass;
  }

}
