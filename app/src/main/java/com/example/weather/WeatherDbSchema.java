package com.example.weather;

public class WeatherDbSchema {

    public static final class BasicWeatherTable {
        public static final String NAME = "basic_weather";
        public static final String CREATE = "create table " + NAME + "("
                + " id integer primary key autoincrement, "
                + Cols.CITY_NAME + ","
                + Cols.DAY + ","
                + Cols.DATE + ","
                + Cols.WEEK + ","
                + Cols.WEA + ","
                + Cols.WEA_IMG + ","
                + Cols.AIR_LEVEL + ","
                + Cols.AIR_TIPS + ","
                + Cols.TEM1 + ","
                + Cols.TEM2 + ","
                + Cols.TEM + ","
                + Cols.WIN_SPEED + ")";

        public static final class Cols {
            public static final String CITY_NAME = "city_name";
            public static final String DAY = "day";
            public static final String DATE = "date";
            public static final String WEEK = "week";
            public static final String WEA = "wea";
            public static final String WEA_IMG = "wea_img";
            public static final String AIR_LEVEL = "air_level";
            public static final String AIR_TIPS = "air_tips";
            public static final String TEM1 = "tem1";
            public static final String TEM2 = "tem2";
            public static final String TEM = "tem";
            public static final String WIN_SPEED = "win_speed";
        }
    }

    public static final class HourTable{
        public static final String NAME = "hours";
        public static final String CREATE = "create table " + NAME + "("
                + " id integer primary key autoincrement, "
                + Cols.CITY_NAME + ","
                + Cols.DATE + ","
                + Cols.DAY + ","
                + Cols.WEA + ","
                + Cols.TEM + ","
                + Cols.WIN + ","
                + Cols.WIN_SPEED + ")";

        public static final class Cols{
            public static final String CITY_NAME = "city_name";
            public static final String DATE = "date";
            public static final String DAY = "day";
            public static final String WEA = "wea";
            public static final String TEM = "tem";
            public static final String WIN = "win";
            public static final String WIN_SPEED = "win_speed";
        }
    }

    public static final class LifeTipsTable{
        public static final String NAME = "life_tips";
        public static final String CREATE = "create table " + NAME + "("
                + " id integer primary key autoincrement, "
                + Cols.CITY_NAME + ","
                + Cols.DATE + ","
                + Cols.TITLE + ","
                + Cols.LEVEL + ","
                + Cols.DESC + ")";

        public static final class Cols{
            public static final String CITY_NAME = "city_name";
            public static final String DATE = "date";
            public static final String TITLE = "title";
            public static final String LEVEL = "level";
            public static final String DESC = "desc";
        }
    }

}
