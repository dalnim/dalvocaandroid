package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.WorkbookListUserVocaBookLocalAdapter;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumType;
import com.dalread.databinding.ActivityWorkbookListUserVocabookLocalBinding;
import com.dalread.dialog.AskRolePlayingPromptEditDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.UserVocabooksLocal;
import com.dalread.model.WordListType;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DialogUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.List;

public class WorkbookListUserVocaBookLocalActivity extends BaseConvActivity {
    private Context context;
    protected List<UserVocabooksLocal> list;
    protected WorkbookListUserVocaBookLocalAdapter adapter;
    private ActivityWorkbookListUserVocabookLocalBinding binding;
    private boolean loadList = true;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, WorkbookListUserVocaBookLocalActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.USER_VOCA_BOOK_LOCAL);
        return intent;
    }


    @Override
    protected View getContentView() {
        binding = ActivityWorkbookListUserVocabookLocalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        binding.fabMakeRolePlaying.setOnClickListener( v -> {
            openConversationRawDataActivity();
        });
        binding.fabChatGPT.setOnClickListener( v -> {
            openGptCustomTab();
        });
//        initData();
//        initLayout();
////        getData(category.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loadList) {
            getList();
            bindData();
            loadList = false;
        }
    }

    private void bindData() {
        adapter.setBooks(list);
        adapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        // toolbar
        binding.header.setTitle(R.string.view_title_user_role_playing);
//        binding.header.getIconRight().setVisibility(View.VISIBLE);
//        binding.header.getIconRight().setImageResource(R.drawable.ic_add_attachment);
//        binding.header.getTvRight().setVisibility(View.GONE);
    }
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
        DialogUtil.showPositiveDialog(this, R.string.dialog_title_how_to_make_role_playing, R.string.dialog_message_how_to_make_role_playing, R.string.ok, () -> {

        });
    }
    private void openGptCustomTab() {
        String content = sharedPreferences.getMakeRolePlayingContent();
        if (Utils.isEmpty(content)) {
            content = subDatabase.getGptShortCutListForMakeRolePlay(EnumLanguage.getMotherTongueLanguage(this));
            content += "\n\n" + subDatabase.getGptShortCutListForMakeRolePlayFormat(EnumLanguage.getMotherTongueLanguage(this));
        }
        AskRolePlayingPromptEditDialog dialog = new AskRolePlayingPromptEditDialog(WorkbookListUserVocaBookLocalActivity.this, content, (value) -> {
            sharedPreferences.setMakeRolePlayingContent(value);
            CopyTextUtil.copyToClipboard(WorkbookListUserVocaBookLocalActivity.this, value);
            ChatGptWebUtil.openUrlInCustomTab(WorkbookListUserVocaBookLocalActivity.this, customTabActivityHelper.getSession());
        });
        dialog.showDialog();
    }
    private void openConversationRawDataActivity() {
        Intent popupIntent = new Intent(context, ConversationRawDataActivity.class);
//        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //BroadcastReceiver에서 액티비티를 띄울려면 이게 꼭 있어야 한다.
        context.startActivity(popupIntent);
        loadList = true;
    }

    @Override
    protected void initData() {
        super.initData();
        context = this;
    }

    @Override
    protected void initLayout() {
        // recycler view
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkbookListUserVocaBookLocalAdapter(this, onDoubleClickListener);
        binding.rvBooks.setAdapter(adapter);
    }

    private void showInstructWhenNoList() {
        if (Utils.isEmpty(list)) {
            binding.rvBooks.setVisibility(View.GONE);
            binding.llHowToUse.setVisibility(View.VISIBLE);
        } else {
            binding.rvBooks.setVisibility(View.VISIBLE);
            binding.llHowToUse.setVisibility(View.GONE);
        }
    }


    private void getList() {
        list = subDatabase.getUserVocabooksLocalList();
        showInstructWhenNoList();
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            UserVocabooksLocal userVocabooksLocal = (UserVocabooksLocal) data;
            switch (view.getId()) {
                case R.id.vItem:
                    checkToOpenVocaListView(userVocabooksLocal);
                    break;
                case R.id.ivDelete:
                    deleteItem(userVocabooksLocal);
                    break;
                case R.id.ivInformation:
                    updateTitle(userVocabooksLocal);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };
    private void updateTitle(UserVocabooksLocal item) {
        TypeInputDialog dialog = new TypeInputDialog(this, R.string.rename, R.string.dialog_message_type_new_name, R.string.dialog_message_type_new_name, item.getTITLE(), new BaseDialogListener() {
            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                String newTitle = (String) data;
                if (Utils.isEmpty(newTitle)) {
                    ToastUtil.getInstance(WorkbookListUserVocaBookLocalActivity.this).show(R.string.toast_name_should_not_be_empty);
                } else {
                    item.setTITLE(newTitle);
                    subDatabase.updateUserVocabookLocal(item);
                    bindData();
                    dialog.dismiss();
                }
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
        });
        dialog.show();
    }
    private void deleteItem(UserVocabooksLocal item) {
        String message = getString(R.string.messsage_ask_to_delete_item_with_name, item.getTITLE());
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, message, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                if (subDatabase.deleteUserVocabookLocal(item.getID())) {
                    list.remove(item);
                    bindData();
                    showInstructWhenNoList();
                }
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void checkToOpenVocaListView(UserVocabooksLocal userVocabooksLocal) {
        if (userVocabooksLocal.getIBookUsed() == Constant.VOCABOOKS.USED.USE_FOR_FREE) {
            openVocaListView(userVocabooksLocal);
        } else {
            if (UserUtil.isLoggedIn(this, true)) {
                openVocaListView(userVocabooksLocal);
            }
        }
    }
    private void openVocaListView(UserVocabooksLocal userVocabooksLocal) {
        openNewScreen(
                ConvVocaListActivity.createIntentByUserVocaBookLocal(this, Integer.parseInt(String.valueOf(userVocabooksLocal.getIBookId())), userVocabooksLocal.getIBookName(null))
        );
    }
}
