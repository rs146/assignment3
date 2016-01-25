package vandy.mooc.operations;

import android.content.res.Configuration;
import android.view.View;

/**
 * This class defines all the weather-data related operations.
 */
public interface WeatherOps {
    /**
     * Initiate the service binding protocol.
     */
    public void bindService();

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService();

    /*
     * Initiate the synchronous weather data lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandWeatherSync(View v);

    /*
     * Initiate the asynchronous weather data lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandWeatherAsync(View v);

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig);
}
