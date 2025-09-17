/**
 * GetVT100UserListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetVT100UserListResponse  implements java.io.Serializable {
    private com.zte.m900.bean.VT100UserInfo[] VT100UserList;

    private String result;

    private int totalCount;

    public GetVT100UserListResponse() {
    }

    public GetVT100UserListResponse(
           com.zte.m900.bean.VT100UserInfo[] VT100UserList,
           String result,
           int totalCount) {
           this.VT100UserList = VT100UserList;
           this.result = result;
           this.totalCount = totalCount;
    }


    /**
     * Gets the VT100UserList value for this GetVT100UserListResponse.
     * 
     * @return VT100UserList
     */
    public com.zte.m900.bean.VT100UserInfo[] getVT100UserList() {
        return VT100UserList;
    }


    /**
     * Sets the VT100UserList value for this GetVT100UserListResponse.
     * 
     * @param VT100UserList
     */
    public void setVT100UserList(com.zte.m900.bean.VT100UserInfo[] VT100UserList) {
        this.VT100UserList = VT100UserList;
    }


    /**
     * Gets the result value for this GetVT100UserListResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetVT100UserListResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the totalCount value for this GetVT100UserListResponse.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetVT100UserListResponse.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVT100UserListResponse)) return false;
        GetVT100UserListResponse other = (GetVT100UserListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.VT100UserList==null && other.getVT100UserList()==null) || 
             (this.VT100UserList!=null &&
              java.util.Arrays.equals(this.VT100UserList, other.getVT100UserList()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            this.totalCount == other.getTotalCount();
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
        if (getVT100UserList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVT100UserList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVT100UserList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        _hashCode += getTotalCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetVT100UserListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VT100UserList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VT100UserList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VT100UserInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalCount"));
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
