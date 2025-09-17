package com.paradisecloud.fcm.web.controller.fs;

import java.util.List;

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
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PageDomain;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.core.page.TableSupport;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.terminal.fs.interfaces.IFreeSwitchUserProviderService;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * freeSwitchProviderController
 * 
 * @author zyz
 * @date 2021-08-17
 */
@RestController
@RequestMapping("/freeSwitchProvider/user")
@Tag(name = "freeSwitchProvider")
public class FreeSwitchUserProviderController extends BaseController
{
    @Autowired
    private IFreeSwitchUserProviderService freeSwitchUserProviderService;

    /**
     * 查询freeSwitch列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询freeSwitch列表")
    public RestResponse list(FreeSwitchUser freeSwitchUser)
    {
        startPage();
        List<FreeSwitchUser> list = freeSwitchUserProviderService.selectFreeSwitchUserList(freeSwitchUser);
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
     * 导出freeSwitch列表
     */
    @Log(title = "freeSwitch", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出freeSwitch列表")
    public RestResponse export(FreeSwitchUser freeSwitchUser)
    {
        List<FreeSwitchUser> list = freeSwitchUserProviderService.selectFreeSwitchUserList(freeSwitchUser);
        ExcelUtil<FreeSwitchUser> util = new ExcelUtil<FreeSwitchUser>(FreeSwitchUser.class);
        return util.exportExcel(list, "user");
    }

    /**
     * 获取freeSwitch详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取freeSwitch详细信息")
    public RestResponse getInfo(@PathVariable("id") String userId)
    {
        return RestResponse.success(freeSwitchUserProviderService.selectFreeSwitchUserById(userId));
    }

    /**
     * 新增freeSwitch
     */
    @Log(title = "freeSwitch", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增freeSwitch")
    public RestResponse add(@RequestBody FreeSwitchUser freeSwitchUser)
    {
        return toAjax(freeSwitchUserProviderService.insertFreeSwitchUser(freeSwitchUser));
    }

    /**
     * 修改freeSwitch
     */
    @Log(title = "freeSwitch", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改freeSwitch")
    public RestResponse edit(@RequestBody FreeSwitchUser freeSwitchUser)
    {
    	freeSwitchUserProviderService.updateFreeSwitchUser(freeSwitchUser);
        return success(freeSwitchUser);
    }

    /**
     * 删除freeSwitch
     */
    @Log(title = "freeSwitch", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
	@Operation(summary = "删除freeSwitch")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(freeSwitchUserProviderService.deleteFreeSwitchUserByIds(ids));
    }
    
    /**
     * 获取freeSwitch里所有的user信息
     */
	@PostMapping("/getFreeSwitchUser")
	@Operation(summary = "获取freeSwitch里所有的user信息")
    public RestResponse getFreeSwitchUser()
    {
        return toAjax(freeSwitchUserProviderService.getFreeSwitchUserInfo());
    }
	
	/**
     * 检查userId是否重复
     */
	@GetMapping("/examUserIdIsRepeat")
	@Operation(summary = "检查userId是否重复")
    public RestResponse examFreeSwitchUserIdIsRepeat(@RequestParam("userId") String userId)
    {
        return success(freeSwitchUserProviderService.userIdIsRepeat(userId));
    }
}
