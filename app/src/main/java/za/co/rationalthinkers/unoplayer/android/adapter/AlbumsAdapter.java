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
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.viewholder.AlbumViewHolder;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private Context context;
    private List<Album> list;
    private AlbumsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAlbumClick(View v, int position);
    }

    public AlbumsAdapter(List<Album> list, AlbumsAdapter.OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);

        final AlbumViewHolder viewHolder = new AlbumViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlbumClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder holder, final int position) {

        Album album = list.get(position);

        String name = album.getName();
        String details = album.getNumberOfSongs() + " songs";

        holder.nameView.setText(name);
        holder.artistView.setText(details);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    public void add(Album name) {
        if(!listContains(list, name)) {
            list.add(name);
            notifyItemInserted(getItemCount());
        }
    }

    public void update(List<Album> albums) {
        list.clear();
        list.addAll(albums);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position); // remove contact
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(Album name) {
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

    private boolean listContains(List<Album> names, Album name) {

        for(Album item: names) {
            if(name == item){
                return true;
            }
        }

        return false;
    }

}
