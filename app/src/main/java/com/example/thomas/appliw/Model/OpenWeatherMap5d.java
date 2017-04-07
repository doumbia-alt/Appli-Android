package com.example.thomas.appliw.Model;

import java.util.List;

public class OpenWeatherMap5d {

    private int cod;

    public OpenWeatherMap5d(int cod, double message, int cnt, List<MeteoList> list, City city) {
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

    public double getMessage() {
        return message;
    }

    public void setMessage(double message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public List<MeteoList> getList() {
        return list;
    }

    public void setList(List<MeteoList> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    private double message;
    private int cnt;
    private List<MeteoList> list;
    private City city;



    public OpenWeatherMap5d() {
    }




    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }


    //TODO City

}
