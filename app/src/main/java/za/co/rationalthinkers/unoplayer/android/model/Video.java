package za.co.rationalthinkers.unoplayer.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    private String id = "";
    private String title = "";
    private long duration = 0L;
    private long size = 0L;
    private String resolution = "";
    private String width = "";
    private String height = "";
    private String artistName = "";
    private String albumTitle = "";
    private String path = "";
    private long dateAdded = 0L;

    public Video(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public String getResolution() {
        return resolution;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getPath() {
        return path;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    /**
     * Parcelable implementations
     */
    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(resolution);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(artistName);
        dest.writeString(albumTitle);
        dest.writeString(path);
        dest.writeLong(dateAdded);
    }

    public Video(Parcel in) {
        id = in.readString();
        title = in.readString();
        duration = in.readLong();
        size = in.readLong();
        resolution = in.readString();
        width = in.readString();
        height = in.readString();
        artistName = in.readString();
        albumTitle = in.readString();
        path = in.readString();
        dateAdded = in.readLong();
    }
}
