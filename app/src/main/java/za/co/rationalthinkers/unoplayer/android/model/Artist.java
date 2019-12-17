package za.co.rationalthinkers.unoplayer.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Artist implements Parcelable {

    private String id = "";
    private String name = "";
    private int numberOfSongs = 0;
    private int numberOfAlbums = 0;
    private List<Album> albums = new ArrayList<>();
    private List<Song> songs = new ArrayList<>();

    public Artist(){

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

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
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

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Song> getSongs() {
        return songs;
    }


    /**
     * Parcelable implementations
     */
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
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
        dest.writeInt(numberOfAlbums);
        dest.writeList(albums);
        dest.writeList(songs);
    }

    public Artist(Parcel in) {
        albums = new ArrayList<Album>();
        songs = new ArrayList<Song>();

        id = in.readString();
        name = in.readString();
        numberOfSongs = in.readInt();
        numberOfAlbums = in.readInt();
        in.readList(albums, Album.class.getClassLoader());
        in.readList(songs, Song.class.getClassLoader());
    }
}
