package com.paradisecloud.fcm.service.impls;

import java.util.List;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceCustomButtonMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceCustomButton;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceCustomButtonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议自定义按钮Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-07-05
 */
@Service
public class BusiConferenceCustomButtonServiceImpl implements IBusiConferenceCustomButtonService
{
    @Resource
    private BusiConferenceCustomButtonMapper busiConferenceCustomButtonMapper;

    /**
     * 查询会议自定义按钮
     * 
     * @param id 会议自定义按钮ID
     * @return 会议自定义按钮
     */
    @Override
    public BusiConferenceCustomButton selectBusiConferenceCustomButtonById(String id, String mcuType)
    {
        return busiConferenceCustomButtonMapper.selectBusiConferenceCustomButtonById(id, mcuType);
    }

    /**
     * 查询会议自定义按钮列表
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 会议自定义按钮
     */
    @Override
    public List<BusiConferenceCustomButton> selectBusiConferenceCustomButtonList(BusiConferenceCustomButton busiConferenceCustomButton)
    {
        return busiConferenceCustomButtonMapper.selectBusiConferenceCustomButtonList(busiConferenceCustomButton);
    }

    /**
     * 新增会议自定义按钮
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 结果
     */
    @Override
    public int insertBusiConferenceCustomButton(BusiConferenceCustomButton busiConferenceCustomButton)
    {
        BusiConferenceCustomButton busiConferenceCustomButtonExist = busiConferenceCustomButtonMapper.selectBusiConferenceCustomButtonById(busiConferenceCustomButton.getId(), busiConferenceCustomButton.getMcuType());
        if (busiConferenceCustomButtonExist != null) {
            return busiConferenceCustomButtonMapper.updateBusiConferenceCustomButton(busiConferenceCustomButton);
        } else {
            return busiConferenceCustomButtonMapper.insertBusiConferenceCustomButton(busiConferenceCustomButton);
        }
    }

    /**
     * 修改会议自定义按钮
     * 
     * @param busiConferenceCustomButton 会议自定义按钮
     * @return 结果
     */
    @Override
    public int updateBusiConferenceCustomButton(BusiConferenceCustomButton busiConferenceCustomButton)
    {
        BusiConferenceCustomButton busiConferenceCustomButtonExist = busiConferenceCustomButtonMapper.selectBusiConferenceCustomButtonById(busiConferenceCustomButton.getId(), busiConferenceCustomButton.getMcuType());
        if (busiConferenceCustomButtonExist != null) {
            return busiConferenceCustomButtonMapper.updateBusiConferenceCustomButton(busiConferenceCustomButton);
        } else {
            return busiConferenceCustomButtonMapper.insertBusiConferenceCustomButton(busiConferenceCustomButton);
        }
    }

    /**
     * 修改会议自定义按钮
     *
     * @param busiConferenceCustomButtonList 会议自定义按钮
     * @return 结果
     */
    @Override
    public int updateBusiConferenceCustomButton(List<BusiConferenceCustomButton> busiConferenceCustomButtonList, String mcuType)
    {
        BusiConferenceCustomButton busiConferenceCustomButtonCon = new BusiConferenceCustomButton();
        busiConferenceCustomButtonCon.setMcuType(mcuType);
        List<BusiConferenceCustomButton> busiConferenceCustomButtonExistList = selectBusiConferenceCustomButtonList(busiConferenceCustomButtonCon);
        for (BusiConferenceCustomButton busiConferenceCustomButtonExist : busiConferenceCustomButtonExistList) {
            deleteBusiConferenceCustomButtonById(busiConferenceCustomButtonExist.getId(), busiConferenceCustomButtonExist.getMcuType());
        }
        int rows = 0;
        for (int i = 0; i < busiConferenceCustomButtonList.size(); i++) {
            BusiConferenceCustomButton busiConferenceCustomButton = busiConferenceCustomButtonList.get(i);
            busiConferenceCustomButton.setSort(i);
            busiConferenceCustomButton.setMcuType(mcuType);
            rows += insertBusiConferenceCustomButton(busiConferenceCustomButton);
        }
        return rows;
    }

    /**
     * 删除会议自定义按钮信息
     * 
     * @param id 会议自定义按钮ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceCustomButtonById(String id, String mcuType)
    {
        return busiConferenceCustomButtonMapper.deleteBusiConferenceCustomButtonById(id, mcuType);
    }

    /**
     * 删除会议自定义按钮信息
     *
     * @param ids 会议自定义按钮ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceCustomButtonByIds(String[] ids, String mcuType)
    {
        int rows = 0;
        for (String id : ids) {
            rows += deleteBusiConferenceCustomButtonById(id, mcuType);
        }
        return rows;
    }
}
