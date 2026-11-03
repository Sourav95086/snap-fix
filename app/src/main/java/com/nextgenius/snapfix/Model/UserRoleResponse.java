package com.nextgenius.snapfix.Model;

public class UserRoleResponse {

    private boolean success;
    private String email;
    private String role;

    public boolean isSuccess() {
        return success;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}