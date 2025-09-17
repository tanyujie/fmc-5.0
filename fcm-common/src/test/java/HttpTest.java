import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.utils.ThreadUtils;

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : HttpTest.java
 * Package     : 
 * @author lilinhai 
 * @since 2020-12-28 12:11
 * @version  V1.0
 */

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2020-12-28 12:11
 * @version V1.0  
 */
public class HttpTest
{
    
    public static void main(String[] args)
    {
        System.out.println(System.currentTimeMillis());
        HttpRequester hr = HttpObjectCreator.getInstance().createHttpRequester("admin", "P@rad1se", true);
        long start = System.currentTimeMillis();
        hr.get("https://172.16.100.93:9443/api/v1/coSpaces", new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                System.out.println(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
            }
        });
        HttpObjectCreator.getInstance().shutdown();
        System.out.println(System.currentTimeMillis() - start);
        ThreadUtils.sleep(6000);
    }
}
