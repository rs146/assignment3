package vandy.mooc.services;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.aidl.WeatherCall;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @class WeatherServiceSync
 */
public class WeatherServiceSync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceSync when passed to bindService().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceSync.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherCallImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface WeatherCall.
     * <p/>
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    WeatherCall.Stub mWeatherCallImpl = new WeatherCall.Stub() {

        @Override
        public WeatherData getCurrentWeather(String location)
                throws RemoteException {

            // Call the web service
            WeatherData weatherResult = Utils.getResult(location, getApplicationContext());

            if (weatherResult != null) {
                Log.d(TAG, ""
                        + weatherResult.toString()
                        + " result for location: "
                        + location);

                // Return the weather data back
                return weatherResult;
            } else {
                return null;
            }
        }
    };
}
