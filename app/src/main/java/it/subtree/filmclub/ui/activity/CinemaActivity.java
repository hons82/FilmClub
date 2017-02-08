package it.subtree.filmclub.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Movie;

public class CinemaActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "it.subtree.filmclub.ui.activity.CinemaActivity.EXTRA_MOVIE";

    public ImageView mCover;
    public TextView mTitle;
    public TextView mAvgRating;
    public TextView mVotes;
    public TextView mOverview;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);

        mCover = (ImageView) findViewById(R.id.iv_movie_cover);
        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mAvgRating = (TextView) findViewById(R.id.tv_movie_avg_rating);
        mVotes = (TextView) findViewById(R.id.tv_movie_votes);
        mOverview = (TextView) findViewById(R.id.tv_movie_overview);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            loadMovieDetail(mMovie);
        }
    }

    private void loadMovieDetail(Movie movie) {
        mTitle.setText(mMovie.getTitle());
        mAvgRating.setText(getResources().getString(R.string.movie_avg_rating) + ": " + movie.getVoteAverage());
        mVotes.setText(getResources().getString(R.string.movie_votes, movie.getVoteCount()));
        mOverview.setText(movie.getOverview());

        Glide.with(this).load(Movie.TMDB_IMAGE_PATH + movie.getPosterPath())
                .placeholder(R.color.colorMovieCoverPlaceholder)
                .fitCenter()
                .crossFade()
                .into(mCover);


    }
}
