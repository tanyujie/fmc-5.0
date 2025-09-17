package com.paradisecloud.fcm.ops.utils;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nj
 * @date 2024/5/28 8:49
 */
public class IpUtil {

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

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

    public static boolean validateIP(final String ip) {
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean isReachable(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(5000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
