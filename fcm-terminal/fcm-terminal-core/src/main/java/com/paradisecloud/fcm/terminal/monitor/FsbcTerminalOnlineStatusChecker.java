/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FsbcTerminalOnlineStatusChecker.java
 * Package     : com.paradisecloud.fcm.terminal.monitor
 * @author sinhy 
 * @since 2021-11-11 11:01
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.utils.DateUtils;
import com.sinhy.utils.RegExpUtils;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>FSBC终端在线状态检测器</pre>
 * @author sinhy
 * @since 2021-11-11 11:01
 * @version V1.0  
 */
public class FsbcTerminalOnlineStatusChecker
{
    private static final Pattern TRANSPORT_PATTERN = Pattern.compile("transport=\\w+");
    private CountDownLatch latch;
    private Map<Long, FsbcBridge> fsbcBridgeMap;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-11-11 11:02 
     * @param fsbcBridgeMap
     * @param latch 
     */
    public FsbcTerminalOnlineStatusChecker(Map<Long, FsbcBridge> fsbcBridgeMap, CountDownLatch latch)
    {
        super();
        this.fsbcBridgeMap = fsbcBridgeMap;
        this.latch = latch;
    }
    
    public void check()
    {
        fsbcBridgeMap.forEach((fsbcServerId, fsbcBridge) -> {
            OnlineStatusMonitor.getInstance().getFixedThreadPool().execute(() -> {
                ThreadUtils.sleep(10);
                try
                {
                    Document doc = fsbcBridge.getRegistrationInvoker().getRegistrations();
                    fsbcBridge.getBusiFsbcRegistrationServer().getParams().put("onlineStatus", doc == null ? TerminalOnlineStatus.OFFLINE.getValue() : TerminalOnlineStatus.ONLINE.getValue());
                    if (doc == null)
                    {
                        fsbcBridge.getFsbcLogger().logInfo("获取FSBC注册信息失败", true, true);;
                    }
                    else
                    {
                        Map<String, BusiTerminal> m = TerminalCache.getInstance().getFsbcTerminalsMap().get(fsbcServerId);
                        if (m != null && !m.isEmpty())
                        {
                            Map<String, JSONObject> jsonMap = new HashMap<>();
                            List<Element> ns = doc.getRootElement().element("Registrations").elements("Registration");
                            if (!ObjectUtils.isEmpty(ns))
                            {
                                for (Element element : ns)
                                {
                                    JSONObject jsonObj = parse(fsbcBridge, element);
                                    if (jsonObj.getString("credential") != null)
                                    {
                                        jsonMap.put(jsonObj.getString("credential"), jsonObj);
                                    }
                                    else
                                    {
                                        fsbcBridge.getFsbcLogger().logInfo("无法解析账号信息：" + element, true, true);
                                    }
                                }
                            }
                            
                            m.forEach((credential, terminal) -> {
                                try
                                {
                                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                    JSONObject json = jsonMap.get(credential);
                                    if (json != null)
                                    {
                                        terminal.setRegistrationTime(json.getDate("registrationTime"));
                                        terminal.setVendor(json.getString("vendor"));
                                        terminal.setProtocol(json.getString("protocol"));
                                        terminal.setIp(fsbcBridge.getBusiFsbcRegistrationServer().getCallIp());
                                        terminal.setIntranetIp(json.getString("intranetIp"));
                                        terminal.setPort(json.getInteger("port"));
                                        terminal.setTransport(json.getString("transport"));
                                        OnlineStatusMonitor.getInstance().processTerminalInfo(terminal, oldStatus, TerminalOnlineStatus.ONLINE);
                                    }
                                    else
                                    {
                                        OnlineStatusMonitor.getInstance().processTerminalInfo(terminal, oldStatus, TerminalOnlineStatus.OFFLINE);
                                    }
                                }
                                catch (Throwable e)
                                {
                                    fsbcBridge.getFsbcLogger().logInfo("解析账号信息入库失败", true, e);
                                }
                            });
                        }
                    }
                }
                catch (Throwable e)
                {
                    fsbcBridge.getFsbcLogger().logInfo("获取并解析FSBC注册信息失败", true, e);
                }
                finally
                {
                    latch.countDown();
                    fsbcBridge.getFsbcLogger().logInfo("FSBC本轮监听已结束", true, false);
                }
            });
        });
    }
    
    private JSONObject parse(FsbcBridge fsbcBridge, Element element)
    {
        JSONObject jsonObj = new JSONObject();
        String vendor = element.elementText("VendorInfo");
        String protocol = element.elementText("Protocol");
        String registrationTime = element.elementText("CreationTime");
        jsonObj.put("vendor", vendor);
        jsonObj.put("protocol", protocol);
        jsonObj.put("registrationTime", DateUtils.convertToDate(registrationTime));
        
        Element protocolElement = element.element(protocol);
        if (protocolElement != null)
        {
            if (protocolElement.getName().equals("H323"))
            {
                Element aliasesElement = protocolElement.element("Aliases");
                if (aliasesElement != null)
                {
                    List<Element> aliasesEls = aliasesElement.elements("Alias");
                    if (!ObjectUtils.isEmpty(aliasesEls))
                    {
                        for (Element aliasEl : aliasesEls)
                        {
                            String type = aliasEl.elementText("Type");
                            String credential = aliasEl.elementText("Value");
                            if (type.equals("E164"))
                            {
                                jsonObj.put("credential", credential);
                                break;
                            }
                        }
                    }
                }
                
                Element callSignalAddressesElement = protocolElement.element("CallSignalAddresses");
                if (callSignalAddressesElement != null)
                {
                    String addressInfo = callSignalAddressesElement.elementText("Address");
                    if (addressInfo != null)
                    {
                        jsonObj.put("intranetIp", RegExpUtils.extractIP(addressInfo));
                        
                        String portStr = addressInfo.substring(addressInfo.lastIndexOf(':') + 1, addressInfo.length());
                        if (!ObjectUtils.isEmpty(portStr))
                        {
                            int port = Integer.parseInt(portStr);
                            jsonObj.put("port", port);
                        }
                    }
                }
            }
            else if (protocolElement.getName().equals("SIP"))
            {
                String contact = protocolElement.elementText("Contact");
                if (contact != null)
                {
                    String credentialInfo = contact.replaceAll("^sips", "").replaceAll("^sip", "");
                    String credential = credentialInfo.substring(1, credentialInfo.indexOf('@'));
                    
                    jsonObj.put("credential", credential);
                    
                    String intranetIp = RegExpUtils.extractIP(contact);
                    jsonObj.put("intranetIp", intranetIp);
                    
                    String portStr = contact.substring(contact.indexOf(intranetIp) + intranetIp.length());
                    int port = 0;
                    int portStrEndIndex = portStr.indexOf(';');
                    if (portStrEndIndex == -1)
                    {
                        port = Integer.parseInt(portStr.substring(1));
                    }
                    else
                    {
                        port = Integer.parseInt(portStr.substring(1, portStrEndIndex));
                        try {
                            String transport = RegExpUtils.extractContent(contact, TRANSPORT_PATTERN).replaceAll("^transport=", "");
                            jsonObj.put("transport", transport.toUpperCase(Locale.ENGLISH));
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                    jsonObj.put("port", port);
                }
            }
            else
            {
                fsbcBridge.getFsbcLogger().logInfo("非法协议类型：" + protocol, true, true);
            }
        }
        return jsonObj;
    }
}
