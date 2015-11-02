package com.moobasoft.damego.rest.services;

import com.moobasoft.damego.rest.models.Comment;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.rest.requests.CommentRequest;

import java.util.List;

import retrofit.Result;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

import static com.moobasoft.damego.Rest.CACHE_CONTROL_HEADER;

public interface PostService {

    @GET("/api/posts/{id}")
    Observable<Result<Post>> show(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                  @Path("id") int id);

    @GET("/api/posts")
    Observable<Result<List<Post>>> index(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                         @Query("page") int page);

    @GET("/api/tags")
    Observable<Result<List<String>>> getTags();

    @GET("/api/tag/{name}")
    Observable<Result<List<Post>>> filterByTag(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                               @Path("name") String tag,
                                               @Query("page") int page);

    @POST("/api/posts/{id}/comments")
    Observable<Result<Comment>> createComment(@Path("id") int postId,
                                              @Body CommentRequest commentRequest);

    @POST("/api/posts/{id}/bookmarks")
    Observable<Result<Void>> createBookmark(@Path("id") int postId);

    @DELETE("/api/bookmarks/{id}")
    Observable<Result<Void>> deleteBookmark(@Path("id") int postId);

    @GET("/api/bookmarks")
    Observable<Result<List<Post>>> getBookmarks(@Header(CACHE_CONTROL_HEADER) String cacheControl,
                                                @Query("page") int page);

    @GET("/api/search")
    Observable<Result<List<Post>>> search(@Query("query") String query,
                                          @Query("page")  int page);
}