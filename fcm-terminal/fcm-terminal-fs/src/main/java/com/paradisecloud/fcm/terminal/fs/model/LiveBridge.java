package com.paradisecloud.fcm.terminal.fs.model;

import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class LiveBridge {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(false);


	private volatile BusiLive busiLive;
	private volatile int weight;
	private LiveBridgeCluster fcmBridgeCluster;
	private volatile LiveBridgeCluster liveBridgeCluster;

	public Boolean getLiveStatus(BusiLive busiLive) {
//		http://172.16.100.155:1985/api/v1/
		String httpUrl = FcmConfigConstant.HTTP + busiLive.getIp() + FcmConfigConstant.COLON + "1985/api/v1/";
		GenericValue<Boolean> genericValue = new GenericValue<>();
		httpRequester.get(httpUrl, new HttpResponseProcessorAdapter() {
			@Override
			public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
				genericValue.setValue(true);
			}
		});
		if (genericValue.getValue() != null) {
			return genericValue.getValue();
		}
		return false;
	}

    public Boolean pingIpAndPort(String ip, Integer port) {
		if (null == ip || 0 == ip.length() || port < 1024 || port > 65535)
		{
			  return false;
		}

	    if (!pingIp(ip))
	    {
	        return false;
	    }

	    Socket s = new Socket();
	    try
	    {
	         SocketAddress add = new InetSocketAddress(ip, port);
	         s.connect(add, 500);// 超时3秒
	         return true;
	    }
	    catch (IOException e)
	    {
	          return false;
	    }
	    finally
	    {
	        try
	        {
	              s.close();
	         }
	         catch (Exception e)
	        {

	        }
	    }
	}

	public Boolean pingIp(String ip)
	{
      if (null == ip || 0 == ip.length()) {
           return false;
      }

      try
      {
          InetAddress.getByName(ip);
          return true;
     }
     catch (IOException e)
     {
          return false;
     }
	}

	public LiveBridge(BusiLive BusiLive) {
		super();
		this.busiLive = BusiLive;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public BusiLive getBusiLive() {
		return busiLive;
	}

	public void setBusiLive(BusiLive busiLive) {
		this.busiLive = busiLive;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public LiveBridgeCluster getFcmBridgeCluster() {
		return fcmBridgeCluster;
	}

	public void setFcmBridgeCluster(LiveBridgeCluster fcmBridgeCluster) {
		this.fcmBridgeCluster = fcmBridgeCluster;
	}

	public LiveBridgeCluster getLiveBridgeCluster() {
		return liveBridgeCluster;
	}

	public void setLiveBridgeCluster(LiveBridgeCluster liveBridgeCluster) {
		this.liveBridgeCluster = liveBridgeCluster;
	}
}
