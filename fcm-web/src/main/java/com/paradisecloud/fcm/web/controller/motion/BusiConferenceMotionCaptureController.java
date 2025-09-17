package com.paradisecloud.fcm.web.controller.motion;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiConferenceMotionCapture;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceMotionCaptureService;
import com.paradisecloud.system.model.ExcelUtil;
import org.springframework.web.bind.annotation.*;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 会议动作捕捉Controller
 * 
 * @author lilinhai
 * @date 2025-06-16
 */
@RestController
@RequestMapping("/busi/motion/capture")
@Tag(name = "会议动作捕捉")
public class BusiConferenceMotionCaptureController extends BaseController
{
    @Resource
    private IBusiConferenceMotionCaptureService busiConferenceMotionCaptureService;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 查询会议动作捕捉列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议动作捕捉列表")
    public RestResponse list(BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        startPage();
        List<BusiConferenceMotionCapture> list = busiConferenceMotionCaptureService.selectBusiConferenceMotionCaptureList(busiConferenceMotionCapture);
        return getDataTable(list);
    }

    /**
     * 查询会议动作捕捉列表
     */
    @GetMapping("/getMotionList")
    @Operation(summary = "查询会议动作捕捉列表")
    public RestResponse list(HttpServletRequest request,
                             @RequestParam(value = "conferenceId", required = false) String conferenceId, @RequestParam(value = "historyConferenceId", required = false) Long historyConferenceId,
                             @RequestParam(value = "pageNum", required = false) Integer pageNum, @RequestParam(value = "pageSize", required = false) Integer pageSize,
                             @RequestParam(value = "motions", required = false) String[] motions)
    {
        if (StringUtils.isEmpty(conferenceId) && historyConferenceId == null) {
            return RestResponse.fail("会议ID或者历史会议ID不能为空！");
        }
        if (historyConferenceId == null && StringUtils.isNotEmpty(conferenceId)) {
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                if (historyConference != null) {
                    historyConferenceId = historyConference.getId();
                }
            }
        }
        if (historyConferenceId == null) {
            return RestResponse.fail("无此会议！");
        }
        if (pageNum == null || pageNum < 0) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (pageSize > 1000) {
            pageSize = 1000;
        }

        String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
        String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
        if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
            try {
                String referer = request.getHeader("referer");
                String ip = referer.replace("http://", "").replace("https://", "");
                if (ip.indexOf(":") > 0) {
                    ip.substring(0, ip.indexOf(":"));
                }
                if (ip.indexOf("/") > 0) {
                    ip = ip.substring(0, ip.indexOf("/"));
                }
                String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                if (externalIp.indexOf(":") > 0) {
                    externalIp.substring(0, externalIp.indexOf(":"));
                }
                if (externalIp.indexOf("/") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                }
                if (externalIp.equals(ip)) {
                    urlTemp = fmcRootUrlExternal;
                }
            } catch (Exception e) {
            }
        } else {
            try {
                String host = request.getHeader("Host");
                if (host.indexOf(":8898") > 0) {
                    urlTemp.replace(":8899", ":8898").replace("https://", "http://");
                }
            } catch (Exception e) {
            }
        }
        PaginationData<ModelBean> paginationData = new PaginationData<>();
        PageHelper.startPage(pageNum, pageSize);
        BusiConferenceMotionCapture busiConferenceMotionCaptureCon = new BusiConferenceMotionCapture();
        busiConferenceMotionCaptureCon.setHistoryConferenceId(historyConferenceId);
        if (motions != null && motions.length > 0) {
            busiConferenceMotionCaptureCon.getParams().put("motions", motions);
        }
        List<BusiConferenceMotionCapture> list = busiConferenceMotionCaptureService.selectBusiConferenceMotionCaptureList(busiConferenceMotionCaptureCon);
        PageInfo pageInfo = new PageInfo(list);
        for (BusiConferenceMotionCapture busiConferenceMotionCapture : list) {
            ModelBean modelBean = new ModelBean(busiConferenceMotionCapture);
            try {
                Map<String, Object> motionMap = objectMapper.readValue(busiConferenceMotionCapture.getMotion(), Map.class);
                modelBean.put("motion", motionMap);
            } catch (JsonProcessingException e) {
            }
            String imagePath = urlTemp + "/spaces/" + busiConferenceMotionCapture.getCoSpace() + "/" + busiConferenceMotionCapture.getImageName();
            modelBean.put("imagePath", imagePath);
            modelBean.put("createTime", busiConferenceMotionCapture.getCreateTime());
            paginationData.addRecord(modelBean);
        }

        paginationData.setTotal(pageInfo.getTotal());
        paginationData.setSize(pageInfo.getSize());
        paginationData.setPage(pageInfo.getPageNum());
        return  RestResponse.success(0L, "查询成功", paginationData);
    }

    /**
     * 导出会议动作捕捉列表
     */
    @Log(title = "会议动作捕捉", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出会议动作捕捉列表")
    public RestResponse export(BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        List<BusiConferenceMotionCapture> list = busiConferenceMotionCaptureService.selectBusiConferenceMotionCaptureList(busiConferenceMotionCapture);
        ExcelUtil<BusiConferenceMotionCapture> util = new ExcelUtil<BusiConferenceMotionCapture>(BusiConferenceMotionCapture.class);
        return util.exportExcel(list, "capture");
    }

    /**
     * 获取会议动作捕捉详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议动作捕捉详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiConferenceMotionCaptureService.selectBusiConferenceMotionCaptureById(id));
    }

    /**
     * 新增会议动作捕捉
     */
    @Log(title = "会议动作捕捉", businessType = BusinessType.INSERT)
//    @PostMapping
    @Operation(summary = "新增会议动作捕捉")
    public RestResponse add(@RequestBody BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        return toAjax(busiConferenceMotionCaptureService.insertBusiConferenceMotionCapture(busiConferenceMotionCapture));
    }

    /**
     * 修改会议动作捕捉
     */
    @Log(title = "会议动作捕捉", businessType = BusinessType.UPDATE)
//    @PutMapping
    @Operation(summary = "修改会议动作捕捉")
    public RestResponse edit(@RequestBody BusiConferenceMotionCapture busiConferenceMotionCapture)
    {
        return toAjax(busiConferenceMotionCaptureService.updateBusiConferenceMotionCapture(busiConferenceMotionCapture));
    }

    /**
     * 删除会议动作捕捉
     */
    @Log(title = "会议动作捕捉", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
	@Operation(summary = "删除会议动作捕捉")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiConferenceMotionCaptureService.deleteBusiConferenceMotionCaptureByIds(ids));
    }
}
