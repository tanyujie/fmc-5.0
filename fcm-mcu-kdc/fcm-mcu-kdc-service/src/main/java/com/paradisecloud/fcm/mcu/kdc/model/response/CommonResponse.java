package com.paradisecloud.fcm.mcu.kdc.model.response;

public class CommonResponse {

     public static final int SUCCESS = 1;

     protected Integer success;
     protected Integer error_code;

     public Integer getSuccess() {
          return success;
     }

     public void setSuccess(Integer success) {
          this.success = success;
     }

     public Integer getError_code() {
          return error_code;
     }

     public void setError_code(Integer error_code) {
          this.error_code = error_code;
     }

     public boolean isSuccess() {
          if (success != null && success == SUCCESS) {
               return true;
          }
          return false;
     }

     @Override
     public String toString() {
          return "CommonResponse{" +
                  "success=" + success +
                  ", error_code=" + error_code +
                  '}';
     }
}
