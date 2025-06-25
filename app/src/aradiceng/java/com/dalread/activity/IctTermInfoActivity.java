package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityIctTermInfoBinding;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.CopyUtil;
import com.dalread.util.DateUtils;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

@SuppressLint("NonConstantResourceId")
public class IctTermInfoActivity extends BaseTermIctActivity implements View.OnClickListener  {
    protected final int TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW = 0;
    private DIC_ICT_TERM item;
    private ActivityIctTermInfoBinding binding;

    public static Intent createIntent(Context context, DIC_ICT_TERM item) {
        Intent intent = new Intent(context, IctTermInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, item.getVIVocaType());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, item.getVIVocaId());
        return intent;
    }

    protected View getContentView() {
        binding = ActivityIctTermInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_hanja_word_list;
    }


    @Override
    protected void initData() {
        super.initData();
        _initLayout();

//        item = subDatabase.getIctTermById(getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_ID, 0));
        hideMenusOnReleaseMode();
        setOnClickListeners();
        updateUI();
        updateSearchHistory();
    }

    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(this)) {
            binding.layoutItemHanjaSentenceInfoIconsHeader.ivEditView.setVisibility(View.VISIBLE);
        } else {
            binding.layoutItemHanjaSentenceInfoIconsHeader.ivEditView.setVisibility(View.GONE);
        }
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivEditView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void initDialog() {
        super.initDialog();
    }

    private void _initLayout() {
        if (toolbar != null) {
            toolbar.setTitle(R.string.detail_info);
        }
    }


    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case EDIT_ICT_TERM:
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        reloadData();
                }
                break;
            case MAIN:
                switch (successEvent.getEventType()) {
                    case BOOKMARK_CHANGED:
                        reloadData();
                }
                break;
        }
    }

    private void reloadData() {
//        item = subDatabase.getIctTermById(item.getID());
        if (item == null)
            return;
        updateUI();
    }
    private void updateSearchHistory() {
        item.setSEARCH_HISTORY(DateUtils.getCurrentDateTimeFullFormat());
//        subDatabase.updateSeachyHistory(item);
    }
    private void updateUI() {
        updateBookmarkIcon(item);
        setTextViewValue(binding.tvEngAbbr, item.getFirstItemToDisplay());
        setTextViewValue(binding.tvEngFull, item.getSecondItemToDisplay());
        if (Utils.hasValue(item.getURL())) {
            setTextViewValue(binding.tvUrl, StringUtils.getUnderlineOnWholeText(context, "Web Site"));
        }
        setTextViewValue(binding.tvKoTitle, item.getThirdItemToDisplay());
        setTextViewValue(binding.tvKoShort, item.getTERM_KO_SHORT());
        setTextViewValue(binding.tvKoFull, item.getTERM_KO_FULL());
    }

    private void setTextViewValue(TextView textView, CharSequence charSequence) {
        textView.setVisibility(View.GONE);
        if (Utils.hasValue(charSequence)) {
            textView.setText(charSequence);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void updateBookmarkIcon(@NonNull IVocaBasicItem hanja) {
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivBookmark.setImageResource(hanja.getVIBookmark() != null && hanja.getVIBookmark() == 1
                ? R.drawable.ic_favorite_new_on
                : R.drawable.ic_favorite_new_off);
    }

    private void setOnClickListeners() {
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivBookmark.setOnClickListener(this);
        binding.tvEngAbbr.setOnClickListener(this);
        binding.tvUrl.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivEditView.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivCopy.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebSearch.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionary.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryKorean.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryEn.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryEn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvUrl:
                Utils.openWeb(context, item.getURL());
                break;
            case R.id.ivBookmark:
                if (vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow != null) {
                    vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow.onClick(view, item);
                }
                break;
            case R.id.ivEditView:
                openEditHanjaMeaningScreen(item);
                break;
            case R.id.ivCopy:
                openCopyTermDialog(context, item);
                break;
            case R.id.tvEngAbbr:
                Utils.copyToClipboard(context, item.getVIVoca(), R.string.copied);
                break;
            case R.id.ivWebSearch:
                Utils.openWebSearch(context, item.getTERM_ENG_ABBR() + " " + item.getTERM_ENG_FULL() + " " + item.getTERM_KO_TITLE());
                break;
            case R.id.ivWebDictionary:
                Utils.openAraHanjaApp(context, item.getHanjaToSearchInWebDictionary());
                break;
            case R.id.ivWebDictionaryKorean:
                Utils.openWebDictionaryForKorean(context, item.getTERM_KO_TITLE());
                break;
            case R.id.ivWebDictionaryEn:
                Utils.openWebDictionaryForHanja_en(context, item.getTERM_ENG_ABBR());
                break;
            case R.id.ivWebDictionaryEn2:
                Utils.openWebDictionaryForHanja_en(context, item.getTERM_ENG_FULL());
                break;
        }
    }

    public void openCopyTermDialog(Context context, DIC_ICT_TERM itemLocal) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_term);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                0,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        String strToCopy = "";
                        switch (which) {
                            case 0:
                                strToCopy = itemLocal.getTERM_ENG_ABBR();
                                break;
                            case 1:
                                strToCopy = itemLocal.getTERM_ENG_FULL();
                                break;
                            case 2:
                                strToCopy = itemLocal.getTERM_KO_TITLE();
                                break;
                            case 3:
                                strToCopy = itemLocal.getTERM_HANJA_TITLE();
                                break;
                            case 4:
                                strToCopy = itemLocal.getTERM_KO_SHORT();
                                break;
                            case 5:
                                strToCopy = itemLocal.getTERM_KO_FULL();
                                break;
                            case 6:
                                strToCopy = itemLocal.getAllValue();
                                break;
                        }
                        String toastText = CopyUtil.getMessageInToastToShow(context, strToCopy);
                        Utils.copyToClipboard(context, strToCopy, toastText);
                    }
                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void openEditHanjaMeaningScreen(DIC_ICT_TERM dicHanjaSentence) {
        openNewScreen(
                EditTermIctActivity.createIntent(this, dicHanjaSentence)
        );
    }
}
