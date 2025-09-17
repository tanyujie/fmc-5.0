/**
 * ModifyConferencePasswordRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class ModifyConferencePasswordRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String newPassword;

    private String oldPassword;

    public ModifyConferencePasswordRequest() {
    }

    public ModifyConferencePasswordRequest(
           String conferenceIdentifier,
           String newPassword,
           String oldPassword) {
        super(
            conferenceIdentifier);
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }


    /**
     * Gets the newPassword value for this ModifyConferencePasswordRequest.
     * 
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }


    /**
     * Sets the newPassword value for this ModifyConferencePasswordRequest.
     * 
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


    /**
     * Gets the oldPassword value for this ModifyConferencePasswordRequest.
     * 
     * @return oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }


    /**
     * Sets the oldPassword value for this ModifyConferencePasswordRequest.
     * 
     * @param oldPassword
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ModifyConferencePasswordRequest)) return false;
        ModifyConferencePasswordRequest other = (ModifyConferencePasswordRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.newPassword==null && other.getNewPassword()==null) || 
             (this.newPassword!=null &&
              this.newPassword.equals(other.getNewPassword()))) &&
            ((this.oldPassword==null && other.getOldPassword()==null) || 
             (this.oldPassword!=null &&
              this.oldPassword.equals(other.getOldPassword())));
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
        if (getNewPassword() != null) {
            _hashCode += getNewPassword().hashCode();
        }
        if (getOldPassword() != null) {
            _hashCode += getOldPassword().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ModifyConferencePasswordRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferencePasswordRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
