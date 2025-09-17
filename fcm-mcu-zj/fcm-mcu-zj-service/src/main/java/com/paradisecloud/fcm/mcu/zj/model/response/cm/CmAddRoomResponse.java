package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.io.Serializable;
import java.util.List;

public class CmAddRoomResponse extends CommonResponse implements Serializable {

    /**
     * has_no_privilege_dep_ids : []
     * room_id : 28
     * result : 0/success
     * fail_room_dep : []
     */

    private int room_id;
    private String result;
    private List<Integer> has_no_privilege_dep_ids;
    private List<Integer> fail_room_dep;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Integer> getHas_no_privilege_dep_ids() {
        return has_no_privilege_dep_ids;
    }

    public void setHas_no_privilege_dep_ids(List<Integer> has_no_privilege_dep_ids) {
        this.has_no_privilege_dep_ids = has_no_privilege_dep_ids;
    }

    public List<Integer> getFail_room_dep() {
        return fail_room_dep;
    }

    public void setFail_room_dep(List<Integer> fail_room_dep) {
        this.fail_room_dep = fail_room_dep;
    }
}
