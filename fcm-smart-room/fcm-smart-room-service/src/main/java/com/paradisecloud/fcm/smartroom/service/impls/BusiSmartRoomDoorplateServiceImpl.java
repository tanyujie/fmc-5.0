package com.paradisecloud.fcm.smartroom.service.impls;

import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateRegisterMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplateRegister;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDoorplateService;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomTask;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会议室门牌Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomDoorplateServiceImpl implements IBusiSmartRoomDoorplateService
{
    @Resource
    private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;
    @Resource
    private IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService;
    @Resource
    private BusiSmartRoomDoorplateRegisterMapper busiSmartRoomDoorplateRegisterMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询会议室门牌
     * 
     * @param id 会议室门牌ID
     * @return 会议室门牌
     */
    @Override
    public BusiSmartRoomDoorplate selectBusiSmartRoomDoorplateById(Long id)
    {
        BusiSmartRoomDoorplate busiSmartRoomDoorplate = busiSmartRoomDoorplateMapper.selectBusiSmartRoomDoorplateById(id);
        if (busiSmartRoomDoorplate != null) {
            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().getRoomByDoorplate(busiSmartRoomDoorplate);
            if (busiSmartRoom != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("roomId", busiSmartRoom.getId());
                map.put("roomName", busiSmartRoom.getRoomName());
                busiSmartRoomDoorplate.setParams(map);
            }
        }
        return busiSmartRoomDoorplate;
    }

    /**
     * 查询会议室门牌列表
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 会议室门牌
     */
    @Override
    public List<BusiSmartRoomDoorplate> selectBusiSmartRoomDoorplateList(BusiSmartRoomDoorplateVo busiSmartRoomDoorplate)
    {
        busiSmartRoomDoorplate.getParams().put("searchKey", busiSmartRoomDoorplate.getSearchKey());
        List<BusiSmartRoomDoorplate> busiSmartRoomDoorplateList = busiSmartRoomDoorplateMapper.selectBusiSmartRoomDoorplateList(busiSmartRoomDoorplate);
        if (busiSmartRoomDoorplateList != null && busiSmartRoomDoorplateList.size() > 0) {
            for (BusiSmartRoomDoorplate smartRoomDoorplate : busiSmartRoomDoorplateList) {
                BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().getRoomByDoorplate(smartRoomDoorplate);
                if (busiSmartRoom != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("roomId", busiSmartRoom.getId());
                    map.put("roomName", busiSmartRoom.getRoomName());
                    smartRoomDoorplate.setParams(map);
                }
            }
        }
        return busiSmartRoomDoorplateList;
    }

    /**
     * 新增会议室门牌
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate)
    {
        busiSmartRoomDoorplate.setCreateTime(new Date());
        if (busiSmartRoomDoorplate.getMqttOnlineStatus() == null) {
            busiSmartRoomDoorplate.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoomDoorplate.setCreateBy(loginUser.getUsername());
        }
        if (StringUtils.isNotEmpty(busiSmartRoomDoorplate.getSn())) {
            BusiSmartRoomDoorplate busiSmartRoomDoorplateExist = SmartRoomDoorplateCache.getInstance().getBySn(busiSmartRoomDoorplate.getSn());
            if (busiSmartRoomDoorplateExist != null) {
                throw new SystemException("序列号已存在，不能重复添加！");
            }
        }

        String sn = busiSmartRoomDoorplate.getSn();
        if (StringUtils.isNotEmpty(sn)) {
            BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister = new BusiSmartRoomDoorplateRegister();
            busiSmartRoomDoorplateRegister.setSn(sn);
            List<BusiSmartRoomDoorplateRegister> busiSmartRoomDoorplateRegisterList = busiSmartRoomDoorplateRegisterMapper.selectBusiSmartRoomDoorplateRegisterList(busiSmartRoomDoorplateRegister);
            if (busiSmartRoomDoorplateRegisterList != null && busiSmartRoomDoorplateRegisterList.size() > 0) {
                BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegisterTemp = busiSmartRoomDoorplateRegisterList.get(0);
                if (busiSmartRoomDoorplateRegisterTemp != null) {
                    busiSmartRoomDoorplate.setAppType(busiSmartRoomDoorplateRegisterTemp.getAppType());
                    busiSmartRoomDoorplate.setAppVersionCode(busiSmartRoomDoorplateRegisterTemp.getAppVersionCode());
                    busiSmartRoomDoorplate.setAppVersionName(busiSmartRoomDoorplateRegisterTemp.getAppVersionName());
                    busiSmartRoomDoorplate.setConnectIp(busiSmartRoomDoorplateRegisterTemp.getConnectIp());
                    busiSmartRoomDoorplate.setIp(busiSmartRoomDoorplateRegisterTemp.getConnectIp());
                    busiSmartRoomDoorplateRegisterMapper.deleteBusiSmartRoomDoorplateRegisterById(busiSmartRoomDoorplateRegisterTemp.getId());
                }
            }
        }

        int i = busiSmartRoomDoorplateMapper.insertBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
        if (i > 0) {
            Map<String, Object> params = busiSmartRoomDoorplate.getParams();
            if (params != null && params.containsKey("roomId")) {
                Object roomIdObj = params.get("roomId");
                Long roomId = null;
                if (roomIdObj != null) {
                    roomId = Long.valueOf(roomIdObj.toString());
                }
                if (roomId != null) {
                    BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(roomId);
                    if (busiSmartRoom != null) {
                        Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(roomId);
                        if (boundDoorplateId != null) {
                            throw new SystemException("当前房间已绑定其它门牌，不能重复绑定！");
                        }
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                        busiSmartRoomDeviceMap.setDeviceId(busiSmartRoomDoorplate.getId());
                        busiSmartRoomDeviceMap.setDeviceType(DeviceType.DOORPLATE.getCode());
                        busiSmartRoomDeviceMap.setRoomId(roomId);
                        int i1 = busiSmartRoomDeviceMapService.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
                        if (i1 > 0) {
                            SmartRoomCache.getInstance().bindDoorplate(busiSmartRoomDeviceMap);

                            // 推送消息给电子门牌
                            UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(roomId.toString(), 100, roomId);
                            taskService.addTask(updateDoorplateForMeetingRoomTask);
                        }
                    }
                }
            }
            SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);

            // 推送消息给电子门牌
            UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(busiSmartRoomDoorplate.getSn(), 0, busiSmartRoomDoorplate.getSn());
            taskService.addTask(updateDoorplateForMeetingRoomTask);
        }
        return i;
    }

    /**
     * 修改会议室门牌
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate)
    {
        int i = busiSmartRoomDoorplateMapper.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
        if (i > 0) {
            Map<String, Object> params = busiSmartRoomDoorplate.getParams();
            Long boundRoomId = SmartRoomCache.getInstance().getRoomIdByDoorplate(busiSmartRoomDoorplate);
            Long roomId = null;
            if (params != null && params.containsKey("roomId")) {
                Object roomIdObj = params.get("roomId");
                if (roomIdObj != null) {
                    roomId = Long.valueOf(roomIdObj.toString());
                }
            }
            boolean isNeedSend = false;
            if (boundRoomId != null) {
                if (roomId == null || roomId.longValue() != boundRoomId) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMapCon = new BusiSmartRoomDeviceMap();
                    busiSmartRoomDeviceMapCon.setRoomId(boundRoomId);
                    busiSmartRoomDeviceMapCon.setDeviceId(busiSmartRoomDoorplate.getId());
                    busiSmartRoomDeviceMapCon.setDeviceType(DeviceType.DOORPLATE.getCode());
                    List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapCon);
                    if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = busiSmartRoomDeviceMapList.get(0);
                        int deleteI = busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(busiSmartRoomDeviceMap.getId());
                        if (deleteI > 0) {
                            SmartRoomCache.getInstance().unBindDoorplate(busiSmartRoomDeviceMap);
                            isNeedSend = true;
                        }
                    }
                }
            }
            if (roomId != null) {
                if (boundRoomId == null || roomId.longValue() != boundRoomId) {
                    if (busiSmartRoomDoorplate != null) {
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                        busiSmartRoomDeviceMap.setRoomId(roomId);
                        busiSmartRoomDeviceMap.setDeviceId(busiSmartRoomDoorplate.getId());
                        busiSmartRoomDeviceMap.setDeviceType(DeviceType.DOORPLATE.getCode());
                        int i1 = busiSmartRoomDeviceMapService.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
                        if (i1 > 0) {
                            SmartRoomCache.getInstance().bindDoorplate(busiSmartRoomDeviceMap);
                            isNeedSend = true;
                        }
                    }
                }
            } else {
                if (boundRoomId != null) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                    busiSmartRoomDeviceMap.setDeviceId(busiSmartRoomDoorplate.getId());
                    busiSmartRoomDeviceMap.setDeviceType(DeviceType.DOORPLATE.getCode());
                    List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                    for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                        int i1 = busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(smartRoomDeviceMap.getId());
                        if (i1 > 0) {
                            SmartRoomCache.getInstance().unBindDoorplate(smartRoomDeviceMap);
                        }
                    }
                }
            }
            if (isNeedSend) {
                // 推送消息给电子门牌
                UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTaskBound = new UpdateDoorplateForMeetingRoomTask(busiSmartRoomDoorplate.getSn(), 0, busiSmartRoomDoorplate.getSn());
                taskService.addTask(updateDoorplateForMeetingRoomTaskBound);
            }
        }
        return i;
    }

    /**
     * 批量删除会议室门牌
     * 
     * @param ids 需要删除的会议室门牌ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDoorplateByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            int i = deleteBusiSmartRoomDoorplateById(id);
            if (i > 0) {
                rows++;
            }
        }
        return rows;
    }

    /**
     * 删除会议室门牌信息
     * 
     * @param id 会议室门牌ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDoorplateById(Long id)
    {
        int rows = 0;
        BusiSmartRoomDoorplate busiSmartRoomDoorplate = busiSmartRoomDoorplateMapper.selectBusiSmartRoomDoorplateById(id);
        if (busiSmartRoomDoorplate != null) {
            BusiSmartRoomDeviceMap busiSmartRoomDeviceMapCon = new BusiSmartRoomDeviceMap();
            busiSmartRoomDeviceMapCon.setDeviceType(DeviceType.DOORPLATE.getCode());
            busiSmartRoomDeviceMapCon.setDeviceId(busiSmartRoomDoorplate.getId());
            List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapCon);
            for (BusiSmartRoomDeviceMap busiSmartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(busiSmartRoomDeviceMap.getId());
                SmartRoomCache.getInstance().unBindDoorplate(busiSmartRoomDeviceMap);
            }
            int i = busiSmartRoomDoorplateMapper.deleteBusiSmartRoomDoorplateById(id);
            if (i > 0) {
                SmartRoomDoorplateCache.getInstance().remove(id);
                rows++;

                // 推送消息给电子门牌
                UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(busiSmartRoomDoorplate.getSn(), 0, busiSmartRoomDoorplate.getSn());
                taskService.addTask(updateDoorplateForMeetingRoomTask);
            }
        }
        return rows;
    }

    @Override
    public int importSmartRoomDoorplateByExcel(MultipartFile uploadFile) {
        int rowsInt = 0;
        Boolean fileType = true;
        //检查文件是否为空
        boolean empty = uploadFile.isEmpty();
        if(empty || uploadFile == null){
            fileType = false;
        }

        //检查文件是否是excel类型文件
        String filename = uploadFile.getOriginalFilename();
        if(!filename.endsWith("xls") && !filename.endsWith("xlsx")){
            fileType = false;
        }
        if(!fileType) {
            throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_th_th_F, "请检查，上传的是否是EXCEL文件!");
        }
        //获取workbook对象
        Workbook workbook = null;
        try {

            InputStream inputStream = uploadFile.getInputStream();
            //根据后缀名是否excel文件
            if(filename.endsWith("xls")){

                //2003
                workbook = new HSSFWorkbook(inputStream);
            }else if(filename.endsWith("xlsx")){

                //2007
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Sheet rows : workbook) {
            Row row = null;
            if (rows != null && rows.getLastRowNum() > 0) {
                row = rows.getRow(0);
            }
            if (row != null) {
                for (int i1 = 1; i1 <= rows.getLastRowNum(); i1++) {
                    Row row1 = rows.getRow(i1);
                    int firstCellNum = row1.getFirstCellNum();
                    int lastCellNum = row1.getLastCellNum();
                    BusiSmartRoomDoorplate busiSmartRoomDoorplate = new BusiSmartRoomDoorplate();
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    if (loginUser != null) {
                        busiSmartRoomDoorplate.setCreateBy(loginUser.getUsername());
                    }
                    busiSmartRoomDoorplate.setCreateTime(new Date());
                    for (int i = firstCellNum; i < lastCellNum; i++) {
                        Cell cell = row1.getCell(i);
                        String cellValue = null;
                        //判断数据的类型
                        switch (cell.getCellType()) {
                            case NUMERIC: //数字0
                                cellValue = new BigDecimal(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
                                break;
                            case STRING: //字符串1
                                cellValue = String.valueOf(cell.getStringCellValue());
                                break;
                            case BOOLEAN: //Boolean
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case BLANK: //空值
                                cellValue = "";
                                break;
                            case ERROR: //故障
                                cellValue = "非法字符";
                                break;
                            default:
                                cellValue = "未知类型";
                                break;
                        }
                        switch (i) {
                            case 0:
                                busiSmartRoomDoorplate.setName(cellValue);
                                break;
                            case 1:
                                busiSmartRoomDoorplate.setSn(cellValue);
                                break;
                            case 2:
                                busiSmartRoomDoorplate.setIp(cellValue);
                                break;
                            case 3:
                                if (cellValue.contains(":")) {
                                    Integer type = Integer.parseInt(cellValue.split(":")[0]);
                                    if (type != null) {
                                        DeviceType convert = DeviceType.convert(type);
                                        if (convert != null) {
                                            busiSmartRoomDoorplate.setAppType(String.valueOf(convert.getCode()));
                                        }
                                    }
                                }
                                break;
                            case 4:
                                busiSmartRoomDoorplate.setRemark(cellValue);
                                break;
                            default:
                                break;
                        }
                    }
                    rowsInt += busiSmartRoomDoorplateMapper.insertBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
                }
            }
        }
        return rowsInt;
    }

    @Override
    public List<BusiSmartRoomDoorplate> notBound(Long roomId) {
        List<BusiSmartRoomDoorplate> busiSmartRoomDoorplateList = busiSmartRoomDoorplateMapper.selectNotBound(roomId);
        return busiSmartRoomDoorplateList;
    }
}
