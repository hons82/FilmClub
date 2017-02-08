package it.subtree.filmclub.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Movie;
import it.subtree.filmclub.ui.viewholder.MovieViewHolder;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> mMovieList;

    public MoviesAdapter(List<Movie> movies) {
        this.mMovieList = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.bind(mMovieList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void addMovies(List<Movie> movies) {
        mMovieList.addAll(movies);
    }
}