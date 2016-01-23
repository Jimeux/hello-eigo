package com.moobasoft.helloeigo.events.comment;

import com.moobasoft.helloeigo.rest.models.Comment;

public class CommentCreatedEvent {

    private final Comment comment;

    public CommentCreatedEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
