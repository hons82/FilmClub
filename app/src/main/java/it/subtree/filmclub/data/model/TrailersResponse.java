package it.subtree.filmclub.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("results")
    @Expose
    private List<Trailer> results = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    protected TrailersResponse(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (Movie.class.getClassLoader()));
    }

    public final static Parcelable.Creator<TrailersResponse> CREATOR = new Creator<TrailersResponse>() {

        public TrailersResponse createFromParcel(Parcel source) {return new TrailersResponse(source);}

        public TrailersResponse[] newArray(int size) {
            return (new TrailersResponse[size]);
        }

    };

}