package com.paradisecloud.fcm.terminal.fs.interfaces;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;


/**
 * freeSwitchService接口
 * 
 * @author zyz
 * @date 2021-08-17
 */
public interface IFreeSwitchUserService 
{
    /**
     * 查询freeSwitch
     * 
     * @param userName freeSwitchID
     * @return freeSwitch
     */
    public FreeSwitchUser selectFreeSwitchUserById(String userName, Long deptId);

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
     * @param busiFreeSwitch 
     * @return 结果
     */
    public int insertFreeSwitchUser(FreeSwitchUser freeSwitchUser, BusiFreeSwitch busiFreeSwitch);
    
    /**在服务器上增加freeSwitch用户
     * @param TerminalId
     * @param userId
     * @param password
     * @param 
     */
//    public int addFreeSwitchUser(BusiTerminal busiTerminal);
    
    
    /**在服务器上修改freeSwitch用户
     * @param TerminalId
     * @param userId
     * @param password
     * @param 
     */
//    public int updateFreeSwitchUser(BusiTerminal busiTerminal);
    
    /**
     * 修改freeSwitch
     * 
     * @param freeSwitchUser freeSwitch
     * @param busiFreeSwitch 
     * @return 结果
     */
    public int updateFreeSwitchUser(FreeSwitchUser freeSwitchUser, BusiFreeSwitch busiFreeSwitch);

    /**
     * 批量删除freeSwitch
     *
     * @param ids 需要删除的freeSwitchID
     * @return 结果
     */
    public int deleteFreeSwitchUserByIds(String[] ids, BusiFreeSwitch busiFreeSwitch);

    /**
     * 批量删除freeSwitch
     * 
     * @param ids 需要删除的freeSwitchID
     * @return 结果
     */
    public int deleteFreeSwitchUserByIds(Long[] ids, Long deptId);

	/**
	 * @return
	 */
	public List<FreeSwitchUser> getFreeSwitchUserInfo(Long deptId);
	

	/**
	 * @param userId
	 * @return
	 */
	public List<String> userIdIsRepeat(FreeSwitchUser freeSwitchUser);
	

//	public Boolean connectServerFcm(BusiFreeSwitch busiFreeSwitch);
}
