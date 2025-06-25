package com.dalread.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;

/*
 * @deprecated Replaced by {@link #AbstractBaseVocaKnowActivity}
 * Will delte this, Use AbstractBaseVocaKnowActivity instead, 이건 지울거임. 이걸 상속하는 대신 AbstractBaseVocaKnowActivity를 조립해서 쓰도록
 */
@SuppressLint("NonConstantResourceId")
public abstract class BaseVocaKnowActivity extends BaseActivity {
    @BindColor(R.color.color_divider)
    protected int clDivider;
    @BindDimen(R.dimen.divider_height)
    protected float dividerHeight;
    @BindString(R.string.app_name)
    protected String appName;

    protected Context context;
    protected AlertDialog alertDialog;
    protected RegisterVocaDialog registerVocaDialog;
    protected OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initContext(); //Don't Change code order
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        initDialog();
        initListener();
        initEventBus(); // In VocaActivity.
    }

    protected void initListener() {
        onDoubleClickListenerOnBaseVocaKnow = onItemDoubleClickListener;
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    private void initContext() {
        context = this;
    }

    protected void initData() {
//        context = this;
    }

    protected void initLayout() {

    }

    protected void initDialog() {
        alertDialog = new AlertDialog(context);
        registerVocaDialog = new RegisterVocaDialog(context, onKnowClickListener);
    }

    private void openChangeKnowDialog(IVocaBasicItem voca) {
        registerVocaDialog.show(voca, false);
    }

    protected void checkLocalAndChangeVocaKnow(IVocaBasicItem iVocaBasicItem, int vocaKnow) {
        application.getDalAiImpl().checkLocalAndChangeVocaKnow(
                context,
                vocaKnow,
                BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                String.valueOf(iVocaBasicItem.getVIVocaId()),
                iVocaBasicItem.getVIVocaType(),
                new DalApiListener<Integer>() {

                    @Override
                    public void onSuccess(Integer newVocaKnow) {
                        updateVocaKnowInDB(iVocaBasicItem, newVocaKnow);
//                        refreshSearchViewAdapterVocaKnow(iVocaBasicItem, newVocaKnow);
                        updateVocaKnowInVariable(iVocaBasicItem, newVocaKnow);
                        //Will remove VOCA_KNOW_CHANGED code line later. Use VOCA_KNOW_CHANGED instead
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.VOCA_KNOW_CHANGED, iVocaBasicItem));
                        iVocaBasicItem.setVIVocaKnow(newVocaKnow);
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
        voca.setVIVocaKnowPronounce(BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
    }

    private void updateVocaKnowPronounceInVariable(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
    }

    protected void refreshVocaFromServer() {

    }

    public OnDoubleClickListener onItemDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            if (object instanceof IVocaBasicItem) {
                switch (view.getId()) {
                    case R.id.ivKnow:
                        openChangeKnowDialog((IVocaBasicItem) object);
                        break;
                    case R.id.iv_known:
                        checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                        break;
                    case R.id.iv_grade_1:
                        checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                        break;
                    case R.id.iv_grade_2:
                        checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                        break;
                    case R.id.iv_unknown:
                        checkLocalAndChangeVocaKnow((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                        break;
                    case R.id.tvVoca:
                    case R.id.ivCopy:
                        BaseVoca.openCopyDialog(BaseVocaKnowActivity.this, (IVocaBasicItem) object);
                        break;
                    case R.id.ivBookmark:
                        udpateBookmarkInDB((IVocaBasicItem) object);
                        break;
                    case R.id.ivWordList:
                        onClickWordListIcon(object);
                        break;
                    case R.id.ivEditView:
                        onClickEditIcon();
                        break;
                    default:
                        onClickDefaultOnItemDoubleClickListener(object);
                        break;
                }

            } else if (object instanceof String) {
                String text = (String) object;
                switch (view.getId()) {
                    case R.id.tvVoca:
                        Utils.copyToClipboard(BaseVocaKnowActivity.this, text, R.string.copied);
                        break;
                    case R.id.ivWordList:
                        onClickWordListText(text);
                        break;
                    default:
                        onClickDefaultTextOnItemDoubleClickListener(text);
                }
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            switchVocaKnow(object);
        }
    };

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

    protected boolean canSwitchVocaKnowObject(Object object) {
        return false;
    }
    protected void switchVocaKnow(Object object) {
        if (canSwitchVocaKnowObject(object)) {
            IVocaFullItem voca = (IVocaFullItem) object;
            if (voca.getVIVocaKnow() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
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
            changeVocaKnowPronounce(iVocaBasicItem, newVocaKnowPronounce);
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

    private void udpateBookmarkInDB(IVocaBasicItem item) {
        if ((item == null) || (!UserUtil.isLoggedIn(this, true)))
            return;

        udpateBookmarkInLocalDB(item);
        udpateBookmarkInServerDB((IVocaBasicItem) item);
        item.swapVIBookmark(); //Don't change code order. Swap bookmark in the variable here.
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.BOOKMARK_CHANGED, item));
        eventBus.post(new SuccessEvent(BaseEvent.Screen.COMMON_VOCA_DATA, BaseEvent.EventType.COMMON_VOCA_BOOKMKARK_CHANGED, item));

    }

    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {

    }
    private void udpateBookmarkInServerDB(IVocaBasicItem item) {
        if (item.isVIBookmark()) {
            application.getDalAiImpl().bookmarkDel(BaseEvent.Screen.WORD_LIST, sharedPreferences.getToken(), sharedPreferences.getUid(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), item.getVIVoca(), item.getVIVocaId().toString(), item.getVIVocaType());
        } else {
            application.getDalAiImpl().bookmarkAdd(BaseEvent.Screen.WORD_LIST, sharedPreferences.getToken(), sharedPreferences.getUid(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), item.getVIVoca(), item.getVIVocaId().toString(), item.getVIVocaType());
        }
    }


    protected void checkAndChangeVocaKnow(IVocaBasicItem voca, int vocaKnow) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().checkAndChangeVocaKnow(
                    context,
                    vocaKnow,
                    BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
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


    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {

    }

    public boolean updateModelWithNewVocaKnow(IVocaBasicItem iVocaBasicItem, int newVocaKnow, int newVocaKnowPronounce) {

        int vocaKnowToSet = newVocaKnow == -1 ? iVocaBasicItem.getVIVocaKnow() : newVocaKnow;
        int vocaKnowPronounceToSet = newVocaKnowPronounce == -1 ? iVocaBasicItem.getVIVocaKnowPronounce() : newVocaKnowPronounce;
        if (newVocaKnowPronounce == -1)
            vocaKnowPronounceToSet = BaseVocaKnow.setVocaKnowPronounceToKnownWhenVocaKnowIsKnown(newVocaKnow, vocaKnowPronounceToSet);

        boolean isVocaKnowValueChanged = BaseVocaKnow.isKnowValueChanged(iVocaBasicItem.getVIVocaKnow(), newVocaKnow, iVocaBasicItem.getVIVocaKnowPronounce(), vocaKnowPronounceToSet);
        if (isVocaKnowValueChanged) {
            iVocaBasicItem.setVIVocaKnow(vocaKnowToSet);
            iVocaBasicItem.setVIVocaKnowPronounce(vocaKnowPronounceToSet);
            return true;
        }

        return false;
    }


    public void updateIconVocaKnowPronounce(Context context, IVocaBasicItem iVocaBasicItem, ImageView ivVocaKnowPronounce) {
//        ivVocaKnowPronounce.setVisibility(VocaKnow.isShowVocaKnowPronounceIcon(iVocaBasicItem) ? View.VISIBLE : View.INVISIBLE);
//        boolean showKnowPronounceIcon = ViewUtil.showKnowPronounceIcon(SharedPreferencesDB.getInstance(context).getLangStudyCode(), iVocaBasicItem.getVIVocaKnow(), iVocaBasicItem.getVIVocaKnowPronounce());
//        ivVocaKnowPronounce.setVisibility(showKnowPronounceIcon ? View.VISIBLE : View.INVISIBLE);
        BaseVocaKnow.updateIconVocaKnowPronounce(context, ivVocaKnowPronounce, iVocaBasicItem);
    }

    public void updateIconVocaKnow(Context context, IVocaBasicItem iVocaBasicItem, TextView tvKnowIcon) {
        BaseVocaKnow.updateIconVocaKnow(context, tvKnowIcon, iVocaBasicItem.getVIVocaKnow());
    }

    public void updateIconVocaBookmark(IVocaBasicItem iVocaBasicItem, ImageView ivBookmark, boolean isDispalyIconAlways) {
//        ivBookmark.setVisibility(iVocaBasicItem.isVIBookmark() ? View.VISIBLE : View.INVISIBLE);
//        VocaKnow.updateBookmarkIcon(ivBookmark, iVocaBasicItem.isVIBookmark());
        BaseVocaKnow.updateIconVocaBookmark(ivBookmark, iVocaBasicItem.isVIBookmark(), isDispalyIconAlways);
    }
}
