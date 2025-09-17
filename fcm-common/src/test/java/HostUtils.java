/*
 * <p>Copyright : LinHai Technologies Co., Ltd. Copyright 2018, All right reserved.</p>
 * <p>Description : <pre>TODO(用一句话描述该文件做什么)</pre></p> <p>FileName : CusAccessObjectUtils.java</p>
 * <p>Package : com.meme.crm.web.util</p>
 * 
 * @Creator lilinhai 2018年4月20日 上午4:49:28
 * 
 * @Version V1.0
 */



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * ip地址获取工具
 * <p>
 * ClassName : IpAddressUtils
 * </p>
 * 
 * @author lilinhai 2018年10月8日 下午4:24:16
 * @version 1.0
 */
public abstract class HostUtils
{
    
    /**
     * 判断ip、端口是否可连接
     * <p>
     * Title : isHostConnectable lilinhai 2019年1月21日 下午9:19:15
     * </p>
     * 
     * @param host
     * @param port
     * @return
     *         boolean
     */
    public static boolean isHostConnectable(String host, int port)
    {
        return isHostConnectable(host, port, 50);
    }
    
    /**
     * <pre>判断ip、端口是否可连接</pre>
     * @author lilinhai
     * @since 2020-07-27 12:23 
     * @param host
     * @param port
     * @param timeout
     * @return boolean
     */
    public static boolean isHostConnectable(String host, int port, int timeout)
    {
        try (Socket socket = new Socket();)
        {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        }
        catch (Throwable e)
        {
//            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public static void copy(String in, OutputStream out, Charset charset) throws IOException
    {
        try (Writer writer = new OutputStreamWriter(out, charset))
        {
            writer.write(in);
        }
    }
    
    public static void main(String[] args)
    {
        List<String> ips = new ArrayList<>();
        ips.add("172.16.100.147");
        ips.add("172.16.100.125");
        ips.add("172.16.100.135");
        ips.add("172.16.100.247");
        File outputFile = new File("/home/test-result.log");
        long i = 0;
        while (true)
        {
            try
            {
                OutputStream out = new FileOutputStream(outputFile, true);
                Thread.sleep(800);
                StringBuilder result = new StringBuilder();
                for (String ip : ips)
                {
                    result.append(ip + "--- socket-test-result: " + isHostConnectable(ip, 5060, 3000)).append("--- reachable-test-result: " + isHostReachable(ip, 1000)).append("\n");
                }
                result.append("---------------------------------------------------------------------------------------: " + i++).append("\n");
                System.out.println(result);
                copy(result.toString(), out, StandardCharsets.UTF_8);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 判断ip是否可以连接 timeOut是超时时间
     * <p>
     * Title : isHostReachable lilinhai 2019年1月21日 下午9:20:05
     * </p>
     * 
     * @param host
     * @param timeOut
     * @return
     *         boolean
     */
    public static boolean isHostReachable(String host, Integer timeOut)
    {
        try
        {
            return InetAddress.getByName(host).isReachable(timeOut);
        }
        catch (Throwable e)
        {
            return false;
        }
    }
    
    /**
     * 获取本机MAC地址
     * <p>
     * Title : getLocalMacAddress lilinhai 2019年1月22日 下午9:22:14
     * </p>
     * 
     * @return
     * @throws Exception
     *                       String
     */
    public static String getLocalMacAddress() throws Exception
    {
        // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        InetAddress ia = InetAddress.getLocalHost();
        
        // 下面代码是把mac地址拼装成String
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        if (mac == null)
        {
            return null;
        }
        return toMac(mac);
    }
    
    private static String toMac(byte[] mac)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mac.length; i++)
        {
            if (i != 0)
            {
                sb.append("-");
            }
            // mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }
    
    // ‎00-24-7E-0A-22-93
    public static String getMac()
    {
        try
        {
            Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements())
            {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null) continue;
                return toMac(mac);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }
}
