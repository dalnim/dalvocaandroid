package com.dalread.composition;

import android.content.Context;
import android.view.View;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.VocaTypeIdListWithComma;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.List;

public abstract class AbstractBaseVocaKnowActivity {
    private static AbstractBaseVocaKnowActivity instance;
    protected Context context;
    private BaseApplication application;
    private SharedPreferencesDB sharedPreferences;
    private AlertDialog alertDialog;
    protected RegisterVocaDialog registerVocaDialog;
    public OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
//    private EventBus eventBus;

    public AbstractBaseVocaKnowActivity(Context context) {
        this.context = context;
        preInit();
        initListener();
        initDialog();
//        initEventBus();
    }

    private void preInit() {
        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();
    }

    private void initListener() {
        onDoubleClickListenerOnBaseVocaKnow = new OnDoubleClickListener() {
            @Override
            public void onClick(View view, Object object) {
                boolean isContinue = false;
                if (AppFlavorUtil.isAraKoicaApp() || AppFlavorUtil.isAraPlayerApp() || AppFlavorUtil.isAraHanjaApp()){
                    isContinue = true;
                } else if (UserUtil.isLoggedIn(context, true) && (object instanceof IVocaBasicItem)) {
                    isContinue = true;
                }

                if (isContinue) {
                    switch (view.getId()) {
                        case R.id.ivKnow: //이건 아는정도를 고르는 RegisterVocaDialog를 띄운다.
                            openChangeKnowDialog((IVocaBasicItem) object);
                            break;
                        case R.id.iv_known: //이건 Voca Know 4버튼을 눌렀을때.
                            checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                            break;
                        case R.id.iv_grade_1: //이건 Voca Know 4버튼을 눌렀을때.
                            checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                            break;
                        case R.id.iv_grade_2:
                            checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                            break;
                        case R.id.iv_unknown:
                            checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                            break;
                        case R.id.ivBookmark:
                            udpateBookmarkInDB((IVocaBasicItem) object);
                            break;
                    }
                }
            }

            @Override
            public void onDoubleClick(View view, Object object) {
                boolean isContinue = false;
                if (AppFlavorUtil.isAraKoicaApp()){
                    isContinue = true;
                } else if (UserUtil.isLoggedIn(context, true)) {
                    isContinue = true;
                }
                if (isContinue) {
                    switchVocaKnow(object);
                }
            }
        };
    }

    protected void initDialog() {
        alertDialog = new AlertDialog(context);
        registerVocaDialog = new RegisterVocaDialog(context, onKnowClickListener);
    }

//    protected void initEventBus() {
//        eventBus = EventBus.getDefault();
//        if (!eventBus.isRegistered(this)) {
//            eventBus.register(this);
//        }
//    }
//
//    protected void unRegisterEventBus() {
//        if (eventBus != null && eventBus.isRegistered(this)) {
//            eventBus.unregister(this);
//        }
//    }

    private void openChangeKnowDialog(IVocaBasicItem voca) {
        registerVocaDialog.show(voca, false);
    }

    public void checkLocalAndChangeVocaKnow(IVocaBasicItem iVocaBasicItem, int vocaKnow) {
        application.getDalAiImpl().checkLocalAndChangeVocaKnow(
                context,
                vocaKnow,
                com.dalread.util.BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                String.valueOf(iVocaBasicItem.getVIVocaId()),
                iVocaBasicItem.getVIVocaType(),
                new DalApiListener<Integer>() {

                    @Override
                    public void onSuccess(Integer newVocaKnow) {
                        updateVocaKnowInDB(iVocaBasicItem, newVocaKnow);
//                        refreshSearchViewAdapterVocaKnow(iVocaBasicItem, newVocaKnow);
                        updateVocaKnowInVariable(iVocaBasicItem, newVocaKnow);
                        //Will remove VOCA_KNOW_CHANGED code line later. Use COMMON_VOCA_KNOW_CHANGED instead
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.VOCA_KNOW_CHANGED, iVocaBasicItem));
//                        iVocaBasicItem.setVIVocaKnow(newVocaKnow); //Do I need this? 이건 updateVocaKnowInVariable(iVocaBasicItem, newVocaKnow);에서 이미 수행함.
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.COMMON_VOCA_DATA, BaseEvent.EventType.COMMON_VOCA_KNOW_CHANGED, iVocaBasicItem));

                    }

                    @Override
                    public void onFailure(String error) {
                    }
                }
        );
    }

    private void changeVocaKnowPronounce(IVocaBasicItem iVocaBasicItem, int vocaKnowPronounce) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().changeMultipleVocaKnow(
                    context,
                    iVocaBasicItem.getVIVocaKnow(),
                    vocaKnowPronounce,
                    String.valueOf(iVocaBasicItem.getVIVocaId()),
                    String.valueOf(iVocaBasicItem.getVIVocaType()),
                    new DalApiListener<Integer>() {

                        @Override
                        public void onSuccess(Integer vocaKnowPronounce) {
                            updateVocaKnowPronounceInDB(iVocaBasicItem, vocaKnowPronounce);
////                            saveVocaKnowPronounce(voca, vocaKnowPronounce);
//                            refreshAdapterVocaKnowPronounce(iVocaBasicItem, vocaKnowPronounce);
                            updateVocaKnowPronounceInVariable(iVocaBasicItem, vocaKnowPronounce);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.VOCA_KNOW_CHANGED, iVocaBasicItem));
                            iVocaBasicItem.setVIVocaKnowPronounce(vocaKnowPronounce);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.COMMON_VOCA_DATA, BaseEvent.EventType.COMMON_VOCA_KNOWPRONOUNCE_CHANGED, iVocaBasicItem));

                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        } else {
            alertDialog.showNoInternet();
        }
    }

    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {

    }

    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {

    }

    private void updateVocaKnowInVariable(IVocaBasicItem voca, int newVocaKnow) {
        voca.setVIVocaKnow(newVocaKnow);
        voca.setVIVocaKnowPronounce(com.dalread.util.BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
    }

    private void updateVocaKnowPronounceInVariable(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
    }

    protected void updateMulitpleVocaKnowInDB(String vocaTypeListWithComma, String vocaIDListWithComma, int newVocaKnow) {

    }
    private void updateMultipleVocaKnowInVariable(String vocaTypeListWithComma, String vocaIDListWithComma, int newVocaKnow) {
//        voca.setVIVocaKnow(newVocaKnow);
//        voca.setVIVocaKnowPronounce(com.dalread.util.BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
    }


    protected void onClickWordListIcon(Object object) {

    }
    protected void onClickWordListText(String text) {

    }
    protected void onClickEditIcon() {

    }

    protected void onClickDefaultOnItemDoubleClickListener(Object object) {

    }
    protected void onClickDefaultTextOnItemDoubleClickListener(String text) {

    }

    public void switchVocaKnow(Object object) {
        if (object instanceof IVocaBasicItem) {
            IVocaBasicItem voca = (IVocaBasicItem) object;
//            if (voca.getVIVocaKnow() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            if (BaseVocaKnow.isUnknownAndLess(voca)) {
                checkLocalAndChangeVocaKnow(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
            } else {
                checkLocalAndChangeVocaKnow(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
            }
        }

    }

    protected final OnKnowChangeListener onKnowClickListener = new OnKnowChangeListener() {

        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
            checkLocalAndChangeVocaKnow(iVocaBasicItem, newVocaKnow);
        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {
            changeVocaKnowPronounce(iVocaBasicItem, newVocaKnowPronounce); //이건 제대로 안된다. newVocaKnow를 리턴한다. 나중에 수정할것
        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {
            udpateBookmarkInDB(iVocaBasicItem);

        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {
            udpateBookmarkInDB(iVocaBasicItem);
        }

        @Override
        public void onDismiss() {

        }
    };

    public void updateHasVoiceFile(IVocaBasicItem item, int hasVoiceFile) {
        item.setVIVoiceFile(hasVoiceFile);
        udpateHasVoiceFileInLocalDB(item);
    }

    protected void udpateHasVoiceFileInLocalDB(IVocaBasicItem item) {

    }

    private void udpateBookmarkInDB(IVocaBasicItem item) {
        if (!AppFlavorUtil.isAraKoicaApp()) {
            if ((item == null) || (!UserUtil.isLoggedIn(context, true)))
                return;
        }

        //아래 3개는 순서를 바꾸면 안됨.
        item.swapVIBookmark();
        udpateBookmarkInLocalDB(item);
        udpateBookmarkIcon(item);
        if (!AppFlavorUtil.isAraKoicaApp()) {
            udpateBookmarkInServerDB((IVocaBasicItem) item);
        }
    }

    private void udpateBookmarkIcon(IVocaBasicItem item) {
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.BOOKMARK_CHANGED, item));
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.COMMON_VOCA_DATA, BaseEvent.EventType.COMMON_VOCA_BOOKMKARK_CHANGED, item));
    }

    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {

    }
    private void udpateBookmarkInServerDB(IVocaBasicItem item) {
        if (item.isVIBookmark()) {
            application.getDalAiImpl().bookmarkAdd(BaseEvent.Screen.WORD_LIST, sharedPreferences.getToken(), sharedPreferences.getUid(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), item.getVIVoca(), item.getVIVocaId().toString(), item.getVIVocaType());
        } else {
            application.getDalAiImpl().bookmarkDel(BaseEvent.Screen.WORD_LIST, sharedPreferences.getToken(), sharedPreferences.getUid(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), item.getVIVoca(), item.getVIVocaId().toString(), item.getVIVocaType());
        }
    }


    public void checkAndChangeVocaKnow(IVocaBasicItem voca, int vocaKnow) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().checkAndChangeVocaKnow(
                    context,
                    vocaKnow,
                    com.dalread.util.BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                    String.valueOf(voca.getVIVocaId()),
                    voca.getVIVocaType(),
                    new DalApiListener<Integer>() {

                        @Override
                        public void onSuccess(Integer newVocaKnow) {
                            onVocaKnowChanged(voca.getVIVocaId(), newVocaKnow);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.VOCA_KNOW_CHANGED, true));
//                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, true));
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        } else {
            alertDialog.showNoInternet();
        }
    }

    public void changeMultipleVocaKnow(List<IVocaBasicItem> vocaList, int vocaKnow) {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            VocaTypeIdListWithComma vocaTypeAndIDListWithComma = BaseVocaList.getVocaTypeAndIDListWithComma(vocaList);
            String vocaTypeListWithComma = vocaTypeAndIDListWithComma.getVocaTypeListWithComma();
            String vocaIDListWithComma = vocaTypeAndIDListWithComma.getVocaIdListWithComma();
            application.getDalAiImpl().changeMultipleVocaKnow(
                    String.valueOf(vocaKnow),
                    String.valueOf(vocaKnow),
                    vocaIDListWithComma,
                    vocaTypeListWithComma,
                    new DalApiListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (response == true) {
                                updateMulitpleVocaKnowInDB(vocaTypeListWithComma, vocaIDListWithComma, vocaKnow);
                                updateMultipleVocaKnowInVariable(vocaTypeListWithComma, vocaIDListWithComma, vocaKnow);
//
//
//                                long vocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
//                                hanjaItemList.stream().forEach(e -> { e.setHI_VOCA_KNOW((long) vocaKnow); e.setHI_VOCA_KNOWPRONOUNCE(vocaKnowPronounce);});
//                                bindData(hanjaItemList);
//                                Voca.updateMultipleVocaKnow(hanjaItemList, vocaKnow);
                                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MULTIPLE_VOCA_KNOW_CHANGED, vocaKnow));
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
    }

    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {

    }

    protected void onMultipleVocaKnowChanged(String vocaTypeListWithComma, String vocaIDListWithComma, int vocaKnow) {

    }

    public void refreshVocaFromServer() {

    }
}
