package com.provider.global.provider.fragment;

import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.provider.global.provider.R;

/**
 * Created by MonarchPedo on 8/28/2017.
 */
public class PaymentActivity extends Fragment {

    public  PaymentActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return  inflater.inflate(R.layout.activity_payment,container,false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}
