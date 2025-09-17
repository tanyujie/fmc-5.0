/**
 * SetVideoWallStateListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SetVideoWallStateListRequest  implements java.io.Serializable {
    private com.zte.m900.bean.VideoWallState[] videoWallStateList;

    public SetVideoWallStateListRequest() {
    }

    public SetVideoWallStateListRequest(
           com.zte.m900.bean.VideoWallState[] videoWallStateList) {
           this.videoWallStateList = videoWallStateList;
    }


    /**
     * Gets the videoWallStateList value for this SetVideoWallStateListRequest.
     * 
     * @return videoWallStateList
     */
    public com.zte.m900.bean.VideoWallState[] getVideoWallStateList() {
        return videoWallStateList;
    }


    /**
     * Sets the videoWallStateList value for this SetVideoWallStateListRequest.
     * 
     * @param videoWallStateList
     */
    public void setVideoWallStateList(com.zte.m900.bean.VideoWallState[] videoWallStateList) {
        this.videoWallStateList = videoWallStateList;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SetVideoWallStateListRequest)) return false;
        SetVideoWallStateListRequest other = (SetVideoWallStateListRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.videoWallStateList==null && other.getVideoWallStateList()==null) || 
             (this.videoWallStateList!=null &&
              java.util.Arrays.equals(this.videoWallStateList, other.getVideoWallStateList())));
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
        if (getVideoWallStateList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVideoWallStateList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getVideoWallStateList(), i);
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
        new org.apache.axis.description.TypeDesc(SetVideoWallStateListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SetVideoWallStateListRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallStateList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallStateList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallState"));
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
