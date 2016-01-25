package vandy.mooc.services;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @class WeatherServiceAsync
 */
public class WeatherServiceAsync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceAsync when passed to bindService().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceAsync.class);
    }

    /**
     * Called when a client (e.g., WeatherActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of mWeatherRequest, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherRequest;
    }

    WeatherRequest.Stub mWeatherRequest = new WeatherRequest.Stub() {

        @Override
        public void getCurrentWeather(String location, WeatherResults callback)
                throws RemoteException {

            // Call the web service to get the weather results for the location
            WeatherData weatherResult = Utils.getResult(location, getApplicationContext());

            if (weatherResult != null) {
                Log.d(TAG, ""
                        + weatherResult.toString()
                        + " result for weather data: "
                        + location);
                callback.sendResults(weatherResult);
            } else {
                callback.sendError("Invalid location entered");
            }
        }
    };
}
