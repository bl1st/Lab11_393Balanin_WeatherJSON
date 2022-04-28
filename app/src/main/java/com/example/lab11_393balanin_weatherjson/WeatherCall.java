package com.example.lab11_393balanin_weatherjson;

public class WeatherCall {
    public String Date;
    public String City;
    public String Information;
    //balanin 393 lab 11
    public WeatherCall() {}

    @Override
    public String toString() {
        return City + " | " + Date;
    }
}
