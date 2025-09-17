package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces;



/**
 * @author nj
 * @date 2024/3/6 11:06
 */
public interface IHwcloudUserService {
    Object getUsers(Long deptId,String deptCode,String searchKey,String searchScope,Integer pageIndex,Integer pageSize);

    Object getExternalContacts(Long deptId,String searchKey,String searchScope,Integer pageIndex,Integer pageSize);

    Object getDevices(Long deptId,String deptCode,String searchKey,Integer pageIndex,Integer pageSize);

    Object getCropDept(Long deptId,String deptCode);
}
