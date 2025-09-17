/**
 * RequestParticipantIFrame.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class RequestParticipantIFrame  implements java.io.Serializable {
    private String ipAddress;

    private int ipType;

    private int mediaType;

    private String terminalNumber;

    public RequestParticipantIFrame() {
    }

    public RequestParticipantIFrame(
           String ipAddress,
           int ipType,
           int mediaType,
           String terminalNumber) {
           this.ipAddress = ipAddress;
           this.ipType = ipType;
           this.mediaType = mediaType;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the ipAddress value for this RequestParticipantIFrame.
     * 
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this RequestParticipantIFrame.
     * 
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the ipType value for this RequestParticipantIFrame.
     * 
     * @return ipType
     */
    public int getIpType() {
        return ipType;
    }


    /**
     * Sets the ipType value for this RequestParticipantIFrame.
     * 
     * @param ipType
     */
    public void setIpType(int ipType) {
        this.ipType = ipType;
    }


    /**
     * Gets the mediaType value for this RequestParticipantIFrame.
     * 
     * @return mediaType
     */
    public int getMediaType() {
        return mediaType;
    }


    /**
     * Sets the mediaType value for this RequestParticipantIFrame.
     * 
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    /**
     * Gets the terminalNumber value for this RequestParticipantIFrame.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this RequestParticipantIFrame.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestParticipantIFrame)) return false;
        RequestParticipantIFrame other = (RequestParticipantIFrame) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            this.ipType == other.getIpType() &&
            this.mediaType == other.getMediaType() &&
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
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        _hashCode += getIpType();
        _hashCode += getMediaType();
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestParticipantIFrame.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "RequestParticipantIFrame"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
