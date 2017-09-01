package com.provider.global.provider.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by MonarchPedo on 8/31/2017.
 */
public class CoordinateBroadCastService extends Service {

    private static final String TAG = CoordinateBroadCastService.class.getSimpleName();

    private GoogleApiClient googleApiClient;

    private Location mLastLocation;

    private boolean requestLocationUpdatesFlag = false;

    private LocationRequest locationRequest;

    private final static int UPDATE_INTERVAL = 10000;

    private final static int FATEST_INTERVAL = 10000;

    private static int DISTANCE = 1000;

    private String latitude = "";
    private String longitude = "";

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public  int onStartCommand(Intent intent, int flags, int startId){
       //I could query the databases and other apis to extract data from 3rd party tools
       //It also help to remove the service from memory after completing the task.
        //It saves the memory and battery both at the same time.
        //It can also be called periodically using AlarmManager to attach the process in pendindintentlist.
        if(checkPlayServices()){

            buildGoogleApiClient();

            createLocationRequest();

            locateClient();
        }

        //to destroy itself
        stopSelf();

        return  START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return  null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(alarm.RTC_WAKEUP, System.currentTimeMillis()+ (1000*60), PendingIntent.getService(this,0,new Intent(this,CoordinateBroadCastService.class),0));
    }

    protected void locateClient() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
           Log.e(TAG, "gps permission is not enabled", e);
        }

        try{
           network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
           Log.e(TAG,"network issue, please connect to data",e);
        }

        if (!gps_enabled && !network_enabled) {
            mLastLocation = (Location) LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude =  String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());

       }
    }


    /*
    * Method to verify Google Play Services on the device
    * */
    private boolean checkPlayServices(){
        GoogleApiAvailability googleApiClient = GoogleApiAvailability.getInstance();
        int resultCode = googleApiClient.isGooglePlayServicesAvailable(this);
        if(resultCode  != ConnectionResult.SUCCESS){
            if(googleApiClient.isUserResolvableError(resultCode)){
                return  false;
            }
            return  false;
        }
     return  true;
    }

    /*
    * Creating GoogleApiClient instance
    * */
    protected synchronized void buildGoogleApiClient(){
         googleApiClient  = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(null).addOnConnectionFailedListener(null).build();
    }

    /*
    * Creating Location request object
    * */
    protected void createLocationRequest() {
        locationRequest =  new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISTANCE);
    }




}

