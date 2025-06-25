package com.dalread.adapter;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dalread.R;
import com.dalread.dialog.TrackSelectionViewFragment;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    private final SparseArray<TrackSelectionViewFragment> tabFragments;
    private final ArrayList<Integer> tabTrackTypes;
    private Context context;

    public FragmentAdapter(FragmentManager fragmentManager, Context context, SparseArray<TrackSelectionViewFragment> tabFragments, ArrayList<Integer> tabTrackTypes) {
        super(fragmentManager);
        this.context = context;
        this.tabFragments = tabFragments;
        this.tabTrackTypes = tabTrackTypes;
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.valueAt(position);
    }

    @Override
    public int getCount() {
        return tabFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Integer trackType = tabTrackTypes.get(position);
        switch (trackType) {
            case C.TRACK_TYPE_VIDEO:
                return context.getResources().getString(R.string.video);
            case C.TRACK_TYPE_AUDIO:
                return context.getResources().getString(R.string.audio);
            case C.TRACK_TYPE_TEXT:
                return context.getResources().getString(R.string.subtitle);
            default:
                throw new IllegalArgumentException();
        }
    }

}
