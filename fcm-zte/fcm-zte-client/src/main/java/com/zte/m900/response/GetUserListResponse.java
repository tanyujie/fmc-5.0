/**
 * GetUserListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetUserListResponse  implements java.io.Serializable {
    private String result;

    private int totalCount;

    private com.zte.m900.bean.UserInfo[] userList;

    public GetUserListResponse() {
    }

    public GetUserListResponse(
           String result,
           int totalCount,
           com.zte.m900.bean.UserInfo[] userList) {
           this.result = result;
           this.totalCount = totalCount;
           this.userList = userList;
    }


    /**
     * Gets the result value for this GetUserListResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetUserListResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the totalCount value for this GetUserListResponse.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetUserListResponse.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    /**
     * Gets the userList value for this GetUserListResponse.
     * 
     * @return userList
     */
    public com.zte.m900.bean.UserInfo[] getUserList() {
        return userList;
    }


    /**
     * Sets the userList value for this GetUserListResponse.
     * 
     * @param userList
     */
    public void setUserList(com.zte.m900.bean.UserInfo[] userList) {
        this.userList = userList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetUserListResponse)) return false;
        GetUserListResponse other = (GetUserListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            this.totalCount == other.getTotalCount() &&
            ((this.userList==null && other.getUserList()==null) || 
             (this.userList!=null &&
              java.util.Arrays.equals(this.userList, other.getUserList())));
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
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        _hashCode += getTotalCount();
        if (getUserList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUserList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getUserList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetUserListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetUserListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "UserInfo"));
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
