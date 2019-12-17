package za.co.rationalthinkers.unoplayer.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class Song implements Parcelable {

    private String id = "";
    private String title = "";
    private long duration = 0L;
    private long size = 0L;
    private int trackNumber = 0;
    private int year = 0;
    private String artistId = "";
    private String artistName = "";
    private String albumId = "";
    private String albumTitle = "";
    private String path = "";
    private long dateAdded = 0L;
    private Artist artist = new Artist();
    private Album album = new Album();

    public Song(){

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

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
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

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setAlbum(Album album) {
        this.album = album;
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

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumId() {
        return albumId;
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

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    /**
     * Parcelable implementations
     */
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
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
        dest.writeInt(trackNumber);
        dest.writeInt(year);
        dest.writeString(artistId);
        dest.writeString(artistName);
        dest.writeString(albumId);
        dest.writeString(albumTitle);
        dest.writeString(path);
        dest.writeLong(dateAdded);
        dest.writeParcelable(artist, flags);
        dest.writeParcelable(album, flags);
    }

    public Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        duration = in.readLong();
        size = in.readLong();
        trackNumber = in.readInt();
        year = in.readInt();
        artistId = in.readString();
        artistName = in.readString();
        albumId = in.readString();
        albumTitle = in.readString();
        path = in.readString();
        dateAdded = in.readLong();
        artist = in.readParcelable(Artist.class.getClassLoader());
        album = in.readParcelable(Album.class.getClassLoader());
    }
}
