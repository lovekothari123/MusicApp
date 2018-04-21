package com.djgeraldo.data.Genre;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by sachin on 20-01-2018.
 */

public interface GenreApi {

    @GET("genre")
    Call<GenreList> getGenres();
}
