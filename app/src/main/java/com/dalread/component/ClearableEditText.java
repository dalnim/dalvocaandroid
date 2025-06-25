package com.dalread.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

/**
 * Created by JetVHS on 2/6/2017.
 */
public class ClearableEditText extends EditText implements View.OnTouchListener, TextWatcherAdapter.TextWatcherListener {

    private final String TAG = "ClearableEditText";

    public static enum Location {
        NOTE(0), LEFT(0), RIGHT(0), RIGHT_RELOAD(R.drawable.ic_reload), RIGHT_CLEAR(R.mipmap.ic_close);

        final int idx;

        private Location(int idx) {
            this.idx = idx;
        }
    }

    public interface Listener {
        void didClearText();
        void onTextChanged(String text);
        void reLoad();
    }

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * null disables the icon
     */
    public void setIconLocation(Location loc) {
        this.loc = loc;
        initIcon();
        setClearIconVisible(loc != null);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.l = l;
    }

    private Location loc = Location.RIGHT_RELOAD;
    private Drawable xD;
    private Listener listener;
    private boolean wasVisible = false;
    private OnTouchListener l;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getDisplayedDrawable() != null) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int left = (loc == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - xD.getIntrinsicWidth();
            int right = (loc == Location.LEFT) ? getPaddingLeft() + xD.getIntrinsicWidth() : getWidth();
            boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (listener != null) {
                        if (loc == Location.RIGHT_RELOAD) {
                            listener.reLoad();
                        } else {
                            listener.didClearText();
                        }
                    }
                }
                return true;
            }
        }
        if (l != null) {
            return l.onTouch(v, event);
        }
        return false;
    }

    @Override
    public void onTextChanged(EditText view, String text) {
        if (listener != null) {
            listener.onTextChanged(text);
        }
        if (isFocused()) {
            setClearIconVisible(!Utils.isEmpty(text));
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        initIcon();
    }

    private void init() {
        super.setOnTouchListener(this);
        addTextChangedListener(new TextWatcherAdapter(this, this));
        initIcon();
        setClearIconVisible(true);
    }

    private void initIcon() {
        DLog.d(TAG, "initIcon");
        xD = null;
        if (loc != null) {
//            xD = getCompoundDrawables()[loc.idx];
            xD = ContextCompat.getDrawable(getContext(), loc.idx);
        }
        if (xD == null) {
            xD = ContextCompat.getDrawable(getContext(), R.drawable.ic_reload);
        }
        int bound = (int) (xD.getIntrinsicWidth() * 0.5);
        xD.setBounds(0, 0, bound, bound);
        int min = bound;
        if (getSuggestedMinimumHeight() < min) {
            setMinimumHeight(min);
        }
    }

    private Drawable getDisplayedDrawable() {
//        return (loc != null) ? getCompoundDrawables()[loc.idx] : null;
        return (loc != null) ? ContextCompat.getDrawable(getContext(), loc.idx) : null;
    }

    public void setClearIconVisible(boolean visible) {
        Drawable[] cd = getCompoundDrawables();
//        if (visible != wasVisible) {
//            wasVisible = visible;
//        }
        DLog.d(this.getClass().getName(), "visible=" + visible);
        Drawable x = visible ? xD : null;
        super.setCompoundDrawables((loc == Location.LEFT) ? x : cd[0], cd[1], (loc == Location.LEFT) ? cd[2] : x,
                cd[3]);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (isFocused()) {
//                Rect outRect = new Rect();
//                getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    clearFocus();
//                    Utils.hideSoftKeyboard(getContext(), this);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }
}
