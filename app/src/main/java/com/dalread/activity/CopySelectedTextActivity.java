package com.dalread.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.DisplayOrderAdapter;
import com.dalread.base.VocaActivity;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperCallback;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnDragListener;
import com.dalread.model.DisplayObject;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CopySelectedTextActivity extends VocaActivity {

    @BindView(R.id.tv_preview)
    TextView tvPreview;
    @BindView(R.id.rv_display_order)
    RecyclerView rvDisplayOrder;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    @BindString(R.string.app_name)
    String appName;
    @BindString(R.string.meaning)
    String meaningName;
    @BindString(R.string.phrase)
    String phraseName;

    private Context context;
    private List<String> indexList = new ArrayList<>();
    private List<String> meaningList = new ArrayList<>();
    private List<String> phraseList = new ArrayList<>();
    private List<String> pronunciationList = new ArrayList<>();
    private boolean useIndex = true;
    private DisplayObject meaning;
    private DisplayObject phrase;
    private boolean usePronunciation = false;
    private boolean useTab = true;
    private String breakLineText;
    private String separateText;
    private String separateTextForPronunciation;
    private String previewText;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_copy_selected_text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
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

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @SuppressWarnings("unchecked")
    private void initData() {
        context = this;
        List<IVocaFullPlayTTSItem> playlistItems = (List<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        for (int i = 0; i < playlistItems.size(); i++) {
            IVocaFullPlayTTSItem playlistItem = playlistItems.get(i);
            indexList.add(String.valueOf(i + 1));
            meaningList.add(playlistItem.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
            phraseList.add(BaseVoca.getVocaDisplay(playlistItem));
            pronunciationList.add(TextUtils.isEmpty(playlistItem.getVIPronounce()) ? "" : playlistItem.getVIPronounce());
        }
        meaning = new DisplayObject(meaningName, 0);
        phrase = new DisplayObject(phraseName, 1);
    }

    private void initLayout() {
        tvPreview.setMovementMethod(new ScrollingMovementMethod());
        setPreviewText();

        DisplayOrderAdapter displayOrderAdapter = new DisplayOrderAdapter(Arrays.asList(meaning, phrase));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(displayOrderAdapter)
        );
        itemTouchHelper.attachToRecyclerView(rvDisplayOrder);
        displayOrderAdapter.setOnDragListener(new OnDragListener() {

            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onDragStopped() {
                setPreviewText();
            }
        });
        rvDisplayOrder.setHasFixedSize(true);
        rvDisplayOrder.setAdapter(displayOrderAdapter);
        rvDisplayOrder.setLayoutManager(new LinearLayoutManager(context));
        rvDisplayOrder.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void setPreviewText() {
        buildBreakLineText();
        buildSeparateText();
        buildSeparateTextForPronunciation();
        buildPreviewText();

        String text = "\n" + previewText + "\n";
        tvPreview.setText(text);
    }

    private void buildBreakLineText() {
        breakLineText = useTab ? "\n" : "\n\n";
    }

    private void buildSeparateText() {
        separateText = useTab ? "\t" : "\n";
    }

    private void buildSeparateTextForPronunciation() {
        separateTextForPronunciation = meaning.getOrder() >= 0 ? (useTab ? "\t" : " ") : "\n";
    }

    private void buildPreviewText() {
        StringBuilder stringBuilder = new StringBuilder(buildPreviewText(0));
        for (int i = 1; i < indexList.size(); i++) {
            stringBuilder.append(breakLineText).append(buildPreviewText(i));
        }
        previewText = stringBuilder.toString();
    }

    private String buildPreviewText(int position) {
        String text = "";
        if (useIndex) {
            text += indexList.get(position);
        }
        if (meaning.getOrder() >= 0 && phrase.getOrder() >= 0) {
            if (!text.isEmpty()) {
                text += separateText;
            }
//            if (meaning.getOrder() < phrase.getOrder()) {
//                text += meaningList.get(position) + separateText + buildPronunciationText(position, phraseList.get(position));
//            } else {
                text += buildPronunciationText(position, phraseList.get(position)) + separateText + meaningList.get(position);
//            }
        } else if (meaning.getOrder() >= 0) {
            if (!text.isEmpty()) {
                text += separateText;
            }
            text += meaningList.get(position);
        } else if (phrase.getOrder() >= 0) {
            if (!text.isEmpty()) {
                text += separateText;
            }
            text += buildPronunciationText(position, phraseList.get(position));
        } else {
            buildPronunciationText(position, text);
        }
        return text;
    }

    private String buildPronunciationText(int position, String text) {
        if (usePronunciation && !pronunciationList.get(position).isEmpty()) {
            if (!text.isEmpty()) {
                text += separateTextForPronunciation;
            }
            text += pronunciationList.get(position);
        }
        return text;
    }

    @OnCheckedChanged({R.id.sc_include_index, R.id.sc_include_pronunciation, R.id.sc_separate_by_tab})
    void onCheckedChanged(CompoundButton button, boolean checked) {
        int id = button.getId();
        if (id == R.id.sc_include_index) {
            useIndex = checked;
        } else if (id == R.id.sc_include_pronunciation) {
            usePronunciation = checked;
        } else {
            useTab = checked;
        }
        setPreviewText();
    }

    @OnClick({R.id.tv_right})
    void onCopyClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(appName, previewText));
            ToastUtil.getInstance(context).show(R.string.copied);
        }
    }
}
