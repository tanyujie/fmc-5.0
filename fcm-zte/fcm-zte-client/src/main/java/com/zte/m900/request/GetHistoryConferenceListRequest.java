/**
 * GetHistoryConferenceListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetHistoryConferenceListRequest  extends com.zte.m900.request.GetListRequest  implements java.io.Serializable {
    private String conferenceName;

    private String conferenceNumber;

    private int numPerPage;

    private int option;

    private int page;

    private java.util.Calendar startTime;

    public GetHistoryConferenceListRequest() {
    }

    public GetHistoryConferenceListRequest(
           String account,
           String conferenceName,
           String conferenceNumber,
           int numPerPage,
           int option,
           int page,
           java.util.Calendar startTime) {
        super(
            account);
        this.conferenceName = conferenceName;
        this.conferenceNumber = conferenceNumber;
        this.numPerPage = numPerPage;
        this.option = option;
        this.page = page;
        this.startTime = startTime;
    }


    /**
     * Gets the conferenceName value for this GetHistoryConferenceListRequest.
     * 
     * @return conferenceName
     */
    public String getConferenceName() {
        return conferenceName;
    }


    /**
     * Sets the conferenceName value for this GetHistoryConferenceListRequest.
     * 
     * @param conferenceName
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }


    /**
     * Gets the conferenceNumber value for this GetHistoryConferenceListRequest.
     * 
     * @return conferenceNumber
     */
    public String getConferenceNumber() {
        return conferenceNumber;
    }


    /**
     * Sets the conferenceNumber value for this GetHistoryConferenceListRequest.
     * 
     * @param conferenceNumber
     */
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }


    /**
     * Gets the numPerPage value for this GetHistoryConferenceListRequest.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetHistoryConferenceListRequest.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the option value for this GetHistoryConferenceListRequest.
     * 
     * @return option
     */
    public int getOption() {
        return option;
    }


    /**
     * Sets the option value for this GetHistoryConferenceListRequest.
     * 
     * @param option
     */
    public void setOption(int option) {
        this.option = option;
    }


    /**
     * Gets the page value for this GetHistoryConferenceListRequest.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetHistoryConferenceListRequest.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the startTime value for this GetHistoryConferenceListRequest.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this GetHistoryConferenceListRequest.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetHistoryConferenceListRequest)) return false;
        GetHistoryConferenceListRequest other = (GetHistoryConferenceListRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceName==null && other.getConferenceName()==null) || 
             (this.conferenceName!=null &&
              this.conferenceName.equals(other.getConferenceName()))) &&
            ((this.conferenceNumber==null && other.getConferenceNumber()==null) || 
             (this.conferenceNumber!=null &&
              this.conferenceNumber.equals(other.getConferenceNumber()))) &&
            this.numPerPage == other.getNumPerPage() &&
            this.option == other.getOption() &&
            this.page == other.getPage() &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime())));
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
        if (getConferenceName() != null) {
            _hashCode += getConferenceName().hashCode();
        }
        if (getConferenceNumber() != null) {
            _hashCode += getConferenceNumber().hashCode();
        }
        _hashCode += getNumPerPage();
        _hashCode += getOption();
        _hashCode += getPage();
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetHistoryConferenceListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetHistoryConferenceListRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceNumber"));
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
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
