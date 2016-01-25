package vandy.mooc.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a JsonWeather object that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @param inputStream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseJsonStream(InputStream inputStream)
        throws IOException {

        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
             new JsonReader(new InputStreamReader(inputStream,
                                                  "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseWeatherDataService(reader);
        }
    }

    /**
     * Parse a Json stream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseWeatherDataService(JsonReader reader)
        throws IOException {

        String name = null;
        Wind wind = null;
        MainPart mainPart = null;
        Sys sys = null;
        int cod = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String jsonPropertyName = reader.nextName();

            if (jsonPropertyName.equals(JsonWeather.cod_JSON)) {
                cod = reader.nextInt();
                if (cod == 404) {
                    return new JsonWeather(404);
                }
            } else if (jsonPropertyName.equals(JsonWeather.name_JSON)) {
                name = reader.nextString();
            } else if (jsonPropertyName.equals(JsonWeather.wind_JSON)) {
                wind = parseWindMessage(reader);
            } else if (jsonPropertyName.equals(JsonWeather.main_JSON)) {
                mainPart = parseMainPartMessage(reader);
            } else if (jsonPropertyName.equals(JsonWeather.sys_JSON)) {
                sys = parseSysMessage(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new JsonWeather(name, wind.mSpeed, wind.mDeg, mainPart.mTemp, mainPart.mHumidity,
                sys.mSunrise, sys.mSunset);
    }

    public Wind parseWindMessage(JsonReader reader) throws IOException {
        double speed = 0.0;
        double deg = 0.0;

        reader.beginObject();
        while (reader.hasNext()) {
            String jsonPropertyName = reader.nextName();
            if (jsonPropertyName.equals(JsonWeather.speed_JSON)) {
                speed = reader.nextDouble();
            } else if (jsonPropertyName.equals(JsonWeather.deg_JSON)) {
                deg = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Wind(speed, deg);
    }

    public MainPart parseMainPartMessage(JsonReader reader) throws IOException {
        double temp = 0.0;
        long humidity = 0l;

        reader.beginObject();
        while (reader.hasNext()) {
            String jsonPropertyName = reader.nextName();
            if (jsonPropertyName.equals(JsonWeather.temp_JSON)) {
                temp = reader.nextDouble();
            } else if (jsonPropertyName.equals(JsonWeather.humidity_JSON)) {
                humidity = reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new MainPart(temp, humidity);
    }

    public Sys parseSysMessage(JsonReader reader) throws IOException {
        long sunrise = 0l;
        long sunset = 0l;

        reader.beginObject();
        while (reader.hasNext()) {
            String jsonPropertyName = reader.nextName();
            if (jsonPropertyName.equals(JsonWeather.sunrise_JSON)) {
                sunrise = reader.nextLong();
            } else if (jsonPropertyName.equals(JsonWeather.sunset_JSON)) {
                sunset = reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Sys(sunrise, sunset);
    }

    private class Wind {
        double mSpeed;
        double mDeg;

        Wind(double speed, double deg) {
            mSpeed = speed;
            mDeg = deg;
        }
    }

    private class MainPart {
        double mTemp;
        long mHumidity;

        MainPart(double temp, long humidity) {
            mTemp = temp;
            mHumidity = humidity;
        }
    }

    private class Sys {
        long mSunrise;
        long mSunset;

        Sys(long sunrise, long sunset) {
            mSunrise = sunrise;
            mSunset = sunset;
        }
    }
}
