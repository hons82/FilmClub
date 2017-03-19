package it.subtree.filmclub.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.db.MoviesContract;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.ui.viewholder.MovieViewHolder;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> mMovieList;
    private Cursor mMovieCursor;

    public MoviesAdapter(List<Movie> movies) {
        this.mMovieList = movies;
    }
    public MoviesAdapter(Cursor movies) {
        this.mMovieCursor = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        if (mMovieCursor != null) {
            mMovieCursor.moveToPosition(position);
            long movie_id = mMovieCursor.getLong(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
            String title = mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE));
            String posterPath = mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));
            String overview = mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
            double vote_average = mMovieCursor.getDouble(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE));
            String releaseDate = mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
            holder.bind(new Movie(movie_id, title, posterPath, overview, vote_average, releaseDate));
        } else {
            holder.bind(mMovieList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieCursor != null) {
            return mMovieCursor.getCount();
        } else {
            return mMovieList.size();
        }
    }

    public void addMovies(List<Movie> movies) {
        if (mMovieList != null) {
            mMovieList.addAll(movies);
            this.notifyDataSetChanged();
        } else {
            Log.w(TAG, "not implemented");
        }
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mMovieCursor != null && mMovieCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mMovieCursor;
        this.mMovieCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.mMovieList = null;
            this.notifyDataSetChanged();
        }
        return temp;
    }

}