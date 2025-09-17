package com.paradisecloud.fcm.license;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author nj
 * @date 2022/7/27 11:55
 */
//@Component
//@WebFilter(filterName = "MyFilter",
//        urlPatterns = "/*"
//)
public class MyFilter  implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LicenseVerify licenseVerify = new LicenseVerify();
        //校验证书是否有效
        boolean verifyResult = licenseVerify.verify();
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
