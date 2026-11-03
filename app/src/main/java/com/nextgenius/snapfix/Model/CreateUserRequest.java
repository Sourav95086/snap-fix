package com.nextgenius.snapfix.Model;

public class CreateUserRequest {

    private String email;

    public CreateUserRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}