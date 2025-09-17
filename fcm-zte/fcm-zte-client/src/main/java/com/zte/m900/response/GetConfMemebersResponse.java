/**
 * GetConfMemebersResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetConfMemebersResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConfNode[] memeberlist;

    private String result;

    private int totalCount;

    public GetConfMemebersResponse() {
    }

    public GetConfMemebersResponse(
           com.zte.m900.bean.ConfNode[] memeberlist,
           String result,
           int totalCount) {
           this.memeberlist = memeberlist;
           this.result = result;
           this.totalCount = totalCount;
    }


    /**
     * Gets the memeberlist value for this GetConfMemebersResponse.
     * 
     * @return memeberlist
     */
    public com.zte.m900.bean.ConfNode[] getMemeberlist() {
        return memeberlist;
    }


    /**
     * Sets the memeberlist value for this GetConfMemebersResponse.
     * 
     * @param memeberlist
     */
    public void setMemeberlist(com.zte.m900.bean.ConfNode[] memeberlist) {
        this.memeberlist = memeberlist;
    }


    /**
     * Gets the result value for this GetConfMemebersResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetConfMemebersResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the totalCount value for this GetConfMemebersResponse.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetConfMemebersResponse.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetConfMemebersResponse)) return false;
        GetConfMemebersResponse other = (GetConfMemebersResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.memeberlist==null && other.getMemeberlist()==null) || 
             (this.memeberlist!=null &&
              java.util.Arrays.equals(this.memeberlist, other.getMemeberlist()))) &&
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
        if (getMemeberlist() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMemeberlist());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getMemeberlist(), i);
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
        new org.apache.axis.description.TypeDesc(GetConfMemebersResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConfMemebersResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memeberlist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memeberlist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConfNode"));
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
