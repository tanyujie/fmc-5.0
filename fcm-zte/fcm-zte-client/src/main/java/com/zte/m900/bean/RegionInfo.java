/**
 * RegionInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class RegionInfo  implements java.io.Serializable {
    private int regionID;

    private String regionName;

    private String regionPath;

    private int regionType;

    public RegionInfo() {
    }

    public RegionInfo(
           int regionID,
           String regionName,
           String regionPath,
           int regionType) {
           this.regionID = regionID;
           this.regionName = regionName;
           this.regionPath = regionPath;
           this.regionType = regionType;
    }


    /**
     * Gets the regionID value for this RegionInfo.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this RegionInfo.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the regionName value for this RegionInfo.
     * 
     * @return regionName
     */
    public String getRegionName() {
        return regionName;
    }


    /**
     * Sets the regionName value for this RegionInfo.
     * 
     * @param regionName
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    /**
     * Gets the regionPath value for this RegionInfo.
     * 
     * @return regionPath
     */
    public String getRegionPath() {
        return regionPath;
    }


    /**
     * Sets the regionPath value for this RegionInfo.
     * 
     * @param regionPath
     */
    public void setRegionPath(String regionPath) {
        this.regionPath = regionPath;
    }


    /**
     * Gets the regionType value for this RegionInfo.
     * 
     * @return regionType
     */
    public int getRegionType() {
        return regionType;
    }


    /**
     * Sets the regionType value for this RegionInfo.
     * 
     * @param regionType
     */
    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RegionInfo)) return false;
        RegionInfo other = (RegionInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.regionID == other.getRegionID() &&
            ((this.regionName==null && other.getRegionName()==null) || 
             (this.regionName!=null &&
              this.regionName.equals(other.getRegionName()))) &&
            ((this.regionPath==null && other.getRegionPath()==null) || 
             (this.regionPath!=null &&
              this.regionPath.equals(other.getRegionPath()))) &&
            this.regionType == other.getRegionType();
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
        _hashCode += getRegionID();
        if (getRegionName() != null) {
            _hashCode += getRegionName().hashCode();
        }
        if (getRegionPath() != null) {
            _hashCode += getRegionPath().hashCode();
        }
        _hashCode += getRegionType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegionInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "RegionInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionPath");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionPath"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionType"));
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
