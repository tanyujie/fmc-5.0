package com.paradisecloud.fcm.wvp.gb28181.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WvpLoginResponse extends WvpCommonResponse {

    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String accessToken;
        private Integer id;
        private Boolean enabled;
        private Boolean accountNonLocked;
        private Boolean credentialsNonExpired;
        private Boolean accountNonExpired;
        private Object password;
        private RoleDTO role;
        private String username;
        private String pushKey;
        private Object authorities;

        @NoArgsConstructor
        @Data
        public static class RoleDTO {
            private Integer id;
            private String name;
            private String authority;
            private String createTime;
            private String updateTime;
        }
    }
}
