package za.co.rationalthinkers.unoplayer.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable {

    private String id = "";
    private String albumId = "";
    private String name = "";
    private String artistId = "";
    private String artistName = "";
    private int numberOfSongs = 0;
    private List<Song> songs = new ArrayList<>();

    public Album(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getName() {
        return name;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public List<Song> getSongs() {
        return songs;
    }


    /**
     * Parcelable implementations
     */
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(albumId);
        dest.writeString(name);
        dest.writeString(artistId);
        dest.writeString(artistName);
        dest.writeInt(numberOfSongs);
        dest.writeList(songs);
    }

    public Album(Parcel in) {
        songs = new ArrayList<Song>();

        id = in.readString();
        albumId = in.readString();
        name = in.readString();
        artistId = in.readString();
        artistName = in.readString();
        numberOfSongs = in.readInt();
        in.readList(songs, Song.class.getClassLoader());
    }
}
