package com.example.voiceassistant2.weatherstack.api;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.voiceassistant2.dialog.AI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForecastToString {

    public static void getForecast(String city, final Consumer<String> callback){

        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response){
                Forecast result = response.body();
                AI ai = new AI();
                if (result.current.temperature!=null) {
                   //String answer = "Сейчас где-то " + result.current.temperature + " градусов"  + " и " + result.current.weather_descriptions.get(0);
                    String answer = "Сейчас где-то " + result.current.temperature.toString() + ai.getDegreeEnding(result.current.temperature) + " и " + result.current.weather_descriptions.get(0);

                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу узнать погоду");
                }
            }
            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.w("WEATHER",t.getMessage());

            }
        });

    }


}
