/**
 * GetVideoWallLayoutConfigResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetVideoWallLayoutConfigResponse  extends com.zte.m900.response.VideoWallResponse  implements java.io.Serializable {
    private int videoWallCount;

    private com.zte.m900.bean.VideoWallLayoutConfig[] videoWallLayoutConfigList;

    public GetVideoWallLayoutConfigResponse() {
    }

    public GetVideoWallLayoutConfigResponse(
           String result,
           int videoWallCount,
           com.zte.m900.bean.VideoWallLayoutConfig[] videoWallLayoutConfigList) {
        super(
            result);
        this.videoWallCount = videoWallCount;
        this.videoWallLayoutConfigList = videoWallLayoutConfigList;
    }


    /**
     * Gets the videoWallCount value for this GetVideoWallLayoutConfigResponse.
     * 
     * @return videoWallCount
     */
    public int getVideoWallCount() {
        return videoWallCount;
    }


    /**
     * Sets the videoWallCount value for this GetVideoWallLayoutConfigResponse.
     * 
     * @param videoWallCount
     */
    public void setVideoWallCount(int videoWallCount) {
        this.videoWallCount = videoWallCount;
    }


    /**
     * Gets the videoWallLayoutConfigList value for this GetVideoWallLayoutConfigResponse.
     * 
     * @return videoWallLayoutConfigList
     */
    public com.zte.m900.bean.VideoWallLayoutConfig[] getVideoWallLayoutConfigList() {
        return videoWallLayoutConfigList;
    }


    /**
     * Sets the videoWallLayoutConfigList value for this GetVideoWallLayoutConfigResponse.
     * 
     * @param videoWallLayoutConfigList
     */
    public void setVideoWallLayoutConfigList(com.zte.m900.bean.VideoWallLayoutConfig[] videoWallLayoutConfigList) {
        this.videoWallLayoutConfigList = videoWallLayoutConfigList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVideoWallLayoutConfigResponse)) return false;
        GetVideoWallLayoutConfigResponse other = (GetVideoWallLayoutConfigResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.videoWallCount == other.getVideoWallCount() &&
            ((this.videoWallLayoutConfigList==null && other.getVideoWallLayoutConfigList()==null) || 
             (this.videoWallLayoutConfigList!=null &&
              java.util.Arrays.equals(this.videoWallLayoutConfigList, other.getVideoWallLayoutConfigList())));
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
        _hashCode += getVideoWallCount();
        if (getVideoWallLayoutConfigList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVideoWallLayoutConfigList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVideoWallLayoutConfigList(), i);
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
        new org.apache.axis.description.TypeDesc(GetVideoWallLayoutConfigResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallLayoutConfigResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallLayoutConfigList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallLayoutConfigList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallLayoutConfig"));
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
