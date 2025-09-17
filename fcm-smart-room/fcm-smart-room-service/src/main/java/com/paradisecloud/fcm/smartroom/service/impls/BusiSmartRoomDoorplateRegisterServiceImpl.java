package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateRegisterMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplateRegister;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateRegisterVO;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDoorplateRegisterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议室门牌注册Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomDoorplateRegisterServiceImpl implements IBusiSmartRoomDoorplateRegisterService
{
    @Resource
    private BusiSmartRoomDoorplateRegisterMapper busiSmartRoomDoorplateRegisterMapper;

    /**
     * 查询会议室门牌注册
     * 
     * @param id 会议室门牌注册ID
     * @return 会议室门牌注册
     */
    @Override
    public BusiSmartRoomDoorplateRegister selectBusiSmartRoomDoorplateRegisterById(Long id)
    {
        return busiSmartRoomDoorplateRegisterMapper.selectBusiSmartRoomDoorplateRegisterById(id);
    }

    /**
     * 查询会议室门牌注册列表
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 会议室门牌注册
     */
    @Override
    public List<BusiSmartRoomDoorplateRegister> selectBusiSmartRoomDoorplateRegisterList(BusiSmartRoomDoorplateRegisterVO busiSmartRoomDoorplateRegister)
    {
        busiSmartRoomDoorplateRegister.getParams().put("searchKey", busiSmartRoomDoorplateRegister.getSearchKey());
        return busiSmartRoomDoorplateRegisterMapper.selectBusiSmartRoomDoorplateRegisterList(busiSmartRoomDoorplateRegister);
    }

    /**
     * 新增会议室门牌注册
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister)
    {
        busiSmartRoomDoorplateRegister.setCreateTime(new Date());
        return busiSmartRoomDoorplateRegisterMapper.insertBusiSmartRoomDoorplateRegister(busiSmartRoomDoorplateRegister);
    }

    /**
     * 修改会议室门牌注册
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister)
    {
        busiSmartRoomDoorplateRegister.setUpdateTime(new Date());
        return busiSmartRoomDoorplateRegisterMapper.updateBusiSmartRoomDoorplateRegister(busiSmartRoomDoorplateRegister);
    }

    /**
     * 批量删除会议室门牌注册
     * 
     * @param ids 需要删除的会议室门牌注册ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDoorplateRegisterByIds(Long[] ids)
    {
        return busiSmartRoomDoorplateRegisterMapper.deleteBusiSmartRoomDoorplateRegisterByIds(ids);
    }

    /**
     * 删除会议室门牌注册信息
     * 
     * @param id 会议室门牌注册ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDoorplateRegisterById(Long id)
    {
        return busiSmartRoomDoorplateRegisterMapper.deleteBusiSmartRoomDoorplateRegisterById(id);
    }
}
