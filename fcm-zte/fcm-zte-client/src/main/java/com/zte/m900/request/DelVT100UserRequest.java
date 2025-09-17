/**
 * DelVT100UserRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class DelVT100UserRequest  implements java.io.Serializable {
    private String[] VT100UserID;

    public DelVT100UserRequest() {
    }

    public DelVT100UserRequest(
           String[] VT100UserID) {
           this.VT100UserID = VT100UserID;
    }


    /**
     * Gets the VT100UserID value for this DelVT100UserRequest.
     * 
     * @return VT100UserID
     */
    public String[] getVT100UserID() {
        return VT100UserID;
    }


    /**
     * Sets the VT100UserID value for this DelVT100UserRequest.
     * 
     * @param VT100UserID
     */
    public void setVT100UserID(String[] VT100UserID) {
        this.VT100UserID = VT100UserID;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof DelVT100UserRequest)) return false;
        DelVT100UserRequest other = (DelVT100UserRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.VT100UserID==null && other.getVT100UserID()==null) || 
             (this.VT100UserID!=null &&
              java.util.Arrays.equals(this.VT100UserID, other.getVT100UserID())));
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
        if (getVT100UserID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVT100UserID());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVT100UserID(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DelVT100UserRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "DelVT100UserRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VT100UserID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VT100UserID"));
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
