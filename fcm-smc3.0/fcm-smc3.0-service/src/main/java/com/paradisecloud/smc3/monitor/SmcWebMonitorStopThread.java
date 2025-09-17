package com.paradisecloud.smc3.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.response.call.ActiveCallsResponse;
import com.paradisecloud.fcm.fme.model.response.call.CallsResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.ActiveCoSpacesResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpacesResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ActiveParticipantsResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.paradisecloud.smc3.model.ConstAPI;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2023/11/22 14:40
 */
@Component
public class SmcWebMonitorStopThread extends Thread implements InitializingBean {

    public  static Set<String> setCallID=new HashSet<>();

    @Override
    public void run() {
        while (true){
            try {
                List<FmeBridge> availableFmeBridges = FmeBridgeCache.getInstance().getAvailableFmeBridges();
                for (FmeBridge fmeBridge : availableFmeBridges) {
                    try {
                        int offset = 0;
                        AtomicInteger totalCount = new AtomicInteger();
                        while (true) {
                            CallsResponse callsResponse = fmeBridge.getCallInvoker().getCalls(offset);
                            if (callsResponse != null) {
                                ActiveCallsResponse activeCallsResponse = callsResponse.getCalls();
                                List<Call> calls = activeCallsResponse.getCall();
                                if (calls != null) {
                                    // 业务处理
                                    doCallService(calls, fmeBridge);
                                    Integer total = activeCallsResponse.getTotal();
                                    totalCount.addAndGet(calls.size());
                                    if (totalCount.get() < total.intValue()) {
                                        offset = totalCount.get();
                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        fmeBridge.getFmeLogger().logWebsocketInfo("Calls data query complete: " + fmeBridge.getDataCache().getCalls(), true);
                    } catch (Throwable e) {
                        fmeBridge.getFmeLogger().logWebsocketInfo("Calls query error Top", true, e);
                    }

                    try {
                        int offset = 0;
                        AtomicInteger totalCount = new AtomicInteger();
                        while (true) {
                            CoSpacesResponse coSpacesResponse = fmeBridge.getCoSpaceInvoker().getCoSpaces(offset);
                            if (coSpacesResponse != null) {
                                ActiveCoSpacesResponse activeCoSpacesResponse = coSpacesResponse.getCoSpaces();
                                List<CoSpace> coSpaces = activeCoSpacesResponse.getCoSpace();
                                if (coSpaces != null) {
                                    // 业务处理
                                    docoSpaceService(coSpaces, fmeBridge);
                                    Integer total = activeCoSpacesResponse.getTotal();
                                    totalCount.addAndGet(coSpaces.size());
                                    if (totalCount.get() < total.intValue()) {
                                        offset = totalCount.get();
                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data query complete: " + fmeBridge.getDataCache().getCalls(), true);
                    } catch (Throwable e) {
                        fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace query error Top", true, e);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                Threads.sleep(1000*60*10);
            }
        }



    }

    private void docoSpaceService(List<CoSpace> coSpaces, FmeBridge fmeBridge) {
        for (CoSpace coSpace : coSpaces) {
            if(Objects.equals(ConstAPI.SMC3_MONITOR,coSpace.getName())){
                if(!setCallID.contains(coSpace.getCallId())){
                    try {
                        fmeBridge.getCoSpaceInvoker().deleteCoSpace(coSpace.getId());
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
                    }

                    try {
                        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                            @Override
                            public void process(FmeBridge fmeBridge) {
                                fmeBridge.getDataCache().deleteCoSpace(coSpace.getId());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }


    private void doCallService(List<Call> calls, FmeBridge fmeBridge) {
        for (Call call : calls) {
            stopCoSpace(fmeBridge, call);
        }
    }


    public void stopCoSpace(FmeBridge fmeBridge, Call call) {
        try {
            if(Objects.equals(ConstAPI.SMC3_MONITOR, call.getName())){
                ParticipantsResponse participantsResponse = fmeBridge.getCallInvoker().getParticipants(call.getId(), 0);
                if (participantsResponse != null) {
                    ActiveParticipantsResponse participantsResponseParticipants = participantsResponse.getParticipants();
                    Integer total = participantsResponseParticipants.getTotal();
                    if (total == 0) {
                        try {
                            fmeBridge.getCoSpaceInvoker().deleteCoSpace(call.getCoSpace());
                        } catch (Exception e) {
                            LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
                        }
                        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                            @Override
                            public void process(FmeBridge fmeBridge) {
                                fmeBridge.getDataCache().deleteCoSpace(call.getCoSpace());
                            }
                        });
                    }
                }
            }
        } catch (Throwable e) {
            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace stop  query error: " + call.getCoSpace(), true, e);
        }
    }

}
