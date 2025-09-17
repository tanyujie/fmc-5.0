package com.paradisecloud.smc3.service.interfaces;


import com.paradisecloud.smc3.model.DefaultServiceZoneIdRep;
import com.paradisecloud.smc3.model.response.SmcOrganization;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 10:57
 */
public interface Smc3ServiceZoneId {

   DefaultServiceZoneIdRep getSmcServiceZoneId(Long deptId);

   Object getSmcArea(Long deptId);

   Object getSmcDevicecapabilities(Long deptId);

   Object searchName(String name,Long deptId);

    Object resetactivecode(String id,Long deptId);

    Object getSmcMcu(Long deptId);

    List<SmcOrganization> getOrganizationsList(Long deptId);
}
