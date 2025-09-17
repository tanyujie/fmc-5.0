/**
 * SetMultiViewNumRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SetMultiViewNumRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private int layout;

    private int multiViewGroupID;

    private int multiViewNum;

    public SetMultiViewNumRequest() {
    }

    public SetMultiViewNumRequest(
           String conferenceIdentifier,
           int layout,
           int multiViewGroupID,
           int multiViewNum) {
        super(
            conferenceIdentifier);
        this.layout = layout;
        this.multiViewGroupID = multiViewGroupID;
        this.multiViewNum = multiViewNum;
    }


    /**
     * Gets the layout value for this SetMultiViewNumRequest.
     * 
     * @return layout
     */
    public int getLayout() {
        return layout;
    }


    /**
     * Sets the layout value for this SetMultiViewNumRequest.
     * 
     * @param layout
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }


    /**
     * Gets the multiViewGroupID value for this SetMultiViewNumRequest.
     * 
     * @return multiViewGroupID
     */
    public int getMultiViewGroupID() {
        return multiViewGroupID;
    }


    /**
     * Sets the multiViewGroupID value for this SetMultiViewNumRequest.
     * 
     * @param multiViewGroupID
     */
    public void setMultiViewGroupID(int multiViewGroupID) {
        this.multiViewGroupID = multiViewGroupID;
    }


    /**
     * Gets the multiViewNum value for this SetMultiViewNumRequest.
     * 
     * @return multiViewNum
     */
    public int getMultiViewNum() {
        return multiViewNum;
    }


    /**
     * Sets the multiViewNum value for this SetMultiViewNumRequest.
     * 
     * @param multiViewNum
     */
    public void setMultiViewNum(int multiViewNum) {
        this.multiViewNum = multiViewNum;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SetMultiViewNumRequest)) return false;
        SetMultiViewNumRequest other = (SetMultiViewNumRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.layout == other.getLayout() &&
            this.multiViewGroupID == other.getMultiViewGroupID() &&
            this.multiViewNum == other.getMultiViewNum();
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
        _hashCode += getLayout();
        _hashCode += getMultiViewGroupID();
        _hashCode += getMultiViewNum();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SetMultiViewNumRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SetMultiViewNumRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "layout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewNum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewNum"));
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
