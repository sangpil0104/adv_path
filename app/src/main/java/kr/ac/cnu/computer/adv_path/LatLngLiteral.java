package kr.ac.cnu.computer.adv_path;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLngLiteral {
    @Expose
    @SerializedName("lat")
    double lat;

    @Expose
    @SerializedName("lng")
    double lng;

    public LatLng getLatLng(){
        return new LatLng(lat, lng);
    }

}
