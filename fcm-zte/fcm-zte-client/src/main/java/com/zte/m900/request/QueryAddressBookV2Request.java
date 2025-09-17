/**
 * QueryAddressBookV2Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class QueryAddressBookV2Request  extends com.zte.m900.request.GetListRequest  implements java.io.Serializable {
    private int numPerPage;

    private int option;

    private int page;

    private int regionID;

    private String terName;

    private String terNo;

    public QueryAddressBookV2Request() {
    }

    public QueryAddressBookV2Request(
           String account,
           int numPerPage,
           int option,
           int page,
           int regionID,
           String terName,
           String terNo) {
        super(
            account);
        this.numPerPage = numPerPage;
        this.option = option;
        this.page = page;
        this.regionID = regionID;
        this.terName = terName;
        this.terNo = terNo;
    }


    /**
     * Gets the numPerPage value for this QueryAddressBookV2Request.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this QueryAddressBookV2Request.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the option value for this QueryAddressBookV2Request.
     * 
     * @return option
     */
    public int getOption() {
        return option;
    }


    /**
     * Sets the option value for this QueryAddressBookV2Request.
     * 
     * @param option
     */
    public void setOption(int option) {
        this.option = option;
    }


    /**
     * Gets the page value for this QueryAddressBookV2Request.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this QueryAddressBookV2Request.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the regionID value for this QueryAddressBookV2Request.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this QueryAddressBookV2Request.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the terName value for this QueryAddressBookV2Request.
     * 
     * @return terName
     */
    public String getTerName() {
        return terName;
    }


    /**
     * Sets the terName value for this QueryAddressBookV2Request.
     * 
     * @param terName
     */
    public void setTerName(String terName) {
        this.terName = terName;
    }


    /**
     * Gets the terNo value for this QueryAddressBookV2Request.
     * 
     * @return terNo
     */
    public String getTerNo() {
        return terNo;
    }


    /**
     * Sets the terNo value for this QueryAddressBookV2Request.
     * 
     * @param terNo
     */
    public void setTerNo(String terNo) {
        this.terNo = terNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof QueryAddressBookV2Request)) return false;
        QueryAddressBookV2Request other = (QueryAddressBookV2Request) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.numPerPage == other.getNumPerPage() &&
            this.option == other.getOption() &&
            this.page == other.getPage() &&
            this.regionID == other.getRegionID() &&
            ((this.terName==null && other.getTerName()==null) || 
             (this.terName!=null &&
              this.terName.equals(other.getTerName()))) &&
            ((this.terNo==null && other.getTerNo()==null) || 
             (this.terNo!=null &&
              this.terNo.equals(other.getTerNo())));
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
        _hashCode += getNumPerPage();
        _hashCode += getOption();
        _hashCode += getPage();
        _hashCode += getRegionID();
        if (getTerName() != null) {
            _hashCode += getTerName().hashCode();
        }
        if (getTerNo() != null) {
            _hashCode += getTerNo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryAddressBookV2Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryAddressBookV2Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("terName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terNo"));
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
