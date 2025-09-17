/**
 * GetConfMemebersRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetConfMemebersRequest  implements java.io.Serializable {
    private int confType;

    private String conferenceIdentifier;

    private int numPerPage;

    private int page;

    public GetConfMemebersRequest() {
    }

    public GetConfMemebersRequest(
           int confType,
           String conferenceIdentifier,
           int numPerPage,
           int page) {
           this.confType = confType;
           this.conferenceIdentifier = conferenceIdentifier;
           this.numPerPage = numPerPage;
           this.page = page;
    }


    /**
     * Gets the confType value for this GetConfMemebersRequest.
     * 
     * @return confType
     */
    public int getConfType() {
        return confType;
    }


    /**
     * Sets the confType value for this GetConfMemebersRequest.
     * 
     * @param confType
     */
    public void setConfType(int confType) {
        this.confType = confType;
    }


    /**
     * Gets the conferenceIdentifier value for this GetConfMemebersRequest.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this GetConfMemebersRequest.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the numPerPage value for this GetConfMemebersRequest.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetConfMemebersRequest.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the page value for this GetConfMemebersRequest.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetConfMemebersRequest.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetConfMemebersRequest)) return false;
        GetConfMemebersRequest other = (GetConfMemebersRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.confType == other.getConfType() &&
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            this.numPerPage == other.getNumPerPage() &&
            this.page == other.getPage();
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
        _hashCode += getConfType();
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        _hashCode += getNumPerPage();
        _hashCode += getPage();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetConfMemebersRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConfMemebersRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
