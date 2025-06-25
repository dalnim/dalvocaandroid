package com.dalread.network;

import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.util.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface AraKoicaApi {

    @GET("getNewIctTermList.ajax")
    Call<List<DIC_ICT_TERM>> getNewIctTermList(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Query(Constant.API_KEY.KEY_AFTER_TIME) String afterTime);

    //이건 안됨.@Body한번 써볼려고 했는데...(일일히 인자로 넘기지말고 자바 클래스를 넘기고 싶음)
//    @POST("updateIctTerm.ajax")
//    Call<Boolean> updateIctTerm(
//            @Header(Constant.API_KEY.KEY_COOKIE) String token,
//            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
////            @Query(Constant.API_KEY.KEY_UID) int uid,
//            @Body DIC_ICT_TERM data
//    );

    @POST("updateIctTerm.ajax")
    @FormUrlEncoded
    Call<Boolean> updateIctTerm(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) int uid,
            @Field(Constant.API_KEY.KEY_ID) int id,
            @Field(Constant.API_KEY.KEY_TERM_ENG_ABBR) String termEngAbbr,
            @Field(Constant.API_KEY.KEY_TERM_ENG_FULL) String termEngFull,
            @Field(Constant.API_KEY.KEY_TERM_KO_TITLE) String termKoTitle,
            @Field(Constant.API_KEY.KEY_TERM_HANJA_TITLE) String termHanjaTitle,
            @Field(Constant.API_KEY.KEY_TERM_KO_SHORT) String termKoShort,
            @Field(Constant.API_KEY.KEY_TERM_KO_FULL) String termKoFull,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_ICT) int termGroupIct,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_HW) int termGroupHw,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_GIS) int termGroupGis,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_ETC) int termGroupEtc,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_NATION) String termGroupNation,
            @Field(Constant.API_KEY.KEY_ORIGINAL_ID) int originalId,
            @Field(Constant.API_KEY.KEY_URL) String url,
            @Field(Constant.API_KEY.KEY_MEMO) String memo,
            @Field(Constant.API_KEY.KEY_TERM_LEVEL) int termLevel
    );

    @POST("addIctTerm.ajax")
    @FormUrlEncoded
    Call<Integer> addIctTerm(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Header(Constant.API_KEY.KEY_CONTENT_TYPE) String contentType,
            @Field(Constant.API_KEY.KEY_UID) int uid,
            @Field(Constant.API_KEY.KEY_TERM_ENG_ABBR) String termEngAbbr,
            @Field(Constant.API_KEY.KEY_TERM_ENG_FULL) String termEngFull,
            @Field(Constant.API_KEY.KEY_TERM_KO_TITLE) String termKoTitle,
            @Field(Constant.API_KEY.KEY_TERM_HANJA_TITLE) String termHanjaTitle,
            @Field(Constant.API_KEY.KEY_TERM_KO_SHORT) String termKoShort,
            @Field(Constant.API_KEY.KEY_TERM_KO_FULL) String termKoFull,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_ICT) int termGroupIct,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_HW) int termGroupHw,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_GIS) int termGroupGis,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_ETC) int termGroupEtc,
            @Field(Constant.API_KEY.KEY_TERM_GROUP_NATION) String termGroupNation,
            @Field(Constant.API_KEY.KEY_ORIGINAL_ID) int originalId,
            @Field(Constant.API_KEY.KEY_URL) String url,
            @Field(Constant.API_KEY.KEY_MEMO) String memo,
            @Field(Constant.API_KEY.KEY_TERM_LEVEL) int termLevel
    );
}
