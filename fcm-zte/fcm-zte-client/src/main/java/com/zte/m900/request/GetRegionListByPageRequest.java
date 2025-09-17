/**
 * GetRegionListByPageRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetRegionListByPageRequest  extends com.zte.m900.request.GetListRequest  implements java.io.Serializable {
    private boolean includeSubRegion;

    private int numPerPage;

    private int option;

    private int page;

    private int regionID;

    private String regionName;

    public GetRegionListByPageRequest() {
    }

    public GetRegionListByPageRequest(
           String account,
           boolean includeSubRegion,
           int numPerPage,
           int option,
           int page,
           int regionID,
           String regionName) {
        super(
            account);
        this.includeSubRegion = includeSubRegion;
        this.numPerPage = numPerPage;
        this.option = option;
        this.page = page;
        this.regionID = regionID;
        this.regionName = regionName;
    }


    /**
     * Gets the includeSubRegion value for this GetRegionListByPageRequest.
     * 
     * @return includeSubRegion
     */
    public boolean isIncludeSubRegion() {
        return includeSubRegion;
    }


    /**
     * Sets the includeSubRegion value for this GetRegionListByPageRequest.
     * 
     * @param includeSubRegion
     */
    public void setIncludeSubRegion(boolean includeSubRegion) {
        this.includeSubRegion = includeSubRegion;
    }


    /**
     * Gets the numPerPage value for this GetRegionListByPageRequest.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetRegionListByPageRequest.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the option value for this GetRegionListByPageRequest.
     * 
     * @return option
     */
    public int getOption() {
        return option;
    }


    /**
     * Sets the option value for this GetRegionListByPageRequest.
     * 
     * @param option
     */
    public void setOption(int option) {
        this.option = option;
    }


    /**
     * Gets the page value for this GetRegionListByPageRequest.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetRegionListByPageRequest.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the regionID value for this GetRegionListByPageRequest.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this GetRegionListByPageRequest.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the regionName value for this GetRegionListByPageRequest.
     * 
     * @return regionName
     */
    public String getRegionName() {
        return regionName;
    }


    /**
     * Sets the regionName value for this GetRegionListByPageRequest.
     * 
     * @param regionName
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRegionListByPageRequest)) return false;
        GetRegionListByPageRequest other = (GetRegionListByPageRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.includeSubRegion == other.isIncludeSubRegion() &&
            this.numPerPage == other.getNumPerPage() &&
            this.option == other.getOption() &&
            this.page == other.getPage() &&
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
        _hashCode += getNumPerPage();
        _hashCode += getOption();
        _hashCode += getPage();
        _hashCode += getRegionID();
        if (getRegionName() != null) {
            _hashCode += getRegionName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRegionListByPageRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListByPageRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeSubRegion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includeSubRegion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numPerPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numPerPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("option");
        elemField.setXmlName(new javax.xml.namespace.QName("", "option"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("page");
        elemField.setXmlName(new javax.xml.namespace.QName("", "page"));
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
