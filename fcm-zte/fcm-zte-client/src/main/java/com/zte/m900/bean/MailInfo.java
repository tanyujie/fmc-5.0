/**
 * MailInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class MailInfo  implements java.io.Serializable {
    private String mailAddress;

    private String mailContent;

    private String mailSubject;

    public MailInfo() {
    }

    public MailInfo(
           String mailAddress,
           String mailContent,
           String mailSubject) {
           this.mailAddress = mailAddress;
           this.mailContent = mailContent;
           this.mailSubject = mailSubject;
    }


    /**
     * Gets the mailAddress value for this MailInfo.
     * 
     * @return mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }


    /**
     * Sets the mailAddress value for this MailInfo.
     * 
     * @param mailAddress
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }


    /**
     * Gets the mailContent value for this MailInfo.
     * 
     * @return mailContent
     */
    public String getMailContent() {
        return mailContent;
    }


    /**
     * Sets the mailContent value for this MailInfo.
     * 
     * @param mailContent
     */
    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }


    /**
     * Gets the mailSubject value for this MailInfo.
     * 
     * @return mailSubject
     */
    public String getMailSubject() {
        return mailSubject;
    }


    /**
     * Sets the mailSubject value for this MailInfo.
     * 
     * @param mailSubject
     */
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MailInfo)) return false;
        MailInfo other = (MailInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mailAddress==null && other.getMailAddress()==null) || 
             (this.mailAddress!=null &&
              this.mailAddress.equals(other.getMailAddress()))) &&
            ((this.mailContent==null && other.getMailContent()==null) || 
             (this.mailContent!=null &&
              this.mailContent.equals(other.getMailContent()))) &&
            ((this.mailSubject==null && other.getMailSubject()==null) || 
             (this.mailSubject!=null &&
              this.mailSubject.equals(other.getMailSubject())));
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
        if (getMailAddress() != null) {
            _hashCode += getMailAddress().hashCode();
        }
        if (getMailContent() != null) {
            _hashCode += getMailContent().hashCode();
        }
        if (getMailSubject() != null) {
            _hashCode += getMailSubject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MailInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "MailInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mailContent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mailContent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mailSubject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mailSubject"));
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
