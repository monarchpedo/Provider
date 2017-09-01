package com.provider.global.provider.activity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.provider.global.provider.LoginActivity;
import com.provider.global.provider.R;
import com.provider.global.provider.merchantfragment.ProductActivity;
import com.provider.global.provider.util.Constant;
import com.provider.global.provider.util.LocationFetcher;
import com.provider.global.provider.util.SessionController;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by MonarchPedo on 8/28/2017.
 */
public class AddressActivity extends AppCompatActivity {

    private static final String TAG = AddressActivity.class.getSimpleName();
    private EditText shopName;
    private EditText city;
    private EditText country;
    private EditText locality;
    private EditText pincode;
    private EditText state;
    private Button button;
    private ProgressBar pBar;
    private String latitude;
    private String longitude;
    private AddressResultReceiver addressResultReceiver;
    private SessionController sessionController;

    public void onCreate(Bundle saveInstancedState){
       super.onCreate(saveInstancedState);
       setContentView(R.layout.activity_address);

       /*declaration of component*/
       init();

       sessionController = new SessionController(getApplicationContext());
       if(!sessionController.isLoggedIn()){
           Intent intent = new Intent(this, LoginActivity.class);
           startActivity(intent);
           finish();
       }

       /*click definition on button*/
       onClickEvent();
    }

    public void onClickEvent(){
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                  String shopname = shopName.getText().toString();
                  String locale = locality.getText().toString();
                  String cityName = city.getText().toString();
                  String pin = pincode.getText().toString();
                  String stateName = state.getText().toString();
                  String country = "india";
                  int userId = sessionController.getUserId();

                  if(checkInput(shopname,locale,cityName,pin,stateName)){
                      Toast.makeText(getApplicationContext(),"please enter all fields",Toast.LENGTH_LONG).show();
                  }
                  else{
                      pBar.setVisibility(View.VISIBLE);
                      StringBuilder builder = new StringBuilder();
                      builder.append(locale).append(city).append(pin).append(stateName).append(country);
                      String locationName = builder.toString();
                      getLocationCoordinates(locationName);
                      AsyncHttpClient client = new AsyncHttpClient();
                      client.addHeader("Authorization", "address");
                      final RequestParams params = new RequestParams();
                      params.put("userId",userId);
                      params.put("shopName",shopname);
                      params.put("locality", locale);
                      params.put("city", cityName);
                      params.put("pin", pin);
                      params.put("state", stateName);
                      params.put("country", country);
                      params.put("latitude",latitude);
                      params.put("longitude",longitude);
                      String url = Constant.MERCHANT_PATH + "/location";
                      client.post(url,params,new JsonHttpResponseHandler(){
                          @Override
                          public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                                super.onSuccess(statusCode,headers,response);
                             try {
                                 if(response.getString("error").equalsIgnoreCase("true")){
                                     pBar.setVisibility(View.GONE);
                                     Toast.makeText(getApplicationContext(),"retry once again",Toast.LENGTH_LONG).show();
                                 }else{
                                     int merchantId = response.getInt("merchantId");
                                     SessionController sessionController = new SessionController(getApplicationContext());
                                     sessionController.setMerchantId(merchantId);
                                     Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                     intent.putExtra("merchantId",merchantId);
                                     pBar.setVisibility(View.GONE);
                                     startActivity(intent);
                                     finish();
                                 }
                             }catch (JSONException e){
                                 pBar.setVisibility(View.GONE);
                                 Log.e("Provider","retry once again",e);
                             }

                          }

                         @Override
                         public void onFailure(int statusCode,Header[] headers,String responseError,Throwable throwable){
                              pBar.setVisibility(View.GONE);
                              Toast.makeText(getApplicationContext(),"retry once again",Toast.LENGTH_LONG).show();
                         }
                      });
                  }
            }
        });
    }

    public void init(){
       shopName = (EditText) findViewById(R.id.shopName);
       locality = (EditText) findViewById(R.id.locality);
       city = (EditText) findViewById(R.id.city);
       pincode = (EditText) findViewById(R.id.pincode);
       state = (EditText) findViewById(R.id.state);
       pBar = (ProgressBar) findViewById(R.id.progressBar);
       pBar.setVisibility(View.GONE);
       button = (Button) findViewById(R.id.btnAddress);
    }

   private class AddressResultReceiver extends ResultReceiver {
       AddressResultReceiver(android.os.Handler handler){
           super(handler);
       }

       @Override
       protected void onReceiveResult(int resultCode, Bundle resultData){

           super.onReceiveResult(resultCode, resultData);


           if(resultCode == Constant.SUCCESS_RESULT) {
               Address coordinates = (Address) resultData.getParcelable(Constant.RESULT_ADDRESS);

               Double lat = (Double) coordinates.getLatitude();
               Double lon = (Double) coordinates.getLongitude();

               latitude = String.valueOf(lat);
               longitude = String.valueOf(lon);
           }
       }
   }

    protected void getLocationCoordinates(String locationName){
        Intent intent = new Intent(this, LocationFetcher.class);
        intent.putExtra(Constant.FETCH_TYPE_EXTRA,Constant.USE_ADDRESS_NAME);
        intent.putExtra(Constant.LOCATION_NAME_DATA_EXTRA,locationName);
        startActivity(intent);
    }

   private boolean checkInput(String shop,String locality, String city,String pincode,String state){
       if(StringUtils.isEmpty(shop) ||StringUtils.isEmpty(locality) ||StringUtils.isEmpty(city) || StringUtils.isEmpty(pincode) || StringUtils.isEmpty(state))
       return  true;

       return  false;
   }
}
