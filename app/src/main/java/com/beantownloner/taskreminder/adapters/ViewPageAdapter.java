package com.beantownloner.taskreminder.adapters;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.beantownloner.taskreminder.R;
import com.beantownloner.taskreminder.fragments.TabFragment;
import com.beantownloner.taskreminder.models.ReminderConstants;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ViewPageAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    private final int[] ICONS = {
            R.drawable.selector_icon_active,
            R.drawable.selector_icon_inactive
    };

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void tabUnselected(View view) {
        view.setSelected(false);
    }

    @Override
    public void tabSelected(View view) {
        view.setSelected(true);
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {
        FrameLayout customLayout = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tab, parent, false);
        ((ImageView) customLayout.findViewById(R.id.image)).setImageResource(ICONS[position]);
        return customLayout;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
            default:
                bundle.putInt("TYPE", ReminderConstants.ACTIVE);
                break;
            case 1:
                bundle.putInt("TYPE", ReminderConstants.INACTIVE);
                break;
        }
        Fragment fragment = new TabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}