package vandy.mooc.jsonweather;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data of
 * interest downloaded in Json from the Weather Service.  We don't
 * care about all the data, just the fields defined in this class.
 */
public class JsonWeather {
    /**
     * Various tags corresponding to data downloaded in Json from the
     * Weather Service.
     */
    final public static String name_JSON = "name";
    final public static String wind_JSON = "wind";
    final public static String main_JSON = "main";
    final public static String sys_JSON = "sys";
    final public static String speed_JSON = "speed";
    final public static String deg_JSON = "deg";
    final public static String temp_JSON = "temp";
    final public static String humidity_JSON = "humidity";
    final public static String sunrise_JSON = "sunrise";
    final public static String sunset_JSON = "sunset";
    final public static String cod_JSON = "cod";

    /**
     * Various fields corresponding to data downloaded in Json from
     * the Weather data service.
     */
    private String mName;
    private double mSpeed;
    private double mDeg;
    private double mTemp;
    private long mHumidity;
    private long mSunrise;
    private long mSunset;
    private int mCod;

    /**
     * Used for Response status code only. Used only when we get a 404 (city/resource not
     * found error).
     *
     * @param cod HTTP status response code
     */
    public JsonWeather(int cod) {
        mCod = cod;
    }

    public JsonWeather(String mName, double mSpeed, double mDeg, double mTemp, long mHumidity, long mSunrise, long mSunset) {
        this.mName = mName;
        this.mSpeed = mSpeed;
        this.mDeg = mDeg;
        this.mTemp = mTemp;
        this.mHumidity = mHumidity;
        this.mSunrise = mSunrise;
        this.mSunset = mSunset;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getmDeg() {
        return mDeg;
    }

    public void setmDeg(double mDeg) {
        this.mDeg = mDeg;
    }

    public double getmTemp() {
        return mTemp;
    }

    public void setmTemp(double mTemp) {
        this.mTemp = mTemp;
    }

    public long getmHumidity() {
        return mHumidity;
    }

    public void setmHumidity(long mHumidity) {
        this.mHumidity = mHumidity;
    }

    public long getmSunrise() {
        return mSunrise;
    }

    public void setmSunrise(long mSunrise) {
        this.mSunrise = mSunrise;
    }

    public long getmSunset() {
        return mSunset;
    }

    public void setmSunset(long mSunset) {
        this.mSunset = mSunset;
    }

    public int getmCod() {
        return mCod;
    }

    public void setmCod(int mCod) {
        this.mCod = mCod;
    }
}
