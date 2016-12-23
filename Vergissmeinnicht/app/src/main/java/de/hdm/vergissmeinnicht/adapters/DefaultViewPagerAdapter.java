package de.hdm.vergissmeinnicht.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import de.hdm.vergissmeinnicht.fragments.DefaultAppointmentsFragment;
import de.hdm.vergissmeinnicht.fragments.DefaultMedicineFragment;
import de.hdm.vergissmeinnicht.fragments.DefaultPlantsFragment;

/**
 * Created by Dennis Jonietz on 6/13/15.
 */
public class DefaultViewPagerAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_PAGES = 3;

    public DefaultViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return DefaultPlantsFragment.newInstance();
            case 1:
                return DefaultMedicineFragment.newInstance();
            case 2:
                return DefaultAppointmentsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }
}
