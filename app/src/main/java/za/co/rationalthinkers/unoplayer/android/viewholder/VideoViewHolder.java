package za.co.rationalthinkers.unoplayer.android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.rationalthinkers.unoplayer.android.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public ImageView picView;
    public TextView nameView;
    public TextView infoView;

    public VideoViewHolder(View view) {
        super(view);

        picView = view.findViewById(R.id.video_pic);
        nameView = view.findViewById(R.id.video_name);
        infoView = view.findViewById(R.id.video_info);
    }

}
