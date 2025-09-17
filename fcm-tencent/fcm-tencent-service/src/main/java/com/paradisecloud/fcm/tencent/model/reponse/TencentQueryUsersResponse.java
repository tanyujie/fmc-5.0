package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import com.tencentcloudapi.wemeet.models.user.QueryUsersResponse;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/12 10:24
 */
public class TencentQueryUsersResponse extends BaseResponse {
    @Expose
    @SerializedName("total_count")
    private Integer totalCount;
    @Expose
    @SerializedName("current_size")
    private Integer currentSize;
    @Expose
    @SerializedName("current_page")
    private Integer currentPage;
    @Expose
    @SerializedName("page_size")
    private Integer pageSize;
    @Expose
    @SerializedName("users")
    private List<UserDetail> users;

    public TencentQueryUsersResponse() {
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurrentSize() {
        return this.currentSize;
    }

    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<UserDetail> getUsers() {
        return this.users;
    }

    public void setUsers(List<UserDetail> users) {
        this.users = users;
    }

    public static class UserDetail {
        @Expose
        @SerializedName("userid")
        private String userId;
        @Expose
        @SerializedName("username")
        private String username;
        @Expose
        @SerializedName("area")
        private String area;
        @Expose
        @SerializedName("update_time")
        private String updateTime;
        @Expose
        @SerializedName("avatar_url")
        private String avatarUrl;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("email")
        private String email;
        @Expose
        @SerializedName("phone")
        private String phone;
        @Expose
        @SerializedName("role_name")
        private String roleName;
        @Expose
        @SerializedName("role_code")
        private String roleCode;


        public UserDetail() {
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getArea() {
            return this.area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getUpdateTime() {
            return this.updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getAvatarUrl() {
            return this.avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return this.phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }
    }
}
