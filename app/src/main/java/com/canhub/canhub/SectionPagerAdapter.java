package com.canhub.canhub;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    private final Context mcontext;

    public SectionPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mcontext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Formulariopt1();
            case 1:
                return new Formulariopt2();
            case 2:
                return new Formulariopt3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
