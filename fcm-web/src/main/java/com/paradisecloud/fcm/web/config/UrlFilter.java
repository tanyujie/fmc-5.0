package com.paradisecloud.fcm.web.config;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/6/13 15:30
 */
//@Component
//@WebFilter(filterName = "UrlFilter",
//        urlPatterns = "/*"
//)
public class UrlFilter implements Filter {
    private static final String SMC_2 = "smc2";
    private static final String TENCENT = "tencent";
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String mcuType = ExternalConfigCache.getInstance().getMcuType();
        if (Strings.isNotBlank(mcuType)) {
            if (Objects.equals(mcuType, SMC_2) && !path.contains(SMC_2)) {
                HttpServletResponseWrapper httpResponse = new HttpServletResponseWrapper((HttpServletResponse) response);
                if (path.contains("smc/appointment/conference")) {
                    path = path.replaceAll("smc/appointment/conference", "smc2/smc/appointment/conference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/busi/conferenceNumber")) {
                    path = path.replaceAll("smc/busi/conferenceNumber", "smc2/smc/busi/conferenceNumber");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/dept")) {
                    path = path.replaceAll("smc/dept", "smc2/smc/dept");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/conference")) {
                    path = path.replaceAll("smc/conference", "smc2/smc/conference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/mulitiPicPoll")) {
                    path = path.replaceAll("smc/mulitiPicPoll", "smc2/smc/mulitiPicPoll");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("busi/mcu/smc/templateConference")) {
                    path = path.replaceAll("busi/mcu/smc/templateConference", "smc2/busi/mcu/smc/templateConference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("cascade")) {
                    path = path.replaceAll("cascade", "smc2/cascade");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc") && !path.contains("/smc/monitor/fme")&& !path.contains("license/info/getTermianlInfos")) {
                    path = path.replaceAll("smc", "smc2/smc");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else if (path.contains("busi/welcomepage/terminalStat")) {
                    path="/smc2/smc/conference/terminalStat";
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else if (path.contains("busi/welcomepage/activeConferences")) {
                    path="/smc2/smc/conference/activeConferences";
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }

                else if (path.contains("/busi/templateConference/")) {
                    path = path.replaceAll("/busi/templateConference/", "/smc2/cascade/getCurrentConferenceInfoByLocalId/");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else {
                    chain.doFilter(request, response);
                }
            }else if(Objects.equals(mcuType, TENCENT) && !path.contains(TENCENT)){
                HttpServletResponseWrapper httpResponse = new HttpServletResponseWrapper((HttpServletResponse) response);
                if (path.contains("smc/appointment/conference")) {
                    path = path.replaceAll("smc/appointment/conference", "tencent/smc/appointment/conference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/busi/conferenceNumber")) {
                    path = path.replaceAll("smc/busi/conferenceNumber", "tencent/smc/busi/conferenceNumber");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/dept")) {
                    path = path.replaceAll("smc/dept", "tencent/smc/dept");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/conference")) {
                    path = path.replaceAll("smc/conference", "tencent/smc/conference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc/mulitiPicPoll")) {
                    path = path.replaceAll("smc/mulitiPicPoll", "tencent/smc/mulitiPicPoll");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("busi/mcu/smc/templateConference")) {
                    path = path.replaceAll("busi/mcu/smc/templateConference", "tencent/busi/mcu/smc/templateConference");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("cascade")) {
                    path = path.replaceAll("cascade", "tencent/cascade");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                } else if (path.contains("smc") && !path.contains("/smc/monitor/fme")&& !path.contains("license/info/getTermianlInfos")) {
                    path = path.replaceAll("smc", "tencent/smc");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else if (path.contains("busi/welcomepage/terminalStat")) {
                    path="/tencent/smc/conference/terminalStat";
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else if (path.contains("busi/welcomepage/activeConferences")) {
                    path="/tencent/smc/conference/activeConferences";
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else if (path.contains("/busi/templateConference/")) {
                    path = path.replaceAll("/busi/templateConference/", "/tencent/cascade/getCurrentConferenceInfoByLocalId/");
                    path = path.replaceAll("/fcm", "");
                    request.getRequestDispatcher(path).forward(request, httpResponse);
                }
                else {
                    chain.doFilter(request, response);
                }
            }else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);

        }

        return;
    }
}
