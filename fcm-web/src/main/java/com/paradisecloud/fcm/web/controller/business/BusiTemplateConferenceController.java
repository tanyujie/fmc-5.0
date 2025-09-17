package com.paradisecloud.fcm.web.controller.business;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTemplateDept;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 会议模板Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
//@RestController
@RequestMapping("/busi/templateConference")
@Tag(name = "会议模板")
public class BusiTemplateConferenceController extends BaseController
{
    @Autowired
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    
    @Autowired
    private ITemplateConferenceStartService templateConferenceStartService;

    /**
     * 查询会议模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list(BusiTemplateConference busiTemplateConference)
    {
        startPage();
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        List<BusiTemplateConference> list = busiTemplateConferenceService.selectBusiTemplateConferenceList(busiTemplateConference);
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(new PageInfo<>(list).getTotal());
        List<ModelBean> mbs = busiTemplateConferenceService.toModelBean(list);
        for (ModelBean mb : mbs)
        {
            pd.addRecord(mb);
        }
        return RestResponse.success(0, "查询成功", pd);
    }


    /**
     * 查询会议模板列表
     */
    @GetMapping("/list/searchkey")
    @Operation(summary = "通过关键字查询会议模板列表")
    public RestResponse listKeySearch(@RequestParam("searchKey")String searchKey,
                                      @RequestParam(value = "deptId",required = false)Long deptId,
                                      @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                      @RequestParam(value="pageSize",defaultValue = "10") int pageSize)
    {
        PageHelper.startPage(pageIndex, pageSize);
        Page<BusiTemplateConference> page = busiTemplateConferenceService.selectBusiTemplateConferenceList(searchKey,deptId);
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(new PageInfo<>(page.getResult()).getTotal());
        List<ModelBean> mbs = busiTemplateConferenceService.toModelBean(page.getResult());
        for (ModelBean mb : mbs)
        {
            pd.addRecord(mb);
        }
        return RestResponse.success(0, "查询成功", pd);
    }


    /**
     * 根据部门查询会议模板列表
     */
    @GetMapping("/list/{deptId}/{businessFieldType}")
    @Operation(summary = "根据部门查询会议模板列表")
    public RestResponse list(@PathVariable("deptId") Long deptId, @PathVariable("businessFieldType") Integer businessFieldType)
    {
        Assert.notNull(deptId, "部门ID不能为空");
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        busiTemplateConference.setDeptId(deptId);
        busiTemplateConference.setBusinessFieldType(businessFieldType);
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        List<BusiTemplateConference> list = busiTemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);
        return RestResponse.success(0, "查询成功", list);
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(id));
    }
    
    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(templateConferenceStartService.buildTemplateConferenceContext(id));
    }
    
    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptTemplateCount/{businessFieldType}")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getDeptTemplateCount(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        return RestResponse.success(busiTemplateConferenceService.getDeptTemplateCount(businessFieldType));
    }
    
    /**
     * 获取会议模板的默认视图配置信息
     * @author lilinhai
     * @since 2021-04-08 15:09 
     * @return RestResponse
     */
    @PutMapping(value = "/updateDefaultViewConfigInfo/{id}")
    @Operation(summary = "修改会议模板的默认视图配置信息")
    public RestResponse updateDefaultViewConfigInfo(@RequestBody JSONObject jsonObj, @PathVariable("id") Long id)
    {
        busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
        return RestResponse.success();
    }

    /**
     * 新增会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增会议模板")
    public RestResponse add(@RequestBody JSONObject jsonObj)
    {
        JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
        Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
        Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
        BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
        List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
        if (busiTemplateParticipantArr != null)
        {
            for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
            {
                BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                busiTemplateParticipants.add(busiTemplateParticipant);
            }
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
        List<BusiTemplateDept> templateDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            BusiTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiTemplateDept.class);
            Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
            Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
            templateDepts.add(busiTemplateDept);
        }

        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
            busiTemplateConference.setStreamUrl(null);
        }
        int c = busiTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
        if (c > 0)
        {
            return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改会议模板")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @PathVariable("id") Long id)
    {
        JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
        Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
        Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
        BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);
        
        busiTemplateConference.setId(id);
        
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
        List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
        if (busiTemplateParticipantArr != null)
        {
            for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
            {
                BusiTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                p.setId(null);
                Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                busiTemplateParticipants.add(p);
            }
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
        List<BusiTemplateDept> templateDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
        }
        
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        int c = busiTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
        if (c > 0)
        {
            return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
        }
        else 
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除会议模板")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiTemplateConferenceService.deleteBusiTemplateConferenceById(id));
    }
}
