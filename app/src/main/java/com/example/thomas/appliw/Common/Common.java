package com.example.thomas.appliw.Common;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String API_KEY = "94fc236e3172357fb45c2ab75ee3f509";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/weather"; //pour recevoir la meteo de la journée
    public static String API_LINK_5D = "http://api.openweathermap.org/data/2.5/forecast"; //meteo des 5j à venir

    @NonNull
    public static String apiRequest(String lat, String lng, int choix){
        StringBuilder sb = new StringBuilder(API_LINK);
        StringBuilder sb5 = new StringBuilder(API_LINK_5D);

        switch (choix){
            case 1:
                sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric&lang=fr",lat,lng,API_KEY));
                return sb.toString();
            case 5:
                sb5.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric&lang=fr",lat,lng,API_KEY));
                return sb5.toString();
            default:
                return null;
        }
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
