/**
 * GetRecordConferenceListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetRecordConferenceListRequest  implements java.io.Serializable {
    private java.util.Calendar beginTime;

    private String confID;

    private java.util.Calendar endTime;

    private int isOffTranscode;

    private int pageIndex;

    private String rcdUserName;

    public GetRecordConferenceListRequest() {
    }

    public GetRecordConferenceListRequest(
           java.util.Calendar beginTime,
           String confID,
           java.util.Calendar endTime,
           int isOffTranscode,
           int pageIndex,
           String rcdUserName) {
           this.beginTime = beginTime;
           this.confID = confID;
           this.endTime = endTime;
           this.isOffTranscode = isOffTranscode;
           this.pageIndex = pageIndex;
           this.rcdUserName = rcdUserName;
    }


    /**
     * Gets the beginTime value for this GetRecordConferenceListRequest.
     * 
     * @return beginTime
     */
    public java.util.Calendar getBeginTime() {
        return beginTime;
    }


    /**
     * Sets the beginTime value for this GetRecordConferenceListRequest.
     * 
     * @param beginTime
     */
    public void setBeginTime(java.util.Calendar beginTime) {
        this.beginTime = beginTime;
    }


    /**
     * Gets the confID value for this GetRecordConferenceListRequest.
     * 
     * @return confID
     */
    public String getConfID() {
        return confID;
    }


    /**
     * Sets the confID value for this GetRecordConferenceListRequest.
     * 
     * @param confID
     */
    public void setConfID(String confID) {
        this.confID = confID;
    }


    /**
     * Gets the endTime value for this GetRecordConferenceListRequest.
     * 
     * @return endTime
     */
    public java.util.Calendar getEndTime() {
        return endTime;
    }


    /**
     * Sets the endTime value for this GetRecordConferenceListRequest.
     * 
     * @param endTime
     */
    public void setEndTime(java.util.Calendar endTime) {
        this.endTime = endTime;
    }


    /**
     * Gets the isOffTranscode value for this GetRecordConferenceListRequest.
     * 
     * @return isOffTranscode
     */
    public int getIsOffTranscode() {
        return isOffTranscode;
    }


    /**
     * Sets the isOffTranscode value for this GetRecordConferenceListRequest.
     * 
     * @param isOffTranscode
     */
    public void setIsOffTranscode(int isOffTranscode) {
        this.isOffTranscode = isOffTranscode;
    }


    /**
     * Gets the pageIndex value for this GetRecordConferenceListRequest.
     * 
     * @return pageIndex
     */
    public int getPageIndex() {
        return pageIndex;
    }


    /**
     * Sets the pageIndex value for this GetRecordConferenceListRequest.
     * 
     * @param pageIndex
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }


    /**
     * Gets the rcdUserName value for this GetRecordConferenceListRequest.
     * 
     * @return rcdUserName
     */
    public String getRcdUserName() {
        return rcdUserName;
    }


    /**
     * Sets the rcdUserName value for this GetRecordConferenceListRequest.
     * 
     * @param rcdUserName
     */
    public void setRcdUserName(String rcdUserName) {
        this.rcdUserName = rcdUserName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRecordConferenceListRequest)) return false;
        GetRecordConferenceListRequest other = (GetRecordConferenceListRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.beginTime==null && other.getBeginTime()==null) || 
             (this.beginTime!=null &&
              this.beginTime.equals(other.getBeginTime()))) &&
            ((this.confID==null && other.getConfID()==null) || 
             (this.confID!=null &&
              this.confID.equals(other.getConfID()))) &&
            ((this.endTime==null && other.getEndTime()==null) || 
             (this.endTime!=null &&
              this.endTime.equals(other.getEndTime()))) &&
            this.isOffTranscode == other.getIsOffTranscode() &&
            this.pageIndex == other.getPageIndex() &&
            ((this.rcdUserName==null && other.getRcdUserName()==null) || 
             (this.rcdUserName!=null &&
              this.rcdUserName.equals(other.getRcdUserName())));
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
        if (getBeginTime() != null) {
            _hashCode += getBeginTime().hashCode();
        }
        if (getConfID() != null) {
            _hashCode += getConfID().hashCode();
        }
        if (getEndTime() != null) {
            _hashCode += getEndTime().hashCode();
        }
        _hashCode += getIsOffTranscode();
        _hashCode += getPageIndex();
        if (getRcdUserName() != null) {
            _hashCode += getRcdUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRecordConferenceListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRecordConferenceListRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("beginTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "beginTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "endTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isOffTranscode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isOffTranscode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pageIndex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pageIndex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdUserName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdUserName"));
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
