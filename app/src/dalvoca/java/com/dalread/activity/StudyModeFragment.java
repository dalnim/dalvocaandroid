package com.dalread.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.RolePlayingAdapter;
import com.dalread.adapter.RolePlayingContentAdapter;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.adapter.StudyChatExamAdapter;
import com.dalread.base.BaseChatDetailsFragment;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumType;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.FuriganaView;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ChangeRolePlayingDialog;
import com.dalread.dialog.ConfirmModifyMessageDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.EvaluateDialog;
import com.dalread.dialog.ExamDialog;
import com.dalread.dialog.LessonModeDialog;
import com.dalread.dialog.ReadingsDialog;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.RolePlayingConversationDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.SyncACellDialog;
import com.dalread.dialog.TopicsDialog;
import com.dalread.dialog.TypeChooseMessageDialog;
import com.dalread.dialog.TypeFeedbackDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.helper.FirestoreHelper;
import com.dalread.helper.StudyChatPlayVocaHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnVocaStudyChatExamClickListener;
import com.dalread.model.ChatMessageResponse;
import com.dalread.model.ChatRoomInfo;
import com.dalread.model.CurrentLesson;
import com.dalread.model.GRAMMAR;
import com.dalread.model.Lesson;
import com.dalread.model.LessonOption;
import com.dalread.model.READING;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.TBL_MESSAGE;
import com.dalread.model.User;
import com.dalread.model.VocaBookInChat;
import com.dalread.model.VocaFromAllVocaBook;
import com.dalread.model.VocaReading;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatAllWords;
import com.dalread.model.VocaStudyChatByCategory;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaStudyChatRuby;
import com.dalread.model.roleplaying.Category;
import com.dalread.model.roleplaying.ContentSubDetails;
import com.dalread.model.roleplaying.Conversation;
import com.dalread.model.roleplaying.RolePlayingContent;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;

public class StudyModeFragment extends BaseChatDetailsFragment {

    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    @BindView(R.id.v_adjust_height)
    View vAdjustHeight;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.ll_separator_bar)
    LinearLayout llSeparatorBar;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_lesson_mode)
    View btnLessonMode;
    @BindView(R.id.btn_topics)
    View btnTopics;
    @BindView(R.id.btn_msg)
    View btnMsg;
    @BindView(R.id.btn_exam)
    View btnExam;
    @BindView(R.id.btn_role_playing)
    View btnRolePlaying;
    @BindView(R.id.ic_next)
    View icNext;
    @BindView(R.id.btn_grammar)
    View btnGrammar;
    @BindView(R.id.wv_grammar)
    WebView wvGrammar;
    @BindView(R.id.v_reading)
    View vReading;
    @BindView(R.id.v_reading_top)
    View vReadingTop;
    @BindView(R.id.fv_reading)
    FuriganaView fvReading;
    @BindView(R.id.v_adjust_height_reading)
    View vAdjustHeightReading;
    @BindView(R.id.tv_reading_words)
    TextView tvReadingWords;
    @BindView(R.id.tv_reading_meaning)
    TextView tvReadingMeaning;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    @BindString(R.string.tpl_move_to_book_name)
    String tplMoveToBookName;
    @BindString(R.string.tpl_join_a_lesson)
    String tplJoinALesson;
    @BindString(R.string.tpl_exit_a_lesson)
    String tplExitALesson;
    @BindString(R.string.tpl_side_glance)
    String tplSideGlance;
    @BindString(R.string.lesson_message_synccell_success)
    String lessonMessageSyncSuccess;
    @BindString(R.string.lesson_message_synccell_fail)
    String lessonMessageSyncFail;
    @BindString(R.string.tpl_saved_as_a_next_wordbook)
    String tplSavedAsALastWordbook;
    @BindString(R.string.tpl_repeat)
    String tplRepeat;
    @BindString(R.string.tpl_index)
    String tplIndex;
    @BindString(R.string.chat_message_title_me)
    String chatMessageTitleMe;
    @BindString(R.string.chat_message_title_opponent)
    String chatMessageTitleOpponent;

    @BindArray(R.array.level_options)
    String[] levelOptions;

    private Context context;
    private Handler handler;
    private Bundle bundle;
    private int uid;
    private ChatRoomInfo chatRoomInfo;
    private ChatRoomInfo studyInfo;
    private Lesson lesson;
    private LessonOption lessonOption;
    private User mainStudent;
    private User mainTutor;
    private User me;
    private List<Object> messages;
    private ArrayList<SERVER_VOCABOOKS> serverVocabooks;
    private ArrayList<VocaBookInChat> studiedBooks;
    private VocaFromAllVocaBook vocaFromAllVocaBook;
    private ArrayList<VocaStudyChat> vocas;
    private StudyChatAdapter adapter;
    private StudyChatAdapter previewOrExamAdapter;
    private ArrayList<VocaStudyChatExam> examVocas;
    private StudyChatExamAdapter examAdapter;
    private RolePlayingContent rolePlayingContent;
    private RolePlayingAdapter rolePlayingAdapter;
    private RolePlayingContentAdapter rolePlayingContentAdapter;
    private EventBus eventBus;
    private CenterLayoutManager centerLayoutManager;
    private CenterLayoutManager contentCenterLayoutManager;
    private AlertDialog alertDialog;
    private RegisterVocaDialog registerVocaDialog;
    private EvaluateDialog evaluateDialog;
    private TypeFeedbackDialog typeFeedbackDialog;
    private TopicsDialog topicsDialog;
    private ExamDialog examDialog;
    private LessonModeDialog lessonModeDialog;
    private TypeChooseMessageDialog messageListDialog;
    private ConfirmModifyMessageDialog confirmModifyMessageDialog;
    private TypeInputDialog typeMessageDialog;
    private SyncACellDialog syncACellDialog;
    private StudyChatPlayVocaHelper studyChatPlayVocaHelper;
    private int bookPos;
    private EnumLanguage enumStudyLanguage;
    private EnumLanguage enumDisplayLanguage;
    private int showAsterisk;
    private int showSentence;
    private boolean isStudentAndTutorJoined;
    private ConfirmationDialog confirmationDialog;
    private ChangeRolePlayingDialog changeRolePlayingDialog;
    private SingleChoiceDialog singleChoiceDialog;
    private int selectedLevelPos;
    private RecyclerViewDialog recyclerViewDialog;
    private String[] orderValues;
    private TypeInputDialog typeChatMessageDialog;
    private FirestoreHelper firestoreHelper;
    private List<Integer> studyOrderList;
    private int studyOrderPos;
    private int studyOrderRolePlayingPos;
    private String studyOrderTitle;
    private String studyOrderMessage;
    private int studyOrderMessageId;
    private int lessonMode;
    private RolePlayingConversationDialog rolePlayingConversationDialog;
    private int grammarId;
    private TopicsDialog grammarDialog;
    private List<GRAMMAR> sortedGrammars;
    private int lessonReadingId;
    private VocaReading vocaReading;
    private ReadingsDialog readingsDialog;
    private List<READING> sortedReadings;
    private boolean showStudentMeaning;

    private int typeCall = Constant.JITSI.STATUS.NONE;
    private int heightRoot = 0;

    private ConfirmationDialog voiceInformationDialog;
    private ConfirmationDialog jointVoiceDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_study_mode;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initEventBus();
        initLayout();
        setMe(chatRoomInfo);
        getData();
    }

    private void initData() {
        context = getContext();
        handler = new Handler(msg -> {
            if (msg.what == Constant.MSG_WHAT.SAVE_STUDIED_WORKBOOK_ID) {
                if (mainStudent != null && (uid == mainStudent.getUid()) && mainTutor != null && Utils.isConnected(context)) {
                    Integer bookId = (Integer) msg.obj;
                    application.getDalAiImpl().saveStudiedWorkbookIDInStudyMode(
                            getStudyLang(),
                            chatRoomInfo.getFirestoreChatRoomId(),
                            mainStudent.getUid(),
                            mainTutor.getUid(),
                            bookId,
                            Constant.API_VALUE.VALUE_SERVER_BOOK,
                            null
                    );
                }
                return true;
            }
            return false;
        });
        bundle = getArguments();
        uid = sharedPreferences.getRealUid();
        chatRoomInfo = (ChatRoomInfo) bundle.getSerializable(Constant.BUNDLE.KEY_CHAT_ROOM_INFO);
        lesson = (Lesson) bundle.getSerializable(Constant.BUNDLE.KEY_LESSON);
        if (lesson != null) {
            studyInfo = chatRoomInfo;
        }
        studyChatPlayVocaHelper = new StudyChatPlayVocaHelper(context);
        studyChatPlayVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                updateItemStatus(utteranceId, true);
            }

            @Override
            public void onDone(String utteranceId) {
                updateItemStatus(utteranceId, false);
            }

            @Override
            public void onError(String utteranceId) {
                updateItemStatus(utteranceId, false);
            }
        });
        enumStudyLanguage = EnumLanguage.findByIdApi(getStudyLang());
        enumDisplayLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        serverVocabooks = Voca.getSortedBookList(String.valueOf(enumStudyLanguage.getIdApi()));
        orderValues = Voca.getOrderValues();
        studyOrderList = new ArrayList<>();
        showStudentMeaning = true;
    }

    private void initEventBus() {
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initLayout() {
        DLog.d(getLogTag(), "initLayout");
        llRoot.post(() -> heightRoot = llRoot.getHeight());
        rvContent.setLayoutManager(contentCenterLayoutManager = new CenterLayoutManager(context));
        rvVoca.setLayoutManager(centerLayoutManager = new CenterLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        alertDialog = new AlertDialog(context);
        registerVocaDialog = new RegisterVocaDialog(context, onKnowChangeListener);
        evaluateDialog = new EvaluateDialog(context, onEvaluateClickListener);
        typeFeedbackDialog = new TypeFeedbackDialog(context, onTypeFeedbackClickListener);
        topicsDialog = new TopicsDialog(context, onTopicsDialogItemClickListener);
        examDialog = new ExamDialog(context, onExamDialogItemClickListener);
        lessonModeDialog = new LessonModeDialog(context, onLessonModeClickListener);
        messageListDialog = new TypeChooseMessageDialog(context, onTypeChooseMessageListener);
        confirmModifyMessageDialog = new ConfirmModifyMessageDialog(context, onConfirmModifyMessageListener);
        typeMessageDialog = new TypeInputDialog(context, onTypeMessageClickListener);
        typeMessageDialog.setTitle(R.string.message);
        syncACellDialog = new SyncACellDialog(context, onSyncACellClickListener);
        // check jitsi call status
        if (activity.isVoiceCallingFromNotification()) {
            typeCall = Constant.JITSI.STATUS.CALL;
        }
        checkTypeVoiceCall(false);
        changeRolePlayingDialog = new ChangeRolePlayingDialog(context, onChangeRolePlayingClickListener);
        singleChoiceDialog = new SingleChoiceDialog(context);
        recyclerViewDialog = new RecyclerViewDialog(context);
        recyclerViewDialog.setOnDismissListener(onRecyclerViewDialogDismissListener);
        llSeparatorBar.setOnTouchListener(separatorBarListener);
        btnLessonMode.setOnTouchListener(separatorBarListener);
        btnTopics.setOnTouchListener(separatorBarListener);
        btnMsg.setOnTouchListener(separatorBarListener);
        btnExam.setOnTouchListener(separatorBarListener);
        btnRolePlaying.setOnTouchListener(separatorBarListener);
        btnGrammar.setOnTouchListener(separatorBarListener);
        icNext.setOnTouchListener(separatorBarListener);
        vAdjustHeightReading.setOnTouchListener(separatorBarListener);
        typeChatMessageDialog = new TypeInputDialog(context, onTypeChatMessageClickListener);
        typeChatMessageDialog.setTitle(R.string.chat_message);
        typeChatMessageDialog.setHint(R.string.hint_chat_message);
        rolePlayingConversationDialog = new RolePlayingConversationDialog(context, onRolePlayingConversationClick);
        grammarDialog = new TopicsDialog(context, onGrammarOptionClickListener);
        wvGrammar.getSettings().setJavaScriptEnabled(true);
        wvGrammar.setWebViewClient(wvGrammarClient);
        readingsDialog = new ReadingsDialog(context, onReadingOptionClickListener);
    }

    private void getData() {
        if (bundle.containsKey(Constant.BUNDLE.KEY_STUDY_INFO)) {
            studyInfo = (ChatRoomInfo) bundle.getSerializable(Constant.BUNDLE.KEY_STUDY_INFO);
            lessonOption = (LessonOption) bundle.getSerializable(Constant.BUNDLE.KEY_LESSON_OPTION);
            initSelectedLevelPos();
            initStudyOrderList();
            setMainStudent();
            setMainTutor();
            studiedBooks = (ArrayList<VocaBookInChat>) bundle.getSerializable(Constant.BUNDLE.KEY_STUDIED_BOOKS);
            vocaFromAllVocaBook = (VocaFromAllVocaBook) bundle.getSerializable(Constant.BUNDLE.KEY_VOCA_FROM_ALL_VOCA_BOOK);
            vocas = vocaFromAllVocaBook.getAllVocaList();
            showAsterisk = bundle.getInt(Constant.BUNDLE.KEY_ASTERISK);
            showSentence = bundle.getInt(Constant.BUNDLE.KEY_SHOW_SENTENCE);
            examVocas = (ArrayList<VocaStudyChatExam>) bundle.getSerializable(Constant.BUNDLE.KEY_STUDY_VOCA_EXAM_LIST);
            rolePlayingContent = (RolePlayingContent) bundle.getSerializable(Constant.BUNDLE.KEY_STUDY_ROLE_PLAYING_CONTENT);
            lessonMode = bundle.getInt(Constant.BUNDLE.KEY_LESSON_MODE);
            grammarId = bundle.getInt(Constant.BUNDLE.KEY_GRAMMAR_ID);
            lessonReadingId = bundle.getInt(Constant.BUNDLE.KEY_LESSON_READING_ID);
            vocaReading = (VocaReading) bundle.getSerializable(Constant.BUNDLE.KEY_VOCA_READING);
            studyOrderPos = bundle.getInt(Constant.BUNDLE.KEY_STUDY_ORDER_POS);
            studyOrderRolePlayingPos = bundle.getInt(Constant.BUNDLE.KEY_STUDY_ORDER_ROLE_PLAYING_POS);
            showStudentMeaning = bundle.getBoolean(Constant.BUNDLE.KEY_SHOW_STUDENT_MEANING);
            if (lessonMode == Constant.API_VALUE.LESSON_MODE_EXAM) {
                setRecyclerViewExamData(examVocas);
            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
                onBackToRolePlayingMode();
            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_GRAMMAR) {
                showGrammarById();
            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                onBackToReadingMode();
            } else {
                onBackToNormalMode();
            }
        } else if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                getLessonOption(lesson);
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
        if (!activity.isVoiceCallingFromNotification() && bundle.getBoolean(Constant.BUNDLE.KEY_IS_OPEN)) {
            // check show voice call when tutor open lesson
            // check observer > show dialog
            if ((lesson != null && lesson.isTutor())) {
                openVoiceInformationDialog();
            } else if (isMeObserver()) {
                openVoiceJoinDialog();
            }
            eventBus.post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.CALL_BACK_SUCCESS, null));
        }
    }

    private void setMe(ChatRoomInfo chatRoomInfo) {
        me = getMe(chatRoomInfo);
        if ((lesson != null && lesson.isTutor()) || isMeObserver()) {
            final User friend = getFriend(chatRoomInfo);
            if (friend != null) {
                enumDisplayLanguage = EnumLanguage.findByFormatApi(friend.getLangNative());
            }
        }
        setTitleStudy(me);
        activity.setStudyRole(getMyStudyRole());
        messages = Voca.getMessageListForLesson(enumStudyLanguage, enumDisplayLanguage);
        List<Object> messagesByStudyRole = Voca.getMessageListForLessonByStudyRole(enumStudyLanguage, enumDisplayLanguage, getMyStudyRole());
        messageListDialog.setAdapter(messagesByStudyRole, onMessageClickListener);
    }

    private User getMe(ChatRoomInfo chatRoomInfo) {
        for (User user : chatRoomInfo.getUserList()) {
            if (user.getUid() == uid) {
                return user;
            }
        }
        return null;
    }

    private User getFriend(ChatRoomInfo chatRoomInfo) {
        for (User user : chatRoomInfo.getUserList()) {
            if (user.getUid() != uid) {
                return user;
            }
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    private void setTitleStudy(User me) {
        String title = Voca.getUserStudyRoleTitle(context, me, lesson, false).replace(" - null", "");
        if (rvVoca.getAdapter() != null && rvVoca.getAdapter() == rolePlayingAdapter) {
            title = String.format("(L%d) ", rolePlayingAdapter.getLanguageLevel()) + title;
        } else if (lessonOption != null) {
            title = String.format("(L%d) ", lessonOption.getLanguageLevel()) + title;
        }
        activity.setToolbarTitle(title);
    }

    private boolean setMainStudent() {
        for (User user : studyInfo.getUserList()) {
            if (Voca.isMainStudent(user)) {
                mainStudent = user;
                setTutorAndStudentJoined();
                return true;
            }
        }
        if (mainStudent == null) {
            for (User user : studyInfo.getUserList()) {
                if (user.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
                    user.setStudyRoleMain(1);
                    mainStudent = user;
                    setTutorAndStudentJoined();
                    return true;
                }
            }
        }
        setTutorAndStudentJoined();
        return false;
    }

    private boolean setMainTutor() {
        for (User user : studyInfo.getUserList()) {
            if (Voca.isMainTutor(user)) {
                mainTutor = user;
                setTutorAndStudentJoined();
                return true;
            }
        }
        if (mainTutor == null) {
            for (User user : studyInfo.getUserList()) {
                if (user.getStudyRole() == Constant.STUDY_ROLE_TUTOR) {
                    user.setStudyRoleMain(1);
                    mainTutor = user;
                    setTutorAndStudentJoined();
                    return true;
                }
            }
        }
        setTutorAndStudentJoined();
        return false;
    }

    private void setTutorAndStudentJoined() {
        isStudentAndTutorJoined = mainStudent != null && mainTutor != null;
    }

    private void joinStudyModeInChatRoom() {
        application.getDalAiImpl().joinStudyModeInChatroom(chatRoomInfo.getFirestoreChatRoomId(), getLessonId(), getMyStudyRole(), new DalApiListener<Boolean>() {

            @Override
            public void onSuccess(Boolean response) {
                getStudyUserInfoInChatRoom();
            }

            @Override
            public void onFailure(String error) {
                getStudyUserInfoInChatRoom();
            }
        });
    }

    private void getLessonOption(final Lesson lesson) {
        if (lesson == null) {
            getInfoInChatRoom();
        } else {
            application.getDalAiImpl().getLessonOption(lesson.getStudentId(), Constant.API_VALUE.LIST_LESSON_FOR_STUDENT, new DalApiListener<LessonOption>() {

                @Override
                public void onSuccess(LessonOption response) {
                    lessonOption = response;
                    initSelectedLevelPos();
                    initStudyOrderList();
                    activity.setPendingFinishLessonToast(lessonOption);
                    getInfoInChatRoom();
                }

                @Override
                public void onFailure(String error) {
                    getInfoInChatRoom();
                }
            });
        }
    }

    private void getInfoInChatRoom() {
        if (studyInfo == null) {
            joinStudyModeInChatRoom();
        } else {
            afterGetStudyUserInfoInChatRoom();
        }
    }

    private void getStudyUserInfoInChatRoom() {
        application.getDalAiImpl().getStudyUserInfoInChatroom(
                chatRoomInfo.getFirestoreChatRoomId(),
                getLessonId(),
                false,
                new DalApiListener<ChatRoomInfo>() {

                    @Override
                    public void onSuccess(ChatRoomInfo response) {
                        studyInfo = response;
                        afterGetStudyUserInfoInChatRoom();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                });
    }

    private void afterGetStudyUserInfoInChatRoom() {
        updateStartStudiedBook();
        activity.setStudying(true);
        activity.setStudyUserListToPopup(studyInfo);
        setMainStudent();
        setMainTutor();
        getCurrentLessonTopicOfUser();
    }

    private void getCurrentLessonTopicOfUser() {
        application.getDalAiImpl().getCurrentLessonTopicOfUser(
                String.valueOf(getStudentId()),
                getStudyLang(),
                new DalApiListener<CurrentLesson>() {

                    @Override
                    public void onSuccess(CurrentLesson response) {
                        boolean makeNewExam = chatRoomInfo.getIsFirstJoinedUser() == 1;
                        if (response == null) {
                            lessonMode = Constant.API_VALUE.LESSON_MODE_NORMAL;
                            getVocas(makeNewExam, true, false);
                        } else {
                            lessonMode = response.getLessonMode();
                            grammarId = response.getGrammarId();
                            lessonReadingId = response.getLessonReadingId();
                            if (lessonMode == Constant.API_VALUE.LESSON_MODE_EXAM) {
                                getVocas(makeNewExam, false, false);
                                getAnExamInStudyMode();
                            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
                                getVocas(makeNewExam, false, false);
                                getRolePlayingContentsJson(response.getRolePlayingId(), getStudentId());
                            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_GRAMMAR) {
                                getVocas(makeNewExam, false, false);
                                showGrammarById();
                            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                                getVocas(makeNewExam, false, false);
                                getReadingContentsJsonInStudyMode();
                            } else {
                                getVocas(makeNewExam, true, false);
                            }
                        }
                        if (lesson.getLessonType() != Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
                            activity.showStudentOptionDialog();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    private void getVocas(boolean makeNewExam, boolean notify, boolean checkMeObserver) {
        if (studyInfo.getVocaBook() == null) {
            Loading.hide();
            return;
        }
        int bookId = studyInfo.getVocaBook().getVocaBookId();
        application.getDalAiImpl().getVocasFromAllVocaBookInStudyMode(
                chatRoomInfo.getFirestoreChatRoomId(),
                getStudentId(),
                getTutorId(),
                bookId,
                studyInfo.getVocaBook().getVocaBookType(),
                0,
                Constant.LOADING_MAX_ITEM + 1,
                getStudyLang(),
                getStudentLangNative(),
                Constant.MAKE_RUBY_TEXT ? 1 : 0,
                makeNewExam ? 1 : 0,
                new DalApiListener<VocaFromAllVocaBook>() {

                    @Override
                    public void onSuccess(VocaFromAllVocaBook response) {
                        Loading.hide();
                        if (response == null) return;
                        vocaFromAllVocaBook = response;
                        onGetVocasSuccess(notify);
                        if (checkMeObserver && isMeObserver()) {
                            confirmationDialog = new ConfirmationDialog(context, new ConfirmationDialog.OnDialogClickListener() {

                                @Override
                                public void onPositive(DialogInterface dialog) {
                                    dialog.dismiss();
                                    sendVocabookInStudyMode(bookId, false);
                                }

                                @Override
                                public void onNegative(DialogInterface dialog) {
                                    dialog.dismiss();
                                    saveVocabookIDInStudyMode(bookId, 0, Constant.SHOW_TOAST_NO, new DalApiListener<Boolean>() {

                                        @Override
                                        public void onSuccess(Boolean response) {
                                            Loading.hide();
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Loading.hide();
                                        }
                                    });
                                }
                            });
                            confirmationDialog.setMyTitle(R.string.choose);
                            confirmationDialog.setMessage(R.string.msg_observer_let_other_users_know_changed_book);
                            confirmationDialog.setPositiveText(R.string.yes);
                            confirmationDialog.setNegativeText(R.string.no);
                            confirmationDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    private void onGetVocasSuccess(boolean notify) {
        int studyLang = enumStudyLanguage.getIdApi();
        for (VocaStudyChat voca : vocaFromAllVocaBook.getStudyVocaList()) {
            voca.setDisplayEvaluation(false);
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
        }
        for (VocaStudyChat voca : (vocas = vocaFromAllVocaBook.getAllVocaList())) {
            voca.setDisplayEvaluation(false);
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
        }
        for (VocaStudyChat voca : vocaFromAllVocaBook.getExamVocaList()) {
            voca.setDisplayEvaluation(false);
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
        }
        showSentence = Constant.SHOW_SENTENCE_ALL;
        if (notify) {
            onShowAsteriskChanged(0, false);
        }
    }

    private void updateStartStudiedBook() {
        if (studyInfo != null && studyInfo.getVocaBook() != null) {
            Message msg = handler.obtainMessage(Constant.MSG_WHAT.SAVE_STUDIED_WORKBOOK_ID, studyInfo.getVocaBook().getVocaBookId());
            handler.sendMessageDelayed(msg, Constant.SAVE_STUDIED_WORKBOOK_ID_DELAY_TIME);
            if (studiedBooks == null) {
                studiedBooks = new ArrayList<>();
            }
            VocaBookInChat book = null;
            for (VocaBookInChat studiedBook : studiedBooks) {
                if (studiedBook.getVocaBookId() == studyInfo.getVocaBook().getVocaBookId()) {
                    book = studiedBook;
                    break;
                }
            }
            if (book == null) {
                book = studyInfo.getVocaBook();
                studiedBooks.add(book);
            }
            book.setStartTime(System.currentTimeMillis());
            book.setFinishTime(0);
        }
    }

    private void updateFinishStudiedBook() {
        handler.removeMessages(Constant.MSG_WHAT.SAVE_STUDIED_WORKBOOK_ID);
        if (studyInfo.getVocaBook() != null && studiedBooks != null) {
            for (VocaBookInChat studiedBook : studiedBooks) {
                if (studiedBook.getVocaBookId() == studyInfo.getVocaBook().getVocaBookId()) {
                    studiedBook.setFinishTime(System.currentTimeMillis());
                    studiedBook.calculateDuration();
                    break;
                }
            }
        }
    }

    private void setRecyclerViewData() {
        activity.getPlayVocaHelper().stop();
        adapter = new StudyChatAdapter(context);
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setStudyRole(getMyStudyRole());
        adapter.setShowAsterisk(showAsterisk);
        adapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        adapter.setStudyLang(enumStudyLanguage.getIdApi());
        adapter.setHasBookName(true);
        adapter.setHasHeader(true);
        adapter.setShowStudentMeaning(showStudentMeaning);
        adapter.setListener(onVocaStudyChatClickListener);
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_REVIEW) {
            adapter.setDataReview(vocas);
        } else {
            adapter.setData(vocas);
        }
        rvVoca.setAdapter(adapter);
        if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
            rvVoca.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
            bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
        }
        updateBookPos(true);
        if (studyInfo.getVocaBook() != null) {
            int cellIndex = studyInfo.getVocaBook().getVocaBookCellIndex();
            highlightCellIndex(cellIndex);
        }
        showButtonsForStudyMode();
        showKnowAllPhrasesDialog();
    }

    private void setRecyclerViewPreviewOrExamData(List<VocaStudyChat> vocas) {
        activity.getPlayVocaHelper().stop();
        previewOrExamAdapter = new StudyChatAdapter(context);
        previewOrExamAdapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        previewOrExamAdapter.setStudyRole(getMyStudyRole());
        previewOrExamAdapter.setShowAsterisk(showAsterisk);
        previewOrExamAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        previewOrExamAdapter.setStudyLang(enumStudyLanguage.getIdApi());
        previewOrExamAdapter.setHasBookName(true);
        previewOrExamAdapter.setHasHeader(true);
        previewOrExamAdapter.setShowStudentMeaning(showStudentMeaning);
        previewOrExamAdapter.setListener(onVocaStudyChatClickListener);
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_REVIEW) {
            previewOrExamAdapter.setDataReview(vocas);
        } else {
            previewOrExamAdapter.setData(vocas);
        }
        rvVoca.setAdapter(previewOrExamAdapter);
        if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
            rvVoca.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
            bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
        }
        updateBookPos(true);
        showButtonsForStudyMode();
    }

    private void updateBookPos(boolean hasCount) {
        if (serverVocabooks != null && studyInfo.getVocaBook() != null) {
            String bookId = String.valueOf(studyInfo.getVocaBook().getVocaBookId());
            for (SERVER_VOCABOOKS serverVocabook : serverVocabooks) {
                if (serverVocabook.getID().equals(bookId)) {
                    bookPos = serverVocabooks.indexOf(serverVocabook);
                    showHeaderBookName(serverVocabook, hasCount);
                    break;
                }
            }
        }
    }

    private void showToastBookChanged(SERVER_VOCABOOKS serverVocabook) {
        String bookName = Voca.getHeaderBookName(serverVocabook, enumDisplayLanguage, enumStudyLanguage);
        ToastUtil.getInstance(context).show(bookName);
    }

    private void showHeaderBookName(SERVER_VOCABOOKS serverVocabook, boolean hasCount) {
        String bookName = Voca.getHeaderBookName(serverVocabook, enumDisplayLanguage, enumStudyLanguage);
        if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
            StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
            adapter.setBookName(bookName);
            adapter.notifyItemChanged(0);
        }
        studyInfo.getVocaBook().setBookName(bookName);
        if (hasCount) {
            String message = String.format(tplMoveToBookName, bookName);
            if (vocas != null) {
                message = "(" + vocas.size() + ") " + message;
            }
            if (lessonOption != null && lessonOption.getRepeatCount() > 1) {
                message += "\n"
                        + String.format(tplIndex, studyInfo.getVocaBook().getTopicBeginIndex() + 1, lessonOption.getRepeatTopics())
                        + String.format(tplRepeat, studyInfo.getVocaBook().getTopicRepeatedCount() + 1, lessonOption.getRepeatCount());
            }
            onMessageChanged(message);
        }
    }

    private OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {

        @Override
        public void onItemClick(VocaStudyChat voca) {
            syncACellDialog.show(voca, recyclerViewDialog.isShowing());
        }

        @Override
        public void onDoubleItemClick(VocaStudyChat voca) {

        }

        @Override
        public void onPlayClick(final VocaStudyChat voca) {
            if (isStudentAndTutorJoined) {
                if (voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_ALL_SENTENCE_LIST
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_MSG_VOCA_LIST
                        || voca.getTblTypeSync() == Constant.API_VALUE.TBL_TYPE_SYNC_READING_VOCA_LIST) {
                    syncCellInDialog(voca);
                } else {
                    blinkCell(voca);
                }
                syncCellWithOtherUsersInStudyMode(voca);
            } else {
                playVoca(voca);
            }
        }

        @Override
        public void onBigIconClick(VocaStudyChat voca) {
            if (me == null || me.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
                registerVocaDialog.show(voca, false);
            } else { // Tutor
                if (mainStudent != null) {
                    evaluateDialog.show(voca);
                }
            }
        }

        @Override
        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
            if (Voca.isShow4Buttons(voca.getVocaKnow())) {
                alertDialog.show(R.string.msg_set_grade_to_see_original, R.string.ok, null);
            } else {
                sideGlanceSentenceInStudyMode(voca);
                if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    rvVoca.postDelayed(() -> onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN), Constant.STOP_BLINK_DELAY_TIME);
                }
            }
        }

        @Override
        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
            onKnowChangeListener.onVocaKnowChange(voca, vocaKnow);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChat voca, String evaluateGrade) {
            changeEvaluateVocaGrade(voca, evaluateGrade, "");
        }

        @Override
        public void onAnswerClick(VocaStudyChatExam voca) {
            onVocaStudyChatExamClickListener.onButtonClick(voca);
        }
    };

    private OnClickListener onRolePlayingCategoryClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            recyclerViewDialog.dismiss();
            if (object instanceof Category) {
                Category category = (Category) object;
                if (category.isRandom() && category.getId() == 0) {
                    List<Object> contents = recyclerViewDialog.getContents();
                    if (!contents.isEmpty()) {
                        int pos = new Random().nextInt(contents.size() - 1) + 1;
                        Category category1 = (Category) contents.get(pos);
                        category1.setRandom(true);
                        onRolePlayingCategoryClickListener.onClick(null, category1);
                    }
                } else if (category.getHasSubList() == 1) {
                    getRolePlayingCategoryList(category.getId(), category.isRandom());
                } else {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING;
                    makeRolePlayingContentsJson(category.getId(), category.getRolePlayingType());
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initPlayVocaHelper();
    }

    private void initPlayVocaHelper() {
        if (activity != null) {
            if (!activity.getPlayVocaHelper().hasMotherTongueListener()) {
                activity.getPlayVocaHelper().setMotherTongueListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        updateItemStatus(utteranceId, true);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
            }
            if (!activity.getPlayVocaHelper().hasStudyListener()) {
                activity.getPlayVocaHelper().setStudyListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        updateItemStatus(utteranceId, false);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        updateItemStatus(utteranceId, false);
                    }
                });
            }
        }
    }

    private void updateItemStatus(String utteranceId, boolean playing) {
        List vocaList = null;
        if (rvVoca.getAdapter() == examAdapter) {
            vocaList = examVocas;
        } else {
            RecyclerView.Adapter adapter = recyclerViewDialog.isShowing()
                    ? recyclerViewDialog.getAdapter()
                    : rvVoca.getAdapter();
            if (adapter instanceof StudyChatAdapter) {
                vocaList = ((StudyChatAdapter) adapter).getVocas();
            }
        }
        if (vocaList != null) {
            for (Object object : vocaList) {
                if (object instanceof VocaStudyChat) {
                    VocaStudyChat voca = (VocaStudyChat) object;
                    if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                        voca.setVIPlaying(playing);
                        notifyItemChanged(voca);
                        break;
                    }
                }
            }
        }
    }

    private void notifyItemChanged(VocaStudyChat voca) {
        rvVoca.post(() -> {
            try {
                if (rvVoca.getAdapter() == rolePlayingAdapter) {
                    if (recyclerViewDialog.isShowing()) {
                        RecyclerView.Adapter adapter = recyclerViewDialog.getAdapter();
                        if (adapter instanceof StudyChatAdapter) {
                            StudyChatAdapter studyChatAdapter = (StudyChatAdapter) adapter;
                            studyChatAdapter.notifyItemChanged(voca);
                        }
                    }
                } else if (rvVoca.getAdapter() == examAdapter) {
                    examAdapter.notifyItemChanged((VocaStudyChatExam) voca);
                } else {
                    RecyclerView.Adapter adapter = recyclerViewDialog.isShowing()
                            ? recyclerViewDialog.getAdapter()
                            : rvVoca.getAdapter();
                    if (adapter instanceof StudyChatAdapter) {
                        StudyChatAdapter studyChatAdapter = (StudyChatAdapter) adapter;
                        studyChatAdapter.notifyItemChanged(voca);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void notifyItemChangedAndMoveToCenter(VocaStudyChat voca) {
        rvVoca.post(() -> {
            if (rvVoca.getAdapter() == rolePlayingAdapter) {
                if (recyclerViewDialog.isShowing()) {
                    RecyclerView.Adapter adapter = recyclerViewDialog.getAdapter();
                    if (adapter instanceof StudyChatAdapter) {
                        StudyChatAdapter studyChatAdapter = (StudyChatAdapter) adapter;
                        if (studyChatAdapter.isOpenCorrectSection(voca)) {
                            recyclerViewDialog.moveToCenter(studyChatAdapter.notifyItemChanged(voca));
                            return;
                        }
                    }
                }
                showSentencesDialog(rolePlayingAdapter.getSentences(voca), voca);
            } else {
                int pos;
                if (rvVoca.getAdapter() == examAdapter) {
                    pos = examAdapter.notifyItemChanged((VocaStudyChatExam) voca);
                } else if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
                    pos = ((StudyChatAdapter) rvVoca.getAdapter()).notifyItemChanged(voca);
                } else {
                    pos = -1;
                }
                if (pos > -1) {
                    centerLayoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            }
        });
    }

    private void notifyItemChangedAndMoveToCenter(Conversation conversation, boolean isBranchSelected) {
        rvVoca.post(() -> {
            if (rvVoca.getAdapter() == rolePlayingAdapter) {
                if (recyclerViewDialog.isShowing()) {
                    recyclerViewDialog.dismiss();
                }
                int pos = rolePlayingAdapter.notifyItemChanged(conversation);
                if (!isBranchSelected && pos > -1) {
                    centerLayoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            }
        });
    }

    private OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {
        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {

        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {

        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDismiss() {

        }

        //TODO : use new interface methods
//        @Override
//        public void onVocaKnowChange(AmkiItem voca, int vocaKnow) {
//            if (voca instanceof VocaStudyChat) {
//                VocaStudyChat vocaStudyChat = (VocaStudyChat) voca;
//                int vocaKnowPronounce;
//                if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
//                    vocaKnowPronounce = vocaKnow;
//                } else {
//                    vocaKnowPronounce = vocaStudyChat.getVocaKnowPronounce();
//                }
//                sendVocaKnowToOthersInStudyMode(vocaStudyChat, vocaKnow, vocaKnowPronounce);
//            }
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, int vocaKnowPronounce) {
//            if (voca instanceof VocaStudyChat) {
//                sendVocaKnowToOthersInStudyMode((VocaStudyChat) voca, voca.getAmkiKnow(), vocaKnowPronounce);
//            }
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//        }
    };

    private void sendVocaKnowToOthersInStudyMode(VocaStudyChat voca, int vocaKnow, int knownPronounce) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().checkAndSendVocaKnowToOthersInStudyMode(
                        context,
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        voca.getVocaId(),
                        voca.getType(),
                        vocaKnow,
                        knownPronounce,
                        studyInfo.getVocaBook().getVocaBookId(),
                        studyInfo.getVocaBook().getVocaBookType(),
                        voca.getTblSection(),
                        voca.getTblRow(),
                        voca.getPnType(),
                        new DalApiListener<Integer>() {

                            @Override
                            public void onSuccess(Integer response) {
                                onVocaKnowChanged(voca, response, knownPronounce);
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onVocaKnowChanged(VocaStudyChat voca, int vocaKnow, int vocaKnowPronounce) {
        voca.setVocaKnow(vocaKnow);
        voca.setVocaKnowPronounce(vocaKnowPronounce);
        voca.updateVocaDisplayRubyText(voca.getVocaId(), Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
        voca.updateVocaDisplayRubyText(voca.getVocaId(), Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
        notifyItemChanged(voca);

        updateVocaDisplayRubyText(voca.getVocaId(), voca.getType(), vocaKnow, vocaKnowPronounce);
    }

    private EvaluateDialog.OnEvaluateClickListener onEvaluateClickListener = (voca, evaluateGrade) -> {
        // TODO: bring back later
        /*typeFeedbackDialog.show(voca, evaluateGrade);*/
        changeEvaluateVocaGrade((VocaStudyChat) voca, evaluateGrade, "");
    };

    private BaseDialogListener onTypeFeedbackClickListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object object) {
            ArrayList<Object> data = (ArrayList<Object>) object;
            changeEvaluateVocaGrade((VocaStudyChat) data.get(0), (String) data.get(1), (String) data.get(2));
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private void changeEvaluateVocaGrade(final VocaStudyChat voca, final String evaluateGrade, final String feedbackMessage) {
        if (mainStudent == null)
            return;
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().changeEvaluateVocaGrade(
                        getStudyLang(),
                        mainStudent.getUid(),
                        evaluateGrade,
                        voca.getVocaId(),
                        voca.getType(),
                        voca.getTblSection(),
                        voca.getTblRow(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    onEvaluateVocaGradeChanged(uid, voca, evaluateGrade, feedbackMessage);
                                    sendPronounceFeedbackToStudent(voca);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onEvaluateVocaGradeChanged(int callerUid, VocaStudyChat voca, String evaluateGrade, String feedbackMessage) {
        if (mainTutor != null && mainTutor.getUid() == callerUid) {
            voca.setEvaluateVocaGrade(evaluateGrade);
        } else {
            voca.setEvaluateVocaGradeTutors(evaluateGrade);
        }
        voca.setDisplayEvaluation(true);
        voca.setFeedbackMessage(feedbackMessage);
        notifyItemChanged(voca);

        if (rvVoca.getAdapter() != adapter && vocas != null) { // sync values of the same voca
            for (VocaStudyChat voca1 : vocas) {
                if (voca1.getVocaId() == voca.getVocaId() && voca1.getType() == voca.getType()) {
                    if (mainTutor != null && mainTutor.getUid() == callerUid) {
                        voca1.setEvaluateVocaGrade(evaluateGrade);
                    } else {
                        voca1.setEvaluateVocaGradeTutors(evaluateGrade);
                    }
                    voca1.setDisplayEvaluation(true);
                    voca1.setFeedbackMessage(feedbackMessage);
                    break;
                }
            }
        }
    }

    private void sendPronounceFeedbackToStudent(final VocaStudyChat voca) {
        if (mainStudent == null)
            return;
        application.getDalAiImpl().sendPronounceFeedbackToStudent(
                getStudyLang(),
                mainStudent.getUid(),
                (mainTutor != null && mainTutor.getUid() == uid) ? voca.getEvaluateVocaGrade() : voca.getEvaluateVocaGradeTutors(),
                voca.getFeedbackMessage(),
                voca.getFileVersion(),
                voca.getVocaId(),
                voca.getType(),
                voca.getVocaDisplay(),
                new DalApiListener<Boolean>() {

                    @Override
                    public void onSuccess(Boolean response) {
                        if (response) {
                            sendPronounceFeedbackToOthersInStudyMode(voca);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                }
        );
    }

    private void sendPronounceFeedbackToOthersInStudyMode(VocaStudyChat voca) {
        if (mainStudent == null)
            return;
        application.getDalAiImpl().sendPronounceFeedbackToOthersInStudyMode(
                getStudyLang(),
                chatRoomInfo.getFirestoreChatRoomId(),
                getLessonId(),
                mainStudent.getUid(),
                (mainTutor != null && mainTutor.getUid() == uid) ? voca.getEvaluateVocaGrade() : voca.getEvaluateVocaGradeTutors(),
                voca.getFeedbackMessage(),
                voca.getFileVersion(),
                voca.getVocaId(),
                voca.getType(),
                voca.getVocaDisplay(),
                voca.getTblSection(),
                voca.getTblRow(),
                voca.getPnType(),
                null
        );
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (isFromLessonList() && lesson.getId() == 0)
            return;
        if (successEvent.getScreen() == BaseEvent.Screen.ALL) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.NOTIFICATION_RECEIVED) {
                if (successEvent.getModel() instanceof Bundle) {
                    Bundle eventData = (Bundle) successEvent.getModel();
                    int chatRoomId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.CHATROOM_ID));
                    if (chatRoomId == chatRoomInfo.getFirestoreChatRoomId()) {
                        String methodName = eventData.getString(Constant.NOTIFICATION_KEY.METHOD_NAME, "");
                        switch (methodName) {
                            case Constant.NOTIFICATION_VALUE.SEND_VOCA_KNOW_TO_OTHERS_IN_STUDY_MODE:
                                onVocaKnowReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_PRONOUNCE_FEEDBACK_TO_OTHERS_IN_STUDY_MODE:
                                onFeedbackReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_VOCA_BOOK_IN_STUDY_MODE:
                                onVocaBookReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SHOW_ASTERISK_IN_STUDY_MODE:
                                onShowAsteriskReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.JOIN_STUDY_MODE_IN_CHAT_ROOM:
                                onJoinStudyReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.EXIT_STUDY_MODE_IN_CHAT_ROOM:
                                onExitStudyReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_MESSAGE_IN_STUDY_MODE:
                                onMessageReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SYNC_CELL_WITH_OTHER_USER_IN_STUDY_MODE:
                                onSyncACellReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SIDE_GLANCE_SENTENCE_IN_STUDY_MODE:
                                onSideGlanceReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_PUSH_TO_STUDENT_FINISH_STUDY_IN_STUDY_MODE:
                                onStudentFinishStudyReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SAVE_VOCABOOK_ID_IN_STUDY_MODE:
                                onSaveVocaBookIdReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.GET_EXAM_IN_STUDY_MODE:
                                onGetExamReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_SELECT_ANSWER_AT_EXAM_IN_STUDY_MODE:
                                onSelectedAnswerReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.MAKE_ROLE_PLAYING_CONTENTS_JSON:
                                onMakeRolePlayingContentsJsonReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_CHAT_MESSAGE_IN_STUDY_MODE:
                                onChatMessageReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SEND_GRAMMAR_WORKBOOK_IN_STUDY_MODE:
                                onGrammarWorkbookReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.GET_STUDY_USER_INFO_IN_CHAT_ROOM:
                                onRefreshUserListClick(false);
                                break;
                            case Constant.NOTIFICATION_VALUE.MAKE_READING_CONTENTS_JSON_IN_STUDY_MODE:
                                onReadingReceived(eventData);
                                break;
                            case Constant.NOTIFICATION_VALUE.SHOW_MEANING_ON_STUDENT_VIEW_IN_STUDY_MODE:
                                onShowStudentMeaningReceived(eventData);
                                break;
                        }
                    }
                }
            }
        } else if (successEvent.getScreen() == BaseEvent.Screen.STUDY_MODE_FRAGMENT) {
            DLog.d(getLogTag(), "SuccessEvent=" + successEvent.getScreen() + " - type=" + successEvent.getEventType());
            switch (successEvent.getEventType()) {
                case UPDATE_TITLE:
                    setTitleStudy(me);
                    break;
                case VOICE_CALL:
                    typeCall = (int) successEvent.getModel();
                    DLog.d(getLogTag(), "typeCall=" + typeCall);
                    checkTypeVoiceCall(false);
                    dismissVoiceDialog();
                    break;
                case CALL_REQUEST_START:
                    openVoiceInformationDialog(false);
                    break;
                case CALL_REQUEST_END:
                    checkTypeVoiceCall();
                    break;
                case CALL_REQUEST_JOIN:
                    openVoiceJoinDialog();
                    break;
            }
        }
    }

    private void onVocaKnowReceived(Bundle eventData) {
        int pnType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.PN_TYPE));
        int tblSection = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_SECTION));
        int tblRow = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_ROW));
        int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
        int vocaType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_TYPE));
        int vocaKnow = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_KNOW));
        int vocaKnowPronounce = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_KNOWPRONOUNCE));
        if (pnType == Constant.API_VALUE.PN_TYPE_STUDY_WORDS
                || pnType == Constant.API_VALUE.PN_TYPE_STUDY_SENTENCES
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT_ALL
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT_CONVERSATION
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT
                || pnType == Constant.API_VALUE.PN_TYPE_MSG_VOCA_LIST
                || pnType == Constant.API_VALUE.PN_TYPE_READING_VOCA_LIST) {
            if (recyclerViewDialog.isShowing() && recyclerViewDialog.getAdapter() instanceof StudyChatAdapter) {
                StudyChatAdapter adapter = (StudyChatAdapter) recyclerViewDialog.getAdapter();
                VocaStudyChat voca = (tblSection == -1 || tblRow == -1)
                        ? adapter.getVocaByVocaId(vocaId, vocaType)
                        : adapter.getVocaByIndex(tblSection, tblRow);
                if (voca != null) {
                    onVocaKnowChanged(voca, vocaKnow, vocaKnowPronounce);
                }
            } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                for (VocaStudyChat voca : vocaReading.getAllVocaList()) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        //
                        voca.setVocaKnow(vocaKnow);
                        voca.setVocaKnowPronounce(vocaKnowPronounce);
                        //
                        updateVocaDisplayRubyText(vocaId, vocaType, vocaKnow, vocaKnowPronounce);
                        break;
                    }
                }
            } else {
                updateVocaDisplayRubyText(vocaId, vocaType, vocaKnow, vocaKnowPronounce);
            }
        } else if (rvVoca.getAdapter() == rolePlayingAdapter) {
            List<Conversation> conversations = rolePlayingContent.getConversationList();
            if (tblSection == -1 || tblRow == -1) {
                for (Conversation conversation : conversations) {
                    for (VocaStudyChat voca : conversation.getSentences()) {
                        if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                            onVocaKnowChanged(voca, vocaKnow, vocaKnowPronounce);
                            return;
                        }
                    }
                }
            } else {
                if (!conversations.isEmpty()) {
                    if (tblSection < conversations.size()) {
                        Conversation conversation = conversations.get(tblSection);
                        List<VocaStudyChat> sentences = conversation.getSentences();
                        if (!sentences.isEmpty()) {
                            if (tblRow < sentences.size()) {
                                VocaStudyChat voca = sentences.get(tblRow);
                                if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                                    onVocaKnowChanged(voca, vocaKnow, vocaKnowPronounce);
                                }
                            }
                        }
                    }
                }
            }
        } else if (rvVoca.getAdapter() == examAdapter) {
            if (examVocas != null) {
                for (VocaStudyChat voca : examVocas) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        onVocaKnowChanged(voca, vocaKnow, vocaKnowPronounce);
                        break;
                    }
                }
            }
        } else if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
            StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
            VocaStudyChat voca = tblSection == -1 || tblRow == -1
                    ? adapter.getVocaByVocaId(vocaId, vocaType)
                    : adapter.getVocaByIndex(tblSection, tblRow);
            onVocaKnowChanged(voca, vocaKnow, vocaKnowPronounce);
        }
    }

    private void onFeedbackReceived(Bundle eventData) {
        int pnType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.PN_TYPE));
        int tblSection = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_SECTION));
        int tblRow = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_ROW));
        int callerUid = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.CALLER_UID));
        int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
        int vocaType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_TYPE));
        String evaluateGrade = eventData.getString(Constant.NOTIFICATION_KEY.EVALUATE_VOCA_GRADE);
        String feedbackMessage = eventData.getString(Constant.NOTIFICATION_KEY.FEEDBACK_MESSAGE);
        if (pnType == Constant.API_VALUE.PN_TYPE_STUDY_WORDS
                || pnType == Constant.API_VALUE.PN_TYPE_STUDY_SENTENCES
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT_ALL
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT_CONVERSATION
                || pnType == Constant.API_VALUE.PN_TYPE_RUBY_TEXT
                || pnType == Constant.API_VALUE.PN_TYPE_MSG_VOCA_LIST
                || pnType == Constant.API_VALUE.PN_TYPE_READING_VOCA_LIST) {
            if (recyclerViewDialog.isShowing() && recyclerViewDialog.getAdapter() instanceof StudyChatAdapter) {
                StudyChatAdapter adapter = (StudyChatAdapter) recyclerViewDialog.getAdapter();
                VocaStudyChat voca = (tblSection == -1 || tblRow == -1)
                        ? adapter.getVocaByVocaId(vocaId, vocaType)
                        : adapter.getVocaByIndex(tblSection, tblRow);
                if (voca != null) {
                    onEvaluateVocaGradeChanged(callerUid, voca, evaluateGrade, feedbackMessage);
                }
            }
        } else if (rvVoca.getAdapter() == rolePlayingAdapter) {
            if (rolePlayingContent != null) {
                List<Conversation> conversations = rolePlayingContent.getConversationList();
                if (tblSection == -1 || tblRow == -1) {
                    for (Conversation conversation : conversations) {
                        for (VocaStudyChat voca : conversation.getSentences()) {
                            if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                                onEvaluateVocaGradeChanged(callerUid, voca, evaluateGrade, feedbackMessage);
                                return;
                            }
                        }
                    }
                } else {
                    if (!conversations.isEmpty()) {
                        if (tblSection < conversations.size()) {
                            Conversation conversation = conversations.get(tblSection);
                            List<VocaStudyChat> sentences = conversation.getSentences();
                            if (!sentences.isEmpty()) {
                                if (tblRow < sentences.size()) {
                                    VocaStudyChat voca = sentences.get(tblRow);
                                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                                        onEvaluateVocaGradeChanged(callerUid, voca, evaluateGrade, feedbackMessage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (rvVoca.getAdapter() == examAdapter) {
            if (examVocas != null) {
                for (VocaStudyChat voca : examVocas) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        onEvaluateVocaGradeChanged(callerUid, voca, evaluateGrade, feedbackMessage);
                        break;
                    }
                }
            }
        } else if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
            StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
            VocaStudyChat voca = tblSection == -1 || tblRow == -1
                    ? adapter.getVocaByVocaId(vocaId, vocaType)
                    : adapter.getVocaByIndex(tblSection, tblRow);
            onEvaluateVocaGradeChanged(callerUid, voca, evaluateGrade, feedbackMessage);
        }
    }

    private void onVocaBookReceived(Bundle eventData) {
        activity.stopPlayVoca();

        int lessonMode = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.LESSON_MODE));
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
            onReadingReceived(eventData);
        } else if ((lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL || lessonMode == Constant.API_VALUE.LESSON_MODE_REVIEW)
                && this.lessonMode != lessonMode) {
            this.lessonMode = lessonMode;
            adapter = null;
            getVocas(false, true, true);
        } else {
            String strBookId = eventData.getString(Constant.NOTIFICATION_KEY.VOCABOOKS_ID);
            final int bookId = Utils.parseInt(strBookId);
            if (bookId > 0) {
                final int topicBeginIndex = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TOPIC_BEGIN_INDEX));
                final int topicRepeatedCount = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TOPIC_REPEATED_COUNT));
                rvVoca.post(() -> {
                    Loading.show(context);
                    studyInfo.getVocaBook().setTopicBeginIndex(topicBeginIndex);
                    studyInfo.getVocaBook().setTopicRepeatedCount(topicRepeatedCount);
                    updateVocabookForStudy(bookId, false);
                    SERVER_VOCABOOKS serverVocabook = serverVocabooks.get(bookPos);
                    showToastBookChanged(serverVocabook);
                });
            }
        }
    }

    private void onShowAsteriskReceived(Bundle eventData) {
        int showAsterisk = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.SHOW_ASTERISK));
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
            onStudyOrderInReadingModeChanged(showAsterisk);
        } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
            onStudyOrderInRolePlayingModeChanged(showAsterisk);
        } else {
            onShowAsteriskChanged(showAsterisk, true);
        }
    }

    private void onJoinStudyReceived(Bundle eventData) {
        final String userName = eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
        application.getDalAiImpl().getStudyUserInfoInChatroom(
                chatRoomInfo.getFirestoreChatRoomId(),
                getLessonId(),
                false,
                new DalApiListener<ChatRoomInfo>() {

                    @Override
                    public void onSuccess(ChatRoomInfo response) {
                        studyInfo = response;
                        setMe(studyInfo);
                        activity.setStudyUserListToPopup(studyInfo);
                        boolean flag = false;
                        if (mainStudent == null) {
                            flag = setMainStudent();
                        }
                        if (mainTutor == null) {
                            flag = setMainTutor();
                        }
                        if (flag) {
                            if (!TextUtils.isEmpty(userName)) {
                                ToastUtil.getInstance(context).show( String.format(tplJoinALesson, userName));
                            }
                            if (me == null || me.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
                                activity.getPlayVocaHelper().stop();
                            } else {
                                studyChatPlayVocaHelper.stop();
                            }
                            notifyMainUserJoinOrExit();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                });
    }

    private void onExitStudyReceived(Bundle eventData) {
        int callerUid = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.CALLER_UID));
        final String userName = eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
        final boolean mainStudentExit = mainStudent != null && mainStudent.getUid() == callerUid;
        if (mainStudentExit) {
            mainStudent = null;
        }
        final boolean mainTutorExit = mainTutor != null && mainTutor.getUid() == callerUid;
        if (mainTutorExit) {
            mainTutor = null;
        }
        application.getDalAiImpl().getStudyUserInfoInChatroom(
                chatRoomInfo.getFirestoreChatRoomId(),
                getLessonId(),
                false,
                new DalApiListener<ChatRoomInfo>() {

                    @Override
                    public void onSuccess(ChatRoomInfo response) {
                        studyInfo = response;
                        activity.setStudyUserListToPopup(studyInfo);
                        if (mainStudentExit || mainTutorExit) {
                            if (mainStudentExit) {
                                setMainStudent();
                            }
                            if (mainTutorExit) {
                                setMainTutor();
                            }
                            notifyMainUserJoinOrExit();
                            ToastUtil.getInstance(context).show( String.format(tplExitALesson, userName));
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                });
    }

    private void notifyMainUserJoinOrExit() {
        if (rvVoca.getAdapter() != null) {
            if (rvVoca.getAdapter() == adapter) {
                adapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
                adapter.notifyDataSetChanged();
            } else if (rvVoca.getAdapter() == examAdapter) {
                examAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
                examAdapter.notifyDataSetChanged();
            } else if (rvVoca.getAdapter() == rolePlayingAdapter) {
                rolePlayingAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
                rolePlayingAdapter.notifyDataSetChanged();
            } else if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
                ((StudyChatAdapter) rvVoca.getAdapter()).setStudentAndTutorJoined(isStudentAndTutorJoined);
                rvVoca.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void onMessageReceived(Bundle eventData) {
        String message = eventData.getString(Constant.NOTIFICATION_KEY.MESSAGE_MODIFIED);
        if (TextUtils.isEmpty(message)) {
            String messageId = eventData.getString(Constant.NOTIFICATION_KEY.MESSAGE_ID);
            for (Object object : messages) {
                if (object instanceof TBL_MESSAGE) {
                    TBL_MESSAGE tblMessage = (TBL_MESSAGE) object;
                    if (tblMessage.getID().equals(messageId)) {
                        message = tblMessage.generateMeaningContent();
                        break;
                    }
                }
            }
        }
        onMessageChanged(message);
    }

    private void onSyncACellReceived(Bundle eventData) {
        int tblParentSection = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_PARENT_SECTION));
        int tblParentRow = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_PARENT_ROW));
        int tblSection = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_SECTION));
        int tblRow = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_ROW));
        int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
        int vocaType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_TYPE));
        int typeSync = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.TBL_TYPE_SYNC));
        if (typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_ALL_SENTENCE_LIST
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_MSG_VOCA_LIST
                || typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_READING_VOCA_LIST) {
            boolean byId = tblSection == -1 || tblRow == -1;
            syncCellInDialogBy(typeSync, tblParentSection, tblParentRow, byId, byId ? vocaType : tblSection, byId ? vocaId : tblRow);
        } else if (rvVoca.getAdapter() == rolePlayingAdapter) {
            if (typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_CONTENT_ID_1) {
                rvContent.post(() -> {
                    boolean checked = "1".equals(eventData.getString(Constant.NOTIFICATION_KEY.IS_CELL_CHECKED));
                    int countOfOrder = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.COUNT_OF_ORDER_FROM_CHECKED_MENU));
                    int pos = rolePlayingContentAdapter.notifyItemChanged(tblSection, tblRow, checked, countOfOrder);
                    if (pos > -1) {
                        contentCenterLayoutManager.smoothScrollToPosition(rvContent, null, pos);
                    }
                });
            } else if (typeSync == Constant.API_VALUE.TBL_TYPE_SYNC_CONVERSATION_LIST) {
                if (tblRow > -1) {
                    List<Conversation> conversations = rolePlayingContent.getConversationList();
                    if (!conversations.isEmpty()) {
                        if (tblSection < conversations.size()) {
                            Conversation conversation = conversations.get(tblRow);
                            String branchSelected = eventData.getString(Constant.NOTIFICATION_KEY.BRANCH_SELECTED);
                            boolean isBranchSelected = !TextUtils.isEmpty(branchSelected);
                            if (isBranchSelected) {
                                conversation.setBranchSelected(branchSelected);
                            }
                            blinkCell(conversation, isBranchSelected);
                        }
                    }
                }
            } else {
                if (rolePlayingContent == null) return;
                if (tblParentSection == -1) {
                    for (Conversation conversation : rolePlayingContent.getConversationList()) {
                        for (VocaStudyChat voca : conversation.getSentences()) {
                            if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                                blinkCell(voca);
                                return;
                            }
                        }
                    }
                } else {
                    List<Conversation> conversations = rolePlayingContent.getConversationList();
                    if (!conversations.isEmpty()) {
                        if (tblParentSection < conversations.size()) {
                            Conversation conversation = conversations.get(tblParentSection);
                            for (VocaStudyChat voca : conversation.getSentences()) {
                                if (voca.getTblSection() == tblSection && voca.getTblRow() == tblRow) {
                                    blinkCell(voca);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } else if (rvVoca.getAdapter() == examAdapter) {
            if (examVocas != null) {
                for (VocaStudyChat voca : examVocas) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        blinkCell(voca);
                        break;
                    }
                }
            }
        } else if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
            StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
            blinkCell(tblSection == -1 || tblRow == -1
                    ? adapter.getVocaByVocaId(vocaId, vocaType)
                    : adapter.getVocaByIndex(tblSection, tblRow));
        }
    }

    private void onSideGlanceReceived(Bundle eventData) {
        if (rvVoca.getAdapter() == examAdapter) {
            if (examVocas != null) {
                int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
                int vocaType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_TYPE));
                for (VocaStudyChat voca : examVocas) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        String message = String.format(
                                tplSideGlance,
                                eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME),
                                Voca.getVocaDisplay(voca)
                        );
                        onMessageChanged(message);
                        break;
                    }
                }
            }
        } else {
            if (vocas != null) {
                int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
                int vocaType = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_TYPE));
                for (VocaStudyChat voca : vocas) {
                    if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                        String message = String.format(
                                tplSideGlance,
                                eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME),
                                Voca.getVocaDisplay(voca)
                        );
                        onMessageChanged(message);
                        break;
                    }
                }
            }
        }
    }

    private void onStudentFinishStudyReceived(Bundle eventData) {
        openConfirmFinishStudyScreen(eventData);
    }

    private void onSaveVocaBookIdReceived(Bundle eventData) {
        int lessonMode = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.LESSON_MODE));
        int cellIndex = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCABOOKS_CELL_INDEX));
        if (lessonMode == this.lessonMode && cellIndex > 0) {
            String bookId = eventData.getString(Constant.NOTIFICATION_KEY.VOCABOOKS_ID);
            highlightCellIndex(cellIndex);
            String showToast = eventData.getString(Constant.NOTIFICATION_KEY.SHOW_TOAST);
            if (Utils.parseInt(showToast) == Constant.SHOW_TOAST_YES) {
                showToastBookSaved(bookId);
            }
        }
    }

    private void onClickSeparatorBar(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_lesson_mode:
                lessonModeDialog.show();
                break;
            case R.id.btn_topics:
                if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL) {
                    topicsDialog.show();
                } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                    readingsDialog.show();
                }
                break;
            case R.id.btn_msg:
                onMenuMessageClick();
                break;
            case R.id.btn_exam:
                examDialog.show();
                break;
            case R.id.btn_role_playing:
                changeRolePlayingDialog.show();
                break;
            case R.id.btn_grammar:
                grammarDialog.show();
                break;
            case R.id.ic_next:
                if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL) {
                    int pos = studyOrderPos;
                    if (++pos >= studyOrderList.size() + (vocaFromAllVocaBook.getStudyVocaList().isEmpty() ? 0 : 1) + (vocaFromAllVocaBook.getExamVocaList().isEmpty() ? 0 : 1)) {
                        pos = 0;
                    }
                    showAsteriskInStudyMode(pos);
                } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_REVIEW) {
                    int pos = studyOrderList.indexOf(showAsterisk);
                    if (pos > -1) {
                        if (++pos >= studyOrderList.size()) {
                            pos = 0;
                        }
                        showAsteriskInStudyMode(studyOrderList.get(pos));
                    }
                } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
                    int pos = studyOrderRolePlayingPos;
                    if (++pos >= Constant.STUDY_ORDER_ROLE_PLAYING_COUNT + rolePlayingContent.getAllSentenceListShowGuide() + rolePlayingContent.getAllVocaListShowGuide()) {
                        pos = 0;
                    }
                    sendStudyOrderInRolePlayingMode(pos);
                } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                    int pos = studyOrderPos;
                    if (++pos >= 1 + (vocaReading.getStudyVocaList().isEmpty() ? 0 : 1) + (vocaReading.getExamVocaList().isEmpty() ? 0 : 1)) {
                        pos = 0;
                    }
                    sendStudyOrderInReadingMode(pos);
                }
                break;
            default:
                break;
        }
    }

    public void onMenuMessageClick() {
        if (isMeObserver()) {
            confirmationDialog = new ConfirmationDialog(context, new ConfirmationDialog.OnDialogClickListener() {

                @Override
                public void onPositive(DialogInterface dialog) {
                    List<Object> messagesByStudyRole = Voca.getMessageListForLessonByStudyRole(enumStudyLanguage, enumDisplayLanguage, Constant.STUDY_ROLE_TUTOR);
                    messageListDialog.setAdapter(messagesByStudyRole, onMessageClickListener);
                    messageListDialog.show();
                    dialog.dismiss();
                }

                @Override
                public void onNegative(DialogInterface dialog) {
                    List<Object> messagesByStudyRole = Voca.getMessageListForLessonByStudyRole(enumStudyLanguage, enumDisplayLanguage, Constant.STUDY_ROLE_STUDENT);
                    messageListDialog.setAdapter(messagesByStudyRole, onMessageClickListener);
                    messageListDialog.show();
                    dialog.dismiss();
                }
            });
            confirmationDialog.setMyTitle(R.string.choose);
            confirmationDialog.setMessage(R.string.msg_observer_see_messages);
            confirmationDialog.setPositiveText(R.string.yes);
            confirmationDialog.setNegativeText(R.string.no);
            confirmationDialog.show();
        } else {
            messageListDialog.show();
        }
    }

    private void onIcPreviousOrNextClick(SERVER_VOCABOOKS serverVocabook) {
        int bookId = Utils.parseInt(serverVocabook.getID());
        onBookWillChange(bookId);
        showToastBookChanged(serverVocabook);
    }

    private void onBookWillChange(final int bookId) {
        if (isMeObserver()) {
            VocaBookInChat book = new VocaBookInChat();
            book.setVocaBookId(bookId);
            book.setVocaBookType(Constant.API_VALUE.VALUE_SERVER_BOOK);
            book.setLangStudy(enumStudyLanguage.getFormatApi());
            studyInfo.setVocaBook(book);
            updateBookPos(false); // hasCount = false here because we didn't load voca list yet.
            adapter = null;
            getVocas(true, true, true);
        } else if (mainTutor != null && mainStudent != null) {
            saveVocabookIDInStudyMode(bookId, 0, Constant.SHOW_TOAST_NO, new DalApiListener<Boolean>() {

                @Override
                public void onSuccess(Boolean response) {
                    if (response) {
                        sendVocabookInStudyMode(bookId, true);
                    } else {
                        Loading.hide();
                    }
                }

                @Override
                public void onFailure(String error) {
                    Loading.hide();
                }
            });
        } else {
            sendVocabookInStudyMode(bookId, true);
        }
    }

    private DialogInterface.OnClickListener onTopicsDialogItemClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // TODO: maybe bring back later
                /*case R.id.v_finish_study:
                    updateFinishStudiedBook();
                    openFinishStudyScreen();
                    break;*/
                case R.id.tv_move_to_next_book:
                    int newRepeatedCount, newBeginIndex, newBookPos;
                    if (studyInfo.getVocaBook().getTopicBeginIndex() + 1 < lessonOption.getRepeatTopics()) {
                        if (bookPos < serverVocabooks.size() - 1) {
                            newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount();
                            newBeginIndex = studyInfo.getVocaBook().getTopicBeginIndex() + 1;
                            newBookPos = bookPos + 1;
                        } else {
                            newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount() + 1;
                            newBeginIndex = 0;
                            newBookPos = bookPos - studyInfo.getVocaBook().getTopicBeginIndex();
                        }
                    } else if (studyInfo.getVocaBook().getTopicRepeatedCount() + 1 < lessonOption.getRepeatCount()) {
                        newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount() + 1;
                        newBeginIndex = 0;
                        newBookPos = bookPos - studyInfo.getVocaBook().getTopicBeginIndex();
                    } else {
                        newRepeatedCount = 0;
                        newBeginIndex = 0;
                        newBookPos = bookPos + 1;
                    }
                    if (newBookPos < serverVocabooks.size() - 1) {
                        studyInfo.getVocaBook().setTopicRepeatedCount(newRepeatedCount);
                        studyInfo.getVocaBook().setTopicBeginIndex(newBeginIndex);
                        onIcPreviousOrNextClick(serverVocabooks.get(newBookPos));
                    } else {
                        ToastUtil.getInstance(context).show( R.string.msg_last_topic);
                    }
                    break;
                case R.id.tv_move_to_previous_book:
                    if (studyInfo.getVocaBook().getTopicBeginIndex() - 1 >= 0) {
                        if (bookPos > 0) {
                            newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount();
                            newBeginIndex = studyInfo.getVocaBook().getTopicBeginIndex() - 1;
                            newBookPos = bookPos - 1;
                        } else {
                            newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount() - 1;
                            newBeginIndex = lessonOption.getRepeatTopics() - 1;
                            newBookPos = bookPos + newBeginIndex;
                        }
                    } else if (studyInfo.getVocaBook().getTopicRepeatedCount() - 1 >= 0) {
                        newRepeatedCount = studyInfo.getVocaBook().getTopicRepeatedCount() - 1;
                        newBeginIndex = lessonOption.getRepeatTopics() - 1;
                        newBookPos = bookPos + newBeginIndex;
                    } else {
                        newRepeatedCount = 0;
                        newBeginIndex = 0;
                        newBookPos = bookPos - 1;
                    }
                    if (newBookPos >= 0) {
                        studyInfo.getVocaBook().setTopicRepeatedCount(newRepeatedCount);
                        studyInfo.getVocaBook().setTopicBeginIndex(newBeginIndex);
                        onIcPreviousOrNextClick(serverVocabooks.get(newBookPos));
                    } else {
                        ToastUtil.getInstance(context).show( R.string.msg_first_topic);
                    }
                    break;
                case R.id.tv_sync_current_topic:
                    sendVocabookInStudyMode(studyInfo.getVocaBook().getVocaBookId(), true);
                    break;
                case R.id.tv_select_wordbook:
                    openSelectWordbookScreen();
                    break;
                case R.id.tv_save_as_a_last_wordbook:
                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_YES, null);
                    break;
                default:
                    break;
            }
        }
    };

    private void openFinishStudyScreen() {
        Intent intent = new Intent(context, ActivityFinishStudy.class);
        intent.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
        intent.putExtra(Constant.BUNDLE.KEY_STUDIED_BOOKS, studiedBooks);
        intent.putExtra(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, studyInfo.getFirestoreChatRoomId());
        if (mainStudent != null) {
            intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, mainStudent.getUid());
        }
        activity.openNewScreen(intent);
    }

    private void openConfirmFinishStudyScreen(Bundle eventData) {
        Intent intent = new Intent(context, ActivityConfirmFinishStudy.class);
        intent.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
        intent.putExtra(Constant.BUNDLE.KEY_EVENT_DATA, eventData);
        intent.putExtra(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, studyInfo.getFirestoreChatRoomId());
        if (mainStudent != null) {
            intent.putExtra(Constant.BUNDLE.KEY_STUDENT_ID, mainStudent.getUid());
        }
        if (mainTutor != null) {
            intent.putExtra(Constant.BUNDLE.KEY_TUTOR_ID, mainTutor.getUid());
        }
        activity.openNewScreen(intent);
    }

    private void openSelectWordbookScreen() {
        ArrayList<Integer> parentBookIds = Voca.getParentBookIds(studyInfo.getVocaBook().getVocaBookId());
        Intent intent = new Intent(context, WordbookByCategoryActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_STUDY_LANG, getStudyLang());
        intent.putExtra(Constant.BUNDLE.KEY_PARENT_BOOK_ID_LIST, parentBookIds);
        intent.putExtra(Constant.BUNDLE.KEY_AUTO_MOVE, true);
        activity.openNewScreenForResult(intent, Constant.REQUEST_CODE.SELECT_BOOK);
    }

    private DialogInterface.OnClickListener onExamDialogItemClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case R.id.tv_from_all_phrases:
                    selectVocasToMakeAnExam(which, vocas);
                    break;
                case R.id.tv_except_known_and_excellent:
                case R.id.tv_except_known:
                case R.id.tv_except_excellent:
                    if (examVocas != null && examVocas.size() == Constant.EXAM_WORD_COUNT_MAX
                            && vocas != null && vocas.size() > Constant.EXAM_WORD_COUNT_MAX) {
                        confirmationDialog = new ConfirmationDialog(
                                context,
                                R.string.info, R.string.msg_reuse_exam_list, R.string.yes, R.string.no,
                                new ConfirmationDialog.OnDialogClickListener() {

                                    @Override
                                    public void onPositive(DialogInterface dialog) {
                                        dialog.dismiss();
                                        selectVocasToMakeAnExam(which, examVocas);
                                    }

                                    @Override
                                    public void onNegative(DialogInterface dialog) {
                                        dialog.dismiss();
                                        selectVocasToMakeAnExam(which, vocas);
                                    }
                                }
                        );
                        confirmationDialog.show();
                    } else {
                        selectVocasToMakeAnExam(which, vocas);
                    }
                    break;
                case R.id.tv_from_memorization_targets:
                    makeAnExamInStudyMode(Constant.EXAM_SOURCE_MEMORIZATION_TARGETS, "", "");
                    break;
                case R.id.tv_shuffle_this_exam:
                    shuffleThisExamInStudyMode();
                    break;
                default:
                    break;
            }
        }
    };

    private void selectVocasToMakeAnExam(int which, List vocas) {
        StringBuilder vocaIds = new StringBuilder();
        StringBuilder vocaTypes = new StringBuilder();
        int count = 0;
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < vocas.size(); i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        for (int i = 0; i < vocas.size() && i < Constant.EXAM_WORD_COUNT_MAX; i++) {
            VocaStudyChat voca = (VocaStudyChat) vocas.get(indexes.get(i));
            if (canSelectToMakeAnExam(which, voca)) {
                vocaIds.append(",").append(voca.getVocaId());
                vocaTypes.append(",").append(voca.getType());
                count++;
            }
        }
//        if (count < 4) {
//            alertDialog.show(R.string.msg_need_4_words, R.string.ok, null);
//        } else {
        makeAnExamInStudyMode(Constant.EXAM_SOURCE_CURRENT_WORKBOOK, vocaIds.substring(1), vocaTypes.substring(1));
//        }
    }

    private boolean canSelectToMakeAnExam(int which, VocaStudyChat voca) {
        if (which == R.id.tv_except_known_and_excellent) {
            return !(voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && (voca.getEvaluateVocaGradeValue().equals(Constant.EVALUATE.GRADE_A)));
        }
        if (which == R.id.tv_except_known)
            return voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
        if (which == R.id.tv_except_excellent)
            return !(voca.getEvaluateVocaGradeValue().equals(Constant.EVALUATE.GRADE_A));
        return true;
    }

//    private boolean canSelectToMakeAnExam(int which, VocaStudyChat voca) {
//        if (which == R.id.tv_except_known_and_excellent)
//            return voca.getAmkiGrade() != Constant.AMKI_GRADE.VALUE_KNOWN
//                    || voca.getKnow() != Constant.KNOW.KNOWN
//                    || !Constant.EVALUATE.GRADE_A.equals(voca.getEvaluateVocaGradeValue());
//        if (which == R.id.tv_except_known)
//            return voca.getAmkiGrade() != Constant.AMKI_GRADE.VALUE_KNOWN
//                    || voca.getKnow() != Constant.KNOW.KNOWN;
//        if (which == R.id.tv_except_excellent)
//            return !Constant.EVALUATE.GRADE_A.equals(voca.getEvaluateVocaGradeValue());
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_CODE.SELECT_BOOK:
                    if (data != null) {
                        int bookId = data.getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0);
                        if (bookId > 0 && bookId != studyInfo.getVocaBook().getVocaBookId()) {
                            studyInfo.getVocaBook().setTopicRepeatedCount(0);
                            studyInfo.getVocaBook().setTopicBeginIndex(0);
                            onBookWillChange(bookId);
                        }
                    }
                    break;
                case Constant.REQUEST_CODE.STUDY_OPTION:
                    application.getDalAiImpl().getLessonOption(lesson.getStudentId(), Constant.API_VALUE.LIST_LESSON_FOR_STUDENT, new DalApiListener<LessonOption>() {

                        @Override
                        public void onSuccess(LessonOption response) {
                            lessonOption = response;
                            initSelectedLevelPos();
                            initStudyOrderList();
                            activity.setPendingFinishLessonToast(lessonOption);
                            lessonMode = Constant.API_VALUE.LESSON_MODE_NORMAL;
                            adapter = null;
                            getVocas(true, true, true);
                            saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_NO, null);
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void sendVocabookInStudyMode(final int bookId, final boolean notify) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().sendVocabookInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        bookId,
                        Constant.API_VALUE.VALUE_SERVER_BOOK,
                        studyInfo.getVocaBook().getTopicBeginIndex(),
                        studyInfo.getVocaBook().getTopicRepeatedCount(),
                        lessonMode,
                        lessonReadingId,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response && notify) {
                                    updateVocabookForStudy(bookId, true);
                                } else {
                                    Loading.hide();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void updateVocabookForStudy(int bookId, boolean makeNewExam) {
        updateFinishStudiedBook();
        VocaBookInChat book = new VocaBookInChat();
        book.setVocaBookId(bookId);
        book.setVocaBookType(Constant.API_VALUE.VALUE_SERVER_BOOK);
        book.setLangStudy(enumStudyLanguage.getFormatApi());
        book.setTopicRepeatedCount(studyInfo.getVocaBook().getTopicRepeatedCount());
        book.setTopicBeginIndex(studyInfo.getVocaBook().getTopicBeginIndex());
        studyInfo.setVocaBook(book);
        updateStartStudiedBook();
        updateBookPos(false); // hasCount = false here because we didn't load voca list yet.
        showSentence = Constant.SHOW_SENTENCE_ALL;
        adapter = null;
        getVocas(makeNewExam, true, false);
    }

    private void saveVocabookIDInStudyMode(final int bookId, final int cellIndex, final int showToast, DalApiListener<Boolean> listener) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().saveVocabookIDInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        bookId,
                        Constant.API_VALUE.VALUE_SERVER_BOOK,
                        cellIndex,
                        showToast,
                        studyInfo.getVocaBook().getTopicBeginIndex(),
                        studyInfo.getVocaBook().getTopicRepeatedCount(),
                        lessonMode,
                        grammarId,
                        lessonReadingId,
                        listener != null ? listener : new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    highlightCellIndex(cellIndex);
                                    if (showToast == Constant.SHOW_TOAST_YES) {
                                        if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                                            showToastReadingSaved();
                                        } else {
                                            showToastBookSaved(String.valueOf(bookId));
                                        }
                                    }
                                }
                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void highlightCellIndex(final int cellIndex) {
        if (cellIndex > 0 && rvVoca.getAdapter() == adapter) {
            rvVoca.post(() -> {
                int pos = adapter.setHighlightIndexAndNotify(cellIndex);
                if (pos > -1) {
                    centerLayoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            });
        }
    }

    private void showToastBookSaved(String bookId) {
        for (SERVER_VOCABOOKS serverVocabook : serverVocabooks) {
            if (serverVocabook.getID().equals(bookId)) {
                final String message = String.format(tplSavedAsALastWordbook, serverVocabook.getName(enumDisplayLanguage));
                rvVoca.post(() -> ToastUtil.getInstance(context).show( message));
                break;
            }
        }
    }

    private void initStudyOrderList() {
        studyOrderList.clear();
        if (lessonOption != null) {
            studyOrderList.add(Constant.SHOW_ASTERISK.SHOW_SENTENCE);
            if (lessonOption.getStudyOrderHidePartOfWords() == Constant.API_VALUE.IS_YES) {
                studyOrderList.add(Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY);
            }
            if (lessonOption.getStudyOrderHideAllPhrases() == Constant.API_VALUE.IS_YES) {
                studyOrderList.add(Constant.SHOW_ASTERISK.HIDE_SENTENCE);
            }
            if (lessonOption.getStudyOrderRandomQuestions() == Constant.API_VALUE.IS_YES) {
                studyOrderList.add(Constant.SHOW_ASTERISK_ALL_QUESTIONS);
            }
        }
    }

    private void showStudyOrderAlert(boolean isSecondRoundOrMore) {
        if (lessonOption != null) {
            int total = studyOrderList.size();
            int pos = studyOrderPos;
            if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL) {
                total += (vocaFromAllVocaBook.getStudyVocaList().isEmpty() ? 0 : 1) + (vocaFromAllVocaBook.getExamVocaList().isEmpty() ? 0 : 1);
                pos -= vocaFromAllVocaBook.getStudyVocaList().isEmpty() ? 0 : 1;
            } // else means LESSON_MODE_REVIEW
            if (pos == -1) {
                alertDialog.show(
                        studyOrderTitle = activity.getString(R.string.preview_words_list),
                        studyOrderMessage = activity.getString(R.string.preview_voca_list_guide_message, studyOrderPos + 1, total),
                        activity.getString(R.string.ok),
                        null
                );
            } else if (pos < studyOrderList.size()) {
                int countOfPhrases = lessonOption.getCountOfPhrasesToStudyAtOnce();
                int title, message;
                if (showAsterisk == Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
                    title = R.string.study_order_1_title;
                    message = R.string.study_order_1_message;
                    String combination;
                    if (isSecondRoundOrMore) {
                        combination = activity.getString(R.string.study_order_combination_message_2, countOfPhrases, countOfPhrases);
                    } else {
                        combination = activity.getString(R.string.study_order_combination_message_1, countOfPhrases);
                    }
                    alertDialog.show(
                            studyOrderTitle = activity.getString(title),
                            studyOrderMessage = activity.getString(message, studyOrderPos + 1, total, combination),
                            activity.getString(R.string.ok),
                            null
                    );
                } else {
                    if (showAsterisk == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY) {
                        title = R.string.study_order_2_title;
                        message = R.string.study_order_2_message;
                    } else if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
                        title = R.string.study_order_3_title;
                        message = R.string.study_order_3_message;
                    } else {
                        title = R.string.study_order_4_title;
                        message = R.string.study_order_4_message;
                    }
                    alertDialog.show(
                            studyOrderTitle = activity.getString(title),
                            studyOrderMessage = activity.getString(message, studyOrderPos + 1, total, countOfPhrases),
                            activity.getString(R.string.ok),
                            null
                    );
                }
            } else {
                alertDialog.show(
                        studyOrderTitle = activity.getString(R.string.exam_words_list),
                        studyOrderMessage = activity.getString(R.string.exam_voca_list_guide_message, studyOrderPos + 1, total),
                        activity.getString(R.string.ok),
                        null
                );
            }
        }
    }

    private void showAsteriskInStudyMode(int showAsterisk) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().showAsteriskInStudyMode(
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        showAsterisk,
                        null
                );
                onShowAsteriskChanged(showAsterisk, true);
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onShowAsteriskChanged(int showAsterisk, boolean isSecondRoundOrMore) {
        if (isSecondRoundOrMore) {
            for (VocaStudyChat voca : vocaFromAllVocaBook.getStudyVocaList()) {
                voca.setDisplayEvaluation(false);
            }
            for (VocaStudyChat voca : (vocaFromAllVocaBook.getAllVocaList())) {
                voca.setDisplayEvaluation(false);
            }
            for (VocaStudyChatExam voca : vocaFromAllVocaBook.getExamVocaList()) {
                voca.setDisplayEvaluation(false);
                voca.setAnswer1Selected(false);
                voca.setAnswer2Selected(false);
                voca.setAnswer3Selected(false);
                voca.setAnswer4Selected(false);
                voca.setLastSelectedAnswer(0);
                voca.setHasWrongAnswer(false);
            }
        }

        studyOrderPos = showAsterisk;
        int pos = showAsterisk;
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL) {
            pos -= vocaFromAllVocaBook.getStudyVocaList().isEmpty() ? 0 : 1;
        } // else means LESSON_MODE_REVIEW
        if (pos == -1) {
            this.showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
            rvVoca.post(() -> {
                setRecyclerViewPreviewOrExamData(vocaFromAllVocaBook.getStudyVocaList());
                showStudyOrderAlert(isSecondRoundOrMore);
            });
        } else if (pos < studyOrderList.size()) {
            this.showAsterisk = pos;
            rvVoca.post(() -> {
                if (adapter == null) {
                    setRecyclerViewData();
                } else {
                    adapter.setShowAsterisk(this.showAsterisk);
                    if (rvVoca.getAdapter() == adapter) {
                        adapter.notifyDataSetChanged();
                    } else {
                        rvVoca.setAdapter(adapter);
                    }
                }
                showStudyOrderAlert(isSecondRoundOrMore);
            });
        } else {
            this.showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
            rvVoca.post(() -> {
                setRecyclerViewPreviewOrExamData(new ArrayList<>(vocaFromAllVocaBook.getExamVocaList()));
                showStudyOrderAlert(isSecondRoundOrMore);
            });
        }
    }

    private DialogInterface.OnClickListener onLessonModeClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_normal_lesson_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_NORMAL) {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_NORMAL;
                    adapter = null;
                    getVocas(true, true, false);
                    sendVocabookInStudyMode(studyInfo.getVocaBook().getVocaBookId(), false);
                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_NO, null);
                }
                break;
            case R.id.tv_exam_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_EXAM) {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_EXAM;
                    selectVocasToMakeAnExam(R.id.tv_from_all_phrases, vocas);
                }
                break;
            case R.id.tv_role_playing_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
                    getRolePlayingCategoryList(1);
                }
                break;
            case R.id.tv_grammar_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_GRAMMAR) {
                    getGrammarList(0);
                }
                break;
            case R.id.tv_reading_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_READING) {
                    getReadingList(0);
                }
                break;
            case R.id.tv_review_mode:
                if (lessonMode != Constant.API_VALUE.LESSON_MODE_REVIEW) {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_REVIEW;
                    adapter = null;
                    getVocas(true, true, false);
                    sendVocabookInStudyMode(studyInfo.getVocaBook().getVocaBookId(), false);
                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_NO, null);
                }
                break;
            default:
                break;
        }
    };

    private OnClickListener onMessageClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            messageListDialog.dismiss();
            TBL_MESSAGE data = (TBL_MESSAGE) object;
            String message = data.generateMeaningContent();
            sendMessageInStudyMode(data, "");
            sendChatMessageInStudyMode(message);
            // TODO: Will use this confirmModifyMessageDialog later.
//            confirmModifyMessageDialog.show(object);
        }
    };

    private OnClickListener onConfirmModifyMessageListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            int id = view.getId();
            if (id == R.id.tv_yes) {
                sendMessageInStudyMode((TBL_MESSAGE) object, "");
            } else if (id == R.id.tv_no) {
                typeMessageDialog.show(object);
            }
        }
    };

    private BaseDialogListener onTypeMessageClickListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object object) {
            typeMessageDialog.dismiss();
            ArrayList<Object> data = (ArrayList<Object>) object;
            sendMessageInStudyMode((TBL_MESSAGE) data.get(0), (String) data.get(1));
            typeMessageDialog.setInput("");
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private void sendMessageInStudyMode(TBL_MESSAGE message, String modifiedMessage) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().sendMessageInStudyMode(
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        message.getID(),
                        modifiedMessage,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    onMessageChanged(message.generateMeaningContent());
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onMessageChanged(final String message) {
        tvMessage.post(() -> tvMessage.setText(message));
    }

    private OnClickListener onSyncACellClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            if (object instanceof VocaStudyChat) {
                VocaStudyChat voca = (VocaStudyChat) object;
                int id = view.getId();
                if (id == R.id.tv_open) {
                    Intent intent;
                    intent = new Intent(context, WordInfoActivity.class);
                    intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
                    intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getType());

                    activity.openNewScreen(intent);
                } else if (id == R.id.tv_sync) {
                    blinkCell(voca);
                    syncCellWithOtherUsersInStudyMode(voca);
                } else if (id == R.id.tv_change_word_known_status) {
                    getRubyVocaList(voca, null);
                } else if (id == R.id.tv_play_this_phrase) {
                    playVoca(voca);
                } else if (id == R.id.tv_save_as_a_last_wordbook) {
                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), voca.getIndex(), Constant.SHOW_TOAST_YES, null);
                }
            }
        }
    };

    private void blinkCell(VocaStudyChat voca) {
        voca.setVIChecked(true);
        notifyItemChangedAndMoveToCenter(voca);
    }

    private void blinkCell(Conversation conversation, boolean isBranchSelected) {
        conversation.setChecked(true);
        notifyItemChangedAndMoveToCenter(conversation, isBranchSelected);
    }

    private void syncCellWithOtherUsersInStudyMode(final VocaStudyChat voca) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().syncCellWithOtherUsersInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        studyInfo.getVocaBook().getVocaBookId(),
                        studyInfo.getVocaBook().getVocaBookType(),
                        voca.getVocaId(),
                        voca.getType(),
                        voca.getTblParentSection(),
                        voca.getTblParentRow(),
                        voca.getTblSection(),
                        voca.getTblRow(),
                        voca.getTblTypeSync(),
                        Constant.API_VALUE.IS_CELL_CHECKED_NO,
                        0,
                        "",
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    onMessageChanged(String.format(lessonMessageSyncSuccess, voca.getIndex(), voca.getVocaDisplay()));
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                onMessageChanged(String.format(lessonMessageSyncFail, voca.getIndex(), voca.getVocaDisplay()));
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void sideGlanceSentenceInStudyMode(VocaStudyChat voca) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().sideGlanceSentenceInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        studyInfo.getVocaBook().getVocaBookId(),
                        studyInfo.getVocaBook().getVocaBookType(),
                        voca.getVocaId(),
                        voca.getType(),
                        voca.getIndex(),
                        null
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void makeAnExamInStudyMode(int examSource, String vocaIds, String vocaTypes) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().makeAnExamInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        examSource,
                        getStudentId(),
                        getStudentLangNative(),
                        getTutorId(),
                        vocaIds,
                        vocaTypes,
                        lessonOption.getUserWordExamType(),
                        new DalApiListener<List<VocaStudyChatExam>>() {

                            @Override
                            public void onSuccess(List<VocaStudyChatExam> response) {
                                Loading.hide();
                                onGetExamVocasSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getAnExamInStudyMode() {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getAnExamInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getStudentId(),
                        getTutorId(),
                        new DalApiListener<List<VocaStudyChatExam>>() {

                            @Override
                            public void onSuccess(List<VocaStudyChatExam> response) {
                                Loading.hide();
                                onGetExamVocasSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void shuffleThisExamInStudyMode() {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().shuffleThisExamInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        getStudentId(),
                        getTutorId(),
                        new DalApiListener<List<VocaStudyChatExam>>() {

                            @Override
                            public void onSuccess(List<VocaStudyChatExam> response) {
                                Loading.hide();
                                onGetExamVocasSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onGetExamVocasSuccess(List<VocaStudyChatExam> response) {
        examVocas = new ArrayList<>();
        if (response != null) {
            examVocas.addAll(response);
        }
        setRecyclerViewExamData(examVocas);
    }

    private void setRecyclerViewExamData(List<VocaStudyChatExam> examVocas) {
        activity.getPlayVocaHelper().stop();
        examAdapter = new StudyChatExamAdapter();
        examAdapter.setVocas(examVocas);
        examAdapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        examAdapter.setStudyRole(getMyStudyRole());
        examAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        examAdapter.setStudyLang(enumStudyLanguage.getIdApi());
        examAdapter.setUid(uid);
        examAdapter.setShowStudentMeaning(showStudentMeaning);
        examAdapter.setListener(onVocaStudyChatClickListener);
        examAdapter.setListener(onVocaStudyChatExamClickListener);
        rvVoca.setAdapter(examAdapter);
        if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
            rvVoca.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
            bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
        }
        showButtonsForExamMode();
    }

    private OnVocaStudyChatExamClickListener onVocaStudyChatExamClickListener = new OnVocaStudyChatExamClickListener() {

        @Override
        public void onPlayClick(VocaStudyChatExam voca) {
            onVocaStudyChatClickListener.onPlayClick(voca);
        }

        @Override
        public void onButtonClick(VocaStudyChatExam voca) {
            sendSelectAnswerAtExamInStudyMode(voca);
            if ((voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || Voca.isShow4Buttons(voca.getVocaKnow()))
                    && voca.getLastSelectedAnswer() != voca.getCorrectAnswerNumber()) {
                onVocaStudyChatClickListener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
            }
        }

        @Override
        public void onBigIconClick(VocaStudyChatExam voca) {
            onVocaStudyChatClickListener.onBigIconClick(voca);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChatExam voca, String grade) {
            onVocaStudyChatClickListener.onEvaluateGradeClick(voca, grade);
        }
    };

    private void sendSelectAnswerAtExamInStudyMode(VocaStudyChatExam voca) {
        if (uid > 0 && Utils.isConnected(context)) {
            application.getDalAiImpl().sendSelectAnswerAtExamInStudyMode(
                    chatRoomInfo.getFirestoreChatRoomId(),
                    getLessonId(),
                    voca.getVocaId(),
                    voca.getType(),
                    voca.getIndex(),
                    voca.getLastSelectedAnswer(),
                    voca.getLastSelectedAnswer() == voca.getCorrectAnswerNumber() ? 1 : 0,
                    null
            );
        }
    }

    private void onGetExamReceived(Bundle eventData) {
        rvVoca.post(this::getAnExamInStudyMode);
    }

    private void onSelectedAnswerReceived(Bundle eventData) {
        List<VocaStudyChatExam> vocas = getExamVocas();
        if (vocas != null) {
            int vocaId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.VOCA_ID));
            for (VocaStudyChatExam examVoca : vocas) {
                if (examVoca.getVocaId() == vocaId) {
                    int lastSelectedAnswer = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.SELECTED_ANSWER_NUMBER));
                    if (lastSelectedAnswer == 1) {
                        examVoca.setAnswer1Selected(true);
                    } else if (lastSelectedAnswer == 2) {
                        examVoca.setAnswer2Selected(true);
                    } else if (lastSelectedAnswer == 3) {
                        examVoca.setAnswer3Selected(true);
                    } else if (lastSelectedAnswer == 4) {
                        examVoca.setAnswer4Selected(true);
                    }
                    examVoca.setLastSelectedAnswer(lastSelectedAnswer);
                    notifyItemChangedAndMoveToCenter(examVoca);
                    break;
                }
            }
        }
    }

    private void showButtonsForStudyMode() {
        btnTopics.setVisibility(View.VISIBLE);
        btnExam.setVisibility(View.GONE);
        btnRolePlaying.setVisibility(View.GONE);
        btnGrammar.setVisibility(View.GONE);
        rvContent.setVisibility(View.GONE);
        vAdjustHeight.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        rvVoca.setVisibility(View.VISIBLE);
        wvGrammar.setVisibility(View.GONE);
        vReading.setVisibility(View.GONE);
    }

    private void showButtonsForExamMode() {
        btnTopics.setVisibility(View.GONE);
        btnExam.setVisibility(View.VISIBLE);
        btnRolePlaying.setVisibility(View.GONE);
        btnGrammar.setVisibility(View.GONE);
        rvContent.setVisibility(View.GONE);
        vAdjustHeight.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        rvVoca.setVisibility(View.VISIBLE);
        wvGrammar.setVisibility(View.GONE);
        vReading.setVisibility(View.GONE);
    }

    private void showButtonsForRolePlaying() {
        btnTopics.setVisibility(View.GONE);
        btnExam.setVisibility(View.GONE);
        btnRolePlaying.setVisibility(View.VISIBLE);
        btnGrammar.setVisibility(View.GONE);
        if (rolePlayingContent == null || rolePlayingContent.getContent() == null || rolePlayingContent.getContent().getContentViewId() == -1) {
            rvContent.setVisibility(View.GONE);
            vAdjustHeight.setVisibility(View.GONE);
        } else {
            rvContent.setVisibility(View.VISIBLE);
            vAdjustHeight.setVisibility(View.VISIBLE);
        }
        tvMessage.setVisibility(View.GONE);
        wvGrammar.setVisibility(View.GONE);
        vReading.setVisibility(View.GONE);
    }

    private void showButtonsForGrammarMode() {
        btnTopics.setVisibility(View.GONE);
        btnExam.setVisibility(View.GONE);
        btnRolePlaying.setVisibility(View.GONE);
        btnGrammar.setVisibility(View.VISIBLE);
        rvContent.setVisibility(View.GONE);
        vAdjustHeight.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        rvVoca.setVisibility(View.GONE);
        wvGrammar.setVisibility(View.GONE);
        vReading.setVisibility(View.GONE);
    }

    private void showButtonsForReadingMode() {
        btnTopics.setVisibility(View.VISIBLE);
        btnExam.setVisibility(View.GONE);
        btnRolePlaying.setVisibility(View.GONE);
        btnGrammar.setVisibility(View.GONE);
        rvContent.setVisibility(View.GONE);
        vAdjustHeight.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        rvVoca.setVisibility(View.GONE);
        wvGrammar.setVisibility(View.GONE);
        vReading.setVisibility(View.GONE);
    }

    private boolean isFromLessonList() {
        return lesson != null;
    }

    private int getLessonId() {
        return lesson == null ? 0 : lesson.getId();
    }

    private boolean isMeObserver() {
        return me != null && me.getStudyRole() == Constant.STUDY_ROLE_OBSERVER;
    }

    private int getStudentId() {
        return mainStudent == null ? (lesson == null ? 0 : lesson.getStudentId()) : mainStudent.getUid();
    }

    private String getStudentLangNative() {
        return (mainStudent == null ? (lesson == null ? "" : lesson.getStudentLangNative()) : mainStudent.getLangNative()).toUpperCase();
    }

    private int getTutorId() {
        return mainTutor == null ? (lesson == null ? 0 : lesson.getTutorId()) : mainTutor.getUid();
    }

    private int getStudyLang() {
        return isFromLessonList() ? lesson.getStudyLangCode() : sharedPreferences.getLangStudyCode();
    }

    @Override
    public void onPause() {
        if (isRemoving() || activity.isFinishing()) {
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
            }
            studyChatPlayVocaHelper.onDestroy();
            handler.removeMessages(Constant.MSG_WHAT.SAVE_STUDIED_WORKBOOK_ID);
        }

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (!activity.isFinishing()) {
            bundle.putSerializable(Constant.BUNDLE.KEY_STUDY_INFO, studyInfo);
            bundle.putSerializable(Constant.BUNDLE.KEY_LESSON_OPTION, lessonOption);
            bundle.putSerializable(Constant.BUNDLE.KEY_STUDIED_BOOKS, studiedBooks);
            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_FROM_ALL_VOCA_BOOK, vocaFromAllVocaBook);
            bundle.putInt(Constant.BUNDLE.KEY_ASTERISK, showAsterisk);
            bundle.putInt(Constant.BUNDLE.KEY_SHOW_SENTENCE, showSentence);
            bundle.putSerializable(Constant.BUNDLE.KEY_STUDY_VOCA_EXAM_LIST, examVocas);
            bundle.putSerializable(Constant.BUNDLE.KEY_STUDY_ROLE_PLAYING_CONTENT, rolePlayingContent);
            bundle.putInt(Constant.BUNDLE.KEY_LESSON_MODE, lessonMode);
            bundle.putInt(Constant.BUNDLE.KEY_GRAMMAR_ID, grammarId);
            bundle.putInt(Constant.BUNDLE.KEY_LESSON_READING_ID, lessonReadingId);
            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_READING, vocaReading);
            bundle.putInt(Constant.BUNDLE.KEY_STUDY_ORDER_POS, studyOrderPos);
            bundle.putInt(Constant.BUNDLE.KEY_STUDY_ORDER_ROLE_PLAYING_POS, studyOrderRolePlayingPos);
            bundle.putBoolean(Constant.BUNDLE.KEY_SHOW_STUDENT_MEANING, showStudentMeaning);
            SparseArray<Parcelable> container = new SparseArray<>();
            rvVoca.saveHierarchyState(container);
            bundle.putSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE, container);
        }
        dismissVoiceDialog();

        super.onDestroyView();
    }

    private void checkTypeVoiceCall() {
        checkTypeVoiceCall(true, false);
    }

    private void checkTypeVoiceCall(boolean isAction) {
        checkTypeVoiceCall(isAction, false);
    }

    private void checkTypeVoiceCall(boolean isAction, boolean isJoinCall) {
        DLog.d(getLogTag(), "checkTypeVoiceCall - isAction=" + isAction + " - typeCall=" + typeCall);
        if (isAction) {
            switch (typeCall) {
                case Constant.JITSI.STATUS.CALL:
                    typeCall = Constant.JITSI.STATUS.END;
                    break;
                default:
                    typeCall = isJoinCall ? Constant.JITSI.STATUS.JOIN_CALL : Constant.JITSI.STATUS.CALL;
                    break;
            }
            eventBus.post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.VOICE_CALL, typeCall));
        }
    }

    private void openVoiceInformationDialog() {
        openVoiceInformationDialog(true);
    }

    private void openVoiceInformationDialog(boolean isShow) {
        DLog.d(getLogTag(), "openVoiceInformationDialog isShow=" + isShow);
        /*
            observer not show dialog
            https://github.com/dalnim/IssueOnly/issues/36
         */
        if (!isShow && lesson.isAdmin()) {
            checkTypeVoiceCall();
            return;
        }
        if (voiceInformationDialog == null) {
            voiceInformationDialog = new ConfirmationDialog(context,
                    R.string.voice_call_information_title,
                    R.string.voice_call_information_question_msg,
                    R.string.voice_call_information_yes,
                    R.string.voice_call_information_no,
                    new ConfirmationDialog.OnDialogClickListener() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            checkTypeVoiceCall();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
        }
        dismissVoiceInformationDialog();
        voiceInformationDialog.show();
    }

    private void dismissVoiceInformationDialog() {
        if (voiceInformationDialog != null && voiceInformationDialog.isShowing()) {
            DLog.d(getLogTag(), "dismissVoiceInformationDialog");
            voiceInformationDialog.dismiss();
        }
    }

    private void openVoiceJoinDialog() {
        if (jointVoiceDialog == null) {
            jointVoiceDialog = new ConfirmationDialog(context,
                    R.string.voice_call_information_title,
                    R.string.voice_call_join_msg,
                    R.string.voice_call_join_btn_yes,
                    R.string.voice_call_join_btn_no,
                    new ConfirmationDialog.OnDialogClickListener() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            checkTypeVoiceCall(true, true);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
        }
        dismissVoiceJoinDialog();
        jointVoiceDialog.show();
    }

    private void dismissVoiceJoinDialog() {
        if (jointVoiceDialog != null && jointVoiceDialog.isShowing()) {
            DLog.d(getLogTag(), "dismissVoiceJoinDialog");
            jointVoiceDialog.dismiss();
        }
    }

    private void dismissVoiceDialog() {
        dismissVoiceInformationDialog();
        dismissVoiceJoinDialog();
    }

    private void getRolePlayingCategoryList(int rolePlayingParentId) {
        getRolePlayingCategoryList(rolePlayingParentId, false);
    }

    private void getRolePlayingCategoryList(int rolePlayingParentId, boolean isRandom) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getRolePlayingCategoryList(
                        getStudyLang(),
                        selectedLevelPos + 1,
                        rolePlayingParentId,
                        new DalApiListener<List<Category>>() {

                            @Override
                            public void onSuccess(List<Category> response) {
                                Loading.hide();
                                if (response != null && !response.isEmpty()) {
                                    if (isRandom) {
                                        int pos = new Random().nextInt(response.size());
                                        Category category = response.get(pos);
                                        category.setRandom(true);
                                        onRolePlayingCategoryClickListener.onClick(null, category);
                                    } else {
                                        if (rolePlayingParentId == 1) {
                                            Category category = new Category();
                                            category.setName(context.getString(R.string.random));
                                            category.setRandom(true);
                                            response.add(0, category);
                                        }
                                        recyclerViewDialog.setAdapter(new ArrayList<>(response), onRolePlayingCategoryClickListener);
                                        recyclerViewDialog.show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void makeRolePlayingContentsJson(int rolePlayingCategoryId, int rolePlayingType) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().makeRolePlayingContentsJson(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        selectedLevelPos + 1,
                        rolePlayingCategoryId,
                        rolePlayingType,
                        getStudentId(),
                        getStudentLangNative(),
                        getTutorId(),
                        Constant.MAKE_RUBY_TEXT ? 1 : 0,
                        new DalApiListener<RolePlayingContent>() {

                            @Override
                            public void onSuccess(RolePlayingContent response) {
                                Loading.hide();
                                onGetRolePlayingDataSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onMakeRolePlayingContentsJsonReceived(Bundle eventData) {
        lessonMode = Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING;
        int rolePlayingId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.ROLE_PLAYING_ID)
                .replace("[", "").replace("]", ""));
        int studentId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.STUDENT_ID));
        rvVoca.post(() -> getRolePlayingContentsJson(rolePlayingId, studentId));
    }

    private void getRolePlayingContentsJson(int rolePlayingId, int studentId) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getRolePlayingContentsJson(
                        getStudyLang(),
                        rolePlayingId,
                        studentId,
                        Constant.MAKE_RUBY_TEXT ? 1 : 0,
                        new DalApiListener<RolePlayingContent>() {

                            @Override
                            public void onSuccess(RolePlayingContent response) {
                                Loading.hide();
                                onGetRolePlayingDataSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onGetRolePlayingDataSuccess(RolePlayingContent response) {
        rolePlayingContent = response;
        if (rolePlayingContent.getAllSentenceListShowGuide() == Constant.API_VALUE.IS_YES) {
            getAllSentenceListDetails(null, false);
        } else if (rolePlayingContent.getAllVocaListShowGuide() == Constant.API_VALUE.IS_YES) {
            getAllVocaListDetails(null, false);
        } else {
            setRecyclerViewRolePlayingData();
        }
        showStudyOrderRolePlayingAlertFirstTime();
    }

    private void setRecyclerViewRolePlayingData() {
        activity.getPlayVocaHelper().stop();
        rolePlayingAdapter = new RolePlayingAdapter(context);
        rolePlayingAdapter.setData(rolePlayingContent);
        rolePlayingAdapter.setStudyRole(getMyStudyRole());
        rolePlayingAdapter.setShowAsterisk(showAsterisk);
        rolePlayingAdapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        rolePlayingAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        rolePlayingAdapter.setStudyLang(enumStudyLanguage.getIdApi());
        rolePlayingAdapter.setUid(uid);
        rolePlayingAdapter.setShowStudentMeaning(showStudentMeaning);
        rolePlayingAdapter.setOnVocaStudyChatClickListener(onVocaStudyChatClickListener);
        rolePlayingAdapter.setOnClickListener(onConversationClickListener);
        rvVoca.setAdapter(rolePlayingAdapter);
        if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
            rvVoca.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
            bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
        }

        rolePlayingContentAdapter = new RolePlayingContentAdapter();
        rolePlayingContentAdapter.setData(rolePlayingContent);
        rolePlayingContentAdapter.setStrTotal(enumStudyLanguage.getTotal());
        rolePlayingContentAdapter.setStudyRole(getMyStudyRole());
        rolePlayingContentAdapter.setStudyLang(enumStudyLanguage.getIdApi());
        rolePlayingContentAdapter.setListener(onContentSubDetailsClickListener);
        rvContent.setAdapter(rolePlayingContentAdapter);

        showButtonsForRolePlaying();
        setTitleStudy(me);
    }

    private void showKnowAllPhrasesDialog() {
        if (adapter.getItemCount() == 1) {
            alertDialog.show(R.string.msg_know_all_phrases, 0, null);
        }
    }

    private DialogInterface.OnClickListener onChangeRolePlayingClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_change_subject:
                getRolePlayingCategoryList(1);
                break;
            case R.id.tv_study_words:
                showAllWordsTitleDialog();
                break;
            case R.id.tv_change_word_known_status:
                getAllVocaListDetails(null, true);
                break;
            case R.id.tv_study_sentences:
                getAllSentenceListDetails(null, true);
                break;
            case R.id.tv_refresh:
                showChangeRolePlayingLevelDialog();
                break;
            default:
                break;
        }
    };

    private void initSelectedLevelPos() {
        if (lessonOption != null && lessonOption.getLanguageLevel() > 0) {
            selectedLevelPos = lessonOption.getLanguageLevel() - 1;
        } else {
            selectedLevelPos = 0;
        }
    }

    private OnClickListener onContentSubDetailsClickListener = (view, object) -> {
        if (object instanceof ContentSubDetails) {
            ContentSubDetails contentSubDetails = (ContentSubDetails) object;
            if (contentSubDetails.isChecked()) {
                singleChoiceDialog.show(
                        R.string.count_of_order,
                        orderValues,
                        0,
                        R.string.done,
                        R.string.cancel,
                        true,
                        new OnClickDialogListener() {
                            @Override
                            public void onClick(View view, Object object) {
                                final int which = (int) object;
                                if (which >= 0) {
                                    contentSubDetails.setCountOfOrder(Utils.parseInt(orderValues[which]));
                                } else {
                                    contentSubDetails.setCountOfOrder(0);
                                    contentSubDetails.setChecked(false);
                                }
                                rolePlayingContentAdapter.calculateTotalAndNotify(contentSubDetails);
                                syncCellWithOtherUsersInStudyMode(contentSubDetails);
                            }

                            @Override
                            public void onDismiss(View view, Object object) {

                            }
                        }
                );
            } else {
                rolePlayingContentAdapter.calculateTotalAndNotify(contentSubDetails);
                syncCellWithOtherUsersInStudyMode(contentSubDetails);
            }
        }
    };

    private void syncCellWithOtherUsersInStudyMode(ContentSubDetails contentSubDetails) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().syncCellWithOtherUsersInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        studyInfo.getVocaBook().getVocaBookId(),
                        studyInfo.getVocaBook().getVocaBookType(),
                        0,
                        0,
                        null,
                        null,
                        contentSubDetails.getTblSection(),
                        contentSubDetails.getTblRow(),
                        Constant.API_VALUE.TBL_TYPE_SYNC_CONTENT_ID_1,
                        contentSubDetails.isChecked() ? Constant.API_VALUE.IS_CELL_CHECKED_YES : Constant.API_VALUE.IS_CELL_CHECKED_NO,
                        contentSubDetails.getCountOfOrder(),
                        "",
                        null
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void syncCellWithOtherUsersInStudyMode(Conversation conversation, boolean isBranchSelected) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                if (!isBranchSelected) {
                    blinkCell(conversation, false);
                }
                application.getDalAiImpl().syncCellWithOtherUsersInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        studyInfo.getVocaBook().getVocaBookId(),
                        studyInfo.getVocaBook().getVocaBookType(),
                        0,
                        0,
                        null,
                        null,
                        conversation.getTblSection(),
                        conversation.getTblRow(),
                        Constant.API_VALUE.TBL_TYPE_SYNC_CONVERSATION_LIST,
                        Constant.API_VALUE.IS_CELL_CHECKED_NO,
                        0,
                        isBranchSelected ? conversation.getBranchSelected() : "",
                        null
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private OnClickListener onConversationClickListener = (view, object) -> {
        if (object instanceof Conversation) {
            Conversation conversation = (Conversation) object;
            switch (view.getId()) {
                case R.id.ic_play:
                    syncCellWithOtherUsersInStudyMode(conversation, false);
                    break;
                case R.id.btn_segment:
                    syncCellWithOtherUsersInStudyMode(conversation, true);
                    break;
                default:
                    rolePlayingConversationDialog.show(conversation, getMyStudyRole());
                    break;
            }
        }
    };

    private void showSentencesDialog(List<VocaStudyChat> vocas, VocaStudyChat voca) {
        StudyChatAdapter studyChatAdapter = new StudyChatAdapter(context);
        studyChatAdapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        studyChatAdapter.setStudyRole(getMyStudyRole());
        studyChatAdapter.setShowAsterisk(showAsterisk);
        studyChatAdapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        studyChatAdapter.setStudyLang(enumStudyLanguage.getIdApi());
        studyChatAdapter.setHasHeader(true);
        studyChatAdapter.setIsDialog(true);
        studyChatAdapter.setShowStudentMeaning(showStudentMeaning);
        studyChatAdapter.setListener(onVocaStudyChatClickListener);
        studyChatAdapter.setDataNoLoop(vocas);
        recyclerViewDialog.setAdapter(studyChatAdapter);
        recyclerViewDialog.show();
        if (voca != null) {
            recyclerViewDialog.moveToCenter(studyChatAdapter.notifyItemChanged(voca));
        }
    }

    private DialogInterface.OnDismissListener onRecyclerViewDialogDismissListener = dialog -> activity.getPlayVocaHelper().stop();

    private void getRubyVocaList(VocaStudyChat voca, OnClickListener callback) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                if (!TextUtils.isEmpty(voca.getRubyVocaIds()) && !TextUtils.isEmpty(voca.getRubyVocaTypes())) {
                    Loading.show(context);
                    application.getDalAiImpl().getVocasFromVocaList(
                            getStudyLang(),
                            getStudentId(),
                            getTutorId(),
                            voca.getRubyVocaIds(),
                            voca.getRubyVocaTypes(),
                            new DalApiListener<List<VocaStudyChatRuby>>() {

                                @Override
                                public void onSuccess(List<VocaStudyChatRuby> response) {
                                    if (response != null) {
                                        int studyLang = enumStudyLanguage.getIdApi();
                                        for (VocaStudyChat childVoca : response) {
                                            childVoca.setPath(Voca.getOutputRecordingFileName(studyLang, childVoca.getType(), childVoca.getVocaId(), uid));
                                            childVoca.setTblParentSection(voca.getTblSection());
                                            childVoca.setTblParentRow(voca.getTblRow());
                                            childVoca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST);
                                            childVoca.setPnType(Constant.API_VALUE.PN_TYPE_RUBY_TEXT);
                                        }
                                        StudyChatAdapter adapter = new StudyChatAdapter(context);
                                        adapter.setDataNoLoop(new ArrayList<>(response));
                                        setRecyclerViewDataDialog(adapter, callback);
                                    }
                                    Loading.hide();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                }
                            }
                    );
                }
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
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
            final int height = heightRoot / 8;
            View vAbove = view.getId() == R.id.v_adjust_height_reading ? vReadingTop : rvContent;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressStartTime = System.currentTimeMillis();
                    pressedX = event.getX();
                    pressedY = event.getY();
                    stayedWithinClickDistance = true;
                    DLog.d(getLogTag(), "pressedY=" + pressedY + " - height=" + vAbove.getHeight());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (stayedWithinClickDistance && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                        stayedWithinClickDistance = false;
                    }
                    if (vAbove != null && vAbove.getVisibility() != View.GONE) {
                        final float currentY = event.getY() - pressedY;
                        DLog.d(getLogTag(), "pressedY=" + pressedY + "currentY=" + currentY);
                        int currentHeight = (int) (vAbove.getHeight() + currentY);
                        if (currentHeight <= height) {
                            currentHeight = height;
                        } else if (currentHeight >= (heightRoot - height)) {
                            currentHeight = heightRoot - height;
                        }
                        vAbove.setLayoutParams(new LinearLayout.LayoutParams(vAbove.getWidth(), currentHeight));
                        DLog.d(getLogTag(), "height move=" + vAbove.getHeight() + " - currentHeight=" + currentHeight);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    long pressDuration = System.currentTimeMillis() - pressStartTime;
                    if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                        onClickSeparatorBar(view);
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    private void updateVocaDisplayRubyText(int vocaId, int vocaType, int vocaKnow, int vocaKnowPronounce) {
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL
                || lessonMode == Constant.API_VALUE.LESSON_MODE_REVIEW
                || lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
            VocaFromAllVocaBook vocaFromAllVocaBook = lessonMode == Constant.API_VALUE.LESSON_MODE_READING
                    ? vocaReading
                    : this.vocaFromAllVocaBook;
            boolean result = false;
            for (VocaStudyChat aVoca : vocaFromAllVocaBook.getStudyVocaList()) {
                if (aVoca.getVocaId() == vocaId && aVoca.getType() == vocaType) {
                    aVoca.setVocaKnow(vocaKnow);
                    aVoca.setVocaKnowPronounce(vocaKnowPronounce);
                }
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
            }
            for (VocaStudyChat aVoca : vocaFromAllVocaBook.getAllVocaList()) {
                if (aVoca.getVocaId() == vocaId && aVoca.getType() == vocaType) {
                    aVoca.setVocaKnow(vocaKnow);
                    aVoca.setVocaKnowPronounce(vocaKnowPronounce);
                }
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
            }
            for (VocaStudyChat aVoca : vocaFromAllVocaBook.getExamVocaList()) {
                if (aVoca.getVocaId() == vocaId && aVoca.getType() == vocaType) {
                    aVoca.setVocaKnow(vocaKnow);
                    aVoca.setVocaKnowPronounce(vocaKnowPronounce);
                }
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
            }
            if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING) {
                result |= vocaReading.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                result |= vocaReading.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
            }
            if (result) {
                if (vReading.getVisibility() == View.VISIBLE) {
                    vReading.post(this::showReadingComprehension);
                } else if (rvVoca.getAdapter() != null) {
                    rvVoca.post(rvVoca.getAdapter()::notifyDataSetChanged);
                }
            }
        } else if (lessonMode == Constant.API_VALUE.LESSON_MODE_ROLE_PLAYING) {
            if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
                StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
                boolean result = false;
                for (VocaStudyChat aVoca : adapter.getVocas()) {
                    result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                    result |= aVoca.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
                }
                if (result) {
                    rvVoca.post(adapter::notifyDataSetChanged);
                }
                if (rolePlayingContent != null) {
                    rolePlayingContent.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                    rolePlayingContent.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
                }
            } else if (rvVoca.getAdapter() instanceof RolePlayingAdapter) {
                if (rolePlayingContent != null) {
                    boolean result = rolePlayingContent.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOW, vocaKnow);
                    result |= rolePlayingContent.updateVocaDisplayRubyText(vocaId, Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
                    if (result) {
                        rvVoca.post(() -> {
                            rvVoca.getAdapter().notifyDataSetChanged();
                            rolePlayingContentAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
        }
    }

    public void onMenuChatMessageClick() {
        typeChatMessageDialog.show();
    }

    private BaseDialogListener onTypeChatMessageClickListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object object) {
            typeChatMessageDialog.dismiss();
            sendChatMessageInStudyMode((String) object);
            typeChatMessageDialog.setInput("");
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private void sendChatMessageInStudyMode(String message) {
        if (uid > 0 && Utils.isConnected(context)) {
            Loading.show(context);
            application.getDalAiImpl().sendChatMessageInStudyMode(
                    chatRoomInfo.getFirestoreChatRoomId(),
                    getLessonId(),
                    message,
                    enumStudyLanguage.getIdApi(),
                    new DalApiListener<ChatMessageResponse>() {

                        @Override
                        public void onSuccess(ChatMessageResponse response) {
                            if (response == null || TextUtils.isEmpty(response.getVocaIds())) {
                                Loading.hide();
                                alertDialog.show(chatMessageTitleMe, message, null, null);
                            } else {
                                getVocasFromVocaList(response.getVocaIds(), chatMessageTitleMe + "\n" + message);
                                if (firestoreHelper == null) {
                                    firestoreHelper = new FirestoreHelper(context, sharedPreferences, chatRoomInfo.getFirestoreChatRoomId());
                                }
                                firestoreHelper.addTextMessage(message, null, null);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        }
    }

    private void onChatMessageReceived(Bundle eventData) {
        String title = String.format(
                chatMessageTitleOpponent,
                eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME)
        );
        String message = eventData.getString(Constant.NOTIFICATION_KEY.MESSAGE);
        String vocaIds = eventData.getString(Constant.NOTIFICATION_KEY.VOCA_IDs);
        rvVoca.post(() -> {
            if (TextUtils.isEmpty(vocaIds)) {
                alertDialog.show(title, message, null, null);
            } else {
                getVocasFromVocaList(vocaIds, title + "\n" + message);
            }
        });
    }

    private void showAllWordsTitleDialog() {
        if (rolePlayingContent != null) {
            recyclerViewDialog.setAdapter(new ArrayList<>(rolePlayingContent.getVocaListByCategory()), (view, object) -> {
                recyclerViewDialog.dismiss();
                if (object instanceof VocaStudyChatAllWords) {
                    getVocasFromVocaListInVocabook((VocaStudyChatAllWords) object, rolePlayingContent.getVocaListByCategory().indexOf(object), null);
                }
            });
            recyclerViewDialog.show();
        }
    }

    private void setRecyclerViewData(StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setStudyRole(getMyStudyRole());
        adapter.setShowAsterisk(showAsterisk);
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        adapter.setStudyLang(enumStudyLanguage.getIdApi());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
        adapter.setShowStudentMeaning(showStudentMeaning);
        adapter.setListener(onVocaStudyChatClickListener);
        rvVoca.post(() -> {
            rvVoca.setAdapter(adapter);
            if (callback != null) {
                callback.onClick(null, adapter);
            }
            rvVoca.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
            vAdjustHeight.setVisibility(View.GONE);
        });
    }

    private void setRecyclerViewDataDialog(StudyChatAdapter adapter, OnClickListener callback) {
        setRecyclerViewDataDialog(null, adapter, callback);
    }

    private void setRecyclerViewDataDialog(String title, StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setStudyRole(getMyStudyRole());
        adapter.setShowAsterisk(showAsterisk);
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        adapter.setStudyLang(enumStudyLanguage.getIdApi());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
        adapter.setIsDialog(true);
        adapter.setShowStudentMeaning(showStudentMeaning);
        adapter.setListener(onVocaStudyChatClickListener);
        rvVoca.post(() -> {
            recyclerViewDialog.setAdapter(adapter, title);
            recyclerViewDialog.show();
            if (callback != null) {
                callback.onClick(null, adapter);
            }
        });
    }

    private void getVocasFromVocaListInVocabook(VocaStudyChatAllWords voca,
                                                int tblParentSection, OnClickListener callback) {
        if (uid > 0 && Utils.isConnected(context) && !voca.getVocaList().isEmpty()) {
            StringBuilder ids = new StringBuilder();
            StringBuilder types = new StringBuilder();
            StringBuilder idsInVocaBook = new StringBuilder();
            for (VocaStudyChat vocaStudyChat : voca.getVocaList()) {
                ids.append(",").append(vocaStudyChat.getVocaId());
                types.append(",").append(vocaStudyChat.getType());
                idsInVocaBook.append(",").append(vocaStudyChat.getIdInVocaBook());
            }
            Loading.show(context);
            application.getDalAiImpl().getVocasFromVocaListInVocabook(
                    getStudyLang(),
                    getStudentId(),
                    getTutorId(),
                    ids.substring(1),
                    types.substring(1),
                    idsInVocaBook.substring(1),
                    new DalApiListener<List<VocaStudyChatByCategory>>() {

                        @Override
                        public void onSuccess(List<VocaStudyChatByCategory> response) {
                            Loading.hide();
                            if (response == null) return;
                            StudyChatAdapter adapter = new StudyChatAdapter(context);
                            adapter.setData(new ArrayList<>(response), tblParentSection);
                            setRecyclerViewDataDialog(adapter, callback);
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        }
    }

    private int getMyStudyRole() {
        return me == null ? Constant.STUDY_ROLE_STUDENT : me.getStudyRole();
    }

    private void syncCellInDialog(VocaStudyChat voca) {
        rvVoca.post(() -> {
            StudyChatAdapter adapter = (StudyChatAdapter) recyclerViewDialog.getAdapter();
            int pos = adapter.notifyItemChanged(voca, true);
            if (pos > -1) {
                recyclerViewDialog.moveToCenter(pos);
            }
        });
    }

    private void syncCellInDialog(StudyChatAdapter adapter, boolean byId, int x, int y) {
        VocaStudyChat voca = byId
                ? adapter.getVocaByVocaId(x, y)
                : adapter.getVocaByIndex(x, y);
        if (voca != null) {
            syncCellInDialog(voca);
        }
    }

    private void syncCellInDialogBy(int tblTypeSync, int tblParentSection, int tblParentRow,
                                    boolean byId, int x, int y) {
        if (recyclerViewDialog.isShowing()) {
            if (recyclerViewDialog.getAdapter() instanceof StudyChatAdapter) {
                syncCellInDialog((StudyChatAdapter) recyclerViewDialog.getAdapter(), byId, x, y);
                return;
            }
            recyclerViewDialog.dismiss();
        }
        OnClickListener callback = (view, object) -> {
            if (object instanceof StudyChatAdapter) {
                syncCellInDialog((StudyChatAdapter) object, byId, x, y);
            }
        };
        if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY) {
            getVocasFromVocaListInVocabook(rolePlayingContent.getVocaListByCategory().get(tblParentSection), tblParentSection, callback);
        } else if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_ALL_SENTENCE_LIST) {
            getAllSentenceListDetails(callback, true);
        } else if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL) {
            getAllVocaListDetails(callback, true);
        } else if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION) {
            for (Conversation conversation : rolePlayingContent.getConversationList()) {
                if (conversation.getTblRow() == tblParentRow) {
                    getRubyVocaList(conversation, callback);
                    break;
                }
            }
        } else if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST) {
            if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
                StudyChatAdapter adapter = (StudyChatAdapter) rvVoca.getAdapter();
                VocaStudyChat voca = adapter.getVocaByIndex(tblParentSection, tblParentRow);
                if (voca != null) {
                    getRubyVocaList(voca, callback);
                }
            }
        } else if (tblTypeSync == Constant.API_VALUE.TBL_TYPE_SYNC_READING_VOCA_LIST) {
            if (vocaReading != null) {
                StudyChatAdapter adapter = new StudyChatAdapter(context);
                adapter.setDataNoLoop(new ArrayList<>(vocaReading.getAllVocaList()));
                setRecyclerViewDataDialog(adapter, callback);
            }
        }
    }

    private void showStudyOrderRolePlayingAlert() {
        int pos = studyOrderRolePlayingPos - rolePlayingContent.getAllSentenceListShowGuide() - rolePlayingContent.getAllVocaListShowGuide();
        if (pos == -2) {
            studyOrderMessageId = R.string.all_sentence_list_guide_message;
        } else if (pos == -1) {
            if (rolePlayingContent.getAllSentenceListShowGuide() == Constant.API_VALUE.IS_YES
                    && rolePlayingContent.getAllVocaListShowGuide() == Constant.API_VALUE.IS_NO) {
                studyOrderMessageId = R.string.all_sentence_list_guide_message;
            } else {
                studyOrderMessageId = R.string.all_voca_list_guide_message;
            }
        } else if (pos == 0) {
            studyOrderMessageId = R.string.study_order_role_playing_1_message;
        } else if (pos == 1) {
            studyOrderMessageId = R.string.study_order_role_playing_2_message;
        } else if (pos == 2) {
            studyOrderMessageId = R.string.study_order_role_playing_3_message;
        } else {
            studyOrderMessageId = R.string.study_order_role_playing_4_message;
        }
        alertDialog.show(
                studyOrderTitle = activity.getString(R.string.study_order_role_playing_title),
                studyOrderMessage = activity.getString(studyOrderMessageId, studyOrderRolePlayingPos + 1, Constant.STUDY_ORDER_ROLE_PLAYING_COUNT + rolePlayingContent.getAllSentenceListShowGuide() + rolePlayingContent.getAllVocaListShowGuide()),
                activity.getString(R.string.ok),
                null
        );
    }

    private void showStudyOrderRolePlayingAlertFirstTime() {
        showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
        studyOrderRolePlayingPos = 0;
        showStudyOrderRolePlayingAlert();
    }

    private void sendStudyOrderInRolePlayingMode(int pos) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().showAsteriskInStudyMode(
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        pos,
                        null
                );
                onStudyOrderInRolePlayingModeChanged(pos);
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onStudyOrderInRolePlayingModeChanged(int pos) {
        studyOrderRolePlayingPos = pos;
        rvVoca.post(() -> {
            showStudyOrderRolePlayingAlert();
            if (studyOrderMessageId == R.string.all_sentence_list_guide_message) {
                getAllSentenceListDetails(null, false);
            } else if (studyOrderMessageId == R.string.all_voca_list_guide_message) {
                getAllVocaListDetails(null, false);
            } else if (rvVoca.getAdapter() == rolePlayingAdapter) {
                rvVoca.scrollToPosition(0);
                rvContent.scrollToPosition(0);
            } else {
                setRecyclerViewRolePlayingData();
            }
        });
    }

    private void showChangeRolePlayingLevelDialog() {
        if (rolePlayingContent != null) {
            singleChoiceDialog.show(
                    R.string.student_level, levelOptions, selectedLevelPos, R.string.ok, R.string.cancel,
                    new OnClickDialogListener() {
                        @Override
                        public void onClick(View view, Object object) {
                            selectedLevelPos = (int) object;
                            makeRolePlayingContentsJson(
                                    rolePlayingContent.getRolePlayingCategoryId(),
                                    rolePlayingContent.getRolePlayingType()
                            );
                        }

                        @Override
                        public void onDismiss(View view, Object object) {

                        }
                    }
            );
        }
    }

    private void getAllSentenceListDetails(OnClickListener callback, boolean showDialog) {
        if (rolePlayingContent == null)
            return;
        List<VocaStudyChat> list = showDialog ? rolePlayingContent.getAllSentenceList() : rolePlayingContent.getStudySentenceList();
        if (list.isEmpty())
            return;
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                StringBuilder allSentenceListIds = new StringBuilder();
                StringBuilder allSentenceListTypes = new StringBuilder();
                for (VocaStudyChat voca : list) {
                    allSentenceListIds.append(",").append(voca.getVocaId());
                    allSentenceListTypes.append(",").append(voca.getType());
                }
                Loading.show(context);
                application.getDalAiImpl().getVocasFromVocaList(
                        getStudyLang(),
                        getStudentId(),
                        getTutorId(),
                        allSentenceListIds.substring(1),
                        allSentenceListTypes.substring(1),
                        new DalApiListener<List<VocaStudyChatRuby>>() {

                            @Override
                            public void onSuccess(List<VocaStudyChatRuby> response) {
                                Loading.hide();
                                if (response == null) return;
                                int studyLang = enumStudyLanguage.getIdApi();
                                List<VocaStudyChat> vocas = new ArrayList<>();
                                for (VocaStudyChatRuby voca : response) {
                                    voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                                    voca.setDisplayEvaluation(false);
                                    vocas.add(voca);
                                }
                                StudyChatAdapter adapter = new StudyChatAdapter(context);
                                if (showDialog) {
                                    adapter.setDataAllSentenceListDialog(vocas);
                                    setRecyclerViewDataDialog(adapter, callback);
                                } else {
                                    adapter.setDataAllSentenceList(vocas);
                                    setRecyclerViewData(adapter, callback);
                                    showButtonsForRolePlaying();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getAllVocaListDetails(OnClickListener callback, boolean showDialog) {
        if (rolePlayingContent == null)
            return;
        List<VocaStudyChat> list = showDialog ? rolePlayingContent.getAllVocaList() : rolePlayingContent.getStudyVocaList();
        if (list.isEmpty())
            return;
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                StringBuilder allVocaListIds = new StringBuilder();
                StringBuilder allVocaListTypes = new StringBuilder();
                for (VocaStudyChat voca : list) {
                    allVocaListIds.append(",").append(voca.getVocaId());
                    allVocaListTypes.append(",").append(voca.getType());
                }
                Loading.show(context);
                application.getDalAiImpl().getVocasFromVocaList(
                        getStudyLang(),
                        getStudentId(),
                        getTutorId(),
                        allVocaListIds.substring(1),
                        allVocaListTypes.substring(1),
                        new DalApiListener<List<VocaStudyChatRuby>>() {

                            @Override
                            public void onSuccess(List<VocaStudyChatRuby> response) {
                                Loading.hide();
                                if (response == null) return;
                                int studyLang = enumStudyLanguage.getIdApi();
                                List<VocaStudyChat> vocas = new ArrayList<>();
                                for (VocaStudyChatRuby voca : response) {
                                    voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                                    voca.setDisplayEvaluation(false);
                                    vocas.add(voca);
                                }
                                StudyChatAdapter adapter = new StudyChatAdapter(context);
                                if (showDialog) {
                                    adapter.setDataAllVocaListDialog(vocas);
                                    setRecyclerViewDataDialog(adapter, callback);
                                } else {
                                    adapter.setDataAllVocaList(vocas);
                                    setRecyclerViewData(adapter, callback);
                                    showButtonsForRolePlaying();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    public void onCurrentGuideClick() {
        if (lessonMode != Constant.API_VALUE.LESSON_MODE_EXAM
                && lessonMode != Constant.API_VALUE.LESSON_MODE_GRAMMAR
                && !TextUtils.isEmpty(studyOrderTitle)
                && !TextUtils.isEmpty(studyOrderMessage)) {
            alertDialog.show(
                    studyOrderTitle,
                    studyOrderMessage,
                    activity.getString(R.string.ok),
                    null
            );
        }
    }

    private OnClickListener onRolePlayingConversationClick = (view, object) -> {
        if (object instanceof Conversation) {
            Conversation conversation = (Conversation) object;
            switch (view.getId()) {
                case R.id.tv_change_word_known_status:
                    getRubyVocaList(conversation, null);
                    break;
                case R.id.tv_see_examples:
                    showSentencesDialog(conversation.getSentences(), null);
                    break;
                default:
                    break;
            }
        }
    };

    private void getRubyVocaList(Conversation conversation, OnClickListener callback) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                if (!TextUtils.isEmpty(conversation.getRubyVocaIds()) && !TextUtils.isEmpty(conversation.getRubyVocaTypes())) {
                    Loading.show(context);
                    application.getDalAiImpl().getVocasFromVocaList(
                            getStudyLang(),
                            getStudentId(),
                            getTutorId(),
                            conversation.getRubyVocaIds(),
                            conversation.getRubyVocaTypes(),
                            new DalApiListener<List<VocaStudyChatRuby>>() {

                                @Override
                                public void onSuccess(List<VocaStudyChatRuby> response) {
                                    Loading.hide();
                                    if (response == null) return;
                                    int studyLang = enumStudyLanguage.getIdApi();
                                    List<VocaStudyChat> vocas = new ArrayList<>();
                                    for (VocaStudyChat voca : response) {
                                        voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                                        vocas.add(voca);
                                    }
                                    StudyChatAdapter adapter = new StudyChatAdapter(context);
                                    adapter.setDataConversation(vocas, conversation.getTblSection(), conversation.getTblRow());
                                    setRecyclerViewDataDialog(adapter, callback);
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                }
                            }
                    );
                }
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getVocasFromVocaList(String vocaIds, String title) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                if (!TextUtils.isEmpty(vocaIds)) {
                    Loading.show(context);
                    int count = vocaIds.split(",").length;
                    StringBuilder vocaTypes = new StringBuilder("1");
                    for (int i = 0; i < count; i++) {
                        vocaTypes.append(",1");
                    }
                    application.getDalAiImpl().getVocasFromVocaList(
                            getStudyLang(),
                            getStudentId(),
                            getTutorId(),
                            vocaIds,
                            vocaTypes.toString(),
                            new DalApiListener<List<VocaStudyChatRuby>>() {

                                @Override
                                public void onSuccess(List<VocaStudyChatRuby> response) {
                                    if (response != null) {
                                        int studyLang = enumStudyLanguage.getIdApi();
                                        for (VocaStudyChat childVoca : response) {
                                            childVoca.setPath(Voca.getOutputRecordingFileName(studyLang, childVoca.getType(), childVoca.getVocaId(), uid));
                                            childVoca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_MSG_VOCA_LIST);
                                            childVoca.setPnType(Constant.API_VALUE.PN_TYPE_MSG_VOCA_LIST);
                                        }
                                        StudyChatAdapter adapter = new StudyChatAdapter(context);
                                        adapter.setDataNoLoop(new ArrayList<>(response));
                                        setRecyclerViewDataDialog(title, adapter, null);
                                    }
                                    Loading.hide();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                }
                            }
                    );
                }
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private OnClickListener onTypeChooseMessageListener = new OnClickListener() {
        @Override
        public void onClick(View view, Object object) {
            sendChatMessageInStudyMode((String) object);
            messageListDialog.dismiss();
        }
    };

    private WebViewClient wvGrammarClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            Loading.show(context);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Loading.hide();
            wvGrammar.scrollTo(0, 0);
            wvGrammar.setVisibility(View.VISIBLE);
        }
    };

    private void getGrammarList(int parentId) {
        List<GRAMMAR> grammarList = Voca.getGrammarList(parentId, enumStudyLanguage, enumDisplayLanguage);
        recyclerViewDialog.setAdapter(new ArrayList<>(grammarList), onGrammarClickListener);
        recyclerViewDialog.show();
    }

    private OnClickListener onGrammarClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            recyclerViewDialog.dismiss();
            if (object instanceof GRAMMAR) {
                GRAMMAR grammar = (GRAMMAR) object;
                if (grammar.getHAS_SUB_LIST() == 1) {
                    getGrammarList(grammar.getID());
                } else {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_GRAMMAR;
                    grammarId = grammar.getID();
                    showGrammarContent(grammar);
                    sendGrammarWorkbookInStudyMode();
                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_NO, null);
                }
            }
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    private void showGrammarContent(GRAMMAR grammar) {
        showButtonsForGrammarMode();
        onMessageChanged(grammar.getNAME_());
        wvGrammar.loadDataWithBaseURL("", grammar.getMEANING_DETAILED(), "text/html; charset=utf-8", "UTF-8", "");
    }

    private void sendGrammarWorkbookInStudyMode() {
        sendGrammarWorkbookInStudyMode(false);
    }

    private void sendGrammarWorkbookInStudyMode(boolean showLoading) {
        if (uid > 0 && Utils.isConnected(context)) {
            if (showLoading) {
                Loading.show(context);
            }
            application.getDalAiImpl().sendGrammarWorkbookInStudyMode(
                    chatRoomInfo.getFirestoreChatRoomId(),
                    getLessonId(),
                    grammarId,
                    getStudyLang(),
                    showLoading ? new DalApiListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            Loading.hide();
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    } : null
            );
        }
    }

    private void onGrammarWorkbookReceived(Bundle eventData) {
        lessonMode = Constant.API_VALUE.LESSON_MODE_GRAMMAR;
        grammarId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.GRAMMAR_ID));
        showGrammarById();
    }

    private void showGrammarById() {
        if (sortedGrammars == null) {
            sortedGrammars = Voca.getSortedGrammarList(enumStudyLanguage, enumDisplayLanguage);
        }
        for (int i = 0; i < sortedGrammars.size(); i++) {
            GRAMMAR grammar = sortedGrammars.get(i);
            if (grammar.getID() == grammarId) {
                int pos = i;
                wvGrammar.post(() -> {
                    showGrammarContent(grammar);

                    if (pos == sortedGrammars.size() - 1) {
                        grammarDialog.hideMoveToNextBook();
                    } else {
                        grammarDialog.showMoveToNextBook();
                    }
                    if (pos == 0) {
                        grammarDialog.hideMoveToPreviousBook();
                    } else {
                        grammarDialog.showMoveToPreviousBook();
                    }
                });
                break;
            }
        }
    }

    private DialogInterface.OnClickListener onGrammarOptionClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_move_to_next_book:
                if (sortedGrammars == null) {
                    sortedGrammars = Voca.getSortedGrammarList(enumStudyLanguage, enumDisplayLanguage);
                }
                for (int i = 0; i < sortedGrammars.size(); i++) {
                    GRAMMAR grammar = sortedGrammars.get(i);
                    if (grammar.getID() == grammarId) {
                        int pos = i + 1;
                        if (pos < sortedGrammars.size()) {
                            GRAMMAR nextGrammar = sortedGrammars.get(pos);
                            grammarId = nextGrammar.getID();

                            wvGrammar.post(() -> {
                                showGrammarContent(nextGrammar);
                                sendGrammarWorkbookInStudyMode();

                                grammarDialog.showMoveToPreviousBook();
                                if (pos == sortedGrammars.size() - 1) {
                                    grammarDialog.hideMoveToNextBook();
                                } else {
                                    grammarDialog.showMoveToNextBook();
                                }
                            });
                        }
                        break;
                    }
                }
                break;
            case R.id.tv_move_to_previous_book:
                if (sortedGrammars == null) {
                    sortedGrammars = Voca.getSortedGrammarList(enumStudyLanguage, enumDisplayLanguage);
                }
                for (int i = 0; i < sortedGrammars.size(); i++) {
                    GRAMMAR grammar = sortedGrammars.get(i);
                    if (grammar.getID() == grammarId) {
                        int pos = i - 1;
                        if (pos >= 0) {
                            GRAMMAR previousGrammar = sortedGrammars.get(pos);
                            grammarId = previousGrammar.getID();

                            wvGrammar.post(() -> {
                                showGrammarContent(previousGrammar);
                                sendGrammarWorkbookInStudyMode();

                                grammarDialog.showMoveToNextBook();
                                if (pos == 0) {
                                    grammarDialog.hideMoveToPreviousBook();
                                } else {
                                    grammarDialog.showMoveToPreviousBook();
                                }
                            });
                        }
                        break;
                    }
                }
                break;
            case R.id.tv_sync_current_topic:
                wvGrammar.scrollTo(0, 0);
                sendGrammarWorkbookInStudyMode(true);
                break;
            case R.id.tv_select_wordbook:
                getGrammarList(0);
                break;
            case R.id.tv_save_as_a_last_wordbook:
                saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_YES, null);
                break;
            default:
                break;
        }
    };

    public void onRefreshUserListClick(boolean sendPN) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getStudyUserInfoInChatroom(
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        sendPN,
                        new DalApiListener<ChatRoomInfo>() {

                            @Override
                            public void onSuccess(ChatRoomInfo response) {
                                studyInfo = response;
                                activity.setStudyUserListToPopup(studyInfo);
                                setMainStudent();
                                setMainTutor();
                                notifyMainUserJoinOrExit();
                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getReadingList(int parentId) {
        getReadingList(parentId, false);
    }

    private void getReadingList(int parentId, boolean isRandom) {
        EnumLanguage enumDisplayLanguageOfCurrentUser = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        List<READING> readingList = Voca.getReadingList(parentId, enumStudyLanguage, enumDisplayLanguageOfCurrentUser);
        if (readingList.isEmpty()) {
            DLog.i(getLogTag(), "readingList is empty!");
        } else {
            if (isRandom) {
                int pos = new Random().nextInt(readingList.size());
                READING reading = readingList.get(pos);
                reading.setRandom(true);
                onReadingClickListener.onClick(null, reading);
            } else {
                if (parentId == 0) {
                    READING reading = new READING();
                    reading.setLANG_STUDY(enumStudyLanguage.getIdApi());
                    reading.setDISPLAY_LANG(enumDisplayLanguageOfCurrentUser.getIdApi());
                    reading.setNAME_ENG("Random");
                    reading.setNAME_KO("임의선택");
                    reading.setRandom(true);
                    readingList.add(0, reading);
                }
                recyclerViewDialog.setAdapter(new ArrayList<>(readingList), onReadingClickListener);
                recyclerViewDialog.show();
            }
        }
    }

    private OnClickListener onReadingClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            recyclerViewDialog.dismiss();
            if (object instanceof READING) {
                READING reading = (READING) object;
                if (reading.isRandom() && reading.getID() == 0) {
                    List<Object> contents = recyclerViewDialog.getContents();
                    if (contents.isEmpty()) {
                        DLog.i(getLogTag(), "readingList is empty!");
                    } else {
                        int pos = new Random().nextInt(contents.size() - 1) + 1;
                        READING reading1 = (READING) contents.get(pos);
                        reading1.setRandom(true);
                        onReadingClickListener.onClick(null, reading1);
                    }
                } else if (reading.getHAS_SUB_LIST() == 1) {
                    getReadingList(reading.getID(), reading.isRandom());
                } else {
                    lessonMode = Constant.API_VALUE.LESSON_MODE_READING;
                    makeReadingContentsJsonInStudyMode(reading.getID());
                }
            }
        }
    };

    private void makeReadingContentsJsonInStudyMode(int readingId) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().makeReadingContentsJsonInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        getStudentId(),
                        getStudentLangNative(),
                        getTutorId(),
                        readingId,
                        1,
                        new DalApiListener<VocaReading>() {

                            @Override
                            public void onSuccess(VocaReading response) {
                                if (response == null) {
                                    Loading.hide();
                                } else {
                                    onGetReadingContentsSuccess(response);
                                    lessonReadingId = response.getLessonReadingId();
                                    sendVocabookInStudyModeForReading(lessonReadingId, null);
                                    saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_NO, null);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getReadingContentsJsonInStudyMode() {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getReadingContentsJsonInStudyMode(
                        getStudyLang(),
                        getStudentId(),
                        lessonReadingId,
                        new DalApiListener<VocaReading>() {

                            @Override
                            public void onSuccess(VocaReading response) {
                                onGetReadingContentsSuccess(response);
                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onGetReadingContentsSuccess(VocaReading response) {
        vocaReading = response;
        if (vocaReading == null) return;
        //
        showButtonsForReadingMode();
        //
        studyOrderPos = 0;
        showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
        //
        int studyLang = enumStudyLanguage.getIdApi();
        Map<String, VocaStudyChat> map = new TreeMap<>();
        for (VocaStudyChat voca : vocaReading.getStudyVocaList()) {
            String key = Voca.getVocaDisplay(voca);
            if (!map.containsKey(key)) {
                voca.setDisplayEvaluation(false);
                voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                map.put(key, voca);
            }
        }
        vocaReading.setStudyVocaList(new ArrayList<>(map.values()));
        map = new TreeMap<>();
        for (VocaStudyChat voca : vocaReading.getAllVocaList()) {
            String key = Voca.getVocaDisplay(voca) + " (" + voca.getMeaning() + ")";
            if (!map.containsKey(key)) {
                voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_READING_VOCA_LIST);
                voca.setPnType(Constant.API_VALUE.PN_TYPE_READING_VOCA_LIST);
                voca.setDisplayEvaluation(false);
                voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                map.put(key, voca);
            }
        }
        vocaReading.setAllVocaList(new ArrayList<>(map.values()));
        for (VocaStudyChat voca : vocaReading.getExamVocaList()) {
            voca.setDisplayEvaluation(false);
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
        }
        showStudyOrderAlertForReading();
        //
        showOrHidePreviousAndNextReadingMenu();
    }

    private void sendVocabookInStudyModeForReading(int lessonReadingId, DalApiListener<Boolean> listener) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().sendVocabookInStudyMode(
                        getStudyLang(),
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        studyInfo.getVocaBook().getVocaBookId(),
                        Constant.API_VALUE.VALUE_SERVER_BOOK,
                        studyInfo.getVocaBook().getTopicBeginIndex(),
                        studyInfo.getVocaBook().getTopicRepeatedCount(),
                        lessonMode,
                        lessonReadingId,
                        listener
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void showStudyOrderAlertForReading() {
        if (vocaReading == null) return;

        int title, message;
        int total = 1 // this is mode 2/3
                + (vocaReading.getStudyVocaList().isEmpty() ? 0 : 1)
                + (vocaReading.getExamVocaList().isEmpty() ? 0 : 1);
        int pos = studyOrderPos - (vocaReading.getStudyVocaList().isEmpty() ? 0 : 1);
        if (pos == -1) {
            title = R.string.preview_words_list;
            message = R.string.preview_voca_list_guide_message;
            setRecyclerViewReadingData(vocaReading.getStudyVocaList());
        } else if (pos == 0) {
            title = R.string.reading_comprehension;
            message = R.string.reading_comprehension_guide_message;
            showReadingComprehension();
        } else {
            title = R.string.exam_words_list;
            message = R.string.exam_voca_list_guide_message;
            setRecyclerViewReadingData(new ArrayList<>(vocaReading.getExamVocaList()));
        }
        alertDialog.show(
                studyOrderTitle = activity.getString(title),
                studyOrderMessage = activity.getString(message, studyOrderPos + 1, total),
                activity.getString(R.string.ok),
                null
        );
    }

    private void showOrHidePreviousAndNextReadingMenu() {
        if (sortedReadings == null) {
            sortedReadings = Voca.getSortedReadingList(enumStudyLanguage, enumDisplayLanguage);
        }
        for (int i = 0; i < sortedReadings.size(); i++) {
            READING reading = sortedReadings.get(i);
            if (reading.getID() == vocaReading.getReadingId()) {
                onMessageChanged(reading.getNAME_());
                int pos = i;
                rvVoca.post(() -> {
                    if (pos == sortedReadings.size() - 1) {
                        readingsDialog.hideMoveToNextReading();
                    } else {
                        readingsDialog.showMoveToNextReading();
                    }
                    if (pos == 0) {
                        readingsDialog.hideMoveToPreviousReading();
                    } else {
                        readingsDialog.showMoveToPreviousReading();
                    }
                });
                break;
            }
        }
    }

    private void setRecyclerViewReadingData(List<VocaStudyChat> vocas) {
        activity.getPlayVocaHelper().stop();
        adapter = new StudyChatAdapter(context);
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setStudyRole(getMyStudyRole());
        adapter.setShowAsterisk(showAsterisk);
        adapter.setStudentAndTutorJoined(isStudentAndTutorJoined);
        adapter.setStudyLang(enumStudyLanguage.getIdApi());
        adapter.setHasBookName(false);
        adapter.setHasHeader(true);
        adapter.setShowStudentMeaning(showStudentMeaning);
        adapter.setListener(onVocaStudyChatClickListener);
        adapter.setData(vocas);
        rvVoca.setAdapter(adapter);
        if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
            rvVoca.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
            bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
        }

        rvVoca.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        vReading.setVisibility(View.GONE);
    }

    private void showReadingComprehension() {
        fvReading.setIsKnownPronounceMeaning(Voca.checkStudyLanguageJPCN(enumStudyLanguage));
        fvReading.resetText();
        if (enumStudyLanguage == EnumLanguage.ENGLISH) {
            fvReading.setTutor(true);
        } else {
            fvReading.setTutor(getMyStudyRole() == Constant.STUDY_ROLE_TUTOR);
        }
        fvReading.setShowMeaningJPCN(!Voca.checkStudyLanguageJPCN(enumStudyLanguage));
        fvReading.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        fvReading.setJText(vocaReading.getRubyText());

        Map<String, VocaStudyChat> difficultWordMap = getDifficultWordMap();
        if (difficultWordMap.isEmpty()) {
            tvReadingWords.setText("");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String word : difficultWordMap.keySet()) {
                stringBuilder.append(", ").append(word);
            }
            tvReadingWords.setText(stringBuilder.substring(2));
        }
        tvReadingMeaning.setText(vocaReading.getMeaning());

        rvVoca.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        vReading.setVisibility(View.VISIBLE);
    }

    private Map<String, VocaStudyChat> getDifficultWordMap() {
        Map<String, VocaStudyChat> map = new TreeMap<>();
        for (VocaStudyChat voca : vocaReading.getAllVocaList()) {
            if (voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || voca.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                String key = Voca.getVocaDisplay(voca) + " (" + voca.getMeaning() + ")";
                if (!map.containsKey(key)) {
                    map.put(key, voca);
                }
            }
        }
        return map;
    }

    private void onReadingReceived(Bundle eventData) {
        lessonReadingId = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.LESSON_READING_ID));
        lessonMode = Constant.API_VALUE.LESSON_MODE_READING;
        getReadingContentsJsonInStudyMode();
    }

    private void sendStudyOrderInReadingMode(int pos) {
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().showAsteriskInStudyMode(
                        chatRoomInfo.getFirestoreChatRoomId(),
                        getLessonId(),
                        pos,
                        null
                );
                onStudyOrderInReadingModeChanged(pos);
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void onStudyOrderInReadingModeChanged(int pos) {
        studyOrderPos = pos;
        rvVoca.post(this::showStudyOrderAlertForReading);
    }

    private List<VocaStudyChatExam> getExamVocas() {
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_EXAM)
            return examVocas;
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_READING)
            return vocaReading.getExamVocaList();
        return vocaFromAllVocaBook.getExamVocaList();
    }

    private DialogInterface.OnClickListener onReadingOptionClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_move_to_next_reading:
                if (sortedReadings == null) {
                    sortedReadings = Voca.getSortedReadingList(enumStudyLanguage, enumDisplayLanguage);
                }
                for (int i = 0; i < sortedReadings.size(); i++) {
                    READING reading = sortedReadings.get(i);
                    if (reading.getID() == vocaReading.getReadingId()) {
                        int pos = i + 1;
                        if (pos < sortedReadings.size()) {
                            READING nextReading = sortedReadings.get(pos);
                            makeReadingContentsJsonInStudyMode(nextReading.getID());

                            readingsDialog.showMoveToPreviousReading();
                            if (pos == sortedReadings.size() - 1) {
                                readingsDialog.hideMoveToNextReading();
                            } else {
                                readingsDialog.showMoveToNextReading();
                            }
                        }
                        break;
                    }
                }
                break;
            case R.id.tv_move_to_previous_reading:
                if (sortedReadings == null) {
                    sortedReadings = Voca.getSortedReadingList(enumStudyLanguage, enumDisplayLanguage);
                }
                for (int i = 0; i < sortedReadings.size(); i++) {
                    READING reading = sortedReadings.get(i);
                    if (reading.getID() == vocaReading.getReadingId()) {
                        int pos = i - 1;
                        if (pos >= 0) {
                            READING previousReading = sortedReadings.get(pos);
                            makeReadingContentsJsonInStudyMode(previousReading.getID());

                            readingsDialog.showMoveToNextReading();
                            if (pos == 0) {
                                readingsDialog.hideMoveToPreviousReading();
                            } else {
                                readingsDialog.showMoveToPreviousReading();
                            }
                        }
                        break;
                    }
                }
                break;
            case R.id.tv_refresh_current_reading:
                makeReadingContentsJsonInStudyMode(vocaReading.getReadingId());
                break;
            case R.id.tv_select_a_reading:
                getReadingList(0);
                break;
            case R.id.tv_save_as_the_last_reading:
                saveVocabookIDInStudyMode(studyInfo.getVocaBook().getVocaBookId(), 0, Constant.SHOW_TOAST_YES, null);
                break;
            case R.id.tv_change_word_known_status:
                StudyChatAdapter adapter = new StudyChatAdapter(context);
                adapter.setDataNoLoop(vocaReading.getAllVocaList());
                setRecyclerViewDataDialog(adapter, null);
                break;
            default:
                break;
        }
    };

    private void showToastReadingSaved() {
        if (sortedReadings == null) {
            sortedReadings = Voca.getSortedReadingList(enumStudyLanguage, enumDisplayLanguage);
        }
        for (READING reading : sortedReadings) {
            if (reading.getID() == vocaReading.getReadingId()) {
                String message = String.format(tplSavedAsALastWordbook, reading.getNAME_());
                rvVoca.post(() -> ToastUtil.getInstance(context).show( message));
                break;
            }
        }
    }

    private void onBackToRolePlayingMode() {
        int pos = studyOrderRolePlayingPos - rolePlayingContent.getAllSentenceListShowGuide() - rolePlayingContent.getAllVocaListShowGuide();
        if (pos == -2) {
            getAllSentenceListDetails(null, false);
        } else if (pos == -1) {
            if (rolePlayingContent.getAllSentenceListShowGuide() == Constant.API_VALUE.IS_YES
                    && rolePlayingContent.getAllVocaListShowGuide() == Constant.API_VALUE.IS_NO) {
                getAllSentenceListDetails(null, false);
            } else {
                getAllVocaListDetails(null, false);
            }
        } else {
            setRecyclerViewRolePlayingData();
        }
    }

    private void onBackToReadingMode() {
        if (vocaReading == null) return;
        //
        int pos = studyOrderPos - (vocaReading.getStudyVocaList().isEmpty() ? 0 : 1);
        if (pos == -1) {
            setRecyclerViewReadingData(vocaReading.getStudyVocaList());
        } else if (pos == 0) {
            showReadingComprehension();
        } else {
            setRecyclerViewReadingData(new ArrayList<>(vocaReading.getExamVocaList()));
        }
        //
        showOrHidePreviousAndNextReadingMenu();
    }

    private void onBackToNormalMode() {
        int pos = studyOrderPos;
        if (lessonMode == Constant.API_VALUE.LESSON_MODE_NORMAL) {
            pos -= vocaFromAllVocaBook.getStudyVocaList().isEmpty() ? 0 : 1;
        } // else means LESSON_MODE_REVIEW
        if (pos == -1) {
            setRecyclerViewPreviewOrExamData(vocaFromAllVocaBook.getStudyVocaList());
        } else if (pos < studyOrderList.size()) {
            if (adapter == null) {
                setRecyclerViewData();
            } else {
                adapter.setShowAsterisk(this.showAsterisk);
                if (rvVoca.getAdapter() == adapter) {
                    adapter.notifyDataSetChanged();
                } else {
                    rvVoca.setAdapter(adapter);
                }
            }
        } else {
            setRecyclerViewPreviewOrExamData(new ArrayList<>(vocaFromAllVocaBook.getExamVocaList()));
        }
    }

    private void playVoca(VocaStudyChat voca) {
        boolean isPlaying = voca.isVIPlaying();
        if (me == null || me.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
            activity.getPlayVocaHelper().stop();
            if (!isPlaying) {
                activity.preparePlayVoca(voca);
            }
        } else {
            studyChatPlayVocaHelper.stop();
            if (!isPlaying && mainStudent != null) {
                studyChatPlayVocaHelper.setOnInitListener(() -> studyChatPlayVocaHelper.play(String.valueOf(voca.getId()), Voca.getMeaningTTS(voca, activity)));
                studyChatPlayVocaHelper.initMotherTongueTTS(EnumLanguage.findByFormatApi(mainStudent.getLangNative()));
            }
        }
    }

    public void onShowHideStudentMeaningClick(boolean isShow) {
        showMeaningOnStudentViewInStudyMode(isShow);
        sendShowMeaningOnStudentViewInStudyModeToOthers(isShow);
    }

    private void showMeaningOnStudentViewInStudyMode(boolean isShow) {
        showStudentMeaning = isShow;
        if (rvVoca.getAdapter() instanceof StudyChatAdapter) {
            ((StudyChatAdapter) rvVoca.getAdapter()).setShowStudentMeaning(showStudentMeaning);
            ((StudyChatAdapter) rvVoca.getAdapter()).notifyDataSetChanged();
        } else if (rvVoca.getAdapter() instanceof StudyChatExamAdapter) {
            ((StudyChatExamAdapter) rvVoca.getAdapter()).setShowStudentMeaning(showStudentMeaning);
            ((StudyChatExamAdapter) rvVoca.getAdapter()).notifyDataSetChanged();
        } else if (rvVoca.getAdapter() instanceof RolePlayingAdapter) {
            ((RolePlayingAdapter) rvVoca.getAdapter()).setShowStudentMeaning(showStudentMeaning);
            ((RolePlayingAdapter) rvVoca.getAdapter()).notifyDataSetChanged();
        }
    }

    private void sendShowMeaningOnStudentViewInStudyModeToOthers(boolean isShow) {
        if (uid > 0 && Utils.isConnected(context)) {
            application.getDalAiImpl().showMeaningOnStudentViewInStudyMode(
                    getStudyLang(),
                    chatRoomInfo.getFirestoreChatRoomId(),
                    getLessonId(),
                    isShow,
                    null
            );
        }
    }

    private void onShowStudentMeaningReceived(Bundle eventData) {
        showStudentMeaning = Utils.parseInt(eventData.getString(Constant.NOTIFICATION_KEY.SHOW_MEANING_AT_STUDENT)) == Constant.API_VALUE.IS_YES;
        showMeaningOnStudentViewInStudyMode(showStudentMeaning);
        activity.setButtonShowHideStudentMeaningText(!showStudentMeaning);
    }
}
