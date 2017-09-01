package com.provider.global.provider.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.provider.global.provider.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MonarchPedo on 8/27/2017.
 */
public class LocationFetcher extends IntentService {

private static final String TAG = LocationFetcher.class.getCanonicalName();
protected ResultReceiver mReceiver;

    public LocationFetcher(){

        super("LocationFetcher");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        int fetchType  = intent.getIntExtra(Constant.FETCH_TYPE_EXTRA,0);

        if(fetchType == Constant.USE_ADDRESS_NAME){
            String name = intent.getStringExtra(Constant.LOCATION_NAME_DATA_EXTRA);

            try {
                addresses = geocoder.getFromLocationName(name, 1);
            }catch (IOException e){
                errorMessage = "service not available";
                Log.e(TAG,errorMessage,e);
            }
        } else if(fetchType == Constant.USE_ADDRESS_LOCATION) {
            //get the location passed to this service to extra
            Location location = (Location) intent.getParcelableExtra(Constant.LOCATION_DATA_EXTRA);

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            } catch (IOException ie) {
                //Catch Network or other I/O problem.
                errorMessage = "service not available";
                Log.e(TAG, errorMessage, ie);
            } catch (IllegalArgumentException e) {
                //catch network or other I/O exception
                errorMessage = "Invalied latitude or longitude";
                Log.e(TAG, errorMessage + "." +
                        "Latitude =" + location.getLatitude() +
                        ", Longitude = " + location.getLatitude(), e);
            }
        } else {
            errorMessage = "Unknown type";
            Log.e(TAG,errorMessage);
        }
            //Handle case where no address found.
            if (addresses == null || addresses.size() == 0) {
                if (StringUtils.isEmpty(errorMessage)) {
                    errorMessage = "Not found the exact location";
                    Log.e(TAG, errorMessage);
                }
                deliverResultToReciever(Constant.FAILURE_RESULT, errorMessage,null);
            } else {
                for(Address address : addresses) {
                    String outputAddress = "";
                    for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        outputAddress += " --- " + address.getAddressLine(i);
                    }
                    Log.e(TAG, outputAddress);
                }

                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                //fetch all addressess near by sended location from calling service
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                Log.i(TAG, "address found");
                deliverResultToReciever(Constant.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments), address);

            }
        }


    protected   void deliverResultToReciever(int resultCode, String message,Address address){
            Bundle  bundle = new Bundle();
            bundle.putParcelable(Constant.RESULT_ADDRESS, address);
            bundle.putString(Constant.RESULT_DATA_KEY, message);
            mReceiver.send(resultCode,bundle);
    }

    }

