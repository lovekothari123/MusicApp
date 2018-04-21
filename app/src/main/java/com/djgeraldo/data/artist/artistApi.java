package com.djgeraldo.data.artist;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by sachin on 20-01-2018.
 */

public interface artistApi {

    @GET("artist")
    Call<artistList> getArtist();

}
