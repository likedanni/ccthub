package com.ccthub.userservice.dto;

public class LoginRequest {
    private String phone;
    private String password;
    private boolean isAdminLogin = false; // 是否是管理员登录

    public LoginRequest() {
    }

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdminLogin() {
        return isAdminLogin;
    }

    public void setIsAdminLogin(boolean isAdminLogin) {
        this.isAdminLogin = isAdminLogin;
    }
}
