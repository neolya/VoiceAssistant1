package com.example.voiceassistant2.weatherstack.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=0419285119ec13ae814b684b5a365ab6")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
//http://api.weatherstack.com/current?access_key=25460c23fd0102cf8300af004f9a5de7&query=%D0%BC%D0%BE%D1%81%D0%BA%D0%B2%D0%B0