package com.paradisecloud.fcm.web.controller.recording;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.fcm.cdr.service.interfaces.ICallDetailRecordHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Call Detail Records控制器
 *
 * @author johnson liu
 * @date 2021/5/11 11:49
 */
@RestController
@RequestMapping("/busi/cdr")
public class CdrController extends BaseController {
    @Autowired
    private ICallDetailRecordHandler iCallDetailRecordHandler;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * @param request
     * @param response
     */
    @RequestMapping(value = "/receiveCdr", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "接收并保存CDR")
    public void receiveCdr(HttpServletRequest request, HttpServletResponse response) {
        lock.lock();
        try {
            iCallDetailRecordHandler.saveCallDetailRecords(request, response);
        } catch (Throwable e) {
            LoggerFactory.getLogger(getClass()).error("receiveCdr error", e);
        } finally {
            lock.unlock();
            response.setStatus(HttpStatus.OK.value());
        }
    }

}
