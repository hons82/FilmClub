package it.subtree.filmclub.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Movie;

public class CinemaActivity extends AppCompatActivity {

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
    @BindView(R.id.tv_movie_overview)
    protected TextView mOverview;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            loadMovieDetail(mMovie);
        }
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


    }
}
