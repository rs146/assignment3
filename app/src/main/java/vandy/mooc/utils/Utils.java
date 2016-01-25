package vandy.mooc.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.jsonweather.WeatherJSONParser;
import vandy.mooc.jsonweather.JsonWeather;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class AcronymDownloadUtils
 *
 * @brief Handles the actual downloading of Acronym information from
 *        the Acronym web service.
 */
public class Utils {
    /**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = Utils.class.getCanonicalName();

    private final static String MY_PREFERENCES_NAME = "vandy.mooc.cache";

    /** 
     * URL to the Acronym web service.
     */
    private final static String WEATHER_DATA_SEARCH_URL =
        "http://api.openweathermap.org/data/2.5/weather?q=";

    /**
     * Obtain the Weather info given the location
     *
     * @param location location for the search
     * @return list of weather data
     */
    public static WeatherData getResult(final String location, Context context) {
        // Create a List that will return the AcronymData obtained
        // from the Weather Service web service.
        WeatherData weatherData = null;
            
        // JsonWeather object
        JsonWeather jsonWeather = null;

        long cachedTimeStamp = getCacheWeatherDataTimeStamp(location, context);
        if (currentTimeWithinRefreshRate(cachedTimeStamp, System.currentTimeMillis())) {
            return readCachedWeatherData(context);
        } else {
            try {
                // Append the location to create the full URL.
                final URL url =
                        new URL(WEATHER_DATA_SEARCH_URL
                                + location);

                // Opens a connection to the Acronym Service.
                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();

                // Sends the GET request and reads the Json results.
                try (InputStream in =
                             new BufferedInputStream(urlConnection.getInputStream())) {
                    // Create the parser.
                    final WeatherJSONParser parser =
                            new WeatherJSONParser();

                    // Parse the Json results and create JsonWeather data
                    // objects.
                    jsonWeather = parser.parseJsonStream(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonWeather.getmCod() == 404) {

                return null;
            } else {
                // cache the weather data (json weather data)
                cacheWeatherData(jsonWeather, location, context);

                // Convert the JsonWeather data object to the WeatherData object
                // so that it can be passed along address spaces
                weatherData = new WeatherData(jsonWeather.getmName(),
                        jsonWeather.getmSpeed(), jsonWeather.getmDeg(),
                        jsonWeather.getmTemp(), jsonWeather.getmHumidity(),
                        jsonWeather.getmSunrise(), jsonWeather.getmSunset());
                return weatherData;
            }
        }
    }

    /**
     * Cache the weather data using Shared Preferences.
     *
     * @param jsonWeather json weather object
     * @param location location
     * @param context app context
     */
    private static void cacheWeatherData(JsonWeather jsonWeather, String location, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(JsonWeather.name_JSON, jsonWeather.getmName());
        editor.putLong(JsonWeather.speed_JSON, Double.doubleToRawLongBits(jsonWeather.getmSpeed()));
        editor.putLong(JsonWeather.deg_JSON, Double.doubleToRawLongBits(jsonWeather.getmDeg()));
        editor.putLong(JsonWeather.temp_JSON, Double.doubleToRawLongBits(jsonWeather.getmTemp()));
        editor.putLong(JsonWeather.humidity_JSON, jsonWeather.getmHumidity());
        editor.putLong(JsonWeather.sunrise_JSON, jsonWeather.getmSunrise());
        editor.putLong(JsonWeather.sunset_JSON, jsonWeather.getmSunset());
        editor.putString("location", location);

        editor.putLong("timestamp", getCurrentTimeInMilliSeconds());
        Log.d("Utils", "" + getCurrentTimeInMilliSeconds());

        editor.commit();
    }

    /**
     * Read the Weather data from the Shared Preferences cache.
     *
     * @param context app context
     * @return WeatherData object that can be passed in between address spaces
     */
    private static WeatherData readCachedWeatherData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString(JsonWeather.name_JSON, null);
        double speed = Double.longBitsToDouble(preferences.getLong(JsonWeather.speed_JSON, 0));
        double deg = Double.longBitsToDouble(preferences.getLong(JsonWeather.deg_JSON, 0));
        double temp = Double.longBitsToDouble(preferences.getLong(JsonWeather.temp_JSON, 0));
        long humidity = preferences.getLong(JsonWeather.humidity_JSON, 0);
        long sunrise = preferences.getLong(JsonWeather.sunrise_JSON, 0);
        long sunset = preferences.getLong(JsonWeather.sunset_JSON, 0);
        Log.d(TAG, "Read data from cache, e.g: sunset: " + sunset);

        return new WeatherData(name, speed, deg, temp, humidity, sunrise, sunset);
    }

    /**
     * Get the current time in milliseconds.
     *
     * @return current time
     */
    private static long getCurrentTimeInMilliSeconds() {
        return System.currentTimeMillis();
    }

    /**
     * Get the Cached weather data timestamp. The location must be equal to
     * the location stored in the shared preferences, otherwise 0 is returned.
     *
     * @param location location
     * @param context app context
     * @return long timestamp, or zero
     */
    private static long getCacheWeatherDataTimeStamp(String location, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE);
        long timestamp = preferences.getLong("timestamp", 0);
        String prefLocation = preferences.getString("location", null);
        if (prefLocation == null) {
            return 0;
        } else if (prefLocation.equals(location)) {
            return timestamp;
        }
        return 0;
    }

    /**
     * See if the cached time period has not elapsed.
     *
     * @param timestampMillis
     * @param currentTimeMillis
     * @return true if cached time period has not elapsed, false otherwise
     */
    private static boolean currentTimeWithinRefreshRate(long timestampMillis, long currentTimeMillis) {
        long refreshRate = 10000;

        long diff = currentTimeMillis - timestampMillis;
        if (diff >= refreshRate) {
            return false;
        }
        return true;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
           (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
