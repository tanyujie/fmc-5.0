package com.paradisecloud.fcm.zte.cache.api;

import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.request.cc.*;
import com.paradisecloud.fcm.zte.model.response.cc.*;
import com.zte.m900.request.*;
import com.zte.m900.response.*;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.logging.log4j.util.Strings;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import java.rmi.RemoteException;

public class ConferenceControlApi {

    private McuZteConferenceContext conferenceContext;

    public ConferenceControlApi(McuZteConferenceContext conferenceContext) {
        this.conferenceContext = conferenceContext;
    }

    private String buildUrl() {
        return conferenceContext.getMcuZteBridge().getBaseUrl();
    }



    private void buildheader()  {
        try {
            SOAPHeaderElement header = new SOAPHeaderElement(new
                    QName("AuthHeader"));
            MessageElement element = new MessageElement(new QName("Version"), "1");
            header.addChild(element);
            McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(conferenceContext.getMcuZteBridge().getBusiMcuZte().getId());
            String mcuToken =mcuZteBridge.getMcuToken();
            if(Strings.isNotBlank(mcuToken)){
                MessageElement tokenElement = new MessageElement(new QName("Token"), mcuToken);
                header.addChild(tokenElement);
            }
            conferenceContext.getMcuZteBridge().getmStub().clearHeaders();
            conferenceContext.getMcuZteBridge().getmStub().setHeader(header);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }


    public GetConferenceInfoResponse getMrInfo(GetConferenceInfoRequest request) {
        GetConferenceInfoResponse connectParticipantResponse=null;
        try {
            buildheader();
            connectParticipantResponse = conferenceContext.getMcuZteBridge().getmStub().getConferenceInfo(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return connectParticipantResponse;
    }

    public GetConferenceStatusResponse getConferenceStatus(GetConferenceStatusRequest request) {
        GetConferenceStatusResponse connectParticipantResponse=null;
        try {
            buildheader();
            connectParticipantResponse = conferenceContext.getMcuZteBridge().getmStub().getConferenceStatus(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return connectParticipantResponse;
    }


    public GetParticipantStatusV3Response getParticipantStatusV3(GetParticipantStatusV3Request request){
        GetParticipantStatusV3Response connectParticipantResponse=null;
        try {
            buildheader();
            connectParticipantResponse = conferenceContext.getMcuZteBridge().getmStub().getParticipantStatusV3(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return connectParticipantResponse;
    }


    public GetParticipantStatusV4Response getParticipantStatusV4(GetParticipantStatusV4Request request){
        GetParticipantStatusV4Response connectParticipantResponse=null;
        try {
            buildheader();
            connectParticipantResponse = conferenceContext.getMcuZteBridge().getmStub().getParticipantStatusV4(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return connectParticipantResponse;
    }


    public ConnectParticipantResponse setConnectMrTerminal(ConnectParticipantRequest request) {
        ConnectParticipantResponse connectParticipantResponse=null;
        try {
             buildheader();
             connectParticipantResponse = conferenceContext.getMcuZteBridge().getmStub().connectParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return connectParticipantResponse;
    }




    public InviteParticipantResponse inviteParticipant(InviteParticipantRequest request) {
        InviteParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().inviteParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public InviteParticipantV2Response inviteParticipantV2(InviteParticipantV2Request request) {
        InviteParticipantV2Response response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().inviteParticipantV2(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public MuteParticipantResponse muteParticipant(MuteParticipantRequest request) {
        MuteParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().muteParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CancelMuteParticipantResponse cancelMuteParticipant(CancelMuteParticipantRequest request) {

        CancelMuteParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().cancelMuteParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public SendMcuTitleResponse sendMcuTitle(SendMcuTitleRequest request) {
        SendMcuTitleResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().sendMcuTitle(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }



    public DisconnectParticipantResponse disconnectParticipant(DisconnectParticipantRequest request) {

        DisconnectParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().disconnectParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }




    public DeleteParticipantResponse deleteParticipant(DeleteParticipantRequest request) {

        DeleteParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().deleteParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }


    public SetParticipantVideoSendResponse setParticipantVideoSendRequest(SetParticipantVideoSendRequest request) {
        SetParticipantVideoSendResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().setParticipantVideoSend(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public LockConferenceResponse lockConferenceRequest(LockConferenceRequest request) {
        LockConferenceResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().lockConference(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public UnlockConferenceResponse unLockConferenceRequest(UnlockConferenceRequest request) {
        UnlockConferenceResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().unlockConference(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;


    }

    public SwitchMultiViewCtrlModeResponse switchMultiCtrlModeRequest(SwitchMultiViewCtrlModeRequest request) {
        SwitchMultiViewCtrlModeResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().switchMultiCtrlMode(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public SelectMultiOrSingleViewResponse updateMrAutoMosicConfig(SelectMultiOrSingleViewRequest request) {
        SelectMultiOrSingleViewResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().selectMultiOrSingleView(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public MultiViewSelectResponse multiViewSelect(MultiViewSelectRequest request) {
        MultiViewSelectResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().multiViewSelect(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }
    public BoardcastParticipantResponse boardcastParticipant(BoardcastParticipantRequest request) {
        BoardcastParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().boardcastParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public SelectParticipantResponse selectParticipant(SelectParticipantRequest request) {
        SelectParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().selectParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public SetMultiViewNumResponse setMultiViewNum(SetMultiViewNumRequest request) {
        SetMultiViewNumResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().setMultiViewNum(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }
    public ProlongConferenceResponse prolongConference(ProlongConferenceRequest request) {
        ProlongConferenceResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().prolongConference(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public ControlParticipantCameraResponse controlParticipantCamera(ControlParticipantCameraRequest request) {
        ControlParticipantCameraResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().controlParticipantCamera(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }
    public GetParticipantCameraInfoResponse getParticipantCameraInfo(GetParticipantCameraInfoRequest request) {
        GetParticipantCameraInfoResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().getParticipantCameraInfo(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }
    public SendParticipantDualVideoResponse sendParticipantDualVideo(SendParticipantDualVideoRequest request) {
        SendParticipantDualVideoResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().sendParticipantDualVideo(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public QuietParticipantResponse quietParticipant(QuietParticipantRequest request) {
        QuietParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().quietParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public CancelQuietParticipantResponse cancelQuietParticipant(CancelQuietParticipantRequest request) {
        CancelQuietParticipantResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().cancelQuietParticipant(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

    public SwitchConfCtrlModeResponse switchConfCtrlMode(SwitchConfCtrlModeRequest request) {
        SwitchConfCtrlModeResponse response=null;
        try {
            buildheader();
            response = conferenceContext.getMcuZteBridge().getmStub().switchConfCtrlMode(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;

    }

}
