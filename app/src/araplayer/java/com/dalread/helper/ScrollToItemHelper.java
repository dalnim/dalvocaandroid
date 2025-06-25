package com.dalread.helper;

import android.os.Handler;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.model.PlayerFileModel;
import com.dalread.util.DLog;
import com.dalread.util.StringCompareUtil;
import com.dalread.util.Utils;

import java.util.List;

public class ScrollToItemHelper {
    // 스크롤할 아이템 수가 적을 경우 부드러운 스크롤을 사용할 임계값
    private static final int SMOOTH_SCROLL_THRESHOLD = 20;
    private static final long SCROLL_DELAY = 200L;
    final Handler mHandler = new Handler();

    static public void scrollToFile(String fileName, List<PlayerFileModel> list, RecyclerView recyclerView) {
        int index = getIndexFromList(fileName, list);
        if (index >= 0) {
            scrollToPosition(index, list.size(), recyclerView);
        }
    }
    private static int getIndexFromList(String fileName, List<PlayerFileModel> list) {
        if (Utils.isEmpty(fileName)) return -1;

        for (int i = 0; i < list.size(); i++) {
            PlayerFileModel item = list.get(i);
            if (StringCompareUtil.areStringsEqualByNfc(item.getName(), fileName)) {
//            if (item.getName().equals(fileName)) { //이걸로 하면 같은 문자인데도 바이트가 달라서 다르게 나올때가 있다.
                item.setCheck(true);
                return i;
            }
        }
        return -1;
    }

    static private void scrollToPosition(int index, int totalSize, RecyclerView recyclerView) {
        if (index < 0 || index > totalSize) return;
        (new Handler()).postDelayed(() -> {
            recyclerView.post(() -> {
                if (index <= SMOOTH_SCROLL_THRESHOLD)
                    recyclerView.smoothScrollToPosition(index);
                else
//                    fastScrollToPosition(index, recyclerView);
                    recyclerView.scrollToPosition(index); //이건 즉시 이동함.
//                    scrollPositionInSubtitleTableView(recyclerView, subtitleIndex);//rvContent.scrollToPosition(subtitleIndex);
            });
        }, SCROLL_DELAY);
    }
    // 스크롤 범위가 크면 빠르게 스크롤
    private static void fastScrollToPosition(int index, RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            FastScroller fastScroller = new FastScroller(recyclerView.getContext());
            fastScroller.setTargetPosition(index);
            layoutManager.startSmoothScroll(fastScroller);
        }
    }
//    static private void fastScrollToPosition(int subtitleIndex, RecyclerView recyclerView) {
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//        FastScroller fastScroller = new FastScroller(recyclerView.getContext());
//        fastScroller.setTargetPosition(subtitleIndex);
//        layoutManager.startSmoothScroll(fastScroller);
//    }

    //이건 굳이 안하고 recyclerView.scrollToPosition를 해도 되는데 혹시나 해서 나둔다.
    static private void scrollPositionInSubtitleTableView(RecyclerView list, int pos) {
        try {
            if (list.getLayoutManager() instanceof LinearLayoutManager) {
                // Centering item in its parent
                final LinearLayoutManager manager = (LinearLayoutManager) list.getLayoutManager();
                final boolean isHorizontal = manager.getOrientation() == LinearLayoutManager.HORIZONTAL;
                int offset = isHorizontal
                        ? (list.getWidth() - list.getPaddingLeft() - list.getPaddingRight()) / 2
                        : (list.getHeight() - list.getPaddingTop() - list.getPaddingBottom()) / 2;
                final RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(pos);
                if (holder != null) {
                    final View view = holder.itemView;
                    offset -= isHorizontal ? view.getWidth() / 2 : view.getHeight() / 2;
                }
                manager.scrollToPositionWithOffset(pos, offset);
            }
        } catch (Exception ex) {
            DLog.e("", "error=" + ex.getMessage());
        }
    }
}


