package com.paradisecloud.fcm.fme.cache.model.invoker;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;

import java.util.List;

/**
 * @author nj
 * @date 2022/7/29 15:07
 */
public class ClusterConfigInvoker  extends  FmeApiInvoker{
    /**
     * <pre>构造方法</pre>
     *
     * @param httpRequester
     * @param rootUrl
     * @author lilinhai
     * @since 2021-01-28 18:46
     */
    public ClusterConfigInvoker(HttpRequester httpRequester, String rootUrl) {
        super(httpRequester, rootUrl);
    }



    public RestResponse updateSystemConfigurationCluster(List<NameValuePair> nameValuePairs)
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder resultBuilder = new StringBuilder();
        String url = rootUrl + "system/configuration/cluster";
        httpRequester.put(url, nameValuePairs, new HttpResponseProcessorAdapter()
        {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                if (isXmlContentType(httpResponse))
                {
                    resultBuilder.append("fail");
                }
            }

            @Override
            public void fail(HttpResponse httpResponse)
            {
                if (isXmlContentType(httpResponse))
                {
                    setErrorInfo(genericValue, getBodyContent(httpResponse));
                }
            }
        });

        if (genericValue.getValue() != null)
        {
            return RestResponse.fail(genericValue.getValue().toString());
        }
        return RestResponse.success();
    }
}
