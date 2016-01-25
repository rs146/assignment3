package vandy.mooc.operations;

import java.lang.ref.WeakReference;

import vandy.mooc.R;
import vandy.mooc.activities.WeatherActivity;
import vandy.mooc.aidl.WeatherCall;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.services.WeatherServiceAsync;
import vandy.mooc.services.WeatherServiceSync;
import vandy.mooc.utils.GenericServiceConnection;
import vandy.mooc.utils.Utils;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class implements all the acronym-related operations defined in
 * the WeatherOps interface.
 */
public class WeatherOpsImpl implements WeatherOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<WeatherActivity> mActivity;

    /**
     * Weather location entered by the user.
     */
    protected WeakReference<EditText> mEditText;

    protected WeakReference<TextView> mResultTextView;

    /**
     * Weather Data result if any.
     */
    protected WeatherData mResult;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceSync Service using bindService().
     */
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceAsync Service using bindService().
     */
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

    /**
     * Constructor initializes the fields.
     */
    public WeatherOpsImpl(WeatherActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Finish the initialization steps.
        initializeViewFields();
        initializeNonViewFields();
    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        // Get references to the UI components.
        mActivity.get().setContentView(R.layout.main_activity);

        // Store the EditText that holds the location entered by the user
        mEditText = new WeakReference<>
            ((EditText) mActivity.get().findViewById(R.id.editText1));

        mResultTextView = new WeakReference<>(
                (TextView) mActivity.get().findViewById(R.id.weatherDataResultTextView));
    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {
        mServiceConnectionSync = 
            new GenericServiceConnection<WeatherCall>(WeatherCall.class);

        mServiceConnectionAsync =
            new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);

        // Display results if any (due to runtime configuration change).
        if (mResult != null)
            displayResult(mResult);
    }

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Checks the orientation of the screen.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
            Log.d(TAG,
                    "Now running in landscape mode");
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            Log.d(TAG,
                  "Now running in portrait mode");
    }

    /**
     * Initiate the service binding protocol.
     */
    @Override
    public void bindService() {
        Log.d(TAG, "calling bindService()");

        // Launch and bind the services
        if (mServiceConnectionSync.getInterface() == null) 
            mActivity.get().bindService
                (WeatherServiceSync.makeIntent(mActivity.get()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null) 
            mActivity.get().bindService
                    (WeatherServiceAsync.makeIntent(mActivity.get()),
                            mServiceConnectionAsync,
                            Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    @Override
    public void unbindService() {
        Log.d(TAG, "calling unbindService()");

        // Unbind the Async Service if it is connected.
        if (mServiceConnectionAsync.getInterface() != null)
            mActivity.get().unbindService
                (mServiceConnectionAsync);

        // Unbind the Sync Service if it is connected.
        if (mServiceConnectionSync.getInterface() != null)
            mActivity.get().unbindService
                (mServiceConnectionSync);
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandWeatherAsync(View v) {
        WeatherRequest weatherRequest =
            mServiceConnectionAsync.getInterface();

        if (weatherRequest != null) {
            // Get the location entered by the user
            final String location =
                mEditText.get().getText().toString();

            resetDisplay();

            try {
                // Invoke a one-way AIDL call, which does not block
                // the client.  The results are returned via the
                // sendResults() method of the mWeatherResults
                // callback object, which runs in a Thread from the
                // Thread pool managed by the Binder framework.
                weatherRequest.getCurrentWeather(location,
                        mWeatherResults);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException:" + e.getMessage());
            }
        } else {
            Log.d(TAG, "weatherRequest was null.");
        }
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandWeatherSync(View v) {
        final WeatherCall weatherCall =
            mServiceConnectionSync.getInterface();

        if (weatherCall != null) {
            // Get the location entered by the user.
            final String location =
                mEditText.get().getText().toString();

            resetDisplay();

            // Use an anonymous AsyncTask to download the Weather data
            // in a separate thread and then display any results in
            // the UI thread.
            new AsyncTask<String, Void, WeatherData> () {
                /**
                 * Location we're trying to see weather data for.
                 */
                private String mLocation;

                // get the weather data for the location in an AsyncTask due to blocking Sync call.
                protected WeatherData doInBackground(String... acronyms) {
                    try {
                        mLocation = acronyms[0];
                        return weatherCall.getCurrentWeather(mLocation);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                /**
                 * Display the results in the UI Thread.
                 */
                protected void onPostExecute(WeatherData weatherData) {
                    if (weatherData != null)
                        displayResult(weatherData);
                    else 
                        Utils.showToast(mActivity.get(),
                                        "no weather data for "
                                        + mLocation
                                        + " found");
                }
                // Execute the AsyncTask to expand the acronym without
                // blocking the caller.
            }.execute(location);
        } else {
            Log.d(TAG, "mWeatherCall was null.");
        }
    }

    /**
     * The implementation of the WeatherResults AIDL Interface, which
     * will be passed to the Weather Data Web service
     */
    private WeatherResults.Stub mWeatherResults = new WeatherResults.Stub() {
        /**
         * This method is invoked by the WeatherServiceAsync to
         * return the results back to the WeatherActivity.
         */
        @Override
        public void sendResults(final WeatherData weatherData)
                throws RemoteException {
            // Since the Android Binder framework dispatches this
            // method in a background Thread we need to explicitly
            // post a runnable containing the results to the UI
            // Thread, where it's displayed.
            mActivity.get().runOnUiThread(new Runnable() {
                public void run() {
                    displayResult(weatherData);
                }
            });
        }

        /**
         * Send the error back to the UI.
         *
         * @param reason the reason for the error
         * @throws RemoteException if something bad happens
         */
        @Override
        public void sendError(final String reason) throws RemoteException {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(mActivity.get(), reason);
                }
            });
        }
    };

    /**
     * Display the result to the screen.
     * 
     * @param result weather data result
     */
    private void displayResult(WeatherData result) {
        if (result == null) {
            mResultTextView.get().setText("Error in retrieving the weather data.");
        } else {
            mResult = result;
            mResultTextView.get().setText(mResult.toString());
        }
    }

    /**
     * Reset the display prior to attempting to find weather data for a location.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(mActivity.get(),
                           mEditText.get().getWindowToken());
        mResult = null;
        mResultTextView.get().setText("");
    }
}
