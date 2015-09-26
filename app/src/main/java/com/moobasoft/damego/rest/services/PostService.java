package com.moobasoft.damego.rest.services;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.requests.CommentRequest;

import java.util.List;

import retrofit.Result;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface PostService {

    @GET("/api/posts/{id}")
    Observable<Result<Post>> show(@Path("id") int id);

    @GET("/api/posts")
    Observable<Result<List<Post>>> index(@Query("page") int page);

    @GET("/api/tag/{name}")
    Observable<Result<List<Post>>> filterByTag(@Path("name")  String tag,
                                               @Query("page") int page);

    @POST("/api/posts/{id}/comments")
    Observable<Result<Comment>> createComment(@Path("id") int postId,
                                              @Body CommentRequest commentRequest);
}