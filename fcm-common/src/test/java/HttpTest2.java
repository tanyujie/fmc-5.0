import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.utils.RegExpUtils;

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
public class HttpTest2
{
    
    public static void main(String[] args)
    {
        Pattern p1 = Pattern.compile("<input type\\s*=\\s*\"hidden\" name\\s*=\\s*\"Acano-Session-Key\" value\\s*=\\s*\"\\w+\"/>");
        Pattern p2 = Pattern.compile("value\\s*=\\s*\"\\w+\"");
        Pattern p3 = Pattern.compile("\"\\w+\"");
        Pattern p4 = Pattern.compile("\\w+");
        HttpRequester hr = HttpObjectCreator.getInstance().createHttpRequester("admin", "P@rad1se", true);
        hr.get("https://172.16.100.126:9443/authenticate.html", new HttpResponseProcessorAdapter() 
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                String acanoSessionKey = RegExpUtils.extractContent(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8), p1, p2, p3, p4);
                
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("0", "admin"));
                nameValuePairList.add(new BasicNameValuePair("1", "P@rad1se"));
                nameValuePairList.add(new BasicNameValuePair("redirect", ""));
                nameValuePairList.add(new BasicNameValuePair("Acano-Session-Key", acanoSessionKey));
                hr.post("https://172.16.100.126:9443/authenticate.html", nameValuePairList, new HttpResponseProcessorAdapter() 
                {
                    public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
                    {
                        Header[] hs = httpResponse.getHeaders("Set-Cookie");
                        if (hs != null && hs.length > 0)
                        {
                            String val = hs[0].getValue();
                            Map<String, String> requestHeaders = new HashMap<>();
                            requestHeaders.put("Cookie", val);
                            
                            //  获取另一个请求的响应header（Acano-Session-Key）https://172.16.100.126:9443/cmd.xml?_=1611285930583
                            hr.get("https://172.16.100.126:9443/cmd.xml?_=" + System.currentTimeMillis(), requestHeaders, new HttpResponseProcessorAdapter() 
                            {
                                public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
                                {
                                    String acanoSessionKey = httpResponse.getFirstHeader("Acano-Session-Key").getValue();
                                    
                                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                                    
                                    nameValuePairList.add(new BasicNameValuePair("cmd", "allow_outgoing_h264chp on"));
                                    nameValuePairList.add(new BasicNameValuePair("cmd_button", ""));
                                    
                                    Map<String, String> requestHeaders = new HashMap<>();
                                    requestHeaders.put("Cookie", val);
                                    requestHeaders.put("Acano-Session-Key", acanoSessionKey);
                                    hr.post("https://172.16.100.126:9443/cmd.xml", requestHeaders, nameValuePairList, new HttpResponseProcessorAdapter() 
                                    {
                                        public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
                                        {
                                            System.out.println(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        
        /*    
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        
        nameValuePairList.add(new BasicNameValuePair("cmd", "allow_outgoing_h264chp%20on"));
        nameValuePairList.add(new BasicNameValuePair("cmd_button", ""));
        hr.post("https://172.16.100.126:9443/cmd.xml", nameValuePairList, new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException
            {
                System.out.println(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
            }
        });*/
    }
}
