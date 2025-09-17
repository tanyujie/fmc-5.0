package com.paradisecloud.fcm.telep.cache;

import com.paradisecloud.fcm.telep.cache.invoker.*;
import com.paradisecloud.fcm.telep.dao.model.BusiTele;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2022/10/11 14:16
 */
public class TelepBridge {


    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Object> params;

    public static final String HTTP = "http://";

    public static final String RPC = "RPC2";
    private String rootUrl;
    private BusiTele busiTele;
    private XmlRpcLocalRequest xmlRpcLocalRequest;


    private TeleConferenceApiInvoker teleConferenceApiInvoker;
    private TeleParticipantApiInvoker teleParticipantApiInvoker;
    private TemplateApiInvoker templateApiInvoker;
    private DeviceApiInvoker deviceApiInvoker;

    public TelepBridge(BusiTele busiTele) {
        this.busiTele = busiTele;
        init();
    }

    public void init() {
        this.params = new HashMap<>();
        this.rootUrl = HTTP + busiTele.getIp() + "/" + RPC;
        this.xmlRpcLocalRequest=new XmlRpcLocalRequest(rootUrl,busiTele.getAdminUsername(),busiTele.getAdminPassword());
        this.teleConferenceApiInvoker = new TeleConferenceApiInvoker(rootUrl,xmlRpcLocalRequest);
        this.teleParticipantApiInvoker=new TeleParticipantApiInvoker(rootUrl,xmlRpcLocalRequest);
        this.templateApiInvoker=new TemplateApiInvoker(rootUrl,xmlRpcLocalRequest);
        this.deviceApiInvoker=new DeviceApiInvoker(rootUrl,xmlRpcLocalRequest);
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public BusiTele getBusiTele() {
        return busiTele;
    }

    public void setBusiTele(BusiTele busiTele) {
        this.busiTele = busiTele;
    }

    public TeleConferenceApiInvoker getTeleConferenceApiInvoker() {
        return teleConferenceApiInvoker;
    }

    public void setTeleConferenceApiInvoker(TeleConferenceApiInvoker teleConferenceApiInvoker) {
        this.teleConferenceApiInvoker = teleConferenceApiInvoker;
    }

    public TeleParticipantApiInvoker getTeleParticipantApiInvoker() {
        return teleParticipantApiInvoker;
    }

    public void setTeleParticipantApiInvoker(TeleParticipantApiInvoker teleParticipantApiInvoker) {
        this.teleParticipantApiInvoker = teleParticipantApiInvoker;
    }

    public TemplateApiInvoker getTemplateApiInvoker() {
        return templateApiInvoker;
    }

    public void setTemplateApiInvoker(TemplateApiInvoker templateApiInvoker) {
        this.templateApiInvoker = templateApiInvoker;
    }

    public DeviceApiInvoker getDeviceApiInvoker() {
        return deviceApiInvoker;
    }

    public void setDeviceApiInvoker(DeviceApiInvoker deviceApiInvoker) {
        this.deviceApiInvoker = deviceApiInvoker;
    }
}
