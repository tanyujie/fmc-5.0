/**
 * SelectParticipantRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SelectParticipantRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String desTerminalIdentifier;

    private int mediaType;

    private String srcTerminalIdentifier;

    public SelectParticipantRequest() {
    }

    public SelectParticipantRequest(
           String conferenceIdentifier,
           String desTerminalIdentifier,
           int mediaType,
           String srcTerminalIdentifier) {
        super(
            conferenceIdentifier);
        this.desTerminalIdentifier = desTerminalIdentifier;
        this.mediaType = mediaType;
        this.srcTerminalIdentifier = srcTerminalIdentifier;
    }


    /**
     * Gets the desTerminalIdentifier value for this SelectParticipantRequest.
     * 
     * @return desTerminalIdentifier
     */
    public String getDesTerminalIdentifier() {
        return desTerminalIdentifier;
    }


    /**
     * Sets the desTerminalIdentifier value for this SelectParticipantRequest.
     * 
     * @param desTerminalIdentifier
     */
    public void setDesTerminalIdentifier(String desTerminalIdentifier) {
        this.desTerminalIdentifier = desTerminalIdentifier;
    }


    /**
     * Gets the mediaType value for this SelectParticipantRequest.
     * 
     * @return mediaType
     */
    public int getMediaType() {
        return mediaType;
    }


    /**
     * Sets the mediaType value for this SelectParticipantRequest.
     * 
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    /**
     * Gets the srcTerminalIdentifier value for this SelectParticipantRequest.
     * 
     * @return srcTerminalIdentifier
     */
    public String getSrcTerminalIdentifier() {
        return srcTerminalIdentifier;
    }


    /**
     * Sets the srcTerminalIdentifier value for this SelectParticipantRequest.
     * 
     * @param srcTerminalIdentifier
     */
    public void setSrcTerminalIdentifier(String srcTerminalIdentifier) {
        this.srcTerminalIdentifier = srcTerminalIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SelectParticipantRequest)) return false;
        SelectParticipantRequest other = (SelectParticipantRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.desTerminalIdentifier==null && other.getDesTerminalIdentifier()==null) || 
             (this.desTerminalIdentifier!=null &&
              this.desTerminalIdentifier.equals(other.getDesTerminalIdentifier()))) &&
            this.mediaType == other.getMediaType() &&
            ((this.srcTerminalIdentifier==null && other.getSrcTerminalIdentifier()==null) || 
             (this.srcTerminalIdentifier!=null &&
              this.srcTerminalIdentifier.equals(other.getSrcTerminalIdentifier())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getDesTerminalIdentifier() != null) {
            _hashCode += getDesTerminalIdentifier().hashCode();
        }
        _hashCode += getMediaType();
        if (getSrcTerminalIdentifier() != null) {
            _hashCode += getSrcTerminalIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SelectParticipantRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectParticipantRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desTerminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "desTerminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("srcTerminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "srcTerminalIdentifier"));
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
