/**
 * ParticipantStatusV2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ParticipantStatusV2  extends com.zte.m900.bean.ParticipantStatus  implements java.io.Serializable {
    private boolean maxVolume;

    private String micState;

    private boolean silent;

    public ParticipantStatusV2() {
    }

    public ParticipantStatusV2(
           String audioType,
           String dualVideoFormat,
           boolean mute,
           int rate,
           String status,
           String terminalIdentifier,
           String terminalName,
           String terminalNumber,
           String videoFormat,
           boolean maxVolume,
           String micState,
           boolean silent) {
        super(
            audioType,
            dualVideoFormat,
            mute,
            rate,
            status,
            terminalIdentifier,
            terminalName,
            terminalNumber,
            videoFormat);
        this.maxVolume = maxVolume;
        this.micState = micState;
        this.silent = silent;
    }


    /**
     * Gets the maxVolume value for this ParticipantStatusV2.
     * 
     * @return maxVolume
     */
    public boolean isMaxVolume() {
        return maxVolume;
    }


    /**
     * Sets the maxVolume value for this ParticipantStatusV2.
     * 
     * @param maxVolume
     */
    public void setMaxVolume(boolean maxVolume) {
        this.maxVolume = maxVolume;
    }


    /**
     * Gets the micState value for this ParticipantStatusV2.
     * 
     * @return micState
     */
    public String getMicState() {
        return micState;
    }


    /**
     * Sets the micState value for this ParticipantStatusV2.
     * 
     * @param micState
     */
    public void setMicState(String micState) {
        this.micState = micState;
    }


    /**
     * Gets the silent value for this ParticipantStatusV2.
     * 
     * @return silent
     */
    public boolean isSilent() {
        return silent;
    }


    /**
     * Sets the silent value for this ParticipantStatusV2.
     * 
     * @param silent
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ParticipantStatusV2)) return false;
        ParticipantStatusV2 other = (ParticipantStatusV2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.maxVolume == other.isMaxVolume() &&
            ((this.micState==null && other.getMicState()==null) || 
             (this.micState!=null &&
              this.micState.equals(other.getMicState()))) &&
            this.silent == other.isSilent();
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
        _hashCode += (isMaxVolume() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMicState() != null) {
            _hashCode += getMicState().hashCode();
        }
        _hashCode += (isSilent() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParticipantStatusV2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxVolume");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxVolume"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("micState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "micState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("silent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "silent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
