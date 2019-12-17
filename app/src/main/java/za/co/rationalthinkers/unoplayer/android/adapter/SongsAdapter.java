package za.co.rationalthinkers.unoplayer.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.viewholder.SongViewHolder;

public class SongsAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private Context context;
    private List<Song> list;
    private SongsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSongClick(View v, int position);
    }

    public SongsAdapter(List<Song> list, SongsAdapter.OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);

        final SongViewHolder viewHolder = new SongViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSongClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SongViewHolder holder, final int position) {

        Song song = list.get(position);

        String name = song.getTitle();
        String details = song.getArtistName();

        holder.nameView.setText(name);
        holder.infoView.setText(details);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    public void add(Song name) {
        if(!listContains(list, name)) {
            list.add(name);
            notifyItemInserted(getItemCount());
        }
    }

    public void update(List<Song> songs) {
        list.clear();
        list.addAll(songs);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position); // remove contact
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(Song name) {
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

    private boolean listContains(List<Song> names, Song name) {

        for(Song item: names) {
            if(name == item){
                return true;
            }
        }

        return false;
    }

}
