/**
 * GetConferenceExtInfoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetConferenceExtInfoResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos;

    private String result;

    public GetConferenceExtInfoResponse() {
    }

    public GetConferenceExtInfoResponse(
           com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos,
           String result) {
           this.conferenceExtInfos = conferenceExtInfos;
           this.result = result;
    }


    /**
     * Gets the conferenceExtInfos value for this GetConferenceExtInfoResponse.
     * 
     * @return conferenceExtInfos
     */
    public com.zte.m900.bean.ConferenceExtInfo[] getConferenceExtInfos() {
        return conferenceExtInfos;
    }


    /**
     * Sets the conferenceExtInfos value for this GetConferenceExtInfoResponse.
     * 
     * @param conferenceExtInfos
     */
    public void setConferenceExtInfos(com.zte.m900.bean.ConferenceExtInfo[] conferenceExtInfos) {
        this.conferenceExtInfos = conferenceExtInfos;
    }


    /**
     * Gets the result value for this GetConferenceExtInfoResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetConferenceExtInfoResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetConferenceExtInfoResponse)) return false;
        GetConferenceExtInfoResponse other = (GetConferenceExtInfoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conferenceExtInfos==null && other.getConferenceExtInfos()==null) || 
             (this.conferenceExtInfos!=null &&
              java.util.Arrays.equals(this.conferenceExtInfos, other.getConferenceExtInfos()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult())));
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
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetConferenceExtInfoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceExtInfoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceExtInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceExtInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceExtInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
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
