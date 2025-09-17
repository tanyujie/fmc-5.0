/**
 * MultimediaConferenceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.services.MultimediaConference;

public interface MultimediaConferenceService extends javax.xml.rpc.Service {
    public String getMultimediaConferenceAddress();

    public MultimediaConference getMultimediaConference() throws javax.xml.rpc.ServiceException;

    public MultimediaConference getMultimediaConference(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
