package za.co.rationalthinkers.unoplayer.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Parcelable {

    private String id = "";
    private String name = "";
    private int numberOfSongs = 0;
    private long dateAdded = 0;
    private List<Song> songs = new ArrayList<>();

    public Playlist(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public List<Song> getSongs() {
        return songs;
    }


    /**
     * Parcelable implementations
     */
    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(numberOfSongs);
        dest.writeLong(dateAdded);
        dest.writeList(songs);
    }

    public Playlist(Parcel in) {
        songs = new ArrayList<Song>();

        id = in.readString();
        name = in.readString();
        numberOfSongs = in.readInt();
        dateAdded = in.readLong();
        in.readList(songs, Song.class.getClassLoader());
    }
}
