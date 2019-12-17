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
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.viewholder.ArtistViewHolder;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistViewHolder> {

    private Context context;
    private List<Artist> list;
    private ArtistsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onArtistClick(View v, int position);
    }

    public ArtistsAdapter(List<Artist> list,ArtistsAdapter.OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);

        final ArtistViewHolder viewHolder = new ArtistViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArtistClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtistViewHolder holder, final int position) {

        Artist artist = list.get(position);

        String name = artist.getName();
        String details = artist.getNumberOfAlbums() + " Albums - " + artist.getNumberOfSongs() + " Songs";

        holder.nameView.setText(name);
        holder.infoView.setText(details);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    public void add(Artist name) {
        if(!listContains(list, name)) {
            list.add(name);
            notifyItemInserted(getItemCount());
        }
    }

    public void update(List<Artist> artists) {
        list.clear();
        list.addAll(artists);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position); // remove contact
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(Artist name) {
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

    private boolean listContains(List<Artist> names, Artist name) {

        for(Artist item: names) {
            if(name == item){
                return true;
            }
        }

        return false;
    }

}
