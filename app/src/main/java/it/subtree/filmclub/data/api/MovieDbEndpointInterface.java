package it.subtree.filmclub.data.api;

import it.subtree.filmclub.data.model.MoviesResponse;
import it.subtree.filmclub.data.model.ReviewsResponse;
import it.subtree.filmclub.data.model.TrailersResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbEndpointInterface {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{movie_id}/videos")
    Call<TrailersResponse> getTrailersForMovie(@Path("movie_id") long movie_id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewsResponse> getReviewsForMovie(@Path("movie_id") long movie_id, @Query("api_key") String apiKey, @Query("page") int page);

}
