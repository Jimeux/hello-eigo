package com.moobasoft.damego.rest.models;

import java.util.List;

import retrofit.Converter;
import retrofit.GsonConverterFactory;

public class CommentError {
    private List<String> body;

    @SuppressWarnings("unchecked")
    public static final Converter<CommentError> CONVERTER =
            (Converter<CommentError>) GsonConverterFactory
                    .create().get(CommentError.class);

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }
}
