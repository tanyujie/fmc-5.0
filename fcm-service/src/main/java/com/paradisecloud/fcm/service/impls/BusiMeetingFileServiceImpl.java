package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiMeetingFileMapper;
import com.paradisecloud.fcm.dao.model.BusiMeetingFile;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 入会文件Service业务层处理
 *
 * @author lilinhai
 * @date 2024-03-29
 */
@Service
public class BusiMeetingFileServiceImpl implements IBusiMeetingFileService {
    @Resource
    private BusiMeetingFileMapper busiMeetingFileMapper;



    /**
     * 查询入会文件
     *
     * @param id 入会文件ID
     * @return 入会文件
     */
    @Override
    public BusiMeetingFile selectBusiMeetingFileById(Long id) {
        return busiMeetingFileMapper.selectBusiMeetingFileById(id);
    }

    /**
     * 查询入会文件列表
     *
     * @param busiMeetingFile 入会文件
     * @return 入会文件
     */
    @Override
    public List<BusiMeetingFile> selectBusiMeetingFileList(BusiMeetingFile busiMeetingFile) {
        List<BusiMeetingFile> busiMeetingFiles = busiMeetingFileMapper.selectBusiMeetingFileList(busiMeetingFile);
        return busiMeetingFiles;
    }

    /**
     * 新增入会文件
     *
     * @param busiMeetingFile 入会文件
     * @return 结果
     */
    @Override
    public int insertBusiMeetingFile(BusiMeetingFile busiMeetingFile) {
        busiMeetingFile.setCreateTime(new Date());
        return busiMeetingFileMapper.insertBusiMeetingFile(busiMeetingFile);
    }

    /**
     * 修改入会文件
     *
     * @param busiMeetingFile 入会文件
     * @return 结果
     */
    @Override
    public int updateBusiMeetingFile(BusiMeetingFile busiMeetingFile) {
        busiMeetingFile.setUpdateTime(new Date());
        return busiMeetingFileMapper.updateBusiMeetingFile(busiMeetingFile);
    }

    /**
     * 批量删除入会文件
     *
     * @param ids 需要删除的入会文件ID
     * @return 结果
     */
    @Override
    public int deleteBusiMeetingFileByIds(Long[] ids) {

        BusiMeetingFile busiMeetingFile = new BusiMeetingFile();
        busiMeetingFile.setFileStatus(0);
        List<BusiMeetingFile> busiMeetingFiles = busiMeetingFileMapper.selectBusiMeetingFileList(busiMeetingFile);
        if (busiMeetingFiles == null || busiMeetingFiles.isEmpty()) {
            return 0;
        }

        if (ids != null) {
            for (Long id : ids) {
                deleteBusiMeetingFileById(id);
            }
        }
        return ids.length;
    }

    /**
     * 删除入会文件信息
     *
     * @param id 入会文件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int deleteBusiMeetingFileById(Long id) {
        BusiMeetingFile busiMeetingFile = busiMeetingFileMapper.selectBusiMeetingFileById(id);
        if (busiMeetingFile == null) {
            throw new CustomException("文件不存在");
        }
        busiMeetingFile.setUpdateTime(new Date());
        busiMeetingFile.setFileStatus(0);


        return busiMeetingFileMapper.updateBusiMeetingFile(busiMeetingFile);

    }
}
