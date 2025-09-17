/**
 * GetAddressBookResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetAddressBookResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.TerminalSimpleInfo[] terminalInfo;

    private int totalCount;

    public GetAddressBookResponse() {
    }

    public GetAddressBookResponse(
           String result,
           com.zte.m900.bean.TerminalSimpleInfo[] terminalInfo,
           int totalCount) {
        super(
            result);
        this.terminalInfo = terminalInfo;
        this.totalCount = totalCount;
    }


    /**
     * Gets the terminalInfo value for this GetAddressBookResponse.
     * 
     * @return terminalInfo
     */
    public com.zte.m900.bean.TerminalSimpleInfo[] getTerminalInfo() {
        return terminalInfo;
    }


    /**
     * Sets the terminalInfo value for this GetAddressBookResponse.
     * 
     * @param terminalInfo
     */
    public void setTerminalInfo(com.zte.m900.bean.TerminalSimpleInfo[] terminalInfo) {
        this.terminalInfo = terminalInfo;
    }


    /**
     * Gets the totalCount value for this GetAddressBookResponse.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetAddressBookResponse.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetAddressBookResponse)) return false;
        GetAddressBookResponse other = (GetAddressBookResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.terminalInfo==null && other.getTerminalInfo()==null) || 
             (this.terminalInfo!=null &&
              java.util.Arrays.equals(this.terminalInfo, other.getTerminalInfo()))) &&
            this.totalCount == other.getTotalCount();
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
        if (getTerminalInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTerminalInfo());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTerminalInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTotalCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetAddressBookResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetAddressBookResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalCount"));
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
