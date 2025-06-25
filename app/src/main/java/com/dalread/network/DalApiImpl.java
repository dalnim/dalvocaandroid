package com.dalread.network;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.dalread.BaseApplication;
import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmAdd2ndGradeDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.model.ChatMessageResponse;
import com.dalread.model.ChatRoom;
import com.dalread.model.ChatRoomInfo;
import com.dalread.model.CurrentLesson;
import com.dalread.model.Lesson;
import com.dalread.model.LessonOption;
import com.dalread.model.MercuryModel;
import com.dalread.model.MultipleWordMeaningWithIDModel;
import com.dalread.model.NativeSpeaker;
import com.dalread.model.PointModel;
import com.dalread.model.RubyListModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.SearchVideoModel;
import com.dalread.model.StudentVoice;
import com.dalread.model.User;
import com.dalread.model.VideoInformationModel;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaCountInBook;
import com.dalread.model.VocaDetailInfo;
import com.dalread.model.VocaDoYouKnow;
import com.dalread.model.VocaDownload;
import com.dalread.model.VocaFeedback;
import com.dalread.model.VocaFromAllVocaBook;
import com.dalread.model.VocaHanja;
import com.dalread.model.VocaHanjaSearch;
import com.dalread.model.VocaHanjaSentence;
import com.dalread.model.VocaInBook;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.model.VocaMemorize;
import com.dalread.model.VocaPractice;
import com.dalread.model.VocaReading;
import com.dalread.model.VocaRecordListForBook;
import com.dalread.model.VocaSearch;
import com.dalread.model.VocaSearchOption;
import com.dalread.model.VocaStudy;
import com.dalread.model.VocaStudyChatByCategory;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaStudyChatRuby;
import com.dalread.model.VocaStudyHistory;
import com.dalread.model.roleplaying.Category;
import com.dalread.model.roleplaying.RolePlayingContent;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.network.models.LoginModel;
import com.dalread.network.models.SignUpModel;
import com.dalread.network.models.UserListResponse;
import com.dalread.network.models.VocaBookListNativeSpeakerResponse;
import com.dalread.network.models.VocaPracticeListResponse;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JetVHS on 02/26/2017.
 */
public class DalApiImpl {
    private final String TAG = "DalApiImpl";
    private BaseApplication mApplication;

    public interface SubtitleDownload {

        void resultready(ResponseBody body);

        void error(String result);
    }

    private SubtitleDownload subtitleDownload;

    public void setCallback(SubtitleDownload fllw_result) {
        this.subtitleDownload = fllw_result;
    }

    public DalApiImpl(BaseApplication mApplication) {
        this.mApplication = mApplication;
    }

    public void login(final BaseEvent.Screen screen, final String email, final String password, final int studyLang) {
        Call<LoginModel> call = mApplication.getDalApi().login(Constant.API.HEADER_CONTENT_TYPE,
                email, password, studyLang);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, final Response<LoginModel> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                    }
                    if (response.isSuccessful() && response.body().getUid() > 0) {
                        Headers headerList = response.headers();
//                        for (String s : headerList.names()) {
//                            DLog.d(TAG, "s=" + s + " - value=" + response.headers().get(s));
//                        }
                        String jsessionid = "";
                        String uid = "";
                        String token = "";
                        for (String header : response.headers().values("Set-Cookie")) {
                            if (!Utils.isEmpty(header)) {
                                DLog.d(TAG, "header=" + header);
                                if (header.contains("JSESSIONID")) {
                                    jsessionid = header.trim().substring(0, header.trim().indexOf(";"));
//                                    jsessionid = jsessionid.replaceFirst("JSESSIONID=","");
//                                    mApplication.getSharedPref().setSessionID(jsessionid);
                                } else if (header.contains("UID")) {
                                    uid = header.trim().substring(0, header.trim().indexOf(";"));
                                } else if (header.contains("TOKEN")) {
                                    token = header.trim().substring(0, header.trim().indexOf(";"));
//                                    mApplication.getSharedPref().setToken(header);
                                }
                            }
                        }
                        mApplication.getSharedPref().setToken(jsessionid + ";" + uid + ";" + token);

//                        String cookie = response.headers().get("Set-Cookie");
//                        DLog.d(TAG, "cookie=" + cookie);
                        mApplication.getSharedPref().setUid(response.body().getUid());
                        mApplication.getSharedPref().setUserName(response.body().getName());
                        mApplication.getSharedPref().setEmail(response.body().getEmail());
                        mApplication.getSharedPref().setPointReading(response.body().getPointReading());
                        mApplication.getSharedPref().setPointVoca(response.body().getPointVoca());
                        mApplication.getSharedPref().setUserRole(response.body().getUserRole());
                        mApplication.getSharedPref().setUserType(response.body().getUserType());
                        mApplication.getSharedPref().setSex(response.body().getSex());
                        mApplication.getSharedPref().setAge(response.body().getAge());
                        mApplication.getSharedPref().setMaxHomework(response.body().getMaxHomework());
                        mApplication.getSharedPref().setMaxQuiz(response.body().getMaxQuiz());
                        mApplication.getSharedPref().setMaxHanjaQuiz(response.body().getMaxHanjaQuiz());
                        mApplication.getSharedPref().setAllowChat(response.body().getAllowChat() == 1);
                        mApplication.getSharedPref().setShareMyRecordings(response.body().getShareMyRecording() == 1);
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.LOGIN, response.body()));
                        updateFirebaseToken();
                        DalFlavor.onLoginSuccess(mApplication.getApplicationContext());
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGIN, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGIN));
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGIN));
            }
        });
    }

    public void logoutOnClient() {
        // logout on client first, no need to wait for server
        clearFirebaseToken();
        mApplication.getSharedPref().logout();
        DalFlavor.onLogoutSuccess(mApplication.getApplicationContext());
    }

    public void logout(final BaseEvent.Screen screen,
                       final String token,
                       final String uid,
                       final String email) {
        // logout on client first, no need to wait for server
        logoutOnClient();

        // logout on server
        Call<ResponseBody> call = mApplication.getDalApi().logout(token, Constant.API.HEADER_CONTENT_TYPE, uid, email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.LOGOUT, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGOUT, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGOUT));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.LOGOUT));
            }
        });
    }

    public void logout(final String token,
                       final String uid,
                       final String email, final DalApiListener<Boolean> listener) {
        // logout on client first, no need to wait for server
        logoutOnClient();

        // logout on server
        Call<ResponseBody> call = mApplication.getDalApi().logout(token, Constant.API.HEADER_CONTENT_TYPE, uid, email);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void deleteAccount(final BaseEvent.Screen screen,
                       final String token,
                       final String email,
                       final String password) {
        Call<ResponseBody> call = mApplication.getDalApi().deleteAccount(token, Constant.API.HEADER_CONTENT_TYPE, email, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response != null && response.isSuccessful()) {
                        String data = response.body().string();
                        DLog.d(TAG, data);
                        if (data.equalsIgnoreCase("true")) {
                            logoutOnClient();
                            mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.DELETE_ACCOUNT, response.body()));
                            return;
                        }
                    }
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DELETE_ACCOUNT, response.body()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DELETE_ACCOUNT));
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DELETE_ACCOUNT));
            }
        });
    }

    public void isNewUser(final BaseEvent.Screen screen, final String idMail) {
        Call<ResponseBody> call = mApplication.getDalApi().isNewUser(Constant.API.HEADER_CONTENT_TYPE, idMail);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        if (data.equalsIgnoreCase("true")) {
                            mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.ISNEWUSER, response.body()));
                        } else {
                            mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ISNEWUSER, response.body()));
                        }
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ISNEWUSER, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ISNEWUSER));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ISNEWUSER));
            }
        });
    }

    public void signUp(final BaseEvent.Screen screen, final String email, final String password, final String name, final int studyLang, final String langNative, final String wordLevel) {
        Call<SignUpModel> call = mApplication.getDalApi().signUp(Constant.API.HEADER_CONTENT_TYPE, email, password, name, studyLang, langNative, wordLevel,
                Constant.CLIENT_TYPE_ANDROID, mApplication.getString(R.string.app_name), Utils.getAppVersion(), Build.MANUFACTURER + " " + Build.MODEL, Build.VERSION.RELEASE);
        call.enqueue(new Callback<SignUpModel>() {
            @Override
            public void onResponse(Call<SignUpModel> call, final Response<SignUpModel> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.SIGN_UP, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.SIGN_UP, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.SIGN_UP));
                }
            }

            @Override
            public void onFailure(Call<SignUpModel> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.SIGN_UP));
            }
        });
    }

    public void resetPassword(final BaseEvent.Screen screen, final String token, final String email, final String password, final String newPassword) {
        Call<ResponseBody> call = mApplication.getDalApi().ResetPassword(token, Constant.API.HEADER_CONTENT_TYPE, email, password, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.CHANGE_PASSWORD, Utils.getMessageResponsePassword(data)));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.CHANGE_PASSWORD, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.CHANGE_PASSWORD));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.CHANGE_PASSWORD));
            }
        });
    }

    public void sendMainContent(final BaseEvent.Screen screen, final String url) {
        Call<MercuryModel> call = mApplication.getDalApi().sendMainContent(Constant.MERCURY_API.HEADER_CONTENT_TYPE, Constant.MERCURY_API.BASE_URL, url, Constant.MERCURY_API.KEY_API);
        call.enqueue(new Callback<MercuryModel>() {
            @Override
            public void onResponse(Call<MercuryModel> call, final Response<MercuryModel> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                    }
                    if (response.isSuccessful()) {
                        if (Utils.isEmpty(response.body().getContent())) {
                            mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.MERCURY, R.string.error_mercury_cannot_read));
                        } else {
                            mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.MERCURY, response.body()));
                        }
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.MERCURY, R.string.error_mercury_msg));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.MERCURY, R.string.error_mercury_msg));
                }
            }

            @Override
            public void onFailure(Call<MercuryModel> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.MERCURY, R.string.error_mercury_msg));
            }
        });
    }

    //    public void dalReadText(final BaseEvent.Screen screen, final String token, final String sessionId, final String comment, final String uid, final String studyLang, final String langDisplay) {
//        Call<ResponseBody> call = mApplication.getDalApi().dalReadText(token, Constant.API.HEADER_CONTENT_TYPE, sessionId, comment, uid, studyLang, langDisplay);
    public void dalReadText(final BaseEvent.Screen screen, final String token, final String comment, final String uid, final int studyLang, final int langDisplay) {
        Call<ResponseBody> call = mApplication.getDalApi().dalReadText(token, Constant.API.HEADER_CONTENT_TYPE, comment, uid, studyLang, langDisplay);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.DAL_READ_TEXT, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DAL_READ_TEXT, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DAL_READ_TEXT));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.DAL_READ_TEXT));
            }
        });
    }

    public void getWordList(final BaseEvent.Screen screen, final String token, final String comment, final String uid, final int studyLang, final int langDisplay) {
        Call<ResponseBody> call = mApplication.getDalApi().getWordList(token, Constant.API.HEADER_CONTENT_TYPE, comment, uid, studyLang, langDisplay);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.WORD_LIST, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_LIST, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_LIST));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_LIST));
            }
        });
    }

    //    Call<ResponseBody> bookmarkDel(@Header("Cookie") String token, @Header("Content-Type") String contentType, @Field("UID") String uid, @Field("studyLang") String studyLang, @Field("word") String word, @Field("strPOS") String strPos);
//    public void bookmarkAdd(final BaseEvent.Screen screen, final String sessionId, final String uid, final String studyLang, final String word, final String strPos) {
//        Call<ResponseBody> call = mApplication.getDalApi().bookmarkAdd(Constant.API.HEADER_CONTENT_TYPE, sessionId, uid, studyLang, word, strPos);
    public void bookmarkAdd(final BaseEvent.Screen screen, final String token, final String uid, final int langStudyCode, final String voca, final String vocaId, int vocaType) {
        Call<ResponseBody> call = mApplication.getDalApi().bookmarkAdd(token, Constant.API.HEADER_CONTENT_TYPE, uid, langStudyCode, voca, vocaId, vocaType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.BOOKMARK_ADD, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_ADD, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_ADD));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_ADD));
            }
        });
    }

    //    public void bookmarkDel(final BaseEvent.Screen screen, final String sessionId, final String uid, final String studyLang, final String word, final String strPos) {
//        Call<ResponseBody> call = mApplication.getDalApi().bookmarkDel(Constant.API.HEADER_CONTENT_TYPE, sessionId, uid, studyLang, word, strPos);
    public void bookmarkDel(final BaseEvent.Screen screen, final String token, final String uid, final int langStudyCode, final String voca, final String vocaId, int vocaType) {
        Call<ResponseBody> call = mApplication.getDalApi().bookmarkDel(token, Constant.API.HEADER_CONTENT_TYPE, uid, langStudyCode, voca, vocaId, vocaType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.BOOKMARK_DEL, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_DEL, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_DEL));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.BOOKMARK_DEL));
            }
        });
    }

    public void wordUnknown(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang, final String voca, final String vocaId) {
        Call<ResponseBody> call = mApplication.getDalApi().wordUnknown(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, voca, vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, "wordUnknown=" + response.body().string());
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.WORD_UNKNOWN, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_UNKNOWN, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_UNKNOWN));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_UNKNOWN));
            }
        });
    }

    public void wordKnown(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang, final String voca, final String vocaId) {
        Call<ResponseBody> call = mApplication.getDalApi().wordKnown(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, voca, vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, "wordKnown=" + response.body().string());
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.WORD_KNOWN, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_KNOWN, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_KNOWN));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.WORD_KNOWN));
            }
        });
    }

    public void getHanajInfo(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang, final String dispMeaningLang, final String strWords) {
        Call<ResponseBody> call = mApplication.getDalApi().getHanajInfo(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, dispMeaningLang, strWords);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_HANAJ_INFO, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_HANAJ_INFO, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_HANAJ_INFO));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_HANAJ_INFO));
            }
        });
    }

    public void updateWordMeaning(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang, final int dispMeaningLang, final String vocaId, int vocaType, final String voca, final String meaning, final String meaningDetail, final String pronounce) {
        Call<Boolean> call = mApplication.getDalApi().updateWordMeaningWithID(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, dispMeaningLang, vocaId, vocaType, voca, meaning, meaningDetail, pronounce);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                        if (response.isSuccessful()) {
                            mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.UPDATE_WORD_MEANING, response.body()));
                        } else {
                            mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_MEANING, response.body()));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_MEANING));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_MEANING));
            }
        });
    }

    public void pointReadingReduce(final BaseEvent.Screen screen, final String token, final String uid, final String email) {
        Call<ResponseBody> call = mApplication.getDalApi().pointReadingReduce(token, Constant.API.HEADER_CONTENT_TYPE, uid, email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.POINT_READING_REDUCE, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.POINT_READING_REDUCE, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.POINT_READING_REDUCE));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.POINT_READING_REDUCE));
            }
        });
    }

    public void getCountUnknown(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang) {
        Call<ResponseBody> call = mApplication.getDalApi().getCountUnknown(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    int value = 0;
                    if (response != null) {
                        value = Utils.parseInt(response.body().string());
                        DLog.d(TAG, "value=" + value);
                    }
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_UNKNOWN, value));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_UNKNOWN, 0));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_UNKNOWN, 0));
            }
        });
    }

    public void getCountKnown(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang) {
        Call<ResponseBody> call = mApplication.getDalApi().getCountKnown(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    int value = 0;
                    if (response != null) {
                        value = Utils.parseInt(response.body().string());
                        DLog.d(TAG, "value=" + value);
                    }
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_KNOWN, value));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_KNOWN, 0));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_KNOWN, 0));
            }
        });
    }

    public void getCountBookmarked(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang) {
        Call<ResponseBody> call = mApplication.getDalApi().getCountBookmarked(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    int value = 0;
                    if (response != null) {
                        value = Utils.parseInt(response.body().string());
                        DLog.d(TAG, "value=" + value);
                    }
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_BOOKMARK, value));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_BOOKMARK, 0));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_COUNT_BOOKMARK, 0));
            }
        });
    }

    public void updateWordLevel(final BaseEvent.Screen screen, final String token, final String uid, final int studyLang, final String wordLevel) {
        Call<ResponseBody> call = mApplication.getDalApi().updateWordLevel(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, wordLevel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.UPDATE_WORD_LEVEL, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_LEVEL, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_LEVEL));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.UPDATE_WORD_LEVEL));
            }
        });
    }

    public void getInAppListOfPoint(final BaseEvent.Screen screen, final String token) {
        Call<List<PointModel>> call = mApplication.getDalApi().getInAppListOfPoint(token, Constant.API.HEADER_CONTENT_TYPE, Constant.API.APP_NAME, EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), Constant.API.IN_APP_TYPE);
        call.enqueue(new Callback<List<PointModel>>() {
            @Override
            public void onResponse(Call<List<PointModel>> call, final Response<List<PointModel>> response) {
                try {
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.GET_LIST_OF_POINT, response.body()));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_LIST_OF_POINT, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_LIST_OF_POINT));
                }
            }

            @Override
            public void onFailure(Call<List<PointModel>> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.GET_LIST_OF_POINT));
            }
        });
    }

    public void addPointUser(final BaseEvent.Screen screen, final String token, final String uid, final String email, final String point) {
        Call<ResponseBody> call = mApplication.getDalApi().addPointUser(token, Constant.API.HEADER_CONTENT_TYPE, uid, email, Constant.API.APP_NAME, EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), point, Constant.API.CLIENT_TYPE);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String data = "";
                    if (response != null) {
                        data = response.body().string();
                        DLog.d(TAG, data);
                    }
                    if (response.isSuccessful()) {
                        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.ADD_POINT_USER, data));
                    } else {
                        mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ADD_POINT_USER, response.body()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ADD_POINT_USER));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                mApplication.getEventBus().post(new ErrorEvent(screen, BaseEvent.EventType.ADD_POINT_USER));
            }
        });
    }

    private static void printRequest(final Request request) {
        Log.e("-----------", "-------------------------------------------------------------------------");
        try {
            final okio.Buffer buffer = new okio.Buffer();
            request.body().writeTo(buffer);
            Log.e("WEB_SERVICE", "BODY \t-> " + buffer.readUtf8());
            Log.e("WEB_SERVICE", "URL\t-> " + request.url().toString());
            Log.e("Headers--> cookie=", request.header("Cookie"));
            // Log.e("Headers--> Content=", request.header("Content-Type"));

            Log.e("-----------", "-------------------------------------------------------------------------");
        } catch (IOException | StringIndexOutOfBoundsException e) {
            Log.e("WEB_SERVICE", e.getMessage());
        }
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public void offlineDataToServer(final Context context, final ApiListener apiListener, String token, String email, String displaylng, String uid,
                                    String toknown, String tounknown, String toBookmark, String toUnbookmark, String toknownpronounce, String tounknownPronounce, String changeMeaning,
                                    String changePronounce) {
        if (email.trim().isEmpty()) {
            email = Constant.DEFAULT_USER_EMAIL;
        }
        HashMap<String, RequestBody> partMap = new HashMap<>();
        partMap.put("UID", createPartFromString(uid));
        partMap.put("studyLang", createPartFromString(Constant.API.STUDY_LANG));
        partMap.put("EMAIL", createPartFromString(email));
        partMap.put("langDisplay", createPartFromString(displaylng));
        partMap.put("ToKnown", createPartFromString(toknown));
        partMap.put("ToUnknown", createPartFromString(tounknown));
        partMap.put("ToKnownPronounce", createPartFromString(toknownpronounce));
        partMap.put("ToUnknownPronounce", createPartFromString(tounknownPronounce));
        partMap.put("ToBookmark", createPartFromString(toBookmark));
        partMap.put("ToUnbookmark", createPartFromString(toUnbookmark));
        partMap.put("ChangeMeanings", createPartFromString(changeMeaning));
        partMap.put("ChangePronounces", createPartFromString(changePronounce));
        Call<ResponseBody> call = mApplication.getDalApi().offlineDataToServer(token, partMap);
        printRequest(call.request());
        Toast.makeText(context, "Sending data on:" + call.request().url().toString(), Toast.LENGTH_LONG).show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Toast.makeText(context, "getting response:", Toast.LENGTH_LONG).show();

                    String data = "";
                    if (response != null) {
                        Toast.makeText(context, " response is not null", Toast.LENGTH_LONG).show();

                        data = response.body().toString();
                        Log.e("OFFLINE", data);
                    }
                    if (response.isSuccessful()) {
                        Toast.makeText(context, " response is successful http code 200", Toast.LENGTH_LONG).show();

                        apiListener.onSucess(data);
                    } else {
                        Toast.makeText(context, " response is failed", Toast.LENGTH_LONG).show();

                        apiListener.onFailure(data);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        Log.e("OFFLINE", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                apiListener.onFailure("ERROR IN API");
            }
        });
    }

    /**
     * DalVoca
     */
    public void getToMemorizeVocaList(String uid, int studyLang, int langDisplay, final DalApiListener<List<VocaStudy>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaStudy>> call = mApplication.getDalApi().getToMemorizeVocaList(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay);
        call.enqueue(new Callback<List<VocaStudy>>() {

            @Override
            public void onResponse(Call<List<VocaStudy>> call, Response<List<VocaStudy>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudy>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(t.getMessage());
                }
            }
        });
    }

    public void getPronounceFeedback(final DalApiListener<List<VocaFeedback>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaFeedback>> call = mApplication.getDalApi().getPronounceFeedback(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaFeedback>>() {

            @Override
            public void onResponse(Call<List<VocaFeedback>> call, Response<List<VocaFeedback>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaFeedback>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getUserVocaBookList(String uid, int studyLang, int langDisplay, final DalApiListener<List<VocaBook>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaBook>> call = mApplication.getDalApi().getUserVocaBookList(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay);
        call.enqueue(new Callback<List<VocaBook>>() {

            @Override
            public void onResponse(Call<List<VocaBook>> call, Response<List<VocaBook>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaBook>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getServerVocaBookList(int studyLang, int langDisplay, final DalApiListener<List<VocaBook>> listener) {
        Call<List<VocaBook>> call = mApplication.getDalApi().getServerVocaBookList(studyLang, langDisplay);
        call.enqueue(new Callback<List<VocaBook>>() {

            @Override
            public void onResponse(Call<List<VocaBook>> call, Response<List<VocaBook>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaBook>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getServerVocaBookListByCategory(int studyLang, int langDisplay, int parentId, int practiceOnly, final DalApiListener<List<VocaBook>> listener) {
        getServerVocaBookListByCategory(studyLang, langDisplay, parentId, practiceOnly, Constant.API_VALUE.VOCABOOK_ALPHABET_ONLY_NO, listener);
    }

    public void getServerVocaBookListByCategory(int studyLang, int langDisplay, int parentId, int practiceOnly, int alphabetOnly, final DalApiListener<List<VocaBook>> listener) {
        Call<List<VocaBook>> call = mApplication.getDalApi().getServerVocaBookListByCategory(studyLang, langDisplay, parentId, practiceOnly, alphabetOnly);
        call.enqueue(new Callback<List<VocaBook>>() {

            @Override
            public void onResponse(Call<List<VocaBook>> call, Response<List<VocaBook>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaBook>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAllVocaBookList(String studentId, int studyLang, int langDisplay, final DalApiListener<AllBookListResponse> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<AllBookListResponse> call = mApplication.getDalApi().getAllVocaBookList(token, Constant.API.HEADER_CONTENT_TYPE, studentId, studyLang, langDisplay);
        call.enqueue(new Callback<AllBookListResponse>() {

            @Override
            public void onResponse(Call<AllBookListResponse> call, Response<AllBookListResponse> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<AllBookListResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void addVocaBookInUserVocaBookList(String uid, int studyLang, int langDisplay, String bookName, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().addVocaBookInUserVocaBookList(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay, bookName);
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void deleteVocaBookInUserVocaBookList(String uid, int studyLang, int langDisplay, int bookId, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().deleteVocaBookInUserVocaBookList(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay, bookId);
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromAllVocaBook(String uid, int studyLang, int langDisplay,
                                        String bookId, int bookType, int startNo, int countRecord,
                                        int makeRubyText, final DalApiListener<List<VocaInBook>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaInBook>> call = mApplication.getDalApi().getVocasFromAllVocaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay,
                bookId, bookType, startNo, countRecord, makeRubyText
        );
        call.enqueue(new Callback<List<VocaInBook>>() {

            @Override
            public void onResponse(Call<List<VocaInBook>> call, Response<List<VocaInBook>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaInBook>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasKnownWordCountFromAllVocaBook(String uid, int studyLang, String bookId, int bookType, final DalApiListener<VocaCountInBook> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaCountInBook> call = mApplication.getDalApi().getVocasKnownWordCountFromAllVocaBook(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, bookId, bookType);
        call.enqueue(new Callback<VocaCountInBook>() {

            @Override
            public void onResponse(Call<VocaCountInBook> call, Response<VocaCountInBook> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaCountInBook> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getTargetVocaCount(String uid, int studyLang, final DalApiListener<Integer> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Integer> call = mApplication.getDalApi().getTargetVocaCount(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang);
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromTargetVoca(String uid, int studyLang, int langDisplay, final DalApiListener<List<VocaMemorize>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaMemorize>> call = mApplication.getDalApi().getVocasFromTargetVoca(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay);
        call.enqueue(new Callback<List<VocaMemorize>>() {

            @Override
            public void onResponse(Call<List<VocaMemorize>> call, Response<List<VocaMemorize>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaMemorize>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromAllVocas(String uid, int studyLang, int langDisplay, VocaSearchOption searchOption, final DalApiListener<List<VocaSearch>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaSearch>> call = mApplication.getDalApi().getVocasFromAllVocas(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay,
                searchOption.getBookId(), searchOption.getStartNo(), searchOption.getCountRecord(), searchOption.getVocaType(), searchOption.getText(), searchOption.getWhere(), searchOption.getUseRegex()
        );
        call.enqueue(new Callback<List<VocaSearch>>() {

            @Override
            public void onResponse(Call<List<VocaSearch>> call, Response<List<VocaSearch>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaSearch>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void addVocaInUserVocaBook(String uid, int studyLang, String bookId, String vocaId, int vocaType, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().addVocaInUserVocaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, bookId, vocaId, vocaType
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void addVocasInUserVocaBook(String uid, int studyLang, String bookId,
                                       String vocaId, String vocaType,
                                       String useServerVocabookData, String idInServerVocabook,
                                       final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().addVocasInUserVocaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, bookId, vocaId, vocaType,
                useServerVocabookData, idInServerVocabook
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void deleteVocaInUserVocaBook(String uid, int studyLang, String bookId, String vocaId, int vocaType, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().deleteVocaInUserVocaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, bookId, vocaId, vocaType
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void checkLocalAndChangeVocaKnow(Context context, int vocaKnow, int vocaKnowPronounce, String vocaId, int vocaType, DalApiListener<Integer> listener) {
        changeMultipleVocaKnow(context, vocaKnow, vocaKnowPronounce, vocaId, String.valueOf(vocaType), listener);
    }

    public void checkAndChangeVocaKnow(Context context, int vocaKnow, int vocaKnowPronounce, String vocaId, int vocaType, DalApiListener<Integer> listener) {
        if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
            getCountOf1StAmkiGrade(new DalApiListener<Integer>() {

                @Override
                public void onSuccess(Integer response) {
                    if (response >= Constant.GRADE_1_MAX) {
                        new ConfirmAdd2ndGradeDialog(context, new ConfirmationDialog.OnDialogClickListener() {

                            @Override
                            public void onPositive(DialogInterface dialog) {
                                dialog.dismiss();
                                changeMultipleVocaKnow(context, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, vocaKnowPronounce, vocaId, String.valueOf(vocaType), listener);
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        changeMultipleVocaKnow(context, vocaKnow, vocaKnowPronounce, vocaId, String.valueOf(vocaType), listener);
                    }
                }

                @Override
                public void onFailure(String error) {
                }
            });
        } else {
            changeMultipleVocaKnow(context, vocaKnow, vocaKnowPronounce, vocaId, String.valueOf(vocaType), listener);
        }
    }

    public void changeMultipleVocaKnow(Context context, int vocaKnow, int vocaKnowPronounce, String vocaId, String vocaType, final DalApiListener<Integer> listener) {
        changeMultipleVocaKnow(context, vocaKnow, vocaKnowPronounce, vocaId, vocaType, true, listener);
    }

    public void changeMultipleVocaKnow(Context context, int vocaKnow, int vocaKnowPronounce, String vocaId, String vocaType, boolean showDialog, DalApiListener<Integer> listener) {
        if (listener != null) {
            listener.onSuccess(vocaKnow);
        }
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        int msg = 0;
        if (BaseVocaKnow.isAmkiGrade1(vocaKnow)) {
            if (sharedPreferences.getAlertAmkiGrade1()) {
                msg = R.string.msg_amki_grade_1;
                sharedPreferences.setAlertAmkiGrade1(false);
            }
        } else if (BaseVocaKnow.isAmkiGrade2(vocaKnow)) {
            if (sharedPreferences.getAlertAmkiGrade2()) {
                msg = R.string.msg_amki_grade_2;
                sharedPreferences.setAlertAmkiGrade2(false);
            }
        } else if (BaseVocaKnow.isKnown(vocaKnow)) {
            if (sharedPreferences.getAlertAmkiGradeKnown()) {
                msg = R.string.msg_amki_grade_known;
                sharedPreferences.setAlertAmkiGradeKnown(false);
            }
        } else {
            if (sharedPreferences.getAlertAmkiGradeUnknown()) {
                msg = R.string.msg_amki_grade_unknown;
                sharedPreferences.setAlertAmkiGradeUnknown(false);
            }
        }
        if (showDialog && msg != 0) {
            new AlertDialog(context).show(msg, 0, null);
        }
        String token = sharedPreferences.getToken();
        String uid = sharedPreferences.getUid();
        int studyLang = sharedPreferences.getLangStudyCode();
        Call<Boolean> call = mApplication.getDalApi().changeMultipleVocaKnow(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang,
                String.valueOf(vocaKnow),
                String.valueOf(vocaKnowPronounce),
                vocaId,
                vocaType
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void changeMultipleVocaKnow(String vocaKnow, String vocaKnowPronounce, String vocaIds, String vocaTypes, DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().changeMultipleVocaKnow(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                vocaKnow,
                vocaKnowPronounce,
                vocaIds,
                vocaTypes
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getStudentStudiedHistoryByDate(String uid, int studyLang, int langDisplay, int startNo, int countRecord, final DalApiListener<List<VocaStudyHistory>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaStudyHistory>> call = mApplication.getDalApi().getStudentStudiedHistoryByDate(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, langDisplay,
                startNo, countRecord);
        call.enqueue(new Callback<List<VocaStudyHistory>>() {

            @Override
            public void onResponse(Call<List<VocaStudyHistory>> call, Response<List<VocaStudyHistory>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyHistory>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getStudentStudiedHistoryByVoca(String uid, int studyLang, int langDisplay, int startNo, int countRecord, final DalApiListener<List<VocaStudyHistory>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaStudyHistory>> call = mApplication.getDalApi().getStudentStudiedHistoryByVoca(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, langDisplay,
                startNo, countRecord);
        call.enqueue(new Callback<List<VocaStudyHistory>>() {

            @Override
            public void onResponse(Call<List<VocaStudyHistory>> call, Response<List<VocaStudyHistory>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyHistory>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendStudyListToSendStudent(String uid, int studyLang, int langDisplay, String vocaId, int vocaType, String studentIds) {
        String token = mApplication.getSharedPref().getToken();
        Call<String> call = mApplication.getDalApi().sendStudyListToSendStudent(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, langDisplay,
                vocaId, vocaType,
                studentIds);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("HUY", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("HUY", t.getMessage());
            }
        });
    }

    public void updateFinishStudy(String uid, int studyLang, int tutorId, String vocaId, int vocaType, int id, String extension, long fileSize, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateFinishStudy(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, tutorId,
                vocaId, vocaType, id,
                extension,
                fileSize
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getHanajInfoDalVoca(String uid, int studyLang, String dispMeaningLang, String strWords, final DalApiListener<String> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<ResponseBody> call = mApplication.getDalApi().getHanajInfo(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, dispMeaningLang,
                strWords
        );
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    String text;
                    try {
                        text = response.body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                        text = "";
                    }
                    listener.onSuccess(text);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocaDetailInfo(String uid, int studyLang, int langDisplay, int vocaId, int vocaType, final DalApiListener<VocaDetailInfo> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaDetailInfo> call = mApplication.getDalApi().getVocaDetailInfo(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, langDisplay,
                vocaId, vocaType
        );
        call.enqueue(new Callback<VocaDetailInfo>() {

            @Override
            public void onResponse(Call<VocaDetailInfo> call, final Response<VocaDetailInfo> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaDetailInfo> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocaDetailInfoForOneHanja(String voca, DalApiListener<VocaHanja> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<VocaHanja> call = mApplication.getDalApi().getVocaDetailInfoForOneHanja(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                voca, sharedPreferences.getStudyLanguage(), sharedPreferences.getMotherTongueLanguage()
        );
        call.enqueue(new Callback<VocaHanja>() {

            @Override
            public void onResponse(Call<VocaHanja> call, Response<VocaHanja> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaHanja> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocaDetailInfoForHanjaSentence(String voca, DalApiListener<VocaHanjaSentence> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<VocaHanjaSentence> call = mApplication.getDalApi().getVocaDetailInfoForHanjaSentence(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                voca, sharedPreferences.getStudyLanguage(), sharedPreferences.getMotherTongueLanguage()
        );
        call.enqueue(new Callback<VocaHanjaSentence>() {

            @Override
            public void onResponse(Call<VocaHanjaSentence> call, Response<VocaHanjaSentence> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaHanjaSentence> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateVoiceFileInfo(String uid, int studyLang, String vocaId, int vocaType, String extension, long fileSize, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateVoiceFileInfo(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang,
                vocaId, vocaType,
                extension,
                fileSize
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAllNativeSpeakers(String uid, int studyLang, final DalApiListener<List<NativeSpeaker>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<NativeSpeaker>> call = mApplication.getDalApi().getAllNativeSpeakers(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang
        );
        call.enqueue(new Callback<List<NativeSpeaker>>() {

            @Override
            public void onResponse(Call<List<NativeSpeaker>> call, final Response<List<NativeSpeaker>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<NativeSpeaker>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void changePreferredNativeSpeakers(String uid, int studyLang, String speakerIds) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().changePreferredNativeSpeakers(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang,
                speakerIds
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void changeStudyLang(String uid, int studyLang) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().changeStudyLang(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
                if (response.body()) {
                    updateFirebaseToken();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void getMultipleVoiceFileVersion(String uid, int studyLang, String fileNames, final DalApiListener<List<VocaDownload>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaDownload>> call = mApplication.getDalApi().getMultipleVoiceFileVersion(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang,
                fileNames
        );
        call.enqueue(new Callback<List<VocaDownload>>() {

            @Override
            public void onResponse(Call<List<VocaDownload>> call, final Response<List<VocaDownload>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaDownload>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void changeVocaDisplayOrderInUserVocaBook(String uid, int studyLang, String bookId, String vocaId, int vocaType, int displayOrder, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().changeVocaDisplayOrderInUserVocaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, bookId, vocaId, vocaType, displayOrder
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocaListToRecordAllFromServerWordBook(String uid, int studyLang, int langDisplay, String bookId, int startNo, int countRecord, final DalApiListener<VocaRecordListForBook> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaRecordListForBook> call = mApplication.getDalApi().getVocaListToRecordAllFromServerWordBook(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay, bookId, startNo, countRecord);
        call.enqueue(new Callback<VocaRecordListForBook>() {

            @Override
            public void onResponse(Call<VocaRecordListForBook> call, Response<VocaRecordListForBook> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaRecordListForBook> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateWordMeaningWithID(String vocaId, int vocaType, String voca, String meaning, String meaningDetail, String pronounce, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().updateWordMeaningWithID(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                vocaId,
                vocaType,
                voca,
                meaning,
                meaningDetail,
                pronounce
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getExpressionOfToday(String uid, int studyLang, int langDisplay, final DalApiListener<VocaStudy> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaStudy> call = mApplication.getDalApi().getExpressionOfToday(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay
        );
        call.enqueue(new Callback<VocaStudy>() {

            @Override
            public void onResponse(Call<VocaStudy> call, Response<VocaStudy> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaStudy> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateLastAccessDate(String uid, int studyLang, String email, int accessOrExitApp) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateLastAccessDate(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, email, accessOrExitApp, studyLang,
                Constant.CLIENT_TYPE_ANDROID, mApplication.getString(R.string.app_name), Utils.getAppVersion(), Build.MANUFACTURER + " " + Build.MODEL, Build.VERSION.RELEASE
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void getListOfStudentForThisVoca(String uid, int studyLang, String vocaId, int vocaType, final DalApiListener<List<StudentVoice>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<StudentVoice>> call = mApplication.getDalApi().getListOfStudentForThisVoca(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, vocaId, vocaType
        );
        call.enqueue(new Callback<List<StudentVoice>>() {

            @Override
            public void onResponse(Call<List<StudentVoice>> call, Response<List<StudentVoice>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<StudentVoice>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void clearFirebaseToken() {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        updateFirebaseToken(
                sharedPreferences.getUid(),
                sharedPreferences.getEmail(),
                "",
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                null
        );
    }

    public void updateFirebaseToken() {
        updateFirebaseToken(null);
    }

    public void updateFirebaseToken(DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        updateFirebaseToken(
                sharedPreferences.getUid(),
                sharedPreferences.getEmail(),
                sharedPreferences.getFirebaseToken(), // could be null/empty/token
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                listener
        );
    }

    public void updateFirebaseToken(String uid, String email, String firebaseToken, int studyLang, int langDisplay, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateFirebaseToken(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, email, firebaseToken, studyLang, langDisplay,
                Utils.getAppVersion(), mApplication.getString(R.string.app_name), "", Constant.CLIENT_TYPE_ANDROID,
                Build.MANUFACTURER + " " + Build.MODEL, Build.VERSION.RELEASE
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateStudyLangAndMotherTongue() {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        updateStudyLangAndMotherTongue(
                sharedPreferences.getUid(),
                sharedPreferences.getEmail(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                null
        );
    }

    public void updateStudyLangAndMotherTongue(String uid, String email, int studyLang, int langDisplay, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateStudyLangAndMotherTongue(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, email, studyLang, langDisplay
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getListToPractice(String uid, int studyLang, int langDisplay, String bookId, int bookType, final DalApiListener<VocaPracticeListResponse> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaPracticeListResponse> call = mApplication.getDalApi().getListToPractice(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay, bookId, bookType);
        call.enqueue(new Callback<VocaPracticeListResponse>() {

            @Override
            public void onResponse(Call<VocaPracticeListResponse> call, Response<VocaPracticeListResponse> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaPracticeListResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasToPracticeAlpahbet(String uid, int studyLang, int langDisplay, final DalApiListener<List<VocaPractice>> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<List<VocaPractice>> call = mApplication.getDalApi().getVocasToPracticeAlpahbet(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay);
        call.enqueue(new Callback<List<VocaPractice>>() {

            @Override
            public void onResponse(Call<List<VocaPractice>> call, Response<List<VocaPractice>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaPractice>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getServerVocaBookListForNativeSpeaker(int parentId, int practiceOnly, final DalApiListener<VocaBookListNativeSpeakerResponse> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<VocaBookListNativeSpeakerResponse> call = mApplication.getDalApi().getServerVocaBookListForNativeSpeaker(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                parentId,
                practiceOnly
        );
        call.enqueue(new Callback<VocaBookListNativeSpeakerResponse>() {

            @Override
            public void onResponse(Call<VocaBookListNativeSpeakerResponse> call, Response<VocaBookListNativeSpeakerResponse> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaBookListNativeSpeakerResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromDoYouKnowThisVoca(final DalApiListener<List<VocaDoYouKnow>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaDoYouKnow>> call = mApplication.getDalApi().getVocasFromDoYouKnowThisVoca(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaDoYouKnow>>() {

            @Override
            public void onResponse(Call<List<VocaDoYouKnow>> call, Response<List<VocaDoYouKnow>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaDoYouKnow>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getCountOf1StAmkiGrade(final DalApiListener<Integer> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Integer> call = mApplication.getDalApi().getCountOf1StAmkiGrade(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode()
        );
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                sharedPreferences.setCountOf1stAmkiGrade(response.body());
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getProfileInfo(int opponentUid, final DalApiListener<User> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<User> call = mApplication.getDalApi().getProfileInfo(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                opponentUid
        );
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateProfileInfo(User user, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().updateProfileInfo(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                user.getName(),
                user.getNation(),
                user.getCity(),
                user.getSex(),
                user.getAge(),
                user.getBio()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getUserList(int category, int uid, int startNo, int countRecord, String searchString, final DalApiListener<UserListResponse> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<UserListResponse> call = mApplication.getDalApi().getUserList(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                uid > 0 ? String.valueOf(uid) : sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                category,
                startNo,
                countRecord,
                searchString
        );
        call.enqueue(new Callback<UserListResponse>() {

            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void manageFollowingETC(int opponentUid, String type, int manageRecord, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().manageFollowingETC(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                opponentUid,
                type,
                manageRecord
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateOpponentNameByMe(int opponentUid, String name, String desc, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().updateOpponentNameByMe(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                opponentUid,
                name,
                desc
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void removeMemorizeVoca(String ids, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().removeMemorizeVoca(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                ids
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateSettingValue(int maxHomework, int maxQuiz, int maxHanjaQuiz, boolean allowChat, boolean shareMyRecording) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().updateSettingValue(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                maxHomework,
                maxQuiz,
                maxHanjaQuiz,
                allowChat ? 1 : 0,
                shareMyRecording ? 1 : 0
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void getChatroomList(final DalApiListener<List<ChatRoom>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<ChatRoom>> call = mApplication.getDalApi().getChatroomList(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid()
        );
        call.enqueue(new Callback<List<ChatRoom>>() {

            @Override
            public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getChatroomIDandAllUserInfo(int opponentUid, int classListId, int chatRoomType, int chatRoomId, final DalApiListener<ChatRoomInfo> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ChatRoomInfo> call = mApplication.getDalApi().getChatroomIDandAllUserInfo(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                opponentUid,
                sharedPreferences.getLangStudyCode(),
                classListId,
                chatRoomType,
                chatRoomId
        );
        call.enqueue(new Callback<ChatRoomInfo>() {

            @Override
            public void onResponse(Call<ChatRoomInfo> call, Response<ChatRoomInfo> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatRoomInfo> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getStudyUserInfoInChatroom(int chatRoomId, int lessonId, boolean sendPN, final DalApiListener<ChatRoomInfo> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ChatRoomInfo> call = mApplication.getDalApi().getStudyUserInfoInChatroom(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                chatRoomId,
                lessonId,
                sendPN ? Constant.API_VALUE.IS_YES : Constant.API_VALUE.IS_NO
        );
        call.enqueue(new Callback<ChatRoomInfo>() {

            @Override
            public void onResponse(Call<ChatRoomInfo> call, Response<ChatRoomInfo> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatRoomInfo> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void joinStudyModeInChatroom(int chatRoomId, int lessonId, int studyRole, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().joinStudyModeInChatroom(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                chatRoomId,
                lessonId,
                studyRole
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void exitStudyModeInTheChatroom(int chatRoomId, int lessonId, int studyRole, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().exitStudyModeInTheChatroom(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                chatRoomId,
                lessonId,
                sharedPreferences.getLangStudyCode(),
                studyRole
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromAllVocaBookInStudyMode(int chatRoomId, int studentId, int tutorId,
                                                   int bookId, int bookType, int startNo, int countRecord,
                                                   int studyLang, String studentLangDisplay,
                                                   int makeRubyText, int makeNewExam,
                                                   DalApiListener<VocaFromAllVocaBook> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<VocaFromAllVocaBook> call = mApplication.getDalApi().getVocasFromAllVocaBookInStudyMode(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, studentId, tutorId, bookId, bookType, startNo, countRecord,
                studyLang, sharedPreferences.getMotherTongueLangCode(), studentLangDisplay,
                makeRubyText, makeNewExam
        );
        call.enqueue(new Callback<VocaFromAllVocaBook>() {

            @Override
            public void onResponse(Call<VocaFromAllVocaBook> call, Response<VocaFromAllVocaBook> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaFromAllVocaBook> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void checkAndSendVocaKnowToOthersInStudyMode(Context context, int studyLang, int chatRoomId, int lessonId,
                                                        int vocaId, int vocaType, int amkiGrade, int knownPronounce,
                                                        int vocaBookId, int vocaBookType,
                                                        Integer tblSection, Integer tblRow, int pnType,
                                                        final DalApiListener<Integer> listener) {
        if (amkiGrade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
            getCountOf1StAmkiGrade(new DalApiListener<Integer>() {

                @Override
                public void onSuccess(Integer response) {
                    if (response >= Constant.GRADE_1_MAX) {
                        new ConfirmAdd2ndGradeDialog(context, new ConfirmationDialog.OnDialogClickListener() {

                            @Override
                            public void onPositive(DialogInterface dialog) {
                                dialog.dismiss();
                                sendVocaKnowToOthersInStudyMode(
                                        studyLang, chatRoomId, lessonId, vocaId, vocaType, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, knownPronounce,
                                        vocaBookId, vocaBookType,
                                        tblSection, tblRow, pnType,
                                        listener
                                );
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        sendVocaKnowToOthersInStudyMode(
                                studyLang, chatRoomId, lessonId, vocaId, vocaType, amkiGrade, knownPronounce,
                                vocaBookId, vocaBookType,
                                tblSection, tblRow, pnType,
                                listener
                        );
                    }
                }

                @Override
                public void onFailure(String error) {
                }
            });
        } else {
            sendVocaKnowToOthersInStudyMode(
                    studyLang, chatRoomId, lessonId, vocaId, vocaType, amkiGrade, knownPronounce,
                    vocaBookId, vocaBookType,
                    tblSection, tblRow, pnType,
                    listener
            );
        }
    }

    private void sendVocaKnowToOthersInStudyMode(int studyLang, int chatRoomId, int lessonId,
                                                 int vocaId, int vocaType, int vocaKnow, int vocaKnowPronounce,
                                                 int vocaBookId, int vocaBookType,
                                                 Integer tblSection, Integer tblRow, int pnType,
                                                 final DalApiListener<Integer> listener) {
        if (listener != null) {
            listener.onSuccess(vocaKnow);
        }
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendVocaKnowToOthersInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(), studyLang,
                chatRoomId, lessonId, vocaId, vocaType, vocaKnow, vocaKnowPronounce, vocaBookId, vocaBookType,
                tblSection, tblRow, pnType
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void changeEvaluateVocaGrade(int studyLang, int studentId, String evaluateGrade, int vocaId, int vocaType,
                                        Integer tblSection, Integer tblRow,
                                        final DalApiListener<Boolean> listener) {
        if (listener != null) {
            listener.onSuccess(true);
        }
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().changeEvaluateVocaGrade(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studentId, evaluateGrade, vocaId, vocaType,
                tblSection, tblRow,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void sendPronounceFeedbackToStudent(int studyLang, int studentId, String evaluateGrade, String feedbackMessage,
                                               int fileVersion, int vocaId, int vocaType, String vocaDisplay,
                                               final DalApiListener<Boolean> listener) {
        if (listener != null) {
            listener.onSuccess(true);
        }
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendPronounceFeedbackToStudent(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(), sharedPreferences.getUid(),
                studentId, evaluateGrade, feedbackMessage, fileVersion, vocaId, vocaType, vocaDisplay,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void sendPronounceFeedbackToOthersInStudyMode(int studyLang, int chatRoomId, int lessonId, int studentId,
                                                         String evaluateGrade, String feedbackMessage, int fileVersion,
                                                         int vocaId, int vocaType, String vocaDisplay,
                                                         Integer tblSection, Integer tblRow, int pnType,
                                                         final DalApiListener<Boolean> listener) {
        if (listener != null) {
            listener.onSuccess(true);
        }
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendPronounceFeedbackToOthersInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(), sharedPreferences.getUid(),
                chatRoomId, lessonId, studentId, evaluateGrade, feedbackMessage, fileVersion, vocaId, vocaType, vocaDisplay,
                tblSection, tblRow, pnType,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void sendVocabookInStudyMode(int studyLang, int chatRoomId, int lessonId, int bookId, int bookType,
                                        int topicBeginIndex, int topicRepeatedCount,
                                        int lessonMode, int lessonReadingId,
                                        final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendVocabookInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, bookId, bookType, topicBeginIndex, topicRepeatedCount,
                studyLang, lessonMode, lessonReadingId
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void saveVocabookIDInStudyMode(int studyLang, int chatRoomId, int lessonId, int bookId, int bookType,
                                          int cellIndex, int showToast, int topicBeginIndex, int topicRepeatedCount,
                                          int lessonMode, int grammarId, int lessonReadingId,
                                          final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().saveVocabookIDInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, bookId, bookType, cellIndex, showToast, topicBeginIndex, topicRepeatedCount,
                lessonMode, studyLang, grammarId, lessonReadingId
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void saveStudiedWorkbookIDInStudyMode(int studyLang, int chatRoomId, int studentId, int tutorId, int bookId, int bookType, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().saveStudiedWorkbookIDInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, studentId, tutorId, bookId, bookType, studyLang
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getTableVersion(String tableName, final DalApiListener<String> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().getTableVersion(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(), sharedPreferences.getEmail(), tableName
        );
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (listener != null) {
                    try {
                        listener.onSuccess(response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void downloadTable(String tableName, final DalApiListener<String> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().downloadTable(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(), sharedPreferences.getEmail(), tableName
        );
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (listener != null) {
                    try {
                        listener.onSuccess(response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void showAsteriskInStudyMode(int chatRoomId, int lessonId, int showAsterisk, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().showAsteriskInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, showAsterisk
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendMessageInStudyMode(int chatRoomId, int lessonId, String messageId, String message, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendMessageInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, messageId, message
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void syncCellWithOtherUsersInStudyMode(int studyLang, int chatRoomId, int lessonId, int bookId, int bookType,
                                                  int vocaId, int vocaType,
                                                  Integer tblParentSection, Integer tblParentRow,
                                                  Integer tblSection, Integer tblRow,
                                                  int tblSyncType, int isCellChecked,
                                                  int countOfOrder, String branchSelected,
                                                  final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().syncCellWithOtherUsersInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, bookId, bookType, vocaId, vocaType, tblParentSection, tblParentRow,
                tblSection, tblRow, tblSyncType, isCellChecked, countOfOrder, branchSelected,
                studyLang
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sideGlanceSentenceInStudyMode(int studyLang, int chatRoomId, int lessonId, int bookId, int bookType, int vocaId, int vocaType, int index, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sideGlanceSentenceInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, bookId, bookType, vocaId, vocaType, index,
                studyLang
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getListOfLesson(int appType, int forWhom, final DalApiListener<List<Lesson>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<Lesson>> call = mApplication.getDalApi().getListOfLesson(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                appType,
                forWhom,
                sharedPreferences.getLangStudyCode()
        );
        call.enqueue(new Callback<List<Lesson>>() {

            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void createOrGetStudyModeAndJoin(int studyLang, int studyRole,
                                            int studentId, int tutorId, int lessonId,
                                            final DalApiListener<ChatRoomInfo> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ChatRoomInfo> call = mApplication.getDalApi().createOrGetStudyModeAndJoin(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyRole, studentId, tutorId, lessonId,
                studyLang
        );
        call.enqueue(new Callback<ChatRoomInfo>() {

            @Override
            public void onResponse(Call<ChatRoomInfo> call, Response<ChatRoomInfo> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatRoomInfo> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendPushToStudentFinishStudyInStudyMode(int chatRoomId, int opponentUid, int lessonId, long startTime, long finishTime, int lastBookId, int lastBookType, String bookIds, String bookTypes, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendPushToStudentFinishStudyInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, opponentUid, lessonId, startTime, finishTime,
                lastBookId, lastBookType, bookIds, bookTypes,
                sharedPreferences.getLangStudyCode()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void confirmFinishStudyInStudyMode(int chatRoomId, int studentId, int tutorId, int lessonId, long startTime, long finishTime, int lastBookId, int lastBookType, String bookIds, String bookTypes, boolean confirm, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().confirmFinishStudyInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, studentId, tutorId, lessonId, startTime, finishTime,
                lastBookId, lastBookType, bookIds, bookTypes,
                sharedPreferences.getLangStudyCode(),
                confirm ? 1 : 0
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getLessonOption(int uid, int forWhom, final DalApiListener<LessonOption> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<LessonOption> call = mApplication.getDalApi().getLessonOption(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, String.valueOf(uid),
                forWhom, sharedPreferences.getLangStudyCode()
        );
        call.enqueue(new Callback<LessonOption>() {

            @Override
            public void onResponse(Call<LessonOption> call, Response<LessonOption> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<LessonOption> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void setLessonOption(int uid, int forWhom, LessonOption lessonOption, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().setLessonOption(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, uid > 0 ? String.valueOf(uid) : sharedPreferences.getUid(),
                forWhom, sharedPreferences.getLangStudyCode(),
                lessonOption.getEnableLessonPN(),
                lessonOption.getPnMinutesBeforeBeginLesson(),
                lessonOption.getPnMinutesBeforeFinishLesson(),
                lessonOption.getLeadLesson(),
                lessonOption.getLanguageLevel(),
                lessonOption.getFreeTalking(),
                lessonOption.getSmallTalking(),
                lessonOption.getSpeakingSpeed(),
                lessonOption.getReadingSpeed(),
                lessonOption.getCorrectPronunciation(),
                lessonOption.getRepeatTopics(),
                lessonOption.getRepeatCount(),
                lessonOption.getUserWordExamType(),
                lessonOption.getRecordLesson(),
                lessonOption.getCountOfPhrasesToStudyAtOnce(),
                lessonOption.getStudyOrderHidePartOfWords(),
                lessonOption.getStudyOrderHideAllPhrases(),
                lessonOption.getStudyOrderRandomQuestions()
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAllStudentListForLesson(int studyLang, int sortType, final DalApiListener<List<User>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<User>> call = mApplication.getDalApi().getAllStudentListForLesson(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sortType
        );
        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAllTutorListForLesson(int studyLang, int sortType, final DalApiListener<List<User>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<User>> call = mApplication.getDalApi().getAllTutorListForLesson(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sortType
        );
        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getHasNewAppVersion(final String clientType, final String version, final String appName, final DalApiListener<Boolean> listener) {
        Call<Boolean> call = mApplication.getDalApi().getHasNewAppVersion(clientType, version, appName);
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeAnExamInStudyMode(int studyLang, int chatRoomId, int lessonId, int examSource, int studentId, String studentLangDisplay,
                                      int tutorId, String vocaIds, String vocaTypes, int userWordExamType, final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaStudyChatExam>> call = mApplication.getDalApi().makeAnExamInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, examSource, studentId, studentLangDisplay, tutorId, vocaIds, vocaTypes,
                studyLang, sharedPreferences.getMotherTongueLangCode(),
                userWordExamType
        );
        call.enqueue(new Callback<List<VocaStudyChatExam>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatExam>> call, Response<List<VocaStudyChatExam>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatExam>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAnExamInStudyMode(int studyLang, int chatRoomId, int studentId, int tutorId, final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaStudyChatExam>> call = mApplication.getDalApi().getAnExamInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, studentId, tutorId,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaStudyChatExam>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatExam>> call, Response<List<VocaStudyChatExam>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatExam>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void shuffleThisExamInStudyMode(int studyLang, int chatRoomId, int lessonId, int studentId, int tutorId, final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaStudyChatExam>> call = mApplication.getDalApi().shuffleThisExamInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, studentId, tutorId,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaStudyChatExam>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatExam>> call, Response<List<VocaStudyChatExam>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatExam>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendSelectAnswerAtExamInStudyMode(int chatRoomId, int lessonId, int vocaId, int vocaType, int index, int selectedAnswerNumber, int isAnswerCorrect,
                                                  final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendSelectAnswerAtExamInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, vocaId, vocaType, index, selectedAnswerNumber, isAnswerCorrect
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateOrMakeNewLesson(String ids, String studentIds, String tutorIds, String startTimes, String finishTimes,
                                      int studyLangs, String lessonTypes, final DalApiListener<Boolean> listener) {
        Call<Boolean> call = mApplication.getDalApi().updateOrMakeNewLesson(
                ids, studentIds, tutorIds, startTimes, finishTimes, studyLangs, lessonTypes
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void requestCallToOpponent(final String uuid,
                                      final int uid,
                                      final int opponentUid,
                                      final String roomName,
                                      final int type,
                                      int chatRoomId,
                                      int lessonId,
                                      int studyRole,
                                      final DalApiListener<Boolean> listener) {
        Call<Boolean> call = mApplication.getDalApi().requestCallToOpponent(
                uuid, uid, opponentUid, roomName, type,
                chatRoomId, lessonId, studyRole);
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void replyCallFromOpponent(String uuid,
                                      int uid,
                                      int opponentId,
                                      int replyCallType,
                                      String chatRoomId,
                                      int lessonId,
                                      final DalApiListener<Boolean> listener) {
        Call<Boolean> call = mApplication.getDalApi().replyCallFromOpponent(uuid, uid, opponentId, replyCallType, chatRoomId, lessonId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void deleteLessons(String lessonIds, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().deleteLessons(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                lessonIds
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeRolePlayingContentsJson(int studyLang, int chatRoomId, int lessonId, int languageLevel, int rolePlayingCategoryId, int rolePlayingType,
                                            int studentId, String studentLangDisplay, int tutorId,
                                            int makeRubyText, final DalApiListener<RolePlayingContent> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<RolePlayingContent> call = mApplication.getDalApi().makeRolePlayingContentsJson(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sharedPreferences.getMotherTongueLangCode(), chatRoomId, lessonId,
                languageLevel, rolePlayingCategoryId, rolePlayingType,
                studentId, studentLangDisplay, tutorId, makeRubyText
        );
        call.enqueue(new Callback<RolePlayingContent>() {

            @Override
            public void onResponse(Call<RolePlayingContent> call, Response<RolePlayingContent> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<RolePlayingContent> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getRolePlayingContentsJson(int studyLang, int rolePlayingId, int studentId
            , int makeRubyText, final DalApiListener<RolePlayingContent> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<RolePlayingContent> call = mApplication.getDalApi().getRolePlayingContentsJson(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sharedPreferences.getMotherTongueLangCode(),
                rolePlayingId, studentId, makeRubyText
        );
        call.enqueue(new Callback<RolePlayingContent>() {

            @Override
            public void onResponse(Call<RolePlayingContent> call, Response<RolePlayingContent> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<RolePlayingContent> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getRolePlayingCategoryList(int studyLang, int languageLevel, int rolePlayingParentId,
                                           DalApiListener<List<Category>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<Category>> call = mApplication.getDalApi().getRolePlayingCategoryList(
                studyLang, sharedPreferences.getMotherTongueLangCode(), languageLevel, rolePlayingParentId
        );
        call.enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeRubyText(int studentId, int tutorId, String inputText,
                             final DalApiListener<RubyTextModel> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<RubyTextModel> call = mApplication.getDalApi().makeRubyText(
                sharedPreferences.getUid(),
                studentId, tutorId,
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                sharedPreferences.getMotherTongueLanguage(),
                inputText);
        call.enqueue(new Callback<RubyTextModel>() {

            @Override
            public void onResponse(Call<RubyTextModel> call, Response<RubyTextModel> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<RubyTextModel> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeRubyTextList(int studentId, int tutorId,
                                 final DalApiListener<RubyListModel> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<RubyListModel> call = mApplication.getDalApi().makeRubyTextList(
                sharedPreferences.getUid(),
                studentId, tutorId,
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                sharedPreferences.getMotherTongueLanguage(),
                2, 21);
        call.enqueue(new Callback<RubyListModel>() {

            @Override
            public void onResponse(Call<RubyListModel> call, Response<RubyListModel> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<RubyListModel> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void addToBookmark(final String voca, String vocaId, int vocaType, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().bookmarkAdd(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                String.valueOf(vocaId),
                vocaType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void deleteFromBookmark(final String voca, String vocaId, int vocaType, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        if (sharedPreferences.getUidDefault() == 0) return; // did not logged in
        Call<ResponseBody> call = mApplication.getDalApi().bookmarkDel(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                String.valueOf(vocaId),
                vocaType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void setWordKnown(final String voca, final String vocaId, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().wordKnown(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void setWordUnknown(final String voca, final String vocaId, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().wordUnknown(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void setPronounceKnown(final String voca, final String vocaId, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().setPronounceKnown(
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void setPronounceUnknown(final String voca, final String vocaId, final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ResponseBody> call = mApplication.getDalApi().setPronounceUnknown(
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                voca,
                vocaId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getCurrentLessonTopicOfUser(String uid, int studyLang, final DalApiListener<CurrentLesson> listener) {
        Call<CurrentLesson> call = mApplication.getDalApi().getCurrentLessonTopicOfUser(uid, studyLang);
        call.enqueue(new Callback<CurrentLesson>() {
            @Override
            public void onResponse(Call<CurrentLesson> call, final Response<CurrentLesson> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<CurrentLesson> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromVocaList(int studyLang,
                                     int studentId, int tutorId,
                                     String vocaIds, String vocaTypes,
                                     final DalApiListener<List<VocaStudyChatRuby>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<List<VocaStudyChatRuby>> call = mApplication.getDalApi().getVocasFromVocaList(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studentId, tutorId,
                vocaIds, vocaTypes,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaStudyChatRuby>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatRuby>> call, Response<List<VocaStudyChatRuby>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatRuby>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVocasFromVocaListInVocabook(int studyLang,
                                               int studentId, int tutorId,
                                               String vocaIds, String vocaTypes, String idsInVocaBook,
                                               final DalApiListener<List<VocaStudyChatByCategory>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<List<VocaStudyChatByCategory>> call = mApplication.getDalApi().getVocasFromVocaListInVocabook(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studentId, tutorId,
                vocaIds, vocaTypes, idsInVocaBook,
                studyLang, sharedPreferences.getMotherTongueLangCode()
        );
        call.enqueue(new Callback<List<VocaStudyChatByCategory>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatByCategory>> call, Response<List<VocaStudyChatByCategory>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatByCategory>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeQuizAtQuizMenu(int studyLang, int examType, int maxValue, final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        makeQuizAtQuizMenu(studyLang, sharedPreferences.getMotherTongueLangCode(), examType, maxValue, listener);
    }

    public void makeQuizAtQuizMenu(int studyLang, int displayLanguage, int examType, int maxValue, final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<List<VocaStudyChatExam>> call = mApplication.getDalApi().makeQuizAtQuizMenu(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, displayLanguage,
                examType, maxValue
        );
        call.enqueue(new Callback<List<VocaStudyChatExam>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatExam>> call, Response<List<VocaStudyChatExam>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatExam>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateQuizStartFinishStatus(int quizStatusId, int isFinish, int studyLang,
                                            final DalApiListener<Integer> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<Integer> call = mApplication.getDalApi().updateQuizStartFinishStatus(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                quizStatusId, isFinish, studyLang
        );
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void saveResutlOfEachQuestion(int opponentUid, int vocaId, int vocaType, String vocaDisplay,
                                         int isAnswerCorrect, int solvingTime, int quizStatusId, int studyLang,
                                         final DalApiListener<Integer> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<Integer> call = mApplication.getDalApi().saveResutlOfEachQuestion(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                opponentUid, vocaId, vocaType, vocaDisplay,
                isAnswerCorrect, solvingTime, quizStatusId, studyLang
        );
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeWordQuizFromListOfVocaID(int langStudyCode, int multipleChoiceQuestionType, int maxValue,
                                             String vocaIds, String vocaTypes,
                                             final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        makeWordQuizFromListOfVocaID(langStudyCode, sharedPreferences.getMotherTongueLangCode(), multipleChoiceQuestionType, maxValue, vocaIds, vocaTypes, listener);
    }

    public void makeWordQuizFromListOfVocaID(int langStudyCode, int langMeaningCode, int multipleChoiceQuestionType, int maxValue,
                                             String vocaIds, String vocaTypes,
                                             final DalApiListener<List<VocaStudyChatExam>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<List<VocaStudyChatExam>> call = mApplication.getDalApi().makeWordQuizFromMultipleListOfVocaID(
                token, Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                multipleChoiceQuestionType, maxValue, vocaIds, vocaTypes,
                langStudyCode, langMeaningCode
        );
        call.enqueue(new Callback<List<VocaStudyChatExam>>() {

            @Override
            public void onResponse(Call<List<VocaStudyChatExam>> call, Response<List<VocaStudyChatExam>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaStudyChatExam>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendChatMessageInStudyMode(int chatRoomId, int lessonId, String message, int studyLang, final DalApiListener<ChatMessageResponse> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<ChatMessageResponse> call = mApplication.getDalApi().sendChatMessageInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, message, studyLang
        );
        call.enqueue(new Callback<ChatMessageResponse>() {

            @Override
            public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void sendGrammarWorkbookInStudyMode(int chatRoomId, int lessonId, int grammarId, int studyLang, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().sendGrammarWorkbookInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, grammarId, studyLang
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void makeReadingContentsJsonInStudyMode(int studyLang, int chatRoomId, int lessonId,
                                                   int studentId, String studentLangDisplay,
                                                   int tutorId, int readingId, int makeNewExam,
                                                   DalApiListener<VocaReading> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<VocaReading> call = mApplication.getDalApi().makeReadingContentsJsonInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sharedPreferences.getMotherTongueLangCode(), chatRoomId, lessonId, studentId, studentLangDisplay,
                tutorId, readingId, "", makeNewExam
        );
        call.enqueue(new Callback<VocaReading>() {

            @Override
            public void onResponse(Call<VocaReading> call, Response<VocaReading> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaReading> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getReadingContentsJsonInStudyMode(int studyLang, int studentId, int lessonReadingId,
                                                  DalApiListener<VocaReading> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<VocaReading> call = mApplication.getDalApi().getReadingContentsJsonInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                studyLang, sharedPreferences.getMotherTongueLangCode(), studentId, lessonReadingId
        );
        call.enqueue(new Callback<VocaReading>() {

            @Override
            public void onResponse(Call<VocaReading> call, Response<VocaReading> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaReading> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void showMeaningOnStudentViewInStudyMode(int studyLang, int chatRoomId, int lessonId, boolean showMeaningAtStudent,
                                                    DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().showMeaningOnStudentViewInStudyMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, studyLang, showMeaningAtStudent ? Constant.API_VALUE.IS_YES : Constant.API_VALUE.IS_NO
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateAppStateMode(int chatRoomId, int lessonId, int appStateMode,
                                   DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getDalApi().updateAppStateMode(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                chatRoomId, lessonId, appStateMode
        );
        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getVideoInformation(String movieId, String mediaType, String language, final DalApiListener<Object> listener) {
        Call<Object> call = mApplication.getDalApi().getVideoInformation(
                Constant.API.HEADER_CONTENT_TYPE,
                Constant.PLAYER.THE_MOVIE_DB.BASE_URL + mediaType + "/" + movieId,
                Constant.PLAYER.THE_MOVIE_DB.API_KEY,
                Constant.PLAYER.THE_MOVIE_DB.APPEND_CREDITS + "," + Constant.PLAYER.THE_MOVIE_DB.APPEND_VIDEOS + "," + Constant.PLAYER.THE_MOVIE_DB.APPEND_ALTERNATIVE_TITLES,
                language);
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void searchMultiVideo(String query, String language, final DalApiListener<SearchVideoModel> listener) {
        searchMultiVideo(query, language, 1, listener);
    }

    public void searchMultiVideo(String query, String language, int page, final DalApiListener<SearchVideoModel> listener) {
        Call<SearchVideoModel> call = mApplication.getDalApi().searchMultiVideo(
                Constant.API.HEADER_CONTENT_TYPE,
                Constant.PLAYER.THE_MOVIE_DB.SEARCH_MULTI_URL,
                Constant.PLAYER.THE_MOVIE_DB.API_KEY,
                query, language, page);
        call.enqueue(new Callback<SearchVideoModel>() {

            @Override
            public void onResponse(Call<SearchVideoModel> call, Response<SearchVideoModel> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<SearchVideoModel> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getCredits(String url, final DalApiListener<VideoInformationModel.Credits> listener) {
        Call<VideoInformationModel.Credits> call = mApplication.getDalApi().getCredits(
                Constant.API.HEADER_CONTENT_TYPE,
                url,
                Constant.PLAYER.THE_MOVIE_DB.API_KEY);
        call.enqueue(new Callback<VideoInformationModel.Credits>() {
            @Override
            public void onResponse(Call<VideoInformationModel.Credits> call, Response<VideoInformationModel.Credits> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VideoInformationModel.Credits> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void searchHanja(String searchString, int searchHanjaType, int searchWithRegex, DalApiListener<List<VocaHanjaSearch>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<VocaHanjaSearch>> call = mApplication.getDalApi().searchHanja(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(), sharedPreferences.getMotherTongueLangCode(), searchString, searchHanjaType, searchWithRegex
        );
        call.enqueue(new Callback<List<VocaHanjaSearch>>() {

            @Override
            public void onResponse(Call<List<VocaHanjaSearch>> call, Response<List<VocaHanjaSearch>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VocaHanjaSearch>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateMultipleWordMeaningWithID(
            MultipleWordMeaningWithIDModel json,
            final DalApiListener<MultipleWordMeaningWithIDModel.ResponseModel> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<MultipleWordMeaningWithIDModel.ResponseModel> call = mApplication.getDalApi().updateMultipleWordMeaningWithID(
                token, json);
        call.enqueue(new Callback<MultipleWordMeaningWithIDModel.ResponseModel>() {

            @Override
            public void onResponse(Call<MultipleWordMeaningWithIDModel.ResponseModel> call, Response<MultipleWordMeaningWithIDModel.ResponseModel> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<MultipleWordMeaningWithIDModel.ResponseModel> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getAllVocasKnowAndBookmarkOfUser(DalApiListener<VocaKnowAndBookmarkList> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<VocaKnowAndBookmarkList> call = mApplication.getDalApi().getAllVocasKnowAndBookmarkOfUser(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode()
        );
        call.enqueue(new Callback<VocaKnowAndBookmarkList>() {

            @Override
            public void onResponse(Call<VocaKnowAndBookmarkList> call, Response<VocaKnowAndBookmarkList> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaKnowAndBookmarkList> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void updateVocaInSerVocabookDetail(final int uid, final int studyLang, final int langDisplay, final String voca, final String meaning, final String meaningDetail, final int idInVocabook, final int vocabookTypeCode, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getDalApi().updateVocaInSerVocabookDetail(token, Constant.API.HEADER_CONTENT_TYPE, uid, studyLang, langDisplay, voca, meaning, meaningDetail, idInVocabook, vocabookTypeCode);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, final Response<Boolean> response) {
                try {
                    if (response != null) {
                        DLog.d(TAG, response.body().toString());
                        if (response.isSuccessful()) {
                            listener.onSuccess(true);
                        } else {
                            listener.onSuccess(false);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    listener.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
                listener.onSuccess(false);
            }
        });
    }

    public void getServerVocaBookInfo(String uid, int studyLang, int langDisplay, final int idInVocabook, final int vocabookTypeCode, final DalApiListener<VocaDetailInfo> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<VocaDetailInfo> call = mApplication.getDalApi().getServerVocaBookInfo(
                token, Constant.API.HEADER_CONTENT_TYPE, uid,
                studyLang, langDisplay,
                idInVocabook, vocabookTypeCode
        );
        call.enqueue(new Callback<VocaDetailInfo>() {

            @Override
            public void onResponse(Call<VocaDetailInfo> call, final Response<VocaDetailInfo> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<VocaDetailInfo> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public interface ApiListener {
        void onSucess(String response);

        void onFailure(String error);
    }
}
