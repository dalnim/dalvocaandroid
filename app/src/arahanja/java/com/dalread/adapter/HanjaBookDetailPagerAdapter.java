package com.dalread.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.dalread.activity.HanjaBookContentFragment;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.util.Constant;

import java.util.List;

public class HanjaBookDetailPagerAdapter extends FragmentStateAdapter {

    private List<DIC_HANJA_BOOK> bookPageList;
    private FragmentActivity activity;
    private Long bookUsed;

    public HanjaBookDetailPagerAdapter(FragmentActivity activity, List<DIC_HANJA_BOOK> bookPageList, Long bookUsed) {
        super(activity);
        this.activity = activity;
        this.bookPageList = bookPageList;
        this.bookUsed = bookUsed;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (bookPageList != null && !bookPageList.isEmpty()) {
            boolean isBlurMeaning = isBlurMeaning(position);
            return HanjaBookContentFragment.getInstance(bookPageList.get(position), position, isBlurMeaning);
        }
        return null;
    }

    private boolean isBlurMeaning(int position) {
        boolean isBlur = false;
        if (bookUsed != Constant.VOCABOOKS.USED.USE_FOR_FREE) {
            if (position >= (bookPageList.size() / 4)) {
                isBlur = true;
            }
        }
        return isBlur;
    }

    @Override
    public int getItemCount() {
        return bookPageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        String tag = "f" + holder.getItemId();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);

        if (fragment instanceof HanjaBookContentFragment) {
            ((HanjaBookContentFragment) fragment).updateItem(bookPageList.get(position));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }
}
