package com.paradisecloud.fcm.fme.apiservice.impls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.config.ApplicationConfig;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.dao.mapper.BusiProfileCallBrandingMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileCallBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallBrandingService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;
import com.paradisecloud.fcm.fme.model.response.callbranding.ActiveCallBrandingProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.callbranding.CallBrandingProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callbranding.CallBrandingProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.FileUtils;
import com.sinhy.utils.HostUtils;
import com.sinhy.utils.IOUtils;

/**
 * callBranding模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@Service
public class BusiProfileCallBrandingServiceImpl implements IBusiProfileCallBrandingService 
{
    @Autowired
    private BusiProfileCallBrandingMapper busiProfileCallBrandingMapper;

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, CallBrandingProfileProcessor callBrandingProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            CallBrandingProfilesResponse response = fmeBridge.getCallBrandingProfileInvoker().getCallBrandingProfiles(offset);
            if (response != null)
            {
                ActiveCallBrandingProfilesResponse activeCallBrandingProfilesResponse = response.getCallBrandingProfiles();
                if (activeCallBrandingProfilesResponse != null)
                {
                    List<CallBrandingProfile> callLegProfile = activeCallBrandingProfilesResponse.getCallBrandingProfile();
                    if (callLegProfile != null)
                    {
                        for (CallBrandingProfile callLegProfile2 : callLegProfile)
                        {
                            CallBrandingProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallBrandingProfileInvoker().getCallBrandingProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCallBrandingProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getCallBrandingProfile();
                                callBrandingProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeCallBrandingProfilesResponse.getTotal();
                        totalCount.addAndGet(callLegProfile.size());
                        if (totalCount.get() < total.intValue())
                        {
                            offset = totalCount.get();
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
    }
    
    public List<ModelBean> getAllCallBrandingProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileCallBranding condition = new BusiProfileCallBranding();
        condition.setDeptId(deptId);
        List<BusiProfileCallBranding> busiProfiles = busiProfileCallBrandingMapper.selectBusiProfileCallBrandingList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiProfileCallBranding profile : busiProfiles)
            {
                CallBrandingProfile fp = mainMaxPriorityFmeHttpInvoker.getDataCache().getCallBrandingProfile(profile.getCallBrandingProfileUuid());
                if (fp == null)
                {
                    busiProfileCallBrandingMapper.deleteBusiProfileCallBrandingById(profile.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(fp);
                    mb.put("deptId", tmpDeptId);
                    mb.put("name", profile.getName());
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }
    
    /**
     * 查询callBranding模板
     * 
     * @param id callBranding模板ID
     * @return callBranding模板
     */
    @Override
    public BusiProfileCallBranding selectBusiProfileCallBrandingById(Long id)
    {
        return busiProfileCallBrandingMapper.selectBusiProfileCallBrandingById(id);
    }

    /**
     * 查询callBranding模板列表
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return callBranding模板
     */
    @Override
    public List<BusiProfileCallBranding> selectBusiProfileCallBrandingList(BusiProfileCallBranding busiProfileCallBranding)
    {
        return busiProfileCallBrandingMapper.selectBusiProfileCallBrandingList(busiProfileCallBranding);
    }

    /**
     * 新增callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding) throws IOException
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCallBranding.getName()), "name不能为空");
        Long deptId = busiProfileCallBranding.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCallBranding.getDeptId();
        
        String outputFolderPath = "/mnt/nfs/callBranding/" + IdUtils.randomUUID();
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String fileName = busiProfileCallBranding.getParams().get("fileName").toString();
        fileName = fileName.replaceAll("^/profile/upload", "");
        String filepath = ApplicationConfig.getUploadPath() + fileName;
        
        // 解压
        try
        {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(filepath));)
            {
                ZipEntry zipEntry = null;
                while ((zipEntry = zis.getNextEntry()) != null)
                {
                    if (!zipEntry.isDirectory())
                    {
                        File targetFile = new File(outputFolder, zipEntry.getName());
                        FileUtils.createDir(targetFile.getParentFile());
                        IOUtils.copy(zis.readAllBytes(), new FileOutputStream(targetFile));
                    }
                    else
                    {
                        throw new RuntimeException("ZIP文件中不能包含目录，请去除重新上传！");
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            FileUtils.delete(filepath);
            FileUtils.delete(outputFolderPath);
            throw e;
        }
        
        FileUtils.delete(filepath);
        String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
        String ip = null;
        if (StringUtils.isEmpty(fmcRootUrl)) {
            List<String> ips = HostUtils.getLocalIPAddresses();
            ip = ips.get(0);
        } else {
            String substringAfter = StringUtils.substringAfter(fmcRootUrl, "://");
            ip = StringUtils.substringBefore(substringAfter, ":");
        }
        busiProfileCallBranding.getParams().put("resourceLocation", outputFolderPath.replaceAll("^/mnt/nfs", "http://" + ip + ":8888"));
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCallBranding, nameValuePairs);
        
        String profileId = fmeBridge.getCallBrandingProfileInvoker().createCallBrandingProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建CallBranding profile失败");
        }
        
        CallBrandingProfileInfoResponse profileInfoResponse = fmeBridge.getCallBrandingProfileInvoker().getCallBrandingProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getCallBrandingProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getCallBrandingProfile());
                }
            });
        }
        
        busiProfileCallBranding.getParams().put("id", profileId);
        busiProfileCallBranding.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileCallBranding.setCreateTime(new Date());
        busiProfileCallBranding.setDeptId(deptId);
        busiProfileCallBranding.setCallBrandingProfileUuid(profileId);
        busiProfileCallBrandingMapper.insertBusiProfileCallBranding(busiProfileCallBranding);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    /**
     * 修改callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @Override
    public int updateBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding) throws IOException
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCallBranding.getName()), "name不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCallBranding.getDeptId());
        
        CallBrandingProfile cbp = fmeBridge.getDataCache().getCallBrandingProfile(busiProfileCallBranding.getParams().get("id").toString());
        if (!ObjectUtils.isEmpty(cbp.getResourceLocation()))
        {
            String rl = "/mnt/nfs" + cbp.getResourceLocation().substring(cbp.getResourceLocation().indexOf(":8888") + 5);
            FileUtils.delete(rl);
        }
        
        String outputFolderPath = "/mnt/nfs/callBranding/" + IdUtils.randomUUID();
        File outputFolder = new File(outputFolderPath);
        String fileName = busiProfileCallBranding.getParams().get("fileName").toString();
        fileName = fileName.replaceAll("^/profile/upload", "");
        String filepath = ApplicationConfig.getUploadPath() + fileName;
        // 解压
        try
        {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(filepath));)
            {
                ZipEntry zipEntry = null;
                while ((zipEntry = zis.getNextEntry()) != null)
                {
                    if (!zipEntry.isDirectory())
                    {
                        File targetFile = new File(outputFolder, zipEntry.getName());
                        FileUtils.createDir(targetFile.getParentFile());
                        IOUtils.copy(zis.readAllBytes(), new FileOutputStream(targetFile));
                    }
                    else
                    {
                        throw new RuntimeException("ZIP文件中不能包含目录，请去除重新上传！");
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            FileUtils.delete(filepath);
            FileUtils.delete(outputFolderPath);
            throw e;
        }
        
        FileUtils.delete(filepath);
        
        List<String> ips = HostUtils.getLocalIPAddresses();
        busiProfileCallBranding.getParams().put("resourceLocation", outputFolderPath.replaceAll("^/mnt/nfs", "http://" + ips.get(0) + ":8888"));
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCallBranding, nameValuePairs);
        RestResponse restResponse = fmeBridge.getCallBrandingProfileInvoker().updateCallBrandingProfile(busiProfileCallBranding.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            CallBrandingProfileInfoResponse profileInfoResponse = fmeBridge.getCallBrandingProfileInvoker().getCallBrandingProfile(busiProfileCallBranding.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getCallBrandingProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileCallBranding.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getCallBrandingProfile());
                    }
                });
            }
            
            long deptId = busiProfileCallBranding.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCallBranding.getDeptId();
            busiProfileCallBranding.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileCallBranding condition = new BusiProfileCallBranding();
            condition.setDeptId(deptId);
            condition.setCallBrandingProfileUuid(busiProfileCallBranding.getParams().get("id").toString());
            List<BusiProfileCallBranding> busiProfiles = busiProfileCallBrandingMapper.selectBusiProfileCallBrandingList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileCallBranding dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    dtmf.setName(busiProfileCallBranding.getName());
                    busiProfileCallBrandingMapper.updateBusiProfileCallBranding(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:08 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallBrandingService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileCallBrandingMapper.getDeptRecordCounts();
    }

    private void buildParams(BusiProfileCallBranding busiProfileCallBranding, List<NameValuePair> nameValuePairs)
    {
//        Assert.isTrue(busiProfileCallBranding.getParams().containsKey("invitationTemplate"), "invitationTemplate不能为空");
        Assert.isTrue(busiProfileCallBranding.getParams().containsKey("resourceLocation"), "resourceLocation不能为空");
//        nameValuePairs.add(new BasicNameValuePair("invitationTemplate", busiProfileCallBranding.getParams().get("invitationTemplate").toString()));
        nameValuePairs.add(new BasicNameValuePair("resourceLocation", busiProfileCallBranding.getParams().get("resourceLocation").toString()));
    }
    
    /**
     * 批量删除callBranding模板
     * 
     * @param ids 需要删除的callBranding模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileCallBrandingByIds(Long[] ids)
    {
        return busiProfileCallBrandingMapper.deleteBusiProfileCallBrandingByIds(ids);
    }

    /**
     * 删除callBranding模板信息
     * 
     * @param id callBranding模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileCallBrandingById(BusiProfileCallBranding busiProfileCallBranding)
    {
        Object idObj = busiProfileCallBranding.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCallBranding.getDeptId());
        CallBrandingProfile cbp = fmeBridge.getDataCache().getCallBrandingProfile(busiProfileCallBranding.getParams().get("id").toString());
        if (!ObjectUtils.isEmpty(cbp.getResourceLocation()))
        {
            String rl = "/mnt/nfs" + cbp.getResourceLocation().substring(cbp.getResourceLocation().indexOf(":8888") + 5);
            FileUtils.delete(rl);
        }
        
        RestResponse result = fmeBridge.getCallBrandingProfileInvoker().deleteCallBrandingProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteCallBrandingProfile(idObj.toString());
            BusiProfileCallBranding profileDeleteCon = new BusiProfileCallBranding();
            profileDeleteCon.setCallBrandingProfileUuid(idObj.toString());
            List<BusiProfileCallBranding> ps = busiProfileCallBrandingMapper.selectBusiProfileCallBrandingList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileCallBranding d : ps)
                {
                    busiProfileCallBrandingMapper.deleteBusiProfileCallBrandingById(d.getId());
                }
            }
        }
        return result;
    }
}
