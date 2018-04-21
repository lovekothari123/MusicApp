package com.djgeraldo.data.Dj;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by sachin on 20-01-2018.
 */

public interface DjApi {
    @GET("dj")
    Call<Dj> getDjs();
}
