package com.paradisecloud.fcm.web.controller.mtr;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMtrMapper;
import com.paradisecloud.fcm.dao.model.BusiMtr;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import io.jsonwebtoken.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * MTR探测用
 */
@RestController
@RequestMapping({"/busi/mtr"})
public class BusiMtrController extends BaseController {

    @Resource
    private BusiMtrMapper busiMtrMapper;

    @PreAuthorize("@ss.hasPermi('busi:mtr:list')")
    @GetMapping({"/list"})
    public RestResponse list(BusiMtr busiMtr) {
        this.startPage();
        List<BusiMtr> list = busiMtrMapper.selectBusiMtrList(busiMtr);
        return this.getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('busi:mtr:add')")
    @PostMapping
    @Operation(summary = "", description = "新增MTR探测")
    public RestResponse add(@RequestBody BusiMtr busiMtr) {
        Assert.isTrue(busiMtr.getTargetIp() != null, "目标地址不能为空");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiMtr.setCreateBy(loginUser.getUsername());
        busiMtr.setCreateTime(new Date());
        if (StringUtils.isEmpty(busiMtr.getSourceIp())) {
            busiMtr.setSourceIp("127.0.0.1");
        }
        if (busiMtr.getTimes() == null) {
            busiMtr.setTimes(1);
        }
        if (StringUtils.isEmpty(busiMtr.getName())) {
            busiMtr.setName(DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss"));
        }
        busiMtr.setStatus(0);
        int i = busiMtrMapper.insertBusiMtr(busiMtr);
        if (i > 0) {
            return RestResponse.success(busiMtr);
        } else {
            return this.toAjax(i);
        }
    }

    @PreAuthorize("@ss.hasPermi('busi:mtr:remove')")
    @DeleteMapping({"/{mtrIds}"})
    @Operation(summary = "", description = "删除MTR探测")
    public RestResponse remove(@PathVariable Long[] mtrIds) {
        return this.toAjax(busiMtrMapper.updateBusiMtrForDeleteByIds(mtrIds));
    }

    /**
     * 下载MTR执行结果
     */
    @RequestMapping("/download/{id}")
    @Operation(summary = "下载MTR执行结果")
    public void downLoad(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {
        BusiMtr busiMtr = busiMtrMapper.selectBusiMtrById(id);
        if (busiMtr == null) {
            throw new SystemException("文件不存在！");
        }
        if (busiMtr.getStatus() == 0) {
            throw new SystemException("文件不存在！");
        }
        String fileUrl = getFilePath(busiMtr.getFileName());
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new SystemException("文件不存在！");
        }

        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(fileUrl);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLength((int) file.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                file.getName());
        response.setHeader(headerKey, headerValue);

        try {
            InputStream myStream = new FileInputStream(fileUrl);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
        }
    }

    /**
     * 查看MTR执行结果
     */
    @RequestMapping("/view/{id}")
    @Operation(summary = "查看MTR执行结果")
    public Object view(@PathVariable("id") Long id) {
        BusiMtr busiMtr = busiMtrMapper.selectBusiMtrById(id);
        if (busiMtr == null) {
            throw new SystemException("结果不存在！");
        }
        if (busiMtr.getStatus() == 0) {
            throw new SystemException("结果不存在！");
        }
        String fileUrl = getFilePath(busiMtr.getFileName());
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new SystemException("结果不存在！");
        }

        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileUrl));
            String strLine = null;
            while ((strLine = bufferedReader.readLine()) != null) {
                result += strLine + "\n";
            }
        } catch (IOException e) {
        }
        return success(result);
    }

    private String getFilePath(String fileName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return PathUtil.getRootPath() + "/mtr/" + fileName;
        } else {
            return "/home/mtr/" + fileName;
        }
    }
}
