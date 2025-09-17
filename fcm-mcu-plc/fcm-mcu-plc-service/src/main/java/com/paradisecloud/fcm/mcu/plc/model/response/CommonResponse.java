package com.paradisecloud.fcm.mcu.plc.model.response;

public class CommonResponse {

     public static final String STATUS_OK = "Status OK";
     public static final String IN_PROGRESS = "In progress";

     protected String status;

     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }
}
