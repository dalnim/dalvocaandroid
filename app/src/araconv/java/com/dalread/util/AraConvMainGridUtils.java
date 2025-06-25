package com.dalread.util;

import android.content.Context;
import android.widget.GridLayout;

import com.dalread.R;

public class AraConvMainGridUtils {
    public static final int minColumnCount = 2;
    public static int getColumnCount(Context context) {
        int widthDp = UnitUtil.getScreenSizeDp(context, true);
        int columnCount = minColumnCount;

        int cardTotalWidthDp = getCardTotalWidthDp(context);
        int extraSpace = 16; // total horizontal spacing between cards
        int totalWidthDp = cardTotalWidthDp;
        int availableWidthDp = widthDp - extraSpace;
        for (int i = columnCount; i <= 7; i++) {
            if (totalWidthDp * i <= availableWidthDp) {
                columnCount = i;
            } else {
                break;
            }
        }
        return columnCount > minColumnCount ? columnCount : minColumnCount;
    }

    private static int getCardTotalWidthDp(Context context) {
        int width = UnitUtil.getDimenInDp(context, R.dimen.item_main_menu_width);
        int margin = UnitUtil.getDimenInDp(context, R.dimen.item_main_menu_margin);
        return width + (margin * 2);
    }

    public static void setColumnCount(Context context, GridLayout gridLayout) {
        int columnCount = getColumnCount(context);
        gridLayout.setColumnCount(columnCount);
    }
}
