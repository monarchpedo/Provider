package com.provider.global.provider.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.provider.global.provider.LoginActivity;
import com.provider.global.provider.R;
import com.provider.global.provider.util.Constant;
import com.provider.global.provider.util.SessionController;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.AsynchronousByteChannel;

import cz.msebera.android.httpclient.Header;

/**
 * Created by MonarchPedo on 8/24/2017.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText email;
    private EditText mobileNumber;
    private EditText password;
    private EditText name;
    private Button registerBtn;
    private Button loginBtn;
    private ProgressBar pBar;
    private SessionController sessionController;

    @Override
    protected void onCreate(Bundle saveInstancestate){
        super.onCreate(saveInstancestate);
        setContentView(R.layout.activity_register);

        /*initilization of all component needed to run the login activity*/
        init();

        /*session controller to fetch the loggedin information*/
        sessionController = new SessionController(getApplicationContext());
        if (sessionController.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        /*declaration of definition of event of component in login activity*/
        onClickEvent();

    }

  private void init(){
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        name = (EditText) findViewById(R.id.name);
        registerBtn = (Button) findViewById(R.id.btnRegister);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);
  }

  private void onClickEvent(){
       loginBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               pBar.setVisibility(View.VISIBLE);
               String emailId = email.getText().toString();
               String mobile = mobileNumber.getText().toString();
               String passwd = password.getText().toString();
               String username = name.getText().toString();
               if (StringUtils.isEmpty(emailId) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(passwd) || StringUtils.isEmpty(username)) {
                   pBar.setVisibility(view.GONE);
                   Toast.makeText(getApplicationContext(), "please enter the required data to register", Toast.LENGTH_LONG).show();
               }
               boolean valid = isValidEmailAddress(emailId);
               if (valid == true) {
                   pBar.setVisibility(view.GONE);
                   Toast.makeText(getApplicationContext(), "please enter the valid email address", Toast.LENGTH_LONG).show();
               }
               valid = isValidNumber(mobile);
               if (valid == true) {
                   pBar.setVisibility(view.GONE);
                   Toast.makeText(getApplicationContext(), "please enter the valid mobile number", Toast.LENGTH_LONG).show();
               }
               newRegistration(emailId, mobile, passwd, username);
           }
       });

      registerBtn.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {
              Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
              startActivity(intent);
              finish();
          }
      });
  }


  private void newRegistration(String emailId,String mobileNumber,String password,String username) {
      AsyncHttpClient client = new AsyncHttpClient();
      client.addHeader("authentication", "provider");
      RequestParams params = new RequestParams();
      params.put("email",emailId);
      params.put("mobileNumber",mobileNumber);
      params.put("password",password);
      params.put("username",username);
      String url = Constant.USER_PATH;
      client.post(url,params,new JsonHttpResponseHandler(){

         @Override
         public void onSuccess(int statusCode,Header[] headers,JSONObject response){
             super.onSuccess(statusCode,headers,response);
             try {
                 if (response.getString("error").equalsIgnoreCase("true")) {
                     pBar.setVisibility(View.GONE);
                     sessionController.setLogin(false);
                     String errorMessage = response.getString("message");
                     Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                 } else {
                     JSONObject userObject = response.getJSONObject("userDetail");

                     sessionController.setLogin(true);
                     sessionController.setUserId(userObject.getInt("userId"));
                     sessionController.setUserType(userObject.getInt("userType"));
                     sessionController.setEmail(userObject.getString("email"));
                     sessionController.setMobileNo(userObject.getString("mobileNumber"));
                     pBar.setVisibility(View.GONE);
                     Intent intent = null;

                     if(sessionController.getUserType()==0) {
                         intent = new Intent(getApplicationContext(), MainActivity.class);
                         intent.putExtra("username", userObject.getString("username"));
                     }
                     else if(sessionController.getUserType()==1){
                         intent = new Intent(getApplicationContext(),AddressActivity.class);
                         intent.putExtra("username", userObject.getString("username"));
                     }
                     startActivity(intent);
                     finish();
                 }
             }catch (JSONException e){
                 Log.e("Provider","unexpected JSON Exception",e);
             }
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
