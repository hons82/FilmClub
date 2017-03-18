package it.subtree.filmclub.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Review;
import it.subtree.filmclub.ui.viewholder.ReviewViewHolder;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private List<Review> mReviewList;

    public ReviewsAdapter(List<Review> reviews) {
        this.mReviewList = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, final int position) {
        holder.bind(mReviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public void addReviews(List<Review> reviews) {
        mReviewList.addAll(reviews);
    }
}