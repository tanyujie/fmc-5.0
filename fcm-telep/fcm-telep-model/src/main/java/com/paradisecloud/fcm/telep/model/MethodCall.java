package com.paradisecloud.fcm.telep.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author nj
 * @date 2022/10/10 17:13
 */

@XmlRootElement(name = "methodCall")
public class MethodCall implements Serializable {


  private ParamClass paramClass;


  private String methodName;

  @XmlElement(name = "params")
  public ParamClass getParamClass() {
    return paramClass;
  }

  public void setParamClass(ParamClass paramClass) {
    this.paramClass = paramClass;
  }

  @XmlElement(name = "methodName")
  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
}
