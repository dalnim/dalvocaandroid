package com.dalread.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.StyleRes;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.Utils;

public class BaseDialog extends Dialog {

    protected final String TAG = this.getClass().getName();
    protected Context mContext;
    protected Resources res;
    protected SharedPreferencesDB mSharedPref;
    protected EnumType enumType;
    protected BaseDialogListener baseDialogListener;
    private boolean isDialogSizeWider;
    private Context context;

    public BaseDialog(Context context) {
        super(context);

        this.context = context;
        init();
    }

    public BaseDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    private void init() {
        mContext = getContext();
        res = mContext.getResources();
        mSharedPref = SharedPreferencesDB.getInstance(mContext);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation_Window;
    }
    public void removeAnimation() {
        getWindow().getAttributes().windowAnimations = 0;
    }
    @Override
    public void show() {
        if (isDialogSizeWider()) {
            Utils.setDialogSizeWider(context, this);
        }
        super.show();
    }

    public boolean isDialogSizeWider() {
        return isDialogSizeWider;
    }

    public void setDialogSizeWider(boolean dialogSizeWider) {
        isDialogSizeWider = dialogSizeWider;
    }

    public void setBaseDialogListener(BaseDialogListener baseDialogListener) {
        this.baseDialogListener = baseDialogListener;
    }
}
