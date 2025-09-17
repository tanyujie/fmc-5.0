package com.paradisecloud.smc3.service.impls;

import java.util.List;

import com.paradisecloud.fcm.dao.mapper.BusiPickerMapper;
import com.paradisecloud.fcm.dao.model.BusiPicker;
import com.paradisecloud.smc3.service.interfaces.IBusiPickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-06-13
 */
@Service
@Slf4j
public class BusiPickerServiceImpl implements IBusiPickerService
{
    @Resource
    private BusiPickerMapper busiPickerMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiPicker selectBusiPickerById(Integer id)
    {
        return busiPickerMapper.selectBusiPickerById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiPicker> selectBusiPickerList(BusiPicker busiPicker)
    {
        return busiPickerMapper.selectBusiPickerList(busiPicker);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiPicker(BusiPicker busiPicker)
    {
        return busiPickerMapper.insertBusiPicker(busiPicker);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiPicker 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiPicker(BusiPicker busiPicker)
    {
        return busiPickerMapper.updateBusiPicker(busiPicker);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiPickerByIds(Integer[] ids)
    {
        return busiPickerMapper.deleteBusiPickerByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiPickerById(Integer id)
    {
        return busiPickerMapper.deleteBusiPickerById(id);
    }

    @Override
    public int updateBusiPickerDeptIds(List<Integer> apiDepts,List<Integer> accessDepts) {

        try {
            BusiPicker busiPicker = new BusiPicker();
            List<BusiPicker> busiPickers = busiPickerMapper.selectBusiPickerList(busiPicker);
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiPickers)){
                for (BusiPicker picker : busiPickers) {
                    busiPickerMapper.deleteBusiPickerById(picker.getId());
                }
            }
            if(!CollectionUtils.isEmpty(apiDepts)){
                for (Integer deptId : apiDepts) {
                    BusiPicker item = new BusiPicker();
                    item.setDeptId(deptId);
                    item.setDeptType("API");
                    busiPickerMapper.insertBusiPicker(item);

                }
            }

            if(!CollectionUtils.isEmpty(accessDepts)){
                for (Integer deptId : accessDepts) {
                    BusiPicker item = new BusiPicker();
                    item.setDeptId(deptId);
                    item.setDeptType("ACCESS");
                    busiPickerMapper.insertBusiPicker(item);
                }
            }
        } catch (Exception e) {
           log.info(e.getMessage());
            return 0;
        }

        return 1;
    }


}
