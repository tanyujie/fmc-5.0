/**
 * MultimediaConferenceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.zte.m900.services.MultimediaConference;

public class MultimediaConferenceSoapBindingStub extends org.apache.axis.client.Stub implements MultimediaConference {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[116];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
        _initOperationDesc7();
        _initOperationDesc8();
        _initOperationDesc9();
        _initOperationDesc10();
        _initOperationDesc11();
        _initOperationDesc12();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("applyConfChairman");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ApplyConfChairmanRequest"), com.zte.m900.request.ApplyConfChairmanRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ApplyConfChairmanResponse"));
        oper.setReturnClass(com.zte.m900.response.ApplyConfChairmanResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "applyConfChairmanReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVideoNo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoNoRequest"), com.zte.m900.request.GetVideoNoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoNoResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVideoNoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVideoNoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceRequest"), com.zte.m900.request.CreateConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.CreateConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "createConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createConferenceV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceRequest"), com.zte.m900.request.CreateConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceV2Response"));
        oper.setReturnClass(com.zte.m900.response.CreateConferenceV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "createConferenceV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createConferenceV3");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceV3Request"), com.zte.m900.request.CreateConferenceV3Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceV3Response"));
        oper.setReturnClass(com.zte.m900.response.CreateConferenceV3Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "createConferenceV3Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("joinConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "JoinConferenceRequest"), com.zte.m900.request.JoinConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "JoinConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.JoinConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "joinConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("startConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "StartConferenceRequest"), com.zte.m900.request.StartConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "StartConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.StartConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "startConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("endConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "EndConferenceRequest"), com.zte.m900.request.EndConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "EndConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.EndConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "endConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceInfoList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceInfoListRequest"), com.zte.m900.request.GetConferenceInfoListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceInfoListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceInfoListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceInfoListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHistoryConferenceList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetHistoryConferenceListRequest"), com.zte.m900.request.GetHistoryConferenceListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetHistoryConferenceListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetHistoryConferenceListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getHistoryConferenceListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHistoryConfNodeList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetHistoryConfNodeListRequest"), com.zte.m900.request.GetHistoryConfNodeListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetHistoryConfNodeListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetHistoryConfNodeListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getHistoryConfNodeListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceReservedList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceReservedListRequest"), com.zte.m900.request.GetConferenceReservedListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceReservedListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceReservedListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceReservedListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceDraftList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceDraftListRequest"), com.zte.m900.request.GetConferenceDraftListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceDraftListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceDraftListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceDraftListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceDraftListByPage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceDraftListByPageRequest"), com.zte.m900.request.GetConferenceDraftListByPageRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceDraftListByPageResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceDraftListByPageResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceDraftListByPageReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancelConferenceReserved");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelConferenceReservedRequest"), com.zte.m900.request.CancelConferenceReservedRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelConferenceReservedResponse"));
        oper.setReturnClass(com.zte.m900.response.CancelConferenceReservedResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "cancelConferenceReservedReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modifyConferenceReserved");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferenceReservedRequest"), com.zte.m900.request.ModifyConferenceReservedRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferenceReservedResponse"));
        oper.setReturnClass(com.zte.m900.response.ModifyConferenceReservedResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "modifyConferenceReservedReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modifyConferenceReservedV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferenceReservedV2Request"), com.zte.m900.request.ModifyConferenceReservedV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferenceReservedV2Response"));
        oper.setReturnClass(com.zte.m900.response.ModifyConferenceReservedV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "modifyConferenceReservedV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("queryAddressBook");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryAddressBookRequest"), com.zte.m900.request.QueryAddressBookRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryAddressBookResponse"));
        oper.setReturnClass(com.zte.m900.response.QueryAddressBookResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "queryAddressBookReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("queryAddressBookV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryAddressBookV2Request"), com.zte.m900.request.QueryAddressBookV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryAddressBookV2Response"));
        oper.setReturnClass(com.zte.m900.response.QueryAddressBookV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "queryAddressBookV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("delAddressBook");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "DelAddressBookRequest"), com.zte.m900.request.DelAddressBookRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DelAddressBookResponse"));
        oper.setReturnClass(com.zte.m900.response.DelAddressBookResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "delAddressBookReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addAddressBook");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "AddAddressBookRequest"), com.zte.m900.request.AddAddressBookRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "AddAddressBookResponse"));
        oper.setReturnClass(com.zte.m900.response.AddAddressBookResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addAddressBookReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceTempletList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceTempletListRequest"), com.zte.m900.request.GetConferenceTempletListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceTempletListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceTempletListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceTempletListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("emeetinglogin");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "EmeetingloginRequest"), com.zte.m900.request.EmeetingloginRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "EmeetingloginResponse"));
        oper.setReturnClass(com.zte.m900.response.EmeetingloginResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "emeetingloginReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviteParticipantV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantV2Request"), com.zte.m900.request.InviteParticipantV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "InviteParticipantV2Response"));
        oper.setReturnClass(com.zte.m900.response.InviteParticipantV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "inviteParticipantV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviteParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantRequest"), com.zte.m900.request.InviteParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "InviteParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.InviteParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "inviteParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceInfoRequest"), com.zte.m900.request.GetConferenceInfoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceInfoResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceInfoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceInfoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceExtInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceExtInfoRequest"), com.zte.m900.request.GetConferenceExtInfoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceExtInfoResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceExtInfoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceExtInfoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVoiceRecordUrl");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVoiceRecordUrlRequest"), com.zte.m900.request.GetVoiceRecordUrlRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVoiceRecordUrlResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVoiceRecordUrlResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVoiceRecordUrlReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVoiceRecordCtrlUrl");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVoiceRecordCtrlUrlRequest"), com.zte.m900.request.GetVoiceRecordCtrlUrlRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVoiceRecordCtrlUrlResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVoiceRecordCtrlUrlResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVoiceRecordCtrlUrlReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "DeleteParticipantRequest"), com.zte.m900.request.DeleteParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DeleteParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.DeleteParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "deleteParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("connectParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ConnectParticipantRequest"), com.zte.m900.request.ConnectParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ConnectParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.ConnectParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "connectParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("disconnectParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "DisconnectParticipantRequest"), com.zte.m900.request.DisconnectParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DisconnectParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.DisconnectParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "disconnectParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("boardcastParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "BoardcastParticipantRequest"), com.zte.m900.request.BoardcastParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "BoardcastParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.BoardcastParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "boardcastParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("muteParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "MuteParticipantRequest"), com.zte.m900.request.MuteParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "MuteParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.MuteParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "muteParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancelMuteParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelMuteParticipantRequest"), com.zte.m900.request.CancelMuteParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelMuteParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.CancelMuteParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "cancelMuteParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("quietParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "QuietParticipantRequest"), com.zte.m900.request.QuietParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "QuietParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.QuietParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "quietParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("cancelQuietParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelQuietParticipantRequest"), com.zte.m900.request.CancelQuietParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelQuietParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.CancelQuietParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "cancelQuietParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("switchConfCtrlMode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SwitchConfCtrlModeRequest"), com.zte.m900.request.SwitchConfCtrlModeRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SwitchConfCtrlModeResponse"));
        oper.setReturnClass(com.zte.m900.response.SwitchConfCtrlModeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "switchConfCtrlModeReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("switchMultiCtrlMode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SwitchMultiViewCtrlModeRequest"), com.zte.m900.request.SwitchMultiViewCtrlModeRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SwitchMultiViewCtrlModeResponse"));
        oper.setReturnClass(com.zte.m900.response.SwitchMultiViewCtrlModeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "switchMultiCtrlModeReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setMultiViewNum");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetMultiViewNumRequest"), com.zte.m900.request.SetMultiViewNumRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetMultiViewNumResponse"));
        oper.setReturnClass(com.zte.m900.response.SetMultiViewNumResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setMultiViewNumReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMultiViewCfg");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetMultiViewCfgRequest"), com.zte.m900.request.GetMultiViewCfgRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetMultiViewCfgResponse"));
        oper.setReturnClass(com.zte.m900.response.GetMultiViewCfgResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMultiViewCfgReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getLiveConferenceList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetLiveConferenceListRequest"), com.zte.m900.request.GetLiveConferenceListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetLiveConferenceListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetLiveConferenceListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getLiveConferenceListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRecordConferenceList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRecordConferenceListRequest"), com.zte.m900.request.GetRecordConferenceListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecordConferenceListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetRecordConferenceListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRecordConferenceListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getURLToken");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetURLTokenRequest"), com.zte.m900.request.GetURLTokenRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetURLTokenResponse"));
        oper.setReturnClass(com.zte.m900.response.GetURLTokenResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getURLTokenReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRecsvrConfID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRecsvrConfIDRequest"), com.zte.m900.request.GetRecsvrConfIDRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecsvrConfIDResponse"));
        oper.setReturnClass(com.zte.m900.response.GetRecsvrConfIDResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRecsvrConfIDReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConferenceStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceStatusRequest"), com.zte.m900.request.GetConferenceStatusRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceStatusResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConferenceStatusResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConferenceStatusReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusRequest"), com.zte.m900.request.GetParticipantStatusRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusResponse"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantStatusResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantStatusReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantStatusV4");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV4Request"), com.zte.m900.request.GetParticipantStatusV4Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV4Response"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantStatusV4Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantStatusV4Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantStatusV3");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV3Request"), com.zte.m900.request.GetParticipantStatusV3Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV3Response"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantStatusV3Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantStatusV3Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantStatusV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV2Request"), com.zte.m900.request.GetParticipantStatusV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV2Response"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantStatusV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantStatusV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("lockConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "LockConferenceRequest"), com.zte.m900.request.LockConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "LockConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.LockConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "lockConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("unlockConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "UnlockConferenceRequest"), com.zte.m900.request.UnlockConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "UnlockConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.UnlockConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "unlockConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("pauseRecord");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "PauseRecordRequest"), com.zte.m900.request.PauseRecordRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "PauseRecordResponse"));
        oper.setReturnClass(com.zte.m900.response.PauseRecordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "pauseRecordReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("resumeRecord");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ResumeRecordRequest"), com.zte.m900.request.ResumeRecordRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ResumeRecordResponse"));
        oper.setReturnClass(com.zte.m900.response.ResumeRecordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "resumeRecordReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("prolongConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ProlongConferenceRequest"), com.zte.m900.request.ProlongConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ProlongConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.ProlongConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "prolongConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modifyConferencePassword");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferencePasswordRequest"), com.zte.m900.request.ModifyConferencePasswordRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferencePasswordResponse"));
        oper.setReturnClass(com.zte.m900.response.ModifyConferencePasswordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "modifyConferencePasswordReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addVT100User");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVT100UserRequest"), com.zte.m900.request.AddVT100UserRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "AddVT100UserResponse"));
        oper.setReturnClass(com.zte.m900.response.AddVT100UserResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addVT100UserReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("delVT100User");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "DelVT100UserRequest"), com.zte.m900.request.DelVT100UserRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DelVT100UserResponse"));
        oper.setReturnClass(com.zte.m900.response.DelVT100UserResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "delVT100UserReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[57] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("modifyVT100UserPassword");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyVT100UserPasswordRequest"), com.zte.m900.request.ModifyVT100UserPasswordRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyVT100UserPasswordResponse"));
        oper.setReturnClass(com.zte.m900.response.ModifyVT100UserPasswordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "modifyVT100UserPasswordReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[58] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVT100UserIsSupportDataConfAttr");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVT100UserIsSupportDataConfAttrRequest"), com.zte.m900.request.GetVT100UserIsSupportDataConfAttrRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserIsSupportDataConfAttrResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVT100UserIsSupportDataConfAttrReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[59] = oper;

    }

    private static void _initOperationDesc7(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("selectParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectParticipantRequest"), com.zte.m900.request.SelectParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SelectParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.SelectParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "selectParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[60] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMcuConfCounts");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "getMcuConfCountsRequest"), com.zte.m900.request.GetMcuConfCountsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "getMcuConfCountsResponse"));
        oper.setReturnClass(com.zte.m900.response.GetMcuConfCountsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMcuConfCountsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[61] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("sendMcuTitle");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SendMcuTitleRequest"), com.zte.m900.request.SendMcuTitleRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SendMcuTitleResponse"));
        oper.setReturnClass(com.zte.m900.response.SendMcuTitleResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "sendMcuTitleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[62] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setParticipantMicGain");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantMicGainRequest"), com.zte.m900.request.SetParticipantMicGainRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantMicGainResponse"));
        oper.setReturnClass(com.zte.m900.response.SetParticipantMicGainResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setParticipantMicGainReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[63] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantMicGain");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantMicGainRequest"), com.zte.m900.request.GetParticipantMicGainRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantMicGainResponse"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantMicGainResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantMicGainReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[64] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantScheduleInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantScheduleInfoRequest"), com.zte.m900.request.GetParticipantScheduleInfoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantScheduleInfoResponse"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantScheduleInfoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantScheduleInfoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[65] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("queryMcuIsReady");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryMcuIsReadyRequest"), com.zte.m900.request.QueryMcuIsReadyRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryMcuIsReadyResponse"));
        oper.setReturnClass(com.zte.m900.response.QueryMcuIsReadyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "queryMcuIsReadyReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[66] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("notifyParticipantMicState");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantMicStateRequest"), com.zte.m900.request.NotifyParticipantMicStateRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantMicStateResponse"));
        oper.setReturnClass(com.zte.m900.response.NotifyParticipantMicStateResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "notifyParticipantMicStateReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[67] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("notifyParticipantHandupState");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantHandupStateRequest"), com.zte.m900.request.NotifyParticipantHandupStateRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantHandupStateResponse"));
        oper.setReturnClass(com.zte.m900.response.NotifyParticipantHandupStateResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "notifyParticipantHandupStateReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[68] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("notifyParticipantCameraState");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantCameraStateRequest"), com.zte.m900.request.NotifyParticipantCameraStateRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantCameraStateResponse"));
        oper.setReturnClass(com.zte.m900.response.NotifyParticipantCameraStateResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "notifyParticipantCameraStateReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[69] = oper;

    }

    private static void _initOperationDesc8(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("changeConfChairman");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ChangeConfChairmanRequest"), com.zte.m900.request.ChangeConfChairmanRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ChangeConfChairmanResponse"));
        oper.setReturnClass(com.zte.m900.response.ChangeConfChairmanResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "changeConfChairmanReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[70] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConfMemebers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConfMemebersRequest"), com.zte.m900.request.GetConfMemebersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConfMemebersResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConfMemebersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConfMemebersReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[71] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTerConferenceInfoList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceInfoListRequest"), com.zte.m900.request.GetTerConferenceInfoListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceInfoListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetTerConferenceInfoListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTerConferenceInfoListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[72] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTerConferenceInfoListV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceInfoListV2Request"), com.zte.m900.request.GetTerConferenceInfoListV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceInfoListV2Response"));
        oper.setReturnClass(com.zte.m900.response.GetTerConferenceInfoListV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTerConferenceInfoListV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[73] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTerConferenceReservedList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceReservedListRequest"), com.zte.m900.request.GetTerConferenceReservedListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceReservedListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetTerConferenceReservedListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTerConferenceReservedListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[74] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTerConferenceReservedListV2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceReservedListV2Request"), com.zte.m900.request.GetTerConferenceReservedListV2Request.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceReservedListV2Response"));
        oper.setReturnClass(com.zte.m900.response.GetTerConferenceReservedListV2Response.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTerConferenceReservedListV2Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[75] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("splitConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SplitConferenceRequest"), com.zte.m900.request.SplitConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SplitConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.SplitConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "splitConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[76] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("mergeConference");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "MergeConferenceRequest"), com.zte.m900.request.MergeConferenceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "MergeConferenceResponse"));
        oper.setReturnClass(com.zte.m900.response.MergeConferenceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "mergeConferenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[77] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setConferenceProperty");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetConferencePropertyRequest"), com.zte.m900.request.SetConferencePropertyRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetConferencePropertyResponse"));
        oper.setReturnClass(com.zte.m900.response.SetConferencePropertyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setConferencePropertyReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[78] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setChairmanParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetChairmanParticipantRequest"), com.zte.m900.request.SetChairmanParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetChairmanParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.SetChairmanParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setChairmanParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[79] = oper;

    }

    private static void _initOperationDesc9(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setParticipantVideoSend");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantVideoSendRequest"), com.zte.m900.request.SetParticipantVideoSendRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantVideoSendResponse"));
        oper.setReturnClass(com.zte.m900.response.SetParticipantVideoSendResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setParticipantVideoSendReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[80] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setParticipantVideReceive");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantVideReceiveRequest"), com.zte.m900.request.SetParticipantVideReceiveRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantVideReceiveResponse"));
        oper.setReturnClass(com.zte.m900.response.SetParticipantVideReceiveResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setParticipantVideReceiveReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[81] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("sendParticipantDualVideo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SendParticipantDualVideoRequest"), com.zte.m900.request.SendParticipantDualVideoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SendParticipantDualVideoResponse"));
        oper.setReturnClass(com.zte.m900.response.SendParticipantDualVideoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "sendParticipantDualVideoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[82] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("transferParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "TransferParticipantRequest"), com.zte.m900.request.TransferParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "TransferParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.TransferParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "transferParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[83] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("browseMultiview");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "BrowseMultiViewRequest"), com.zte.m900.request.BrowseMultiViewRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "BrowseMultiViewResponse"));
        oper.setReturnClass(com.zte.m900.response.BrowseMultiViewResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "browseMultiviewReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[84] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("browseParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "BrowseParticipantRequest"), com.zte.m900.request.BrowseParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "BrowseParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.BrowseParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "browseParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[85] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("monitorParticipant");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "MonitorParticipantRequest"), com.zte.m900.request.MonitorParticipantRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "MonitorParticipantResponse"));
        oper.setReturnClass(com.zte.m900.response.MonitorParticipantResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "monitorParticipantReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[86] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVideoWallLayoutConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallLayoutConfigRequest"), com.zte.m900.request.GetVideoWallLayoutConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallLayoutConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVideoWallLayoutConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVideoWallLayoutConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[87] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("videoWallSelect");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallSelectRequest"), com.zte.m900.request.VideoWallSelectRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallSelectResponse"));
        oper.setReturnClass(com.zte.m900.response.VideoWallSelectResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "videoWallSelectReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[88] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("videoWallPollConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallPollConfigRequest"), com.zte.m900.request.VideoWallPollConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallPollConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.VideoWallPollConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "videoWallPollConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[89] = oper;

    }

    private static void _initOperationDesc10(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addVideoWallLayoutConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVideoWallLayoutConfigRequest"), com.zte.m900.request.AddVideoWallLayoutConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "AddVideoWallLayoutConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.AddVideoWallLayoutConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addVideoWallLayoutConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[90] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("delVideoWallLayoutConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "DelVideoWallLayoutConfigRequest"), com.zte.m900.request.DelVideoWallLayoutConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "DelVideoWallLayoutConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.DelVideoWallLayoutConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "delVideoWallLayoutConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[91] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setVideoWallStateList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SetVideoWallStateListRequest"), com.zte.m900.request.SetVideoWallStateListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SetVideoWallStateListResponse"));
        oper.setReturnClass(com.zte.m900.response.SetVideoWallStateListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "setVideoWallStateListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[92] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVideoWallStateList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallStateListRequest"), com.zte.m900.request.GetVideoWallStateListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallStateListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVideoWallStateListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVideoWallStateListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[93] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRegionListByPage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListByPageRequest"), com.zte.m900.request.GetRegionListByPageRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRegionListByPageResponse"));
        oper.setReturnClass(com.zte.m900.response.GetRegionListByPageResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRegionListByPageReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[94] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("multiViewSelect");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "MultiViewSelectRequest"), com.zte.m900.request.MultiViewSelectRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "MultiViewSelectResponse"));
        oper.setReturnClass(com.zte.m900.response.MultiViewSelectResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "multiViewSelectReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[95] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("selectMultiOrSingleView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectMultiOrSingleViewRequest"), com.zte.m900.request.SelectMultiOrSingleViewRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SelectMultiOrSingleViewResponse"));
        oper.setReturnClass(com.zte.m900.response.SelectMultiOrSingleViewResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "selectMultiOrSingleViewReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[96] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMultiOrSingleView");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetMultiOrSingleViewRequest"), com.zte.m900.request.GetMultiOrSingleViewRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetMultiOrSingleViewResponse"));
        oper.setReturnClass(com.zte.m900.response.GetMultiOrSingleViewResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMultiOrSingleViewReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[97] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParticipantCameraInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantCameraInfoRequest"), com.zte.m900.request.GetParticipantCameraInfoRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantCameraInfoResponse"));
        oper.setReturnClass(com.zte.m900.response.GetParticipantCameraInfoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParticipantCameraInfoReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[98] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("controlParticipantCamera");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "ControlParticipantCameraRequest"), com.zte.m900.request.ControlParticipantCameraRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "ControlParticipantCameraResponse"));
        oper.setReturnClass(com.zte.m900.response.ControlParticipantCameraResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "controlParticipantCameraReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[99] = oper;

    }

    private static void _initOperationDesc11(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRegionList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListRequest"), com.zte.m900.request.GetRegionListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRegionListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetRegionListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRegionListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[100] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("requestParticipantIFrame");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "RequestParticipantIFrame"), com.zte.m900.request.RequestParticipantIFrame.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "RequestParticipantIFrameResponse"));
        oper.setReturnClass(com.zte.m900.response.RequestParticipantIFrameResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "requestParticipantIFrameReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[101] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUserList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetUserListRequest"), com.zte.m900.request.GetUserListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetUserListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetUserListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getUserListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[102] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateUserPassword");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "UpdateUserPasswordRequest"), com.zte.m900.request.UpdateUserPasswordRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "UpdateUserPasswordResponse"));
        oper.setReturnClass(com.zte.m900.response.UpdateUserPasswordResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "updateUserPasswordReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[103] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataConfServerConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://resquest.m900.zte.com", "GetDataConfServerConfigRequest"), com.zte.m900.resquest.GetDataConfServerConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetDataConfServerConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.GetDataConfServerConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDataConfServerConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[104] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("sendT140Message");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "SendT140MessageRequest"), com.zte.m900.request.SendT140MessageRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "SendT140MessageResponse"));
        oper.setReturnClass(com.zte.m900.response.SendT140MessageResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "sendT140MessageReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[105] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTerList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerListRequest"), com.zte.m900.request.GetTerListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerminalListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetTerminalListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTerListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[106] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getConfNodeList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConfNodeListRequest"), com.zte.m900.request.GetConfNodeListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConfNodeListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetConfNodeListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getConfNodeListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[107] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVT100UserList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVT100UserListRequest"), com.zte.m900.request.GetVT100UserListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserListResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVT100UserListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVT100UserListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[108] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("login");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "LoginRequest"), com.zte.m900.request.LoginRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "LoginResponse"));
        oper.setReturnClass(com.zte.m900.response.LoginResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "loginReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[109] = oper;

    }

    private static void _initOperationDesc12(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("logout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "req"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "LogoutRequest"), com.zte.m900.request.LogoutRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "LogoutResponse"));
        oper.setReturnClass(com.zte.m900.response.LogoutResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "logoutReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[110] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAddressBook");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetAddressBookRequest"), com.zte.m900.request.GetAddressBookRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetAddressBookResponse"));
        oper.setReturnClass(com.zte.m900.response.GetAddressBookResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAddressBookReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[111] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVideoWallConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallConfigRequest"), com.zte.m900.request.GetVideoWallConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVideoWallConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVideoWallConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[112] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVideoWallPollConfig");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallPollConfigRequest"), com.zte.m900.request.GetVideoWallPollConfigRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallPollConfigResponse"));
        oper.setReturnClass(com.zte.m900.response.GetVideoWallPollConfigResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVideoWallPollConfigReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[113] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("videoWallControl");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallControlRequest"), com.zte.m900.request.VideoWallControlRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallControlResponse"));
        oper.setReturnClass(com.zte.m900.response.VideoWallControlResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "videoWallControlReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[114] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("videoWallLoopControl");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallLoopControlRequest"), com.zte.m900.request.VideoWallLoopControlRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallLoopControlResponse"));
        oper.setReturnClass(com.zte.m900.response.VideoWallLoopControlResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "videoWallLoopControlReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[115] = oper;

    }

    public MultimediaConferenceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public MultimediaConferenceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public MultimediaConferenceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
        addBindings2();
        addBindings3();
    }

    private void addBindings0() {
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_soapenc_string");
            cachedSerQNames.add(qName);
            cls = String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_AudioAndVideoCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.AudioAndVideoCapability[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioAndVideoCapability");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_CameraInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.CameraInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "CameraInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceExtInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceExtInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceExtInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceInfoV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceInfoV2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfoV2");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceSimpleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceSimpleInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceSimpleInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceStatus");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConferenceTemplet");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceTemplet[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceTemplet");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ConfNode");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConfNode[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConfNode");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_DataCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.DataCapability[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "DataCapability");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_FailInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.FailInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "FailInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_HisConfNode");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.HisConfNode[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "HisConfNode");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_McuConfCounts");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.McuConfCounts[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "McuConfCounts");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_MultiViewCfg");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.MultiViewCfg[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "MultiViewCfg");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_Participant");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.Participant[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "Participant");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ParticipantExtInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantExtInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantExtInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ParticipantScheduleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantScheduleInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantScheduleInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ParticipantStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatus");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ParticipantStatusV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatusV2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV2");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_ParticipantStatusV3");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatusV3[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV3");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_PartStaV4");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.PartStaV4[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "PartStaV4");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_PollTer");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.PollTer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_QueryConfInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.QueryConfInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "QueryConfInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_RegionInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.RegionInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "RegionInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_SynRcdInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.SynRcdInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "SynRcdInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_TerminalSimpleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalSimpleInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_TerminalSimpleInfoV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalSimpleInfoV2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfoV2");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_TerminalStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalStatus");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_UserInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.UserInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "UserInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_VideoCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoCapability[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoCapability");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_VideoWallCellConfig");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallCellConfig[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallCellConfig");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_VideoWallLayoutConfig");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallLayoutConfig[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallLayoutConfig");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_VideoWallState");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallState[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallState");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://127.0.0.1:8080/services/MultimediaConference", "ArrayOf_tns3_VT100UserInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VT100UserInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VT100UserInfo");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioAndVideoCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.AudioAndVideoCapability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "AudioCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.AudioCapability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "CameraInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.CameraInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceExtInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceExtInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceInfoV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceInfoV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceSimpleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceSimpleInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConferenceTemplet");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConferenceTemplet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ConfNode");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ConfNode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "DataCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.DataCapability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "FailInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.FailInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "HisConfNode");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.HisConfNode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "MailInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.MailInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "McuConfCounts");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.McuConfCounts.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "MultiViewCfg");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.MultiViewCfg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "Participant");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.Participant.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantExtInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantExtInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantScheduleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantScheduleInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatusV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantStatusV3");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantStatusV3.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "ParticipantV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.ParticipantV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "PartStaV4");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.PartStaV4.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "PollTer");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.PollTer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "QueryConfInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.QueryConfInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "RecordParam");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.RecordParam.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "RegionInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.RegionInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "SynRcdInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.SynRcdInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalSimpleInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalSimpleInfoV2");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalSimpleInfoV2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "TerminalStatus");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.TerminalStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "UserInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.UserInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoCapability");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoCapability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallCellConfig");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallCellConfig.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallLayoutConfig");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallLayoutConfig.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VideoWallState");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VideoWallState.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://bean.m900.zte.com", "VT100UserInfo");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.bean.VT100UserInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "AddAddressBookRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.AddAddressBookRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVideoWallLayoutConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.AddVideoWallLayoutConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "AddVT100UserRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.AddVT100UserRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ApplyConfChairmanRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ApplyConfChairmanRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "BoardcastParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.BoardcastParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "BrowseMultiViewRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.BrowseMultiViewRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "BrowseParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.BrowseParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelConferenceReservedRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.CancelConferenceReservedRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelMuteParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.CancelMuteParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "CancelQuietParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.CancelQuietParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ChangeConfChairmanRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ChangeConfChairmanRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ConnectParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ConnectParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ControlParticipantCameraRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ControlParticipantCameraRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.CreateConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "CreateConferenceV3Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.CreateConferenceV3Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "DelAddressBookRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.DelAddressBookRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "DeleteParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.DeleteParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "DelVideoWallLayoutConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.DelVideoWallLayoutConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "DelVT100UserRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.DelVT100UserRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "DisconnectParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.DisconnectParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "EmeetingloginRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.EmeetingloginRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "EndConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.EndConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetAddressBookRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetAddressBookRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceDraftListByPageRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceDraftListByPageRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceDraftListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceDraftListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceExtInfoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceExtInfoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceInfoListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceInfoListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceInfoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceInfoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceReservedListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceReservedListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceStatusRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceStatusRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConferenceTempletListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConferenceTempletListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConfMemebersRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConfMemebersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetConfNodeListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetConfNodeListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetHistoryConferenceListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetHistoryConferenceListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetHistoryConfNodeListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetHistoryConfNodeListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetLiveConferenceListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetLiveConferenceListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "getMcuConfCountsRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetMcuConfCountsRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetMultiOrSingleViewRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetMultiOrSingleViewRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetMultiViewCfgRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetMultiViewCfgRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantCameraInfoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantCameraInfoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantMicGainRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantMicGainRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantScheduleInfoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantScheduleInfoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantStatusRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantStatusV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV3Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantStatusV3Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetParticipantStatusV4Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetParticipantStatusV4Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRecordConferenceListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetRecordConferenceListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRecsvrConfIDRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetRecsvrConfIDRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListByPageRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetRegionListByPageRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetRegionListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetRegionListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceInfoListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetTerConferenceInfoListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceInfoListV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetTerConferenceInfoListV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceReservedListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetTerConferenceReservedListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerConferenceReservedListV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetTerConferenceReservedListV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetTerListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetTerListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetURLTokenRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetURLTokenRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetUserListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetUserListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoNoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVideoNoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVideoWallConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallLayoutConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVideoWallLayoutConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallPollConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVideoWallPollConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVideoWallStateListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVideoWallStateListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVoiceRecordCtrlUrlRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVoiceRecordCtrlUrlRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVoiceRecordUrlRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVoiceRecordUrlRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVT100UserIsSupportDataConfAttrRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVT100UserIsSupportDataConfAttrRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "GetVT100UserListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.GetVT100UserListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.InviteParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "InviteParticipantV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.InviteParticipantV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "JoinConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.JoinConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "LockConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.LockConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "LoginRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.LoginRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "LogoutRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.LogoutRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "MergeConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.MergeConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferencePasswordRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ModifyConferencePasswordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferenceReservedRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ModifyConferenceReservedRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyConferenceReservedV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ModifyConferenceReservedV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ModifyVT100UserPasswordRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ModifyVT100UserPasswordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "MonitorParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.MonitorParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "MultiViewSelectRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.MultiViewSelectRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "MuteParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.MuteParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantCameraStateRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.NotifyParticipantCameraStateRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantHandupStateRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.NotifyParticipantHandupStateRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "NotifyParticipantMicStateRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.NotifyParticipantMicStateRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "PauseRecordRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.PauseRecordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ProlongConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ProlongConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryAddressBookRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.QueryAddressBookRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryAddressBookV2Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.QueryAddressBookV2Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "QueryMcuIsReadyRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.QueryMcuIsReadyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "QuietParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.QuietParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "Request");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.Request.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "RequestParticipantIFrame");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.RequestParticipantIFrame.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "ResumeRecordRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.ResumeRecordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectMultiOrSingleViewRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SelectMultiOrSingleViewRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SelectParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SelectParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SendMcuTitleRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SendMcuTitleRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SendParticipantDualVideoRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SendParticipantDualVideoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SendT140MessageRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SendT140MessageRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetChairmanParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetChairmanParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetConferencePropertyRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetConferencePropertyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetMultiViewNumRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetMultiViewNumRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantMicGainRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetParticipantMicGainRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantVideoSendRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetParticipantVideoSendRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetParticipantVideReceiveRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetParticipantVideReceiveRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SetVideoWallStateListRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SetVideoWallStateListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SplitConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SplitConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "StartConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.StartConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SwitchConfCtrlModeRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SwitchConfCtrlModeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "SwitchMultiViewCtrlModeRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.SwitchMultiViewCtrlModeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "TerCtrlRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.TerCtrlRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "TransferParticipantRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.TransferParticipantRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "UnlockConferenceRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.UnlockConferenceRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "UpdateUserPasswordRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.UpdateUserPasswordRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallControlRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.VideoWallControlRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallLoopControlRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.VideoWallLoopControlRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallPollConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.VideoWallPollConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.VideoWallRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://request.m900.zte.com", "VideoWallSelectRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.request.VideoWallSelectRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "AddAddressBookResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.AddAddressBookResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "AddVideoWallLayoutConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.AddVideoWallLayoutConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "AddVT100UserResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.AddVT100UserResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ApplyConfChairmanResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ApplyConfChairmanResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "BoardcastParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.BoardcastParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "BrowseMultiViewResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.BrowseMultiViewResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "BrowseParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.BrowseParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings2() {
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelConferenceReservedResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CancelConferenceReservedResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelMuteParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CancelMuteParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CancelQuietParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CancelQuietParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ChangeConfChairmanResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ChangeConfChairmanResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ConnectParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ConnectParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ControlParticipantCameraResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ControlParticipantCameraResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CreateConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CreateConferenceV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "CreateConferenceV3Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.CreateConferenceV3Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "DelAddressBookResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.DelAddressBookResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "DeleteParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.DeleteParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "DelVideoWallLayoutConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.DelVideoWallLayoutConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "DelVT100UserResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.DelVT100UserResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "DisconnectParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.DisconnectParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "EmeetingloginResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.EmeetingloginResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "EndConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.EndConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetAddressBookResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetAddressBookResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceDraftListByPageResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceDraftListByPageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceDraftListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceDraftListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceExtInfoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceExtInfoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceInfoListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceInfoListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceInfoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceInfoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceReservedListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceReservedListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceStatusResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConferenceTempletListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConferenceTempletListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConfMemebersResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConfMemebersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetConfNodeListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetConfNodeListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetDataConfServerConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetDataConfServerConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetHistoryConferenceListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetHistoryConferenceListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetHistoryConfNodeListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetHistoryConfNodeListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetLiveConferenceListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetLiveConferenceListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "getMcuConfCountsResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetMcuConfCountsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetMultiOrSingleViewResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetMultiOrSingleViewResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetMultiViewCfgResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetMultiViewCfgResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantCameraInfoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantCameraInfoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantMicGainResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantMicGainResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantScheduleInfoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantScheduleInfoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantStatusV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV3Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantStatusV3Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetParticipantStatusV4Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetParticipantStatusV4Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecordConferenceListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetRecordConferenceListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRecsvrConfIDResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetRecsvrConfIDResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRegionListByPageResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetRegionListByPageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetRegionListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetRegionListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceInfoListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetTerConferenceInfoListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceInfoListV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetTerConferenceInfoListV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceReservedListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetTerConferenceReservedListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerConferenceReservedListV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetTerConferenceReservedListV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetTerminalListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetTerminalListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetURLTokenResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetURLTokenResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetUserListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetUserListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoNoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVideoNoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVideoWallConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallLayoutConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVideoWallLayoutConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallPollConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVideoWallPollConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVideoWallStateListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVideoWallStateListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVoiceRecordCtrlUrlResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVoiceRecordCtrlUrlResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVoiceRecordUrlResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVoiceRecordUrlResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserIsSupportDataConfAttrResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "GetVT100UserListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.GetVT100UserListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "InviteParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.InviteParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "InviteParticipantV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.InviteParticipantV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "JoinConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.JoinConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "LockConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.LockConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "LoginResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.LoginResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "LogoutResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.LogoutResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "MergeConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.MergeConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferencePasswordResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ModifyConferencePasswordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferenceReservedResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ModifyConferenceReservedResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyConferenceReservedV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ModifyConferenceReservedV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ModifyVT100UserPasswordResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ModifyVT100UserPasswordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "MonitorParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.MonitorParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "MultiViewSelectResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.MultiViewSelectResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "MuteParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.MuteParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantCameraStateResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.NotifyParticipantCameraStateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantHandupStateResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.NotifyParticipantHandupStateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "NotifyParticipantMicStateResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.NotifyParticipantMicStateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "PauseRecordResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.PauseRecordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ProlongConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ProlongConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryAddressBookResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.QueryAddressBookResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryAddressBookV2Response");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.QueryAddressBookV2Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "QueryMcuIsReadyResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.QueryMcuIsReadyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "QuietParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.QuietParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "RequestParticipantIFrameResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.RequestParticipantIFrameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "ResumeRecordResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.ResumeRecordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SelectMultiOrSingleViewResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SelectMultiOrSingleViewResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SelectParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SelectParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SendMcuTitleResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SendMcuTitleResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SendParticipantDualVideoResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SendParticipantDualVideoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SendT140MessageResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SendT140MessageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetChairmanParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetChairmanParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetConferencePropertyResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetConferencePropertyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetMultiViewNumResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetMultiViewNumResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantMicGainResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetParticipantMicGainResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantVideoSendResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetParticipantVideoSendResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetParticipantVideReceiveResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetParticipantVideReceiveResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SetVideoWallStateListResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SetVideoWallStateListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SplitConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SplitConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings3() {
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "StartConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.StartConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SwitchConfCtrlModeResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SwitchConfCtrlModeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "SwitchMultiViewCtrlModeResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.SwitchMultiViewCtrlModeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "TransferParticipantResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.TransferParticipantResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "UnlockConferenceResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.UnlockConferenceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "UpdateUserPasswordResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.UpdateUserPasswordResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallControlResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.VideoWallControlResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallLoopControlResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.VideoWallLoopControlResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallPollConfigResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.VideoWallPollConfigResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.VideoWallResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://response.m900.zte.com", "VideoWallSelectResponse");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.response.VideoWallSelectResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://resquest.m900.zte.com", "GetDataConfServerConfigRequest");
            cachedSerQNames.add(qName);
            cls = com.zte.m900.resquest.GetDataConfServerConfigRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            Class sf = (Class)
                                 cachedSerFactories.get(i);
                            Class df = (Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.zte.m900.response.ApplyConfChairmanResponse applyConfChairman(com.zte.m900.request.ApplyConfChairmanRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "applyConfChairman"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ApplyConfChairmanResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ApplyConfChairmanResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ApplyConfChairmanResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVideoNoResponse getVideoNo(com.zte.m900.request.GetVideoNoRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVideoNo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVideoNoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVideoNoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVideoNoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CreateConferenceResponse createConference(com.zte.m900.request.CreateConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "createConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CreateConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CreateConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CreateConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CreateConferenceV2Response createConferenceV2(com.zte.m900.request.CreateConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "createConferenceV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CreateConferenceV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CreateConferenceV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CreateConferenceV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CreateConferenceV3Response createConferenceV3(com.zte.m900.request.CreateConferenceV3Request request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "createConferenceV3"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CreateConferenceV3Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CreateConferenceV3Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CreateConferenceV3Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.JoinConferenceResponse joinConference(com.zte.m900.request.JoinConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "joinConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.JoinConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.JoinConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.JoinConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.StartConferenceResponse startConference(com.zte.m900.request.StartConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "startConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.StartConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.StartConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.StartConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.EndConferenceResponse endConference(com.zte.m900.request.EndConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "endConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.EndConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.EndConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.EndConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceInfoListResponse getConferenceInfoList(com.zte.m900.request.GetConferenceInfoListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceInfoList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceInfoListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceInfoListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceInfoListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetHistoryConferenceListResponse getHistoryConferenceList(com.zte.m900.request.GetHistoryConferenceListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getHistoryConferenceList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetHistoryConferenceListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetHistoryConferenceListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetHistoryConferenceListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetHistoryConfNodeListResponse getHistoryConfNodeList(com.zte.m900.request.GetHistoryConfNodeListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getHistoryConfNodeList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetHistoryConfNodeListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetHistoryConfNodeListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetHistoryConfNodeListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceReservedListResponse getConferenceReservedList(com.zte.m900.request.GetConferenceReservedListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceReservedList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceReservedListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceReservedListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceReservedListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceDraftListResponse getConferenceDraftList(com.zte.m900.request.GetConferenceDraftListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceDraftList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceDraftListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceDraftListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceDraftListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceDraftListByPageResponse getConferenceDraftListByPage(com.zte.m900.request.GetConferenceDraftListByPageRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceDraftListByPage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceDraftListByPageResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceDraftListByPageResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceDraftListByPageResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CancelConferenceReservedResponse cancelConferenceReserved(com.zte.m900.request.CancelConferenceReservedRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "cancelConferenceReserved"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CancelConferenceReservedResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CancelConferenceReservedResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CancelConferenceReservedResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ModifyConferenceReservedResponse modifyConferenceReserved(com.zte.m900.request.ModifyConferenceReservedRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "modifyConferenceReserved"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ModifyConferenceReservedResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ModifyConferenceReservedResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ModifyConferenceReservedResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ModifyConferenceReservedV2Response modifyConferenceReservedV2(com.zte.m900.request.ModifyConferenceReservedV2Request request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "modifyConferenceReservedV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ModifyConferenceReservedV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ModifyConferenceReservedV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ModifyConferenceReservedV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.QueryAddressBookResponse queryAddressBook(com.zte.m900.request.QueryAddressBookRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "queryAddressBook"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.QueryAddressBookResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.QueryAddressBookResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.QueryAddressBookResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.QueryAddressBookV2Response queryAddressBookV2(com.zte.m900.request.QueryAddressBookV2Request request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "queryAddressBookV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.QueryAddressBookV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.QueryAddressBookV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.QueryAddressBookV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.DelAddressBookResponse delAddressBook(com.zte.m900.request.DelAddressBookRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "delAddressBook"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.DelAddressBookResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.DelAddressBookResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.DelAddressBookResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.AddAddressBookResponse addAddressBook(com.zte.m900.request.AddAddressBookRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "addAddressBook"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.AddAddressBookResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.AddAddressBookResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.AddAddressBookResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceTempletListResponse getConferenceTempletList(com.zte.m900.request.GetConferenceTempletListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceTempletList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceTempletListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceTempletListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceTempletListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.EmeetingloginResponse emeetinglogin(com.zte.m900.request.EmeetingloginRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "emeetinglogin"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.EmeetingloginResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.EmeetingloginResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.EmeetingloginResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.InviteParticipantV2Response inviteParticipantV2(com.zte.m900.request.InviteParticipantV2Request req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "inviteParticipantV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.InviteParticipantV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.InviteParticipantV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.InviteParticipantV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.InviteParticipantResponse inviteParticipant(com.zte.m900.request.InviteParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "inviteParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.InviteParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.InviteParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.InviteParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceInfoResponse getConferenceInfo(com.zte.m900.request.GetConferenceInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceInfoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceInfoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceInfoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceExtInfoResponse getConferenceExtInfo(com.zte.m900.request.GetConferenceExtInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceExtInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceExtInfoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceExtInfoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceExtInfoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVoiceRecordUrlResponse getVoiceRecordUrl(com.zte.m900.request.GetVoiceRecordUrlRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVoiceRecordUrl"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVoiceRecordUrlResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVoiceRecordUrlResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVoiceRecordUrlResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVoiceRecordCtrlUrlResponse getVoiceRecordCtrlUrl(com.zte.m900.request.GetVoiceRecordCtrlUrlRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVoiceRecordCtrlUrl"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVoiceRecordCtrlUrlResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVoiceRecordCtrlUrlResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVoiceRecordCtrlUrlResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.DeleteParticipantResponse deleteParticipant(com.zte.m900.request.DeleteParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "deleteParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.DeleteParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.DeleteParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.DeleteParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ConnectParticipantResponse connectParticipant(com.zte.m900.request.ConnectParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "connectParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ConnectParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ConnectParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ConnectParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.DisconnectParticipantResponse disconnectParticipant(com.zte.m900.request.DisconnectParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "disconnectParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.DisconnectParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.DisconnectParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.DisconnectParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.BoardcastParticipantResponse boardcastParticipant(com.zte.m900.request.BoardcastParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "boardcastParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.BoardcastParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.BoardcastParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.BoardcastParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.MuteParticipantResponse muteParticipant(com.zte.m900.request.MuteParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "muteParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.MuteParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.MuteParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.MuteParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CancelMuteParticipantResponse cancelMuteParticipant(com.zte.m900.request.CancelMuteParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "cancelMuteParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CancelMuteParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CancelMuteParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CancelMuteParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.QuietParticipantResponse quietParticipant(com.zte.m900.request.QuietParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "quietParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.QuietParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.QuietParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.QuietParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.CancelQuietParticipantResponse cancelQuietParticipant(com.zte.m900.request.CancelQuietParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "cancelQuietParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.CancelQuietParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.CancelQuietParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.CancelQuietParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SwitchConfCtrlModeResponse switchConfCtrlMode(com.zte.m900.request.SwitchConfCtrlModeRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "switchConfCtrlMode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SwitchConfCtrlModeResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SwitchConfCtrlModeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SwitchConfCtrlModeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SwitchMultiViewCtrlModeResponse switchMultiCtrlMode(com.zte.m900.request.SwitchMultiViewCtrlModeRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "switchMultiCtrlMode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SwitchMultiViewCtrlModeResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SwitchMultiViewCtrlModeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SwitchMultiViewCtrlModeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetMultiViewNumResponse setMultiViewNum(com.zte.m900.request.SetMultiViewNumRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setMultiViewNum"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetMultiViewNumResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetMultiViewNumResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetMultiViewNumResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetMultiViewCfgResponse getMultiViewCfg(com.zte.m900.request.GetMultiViewCfgRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getMultiViewCfg"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetMultiViewCfgResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetMultiViewCfgResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetMultiViewCfgResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetLiveConferenceListResponse getLiveConferenceList(com.zte.m900.request.GetLiveConferenceListRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getLiveConferenceList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetLiveConferenceListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetLiveConferenceListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetLiveConferenceListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetRecordConferenceListResponse getRecordConferenceList(com.zte.m900.request.GetRecordConferenceListRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getRecordConferenceList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetRecordConferenceListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetRecordConferenceListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetRecordConferenceListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetURLTokenResponse getURLToken(com.zte.m900.request.GetURLTokenRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getURLToken"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetURLTokenResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetURLTokenResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetURLTokenResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetRecsvrConfIDResponse getRecsvrConfID(com.zte.m900.request.GetRecsvrConfIDRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getRecsvrConfID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetRecsvrConfIDResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetRecsvrConfIDResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetRecsvrConfIDResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConferenceStatusResponse getConferenceStatus(com.zte.m900.request.GetConferenceStatusRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConferenceStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConferenceStatusResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConferenceStatusResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConferenceStatusResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantStatusResponse getParticipantStatus(com.zte.m900.request.GetParticipantStatusRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantStatusResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantStatusResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantStatusResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantStatusV4Response getParticipantStatusV4(com.zte.m900.request.GetParticipantStatusV4Request req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantStatusV4"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantStatusV4Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantStatusV4Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantStatusV4Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantStatusV3Response getParticipantStatusV3(com.zte.m900.request.GetParticipantStatusV3Request req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantStatusV3"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantStatusV3Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantStatusV3Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantStatusV3Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantStatusV2Response getParticipantStatusV2(com.zte.m900.request.GetParticipantStatusV2Request req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantStatusV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantStatusV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantStatusV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantStatusV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.LockConferenceResponse lockConference(com.zte.m900.request.LockConferenceRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "lockConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.LockConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.LockConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.LockConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.UnlockConferenceResponse unlockConference(com.zte.m900.request.UnlockConferenceRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "unlockConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.UnlockConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.UnlockConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.UnlockConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.PauseRecordResponse pauseRecord(com.zte.m900.request.PauseRecordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "pauseRecord"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.PauseRecordResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.PauseRecordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.PauseRecordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ResumeRecordResponse resumeRecord(com.zte.m900.request.ResumeRecordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "resumeRecord"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ResumeRecordResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ResumeRecordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ResumeRecordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ProlongConferenceResponse prolongConference(com.zte.m900.request.ProlongConferenceRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "prolongConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ProlongConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ProlongConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ProlongConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ModifyConferencePasswordResponse modifyConferencePassword(com.zte.m900.request.ModifyConferencePasswordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "modifyConferencePassword"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ModifyConferencePasswordResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ModifyConferencePasswordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ModifyConferencePasswordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.AddVT100UserResponse addVT100User(com.zte.m900.request.AddVT100UserRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "addVT100User"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.AddVT100UserResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.AddVT100UserResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.AddVT100UserResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.DelVT100UserResponse delVT100User(com.zte.m900.request.DelVT100UserRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "delVT100User"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.DelVT100UserResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.DelVT100UserResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.DelVT100UserResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ModifyVT100UserPasswordResponse modifyVT100UserPassword(com.zte.m900.request.ModifyVT100UserPasswordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[58]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "modifyVT100UserPassword"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ModifyVT100UserPasswordResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ModifyVT100UserPasswordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ModifyVT100UserPasswordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse getVT100UserIsSupportDataConfAttr(com.zte.m900.request.GetVT100UserIsSupportDataConfAttrRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[59]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVT100UserIsSupportDataConfAttr"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVT100UserIsSupportDataConfAttrResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SelectParticipantResponse selectParticipant(com.zte.m900.request.SelectParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[60]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "selectParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SelectParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SelectParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SelectParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetMcuConfCountsResponse getMcuConfCounts(com.zte.m900.request.GetMcuConfCountsRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[61]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getMcuConfCounts"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetMcuConfCountsResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetMcuConfCountsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetMcuConfCountsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SendMcuTitleResponse sendMcuTitle(com.zte.m900.request.SendMcuTitleRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[62]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "sendMcuTitle"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SendMcuTitleResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SendMcuTitleResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SendMcuTitleResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetParticipantMicGainResponse setParticipantMicGain(com.zte.m900.request.SetParticipantMicGainRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[63]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setParticipantMicGain"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetParticipantMicGainResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetParticipantMicGainResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetParticipantMicGainResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantMicGainResponse getParticipantMicGain(com.zte.m900.request.GetParticipantMicGainRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[64]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantMicGain"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantMicGainResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantMicGainResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantMicGainResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetParticipantScheduleInfoResponse getParticipantScheduleInfo(com.zte.m900.request.GetParticipantScheduleInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[65]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantScheduleInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantScheduleInfoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantScheduleInfoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantScheduleInfoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.QueryMcuIsReadyResponse queryMcuIsReady(com.zte.m900.request.QueryMcuIsReadyRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[66]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "queryMcuIsReady"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.QueryMcuIsReadyResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.QueryMcuIsReadyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.QueryMcuIsReadyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.NotifyParticipantMicStateResponse notifyParticipantMicState(com.zte.m900.request.NotifyParticipantMicStateRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[67]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "notifyParticipantMicState"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.NotifyParticipantMicStateResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.NotifyParticipantMicStateResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.NotifyParticipantMicStateResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.NotifyParticipantHandupStateResponse notifyParticipantHandupState(com.zte.m900.request.NotifyParticipantHandupStateRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[68]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "notifyParticipantHandupState"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.NotifyParticipantHandupStateResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.NotifyParticipantHandupStateResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.NotifyParticipantHandupStateResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.NotifyParticipantCameraStateResponse notifyParticipantCameraState(com.zte.m900.request.NotifyParticipantCameraStateRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[69]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "notifyParticipantCameraState"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.NotifyParticipantCameraStateResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.NotifyParticipantCameraStateResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.NotifyParticipantCameraStateResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.ChangeConfChairmanResponse changeConfChairman(com.zte.m900.request.ChangeConfChairmanRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[70]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "changeConfChairman"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ChangeConfChairmanResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ChangeConfChairmanResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ChangeConfChairmanResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConfMemebersResponse getConfMemebers(com.zte.m900.request.GetConfMemebersRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[71]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConfMemebers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConfMemebersResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConfMemebersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConfMemebersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetTerConferenceInfoListResponse getTerConferenceInfoList(com.zte.m900.request.GetTerConferenceInfoListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[72]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getTerConferenceInfoList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetTerConferenceInfoListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetTerConferenceInfoListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetTerConferenceInfoListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetTerConferenceInfoListV2Response getTerConferenceInfoListV2(com.zte.m900.request.GetTerConferenceInfoListV2Request request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[73]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getTerConferenceInfoListV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetTerConferenceInfoListV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetTerConferenceInfoListV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetTerConferenceInfoListV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetTerConferenceReservedListResponse getTerConferenceReservedList(com.zte.m900.request.GetTerConferenceReservedListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[74]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getTerConferenceReservedList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetTerConferenceReservedListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetTerConferenceReservedListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetTerConferenceReservedListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetTerConferenceReservedListV2Response getTerConferenceReservedListV2(com.zte.m900.request.GetTerConferenceReservedListV2Request request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[75]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getTerConferenceReservedListV2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetTerConferenceReservedListV2Response) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetTerConferenceReservedListV2Response) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetTerConferenceReservedListV2Response.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SplitConferenceResponse splitConference(com.zte.m900.request.SplitConferenceRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[76]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "splitConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SplitConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SplitConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SplitConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.MergeConferenceResponse mergeConference(com.zte.m900.request.MergeConferenceRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[77]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "mergeConference"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.MergeConferenceResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.MergeConferenceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.MergeConferenceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetConferencePropertyResponse setConferenceProperty(com.zte.m900.request.SetConferencePropertyRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[78]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setConferenceProperty"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetConferencePropertyResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetConferencePropertyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetConferencePropertyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetChairmanParticipantResponse setChairmanParticipant(com.zte.m900.request.SetChairmanParticipantRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[79]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setChairmanParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetChairmanParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetChairmanParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetChairmanParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetParticipantVideoSendResponse setParticipantVideoSend(com.zte.m900.request.SetParticipantVideoSendRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[80]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setParticipantVideoSend"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetParticipantVideoSendResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetParticipantVideoSendResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetParticipantVideoSendResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetParticipantVideReceiveResponse setParticipantVideReceive(com.zte.m900.request.SetParticipantVideReceiveRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[81]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setParticipantVideReceive"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetParticipantVideReceiveResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetParticipantVideReceiveResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetParticipantVideReceiveResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SendParticipantDualVideoResponse sendParticipantDualVideo(com.zte.m900.request.SendParticipantDualVideoRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[82]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "sendParticipantDualVideo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SendParticipantDualVideoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SendParticipantDualVideoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SendParticipantDualVideoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.TransferParticipantResponse transferParticipant(com.zte.m900.request.TransferParticipantRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[83]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "transferParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.TransferParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.TransferParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.TransferParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.BrowseMultiViewResponse browseMultiview(com.zte.m900.request.BrowseMultiViewRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[84]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "browseMultiview"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.BrowseMultiViewResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.BrowseMultiViewResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.BrowseMultiViewResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.BrowseParticipantResponse browseParticipant(com.zte.m900.request.BrowseParticipantRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[85]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "browseParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.BrowseParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.BrowseParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.BrowseParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.MonitorParticipantResponse monitorParticipant(com.zte.m900.request.MonitorParticipantRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[86]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "monitorParticipant"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.MonitorParticipantResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.MonitorParticipantResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.MonitorParticipantResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVideoWallLayoutConfigResponse getVideoWallLayoutConfig(com.zte.m900.request.GetVideoWallLayoutConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[87]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVideoWallLayoutConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVideoWallLayoutConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVideoWallLayoutConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVideoWallLayoutConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.VideoWallSelectResponse videoWallSelect(com.zte.m900.request.VideoWallSelectRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[88]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "videoWallSelect"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.VideoWallSelectResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.VideoWallSelectResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.VideoWallSelectResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.VideoWallPollConfigResponse videoWallPollConfig(com.zte.m900.request.VideoWallPollConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[89]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "videoWallPollConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.VideoWallPollConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.VideoWallPollConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.VideoWallPollConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.AddVideoWallLayoutConfigResponse addVideoWallLayoutConfig(com.zte.m900.request.AddVideoWallLayoutConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[90]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "addVideoWallLayoutConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.AddVideoWallLayoutConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.AddVideoWallLayoutConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.AddVideoWallLayoutConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.DelVideoWallLayoutConfigResponse delVideoWallLayoutConfig(com.zte.m900.request.DelVideoWallLayoutConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[91]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "delVideoWallLayoutConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.DelVideoWallLayoutConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.DelVideoWallLayoutConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.DelVideoWallLayoutConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.SetVideoWallStateListResponse setVideoWallStateList(com.zte.m900.request.SetVideoWallStateListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[92]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "setVideoWallStateList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SetVideoWallStateListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SetVideoWallStateListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SetVideoWallStateListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetVideoWallStateListResponse getVideoWallStateList(com.zte.m900.request.GetVideoWallStateListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[93]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVideoWallStateList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVideoWallStateListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVideoWallStateListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVideoWallStateListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetRegionListByPageResponse getRegionListByPage(com.zte.m900.request.GetRegionListByPageRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[94]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getRegionListByPage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetRegionListByPageResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetRegionListByPageResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetRegionListByPageResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.MultiViewSelectResponse multiViewSelect(com.zte.m900.request.MultiViewSelectRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[95]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "multiViewSelect"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.MultiViewSelectResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.MultiViewSelectResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.MultiViewSelectResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.SelectMultiOrSingleViewResponse selectMultiOrSingleView(com.zte.m900.request.SelectMultiOrSingleViewRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[96]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "selectMultiOrSingleView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SelectMultiOrSingleViewResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SelectMultiOrSingleViewResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SelectMultiOrSingleViewResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetMultiOrSingleViewResponse getMultiOrSingleView(com.zte.m900.request.GetMultiOrSingleViewRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[97]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getMultiOrSingleView"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetMultiOrSingleViewResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetMultiOrSingleViewResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetMultiOrSingleViewResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetParticipantCameraInfoResponse getParticipantCameraInfo(com.zte.m900.request.GetParticipantCameraInfoRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[98]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getParticipantCameraInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetParticipantCameraInfoResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetParticipantCameraInfoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetParticipantCameraInfoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.ControlParticipantCameraResponse controlParticipantCamera(com.zte.m900.request.ControlParticipantCameraRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[99]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "controlParticipantCamera"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.ControlParticipantCameraResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.ControlParticipantCameraResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.ControlParticipantCameraResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetRegionListResponse getRegionList(com.zte.m900.request.GetRegionListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[100]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getRegionList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetRegionListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetRegionListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetRegionListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.RequestParticipantIFrameResponse requestParticipantIFrame(com.zte.m900.request.RequestParticipantIFrame req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[101]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "requestParticipantIFrame"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.RequestParticipantIFrameResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.RequestParticipantIFrameResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.RequestParticipantIFrameResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetUserListResponse getUserList(com.zte.m900.request.GetUserListRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[102]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getUserList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetUserListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetUserListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetUserListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.UpdateUserPasswordResponse updateUserPassword(com.zte.m900.request.UpdateUserPasswordRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[103]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "updateUserPassword"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.UpdateUserPasswordResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.UpdateUserPasswordResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.UpdateUserPasswordResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetDataConfServerConfigResponse getDataConfServerConfig(com.zte.m900.resquest.GetDataConfServerConfigRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[104]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getDataConfServerConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetDataConfServerConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetDataConfServerConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetDataConfServerConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.SendT140MessageResponse sendT140Message(com.zte.m900.request.SendT140MessageRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[105]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "sendT140Message"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.SendT140MessageResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.SendT140MessageResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.SendT140MessageResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetTerminalListResponse getTerList(com.zte.m900.request.GetTerListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[106]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getTerList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetTerminalListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetTerminalListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetTerminalListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetConfNodeListResponse getConfNodeList(com.zte.m900.request.GetConfNodeListRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[107]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getConfNodeList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetConfNodeListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetConfNodeListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetConfNodeListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVT100UserListResponse getVT100UserList(com.zte.m900.request.GetVT100UserListRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[108]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVT100UserList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVT100UserListResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVT100UserListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVT100UserListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.LoginResponse login(com.zte.m900.request.LoginRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[109]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "login"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.LoginResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.LoginResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.LoginResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.LogoutResponse logout(com.zte.m900.request.LogoutRequest req) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[110]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "logout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {req});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.LogoutResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.LogoutResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.LogoutResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetAddressBookResponse getAddressBook(com.zte.m900.request.GetAddressBookRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[111]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getAddressBook"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetAddressBookResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetAddressBookResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetAddressBookResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
    public com.zte.m900.response.GetVideoWallConfigResponse getVideoWallConfig(com.zte.m900.request.GetVideoWallConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[112]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVideoWallConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVideoWallConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVideoWallConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVideoWallConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.GetVideoWallPollConfigResponse getVideoWallPollConfig(com.zte.m900.request.GetVideoWallPollConfigRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[113]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "getVideoWallPollConfig"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.GetVideoWallPollConfigResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.GetVideoWallPollConfigResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.GetVideoWallPollConfigResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.VideoWallControlResponse videoWallControl(com.zte.m900.request.VideoWallControlRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[114]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "videoWallControl"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.VideoWallControlResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.VideoWallControlResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.VideoWallControlResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.zte.m900.response.VideoWallLoopControlResponse videoWallLoopControl(com.zte.m900.request.VideoWallLoopControlRequest request) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[115]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://m900.zte.com", "videoWallLoopControl"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {request});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.zte.m900.response.VideoWallLoopControlResponse) _resp;
            } catch (Exception _exception) {
                return (com.zte.m900.response.VideoWallLoopControlResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.zte.m900.response.VideoWallLoopControlResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
