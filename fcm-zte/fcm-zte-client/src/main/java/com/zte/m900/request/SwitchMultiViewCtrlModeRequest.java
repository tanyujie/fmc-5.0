/**
 * SwitchMultiViewCtrlModeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SwitchMultiViewCtrlModeRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String multiViewMode;

    public SwitchMultiViewCtrlModeRequest() {
    }

    public SwitchMultiViewCtrlModeRequest(
           String conferenceIdentifier,
           String multiViewMode) {
        super(
            conferenceIdentifier);
        this.multiViewMode = multiViewMode;
    }


    /**
     * Gets the multiViewMode value for this SwitchMultiViewCtrlModeRequest.
     * 
     * @return multiViewMode
     */
    public String getMultiViewMode() {
        return multiViewMode;
    }


    /**
     * Sets the multiViewMode value for this SwitchMultiViewCtrlModeRequest.
     * 
     * @param multiViewMode
     */
    public void setMultiViewMode(String multiViewMode) {
        this.multiViewMode = multiViewMode;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SwitchMultiViewCtrlModeRequest)) return false;
        SwitchMultiViewCtrlModeRequest other = (SwitchMultiViewCtrlModeRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.multiViewMode==null && other.getMultiViewMode()==null) || 
             (this.multiViewMode!=null &&
              this.multiViewMode.equals(other.getMultiViewMode())));
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
        if (getMultiViewMode() != null) {
            _hashCode += getMultiViewMode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SwitchMultiViewCtrlModeRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SwitchMultiViewCtrlModeRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewMode"));
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
