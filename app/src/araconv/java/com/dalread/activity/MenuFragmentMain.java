package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseConvFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.FragmentMainMenuBinding;
import com.dalread.helper.PromptUtil;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.model.WordListType;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AraConvMainGridUtils;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.ConversationBookUtil;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DialogUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

public class MenuFragmentMain extends BaseConvFragment implements View.OnClickListener {
//    private ArrayList<BaseEvent.EventType> eventTypes;
    private MainHomeActivity activity;
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_SYNC_KNOW = TYPE_INIT_DATA + 1;

    private FragmentMainMenuBinding binding;
//    private DIC_SENTENCE_MODEL dicSentenceModel;
    private IVocaFullPlayTTSItem todayExpressionItem;

    @Override
    protected View getContentView() {
        binding = FragmentMainMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        initMainMenuIcon();
        initLayout();
        hideMenusOnReleaseMode();
        setOnClickListeners();
//        eventTypes = new ArrayList<>();
        refreshTodayExpression();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        binding.fabPracticeConversation.setVisibility(sharedPreferences.getLastReadBookIdReal() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(activity)) {
            binding.vVocaListRecording.cvMainMenu.setVisibility(View.VISIBLE);
        } else {
            binding.vVocaListRecording.cvMainMenu.setVisibility(View.GONE);
        }
//        binding.includeTodayExpression.vChatGpt.setVisibility(View.VISIBLE);
    }
    private void refreshTodayExpression() {
        getTodayExpression();
        displayTodayExpression();
    }

    private void initMainMenuIcon() {
        binding.vBasicExpression.ivIcon.setImageResource(R.drawable.ic_araconv_home_basic);
        binding.vBookmark.ivIcon.setImageResource(R.drawable.ic_araconv_home_bookmark);
//        binding.vPreviousPractice.ivIcon.setImageResource(R.drawable.ic_araconv_home_previous);
        binding.vTravelHotel.ivIcon.setImageResource(R.drawable.ic_araconv_home_hotel);
        binding.vTravelRestaurant.ivIcon.setImageResource(R.drawable.ic_araconv_home_food);
        binding.vTravelAirport.ivIcon.setImageResource(R.drawable.ic_araconv_home_airport);
        binding.vTravelTransport.ivIcon.setImageResource(R.drawable.ic_araconv_home_transport);
        binding.vTravelShopping.ivIcon.setImageResource(R.drawable.ic_araconv_home_shopping);
        binding.vTravelTour.ivIcon.setImageResource(R.drawable.ic_araconv_home_tour);
        binding.vTravelTelephone.ivIcon.setImageResource(R.drawable.ic_araconv_home_telephone);
        binding.vTravelEmergency.ivIcon.setImageResource(R.drawable.ic_araconv_home_emergency);
        binding.vVocaListRecording.ivIcon.setImageResource(R.drawable.ic_araconv_home_recording);

//        if (application.isDarkMode(requireActivity())) {
//            ColorDrawable colorDrawable = BaseColorUtil.getColorDrawableLayerColorInDarkMode(requireActivity());
//            binding.vBasicExpression.ivIcon.setForeground(colorDrawable);
//            binding.vBookmark.ivIcon.setForeground(colorDrawable);
//            binding.vTravelHotel.ivIcon.setForeground(colorDrawable);
//            binding.vTravelRestaurant.ivIcon.setForeground(colorDrawable);
//            binding.vTravelAirport.ivIcon.setForeground(colorDrawable);
//            binding.vTravelTransport.ivIcon.setForeground(colorDrawable);
//            binding.vTravelShopping.ivIcon.setForeground(colorDrawable);
//            binding.vTravelTour.ivIcon.setForeground(colorDrawable);
//            binding.vTravelTelephone.ivIcon.setForeground(colorDrawable);
//            binding.vTravelEmergency.ivIcon.setForeground(colorDrawable);
//            binding.vVocaListRecording.ivIcon.setForeground(colorDrawable);
//            binding.fabPracticeConversation.setForeground(colorDrawable);
//        }
//        binding.vBasicExpression.ivIcon.setImageResource(R.drawable.ic_home_basic);
//        binding.vBookmark.ivIcon.setImageResource(R.drawable.ic_araconv_home_bookmark);
//        binding.vPreviousPractice.ivIcon.setImageResource(R.drawable.ic_home_basic);
//        binding.vTravelHotel.ivIcon.setImageResource(R.drawable.ic_home_hotel);
//        binding.vTravelRestaurant.ivIcon.setImageResource(R.drawable.ic_home_restaurant);
//        binding.vTravelAirport.ivIcon.setImageResource(R.drawable.ic_home_airport);
//        binding.vTravelTransport.ivIcon.setImageResource(R.drawable.ic_home_transport);
//        binding.vTravelShopping.ivIcon.setImageResource(R.drawable.ic_home_shopping);
//        binding.vTravelTour.ivIcon.setImageResource(R.drawable.ic_home_tour);
//        binding.vTravelTelephone.ivIcon.setImageResource(R.drawable.ic_home_telephone);
//        binding.vTravelEmergency.ivIcon.setImageResource(R.drawable.ic_home_emergency);
//        binding.vVocaListRecording.ivIcon.setImageResource(R.drawable.ic_araconv_home_recording);


        binding.vBasicExpression.tvTitle.setText(R.string.conv_home_menu_basic_expression);
        binding.vBookmark.tvTitle.setText(R.string.conv_home_menu_bookmark);
//        binding.vPreviousPractice.tvTitle.setText(R.string.conv_home_menu_previous_practice);
        binding.vTravelHotel.tvTitle.setText(R.string.conv_home_menu_travel_hotel);
        binding.vTravelRestaurant.tvTitle.setText(R.string.conv_home_menu_travel_restaurant);
        binding.vTravelAirport.tvTitle.setText(R.string.conv_home_menu_travel_airport);
        binding.vTravelTransport.tvTitle.setText(R.string.conv_home_menu_travel_transport);
        binding.vTravelShopping.tvTitle.setText(R.string.conv_home_menu_travel_shopping);
        binding.vTravelTour.tvTitle.setText(R.string.conv_home_menu_travel_tour);
        binding.vTravelTelephone.tvTitle.setText(R.string.conv_home_menu_travel_telephone);
        binding.vTravelEmergency.tvTitle.setText(R.string.conv_home_menu_travel_emergency);
        binding.vVocaListRecording.tvTitle.setText(R.string.conv_home_menu_record);
    }

    private void getTodayExpression() {
        todayExpressionItem = activity.subDatabase.getTodayExpression();
    }

    private void displayTodayExpression() {
        if ((todayExpressionItem == null) || (Utils.isEmpty(todayExpressionItem.getVIVoca())))
            return;

        binding.includeTodayExpression.ivKnow.setOnClickListener(new DoubleClick(activity.vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, todayExpressionItem));
        BaseVocaKnow.updateIconVocaKnowText(binding.includeTodayExpression.ivKnow, todayExpressionItem.getVIVocaKnow());
        binding.includeTodayExpression.tvTodayVoca.setText(todayExpressionItem.getVIVoca());
        showPronounce();
        displayTodayExpressionEnglish();
        displayMeaning();

    }

    private void displayMeaning() {
        binding.includeTodayExpression.tvTodayMeaning.setText(Voca.getMeaningOrEnglishMeaning(activity, todayExpressionItem));
    }

    private void displayTodayExpressionEnglish() {
        if (Utils.needToDisplayMeaningEnglish(activity)  && !Utils.isEmpty(todayExpressionItem.getVIMeaningEng())) {
            binding.includeTodayExpression.tvTodayMeaningEnglish.setVisibility(View.VISIBLE);
            binding.includeTodayExpression.tvTodayMeaningEnglish.setText(Voca.wrapMeaningEnglish(todayExpressionItem));
        } else {
            binding.includeTodayExpression.tvTodayMeaningEnglish.setVisibility(View.GONE);
        }
    }

    private void showPronounce() {
        Voca.displayShortOrLongPronounce(activity, todayExpressionItem, binding.includeTodayExpression.tvShortPronounce, binding.includeTodayExpression.tvLongPronounce);
    }

    private void initLayout() {
        if (Utils.isDebugOrAdminUser(getContext())) {

        }

        if (LanguageUtil.isStudyLangKorean(getContext())) {

        }
        AraConvMainGridUtils.setColumnCount(activity, binding.glOption);
        initLayoutTodayExpressionLineSpacing();
        initLayoutTodayExpression();
    }

    private void initLayoutTodayExpressionLineSpacing() {
        float lineSpacingMultiplier = 1.0f;
        if (LanguageUtil.isStudyLangKorean(getContext())) {
            lineSpacingMultiplier = 0.8f;
        }
        binding.includeTodayExpression.tvTodayVoca.setLineSpacing(0f, lineSpacingMultiplier);
        binding.includeTodayExpression.tvTodayMeaning.setLineSpacing(0f, lineSpacingMultiplier);

    }

    private void initLayoutTodayExpression() {
        binding.includeTodayExpression.ivSpeaker.setVisibility(View.VISIBLE);
        binding.includeTodayExpression.llButtons.setVisibility(View.VISIBLE);
//        binding.includeTodayExpression.llVocaKnow.setVisibility(View.GONE);
//        binding.includeTodayExpression.tvTodayVoca.setDefaultTextSize(getResources().getDimension(R.dimen.today_voca_text_size));
//        binding.includeTodayExpression.tvTodayMeaning.setDefaultTextSize(getResources().getDimension(R.dimen.today_voca_meaning_text_size));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
//            if(activity.playTTS.isPlaying()) {
//                onSpeakerClick();
//            }
//        } else {
//            Voca.updateSpeakerIconStop(binding.includeTodayExpression.ivSpeaker);
//        }
////        stopPlayTTS();
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            if(!activity.playTTS.isPlaying()) {
                Voca.updateSpeakerIconStop(binding.includeTodayExpression.ivSpeaker);
            }
        } else {
            if(activity.playTTS.isPlaying()) {
                onSpeakerClick();
            }
        }
//        stopPlayTTS();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            switch (successEvent.getEventType()) {
                case LOGOUT:
                case LOGIN:
                    initLayout();
                    break;
                case VOCA_KNOW_CHANGED:
                    IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) successEvent.getModel();
                    if ((iVocaBasicItem != null) && (Voca.isSameVoca(iVocaBasicItem, (IVocaBasicItem) todayExpressionItem))) {
                        todayExpressionItem = activity.subDatabase.getTodayExpression(iVocaBasicItem.getVIId());
                        displayTodayExpression();
                    }
                    break;
                case MOTHER_TONGUE_LANGUAGE_CHANGED:
                case DISPLAY_ENGLISH_MEANING_TOO:
                    if (successEvent.getEventType() == BaseEvent.EventType.MOTHER_TONGUE_LANGUAGE_CHANGED) {
                        activity.subDatabase.updateMotherTongueLanguage(activity);
                    }
                    todayExpressionItem = activity.subDatabase.getTodayExpression(todayExpressionItem.getVIId());
                    displayTodayExpression();
                    break;
                case DISPLAY_PRONUNCIATION:
                    displayTodayExpression();
                    break;
            }
        }
    }

    private void setOnClickListeners() {
        binding.vBasicExpression.cvMainMenu.setOnClickListener(this);
        binding.vBookmark.cvMainMenu.setOnClickListener(this);
//        binding.vPreviousPractice.cvMainMenu.setOnClickListener(this);
        binding.vTravelHotel.cvMainMenu.setOnClickListener(this);
        binding.includeTodayExpression.vRefresh.setOnClickListener(this);
        binding.includeTodayExpression.vChatGpt.setOnClickListener(this);
        binding.vTravelRestaurant.cvMainMenu.setOnClickListener(this);
        binding.vTravelAirport.cvMainMenu.setOnClickListener(this);
        binding.includeTodayExpression.vToday.setOnClickListener(this);
        binding.includeTodayExpression.ivSpeaker.setOnClickListener(this);
        binding.vTravelTransport.cvMainMenu.setOnClickListener(this);
        binding.vTravelShopping.cvMainMenu.setOnClickListener(this);
        binding.vTravelTour.cvMainMenu.setOnClickListener(this);
        binding.vTravelTelephone.cvMainMenu.setOnClickListener(this);
        binding.vTravelEmergency.cvMainMenu.setOnClickListener(this);
        binding.vVocaListRecording.cvMainMenu.setOnClickListener(this);
        binding.fabPracticeConversation.setOnClickListener(view -> {
            if (sharedPreferences.isFirstConversationPracticeFab()) {
                DialogUtil.showPositiveDialog(activity, R.string.info, R.string.dialog_message_first_practice_conversation, R.string.ok, () -> {
                    sharedPreferences.setFirstConversationPracticeFab();
                    startActivity(PracticeConversationActivity.createIntentWithBookId(activity, WordListType.BOOK, (int) sharedPreferences.getLastReadBookId()));
                });
            } else {
                startActivity(PracticeConversationActivity.createIntentWithBookId(activity, WordListType.BOOK, (int) sharedPreferences.getLastReadBookId()));
            }
        });
    }
    @Override
    public void onClick(View view) {
        EnumLanguage studyLanuage = EnumLanguage.getStudyLanguage(getContext());
        switch (view.getId()) {
//            case R.id.vBasicExpression:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookBasicExpression(studyLanuage));
//                break;
//            case R.id.vTravelAirport:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelAirport(studyLanuage));
//                break;
//            case R.id.vTravelHotel:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelHotel(studyLanuage));
//                break;
//            case R.id.vTravelTransport:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTransport(studyLanuage));
//                break;
//            case R.id.vTravelRestaurant:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelRestaurant(studyLanuage));
//                break;
//            case R.id.vTravelShopping:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelShopping(studyLanuage));
//                break;
//            case R.id.vTravelTour:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTour(studyLanuage));
//                break;
//            case R.id.vTravelTelephone:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTelephone(studyLanuage));
//                break;
//            case R.id.vTravelEmergency:
//                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelEmergency(studyLanuage));
//                break;
            case R.id.vBasicExpression:
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookBasicExpression(studyLanuage), ConversationBookUtil.getBookBasicExpressionGroup(studyLanuage), getString(R.string.conv_home_menu_basic_expression));
                break;
            case R.id.vTravelAirport:
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelAirport(studyLanuage),ConversationBookUtil.getBookTravelAirportGroup(studyLanuage), getString(R.string.conv_home_menu_travel_airport));
                break;
//            case R.id.vPreviousPractice:
//                int bookId = (int) sharedPreferences.getLastReadBookId();
//                startActivity(PracticeConversationActivity.createIntentWithBookId(activity, WordListType.BOOK, bookId));
//                break;
            case R.id.vTravelHotel:
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelHotel(studyLanuage),ConversationBookUtil.getBookTravelHotelGroup(studyLanuage), getString(R.string.conv_home_menu_travel_hotel));
                break;
            case R.id.vTravelTransport:
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTransport(studyLanuage),ConversationBookUtil.getBookTravelTransportGroup(studyLanuage), getString(R.string.conv_home_menu_travel_transport));
                break;
            case R.id.vTravelRestaurant:
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelRestaurant(studyLanuage),ConversationBookUtil.getBookTravelRestaurantGroup(studyLanuage), getString(R.string.conv_home_menu_travel_restaurant));
                break;
            case R.id.vTravelShopping:
//                if (UserUtil.isDebugOrAdminUser(activity)) {
//                    Intent i = new Intent(activity, GptWebTextShortCutActivity.class);
//                    startActivity(i);
//                } else {
                openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelShopping(studyLanuage),ConversationBookUtil.getBookTravelShoppingGroup(studyLanuage), getString(R.string.conv_home_menu_travel_shopping));
//                }
                break;
            case R.id.vTravelTour:
//                if (UserUtil.isDebugOrAdminUser(activity)) {
//                    Intent i1 = new Intent(activity, GptWebMenuActivity.class);
//                    startActivity(i1);
//                } else {
                    openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTour(studyLanuage),ConversationBookUtil.getBookTravelTourGroup(studyLanuage), getString(R.string.conv_home_menu_travel_tour));
//                }
                break;
            case R.id.vTravelTelephone:
//                if (UserUtil.isDebugOrAdminUser(activity)) {
//                    startActivity(WorkbookListUserVocaBookLocalActivity.createIntent(activity));
//                } else {
                    openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelTelephone(studyLanuage),ConversationBookUtil.getBookTravelTelephoneGroup(studyLanuage), getString(R.string.conv_home_menu_travel_telephone));
//                }
                break;
            case R.id.vTravelEmergency:
//                if (UserUtil.isDebugOrAdminUser(activity)) {
//                    startActivity(TTSSaveActivity.createIntent(activity));
//                } else {
                    openConvVocaListScreenByBookId(ConversationBookUtil.getBookTravelEmergency(studyLanuage),ConversationBookUtil.getBookTravelEmergencyGroup(studyLanuage), getString(R.string.conv_home_menu_travel_emergency));
//                }
                break;
            case R.id.vVocaListRecording:
                openVocaListRecordingBookList();
                break;
            case R.id.vBookmark:
                openConvVocaListScreenByBookmark();
                break;
            case R.id.vRefresh:
                refreshTodayExpression();
                break;
            case R.id.ivSpeaker:
                onSpeakerClick();
                break;
            case R.id.vChatGpt:
//                ChatGptWebUtil.openGptCustomTabForSentence(activity, todayExpressionItem.getVIVoca(), -1);

//                String todayExpression = todayExpressionItem.getVIVocaMeaning(activity);
//                sharedPreferences.setConversationToStudyInGptWeb(todayExpression);
//                CopyTextUtil.copyToClipboard(activity, todayExpression);
                PromptUtil.askWhatToDoWithCopiedText(activity, activity.subDatabase, todayExpressionItem.getVIVocaMeaning(activity), menu -> {
                    CopyTextUtil.copyToClipboard(activity, menu);
                    ChatGptWebUtil.openUrlInCustomTabWithDialogue(activity, menu, activity.customTabActivityHelper.getSession());
                });
//                ChatGptWebUtil.openUrlInCustomTabWithDialogue(activity, todayExpression, activity.customTabActivityHelper.getSession());

//                startActivity(PracticeConversationActivity.createIntentWithBookId(activity, 101)); //101, 112,
//                startActivity(ChatGptActivity.createIntentWithVocaId(activity, todayExpressionItem.getVIVocaId()));
                break;
        }
    }

    private void onSpeakerClick() {
        activity.playTTS.onSpeakerClick(todayExpressionItem, binding.includeTodayExpression.ivSpeaker);
    }

    private void openConvVocaListScreenByBookId(int expressionBookId, int bookId, String bookTitle) {
//        openNewScreen(
//                ConvVocaListActivity.createIntentByBookId(activity, bookId, true)
//        );

        openNewScreen(
                WorkbookListActivity.createIntentByExpressionAndBookId(activity, expressionBookId, bookId, bookTitle)
        );


//        Fragment fragment = new MenuFragmentWorkbook();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, parentBook);
//        fragment.setArguments(bundle);
//        Utils.loadFragment((AppCompatActivity) getActivity(), fragment, getFragmentContainerId(), true, getLogTag() + parentBook.getIBookId());
    }

    private void openConvVocaListScreenByBookmark() {
        openNewScreen(
                ConvVocaListActivity.createIntentByBookmark(activity)
        );
    }

    private void openVocaListRecordingBookList() {
        if (UserUtil.isLoggedIn(activity, true)) {
            openNewScreen(
                    VocaListRecordingBookListActivity.createIntent(activity)
            );
        }
    }
}
