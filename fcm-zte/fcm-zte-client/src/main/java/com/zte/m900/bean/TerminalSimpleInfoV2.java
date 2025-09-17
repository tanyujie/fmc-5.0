/**
 * TerminalSimpleInfoV2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class TerminalSimpleInfoV2  extends com.zte.m900.bean.TerminalSimpleInfo  implements java.io.Serializable {
    private String regionName;

    private int regionType;

    private int terState;

    public TerminalSimpleInfoV2() {
    }

    public TerminalSimpleInfoV2(
           int callMode,
           String contactName,
           String email,
           String ipAddress,
           String memo,
           String password,
           String telephoneNumber,
           int terType,
           int terminalId,
           String terminalName,
           String terminalNumber,
           String regionName,
           int regionType,
           int terState) {
        super(
            callMode,
            contactName,
            email,
            ipAddress,
            memo,
            password,
            telephoneNumber,
            terType,
            terminalId,
            terminalName,
            terminalNumber);
        this.regionName = regionName;
        this.regionType = regionType;
        this.terState = terState;
    }


    /**
     * Gets the regionName value for this TerminalSimpleInfoV2.
     * 
     * @return regionName
     */
    public String getRegionName() {
        return regionName;
    }


    /**
     * Sets the regionName value for this TerminalSimpleInfoV2.
     * 
     * @param regionName
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    /**
     * Gets the regionType value for this TerminalSimpleInfoV2.
     * 
     * @return regionType
     */
    public int getRegionType() {
        return regionType;
    }


    /**
     * Sets the regionType value for this TerminalSimpleInfoV2.
     * 
     * @param regionType
     */
    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }


    /**
     * Gets the terState value for this TerminalSimpleInfoV2.
     * 
     * @return terState
     */
    public int getTerState() {
        return terState;
    }


    /**
     * Sets the terState value for this TerminalSimpleInfoV2.
     * 
     * @param terState
     */
    public void setTerState(int terState) {
        this.terState = terState;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof TerminalSimpleInfoV2)) return false;
        TerminalSimpleInfoV2 other = (TerminalSimpleInfoV2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.regionName==null && other.getRegionName()==null) || 
             (this.regionName!=null &&
              this.regionName.equals(other.getRegionName()))) &&
            this.regionType == other.getRegionType() &&
            this.terState == other.getTerState();
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
        if (getRegionName() != null) {
            _hashCode += getRegionName().hashCode();
        }
        _hashCode += getRegionType();
        _hashCode += getTerState();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TerminalSimpleInfoV2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfoV2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terState"));
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
