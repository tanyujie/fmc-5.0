package com.paradisecloud.fcm.license;


import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author nj
 * @date 2022/7/26 9:11
 */
public class  LinuxServerInfos extends AbstractServerInfos {

    @Override
    protected List<String> getIpAddress() throws Exception {
        List<String> result = null;

        //获取所有网络接口
        List<InetAddress> inetAddresses = getLocalAllInetAddress();

        if(inetAddresses != null && inetAddresses.size() > 0){
            result = inetAddresses.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
        }

        return result;
    }

    @Override
    protected List<String> getMacAddress() throws Exception {
        List<String> result = new ArrayList<>();
        if(1==2){
            //1. 获取所有网络接口
            List<InetAddress> inetAddresses = getLocalAllInetAddress();
            logger.info("inetAddresses "+inetAddresses);
            if(inetAddresses != null && inetAddresses.size() > 0){
                //2. 获取所有网络接口的Mac地址
                for (InetAddress inetAddress : inetAddresses) {
                    String macByInetAddress = getMacByInetAddress(inetAddress);
                    if(StringUtils.isNotBlank(macByInetAddress)){
                        if(CollectionUtils.isEmpty(result)){
                            result.add(macByInetAddress);
                        }else {
                            if(!result.contains(macByInetAddress)){
                                result.add(macByInetAddress);
                            }
                        }

                    }
                }
            }
        }


        try {
            // 执行 lshw 命令
//            Process lshwProcess = Runtime.getRuntime().exec("lshw | sed -n '/logical name: enp2s0/ {n; n; s/.*\\([0-9A-Fa-f:]\\{17\\}\\).*/\\1/p}'");
//            BufferedReader lshwReader = new BufferedReader(new InputStreamReader(lshwProcess.getInputStream()));
//            String line;
//            StringBuilder lshwOutput = new StringBuilder();
//            while ((line = lshwReader.readLine()) != null) {
//                lshwOutput.append(line).append("\n");
//            }

            Process lshwProcess = Runtime.getRuntime().exec("lshw | sed -n '/logical name: e/ {n; n; s/.*\\([0-9A-Fa-f:]\\{17\\}\\).*/\\1/p}'");
            InputStream inputStream = lshwProcess.getInputStream();
            byte[] data = new byte[1024];
            StringBuilder lshwOutput = new StringBuilder();
            while (inputStream.read(data) != -1) {
                lshwOutput.append(new String(data, "UTF-8"));
            }

            // 解析 lshw 输出
            String lshwOutputString = lshwOutput.toString();

            String enp2s0MacAddress = getEnp2s0MacAddress();
            logger.info("MAC Address lshwOutputString:" + lshwOutputString);
            logger.info("MAC Address enp2s0//br0MacAddress: " + enp2s0MacAddress);
            result.add(enp2s0MacAddress);
            //List<String> networkInterfacesByLshw = getsMacSerialsByLshw();

            // 提取 MAC 地址
            // String macAddress = extractMacAddress(lshwOutputString); // 示例函数



        } catch (IOException e) {
            e.printStackTrace();
        }


        logger.info("result "+result);
        return result;
    }

    @Override
    protected String getCPUSerial() throws Exception {

        //序列号
        String serialNumber = "";
        //使用dmidecode命令获取CPU序列号
        String[] shell = {"/bin/bash","-c","dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if(StringUtils.isNotBlank(line)){
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }

    @Override
    protected String getMainBoardSerial() throws Exception {
        //序列号
        String serialNumber = "";

        //使用dmidecode命令获取主板序列号
        String[] shell = {"/bin/bash","-c","dmidecode | grep 'Serial Number'"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if(StringUtils.isNotBlank(line)){
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }

    protected String getSnSerial() throws Exception {

        try {
            // 执行 lshw 命令
            Process lshwProcess = Runtime.getRuntime().exec("lshw -class system -class network");
            BufferedReader lshwReader = new BufferedReader(new InputStreamReader(lshwProcess.getInputStream()));
            String line;
            StringBuilder lshwOutput = new StringBuilder();
            while ((line = lshwReader.readLine()) != null) {
                lshwOutput.append(line).append("\n");
            }

            // 执行 dmidecode 命令
            Process dmidecodeProcess = Runtime.getRuntime().exec("dmidecode -t system");
            BufferedReader dmidecodeReader = new BufferedReader(new InputStreamReader(dmidecodeProcess.getInputStream()));
            StringBuilder dmidecodeOutput = new StringBuilder();
            while ((line = dmidecodeReader.readLine()) != null) {
                dmidecodeOutput.append(line).append("\n");
            }

            // 解析 lshw 输出
            String lshwOutputString = lshwOutput.toString();
            String cpuSerialNumber = extractSerialNumber(lshwOutputString, "serial:"); // 示例函数

            // 解析 dmidecode 输出
            String dmidecodeOutputString = dmidecodeOutput.toString();
            String motherboardSerialNumber = extractSerialNumber(dmidecodeOutputString, "Serial Number:"); // 示例函数

            // 提取 MAC 地址
            String macAddress = extractMacAddress(lshwOutputString); // 示例函数

            System.out.println("CPU Serial Number: " + cpuSerialNumber);
            System.out.println("Motherboard Serial Number: " + motherboardSerialNumber);
            System.out.println("MAC Address: " + macAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String extractSerialNumber(String output, String key) {
        for (String line : output.split("\n")) {
            if (line.contains(key)) {
                return line.split(key)[1].trim();
            }
        }
        return null;
    }

    private static String extractMacAddress(String output) {
        for (String line : output.split("\n")) {
            if (line.contains("serial:") && line.contains(":")) {
                return line.split("serial:")[1].trim();
            }
        }
        return null;
    }

    public static List<String> getsMacSerialsByLshw() throws IOException {
        List<String> serials = new ArrayList<>();
        try {
            Process lshwProcess = Runtime.getRuntime().exec("lshw -class system -class network");
            BufferedReader reader = new BufferedReader(new InputStreamReader(lshwProcess.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("serial:")) {
                    String serialvalue= line.split(":")[1].trim();
                    serials.add(serialvalue);
                }
            }
            lshwProcess.waitFor();
            reader.close();
        } catch (IOException |InterruptedException  e) {
            e.printStackTrace();
        }
        return serials;
    }


    public static String  getEnp2s0MacAddress(){
        try {
            // 创建一个进程构建器，用于执行lshw命令
            ProcessBuilder processBuilder = new ProcessBuilder("lshw", "-class", "network");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean enp2s0Found = false;
            String macAddress = null;


            if(Objects.equals("windows",ExternalConfigCache.getInstance().getOpsVersion())){
                // 逐行读取输出并查找eth0和MAC地址
                while ((line = reader.readLine()) != null) {
                    if (line.contains("eth0")) {
                        enp2s0Found = true;
                    } else if (enp2s0Found && line.trim().startsWith("serial:")) {
                        macAddress = line.trim().split(" ")[1];
                        break;
                    }
                }
            }else {
                // 逐行读取输出并查找enp2s0和MAC地址
                while ((line = reader.readLine()) != null) {
                    if (line.contains("logical name: e")) {
                        enp2s0Found = true;
                    } else if (enp2s0Found && line.trim().startsWith("serial:")) {
                        macAddress = line.trim().split(" ")[1];
                        break;
                    }
                }
            }

            reader.close();

            // 打印MAC地址
            if (macAddress != null) {
                logger.info("MAC Address of br0: " + macAddress);
                return macAddress;
            } else {
                logger.info("br0 or its MAC address not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
