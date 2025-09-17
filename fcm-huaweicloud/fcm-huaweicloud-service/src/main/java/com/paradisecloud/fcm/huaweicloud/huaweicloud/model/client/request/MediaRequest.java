package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body.RestCustomMultiPictureBody2;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body.RestMediaBody;
import lombok.Data;

import java.util.function.Consumer;

/**
 * @author nj
 * @date 2024/3/15 15:53
 */
@Data
public class MediaRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("conferenceID")
    private String conferenceID;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("X-Conference-Authorization")
    private String xConferenceAuthorization;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("body")
    private RestMediaBody body;

    public MediaRequest() {
    }

    public MediaRequest withConferenceID(String conferenceID) {
        this.conferenceID = conferenceID;
        return this;
    }

    public String getConferenceID() {
        return this.conferenceID;
    }

    public void setConferenceID(String conferenceID) {
        this.conferenceID = conferenceID;
    }

    public MediaRequest withXConferenceAuthorization(String xConferenceAuthorization) {
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

    public MediaRequest withBody(RestMediaBody body) {
        this.body = body;
        return this;
    }

    public MediaRequest withBody(Consumer<RestMediaBody> bodySetter) {
        if (this.body == null) {
            this.body = new RestMediaBody();
            bodySetter.accept(this.body);
        }

        return this;
    }

    public RestMediaBody getBody() {
        return this.body;
    }

    public void setBody(RestMediaBody body) {
        this.body = body;
    }
}
