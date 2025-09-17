/**
 * GetRegionListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetRegionListRequest  extends com.zte.m900.request.GetListRequest  implements java.io.Serializable {
    private boolean includeSubRegion;

    private int option;

    private int regionID;

    private String regionName;

    public GetRegionListRequest() {
    }

    public GetRegionListRequest(
           String account,
           boolean includeSubRegion,
           int option,
           int regionID,
           String regionName) {
        super(
            account);
        this.includeSubRegion = includeSubRegion;
        this.option = option;
        this.regionID = regionID;
        this.regionName = regionName;
    }


    /**
     * Gets the includeSubRegion value for this GetRegionListRequest.
     * 
     * @return includeSubRegion
     */
    public boolean isIncludeSubRegion() {
        return includeSubRegion;
    }


    /**
     * Sets the includeSubRegion value for this GetRegionListRequest.
     * 
     * @param includeSubRegion
     */
    public void setIncludeSubRegion(boolean includeSubRegion) {
        this.includeSubRegion = includeSubRegion;
    }


    /**
     * Gets the option value for this GetRegionListRequest.
     * 
     * @return option
     */
    public int getOption() {
        return option;
    }


    /**
     * Sets the option value for this GetRegionListRequest.
     * 
     * @param option
     */
    public void setOption(int option) {
        this.option = option;
    }


    /**
     * Gets the regionID value for this GetRegionListRequest.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this GetRegionListRequest.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the regionName value for this GetRegionListRequest.
     * 
     * @return regionName
     */
    public String getRegionName() {
        return regionName;
    }


    /**
     * Sets the regionName value for this GetRegionListRequest.
     * 
     * @param regionName
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRegionListRequest)) return false;
        GetRegionListRequest other = (GetRegionListRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.includeSubRegion == other.isIncludeSubRegion() &&
            this.option == other.getOption() &&
            this.regionID == other.getRegionID() &&
            ((this.regionName==null && other.getRegionName()==null) || 
             (this.regionName!=null &&
              this.regionName.equals(other.getRegionName())));
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
        _hashCode += (isIncludeSubRegion() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getOption();
        _hashCode += getRegionID();
        if (getRegionName() != null) {
            _hashCode += getRegionName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRegionListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeSubRegion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includeSubRegion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("option");
        elemField.setXmlName(new javax.xml.namespace.QName("", "option"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
