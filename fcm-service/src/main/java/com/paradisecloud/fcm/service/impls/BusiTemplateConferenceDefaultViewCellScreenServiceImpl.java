package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewCellScreen;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewCellScreenService;

/**
 * 默认视图下指定的多分频单元格Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@Service
public class BusiTemplateConferenceDefaultViewCellScreenServiceImpl implements IBusiTemplateConferenceDefaultViewCellScreenService 
{
    @Autowired
    private BusiTemplateConferenceDefaultViewCellScreenMapper busiTemplateConferenceDefaultViewCellScreenMapper;

    /**
     * 查询默认视图下指定的多分频单元格
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 默认视图下指定的多分频单元格
     */
    @Override
    public BusiTemplateConferenceDefaultViewCellScreen selectBusiTemplateConferenceDefaultViewCellScreenById(Long id)
    {
        return busiTemplateConferenceDefaultViewCellScreenMapper.selectBusiTemplateConferenceDefaultViewCellScreenById(id);
    }

    /**
     * 查询默认视图下指定的多分频单元格列表
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 默认视图下指定的多分频单元格
     */
    @Override
    public List<BusiTemplateConferenceDefaultViewCellScreen> selectBusiTemplateConferenceDefaultViewCellScreenList(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        return busiTemplateConferenceDefaultViewCellScreenMapper.selectBusiTemplateConferenceDefaultViewCellScreenList(busiTemplateConferenceDefaultViewCellScreen);
    }

    /**
     * 新增默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    @Override
    public int insertBusiTemplateConferenceDefaultViewCellScreen(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        busiTemplateConferenceDefaultViewCellScreen.setCreateTime(new Date());
        return busiTemplateConferenceDefaultViewCellScreenMapper.insertBusiTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
    }

    /**
     * 修改默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    @Override
    public int updateBusiTemplateConferenceDefaultViewCellScreen(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        busiTemplateConferenceDefaultViewCellScreen.setUpdateTime(new Date());
        return busiTemplateConferenceDefaultViewCellScreenMapper.updateBusiTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
    }

    /**
     * 批量删除默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的默认视图下指定的多分频单元格ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewCellScreenByIds(Long[] ids)
    {
        return busiTemplateConferenceDefaultViewCellScreenMapper.deleteBusiTemplateConferenceDefaultViewCellScreenByIds(ids);
    }

    /**
     * 删除默认视图下指定的多分频单元格信息
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 结果
     */
    @Override
    public int deleteBusiTemplateConferenceDefaultViewCellScreenById(Long id)
    {
        return busiTemplateConferenceDefaultViewCellScreenMapper.deleteBusiTemplateConferenceDefaultViewCellScreenById(id);
    }
}
