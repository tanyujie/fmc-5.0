/**
 * ConferenceStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class ConferenceStatus  implements java.io.Serializable {
    private String boardcaster;

    private String chairman;

    private String confCtrlMode;

    private String conferenceIdentifier;

    private String conferenceName;

    private String conferenceNumber;

    private String conferencePassword;

    private int curPicNum;

    private String dualBoardcaster;

    private int duration;

    private boolean ifRecord;

    private String ifUpMode;

    private boolean lockState;

    private int maxPicNum;

    private String multiPicCtrlMode;

    private String recordState;

    private java.util.Calendar startTime;

    public ConferenceStatus() {
    }

    public ConferenceStatus(
           String boardcaster,
           String chairman,
           String confCtrlMode,
           String conferenceIdentifier,
           String conferenceName,
           String conferenceNumber,
           String conferencePassword,
           int curPicNum,
           String dualBoardcaster,
           int duration,
           boolean ifRecord,
           String ifUpMode,
           boolean lockState,
           int maxPicNum,
           String multiPicCtrlMode,
           String recordState,
           java.util.Calendar startTime) {
           this.boardcaster = boardcaster;
           this.chairman = chairman;
           this.confCtrlMode = confCtrlMode;
           this.conferenceIdentifier = conferenceIdentifier;
           this.conferenceName = conferenceName;
           this.conferenceNumber = conferenceNumber;
           this.conferencePassword = conferencePassword;
           this.curPicNum = curPicNum;
           this.dualBoardcaster = dualBoardcaster;
           this.duration = duration;
           this.ifRecord = ifRecord;
           this.ifUpMode = ifUpMode;
           this.lockState = lockState;
           this.maxPicNum = maxPicNum;
           this.multiPicCtrlMode = multiPicCtrlMode;
           this.recordState = recordState;
           this.startTime = startTime;
    }


    /**
     * Gets the boardcaster value for this ConferenceStatus.
     * 
     * @return boardcaster
     */
    public String getBoardcaster() {
        return boardcaster;
    }


    /**
     * Sets the boardcaster value for this ConferenceStatus.
     * 
     * @param boardcaster
     */
    public void setBoardcaster(String boardcaster) {
        this.boardcaster = boardcaster;
    }


    /**
     * Gets the chairman value for this ConferenceStatus.
     * 
     * @return chairman
     */
    public String getChairman() {
        return chairman;
    }


    /**
     * Sets the chairman value for this ConferenceStatus.
     * 
     * @param chairman
     */
    public void setChairman(String chairman) {
        this.chairman = chairman;
    }


    /**
     * Gets the confCtrlMode value for this ConferenceStatus.
     * 
     * @return confCtrlMode
     */
    public String getConfCtrlMode() {
        return confCtrlMode;
    }


    /**
     * Sets the confCtrlMode value for this ConferenceStatus.
     * 
     * @param confCtrlMode
     */
    public void setConfCtrlMode(String confCtrlMode) {
        this.confCtrlMode = confCtrlMode;
    }


    /**
     * Gets the conferenceIdentifier value for this ConferenceStatus.
     * 
     * @return conferenceIdentifier
     */
    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }


    /**
     * Sets the conferenceIdentifier value for this ConferenceStatus.
     * 
     * @param conferenceIdentifier
     */
    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }


    /**
     * Gets the conferenceName value for this ConferenceStatus.
     * 
     * @return conferenceName
     */
    public String getConferenceName() {
        return conferenceName;
    }


    /**
     * Sets the conferenceName value for this ConferenceStatus.
     * 
     * @param conferenceName
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }


    /**
     * Gets the conferenceNumber value for this ConferenceStatus.
     * 
     * @return conferenceNumber
     */
    public String getConferenceNumber() {
        return conferenceNumber;
    }


    /**
     * Sets the conferenceNumber value for this ConferenceStatus.
     * 
     * @param conferenceNumber
     */
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }


    /**
     * Gets the conferencePassword value for this ConferenceStatus.
     * 
     * @return conferencePassword
     */
    public String getConferencePassword() {
        return conferencePassword;
    }


    /**
     * Sets the conferencePassword value for this ConferenceStatus.
     * 
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }


    /**
     * Gets the curPicNum value for this ConferenceStatus.
     * 
     * @return curPicNum
     */
    public int getCurPicNum() {
        return curPicNum;
    }


    /**
     * Sets the curPicNum value for this ConferenceStatus.
     * 
     * @param curPicNum
     */
    public void setCurPicNum(int curPicNum) {
        this.curPicNum = curPicNum;
    }


    /**
     * Gets the dualBoardcaster value for this ConferenceStatus.
     * 
     * @return dualBoardcaster
     */
    public String getDualBoardcaster() {
        return dualBoardcaster;
    }


    /**
     * Sets the dualBoardcaster value for this ConferenceStatus.
     * 
     * @param dualBoardcaster
     */
    public void setDualBoardcaster(String dualBoardcaster) {
        this.dualBoardcaster = dualBoardcaster;
    }


    /**
     * Gets the duration value for this ConferenceStatus.
     * 
     * @return duration
     */
    public int getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this ConferenceStatus.
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }


    /**
     * Gets the ifRecord value for this ConferenceStatus.
     * 
     * @return ifRecord
     */
    public boolean isIfRecord() {
        return ifRecord;
    }


    /**
     * Sets the ifRecord value for this ConferenceStatus.
     * 
     * @param ifRecord
     */
    public void setIfRecord(boolean ifRecord) {
        this.ifRecord = ifRecord;
    }


    /**
     * Gets the ifUpMode value for this ConferenceStatus.
     * 
     * @return ifUpMode
     */
    public String getIfUpMode() {
        return ifUpMode;
    }


    /**
     * Sets the ifUpMode value for this ConferenceStatus.
     * 
     * @param ifUpMode
     */
    public void setIfUpMode(String ifUpMode) {
        this.ifUpMode = ifUpMode;
    }


    /**
     * Gets the lockState value for this ConferenceStatus.
     * 
     * @return lockState
     */
    public boolean isLockState() {
        return lockState;
    }


    /**
     * Sets the lockState value for this ConferenceStatus.
     * 
     * @param lockState
     */
    public void setLockState(boolean lockState) {
        this.lockState = lockState;
    }


    /**
     * Gets the maxPicNum value for this ConferenceStatus.
     * 
     * @return maxPicNum
     */
    public int getMaxPicNum() {
        return maxPicNum;
    }


    /**
     * Sets the maxPicNum value for this ConferenceStatus.
     * 
     * @param maxPicNum
     */
    public void setMaxPicNum(int maxPicNum) {
        this.maxPicNum = maxPicNum;
    }


    /**
     * Gets the multiPicCtrlMode value for this ConferenceStatus.
     * 
     * @return multiPicCtrlMode
     */
    public String getMultiPicCtrlMode() {
        return multiPicCtrlMode;
    }


    /**
     * Sets the multiPicCtrlMode value for this ConferenceStatus.
     * 
     * @param multiPicCtrlMode
     */
    public void setMultiPicCtrlMode(String multiPicCtrlMode) {
        this.multiPicCtrlMode = multiPicCtrlMode;
    }


    /**
     * Gets the recordState value for this ConferenceStatus.
     * 
     * @return recordState
     */
    public String getRecordState() {
        return recordState;
    }


    /**
     * Sets the recordState value for this ConferenceStatus.
     * 
     * @param recordState
     */
    public void setRecordState(String recordState) {
        this.recordState = recordState;
    }


    /**
     * Gets the startTime value for this ConferenceStatus.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this ConferenceStatus.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConferenceStatus)) return false;
        ConferenceStatus other = (ConferenceStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.boardcaster==null && other.getBoardcaster()==null) || 
             (this.boardcaster!=null &&
              this.boardcaster.equals(other.getBoardcaster()))) &&
            ((this.chairman==null && other.getChairman()==null) || 
             (this.chairman!=null &&
              this.chairman.equals(other.getChairman()))) &&
            ((this.confCtrlMode==null && other.getConfCtrlMode()==null) || 
             (this.confCtrlMode!=null &&
              this.confCtrlMode.equals(other.getConfCtrlMode()))) &&
            ((this.conferenceIdentifier==null && other.getConferenceIdentifier()==null) || 
             (this.conferenceIdentifier!=null &&
              this.conferenceIdentifier.equals(other.getConferenceIdentifier()))) &&
            ((this.conferenceName==null && other.getConferenceName()==null) || 
             (this.conferenceName!=null &&
              this.conferenceName.equals(other.getConferenceName()))) &&
            ((this.conferenceNumber==null && other.getConferenceNumber()==null) || 
             (this.conferenceNumber!=null &&
              this.conferenceNumber.equals(other.getConferenceNumber()))) &&
            ((this.conferencePassword==null && other.getConferencePassword()==null) || 
             (this.conferencePassword!=null &&
              this.conferencePassword.equals(other.getConferencePassword()))) &&
            this.curPicNum == other.getCurPicNum() &&
            ((this.dualBoardcaster==null && other.getDualBoardcaster()==null) || 
             (this.dualBoardcaster!=null &&
              this.dualBoardcaster.equals(other.getDualBoardcaster()))) &&
            this.duration == other.getDuration() &&
            this.ifRecord == other.isIfRecord() &&
            ((this.ifUpMode==null && other.getIfUpMode()==null) || 
             (this.ifUpMode!=null &&
              this.ifUpMode.equals(other.getIfUpMode()))) &&
            this.lockState == other.isLockState() &&
            this.maxPicNum == other.getMaxPicNum() &&
            ((this.multiPicCtrlMode==null && other.getMultiPicCtrlMode()==null) || 
             (this.multiPicCtrlMode!=null &&
              this.multiPicCtrlMode.equals(other.getMultiPicCtrlMode()))) &&
            ((this.recordState==null && other.getRecordState()==null) || 
             (this.recordState!=null &&
              this.recordState.equals(other.getRecordState()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime())));
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
        if (getBoardcaster() != null) {
            _hashCode += getBoardcaster().hashCode();
        }
        if (getChairman() != null) {
            _hashCode += getChairman().hashCode();
        }
        if (getConfCtrlMode() != null) {
            _hashCode += getConfCtrlMode().hashCode();
        }
        if (getConferenceIdentifier() != null) {
            _hashCode += getConferenceIdentifier().hashCode();
        }
        if (getConferenceName() != null) {
            _hashCode += getConferenceName().hashCode();
        }
        if (getConferenceNumber() != null) {
            _hashCode += getConferenceNumber().hashCode();
        }
        if (getConferencePassword() != null) {
            _hashCode += getConferencePassword().hashCode();
        }
        _hashCode += getCurPicNum();
        if (getDualBoardcaster() != null) {
            _hashCode += getDualBoardcaster().hashCode();
        }
        _hashCode += getDuration();
        _hashCode += (isIfRecord() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getIfUpMode() != null) {
            _hashCode += getIfUpMode().hashCode();
        }
        _hashCode += (isLockState() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getMaxPicNum();
        if (getMultiPicCtrlMode() != null) {
            _hashCode += getMultiPicCtrlMode().hashCode();
        }
        if (getRecordState() != null) {
            _hashCode += getRecordState().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConferenceStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("boardcaster");
        elemField.setXmlName(new javax.xml.namespace.QName("", "boardcaster"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chairman");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chairman"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confCtrlMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confCtrlMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conferencePassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferencePassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("curPicNum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "curPicNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dualBoardcaster");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dualBoardcaster"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ifRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ifRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ifUpMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ifUpMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lockState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lockState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxPicNum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxPicNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiPicCtrlMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiPicCtrlMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recordState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
