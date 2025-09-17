/**
 * MultiViewSelectRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class MultiViewSelectRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private int mediaType;

    private int multiViewGroupID;

    private String terminalIdentifier;

    private int viewNo;

    public MultiViewSelectRequest() {
    }

    public MultiViewSelectRequest(
           String conferenceIdentifier,
           int mediaType,
           int multiViewGroupID,
           String terminalIdentifier,
           int viewNo) {
        super(
            conferenceIdentifier);
        this.mediaType = mediaType;
        this.multiViewGroupID = multiViewGroupID;
        this.terminalIdentifier = terminalIdentifier;
        this.viewNo = viewNo;
    }


    /**
     * Gets the mediaType value for this MultiViewSelectRequest.
     * 
     * @return mediaType
     */
    public int getMediaType() {
        return mediaType;
    }


    /**
     * Sets the mediaType value for this MultiViewSelectRequest.
     * 
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    /**
     * Gets the multiViewGroupID value for this MultiViewSelectRequest.
     * 
     * @return multiViewGroupID
     */
    public int getMultiViewGroupID() {
        return multiViewGroupID;
    }


    /**
     * Sets the multiViewGroupID value for this MultiViewSelectRequest.
     * 
     * @param multiViewGroupID
     */
    public void setMultiViewGroupID(int multiViewGroupID) {
        this.multiViewGroupID = multiViewGroupID;
    }


    /**
     * Gets the terminalIdentifier value for this MultiViewSelectRequest.
     * 
     * @return terminalIdentifier
     */
    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this MultiViewSelectRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the viewNo value for this MultiViewSelectRequest.
     * 
     * @return viewNo
     */
    public int getViewNo() {
        return viewNo;
    }


    /**
     * Sets the viewNo value for this MultiViewSelectRequest.
     * 
     * @param viewNo
     */
    public void setViewNo(int viewNo) {
        this.viewNo = viewNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MultiViewSelectRequest)) return false;
        MultiViewSelectRequest other = (MultiViewSelectRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.mediaType == other.getMediaType() &&
            this.multiViewGroupID == other.getMultiViewGroupID() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              this.terminalIdentifier.equals(other.getTerminalIdentifier()))) &&
            this.viewNo == other.getViewNo();
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
        _hashCode += getMediaType();
        _hashCode += getMultiViewGroupID();
        if (getTerminalIdentifier() != null) {
            _hashCode += getTerminalIdentifier().hashCode();
        }
        _hashCode += getViewNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MultiViewSelectRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "MultiViewSelectRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaType"));
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
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("viewNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "viewNo"));
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
