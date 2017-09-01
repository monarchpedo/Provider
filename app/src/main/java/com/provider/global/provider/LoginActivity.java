package com.provider.global.provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.provider.global.provider.activity.MainActivity;
import com.provider.global.provider.activity.RegisterActivity;
import com.provider.global.provider.util.Constant;
import com.provider.global.provider.util.SessionController;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button   loginButton;
    private Button   registerButton;
    private SessionController sessionController;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*initilization of all component needed to run the login activity*/
        init();

        /*session checking for resuming puropose*/
        sessionController  = new SessionController(getApplicationContext());
        if (sessionController.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        /*declaration of definition of event of component in login activity*/
        onClickEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   //definition of component
   private void  init(){
       emailEditText = (EditText) findViewById(R.id.email);
       passwordEditText = (EditText) findViewById(R.id.password);
       loginButton = (Button) findViewById(R.id.btnLogin);
       registerButton = (Button) findViewById(R.id.btnLinkToRegisterScreen);
       pBar = (ProgressBar) findViewById(R.id.progressBar);
   }

  //definition of event listener
  private void onClickEvent(){
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
               String userCred = emailEditText.getText().toString();
               String password = passwordEditText.getText().toString();

               if(!StringUtils.isEmpty(userCred) || !StringUtils.isEmpty(password) || isValidEmailAddress(userCred) || isValidNumber(userCred)){
                  checkLogin(userCred, password);
               }
                else{
                   Toast.makeText(getApplicationContext(),"please enter right user credentials",Toast.LENGTH_LONG).show();
               }
            }
        });

      registerButton.setOnClickListener(new View.OnClickListener(){
          public void onClick(View view){
              Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
              startActivity(intent);
              finish();


          }
      });
  }

 //login event by calling asynchttpclient to call post request for
 private void checkLogin(final String userCred,final String password){
    String appendPath = "/user/login";
     pBar.setVisibility(View.VISIBLE);
     AsyncHttpClient client = new AsyncHttpClient();
     RequestParams params = new RequestParams();
     if(isValidNumber(userCred)){
        params.put("mobileNumber",userCred);
     }
     else if(isValidNumber(userCred)){
         params.put("email",userCred);
     }

     params.put("password", password);
     client.addHeader("authentication", "login");
     String url = Constant.LOGIN_PATH+"/login";
     client.post(url,params,new JsonHttpResponseHandler(){
         @Override
         public void onSuccess(int statusCode,Header[] headers, JSONObject response){
             super.onSuccess(statusCode,headers,response);
             try {
                 if (response.getString("error").equalsIgnoreCase("true")) {
                     pBar.setVisibility(View.GONE);
                     String errorMessage = response.getString("message");
                     //Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                     sessionController.setLogin(false);
                 } else {
                     sessionController.setLogin(true);
                     JSONObject userObject = response.getJSONObject("userDetail");
                     sessionController.setUserId(userObject.getInt("userId"));
                     int userType =userObject.getInt("userType");
                     sessionController.setUserType(userType);
                     Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                     intent.putExtra("userType",userType);
                     pBar.setVisibility(View.GONE);
                     startActivity(intent);
                     finish();
                 }
             }catch (JSONException e){
                 pBar.setVisibility(View.GONE);
                 Log.e("Provider","unexpected JSON Exception",e);
             }
         }
         @Override
         public void onSuccess(int statusCode,Header[] headers, JSONArray response){

     }
         @Override
         public void onFailure(int statusCode,Header[] headers,String responseError,Throwable throwable){
             pBar.setVisibility(View.GONE);
             Toast.makeText(getApplicationContext(),"server issue",Toast.LENGTH_LONG).show();
         }

     });

 }

    private static boolean isValidEmailAddress(String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private static boolean isValidNumber(String mobileNumber){
        Phonenumber.PhoneNumber numberProto = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            numberProto = phoneUtil.parse(mobileNumber, "IND");
        } catch (NumberParseException e) {
            Log.e("provider", "NumberParseException was thrown: ", e);
            return false;
        }
        boolean isValid = phoneUtil.isValidNumber(numberProto);
        return isValid;
    }

}
