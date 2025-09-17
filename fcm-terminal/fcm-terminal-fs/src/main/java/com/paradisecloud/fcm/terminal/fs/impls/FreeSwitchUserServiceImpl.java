package com.paradisecloud.fcm.terminal.fs.impls;

import java.util.ArrayList;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.UserinfoMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.db.dao.Userinfo;
import com.paradisecloud.fcm.terminal.fs.db.manager.UserinfoManager;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfig;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.interfaces.IFreeSwitchUserService;
import com.paradisecloud.fcm.terminal.fs.model.FreeSwitchUser;
import com.sinhy.exception.SystemException;

import javax.annotation.Resource;

/**
 * freeSwitchService业务层处理
 * 
 * @author zyz
 * @date 2021-08-17
 */
@Transactional
@Service("freeSwitchUserService")
public class FreeSwitchUserServiceImpl implements IFreeSwitchUserService 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchUserServiceImpl.class);

    @Value("${application.home}")
	private String projectPath;
	/** 是否启用FS特殊连接 */
	@Value("${freeswitch.userinfo.useFcmDb}")
	private boolean userinfo_db_useFcmDb = false;

	@Resource
	private UserinfoMapper userinfoMapper;

    /**
     * 查询freeSwitch
     * 
     * @param userName freeSwitchID
     * @return freeSwitch
     */
    @Override
    public FreeSwitchUser selectFreeSwitchUserById(String userName, Long deptId)
    {
    	FreeSwitchUser freeSwitchUser = null;
    	if (!userinfo_db_useFcmDb) {
			FcmBridge fcmBridge = null;
			BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(deptId);
			if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
				FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
				if (fcmBridgeCluster != null) {
					List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
					// 由于使用固定用户信息数据库，任意一个FCM即可
					fcmBridge = fcmBridges.get(0);
				}
			} else {
				fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
			}
			BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
			String ip = busiFreeSwitch.getIp();
			//id代表userId
			UserinfoManager userinfoManager = new UserinfoManager(ip);
			Userinfo userinfo = userinfoManager.queryByUsername(userName);
			if (userinfo != null) {
				freeSwitchUser = new FreeSwitchUser();
				freeSwitchUser.setUserId(userinfo.getUsername());
				freeSwitchUser.setPassword(userinfo.getPassword());
			}
		} else {
    		com.paradisecloud.fcm.dao.model.Userinfo userinfoCon = new com.paradisecloud.fcm.dao.model.Userinfo();
    		userinfoCon.setUsername(userName);
			com.paradisecloud.fcm.dao.model.Userinfo userinfo = userinfoMapper.selectUserinfoByUsername(userName);
			if (userinfo != null) {
				freeSwitchUser = new FreeSwitchUser();
				freeSwitchUser.setUserId(userinfo.getUsername());
				freeSwitchUser.setPassword(userinfo.getPassword());
			}
		}
    	
        return freeSwitchUser;
    }

    /**
     * 查询freeSwitch列表
     * 
     * @param freeSwitchUser freeSwitch
     * @return freeSwitch
     */
    @Override
    public List<FreeSwitchUser> selectFreeSwitchUserList(FreeSwitchUser freeSwitchUser)
    {
        return this.getFreeSwitchUserInfo(freeSwitchUser.getDeptId());
    }

	/**
	 * 新增freeSwitch
	 *
	 * @param freeSwitchUser freeSwitch
	 * @return 结果
	 */
	@Override
	public int insertFreeSwitchUser(FreeSwitchUser freeSwitchUser, BusiFreeSwitch busiFreeSwitch)
	{
		int s = FcmConfigConstant.ZERO;
		try
		{
			if(null != freeSwitchUser)
			{
				if (!userinfo_db_useFcmDb) {
					UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
					if (freeSwitchUser.getUserId().contains(FcmConfig.SPLIT_LINE)) {
						String[] splitUserId = freeSwitchUser.getUserId().split(FcmConfig.SPLIT_LINE);
						if (null != splitUserId && splitUserId.length > 0) {
							Integer beginUserId = Integer.valueOf(splitUserId[0]);
							Integer endUserId = Integer.valueOf(splitUserId[1]) + 1;
							for (int i = beginUserId; i < endUserId; i++) {
								String username = String.valueOf(i);
								Userinfo userinfo = userinfoManager.queryByUsername(username);
								if (userinfo != null) {
									//处理freeSwitch下的user信息
									userinfo.setPassword(freeSwitchUser.getPassword());
									userinfoManager.update(userinfo);
								} else {
									userinfo = new Userinfo();
									userinfo.setUsername(username);
									userinfo.setPassword(freeSwitchUser.getPassword());
									//新增
									userinfoManager.insert(userinfo);
								}
							}
						}
					} else {
						String username = freeSwitchUser.getUserId();
						Userinfo userinfo = userinfoManager.queryByUsername(username);
						if (userinfo != null) {
							//处理freeSwitch下的user信息
							userinfo.setPassword(freeSwitchUser.getPassword());
							userinfoManager.update(userinfo);
						} else {
							userinfo = new Userinfo();
							userinfo.setUsername(username);
							userinfo.setPassword(freeSwitchUser.getPassword());
							//新增
							userinfoManager.insert(userinfo);
						}
					}
				} else {
					if (freeSwitchUser.getUserId().contains(FcmConfig.SPLIT_LINE)) {
						String[] splitUserId = freeSwitchUser.getUserId().split(FcmConfig.SPLIT_LINE);
						if (null != splitUserId && splitUserId.length > 0) {
							Integer beginUserId = Integer.valueOf(splitUserId[0]);
							Integer endUserId = Integer.valueOf(splitUserId[1]) + 1;
							for (int i = beginUserId; i < endUserId; i++) {
								String username = String.valueOf(i);
								com.paradisecloud.fcm.dao.model.Userinfo userinfo = userinfoMapper.selectUserinfoByUsername(username);
								if (userinfo != null) {
									//处理freeSwitch下的user信息
									userinfo.setPassword(freeSwitchUser.getPassword());
									userinfoMapper.updateUserinfo(userinfo);
								} else {
									userinfo = new com.paradisecloud.fcm.dao.model.Userinfo();
									userinfo.setUsername(username);
									userinfo.setPassword(freeSwitchUser.getPassword());
									//新增
									userinfoMapper.insertUserinfo(userinfo);
								}
							}
						}
					} else {
						String username = freeSwitchUser.getUserId();
						com.paradisecloud.fcm.dao.model.Userinfo userinfo = userinfoMapper.selectUserinfoByUsername(username);
						if (userinfo != null) {
							//处理freeSwitch下的user信息
							userinfo.setPassword(freeSwitchUser.getPassword());
							userinfoMapper.updateUserinfo(userinfo);
						} else {
							userinfo = new com.paradisecloud.fcm.dao.model.Userinfo();
							userinfo.setUsername(username);
							userinfo.setPassword(freeSwitchUser.getPassword());
							//新增
							userinfoMapper.insertUserinfo(userinfo);
						}
					}
				}
			}
			s = FcmConfigConstant.SUCCESS;
		}
		catch (Exception e)
		{
			s = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "新增FS用户异常!");
		}finally {
			LOGGER.info("新增FREESWITCH用户成功，关闭会话!");
		}

		return s;
	}

	/**
	 * 修改freeSwitch
	 *
	 * @param freeSwitchUser freeSwitch
	 * @return 结果
	 */
	@Override
	public int updateFreeSwitchUser(FreeSwitchUser freeSwitchUser, BusiFreeSwitch busiFreeSwitch)
	{
		int u = FcmConfigConstant.ZERO;
		try
		{
			if(null != freeSwitchUser)
			{
				if (!userinfo_db_useFcmDb) {
					UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
					String username = freeSwitchUser.getUserId();
					Userinfo userinfo = userinfoManager.queryByUsername(username);
					if (userinfo != null) {
						//处理freeSwitch下的user信息
						userinfo.setPassword(freeSwitchUser.getPassword());
						userinfoManager.update(userinfo);
					}
				} else {
					String username = freeSwitchUser.getUserId();
					com.paradisecloud.fcm.dao.model.Userinfo userinfo = userinfoMapper.selectUserinfoByUsername(username);
					if (userinfo != null) {
						//处理freeSwitch下的user信息
						userinfo.setPassword(freeSwitchUser.getPassword());
						userinfoMapper.updateUserinfo(userinfo);
					}
				}
			}
			u = FcmConfigConstant.SUCCESS;
		}
		catch (Exception e)
		{
			u = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "更新FS用户异常!");
		} finally {
			LOGGER.info("==========================>更新FREESWITCH用户成功!");
		}

		return u;
	}

	/**
	 * 批量删除freeSwitch
	 *
	 * @param ids 需要删除的freeSwitchID
	 * @return 结果
	 */
	@Override
	public int deleteFreeSwitchUserByIds(String[] ids, BusiFreeSwitch busiFreeSwitch)
	{
		int del = FcmConfigConstant.ZERO;
		try
		{
			if (!userinfo_db_useFcmDb) {
				UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
				StringBuilder usersSb = new StringBuilder();
				int m = 0;
				for (int i = 0; i < ids.length; i++) {
					if (i > 0) {
						usersSb.append(",");
					}
					usersSb.append(ids[i]);
					m++;
					if (m == 10) {
						String[] userNames = usersSb.toString().split(",", -1);
						userinfoManager.deleteByUsernames(userNames);
						m = 0;
						usersSb = new StringBuilder();
					}
				}
				if (m > 0) {
					String[] userNames = usersSb.toString().split(",", -1);
					userinfoManager.deleteByUsernames(userNames);
				}
			} else {
				StringBuilder usersSb = new StringBuilder();
				int m = 0;
				for (int i = 0; i < ids.length; i++) {
					if (i > 0) {
						usersSb.append(",");
					}
					usersSb.append(ids[i]);
					m++;
					if (m == 10) {
						String[] userNames = usersSb.toString().split(",", -1);
						userinfoMapper.deleteUserinfoByUsernames(userNames);
						m = 0;
						usersSb = new StringBuilder();
					}
				}
				if (m > 0) {
					String[] userNames = usersSb.toString().split(",", -1);
					userinfoMapper.deleteUserinfoByUsernames(userNames);
				}
			}

			del = FcmConfigConstant.SUCCESS;
		}
		catch (Exception e)
		{
			del = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "删除FS服务的用户异常!");
		}finally {
			LOGGER.info("==========================>删除FREESWITCH用户成功!");
		}

		return del;
	}

	/**
     * 批量删除freeSwitch
     *
     * @param ids 需要删除的freeSwitchID
     * @return 结果
     */
    @Override
    public int deleteFreeSwitchUserByIds(Long[] ids, Long deptId)
    {
		int del = FcmConfigConstant.ZERO;

		try
		{
			if (!userinfo_db_useFcmDb) {
				FcmBridge fcmBridge = null;
				BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(deptId);
				if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
					FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
					if (fcmBridgeCluster != null) {
						List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
						// 由于使用固定用户信息数据库，任意一个FCM即可
						fcmBridge = fcmBridges.get(0);
					}
				} else {
					fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
				}
				BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
				UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
				StringBuilder usersSb = new StringBuilder();
				int m = 0;
				for (int i = 0; i < ids.length; i++) {
					if (i > 0) {
						usersSb.append(",");
					}
					usersSb.append(ids[i]);
					m++;
					if (m == 10) {
						String[] userNames = usersSb.toString().split(",", -1);
						userinfoManager.deleteByUsernames(userNames);
						m = 0;
						usersSb = new StringBuilder();
					}
				}
				if (m > 0) {
					String[] userNames = usersSb.toString().split(",", -1);
					userinfoManager.deleteByUsernames(userNames);
				}
			} else {
				StringBuilder usersSb = new StringBuilder();
				int m = 0;
				for (int i = 0; i < ids.length; i++) {
					if (i > 0) {
						usersSb.append(",");
					}
					usersSb.append(ids[i]);
					m++;
					if (m == 10) {
						String[] userNames = usersSb.toString().split(",", -1);
						userinfoMapper.deleteUserinfoByUsernames(userNames);
						m = 0;
						usersSb = new StringBuilder();
					}
				}
				if (m > 0) {
					String[] userNames = usersSb.toString().split(",", -1);
					userinfoMapper.deleteUserinfoByUsernames(userNames);
				}
			}

			del = FcmConfigConstant.SUCCESS;
		}
		catch (Exception e)
		{
			del = FcmConfigConstant.ZERO;
			throw new SystemException(100334, "删除FS服务的用户异常!");
		}finally {
			LOGGER.info("==========================>删除FREESWITCH用户成功!");
		}

        return del;
    }

	@Override
	public List<FreeSwitchUser> getFreeSwitchUserInfo(Long deptId) {
		List<FreeSwitchUser> switchUsers = new ArrayList<FreeSwitchUser>();

			try {
				FcmBridge fcmBridge = null;
				BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(deptId);
				if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
					FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
					if (fcmBridgeCluster != null) {
						List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
						// 由于使用固定用户信息数据库，任意一个FCM即可
						fcmBridge = fcmBridges.get(0);
					}
				} else {
					fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
				}
				BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
				if (!userinfo_db_useFcmDb) {
					UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
					List<Userinfo> userinfoList = userinfoManager.queryAll();
					for (Userinfo userinfo : userinfoList) {
						FreeSwitchUser freeSwitchUser = new FreeSwitchUser();
						freeSwitchUser.setId(userinfo.getId());
						freeSwitchUser.setUserId(userinfo.getUsername());
						freeSwitchUser.setPassword(userinfo.getPassword());
					}
				} else {
					com.paradisecloud.fcm.dao.model.Userinfo userinfoCon = new com.paradisecloud.fcm.dao.model.Userinfo();
					List<com.paradisecloud.fcm.dao.model.Userinfo> userinfoList = userinfoMapper.selectUserinfoList(userinfoCon);
					for (com.paradisecloud.fcm.dao.model.Userinfo userinfo : userinfoList) {
						FreeSwitchUser freeSwitchUser = new FreeSwitchUser();
						freeSwitchUser.setId(userinfo.getId().longValue());
						freeSwitchUser.setUserId(userinfo.getUsername());
						freeSwitchUser.setPassword(userinfo.getPassword());
					}
				}
			} catch (Exception e) {
				throw new SystemException(100334, "获取FS用户异常!");
			}finally {
				LOGGER.info("==========================>获取FREESWITCH用户信息成功!");
			}

		return switchUsers;
	}

	@Override
	public List<String> userIdIsRepeat(FreeSwitchUser freeSwitchUser) {
		List<String> userIdLists = new ArrayList<String>();
		if(StringUtils.isNotEmpty(freeSwitchUser.getUserId())) 
		{	
			try {
				if (!userinfo_db_useFcmDb) {
					FcmBridge fcmBridge = null;
					BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(freeSwitchUser.getDeptId());
					if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
						FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
						if (fcmBridgeCluster != null) {
							List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
							// 由于使用固定用户信息数据库，任意一个FCM即可
							fcmBridge = fcmBridges.get(0);
						}
					} else {
						fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
					}
					BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
					UserinfoManager userinfoManager = new UserinfoManager(busiFreeSwitch.getIp());
					if (freeSwitchUser.getUserId().contains(FcmConfig.SPLIT_LINE)) {
						String[] userSplit = freeSwitchUser.getUserId().split(FcmConfig.SPLIT_LINE);
						//检查userId,重复的放入list中
						List<Userinfo> userinfoList = userinfoManager.queryByUsernames(userSplit);
						for (Userinfo userinfo : userinfoList) {
							if (userinfo != null) {
								userIdLists.add(userinfo.getUsername());
							}
						}
					} else {
						//检查userId,重复的放入list中
						Userinfo userinfo = userinfoManager.queryByUsername(freeSwitchUser.getUserId());
						if (userinfo != null) {
							userIdLists.add(userinfo.getUsername());
						}
					}
				} else {
					if (freeSwitchUser.getUserId().contains(FcmConfig.SPLIT_LINE)) {
						String[] userSplit = freeSwitchUser.getUserId().split(FcmConfig.SPLIT_LINE);
						List<com.paradisecloud.fcm.dao.model.Userinfo> userinfoList = userinfoMapper.selectUserinfoByUsernames(userSplit);
						for (com.paradisecloud.fcm.dao.model.Userinfo userinfo : userinfoList) {
							if (userinfo != null) {
								userIdLists.add(userinfo.getUsername());
							}
						}
					} else {
						String[] userSplit = new String[]{freeSwitchUser.getUserId()};
						List<com.paradisecloud.fcm.dao.model.Userinfo> userinfoList = userinfoMapper.selectUserinfoByUsernames(userSplit);
						for (com.paradisecloud.fcm.dao.model.Userinfo userinfo : userinfoList) {
							if (userinfo != null) {
								userIdLists.add(userinfo.getUsername());
							}
						}
					}
				}
			} finally {
				LOGGER.info("==========================>检查FREESWITCH用户是否重复！");
			}
			
		}

		return userIdLists;
	}
}
