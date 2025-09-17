package com.paradisecloud.fcm.service.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOperationLogMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 操作日志记录Service业务层处理
 *
 * @author lilinhai
 * @date 2023-11-27
 */
@Service
public class BusiOperationLogServiceImpl implements IBusiOperationLogService
{
    private static final Logger LOG = LoggerFactory.getLogger(BusiOperationLogServiceImpl.class);

    @Resource
    private BusiOperationLogMapper busiOperationLogMapper;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    /**
     * 查询操作日志记录
     *
     * @param id 操作日志记录ID
     * @return 操作日志记录
     */
    @Override
    public BusiOperationLog selectBusiOperationLogById(Long id)
    {
        return busiOperationLogMapper.selectBusiOperationLogById(id);
    }

    /**
     * 查询操作日志记录列表
     *
     * @param busiOperationLog 操作日志记录
     * @return 操作日志记录
     */
    @Override
    public List<BusiOperationLog> selectBusiOperationLogList(OperationLogSearchVo busiOperationLog) {
        List<BusiOperationLog> busiOperationLogList = new ArrayList<>();
        Long historyConferenceId = busiOperationLog.getHistoryConferenceId();
        if (historyConferenceId != null) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
            if (busiHistoryConference == null) {
                return busiOperationLogList;
            }

            if (busiOperationLog.getPageNum() != null && busiOperationLog.getPageSize() != null) {
                PageHelper.startPage(busiOperationLog.getPageNum(), busiOperationLog.getPageSize());
            }
            Date date = new Date();
            String yearNew = DateUtil.convertDateToString(date, "yyyy");
            Integer nowYear = Integer.valueOf(DateUtil.convertDateToString(date, "yyyy"));
            Date startTime = busiHistoryConference.getConferenceStartTime();
            Date endTime = busiHistoryConference.getConferenceEndTime();
            Integer startYear = Integer.valueOf(DateUtil.convertDateToString(startTime, "yyyy"));
            Integer endYear;
            if (endTime != null) {
                endYear = Integer.valueOf(DateUtil.convertDateToString(endTime, "yyyy"));
            } else {
                endYear = nowYear;
            }

            try {
                for (int i = startYear; i <= endYear; i++) {
                    OperationLogSearchVo operationLogSearchVo = new OperationLogSearchVo();
                    operationLogSearchVo.setHistoryConferenceId(historyConferenceId);
                    String year = String.valueOf(i);
                    if (!year.equals(yearNew)) {
                        operationLogSearchVo.setYear(year);
                    }
                    try {
                        busiOperationLogList = busiOperationLogMapper.selectBusiOperationLogListHistory(operationLogSearchVo);
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        } else {
            if (busiOperationLog.getPageNum() != null && busiOperationLog.getPageSize() != null) {
                PageHelper.startPage(busiOperationLog.getPageNum(), busiOperationLog.getPageSize());
            }
            Date date = new Date();
            String yearNew = DateUtil.convertDateToString(date, "yyyy");
            try {
                OperationLogSearchVo operationLogSearchVo = new OperationLogSearchVo();
                operationLogSearchVo.setOperatorName(busiOperationLog.getOperatorName());
                operationLogSearchVo.setStartTime(busiOperationLog.getStartTime());
                operationLogSearchVo.setEndTime(busiOperationLog.getEndTime());
                if (busiOperationLog.getStartTime() != null) {
                    String year = DateUtil.convertDateToString(busiOperationLog.getStartTime(), "yyyy");
                    if (!year.equals(yearNew)) {
                        operationLogSearchVo.setYear(year);
                    }
                }
                busiOperationLogList = busiOperationLogMapper.selectBusiOperationLogListHistory(operationLogSearchVo);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return busiOperationLogList;
    }

    /**
     * 新增操作日志记录
     *
     * @param busiOperationLog 操作日志记录
     * @return 结果
     */
    @Override
    public int insertBusiOperationLog(BusiOperationLog busiOperationLog)
    {
        return busiOperationLogMapper.insertBusiOperationLog(busiOperationLog);
    }

    /**
     * 修改操作日志记录
     *
     * @param busiOperationLog 操作日志记录
     * @return 结果
     */
    @Override
    public int updateBusiOperationLog(BusiOperationLog busiOperationLog)
    {
        return busiOperationLogMapper.updateBusiOperationLog(busiOperationLog);
    }

    /**
     * 批量删除操作日志记录
     *
     * @param ids 需要删除的操作日志记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiOperationLogByIds(Long[] ids)
    {
        return busiOperationLogMapper.deleteBusiOperationLogByIds(ids);
    }

    /**
     * 删除操作日志记录信息
     *
     * @param id 操作日志记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiOperationLogById(Long id)
    {
        return busiOperationLogMapper.deleteBusiOperationLogById(id);
    }
}
