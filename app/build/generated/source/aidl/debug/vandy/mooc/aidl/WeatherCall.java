/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/rahulsingh/git-android/communication-assignments/assignment3/app/src/main/aidl/vandy/mooc/aidl/WeatherCall.aidl
 */
package vandy.mooc.aidl;
/**
 * Interface defining the method implemented within WeatherServiceSync
 * that provides synchronous access to the Weather Service web
 * service.
 */
public interface WeatherCall extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements vandy.mooc.aidl.WeatherCall
{
private static final java.lang.String DESCRIPTOR = "vandy.mooc.aidl.WeatherCall";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an vandy.mooc.aidl.WeatherCall interface,
 * generating a proxy if needed.
 */
public static vandy.mooc.aidl.WeatherCall asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof vandy.mooc.aidl.WeatherCall))) {
return ((vandy.mooc.aidl.WeatherCall)iin);
}
return new vandy.mooc.aidl.WeatherCall.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getCurrentWeather:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
vandy.mooc.aidl.WeatherData _result = this.getCurrentWeather(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements vandy.mooc.aidl.WeatherCall
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
    * A two-way (blocking) call that retrieves information about the
    * current weather from the Weather Service web service and returns
    * a list of WeatherData objects containing the results from the
    * Weather Service web service back to the WeatherActivity.
    */
@Override public vandy.mooc.aidl.WeatherData getCurrentWeather(java.lang.String Weather) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
vandy.mooc.aidl.WeatherData _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(Weather);
mRemote.transact(Stub.TRANSACTION_getCurrentWeather, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = vandy.mooc.aidl.WeatherData.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getCurrentWeather = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
    * A two-way (blocking) call that retrieves information about the
    * current weather from the Weather Service web service and returns
    * a list of WeatherData objects containing the results from the
    * Weather Service web service back to the WeatherActivity.
    */
public vandy.mooc.aidl.WeatherData getCurrentWeather(java.lang.String Weather) throws android.os.RemoteException;
}
