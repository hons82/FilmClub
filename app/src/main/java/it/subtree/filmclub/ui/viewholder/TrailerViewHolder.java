package it.subtree.filmclub.ui.viewholder;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.subtree.filmclub.R;
import it.subtree.filmclub.data.model.Trailer;

public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static String TAG = TrailerViewHolder.class.getSimpleName();

    @BindView(R.id.iv_trailer_thumbnail)
    protected ImageView mThumbnail;
    @BindView(R.id.iv_trailer_play)
    protected ImageView mPlay;

    private Trailer mTrailer;

    public TrailerViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);

        itemView.setOnClickListener(this);
    }

    public void bind(@NonNull final Trailer trailer) {
        mTrailer = trailer;

        Glide.with(itemView.getContext()).load(mTrailer.getThumbnailUrl())
                .placeholder(R.color.colorTrailerPlaceholder)
                .fitCenter()
                .crossFade()
                .into(mThumbnail);
    }

    @Override
    public void onClick(View v) {
        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailer.getTrailerUrl())));
    }
}
