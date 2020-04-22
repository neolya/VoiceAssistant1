package com.example.voiceassistant2.digit.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
// https://htmlweb.ru/service/number2word.php
public class Convert implements Serializable {

    @SerializedName("str")
    @Expose
    public String convertedStr;
}
