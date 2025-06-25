package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.DialogRecyclerViewBinding;
import com.dalread.model.TBL_MESSAGE;
import com.dalread.model.VocaStudyChatAllWords;
import com.dalread.model.roleplaying.Category;
import com.dalread.util.BaseBindUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecyclerViewDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private CenterLayoutManager centerLayoutManager;
    private com.dalread.listener.OnClickListener listener;
    private boolean isUpdateData = false;
    private DialogRecyclerViewBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogRecyclerViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public RecyclerViewDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        setDialogSizeWider(true);
        setOnDismissListener(this);
        this.listener = listener;
        binding.rvContent.setLayoutManager(centerLayoutManager = new CenterLayoutManager(context));
        binding.rvContent.addItemDecoration(new SeparatorDecoration(context, ContextCompat.getColor(context, R.color.backgroundDialogBorderColor), BaseBindUtils.getDividerHeight(getContext())));
    }

    public void setAdapter(List<Object> contents, com.dalread.listener.OnClickListener listener) {
        binding.tvTitle.setText(R.string.choose);

        Adapter adapter = new Adapter();
        adapter.setContents(contents);
        adapter.setListener(listener);
        binding.rvContent.setAdapter(adapter);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        setAdapter(adapter, null);
    }

    public void setAdapter(RecyclerView.Adapter adapter, String title) {
        if (TextUtils.isEmpty(title)) {
            binding.tvTitle.setText(R.string.choose);
        } else {
            binding.tvTitle.setText(title);
        }

        binding.rvContent.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return binding.rvContent.getAdapter();
    }

    @Override
    protected void initOnClickListener() {
        binding.tvClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }


//    @OnClick({R.id.tv_close})
//    void onCloseClick() {
//        dismiss();
//    }

    public void showCloseButton(boolean showButton) {
        binding.tvClose.setVisibility(showButton ? View.VISIBLE : View.GONE);
    }

    public void moveToCenter(int pos) {
        if (pos > -1) {
            binding.rvContent.post(() -> centerLayoutManager.smoothScrollToPosition(binding.rvContent, null, pos));
        }
    }

    public void setUpdateData(boolean updateData) {
        isUpdateData = updateData;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            listener.onClick(null, isUpdateData);
        }
    }

    static class Adapter extends RecyclerView.Adapter {

        private List<Object> contents;
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
