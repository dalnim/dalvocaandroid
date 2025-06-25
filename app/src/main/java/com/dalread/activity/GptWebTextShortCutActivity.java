package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.databinding.ActivityGptWebTextShortCutBinding;
import com.dalread.databinding.ItemGptWebTextShortCutBinding;
import com.dalread.dialog.ChatGptTextShortCutEditDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.GptMenuTextShortCutHelper;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.GptTextShortCut;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.util.List;

public class GptWebTextShortCutActivity extends BaseActivity {
    private static List<GptTextShortCut> textShortCutList;
    private static boolean hasConversation;
    private static String conversation;
    private GptWebTextShortCutAdapter adapter;
    private GptMenuTextShortCutHelper helper;
    private ActivityGptWebTextShortCutBinding binding;
    private SubDatabase subDatabase;
    protected View getContentView() {
        binding = ActivityGptWebTextShortCutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSubDatabase();
        conversation = sharedPreferences.getConversationToStudyInGptWeb();
        binding.header.setTitle(R.string.view_title_common_phrases);
        helper = new GptMenuTextShortCutHelper(this, subDatabase);
        textShortCutList = helper.getList();
        hasConversation = false;
        if (Utils.isEmpty(conversation)) {
            hasConversation = false;
        } else {
            hasConversation = true;
            GptTextShortCut conversationGptTextShortCut = new GptTextShortCut();
            conversationGptTextShortCut.setMEANINGByMenuLang(this, conversation);
            textShortCutList.add(0, conversationGptTextShortCut);
//            textShortCutList.add(0, conversation);
        }
        initRecyclerView();
        //클립보드에서 가져오는게 아니라. 이제 아래줄은 필요가 없음.
//        CopyTextUtil.getTextFromClipboardWithDelay(this, new CopyTextUtil.OnTextFromClipboardListener() {
//            @Override
//            public void onTextFromClipboard(String text) {
//                if (!Utils.isEmpty(text)) {
//                    clipboardText = text;
//                    hasClipboardText = true;
//                    textShortCutList.add(0, clipboardText);
//                }
//                initRecyclerView();
//            }
//        });
    }
    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(this);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }
    protected void initRecyclerView() {
        adapter = new GptWebTextShortCutAdapter(textShortCutList);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.addItemDecoration(new SeparatorDecoration(context, ContextCompat.getColor(context, R.color.color_divider), context.getResources().getDimension(R.dimen.divider_height)));
        udpateMessage();
    }

    private void udpateMessage() {
        if (hasConversation) {
            binding.tvMessage.setText(getString(R.string.this_is_list_of_common_phrases) + "\n" + getString(R.string.this_is_list_of_common_phrases_has_clipboard_text));
        } else {
            binding.tvMessage.setText(getString(R.string.this_is_list_of_common_phrases));
        }
    }


    public class GptWebTextShortCutAdapter extends RecyclerView.Adapter<GptWebTextShortCutAdapter.MenuViewHolder> {
        private List<GptTextShortCut> textShortCutList;
        public GptWebTextShortCutAdapter(List<GptTextShortCut> textShortCutList) {
            this.textShortCutList = textShortCutList;
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemGptWebTextShortCutBinding binding = ItemGptWebTextShortCutBinding.inflate(inflater, parent, false);
            return new MenuViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            holder.bind(position, this);
        }

        @Override
        public int getItemCount() {
            return textShortCutList.size();
        }

        private class MenuViewHolder extends RecyclerView.ViewHolder {
            private final ItemGptWebTextShortCutBinding binding;

            public MenuViewHolder(@NonNull ItemGptWebTextShortCutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

            }

            public void bind(int position, GptWebTextShortCutAdapter adapter) {
                GptTextShortCut textShortCut = textShortCutList.get(position);
                String shortCut = textShortCut.getMEANINGByMenuLang(context);
                updateVisibility_DeleteIcon(textShortCut, position);
                if ((position == 0) && (Utils.isEmpty(shortCut))) {
                    binding.tvShortcut.setHint("학습할 문장입니다.");
                }
                binding.tvShortcut.setText(shortCut);
                onDeleteItem(position);
                if (!(hasConversation && position == 0)) {
                    onEditItem(adapter, textShortCut, position);
                }
                onCopyItem(textShortCut);
            }

            private void updateVisibility_DeleteIcon(GptTextShortCut textShortCut, int position) {
                if (hasConversation && position == 0) {
                    binding.ivDelete.setVisibility(View.GONE);
                } else {
                    if (textShortCut.isSystemDefault()) {
                        binding.ivDelete.setVisibility(View.GONE);
                    } else {
                        binding.ivDelete.setVisibility(View.VISIBLE);
                    }
                }
            }

            private void onCopyItem(GptTextShortCut textShortCut) {
                binding.ibCopy.setOnClickListener( v -> {
                    CopyTextUtil.copyToClipboardShowWhatCopied(context, StringUtils.insertText(textShortCut.getMEANING(EnumLanguage.getMotherTongueLanguage(context)), conversation));
                });
            }

            private void onEditItem(GptWebTextShortCutAdapter adapter, GptTextShortCut originalShortCut, int position) {
                binding.llRoot.setOnClickListener( v -> {
                    ChatGptTextShortCutEditDialog dialog = new ChatGptTextShortCutEditDialog(GptWebTextShortCutActivity.this, originalShortCut, true,(shortCut) -> {
//                        textShortCutList.set(position, shortCut);
                        if (hasConversation && position == 0) {

                        } else {
                            helper.save(shortCut);
                        }
                        adapter.notifyDataSetChanged();
                    });
                    dialog.showDialog();
                });
            }
            private void onDeleteItem(int position) {
                binding.ivDelete.setOnClickListener(v -> {
                    final YesNoDialog dialog = new YesNoDialog(GptWebTextShortCutActivity.this, R.string.warning, R.string.messsage_ask_to_delete_item, null, new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            GptTextShortCut item = textShortCutList.get(position);
                            if (helper.delete(item.getId())) {
                                textShortCutList.remove(position);
                                notifyItemRemoved(getBindingAdapterPosition());
                            }
                        }
                        @Override
                        public void onNoClick(View view, Object object) {

                        }
                    });
                    dialog.show();

                });
            }
        }
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
        ChatGptTextShortCutEditDialog dialog = new ChatGptTextShortCutEditDialog(GptWebTextShortCutActivity.this, new GptTextShortCut(), false, (shortCut) -> {
            if (helper.add(shortCut)) {
                textShortCutList.add(shortCut);
                adapter.notifyItemInserted(textShortCutList.size() - 1);
            }
        });
        dialog.showDialog();
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


