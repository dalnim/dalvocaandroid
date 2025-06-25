package com.dalread.network;

import com.dalread.AraHanjaApplication;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.util.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AraHanjaApiImpl {
    private final String TAG = "AraHanjaApiImpl";
    private AraHanjaApplication mApplication;

    public AraHanjaApiImpl(AraHanjaApplication mApplication) {
        this.mApplication = mApplication;
    }

    public void addHanjaSentenceWithoutVocaId(DIC_HANJA_SENTENCE dicHanjaSentence, final DalApiListener<Integer> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Integer> call = mApplication.getAraHanjaApi().addHanjaSentenceWithoutVocaId(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                3, //sharedPreferences.getLangMeaningCode(), //MEANINGCODE IS ENGLISH NOW, So I use hardcoded value for temp.
                dicHanjaSentence.getVIVocaType(),
                dicHanjaSentence.getVOCA(),
                dicHanjaSentence.getVOCA_CH_S(),
                dicHanjaSentence.getVOCA_JP(),
                dicHanjaSentence.getMEANING_KO(),
                dicHanjaSentence.getMEANING_KO_DETAILED(),
                dicHanjaSentence.getMEANING_ENG(),
                dicHanjaSentence.getMEANING_ENG_DETAILED(),
                dicHanjaSentence.getPRONOUNCE(),
                dicHanjaSentence.getPRONOUNCE_CH_S(),
                dicHanjaSentence.getPRONOUNCE_JP()
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

    public void getHanjaBook(final int uid, final int vocaId, final int vocaType, final DalApiListener<DIC_HANJA_BOOK> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<DIC_HANJA_BOOK> call = mApplication.getAraHanjaApi().getHanjaBook(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, vocaId, vocaType
        );
        call.enqueue(new Callback<DIC_HANJA_BOOK>() {
            @Override
            public void onResponse(Call<DIC_HANJA_BOOK> call, Response<DIC_HANJA_BOOK> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DIC_HANJA_BOOK> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getHanjaWord(final int uid, final int vocaId, final int vocaType, final DalApiListener<DIC_HANJA> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<DIC_HANJA> call = mApplication.getAraHanjaApi().getHanjaWord(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, vocaId, vocaType
            );
        call.enqueue(new Callback<DIC_HANJA>() {
            @Override
            public void onResponse(Call<DIC_HANJA> call, Response<DIC_HANJA> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DIC_HANJA> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    //Need to check this API is working correctly.
    public void getHanjaWordByVoca(final int uid, final String voca, final DalApiListener<DIC_HANJA> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<DIC_HANJA> call = mApplication.getAraHanjaApi().getHanjaWord(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, voca
        );
        call.enqueue(new Callback<DIC_HANJA>() {
            @Override
            public void onResponse(Call<DIC_HANJA> call, Response<DIC_HANJA> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DIC_HANJA> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getHanjaSentence(final int uid, final int vocaId, final int vocaType, final DalApiListener<DIC_HANJA_SENTENCE> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<DIC_HANJA_SENTENCE> call = mApplication.getAraHanjaApi().getHanjaSentence(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, vocaId, vocaType
        );
        call.enqueue(new Callback<DIC_HANJA_SENTENCE>() {
            @Override
            public void onResponse(Call<DIC_HANJA_SENTENCE> call, Response<DIC_HANJA_SENTENCE> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DIC_HANJA_SENTENCE> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void getHanjaSentenceByVoca(final int uid, final String voca, final DalApiListener<DIC_HANJA_SENTENCE> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<DIC_HANJA_SENTENCE> call = mApplication.getAraHanjaApi().getHanjaSentenceByVoca(
                token, Constant.API.HEADER_CONTENT_TYPE, uid, voca
        );
        call.enqueue(new Callback<DIC_HANJA_SENTENCE>() {
            @Override
            public void onResponse(Call<DIC_HANJA_SENTENCE> call, Response<DIC_HANJA_SENTENCE> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DIC_HANJA_SENTENCE> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void isExistHanjaByVocaAndType(final String voca, final int vocaType, final DalApiListener<Boolean> listener) {
        String token = mApplication.getSharedPref().getToken();
        Call<Boolean> call = mApplication.getAraHanjaApi().isExistHanjaByVocaAndType(
                token, Constant.API.HEADER_CONTENT_TYPE, voca, vocaType
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



    public void updateHanjaBookWithID(DIC_HANJA_BOOK item, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getAraHanjaApi().updateHanjaBookWithID(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                3, //sharedPreferences.getLangMeaningCode(), //MEANINGCODE IS ENGLISH NOW, So I use hardcoded value for temp.
                item.getID().intValue(),
                item.getHI_VOCA_TYPE(),
                item.getVOCA(),
                item.getMEANING_KO(),
                item.getMEANING_KO_DETAILED(),
                item.getPRONOUNCE()
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


    public void updateHanjaSentenceMeaningWithID(DIC_HANJA_SENTENCE dicHanjaSentence, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getAraHanjaApi().updateHanjaSentenceMeaningWithID(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                3, //sharedPreferences.getLangMeaningCode(), //MEANINGCODE IS ENGLISH NOW, So I use hardcoded value for temp.
                dicHanjaSentence.getID().intValue(),
                dicHanjaSentence.getVIVocaType(),
                Integer.parseInt(String.valueOf(dicHanjaSentence.getDELETED())),
                dicHanjaSentence.getVOCA(),
                dicHanjaSentence.getVOCA_JP(),
                dicHanjaSentence.getVOCA_CH_S(),
                dicHanjaSentence.getMEANING_KO(),
                dicHanjaSentence.getMEANING_KO_DETAILED(),
                dicHanjaSentence.getMEANING_ENG(),
                dicHanjaSentence.getMEANING_ENG_DETAILED(),
                dicHanjaSentence.getPRONOUNCE(),
                dicHanjaSentence.getPRONOUNCE_CH_S(),
                dicHanjaSentence.getPRONOUNCE_JP()
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


    public void updateHanjaWordMeaningWithID(DIC_HANJA dicHanja, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<Boolean> call = mApplication.getAraHanjaApi().updateHanjaWordMeaningWithID(
                sharedPreferences.getToken(),
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                3, //sharedPreferences.getLangMeaningCode(), //MEANINGCODE IS ENGLISH NOW, So I use hardcoded value for temp.
                dicHanja.getID().intValue(),
                dicHanja.getVIVocaType(),
                dicHanja.getVOCA(),
                dicHanja.getMEANING1(),
                dicHanja.getPRONOUNCE1(),
                dicHanja.getPRONOUNCE1_FIRST(),
                dicHanja.getMEANING2(),
                dicHanja.getPRONOUNCE2(),
                dicHanja.getPRONOUNCE2_FIRST(),
                dicHanja.getMEANING3(),
                dicHanja.getPRONOUNCE3(),
                dicHanja.getPRONOUNCE3_FIRST(),
                dicHanja.getLEFTCOMPONENT(),
                dicHanja.getRIGHTCOMPONENT(),
                dicHanja.getMEANING(),
                dicHanja.getMEANING_DETAILED(),
                dicHanja.getSTROKES().intValue(),
                dicHanja.getRADICAL(),
                dicHanja.getPINYIN(),
                dicHanja.getVOCAORI(),
                dicHanja.getHANJA_KOREA(),
                dicHanja.getHANJA_JAPAN(),
                dicHanja.getHANJA_SIMPLIFIED(),
                dicHanja.getHANJA_TAIWAN(),
                dicHanja.getHANJA_SHORT_FORM(),
                dicHanja.getHANJA_VARIANT_1(),
                dicHanja.getHANJA_VARIANT_2(),
                dicHanja.getHANJA_SOKJA(),
                dicHanja.getKUNYOMI(),
                dicHanja.getONYOMI(),
                dicHanja.getMEANING_ENG(),
                dicHanja.getMEANING_ENG_DETAILED()
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

}
