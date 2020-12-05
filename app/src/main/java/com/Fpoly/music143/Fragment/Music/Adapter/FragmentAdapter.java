package com.Fpoly.music143.Fragment.Music.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class FragmentAdapter  extends FragmentPagerAdapter {
    private ArrayList<Fragment> Fragment = new ArrayList<>();
    private ArrayList<String> NamePage = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addFragment(Fragment fm, String title){
        Fragment.add((fm));
        NamePage.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.get(position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return NamePage.get(position);
    }

    @Override
    public int getCount() {
        return Fragment.size();
    }
}
