package it.subtree.filmclub.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Review;

public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static String TAG = ReviewViewHolder.class.getSimpleName();

    @BindView(R.id.tv_review_author)
    protected TextView mAuthor;
    @BindView(R.id.tv_review_content)
    protected TextView mContent;

    private Review mReview;

    public ReviewViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);

        itemView.setOnClickListener(this);
    }

    public void bind(@NonNull final Review review) {
        mReview = review;
        mAuthor.setText(mReview.getAuthor());
        mContent.setText(mReview.getContent());
    }

    @Override
    public void onClick(View v) {

    }
}
