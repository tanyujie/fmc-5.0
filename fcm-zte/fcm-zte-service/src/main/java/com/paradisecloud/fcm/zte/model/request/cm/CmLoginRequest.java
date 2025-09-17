package com.paradisecloud.fcm.zte.model.request.cm;


import com.paradisecloud.common.utils.sign.Md5Utils;
import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CmLoginRequest extends CommonRequest {

    private String ip;
    private Integer port;
    private String userName;
    private String password;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <soapenv:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:m900="http://m900.zte.com">
     * <soapenv:Header>
     * </soapenv:Header>
     * <soapenv:Body>
     * <m900:login soapenv:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
     * <req xsi:type="req:LoginRequest" xs:type="type:LoginRequest" xmlns:req="http://request.m900.zte.com" xmlns:xs="http://www.w3.org/2000/XMLSchema-instance">
     * 	<password xsi:type="soapenc:string" xs:type="type:string" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/">96e79218965eb72c92a549dd5a330112</password >
     * 	<userName xsi:type="soapenc:string" xs:type="type:string" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/">admin</userName>
     * </req>
     * </m900:login>
     * </soapenv:Body>
     * </soapenv:Envelope>
     * @return
     */
    public String buildToXml() {
        String xml = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:m900=\"http://m900.zte.com\">\n" +
                "<soapenv:Header>\n" +
                "</soapenv:Header>\n" +
                "<soapenv:Body>\n" +
                "<m900:login soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "<req xsi:type=\"req:LoginRequest\" xs:type=\"type:LoginRequest\" xmlns:req=\"http://request.m900.zte.com\" xmlns:xs=\"http://www.w3.org/2000/XMLSchema-instance\">\n" +
                "\t<password xsi:type=\"soapenc:string\" xs:type=\"type:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+ Md5Utils.hash(password) +"</password >\n" +
                "\t<userName xsi:type=\"soapenc:string\" xs:type=\"type:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+userName+"</userName>\n" +
                "</req>\n" +
                "</m900:login>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return xml;
    }
}
