package com.example.thomas.appliw.Model;

/**
 * Created by reale on 05/10/2016.
 */

public class Wind {

    private double speed;
    private double deg;

    public Wind(double speed, double deg) {
        this.speed = speed;
        this.deg = deg;
    }

    public double getSpeed() {
        return speed*3.6;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getDeg() {
        if(deg>180){
            deg=deg-180;
        }else{
            deg = deg+180;
        }

        if ((deg>330 && deg<=360)|| (deg<30 && deg>= 0) ){
            return "E";
        }
        else if(deg >60 && deg<120){
            return "N";
        }
        else if(deg>150 && deg<210){
            return "O";
        }
        else if(deg>240 && deg <300){
            return "S";
        }
        else if(deg>=30 && deg<=60){
            return "NE";
        }
        else if(deg>=120 && deg<=150){
            return "NO";
        }
        else if(deg>=210 && deg<=240){
            return "SO";
        }
        else if(deg>=300 && deg<=330){
            return "SE";
        }
        else{
            return "error "+String.valueOf(deg);
        }
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
