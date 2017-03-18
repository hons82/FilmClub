package it.subtree.filmclub.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Trailer;
import it.subtree.filmclub.ui.viewholder.TrailerViewHolder;

public class TrailersAdapter extends RecyclerView.Adapter<TrailerViewHolder> {

    private List<Trailer> mTrailerList;

    public TrailersAdapter(List<Trailer> trailers) {
        this.mTrailerList = trailers;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, final int position) {
        holder.bind(mTrailerList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public void addReviews(List<Trailer> trailers) {
        mTrailerList.addAll(trailers);
    }
}