/**
 * GetRecordConferenceListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetRecordConferenceListResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private int currentPage;

    private com.zte.m900.bean.QueryConfInfo[] recordConfInfo;

    private int totalLine;

    private int totalPage;

    public GetRecordConferenceListResponse() {
    }

    public GetRecordConferenceListResponse(
           String result,
           int currentPage,
           com.zte.m900.bean.QueryConfInfo[] recordConfInfo,
           int totalLine,
           int totalPage) {
        super(
            result);
        this.currentPage = currentPage;
        this.recordConfInfo = recordConfInfo;
        this.totalLine = totalLine;
        this.totalPage = totalPage;
    }


    /**
     * Gets the currentPage value for this GetRecordConferenceListResponse.
     * 
     * @return currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }


    /**
     * Sets the currentPage value for this GetRecordConferenceListResponse.
     * 
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }


    /**
     * Gets the recordConfInfo value for this GetRecordConferenceListResponse.
     * 
     * @return recordConfInfo
     */
    public com.zte.m900.bean.QueryConfInfo[] getRecordConfInfo() {
        return recordConfInfo;
    }


    /**
     * Sets the recordConfInfo value for this GetRecordConferenceListResponse.
     * 
     * @param recordConfInfo
     */
    public void setRecordConfInfo(com.zte.m900.bean.QueryConfInfo[] recordConfInfo) {
        this.recordConfInfo = recordConfInfo;
    }


    /**
     * Gets the totalLine value for this GetRecordConferenceListResponse.
     * 
     * @return totalLine
     */
    public int getTotalLine() {
        return totalLine;
    }


    /**
     * Sets the totalLine value for this GetRecordConferenceListResponse.
     * 
     * @param totalLine
     */
    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }


    /**
     * Gets the totalPage value for this GetRecordConferenceListResponse.
     * 
     * @return totalPage
     */
    public int getTotalPage() {
        return totalPage;
    }


    /**
     * Sets the totalPage value for this GetRecordConferenceListResponse.
     * 
     * @param totalPage
     */
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRecordConferenceListResponse)) return false;
        GetRecordConferenceListResponse other = (GetRecordConferenceListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.currentPage == other.getCurrentPage() &&
            ((this.recordConfInfo==null && other.getRecordConfInfo()==null) || 
             (this.recordConfInfo!=null &&
              java.util.Arrays.equals(this.recordConfInfo, other.getRecordConfInfo()))) &&
            this.totalLine == other.getTotalLine() &&
            this.totalPage == other.getTotalPage();
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
        _hashCode += getCurrentPage();
        if (getRecordConfInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRecordConfInfo());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getRecordConfInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTotalLine();
        _hashCode += getTotalPage();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRecordConferenceListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecordConferenceListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordConfInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recordConfInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "QueryConfInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalLine");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalPage"));
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
