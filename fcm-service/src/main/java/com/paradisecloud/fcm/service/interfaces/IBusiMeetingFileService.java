package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiMeetingFile;

import java.util.List;

/**
 * 入会文件Service接口
 * 
 * @author lilinhai
 * @date 2024-03-29
 */
public interface IBusiMeetingFileService 
{
    /**
     * 查询入会文件
     * 
     * @param id 入会文件ID
     * @return 入会文件
     */
    public BusiMeetingFile selectBusiMeetingFileById(Long id);

    /**
     * 查询入会文件列表
     * 
     * @param busiMeetingFile 入会文件
     * @return 入会文件集合
     */
    public List<BusiMeetingFile> selectBusiMeetingFileList(BusiMeetingFile busiMeetingFile);

    /**
     * 新增入会文件
     * 
     * @param busiMeetingFile 入会文件
     * @return 结果
     */
    public int insertBusiMeetingFile(BusiMeetingFile busiMeetingFile);

    /**
     * 修改入会文件
     * 
     * @param busiMeetingFile 入会文件
     * @return 结果
     */
    public int updateBusiMeetingFile(BusiMeetingFile busiMeetingFile);

    /**
     * 批量删除入会文件
     * 
     * @param ids 需要删除的入会文件ID
     * @return 结果
     */
    public int deleteBusiMeetingFileByIds(Long[] ids);

    /**
     * 删除入会文件信息
     * 
     * @param id 入会文件ID
     * @return 结果
     */
    public int deleteBusiMeetingFileById(Long id);
}
