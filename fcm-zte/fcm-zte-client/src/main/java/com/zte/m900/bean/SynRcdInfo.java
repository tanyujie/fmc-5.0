/**
 * SynRcdInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class SynRcdInfo  implements java.io.Serializable {
    private String confID;

    private String confName;

    private int isOffTranscode;

    private int liveState;

    private String liveURL;

    private int rcdState;

    private String rcdUserName;

    private String rcdUserPWD;

    public SynRcdInfo() {
    }

    public SynRcdInfo(
           String confID,
           String confName,
           int isOffTranscode,
           int liveState,
           String liveURL,
           int rcdState,
           String rcdUserName,
           String rcdUserPWD) {
           this.confID = confID;
           this.confName = confName;
           this.isOffTranscode = isOffTranscode;
           this.liveState = liveState;
           this.liveURL = liveURL;
           this.rcdState = rcdState;
           this.rcdUserName = rcdUserName;
           this.rcdUserPWD = rcdUserPWD;
    }


    /**
     * Gets the confID value for this SynRcdInfo.
     * 
     * @return confID
     */
    public String getConfID() {
        return confID;
    }


    /**
     * Sets the confID value for this SynRcdInfo.
     * 
     * @param confID
     */
    public void setConfID(String confID) {
        this.confID = confID;
    }


    /**
     * Gets the confName value for this SynRcdInfo.
     * 
     * @return confName
     */
    public String getConfName() {
        return confName;
    }


    /**
     * Sets the confName value for this SynRcdInfo.
     * 
     * @param confName
     */
    public void setConfName(String confName) {
        this.confName = confName;
    }


    /**
     * Gets the isOffTranscode value for this SynRcdInfo.
     * 
     * @return isOffTranscode
     */
    public int getIsOffTranscode() {
        return isOffTranscode;
    }


    /**
     * Sets the isOffTranscode value for this SynRcdInfo.
     * 
     * @param isOffTranscode
     */
    public void setIsOffTranscode(int isOffTranscode) {
        this.isOffTranscode = isOffTranscode;
    }


    /**
     * Gets the liveState value for this SynRcdInfo.
     * 
     * @return liveState
     */
    public int getLiveState() {
        return liveState;
    }


    /**
     * Sets the liveState value for this SynRcdInfo.
     * 
     * @param liveState
     */
    public void setLiveState(int liveState) {
        this.liveState = liveState;
    }


    /**
     * Gets the liveURL value for this SynRcdInfo.
     * 
     * @return liveURL
     */
    public String getLiveURL() {
        return liveURL;
    }


    /**
     * Sets the liveURL value for this SynRcdInfo.
     * 
     * @param liveURL
     */
    public void setLiveURL(String liveURL) {
        this.liveURL = liveURL;
    }


    /**
     * Gets the rcdState value for this SynRcdInfo.
     * 
     * @return rcdState
     */
    public int getRcdState() {
        return rcdState;
    }


    /**
     * Sets the rcdState value for this SynRcdInfo.
     * 
     * @param rcdState
     */
    public void setRcdState(int rcdState) {
        this.rcdState = rcdState;
    }


    /**
     * Gets the rcdUserName value for this SynRcdInfo.
     * 
     * @return rcdUserName
     */
    public String getRcdUserName() {
        return rcdUserName;
    }


    /**
     * Sets the rcdUserName value for this SynRcdInfo.
     * 
     * @param rcdUserName
     */
    public void setRcdUserName(String rcdUserName) {
        this.rcdUserName = rcdUserName;
    }


    /**
     * Gets the rcdUserPWD value for this SynRcdInfo.
     * 
     * @return rcdUserPWD
     */
    public String getRcdUserPWD() {
        return rcdUserPWD;
    }


    /**
     * Sets the rcdUserPWD value for this SynRcdInfo.
     * 
     * @param rcdUserPWD
     */
    public void setRcdUserPWD(String rcdUserPWD) {
        this.rcdUserPWD = rcdUserPWD;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SynRcdInfo)) return false;
        SynRcdInfo other = (SynRcdInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.confID==null && other.getConfID()==null) || 
             (this.confID!=null &&
              this.confID.equals(other.getConfID()))) &&
            ((this.confName==null && other.getConfName()==null) || 
             (this.confName!=null &&
              this.confName.equals(other.getConfName()))) &&
            this.isOffTranscode == other.getIsOffTranscode() &&
            this.liveState == other.getLiveState() &&
            ((this.liveURL==null && other.getLiveURL()==null) || 
             (this.liveURL!=null &&
              this.liveURL.equals(other.getLiveURL()))) &&
            this.rcdState == other.getRcdState() &&
            ((this.rcdUserName==null && other.getRcdUserName()==null) || 
             (this.rcdUserName!=null &&
              this.rcdUserName.equals(other.getRcdUserName()))) &&
            ((this.rcdUserPWD==null && other.getRcdUserPWD()==null) || 
             (this.rcdUserPWD!=null &&
              this.rcdUserPWD.equals(other.getRcdUserPWD())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getConfID() != null) {
            _hashCode += getConfID().hashCode();
        }
        if (getConfName() != null) {
            _hashCode += getConfName().hashCode();
        }
        _hashCode += getIsOffTranscode();
        _hashCode += getLiveState();
        if (getLiveURL() != null) {
            _hashCode += getLiveURL().hashCode();
        }
        _hashCode += getRcdState();
        if (getRcdUserName() != null) {
            _hashCode += getRcdUserName().hashCode();
        }
        if (getRcdUserPWD() != null) {
            _hashCode += getRcdUserPWD().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SynRcdInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "SynRcdInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isOffTranscode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isOffTranscode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("liveState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "liveState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("liveURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "liveURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdUserName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdUserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdUserPWD");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdUserPWD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
