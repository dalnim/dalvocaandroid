package com.dalread.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.dalread.R;
import com.dalread.databinding.ActivityConversationRawDataBinding;
import com.dalread.helper.ExecutorHelper;
import com.dalread.model.UserVocabookLocal;
import com.dalread.model.UserVocabooksLocal;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.EditTextUtils;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConversationRawDataActivity extends BaseConvActivity {
    private List<UserVocabookLocal> vocaList;
    private ActivityConversationRawDataBinding binding;
    private int newVocabooksId;
    private int newVocabookId;
    protected View getContentView() {
        binding = ActivityConversationRawDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    @Override
    protected int getContentViewId() {
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.header.setTitle(R.string.view_title_make_role_playing);
    }
    @Override
    public void onResume() {
        super.onResume();
        newVocabooksId = subDatabase.getNewIDInTable(Constant.PLAYER.SQL.TABLE.USER_VOCABOOKS_LOCAL);
        newVocabookId = subDatabase.getNewIDInTable(Constant.PLAYER.SQL.TABLE.USER_VOCABOOK_LOCAL);
    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.etTitle.addTextChangedListener(EditTextUtils.createTextWatcher(binding.etTitle));
    }
    @Override
    protected void initLayout() {
        super.initLayout();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.btnOk.setOnClickListener(view -> parserUserVocabookLocal());
        binding.btnClear.setOnClickListener(v -> binding.etConversationRawData.setText(""));
        binding.btnPaste.setOnClickListener(v -> {
            CopyTextUtil.getTextFromClipboardWithDelay(this, (text) -> {
                String normalizedText = text.replace( sharedPreferences.getMakeRolePlayingContent(), "")
                        .replace("\n\nModel: Default (GPT-3.5)", "")
                        .replace("\n\nModel: Default (GPT-4)", "")
                        .replace("\n\nUser\n\n\nChatGPT\n", "");
                binding.etConversationRawData.setText(normalizedText);
            });
        });
        EditTextUtils.setClearButtonOnTouchListener(binding.etTitle);
        binding.etTitle.setText(getString(R.string.title) +  " : " + subDatabase.getNewIDInTable(Constant.PLAYER.SQL.TABLE.USER_VOCABOOKS_LOCAL));
    }
    private void parserUserVocabookLocal() {
        ExecutorHelper executorHelper = new ExecutorHelper();
        Runnable task = () -> {
            Loading.showDelay(ConversationRawDataActivity.this);
            String conversationRawData = binding.etConversationRawData.getText().toString();
            if (Utils.isEmpty(conversationRawData)) {
                ToastUtil.getInstance(ConversationRawDataActivity.this).show(R.string.toast_no_role_playing_content);
            } else {
                vocaList = parserUserVocabookLocal(conversationRawData);
                if (Utils.isEmpty(vocaList)) {
                    ToastUtil.getInstance(ConversationRawDataActivity.this).show(R.string.toast_fail_to_make_role_playing);
                } else {
                    if (saveVocaListInUserVocaBook()) {
                        startActivity(ConvVocaListActivity.createIntentByUserVocaBookLocal(ConversationRawDataActivity.this, newVocabooksId, binding.etTitle.getText().toString()));
                    } else {
                        ToastUtil.getInstance(ConversationRawDataActivity.this).show(R.string.toast_fail_to_make_role_playing);
                    }
                }
            }
            Loading.hide();

        };
        executorHelper.executeTask(task);
    }

    private boolean saveVocaListInUserVocaBook() {
        return subDatabase.saveVocaListInUserVocaBook(makeUserVocabooksLocal(), vocaList);
    }
    private UserVocabooksLocal makeUserVocabooksLocal() {
        UserVocabooksLocal userVocabooksLocal = new UserVocabooksLocal();
        userVocabooksLocal.setID(newVocabooksId);
        userVocabooksLocal.setUID(UserUtil.getUserID(this));
        userVocabooksLocal.setTITLE(binding.etTitle.getText().toString());
        userVocabooksLocal.setLANG_STUDY(sharedPreferences.getLangStudyCode());
        return userVocabooksLocal;
    }
    private List<UserVocabookLocal> parserUserVocabookLocal(String conversationRawData) {
        List<UserVocabookLocal> list = new ArrayList<>();
        String[] lines = conversationRawData.trim().split("\n");
        // Parse each line and add it to the conversation list
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.replace("\\t", "\t").replace(")","").replace("(","\t");
            if (isNotValid(line))
                continue;

            UserVocabookLocal model = new UserVocabookLocal();
            String personAB = "";
            String studyLangPart = "";
            String motherTonguePart = "";
            line = line.replace(":", "\t").replace("\t\t", "\t");
            String[] parts = line.split("\t");
            personAB = parts[0].trim();
            studyLangPart = parts[1].trim();
            if (parts.length == 3) {
                motherTonguePart = parts[2].trim();
            }

            model.setVIId(newVocabookId);
            model.setVOCABOOKS_ID(newVocabooksId);
            model.setPersonAB(personAB);
            model.setDISP_ORDER(i * 10);
            model.setVIVoca(studyLangPart);
            model.setVIMeaning(null, motherTonguePart);
            list.add(model);
            newVocabookId++;
        }

        return list;
    }

    private static boolean isNotValid(String line) {
        if (Utils.isEmpty(line.trim()))
            return true;
        if (line.contains("\t")) {
            String[] parts = line.split("\t");
            if (parts.length <= 1) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}
