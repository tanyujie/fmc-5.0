package com.paradisecloud.fcm.ops.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author nj
 * @date 2024/6/22 10:14
 */
public class NetPlanConfigGen {

    private static Logger logger = LoggerFactory.getLogger(NetPlanConfigGen.class);


    public static List<String> getNetworkInterfacesByLshw() throws IOException {
        List<String> logicalNames = new ArrayList<>();
        try {
            Process lshwProcess = Runtime.getRuntime().exec("lshw -class system -class network");
            BufferedReader reader = new BufferedReader(new InputStreamReader(lshwProcess.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("logical name:")) {
                    String logicalName = line.split(":")[1].trim();
                    if (!logicalName.equals("docker0") && !logicalName.equals("vnet0") && !logicalName.equals("br0") && !logicalName.equals("virbr0")) {
                        logicalNames.add(logicalName);
                        logger.info("Logical Name: " + logicalName);
                    }

                }
            }
            lshwProcess.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return logicalNames;
    }

    public static List<String> getNetworkInterfacesByLshwNoEnp2s0() throws IOException {
        List<String> logicalNames = new ArrayList<>();
        try {
            Process lshwProcess = Runtime.getRuntime().exec("lshw -class system -class network");
            BufferedReader reader = new BufferedReader(new InputStreamReader(lshwProcess.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("logical name:")) {
                    String logicalName = line.split(":")[1].trim();
                    if (!logicalName.equals("docker0") && !logicalName.equals("vnet0") && !logicalName.equals("br0") && !logicalName.equals("virbr0") && !logicalName.equals("enp2s0") && !logicalName.equals("eth0")) {
                        logicalNames.add(logicalName);
                        logger.info("Logical Name: " + logicalName);
                    }

                }
            }
            lshwProcess.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return logicalNames;
    }


    public static List<String> getNetworkInterfaces() throws IOException {
        List<String> interfaces = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            String name = networkInterface.getName();
            if (!networkInterface.isLoopback() && !name.startsWith("docker") && !name.startsWith("br-") && !name.startsWith("veth")) {
                interfaces.add(name);
                logger.info("Interfaces name:{}", name);
            }
        }
        return interfaces;
    }

    public static void generateNetplanConfig(List<String> interfaces, String addresses, String via, String path, String macaddress) throws IOException {
        Map<String, Object> netplan = new LinkedHashMap<>();

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("version", 2);
        config.put("renderer", "NetworkManager");

        Map<String, Object> ethernets = new LinkedHashMap<>();
        for (String iface : interfaces) {
            Map<String, Object> ifaceConfig = new HashMap<>();
            ifaceConfig.put("dhcp4", false);
            ethernets.put(iface, ifaceConfig);
        }

        Map<String, Object> bridge = new LinkedHashMap<>();
        bridge.put("dhcp4", false);
        bridge.put("interfaces", interfaces);
        bridge.put("addresses", Collections.singletonList(addresses));
        bridge.put("macaddress", macaddress);

        Map<String, Object> nameservers = new LinkedHashMap<>();
        nameservers.put("addresses", Collections.singletonList("114.114.114.114"));
        bridge.put("nameservers", nameservers);

        Map<String, Object> route = new LinkedHashMap<>();
        route.put("to", "default");
        route.put("via", via);
        bridge.put("routes", Collections.singletonList(route));

        Map<String, Object> bridges = new LinkedHashMap<>();
        bridges.put("br0", bridge);

        config.put("ethernets", ethernets);
        config.put("bridges", bridges);

        netplan.put("network", config);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(netplan);

        Files.write(Paths.get(path), output.getBytes());

        logger.info("Netplan configuration generated successfully.");
    }


    public static String getMacAddress(String filePath, String bridgeName) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            // 解析网络配置
            Map<String, Object> network = (Map<String, Object>) config.get("network");
            if (network != null) {
                // 解析bridges
                Map<String, Object> bridges = (Map<String, Object>) network.get("bridges");
                if (bridges != null) {
                    Map<String, Object> bridgeConfig = (Map<String, Object>) bridges.get(bridgeName);
                    if (bridgeConfig != null) {
                        return (String) bridgeConfig.get("macaddress");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getIpAddress(String filePath) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            // 解析网络配置
            // 获取 'network -> bridges -> br0 -> addresses'
            Map<String, Object> network = (Map<String, Object>) config.get("network");
            Map<String, Object> bridges = (Map<String, Object>) network.get("bridges");
            Map<String, Object> br0 = (Map<String, Object>) bridges.get("br0");
            List<String> addresses = (List<String>) br0.get("addresses");
            if (addresses != null) {
                for (String address : addresses) {
                    String ip = address.split("/")[0]; // 只提取IP地址部分
                    return ip;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
