/**
 * UserInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class UserInfo  implements java.io.Serializable {
    private String email;

    private String memo;

    private String phone;

    private int regionID;

    private String userID;

    private int userLevel;

    private String userName;

    public UserInfo() {
    }

    public UserInfo(
           String email,
           String memo,
           String phone,
           int regionID,
           String userID,
           int userLevel,
           String userName) {
           this.email = email;
           this.memo = memo;
           this.phone = phone;
           this.regionID = regionID;
           this.userID = userID;
           this.userLevel = userLevel;
           this.userName = userName;
    }


    /**
     * Gets the email value for this UserInfo.
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this UserInfo.
     * 
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Gets the memo value for this UserInfo.
     * 
     * @return memo
     */
    public String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this UserInfo.
     * 
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }


    /**
     * Gets the phone value for this UserInfo.
     * 
     * @return phone
     */
    public String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this UserInfo.
     * 
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * Gets the regionID value for this UserInfo.
     * 
     * @return regionID
     */
    public int getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this UserInfo.
     * 
     * @param regionID
     */
    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the userID value for this UserInfo.
     * 
     * @return userID
     */
    public String getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this UserInfo.
     * 
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }


    /**
     * Gets the userLevel value for this UserInfo.
     * 
     * @return userLevel
     */
    public int getUserLevel() {
        return userLevel;
    }


    /**
     * Sets the userLevel value for this UserInfo.
     * 
     * @param userLevel
     */
    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }


    /**
     * Gets the userName value for this UserInfo.
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this UserInfo.
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UserInfo)) return false;
        UserInfo other = (UserInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.phone==null && other.getPhone()==null) || 
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            this.regionID == other.getRegionID() &&
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            this.userLevel == other.getUserLevel() &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName())));
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
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        _hashCode += getRegionID();
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        _hashCode += getUserLevel();
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "UserInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
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
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userName"));
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
