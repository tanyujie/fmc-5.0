/**
 * EmeetingloginResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class EmeetingloginResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private String randomKey;

    private com.zte.m900.bean.UserInfo userInfo;

    public EmeetingloginResponse() {
    }

    public EmeetingloginResponse(
           String result,
           String randomKey,
           com.zte.m900.bean.UserInfo userInfo) {
        super(
            result);
        this.randomKey = randomKey;
        this.userInfo = userInfo;
    }


    /**
     * Gets the randomKey value for this EmeetingloginResponse.
     * 
     * @return randomKey
     */
    public String getRandomKey() {
        return randomKey;
    }


    /**
     * Sets the randomKey value for this EmeetingloginResponse.
     * 
     * @param randomKey
     */
    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }


    /**
     * Gets the userInfo value for this EmeetingloginResponse.
     * 
     * @return userInfo
     */
    public com.zte.m900.bean.UserInfo getUserInfo() {
        return userInfo;
    }


    /**
     * Sets the userInfo value for this EmeetingloginResponse.
     * 
     * @param userInfo
     */
    public void setUserInfo(com.zte.m900.bean.UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof EmeetingloginResponse)) return false;
        EmeetingloginResponse other = (EmeetingloginResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.randomKey==null && other.getRandomKey()==null) || 
             (this.randomKey!=null &&
              this.randomKey.equals(other.getRandomKey()))) &&
            ((this.userInfo==null && other.getUserInfo()==null) || 
             (this.userInfo!=null &&
              this.userInfo.equals(other.getUserInfo())));
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
        if (getRandomKey() != null) {
            _hashCode += getRandomKey().hashCode();
        }
        if (getUserInfo() != null) {
            _hashCode += getUserInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EmeetingloginResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "EmeetingloginResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("randomKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "randomKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userInfo"));
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
