package com.nextgenius.snapfix.Model;

public class CreateUserResponse {

    private boolean success;
    private String message;
    private String email;
    private String role;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}