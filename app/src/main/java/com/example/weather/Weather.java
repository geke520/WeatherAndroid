package com.example.weather;

import java.util.List;

public class Weather {
    private String day = "1日(今天)";
    private String date = "2019-1-1";
    private String week = "星期二";
    private String wea = "晴";
    private String weaImg = "qing";
    private String airLevel = "良";
    private String tem1 = "16℃";
    private String tem2 = "6℃";
    private String tem = "9℃";
    private String winSpeed = "<3级";
     //private List<HourWeather> hours;
     // private LifeTip ultravioletRays;
     // private LifeTip cloth;
     // private LifeTip air;

    public Weather(){}


    public Weather(String day, String date, String week, String wea) {
        this.day = day;
        this.date = date;
        this.week = week;
        this.wea = wea;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getWeaImg() {
        return weaImg;
    }

    public void setWeaImg(String weaImg) {
        this.weaImg = weaImg;
    }

    public String getAirLevel() {
        return airLevel;
    }

    public void setAirLevel(String airLevel) {
        this.airLevel = airLevel;
    }

    public String getTem1() {
        return tem1;
    }

    public void setTem1(String tem1) {
        this.tem1 = tem1;
    }

    public String getTem2() {
        return tem2;
    }

    public void setTem2(String tem2) {
        this.tem2 = tem2;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getWinSpeed() {
        return winSpeed;
    }

    public void setWinSpeed(String winSpeed) {
        this.winSpeed = winSpeed;
    }


    @Override
    public String toString() {
        return "Weather{" +
                "day='" + day + '\'' +
                ", date='" + date + '\'' +
                ", week='" + week + '\'' +
                ", wea='" + wea + '\'' +
                ", weaImg='" + weaImg + '\'' +
                ", airLevel='" + airLevel + '\'' +
                ", tem1='" + tem1 + '\'' +
                ", tem2='" + tem2 + '\'' +
                ", tem='" + tem + '\'' +
                '}';
    }

    /**
    public String getAirTips() {
        return airTips;
    }

    public void setAirTips(String airTips) {
        this.airTips = airTips;
    }

    public String getWinSpeed() {
        return winSpeed;
    }

    public void setWinSpeed(String winSpeed) {
        this.winSpeed = winSpeed;
    }

    public String getTem1() {
        return tem1;
    }

    public void setTem1(String tem1) {
        this.tem1 = tem1;
    }

    public String getTem2() {
        return tem2;
    }

    public void setTem2(String tem2) {
        this.tem2 = tem2;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public List<HourWeather> getHours() {
        return hours;
    }

    public void setHours(List<HourWeather> hours) {
        this.hours = hours;
    }

    public LifeTip getUltravioletRays() {
        return ultravioletRays;
    }

    public void setUltravioletRays(LifeTip ultravioletRays) {
        this.ultravioletRays = ultravioletRays;
    }

    public LifeTip getCloth() {
        return cloth;
    }

    public void setCloth(LifeTip cloth) {
        this.cloth = cloth;
    }

    public LifeTip getAir() {
        return air;
    }

    public void setAir(LifeTip air) {
        this.air = air;
    }
     */
}

class HourWeather{
    private String day;
    private String wea;
    private String tem;

    public HourWeather( String day, String wea, String tem) {
        this.day = day;
        this.wea = wea;
        this.tem = tem;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    @Override
    public String toString() {
        return "HourWeather{" +
                "day='" + day + '\'' +
                ", wea='" + wea + '\'' +
                ", tem='" + tem + '\'' +
                '}';
    }
}

class LifeTip{
    private String title;
    private String desc;

    public LifeTip(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "LifeTip{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}