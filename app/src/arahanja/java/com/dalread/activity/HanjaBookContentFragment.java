package com.dalread.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseFragment;
import com.dalread.component.FuriganaView;
import com.dalread.databinding.ItemHanjaBookContentDetailGeneralBinding;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.HanjaItem;
import com.dalread.model.RubyForBook;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HanjaBookContentFragment extends BaseFragment {
    private static final String DATA_KEY = "com.dalread.activity.HanjaBookDetailFragment.DATA_KEY";
    private static final String POSITION_KEY = "com.dalread.activity.HanjaBookDetailFragment.POSITION_KEY";

    private HanjaBookContentActivity activity;
    private ItemHanjaBookContentDetailGeneralBinding binding;
    private HanjaItem hanjaItem;
    private static boolean isBlurMeaning = false;
    private int heightRoot = 0;
    private int heightReading = 0;


    public static HanjaBookContentFragment getInstance(HanjaItem item, int pagePosition, boolean isBlurTemp) {
        HanjaBookContentFragment fragment = new HanjaBookContentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_KEY, item);
        bundle.putSerializable(POSITION_KEY, pagePosition);
        fragment.setArguments(bundle);
        isBlurMeaning = isBlurTemp;
        return fragment;
    }

    @Override
    protected View getContentView() {
        binding = ItemHanjaBookContentDetailGeneralBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HanjaBookContentActivity) requireActivity();
        Bundle bundle = getArguments();

        if (bundle != null) {
            hanjaItem = (HanjaItem) bundle.getSerializable(DATA_KEY);
            int pagePosition = bundle.getInt(POSITION_KEY);
            updateLayout(updateHanjaBookChapterModel());
            updatePageIndex(pagePosition);
        }

        initLayout();
        hideMenusOnReleaseMode();
    }


    private void hideMenusOnReleaseMode() {
        if (UserUtil.isEditContentUser(activity)) {
            binding.ivEditView.setVisibility(View.VISIBLE);
        } else {
            binding.ivEditView.setVisibility(View.INVISIBLE);
        }
    }

    private void initLayout() {
        DLog.d(getLogTag(), "initLayout");
        binding.llRoot.post(() -> heightRoot = binding.llRoot.getHeight());
        binding.vAdjustHeightReading.setOnTouchListener(separatorBarListener);
        heightReading = activity.getHeightReading();
        setHeightReading(true);
    }

    public void updateItem(HanjaItem hanjaItem) {
        this.hanjaItem = hanjaItem;
        updateLayout(updateHanjaBookChapterModel());
    }

    private void updateLayout(RubyForBook rubyForBook) {
        if (hanjaItem == null)
            return;

        requireActivity().runOnUiThread(() -> {

            binding.tvBookHanjaFurigana.setFuriganViewForBookVoca(rubyForBook.getVoca(), getIsShowFurigana());
            binding.tvBookHanjaFurigana.setOnTextSelectedListener(getOnRubyTextSelectedListener());

            binding.tvBookUnknownWordInfoFurigana.setFuriganViewForDifficultWords(rubyForBook.getDifficultWords());
            binding.tvBookUnknownWordInfoFurigana.setOnTextSelectedListener(getOnRubyTextSelectedListener());

            binding.tvBookMeaning.setFuriganViewForMeaning(rubyForBook.getMeaning());
            binding.tvBookMeaning.setOnTextSelectedListener(getOnRubyTextSelectedListener());


            binding.tvBookMeaningDetailed.setFuriganViewForMeaning(rubyForBook.getMeaningDetailed());
            binding.tvBookMeaningDetailed.setOnTextSelectedListener(getOnRubyTextSelectedListener());
            if (isBlurMeaning) {
                ToastUtil.getInstance(getActivity()).show(R.string.msg_warning_blur_text_buy_inapp_book);
                int blurColor = Color.argb(180, 0, 0, 0);
                binding.tvBookMeaning.setBackgroundColor(blurColor);
                binding.tvBookMeaningDetailed.setBackgroundColor(blurColor);
            }

        });
    }

    private FuriganaView.OnTextSelectedListener getOnRubyTextSelectedListener() {
        return activity.getRubyWordClickListener();
    }

    private View.OnTouchListener separatorBarListener = new View.OnTouchListener() {
        /**
         * Max allowed distance to move during a "click", in DP.
         */
        private static final int MAX_CLICK_DISTANCE = 15;
        private float pressedX;
        private float pressedY;
        /**
         * Max allowed duration for a "click", in milliseconds.
         */
        private static final int MAX_CLICK_DURATION = 200;
        private long pressStartTime;
        private boolean stayedWithinClickDistance;

        private float distance(float x1, float y1, float x2, float y2) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
            return pxToDp(distanceInPx);
        }

        private float pxToDp(float px) {
            return px / getResources().getDisplayMetrics().density;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int height = heightRoot / 4;
//            View vAbove = view.getId() == R.id.v_adjust_height_reading ? vReadingTop : rvContent;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressStartTime = System.currentTimeMillis();
                    pressedX = event.getX();
                    pressedY = event.getY();
                    stayedWithinClickDistance = true;
                    DLog.d(getLogTag(), "pressedY=" + pressedY + " - height=" + binding.vReadingTop.getHeight());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (stayedWithinClickDistance && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                        stayedWithinClickDistance = false;
                    }
//                    if (vAbove != null && vAbove.getVisibility() != View.GONE) {
                    final float currentY = event.getY() - pressedY;
                    DLog.d(getLogTag(), "pressedY=" + pressedY + "currentY=" + currentY);
                    heightReading = (int) (binding.vReadingTop.getHeight() + currentY);
                    if (heightReading <= height) {
                        heightReading = height;
                    } else if (heightReading >= (heightRoot - height)) {
                        heightReading = heightRoot - height;
                    }
                    setHeightReading(false);
                    DLog.d(getLogTag(), "height move=" + binding.vReadingTop.getHeight() + " - currentHeight=" + heightReading);
//                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    long pressDuration = System.currentTimeMillis() - pressStartTime;
//                    if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
////                        onClickSeparatorBar(view);
//                    }
                    activity.changeHeightReading(heightReading);
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    private void setHeightReading(boolean isInitLayout) {
        if (heightReading > 0) {
            if (isInitLayout) {
                binding.llRoot.setVisibility(View.INVISIBLE);
            }
            binding.vReadingTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.vReadingTop.setLayoutParams(new LinearLayout.LayoutParams(binding.vReadingTop.getWidth(), heightReading));
                    if (isInitLayout) {
                        binding.llRoot.post(() -> binding.llRoot.setVisibility(View.VISIBLE));
                    }
                    binding.vReadingTop.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
                }
            });
        }
    }

    private int getIsShowFurigana() {
        return sharedPreferences.getShowHuriganaForHanja();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.HANJA_BOOK_DETAIL) {
            switch (successEvent.getEventType()) {
                case CHANGE_READING_HEIGHT:
                    int height = (int) successEvent.getModel();
                    if (height > 0 && height != heightReading) {
                        heightReading = height;
                        setHeightReading(false);
                    }
                    break;
                case PAGE_INDEX:
                    int pageIndex = (int) successEvent.getModel();
                    updatePageIndex(pageIndex);
                    break;
            }
        }
    }

    private RubyForBook updateHanjaBookChapterModel() {
        List<String> listRubyTextAndUnknownWord = Voca.getRubyTextAndUnknownWord(hanjaItem.getHI_VOCA(), hanjaItem.getHI_PRONOUNCE1_FIRST());
//        String rubyText = listRubyTextAndUnknownWord.get(0);
//        unknownWordText = listRubyTextAndUnknownWord.get(1);
        List<String> listRubyTextDifficultWords = Voca.getRubyTextAndUnknownWord(listRubyTextAndUnknownWord.get(1),"");
        List<String> listRubyTextForMeaning = Voca.getRubyTextAndUnknownWord(hanjaItem.getHI_MEANING(requireActivity()),"");
        List<String> listRubyTextForMeaningDetailed = Voca.getRubyTextAndUnknownWord(hanjaItem.getHI_MEANING_DETAILED(requireActivity()),"");

        RubyForBook rubyForBook = new RubyForBook(listRubyTextAndUnknownWord.get(0), listRubyTextDifficultWords.get(0), listRubyTextForMeaning.get(0), listRubyTextForMeaningDetailed.get(0));
        return rubyForBook;
    }

    private void updatePageIndex(int pageIndex) {
        requireActivity().runOnUiThread(() -> {
            binding.tvBookName.setText((pageIndex + 1) + "/" + activity.getPageListSize());
        });
    }

    @OnClick({R.id.ivPrev, R.id.ivNext, R.id.ivCopy, R.id.ivEditView, R.id.ivWordList, R.id.ivWebSearch})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPrev:
                activity.movePreviousChapter();
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.MOVE_PREVIOUS_CHAPTER, null));
                break;
            case R.id.ivNext:
                activity.moveNextChapter();
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.MOVE_NEXT_CHAPTER, null));
                break;
            case R.id.ivCopy:
                Voca.openCopyDialog(activity, (DIC_HANJA_BOOK) hanjaItem);
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.COPY, (DIC_HANJA_BOOK)hanjaItem));
                break;
            case R.id.ivEditView:
                activity.openEditHanjaBookContentScreen((DIC_HANJA_BOOK)hanjaItem);
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.OPEN_EDIT_HANJA_BOOK_SCREEN, (DIC_HANJA_BOOK)hanjaItem));
                break;
            case R.id.ivWordList:
                activity.openHanjaWordListInfoView(hanjaItem);
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.WORD_LIST, hanjaItem));
                break;
            case R.id.ivWebSearch:
                Utils.openWebSearchForHanja(requireContext(), hanjaItem.getHI_VOCA() + " 유래");
                break;
            default:
                break;
        }
    }
}
