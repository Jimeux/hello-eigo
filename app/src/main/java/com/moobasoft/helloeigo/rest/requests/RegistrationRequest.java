package com.moobasoft.helloeigo.rest.requests;


import com.moobasoft.helloeigo.rest.models.User;

public class RegistrationRequest {

    private User user;

    public RegistrationRequest(String email, String username, String password) {
        this.user = new User();
        this.user.setEmail(email);
        this.user.setUsername(username);
        this.user.setPassword(password);
    }
}