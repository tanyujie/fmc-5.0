/**
 * GetRegionListResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetRegionListResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private int counts;

    private com.zte.m900.bean.RegionInfo[] regionList;

    public GetRegionListResponse() {
    }

    public GetRegionListResponse(
           String result,
           int counts,
           com.zte.m900.bean.RegionInfo[] regionList) {
        super(
            result);
        this.counts = counts;
        this.regionList = regionList;
    }


    /**
     * Gets the counts value for this GetRegionListResponse.
     * 
     * @return counts
     */
    public int getCounts() {
        return counts;
    }


    /**
     * Sets the counts value for this GetRegionListResponse.
     * 
     * @param counts
     */
    public void setCounts(int counts) {
        this.counts = counts;
    }


    /**
     * Gets the regionList value for this GetRegionListResponse.
     * 
     * @return regionList
     */
    public com.zte.m900.bean.RegionInfo[] getRegionList() {
        return regionList;
    }


    /**
     * Sets the regionList value for this GetRegionListResponse.
     * 
     * @param regionList
     */
    public void setRegionList(com.zte.m900.bean.RegionInfo[] regionList) {
        this.regionList = regionList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRegionListResponse)) return false;
        GetRegionListResponse other = (GetRegionListResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.counts == other.getCounts() &&
            ((this.regionList==null && other.getRegionList()==null) || 
             (this.regionList!=null &&
              java.util.Arrays.equals(this.regionList, other.getRegionList())));
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
        _hashCode += getCounts();
        if (getRegionList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegionList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getRegionList(), i);
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
        new org.apache.axis.description.TypeDesc(GetRegionListResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRegionListResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("counts");
        elemField.setXmlName(new javax.xml.namespace.QName("", "counts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "RegionInfo"));
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
