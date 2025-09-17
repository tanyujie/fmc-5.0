package com.paradisecloud.fcm.telep.cache.invoker;



/**
 * @author nj
 * @date 2022/10/13 10:28
 */
public class TemplateApiInvoker extends TelePApiInvoker{

    public TemplateApiInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        super(rootUrl, xmlRpcLocalRequest);
    }

    /**
     * template.enumerate
     * The template.enumerate function returns an array of template structures, each of which contains the settings
     * of a template. The call does not take any parameters.
     */
    public void enumerate() {
        xmlRpcLocalRequest.execute("template.enumerate", null);
    }

}
