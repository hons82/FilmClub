package it.subtree.filmclub.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class Trailer implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("iso_3166_1")
    @Expose
    private String iso31661;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("size")
    @Expose
    private long size;
    @SerializedName("type")
    @Expose
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnailUrl() {
        if (getSite().equalsIgnoreCase("YouTube")) {
            return "http://img.youtube.com/vi/" + getKey() + "/0.jpg";
        } else {
            Log.w(TAG, "Site not recognized " + getSite());
        }
        return "";
    }

    public String getTrailerUrl() {
        if (getSite().equalsIgnoreCase("YouTube")) {
            return "http://www.youtube.com/watch?v=" + getKey();
        } else {
            Log.w(TAG, "Site not recognized " + getSite());
        }
        return "";
    }

    public int describeContents() {
        return 0;
    }

    ;

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso6391);
        dest.writeString(iso31661);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeLong(size);
        dest.writeString(type);
    }

    protected Trailer(Parcel in) {
        this.id = in.readString();
        this.iso6391 = in.readString();
        this.iso31661 = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readLong();
        this.type = in.readString();
    }

    public final static Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {

        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        public Trailer[] newArray(int size) {
            return (new Trailer[size]);
        }

    };

}