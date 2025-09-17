/**
 * CameraInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class CameraInfo  implements java.io.Serializable {
    private int actionFocus;

    private int actionPan;

    private int actionTilt;

    private int actionZoom;

    private int cameraNo;

    private String encodedCharacters;

    private int presetsNumber;

    public CameraInfo() {
    }

    public CameraInfo(
           int actionFocus,
           int actionPan,
           int actionTilt,
           int actionZoom,
           int cameraNo,
           String encodedCharacters,
           int presetsNumber) {
           this.actionFocus = actionFocus;
           this.actionPan = actionPan;
           this.actionTilt = actionTilt;
           this.actionZoom = actionZoom;
           this.cameraNo = cameraNo;
           this.encodedCharacters = encodedCharacters;
           this.presetsNumber = presetsNumber;
    }


    /**
     * Gets the actionFocus value for this CameraInfo.
     * 
     * @return actionFocus
     */
    public int getActionFocus() {
        return actionFocus;
    }


    /**
     * Sets the actionFocus value for this CameraInfo.
     * 
     * @param actionFocus
     */
    public void setActionFocus(int actionFocus) {
        this.actionFocus = actionFocus;
    }


    /**
     * Gets the actionPan value for this CameraInfo.
     * 
     * @return actionPan
     */
    public int getActionPan() {
        return actionPan;
    }


    /**
     * Sets the actionPan value for this CameraInfo.
     * 
     * @param actionPan
     */
    public void setActionPan(int actionPan) {
        this.actionPan = actionPan;
    }


    /**
     * Gets the actionTilt value for this CameraInfo.
     * 
     * @return actionTilt
     */
    public int getActionTilt() {
        return actionTilt;
    }


    /**
     * Sets the actionTilt value for this CameraInfo.
     * 
     * @param actionTilt
     */
    public void setActionTilt(int actionTilt) {
        this.actionTilt = actionTilt;
    }


    /**
     * Gets the actionZoom value for this CameraInfo.
     * 
     * @return actionZoom
     */
    public int getActionZoom() {
        return actionZoom;
    }


    /**
     * Sets the actionZoom value for this CameraInfo.
     * 
     * @param actionZoom
     */
    public void setActionZoom(int actionZoom) {
        this.actionZoom = actionZoom;
    }


    /**
     * Gets the cameraNo value for this CameraInfo.
     * 
     * @return cameraNo
     */
    public int getCameraNo() {
        return cameraNo;
    }


    /**
     * Sets the cameraNo value for this CameraInfo.
     * 
     * @param cameraNo
     */
    public void setCameraNo(int cameraNo) {
        this.cameraNo = cameraNo;
    }


    /**
     * Gets the encodedCharacters value for this CameraInfo.
     * 
     * @return encodedCharacters
     */
    public String getEncodedCharacters() {
        return encodedCharacters;
    }


    /**
     * Sets the encodedCharacters value for this CameraInfo.
     * 
     * @param encodedCharacters
     */
    public void setEncodedCharacters(String encodedCharacters) {
        this.encodedCharacters = encodedCharacters;
    }


    /**
     * Gets the presetsNumber value for this CameraInfo.
     * 
     * @return presetsNumber
     */
    public int getPresetsNumber() {
        return presetsNumber;
    }


    /**
     * Sets the presetsNumber value for this CameraInfo.
     * 
     * @param presetsNumber
     */
    public void setPresetsNumber(int presetsNumber) {
        this.presetsNumber = presetsNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CameraInfo)) return false;
        CameraInfo other = (CameraInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.actionFocus == other.getActionFocus() &&
            this.actionPan == other.getActionPan() &&
            this.actionTilt == other.getActionTilt() &&
            this.actionZoom == other.getActionZoom() &&
            this.cameraNo == other.getCameraNo() &&
            ((this.encodedCharacters==null && other.getEncodedCharacters()==null) || 
             (this.encodedCharacters!=null &&
              this.encodedCharacters.equals(other.getEncodedCharacters()))) &&
            this.presetsNumber == other.getPresetsNumber();
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
        _hashCode += getActionFocus();
        _hashCode += getActionPan();
        _hashCode += getActionTilt();
        _hashCode += getActionZoom();
        _hashCode += getCameraNo();
        if (getEncodedCharacters() != null) {
            _hashCode += getEncodedCharacters().hashCode();
        }
        _hashCode += getPresetsNumber();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CameraInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "CameraInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionFocus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actionFocus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionPan");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actionPan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionTilt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actionTilt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionZoom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actionZoom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cameraNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cameraNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encodedCharacters");
        elemField.setXmlName(new javax.xml.namespace.QName("", "encodedCharacters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("presetsNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "presetsNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
