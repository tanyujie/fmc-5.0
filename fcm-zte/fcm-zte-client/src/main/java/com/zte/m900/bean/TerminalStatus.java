/**
 * TerminalStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class TerminalStatus  implements java.io.Serializable {
    private int dualVideoState;

    private int isInConf;

    private String terId;

    private String terminalName;

    private String terminalNumber;

    public TerminalStatus() {
    }

    public TerminalStatus(
           int dualVideoState,
           int isInConf,
           String terId,
           String terminalName,
           String terminalNumber) {
           this.dualVideoState = dualVideoState;
           this.isInConf = isInConf;
           this.terId = terId;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the dualVideoState value for this TerminalStatus.
     * 
     * @return dualVideoState
     */
    public int getDualVideoState() {
        return dualVideoState;
    }


    /**
     * Sets the dualVideoState value for this TerminalStatus.
     * 
     * @param dualVideoState
     */
    public void setDualVideoState(int dualVideoState) {
        this.dualVideoState = dualVideoState;
    }


    /**
     * Gets the isInConf value for this TerminalStatus.
     * 
     * @return isInConf
     */
    public int getIsInConf() {
        return isInConf;
    }


    /**
     * Sets the isInConf value for this TerminalStatus.
     * 
     * @param isInConf
     */
    public void setIsInConf(int isInConf) {
        this.isInConf = isInConf;
    }


    /**
     * Gets the terId value for this TerminalStatus.
     * 
     * @return terId
     */
    public String getTerId() {
        return terId;
    }


    /**
     * Sets the terId value for this TerminalStatus.
     * 
     * @param terId
     */
    public void setTerId(String terId) {
        this.terId = terId;
    }


    /**
     * Gets the terminalName value for this TerminalStatus.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this TerminalStatus.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this TerminalStatus.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this TerminalStatus.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof TerminalStatus)) return false;
        TerminalStatus other = (TerminalStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.dualVideoState == other.getDualVideoState() &&
            this.isInConf == other.getIsInConf() &&
            ((this.terId==null && other.getTerId()==null) || 
             (this.terId!=null &&
              this.terId.equals(other.getTerId()))) &&
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
        _hashCode += getDualVideoState();
        _hashCode += getIsInConf();
        if (getTerId() != null) {
            _hashCode += getTerId().hashCode();
        }
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
        new org.apache.axis.description.TypeDesc(TerminalStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dualVideoState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dualVideoState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isInConf");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isInConf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
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
