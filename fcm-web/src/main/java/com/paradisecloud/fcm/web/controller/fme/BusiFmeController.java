package com.paradisecloud.fcm.web.controller.fme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.file.FileUtils;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import com.sinhy.utils.IOUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FME终端信息Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/fme")
@Tag(name = "FME基础配置控制层")
@Slf4j
public class BusiFmeController extends BaseController
{

    @Autowired
    private IFmeCacheService fmeCacheService;
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "获取FME桥列表（不分页）")
    @GetMapping("/list")
    public RestResponse list()
    {
        List<ModelBean> busiFmes = new ArrayList<>();
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridges();
        for (FmeBridge fmeBridge : fbs)
        {
            ModelBean mb = new ModelBean(fmeBridge.getBusiFme());
            mb.remove("password");
            mb.remove("adminPassword");
            mb.put("bindDeptCount", DeptFmeMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiFme().getId()));
            
            // websocket断开次数
            mb.put("webSocketBreakCount", fmeBridge.getWebSocketBreakCount());
            
            // 连接创建成功的数量，理论只能存在一个
            mb.put("websocketConnections", fmeBridge.getWsAuthTokens());
            
            // 参会者数
            mb.put("participantCount", fmeBridge.getDataCache().getParticipantCount());
            
            // 活跃会议数
            mb.put("callCount", fmeBridge.getDataCache().getCallCount());
            
            // 会议号数
            mb.put("coSpaceCount", fmeBridge.getDataCache().getCoSpaceCount());
            
            // 自上次断开后的重试次数
            mb.put("websocketConnectionTryTimesSinceLastDisconnected", fmeBridge.getWebsocketConnectionTryTimesSinceLastDisconnected());
            
            // 首次建立连接时间
            mb.put("firstConnectedTime", fmeBridge.getFirstConnectedTime());
            
            // 最后一次建立连接时间
            mb.put("lastConnectedTime", fmeBridge.getLastConnectedTime());
            
            // 最后一次连接断开时间
            mb.put("lastDisConnectedTime", fmeBridge.getLastDisConnectedTime());
            mb.put("fmeStatus", fmeBridge.getDataCache().getSystemStatus());
            
            // 连接失败原因
            mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());
            busiFmes.add(mb);
        }
        return success(busiFmes);
    }
    
    @Operation(summary = "单个会议桥MTU设置")
    @PostMapping("/mtuSettings")
    public RestResponse mtuSettings(@RequestBody JSONObject params)
    {
        Assert.isTrue(params.containsKey("mtuValue"), "请输入mtu值");
        fmeCacheService.mtuSettings(params.getLongValue("fmeId"), params.getIntValue("mtuValue"));
        return success(params);
    }

    @Operation(summary = "ping终端IP返回消息")
    @GetMapping("/pingIp")
    public RestResponse pingIp(@PathParam("ip") String ip,@PathParam("id") long id){
        return success(fmeCacheService.pingIp(ip, id));
    }
    
    @PostMapping("/detailedTracing")
    public RestResponse detailedTracing(@RequestBody JSONObject body)
    {
        Assert.isTrue(body.containsKey("fmeId"), "fmeId不能为空！");
        Assert.isTrue(body.containsKey("params"), "params不能为空！");
        
        // 参数
        JSONObject params = body.getJSONObject("params");
        params.put("key", "undefined");
        
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (Entry<String, Object> e : params.entrySet())
        {
            nameValuePairList.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }
        
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(body.getLongValue("fmeId"));
        fmeBridge.getFmeBackgroundInvoker().detailedTracing(nameValuePairList);
        return RestResponse.success();
    }

    @PostMapping("/detailedH264chp")
    public RestResponse detailedH264chp(@RequestBody JSONObject body)
    {
        Assert.isTrue(body.containsKey("fmeId"), "fmeId不能为空！");
        Assert.isTrue(body.containsKey("params"), "params不能为空！");
        JSONObject result=new JSONObject();

        // 参数
        JSONObject params = body.getJSONObject("params");
        if(params==null){
            params.put("cmd", "allow_outgoing_h264chp");
        }
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (Entry<String, Object> e : params.entrySet())
        {
            nameValuePairList.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(body.getLongValue("fmeId"));
        StringBuilder stringBuilder = fmeBridge.getFmeBackgroundInvoker().detailedH264chp(nameValuePairList);
        JSONObject jsonObject = JSON.parseObject(stringBuilder.toString());
        log.info("####################################"+jsonObject.toJSONString());
        Object data = jsonObject.get("data");
        if(data instanceof JSONObject){
            JSONObject jsonObject_data=(JSONObject) data;
            JSONArray dataTable = (JSONArray)jsonObject_data.get("dataTable");
            Object data_t_0 = dataTable.get(0);
            JSONObject data_t_0_json=(JSONObject)data_t_0;
            JSONObject records =  (JSONObject)data_t_0_json.get("records");
            JSONObject record = (JSONObject) records.get("record");
            Object datum = record.get("datum");
            if(datum instanceof JSONArray){
                JSONArray jsonArray=(JSONArray) datum;
                Object datum_first= jsonArray.get(0);
                JSONObject datum_firt_obj = (JSONObject) datum_first;
                Object datum_last= jsonArray.get(1);
                JSONObject datum_last_obj = (JSONObject) datum_last;
                Object value = datum_last_obj.get("value");
                Object firstValue = datum_firt_obj.get("value");
                String firstStr = firstValue.toString();
                String s = value.toString();
                if(s.contains("currently on")){
                    result.put("allow_outgoing_h264chp","on");
                }else if(s.contains("currently off")){
                    result.put("allow_outgoing_h264chp","off");
                }else if(s.contains("ok")){
                    if(firstStr.contains("on")){
                        result.put("allow_outgoing_h264chp","on");
                    }
                    if(firstStr.contains("off")){
                        result.put("allow_outgoing_h264chp","off");
                    }
                }
            }
        }

        return RestResponse.success(result);
    }

    
    @GetMapping("/detailedTracing")
    public RestResponse getDetailedTracing(Long fmeId)
    {
        Assert.isTrue(fmeId != null, "fmeId不能为空！");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        return RestResponse.success(JSON.parseObject(fmeBridge.getFmeBackgroundInvoker().detailedTracing().toString()));
    }
    
    @PostMapping("/downloadFmeLog")
    public void downloadFmeLog(@RequestBody JSONObject params, HttpServletResponse response) throws Exception
    {
        Assert.isTrue(params.containsKey("fmeId"), "请传入fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(params.getLongValue("fmeId"));
        StringBuilder content = fmeBridge.getFmeBackgroundInvoker().downloadEventLog();
        String realFileName = fmeBridge.getBusiFme().getIp() + "-" + fmeBridge.getBusiFme().getName() + ".txt";
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, realFileName);
        IOUtils.copy(content.toString(), response.getOutputStream());
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥[bridgeHost]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥记录新增：记录的属性和属性值放到请求body中封装为json格式")
    @PostMapping("")
    public RestResponse saveFme(@RequestBody BusiFme busiFme)
    {
        fmeCacheService.addFme(busiFme);
        return success(busiFme);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处")
    @DeleteMapping("/{id}")
    public RestResponse deleteFme(@PathVariable Long id)
    {
        fmeCacheService.deleteFme(id);
        return success("Delete Entity successfully, id: " + id);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式")
    @PutMapping("/{id}")
    public RestResponse updateFme(@RequestBody BusiFme busiFme, @PathVariable Long id)
    {
        busiFme.setId(id);
        fmeCacheService.updateFme(busiFme);
        return success(busiFme);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个会议桥记录：id放到rest地址上占位符处")
    @GetMapping("/{id}")
    public RestResponse getFme(@PathVariable Long id)
    {
        FmeBridge fmeHttpInvoker = FmeBridgeCache.getInstance().get(id);
        if (fmeHttpInvoker != null)
        {
            return success(fmeHttpInvoker.getBusiFme());
        }
        return success(null);
    }
}
