package com.paradisecloud.fcm.terminal.fs.interfaces;

import java.util.List;

import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;



/**
 * freeSwitchService接口
 * 
 * @author zyz
 * @date 2021-08-17
 */
public interface IFreeSwitchUserProviderService 
{
    /**
     * 查询freeSwitch
     * 
     * @param userId freeSwitchID
     * @return freeSwitch
     */
    public FreeSwitchUser selectFreeSwitchUserById(String userId);

    /**
     * 查询freeSwitch列表
     * 
     * @param freeSwitchUser freeSwitch
     * @return freeSwitch集合
     */
    public List<FreeSwitchUser> selectFreeSwitchUserList(FreeSwitchUser freeSwitchUser);

    /**
     * 新增freeSwitch
     * 
     * @param freeSwitchUser freeSwitch
     * @return 结果
     */
    public int insertFreeSwitchUser(FreeSwitchUser freeSwitchUser);

    /**
     * 修改freeSwitch
     * 
     * @param freeSwitchUser freeSwitch
     * @return 结果
     */
    public int updateFreeSwitchUser(FreeSwitchUser freeSwitchUser);

    /**
     * 批量删除freeSwitch
     * 
     * @param ids 需要删除的freeSwitchID
     * @return 结果
     */
    public int deleteFreeSwitchUserByIds(Long[] ids);

	/**
	 * @return
	 */
	public int getFreeSwitchUserInfo();
	

	/**
	 * @param userId
	 * @return
	 */
	public List<String> userIdIsRepeat(String userId);
}
