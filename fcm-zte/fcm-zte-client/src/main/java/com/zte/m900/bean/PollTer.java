/**
 * PollTer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class PollTer  implements java.io.Serializable {
    private int mediaChan;

    private String terminalId;

    private String terminalNumber;

    public PollTer() {
    }

    public PollTer(
           int mediaChan,
           String terminalId,
           String terminalNumber) {
           this.mediaChan = mediaChan;
           this.terminalId = terminalId;
           this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the mediaChan value for this PollTer.
     * 
     * @return mediaChan
     */
    public int getMediaChan() {
        return mediaChan;
    }


    /**
     * Sets the mediaChan value for this PollTer.
     * 
     * @param mediaChan
     */
    public void setMediaChan(int mediaChan) {
        this.mediaChan = mediaChan;
    }


    /**
     * Gets the terminalId value for this PollTer.
     * 
     * @return terminalId
     */
    public String getTerminalId() {
        return terminalId;
    }


    /**
     * Sets the terminalId value for this PollTer.
     * 
     * @param terminalId
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }


    /**
     * Gets the terminalNumber value for this PollTer.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this PollTer.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof PollTer)) return false;
        PollTer other = (PollTer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.mediaChan == other.getMediaChan() &&
            ((this.terminalId==null && other.getTerminalId()==null) || 
             (this.terminalId!=null &&
              this.terminalId.equals(other.getTerminalId()))) &&
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
        _hashCode += getMediaChan();
        if (getTerminalId() != null) {
            _hashCode += getTerminalId().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PollTer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaChan");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaChan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalId"));
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
