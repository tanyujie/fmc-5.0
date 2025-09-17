package com.paradisecloud.fcm.web.controller.fs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PageDomain;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.core.page.TableSupport;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchService;
import com.paradisecloud.fcm.terminal.fs.model.BusiFcmConfig;
import com.paradisecloud.fcm.terminal.fs.model.FcmServerConfig;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.paradisecloud.fcm.terminal.fs.model.NfsConfig;
import com.paradisecloud.fcm.terminal.fs.model.TurnServerConf;
import com.paradisecloud.fcm.terminal.fs.model.XmlEntity;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.websocket.server.PathParam;

/**
 * 服务器资源信息  Controller
 * 
 * @author zyz
 * @date 2021-09-02
 */
@RestController
@RequestMapping("/freeSwitch/switch")
@Tag(name = "服务器资源信息")
public class BusiFreeSwitchController extends BaseController
{
    @Autowired
    private IBusiFreeSwitchService busiFreeSwitchService;

    /**
     * 查询服务器资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询服务器资源列表")
    public RestResponse list(BusiFreeSwitch busiFreeSwitch)
    {
        startPage();
        List<BusiFreeSwitch> list = busiFreeSwitchService.selectBusiFreeSwitchList(busiFreeSwitch);
        for (BusiFreeSwitch busiFreeSwitchTemp : list) {
            busiFreeSwitchTemp.setPassword(null);
        }
        return getDataTable(list);
    }
    
    /**
     * 查询服务器资源列表
     */
    @GetMapping("/severInfo/list")
    @Operation(summary = "查询服务器资源列表")
    public RestResponse freeSwitchList(BusiFreeSwitch busiFreeSwitch)
    {
        List<ModelBean> freeSwitchBenas = busiFreeSwitchService.getServerConfigurationInfo();
        for (ModelBean modelBean : freeSwitchBenas) {
            modelBean.remove("password");
        }
        return success(freeSwitchBenas);
    }

    /**
     * 导出服务器资源信息 列表
     */
    @GetMapping("/export")
    @Operation(summary = "导出服务器资源信息 列表")
    public RestResponse export(BusiFreeSwitch busiFreeSwitch)
    {
        List<BusiFreeSwitch> list = busiFreeSwitchService.selectBusiFreeSwitchList(busiFreeSwitch);
        ExcelUtil<BusiFreeSwitch> util = new ExcelUtil<BusiFreeSwitch>(BusiFreeSwitch.class);
        return util.exportExcel(list, "switch");
    }

    /**
     * 获取服务器资源信息 详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取服务器资源详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFreeSwitchService.selectBusiFreeSwitchById(id));
    }

    /**
     * 新增服务器资源信息 
     */
    @PostMapping
    @Operation(summary = "新增服务器资源信息")
    public RestResponse add(@RequestBody BusiFreeSwitch busiFreeSwitch)
    {
        return toAjax(busiFreeSwitchService.insertBusiFreeSwitch(busiFreeSwitch));
    }

    /**
     * 修改服务器资源信息 
     */
    @PutMapping
    @Operation(summary = "修改服务器资源信息 ")
    public RestResponse edit(@RequestBody BusiFreeSwitch busiFreeSwitch)
    {
        return toAjax(busiFreeSwitchService.updateBusiFreeSwitch(busiFreeSwitch));
    }

    /**
     * 删除服务器资源信息 
     */
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除服务器资源信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiFreeSwitchService.deleteBusiFreeSwitchByIds(ids));
    }
	
	/**
     * 获取fcm配置文件的信息(根据部门id)
     */
    @PostMapping("/fcmConfigFileInfo")
    @Operation(summary = "获取fcm配置文件的信息")
    public Map<String, Object> getFcmConfigFileInfo(@RequestBody BusiFreeSwitchDept busiFreeSwitchDept)
    {
        return busiFreeSwitchService.getFcmConfigFileInfo(busiFreeSwitchDept);
    }
    
    
    /**
     * 获取fcm配置文件的信息 (根据id) 
     */
    @PostMapping("/fcmConfigInfo")
    @Operation(summary = "获取fcm配置文件的信息")
    public Map<String, Object> getFcmConfigInfo(@RequestBody BusiFcmConfig busiFcmConfig)
    {
        return busiFreeSwitchService.getFcmConfigInfoById(busiFcmConfig);
    }
    
    /**
     * 修改服务器上的文件和保存上传文件
     */
    @PostMapping("/updateServerData")
	@Operation(summary = "修改服务器上的文件和保存上传文件")
    public RestResponse updateServerFcmConfigInfo(@RequestBody BusiFcmConfig busiFcmConfig)
    {
        return toAjax(busiFreeSwitchService.operateFcmConfig(busiFcmConfig));
    }
    
    /**
	 	* 获取turnserver.conf配置文件的信息 (根据租户id)
	 */
	@PostMapping("/turnServerInfo")
	@Operation(summary = "获取turnserver.conf配置文件的信息")
	public Map<String,Object> getTurnServerInfo(@RequestBody BusiFreeSwitchDept busiFreeSwitchDept)
	{
	    return busiFreeSwitchService.getCutrnConfigData(busiFreeSwitchDept);
	}
	
	 /**
	 	* 获取turnserver.conf配置文件的信息 (根据id)
	 */
	@PostMapping("/turnServerConfigInfo")
	@Operation(summary = "获取turnserver.conf配置文件的信息")
	public Map<String,Object> getTurnServerConfigInfo(@RequestBody BusiFcmConfig busiFcmConfig)
	{
	    return busiFreeSwitchService.getCutrnConfigDataById(busiFcmConfig);
	}
	
	@PostMapping("/getFsOnlineUser")
	@Operation(summary = "获取fs的在线用户")
	public Map<String, TerminalOnlineStatus> getFsOnlineUserMaps(@RequestBody BusiFcmConfig busiFcmConfig)
	{
	    return busiFreeSwitchService.getFsOnlineUser(busiFcmConfig.getId());
	}
	
	/**
	 	* 更新turnserver.conf配置文件的信息
	 */
	@PutMapping("/updateTurnServerInfo")
	@Operation(summary = "更新turnserver.conf配置文件的信息")
	public RestResponse updateTurnServerInfo(@RequestBody TurnServerConf turnServerConf)
	{
	    return toAjax(busiFreeSwitchService.updateCutrnConfigData(turnServerConf));
	}
	
	/**
     * 获取default.xml的数据
     */
    @GetMapping("/getXmlData")
	@Operation(summary = "获取default.xml的数据")
    public String getServerDefaultOrPullicXmlData(@RequestParam("xmlType") String xmlType, @RequestParam("id") Long id)
    {
        return busiFreeSwitchService.getDefaultPullicXml(xmlType, id);
    }
    
    /**
     * 获取default.xml的数据
     */
    @PutMapping("/updateXmlUploadServer")
	@Operation(summary = "更新default.xml、public.xml、distributor.conf.xml的数据")
    public RestResponse XmlUploadServer(@RequestBody XmlEntity xmlEntity)
    {
        return toAjax(busiFreeSwitchService.uploadXmlFileStr(xmlEntity));
    }
    
    /**
 	* 更新turnserver.conf配置文件的信息
	*/
	@PutMapping("/sendNfsConfig")
	@Operation(summary = "发送nfs配置文件的信息")
	public RestResponse sendNfsConfig(@RequestBody NfsConfig nfsConfig)
	{
	    return toAjax(busiFreeSwitchService.sendNfsConfigCommand(nfsConfig));
	}
	
	/**
 	* 重新启动freeSwitch
	*/
	@PostMapping("/restartFreeSwitch")
	@Operation(summary = "重新启动freeSwitch")
	public RestResponse restartFreeSwitchServer(@RequestBody FreeSwitchUser freeSwitchUser)
	{
	    return toAjax(busiFreeSwitchService.restartFreeSwitchServer(freeSwitchUser.getId()));
	}
	
	/**
 	* 监测freeSwitch服务是都启动成功
	*/
	@PostMapping("/restartFreeSwitchIsSuccess")
	@Operation(summary = "重新启动freeSwitch")
	public Boolean FreeSwitchServerIsSuccess(@RequestBody FreeSwitchUser freeSwitchUser)
	{
	    return busiFreeSwitchService.restartFreeSwitchListen(freeSwitchUser.getId());
	}
	
	/**
 	* 获取freeSwitch下的用户
	*/
	@PostMapping("/getFreeSwitchAllUser")
	@Operation(summary = "获取freeSwitch下的用户")
	public RestResponse loadFreeSwitchAllUser(@RequestBody BusiFcmConfig busiFcmConfig)
	{
        List<FreeSwitchUser> list = busiFreeSwitchService.freeSwitchAllUser(busiFcmConfig.getDeptId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        
        //从第几条数据开始
        int firstIndex = (pageNum - 1) * pageSize;
        
        //到第几条数据结束
        int lastIndex = pageNum * pageSize;
        
        long total = new PageInfo<>(list).getTotal();
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(total);
        if(lastIndex > total) {
        	lastIndex = (int) total;
        }
        List<FreeSwitchUser> subList = list.subList(firstIndex, lastIndex);
        for (Object object : subList)
        {
            pd.addRecord(object);
        }
        return RestResponse.success(0, "查询成功", pd);
	}
	
	/**
     * 查询Fcm服务器时间
     * @return
     */
	@GetMapping("/getFcmDate")
	@Operation(summary = "获取服务器的时间")
	public RestResponse getFcmDate(@RequestParam Long Id){
		if (busiFreeSwitchService.getFcmServerDate(Id) != null){
	        return RestResponse.success(busiFreeSwitchService.getFcmServerDate(Id));
	    }else {
	    	return RestResponse.fail("请检查服务的配置信息！");
		}
	}

    /**
     * 在 fcm中ping终端Ip
     * @param ip
     * @param id
     * @return
     */
    @Operation(summary = "ping终端IP返回消息")
    @GetMapping("/pingIp")
    public RestResponse pingIp(@PathParam("ip") String ip, @PathParam("id") long id){
        return success(busiFreeSwitchService.pingIp(ip, id));
    }
}
