//package com.paradisecloud.fcm.web.controller.mqtt;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.github.pagehelper.PageInfo;
//import com.paradisecloud.common.annotation.Log;
//import com.paradisecloud.common.core.controller.BaseController;
//import com.paradisecloud.common.core.model.RestResponse;
//import com.paradisecloud.common.core.page.PageDomain;
//import com.paradisecloud.common.core.page.PaginationData;
//import com.paradisecloud.common.core.page.TableSupport;
//import com.paradisecloud.common.enums.BusinessType;
//import com.paradisecloud.fcm.mqtt.interfaces.IFreeSwitchUserService;
//import com.paradisecloud.fcm.mqtt.model.FreeSwitchUser;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
///**
// * freeSwitchController
// * 
// * @author zyz
// * @date 2021-08-17
// */
//@RestController
//@RequestMapping("/freeSwitch/user")
//@Tag(name = "freeSwitch")
//public class FreeSwitchUserController extends BaseController
//{
//    @Autowired
//    private IFreeSwitchUserService freeSwitchUserService;
//
//    /**
//     * 查询freeSwitch列表
//     */
//    @GetMapping("/list")
//    @Operation(summary = "查询freeSwitch列表")
//    public RestResponse list(FreeSwitchUser freeSwitchUser)
//    {
////        startPage();
//        List<FreeSwitchUser> list = freeSwitchUserService.selectFreeSwitchUserList(freeSwitchUser);
//        
//        PageDomain pageDomain = TableSupport.buildPageRequest();
//        Integer pageNum = pageDomain.getPageNum();
//        Integer pageSize = pageDomain.getPageSize();
//        
//        //从第几条数据开始
//        int firstIndex = (pageNum - 1) * pageSize;
//        
//        //到第几条数据结束
//        int lastIndex = pageNum * pageSize;
//        
//        long total = new PageInfo<>(list).getTotal();
//        PaginationData<Object> pd = new PaginationData<>();
//        pd.setTotal(total);
//        if(lastIndex > total) {
//        	lastIndex = (int) total;
//        }
//        List<FreeSwitchUser> subList = list.subList(firstIndex, lastIndex);
//        for (Object object : subList)
//        {
//            pd.addRecord(object);
//        }
//        return RestResponse.success(0, "查询成功", pd);
//    }
//
//    /**
//     * 获取freeSwitch详细信息
//     */
//    @GetMapping(value = "/{userId}/{deptId}")
//    @Operation(summary = "获取freeSwitch详细信息")
//    public RestResponse getInfo(@PathVariable("userId") Long userId, @PathVariable("deptId") Long deptId)
//    {
//        return RestResponse.success(freeSwitchUserService.selectFreeSwitchUserById(userId, deptId));
//    }
//
//    /**
//     * 新增freeSwitch
//     */
//    @Log(title = "freeSwitch", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Operation(summary = "新增freeSwitch")
//    public RestResponse add(@RequestBody FreeSwitchUser freeSwitchUser)
//    {
//        return toAjax(freeSwitchUserService.insertFreeSwitchUser(freeSwitchUser));
//    }
//
//    /**
//     * 修改freeSwitch
//     */
//    @Log(title = "freeSwitch", businessType = BusinessType.UPDATE)
//    @PutMapping
//    @Operation(summary = "修改freeSwitch")
//    public RestResponse edit(@RequestBody FreeSwitchUser freeSwitchUser)
//    {
//    	freeSwitchUserService.updateFreeSwitchUser(freeSwitchUser);
//        return success(freeSwitchUser);
//    }
//
//    /**
//     * 删除freeSwitch
//     */
//    @Log(title = "freeSwitch", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}/{deptId}")
//	@Operation(summary = "删除freeSwitch")
//    public RestResponse remove(@PathVariable Long[] ids, @PathVariable("deptId") Long deptId)
//    {
//        return toAjax(freeSwitchUserService.deleteFreeSwitchUserByIds(ids, deptId));
//    }
//    
//    /**
//     * 获取freeSwitch里所有的user信息
//     */
//	@PostMapping("/getFreeSwitchUser")
//	@Operation(summary = "获取freeSwitch里所有的user信息")
//    public List<FreeSwitchUser> getFreeSwitchUser(@PathVariable("deptId") Long deptId)
//    {
//        return freeSwitchUserService.getFreeSwitchUserInfo(deptId);
//    }
//	
//	/**
//     * 检查userId是否重复
//     */
//	@PostMapping("/examUserIdIsRepeat")
//	@Operation(summary = "检查userId是否重复")
//    public RestResponse examFreeSwitchUserIdIsRepeat(@RequestBody FreeSwitchUser freeSwitchUser)
//    {
//        return success(freeSwitchUserService.userIdIsRepeat(freeSwitchUser));
//    }
//}
