/**
 * MultimediaConferenceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.services.MultimediaConference;

public class MultimediaConferenceServiceLocator extends org.apache.axis.client.Service implements MultimediaConferenceService {

    public MultimediaConferenceServiceLocator() {
    }


    public MultimediaConferenceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MultimediaConferenceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MultimediaConference
    private String MultimediaConference_address = "http://127.0.0.1:8080/services/MultimediaConference";

    @Override
    public String getMultimediaConferenceAddress() {
        return MultimediaConference_address;
    }

    // The WSDD service name defaults to the port name.
    private String MultimediaConferenceWSDDServiceName = "MultimediaConference";

    public String getMultimediaConferenceWSDDServiceName() {
        return MultimediaConferenceWSDDServiceName;
    }

    public void setMultimediaConferenceWSDDServiceName(String name) {
        MultimediaConferenceWSDDServiceName = name;
    }

    @Override
    public MultimediaConference getMultimediaConference() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MultimediaConference_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMultimediaConference(endpoint);
    }

    @Override
    public MultimediaConference getMultimediaConference(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            MultimediaConferenceSoapBindingStub _stub = new MultimediaConferenceSoapBindingStub(portAddress, this);
            _stub.setPortName(getMultimediaConferenceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMultimediaConferenceEndpointAddress(String address) {
        MultimediaConference_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (MultimediaConference.class.isAssignableFrom(serviceEndpointInterface)) {
                MultimediaConferenceSoapBindingStub _stub = new MultimediaConferenceSoapBindingStub(new java.net.URL(MultimediaConference_address), this);
                _stub.setPortName(getMultimediaConferenceWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("MultimediaConference".equals(inputPortName)) {
            return getMultimediaConference();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "MultimediaConferenceService");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "MultimediaConference"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("MultimediaConference".equals(portName)) {
            setMultimediaConferenceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
