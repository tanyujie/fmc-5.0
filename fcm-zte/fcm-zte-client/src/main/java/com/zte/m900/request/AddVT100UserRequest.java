/**
 * AddVT100UserRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class AddVT100UserRequest  implements java.io.Serializable {
    private com.zte.m900.bean.VT100UserInfo[] VT100UserList;

    public AddVT100UserRequest() {
    }

    public AddVT100UserRequest(
           com.zte.m900.bean.VT100UserInfo[] VT100UserList) {
           this.VT100UserList = VT100UserList;
    }


    /**
     * Gets the VT100UserList value for this AddVT100UserRequest.
     * 
     * @return VT100UserList
     */
    public com.zte.m900.bean.VT100UserInfo[] getVT100UserList() {
        return VT100UserList;
    }


    /**
     * Sets the VT100UserList value for this AddVT100UserRequest.
     * 
     * @param VT100UserList
     */
    public void setVT100UserList(com.zte.m900.bean.VT100UserInfo[] VT100UserList) {
        this.VT100UserList = VT100UserList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AddVT100UserRequest)) return false;
        AddVT100UserRequest other = (AddVT100UserRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.VT100UserList==null && other.getVT100UserList()==null) || 
             (this.VT100UserList!=null &&
              java.util.Arrays.equals(this.VT100UserList, other.getVT100UserList())));
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
        if (getVT100UserList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVT100UserList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVT100UserList(), i);
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
        new org.apache.axis.description.TypeDesc(AddVT100UserRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVT100UserRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VT100UserList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VT100UserList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VT100UserInfo"));
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
