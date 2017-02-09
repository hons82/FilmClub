package it.subtree.filmclub.ui.viewholder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.ui.activity.CinemaActivity;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static String TAG = MovieViewHolder.class.getSimpleName();

    @BindView(R.id.iv_movie_cover)
    protected ImageView mCover;
    @BindView(R.id.tv_movie_title)
    protected TextView mTitle;
    @BindView(R.id.tv_movie_year)
    protected TextView mYear;

    private Movie mMovie;

    public MovieViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);

        itemView.setOnClickListener(this);
    }

    public void bind(@NonNull final Movie movie) {
        mMovie = movie;
        mTitle.setText(movie.getTitle());
        mYear.setText(movie.getReleaseDate().substring(0, Math.min(movie.getReleaseDate().length(), 4)));

        Glide.with(itemView.getContext()).load(Movie.TMDB_IMAGE_PATH + movie.getPosterPath())
                .placeholder(R.color.colorMovieCoverPlaceholder)
                .fitCenter()
                .crossFade()
                .into(mCover);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), CinemaActivity.class);

        intent.putExtra(CinemaActivity.EXTRA_MOVIE, mMovie);

        v.getContext().startActivity(intent);
    }
}
