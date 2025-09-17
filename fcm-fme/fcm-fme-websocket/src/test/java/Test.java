import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.sinhy.utils.ThreadUtils;

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Test.java
 * Package     : 
 * @author lilinhai 
 * @since 2021-03-23 15:10
 * @version  V1.0
 */

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-03-23 15:10
 * @version V1.0  
 */
public class Test
{
    
    public static void main(String[] args)
    {
        new Thread(()->{
            Socket socket = new Socket();
            try
            {
                socket.connect(new InetSocketAddress("192.166.1.3", 9443), 100);
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();
        ThreadUtils.sleep(100000000);
    }
}
