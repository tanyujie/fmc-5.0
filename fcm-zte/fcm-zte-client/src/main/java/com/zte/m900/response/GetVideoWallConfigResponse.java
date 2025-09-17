/**
 * GetVideoWallConfigResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetVideoWallConfigResponse  extends com.zte.m900.response.VideoWallResponse  implements java.io.Serializable {
    private int videlWallCellCount;

    private com.zte.m900.bean.VideoWallCellConfig[] videoWallCellConfigList;

    public GetVideoWallConfigResponse() {
    }

    public GetVideoWallConfigResponse(
           String result,
           int videlWallCellCount,
           com.zte.m900.bean.VideoWallCellConfig[] videoWallCellConfigList) {
        super(
            result);
        this.videlWallCellCount = videlWallCellCount;
        this.videoWallCellConfigList = videoWallCellConfigList;
    }


    /**
     * Gets the videlWallCellCount value for this GetVideoWallConfigResponse.
     * 
     * @return videlWallCellCount
     */
    public int getVidelWallCellCount() {
        return videlWallCellCount;
    }


    /**
     * Sets the videlWallCellCount value for this GetVideoWallConfigResponse.
     * 
     * @param videlWallCellCount
     */
    public void setVidelWallCellCount(int videlWallCellCount) {
        this.videlWallCellCount = videlWallCellCount;
    }


    /**
     * Gets the videoWallCellConfigList value for this GetVideoWallConfigResponse.
     * 
     * @return videoWallCellConfigList
     */
    public com.zte.m900.bean.VideoWallCellConfig[] getVideoWallCellConfigList() {
        return videoWallCellConfigList;
    }


    /**
     * Sets the videoWallCellConfigList value for this GetVideoWallConfigResponse.
     * 
     * @param videoWallCellConfigList
     */
    public void setVideoWallCellConfigList(com.zte.m900.bean.VideoWallCellConfig[] videoWallCellConfigList) {
        this.videoWallCellConfigList = videoWallCellConfigList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetVideoWallConfigResponse)) return false;
        GetVideoWallConfigResponse other = (GetVideoWallConfigResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.videlWallCellCount == other.getVidelWallCellCount() &&
            ((this.videoWallCellConfigList==null && other.getVideoWallCellConfigList()==null) || 
             (this.videoWallCellConfigList!=null &&
              java.util.Arrays.equals(this.videoWallCellConfigList, other.getVideoWallCellConfigList())));
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
        _hashCode += getVidelWallCellCount();
        if (getVideoWallCellConfigList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVideoWallCellConfigList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVideoWallCellConfigList(), i);
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
        new org.apache.axis.description.TypeDesc(GetVideoWallConfigResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallConfigResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videlWallCellCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videlWallCellCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallCellConfigList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallCellConfigList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallCellConfig"));
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
