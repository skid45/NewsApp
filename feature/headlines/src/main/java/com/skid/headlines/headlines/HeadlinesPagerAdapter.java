package com.skid.headlines.headlines;

import static com.skid.utils.Constants.CATEGORY_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skid.headlines.newsbycategory.NewsByCategoryFragment;

public class HeadlinesPagerAdapter extends FragmentStateAdapter {

    private final String[] tabNames;

    public HeadlinesPagerAdapter(@NonNull Fragment fragment, @NonNull String[] tabNames) {
        super(fragment);
        this.tabNames = tabNames;
    }

    @Override
    public int getItemCount() {
        return tabNames.length;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, tabNames[position]);
        NewsByCategoryFragment fragment = new NewsByCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
