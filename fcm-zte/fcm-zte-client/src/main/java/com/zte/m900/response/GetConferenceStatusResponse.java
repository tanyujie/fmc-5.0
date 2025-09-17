/**
 * GetConferenceStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetConferenceStatusResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConferenceStatus conferenceInfo;

    public GetConferenceStatusResponse() {
    }

    public GetConferenceStatusResponse(
           String result,
           com.zte.m900.bean.ConferenceStatus conferenceInfo) {
        super(
            result);
        this.conferenceInfo = conferenceInfo;
    }


    /**
     * Gets the conferenceInfo value for this GetConferenceStatusResponse.
     * 
     * @return conferenceInfo
     */
    public com.zte.m900.bean.ConferenceStatus getConferenceInfo() {
        return conferenceInfo;
    }


    /**
     * Sets the conferenceInfo value for this GetConferenceStatusResponse.
     * 
     * @param conferenceInfo
     */
    public void setConferenceInfo(com.zte.m900.bean.ConferenceStatus conferenceInfo) {
        this.conferenceInfo = conferenceInfo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetConferenceStatusResponse)) return false;
        GetConferenceStatusResponse other = (GetConferenceStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceInfo==null && other.getConferenceInfo()==null) || 
             (this.conferenceInfo!=null &&
              this.conferenceInfo.equals(other.getConferenceInfo())));
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
        if (getConferenceInfo() != null) {
            _hashCode += getConferenceInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetConferenceStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceStatus"));
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
