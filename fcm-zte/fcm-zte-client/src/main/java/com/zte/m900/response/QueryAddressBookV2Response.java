/**
 * QueryAddressBookV2Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class QueryAddressBookV2Response  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.TerminalSimpleInfoV2[] terminalInfoV2;

    private int totalCount;

    public QueryAddressBookV2Response() {
    }

    public QueryAddressBookV2Response(
           String result,
           com.zte.m900.bean.TerminalSimpleInfoV2[] terminalInfoV2,
           int totalCount) {
        super(
            result);
        this.terminalInfoV2 = terminalInfoV2;
        this.totalCount = totalCount;
    }


    /**
     * Gets the terminalInfoV2 value for this QueryAddressBookV2Response.
     * 
     * @return terminalInfoV2
     */
    public com.zte.m900.bean.TerminalSimpleInfoV2[] getTerminalInfoV2() {
        return terminalInfoV2;
    }


    /**
     * Sets the terminalInfoV2 value for this QueryAddressBookV2Response.
     * 
     * @param terminalInfoV2
     */
    public void setTerminalInfoV2(com.zte.m900.bean.TerminalSimpleInfoV2[] terminalInfoV2) {
        this.terminalInfoV2 = terminalInfoV2;
    }


    /**
     * Gets the totalCount value for this QueryAddressBookV2Response.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this QueryAddressBookV2Response.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof QueryAddressBookV2Response)) return false;
        QueryAddressBookV2Response other = (QueryAddressBookV2Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.terminalInfoV2==null && other.getTerminalInfoV2()==null) || 
             (this.terminalInfoV2!=null &&
              java.util.Arrays.equals(this.terminalInfoV2, other.getTerminalInfoV2()))) &&
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
        if (getTerminalInfoV2() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTerminalInfoV2());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTerminalInfoV2(), i);
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
        new org.apache.axis.description.TypeDesc(QueryAddressBookV2Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryAddressBookV2Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalInfoV2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalInfoV2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfoV2"));
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
