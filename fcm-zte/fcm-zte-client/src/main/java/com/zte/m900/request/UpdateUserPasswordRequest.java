/**
 * UpdateUserPasswordRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class UpdateUserPasswordRequest  implements java.io.Serializable {
    private String memo;

    private String newPassword;

    private String oldPassword;

    private String userId;

    public UpdateUserPasswordRequest() {
    }

    public UpdateUserPasswordRequest(
           String memo,
           String newPassword,
           String oldPassword,
           String userId) {
           this.memo = memo;
           this.newPassword = newPassword;
           this.oldPassword = oldPassword;
           this.userId = userId;
    }


    /**
     * Gets the memo value for this UpdateUserPasswordRequest.
     * 
     * @return memo
     */
    public String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this UpdateUserPasswordRequest.
     * 
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }


    /**
     * Gets the newPassword value for this UpdateUserPasswordRequest.
     * 
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }


    /**
     * Sets the newPassword value for this UpdateUserPasswordRequest.
     * 
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


    /**
     * Gets the oldPassword value for this UpdateUserPasswordRequest.
     * 
     * @return oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }


    /**
     * Sets the oldPassword value for this UpdateUserPasswordRequest.
     * 
     * @param oldPassword
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }


    /**
     * Gets the userId value for this UpdateUserPasswordRequest.
     * 
     * @return userId
     */
    public String getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this UpdateUserPasswordRequest.
     * 
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UpdateUserPasswordRequest)) return false;
        UpdateUserPasswordRequest other = (UpdateUserPasswordRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.newPassword==null && other.getNewPassword()==null) || 
             (this.newPassword!=null &&
              this.newPassword.equals(other.getNewPassword()))) &&
            ((this.oldPassword==null && other.getOldPassword()==null) || 
             (this.oldPassword!=null &&
              this.oldPassword.equals(other.getOldPassword()))) &&
            ((this.userId==null && other.getUserId()==null) || 
             (this.userId!=null &&
              this.userId.equals(other.getUserId())));
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
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getNewPassword() != null) {
            _hashCode += getNewPassword().hashCode();
        }
        if (getOldPassword() != null) {
            _hashCode += getOldPassword().hashCode();
        }
        if (getUserId() != null) {
            _hashCode += getUserId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateUserPasswordRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "UpdateUserPasswordRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newPassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newPassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldPassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "oldPassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userId"));
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
