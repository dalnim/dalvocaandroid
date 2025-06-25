package com.dalread.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.listener.OnHeaderListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Toolbar extends RelativeLayout implements View.OnClickListener {

    @BindView(R.id.header)
    View header;
    @BindView(R.id.llLeft)
    LinearLayout llLeft;
    @BindView(R.id.llRight)
    LinearLayout llRight;
    @BindView(R.id.vRight)
    LinearLayout vRight;
    @BindView(R.id.iconLeft)
    ImageView iconLeft;
    @BindView(R.id.iconLeft2)
    ImageView iconLeft2;
    @BindView(R.id.iconRight)
    ImageView iconRight;
    @BindView(R.id.iconRight2)
    ImageView iconRight2;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.v_search)
    EmptySubmitSearchView vSearch;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private PopupWindow popupWindow;
    private OnHeaderListener listener;
    private SearchView.OnCloseListener onCloseListener;
    private boolean isSearchStarted;
    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.layout_toolbar, this);
        ButterKnife.bind(this, view);
        isSearchStarted = false;
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.Header);

        String title = typeArray.getString(R.styleable.Header_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        boolean showSearch = typeArray.getBoolean(R.styleable.Header_searchShow, false);
        vSearch.setVisibility(showSearch ? View.VISIBLE : View.GONE);

        int leftIcon = typeArray.getResourceId(R.styleable.Header_leftIcon, 0);
        iconLeft.setImageResource(leftIcon);

        int rightIcon = typeArray.getResourceId(R.styleable.Header_rightIcon, 0);
        if (rightIcon == 0) {
            iconRight.setVisibility(View.GONE);
        } else {
            iconRight.setImageResource(rightIcon);
            iconRight.setVisibility(View.VISIBLE);
        }

        int rightIcon2 = typeArray.getResourceId(R.styleable.Header_rightIcon2, 0);
        if (rightIcon2 == 0) {
            iconRight2.setVisibility(View.GONE);
        } else {
            iconRight2.setImageResource(rightIcon2);
            iconRight2.setVisibility(View.VISIBLE);
        }

        int leftIcon2 = typeArray.getResourceId(R.styleable.Header_leftIcon2, 0);
        if (leftIcon2 == 0) {
            iconLeft2.setVisibility(View.GONE);
        } else {
            iconLeft2.setImageResource(leftIcon2);
            iconLeft2.setVisibility(View.VISIBLE);
        }

        String rightText = typeArray.getString(R.styleable.Header_rightText);
        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(View.GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(View.VISIBLE);
        }

        int rightColor = typeArray.getResourceId(R.styleable.Header_rightColor, 0);
        if (rightColor != 0) {
            tvRight.setTextColor(ContextCompat.getColor(context, rightColor));
        }

        boolean showRight = typeArray.getBoolean(R.styleable.Header_rightShow, true);
        vRight.setVisibility(showRight ? View.VISIBLE : View.GONE);

        typeArray.recycle();
        calculateHeaderWidth();
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            //xml에서 android:background="?attr/colorPrimaryDark"를 하면, 다크모드일때 색상이 colorPrimary로 나와서 여기서 코드로 다시 해준다.
            int color = AraThemeUtil.getThemeColor(context, R.attr.colorPrimaryDark);
            AraThemeUtil.setBackgroundColor(header, color);
        }
    }

    public void setListener(OnHeaderListener listener) {
        this.listener = listener;
    }

    public void setIconLeft(@DrawableRes int id) {
        setIconCommon(iconLeft, id);
//        icLeft.setImageResource(id);
//        icLeft.setTag(id);
//        icLeft.setVisibility(View.VISIBLE);
//        calculateHeaderWidth();
    }

    public void setIconLeft2(@DrawableRes int id) {
        setIconCommon(iconLeft2, id);
//        icLeft2.setImageResource(id);
//        icLeft2.setTag(id);
//        icLeft2.setVisibility(View.VISIBLE);
//        calculateHeaderWidth();
    }

    public void setIconRight(@DrawableRes int id) {
        setIconCommon(iconRight, id);
//        icRight.setImageResource(id);
//        icRight.setTag(id);
//        icRight.setVisibility(View.VISIBLE);
//        calculateHeaderWidth();
    }

    public void setIconRight2(@DrawableRes int id) {
        setIconCommon(iconRight2, id);
//        icRight2.setImageResource(id);
//        icRight2.setTag(id);
//        icRight2.setVisibility(View.VISIBLE);
//        calculateHeaderWidth();
    }

    private void setIconCommon(ImageView imageView, @DrawableRes int id) {
        imageView.setImageResource(id);
        imageView.setTag(id);
        imageView.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void setTextRight(@StringRes int id) {
        tvRight.setText(id);
        tvRight.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public LinearLayout getViewRight() {
        return vRight;
    }

    public ImageView getIconRight() {
        return iconRight;
    }

    public ImageView getIconRight2() {
        return iconRight2;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public ImageView getIconLeft() {
        return iconLeft;
    }
    public ImageView getIconLeft2() {
        return iconLeft2;
    }

    public void setTitle(@StringRes int id) {
        tvTitle.setText(id);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void showTitle() {
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        tvTitle.setVisibility(View.GONE);
    }

    public void showRight() {
        vRight.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void hideRight() {
        vRight.setVisibility(View.GONE);
        calculateHeaderWidth();
    }

    public void visibleRight() {
        vRight.setVisibility(View.VISIBLE);
    }

    public void invisibleRight() {
        vRight.setVisibility(View.INVISIBLE);
    }

    public void showIconRight() {
        iconRight.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void hideIconRight() {
        iconRight.setVisibility(View.GONE);
        iconRight2.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
        calculateHeaderWidth();
    }

    public void showTvRight() {
        tvRight.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void hideTvRight() {
        tvRight.setVisibility(View.GONE);
        calculateHeaderWidth();
    }


    public void showIconLeft() {
        iconLeft.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void hideIconLeft() {
        iconLeft.setVisibility(View.GONE);
        calculateHeaderWidth();
    }

    public void showIconLeft2() {
        iconLeft2.setVisibility(View.VISIBLE);
        calculateHeaderWidth();
    }

    public void hideIconLeft2() {
        iconLeft2.setVisibility(View.GONE);
        calculateHeaderWidth();
    }

    //이걸 호출할때 runOnUiThread을 사용할것. 아니면 쓰레드 관련 에러 날수가 있다.
    public void showSearchView() {
        //Handler가 여기 있으니 멀티 비디오의 비디오리스트로 갈때 리스트가 안보인다.
//        new Handler(Looper.getMainLooper()).post(() -> {
            vSearch.onActionViewCollapsed();
            vSearch.setVisibility(View.VISIBLE);
            calculateHeaderWidth();
//        });
    }


    //이걸 호출할때 runOnUiThread을 사용할것. 아니면 에러 발생 An error occurred while executing doInBackground()
    public void hideSearchView() {
        vSearch.onActionViewCollapsed();
        vSearch.setVisibility(View.GONE);
        calculateHeaderWidth();
    }

    public EmptySubmitSearchView getViewSearch() {
        return vSearch;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public void setSearchListener(@NonNull final SearchView.OnQueryTextListener queryListener, @NonNull final SearchView.OnCloseListener closeListener) {
        vSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                vSearch.clearFocus();
                if (Utils.isEmpty(s)) {
                    showTitle();
                    vSearch.onActionViewCollapsed();
                }
                return queryListener.onQueryTextSubmit(s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return queryListener.onQueryTextChange(s);
            }
        });
        vSearch.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSearchStarted = true;
                    hideTitle();
                    vSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                    if (popupWindow != null) {
                        popupWindow.showAsDropDown(vSearch);
                    }
                } else {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
            }
        });
        vSearch.setOnCloseListener(onCloseListener = () -> {
            isSearchStarted = false;
            vSearch.onActionViewCollapsed();
            showTitle();
            return closeListener.onClose();
        });
    }

    public void clearSearchListener() {
        vSearch.setOnQueryTextListener(null);
        vSearch.setOnQueryTextFocusChangeListener(null);
        vSearch.setOnCloseListener(null);
        vSearch.onActionViewCollapsed();
    }

    private void calculateHeaderWidth() {
        header.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int max = Math.max(llLeft.getWidth(), llRight.getWidth());
            ViewGroup.LayoutParams params = tvTitle.getLayoutParams();
            params.width = header.getWidth() - 2 * max;
            tvTitle.setLayoutParams(params);
        });
    }

    @OnClick({R.id.iconLeft, R.id.iconLeft2, R.id.vRight, R.id.iconRight, R.id.iconRight2, R.id.tv_right})
    public void onClick(View view) {
        if (listener == null) return;

        switch (view.getId()) {
            case R.id.iconLeft:
                listener.onHeaderLeftClick();
                break;
            case R.id.iconLeft2:
                listener.onHeaderLeft2Click();
                break;
            case R.id.vRight:
                listener.onHeaderRightClick();
                break;
            case R.id.iconRight:
                listener.onHeaderIconRightClick();
                break;
            case R.id.iconRight2:
            case R.id.tv_right:
                listener.onHeaderTextRightClick();
                break;
        }
    }

    public void closeSearchView() {
        onCloseListener.onClose();
    }

    public boolean isSearchStarted() {
        return isSearchStarted;
    }
}
