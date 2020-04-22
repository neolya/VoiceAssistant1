package com.example.voiceassistant2.weatherstack.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

//import retrofit2.Call;
//import retrofit2.http.GET;
//import retrofit2.http.Query;


public class Forecast implements Serializable {

    @SerializedName("current")
    @Expose
    public Weather current;

    public class Weather{
        @SerializedName("temperature")
        @Expose

        public Integer temperature;

        @SerializedName("weather_descriptions")
        @Expose

        public List<String> weather_descriptions;

    }
}

//interface ForecastApi
//{
//    @GET("/current?access_key=25460c23fd0102cf8300af004f9a5de7")
//    Call<Forecast> getCurrentWeather(@Query("query") String city);
//}
