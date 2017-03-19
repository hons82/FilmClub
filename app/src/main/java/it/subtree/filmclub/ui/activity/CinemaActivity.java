package it.subtree.filmclub.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.api.MovieDbApiClient;
import it.subtree.filmclub.data.api.MovieDbEndpointInterface;
import it.subtree.filmclub.data.db.MoviesContract;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.data.model.Review;
import it.subtree.filmclub.data.model.ReviewsResponse;
import it.subtree.filmclub.data.model.Trailer;
import it.subtree.filmclub.data.model.TrailersResponse;
import it.subtree.filmclub.ui.adapter.ReviewsAdapter;
import it.subtree.filmclub.ui.adapter.TrailersAdapter;
import it.subtree.filmclub.util.EndlessRecyclerOnScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CinemaActivity extends AppCompatActivity {

    public static String TAG = CinemaActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "it.subtree.filmclub.ui.activity.CinemaActivity.EXTRA_MOVIE";

    @BindView(R.id.iv_movie_cover)
    protected ImageView mCover;
    @BindView(R.id.tv_movie_title)
    protected TextView mTitle;
    @BindView(R.id.tv_movie_release_date)
    protected TextView mReleaseDate;
    @BindView(R.id.tv_movie_avg_rating)
    protected TextView mAvgRating;
    @BindView(R.id.tv_movie_votes)
    protected TextView mVotes;
    @BindView(R.id.ib_favorites)
    protected ImageButton mFavorites;
    @BindView(R.id.tv_movie_overview)
    protected TextView mOverview;
    @BindView(R.id.rv_trailer)
    protected RecyclerView mTrailer;
    @BindView(R.id.rv_review)
    protected RecyclerView mReviews;

    private Movie mMovie;
    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);
        ButterKnife.bind(this);

        mTrailer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            loadMovieDetail(mMovie);
        }

        mFavorites.setSelected(isFavorite());
        mFavorites.setOnClickListener(new View.OnClickListener() {

            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    //Handle selected state change
                    markAsFavorite();
                } else {
                    //Handle de-select state change
                    removeFromFavorites();
                }

            }

        });
    }

    private boolean isFavorite() {
        Cursor movieCursor = this.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry.COLUMN_MOVIE_ID},
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getPosterPath());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getVoteAverage());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    getContentResolver().insert(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                            MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);

                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void loadMovieDetail(Movie movie) {
        mTitle.setText(mMovie.getTitle());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mAvgRating.setText(getResources().getString(R.string.movie_avg_rating) + ": " + movie.getVoteAverage());
        mVotes.setText(getResources().getString(R.string.movie_votes, movie.getVoteCount()));
        mOverview.setText(movie.getOverview());

        Glide.with(this).load(Movie.TMDB_IMAGE_PATH + movie.getPosterPath())
                .placeholder(R.color.colorMovieCoverPlaceholder)
                .fitCenter()
                .crossFade()
                .into(mCover);

        loadTrailers(mMovie.getId());
        loadReviews(mMovie.getId());
    }

    private void loadTrailers(final long movie_id) {
        updateTrailers(movie_id);
    }

    private void updateTrailers(final long movie_id) {
        if (MovieDbApiClient.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_api_key_missing), Toast.LENGTH_LONG).show();
            return;
        }
        if (mLoading == null || !mLoading.isShowing()) {
            mLoading = ProgressDialog.show(CinemaActivity.this, getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
        }

        MovieDbEndpointInterface apiService =
                MovieDbApiClient.getClient().create(MovieDbEndpointInterface.class);

        apiService.getTrailersForMovie(movie_id, MovieDbApiClient.API_KEY).enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                int statusCode = response.code();
                List<Trailer> trailers = null;
                if (statusCode == 200) {
                    trailers = response.body().getResults();
                } else {
                    Log.i(TAG, "StatusCode " + statusCode);
                    trailers = new ArrayList<Trailer>();
                }
                if (mTrailer.getAdapter() == null) {
                    mTrailer.setAdapter(new TrailersAdapter(trailers));
                } else {
                    ((TrailersAdapter) mTrailer.getAdapter()).addReviews(trailers);
                    mTrailer.requestLayout();
                }
                dismissLoading();
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                dismissLoading();

                View.OnClickListener reloadOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateTrailers(movie_id);
                    }
                };

                Snackbar.make(mReviews, getResources().getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), reloadOnClickListener)
                        .show();
            }
        });

    }

    private void loadReviews(final long movie_id) {
        updateReviews(movie_id, 1);
    }

    private void updateReviews(final long movie_id, final int page) {
        if (MovieDbApiClient.API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_api_key_missing), Toast.LENGTH_LONG).show();
            return;
        }

        if (mLoading == null || !mLoading.isShowing()) {
            mLoading = ProgressDialog.show(CinemaActivity.this, getResources().getString(R.string.loading_title), getResources().getString(R.string.loading_message));
        }

        MovieDbEndpointInterface apiService =
                MovieDbApiClient.getClient().create(MovieDbEndpointInterface.class);

        apiService.getReviewsForMovie(movie_id, MovieDbApiClient.API_KEY, page).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                int statusCode = response.code();
                List<Review> reviews = null;
                if (statusCode == 200) {
                    reviews = response.body().getResults();
                } else {
                    Log.i(TAG, "StatusCode " + statusCode);
                    reviews = new ArrayList<Review>();
                }
                if (page == 1 || mReviews.getAdapter() == null) {
                    mReviews.setAdapter(new ReviewsAdapter(reviews));
                    mReviews.clearOnScrollListeners();
                    mReviews.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) mReviews.getLayoutManager()) {
                        @Override
                        public void onLoadMore(int current_page) {
                            Log.i(TAG, "Load page " + current_page);
                            updateReviews(movie_id, current_page);
                        }
                    });
                } else {
                    ((ReviewsAdapter) mReviews.getAdapter()).addReviews(reviews);
                    mReviews.requestLayout();
                }
                dismissLoading();
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                dismissLoading();

                View.OnClickListener reloadOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateReviews(movie_id, page);
                    }
                };

                Snackbar.make(mReviews, getResources().getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), reloadOnClickListener)
                        .show();
            }
        });

    }

    private void dismissLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }
}
