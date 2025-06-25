package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.model.TBL_MESSAGE;
import com.dalread.model.VocaStudyChatAllWords;
import com.dalread.model.roleplaying.Category;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TypeChooseMessageDialog extends BaseDialog {

    @BindView(R.id.etInputMessage)
    EditText etInputMessage;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindColor(R.color.color_divider)
    int clDivider;
    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private final CenterLayoutManager centerLayoutManager;
    private final com.dalread.listener.OnClickListener listener;

    public TypeChooseMessageDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        setContentView(R.layout.dialog_type_choose_message);
        ButterKnife.bind(this);

        this.listener = listener;
        rvContent.setLayoutManager(centerLayoutManager = new CenterLayoutManager(context));
        rvContent.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    public void setAdapter(List<Object> contents, com.dalread.listener.OnClickListener listener) {
        Adapter adapter = new Adapter();
        adapter.setContents(contents);
        adapter.setListener(listener);
        rvContent.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return rvContent.getAdapter();
    }

    @OnClick(R.id.tv_close)
    void onCloseClick() {
        dismiss();
    }

    @OnClick(R.id.ivSendMessage)
    void onSendMessageClick(View view) {
        if (listener == null) return;
        final String value = etInputMessage.getText().toString();
        if (Utils.isEmpty(value)) return;
        listener.onClick(view, value);
        etInputMessage.setText(Constant.BASE_BLANK);
    }

    public void moveToCenter(int pos) {
        if (pos > -1) {
            rvContent.post(() -> centerLayoutManager.smoothScrollToPosition(rvContent, null, pos));
        }
    }

    static class Adapter extends RecyclerView.Adapter {
        private final List<Object> contents;
        private com.dalread.listener.OnClickListener listener;

        Adapter() {
            contents = new ArrayList<>();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_recycler_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Holder) {
                ((Holder) holder).bind(contents.get(position), listener);
            }
        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        void setContents(List<Object> contents1) {
            contents.clear();
            contents.addAll(contents1);
        }

        void setListener(com.dalread.listener.OnClickListener listener) {
            this.listener = listener;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_content)
        TextView tvContent;
        private Object object;
        private com.dalread.listener.OnClickListener listener;

        Holder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Object object, com.dalread.listener.OnClickListener listener) {
            this.object = object;
            this.listener = listener;

            String content;
            if (object instanceof TBL_MESSAGE) {
                TBL_MESSAGE message = (TBL_MESSAGE) object;
                content = message.generateMeaningContent();
            } else if (object instanceof Category) {
                Category category = (Category) object;
                content = category.getName();
            } else if (object instanceof VocaStudyChatAllWords) {
                VocaStudyChatAllWords voca = (VocaStudyChatAllWords) object;
                content = voca.getTitle();
            } else {
                content = null;
            }
            tvContent.setText(content);
        }

        @OnClick(R.id.tv_content)
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, object);
            }
        }
    }
}
