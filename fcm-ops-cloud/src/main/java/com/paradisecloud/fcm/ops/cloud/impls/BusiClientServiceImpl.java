package com.paradisecloud.fcm.ops.cloud.impls;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.dao.mapper.BusiClientMapper;
import com.paradisecloud.fcm.dao.mapper.BusiClientResourceMapper;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.dao.model.BusiClientResource;
import com.paradisecloud.fcm.dao.model.vo.BusiClientResourceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiClientVo;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientService;
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
public class BusiClientServiceImpl implements IBusiClientService {

    @Resource
    private BusiClientMapper busiClientMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private BusiClientResourceMapper busiClientResourceMapper;

    /**
     * 查询OPS
     *
     * @param id OPSID
     * @return OPS
     */
    @Override
    public BusiClientVo selectBusiClientById(Long id)
    {
        BusiClientVo busiClientVo = null;
        BusiClient busiClient = busiClientMapper.selectBusiClientById(id);
        if (busiClient != null) {
            busiClientVo = new BusiClientVo();
            BeanUtils.copyProperties(busiClient, busiClientVo);
            if (busiClient.getUserId() != null) {
                SysUser sysUser = sysUserService.selectUserById(busiClient.getUserId());
                if (sysUser != null) {
                    busiClientVo.setEmail(sysUser.getEmail());
                    busiClientVo.setPhoneNumber(sysUser.getPhonenumber());
                }
            }
            List<BusiClientResourceVo> resourceList = new ArrayList<>();
            BusiClientResource busiClientResourceCon = new BusiClientResource();
            busiClientResourceCon.setSn(busiClient.getSn());
            List<BusiClientResource> busiClientResources = busiClientResourceMapper.selectBusiClientResourceList(busiClientResourceCon);
            for (BusiClientResource busiClientResource : busiClientResources) {
                BusiClientResourceVo busiClientResourceVo = new BusiClientResourceVo();
                BeanUtils.copyProperties(busiClientResource, busiClientResourceVo);
                busiClientResourceVo.setClientId(busiClient.getId());
            }
            busiClientVo.setResourceList(resourceList);
        }
        return busiClientVo;
    }

    /**
     * 查询OPS列表
     *
     * @param busiClient OPS
     * @return OPS
     */
    @Override
    public PaginationData<Object> selectBusiClientList(BusiClientVo busiClient)
    {
        PaginationData<Object> paginationData = new PaginationData();
        List<BusiClient> busiClientList = busiClientMapper.selectBusiClientList(busiClient);
        PageInfo pageInfo = new PageInfo(busiClientList);
        paginationData.setTotal(pageInfo.getTotal());
        paginationData.setPage(pageInfo.getPageNum());
        paginationData.setSize(pageInfo.getSize());
        for (BusiClient busiClientTemp : busiClientList) {
            BusiClientVo busiClientVo = new BusiClientVo();
            BeanUtils.copyProperties(busiClientTemp, busiClientVo);
            if (busiClientTemp.getUserId() != null) {
                SysUser sysUser = sysUserService.selectUserById(busiClientTemp.getUserId());
                if (sysUser != null) {
                    busiClientVo.setEmail(sysUser.getEmail());
                    busiClientVo.setPhoneNumber(sysUser.getPhonenumber());
                }
            }
            List<BusiClientResourceVo> resourceList = new ArrayList<>();
            BusiClientResource busiClientResourceCon = new BusiClientResource();
            busiClientResourceCon.setSn(busiClientTemp.getSn());
            List<BusiClientResource> busiClientResources = busiClientResourceMapper.selectBusiClientResourceList(busiClientResourceCon);
            for (BusiClientResource busiClientResource : busiClientResources) {
                BusiClientResourceVo busiClientResourceVo = new BusiClientResourceVo();
                BeanUtils.copyProperties(busiClientResource, busiClientResourceVo);
                busiClientResourceVo.setClientId(busiClientTemp.getId());
                if (StringUtils.isNotEmpty(busiClientResource.getPurchaseType())) {
                    PurchaseType purchaseType = PurchaseType.convert(busiClientResource.getPurchaseType());
                    if (purchaseType != null) {
                        busiClientResourceVo.setPurchaseTypeName(purchaseType.getName());
                        busiClientResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                    }
                }
                if (StringUtils.isNotEmpty(busiClientResource.getMcuType())) {
                    McuType mcuType = McuType.convert(busiClientResource.getMcuType());
                    if (mcuType != null) {
                        busiClientResourceVo.setMcuTypeName(mcuType.getName());
                        busiClientResourceVo.setMcuTypeAlias(mcuType.getAlias());
                    }
                }
                resourceList.add(busiClientResourceVo);
            }
            busiClientVo.setResourceList(resourceList);
            paginationData.addRecord(busiClientVo);
        }

        return paginationData;
    }

    /**
     * 新增OPS
     *
     * @param busiClient OPS
     * @return 结果
     */
    @Override
    public int insertBusiClient(BusiClientVo busiClient)
    {
        busiClient.setCreateTime(new Date());
        int row = 0;
        try {
            if (busiClient.isRegister() != null && busiClient.isRegister()) {
                SysUser sysUser = new SysUser();
                String name = busiClient.getName();
                if (StringUtils.isEmpty(name) || !name.startsWith("client_")) {
                    name = "client_" + System.currentTimeMillis();
                    Random random = new Random();
                    for (int i = 0; i < 3; i++) {
                        name += random.nextInt(9);
                    }
                }
                sysUser.setUserName(name);
                sysUser.setNickName(name);
                sysUser.setPhonenumber(busiClient.getPhoneNumber());
                sysUser.setEmail(busiClient.getEmail());
                sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                sysUser.setDeptId(DeptConstant.CLIENT_DEPT_ID);
                Long[] roleIds = new Long[]{5l};
                sysUser.setRoleIds(roleIds);
                sysUserService.insertUser(sysUser);
                busiClient.setUserId(sysUser.getUserId());

                row = busiClientMapper.insertBusiClient(busiClient);
                if (row > 0){
                    BusiClient busiClientUpdated = busiClientMapper.selectBusiClientById(busiClient.getId());
                    ClientCache.getInstance().add(busiClientUpdated);
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
     * @param busiClient OPS
     * @return 结果
     */
    @Override
    public int updateBusiClient(BusiClientVo busiClient)
    {
        int row = 0;
        BusiClient busiClientUpdate = new BusiClient();
        busiClientUpdate.setId(busiClient.getId());
        busiClientUpdate.setName(busiClient.getName());
        busiClientUpdate.setUpdateTime(new Date());
        busiClientUpdate.setSourceId(busiClient.getSourceId());
        busiClientUpdate.setRemark(busiClient.getRemark());
        if (busiClient.isAutoUpdate()) {
            busiClientUpdate.setMqttOnlineStatus(busiClient.getMqttOnlineStatus());
            busiClientUpdate.setLastOnlineTime(busiClient.getLastOnlineTime());
            busiClientUpdate.setAppVersionCode(busiClient.getAppVersionCode());
            busiClientUpdate.setAppVersionName(busiClient.getAppVersionName());
            busiClientUpdate.setIp(busiClient.getIp());
            busiClientUpdate.setConnectIp(busiClient.getConnectIp());
            busiClientUpdate.setExpiredDate(busiClient.getExpiredDate());
        }
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiClientUpdate.setUpdateBy(loginUser.getUsername());
            }
        } catch (Exception e) {
        }
        BusiClient busiClientExist = ClientCache.getInstance().get(busiClient.getId());
        if (busiClientExist != null) {
            if (busiClient.isRegister() != null && busiClient.isRegister()) {
                if (busiClientExist.getUserId() == null) {
                    SysUser sysUser = new SysUser();
                    String name = busiClient.getName();
                    if (StringUtils.isEmpty(name) || name.startsWith("client_")) {
                        name = "client_" + System.currentTimeMillis();
                        Random random = new Random();
                        for (int i = 0; i < 3; i++) {
                            name += random.nextInt(9);
                        }
                    }
                    sysUser.setUserName(name);
                    sysUser.setNickName(name);
                    sysUser.setPhonenumber(busiClient.getPhoneNumber());
                    sysUser.setEmail(busiClient.getEmail());
                    sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                    sysUser.setDeptId(DeptConstant.CLIENT_DEPT_ID);
                    Long[] roleIds = new Long[]{5l};
                    sysUser.setRoleIds(roleIds);
                    int i = sysUserService.insertUser(sysUser);
                    if (i > 0) {
                        busiClientUpdate.setUserId(sysUser.getUserId());
                    }
                } else {
                    SysUser sysUser = sysUserService.selectUserById(busiClientExist.getUserId());
                    if (sysUser == null) {
                        sysUser = new SysUser();
                        String name = busiClient.getName();
                        if (StringUtils.isEmpty(name) || name.startsWith("client_")) {
                            name = "client_" + System.currentTimeMillis();
                            Random random = new Random();
                            for (int i = 0; i < 3; i++) {
                                name += random.nextInt(9);
                            }
                        }
                        sysUser.setUserName(name);
                        sysUser.setNickName(name);
                        sysUser.setPhonenumber(busiClient.getPhoneNumber());
                        sysUser.setEmail(busiClient.getEmail());
                        sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                        sysUser.setDeptId(DeptConstant.CLIENT_DEPT_ID);
                        Long[] roleIds = new Long[]{5l};
                        sysUser.setRoleIds(roleIds);
                        int i = sysUserService.insertUser(sysUser);
                        if (i > 0) {
                            busiClientUpdate.setUserId(sysUser.getUserId());
                        }
                    } else {
                        sysUser.setUserName("client_" + System.currentTimeMillis());
                        sysUser.setNickName(busiClient.getName());
                        sysUser.setPhonenumber(busiClient.getPhoneNumber());
                        sysUser.setEmail(busiClient.getEmail());
                        sysUserService.updateUser(sysUser);
                    }
                }
            }
        } else {
            return row;
        }
        try {
            row = busiClientMapper.updateBusiClient(busiClientUpdate);
            if (row > 0) {
                BusiClient busiClientUpdated = busiClientMapper.selectBusiClientById(busiClient.getId());
                ClientCache.getInstance().add(busiClientUpdated);

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
    public int deleteBusiClientByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            int i = deleteBusiClientById(id);
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
    public int deleteBusiClientById(Long id)
    {
        int row = 0;
        BusiClient busiClientExist = busiClientMapper.selectBusiClientById(id);
        if (busiClientExist != null) {
            row = busiClientMapper.deleteBusiClientById(id);
            if (row > 0) {
                if (busiClientExist.getUserId() != null) {
                    try {
                        sysUserService.deleteUserById(busiClientExist.getUserId());
                    } catch (Exception e) {
                    }
                }
                ClientCache.getInstance().remove(id);
            }
        }
        return row;
    }
}
