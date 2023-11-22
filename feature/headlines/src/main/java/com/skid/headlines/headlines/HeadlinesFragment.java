package com.skid.headlines.headlines;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;
import com.skid.headlines.databinding.FragmentHeadlinesBinding;
import com.skid.headlines.di.HeadlinesComponentViewModel;

public class HeadlinesFragment extends Fragment {

    private FragmentHeadlinesBinding binding;

    private HeadlinesPagerAdapter headlinesPagerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        new ViewModelProvider(this)
                .get(HeadlinesComponentViewModel.class)
                .getHeadlinesComponent()
                .inject(this);

        headlinesPagerAdapter = new HeadlinesPagerAdapter(
                this,
                getResources().getStringArray(com.skid.ui.R.array.tab_names)
        );
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHeadlinesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupPager();
        setupTabs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        headlinesPagerAdapter = null;
    }

    private void setupPager() {
        binding.headlinesPager.setAdapter(headlinesPagerAdapter);
    }

    private void setupTabs() {
        String[] tabNames = getResources().getStringArray(com.skid.ui.R.array.tab_names);
        TypedArray tabIcons = getResources().obtainTypedArray(com.skid.ui.R.array.tab_icons);

        new TabLayoutMediator(binding.headlinesTabLayout, binding.headlinesPager, (tab, position) -> {
            tab.setText(tabNames[position]);
            tab.setIcon(AppCompatResources.getDrawable(requireContext(), tabIcons.getResourceId(position, 0)));
        }).attach();

        tabIcons.recycle();
    }
}
