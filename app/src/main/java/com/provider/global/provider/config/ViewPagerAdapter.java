package com.provider.global.provider.config;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MonarchPedo on 8/28/2017.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mListOfFragment = new ArrayList<>();
    private final List<String> mFragmentListName = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager){
        super(manager);
    }

    @Override
    public int getCount(){
            return mListOfFragment.size();
    }

    @Override
    public Fragment getItem(int position){
        return mListOfFragment.get(position);
    }

    @Override
    public String getPageTitle(int position){
        return  mFragmentListName.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        mListOfFragment.add(fragment);
        mFragmentListName.add(title);
    }

}
