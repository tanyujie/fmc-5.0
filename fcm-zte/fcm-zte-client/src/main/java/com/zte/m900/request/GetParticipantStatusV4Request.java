/**
 * GetParticipantStatusV4Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetParticipantStatusV4Request  implements java.io.Serializable {
    private int confIdOpt;

    private String confIdentifier;

    private int numPerPage;

    private int page;

    private int terIdOpt;

    private String[] terIdentifier;

    public GetParticipantStatusV4Request() {
    }

    public GetParticipantStatusV4Request(
           int confIdOpt,
           String confIdentifier,
           int numPerPage,
           int page,
           int terIdOpt,
           String[] terIdentifier) {
           this.confIdOpt = confIdOpt;
           this.confIdentifier = confIdentifier;
           this.numPerPage = numPerPage;
           this.page = page;
           this.terIdOpt = terIdOpt;
           this.terIdentifier = terIdentifier;
    }


    /**
     * Gets the confIdOpt value for this GetParticipantStatusV4Request.
     * 
     * @return confIdOpt
     */
    public int getConfIdOpt() {
        return confIdOpt;
    }


    /**
     * Sets the confIdOpt value for this GetParticipantStatusV4Request.
     * 
     * @param confIdOpt
     */
    public void setConfIdOpt(int confIdOpt) {
        this.confIdOpt = confIdOpt;
    }


    /**
     * Gets the confIdentifier value for this GetParticipantStatusV4Request.
     * 
     * @return confIdentifier
     */
    public String getConfIdentifier() {
        return confIdentifier;
    }


    /**
     * Sets the confIdentifier value for this GetParticipantStatusV4Request.
     * 
     * @param confIdentifier
     */
    public void setConfIdentifier(String confIdentifier) {
        this.confIdentifier = confIdentifier;
    }


    /**
     * Gets the numPerPage value for this GetParticipantStatusV4Request.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetParticipantStatusV4Request.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the page value for this GetParticipantStatusV4Request.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetParticipantStatusV4Request.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the terIdOpt value for this GetParticipantStatusV4Request.
     * 
     * @return terIdOpt
     */
    public int getTerIdOpt() {
        return terIdOpt;
    }


    /**
     * Sets the terIdOpt value for this GetParticipantStatusV4Request.
     * 
     * @param terIdOpt
     */
    public void setTerIdOpt(int terIdOpt) {
        this.terIdOpt = terIdOpt;
    }


    /**
     * Gets the terIdentifier value for this GetParticipantStatusV4Request.
     * 
     * @return terIdentifier
     */
    public String[] getTerIdentifier() {
        return terIdentifier;
    }


    /**
     * Sets the terIdentifier value for this GetParticipantStatusV4Request.
     * 
     * @param terIdentifier
     */
    public void setTerIdentifier(String[] terIdentifier) {
        this.terIdentifier = terIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantStatusV4Request)) return false;
        GetParticipantStatusV4Request other = (GetParticipantStatusV4Request) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.confIdOpt == other.getConfIdOpt() &&
            ((this.confIdentifier==null && other.getConfIdentifier()==null) || 
             (this.confIdentifier!=null &&
              this.confIdentifier.equals(other.getConfIdentifier()))) &&
            this.numPerPage == other.getNumPerPage() &&
            this.page == other.getPage() &&
            this.terIdOpt == other.getTerIdOpt() &&
            ((this.terIdentifier==null && other.getTerIdentifier()==null) || 
             (this.terIdentifier!=null &&
              java.util.Arrays.equals(this.terIdentifier, other.getTerIdentifier())));
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
        _hashCode += getConfIdOpt();
        if (getConfIdentifier() != null) {
            _hashCode += getConfIdentifier().hashCode();
        }
        _hashCode += getNumPerPage();
        _hashCode += getPage();
        _hashCode += getTerIdOpt();
        if (getTerIdentifier() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTerIdentifier());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTerIdentifier(), i);
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
        new org.apache.axis.description.TypeDesc(GetParticipantStatusV4Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV4Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confIdOpt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confIdOpt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numPerPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numPerPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("page");
        elemField.setXmlName(new javax.xml.namespace.QName("", "page"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terIdOpt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terIdOpt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terIdentifier"));
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
