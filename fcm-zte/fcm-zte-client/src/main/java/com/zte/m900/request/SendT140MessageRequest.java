/**
 * SendT140MessageRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SendT140MessageRequest  implements java.io.Serializable {
    private int BGColor;

    private String conferenceIdentifier;

    private String content;

    private int fontColor;

    private int fontSize;

    private int includeSiteName;

    private String[] terminalIdentifier;

    private int type;

    public SendT140MessageRequest() {
    }

    public SendT140MessageRequest(
           int BGColor,
           String conferenceIdentifier,
           String content,
           int fontColor,
           int fontSize,
           int includeSiteName,
           String[] terminalIdentifier,
           int type) {
           this.BGColor = BGColor;
           this.conferenceIdentifier = conferenceIdentifier;
           this.content = content;
           this.fontColor = fontColor;
           this.fontSize = fontSize;
           this.includeSiteName = includeSiteName;
           this.terminalIdentifier = terminalIdentifier;
           this.type = type;
    }


    /**
     * Gets the BGColor value for this SendT140MessageRequest.
     * 
     * @return BGColor
     */
    public int getBGColor() {
        return BGColor;
    }


    /**
     * Sets the BGColor value for this SendT140MessageRequest.
     * 
     * @param BGColor
     */
    public void setBGColor(int BGColor) {
        this.BGColor = BGColor;
    }


    /**
     * Gets the conferenceIdentifier value for this SendT140MessageRequest.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this SendT140MessageRequest.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the content value for this SendT140MessageRequest.
     * 
     * @return content
     */
    public String getContent() {
        return content;
    }


    /**
     * Sets the content value for this SendT140MessageRequest.
     * 
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * Gets the fontColor value for this SendT140MessageRequest.
     * 
     * @return fontColor
     */
    public int getFontColor() {
        return fontColor;
    }


    /**
     * Sets the fontColor value for this SendT140MessageRequest.
     * 
     * @param fontColor
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }


    /**
     * Gets the fontSize value for this SendT140MessageRequest.
     * 
     * @return fontSize
     */
    public int getFontSize() {
        return fontSize;
    }


    /**
     * Sets the fontSize value for this SendT140MessageRequest.
     * 
     * @param fontSize
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    /**
     * Gets the includeSiteName value for this SendT140MessageRequest.
     * 
     * @return includeSiteName
     */
    public int getIncludeSiteName() {
        return includeSiteName;
    }


    /**
     * Sets the includeSiteName value for this SendT140MessageRequest.
     * 
     * @param includeSiteName
     */
    public void setIncludeSiteName(int includeSiteName) {
        this.includeSiteName = includeSiteName;
    }


    /**
     * Gets the terminalIdentifier value for this SendT140MessageRequest.
     * 
     * @return terminalIdentifier
     */
    public String[] getTerminalIdentifier() {
        return terminalIdentifier;
    }


    /**
     * Sets the terminalIdentifier value for this SendT140MessageRequest.
     * 
     * @param terminalIdentifier
     */
    public void setTerminalIdentifier(String[] terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }


    /**
     * Gets the type value for this SendT140MessageRequest.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets the type value for this SendT140MessageRequest.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SendT140MessageRequest)) return false;
        SendT140MessageRequest other = (SendT140MessageRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.BGColor == other.getBGColor() &&
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            ((this.content==null && other.getContent()==null) || 
             (this.content!=null &&
              this.content.equals(other.getContent()))) &&
            this.fontColor == other.getFontColor() &&
            this.fontSize == other.getFontSize() &&
            this.includeSiteName == other.getIncludeSiteName() &&
            ((this.terminalIdentifier==null && other.getTerminalIdentifier()==null) || 
             (this.terminalIdentifier!=null &&
              java.util.Arrays.equals(this.terminalIdentifier, other.getTerminalIdentifier()))) &&
            this.type == other.getType();
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
        _hashCode += getBGColor();
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        if (getContent() != null) {
            _hashCode += getContent().hashCode();
        }
        _hashCode += getFontColor();
        _hashCode += getFontSize();
        _hashCode += getIncludeSiteName();
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
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SendT140MessageRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SendT140MessageRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("BGColor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "BGColor"));
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
        elemField.setFieldName("content");
        elemField.setXmlName(new javax.xml.namespace.QName("", "content"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fontColor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fontColor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fontSize");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fontSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeSiteName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includeSiteName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
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
