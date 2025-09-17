package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmc;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/24 11:29
 */
public interface BusiSmcMapper {

     BusiSmc selectBusiSmcById(Long id);

     List<BusiSmc> selectBusiSmcList(BusiSmc busiSmc);
     

     int insertBusiSmc(BusiSmc busiSmc);

     int updateBusiSmc(BusiSmc busiSmc);

     int deleteBusiSmcById(Long id);

     List<BusiSmc> selectBusiSmcListNoP(BusiSmc busiSmc);
}
