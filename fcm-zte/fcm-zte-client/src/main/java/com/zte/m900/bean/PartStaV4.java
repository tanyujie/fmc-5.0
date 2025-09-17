/**
 * PartStaV4.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class PartStaV4  implements java.io.Serializable {
    private int alr;

    private int camera;

    private String ext;

    private int handup;

    private String id;

    private int mic;

    private int mute;

    private String name;

    private String no;

    private int silent;

    private int state;

    public PartStaV4() {
    }

    public PartStaV4(
           int alr,
           int camera,
           String ext,
           int handup,
           String id,
           int mic,
           int mute,
           String name,
           String no,
           int silent,
           int state) {
           this.alr = alr;
           this.camera = camera;
           this.ext = ext;
           this.handup = handup;
           this.id = id;
           this.mic = mic;
           this.mute = mute;
           this.name = name;
           this.no = no;
           this.silent = silent;
           this.state = state;
    }


    /**
     * Gets the alr value for this PartStaV4.
     * 
     * @return alr
     */
    public int getAlr() {
        return alr;
    }


    /**
     * Sets the alr value for this PartStaV4.
     * 
     * @param alr
     */
    public void setAlr(int alr) {
        this.alr = alr;
    }


    /**
     * Gets the camera value for this PartStaV4.
     * 
     * @return camera
     */
    public int getCamera() {
        return camera;
    }


    /**
     * Sets the camera value for this PartStaV4.
     * 
     * @param camera
     */
    public void setCamera(int camera) {
        this.camera = camera;
    }


    /**
     * Gets the ext value for this PartStaV4.
     * 
     * @return ext
     */
    public String getExt() {
        return ext;
    }


    /**
     * Sets the ext value for this PartStaV4.
     * 
     * @param ext
     */
    public void setExt(String ext) {
        this.ext = ext;
    }


    /**
     * Gets the handup value for this PartStaV4.
     * 
     * @return handup
     */
    public int getHandup() {
        return handup;
    }


    /**
     * Sets the handup value for this PartStaV4.
     * 
     * @param handup
     */
    public void setHandup(int handup) {
        this.handup = handup;
    }


    /**
     * Gets the id value for this PartStaV4.
     * 
     * @return id
     */
    public String getId() {
        return id;
    }


    /**
     * Sets the id value for this PartStaV4.
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Gets the mic value for this PartStaV4.
     * 
     * @return mic
     */
    public int getMic() {
        return mic;
    }


    /**
     * Sets the mic value for this PartStaV4.
     * 
     * @param mic
     */
    public void setMic(int mic) {
        this.mic = mic;
    }


    /**
     * Gets the mute value for this PartStaV4.
     * 
     * @return mute
     */
    public int getMute() {
        return mute;
    }


    /**
     * Sets the mute value for this PartStaV4.
     * 
     * @param mute
     */
    public void setMute(int mute) {
        this.mute = mute;
    }


    /**
     * Gets the name value for this PartStaV4.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name value for this PartStaV4.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the no value for this PartStaV4.
     * 
     * @return no
     */
    public String getNo() {
        return no;
    }


    /**
     * Sets the no value for this PartStaV4.
     * 
     * @param no
     */
    public void setNo(String no) {
        this.no = no;
    }


    /**
     * Gets the silent value for this PartStaV4.
     * 
     * @return silent
     */
    public int getSilent() {
        return silent;
    }


    /**
     * Sets the silent value for this PartStaV4.
     * 
     * @param silent
     */
    public void setSilent(int silent) {
        this.silent = silent;
    }


    /**
     * Gets the state value for this PartStaV4.
     * 
     * @return state
     */
    public int getState() {
        return state;
    }


    /**
     * Sets the state value for this PartStaV4.
     * 
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof PartStaV4)) return false;
        PartStaV4 other = (PartStaV4) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.alr == other.getAlr() &&
            this.camera == other.getCamera() &&
            ((this.ext==null && other.getExt()==null) || 
             (this.ext!=null &&
              this.ext.equals(other.getExt()))) &&
            this.handup == other.getHandup() &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            this.mic == other.getMic() &&
            this.mute == other.getMute() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.no==null && other.getNo()==null) || 
             (this.no!=null &&
              this.no.equals(other.getNo()))) &&
            this.silent == other.getSilent() &&
            this.state == other.getState();
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
        _hashCode += getAlr();
        _hashCode += getCamera();
        if (getExt() != null) {
            _hashCode += getExt().hashCode();
        }
        _hashCode += getHandup();
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        _hashCode += getMic();
        _hashCode += getMute();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getNo() != null) {
            _hashCode += getNo().hashCode();
        }
        _hashCode += getSilent();
        _hashCode += getState();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PartStaV4.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PartStaV4"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alr");
        elemField.setXmlName(new javax.xml.namespace.QName("", "alr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("camera");
        elemField.setXmlName(new javax.xml.namespace.QName("", "camera"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ext"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("handup");
        elemField.setXmlName(new javax.xml.namespace.QName("", "handup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mic");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mute");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("no");
        elemField.setXmlName(new javax.xml.namespace.QName("", "no"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("silent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "silent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("", "state"));
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
