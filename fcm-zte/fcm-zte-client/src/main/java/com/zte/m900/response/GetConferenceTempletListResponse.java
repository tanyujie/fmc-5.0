/**
 * GetConferenceTempletListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetConferenceTempletListResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConferenceTemplet[] conferenceTemplet;

    public GetConferenceTempletListResponse() {
    }

    public GetConferenceTempletListResponse(
           String result,
           com.zte.m900.bean.ConferenceTemplet[] conferenceTemplet) {
        super(
            result);
        this.conferenceTemplet = conferenceTemplet;
    }


    /**
     * Gets the conferenceTemplet value for this GetConferenceTempletListResponse.
     * 
     * @return conferenceTemplet
     */
    public com.zte.m900.bean.ConferenceTemplet[] getConferenceTemplet() {
        return conferenceTemplet;
    }


    /**
     * Sets the conferenceTemplet value for this GetConferenceTempletListResponse.
     * 
     * @param conferenceTemplet
     */
    public void setConferenceTemplet(com.zte.m900.bean.ConferenceTemplet[] conferenceTemplet) {
        this.conferenceTemplet = conferenceTemplet;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetConferenceTempletListResponse)) return false;
        GetConferenceTempletListResponse other = (GetConferenceTempletListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceTemplet==null && other.getConferenceTemplet()==null) || 
             (this.conferenceTemplet!=null &&
              java.util.Arrays.equals(this.conferenceTemplet, other.getConferenceTemplet())));
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
        if (getConferenceTemplet() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConferenceTemplet());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getConferenceTemplet(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetConferenceTempletListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceTempletListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceTemplet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceTemplet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceTemplet"));
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
