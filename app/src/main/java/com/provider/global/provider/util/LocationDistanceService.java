package com.provider.global.provider.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

/**
 * Created by MonarchPedo on 8/28/2017.
 */
public class LocationDistanceService extends IntentService {

  private LocationManager locationManager;
  private String provider;
  private ResultReceiver mReceiver;

   public LocationDistanceService() {

       super("LocationDistanceService");
   }

    public void onHandleIntent(Intent intent) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkLocationPermission() == true) {
            location = locationManager.getLastKnownLocation(provider);
            deliverResultToReciever(Constant.SUCCESS_RESULT,Constant.LOCATION_DATA,location);
        } else {
            deliverResultToReciever(Constant.FAILURE_RESULT,Constant.LOCATION_DATA,location);
        }

    }
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    protected void deliverResultToReciever(int resultCode, String message, Location location){
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.RESULT_ADDRESS, location);
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode,bundle);
    }
}

