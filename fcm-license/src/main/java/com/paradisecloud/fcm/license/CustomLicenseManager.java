package com.paradisecloud.fcm.license;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.utils.PropertiesUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.sinhy.spring.BeanFactory;
import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.beans.XMLDecoder;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * 服务器信息校验
 * @author nj
 * @date 2022/7/26 9:17
 */
public class CustomLicenseManager extends LicenseManager {




    private static Logger logger = LogManager.getLogger(CustomLicenseManager.class);

    //XML编码
    private static final String XML_CHARSET = "UTF-8";
    //默认BUFSIZE
    private static final int DEFAULT_BUFSIZE = 8 * 1024;

    public CustomLicenseManager() {

    }

    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }


    @Override
    protected synchronized byte[] create(
            LicenseContent content,
            LicenseNotary notary)
            throws Exception {
        initialize(content);
        this.validateCreate(content);
        final GenericCertificate certificate = notary.sign(content);
        return getPrivacyGuard().cert2key(certificate);
    }

    /**
     * 复写install方法，其中validate方法调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     */
    @Override
    protected synchronized LicenseContent install(
            final byte[] key,
            final LicenseNotary notary)
            throws Exception {
        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

        notary.verify(certificate);
        final LicenseContent content = (LicenseContent)this.load(certificate.getEncoded());
        this.validate(content);
        setLicenseKey(key);
        setCertificate(certificate);

        return content;
    }

    /**
     * 复写verify方法，调用本类中的validate方法，校验IP地址、Mac地址等其他信息
     */
    @Override
    protected synchronized LicenseContent verify(final LicenseNotary notary)
            throws Exception {
        GenericCertificate certificate = getCertificate();

        // Load license key from preferences,
        final byte[] key = getLicenseKey();
        if (null == key){
            throw new NoLicenseInstalledException(getLicenseParam().getSubject());
        }

        certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent)this.load(certificate.getEncoded());
        this.validate(content);
        setCertificate(certificate);

        return content;
    }

    /**
     * 校验生成证书的参数信息
     */
    protected synchronized void validateCreate(final LicenseContent content)
            throws LicenseContentException {
        final LicenseParam param = getLicenseParam();

        final Date now = new Date();
        final Date notBefore = content.getNotBefore();
        final Date notAfter = content.getNotAfter();
        if (null != notAfter && now.after(notAfter)){
            throw new LicenseContentException("证书失效时间不能早于当前时间");
        }
        if (null != notBefore && null != notAfter && notAfter.before(notBefore)){
            throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
        }
        final String consumerType = content.getConsumerType();
        if (null == consumerType){
            throw new LicenseContentException("用户类型不能为空");
        }
    }


    /**
     * 复写validate方法，增加IP地址、Mac地址等其他信息校验
     */
    @Override
    protected synchronized void validate(final LicenseContent content)
            throws LicenseContentException {
        //1. 首先调用父类的validate方法
        super.validate(content);

        //2. 然后校验自定义的License参数
        //License中可被允许的参数信息
        Object extra = content.getExtra();
        Map<String,Object> objectsMap=(Map<String, Object>) extra;

        LicenseCheckModel expectedCheckModel = mapToObj(objectsMap,LicenseCheckModel.class) ;
        //当前服务器真实的参数信息
        LicenseCheckModel serverCheckModel = getServerInfos();
        if(expectedCheckModel != null && serverCheckModel != null){
            //校验IP地址
//            if(!checkIpAddress(expectedCheckModel.getIpAddress(),serverCheckModel.getIpAddress())){
//                throw new LicenseContentException("当前服务器的IP没在授权范围内");
//            }
            if(1==1){
                String sn = expectedCheckModel.getSn();
                logger.info("sn===================================:"+sn);


                String opsVersion = ExternalConfigCache.getInstance().getOpsVersion();
                if(Objects.equals("opsVersion",opsVersion)){
                    BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
                    List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
                    if(!CollectionUtils.isEmpty(busiOpsInfos)){
                        String sn1 = busiOpsInfos.get(0).getSn();
                        if(!sn1.equals(sn)){
                            logger.info("sn===================================:"+sn1);
                            throw new LicenseContentException("当前服务器没在授权范围内");
                        }
                    }else {
                        throw new LicenseContentException("当前服务器没在授权范围内");
                    }
                }else {
                    String    osName = System.getProperty("os.name");
                    osName = osName.toLowerCase();
                    AbstractServerInfos abstractServerInfos = null;
                    if (osName.startsWith("windows")) {
                        abstractServerInfos = new WindowsServerInfos();
                    } else if (osName.startsWith("linux")) {
                        abstractServerInfos = new LinuxServerInfos();
                    }else{//其他服务器类型
                        abstractServerInfos = new LinuxServerInfos();
                    }
                    try {
                        String content_t = JSONObject.toJSONString(abstractServerInfos.getServerInfos());

                        String s = AESUtil.encryptAESByte2HexStr(content_t, FmcVerInfoController.LICENSE_PUBLIC_KEY);
                        // 获取字符串的前20位
                        //  String first20 = s.length() > 20 ? s.substring(0, 20) : s;
                        // 获取字符串的后20位
                        String last20 = s.length() > 40 ? s.substring(s.length() - 40) : s;
                        //      String sn_local = first20+last20;
                        if(!last20.equals(sn)){
                            logger.info("sn===================================:"+sn);
                            logger.info("ESN==================================:"+last20);
                            throw new LicenseContentException("当前服务器没在授权范围内");
                        }
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }


            if (1 == 2) {
                //校验Mac地址
                if (!checkIpAddress(expectedCheckModel.getMacAddress(), serverCheckModel.getMacAddress())) {
                    List<String> macAddress = serverCheckModel.getMacAddress();
                    for (String address : macAddress) {
                        logger.info("serverCheckModel:" + address);
                    }
                    List<String> macAddress1 = expectedCheckModel.getMacAddress();
                    for (String address : macAddress1) {
                        logger.info("expectedCheckModel:" + address);
                    }
                    throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
                }

                //校验主板序列号
                if (!checkSerial(expectedCheckModel.getMainBoardSerial(), serverCheckModel.getMainBoardSerial())) {
                    throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
                }

                //校验CPU序列号
                if (!checkSerial(expectedCheckModel.getCpuSerial(), serverCheckModel.getCpuSerial())) {
                    throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
                }

            }


        }else{
            throw new LicenseContentException("不能获取服务器硬件信息");
        }
    }


    /**
     * 重写XMLDecoder解析XML
     * @param encoded XML类型字符串
     */
    private Object load(String encoded){
        BufferedInputStream inputStream = null;
        XMLDecoder decoder = null;
        try {
            inputStream = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));

            decoder = new XMLDecoder(new BufferedInputStream(inputStream, DEFAULT_BUFSIZE),null,null);

            return decoder.readObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                if(decoder != null){
                    decoder.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("XMLDecoder解析XML失败",e);
            }
        }

        return null;
    }

    /**
     * 获取当前服务器需要额外校验的License参数
     */
    private LicenseCheckModel getServerInfos(){
        //操作系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return abstractServerInfos.getServerInfos();
    }

    /**
     * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
     * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
     */
    private boolean checkIpAddress(List<String> expectedList,List<String> serverList){
        if(expectedList != null && expectedList.size() > 0){
            if(serverList != null && serverList.size() > 0){
                for(String expected : expectedList){
                    if(serverList.contains(expected.trim())){
                        return true;
                    }
                }
            }

            return false;
        }else {
            return true;
        }
    }

    /**
     * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
     */
    private boolean checkSerial(String expectedSerial,String serverSerial){
        if(StringUtils.isNotBlank(expectedSerial)){
            if(StringUtils.isNotBlank(serverSerial)){
                if(expectedSerial.equals(serverSerial)){
                    return true;
                }
            }

            return false;
        }else{
            return true;
        }
    }

    public  static<T> T  mapToObj(Map source,Class<T> target) {
        Field[] fields = target.getDeclaredFields();
        T t = null;
        try {
            t = target.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Field field:fields){
            Object val;
            if((val=source.get(field.getName()))!=null){
                field.setAccessible(true);
                try {
                    field.set(t,val);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;

    }

}


