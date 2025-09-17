/**
 * GetParticipantStatusV3Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class GetParticipantStatusV3Request  implements java.io.Serializable {
    private int conferenceIdOption;

    private String conferenceIdentifier;

    private int numPerPage;

    private int page;

    private int terminalIdOption;

    private String[] terminalIdentifier;

    public GetParticipantStatusV3Request() {
    }

    public GetParticipantStatusV3Request(
           int conferenceIdOption,
           String conferenceIdentifier,
           int numPerPage,
           int page,
           int terminalIdOption,
           String[] terminalIdentifier) {
           this.conferenceIdOption = conferenceIdOption;
           this.conferenceIdentifier = conferenceIdentifier;
           this.numPerPage = numPerPage;
           this.page = page;
           this.terminalIdOption = terminalIdOption;
           this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the conferenceIdOption value for this GetParticipantStatusV3Request.
     * 
     * @return conferenceIdOption
     */
    public int getConferenceIdOption() {
        return conferenceIdOption;
    }


    /**
     * Sets the conferenceIdOption value for this GetParticipantStatusV3Request.
     * 
     * @param conferenceIdOption
     */
    public void setConferenceIdOption(int conferenceIdOption) {
        this.conferenceIdOption = conferenceIdOption;
    }


    /**
     * Gets the conferenceIdentifier value for this GetParticipantStatusV3Request.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this GetParticipantStatusV3Request.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the numPerPage value for this GetParticipantStatusV3Request.
     * 
     * @return numPerPage
     */
    public int getNumPerPage() {
        return numPerPage;
    }


    /**
     * Sets the numPerPage value for this GetParticipantStatusV3Request.
     * 
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }


    /**
     * Gets the page value for this GetParticipantStatusV3Request.
     * 
     * @return page
     */
    public int getPage() {
        return page;
    }


    /**
     * Sets the page value for this GetParticipantStatusV3Request.
     * 
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Gets the terminalIdOption value for this GetParticipantStatusV3Request.
     * 
     * @return terminalIdOption
     */
    public int getTerminalIdOption() {
        return terminalIdOption;
    }


    /**
     * Sets the terminalIdOption value for this GetParticipantStatusV3Request.
     * 
     * @param terminalIdOption
     */
    public void setTerminalIdOption(int terminalIdOption) {
        this.terminalIdOption = terminalIdOption;
    }


    /**
     * Gets the terminalIdentifier value for this GetParticipantStatusV3Request.
     * 
     * @return terminalIdentifier
     */
    public String[] getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this GetParticipantStatusV3Request.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String[] terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetParticipantStatusV3Request)) return false;
        GetParticipantStatusV3Request other = (GetParticipantStatusV3Request) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.conferenceIdOption == other.getConferenceIdOption() &&
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            this.numPerPage == other.getNumPerPage() &&
            this.page == other.getPage() &&
            this.terminalIdOption == other.getTerminalIdOption() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              java.util.Arrays.equals(this.terminalIdentifier, other.getTerminalIdentifier())));
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
        _hashCode += getConferenceIdOption();
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        _hashCode += getNumPerPage();
        _hashCode += getPage();
        _hashCode += getTerminalIdOption();
        if (getTerminalIdentifier() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTerminalIdentifier());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTerminalIdentifier(), i);
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
        new org.apache.axis.description.TypeDesc(GetParticipantStatusV3Request.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV3Request"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdOption");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
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
        elemField.setFieldName("terminalIdOption");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
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
