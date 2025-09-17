/**
 * GetMultiViewCfgResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetMultiViewCfgResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private int layout;

    private com.zte.m900.bean.MultiViewCfg[] multiViewCfg;

    private int multiViewGroupID;

    private int multiViewNum;

    public GetMultiViewCfgResponse() {
    }

    public GetMultiViewCfgResponse(
           String result,
           int layout,
           com.zte.m900.bean.MultiViewCfg[] multiViewCfg,
           int multiViewGroupID,
           int multiViewNum) {
        super(
            result);
        this.layout = layout;
        this.multiViewCfg = multiViewCfg;
        this.multiViewGroupID = multiViewGroupID;
        this.multiViewNum = multiViewNum;
    }


    /**
     * Gets the layout value for this GetMultiViewCfgResponse.
     * 
     * @return layout
     */
    public int getLayout() {
        return layout;
    }


    /**
     * Sets the layout value for this GetMultiViewCfgResponse.
     * 
     * @param layout
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }


    /**
     * Gets the multiViewCfg value for this GetMultiViewCfgResponse.
     * 
     * @return multiViewCfg
     */
    public com.zte.m900.bean.MultiViewCfg[] getMultiViewCfg() {
        return multiViewCfg;
    }


    /**
     * Sets the multiViewCfg value for this GetMultiViewCfgResponse.
     * 
     * @param multiViewCfg
     */
    public void setMultiViewCfg(com.zte.m900.bean.MultiViewCfg[] multiViewCfg) {
        this.multiViewCfg = multiViewCfg;
    }


    /**
     * Gets the multiViewGroupID value for this GetMultiViewCfgResponse.
     * 
     * @return multiViewGroupID
     */
    public int getMultiViewGroupID() {
        return multiViewGroupID;
    }


    /**
     * Sets the multiViewGroupID value for this GetMultiViewCfgResponse.
     * 
     * @param multiViewGroupID
     */
    public void setMultiViewGroupID(int multiViewGroupID) {
        this.multiViewGroupID = multiViewGroupID;
    }


    /**
     * Gets the multiViewNum value for this GetMultiViewCfgResponse.
     * 
     * @return multiViewNum
     */
    public int getMultiViewNum() {
        return multiViewNum;
    }


    /**
     * Sets the multiViewNum value for this GetMultiViewCfgResponse.
     * 
     * @param multiViewNum
     */
    public void setMultiViewNum(int multiViewNum) {
        this.multiViewNum = multiViewNum;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetMultiViewCfgResponse)) return false;
        GetMultiViewCfgResponse other = (GetMultiViewCfgResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.layout == other.getLayout() &&
            ((this.multiViewCfg==null && other.getMultiViewCfg()==null) || 
             (this.multiViewCfg!=null &&
              java.util.Arrays.equals(this.multiViewCfg, other.getMultiViewCfg()))) &&
            this.multiViewGroupID == other.getMultiViewGroupID() &&
            this.multiViewNum == other.getMultiViewNum();
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
        _hashCode += getLayout();
        if (getMultiViewCfg() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMultiViewCfg());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getMultiViewCfg(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getMultiViewGroupID();
        _hashCode += getMultiViewNum();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetMultiViewCfgResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetMultiViewCfgResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "layout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewCfg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewCfg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "MultiViewCfg"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewNum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewNum"));
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
