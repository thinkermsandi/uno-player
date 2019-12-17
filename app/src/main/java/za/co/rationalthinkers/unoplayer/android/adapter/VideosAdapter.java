package za.co.rationalthinkers.unoplayer.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.loader.VideoLoader;
import za.co.rationalthinkers.unoplayer.android.model.Video;
import za.co.rationalthinkers.unoplayer.android.util.Utils;
import za.co.rationalthinkers.unoplayer.android.viewholder.VideoViewHolder;

public class VideosAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private Context context;
    private List<Video> list;
    private VideosAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onVideoClick(View v, int position);
    }

    public VideosAdapter(List<Video> list, VideosAdapter.OnItemClickListener listener){
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

        final VideoViewHolder viewHolder = new VideoViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVideoClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, final int position) {

        Video video = list.get(position);

        String title = video.getTitle();
        String details = Utils.millisToString(video.getDuration());
        String path = video.getPath();

        holder.nameView.setText(title);
        holder.infoView.setText(details);

        /*Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        Glide.with(context)
                .load(bitmap)
                .into(holder.picView);*/

        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .into(holder.picView);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

    public void add(Video name) {
        if(!listContains(list, name)) {
            list.add(name);
            notifyItemInserted(getItemCount());
        }
    }

    public void update(List<Video>videos) {
        list.clear();
        list.addAll(videos);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position); // remove contact
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(Video name) {
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

    private boolean listContains(List<Video> names, Video name) {

        for(Video item: names) {
            if(name == item){
                return true;
            }
        }

        return false;
    }

}
