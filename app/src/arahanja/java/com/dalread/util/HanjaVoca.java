package com.dalread.util;

import android.content.Context;

import com.dalread.AraHanjaApplication;
import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.model.VocaTypeId;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;

import java.util.ArrayList;
import java.util.List;

public class HanjaVoca {

    public static boolean refreshHanjaSentenceAndOpenEditView(Context context, DIC_HANJA_SENTENCE dicHanjaSentence, boolean isOpenEditView) {
        boolean blnResult = false;
        final int uid = SharedPreferencesDB.getInstance(context).getRealUid();
        if (Utils.isConnected(context)) {
            Loading.show(context);
            AraHanjaApplication application = (AraHanjaApplication)BaseApplication.getInstance();
            application.getAraHanjaApiImpl().getHanjaSentence(uid, Math.toIntExact(dicHanjaSentence.getID()), dicHanjaSentence.getHI_VOCA_TYPE(), new DalApiListener<DIC_HANJA_SENTENCE>() {
                @Override
                public void onSuccess(DIC_HANJA_SENTENCE response) {
                    if ((response != null) && (!dicHanjaSentence.equalFromServerHanjaToUpdateLocalData(response))) {
                        Voca.updateHanjaSentence(response);
                        ToastUtil.getInstance(context).show(R.string.updated_with_latest_data_from_server);
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.UPDATE_LOCAL_HANJA_SENTENCE_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN, response));
                    } else {
                        if (isOpenEditView) {
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_SENTENCE_SCREEN, dicHanjaSentence));
                        } else {
                            ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                        }
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    if (isOpenEditView) {
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_SENTENCE_SCREEN, dicHanjaSentence));
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                    }
                    Loading.hide();
                }
            });
        }
        return blnResult;
    }

    public static boolean getHanjaSentenceByVocaFromServer(Context context, String voca) {
        boolean blnResult = false;
        final int uid = SharedPreferencesDB.getInstance(context).getRealUid();
        if (Utils.isConnected(context)) {
            Loading.show(context);
            AraHanjaApplication application = (AraHanjaApplication)BaseApplication.getInstance();
            application.getAraHanjaApiImpl().getHanjaSentenceByVoca(uid, voca, new DalApiListener<DIC_HANJA_SENTENCE>() {
                @Override
                public void onSuccess(DIC_HANJA_SENTENCE response) {
                    if ((response != null)) {
                        Voca.updateHanjaSentence(response);
                        ToastUtil.getInstance(context).show(R.string.updated_with_latest_data_from_server);
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.UPDATE_LOCAL_HANJA_SENTENCE_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN, response));
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_no_data_in_the_server);
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    ToastUtil.getInstance(context).show(R.string.toast_no_data_in_the_server);
                    Loading.hide();
                }
            });
        }
        return blnResult;
    }

    public static boolean refreshHanjaWordAndOpenEditView(Context context, DIC_HANJA dicHanja, boolean isOpenEditView) {
        boolean blnResult = false;
        final int uid = SharedPreferencesDB.getInstance(context).getRealUid();
        if (Utils.isConnected(context)) {
            Loading.show(context);
            AraHanjaApplication application = (AraHanjaApplication)BaseApplication.getInstance();
            application.getAraHanjaApiImpl().getHanjaWord(uid, Math.toIntExact(dicHanja.getID()), dicHanja.getHI_VOCA_TYPE(), new DalApiListener<DIC_HANJA>() {
                @Override
                public void onSuccess(DIC_HANJA response) {
                    if ((response != null) && (!dicHanja.equalFromServerHanjaToUpdateLocalData(response))) {
                        Voca.updateHanjaWord(response);
                        ToastUtil.getInstance(context).show(R.string.updated_with_latest_data_from_server);
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.UPDATE_LOCAL_HANJA_WORD_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN, response));
                    } else {
                        if (isOpenEditView) {
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_WORD_SCREEN, dicHanja));
                        } else {
                            ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                        }
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    if (isOpenEditView) {
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_WORD_SCREEN, dicHanja));
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                    }
//                    openEditHanjaMeaningScreen(dicHanjaSentence);
                    Loading.hide();
                }
            });
        }
        return blnResult;
    }

    public static boolean getHanjaWordByVocaFromServer(Context context, String voca) {
        boolean blnResult = false;
        final int uid = SharedPreferencesDB.getInstance(context).getRealUid();
        if (Utils.isConnected(context)) {
            Loading.show(context);
            AraHanjaApplication application = (AraHanjaApplication)BaseApplication.getInstance();
            application.getAraHanjaApiImpl().getHanjaWordByVoca(uid, voca, new DalApiListener<DIC_HANJA>() {
                @Override
                public void onSuccess(DIC_HANJA response) {
                    if ((response != null)) {
                        Voca.updateHanjaWord(response);
                        ToastUtil.getInstance(context).show(R.string.updated_with_latest_data_from_server);
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.UPDATE_LOCAL_HANJA_WORD_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN, response));
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_no_data_in_the_server);
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    ToastUtil.getInstance(context).show(R.string.toast_no_data_in_the_server);
                    Loading.hide();
                }
            });
        }
        return blnResult;
    }

    public static boolean refreshHanjaBookAndOpenEditView(Context context, DIC_HANJA_BOOK dicHanjaBook, boolean isOpenEditView) {
        boolean blnResult = false;
        final int uid = SharedPreferencesDB.getInstance(context).getRealUid();
        if (Utils.isConnected(context)) {
            Loading.show(context);
            AraHanjaApplication application = (AraHanjaApplication)BaseApplication.getInstance();
            application.getAraHanjaApiImpl().getHanjaBook(uid, Math.toIntExact(dicHanjaBook.getID()), dicHanjaBook.getHI_VOCA_TYPE(), new DalApiListener<DIC_HANJA_BOOK>() {
                @Override
                public void onSuccess(DIC_HANJA_BOOK response) {
                    if ((response != null) && (!dicHanjaBook.equalFromServerHanjaToUpdateLocalData(response))) {
                        Voca.updateHanjaBook(response);
                        ToastUtil.getInstance(context).show(R.string.updated_with_latest_data_from_server);
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.UPDATE_LOCAL_HANJA_BOOK_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN, response));
                    } else {
                        if (isOpenEditView) {
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_BOOK_SCREEN, dicHanjaBook));
                        } else {
                            ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                        }
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    if (isOpenEditView) {
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.HANJA_VOCA, BaseEvent.EventType.OPEN_EDIT_HANJA_BOOK_SCREEN, dicHanjaBook));
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_nothing_to_update);
                    }
                }
            });
        }
        return blnResult;
    }

    public static List<VocaTypeId> getVocaTypeIdFromHanjaItemList(List<HanjaItem> hanjas) {
        List<VocaTypeId> vocaTypeIdList = new ArrayList<>();
        if (hanjas != null) {
            for (HanjaItem hanjaItem : hanjas) {
                VocaTypeId vocaTypeId = new VocaTypeId(hanjaItem.getHI_ID().intValue(), hanjaItem.getHI_VOCA_TYPE());
                vocaTypeIdList.add(vocaTypeId);
            }
        }
        return vocaTypeIdList;
    }

    public static String getUnicodeGroupName(long unicodeDec) {
        String unicodeGroupName = "";
        if ((19968 <= unicodeDec) && (unicodeDec <= 40959)) {
            unicodeGroupName = "한중일 통합 한자";
        } else if ((13312 <= unicodeDec) && (unicodeDec <= 19903)) {
            unicodeGroupName = "한중일 통합 한자 확장 A";
        } else if ((131072 <= unicodeDec) && (unicodeDec <= 173791)) {
            unicodeGroupName = "한중일 통합 한자 확장 B";
        } else if ((173824 <= unicodeDec) && (unicodeDec <= 177983)) {
            unicodeGroupName = "한중일 통합 한자 확장 C";
        } else if ((177984 <= unicodeDec) && (unicodeDec <= 178207)) {
            unicodeGroupName = "한중일 통합 한자 확장 D";
        } else if ((178208 <= unicodeDec) && (unicodeDec <= 183983)) {
            unicodeGroupName = "한중일 통합 한자 확장 E";
        } else if ((183984 <= unicodeDec) && (unicodeDec <= 191456)) {
            unicodeGroupName = "한중일 통합 한자 확장 F";
        } else if ((196608 <= unicodeDec) && (unicodeDec <= 201551)) {
            unicodeGroupName = "한중일 통합 한자 확장 G";
        } else if ((11904 <= unicodeDec) && (unicodeDec <= 12031)) {
            unicodeGroupName = "한중일 부수 보충";
        } else if ((12032 <= unicodeDec) && (unicodeDec <= 12255)) {
            unicodeGroupName = "강희자전 부수";
        } else if ((12736 <= unicodeDec) && (unicodeDec <= 12783)) {
            unicodeGroupName = "한중일 한자 획";
        } else if ((63744 <= unicodeDec) && (unicodeDec <= 64255)) {
            unicodeGroupName = "한중일 호환용 한자";
        } else if ((194560 <= unicodeDec) && (unicodeDec <= 195103)) {
            unicodeGroupName = "한중일 호환용 한자 보충";
        }
        return unicodeGroupName;
    }

    public static boolean isBookUsedType_Free(long bookUsedType) {
        return bookUsedType == Constant.VOCABOOKS.USED.USE_FOR_FREE;
    }
}
