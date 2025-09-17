package com.paradisecloud.fcm.license;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
* @author nj
* @date 2022/7/26 9:09
*/
public abstract class AbstractServerInfos {

   public static Logger logger = LogManager.getLogger(AbstractServerInfos.class);

   /**
    * 组装需要额外校验的License参数
    */
   public LicenseCheckModel getServerInfos(){
       LicenseCheckModel result = new LicenseCheckModel();

       try {
           //result.setIpAddress(this.getIpAddress());
           result.setMacAddress(this.getMacAddress());
        //   result.setCpuSerial(this.getCPUSerial());
          // result.setMainBoardSerial(this.getMainBoardSerial());
       }catch (Exception e){
           logger.error("获取服务器硬件信息失败",e);
       }

       return result;
   }

   /**
    * 获取IP地址
    */
   protected abstract List<String> getIpAddress() throws Exception;

   /**
    * 获取Mac地址
    */
   protected abstract List<String> getMacAddress() throws Exception;

   /**
    * 获取CPU序列号
    */
   protected abstract String getCPUSerial() throws Exception;

   /**
    * 获取主板序列号
    */
   protected abstract String getMainBoardSerial() throws Exception;

   /**
    * 获取当前服务器所有符合条件的InetAddress
    */
   protected List<InetAddress> getLocalAllInetAddress() throws Exception {
       List<InetAddress> result = new ArrayList<>(4);

       // 遍历所有的网络接口
       for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
           NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();
           for (Enumeration inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
               InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();

               if(!inetAddr.isLoopbackAddress()
                       && !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()){
                   result.add(inetAddr);
               }
           }
       }

       return result;
   }

   /**
    * 获取某个网络接口的Mac地址
    */
   protected String getMacByInetAddress(InetAddress inetAddr){
       try {
           logger.info("InetAddress "+inetAddr);
           NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(inetAddr);
           logger.info("byInetAddress "+byInetAddress);
           byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
           logger.info("mac "+mac);
           if(mac==null){
               return null;
           }
           StringBuffer stringBuffer = new StringBuffer();

           for(int i=0;i<mac.length;i++){
               if(i != 0) {
                   stringBuffer.append("-");
               }

               //将十六进制byte转化为字符串
               String temp = Integer.toHexString(mac[i] & 0xff);
               if(temp.length() == 1){
                   stringBuffer.append("0" + temp);
               }else{
                   stringBuffer.append(temp);
               }
           }

           return stringBuffer.toString().toUpperCase();
       } catch (SocketException e) {
           e.printStackTrace();
       }

       return null;
   }

}
