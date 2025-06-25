package com.dalread.network;

import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.util.Constant;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AraHanjaApi {

    @POST("addHanjaSentenceWithoutVocaId.ajax")
    @FormUrlEncoded
    Call<Integer> addHanjaSentenceWithoutVocaId(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_CH_S) String voca_ch_s,
            @Field(Constant.API_KEY.KEY_VOCA_JP) String voca_jp,
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_MEANING_ENG) String meaningEng,
            @Field(Constant.API_KEY.KEY_MEANING_ENG_DETAILED) String meaningEngDetail,
            @Field(Constant.API_KEY.KEY_PRONOUNCE) String pronounce,
            @Field(Constant.API_KEY.KEY_PRONOUNCE_CH_S) String pronounce_ch_s,
            @Field(Constant.API_KEY.KEY_PRONOUNCE_JP) String pronounce_jp
    );

    @GET("getHanjaBook.ajax")
    Call<DIC_HANJA_BOOK> getHanjaBook(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) int uid,
            @Query(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Query(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);

    @GET("getHanjaWord.ajax")
    Call<DIC_HANJA> getHanjaWord(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) int uid,
            @Query(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Query(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);

    @GET("getHanjaWordByVoca.ajax")
    Call<DIC_HANJA> getHanjaWord(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) int uid,
            @Query(Constant.API_KEY.KEY_VOCA) String voca);

    @GET("getHanjaSentence.ajax")
    Call<DIC_HANJA_SENTENCE> getHanjaSentence(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) int uid,
            @Query(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Query(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);

    @GET("getHanjaSentenceByVoca.ajax")
    Call<DIC_HANJA_SENTENCE> getHanjaSentenceByVoca(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_UID) int uid,
            @Query(Constant.API_KEY.KEY_VOCA) String voca);

    @GET("isExistHanjaByVocaAndType.ajax")
    Call<Boolean> isExistHanjaByVocaAndType(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_VOCA) String voca,
            @Query(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType);



    @POST("updateHanjaBookWithID.ajax")
    @FormUrlEncoded
    Call<Boolean> updateHanjaBookWithID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langMotherTongue,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_PRONOUNCE) String pronounce
    );
    @POST("updateHanjaSentenceMeaningWithID.ajax")
    @FormUrlEncoded
    Call<Boolean> updateHanjaSentenceMeaningWithID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_DELETED) int deleted,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_VOCA_JP) String voca_jp,
            @Field(Constant.API_KEY.KEY_VOCA_CH_S) String voca_ch_s,
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_MEANING_ENG) String meaningEng,
            @Field(Constant.API_KEY.KEY_MEANING_ENG_DETAILED) String meaningEngDetail,
            @Field(Constant.API_KEY.KEY_PRONOUNCE) String pronounce,
            @Field(Constant.API_KEY.KEY_PRONOUNCE_CH_S) String pronounce_ch_s,
            @Field(Constant.API_KEY.KEY_PRONOUNCE_JP) String pronounce_jp
    );

    @POST("updateHanjaWordMeaningWithID.ajax")
    @FormUrlEncoded
    Call<Boolean> updateHanjaWordMeaningWithID(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_LANG_STUDY_CODE) int studyLang,
            @Field(Constant.API_KEY.KEY_LANG_MEANING_CODE) int langDisplay,
            @Field(Constant.API_KEY.KEY_VOCA_ID) int vocaId,
            @Field(Constant.API_KEY.KEY_VOCA_TYPE) int vocaType,
            @Field(Constant.API_KEY.KEY_VOCA) String voca,
            @Field(Constant.API_KEY.KEY_MEANING1) String meaning1,
            @Field(Constant.API_KEY.KEY_PRONOUNCE1) String pronounce1,
            @Field(Constant.API_KEY.KEY_PRONOUNCE1_FIRST) String pronounce1_first,

            @Field(Constant.API_KEY.KEY_MEANING2) String meaning2,
            @Field(Constant.API_KEY.KEY_PRONOUNCE2) String pronounce2,
            @Field(Constant.API_KEY.KEY_PRONOUNCE2_FIRST) String pronounce2_first,

            @Field(Constant.API_KEY.KEY_MEANING3) String meaning3,
            @Field(Constant.API_KEY.KEY_PRONOUNCE3) String pronounce3,
            @Field(Constant.API_KEY.KEY_PRONOUNCE3_FIRST) String pronounce3_first,

            @Field(Constant.API_KEY.KEY_LEFTCOMPONENT) String leftComponent,
            @Field(Constant.API_KEY.KEY_RIGHTCOMPONENT) String rightComponent,
            @Field(Constant.API_KEY.KEY_MEANING) String meaning,
            @Field(Constant.API_KEY.KEY_MEANING_DETAILED) String meaningDetail,
            @Field(Constant.API_KEY.KEY_STROKES) int strokes,
            @Field(Constant.API_KEY.KEY_RADICAL) String radical,
            @Field(Constant.API_KEY.KEY_PINYIN) String pinyin,
            @Field(Constant.API_KEY.KEY_VOCAORI) String vocaOri,
            @Field(Constant.API_KEY.KEY_HANJA_KOREA) String hanjaKorea,
            @Field(Constant.API_KEY.KEY_HANJA_JAPAN) String hanjaJapan,
            @Field(Constant.API_KEY.KEY_HANJA_SIMPLIFIED) String hanjaSimplified,
            @Field(Constant.API_KEY.KEY_HANJA_TAIWAN) String hanjaTaiwan,
            @Field(Constant.API_KEY.KEY_HANJA_SHORT_FORM) String hanjaShortForm,
            @Field(Constant.API_KEY.KEY_HANJA_VARIANT_1) String hanjaVariant1,
            @Field(Constant.API_KEY.KEY_HANJA_VARIANT_2) String hanjaVariant2,
            @Field(Constant.API_KEY.KEY_HANJA_SOKJA) String hanjaSokja,
            @Field(Constant.API_KEY.KEY_KUNYOMI) String kunYomi,
            @Field(Constant.API_KEY.KEY_ONYOMI) String onYomi,
            @Field(Constant.API_KEY.KEY_MEANING_ENG) String meaningEng,
            @Field(Constant.API_KEY.KEY_MEANING_ENG_DETAILED) String meaningEngDetailed
    );


}
