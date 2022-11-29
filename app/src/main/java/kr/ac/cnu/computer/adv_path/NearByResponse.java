package kr.ac.cnu.computer.adv_path;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearByResponse {
    @Expose
    @SerializedName("results")
    List<Place> results;

}
