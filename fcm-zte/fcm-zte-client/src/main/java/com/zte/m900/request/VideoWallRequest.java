/**
 * VideoWallRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class VideoWallRequest  implements java.io.Serializable {
    private String MS90Address;

    private int audioState;

    private int cmdType;

    private int ctrlType;

    private int mediaChan;

    private int monitorType;

    private int picNo;

    private com.zte.m900.bean.PollTer[] pollTers;

    private int terCount;

    private String terminalId;

    private String terminalName;

    private String terminalNumber;

    private int timeInterval;

    private int videoWallNo;

    public VideoWallRequest() {
    }

    public VideoWallRequest(
           String MS90Address,
           int audioState,
           int cmdType,
           int ctrlType,
           int mediaChan,
           int monitorType,
           int picNo,
           com.zte.m900.bean.PollTer[] pollTers,
           int terCount,
           String terminalId,
           String terminalName,
           String terminalNumber,
           int timeInterval,
           int videoWallNo) {
           this.MS90Address = MS90Address;
           this.audioState = audioState;
           this.cmdType = cmdType;
           this.ctrlType = ctrlType;
           this.mediaChan = mediaChan;
           this.monitorType = monitorType;
           this.picNo = picNo;
           this.pollTers = pollTers;
           this.terCount = terCount;
           this.terminalId = terminalId;
           this.terminalName = terminalName;
           this.terminalNumber = terminalNumber;
           this.timeInterval = timeInterval;
           this.videoWallNo = videoWallNo;
    }


    /**
     * Gets the MS90Address value for this VideoWallRequest.
     * 
     * @return MS90Address
     */
    public String getMS90Address() {
        return MS90Address;
    }


    /**
     * Sets the MS90Address value for this VideoWallRequest.
     * 
     * @param MS90Address
     */
    public void setMS90Address(String MS90Address) {
        this.MS90Address = MS90Address;
    }


    /**
     * Gets the audioState value for this VideoWallRequest.
     * 
     * @return audioState
     */
    public int getAudioState() {
        return audioState;
    }


    /**
     * Sets the audioState value for this VideoWallRequest.
     * 
     * @param audioState
     */
    public void setAudioState(int audioState) {
        this.audioState = audioState;
    }


    /**
     * Gets the cmdType value for this VideoWallRequest.
     * 
     * @return cmdType
     */
    public int getCmdType() {
        return cmdType;
    }


    /**
     * Sets the cmdType value for this VideoWallRequest.
     * 
     * @param cmdType
     */
    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }


    /**
     * Gets the ctrlType value for this VideoWallRequest.
     * 
     * @return ctrlType
     */
    public int getCtrlType() {
        return ctrlType;
    }


    /**
     * Sets the ctrlType value for this VideoWallRequest.
     * 
     * @param ctrlType
     */
    public void setCtrlType(int ctrlType) {
        this.ctrlType = ctrlType;
    }


    /**
     * Gets the mediaChan value for this VideoWallRequest.
     * 
     * @return mediaChan
     */
    public int getMediaChan() {
        return mediaChan;
    }


    /**
     * Sets the mediaChan value for this VideoWallRequest.
     * 
     * @param mediaChan
     */
    public void setMediaChan(int mediaChan) {
        this.mediaChan = mediaChan;
    }


    /**
     * Gets the monitorType value for this VideoWallRequest.
     * 
     * @return monitorType
     */
    public int getMonitorType() {
        return monitorType;
    }


    /**
     * Sets the monitorType value for this VideoWallRequest.
     * 
     * @param monitorType
     */
    public void setMonitorType(int monitorType) {
        this.monitorType = monitorType;
    }


    /**
     * Gets the picNo value for this VideoWallRequest.
     * 
     * @return picNo
     */
    public int getPicNo() {
        return picNo;
    }


    /**
     * Sets the picNo value for this VideoWallRequest.
     * 
     * @param picNo
     */
    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }


    /**
     * Gets the pollTers value for this VideoWallRequest.
     * 
     * @return pollTers
     */
    public com.zte.m900.bean.PollTer[] getPollTers() {
        return pollTers;
    }


    /**
     * Sets the pollTers value for this VideoWallRequest.
     * 
     * @param pollTers
     */
    public void setPollTers(com.zte.m900.bean.PollTer[] pollTers) {
        this.pollTers = pollTers;
    }


    /**
     * Gets the terCount value for this VideoWallRequest.
     * 
     * @return terCount
     */
    public int getTerCount() {
        return terCount;
    }


    /**
     * Sets the terCount value for this VideoWallRequest.
     * 
     * @param terCount
     */
    public void setTerCount(int terCount) {
        this.terCount = terCount;
    }


    /**
     * Gets the terminalId value for this VideoWallRequest.
     * 
     * @return terminalId
     */
    public String getTerminalId() {
        return terminalId;
    }


    /**
     * Sets the terminalId value for this VideoWallRequest.
     * 
     * @param terminalId
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }


    /**
     * Gets the terminalName value for this VideoWallRequest.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this VideoWallRequest.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalNumber value for this VideoWallRequest.
     * 
     * @return terminalNumber
     */
    public String getTerminalNumber() {
        return terminalNumber;
    }


    /**
     * Sets the terminalNumber value for this VideoWallRequest.
     * 
     * @param terminalNumber
     */
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }


    /**
     * Gets the timeInterval value for this VideoWallRequest.
     * 
     * @return timeInterval
     */
    public int getTimeInterval() {
        return timeInterval;
    }


    /**
     * Sets the timeInterval value for this VideoWallRequest.
     * 
     * @param timeInterval
     */
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }


    /**
     * Gets the videoWallNo value for this VideoWallRequest.
     * 
     * @return videoWallNo
     */
    public int getVideoWallNo() {
        return videoWallNo;
    }


    /**
     * Sets the videoWallNo value for this VideoWallRequest.
     * 
     * @param videoWallNo
     */
    public void setVideoWallNo(int videoWallNo) {
        this.videoWallNo = videoWallNo;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof VideoWallRequest)) return false;
        VideoWallRequest other = (VideoWallRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.MS90Address==null && other.getMS90Address()==null) || 
             (this.MS90Address!=null &&
              this.MS90Address.equals(other.getMS90Address()))) &&
            this.audioState == other.getAudioState() &&
            this.cmdType == other.getCmdType() &&
            this.ctrlType == other.getCtrlType() &&
            this.mediaChan == other.getMediaChan() &&
            this.monitorType == other.getMonitorType() &&
            this.picNo == other.getPicNo() &&
            ((this.pollTers==null && other.getPollTers()==null) || 
             (this.pollTers!=null &&
              java.util.Arrays.equals(this.pollTers, other.getPollTers()))) &&
            this.terCount == other.getTerCount() &&
            ((this.terminalId==null && other.getTerminalId()==null) || 
             (this.terminalId!=null &&
              this.terminalId.equals(other.getTerminalId()))) &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.terminalNumber==null && other.getTerminalNumber()==null) || 
             (this.terminalNumber!=null &&
              this.terminalNumber.equals(other.getTerminalNumber()))) &&
            this.timeInterval == other.getTimeInterval() &&
            this.videoWallNo == other.getVideoWallNo();
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
        if (getMS90Address() != null) {
            _hashCode += getMS90Address().hashCode();
        }
        _hashCode += getAudioState();
        _hashCode += getCmdType();
        _hashCode += getCtrlType();
        _hashCode += getMediaChan();
        _hashCode += getMonitorType();
        _hashCode += getPicNo();
        if (getPollTers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPollTers());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getPollTers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getTerCount();
        if (getTerminalId() != null) {
            _hashCode += getTerminalId().hashCode();
        }
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getTerminalNumber() != null) {
            _hashCode += getTerminalNumber().hashCode();
        }
        _hashCode += getTimeInterval();
        _hashCode += getVideoWallNo();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VideoWallRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MS90Address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MS90Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audioState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "audioState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmdType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cmdType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ctrlType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ctrlType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaChan");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaChan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monitorType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "monitorType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("picNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "picNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pollTers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pollTers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("videoWallNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "videoWallNo"));
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
