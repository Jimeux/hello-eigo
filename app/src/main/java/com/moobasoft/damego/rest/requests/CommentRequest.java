package com.moobasoft.damego.rest.requests;

import com.moobasoft.damego.rest.models.Comment;

public class CommentRequest {
    private Comment comment;

    public CommentRequest(String body) {
        this.comment = new Comment();
        this.comment.setBody(body);
    }
}