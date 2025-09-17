package com.paradisecloud.fcm.terminal.fs.impls;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.mapper.BusiSipAccountMapper;
import com.paradisecloud.fcm.dao.model.BusiSipAccount;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiSipAccountService;

/**
 * sip账号信息Service业务层处理
 * 
 * @author zyz
 * @date 2021-09-24
 */
@Transactional
@Service("busiSipAccountService")
public class BusiSipAccountServiceImpl implements IBusiSipAccountService 
{
    @Autowired
    private BusiSipAccountMapper busiSipAccountMapper;

    /**
     * 查询sip账号信息
     * 
     * @param id sip账号信息ID
     * @return sip账号信息
     */
    @Override
    public BusiSipAccount selectBusiSipAccountById(Long id)
    {
        return busiSipAccountMapper.selectBusiSipAccountById(id);
    }

    /**
     * 查询sip账号信息列表
     * 
     * @param busiSipAccount sip账号信息
     * @return sip账号信息
     */
    @Override
    public List<ModelBean> selectBusiSipAccountList(BusiSipAccount busiSipAccount)
    {
    	List<ModelBean> modelBeans = new ArrayList<ModelBean>();
//    	BusiTerminal busiTerminal = new BusiTerminal();
//    	busiTerminal.setDeptId(busiSipAccount.getDeptId());
//    	List<BusiTerminal> selectBusiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
//    	if(null != selectBusiTerminalList && selectBusiTerminalList.size() > 0) {
//    		for (BusiTerminal busiTerminal2 : selectBusiTerminalList) {
//    			ModelBean modelBean = new ModelBean(busiTerminal2);
//    			modelBean.remove("createTime");
//    			modelBean.remove("updateTime");
//    			modelBean.remove("createUserId");
//    			modelBean.remove("createUserName");
//    			modelBean.remove("ip");
//    			modelBean.remove("number");
//    			modelBean.remove("cameraIp");
//    			modelBean.remove("type");
//    			modelBean.remove("onlineStatus");
//    			modelBean.remove("credential");
//    			modelBean.remove("password");
//    			modelBean.remove("fsbcServerId");
//    			modelBean.remove("protocol");
//    			modelBean.remove("vendor");
//    			modelBean.remove("registrationTime");
//    			modelBean.remove("intranetIp");
//    			modelBean.remove("port");
//    			modelBean.remove("transport");
//    			modelBean.remove("id");
//    			
//    			BusiSipAccount busiSipAccount2 = new BusiSipAccount();
//    			busiSipAccount2.setTerminalId(busiTerminal2.getId());
//    			List<BusiSipAccount> busiSipAccounts = busiSipAccountMapper.selectBusiSipAccountList(busiSipAccount2);
//    			if(null != busiSipAccounts && busiSipAccounts.size() > 0) {
//    				BusiSipAccount sipAccount = busiSipAccounts.get(0);
//    				modelBean.put(SipAccountProperty.ID, sipAccount.getId());
//    				modelBean.put(SipAccountProperty.TERMINAL_ID, busiTerminal2.getId());
//    				modelBean.put(SipAccountProperty.PROXY_SERVER, sipAccount.getProxyServer());
//    				modelBean.put(SipAccountProperty.SIP_PASSWORD, sipAccount.getSipPassword());
//    				modelBean.put(SipAccountProperty.SIP_PORT, sipAccount.getSipPort());
//    				modelBean.put(SipAccountProperty.SIP_SERVER, sipAccount.getSipServer());
//    				modelBean.put(SipAccountProperty.SIP_USER_NAME, sipAccount.getSipUserName());
//    				modelBean.put(SipAccountProperty.STUN_PORT, sipAccount.getStunPort());
//    				modelBean.put(SipAccountProperty.TURN_SERVER, sipAccount.getTurnServer());
//    				modelBean.put(SipAccountProperty.TURN_USER_NAME, sipAccount.getTurnUserName());
//    				modelBean.put(SipAccountProperty.TURN_PORT, sipAccount.getTurnPort());
//    				modelBean.put(SipAccountProperty.STUN_SERVER, sipAccount.getStunServer());
//    				modelBean.put(SipAccountProperty.TURN_PASSWORD, sipAccount.getTurnPassword());
//    				modelBeans.add(modelBean);
//    			}
//    			
//			}
//    	}
    	
    	return modelBeans;
    }

    /**
     * 新增sip账号信息
     * 
     * @param busiSipAccount sip账号信息
     * @return 结果
     */
//    @Override
//    public int insertBusiSipAccount(BusiSipAccount busiSipAccount)
//    {
//    	if(StringUtils.isEmpty(busiSipAccount.getSipServer())) 
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.SIP_SERVER);
//    	}
//    	
//    	if(!RegExpUtils.isIP(busiSipAccount.getSipServer())) {
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, SipAccountTip.IP_FORMAT);
//    	}
//    	
//    	if(null == busiSipAccount.getSipPort()) 
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.SIP_PORT);
//    	}
//    	
//    	if(null == busiSipAccount.getSipUserName())
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.SIP_USER_NAME);
//    	}
//    	
//    	if(StringUtils.isEmpty(busiSipAccount.getSipPassword()))
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.SIP_PASSWORD);
//    	}
//    	
//    	if(StringUtils.isEmpty(busiSipAccount.getTurnServer()))
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.TURN_SERVER);
//    	}
//    	
//    	if(!RegExpUtils.isIP(busiSipAccount.getTurnServer())) {
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, SipAccountTip.IP_FORMAT);
//    	}
//    	
//    	if(null == busiSipAccount.getTurnPort())
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.TURN_PORT);
//    	}
//    	
//    	if(StringUtils.isEmpty(busiSipAccount.getStunServer()))
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.STUN_SERVER);
//    	}
//    	
//    	if(!RegExpUtils.isIP(busiSipAccount.getStunServer())) {
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ONE_TH, SipAccountTip.IP_FORMAT);
//    	}
//    	
//    	if(null == busiSipAccount.getStunPort())
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.STUN_PORT);
//    	}
//    	
//    	if(StringUtils.isEmpty(busiSipAccount.getTurnUserName()))
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.TURN_USER_NAME);
//    	}
//    	
//    	if(StringUtils.isEmpty(busiSipAccount.getTurnPassword()))
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.TURN_PASSWORD);
//    	}
//    	
//    	if(null == busiSipAccount.getTerminalId())
//    	{
//    		new SystemException(FcmConfigConstant.EXCEPTION_ONE_ZERO, SipAccountTip.TERMINAL_ID);
//    	}
//    	
//        busiSipAccount.setCreateTime(new Date());
//        busiSipAccount.setTerminalId(busiSipAccount.getTerminalId());
//        busiSipAccount.setProxyServer(busiSipAccount.getSipServer() + FcmConfigConstant.COLON + busiSipAccount.getSipPort());
//        return busiSipAccountMapper.insertBusiSipAccount(busiSipAccount);
//    }

    /**
     * 修改sip账号信息
     * 
     * @param busiSipAccount sip账号信息
     * @return 结果
     */
    @Override
    public int updateBusiSipAccount(BusiSipAccount busiSipAccount)
    {
    	int s = 0;
//    	JSONObject jsonObject = new JSONObject();
//		JSONObject jObject = new JSONObject();
//		if(null != busiSipAccount) {
//			BusiSipAccount account = busiSipAccountMapper.selectBusiSipAccountById(busiSipAccount.getId());
//			if(null != account) {
//				String terminalTopic = FcmConfigConstant.TOPIC_PREFIX + account.getSn();
//				account.setUpdateTime(new Date());
//				account.setProxyServer(busiSipAccount.getSipServer() + FcmConfigConstant.COLON + busiSipAccount.getSipPort());
//				account.setSipPassword(busiSipAccount.getSipPassword());
//				account.setSipPort(busiSipAccount.getSipPort());
//				account.setSipServer(busiSipAccount.getSipServer());
//				account.setSipUserName(busiSipAccount.getSipUserName());
//				account.setStunPort(busiSipAccount.getStunPort());
//				account.setStunServer(busiSipAccount.getStunServer());
//				account.setTurnPassword(busiSipAccount.getTurnPassword());
//				account.setTurnPort(busiSipAccount.getTurnPort());
//				account.setTurnServer(busiSipAccount.getTurnServer());
//				account.setTurnUserName(busiSipAccount.getTurnUserName());
//				
//				s = busiSipAccountMapper.updateBusiSipAccount(account);
//				if(s > 0) {
//		        	BusiTerminal busiTerminal = new BusiTerminal();
//		        	busiTerminal.setSn(account.getSn());
//		        	List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
//		        	if (null != busiTerminalList && busiTerminalList.size() > 0) {
//		        		jsonObject.put(SipAccountProperty.TURN_USER_NAME, account.getTurnUserName());
//		            	jsonObject.put(SipAccountProperty.TURN_PASSWORD, account.getTurnPassword());
//		    			jsonObject.put(SipAccountProperty.SIP_SERVER, account.getSipServer());
//		    			jsonObject.put(SipAccountProperty.SIP_PORT, account.getSipPort());
//		    			jsonObject.put(SipAccountProperty.PROXY_SERVER, account.getSipServer() + FcmConfigConstant.COLON + account.getSipPort());
//		    			jsonObject.put(SipAccountProperty.DISPLAY_NAME, busiTerminalList.get(0).getName());
//		    			jsonObject.put(SipAccountProperty.SIP_USER_NAME, busiTerminalList.get(0).getNumber());
//		    			jsonObject.put(SipAccountProperty.SIP_PASSWORD, account.getSipPassword());
//		    			jsonObject.put(SipAccountProperty.TURN_SERVER, account.getTurnServer());
//		    			jsonObject.put(SipAccountProperty.TURN_PORT, account.getTurnPort());
//		    			jsonObject.put(SipAccountProperty.STUN_SERVER, account.getStunServer());
//		    			jsonObject.put(SipAccountProperty.STUN_PORT, account.getStunPort());
//		    			jsonObject.put(SipAccountProperty.EXTRA, null);
//		            	
//		            	jObject.put(FcmConfigConstant.CODE, ResponseInfo.CODE_200);
//		    			jObject.put(FcmConfigConstant.MSG, ResponseInfo.SUCCESS);
//		    			jObject.put(FcmConfigConstant.ACTION, TerminalTopic.GET_SIP_ACCOUNT);
//		    			jObject.put(FcmConfigConstant.JSON_DATA_STR, jsonObject);
//		            	terminalActionService.publishTopicMsg(terminalTopic, account.getSn(), jObject.toString(), false);
//					}
//		        }
//		        
//			}
//		}
		
		return s;
    }

    /**
     * 批量删除sip账号信息
     * 
     * @param ids 需要删除的sip账号信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiSipAccountByIds(Long[] ids)
    {
        return busiSipAccountMapper.deleteBusiSipAccountByIds(ids);
    }

    /**
     * 删除sip账号信息信息
     * 
     * @param id sip账号信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiSipAccountById(Long id)
    {
        return busiSipAccountMapper.deleteBusiSipAccountById(id);
    }
}
