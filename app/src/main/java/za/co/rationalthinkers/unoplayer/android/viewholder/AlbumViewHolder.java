package za.co.rationalthinkers.unoplayer.android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.rationalthinkers.unoplayer.android.R;

public class AlbumViewHolder extends RecyclerView.ViewHolder {

    public ImageView picView;
    public TextView nameView;
    public TextView artistView;

    public AlbumViewHolder(View view) {
        super(view);

        picView = view.findViewById(R.id.album_pic);
        nameView = view.findViewById(R.id.album_name);
        artistView = view.findViewById(R.id.album_artist);
    }

}
