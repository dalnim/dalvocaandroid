package com.dalread.util;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.SeparatorDecoration;

//현재는 쓰지 않음. addItemDecoration에서 공통으로 쓰는 칼라와 높이가 있는데 이걸 통일해서 써볼려고 하는중. Butter Knife를 제거하기 위한 목적도 있음.
//LayoutManager는 이걸 안쓰는게 좋을거 같음.
public class RecyclerViewUtil {
    public static void addDefaultLayoutMangerItemDecoration(Context context, RecyclerView recyclerView) {
        addDefaultLayoutManager(context, recyclerView);
        addDefaultSeparatorDecoration(context, recyclerView);
    }

    private static void addDefaultLayoutManager(Context context, RecyclerView recyclerView) {
        addLinearLayoutManager(context, recyclerView);
    }
    public static void addLinearLayoutManager(Context context, RecyclerView recyclerView) {
        addLayoutManager(recyclerView, new LinearLayoutManager(context));
    }
//    public static void addGridLayoutManager(Context context, RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
//        addLayoutManager(recyclerView, layoutManager);
//    }

    public static void addLayoutManager(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }


    private static void addDefaultSeparatorDecoration(Context context, RecyclerView recyclerView) {
//        BaseBindUtils.getSeparatorDecoration(context)
        addSeparatorDecoration(context, recyclerView, BaseBindUtils.getDividerColor(context), BaseBindUtils.getDividerHeightRes());//  R.color.color_divider, R.dimen.divider_height);
    }

    public static void addSeparatorDecoration(Context context, RecyclerView recyclerView, int colorResId, int heightDimenResId) {
        recyclerView.addItemDecoration(new SeparatorDecoration(context, ContextCompat.getColor(context, colorResId), context.getResources().getDimension(heightDimenResId)));
    }
}