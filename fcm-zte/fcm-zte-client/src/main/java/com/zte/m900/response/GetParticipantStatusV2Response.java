/**
 * GetParticipantStatusV2Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetParticipantStatusV2Response  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ParticipantStatusV2[] participantStatusV2;

    public GetParticipantStatusV2Response() {
    }

    public GetParticipantStatusV2Response(
           String result,
           com.zte.m900.bean.ParticipantStatusV2[] participantStatusV2) {
        super(
            result);
        this.participantStatusV2 = participantStatusV2;
    }


    /**
     * Gets the participantStatusV2 value for this GetParticipantStatusV2Response.
     * 
     * @return participantStatusV2
     */
    public com.zte.m900.bean.ParticipantStatusV2[] getParticipantStatusV2() {
        return participantStatusV2;
    }


    /**
     * Sets the participantStatusV2 value for this GetParticipantStatusV2Response.
     * 
     * @param participantStatusV2
     */
    public void setParticipantStatusV2(com.zte.m900.bean.ParticipantStatusV2[] participantStatusV2) {
        this.participantStatusV2 = participantStatusV2;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantStatusV2Response)) return false;
        GetParticipantStatusV2Response other = (GetParticipantStatusV2Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.participantStatusV2==null && other.getParticipantStatusV2()==null) || 
             (this.participantStatusV2!=null &&
              java.util.Arrays.equals(this.participantStatusV2, other.getParticipantStatusV2())));
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
        if (getParticipantStatusV2() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipantStatusV2());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getParticipantStatusV2(), i);
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
        new org.apache.axis.description.TypeDesc(GetParticipantStatusV2Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV2Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantStatusV2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participantStatusV2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV2"));
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
