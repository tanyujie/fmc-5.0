package com.paradisecloud.fcm.zte.cache.api;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.model.request.CommonRequest;
import com.paradisecloud.fcm.zte.model.request.cm.*;
import com.paradisecloud.fcm.zte.model.response.cm.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import com.zte.m900.request.*;
import com.zte.m900.response.*;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.util.Strings;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;


/**
 * 会议管理API
 */
public class ConferenceManageApi {

    private  McuZteBridge mcuZteBridge;

    public ConferenceManageApi(McuZteBridge mcuZteBridge) {

        this.mcuZteBridge = mcuZteBridge;

    }


    private String buildUrl() {
        return mcuZteBridge.getBaseUrl();
    }


    private Map<String, String> buildHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml");

        return headers;
    }

    private StringEntity buildParams(CommonRequest request) {
        if (!(request instanceof CmLoginRequest)) {
            if (StringUtils.isEmpty(mcuZteBridge.getMcuToken())) {
                return null;
            }
        }
        request.setMcuToken(mcuZteBridge.getMcuToken());
        request.setMcuUserToken(mcuZteBridge.getMcuUserToken());
        String xml = request.buildToXml();
        StringEntity entity = new StringEntity(xml, ContentType.create("application/xml", Consts.UTF_8));
        return entity;
    }

    /**
     * 登录
     */
    public CmLoginResponse login(CmLoginRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmLoginResponse> genericValue = new GenericValue<>();
        mcuZteBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmLoginResponse response = new CmLoginResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 登录
     */
    public EmeetingloginResponse login(EmeetingloginRequest request) {
        EmeetingloginResponse response= null;
        try {
            SOAPHeaderElement header = new SOAPHeaderElement(new
                    QName("AuthHeader"));
            MessageElement element = new MessageElement(new QName("Version"), "1");
            header.addChild(element);
            mcuZteBridge.getmStub().clearHeaders();
            mcuZteBridge.getmStub().setHeader(header);
            response = mcuZteBridge.getmStub().emeetinglogin(request);
        } catch (SOAPException e) {
        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public CmGetMcuTimeResponse getMcuTime(CmGetMcuTimeRequest cmGetMcuTimeRequest) {

        return null;
    }

    public CmGetChangesResponse getChanges(CmGetChangesRequest cmGetChangesRequest) {

        return null;
    }



    public EndConferenceResponse stopMr(EndConferenceRequest request) {

        EndConferenceResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().endConference(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public CancelConferenceReservedResponse cancelConferenceReserved(CancelConferenceReservedRequest request) {

        CancelConferenceReservedResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().cancelConferenceReserved(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }



    public GetConferenceTempletListResponse getConferenceTempletList(GetConferenceTempletListRequest request) {
        GetConferenceTempletListResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().getConferenceTempletList(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public GetConferenceInfoListResponse getConferenceInfoList(GetConferenceInfoListRequest request) {
        GetConferenceInfoListResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().getConferenceInfoList(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public CreateConferenceResponse startMr(CreateConferenceRequest request) {
        CreateConferenceResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().createConference(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public ModifyConferenceReservedResponse modifyConferenceReserved(ModifyConferenceReservedRequest request) {
        ModifyConferenceReservedResponse response= null;
        try {
            buildheader();
            response =  mcuZteBridge.getmStub().modifyConferenceReserved(request);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void buildheader()  {
        try {
            SOAPHeaderElement header = new SOAPHeaderElement(new
                    QName("AuthHeader"));
            MessageElement element = new MessageElement(new QName("Version"), "1");
            header.addChild(element);
            String mcuToken = mcuZteBridge.getMcuToken();
            if(Strings.isNotBlank(mcuToken)){
                MessageElement tokenElement = new MessageElement(new QName("Token"), mcuToken);
                header.addChild(tokenElement);
            }
            mcuZteBridge.getmStub().clearHeaders();
            mcuZteBridge.getmStub().setHeader(header);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    public CmGetMrCdrResponse getMrCdr(CmGetMrCdrRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmGetMrCdrResponse> genericValue = new GenericValue<>();
        mcuZteBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetMrCdrResponse response = new CmGetMrCdrResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    public QueryAddressBookResponse queryAddressBook(QueryAddressBookRequest request) {
        QueryAddressBookResponse response=null;
        try {
            buildheader();
            response = mcuZteBridge.getmStub().queryAddressBook(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public QueryAddressBookV2Response getCcTerminalInfo(QueryAddressBookV2Request request) {
        QueryAddressBookV2Response response=null;
        try {
            buildheader();
            response = mcuZteBridge.getmStub().queryAddressBookV2(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public DelAddressBookResponse deleteMrTerminal(DelAddressBookRequest request) {

        DelAddressBookResponse response=null;
        try {
            buildheader();
            response = mcuZteBridge.getmStub().delAddressBook(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public AddAddressBookResponse addMrTerminal(AddAddressBookRequest request) {
        AddAddressBookResponse response=null;
        try {
            buildheader();
            response = mcuZteBridge.getmStub().addAddressBook(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }
}
