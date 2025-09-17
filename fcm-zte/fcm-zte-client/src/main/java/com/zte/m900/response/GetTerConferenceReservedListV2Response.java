/**
 * GetTerConferenceReservedListV2Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetTerConferenceReservedListV2Response  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.ConferenceInfoV2[] conferenceList;

    private int totalCount;

    public GetTerConferenceReservedListV2Response() {
    }

    public GetTerConferenceReservedListV2Response(
           String result,
           com.zte.m900.bean.ConferenceInfoV2[] conferenceList,
           int totalCount) {
        super(
            result);
        this.conferenceList = conferenceList;
        this.totalCount = totalCount;
    }


    /**
     * Gets the conferenceList value for this GetTerConferenceReservedListV2Response.
     * 
     * @return conferenceList
     */
    public com.zte.m900.bean.ConferenceInfoV2[] getConferenceList() {
        return conferenceList;
    }


    /**
     * Sets the conferenceList value for this GetTerConferenceReservedListV2Response.
     * 
     * @param conferenceList
     */
    public void setConferenceList(com.zte.m900.bean.ConferenceInfoV2[] conferenceList) {
        this.conferenceList = conferenceList;
    }


    /**
     * Gets the totalCount value for this GetTerConferenceReservedListV2Response.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetTerConferenceReservedListV2Response.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetTerConferenceReservedListV2Response)) return false;
        GetTerConferenceReservedListV2Response other = (GetTerConferenceReservedListV2Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.conferenceList==null && other.getConferenceList()==null) || 
             (this.conferenceList!=null &&
              java.util.Arrays.equals(this.conferenceList, other.getConferenceList()))) &&
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
        if (getConferenceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConferenceList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getConferenceList(), i);
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
        new org.apache.axis.description.TypeDesc(GetTerConferenceReservedListV2Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceReservedListV2Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfoV2"));
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
