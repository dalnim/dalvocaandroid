package com.dalread.dialog;

import android.content.Context;

import androidx.viewbinding.ViewBinding;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public abstract class BaseBottomSheetDialog<T extends ViewBinding> extends BottomSheetDialog {
    protected final Context context;
    protected final T binding;
    protected SharedPreferencesDB sharedPreferences;

    public BaseBottomSheetDialog(Context context, T binding) {
        super(context, R.style.BottomSheetDialogTheme);
        this.context = context;
        this.binding = binding;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
    }


    @Override
    public void onStart() {
        super.onStart();
        //아래는 주석처리해도 똑같은거 같은데?
//        View bottomSheet = getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
//        if (bottomSheet != null) {
//            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
//            int peekHeight = (int) (UnitUtil.getScreenHeight(context) * 0.7);
//            behavior.setPeekHeight(peekHeight);
//            // animate the dialog sliding up (but doesn't work)
//            View view = getWindow().getDecorView();
//            view.setTranslationY(view.getHeight());
//            view.animate().translationY(0).setDuration(1000).start();
//        }
    }
}