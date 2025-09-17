/**
 * GetParticipantStatusV4Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.response;

public class GetParticipantStatusV4Response  implements java.io.Serializable {
    private com.zte.m900.bean.PartStaV4[] partStaV4;

    private String result;

    private int totalCount;

    public GetParticipantStatusV4Response() {
    }

    public GetParticipantStatusV4Response(
           com.zte.m900.bean.PartStaV4[] partStaV4,
           String result,
           int totalCount) {
           this.partStaV4 = partStaV4;
           this.result = result;
           this.totalCount = totalCount;
    }


    /**
     * Gets the partStaV4 value for this GetParticipantStatusV4Response.
     * 
     * @return partStaV4
     */
    public com.zte.m900.bean.PartStaV4[] getPartStaV4() {
        return partStaV4;
    }


    /**
     * Sets the partStaV4 value for this GetParticipantStatusV4Response.
     * 
     * @param partStaV4
     */
    public void setPartStaV4(com.zte.m900.bean.PartStaV4[] partStaV4) {
        this.partStaV4 = partStaV4;
    }


    /**
     * Gets the result value for this GetParticipantStatusV4Response.
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }


    /**
     * Sets the result value for this GetParticipantStatusV4Response.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }


    /**
     * Gets the totalCount value for this GetParticipantStatusV4Response.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this GetParticipantStatusV4Response.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantStatusV4Response)) return false;
        GetParticipantStatusV4Response other = (GetParticipantStatusV4Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.partStaV4==null && other.getPartStaV4()==null) || 
             (this.partStaV4!=null &&
              java.util.Arrays.equals(this.partStaV4, other.getPartStaV4()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
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
        int _hashCode = 1;
        if (getPartStaV4() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPartStaV4());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPartStaV4(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        _hashCode += getTotalCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetParticipantStatusV4Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV4Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partStaV4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "partStaV4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PartStaV4"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
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
