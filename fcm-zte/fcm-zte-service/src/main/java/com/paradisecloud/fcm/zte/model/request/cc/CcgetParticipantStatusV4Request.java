package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.common.utils.sign.Md5Utils;
import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/8 14:34
 */
public class CcgetParticipantStatusV4Request extends CommonRequest {

    private String ConfIdentifier;
    private String TerIdentifier;
    /**
     * 页号，如果为-1，则表
     * 示不进行分页查询，查
     * 询所有的
     */
    private int Page;
    /**
     * 每页显示多少条记录，
     * 如果为-1，则表示不进
     * 行分页查询，查询所有
     * 的
     */
    private int NumPerPage;
    /**
     * 会议 ID 属性：ConfIdO
     * pt=0 表示会议 ID
     * ConfIdOpt=1 表示会议
     * 号码
     * ConfIdOpt=2 表示客户
     * 端召开会议时生成的
     * 会议 ID
     * 不填表示会议 ID
     */
    private int ConfIdOpt;
    /**
     * 终端 ID 属性：
     * TerIdOpt=0 表示终端 I
     * D
     * TerIdOpt=1 表示终端
     * 号码
     * 不填表示终端 ID
     */
    private int TerIdOpt;


    public String getConfIdentifier() {
        return ConfIdentifier;
    }

    public void setConfIdentifier(String confIdentifier) {
        ConfIdentifier = confIdentifier;
    }

    public String getTerIdentifier() {
        return TerIdentifier;
    }

    public void setTerIdentifier(String terIdentifier) {
        TerIdentifier = terIdentifier;
    }

    public int getPage() {
        return Page;
    }

    public void setPage(int page) {
        Page = page;
    }

    public int getNumPerPage() {
        return NumPerPage;
    }

    public void setNumPerPage(int numPerPage) {
        NumPerPage = numPerPage;
    }

    public int getConfIdOpt() {
        return ConfIdOpt;
    }

    public void setConfIdOpt(int confIdOpt) {
        ConfIdOpt = confIdOpt;
    }

    public int getTerIdOpt() {
        return TerIdOpt;
    }

    public void setTerIdOpt(int terIdOpt) {
        TerIdOpt = terIdOpt;
    }

    @Override
    public String buildToXml() {
        String xml = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:m900=\"http://m900.zte.com\">\n" +
                "<soapenv:Header>\n" +
                "</soapenv:Header>\n" +
                "<soapenv:Body>\n" +
                "<m900:login soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "<req xsi:type=\"req:LoginRequest\" xs:type=\"type:LoginRequest\" xmlns:req=\"http://request.m900.zte.com\" xmlns:xs=\"http://www.w3.org/2000/XMLSchema-instance\">\n" +
                "\t<password xsi:type=\"soapenc:string\" xs:type=\"type:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+ "</password >\n" +
                "\t<userName xsi:type=\"soapenc:string\" xs:type=\"type:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+"</userName>\n" +
                "</req>\n" +
                "</m900:login>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return xml;
    }
}
