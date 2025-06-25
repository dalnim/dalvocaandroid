package com.dalread.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class RecyclerViewUtils {
    //ChatGptHelper에서 GPT응답을 단어단위로 업데이트 할때 깜빡거려서 방지하기 위해서 애니메이션 효과를 없앰.
    public static void disableChangeAnimations(RecyclerView recyclerView) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();

        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }
}
