package com.paradisecloud.fcm.web.controller.mobile;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.system.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author nj
 * @date 2022/12/12 17:42
 */
@RestController
@RequestMapping("/mobile/busi/recording")
@Tag(name = "移动端会议录制")
public class MobileRecordingController extends BaseController {

    @Resource
    private IRecordingService recordingService;
    @Resource
    private IBusiRecordsService busiRecordsService;
    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;
    @Resource
    private BusiRecordsMapper busiRecordsMapper;
    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    /**
     * 获取录制文件列表
     *
     * @param conferenceNumber 会议号
     * @return
     */
    @GetMapping("/getFolder")
    @Operation(summary = "获取录制文件列表")
    public RestResponse getFolder(@RequestParam("conferenceNumber") String conferenceNumber,  String coSpaceId) {

        List<Map<String, Object>> folder = recordingService.getFolder(conferenceNumber, AuthenticationUtil.getDeptId(), coSpaceId);
        return RestResponse.success(folder);
    }


    /**
     * 根据部门ID和会议名称
     * 获取存在录制文件的会议列表
     * @return
     */
    @GetMapping("/getRecordingConferences")
    @Operation(summary = "获取存在录制文件的会议列表")
    public RestResponse getRecordingConferences(@RequestParam(value = "searchKey",required = false)String searchKey,
                                                @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                                @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();

        RecordsSearchVo recordsSearchVo = new RecordsSearchVo();
        recordsSearchVo.setPageNum(pageIndex);
        recordsSearchVo.setPageSize(pageSize);
        recordsSearchVo.setSearchKey(searchKey);
        recordsSearchVo.setDeptId(deptId);
        return RestResponse.success(recordingService.getBusiConferenceNumberVoList(recordsSearchVo));

    }


    /**
     * 删除录制文件
     *
     * @return
     */
    @PostMapping("/deleteRecordingFile/{coSpaceId}")
    @Operation(summary = "删除录制文件")
    public RestResponse deleteRecordingFile(String id, String fileName, @PathVariable String coSpaceId) {
        logger.info("deleteRecordingFile入参文件名:{},会议号：{}", fileName, coSpaceId);
        recordingService.deleteRecordingFile(id, fileName, coSpaceId, false);
        return RestResponse.success();
    }

    /**
     * 获取录制文件列表
     *
     * @return
     */
    @GetMapping("/getRecordList")
    @Operation(summary = "获取录制文件列表")
    public RestResponse getRecordList() {
        Integer page = 1;
        Integer size = 100;
        Integer startIndex = 0;
        Integer endIndex = startIndex + size;
        List<Map> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        long deptId = getDeptId();
        BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
        busiRecordSetting.setDeptId(deptId);
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
        if (!(busiRecordSettings == null || busiRecordSettings.isEmpty())) {
            try {
                RecordsSearchVo recordsSearchVo = new RecordsSearchVo();
                if (recordsSearchVo.getPageNum() == null || recordsSearchVo.getPageNum() <= 0) {
                    recordsSearchVo.setPageNum(1);
                }
                if (recordsSearchVo.getPageSize() == null || recordsSearchVo.getPageSize() > 100) {
                    recordsSearchVo.setPageSize(100);
                }
                List<BusiRecordsSearchResult> busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroup(deptId, recordsSearchVo.getSearchKey());
                if (busiRecordsSearchResultList != null && busiRecordsSearchResultList.size() > 0) {
                    for (BusiRecordsSearchResult busiRecordsSearchResult : busiRecordsSearchResultList) {

                        Map<String, Object> stringObjectMap = new HashMap<>();
                        String coSpaceId = busiRecordsSearchResult.getCoSpaceId();
                        stringObjectMap.put("coSpaceId", coSpaceId);
                        if (StringUtils.isNotEmpty(coSpaceId) && (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc"))) {
                            if (coSpaceId.contains("-")) {
                                stringObjectMap.put("conferenceNumber", coSpaceId.substring(0, coSpaceId.indexOf("-")));
                            }
                        } else {
                            stringObjectMap.put("conferenceNumber", busiRecordsSearchResult.getConferenceNumber().toString());
                        }
                        stringObjectMap.put("deptId", busiRecordsSearchResult.getDeptId());
                        stringObjectMap.put("fileSize", 0);
                        stringObjectMap.put("recordFileNum", busiRecordsSearchResult.getRecordFileNum());
                        stringObjectMap.put("recordingTimeOfLate", busiRecordsSearchResult.getRecordingTimeOfLate());
                        stringObjectMap.put("conferenceName", busiRecordsSearchResult.getName());

                        // recordInfo
                        List<Map<String, Object>> folders = new ArrayList<>();

                        List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(deptId, coSpaceId, Boolean.FALSE);
                        for (int i = 0; i < busiRecordsList.size(); i++) {
                            BusiRecords busiRecords = busiRecordsList.get(i);
                            if (StringUtils.isNotEmpty(busiRecords.getRealName())) {
                                Map<String, Object> map = new HashMap<>();
                                String coSpaceIdTemp = busiRecords.getCoSpaceId();
                                String comUrl = (busiRecordSettings != null && busiRecordSettings.size() > 0) ? busiRecordSettings.get(0).getUrl() : "";
                                if (comUrl.lastIndexOf("/") < comUrl.length() - 1) {
                                    comUrl += "/" + coSpaceIdTemp;
                                } else {
                                    comUrl += coSpaceIdTemp;
                                }
                                String url = comUrl + "/" + busiRecords.getRealName();
                                map.put("fileName", busiRecords.getFileName());
                                map.put("realName", busiRecords.getRealName());
                                map.put("recordingTime", busiRecords.getCreateTime());
                                map.put("fileSize", busiRecords.getFileSize());
                                map.put("url", url);
                                map.put("id", busiRecords.getId());
                                map.put("coSpaceId", coSpaceIdTemp);
                                map.put("deptId", deptId);
                                folders.add(map);
                            }
                        }

                        stringObjectMap.put("recordInfoList", folders);
                        list.add(stringObjectMap);
                    }
                }
            } catch (Exception e) {
                logger.error("录制列表异常===", e);
            }
            jsonObject.put("total", list.size());
            jsonObject.put("page", page);
            jsonObject.put("size", size);

            List<Map> arrayList = new ArrayList<>();
            if (startIndex < list.size() && endIndex >= list.size()) {
                endIndex = list.size();
                arrayList = list.subList(startIndex, endIndex);
            } else if (startIndex < list.size() && endIndex < list.size()) {
                arrayList = list.subList(startIndex, endIndex);
            }
            jsonObject.put("data", arrayList);
            return RestResponse.success(jsonObject);
        }
        return RestResponse.fail();
    }

    private Long getDeptId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();
        return deptId;
    }

}
