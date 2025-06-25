package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseHanjaFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.FragmentMainMenuBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaItem;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.WordListType;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaQuiz;
import com.dalread.util.Constant;
import com.dalread.util.GuideUtil;
import com.dalread.util.PointUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.dalread.util.VocaQuiz;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends BaseHanjaFragment implements View.OnClickListener {
    private ArrayList<BaseEvent.EventType> eventTypes;
    private HanjaItem hanjaToday;
    private MainHomeActivity activity;
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_SYNC_KNOW = TYPE_INIT_DATA + 1;
    private AbstractPointUtil pointUtil;
    private FragmentMainMenuBinding binding;
    @Override
    protected View getContentView() {
        binding = FragmentMainMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // refreshRemainPoint(); // 아라한자에서는 포인트 관련 UI 업데이트 비활성화
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        pointUtil = new PointUtil(activity);
        initLayout();
        setOnClickListeners();
        // showGuideWatchAd(); // 아라한자에서는 광고 가이드 비활성화
        eventTypes = new ArrayList<>();
        return view;
    }

    private void initLayout() {
        binding.glOption.removeView(binding.vQuizView);
        binding.glOption.removeView(binding.vPracticeQuiz);
//        binding.glOption.removeView(binding.vLevelKoreaType1);
//        binding.glOption.removeView(binding.vLevelKoreaType2);
//        binding.glOption.removeView(binding.vLevelKoreaType3);
        binding.glOption.removeView(binding.vAppendHanjaToHangulView);
        binding.glOption.removeView(binding.vUnicodeHanja);
        binding.glOption.removeView(binding.vOCR);
        if (UserUtil.isDebugOrAdminUser(getContext())) {
            binding.glOption.addView(binding.vQuizView);
            binding.glOption.addView(binding.vPracticeQuiz);
//            binding.glOption.addView(binding.vLevelKoreaType1);
//            binding.glOption.addView(binding.vLevelKoreaType2);
//            binding.glOption.addView(binding.vLevelKoreaType3);
            binding.glOption.addView(binding.vAppendHanjaToHangulView);
            binding.glOption.addView(binding.vUnicodeHanja);
            binding.glOption.addView(binding.vOCR);
        }
//        binding.includeTodayExpression.llButtons.setVisibility(View.VISIBLE);
        binding.includeTodayExpression.tvTodayVoca.setDefaultTextSize(getResources().getDimension(R.dimen.today_voca_text_size));
        binding.includeTodayExpression.tvTodayMeaning.setDefaultTextSize(getResources().getDimension(R.dimen.today_voca_meaning_text_size));
        displayLastReadBookMenu();
        resizeMenuIconWidthBySystemFontSize();

        binding.includeTodayExpression.ivKnow.setVisibility(View.VISIBLE);
        binding.includeTodayExpression.llKnow.setVisibility(View.VISIBLE);
        
        // 아라한자에서는 광고 보기와 포인트 레이아웃을 숨김 처리
        binding.includeInappPoints.llRoot.setVisibility(View.GONE);
    }

    private void showGuidePointDeduction() {
        if (sharedPreferences.isFirstShowGuidePointDeduction()) {
            String title = getString(R.string.guide_hanja_point_deduction);
            activity.runOnUiThread(() -> {
                GuideUtil.showGuideView(activity, title, binding.includeInappPoints.tvRemainPoint, view -> showGuideChangeVocaKnow());
            });
        }
    }

    private void showGuideWatchAd() {
        if (sharedPreferences.isFirstShowGuideWatchAd()) {
            sharedPreferences.setFirstShowGuideWatchAd();
            String title = getString(R.string.guide_watch_ad_to_get_point);
            GuideUtil.showGuideView(activity, title, binding.includeInappPoints.btnWatchRewardedAd, view -> showGuidePointDeduction());
        }
    }

    private void showGuideChangeVocaKnow() {
        if (sharedPreferences.isFirstShowGuideChangeVocaKnow()) {
            sharedPreferences.setFirstShowGuideChangeVocaKnow();
//            binding.includeInappPoints.btnWatchRewardedAd.setVisibility(View.VISIBLE);
            String title = getString(R.string.guide_change_voca_know);
            GuideUtil.showGuideView(activity, title, binding.includeTodayExpression.ivKnow, view -> {});
        }
    }

    private void displayLastReadBookMenu() {
        binding.glOption.removeView(binding.vLastReadBook);
        if (sharedPreferences.getLastReadBookId() > 0) {
            binding.glOption.addView(binding.vLastReadBook, 0);
        }
    }

    private void resizeMenuIconWidthBySystemFontSize() {
        int menuWidth = Utils.getDisplayDimensions(requireContext()).x / binding.glOption.getColumnCount();
        for (int i=0; i< binding.glOption.getChildCount(); i++) {
            View childView = binding.glOption.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
            layoutParams.width = menuWidth;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getTodayData();
        //TODO : dalnim signInFirebaseAccount is in VocaActivity so can't use it here
//        handler.postDelayed(this::signInFirebaseAccount, Constant.ON_RESUME_DELAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if ((type == BaseEvent.EventType.LOGOUT) || (type == BaseEvent.EventType.LOGIN)) {
                initLayout();
                /*Voca.deleteAllRealmData();
                Voca.deleteVocaVersion();*/
            } else if (type == BaseEvent.EventType.VOCA_KNOW_CHANGED) {
                IVocaBasicItem iVocaBasicItem= (IVocaBasicItem) successEvent.getModel();
                if ((iVocaBasicItem != null) && (Voca.isSameVoca(iVocaBasicItem, (IVocaBasicItem)hanjaToday))){
                    hanjaToday = Voca.searchHanjaWordByID(Long.valueOf(iVocaBasicItem.getVIId()));
                    displayTodayData();
                }
            } else if (!eventTypes.contains(BaseEvent.EventType.MENU_LANGUAGE_CHANGED) // not had MENU_LANGUAGE_CHANGED yet
                    && !eventTypes.contains(type)) { // not had this eventType yet
                if (type == BaseEvent.EventType.MENU_LANGUAGE_CHANGED) { // if have MENU_LANGUAGE_CHANGED, activity will be re-created, no need to track for other events
                    eventTypes.clear();
                }
                eventTypes.add(type);
            }
        }
    }
    private void handleEvent(BaseEvent.EventType eventType) {
        if (eventType != null) {
            switch (eventType) {
                case LOGIN:
                    initLayout();
                    break;
                case STUDY_LANGUAGE_CHANGED:
                case DATA_CHANGED:
                    /*Voca.deleteAllRealmData();*/
                    break;
                default:
                    break;
            }
        }
    }

    private void setOnClickListeners() {
        binding.vRadicals.setOnClickListener(this);
        binding.vStrokes.setOnClickListener(this);
        binding.vHangulGroup.setOnClickListener(this);
        binding.vVocaLevel.setOnClickListener(this);
        binding.vLevelKoreaType1.setOnClickListener(this);
        binding.vLevelKoreaType2.setOnClickListener(this);
        binding.vLevelKoreaType3.setOnClickListener(this);
        binding.vConvertToHanjaView.setOnClickListener(this);
        binding.vAppendHanjaToHangulView.setOnClickListener(this);
        binding.vBookmarkList.setOnClickListener(this);
        binding.includeTodayExpression.vRefresh.setOnClickListener(this);
        binding.vWorkbooks.setOnClickListener(this);
        binding.vHanjaIdioms.setOnClickListener(this);
        binding.includeTodayExpression.vToday.setOnClickListener(this);
        binding.vQuizView.setOnClickListener(this);
        binding.vPracticeQuiz.setOnClickListener(this);
        binding.vUnicodeHanja.setOnClickListener(this);
        binding.vOCR.setOnClickListener(this);
        binding.vLastReadBook.setOnClickListener(this);
        // binding.includeInappPoints.tvRemainPoint.setOnClickListener(this); // 아라한자에서는 포인트 관련 클릭 비활성화
        // binding.includeInappPoints.btnWatchRewardedAd.setOnClickListener(this); // 아라한자에서는 광고 관련 클릭 비활성화
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vRadicals:
                openRadicalsScreen();
                break;
            case R.id.vStrokes:
                openStrokesScreen(HanjaGroupType.STROKES);
                break;
            case R.id.vHangulGroup:
                openStrokesScreen(HanjaGroupType.HANGUL);
                break;
            case R.id.vVocaLevel:
                openStrokesScreen(HanjaGroupType.VOCA_LEVEL);
                break;
            case R.id.vLevelKoreaType1:
                openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_1);
                break;
            case R.id.vLevelKoreaType2:
                openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_2);
                break;
            case R.id.vLevelKoreaType3:
                openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_3);
                break;
            case R.id.vHanjaIdioms:
                openHanjaIdiomScreen();
                break;
            case R.id.vWorkbooks:
                openWorkbooksScreen();
                break;
            case R.id.vConvertToHanjaView:
                openConvertToHanjaScreen();
                break;
            case R.id.vOCR:
                openOCRScreen();
                break;
            case R.id.vAppendHanjaToHangulView:
                openAppendHanjaToHangulScreen();
                break;
            case R.id.vBookmarkList:
                openBookmarListInfoView();
                break;
            case R.id.vUnicodeHanja:
                openUnicoceHanjaListInfoView();
                break;
            case R.id.vQuizView:
                openChoiceQuizScreen();
                break;
            case R.id.vPracticeQuiz:
                openPracticeQuizScreen();
                break;
            case R.id.vRefresh:
                getTodayData();
                break;
            case R.id.vLastReadBook:
                openLastReadBook();
                break;
            case R.id.vToday:
                if ((hanjaToday != null) && (hanjaToday instanceof DIC_HANJA)) {
                    openHanjaInfoView((DIC_HANJA) hanjaToday);
                }
                break;
            case R.id.tvRemainPoint:
                activity.startActivity(new Intent(activity, InAppPointListActivity.class));
                break;
            case R.id.btnWatchRewardedAd:
                watchRewardedAd();
                break;
        }
    }

    private void showRewardButton() {
        binding.includeInappPoints.btnWatchRewardedAd.setVisibility(View.VISIBLE);
//        if (sharedPreferences.isFirstShowGuidePointDeduction() || pointUtil.needToShowRewardButton()) {
//            sharedPreferences.setFirstShowGuidePointDeduction();
//            binding.includeInappPoints.btnWatchRewardedAd.setVisibility(View.VISIBLE);
//        } else {
//            binding.includeInappPoints.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
//        }
    }
    private void watchRewardedAd() {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, R.string.dialog_title_need_point_to_open_hanja_detail_view, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                pointUtil.showRewardedAd(createRewardPointListener());
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
//                binding.includeInappPoints.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
                refreshRemainPoint();
            }

            @Override
            public void onContinue() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail() {

            }
        };
    }
    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        if (sharedPreferences.isPointAdded()) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.text_scale_anim);
            binding.includeInappPoints.tvRemainPoint.startAnimation(animation);
            sharedPreferences.setPointAdded(false);
        }
        binding.includeInappPoints.tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
        showRewardButton();
    }
    private void openRadicalsScreen() {
        HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, HanjaGroupType.RADICAL);
        openNewScreen(
                RadicalsActivity.createIntent(getContext(), hanjaGroupTypeModel)
        );
    }

    private void openStrokesScreen(HanjaGroupType hanjaGroupType) {
        HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, hanjaGroupType);
        openNewScreen(
                StrokesActivity.createIntent(activity, hanjaGroupTypeModel)
        );
    }

    private void openGroupViewLevelKoreanTypeScreen(HanjaGroupType hanjaGroupType) {
        int bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType1;
        if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType2;
        } else if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType3;
        }
        HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, hanjaGroupType);
        openNewScreen(
                LevelKoreaTypeActivity.createIntent(activity, hanjaGroupTypeModel, bookId, "")
        );
    }

    private void openWorkbooksScreen() {
        Intent intent = new Intent(activity, WorkbooksActivity.class);
        startActivity(intent);
    }

    private void openHanjaIdiomScreen() {
        int bookIdOfHanjaIdiom = Constant.HANJA.WORKBOOK_ID.bookIdIdiom;
        openNewScreen(
                HanjaWordListInfoActivity.createIntentByBookId(activity, bookIdOfHanjaIdiom, Voca.getVocabooksHanjaNameByBookId(bookIdOfHanjaIdiom, getContext()))
        );
    }

    private void openOCRScreen() {
        openNewScreen(
                OcrBackup.createIntent(activity)
        );
    }

    private void openConvertToHanjaScreen() {
        openNewScreen(
                ConvertToHanjaActivity.createIntent(activity, StringUtils.getTextFromClipboard(activity))
        );
    }

    private void openAppendHanjaToHangulScreen() {
        openNewScreen(
                AppendHanjaToHangulActivity.createIntent(activity, StringUtils.getTextFromClipboard(activity))
        );
    }

    protected void openBookmarListInfoView() {
        openNewScreen(
                HanjaWordListInfoActivity.createIntentByWordListTypeOnly(activity, WordListType.BOOKMARK, false)
        );

    }

    protected void openUnicoceHanjaListInfoView() {
        openNewScreen(
                HanjaWordListInfoActivity.createIntentByWordListTypeOnly(activity, WordListType.UNICODE, false)
        );
    }

    private void openLastReadBook() {
        long bookId = sharedPreferences.getLastReadBookId();
        if (bookId > 0) {
            VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics = Voca.getVocaBooksHanjaClassics(BaseVoca.getRealm(), bookId);
            if (vocabooksHanjaClassics != null) {
                openNewScreen(
                        HanjaBookContentActivity.createIntent(getContext(), vocabooksHanjaClassics)
                );
            }
        }
    }

    private void openChoiceQuizScreen() {
        int quizCount = 5;
        List<DIC_HANJA> hanjaTodayCandidateList = Voca.getHanjaTodayCandidateList(quizCount);
        List<IVocaFullPlayTTSItem> ttsItemList = Voca.convertDicHanjaListToIVocaFullPlayTTSItemList(hanjaTodayCandidateList, quizCount);
        List<VocaStudyChatExam> vocaStudyChatExamList = BaseVocaQuiz.generateVocaStudyChatExam(activity, ttsItemList);
        if (vocaStudyChatExamList.size() > 0) {
            Intent intent = new Intent(activity, ChoiceQuizActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_STUDY_LANG, EnumLanguage.HANJA.getIdApi());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_STUDY_CHAT_EXAM, (Serializable) vocaStudyChatExamList);
            startActivity(intent);
        } else {

        }
    }

    private void openPracticeQuizScreen() {
        startActivity(PracticeConversationActivity.createIntentByQuizList(activity, VocaQuiz.generateVocaStudyChatExamFromTodayCandidateList(activity)));
    }

    private void getTodayData() {
        hanjaToday = Voca.getHanjaToday(hanjaToday);
        displayTodayData();
    }

    private void displayTodayData() {
        if ((hanjaToday == null) || (hanjaToday.getHI_VOCA_KNOW() == null))
            return;

        binding.includeTodayExpression.ivKnow.setOnClickListener(new DoubleClick(activity.vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, hanjaToday));
        VocaKnow.updateIconVocaKnowGrayCircle(binding.includeTodayExpression.ivKnow, hanjaToday.getHI_VOCA_KNOW().intValue());
        VocaKnow.updateVocaColor(activity, binding.includeTodayExpression.tvTodayVoca, hanjaToday.getHI_VOCA_KNOW().intValue());
        binding.includeTodayExpression.tvTodayVoca.setText(hanjaToday.getHI_VOCA());
        binding.includeTodayExpression.tvTodayMeaning.setText(hanjaToday.getHI_MEANING1() + " " + hanjaToday.getHI_PRONOUNCE1_FIRST());
    }
}
