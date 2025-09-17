/**
 * GetParticipantScheduleInfoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetParticipantScheduleInfoResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ParticipantScheduleInfo[] participantScheduleInfoList;

    public GetParticipantScheduleInfoResponse() {
    }

    public GetParticipantScheduleInfoResponse(
           String result,
           com.zte.m900.bean.ParticipantScheduleInfo[] participantScheduleInfoList) {
        super(
            result);
        this.participantScheduleInfoList = participantScheduleInfoList;
    }


    /**
     * Gets the participantScheduleInfoList value for this GetParticipantScheduleInfoResponse.
     * 
     * @return participantScheduleInfoList
     */
    public com.zte.m900.bean.ParticipantScheduleInfo[] getParticipantScheduleInfoList() {
        return participantScheduleInfoList;
    }


    /**
     * Sets the participantScheduleInfoList value for this GetParticipantScheduleInfoResponse.
     * 
     * @param participantScheduleInfoList
     */
    public void setParticipantScheduleInfoList(com.zte.m900.bean.ParticipantScheduleInfo[] participantScheduleInfoList) {
        this.participantScheduleInfoList = participantScheduleInfoList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantScheduleInfoResponse)) return false;
        GetParticipantScheduleInfoResponse other = (GetParticipantScheduleInfoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.participantScheduleInfoList==null && other.getParticipantScheduleInfoList()==null) || 
             (this.participantScheduleInfoList!=null &&
              java.util.Arrays.equals(this.participantScheduleInfoList, other.getParticipantScheduleInfoList())));
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
        if (getParticipantScheduleInfoList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipantScheduleInfoList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getParticipantScheduleInfoList(), i);
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
        new org.apache.axis.description.TypeDesc(GetParticipantScheduleInfoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantScheduleInfoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantScheduleInfoList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participantScheduleInfoList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantScheduleInfo"));
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
