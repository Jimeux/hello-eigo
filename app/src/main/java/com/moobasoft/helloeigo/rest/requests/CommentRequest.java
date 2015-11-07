package com.moobasoft.helloeigo.rest.requests;

import com.moobasoft.helloeigo.rest.models.Comment;

public class CommentRequest {
    private Comment comment;

    public CommentRequest(String body) {
        this.comment = new Comment();
        this.comment.setBody(body);
    }
}