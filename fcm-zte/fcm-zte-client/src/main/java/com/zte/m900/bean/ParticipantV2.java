/**
 * ParticipantV2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantV2  implements java.io.Serializable {
    private int callMode;

    private int cascadePortType;

    private String ext;

    private String ipAddress;

    private int rate;

    private int terType;

    private String terminalIdentifier;

    private String terminalName;

    private String terminalNumber;

    public ParticipantV2() {
    }

    public ParticipantV2(
           int callMode,
           int cascadePortType,
           String ext,
           String ipAddress,
           int rate,
           int terType,
           String terminalIdentifier,
           String terminalName,
           String terminalNumber) {
           this.callMode = callMode;
           this.cascadePortType = cascadePortType;
           this.ext = ext;
           this.ipAddress = ipAddress;
           this.rate = rate;
           this.terType = terType;
           this.terminalIdentifier = terminalIdentifier;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the callMode value for this ParticipantV2.
     * 
     * @return callMode
     */
    public int getCallMode() {
        return callMode;
    }


    /**
     * Sets the callMode value for this ParticipantV2.
     * 
     * @param callMode
     */
    public void setCallMode(int callMode) {
        this.callMode = callMode;
    }


    /**
     * Gets the cascadePortType value for this ParticipantV2.
     * 
     * @return cascadePortType
     */
    public int getCascadePortType() {
        return cascadePortType;
    }


    /**
     * Sets the cascadePortType value for this ParticipantV2.
     * 
     * @param cascadePortType
     */
    public void setCascadePortType(int cascadePortType) {
        this.cascadePortType = cascadePortType;
    }


    /**
     * Gets the ext value for this ParticipantV2.
     * 
     * @return ext
     */
    public String getExt() {
        return ext;
    }


    /**
     * Sets the ext value for this ParticipantV2.
     * 
     * @param ext
     */
    public void setExt(String ext) {
        this.ext = ext;
    }


    /**
     * Gets the ipAddress value for this ParticipantV2.
     * 
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this ParticipantV2.
     * 
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the rate value for this ParticipantV2.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this ParticipantV2.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }


    /**
     * Gets the terType value for this ParticipantV2.
     * 
     * @return terType
     */
    public int getTerType() {
        return terType;
    }


    /**
     * Sets the terType value for this ParticipantV2.
     * 
     * @param terType
     */
    public void setTerType(int terType) {
        this.terType = terType;
    }


    /**
     * Gets the terminalIdentifier value for this ParticipantV2.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this ParticipantV2.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the terminalName value for this ParticipantV2.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this ParticipantV2.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this ParticipantV2.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this ParticipantV2.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantV2)) return false;
        ParticipantV2 other = (ParticipantV2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.callMode == other.getCallMode() &&
            this.cascadePortType == other.getCascadePortType() &&
            ((this.ext==null && other.getExt()==null) || 
             (this.ext!=null &&
              this.ext.equals(other.getExt()))) &&
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            this.rate == other.getRate() &&
            this.terType == other.getTerType() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier()))) &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.terminalNumber==null && other.getTerminalNumber()==null) || 
             (this.terminalNumber!=null &&
              this.terminalNumber.equals(other.getTerminalNumber())));
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
        _hashCode += getCallMode();
        _hashCode += getCascadePortType();
        if (getExt() != null) {
            _hashCode += getExt().hashCode();
        }
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        _hashCode += getRate();
        _hashCode += getTerType();
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantV2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantV2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cascadePortType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cascadePortType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ext"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalNumber"));
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
