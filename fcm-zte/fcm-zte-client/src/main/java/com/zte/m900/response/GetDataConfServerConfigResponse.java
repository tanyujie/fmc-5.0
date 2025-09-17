/**
 * GetDataConfServerConfigResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetDataConfServerConfigResponse  implements java.io.Serializable {
    private int enableSvrCfg;

    private String ipAddress;

    private String pwd;

    private String result;

    private String userName;

    public GetDataConfServerConfigResponse() {
    }

    public GetDataConfServerConfigResponse(
           int enableSvrCfg,
           String ipAddress,
           String pwd,
           String result,
           String userName) {
           this.enableSvrCfg = enableSvrCfg;
           this.ipAddress = ipAddress;
           this.pwd = pwd;
           this.result = result;
           this.userName = userName;
    }


    /**
     * Gets the enableSvrCfg value for this GetDataConfServerConfigResponse.
     * 
     * @return enableSvrCfg
     */
    public int getEnableSvrCfg() {
        return enableSvrCfg;
    }


    /**
     * Sets the enableSvrCfg value for this GetDataConfServerConfigResponse.
     * 
     * @param enableSvrCfg
     */
    public void setEnableSvrCfg(int enableSvrCfg) {
        this.enableSvrCfg = enableSvrCfg;
    }


    /**
     * Gets the ipAddress value for this GetDataConfServerConfigResponse.
     * 
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the ipAddress value for this GetDataConfServerConfigResponse.
     * 
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * Gets the pwd value for this GetDataConfServerConfigResponse.
     * 
     * @return pwd
     */
    public String getPwd() {
        return pwd;
    }


    /**
     * Sets the pwd value for this GetDataConfServerConfigResponse.
     * 
     * @param pwd
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


    /**
     * Gets the result value for this GetDataConfServerConfigResponse.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetDataConfServerConfigResponse.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the userName value for this GetDataConfServerConfigResponse.
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this GetDataConfServerConfigResponse.
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetDataConfServerConfigResponse)) return false;
        GetDataConfServerConfigResponse other = (GetDataConfServerConfigResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.enableSvrCfg == other.getEnableSvrCfg() &&
            ((this.ipAddress==null && other.getIpAddress()==null) || 
             (this.ipAddress!=null &&
              this.ipAddress.equals(other.getIpAddress()))) &&
            ((this.pwd==null && other.getPwd()==null) || 
             (this.pwd!=null &&
              this.pwd.equals(other.getPwd()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
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
        _hashCode += getEnableSvrCfg();
        if (getIpAddress() != null) {
            _hashCode += getIpAddress().hashCode();
        }
        if (getPwd() != null) {
            _hashCode += getPwd().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetDataConfServerConfigResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetDataConfServerConfigResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableSvrCfg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enableSvrCfg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pwd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pwd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
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
