package kr.ac.cnu.computer.adv_path;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearByRetrofit {
    // @GET( EndPoint-자원위치(URI) )
    @GET("maps/api/place/nearbysearch/json") //HTTP 메서드 및 URL
    //Requests 타입의 DTO 데이터와 API 키를 요청

    Call<NearByResponse> getPosts(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String key, @Query("language") String language);
}