package com.dalread.activity;

import android.content.DialogInterface;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.component.Toolbar;
import com.dalread.model.VocaInBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.BindString;

public class SelectVocaInVocaBookActivity extends VocaBookActivity {

    @BindString(R.string.msg_no_word_selected)
    String msgNoWord;
    @BindString(R.string.ok)
    String msgOk;
    @BindString(R.string.msg_add_to_book_success)
    String msgSuccess;
    @BindString(R.string.msg_add_to_book_failed)
    String msgFailed;

    @Override
    protected void initPopupMenu(Toolbar toolbar) {
        toolbar.setIconRight(R.drawable.ic_more_vert_white_24dp);
        popupMenu = new PopupMenu(this, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_select_voca, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addVocasToVocaBook();
                return true;
            }
        });
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();

        adapter.setSelecting(true);
    }

    @Override
    protected void handleInfoClick(VocaInBook voca) {
        voca.setVIChecked(!voca.isVIChecked());
        adapter.notifyRegisteredOrRemoved(voca);
    }

    private void addVocasToVocaBook() {
        if (Utils.isConnected(this)) {
            int uid = getUserID();
            if (uid > 0) {
                int targetBookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0);
                if (targetBookId > 0) {
                    StringBuilder vocaIds = new StringBuilder();
                    StringBuilder typeIds = new StringBuilder();
                    StringBuilder useServerVocabookDatas = new StringBuilder();
                    StringBuilder idInServerVocabooks = new StringBuilder();
                    int checkedCount = 0;
                    for (VocaInBook voca : vocas) {
                        if (voca.isVIChecked()) {
                            vocaIds.append(",").append(voca.getVIVocaId());
                            typeIds.append(",").append(voca.getVIVocaType());
                            useServerVocabookDatas.append(",").append(voca.getUseServerVocabookData());
                            idInServerVocabooks.append(",").append(voca.getIdInServerVocabook());
                            checkedCount++;
                        }
                    }
                    if (checkedCount == 0) {
                        alertDialog.show(msgNoWord, msgOk, null);
                    } else {
                        Loading.show(this);
                        final int finalCheckedCount = checkedCount;
                        application.getDalAiImpl().addVocasInUserVocaBook(
                                String.valueOf(uid),
                                sharedPreferences.getLangStudyCode(),
                                String.valueOf(targetBookId),
                                vocaIds.substring(1),
                                typeIds.substring(1),
                                useServerVocabookDatas.substring(1),
                                idInServerVocabooks.substring(1),
                                new DalApiListener<Boolean>() {

                                    @Override
                                    public void onSuccess(Boolean response) {
                                        Loading.hide();
                                        if (response) {
                                            alertDialog.show(msgSuccess, msgOk, new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, finalCheckedCount));
                                                    finish();
                                                }
                                            });
                                        } else {
                                            alertDialog.show(msgFailed, msgOk, null);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        alertDialog.show(msgFailed, msgOk, null);
                                    }
                                });
                    }
                } else if (BuildConfig.DEBUG) {
                    ToastUtil.getInstance(this).show("Dev: something went wrong!");
                }
            } else {
                alertDialog.showLogInRequired();
            }
        } else {
            alertDialog.showNoInternet();
        }
    }
}
