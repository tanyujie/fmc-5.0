/**
 * AddVideoWallLayoutConfigRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class AddVideoWallLayoutConfigRequest  implements java.io.Serializable {
    private com.zte.m900.bean.VideoWallLayoutConfig videoWallLayoutConfigList;

    public AddVideoWallLayoutConfigRequest() {
    }

    public AddVideoWallLayoutConfigRequest(
           com.zte.m900.bean.VideoWallLayoutConfig videoWallLayoutConfigList) {
           this.videoWallLayoutConfigList = videoWallLayoutConfigList;
    }


    /**
     * Gets the videoWallLayoutConfigList value for this AddVideoWallLayoutConfigRequest.
     * 
     * @return videoWallLayoutConfigList
     */
    public com.zte.m900.bean.VideoWallLayoutConfig getVideoWallLayoutConfigList() {
        return videoWallLayoutConfigList;
    }


    /**
     * Sets the videoWallLayoutConfigList value for this AddVideoWallLayoutConfigRequest.
     * 
     * @param videoWallLayoutConfigList
     */
    public void setVideoWallLayoutConfigList(com.zte.m900.bean.VideoWallLayoutConfig videoWallLayoutConfigList) {
        this.videoWallLayoutConfigList = videoWallLayoutConfigList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AddVideoWallLayoutConfigRequest)) return false;
        AddVideoWallLayoutConfigRequest other = (AddVideoWallLayoutConfigRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.videoWallLayoutConfigList==null && other.getVideoWallLayoutConfigList()==null) || 
             (this.videoWallLayoutConfigList!=null &&
              this.videoWallLayoutConfigList.equals(other.getVideoWallLayoutConfigList())));
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
        if (getVideoWallLayoutConfigList() != null) {
            _hashCode += getVideoWallLayoutConfigList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddVideoWallLayoutConfigRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVideoWallLayoutConfigRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
