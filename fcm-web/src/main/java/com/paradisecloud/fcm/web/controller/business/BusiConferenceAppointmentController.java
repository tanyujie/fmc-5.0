package com.paradisecloud.fcm.web.controller.business;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;

/**
 * 会议预约记录Controller
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
//@RestController
@RequestMapping("/busi/conferenceAppointment")
@Tag(name = "会议预约记录")
public class BusiConferenceAppointmentController extends BaseController
{
    @Autowired
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptRecordCounts/{businessFieldType}")
    @Operation(summary = "获取部门预约会议计数")
    public RestResponse getDeptRecordCounts(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        return RestResponse.success(busiConferenceAppointmentService.getDeptRecordCounts(businessFieldType));
    }
    
    /**
     * 查询会议预约记录列表
     */
    @PostMapping(value = "/list")
    @Operation(summary = "查询会议预约记录列表")
    public RestResponse list(@RequestBody BusiConferenceAppointment busiConferenceAppointment)
    {
        startPage();
        List<BusiConferenceAppointment> list = busiConferenceAppointmentService.selectBusiConferenceAppointmentList(busiConferenceAppointment);
        return getDataTable(list);
    }

    /**
     * 通过关键字查询预约会议列表
     */
    @GetMapping("/list/searchkey")
    @Operation(summary = "通过关键字查询预约会议列表")
    public RestResponse listKeySearch(@RequestParam(value = "searchKey",required = false)String searchKey,
                                      @RequestParam(value = "deptId",required = false)Long deptId,
                                      @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                      @RequestParam(value="pageSize",defaultValue = "10") int pageSize)
    {
        Page<BusiConferenceAppointment> page = busiConferenceAppointmentService.selectBusiConferenceAppointmentListBykey(searchKey,deptId,pageIndex,pageSize);
        List<BusiConferenceAppointment> list = page.getResult();
        list.stream().forEach(p->{
            Object conferenceNumber = p.getParams().get("conferenceNumber");
            if(conferenceNumber!=null){
                String conferenceId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(conferenceNumber));
                p.getParams().put("conferenceId",conferenceId);
            }
            ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(p.getTemplateId());
            if(!Objects.isNull(modelBean)){
                Object obj = modelBean.get("templateConference");
                String jsonString = JSON.toJSONString(obj);
                BusiTemplateConference templateConference = JSON.parseObject(jsonString, BusiTemplateConference.class);
                if(Objects.isNull(templateConference.getConferencePassword())){
                    p.setPassword("DISABLED");
                }else {
                    p.setPassword("ENABLED");
                }
            }

        });
        return getDataTable(list);
    }

    /**
     * 获取会议预约记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议预约记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiConferenceAppointmentService.selectBusiConferenceAppointmentById(id));
    }

    /**
     * 新增会议预约记录
     */
    @PostMapping
    @Operation(summary = "新增会议预约记录")
    public RestResponse add(@RequestBody BusiConferenceAppointment busiConferenceAppointment)
    {
        Map<String, Object> resultMap = busiConferenceAppointmentService.insertBusiConferenceAppointment(busiConferenceAppointment);
        int rows = 0;
        Object rowsObj = resultMap.get("rows");
        if (rowsObj != null) {
            rows = (int) rowsObj;
        }
        return toAjax(rows);
    }

    /**
     * 修改会议预约记录
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改会议预约记录")
    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiConferenceAppointment busiConferenceAppointment)
    {
        busiConferenceAppointment.setId(id);
        return toAjax(busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, true));
    }

    /**
     * 删除会议预约记录
     */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除会议预约记录")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(id));
    }
}
