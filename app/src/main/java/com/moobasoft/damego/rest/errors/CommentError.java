package com.moobasoft.damego.rest.errors;

import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Converter;
import retrofit.GsonConverterFactory;

public class CommentError {
    private List<String> body;

    @SuppressWarnings("unchecked")
    public static final Converter<ResponseBody, CommentError> CONVERTER =
            (Converter<ResponseBody, CommentError>) GsonConverterFactory
                    .create().fromResponseBody(CommentError.class, null);

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }
}
