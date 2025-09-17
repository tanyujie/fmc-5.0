/**
 * ConfNode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ConfNode  implements java.io.Serializable {
    private String conferenceIdentifier;

    private int connectState;

    private String extention;

    private String terId;

    private int terType;

    private String terminalName;

    private String terminalNumber;

    public ConfNode() {
    }

    public ConfNode(
           String conferenceIdentifier,
           int connectState,
           String extention,
           String terId,
           int terType,
           String terminalName,
           String terminalNumber) {
           this.conferenceIdentifier = conferenceIdentifier;
           this.connectState = connectState;
           this.extention = extention;
           this.terId = terId;
           this.terType = terType;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the conferenceIdentifier value for this ConfNode.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this ConfNode.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the connectState value for this ConfNode.
     * 
     * @return connectState
     */
    public int getConnectState() {
        return connectState;
    }


    /**
     * Sets the connectState value for this ConfNode.
     * 
     * @param connectState
     */
    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }


    /**
     * Gets the extention value for this ConfNode.
     * 
     * @return extention
     */
    public String getExtention() {
        return extention;
    }


    /**
     * Sets the extention value for this ConfNode.
     * 
     * @param extention
     */
    public void setExtention(String extention) {
        this.extention = extention;
    }


    /**
     * Gets the terId value for this ConfNode.
     * 
     * @return terId
     */
    public String getTerId() {
        return terId;
    }


    /**
     * Sets the terId value for this ConfNode.
     * 
     * @param terId
     */
    public void setTerId(String terId) {
        this.terId = terId;
    }


    /**
     * Gets the terType value for this ConfNode.
     * 
     * @return terType
     */
    public int getTerType() {
        return terType;
    }


    /**
     * Sets the terType value for this ConfNode.
     * 
     * @param terType
     */
    public void setTerType(int terType) {
        this.terType = terType;
    }


    /**
     * Gets the terminalName value for this ConfNode.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this ConfNode.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this ConfNode.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this ConfNode.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConfNode)) return false;
        ConfNode other = (ConfNode) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            this.connectState == other.getConnectState() &&
            ((this.extention==null && other.getExtention()==null) || 
             (this.extention!=null &&
              this.extention.equals(other.getExtention()))) &&
            ((this.terId==null && other.getTerId()==null) || 
             (this.terId!=null &&
              this.terId.equals(other.getTerId()))) &&
            this.terType == other.getTerType() &&
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
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        _hashCode += getConnectState();
        if (getExtention() != null) {
            _hashCode += getExtention().hashCode();
        }
        if (getTerId() != null) {
            _hashCode += getTerId().hashCode();
        }
        _hashCode += getTerType();
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
        new org.apache.axis.description.TypeDesc(ConfNode.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConfNode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "connectState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extention");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extention"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
