/**
 * CreateConferenceV2Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class CreateConferenceV2Response  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos;

    private String conferenceIdentifier;

    public CreateConferenceV2Response() {
    }

    public CreateConferenceV2Response(
           String result,
           com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos,
           String conferenceIdentifier) {
        super(
            result);
        this.conferenceExtInfos = conferenceExtInfos;
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferenceExtInfos value for this CreateConferenceV2Response.
     * 
     * @return conferenceExtInfos
     */
    public com.zte.m900.bean.ConferenceExtInfo[] getConferenceExtInfos() {
        return conferenceExtInfos;
    }


    /**
     * Sets the conferenceExtInfos value for this CreateConferenceV2Response.
     * 
     * @param conferenceExtInfos
     */
    public void setConferenceExtInfos(com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos) {
        this.conferenceExtInfos = conferenceExtInfos;
    }


    /**
     * Gets the conferenceIdentifier value for this CreateConferenceV2Response.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this CreateConferenceV2Response.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CreateConferenceV2Response)) return false;
        CreateConferenceV2Response other = (CreateConferenceV2Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceExtInfos==null && other.getConferenceExtInfos()==null) || 
             (this.conferenceExtInfos!=null &&
              java.util.Arrays.equals(this.conferenceExtInfos, other.getConferenceExtInfos()))) &&
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
        if (getConferenceExtInfos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConferenceExtInfos());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getConferenceExtInfos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreateConferenceV2Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceV2Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceExtInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceExtInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceExtInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
