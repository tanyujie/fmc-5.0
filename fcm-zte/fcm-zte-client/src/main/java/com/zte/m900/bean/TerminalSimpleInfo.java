/**
 * TerminalSimpleInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class TerminalSimpleInfo  implements java.io.Serializable {
    private int callMode;

    private String contactName;

    private String email;

    private String ipAddress;

    private String memo;

    private String password;

    private String telephoneNumber;

    private int terType;

    private int terminalId;

    private String terminalName;

    private String terminalNumber;

    public TerminalSimpleInfo() {
    }

    public TerminalSimpleInfo(
           int callMode,
           String contactName,
           String email,
           String ipAddress,
           String memo,
           String password,
           String telephoneNumber,
           int terType,
           int terminalId,
           String terminalName,
           String terminalNumber) {
           this.callMode = callMode;
           this.contactName = contactName;
           this.email = email;
           this.ipAddress = ipAddress;
           this.memo = memo;
           this.password = password;
           this.telephoneNumber = telephoneNumber;
           this.terType = terType;
           this.terminalId = terminalId;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the callMode value for this TerminalSimpleInfo.
     * 
     * @return callMode
     */
    public int getCallMode() {
        return callMode;
    }


    /**
     * Sets the callMode value for this TerminalSimpleInfo.
     * 
     * @param callMode
     */
    public void setCallMode(int callMode) {
        this.callMode = callMode;
    }


    /**
     * Gets the contactName value for this TerminalSimpleInfo.
     * 
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }


    /**
     * Sets the contactName value for this TerminalSimpleInfo.
     * 
     * @param contactName
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    /**
     * Gets the email value for this TerminalSimpleInfo.
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this TerminalSimpleInfo.
     * 
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Gets the ipAddress value for this TerminalSimpleInfo.
     * 
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this TerminalSimpleInfo.
     * 
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the memo value for this TerminalSimpleInfo.
     * 
     * @return memo
     */
    public String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this TerminalSimpleInfo.
     * 
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }


    /**
     * Gets the password value for this TerminalSimpleInfo.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this TerminalSimpleInfo.
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Gets the telephoneNumber value for this TerminalSimpleInfo.
     * 
     * @return telephoneNumber
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }


    /**
     * Sets the telephoneNumber value for this TerminalSimpleInfo.
     * 
     * @param telephoneNumber
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }


    /**
     * Gets the terType value for this TerminalSimpleInfo.
     * 
     * @return terType
     */
    public int getTerType() {
        return terType;
    }


    /**
     * Sets the terType value for this TerminalSimpleInfo.
     * 
     * @param terType
     */
    public void setTerType(int terType) {
        this.terType = terType;
    }


    /**
     * Gets the terminalId value for this TerminalSimpleInfo.
     * 
     * @return terminalId
     */
    public int getTerminalId() {
        return terminalId;
    }


    /**
     * Sets the terminalId value for this TerminalSimpleInfo.
     * 
     * @param terminalId
     */
    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }


    /**
     * Gets the terminalName value for this TerminalSimpleInfo.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this TerminalSimpleInfo.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this TerminalSimpleInfo.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this TerminalSimpleInfo.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof TerminalSimpleInfo)) return false;
        TerminalSimpleInfo other = (TerminalSimpleInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.callMode == other.getCallMode() &&
            ((this.contactName==null && other.getContactName()==null) || 
             (this.contactName!=null &&
              this.contactName.equals(other.getContactName()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.telephoneNumber==null && other.getTelephoneNumber()==null) || 
             (this.telephoneNumber!=null &&
              this.telephoneNumber.equals(other.getTelephoneNumber()))) &&
            this.terType == other.getTerType() &&
            this.terminalId == other.getTerminalId() &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.terminalNumber==null && other.getTerminalNumber()==null) || 
             (this.terminalNumber!=null &&
              this.terminalNumber.equals(other.getTerminalNumber())));
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
        _hashCode += getCallMode();
        if (getContactName() != null) {
            _hashCode += getContactName().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getTelephoneNumber() != null) {
            _hashCode += getTelephoneNumber().hashCode();
        }
        _hashCode += getTerType();
        _hashCode += getTerminalId();
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TerminalSimpleInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "contactName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "telephoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalNumber"));
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
