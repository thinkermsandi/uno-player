package za.co.rationalthinkers.unoplayer.android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import za.co.rationalthinkers.unoplayer.android.R;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView infoView;
    public ImageView optionsView;

    public PlaylistViewHolder(View view) {
        super(view);

        titleView = view.findViewById(R.id.playlist_title);
        infoView = view.findViewById(R.id.playlist_info);
        optionsView = view.findViewById(R.id.playlist_options);
    }

}
