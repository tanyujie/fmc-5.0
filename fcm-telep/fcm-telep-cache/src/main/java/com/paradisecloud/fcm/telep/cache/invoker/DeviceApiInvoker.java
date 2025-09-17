package com.paradisecloud.fcm.telep.cache.invoker;



/**
 * @author nj
 * @date 2022/10/13 13:58
 */
public class DeviceApiInvoker extends TelePApiInvoker{

    public DeviceApiInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        super(rootUrl, xmlRpcLocalRequest);
    }

    /**
     * device.query
     * Queries for information about the MCU device. There are no parameters passed with this call. The
     * call response returns the following
     */
    public void query() {
        xmlRpcLocalRequest.execute("device.query", null);
    }

}
