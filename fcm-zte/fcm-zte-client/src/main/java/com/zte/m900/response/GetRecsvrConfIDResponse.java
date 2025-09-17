/**
 * GetRecsvrConfIDResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetRecsvrConfIDResponse  extends com.zte.m900.response.ConferenceResponse  implements java.io.Serializable {
    private String recsvrConfID;

    public GetRecsvrConfIDResponse() {
    }

    public GetRecsvrConfIDResponse(
           String result,
           String recsvrConfID) {
        super(
            result);
        this.recsvrConfID = recsvrConfID;
    }


    /**
     * Gets the recsvrConfID value for this GetRecsvrConfIDResponse.
     * 
     * @return recsvrConfID
     */
    public String getRecsvrConfID() {
        return recsvrConfID;
    }


    /**
     * Sets the recsvrConfID value for this GetRecsvrConfIDResponse.
     * 
     * @param recsvrConfID
     */
    public void setRecsvrConfID(String recsvrConfID) {
        this.recsvrConfID = recsvrConfID;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRecsvrConfIDResponse)) return false;
        GetRecsvrConfIDResponse other = (GetRecsvrConfIDResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.recsvrConfID==null && other.getRecsvrConfID()==null) || 
             (this.recsvrConfID!=null &&
              this.recsvrConfID.equals(other.getRecsvrConfID())));
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
        if (getRecsvrConfID() != null) {
            _hashCode += getRecsvrConfID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRecsvrConfIDResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecsvrConfIDResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recsvrConfID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recsvrConfID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
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
