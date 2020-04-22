package com.example.voiceassistant2.digit.api;

import com.example.voiceassistant2.digit.api.Convert;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConvertApi {
    @GET("/json/convert/num2str")
    Call<Convert> getConvert(@Query("num") String number);
}

