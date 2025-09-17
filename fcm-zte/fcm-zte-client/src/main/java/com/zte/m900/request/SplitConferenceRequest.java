/**
 * SplitConferenceRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SplitConferenceRequest  extends com.zte.m900.request.Request  implements java.io.Serializable {
    private String[] IDList;

    private String newConferenceName;

    private int type;

    public SplitConferenceRequest() {
    }

    public SplitConferenceRequest(
           String conferenceIdentifier,
           String[] IDList,
           String newConferenceName,
           int type) {
        super(
            conferenceIdentifier);
        this.IDList = IDList;
        this.newConferenceName = newConferenceName;
        this.type = type;
    }


    /**
     * Gets the IDList value for this SplitConferenceRequest.
     * 
     * @return IDList
     */
    public String[] getIDList() {
        return IDList;
    }


    /**
     * Sets the IDList value for this SplitConferenceRequest.
     * 
     * @param IDList
     */
    public void setIDList(String[] IDList) {
        this.IDList = IDList;
    }


    /**
     * Gets the newConferenceName value for this SplitConferenceRequest.
     * 
     * @return newConferenceName
     */
    public String getNewConferenceName() {
        return newConferenceName;
    }


    /**
     * Sets the newConferenceName value for this SplitConferenceRequest.
     * 
     * @param newConferenceName
     */
    public void setNewConferenceName(String newConferenceName) {
        this.newConferenceName = newConferenceName;
    }


    /**
     * Gets the type value for this SplitConferenceRequest.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets the type value for this SplitConferenceRequest.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SplitConferenceRequest)) return false;
        SplitConferenceRequest other = (SplitConferenceRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.IDList==null && other.getIDList()==null) || 
             (this.IDList!=null &&
              java.util.Arrays.equals(this.IDList, other.getIDList()))) &&
            ((this.newConferenceName==null && other.getNewConferenceName()==null) || 
             (this.newConferenceName!=null &&
              this.newConferenceName.equals(other.getNewConferenceName()))) &&
            this.type == other.getType();
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
        if (getIDList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getIDList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getIDList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNewConferenceName() != null) {
            _hashCode += getNewConferenceName().hashCode();
        }
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SplitConferenceRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SplitConferenceRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IDList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "IDList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newConferenceName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newConferenceName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
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
