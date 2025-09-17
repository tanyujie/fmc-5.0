package com.paradisecloud.com.fcm.smc.modle.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/29 11:41
 */
@NoArgsConstructor
@Data
public class UserInfoRep {


    private String id;
    private String username;
    private AccountDTO account;
    private Integer isFirstAccess;
    private String emailLanguage;
    private Boolean emailLanguageModified;

    @NoArgsConstructor
    @Data
    public static class AccountDTO {
        private String id;
        private String name;
        private UserRoleDTO userRole;
        private OrganizationDTO organization;
        private String userStatus;

        @NoArgsConstructor
        @Data
        public static class UserRoleDTO {
            private String userType;
            private List<String> roles;
        }

        @NoArgsConstructor
        @Data
        public static class OrganizationDTO {
            private String id;
            private String name;
        }
    }
}
