/**
 * ConferenceRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.request;

public class ConferenceRequest  implements java.io.Serializable {
    private int UPConf;

    private String account;

    private int confCascadeMode;

    private String conferenceName;

    private String conferenceNumber;

    private String conferencePassword;

    private String conferenceTemplet;

    private String defaultBroadcastTerminalNumber;

    private String defaultChairmanTerminalNumber;

    private int duration;

    private String dynamicRes;

    private int enableAutoVoiceRecord;

    private int enableMcuBanner;

    private int enableMcuTitle;

    private int enableVoiceRecord;

    private int inviteWithSDP;

    private int maxParticipants;

    private String multiPicControl;

    private int multiViewNumber;

    private com.zte.m900.bean.Participant[] participants;

    private com.zte.m900.bean.RecordParam record;

    private com.zte.m900.bean.MailInfo sendMail;

    private java.util.Calendar startTime;

    private String subject;

    public ConferenceRequest() {
    }

    public ConferenceRequest(
           int UPConf,
           String account,
           int confCascadeMode,
           String conferenceName,
           String conferenceNumber,
           String conferencePassword,
           String conferenceTemplet,
           String defaultBroadcastTerminalNumber,
           String defaultChairmanTerminalNumber,
           int duration,
           String dynamicRes,
           int enableAutoVoiceRecord,
           int enableMcuBanner,
           int enableMcuTitle,
           int enableVoiceRecord,
           int inviteWithSDP,
           int maxParticipants,
           String multiPicControl,
           int multiViewNumber,
           com.zte.m900.bean.Participant[] participants,
           com.zte.m900.bean.RecordParam record,
           com.zte.m900.bean.MailInfo sendMail,
           java.util.Calendar startTime,
           String subject) {
           this.UPConf = UPConf;
           this.account = account;
           this.confCascadeMode = confCascadeMode;
           this.conferenceName = conferenceName;
           this.conferenceNumber = conferenceNumber;
           this.conferencePassword = conferencePassword;
           this.conferenceTemplet = conferenceTemplet;
           this.defaultBroadcastTerminalNumber = defaultBroadcastTerminalNumber;
           this.defaultChairmanTerminalNumber = defaultChairmanTerminalNumber;
           this.duration = duration;
           this.dynamicRes = dynamicRes;
           this.enableAutoVoiceRecord = enableAutoVoiceRecord;
           this.enableMcuBanner = enableMcuBanner;
           this.enableMcuTitle = enableMcuTitle;
           this.enableVoiceRecord = enableVoiceRecord;
           this.inviteWithSDP = inviteWithSDP;
           this.maxParticipants = maxParticipants;
           this.multiPicControl = multiPicControl;
           this.multiViewNumber = multiViewNumber;
           this.participants = participants;
           this.record = record;
           this.sendMail = sendMail;
           this.startTime = startTime;
           this.subject = subject;
    }


    /**
     * Gets the UPConf value for this ConferenceRequest.
     * 
     * @return UPConf
     */
    public int getUPConf() {
        return UPConf;
    }


    /**
     * Sets the UPConf value for this ConferenceRequest.
     * 
     * @param UPConf
     */
    public void setUPConf(int UPConf) {
        this.UPConf = UPConf;
    }


    /**
     * Gets the account value for this ConferenceRequest.
     * 
     * @return account
     */
    public String getAccount() {
        return account;
    }


    /**
     * Sets the account value for this ConferenceRequest.
     * 
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }


    /**
     * Gets the confCascadeMode value for this ConferenceRequest.
     * 
     * @return confCascadeMode
     */
    public int getConfCascadeMode() {
        return confCascadeMode;
    }


    /**
     * Sets the confCascadeMode value for this ConferenceRequest.
     * 
     * @param confCascadeMode
     */
    public void setConfCascadeMode(int confCascadeMode) {
        this.confCascadeMode = confCascadeMode;
    }


    /**
     * Gets the conferenceName value for this ConferenceRequest.
     * 
     * @return conferenceName
     */
    public String getConferenceName() {
        return conferenceName;
    }


    /**
     * Sets the conferenceName value for this ConferenceRequest.
     * 
     * @param conferenceName
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }


    /**
     * Gets the conferenceNumber value for this ConferenceRequest.
     * 
     * @return conferenceNumber
     */
    public String getConferenceNumber() {
        return conferenceNumber;
    }


    /**
     * Sets the conferenceNumber value for this ConferenceRequest.
     * 
     * @param conferenceNumber
     */
    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }


    /**
     * Gets the conferencePassword value for this ConferenceRequest.
     * 
     * @return conferencePassword
     */
    public String getConferencePassword() {
        return conferencePassword;
    }


    /**
     * Sets the conferencePassword value for this ConferenceRequest.
     * 
     * @param conferencePassword
     */
    public void setConferencePassword(String conferencePassword) {
        this.conferencePassword = conferencePassword;
    }


    /**
     * Gets the conferenceTemplet value for this ConferenceRequest.
     * 
     * @return conferenceTemplet
     */
    public String getConferenceTemplet() {
        return conferenceTemplet;
    }


    /**
     * Sets the conferenceTemplet value for this ConferenceRequest.
     * 
     * @param conferenceTemplet
     */
    public void setConferenceTemplet(String conferenceTemplet) {
        this.conferenceTemplet = conferenceTemplet;
    }


    /**
     * Gets the defaultBroadcastTerminalNumber value for this ConferenceRequest.
     * 
     * @return defaultBroadcastTerminalNumber
     */
    public String getDefaultBroadcastTerminalNumber() {
        return defaultBroadcastTerminalNumber;
    }


    /**
     * Sets the defaultBroadcastTerminalNumber value for this ConferenceRequest.
     * 
     * @param defaultBroadcastTerminalNumber
     */
    public void setDefaultBroadcastTerminalNumber(String defaultBroadcastTerminalNumber) {
        this.defaultBroadcastTerminalNumber = defaultBroadcastTerminalNumber;
    }


    /**
     * Gets the defaultChairmanTerminalNumber value for this ConferenceRequest.
     * 
     * @return defaultChairmanTerminalNumber
     */
    public String getDefaultChairmanTerminalNumber() {
        return defaultChairmanTerminalNumber;
    }


    /**
     * Sets the defaultChairmanTerminalNumber value for this ConferenceRequest.
     * 
     * @param defaultChairmanTerminalNumber
     */
    public void setDefaultChairmanTerminalNumber(String defaultChairmanTerminalNumber) {
        this.defaultChairmanTerminalNumber = defaultChairmanTerminalNumber;
    }


    /**
     * Gets the duration value for this ConferenceRequest.
     * 
     * @return duration
     */
    public int getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this ConferenceRequest.
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }


    /**
     * Gets the dynamicRes value for this ConferenceRequest.
     * 
     * @return dynamicRes
     */
    public String getDynamicRes() {
        return dynamicRes;
    }


    /**
     * Sets the dynamicRes value for this ConferenceRequest.
     * 
     * @param dynamicRes
     */
    public void setDynamicRes(String dynamicRes) {
        this.dynamicRes = dynamicRes;
    }


    /**
     * Gets the enableAutoVoiceRecord value for this ConferenceRequest.
     * 
     * @return enableAutoVoiceRecord
     */
    public int getEnableAutoVoiceRecord() {
        return enableAutoVoiceRecord;
    }


    /**
     * Sets the enableAutoVoiceRecord value for this ConferenceRequest.
     * 
     * @param enableAutoVoiceRecord
     */
    public void setEnableAutoVoiceRecord(int enableAutoVoiceRecord) {
        this.enableAutoVoiceRecord = enableAutoVoiceRecord;
    }


    /**
     * Gets the enableMcuBanner value for this ConferenceRequest.
     * 
     * @return enableMcuBanner
     */
    public int getEnableMcuBanner() {
        return enableMcuBanner;
    }


    /**
     * Sets the enableMcuBanner value for this ConferenceRequest.
     * 
     * @param enableMcuBanner
     */
    public void setEnableMcuBanner(int enableMcuBanner) {
        this.enableMcuBanner = enableMcuBanner;
    }


    /**
     * Gets the enableMcuTitle value for this ConferenceRequest.
     * 
     * @return enableMcuTitle
     */
    public int getEnableMcuTitle() {
        return enableMcuTitle;
    }


    /**
     * Sets the enableMcuTitle value for this ConferenceRequest.
     * 
     * @param enableMcuTitle
     */
    public void setEnableMcuTitle(int enableMcuTitle) {
        this.enableMcuTitle = enableMcuTitle;
    }


    /**
     * Gets the enableVoiceRecord value for this ConferenceRequest.
     * 
     * @return enableVoiceRecord
     */
    public int getEnableVoiceRecord() {
        return enableVoiceRecord;
    }


    /**
     * Sets the enableVoiceRecord value for this ConferenceRequest.
     * 
     * @param enableVoiceRecord
     */
    public void setEnableVoiceRecord(int enableVoiceRecord) {
        this.enableVoiceRecord = enableVoiceRecord;
    }


    /**
     * Gets the inviteWithSDP value for this ConferenceRequest.
     * 
     * @return inviteWithSDP
     */
    public int getInviteWithSDP() {
        return inviteWithSDP;
    }


    /**
     * Sets the inviteWithSDP value for this ConferenceRequest.
     * 
     * @param inviteWithSDP
     */
    public void setInviteWithSDP(int inviteWithSDP) {
        this.inviteWithSDP = inviteWithSDP;
    }


    /**
     * Gets the maxParticipants value for this ConferenceRequest.
     * 
     * @return maxParticipants
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }


    /**
     * Sets the maxParticipants value for this ConferenceRequest.
     * 
     * @param maxParticipants
     */
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }


    /**
     * Gets the multiPicControl value for this ConferenceRequest.
     * 
     * @return multiPicControl
     */
    public String getMultiPicControl() {
        return multiPicControl;
    }


    /**
     * Sets the multiPicControl value for this ConferenceRequest.
     * 
     * @param multiPicControl
     */
    public void setMultiPicControl(String multiPicControl) {
        this.multiPicControl = multiPicControl;
    }


    /**
     * Gets the multiViewNumber value for this ConferenceRequest.
     * 
     * @return multiViewNumber
     */
    public int getMultiViewNumber() {
        return multiViewNumber;
    }


    /**
     * Sets the multiViewNumber value for this ConferenceRequest.
     * 
     * @param multiViewNumber
     */
    public void setMultiViewNumber(int multiViewNumber) {
        this.multiViewNumber = multiViewNumber;
    }


    /**
     * Gets the participants value for this ConferenceRequest.
     * 
     * @return participants
     */
    public com.zte.m900.bean.Participant[] getParticipants() {
        return participants;
    }


    /**
     * Sets the participants value for this ConferenceRequest.
     * 
     * @param participants
     */
    public void setParticipants(com.zte.m900.bean.Participant[] participants) {
        this.participants = participants;
    }


    /**
     * Gets the record value for this ConferenceRequest.
     * 
     * @return record
     */
    public com.zte.m900.bean.RecordParam getRecord() {
        return record;
    }


    /**
     * Sets the record value for this ConferenceRequest.
     * 
     * @param record
     */
    public void setRecord(com.zte.m900.bean.RecordParam record) {
        this.record = record;
    }


    /**
     * Gets the sendMail value for this ConferenceRequest.
     * 
     * @return sendMail
     */
    public com.zte.m900.bean.MailInfo getSendMail() {
        return sendMail;
    }


    /**
     * Sets the sendMail value for this ConferenceRequest.
     * 
     * @param sendMail
     */
    public void setSendMail(com.zte.m900.bean.MailInfo sendMail) {
        this.sendMail = sendMail;
    }


    /**
     * Gets the startTime value for this ConferenceRequest.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this ConferenceRequest.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }


    /**
     * Gets the subject value for this ConferenceRequest.
     * 
     * @return subject
     */
    public String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this ConferenceRequest.
     * 
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConferenceRequest)) return false;
        ConferenceRequest other = (ConferenceRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.UPConf == other.getUPConf() &&
            ((this.account==null && other.getAccount()==null) || 
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            this.confCascadeMode == other.getConfCascadeMode() &&
            ((this.conferenceName==null && other.getConferenceName()==null) || 
             (this.conferenceName!=null &&
              this.conferenceName.equals(other.getConferenceName()))) &&
            ((this.conferenceNumber==null && other.getConferenceNumber()==null) || 
             (this.conferenceNumber!=null &&
              this.conferenceNumber.equals(other.getConferenceNumber()))) &&
            ((this.conferencePassword==null && other.getConferencePassword()==null) || 
             (this.conferencePassword!=null &&
              this.conferencePassword.equals(other.getConferencePassword()))) &&
            ((this.conferenceTemplet==null && other.getConferenceTemplet()==null) || 
             (this.conferenceTemplet!=null &&
              this.conferenceTemplet.equals(other.getConferenceTemplet()))) &&
            ((this.defaultBroadcastTerminalNumber==null && other.getDefaultBroadcastTerminalNumber()==null) || 
             (this.defaultBroadcastTerminalNumber!=null &&
              this.defaultBroadcastTerminalNumber.equals(other.getDefaultBroadcastTerminalNumber()))) &&
            ((this.defaultChairmanTerminalNumber==null && other.getDefaultChairmanTerminalNumber()==null) || 
             (this.defaultChairmanTerminalNumber!=null &&
              this.defaultChairmanTerminalNumber.equals(other.getDefaultChairmanTerminalNumber()))) &&
            this.duration == other.getDuration() &&
            ((this.dynamicRes==null && other.getDynamicRes()==null) || 
             (this.dynamicRes!=null &&
              this.dynamicRes.equals(other.getDynamicRes()))) &&
            this.enableAutoVoiceRecord == other.getEnableAutoVoiceRecord() &&
            this.enableMcuBanner == other.getEnableMcuBanner() &&
            this.enableMcuTitle == other.getEnableMcuTitle() &&
            this.enableVoiceRecord == other.getEnableVoiceRecord() &&
            this.inviteWithSDP == other.getInviteWithSDP() &&
            this.maxParticipants == other.getMaxParticipants() &&
            ((this.multiPicControl==null && other.getMultiPicControl()==null) || 
             (this.multiPicControl!=null &&
              this.multiPicControl.equals(other.getMultiPicControl()))) &&
            this.multiViewNumber == other.getMultiViewNumber() &&
            ((this.participants==null && other.getParticipants()==null) || 
             (this.participants!=null &&
              java.util.Arrays.equals(this.participants, other.getParticipants()))) &&
            ((this.record==null && other.getRecord()==null) || 
             (this.record!=null &&
              this.record.equals(other.getRecord()))) &&
            ((this.sendMail==null && other.getSendMail()==null) || 
             (this.sendMail!=null &&
              this.sendMail.equals(other.getSendMail()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject())));
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
        _hashCode += getUPConf();
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        _hashCode += getConfCascadeMode();
        if (getConferenceName() != null) {
            _hashCode += getConferenceName().hashCode();
        }
        if (getConferenceNumber() != null) {
            _hashCode += getConferenceNumber().hashCode();
        }
        if (getConferencePassword() != null) {
            _hashCode += getConferencePassword().hashCode();
        }
        if (getConferenceTemplet() != null) {
            _hashCode += getConferenceTemplet().hashCode();
        }
        if (getDefaultBroadcastTerminalNumber() != null) {
            _hashCode += getDefaultBroadcastTerminalNumber().hashCode();
        }
        if (getDefaultChairmanTerminalNumber() != null) {
            _hashCode += getDefaultChairmanTerminalNumber().hashCode();
        }
        _hashCode += getDuration();
        if (getDynamicRes() != null) {
            _hashCode += getDynamicRes().hashCode();
        }
        _hashCode += getEnableAutoVoiceRecord();
        _hashCode += getEnableMcuBanner();
        _hashCode += getEnableMcuTitle();
        _hashCode += getEnableVoiceRecord();
        _hashCode += getInviteWithSDP();
        _hashCode += getMaxParticipants();
        if (getMultiPicControl() != null) {
            _hashCode += getMultiPicControl().hashCode();
        }
        _hashCode += getMultiViewNumber();
        if (getParticipants() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipants());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getParticipants(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRecord() != null) {
            _hashCode += getRecord().hashCode();
        }
        if (getSendMail() != null) {
            _hashCode += getSendMail().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConferenceRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://request.m900.zte.com", "ConferenceRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("UPConf");
        elemField.setXmlName(new javax.xml.namespace.QName("", "UPConf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("", "account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confCascadeMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confCascadeMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
        elemField.setFieldName("conferenceTemplet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "conferenceTemplet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultBroadcastTerminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "defaultBroadcastTerminalNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultChairmanTerminalNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "defaultChairmanTerminalNumber"));
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
        elemField.setFieldName("dynamicRes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dynamicRes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableAutoVoiceRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enableAutoVoiceRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableMcuBanner");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enableMcuBanner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableMcuTitle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enableMcuTitle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableVoiceRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enableVoiceRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inviteWithSDP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inviteWithSDP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxParticipants");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxParticipants"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiPicControl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiPicControl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multiViewNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multiViewNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participants");
        elemField.setXmlName(new javax.xml.namespace.QName("", "participants"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "Participant"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("record");
        elemField.setXmlName(new javax.xml.namespace.QName("", "record"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "RecordParam"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sendMail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sendMail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "MailInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subject"));
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
