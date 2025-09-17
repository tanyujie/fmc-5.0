package com.paradisecloud.fcm.mqtt.interfaces;

import java.io.IOException;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.springframework.web.multipart.MultipartFile;

import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalAction;
import com.paradisecloud.fcm.mqtt.model.BannerParams;
import com.paradisecloud.fcm.mqtt.model.ExcelTerminalOut;
import com.paradisecloud.fcm.mqtt.model.TerminalAction;

/**
 * 终端动作Service接口
 * 
 * @author zyz
 * @date 2021-07-31
 */
public interface IBusiTerminalActionService 
{
    /**
     * 查询终端动作具体信息
     * 
     * @param id 
     * @return BusiTerminalAction
     */
    public BusiTerminalAction selectBusiTerminalActionById(Long id);

    /**
     * 查询终端动作列表
     * 
     * @param busiTerminalAction 
     * @return List<BusiTerminalAction>
     */
    public List<BusiTerminalAction> selectBusiTerminalActionList(BusiTerminalAction busiTerminalAction);

    /**
     * 新增终端动作信息
     * 
     * @param busiTerminalAction 
     * @return int
     */
    public int insertBusiTerminalAction(BusiTerminalAction busiTerminalAction);

    /**
     * 修改终端动作信息
     * 
     * @param busiTerminalAction 
     * @return int
     */
    public int updateBusiTerminalAction(BusiTerminalAction busiTerminalAction);

    /**
     * 批量删除终端动作信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiTerminalActionByIds(Long[] ids);

    /**
     * 删除终端动作信息
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiTerminalActionById(Long id);
	
    /**
	 * 是否同意终端直播入会和会议发言
	 * @param 
	 * @return
	 */
	public int isAgreeTerminalAction(Long id, Boolean isAgree);
	

	/**
	 * 重启mqtt服务器
	 * @param id
	 * @return
	 */
	public Boolean mqttServerRestart(Long id);
	
	/**
	 * 终端动作的处理(reboot、powerOff、reset、presentationOpen、presentationClose)
	 * @param id
	 * @return
	 */
	public void terminalActionDealResult(TerminalAction terminalAction);
	
	
	/**
	 * 终端信息修改，会控修改了终端信息，下发给终端
	 * @param id
	 * @return
	 */
	public void terminalInfoModify(Long id, String name);
	
	
	/**
	 * 设置摄像头的转动，会控下发给终端
	 * @param id
	 * @return
	 */
	public void cameraControl(Long id, String direction, Boolean isFar);
	
	
	/**
	 * 设置摄像头本端的转动停止，会控下发给终端
	 * @param id
	 * @return
	 */
	public void cameraControlStop(Long id, Boolean isFar);
	
	

	/**
	 * @param ids
	 * 终端升级
	 */
	public String terminalRemoteUpgrade(Long[] ids);

	/**
	 * 终端日志采集
	 * @param id
	 */
	public void terminalCollectLogs(Long id) throws MalformedObjectNameException;

	/**
	 * 保存日志信息
	 * @param sn
	 * @param filePath
	 * @param fileName
	 */
	public void saveTerminalLogInfo(String mac, String filePath, String fileName);
	

	public void hostControlCamera(String clientId, String conferenceNum, String controlParam, String controlCmd);
	

	public void bindNoRegisterAccount(BusiTerminal busiTerminal);
	
	
	public void inviteTerminalIntoConference(Long templateId, String sn);
	

	public void hostSetConferenceBanner(BannerParams bannerParams);
	

	public void hostSetConferenceScrollBanner(BannerParams bannerParams);

	public int saveTerminalExcelFile(MultipartFile multipartFile, Long deptId);
	
	public List<ExcelTerminalOut> selectExcel(long id);

	public int isInviteLiveTerminal(String mac,String conferenceId,String status,String conferenceNumber,String  conferenceName);

	String terminalRemoteUpgradeAll(long id);

//	public void vhdTermminalGetSipAccount(BusiTerminal busiTerminal, String messageId, String sn);
}
