/**
 * ParticipantStatusV3.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantStatusV3  implements java.io.Serializable {
    private com.zte.m900.bean.ParticipantExtInfo[] participantExtInfos;

    private com.zte.m900.bean.ParticipantStatusV2 participantStatus;

    public ParticipantStatusV3() {
    }

    public ParticipantStatusV3(
           com.zte.m900.bean.ParticipantExtInfo[] participantExtInfos,
           com.zte.m900.bean.ParticipantStatusV2 participantStatus) {
           this.participantExtInfos = participantExtInfos;
           this.participantStatus = participantStatus;
    }


    /**
     * Gets the participantExtInfos value for this ParticipantStatusV3.
     * 
     * @return participantExtInfos
     */
    public com.zte.m900.bean.ParticipantExtInfo[] getParticipantExtInfos() {
        return participantExtInfos;
    }


    /**
     * Sets the participantExtInfos value for this ParticipantStatusV3.
     * 
     * @param participantExtInfos
     */
    public void setParticipantExtInfos(com.zte.m900.bean.ParticipantExtInfo[] participantExtInfos) {
        this.participantExtInfos = participantExtInfos;
    }


    /**
     * Gets the participantStatus value for this ParticipantStatusV3.
     * 
     * @return participantStatus
     */
    public com.zte.m900.bean.ParticipantStatusV2 getParticipantStatus() {
        return participantStatus;
    }


    /**
     * Sets the participantStatus value for this ParticipantStatusV3.
     * 
     * @param participantStatus
     */
    public void setParticipantStatus(com.zte.m900.bean.ParticipantStatusV2 participantStatus) {
        this.participantStatus = participantStatus;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantStatusV3)) return false;
        ParticipantStatusV3 other = (ParticipantStatusV3) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.participantExtInfos==null && other.getParticipantExtInfos()==null) || 
             (this.participantExtInfos!=null &&
              java.util.Arrays.equals(this.participantExtInfos, other.getParticipantExtInfos()))) &&
            ((this.participantStatus==null && other.getParticipantStatus()==null) || 
             (this.participantStatus!=null &&
              this.participantStatus.equals(other.getParticipantStatus())));
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
        if (getParticipantExtInfos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipantExtInfos());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getParticipantExtInfos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getParticipantStatus() != null) {
            _hashCode += getParticipantStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantStatusV3.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV3"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantExtInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participantExtInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantExtInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participantStatus"));
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
