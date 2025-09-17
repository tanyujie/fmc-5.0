package com.paradisecloud.smc3.invoker;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.util.FileCopyUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/12 14:31
 */
public class ClientAuthentication {


    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    // 连接存活
                    //                  .setConnectionTimeToLive(5, TimeUnit.SECONDS)
//                    .setConnectTimeout(1000) //连接超时时间
//                    .setConnectionRequestTimeout(1000) //从连接池中取的连接的最长时间
//                    .setSocketTimeout(10 * 1000) //数据传输的超时时间
//                    .setStaleConnectionCheckEnabled(true) //提交请求前测试连接是否可用
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();

    }

    public static String httpPost(String url, String requestBody, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpPost.setEntity(entity);
        try {
            HttpEntity responseEntity = getHttpEntity(httpclient, httpclient.execute(httpPost));
            if(responseEntity==null){
                return null;
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } finally {
            EntityUtils.consume(entity);
        }
    }


    public static void httpPostVoid(String url, String requestBody, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpPost.setEntity(entity);
        try {
           httpclient.execute(httpPost);

        } finally {
            EntityUtils.consume(entity);
        }
    }

    public static String httpPut(String url, String requestBody, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpPut httpPut = new HttpPut(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPut.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpPut.setEntity(entity);
        try {
            HttpEntity responseEntity = getHttpEntity(httpclient, httpclient.execute(httpPut));
            if(responseEntity==null){
                return null;
            }
            return EntityUtils.toString(responseEntity, "UTF-8");
        } finally {
            EntityUtils.consume(entity);
        }
    }

    public static String httpDelete(String url, String requestBody, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpDelete.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpDelete.setEntity(entity);
        try {
            HttpEntity responseEntity = getHttpEntity(httpclient, httpclient.execute(httpDelete));
            if (responseEntity == null) {
                return null;
            }
            return EntityUtils.toString(responseEntity);
        } finally {
            EntityUtils.consume(entity);
        }
    }

    public static void httpDeleteVoid(String url, String requestBody, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpDelete.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpDelete.setEntity(entity);
        try {
           httpclient.execute(httpDelete);

        } finally {
            EntityUtils.consume(entity);
        }
    }


    public static void httpPatch(String url, String requestBody, Map<String, String> headers) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpPatch httpPatch = new HttpPatch(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPatch.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpPatch.setEntity(entity);
//        try {
//            HttpEntity responseEntity = getHttpEntity(httpclient, httpclient.execute(httpPatch));
//            return EntityUtils.toString(responseEntity);
//        } finally {
//            EntityUtils.consume(entity);
//        }
        try {
            httpclient.execute(httpPatch);
        } finally {
            EntityUtils.consume(entity);
        }
    }

    public static String getAuthToken(String url, String admin, String password) throws IOException {
        String res;
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            String auth = admin + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            httpget.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                res = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

        return res;
    }

    /**HTTP
     * get请求
     * 以String格式返回响应体
     * url：请求路径
     * param：请求参数（可为空）
     * header：请求头部数据(可为空)
     * */
    public static String httpGet(String url,Map<String,String> param,Map<String,String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        String getUrl = null;
        if(param!=null&&param.size()>0){
            String data = "";
            for(Map.Entry<String,String> map :param.entrySet()){
                data=data+"&"+map.getKey()+"="+map.getValue();
            }
            getUrl=url+"?"+data.substring(1);
        }else {
            getUrl = url;
        }
        //创建get请求
        HttpGet httpGet = new HttpGet(getUrl);
        //设置header
        httpGet.setHeader("Content-type", "application/json");
        //设置请求头部信息
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        //声明response响应模型

        try {
            HttpEntity responseEntity = getHttpEntity(httpclient, httpclient.execute(httpGet));
            return EntityUtils.toString(responseEntity);
        } finally {
            if (httpclient != null) {
                httpclient.close();
            }
        }

    }


    public static void httpGetFileDownLoad(String url, Map<String, String> param, Map<String, String> header, String localFileName) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        String getUrl = null;
        OutputStream out = null;
        InputStream in = null;
        if (param != null && param.size() > 0) {
            String data = "";
            for (Map.Entry<String, String> map : param.entrySet()) {
                data = data + "&" + map.getKey() + "=" + map.getValue();
            }
            getUrl = url + "?" + data.substring(1);
        } else {
            getUrl = url;
        }
        try {
            //创建get请求
            HttpGet httpGet = new HttpGet(getUrl);
            //设置请求头部信息
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            //声明response响应模型


            HttpEntity entity = getHttpEntity(httpclient, httpclient.execute(httpGet));
            in = entity.getContent();


            long length = entity.getContentLength();
            if (length <= 0) {
                return;
            }
            File file = new File(localFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public static void httpGetDownLoad(String url, Map<String, String> param, Map<String, String> header, HttpServletResponse resp) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        String getUrl = null;
        OutputStream out = null;
        InputStream in = null;
        if (param != null && param.size() > 0) {
            String data = "";
            for (Map.Entry<String, String> map : param.entrySet()) {
                data = data + "&" + map.getKey() + "=" + map.getValue();
            }
            getUrl = url + "?" + data.substring(1);
        } else {
            getUrl = url;
        }
        //创建get请求
        HttpGet httpGet = new HttpGet(getUrl);
        //设置请求头部信息
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        //声明response响应模型
        CloseableHttpResponse response = httpclient.execute(httpGet);
        resp.setContentType(response.getEntity().getContentType().getValue());
        String srcString = response.getHeaders("Content-disposition")[0].getValue();
        resp.setHeader("Content-disposition", srcString);
        resp.setHeader("Content-Length", response.getHeaders("Content-Length")[0].getValue());


        FileCopyUtils.copy(response.getEntity().getContent(), resp.getOutputStream());


    }


    public static List<String[]> httpGetListString(String url, Map<String, String> param, Map<String, String> header) throws IOException {
        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        String getUrl = null;
        if (param != null && param.size() > 0) {
            String data = "";
            for (Map.Entry<String, String> map : param.entrySet()) {
                data = data + "&" + map.getKey() + "=" + map.getValue();
            }
            getUrl = url + "?" + data.substring(1);
        } else {
            getUrl = url;
        }
        //创建get请求
        HttpGet httpGet = new HttpGet(getUrl);
        //设置请求头部信息
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        //声明response响应模型
        CloseableHttpResponse response = httpclient.execute(httpGet);

        CSVReader csvReader = new CSVReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
        List<String[]> stringsList = csvReader.readAll();
        return stringsList;

    }


    private static HttpEntity getHttpEntity(CloseableHttpClient httpclient, CloseableHttpResponse execute) {
        if(execute==null){
            return null;
        }
        CloseableHttpResponse response = execute;
        HttpEntity responseEntity = response.getEntity();

        return responseEntity;
    }


    /**
     * 发送http delete请求
     */
    public static String httpDelete(String url, Map<String,String> headers, String encode){
        if(encode == null){
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = ClientAuthentication.createSSLClientDefault();
        HttpDelete httpdelete = new HttpDelete(url);
        //设置header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpdelete.setHeader(entry.getKey(),entry.getValue());
            }
        }
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpdelete);
            HttpEntity entity = httpResponse.getEntity();
           return EntityUtils.toString(entity, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送http delete请求
     */
    public static void httpDeleteUrl(String url, Map<String,String> headers, String encode){
        if(encode == null){
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = ClientAuthentication.createSSLClientDefault();
        HttpDelete httpdelete = new HttpDelete(url);
        //设置header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpdelete.setHeader(entry.getKey(),entry.getValue());
            }
        }
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpdelete);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * 发送 http put 请求，参数以原生字符串进行提交
     * @param url
     * @param encode
     * @return
     */
    public static String httpPut(String url,String stringJson,Map<String,String> headers, String encode){
        if(encode == null){
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = ClientAuthentication.createSSLClientDefault();
        HttpPut httpput = new HttpPut(url);

        //设置header
        httpput.setHeader("Content-type", "application/json");
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpput.setHeader(entry.getKey(),entry.getValue());
            }
        }
        //组织请求参数
        StringEntity stringEntity = new StringEntity(stringJson, encode);
        httpput.setEntity(stringEntity);
        CloseableHttpResponse  httpResponse = null;
        try {
            //响应信息
            httpResponse = closeableHttpClient.execute(httpput);
            HttpEntity entity = httpResponse.getEntity();
            if(entity==null){
                return null;
            }
            return EntityUtils.toString(entity, encode);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String postXml(String url, String xml) {

        String res = null;

        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();


        HttpPost httpPost = new HttpPost(url);


        httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");

        StringEntity stringEntity = new StringEntity(xml, "utf-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse httpResponse = null;
        try {

            httpResponse = httpclient.execute(httpPost);
            return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String httpPatchStr(String url, String requestBody, Map<String, String> headers) {

        CloseableHttpClient httpclient = ClientAuthentication.createSSLClientDefault();
        HttpPatch httpPatch = new HttpPatch(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPatch.setHeader(entry.getKey(), entry.getValue());
        }
        ByteArrayEntity entity;
        try {
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("请求body封装异常！", e);
        }
        httpPatch.setEntity(entity);

        try {
            CloseableHttpResponse execute = httpclient.execute(httpPatch);
            HttpEntity responseEntity = execute.getEntity();
            if(responseEntity==null){
                return null;
            }
            return EntityUtils.toString(responseEntity, "utf-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
