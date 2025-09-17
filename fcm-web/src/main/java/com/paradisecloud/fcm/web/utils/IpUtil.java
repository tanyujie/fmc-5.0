package com.paradisecloud.fcm.web.utils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author nj
 * @date 2024/5/28 8:49
 */
public class IpUtil {

    public static String incrementLastOctet(String ipAddress) {
        String[] parts = ipAddress.split("\\.");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IP address format");
        }

        try {
            int lastOctet = Integer.parseInt(parts[3]);
            lastOctet++;

            if (lastOctet > 255) {
                throw new IllegalArgumentException("Last octet exceeds 255 after increment");
            }

            parts[3] = String.valueOf(lastOctet);
            return String.join(".", parts);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in IP address", e);
        }
    }

    public static int subnetMaskToCIDR(String subnetMask) {
        int cidr = 0;
        String[] octets = subnetMask.split("\\.");
        for (String octet : octets) {
            int value = Integer.parseInt(octet);
            while (value > 0) {
                cidr += (value & 1);
                value >>= 1;
            }
        }
        return cidr;
    }

    public static String calculateNetworkAddress(String ip, String subnetMask) throws UnknownHostException {
        byte[] ipAddr = InetAddress.getByName(ip).getAddress();
        byte[] maskAddr = InetAddress.getByName(subnetMask).getAddress();
        byte[] network = new byte[ipAddr.length];
        for (int i = 0; i < ipAddr.length; i++) {
            network[i] = (byte) (ipAddr[i] & maskAddr[i]);
        }
        return InetAddress.getByAddress(network).getHostAddress();
    }

    public static String calculateBroadcastAddress(String ip, String subnetMask) throws UnknownHostException {
        byte[] ipAddr = InetAddress.getByName(ip).getAddress();
        byte[] maskAddr = InetAddress.getByName(subnetMask).getAddress();
        byte[] broadcast = new byte[ipAddr.length];
        for (int i = 0; i < ipAddr.length; i++) {
            broadcast[i] = (byte) (ipAddr[i] | ~maskAddr[i]);
        }
        return InetAddress.getByAddress(broadcast).getHostAddress();
    }

    public static int calculateNumberOfHosts(int cidr) {
        return (int) Math.pow(2, 32 - cidr) - 2;
    }


    public static String getLocalIp()   {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

}
