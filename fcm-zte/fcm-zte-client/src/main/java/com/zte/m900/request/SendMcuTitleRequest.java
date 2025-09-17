/**
 * SendMcuTitleRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class SendMcuTitleRequest  implements java.io.Serializable {
    private int BGColor;

    private String conferenceIdentifier;

    private String content;

    private int fontColor;

    private int fontSize;

    private int includeSiteName;
    /**
     * 显示位置， 0：居中
     * 1：左上 2：中上 3：
     * 右上 4：左下 5：中下
     * 6：右下。该字段只对
     * 字幕有效
     */
    private int position;

    private int rollSpeed;

    private int scrollMode;

    private int sendMode;

    private int type;

    public SendMcuTitleRequest() {
    }

    public SendMcuTitleRequest(
           int BGColor,
           String conferenceIdentifier,
           String content,
           int fontColor,
           int fontSize,
           int includeSiteName,
           int position,
           int rollSpeed,
           int scrollMode,
           int sendMode,
           int type) {
           this.BGColor = BGColor;
           this.conferenceIdentifier = conferenceIdentifier;
           this.content = content;
           this.fontColor = fontColor;
           this.fontSize = fontSize;
           this.includeSiteName = includeSiteName;
           this.position = position;
           this.rollSpeed = rollSpeed;
           this.scrollMode = scrollMode;
           this.sendMode = sendMode;
           this.type = type;
    }


    /**
     * Gets the BGColor value for this SendMcuTitleRequest.
     * 
     * @return BGColor
     */
    public int getBGColor() {
        return BGColor;
    }


    /**
     * Sets the BGColor value for this SendMcuTitleRequest.
     * 
     * @param BGColor
     */
    public void setBGColor(int BGColor) {
        this.BGColor = BGColor;
    }


    /**
     * Gets the conferenceIdentifier value for this SendMcuTitleRequest.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this SendMcuTitleRequest.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the content value for this SendMcuTitleRequest.
     * 
     * @return content
     */
    public String getContent() {
        return content;
    }


    /**
     * Sets the content value for this SendMcuTitleRequest.
     * 
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * Gets the fontColor value for this SendMcuTitleRequest.
     * 
     * @return fontColor
     */
    public int getFontColor() {
        return fontColor;
    }


    /**
     * Sets the fontColor value for this SendMcuTitleRequest.
     * 
     * @param fontColor
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }


    /**
     * Gets the fontSize value for this SendMcuTitleRequest.
     * 
     * @return fontSize
     */
    public int getFontSize() {
        return fontSize;
    }


    /**
     * Sets the fontSize value for this SendMcuTitleRequest.
     * 
     * @param fontSize
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    /**
     * Gets the includeSiteName value for this SendMcuTitleRequest.
     * 
     * @return includeSiteName
     */
    public int getIncludeSiteName() {
        return includeSiteName;
    }


    /**
     * Sets the includeSiteName value for this SendMcuTitleRequest.
     * 
     * @param includeSiteName
     */
    public void setIncludeSiteName(int includeSiteName) {
        this.includeSiteName = includeSiteName;
    }


    /**
     * Gets the position value for this SendMcuTitleRequest.
     * 
     * @return position
     */
    public int getPosition() {
        return position;
    }


    /**
     * Sets the position value for this SendMcuTitleRequest.
     * 
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }


    /**
     * Gets the rollSpeed value for this SendMcuTitleRequest.
     * 
     * @return rollSpeed
     */
    public int getRollSpeed() {
        return rollSpeed;
    }


    /**
     * Sets the rollSpeed value for this SendMcuTitleRequest.
     * 
     * @param rollSpeed
     */
    public void setRollSpeed(int rollSpeed) {
        this.rollSpeed = rollSpeed;
    }


    /**
     * Gets the scrollMode value for this SendMcuTitleRequest.
     * 
     * @return scrollMode
     */
    public int getScrollMode() {
        return scrollMode;
    }


    /**
     * Sets the scrollMode value for this SendMcuTitleRequest.
     * 
     * @param scrollMode
     */
    public void setScrollMode(int scrollMode) {
        this.scrollMode = scrollMode;
    }


    /**
     * Gets the sendMode value for this SendMcuTitleRequest.
     * 
     * @return sendMode
     */
    public int getSendMode() {
        return sendMode;
    }


    /**
     * Sets the sendMode value for this SendMcuTitleRequest.
     * 
     * @param sendMode
     */
    public void setSendMode(int sendMode) {
        this.sendMode = sendMode;
    }


    /**
     * Gets the type value for this SendMcuTitleRequest.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets the type value for this SendMcuTitleRequest.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SendMcuTitleRequest)) return false;
        SendMcuTitleRequest other = (SendMcuTitleRequest) obj;
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
            this.position == other.getPosition() &&
            this.rollSpeed == other.getRollSpeed() &&
            this.scrollMode == other.getScrollMode() &&
            this.sendMode == other.getSendMode() &&
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
        _hashCode += getPosition();
        _hashCode += getRollSpeed();
        _hashCode += getScrollMode();
        _hashCode += getSendMode();
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SendMcuTitleRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "SendMcuTitleRequest"));
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
        elemField.setFieldName("position");
        elemField.setXmlName(new javax.xml.namespace.QName("", "position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rollSpeed");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rollSpeed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scrollMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scrollMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sendMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sendMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
