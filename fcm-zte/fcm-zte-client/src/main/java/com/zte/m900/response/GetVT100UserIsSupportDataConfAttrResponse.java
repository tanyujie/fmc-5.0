/**
 * GetVT100UserIsSupportDataConfAttrResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetVT100UserIsSupportDataConfAttrResponse  implements java.io.Serializable {
    private int isSupportDataConf;

    private String result;

    public GetVT100UserIsSupportDataConfAttrResponse() {
    }

    public GetVT100UserIsSupportDataConfAttrResponse(
           int isSupportDataConf,
           String result) {
           this.isSupportDataConf = isSupportDataConf;
           this.result = result;
    }


    /**
     * Gets the isSupportDataConf value for this GetVT100UserIsSupportDataConfAttrResponse.
     * 
     * @return isSupportDataConf
     */
    public int getIsSupportDataConf() {
        return isSupportDataConf;
    }


    /**
     * Sets the isSupportDataConf value for this GetVT100UserIsSupportDataConfAttrResponse.
     * 
     * @param isSupportDataConf
     */
    public void setIsSupportDataConf(int isSupportDataConf) {
        this.isSupportDataConf = isSupportDataConf;
    }


    /**
     * Gets the result value for this GetVT100UserIsSupportDataConfAttrResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetVT100UserIsSupportDataConfAttrResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVT100UserIsSupportDataConfAttrResponse)) return false;
        GetVT100UserIsSupportDataConfAttrResponse other = (GetVT100UserIsSupportDataConfAttrResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.isSupportDataConf == other.getIsSupportDataConf() &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult())));
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
        _hashCode += getIsSupportDataConf();
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetVT100UserIsSupportDataConfAttrResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserIsSupportDataConfAttrResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isSupportDataConf");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isSupportDataConf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
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
