/**
 * GetVideoWallPollConfigResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetVideoWallPollConfigResponse  extends com.zte.m900.response.VideoWallResponse  implements java.io.Serializable {
    private com.zte.m900.bean.PollTer[] pollTers;

    private int terCount;

    public GetVideoWallPollConfigResponse() {
    }

    public GetVideoWallPollConfigResponse(
           String result,
           com.zte.m900.bean.PollTer[] pollTers,
           int terCount) {
        super(
            result);
        this.pollTers = pollTers;
        this.terCount = terCount;
    }


    /**
     * Gets the pollTers value for this GetVideoWallPollConfigResponse.
     * 
     * @return pollTers
     */
    public com.zte.m900.bean.PollTer[] getPollTers() {
        return pollTers;
    }


    /**
     * Sets the pollTers value for this GetVideoWallPollConfigResponse.
     * 
     * @param pollTers
     */
    public void setPollTers(com.zte.m900.bean.PollTer[] pollTers) {
        this.pollTers = pollTers;
    }


    /**
     * Gets the terCount value for this GetVideoWallPollConfigResponse.
     * 
     * @return terCount
     */
    public int getTerCount() {
        return terCount;
    }


    /**
     * Sets the terCount value for this GetVideoWallPollConfigResponse.
     * 
     * @param terCount
     */
    public void setTerCount(int terCount) {
        this.terCount = terCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVideoWallPollConfigResponse)) return false;
        GetVideoWallPollConfigResponse other = (GetVideoWallPollConfigResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.pollTers==null && other.getPollTers()==null) || 
             (this.pollTers!=null &&
              java.util.Arrays.equals(this.pollTers, other.getPollTers()))) &&
            this.terCount == other.getTerCount();
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
        if (getPollTers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPollTers());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPollTers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTerCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetVideoWallPollConfigResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallPollConfigResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pollTers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pollTers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terCount"));
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
