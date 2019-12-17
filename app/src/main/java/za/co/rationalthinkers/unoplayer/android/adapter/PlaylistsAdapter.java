package za.co.rationalthinkers.unoplayer.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.viewholder.PlaylistViewHolder;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private Context context;
    private List<Playlist> list;
    private PlaylistsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPlaylistClick(View v, int position);
        void onPlaylistOptionsClick(View v, int position);
    }

    public PlaylistsAdapter(List<Playlist> list, PlaylistsAdapter.OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);

        final PlaylistViewHolder viewHolder = new PlaylistViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onPlaylistClick(v, viewHolder.getAdapterPosition());
                }
            }
        });

        viewHolder.optionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onPlaylistOptionsClick(v, viewHolder.getAdapterPosition());
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaylistViewHolder holder, final int position) {

        Playlist playlist = list.get(position);

        String title = playlist.getName();
        String details = playlist.getNumberOfSongs() + " Songs";

        holder.titleView.setText(title);
        holder.infoView.setText(details);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    public void add(Playlist name) {
        if(!listContains(list, name)) {
            list.add(name);
            notifyItemInserted(getItemCount());
        }
    }

    public void update(List<Playlist> playlists) {
        list.clear();
        list.addAll(playlists);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position); // remove contact
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(Playlist name) {
        if(listContains(list, name)) {
            int position = list.indexOf(name);
            list.remove(name);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    public void clear(){
        list.clear();
    }

    private boolean listContains(List<Playlist> names, Playlist name) {

        for(Playlist item: names) {
            if(name == item){
                return true;
            }
        }

        return false;
    }

}
