/**
 * ConferenceTemplet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ConferenceTemplet  implements java.io.Serializable {
    private com.zte.m900.bean.AudioAndVideoCapability[] audioAndVideoCapability;

    private String autoCall;

    private String boardcasterView;

    private String capConfigType;

    private com.zte.m900.bean.DataCapability[] dataCapability;

    private com.zte.m900.bean.VideoCapability[] dualVideoCapability;

    private int rate;

    private int templetId;

    private String templetName;

    public ConferenceTemplet() {
    }

    public ConferenceTemplet(
           com.zte.m900.bean.AudioAndVideoCapability[] audioAndVideoCapability,
           String autoCall,
           String boardcasterView,
           String capConfigType,
           com.zte.m900.bean.DataCapability[] dataCapability,
           com.zte.m900.bean.VideoCapability[] dualVideoCapability,
           int rate,
           int templetId,
           String templetName) {
           this.audioAndVideoCapability = audioAndVideoCapability;
           this.autoCall = autoCall;
           this.boardcasterView = boardcasterView;
           this.capConfigType = capConfigType;
           this.dataCapability = dataCapability;
           this.dualVideoCapability = dualVideoCapability;
           this.rate = rate;
           this.templetId = templetId;
           this.templetName = templetName;
    }


    /**
     * Gets the audioAndVideoCapability value for this ConferenceTemplet.
     * 
     * @return audioAndVideoCapability
     */
    public com.zte.m900.bean.AudioAndVideoCapability[] getAudioAndVideoCapability() {
        return audioAndVideoCapability;
    }


    /**
     * Sets the audioAndVideoCapability value for this ConferenceTemplet.
     * 
     * @param audioAndVideoCapability
     */
    public void setAudioAndVideoCapability(com.zte.m900.bean.AudioAndVideoCapability[] audioAndVideoCapability) {
        this.audioAndVideoCapability = audioAndVideoCapability;
    }


    /**
     * Gets the autoCall value for this ConferenceTemplet.
     * 
     * @return autoCall
     */
    public String getAutoCall() {
        return autoCall;
    }


    /**
     * Sets the autoCall value for this ConferenceTemplet.
     * 
     * @param autoCall
     */
    public void setAutoCall(String autoCall) {
        this.autoCall = autoCall;
    }


    /**
     * Gets the boardcasterView value for this ConferenceTemplet.
     * 
     * @return boardcasterView
     */
    public String getBoardcasterView() {
        return boardcasterView;
    }


    /**
     * Sets the boardcasterView value for this ConferenceTemplet.
     * 
     * @param boardcasterView
     */
    public void setBoardcasterView(String boardcasterView) {
        this.boardcasterView = boardcasterView;
    }


    /**
     * Gets the capConfigType value for this ConferenceTemplet.
     * 
     * @return capConfigType
     */
    public String getCapConfigType() {
        return capConfigType;
    }


    /**
     * Sets the capConfigType value for this ConferenceTemplet.
     * 
     * @param capConfigType
     */
    public void setCapConfigType(String capConfigType) {
        this.capConfigType = capConfigType;
    }


    /**
     * Gets the dataCapability value for this ConferenceTemplet.
     * 
     * @return dataCapability
     */
    public com.zte.m900.bean.DataCapability[] getDataCapability() {
        return dataCapability;
    }


    /**
     * Sets the dataCapability value for this ConferenceTemplet.
     * 
     * @param dataCapability
     */
    public void setDataCapability(com.zte.m900.bean.DataCapability[] dataCapability) {
        this.dataCapability = dataCapability;
    }


    /**
     * Gets the dualVideoCapability value for this ConferenceTemplet.
     * 
     * @return dualVideoCapability
     */
    public com.zte.m900.bean.VideoCapability[] getDualVideoCapability() {
        return dualVideoCapability;
    }


    /**
     * Sets the dualVideoCapability value for this ConferenceTemplet.
     * 
     * @param dualVideoCapability
     */
    public void setDualVideoCapability(com.zte.m900.bean.VideoCapability[] dualVideoCapability) {
        this.dualVideoCapability = dualVideoCapability;
    }


    /**
     * Gets the rate value for this ConferenceTemplet.
     * 
     * @return rate
     */
    public int getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this ConferenceTemplet.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }


    /**
     * Gets the templetId value for this ConferenceTemplet.
     * 
     * @return templetId
     */
    public int getTempletId() {
        return templetId;
    }


    /**
     * Sets the templetId value for this ConferenceTemplet.
     * 
     * @param templetId
     */
    public void setTempletId(int templetId) {
        this.templetId = templetId;
    }


    /**
     * Gets the templetName value for this ConferenceTemplet.
     * 
     * @return templetName
     */
    public String getTempletName() {
        return templetName;
    }


    /**
     * Sets the templetName value for this ConferenceTemplet.
     * 
     * @param templetName
     */
    public void setTempletName(String templetName) {
        this.templetName = templetName;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConferenceTemplet)) return false;
        ConferenceTemplet other = (ConferenceTemplet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.audioAndVideoCapability==null && other.getAudioAndVideoCapability()==null) || 
             (this.audioAndVideoCapability!=null &&
              java.util.Arrays.equals(this.audioAndVideoCapability, other.getAudioAndVideoCapability()))) &&
            ((this.autoCall==null && other.getAutoCall()==null) || 
             (this.autoCall!=null &&
              this.autoCall.equals(other.getAutoCall()))) &&
            ((this.boardcasterView==null && other.getBoardcasterView()==null) || 
             (this.boardcasterView!=null &&
              this.boardcasterView.equals(other.getBoardcasterView()))) &&
            ((this.capConfigType==null && other.getCapConfigType()==null) || 
             (this.capConfigType!=null &&
              this.capConfigType.equals(other.getCapConfigType()))) &&
            ((this.dataCapability==null && other.getDataCapability()==null) || 
             (this.dataCapability!=null &&
              java.util.Arrays.equals(this.dataCapability, other.getDataCapability()))) &&
            ((this.dualVideoCapability==null && other.getDualVideoCapability()==null) || 
             (this.dualVideoCapability!=null &&
              java.util.Arrays.equals(this.dualVideoCapability, other.getDualVideoCapability()))) &&
            this.rate == other.getRate() &&
            this.templetId == other.getTempletId() &&
            ((this.templetName==null && other.getTempletName()==null) || 
             (this.templetName!=null &&
              this.templetName.equals(other.getTempletName())));
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
        if (getAudioAndVideoCapability() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAudioAndVideoCapability());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getAudioAndVideoCapability(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAutoCall() != null) {
            _hashCode += getAutoCall().hashCode();
        }
        if (getBoardcasterView() != null) {
            _hashCode += getBoardcasterView().hashCode();
        }
        if (getCapConfigType() != null) {
            _hashCode += getCapConfigType().hashCode();
        }
        if (getDataCapability() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDataCapability());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getDataCapability(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDualVideoCapability() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDualVideoCapability());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getDualVideoCapability(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getRate();
        _hashCode += getTempletId();
        if (getTempletName() != null) {
            _hashCode += getTempletName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConferenceTemplet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceTemplet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioAndVideoCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioAndVideoCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioAndVideoCapability"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autoCall");
        elemField.setXmlName(new javax.xml.namespace.QName("", "autoCall"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("boardcasterView");
        elemField.setXmlName(new javax.xml.namespace.QName("", "boardcasterView"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capConfigType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "capConfigType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "DataCapability"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dualVideoCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dualVideoCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoCapability"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("templetId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "templetId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("templetName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "templetName"));
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
