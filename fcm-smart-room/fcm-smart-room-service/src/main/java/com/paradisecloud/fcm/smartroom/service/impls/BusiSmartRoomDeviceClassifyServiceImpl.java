package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceClassifyMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceClassify;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceClassifyVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceClassifyService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.stereotype.Service;;import javax.annotation.Resource;

/**
 * 会议室设备分类Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-02-02
 */
@Service
public class BusiSmartRoomDeviceClassifyServiceImpl implements IBusiSmartRoomDeviceClassifyService
{
    @Resource
    private BusiSmartRoomDeviceClassifyMapper busiSmartRoomDeviceClassifyMapper;
    @Resource
    private BusiSmartRoomDeviceMapper busiSmartRoomDeviceMapper;

    /**
     * 查询会议室设备分类
     * 
     * @param id 会议室设备分类ID
     * @return 会议室设备分类
     */
    @Override
    public BusiSmartRoomDeviceClassify selectBusiSmartRoomDeviceClassifyById(Long id)
    {
        return busiSmartRoomDeviceClassifyMapper.selectBusiSmartRoomDeviceClassifyById(id);
    }

    /**
     * 查询会议室设备分类列表
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 会议室设备分类
     */
    @Override
    public List<BusiSmartRoomDeviceClassify> selectBusiSmartRoomDeviceClassifyList(BusiSmartRoomDeviceClassifyVo busiSmartRoomDeviceClassify)
    {
        String searchKey = busiSmartRoomDeviceClassify.getSearchKey();
        busiSmartRoomDeviceClassify.setDeviceClassifyName(searchKey);
        return busiSmartRoomDeviceClassifyMapper.selectBusiSmartRoomDeviceClassifyList(busiSmartRoomDeviceClassify);
    }

    /**
     * 新增会议室设备分类
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify)
    {
        busiSmartRoomDeviceClassify.setCreateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoomDeviceClassify.setCreateBy(loginUser.getUsername());
        }
        return busiSmartRoomDeviceClassifyMapper.insertBusiSmartRoomDeviceClassify(busiSmartRoomDeviceClassify);
    }

    /**
     * 修改会议室设备分类
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify)
    {
        busiSmartRoomDeviceClassify.setUpdateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoomDeviceClassify.setUpdateBy(loginUser.getUsername());
        }
        return busiSmartRoomDeviceClassifyMapper.updateBusiSmartRoomDeviceClassify(busiSmartRoomDeviceClassify);
    }

    /**
     * 批量删除会议室设备分类
     * 
     * @param ids 需要删除的会议室设备分类ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceClassifyByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            BusiSmartRoomDevice busiSmartRoomDevice = new BusiSmartRoomDevice();
            busiSmartRoomDevice.setDeviceClassify(id);
            List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(busiSmartRoomDevice);
            if (busiSmartRoomDeviceList.size() > 0) {
                throw new CustomException("当前类型已有设备使用，不能删除！");
            } else {
                i += busiSmartRoomDeviceClassifyMapper.deleteBusiSmartRoomDeviceClassifyById(id);
            }
        }
        return i;
    }

    /**
     * 删除会议室设备分类信息
     * 
     * @param id 会议室设备分类ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceClassifyById(Long id)
    {
        int i = 0;
        BusiSmartRoomDevice busiSmartRoomDevice = new BusiSmartRoomDevice();
        busiSmartRoomDevice.setDeviceClassify(id);
        List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(busiSmartRoomDevice);
        if (busiSmartRoomDeviceList.size() > 0) {
            i = busiSmartRoomDeviceClassifyMapper.deleteBusiSmartRoomDeviceClassifyById(id);
        }
        return i;
    }
}
