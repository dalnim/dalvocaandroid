package com.dalread.network;

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
import com.dalread.model.VocaStudy;
import com.dalread.model.VocaStudyChatByCategory;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaStudyChatRuby;
import com.dalread.model.VocaStudyHistory;
import com.dalread.model.roleplaying.Category;
import com.dalread.model.roleplaying.RolePlayingContent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.network.models.LoginModel;
import com.dalread.network.models.SignUpModel;
import com.dalread.network.models.UserListResponse;
import com.dalread.network.models.VocaBookListNativeSpeakerResponse;
import com.dalread.network.models.VocaPracticeListResponse;
import com.dalread.util.Constant;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JetVHS on 10/30/2016.
 */
public interface DalApi {
    @POST("mobileLogin.ajax")
    @FormUrlEncoded
    Call<LoginModel> login(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_PASSWORD) String password,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang);

    @POST("mobileLogout.ajax")
    @FormUrlEncoded
    Call<ResponseBody> logout(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email);

    @POST("mobileDeleteAccount.ajax")
    @FormUrlEncoded
    Call<ResponseBody> deleteAccount(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_PASSWORD) String password);

    @POST("checkIsNewUser.ajax")
    @FormUrlEncoded
    Call<ResponseBody> isNewUser(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_ID_MAIL) String idMail);

    @POST("mobileSignup.ajax")
    @FormUrlEncoded
    Call<SignUpModel> signUp(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_PASSWORD) String password,
            @Field(Constant.API_KEY.KEY_NAME) String name,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_NATIVE) String langNative,
            @Field(Constant.API_KEY.KEY_VOCALEVEL) String vocaLevel,

            @Field(Constant.API_KEY.KEY_CLIENT_TYPE) String client_type,
            @Field(Constant.API_KEY.KEY_APP_NAME) String appName,
            @Field(Constant.API_KEY.KEY_APP_VERSION) String appVersion,
            @Field(Constant.API_KEY.KEY_DEVICE_TYPE) String deviceType,
            @Field(Constant.API_KEY.KEY_DEVICE_OS_VERSION) String deviceOsVersion);

    @POST("ResetPassword.ajax")
    @FormUrlEncoded
    Call<ResponseBody> ResetPassword(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_PASSWORD) String password,
            @Field(Constant.API_KEY.KEY_NEW_PASSWORD) String newPassword);

    @GET("{fullUrl}")
    Call<MercuryModel> sendMainContent(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Query("url") String url,
            @Header("x-api-key") String apiKey);

    @POST("DalReadText")
    @FormUrlEncoded
//    Call<ResponseBody> dalReadText(@Header(Constant.API_KEY.KEY_COOKIE) String token, @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType, @Header("JSESSIONID") String session, @Field("comment") String comment, @Field("UID") String uid, @Field("studyLang") String studyLang, @Field("langDisplay") String langDisplay);
    Call<ResponseBody> dalReadText(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_COMMENT) String comment,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay);

    @POST("DalReadTextForMobile")
    @FormUrlEncoded
    Call<ResponseBody> getWordList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_COMMENT) String comment,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay);

    @POST("addToBookmark.ajax")
    @FormUrlEncoded
    Call<ResponseBody> bookmarkAdd(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int langStudyCode,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);

    @POST("deleteFromBookmark.ajax")
    @FormUrlEncoded
    Call<ResponseBody> bookmarkDel(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int langStudyCode,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);

    @POST("setWordKnown.ajax")
    @FormUrlEncoded
    Call<ResponseBody> wordKnown(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId);

    @POST("setWordUnknown.ajax")
    @FormUrlEncoded
    Call<ResponseBody> wordUnknown(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId);

    @POST("getHanajInfo.ajax")
    @FormUrlEncoded
    Call<ResponseBody> getHanajInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_DISP_MEANING_LANG) String dispMeaningLang,
            @Field(Constant.API_KEY.KEY_STR_WORDS) String strWords);

    @POST("updateWordMeaning.ajax")
    @FormUrlEncoded
    Call<ResponseBody> updateWordMeaning(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_DISP_MEANING_LANG) String dispMeaningLang,
            @Field(Constant.API_KEY.KEY_WORD) String word,
            @Field(Constant.API_KEY.KEY_MEANING_LOW) String meaning,
            @Field(Constant.API_KEY.KEY_PRONOUNCE_LOW) String pronounce,
            @Field(Constant.API_KEY.KEY_STR_POS) String strPos);

    @POST("point_reading_reduce.ajax")
    @FormUrlEncoded
    Call<ResponseBody> pointReadingReduce(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email);

    @POST("countOfUnknownWord.ajax")
    @FormUrlEncoded
    Call<ResponseBody> getCountUnknown(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang);

    @POST("countOfKnownWord.ajax")
    @FormUrlEncoded
    Call<ResponseBody> getCountKnown(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang);

    @POST("countOfBookmarkWord.ajax")
    @FormUrlEncoded
    Call<ResponseBody> getCountBookmarked(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang);

    @POST("updateWordLevel.ajax")
    @FormUrlEncoded
    Call<ResponseBody> updateWordLevel(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA_LEVEL) String vocaLevel);

    @POST("getInAppListOfPoint.ajax")
    @FormUrlEncoded
    Call<List<PointModel>> getInAppListOfPoint(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_IN_APP_TYPE) String inAppType);

    @POST("addPointToUser.ajax")
    @FormUrlEncoded
    Call<ResponseBody> addPointUser(@Header(Constant.API_KEY.KEY_COOKIE) String token,
                                    @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
                                    @Field(Constant.API_KEY.KEY_UID) String uid,
                                    @Field(Constant.API_KEY.KEY_EMAIL) String email,
                                    @Field(Constant.API_KEY.KEY_APP_NAME) String appName,
                                    @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
                                    @Field(Constant.API_KEY.KEY_POINT) String point,
                                    @Field(Constant.API_KEY.KEY_CLIENTTYPE) String clientType);

    @POST("updateWordsInfo.ajax")
    @Multipart
    Call<ResponseBody> offlineDataToServer(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @PartMap Map<String, RequestBody> partMap);

    /**
     * DalVoca
     */
    @POST("GetToMemorizeVocaList.ajax")
    @FormUrlEncoded
    Call<List<VocaStudy>> getToMemorizeVocaList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetPronounceFeedback.ajax")
    @FormUrlEncoded
    Call<List<VocaFeedback>> getPronounceFeedback(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetUserVocaBookList.ajax")
    @FormUrlEncoded
    Call<List<VocaBook>> getUserVocaBookList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetServerVocaBookList.ajax")
    @FormUrlEncoded
    Call<List<VocaBook>> getServerVocaBookList(
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetServerVocaBookListByCategory.ajax")
    @FormUrlEncoded
    Call<List<VocaBook>> getServerVocaBookListByCategory(
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCABOOK_PARENT_ID) int parentId,
            @Field(Constant.API_KEY.KEY_VOCABOOK_PRACTICE_ONLY) int practiceOnly,
            @Field(Constant.API_KEY.KEY_VOCABOOK_ALPHABET_ONLY) int alphabetOnly
    );

    @POST("GetAllVocaBookList.ajax")
    @FormUrlEncoded
    Call<AllBookListResponse> getAllVocaBookList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) String studentId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("AddVocaBookInUserVocaBookList.ajax")
    @FormUrlEncoded
    Call<Boolean> addVocaBookInUserVocaBookList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_NAME) String bookName
    );

    @POST("DeleteVocaBookInUserVocaBookList.ajax")
    @FormUrlEncoded
    Call<Boolean> deleteVocaBookInUserVocaBookList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId
    );

    @POST("GetVocasFromAllVocaBook.ajax")
    @FormUrlEncoded
    Call<List<VocaInBook>> getVocasFromAllVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord,
            @Field(Constant.API_KEY.KEY_MAKE_RUBY_TEXT) int makeRubyText
    );

    @POST("GetVocasKnownWordCountFromAllVocaBook.ajax")
    @FormUrlEncoded
    Call<VocaCountInBook> getVocasKnownWordCountFromAllVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType
    );

    @POST("AddVocaInUserVocaBook.ajax")
    @FormUrlEncoded
    Call<Boolean> addVocaInUserVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType
    );

    @POST("AddVocasInUserVocaBook.ajax")
    @FormUrlEncoded
    Call<Boolean> addVocasInUserVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) String vocaType,
            @Field(Constant.API_KEY.KEY_USE_SERVER_VOCABOOK_DATA) String useServerVocabookData,
            @Field(Constant.API_KEY.KEY_ID_IN_SERVER_VOCABOOK) String idInServerVocabook
    );

    @POST("DeleteVocaInUserVocaBook.ajax")
    @FormUrlEncoded
    Call<Boolean> deleteVocaInUserVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType
    );

    @POST("ChangeMultipleVocaKnow.ajax")
    @FormUrlEncoded
    Call<Boolean> changeMultipleVocaKnow(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA_KNOW) String vocaKnow,
            @Field(Constant.API_KEY.KEY_VOCA_KNOWPRONOUNCE) String vocaKnowPronounce,
            @Field(Constant.API_KEY.KEY_STR_VOCA_ID_LIST) String vocaIds,
            @Field(Constant.API_KEY.KEY_STR_VOCA_TYPE_LIST) String vocaTypes
    );

    @POST("GetTargetVocaCount.ajax")
    @FormUrlEncoded
    Call<Integer> getTargetVocaCount(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("GetVocasFromTargetVoca.ajax")
    @FormUrlEncoded
    Call<List<VocaMemorize>> getVocasFromTargetVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetVocasFromAllVocas.ajax")
    @FormUrlEncoded
    Call<List<VocaSearch>> getVocasFromAllVocas(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_SEARCH_STRING) String searchString,
            @Field(Constant.API_KEY.KEY_SEARCH_IN) String searchIn,
            @Field(Constant.API_KEY.KEY_SEARCH_WITH_REGEX) int useRegex
    );

    @POST("GetStudentStudiedHistoryByDate.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyHistory>> getStudentStudiedHistoryByDate(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord
    );

    @POST("GetStudentStudiedHistoryByVoca.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyHistory>> getStudentStudiedHistoryByVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord
    );

    @POST("SendStudyListToSendStudent.ajax")
    @FormUrlEncoded
    Call<String> sendStudyListToSendStudent(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) String studentId
    );

    @POST("updateFinishStudy.ajax")
    @FormUrlEncoded
    Call<Boolean> updateFinishStudy(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_STUDENT_STUDY_VOCA_ID) int id,
            @Field(Constant.API_KEY.KEY_EXTENSION) String extension,
            @Field(Constant.API_KEY.KEY_FILE_SIZE) long fileSize
    );

    @POST("GetVocaDetailInfo.ajax")
    @FormUrlEncoded
    Call<VocaDetailInfo> getVocaDetailInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType
    );

    @POST("GetVocaDetailInfoForOneHanja.ajax")
    @FormUrlEncoded
    Call<VocaHanja> getVocaDetailInfoForOneHanja(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_LANG_STUDY) String langStudy,
            @Field(Constant.API_KEY.KEY_LANG_MEANING) String langMeaning
    );

    @POST("GetVocaDetailInfoForHanjaSentence.ajax")
    @FormUrlEncoded
    Call<VocaHanjaSentence> getVocaDetailInfoForHanjaSentence(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_LANG_STUDY) String langStudy,
            @Field(Constant.API_KEY.KEY_LANG_MEANING) String langMeaning
    );

    @POST("UpdateVoiceFileInfo.ajax")
    @FormUrlEncoded
    Call<Boolean> updateVoiceFileInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_EXTENSION) String extension,
            @Field(Constant.API_KEY.KEY_FILE_SIZE) long fileSize
    );

    @POST("GetAllNativeSpeakers.ajax")
    @FormUrlEncoded
    Call<List<NativeSpeaker>> getAllNativeSpeakers(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("ChangePreferredNativeSpeakers.ajax")
    @FormUrlEncoded
    Call<Boolean> changePreferredNativeSpeakers(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_NATIVE_SPEAKER_ID) String speakerIds
    );

    @POST("ChnageStudyLang.ajax")
    @FormUrlEncoded
    Call<Boolean> changeStudyLang(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("GetMultipleVoiceFileVersion.ajax")
    @FormUrlEncoded
    Call<List<VocaDownload>> getMultipleVoiceFileVersion(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_STR_FILE_NAME_LIST) String fileNames
    );

    @POST("ChangeVocaDisplayOrderInUserVocaBook.ajax")
    @FormUrlEncoded
    Call<Boolean> changeVocaDisplayOrderInUserVocaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_DISP_ORDER) int displayOrder
    );

    @POST("GetVocaListToRecordAllFromServerWordBook.ajax")
    @FormUrlEncoded
    Call<VocaRecordListForBook> getVocaListToRecordAllFromServerWordBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord
    );

    @POST("updateWordMeaningWithID.ajax")
    @FormUrlEncoded
    Call<Boolean> updateWordMeaningWithID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_PRONOUNCE) String pronounce
    );

    @POST("getExpressionOfToday.ajax")
    @FormUrlEncoded
    Call<VocaStudy> getExpressionOfToday(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("updateLastAccessDate.ajax")
    @FormUrlEncoded
    Call<Boolean> updateLastAccessDate(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_ACCESS_OR_EXIT_APP) int accessOrExitApp,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,

            @Field(Constant.API_KEY.KEY_CLIENT_TYPE) String client_type,
            @Field(Constant.API_KEY.KEY_APP_NAME) String appName,
            @Field(Constant.API_KEY.KEY_APP_VERSION) String appVersion,
            @Field(Constant.API_KEY.KEY_DEVICE_TYPE) String deviceType,
            @Field(Constant.API_KEY.KEY_DEVICE_OS_VERSION) String deviceOsVersion);

    @POST("GetListOfStudentForThisVoca.ajax")
    @FormUrlEncoded
    Call<List<StudentVoice>> getListOfStudentForThisVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType
    );

    @POST("updateFirebaseToken.ajax")
    @FormUrlEncoded
    Call<Boolean> updateFirebaseToken(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_FIREBASE_TOKEN) String firebaseToken,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_APP_VERSION) String appVersion,
            @Field(Constant.API_KEY.KEY_APP_NAME) String appName,
            @Field(Constant.API_KEY.KEY_VOIP_TOKEN) String voipToken,
            @Field(Constant.API_KEY.KEY_CLIENT_TYPE) String clientType,
            @Field(Constant.API_KEY.KEY_DEVICE_TYPE) String deviceType,
            @Field(Constant.API_KEY.KEY_DEVICE_OS_VERSION) String deviceOsVersion
    );

    @POST("updateStudyLangAndMotherTongue.ajax")
    @FormUrlEncoded
    Call<Boolean> updateStudyLangAndMotherTongue(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("getListToPractice.ajax")
    @FormUrlEncoded
    Call<VocaPracticeListResponse> getListToPractice(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType
    );

    @POST("GetVocasToPracticeAlpahbet.ajax")
    @FormUrlEncoded
    Call<List<VocaPractice>> getVocasToPracticeAlpahbet(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetServerVocaBookListForNativeSpeaker.ajax")
    @FormUrlEncoded
    Call<VocaBookListNativeSpeakerResponse> getServerVocaBookListForNativeSpeaker(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCABOOK_PARENT_ID) int parentId,
            @Field(Constant.API_KEY.KEY_VOCABOOK_PRACTICE_ONLY) int practiceOnly
    );

    @POST("GetVocasFromDoYouKnowThisVoca.ajax")
    @FormUrlEncoded
    Call<List<VocaDoYouKnow>> getVocasFromDoYouKnowThisVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetCountOf1StAmkiGrade.ajax")
    @FormUrlEncoded
    Call<Integer> getCountOf1StAmkiGrade(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("getProfileInfo.ajax")
    @FormUrlEncoded
    Call<User> getProfileInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid
    );

    @POST("updateProfileInfo.ajax")
    @FormUrlEncoded
    Call<Boolean> updateProfileInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_NAME) String name,
            @Field(Constant.API_KEY.KEY_NATION) String nation,
            @Field(Constant.API_KEY.KEY_CITY) String city,
            @Field(Constant.API_KEY.KEY_SEX) int sex,
            @Field(Constant.API_KEY.KEY_AGE) int age,
            @Field(Constant.API_KEY.KEY_BIO) String bio
    );

    @POST("GetUserList.ajax")
    @FormUrlEncoded
    Call<UserListResponse> getUserList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_USER_LIST_BY_CATEGORY) int category,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord,
            @Field(Constant.API_KEY.KEY_SEARCH_STRING) String searchString
    );

    @POST("manageFollowingETC.ajax")
    @FormUrlEncoded
    Call<Boolean> manageFollowingETC(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_FOLLOWING_ETC_TYPE) String type,
            @Field(Constant.API_KEY.KEY_MANAGE_RECORD) int manageRecord
    );

    @POST("updateOpponentNameByMe.ajax")
    @FormUrlEncoded
    Call<Boolean> updateOpponentNameByMe(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_OPPONENT_NAME_BY_ME) String name,
            @Field(Constant.API_KEY.KEY_OPPONENT_DESC_BY_ME) String desc
    );

    @POST("removeMemorizeVoca.ajax")
    @FormUrlEncoded
    Call<Boolean> removeMemorizeVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_ID) String ids
    );

    @POST("updateSettingValue.ajax")
    @FormUrlEncoded
    Call<Boolean> updateSettingValue(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_MAX_HOMEWORK) int maxHomework,
            @Field(Constant.API_KEY.KEY_MAX_QUIZ) int maxQuiz,
            @Field(Constant.API_KEY.KEY_MAX_HANJA_QUIZ) int maxHanjaQuiz,
            @Field(Constant.API_KEY.KEY_ALLOW_CHAT) int allowChat,
            @Field(Constant.API_KEY.KEY_SHARE_MY_RECORDING) int shareMyRecording
    );

    @POST("GetChatroomList.ajax")
    @FormUrlEncoded
    Call<List<ChatRoom>> getChatroomList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid
    );

    @POST("GetChatroomIDandAllUserInfo.ajax")
    @FormUrlEncoded
    Call<ChatRoomInfo> getChatroomIDandAllUserInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_CLASS_LIST_ID) int classListId,
            @Field(Constant.API_KEY.KEY_CHATROOM_TYPE) int chatRoomType,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId
    );

    @POST("GetStudyUserInfoInChatroom.ajax")
    @FormUrlEncoded
    Call<ChatRoomInfo> getStudyUserInfoInChatroom(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_SEND_PN) int sendPN
    );

    @POST("JoinStudyModeInChatroom.ajax")
    @FormUrlEncoded
    Call<Boolean> joinStudyModeInChatroom(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_STUDY_ROLE) int studyRole
    );

    @POST("ExitStudyModeInTheChatroom.ajax")
    @FormUrlEncoded
    Call<Boolean> exitStudyModeInTheChatroom(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_STUDY_ROLE) int studyRole
    );

    @POST("GetVocasFromAllVocaBookInStudyMode.ajax")
    @FormUrlEncoded
    Call<VocaFromAllVocaBook> getVocasFromAllVocaBookInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_START_NO) int startNo,
            @Field(Constant.API_KEY.KEY_COUNT_RECORD) int countRecord,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_MAKE_RUBY_TEXT) int makeRubyText,
            @Field(Constant.API_KEY.KEY_MAKE_NEW_EXAM) int makeNewExam
    );

    @POST("SendVocaKnowToOthersInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendVocaKnowToOthersInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA_KNOW) int vocaKnow,
            @Field(Constant.API_KEY.KEY_VOCA_KNOWPRONOUNCE) int vocaKnowPronounce,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_TBL_SECTION) Integer tblSection,
            @Field(Constant.API_KEY.KEY_TBL_ROW) Integer tblRow,
            @Field(Constant.API_KEY.KEY_PN_TYPE) int pnType
    );

    @POST("ChangeEvaluateVocaGrade.ajax")
    @FormUrlEncoded
    Call<Boolean> changeEvaluateVocaGrade(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_EVALUATE_VOCA_GRADE) String evaluateGrade,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_TBL_SECTION) Integer tblSection,
            @Field(Constant.API_KEY.KEY_TBL_ROW) Integer tblRow,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("SendPronounceFeedbackToStudent.ajax")
    @FormUrlEncoded
    Call<Boolean> sendPronounceFeedbackToStudent(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_SENDER_ID) String senderId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_EVALUATE_VOCA_GRADE) String evaluateGrade,
            @Field(Constant.API_KEY.KEY_FEEDBACK_MESSAGE) String feedbackMessage,
            @Field(Constant.API_KEY.KEY_FILE_VERSION) int fileVersion,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA_DISPLAY) String vocaDisplay,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("SendPronounceFeedbackToOthersInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendPronounceFeedbackToOthersInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_SENDER_ID) String senderId,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_EVALUATE_VOCA_GRADE) String evaluateGrade,
            @Field(Constant.API_KEY.KEY_FEEDBACK_MESSAGE) String feedbackMessage,
            @Field(Constant.API_KEY.KEY_FILE_VERSION) int fileVersion,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA_DISPLAY) String vocaDisplay,
            @Field(Constant.API_KEY.KEY_TBL_SECTION) Integer tblSection,
            @Field(Constant.API_KEY.KEY_TBL_ROW) Integer tblRow,
            @Field(Constant.API_KEY.KEY_PN_TYPE) int pnType,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("SendVocabookInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendVocabookInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_TOPIC_BEGIN_INDEX) int topicBeginIndex,
            @Field(Constant.API_KEY.KEY_TOPIC_REPEATED_COUNT) int topicRepeatedCount,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LESSON_MODE) int lessonMode,
            @Field(Constant.API_KEY.KEY_LESSON_READING_ID) int lessonReadingId
    );

    @POST("SaveVocabookIDInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> saveVocabookIDInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_VOCABOOKS_CELL_INDEX) int cellIndex,
            @Field(Constant.API_KEY.KEY_SHOW_TOAST) int showToast,
            @Field(Constant.API_KEY.KEY_TOPIC_BEGIN_INDEX) int topicBeginIndex,
            @Field(Constant.API_KEY.KEY_TOPIC_REPEATED_COUNT) int topicRepeatedCount,
            @Field(Constant.API_KEY.KEY_LESSON_MODE) int lessonMode,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_GRAMMAR_ID) int grammarId,
            @Field(Constant.API_KEY.KEY_LESSON_READING_ID) int lessonReadingId
    );

    @POST("SaveStudiedWorkbookIDInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> saveStudiedWorkbookIDInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("GetTableVersion.ajax")
    @FormUrlEncoded
    Call<ResponseBody> getTableVersion(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_TBL_NAME) String tableName
    );

    @POST("DownloadTable.ajax")
    @FormUrlEncoded
    Call<ResponseBody> downloadTable(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_EMAIL) String email,
            @Field(Constant.API_KEY.KEY_TBL_NAME) String tableName
    );

    @POST("ShowAsteriskInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> showAsteriskInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_SHOW_ASTERISK) int showAsterisk
    );

    @POST("SendMessageInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendMessageInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_MESSAGE_ID) String messageId,
            @Field(Constant.API_KEY.KEY_MESSAGE) String message
    );

    @POST("SyncCellWithOtherUsersInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> syncCellWithOtherUsersInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_TBL_PARENT_SECTION) Integer tblParentSection,
            @Field(Constant.API_KEY.KEY_TBL_PARENT_ROW) Integer tblParentRow,
            @Field(Constant.API_KEY.KEY_TBL_SECTION) Integer tblSection,
            @Field(Constant.API_KEY.KEY_TBL_ROW) Integer tblRow,
            @Field(Constant.API_KEY.KEY_TBL_TYPE_SYNC) int tblTypeSync,
            @Field(Constant.API_KEY.KEY_IS_CELL_CHECKED) int isCellChecked,
            @Field(Constant.API_KEY.KEY_COUNT_OF_ORDER_FROM_CHECKED_MENU) int countOfOrder,
            @Field(Constant.API_KEY.KEY_BRANCH_SELECTED) String branchSelected,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("SideGlanceSentenceInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sideGlanceSentenceInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int bookId,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) int bookType,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_INDEX) int index,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("GetListOfLesson.ajax")
    @FormUrlEncoded
    Call<List<Lesson>> getListOfLesson(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_APP_TYPE) int appType,
            @Field(Constant.API_KEY.KEY_LESSON_FOR_WHOM) int forWhom,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("CreateOrGetStudyModeAndJoin.ajax")
    @FormUrlEncoded
    Call<ChatRoomInfo> createOrGetStudyModeAndJoin(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDY_ROLE) int studyRole,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("SendPushToStudentFinishStudyInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendPushToStudentFinishStudyInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_START_TIME) long startTime,
            @Field(Constant.API_KEY.KEY_FINISH_TIME) long finishTime,
            @Field(Constant.API_KEY.KEY_LAST_VOCABOOKS_ID) int lastBookId,
            @Field(Constant.API_KEY.KEY_LAST_VOCABOOK_TYPE) int lastBookType,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookIds,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) String bookTypes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("ConfirmFinishStudyInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> confirmFinishStudyInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_START_TIME) long startTime,
            @Field(Constant.API_KEY.KEY_FINISH_TIME) long finishTime,
            @Field(Constant.API_KEY.KEY_LAST_VOCABOOKS_ID) int lastBookId,
            @Field(Constant.API_KEY.KEY_LAST_VOCABOOK_TYPE) int lastBookType,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) String bookIds,
            @Field(Constant.API_KEY.KEY_WORDBOOK_TYPE) String bookTypes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_CONFIRM) int confirm
    );

    @POST("GetLessonOption.ajax")
    @FormUrlEncoded
    Call<LessonOption> getLessonOption(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LESSON_FOR_WHOM) int forWhom,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("SetLessonOption.ajax")
    @FormUrlEncoded
    Call<Boolean> setLessonOption(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LESSON_FOR_WHOM) int forWhom,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_ENABLE_LESSON_PN) int enableLessonPN,
            @Field(Constant.API_KEY.KEY_PN_MINUTES_BEFORE_BEGIN_LESSON) int minutesBeforeBegin,
            @Field(Constant.API_KEY.KEY_PN_MINUTES_BEFORE_FINISH_LESSON) int minutesBeforeFinish,
            @Field(Constant.API_KEY.KEY_LEAD_LESSON) int leadLesson,
            @Field(Constant.API_KEY.KEY_LANGUAGE_LEVEL) int languageLevel,
            @Field(Constant.API_KEY.KEY_FREE_TALKING) int freeTalking,
            @Field(Constant.API_KEY.KEY_SMALL_TALKING) int smallTalking,
            @Field(Constant.API_KEY.KEY_SPEAKING_SPEED) int speakingSpeech,
            @Field(Constant.API_KEY.KEY_READING_SPEED) int readingSpeech,
            @Field(Constant.API_KEY.KEY_CORRECT_PRONUNCIATION) int correctPronunciation,
            @Field(Constant.API_KEY.KEY_LESSON_REPEAT_TOPICS) int repeatTopics,
            @Field(Constant.API_KEY.KEY_LESSON_REPEAT_COUNT) int repeatCount,
            @Field(Constant.API_KEY.KEY_USER_WORD_EXAM_TYPE) int userWordExamType,
            @Field(Constant.API_KEY.KEY_RECORD_LESSON) int recordLesson,
            @Field(Constant.API_KEY.KEY_COUNT_OF_PHRASES_TO_STUDY_AT_ONCE) int countOfPhrasesToStudyAtOnce,
            @Field(Constant.API_KEY.KEY_STUDY_ORDER_HIDE_PART_OF_WORDS) int studyOrderHidePartOfWords,
            @Field(Constant.API_KEY.KEY_STUDY_ORDER_HIDE_ALL_PHRASES) int studyOrderHideAllPhrases,
            @Field(Constant.API_KEY.KEY_STUDY_ORDER_RANDOM_QUESTIONS) int studyOrderRandomQuestions
    );

    @POST("GetAllStudentListForLesson.ajax")
    @FormUrlEncoded
    Call<List<User>> getAllStudentListForLesson(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_SORT_TYPE) int sortType
    );

    @POST("GetAllTutorListForLesson.ajax")
    @FormUrlEncoded
    Call<List<User>> getAllTutorListForLesson(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_SORT_TYPE) int sortType
    );

    @POST("HasNewAppVersion.ajax")
    @FormUrlEncoded
    Call<Boolean> getHasNewAppVersion(
            @Field(Constant.API_KEY.KEY_CLIENT_TYPE) String clientType,
            @Field(Constant.API_KEY.KEY_VERSION) String version,
            @Field(Constant.API_KEY.KEY_APP_NAME) String appName
    );

    @POST("requestCallToOpponent.ajax")
    @FormUrlEncoded
    Call<Boolean> requestCallToOpponent(
            @Field(Constant.API_KEY.KEY_UUID) String uuid,
            @Field(Constant.API_KEY.KEY_CALL_UID) int uid,
            @Field(Constant.API_KEY.KEY_CALL_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_CALL_ROOMNAME) String roomName,
            @Field(Constant.API_KEY.KEY_CALL_TYPE) int type,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_OPPONENT_STUDY_ROLE) int studyRole
    );

    @POST("MakeAnExamInStudyMode.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatExam>> makeAnExamInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_EXAM_SOURCE) int examSource,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaIds,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) String vocaTypes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_USER_WORD_EXAM_TYPE) int userWordExamType
    );

    @POST("GetAnExamInStudyMode.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatExam>> getAnExamInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("ShuffleThisExamInStudyMode.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatExam>> shuffleThisExamInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("SendSelectAnswerAtExamInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendSelectAnswerAtExamInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_INDEX) int index,
            @Field(Constant.API_KEY.KEY_SELECTED_ANSWER_NUMBER) int selectedAnswerNumber,
            @Field(Constant.API_KEY.KEY_ANSWER_CORRECT) int isAnswerCorrect
    );

    @POST("UpdateOrMakeNewLesson.ajax")
    @FormUrlEncoded
    Call<Boolean> updateOrMakeNewLesson(
            @Field(Constant.API_KEY.KEY_ID) String ids,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) String studentIds,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) String tutorIds,
            @Field(Constant.API_KEY.KEY_START_TIME) String startTimes,
            @Field(Constant.API_KEY.KEY_FINISH_TIME) String finishTimes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLangs,
            @Field(Constant.API_KEY.KEY_LESSON_TYPE) String lessonTypes
    );

    @POST("replyCallFromOpponent.ajax")
    @FormUrlEncoded
    Call<Boolean> replyCallFromOpponent(
            @Field(Constant.API_KEY.KEY_UUID) String uuid,
            @Field(Constant.API_KEY.KEY_UID) int uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentId,
            @Field(Constant.API_KEY.KEY_REPLY_CALL_TYPE) int replyCallType,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) String chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId
    );

    @POST("DeleteLessons.ajax")
    @FormUrlEncoded
    Call<Boolean> deleteLessons(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LESSON_ID) String lessonIds
    );

    @POST("MakeRolePlayingContentsJson.ajax")
    @FormUrlEncoded
    Call<RolePlayingContent> makeRolePlayingContentsJson(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_LANGUAGE_LEVEL) int languageLevel,
            @Field(Constant.API_KEY.KEY_ROLE_PLAYING_CATEGORY_ID) int rolePlayingCategoryId,
            @Field(Constant.API_KEY.KEY_ROLE_PLAYING_TYPE) int rolePlayingType,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_MAKE_RUBY_TEXT) int makeRubyText
    );

    @POST("GetRolePlayingContentsJson.ajax")
    @FormUrlEncoded
    Call<RolePlayingContent> getRolePlayingContentsJson(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_ROLE_PLAYING_ID) int rolePlayingId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_MAKE_RUBY_TEXT) int makeRubyText
    );

    @POST("makeRubyText.ajax")
    @FormUrlEncoded
    Call<RubyTextModel> makeRubyText(
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_INPUT_TEXT) String inputText
    );

    @POST("makeRubyTextListTemp.ajax")
    @FormUrlEncoded
    Call<RubyListModel> makeRubyTextList(
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_WORDBOOK_ID) int wordBookId
    );

    @POST("GetRolePlayingCategoryList.ajax")
    @FormUrlEncoded
    Call<List<Category>> getRolePlayingCategoryList(
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_LANGUAGE_LEVEL) int languageLevel,
            @Field(Constant.API_KEY.KEY_ROLE_PLAYING_PARENT_ID) int rolePlayingParentId
    );

    @POST("setPronounceKnown.ajax")
    @FormUrlEncoded
    Call<ResponseBody> setPronounceKnown(
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId
    );

    @POST("setPronounceUnknown.ajax")
    @FormUrlEncoded
    Call<ResponseBody> setPronounceUnknown(
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaId
    );

    @POST("getCurrentLessonTopicOfUser.ajax")
    @FormUrlEncoded
    Call<CurrentLesson> getCurrentLessonTopicOfUser(
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("GetVocasFromVocaList.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatRuby>> getVocasFromVocaList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaIds,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) String vocaTypes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("GetVocasFromVocaListInVocabook.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatByCategory>> getVocasFromVocaListInVocabook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_VOCA_ID) String vocaIds,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) String vocaTypes,
            @Field(Constant.API_KEY.KEY_ID_IN_SERVER_VOCABOOK) String idsInVocaBook,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay
    );

    @POST("MakeQuizAtQuizMenu.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatExam>> makeQuizAtQuizMenu(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_USER_WORD_EXAM_TYPE) int examType,
            @Field(Constant.API_KEY.KEY_COUNT_OF_QUIZ) int countOfQuiz
    );

    @POST("updateQuizStartFinishStatus.ajax")
    @FormUrlEncoded
    Call<Integer> updateQuizStartFinishStatus(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_QUIZ_STATUS_ID) int quizStatusId,
            @Field(Constant.API_KEY.KEY_IS_FINISH) int isFinish,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("saveResutlOfEachQuestion.ajax")
    @FormUrlEncoded
    Call<Integer> saveResutlOfEachQuestion(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_OPPONENT_UID) int opponentUid,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA_DISPLAY) String vocaDisplay,
            @Field(Constant.API_KEY.KEY_ANSWER_CORRECT) int isAnswerCorrect,
            @Field(Constant.API_KEY.KEY_SOLVING_TIME) int solvingTime,
            @Field(Constant.API_KEY.KEY_QUIZ_STATUS_ID) int quizStatusId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("MakeWordQuizFromMultipleListOfVocaID.ajax")
    @FormUrlEncoded
    Call<List<VocaStudyChatExam>> makeWordQuizFromMultipleListOfVocaID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_MULTIPLE_CHOICE_QUESTION_TYPE) int multipleChoiceQuestionType,
            @Field(Constant.API_KEY.KEY_COUNT_OF_QUIZ) int countOfQuiz,
            @Field(Constant.API_KEY.KEY_STR_VOCA_ID_LIST) String vocaIds,
            @Field(Constant.API_KEY.KEY_STR_VOCA_TYPE_LIST) String vocaTypes,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int langStudyCode,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langMeaningCode
    );

    @POST("SendChatMessageInStudyMode.ajax")
    @FormUrlEncoded
    Call<ChatMessageResponse> sendChatMessageInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_MESSAGE) String message,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("SendGrammarWorkbookInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> sendGrammarWorkbookInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_GRAMMAR_ID) int grammarId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("MakeReadingContentsJsonInStudyMode.ajax")
    @FormUrlEncoded
    Call<VocaReading> makeReadingContentsJsonInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_STUDENT_LANG_MEANING_CODE) String studentLangDisplay,
            @Field(Constant.API_KEY.KEY_TUTOR_ID) int tutorId,
            @Field(Constant.API_KEY.KEY_READING_ID) int readingId,
            @Field(Constant.API_KEY.KEY_INPUT_TEXT) String inputText,
            @Field(Constant.API_KEY.KEY_MAKE_NEW_EXAM) int makeNewExam
    );

    @POST("GetReadingContentsJsonInStudyMode.ajax")
    @FormUrlEncoded
    Call<VocaReading> getReadingContentsJsonInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_STUDENT_ID) int studentId,
            @Field(Constant.API_KEY.KEY_LESSON_READING_ID) int lessonReadingId
    );

    @POST("showMeaningOnStudentViewInStudyMode.ajax")
    @FormUrlEncoded
    Call<Boolean> showMeaningOnStudentViewInStudyMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_SHOW_MEANING_AT_STUDENT) int showMeaningAtStudent
    );

    @POST("UpdateAppStateMode.ajax")
    @FormUrlEncoded
    Call<Boolean> updateAppStateMode(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_CHATROOM_ID) int chatRoomId,
            @Field(Constant.API_KEY.KEY_LESSON_ID) int lessonId,
            @Field(Constant.API_KEY.KEY_APP_STATE_MODE) int appStateMode
    );

    @GET("{fullUrl}")
    Call<Object> getVideoInformation(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String append,
            @Query("language") String language);

    @GET("{fullUrl}")
    Call<SearchVideoModel> searchMultiVideo(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page);

    @GET("{fullUrl}")
    Call<VideoInformationModel.Credits> getCredits(
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Query("api_key") String apiKey);

    @POST("searchHanja.ajax")
    @FormUrlEncoded
    Call<List<VocaHanjaSearch>> searchHanja(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int langStudyCode,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langMeaningCode,
            @Field(Constant.API_KEY.KEY_SEARCH_STRING) String searchString,
            @Field(Constant.API_KEY.KEY_SEARCH_HANJA_TYPE) int searchHanjaType,
            @Field(Constant.API_KEY.KEY_SEARCH_WITH_REGEX) int searchWithRegex
    );

    @POST("getAllVocasKnowAndBookmarkOfUser.ajax")
    @FormUrlEncoded
    Call<VocaKnowAndBookmarkList> getAllVocasKnowAndBookmarkOfUser(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang
    );

    @POST("updateMultipleWordMeaningWithID.ajax")
    Call<MultipleWordMeaningWithIDModel.ResponseModel> updateMultipleWordMeaningWithID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Body MultipleWordMeaningWithIDModel data);

    @POST("updateVocaBookMeaningWithID.ajax")
    @FormUrlEncoded
    Call<Boolean> updateVocaInSerVocabookDetail(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) int uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA) String voca, //실제 업데이트는 안함. 참조용.
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_ID_IN_VOCABOOK) int idInVocabook,
            @Field(Constant.API_KEY.KEY_VOCABOOK_TYPE_CODE) int vocabookTypeCode
    );
    @GET("ServerVocaBookInfo.ajax")
    Call<VocaDetailInfo> getServerVocaBookInfo(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) String uid,
            @Query(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Query(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Query(Constant.API_KEY.KEY_ID_IN_VOCABOOK) int idInVocabook,
            @Query(Constant.API_KEY.KEY_VOCABOOK_TYPE_CODE) int vocabookTypeCode
    );
}
