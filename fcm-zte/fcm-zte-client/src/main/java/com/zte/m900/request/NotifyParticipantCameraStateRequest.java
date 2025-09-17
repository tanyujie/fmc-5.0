/**
 * NotifyParticipantCameraStateRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class NotifyParticipantCameraStateRequest  implements java.io.Serializable {
    private int cameraState;

    private String conferenceNumber;

    private String terminalNumber;

    public NotifyParticipantCameraStateRequest() {
    }

    public NotifyParticipantCameraStateRequest(
           int cameraState,
           String conferenceNumber,
           String terminalNumber) {
           this.cameraState = cameraState;
           this.conferenceNumber = conferenceNumber;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the cameraState value for this NotifyParticipantCameraStateRequest.
     * 
     * @return cameraState
     */
    public int getCameraState() {
        return cameraState;
    }


    /**
     * Sets the cameraState value for this NotifyParticipantCameraStateRequest.
     * 
     * @param cameraState
     */
    public void setCameraState(int cameraState) {
        this.cameraState = cameraState;
    }


    /**
     * Gets the conferenceNumber value for this NotifyParticipantCameraStateRequest.
     * 
     * @return conferenceNumber
     */
    public String getConferenceNumber() {
        return conferenceNumber;
    }


    /**
     * Sets the conferenceNumber value for this NotifyParticipantCameraStateRequest.
     * 
     * @param conferenceNumber
     */
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }


    /**
     * Gets the terminalNumber value for this NotifyParticipantCameraStateRequest.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this NotifyParticipantCameraStateRequest.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof NotifyParticipantCameraStateRequest)) return false;
        NotifyParticipantCameraStateRequest other = (NotifyParticipantCameraStateRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cameraState == other.getCameraState() &&
            ((this.conferenceNumber==null && other.getConferenceNumber()==null) || 
             (this.conferenceNumber!=null &&
              this.conferenceNumber.equals(other.getConferenceNumber()))) &&
            ((this.terminalNumber==null && other.getTerminalNumber()==null) || 
             (this.terminalNumber!=null &&
              this.terminalNumber.equals(other.getTerminalNumber())));
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
        _hashCode += getCameraState();
        if (getConferenceNumber() != null) {
            _hashCode += getConferenceNumber().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NotifyParticipantCameraStateRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantCameraStateRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cameraState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cameraState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalNumber"));
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
