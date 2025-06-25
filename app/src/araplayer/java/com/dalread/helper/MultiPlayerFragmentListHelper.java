package com.dalread.helper;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.dalread.R;
import com.dalread.activity.MultiplePlayerActivity;
import com.dalread.util.ToastUtil;

import java.util.Collections;

public class MultiPlayerFragmentListHelper {
    private MultiplePlayerActivity activity;
    public MultiPlayerFragmentListHelper(MultiplePlayerActivity context) {
        this.activity = context;
    }
    public void showIndexInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.multi_player_swap_screen_positions);

        // 기본 EditText 객체 생성
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // 첫 번째 인덱스 입력용 EditText
        final EditText fromIndexEditText = new EditText(activity);
        fromIndexEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        fromIndexEditText.setHint("From Index");

        // 두 번째 인덱스 입력용 EditText
        final EditText toIndexEditText = new EditText(activity);
        toIndexEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        toIndexEditText.setHint("To Index");

        layout.addView(fromIndexEditText);
        layout.addView(toIndexEditText);

        builder.setView(layout);

        // OK 버튼 클릭 리스너 설정
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            try {
                int fromIndex = Integer.parseInt(fromIndexEditText.getText().toString()) - 1;
                int toIndex = Integer.parseInt(toIndexEditText.getText().toString()) - 1;
                swapIndex(fromIndex, toIndex);
            } catch (NumberFormatException e) {
                // 숫자 입력 오류 처리
                ToastUtil.getInstance(activity).show("Invalid input. Please enter valid numbers.");
            }
        });

        // 취소 버튼 설정
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void swapIndex(int fromIndex, int toIndex) {
        swapGridLayout(fromIndex, toIndex);
        swapFragments(fromIndex, toIndex);
    }
    private void swapGridLayout(int fromIndex, int toIndex) {
        int childCount = activity.binding.gridLayout.getChildCount();
        if (fromIndex < 0 || fromIndex >= childCount || toIndex < 0 || toIndex >= childCount || fromIndex == toIndex) {
            return;
        }

        View fromView = activity.binding.gridLayout.getChildAt(fromIndex);
        View toView = activity.binding.gridLayout.getChildAt(toIndex);

        if (toIndex > fromIndex) {
            activity.binding.gridLayout.removeViewAt(toIndex);
            activity.binding.gridLayout.removeViewAt(fromIndex);
        } else {
            activity.binding.gridLayout.removeViewAt(fromIndex);
            activity.binding.gridLayout.removeViewAt(toIndex);
        }

        if (toIndex < fromIndex) {
            activity.binding.gridLayout.addView(fromView, toIndex);
            activity.binding.gridLayout.addView(toView, fromIndex);
        } else {
            activity.binding.gridLayout.addView(toView, fromIndex);
            activity.binding.gridLayout.addView(fromView, toIndex);
        }
    }

    private void swapFragments(int fromIndex, int toIndex) {
        if (fromIndex < activity.fragmentList.size() && toIndex < activity.fragmentList.size()) {
            int fromScreenId = activity.fragmentList.get(fromIndex).getScreenId();
            int toScreenId = activity.fragmentList.get(toIndex).getScreenId();
            Collections.swap(activity.fragmentList, fromIndex, toIndex);
            activity.fragmentList.get(fromIndex).setScreenId(fromScreenId);
            activity.fragmentList.get(toIndex).setScreenId(toScreenId);
        }
    }
}
