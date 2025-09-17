package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 会议室门牌Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomDoorplateService
{
    /**
     * 查询会议室门牌
     * 
     * @param id 会议室门牌ID
     * @return 会议室门牌
     */
    public BusiSmartRoomDoorplate selectBusiSmartRoomDoorplateById(Long id);

    /**
     * 查询会议室门牌列表
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 会议室门牌集合
     */
    public List<BusiSmartRoomDoorplate> selectBusiSmartRoomDoorplateList(BusiSmartRoomDoorplateVo busiSmartRoomDoorplate);

    /**
     * 新增会议室门牌
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 结果
     */
    public int insertBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate);

    /**
     * 修改会议室门牌
     * 
     * @param busiSmartRoomDoorplate 会议室门牌
     * @return 结果
     */
    public int updateBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate);

    /**
     * 批量删除会议室门牌
     * 
     * @param ids 需要删除的会议室门牌ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateByIds(Long[] ids);

    /**
     * 删除会议室门牌信息
     * 
     * @param id 会议室门牌ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateById(Long id);

    /**
     * 导入会议室门牌
     * @param uploadFile
     * @return
     */
    int importSmartRoomDoorplateByExcel(MultipartFile uploadFile);

    List<BusiSmartRoomDoorplate> notBound(Long roomId);
}
