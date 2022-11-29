package kr.ac.cnu.computer.adv_path;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {
    @Expose
    @SerializedName("place_id")
    String place_id;

    @Expose
    @SerializedName("vicinity")
    String vicinity;

    @Expose
    @SerializedName("name")
    String name;

    @Expose
    @SerializedName("geometry")
    Geometry geometry;

    @Expose
    @SerializedName("icon")
    String icon;
}
