package com.paradisecloud.fcm.web.controller.smc3;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.smc3.busi.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateParticipant;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议模板Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/smc3/templateConference")
@Tag(name = "会议模板")
public class BusiMcuSmc3TemplateConferenceController extends BaseController
{
    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;


    /**
     * 查询会议模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list(BusiMcuSmc3TemplateConference busiTemplateConference)
    {
        startPage();
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        List<BusiMcuSmc3TemplateConference> list = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceList(busiTemplateConference);
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(new PageInfo<>(list).getTotal());
        List<ModelBean> mbs = busiMcuSmc3TemplateConferenceService.toModelBean(list);
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
        Page<BusiMcuSmc3TemplateConference> page = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceList(searchKey,deptId);
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(new PageInfo<>(page.getResult()).getTotal());
        List<ModelBean> mbs = busiMcuSmc3TemplateConferenceService.toModelBean(page.getResult());
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
        BusiMcuSmc3TemplateConference busiTemplateConference = new BusiMcuSmc3TemplateConference();
        busiTemplateConference.setDeptId(deptId);
        busiTemplateConference.setBusinessFieldType(businessFieldType);
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        List<BusiMcuSmc3TemplateConference> list = busiMcuSmc3TemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);
        return RestResponse.success(0, "查询成功", list);
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(id));
    }
    
    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(new BuildTemplateConferenceContext().buildTemplateConferenceContext(id));
    }
    
    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptTemplateCount/{businessFieldType}")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getDeptTemplateCount(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        return RestResponse.success(busiMcuSmc3TemplateConferenceService.getDeptTemplateCount(businessFieldType));
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
        busiMcuSmc3TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
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
        BusiMcuSmc3TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc3TemplateConference.class);
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
        List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
        if (busiTemplateParticipantArr != null)
        {
            for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
            {
                BusiMcuSmc3TemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                busiTemplateParticipants.add(busiTemplateParticipant);
            }
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
        List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            BusiMcuSmc3TemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class);
            Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
            Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
            templateDepts.add(busiTemplateDept);
        }

        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
            busiTemplateConference.setStreamUrl(null);
        }
        int c = busiMcuSmc3TemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
        if (c > 0)
        {
            return RestResponse.success(busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
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
        BusiMcuSmc3TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc3TemplateConference.class);
        
        busiTemplateConference.setId(id);
        
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
        List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
        if (busiTemplateParticipantArr != null)
        {
            for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
            {
                BusiMcuSmc3TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                p.setId(null);
                Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                busiTemplateParticipants.add(p);
            }
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
        List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class));
        }
        
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        int c = busiMcuSmc3TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
        if (c > 0)
        {
            return RestResponse.success(busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
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
        return toAjax(busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(id));
    }
}
