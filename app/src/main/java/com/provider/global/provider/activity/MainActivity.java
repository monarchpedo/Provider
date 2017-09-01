package com.provider.global.provider.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.provider.global.provider.R;
import com.provider.global.provider.config.ViewPagerAdapter;
import com.provider.global.provider.fragment.MerchantActivity;
import com.provider.global.provider.fragment.NotificationActivity;
import com.provider.global.provider.fragment.OrderActivity;
import com.provider.global.provider.fragment.PaymentActivity;
import com.provider.global.provider.merchantfragment.AddOfferActivity;
import com.provider.global.provider.merchantfragment.AddProductActivity;
import com.provider.global.provider.merchantfragment.MerchantOrderActivity;
import com.provider.global.provider.merchantfragment.ProductActivity;

/**
 * Created by MonarchPedo on 8/28/2017.
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private int userId  = 0;
    private int userType = 0;

    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    PaymentActivity paymentActivity;
    OrderActivity orderActivity;
    MerchantActivity merchantActivity;

    @Override
    protected void onCreate(Bundle saveInstancedState){
        super.onCreate(saveInstancedState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        userType = saveInstancedState.getInt("userType");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position){
                  viewPager.setCurrentItem(position, false);
            }

           @Override
           public void onPageScrollStateChanged(int state){

           }
        });

        setUpViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
       getMenuInflater().inflate(R.menu.main_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
     //Handle item selection
     switch(item.getItemId()){
         case R.id.action_settings:
             Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
             startActivity(intent);
             finish();
             return  true;
         case R.id.action_history:
             Intent historyIntent = new Intent(getApplicationContext(),HistoryActivity.class);
             startActivity(historyIntent);
             finish();
             return  true;
         default:
             return super.onOptionsItemSelected(item);
     }
    }

    private void setUpViewPager(ViewPager viewPager){
        FragmentManager manager = getFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(manager);
        if(userType == 0){
            MerchantActivity merchantActivity = new MerchantActivity();
            OrderActivity orderActivity = new OrderActivity();
            PaymentActivity paymentActivity = new PaymentActivity();
            NotificationActivity notificationActivity = new NotificationActivity();
            adapter.addFragment(merchantActivity,"MER");
            adapter.addFragment(orderActivity,"O");
            adapter.addFragment(paymentActivity,"Pay");
            adapter.addFragment(notificationActivity,"Noti");
            viewPager.setAdapter(adapter);
        }
        else{
            ProductActivity productActivity = new ProductActivity();
            AddProductActivity addProductActivity = new AddProductActivity();
            MerchantOrderActivity merchantOrderActivity = new MerchantOrderActivity();
            AddOfferActivity addOfferActivity = new AddOfferActivity();
            adapter.addFragment(productActivity,"P");
            adapter.addFragment(addProductActivity,"+P");
            adapter.addFragment(merchantOrderActivity,"OL");
            adapter.addFragment(addOfferActivity,"+O");
            viewPager.setAdapter(adapter);
        }
    }


}
