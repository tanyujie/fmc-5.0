package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.SmcError;
import com.paradisecloud.smc3.model.response.SmcErrorResponse;

/**
 * @author nj
 * @date 2022/8/12 10:47
 */
public abstract class SmcApiInvoker {

    protected String baseIp;

    protected String rootUrl;

    protected String meetingUrl;

    public static final String ERROR_NO = "errorNo";

    public SmcApiInvoker(String rootUrl,String meetingUrl) {
        super();
        this.rootUrl = rootUrl;
        this.meetingUrl = meetingUrl;
        this.baseIp=rootUrl.replaceAll("https://","").replaceAll("/sys-portal","");
    }

   public  static void errorString(String result) {
       if (result != null && result.contains(ConstAPI.ERRORNO_0x30000c0)) {
           throw new CustomException("该会场不支持共享材料");
       }

       if (result != null && result.contains(ConstAPI.ERRORNO_0x30010029)) {
           throw new CustomException("MCU不支持修改会场名字体大小, 请检查MCU版本和MCU会场名配置");
       }

        if (result != null && result.contains(ConstAPI.EXCEPTION)) {
            SmcError smcError = JSON.parseObject(result, SmcError.class);
            throw new CustomException(smcError.getMessage() + smcError.getException());
        }
        if (result != null && result.contains(ConstAPI.ERRORNO)) {
            SmcErrorResponse smcError = JSON.parseObject(result, SmcErrorResponse.class);
            throw new CustomException(smcError.getErrorDesc());
        }

       if (result != null && result.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
           throw new CustomException("会议已结束",110330);
       }

       if (result != null && result.contains(ConstAPI.TOKEN_NOT_EXIST)) {
           throw new CustomException("token错误");
       }

       if (result.contains(ConstAPI.MEETINGROOM_NAME_EXIST)) {
           throw new CustomException("SMC侧设备名称重复");
       }
       if (result.contains(ConstAPI.TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST)) {
           throw new CustomException("账号重复");
       }
//       if (result != null && (result.contains(ConstAPI.SMC_CODE_103)||result.contains(ConstAPI.SMC_CODE_104))) {
//           throw new CustomException("请求错误");
//       }

   }



}
