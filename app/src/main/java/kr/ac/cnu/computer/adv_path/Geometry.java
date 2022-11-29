package kr.ac.cnu.computer.adv_path;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @Expose
    @SerializedName("location")
    LatLngLiteral location;
}
