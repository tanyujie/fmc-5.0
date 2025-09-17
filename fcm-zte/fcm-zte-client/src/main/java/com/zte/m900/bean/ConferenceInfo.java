/**
 * ConferenceInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ConferenceInfo  implements java.io.Serializable {
    private String confCreator;

    private com.zte.m900.bean.ConferenceSimpleInfo confSimpleInfo;

    private int terminalCount;

    public ConferenceInfo() {
    }

    public ConferenceInfo(
           String confCreator,
           com.zte.m900.bean.ConferenceSimpleInfo confSimpleInfo,
           int terminalCount) {
           this.confCreator = confCreator;
           this.confSimpleInfo = confSimpleInfo;
           this.terminalCount = terminalCount;
    }


    /**
     * Gets the confCreator value for this ConferenceInfo.
     * 
     * @return confCreator
     */
    public String getConfCreator() {
        return confCreator;
    }


    /**
     * Sets the confCreator value for this ConferenceInfo.
     * 
     * @param confCreator
     */
    public void setConfCreator(String confCreator) {
        this.confCreator = confCreator;
    }


    /**
     * Gets the confSimpleInfo value for this ConferenceInfo.
     * 
     * @return confSimpleInfo
     */
    public com.zte.m900.bean.ConferenceSimpleInfo getConfSimpleInfo() {
        return confSimpleInfo;
    }


    /**
     * Sets the confSimpleInfo value for this ConferenceInfo.
     * 
     * @param confSimpleInfo
     */
    public void setConfSimpleInfo(com.zte.m900.bean.ConferenceSimpleInfo confSimpleInfo) {
        this.confSimpleInfo = confSimpleInfo;
    }


    /**
     * Gets the terminalCount value for this ConferenceInfo.
     * 
     * @return terminalCount
     */
    public int getTerminalCount() {
        return terminalCount;
    }


    /**
     * Sets the terminalCount value for this ConferenceInfo.
     * 
     * @param terminalCount
     */
    public void setTerminalCount(int terminalCount) {
        this.terminalCount = terminalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConferenceInfo)) return false;
        ConferenceInfo other = (ConferenceInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.confCreator==null && other.getConfCreator()==null) || 
             (this.confCreator!=null &&
              this.confCreator.equals(other.getConfCreator()))) &&
            ((this.confSimpleInfo==null && other.getConfSimpleInfo()==null) || 
             (this.confSimpleInfo!=null &&
              this.confSimpleInfo.equals(other.getConfSimpleInfo()))) &&
            this.terminalCount == other.getTerminalCount();
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
        if (getConfCreator() != null) {
            _hashCode += getConfCreator().hashCode();
        }
        if (getConfSimpleInfo() != null) {
            _hashCode += getConfSimpleInfo().hashCode();
        }
        _hashCode += getTerminalCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConferenceInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confCreator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confCreator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confSimpleInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confSimpleInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceSimpleInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
