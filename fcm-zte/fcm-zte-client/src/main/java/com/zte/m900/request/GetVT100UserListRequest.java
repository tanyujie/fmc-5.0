/**
 * GetVT100UserListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetVT100UserListRequest  implements java.io.Serializable {
    private int numPerPage;

    private int page;

    private int regionID;

    public GetVT100UserListRequest() {
    }

    public GetVT100UserListRequest(
           int numPerPage,
           int page,
           int regionID) {
           this.numPerPage = numPerPage;
           this.page = page;
           this.regionID = regionID;
    }


    /**
     * Gets the numPerPage value for this GetVT100UserListRequest.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetVT100UserListRequest.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the page value for this GetVT100UserListRequest.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetVT100UserListRequest.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the regionID value for this GetVT100UserListRequest.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this GetVT100UserListRequest.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVT100UserListRequest)) return false;
        GetVT100UserListRequest other = (GetVT100UserListRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numPerPage == other.getNumPerPage() &&
            this.page == other.getPage() &&
            this.regionID == other.getRegionID();
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
        _hashCode += getNumPerPage();
        _hashCode += getPage();
        _hashCode += getRegionID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetVT100UserListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVT100UserListRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numPerPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numPerPage"));
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
