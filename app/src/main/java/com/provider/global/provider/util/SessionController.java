package com.provider.global.provider.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MonarchPedo on 8/24/2017.
 */
public class SessionController {

  public static final String TAG = SessionController.class.getSimpleName();

  SharedPreferences sharedPreferences;

  SharedPreferences.Editor editor;
  Context context;


  int PRIVATE_MODE = 0;

  private  static final String PREF_NAME = "Provider";

  private static final String LOGGED_INFO = "isLoggedIn";

  private static final String USERID = "userId";

  private static final String USERTYPE = "userType";

  private static final String EMAIL = "email";

  private static final String MOBILENO = "mobileNumber";


  private static final String MERCHANTID = "merchantId";

  public  SessionController(Context context){
      this.context = context;
      sharedPreferences = this.context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
      editor = sharedPreferences.edit();
  }

  public void setLogin(final boolean isLoggedIn){
      this.editor.putBoolean(LOGGED_INFO,isLoggedIn);
      this.editor.commit();
  }

  public Boolean isLoggedIn(){
      return  this.sharedPreferences.getBoolean(LOGGED_INFO,false);
  }

  public void setUserId(int userId){
       this.editor.putInt(USERID,userId);
       this.editor.commit();
  }

  public int getUserId(){
      return  this.sharedPreferences.getInt(USERID,0);
  }

  public void setUserType(int userType){
      this.editor.putInt(USERTYPE,userType);
      this.editor.commit();
  }

  public int getUserType(){
      return this.sharedPreferences.getInt(USERTYPE,0);
  }

 public void setEmail(String email){
   this.editor.putString(EMAIL,email);
   this.editor.commit();
 }

 public String getEmail(){
     return  this.sharedPreferences.getString(EMAIL,null);
 }

 public void setMobileNo(String mobileno){
     this.editor.putString(MOBILENO,mobileno);
     this.editor.commit();
 }

 public String getMobileNo(){
    return  this.sharedPreferences.getString(MOBILENO,null);
 }

 public void setMerchantId(int merchantId){
     this.editor.putInt(MERCHANTID,merchantId);
     this.editor.commit();
 }

 public int getMerchantId(){
     return  this.sharedPreferences.getInt(MERCHANTID,0);
 }

}
