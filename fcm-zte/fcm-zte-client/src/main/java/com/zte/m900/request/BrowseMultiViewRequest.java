/**
 * BrowseMultiViewRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class BrowseMultiViewRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private int cmdType;

    private int multiViewGroupID;

    private String[] terminalIdentifier;

    private int timeInterval;

    private int viewNo;

    public BrowseMultiViewRequest() {
    }

    public BrowseMultiViewRequest(
           String conferenceIdentifier,
           int cmdType,
           int multiViewGroupID,
           String[] terminalIdentifier,
           int timeInterval,
           int viewNo) {
        super(
            conferenceIdentifier);
        this.cmdType = cmdType;
        this.multiViewGroupID = multiViewGroupID;
        this.terminalIdentifier = terminalIdentifier;
        this.timeInterval = timeInterval;
        this.viewNo = viewNo;
    }


    /**
     * Gets the cmdType value for this BrowseMultiViewRequest.
     * 
     * @return cmdType
     */
    public int getCmdType() {
        return cmdType;
    }


    /**
     * Sets the cmdType value for this BrowseMultiViewRequest.
     * 
     * @param cmdType
     */
    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }


    /**
     * Gets the multiViewGroupID value for this BrowseMultiViewRequest.
     * 
     * @return multiViewGroupID
     */
    public int getMultiViewGroupID() {
        return multiViewGroupID;
    }


    /**
     * Sets the multiViewGroupID value for this BrowseMultiViewRequest.
     * 
     * @param multiViewGroupID
     */
    public void setMultiViewGroupID(int multiViewGroupID) {
        this.multiViewGroupID = multiViewGroupID;
    }


    /**
     * Gets the terminalIdentifier value for this BrowseMultiViewRequest.
     * 
     * @return terminalIdentifier
     */
    public String[] getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this BrowseMultiViewRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String[] terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the timeInterval value for this BrowseMultiViewRequest.
     * 
     * @return timeInterval
     */
    public int getTimeInterval() {
        return timeInterval;
    }


    /**
     * Sets the timeInterval value for this BrowseMultiViewRequest.
     * 
     * @param timeInterval
     */
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }


    /**
     * Gets the viewNo value for this BrowseMultiViewRequest.
     * 
     * @return viewNo
     */
    public int getViewNo() {
        return viewNo;
    }


    /**
     * Sets the viewNo value for this BrowseMultiViewRequest.
     * 
     * @param viewNo
     */
    public void setViewNo(int viewNo) {
        this.viewNo = viewNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof BrowseMultiViewRequest)) return false;
        BrowseMultiViewRequest other = (BrowseMultiViewRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.cmdType == other.getCmdType() &&
            this.multiViewGroupID == other.getMultiViewGroupID() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              java.util.Arrays.equals(this.terminalIdentifier, other.getTerminalIdentifier()))) &&
            this.timeInterval == other.getTimeInterval() &&
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
        _hashCode += getCmdType();
        _hashCode += getMultiViewGroupID();
        if (getTerminalIdentifier() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTerminalIdentifier());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTerminalIdentifier(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTimeInterval();
        _hashCode += getViewNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BrowseMultiViewRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "BrowseMultiViewRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmdType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cmdType"));
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
        elemField.setFieldName("timeInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
