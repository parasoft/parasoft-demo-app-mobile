package com.parasoft.demoapp.retrofitConfig.response;

import lombok.Data;

@Data
public class ForgotPasswordUserInfo {
    private String roleName;
    private boolean hasPrimaryUser;
    private PrimaryUserInfo primaryUserInfo;

    @Data
    public static class PrimaryUserInfo {
        private String userName;
        private String password;
    }
}
