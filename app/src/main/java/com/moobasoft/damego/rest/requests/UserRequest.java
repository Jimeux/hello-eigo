package com.moobasoft.damego.rest.requests;


import com.moobasoft.damego.rest.models.User;

public class UserRequest {

    private User user;

    public UserRequest(String email, String username, String password) {
        this.user = new User();
        this.user.setEmail(email);
        this.user.setUsername(username);
        this.user.setPassword(password);
    }
}