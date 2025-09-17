/**
 * StartConferenceResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class StartConferenceResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private String conferenceIdentifier;

    public StartConferenceResponse() {
    }

    public StartConferenceResponse(
           String result,
           String conferenceIdentifier) {
        super(
            result);
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferenceIdentifier value for this StartConferenceResponse.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this StartConferenceResponse.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof StartConferenceResponse)) return false;
        StartConferenceResponse other = (StartConferenceResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier())));
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
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StartConferenceResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "StartConferenceResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
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
