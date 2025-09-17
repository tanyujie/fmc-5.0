/**
 * QueryConfInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.bean;

public class QueryConfInfo  implements java.io.Serializable {
    private String confID;

    private String duration;

    private String fileCName;

    private int fileSize;

    private String fileURL;

    private String rcdUserName;

    private String rcdUserPWD;

    private java.util.Calendar recordTime;

    public QueryConfInfo() {
    }

    public QueryConfInfo(
           String confID,
           String duration,
           String fileCName,
           int fileSize,
           String fileURL,
           String rcdUserName,
           String rcdUserPWD,
           java.util.Calendar recordTime) {
           this.confID = confID;
           this.duration = duration;
           this.fileCName = fileCName;
           this.fileSize = fileSize;
           this.fileURL = fileURL;
           this.rcdUserName = rcdUserName;
           this.rcdUserPWD = rcdUserPWD;
           this.recordTime = recordTime;
    }


    /**
     * Gets the confID value for this QueryConfInfo.
     * 
     * @return confID
     */
    public String getConfID() {
        return confID;
    }


    /**
     * Sets the confID value for this QueryConfInfo.
     * 
     * @param confID
     */
    public void setConfID(String confID) {
        this.confID = confID;
    }


    /**
     * Gets the duration value for this QueryConfInfo.
     * 
     * @return duration
     */
    public String getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this QueryConfInfo.
     * 
     * @param duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }


    /**
     * Gets the fileCName value for this QueryConfInfo.
     * 
     * @return fileCName
     */
    public String getFileCName() {
        return fileCName;
    }


    /**
     * Sets the fileCName value for this QueryConfInfo.
     * 
     * @param fileCName
     */
    public void setFileCName(String fileCName) {
        this.fileCName = fileCName;
    }


    /**
     * Gets the fileSize value for this QueryConfInfo.
     * 
     * @return fileSize
     */
    public int getFileSize() {
        return fileSize;
    }


    /**
     * Sets the fileSize value for this QueryConfInfo.
     * 
     * @param fileSize
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }


    /**
     * Gets the fileURL value for this QueryConfInfo.
     * 
     * @return fileURL
     */
    public String getFileURL() {
        return fileURL;
    }


    /**
     * Sets the fileURL value for this QueryConfInfo.
     * 
     * @param fileURL
     */
    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }


    /**
     * Gets the rcdUserName value for this QueryConfInfo.
     * 
     * @return rcdUserName
     */
    public String getRcdUserName() {
        return rcdUserName;
    }


    /**
     * Sets the rcdUserName value for this QueryConfInfo.
     * 
     * @param rcdUserName
     */
    public void setRcdUserName(String rcdUserName) {
        this.rcdUserName = rcdUserName;
    }


    /**
     * Gets the rcdUserPWD value for this QueryConfInfo.
     * 
     * @return rcdUserPWD
     */
    public String getRcdUserPWD() {
        return rcdUserPWD;
    }


    /**
     * Sets the rcdUserPWD value for this QueryConfInfo.
     * 
     * @param rcdUserPWD
     */
    public void setRcdUserPWD(String rcdUserPWD) {
        this.rcdUserPWD = rcdUserPWD;
    }


    /**
     * Gets the recordTime value for this QueryConfInfo.
     * 
     * @return recordTime
     */
    public java.util.Calendar getRecordTime() {
        return recordTime;
    }


    /**
     * Sets the recordTime value for this QueryConfInfo.
     * 
     * @param recordTime
     */
    public void setRecordTime(java.util.Calendar recordTime) {
        this.recordTime = recordTime;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof QueryConfInfo)) return false;
        QueryConfInfo other = (QueryConfInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.confID==null && other.getConfID()==null) || 
             (this.confID!=null &&
              this.confID.equals(other.getConfID()))) &&
            ((this.duration==null && other.getDuration()==null) || 
             (this.duration!=null &&
              this.duration.equals(other.getDuration()))) &&
            ((this.fileCName==null && other.getFileCName()==null) || 
             (this.fileCName!=null &&
              this.fileCName.equals(other.getFileCName()))) &&
            this.fileSize == other.getFileSize() &&
            ((this.fileURL==null && other.getFileURL()==null) || 
             (this.fileURL!=null &&
              this.fileURL.equals(other.getFileURL()))) &&
            ((this.rcdUserName==null && other.getRcdUserName()==null) || 
             (this.rcdUserName!=null &&
              this.rcdUserName.equals(other.getRcdUserName()))) &&
            ((this.rcdUserPWD==null && other.getRcdUserPWD()==null) || 
             (this.rcdUserPWD!=null &&
              this.rcdUserPWD.equals(other.getRcdUserPWD()))) &&
            ((this.recordTime==null && other.getRecordTime()==null) || 
             (this.recordTime!=null &&
              this.recordTime.equals(other.getRecordTime())));
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
        if (getConfID() != null) {
            _hashCode += getConfID().hashCode();
        }
        if (getDuration() != null) {
            _hashCode += getDuration().hashCode();
        }
        if (getFileCName() != null) {
            _hashCode += getFileCName().hashCode();
        }
        _hashCode += getFileSize();
        if (getFileURL() != null) {
            _hashCode += getFileURL().hashCode();
        }
        if (getRcdUserName() != null) {
            _hashCode += getRcdUserName().hashCode();
        }
        if (getRcdUserPWD() != null) {
            _hashCode += getRcdUserPWD().hashCode();
        }
        if (getRecordTime() != null) {
            _hashCode += getRecordTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryConfInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.m900.zte.com", "QueryConfInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "confID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileCName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fileCName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileSize");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fileSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fileURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdUserName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdUserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rcdUserPWD");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rcdUserPWD"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recordTime"));
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
