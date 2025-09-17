/**
 * DelVT100UserResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class DelVT100UserResponse  implements java.io.Serializable {
    private com.zte.m900.bean.FailInfo[] VT100FailInfo;

    private String result;

    public DelVT100UserResponse() {
    }

    public DelVT100UserResponse(
           com.zte.m900.bean.FailInfo[] VT100FailInfo,
           String result) {
           this.VT100FailInfo = VT100FailInfo;
           this.result = result;
    }


    /**
     * Gets the VT100FailInfo value for this DelVT100UserResponse.
     * 
     * @return VT100FailInfo
     */
    public com.zte.m900.bean.FailInfo[] getVT100FailInfo() {
        return VT100FailInfo;
    }


    /**
     * Sets the VT100FailInfo value for this DelVT100UserResponse.
     * 
     * @param VT100FailInfo
     */
    public void setVT100FailInfo(com.zte.m900.bean.FailInfo[] VT100FailInfo) {
        this.VT100FailInfo = VT100FailInfo;
    }


    /**
     * Gets the result value for this DelVT100UserResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this DelVT100UserResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof DelVT100UserResponse)) return false;
        DelVT100UserResponse other = (DelVT100UserResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.VT100FailInfo==null && other.getVT100FailInfo()==null) || 
             (this.VT100FailInfo!=null &&
              java.util.Arrays.equals(this.VT100FailInfo, other.getVT100FailInfo()))) &&
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
        if (getVT100FailInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVT100FailInfo());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVT100FailInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DelVT100UserResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DelVT100UserResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VT100FailInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VT100FailInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "FailInfo"));
        elemField.setNillable(true);
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
