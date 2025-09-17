package com.paradisecloud.fcm.ops.cloud.impls;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.dao.mapper.BusiOpsMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOpsResourceMapper;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.BusiOpsResource;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsVo;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsService;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author 刘禧龙
 */
@Service
public class BusiOpsServiceImpl implements IBusiOpsService {

    @Resource
    private BusiOpsMapper busiOpsMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private BusiOpsResourceMapper busiOpsResourceMapper;

    /**
     * 查询OPS
     *
     * @param id OPSID
     * @return OPS
     */
    @Override
    public BusiOpsVo selectBusiOpsById(Long id)
    {
        BusiOpsVo busiOpsVo = null;
        BusiOps busiOps = busiOpsMapper.selectBusiOpsById(id);
        if (busiOps != null) {
            busiOpsVo = new BusiOpsVo();
            BeanUtils.copyProperties(busiOps, busiOpsVo);
            if (busiOps.getUserId() != null) {
                SysUser sysUser = sysUserService.selectUserById(busiOps.getUserId());
                if (sysUser != null) {
                    busiOpsVo.setEmail(sysUser.getEmail());
                    busiOpsVo.setPhoneNumber(sysUser.getPhonenumber());
                }
            }
            List<BusiOpsResourceVo> resourceList = new ArrayList<>();
            BusiOpsResource busiOpsResourceCon = new BusiOpsResource();
            busiOpsResourceCon.setSn(busiOps.getSn());
            List<BusiOpsResource> busiOpsResources = busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResourceCon);
            for (BusiOpsResource busiOpsResource : busiOpsResources) {
                BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
                BeanUtils.copyProperties(busiOpsResource, busiOpsResourceVo);
                busiOpsResourceVo.setOpsId(busiOps.getId());
            }
            busiOpsVo.setResourceList(resourceList);
        }
        return busiOpsVo;
    }

    /**
     * 查询OPS列表
     *
     * @param busiOps OPS
     * @return OPS
     */
    @Override
    public PaginationData<Object> selectBusiOpsList(BusiOpsVo busiOps)
    {
        PaginationData<Object> paginationData = new PaginationData();
        List<BusiOps> busiOpsList = busiOpsMapper.selectBusiOpsList(busiOps);
        PageInfo pageInfo = new PageInfo(busiOpsList);
        paginationData.setTotal(pageInfo.getTotal());
        paginationData.setPage(pageInfo.getPageNum());
        paginationData.setSize(pageInfo.getSize());
        for (BusiOps busiOpsTemp : busiOpsList) {
            BusiOpsVo busiOpsVo = new BusiOpsVo();
            BeanUtils.copyProperties(busiOpsTemp, busiOpsVo);
            if (busiOpsTemp.getUserId() != null) {
                SysUser sysUser = sysUserService.selectUserById(busiOpsTemp.getUserId());
                if (sysUser != null) {
                    busiOpsVo.setEmail(sysUser.getEmail());
                    busiOpsVo.setPhoneNumber(sysUser.getPhonenumber());
                }
            }
            List<BusiOpsResourceVo> resourceList = new ArrayList<>();
            BusiOpsResource busiOpsResourceCon = new BusiOpsResource();
            busiOpsResourceCon.setSn(busiOpsTemp.getSn());
            List<BusiOpsResource> busiOpsResources = busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResourceCon);
            for (BusiOpsResource busiOpsResource : busiOpsResources) {
                BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
                BeanUtils.copyProperties(busiOpsResource, busiOpsResourceVo);
                busiOpsResourceVo.setOpsId(busiOpsTemp.getId());
                if (StringUtils.isNotEmpty(busiOpsResource.getPurchaseType())) {
                    PurchaseType purchaseType = PurchaseType.convert(busiOpsResource.getPurchaseType());
                    if (purchaseType != null) {
                        busiOpsResourceVo.setPurchaseTypeName(purchaseType.getName());
                        busiOpsResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                    }
                }
                if (StringUtils.isNotEmpty(busiOpsResource.getMcuType())) {
                    McuType mcuType = McuType.convert(busiOpsResource.getMcuType());
                    if (mcuType != null) {
                        busiOpsResourceVo.setMcuTypeName(mcuType.getName());
                        busiOpsResourceVo.setMcuTypeAlias(mcuType.getAlias());
                    }
                }
                resourceList.add(busiOpsResourceVo);
            }
            busiOpsVo.setResourceList(resourceList);
            paginationData.addRecord(busiOpsVo);
        }

        return paginationData;
    }

    /**
     * 新增OPS
     *
     * @param busiOps OPS
     * @return 结果
     */
    @Override
    public int insertBusiOps(BusiOpsVo busiOps)
    {
        busiOps.setCreateTime(new Date());
        int row = 0;
        try {
            if (busiOps.isRegister() != null && busiOps.isRegister()) {
                SysUser sysUser = new SysUser();
                String name = busiOps.getName();
                if (StringUtils.isEmpty(name) || !name.startsWith("ops_")) {
                    name = "ops_" + System.currentTimeMillis();
                    Random random = new Random();
                    for (int i = 0; i < 3; i++) {
                        name += random.nextInt(9);
                    }
                }
                sysUser.setUserName(name);
                sysUser.setNickName(name);
                sysUser.setPhonenumber(busiOps.getPhoneNumber());
                sysUser.setEmail(busiOps.getEmail());
                sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                sysUser.setDeptId(DeptConstant.OPS_DEPT_ID);
                Long[] roleIds = new Long[]{5l};
                sysUser.setRoleIds(roleIds);
                sysUserService.insertUser(sysUser);
                busiOps.setUserId(sysUser.getUserId());

                row = busiOpsMapper.insertBusiOps(busiOps);
                if (row > 0){
                    BusiOps busiOpsUpdated = busiOpsMapper.selectBusiOpsById(busiOps.getId());
                    OpsCache.getInstance().add(busiOpsUpdated);
                }
            }
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new SystemException("该序列号的OPS已存在！");
            }
        }
        return row;
    }

    /**
     * 修改OPS
     *
     * @param busiOps OPS
     * @return 结果
     */
    @Override
    public int updateBusiOps(BusiOpsVo busiOps)
    {
        int row = 0;
        BusiOps busiOpsUpdate = new BusiOps();
        busiOpsUpdate.setId(busiOps.getId());
        busiOpsUpdate.setName(busiOps.getName());
        busiOpsUpdate.setUpdateTime(new Date());
        busiOpsUpdate.setSourceId(busiOps.getSourceId());
        busiOpsUpdate.setRemark(busiOps.getRemark());
        if (busiOps.isAutoUpdate()) {
            busiOpsUpdate.setMqttOnlineStatus(busiOps.getMqttOnlineStatus());
            busiOpsUpdate.setLastOnlineTime(busiOps.getLastOnlineTime());
            busiOpsUpdate.setAppVersionCode(busiOps.getAppVersionCode());
            busiOpsUpdate.setAppVersionName(busiOps.getAppVersionName());
            busiOpsUpdate.setIp(busiOps.getIp());
            busiOpsUpdate.setConnectIp(busiOps.getConnectIp());
        }
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiOpsUpdate.setUpdateBy(loginUser.getUsername());
            }
        } catch (Exception e) {
        }
        BusiOps busiOpsExist = OpsCache.getInstance().get(busiOps.getId());
        if (busiOpsExist != null) {
            if (busiOps.isRegister() != null && busiOps.isRegister()) {
                if (busiOpsExist.getUserId() == null) {
                    SysUser sysUser = new SysUser();
                    String name = busiOps.getName();
                    if (StringUtils.isEmpty(name) || name.startsWith("ops_")) {
                        name = "ops_" + System.currentTimeMillis();
                        Random random = new Random();
                        for (int i = 0; i < 3; i++) {
                            name += random.nextInt(9);
                        }
                    }
                    sysUser.setUserName(name);
                    sysUser.setNickName(name);
                    sysUser.setPhonenumber(busiOps.getPhoneNumber());
                    sysUser.setEmail(busiOps.getEmail());
                    sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                    sysUser.setDeptId(DeptConstant.OPS_DEPT_ID);
                    Long[] roleIds = new Long[]{5l};
                    sysUser.setRoleIds(roleIds);
                    int i = sysUserService.insertUser(sysUser);
                    if (i > 0) {
                        busiOpsUpdate.setUserId(sysUser.getUserId());
                    }
                } else {
                    SysUser sysUser = sysUserService.selectUserById(busiOpsExist.getUserId());
                    if (sysUser == null) {
                        sysUser = new SysUser();
                        String name = busiOps.getName();
                        if (StringUtils.isEmpty(name) || name.startsWith("ops_")) {
                            name = "ops_" + System.currentTimeMillis();
                            Random random = new Random();
                            for (int i = 0; i < 3; i++) {
                                name += random.nextInt(9);
                            }
                        }
                        sysUser.setUserName(name);
                        sysUser.setNickName(name);
                        sysUser.setPhonenumber(busiOps.getPhoneNumber());
                        sysUser.setEmail(busiOps.getEmail());
                        sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                        sysUser.setDeptId(DeptConstant.OPS_DEPT_ID);
                        Long[] roleIds = new Long[]{5l};
                        sysUser.setRoleIds(roleIds);
                        int i = sysUserService.insertUser(sysUser);
                        if (i > 0) {
                            busiOpsUpdate.setUserId(sysUser.getUserId());
                        }
                    } else {
                        sysUser.setUserName("ops_" + System.currentTimeMillis());
                        sysUser.setNickName(busiOps.getName());
                        sysUser.setPhonenumber(busiOps.getPhoneNumber());
                        sysUser.setEmail(busiOps.getEmail());
                        sysUserService.updateUser(sysUser);
                    }
                }
            }
        } else {
            return row;
        }
        try {
            row = busiOpsMapper.updateBusiOps(busiOpsUpdate);
            if (row > 0) {
                BusiOps busiOpsUpdated = busiOpsMapper.selectBusiOpsById(busiOps.getId());
                OpsCache.getInstance().add(busiOpsUpdated);

            }
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new SystemException("该序列号的OPS已存在！！");
            }
        }
        return row;
    }

    /**
     * 批量删除OPS
     *
     * @param ids 需要删除的OPSID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            int i = deleteBusiOpsById(id);
            rows += i;
        }
        return rows;
    }

    /**
     * 删除OPS信息
     *
     * @param id OPSID
     * @return 结果
     */
    @Override
    public int deleteBusiOpsById(Long id)
    {
        int row = 0;
        BusiOps busiOpsExist = busiOpsMapper.selectBusiOpsById(id);
        if (busiOpsExist != null) {
            row = busiOpsMapper.deleteBusiOpsById(id);
            if (row > 0) {
                if (busiOpsExist.getUserId() != null) {
                    try {
                        sysUserService.deleteUserById(busiOpsExist.getUserId());
                    } catch (Exception e) {
                    }
                }
                OpsCache.getInstance().remove(id);
            }
        }
        return row;
    }
}
