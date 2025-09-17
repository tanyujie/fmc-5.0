import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : XstreamTest.java
 * Package     : 
 * @author sinhy 
 * @since 2021-08-29 18:23
 * @version  V1.0
 */

/**  
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-08-29 18:23
 * @version V1.0  
 */
public class XstreamTest
{
    
    public static void main(String[] args)
    {
        BusiFme fme = new BusiFme();
        fme.setIp("218.28.249.139");
        fme.setPort(9443);
        fme.setName("138");
        fme.setId(1L);
        fme.setUsername("admin");
        fme.setPassword("P@rad1se");
        FmeBridge fmeBridge = new FmeBridge(fme);
        System.out.println(fmeBridge.getCallBridgeId());
    }
}
