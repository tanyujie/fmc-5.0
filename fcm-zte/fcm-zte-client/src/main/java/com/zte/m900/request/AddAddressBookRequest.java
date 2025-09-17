/**
 * AddAddressBookRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class AddAddressBookRequest  implements java.io.Serializable {
    private String account;

    private com.zte.m900.bean.TerminalSimpleInfo terminalInfo;

    public AddAddressBookRequest() {
    }

    public AddAddressBookRequest(
           String account,
           com.zte.m900.bean.TerminalSimpleInfo terminalInfo) {
           this.account = account;
           this.terminalInfo = terminalInfo;
    }


    /**
     * Gets the account value for this AddAddressBookRequest.
     * 
     * @return account
     */
    public String getAccount() {
        return account;
    }


    /**
     * Sets the account value for this AddAddressBookRequest.
     * 
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }


    /**
     * Gets the terminalInfo value for this AddAddressBookRequest.
     * 
     * @return terminalInfo
     */
    public com.zte.m900.bean.TerminalSimpleInfo getTerminalInfo() {
        return terminalInfo;
    }


    /**
     * Sets the terminalInfo value for this AddAddressBookRequest.
     * 
     * @param terminalInfo
     */
    public void setTerminalInfo(com.zte.m900.bean.TerminalSimpleInfo terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AddAddressBookRequest)) return false;
        AddAddressBookRequest other = (AddAddressBookRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.account==null && other.getAccount()==null) || 
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            ((this.terminalInfo==null && other.getTerminalInfo()==null) || 
             (this.terminalInfo!=null &&
              this.terminalInfo.equals(other.getTerminalInfo())));
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
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getTerminalInfo() != null) {
            _hashCode += getTerminalInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddAddressBookRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "AddAddressBookRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("", "account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfo"));
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
