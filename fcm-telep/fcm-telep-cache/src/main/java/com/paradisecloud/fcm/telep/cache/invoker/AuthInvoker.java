package com.paradisecloud.fcm.telep.cache.invoker;



/**
 * @author nj
 * @date 2022/10/12 15:07
 */
public class AuthInvoker extends TelePApiInvoker {

    protected String admin;
    protected String password;

    public AuthInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        super(rootUrl, xmlRpcLocalRequest);
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
