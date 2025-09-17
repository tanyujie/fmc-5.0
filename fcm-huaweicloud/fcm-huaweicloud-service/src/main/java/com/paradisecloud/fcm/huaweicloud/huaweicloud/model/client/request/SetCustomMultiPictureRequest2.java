package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body.RestCustomMultiPictureBody2;
import lombok.Data;


import java.util.function.Consumer;

/**
 * @author nj
 * @date 2024/3/6 16:23
 */
@Data
public class SetCustomMultiPictureRequest2 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("conferenceID")
    private String conferenceID;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("X-Conference-Authorization")
    private String xConferenceAuthorization;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("body")
    private RestCustomMultiPictureBody2 body;

    public SetCustomMultiPictureRequest2() {
    }

    public SetCustomMultiPictureRequest2 withConferenceID(String conferenceID) {
        this.conferenceID = conferenceID;
        return this;
    }

    public String getConferenceID() {
        return this.conferenceID;
    }

    public void setConferenceID(String conferenceID) {
        this.conferenceID = conferenceID;
    }

    public SetCustomMultiPictureRequest2 withXConferenceAuthorization(String xConferenceAuthorization) {
        this.xConferenceAuthorization = xConferenceAuthorization;
        return this;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("X-Conference-Authorization")
    public String getXConferenceAuthorization() {
        return this.xConferenceAuthorization;
    }

    public void setXConferenceAuthorization(String xConferenceAuthorization) {
        this.xConferenceAuthorization = xConferenceAuthorization;
    }

    public SetCustomMultiPictureRequest2 withBody(RestCustomMultiPictureBody2 body) {
        this.body = body;
        return this;
    }

    public SetCustomMultiPictureRequest2 withBody(Consumer<RestCustomMultiPictureBody2> bodySetter) {
        if (this.body == null) {
            this.body = new RestCustomMultiPictureBody2();
            bodySetter.accept(this.body);
        }

        return this;
    }

    public RestCustomMultiPictureBody2 getBody() {
        return this.body;
    }

    public void setBody(RestCustomMultiPictureBody2 body) {
        this.body = body;
    }


}
