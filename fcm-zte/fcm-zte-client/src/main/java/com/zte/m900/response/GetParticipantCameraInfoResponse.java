/**
 * GetParticipantCameraInfoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetParticipantCameraInfoResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private com.zte.m900.bean.CameraInfo[] cameraList;

    private int cameraNo;

    public GetParticipantCameraInfoResponse() {
    }

    public GetParticipantCameraInfoResponse(
           String result,
           com.zte.m900.bean.CameraInfo[] cameraList,
           int cameraNo) {
        super(
            result);
        this.cameraList = cameraList;
        this.cameraNo = cameraNo;
    }


    /**
     * Gets the cameraList value for this GetParticipantCameraInfoResponse.
     * 
     * @return cameraList
     */
    public com.zte.m900.bean.CameraInfo[] getCameraList() {
        return cameraList;
    }


    /**
     * Sets the cameraList value for this GetParticipantCameraInfoResponse.
     * 
     * @param cameraList
     */
    public void setCameraList(com.zte.m900.bean.CameraInfo[] cameraList) {
        this.cameraList = cameraList;
    }


    /**
     * Gets the cameraNo value for this GetParticipantCameraInfoResponse.
     * 
     * @return cameraNo
     */
    public int getCameraNo() {
        return cameraNo;
    }


    /**
     * Sets the cameraNo value for this GetParticipantCameraInfoResponse.
     * 
     * @param cameraNo
     */
    public void setCameraNo(int cameraNo) {
        this.cameraNo = cameraNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantCameraInfoResponse)) return false;
        GetParticipantCameraInfoResponse other = (GetParticipantCameraInfoResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.cameraList==null && other.getCameraList()==null) || 
             (this.cameraList!=null &&
              java.util.Arrays.equals(this.cameraList, other.getCameraList()))) &&
            this.cameraNo == other.getCameraNo();
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
        if (getCameraList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCameraList());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getCameraList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getCameraNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetParticipantCameraInfoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantCameraInfoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cameraList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cameraList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "CameraInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cameraNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cameraNo"));
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
