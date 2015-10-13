package com.moobasoft.damego.rest.errors;

import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Converter;
import retrofit.GsonConverterFactory;

public class RegistrationError {

    private List<String> username;
    private List<String> email;
    private List<String> password;

    @SuppressWarnings("unchecked")
    public static final Converter<ResponseBody, RegistrationError> CONVERTER =
            (Converter< ResponseBody, RegistrationError>) GsonConverterFactory
                    .create().fromResponseBody(RegistrationError.class, null);

    // TODO: Make a static util method
    private String composeErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) return null;
        String errorString = "";
        for (String s : errors)
            errorString += s + "\n";
        return errorString.trim();
    }

    public String getUsername() {
        return composeErrors(username);
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public String getEmail() {
        return composeErrors(email);
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getPassword() {
        return composeErrors(password);
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }
}
