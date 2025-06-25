package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.component.FuriganaView;
import com.dalread.listener.OnClickListener;
import com.dalread.model.roleplaying.Content;
import com.dalread.model.roleplaying.ContentMain;
import com.dalread.model.roleplaying.ContentSubDetails;
import com.dalread.model.roleplaying.RolePlayingContent;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class RolePlayingContentAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_SUB_TITLE = TYPE_TITLE + 1;
    private static final int TYPE_SUB_DETAILS = TYPE_SUB_TITLE + 1;

    private List<Object> items;
    private int studyRole;
    private int studyLang;
    private OnClickListener listener;
    private String strTotal;
    private float total;
    private DecimalFormat decimalFormat;

    public RolePlayingContentAdapter() {
        items = new ArrayList<>();
        decimalFormat = new DecimalFormat("$#.##");
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Content)
            return TYPE_TITLE;
        if (item instanceof ContentMain)
            return TYPE_SUB_TITLE;
        return TYPE_SUB_DETAILS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE)
            return new ContentTitleHolder(getHolderView(parent, R.layout.item_role_playing_content_title));
        if (viewType == TYPE_SUB_TITLE)
            return new ContentSubTitleHolder(getHolderView(parent, R.layout.item_role_playing_content_sub_title));
        return new ContentSubDetailsHolder(getHolderView(parent, R.layout.item_role_playing_content_sub_details));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TITLE) {
            ((ContentTitleHolder) holder).bind((Content) items.get(position));
        } else if (viewType == TYPE_SUB_TITLE) {
            ((ContentSubTitleHolder) holder).bind((ContentMain) items.get(position));
        } else {
            ((ContentSubDetailsHolder) holder).bind((ContentSubDetails) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private View getHolderView(@NonNull ViewGroup parent, @LayoutRes int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    public void setData(RolePlayingContent rolePlayingContent) {
        items.clear();
        total = 0;
        if (rolePlayingContent == null || rolePlayingContent.getContent() == null || rolePlayingContent.getContent().getContentViewId() == -1)
            return;
        items.add(rolePlayingContent.getContent());
        for (int i = 0; i < rolePlayingContent.getContent().getContentMainList().size(); i++) {
            ContentMain contentMain = rolePlayingContent.getContent().getContentMainList().get(i);
            items.add(contentMain);
            for (int j = 0; j < contentMain.getContentSubDetails().size(); j++) {
                ContentSubDetails contentSubDetails = contentMain.getContentSubDetails().get(j);
                contentSubDetails.setTblSection(i);
                contentSubDetails.setTblRow(j);
                items.add(contentSubDetails);
                if (contentSubDetails.isChecked()) {
                    total += Utils.parseFloat(contentSubDetails.getPrice());
                }
            }
        }
    }

    public int notifyItemChanged(int tblSection, int tblRow, boolean checked, int countOfOrder) {
        for (int i = 0; i < items.size(); i++) {
            Object object = items.get(i);
            if (object instanceof ContentSubDetails) {
                ContentSubDetails contentSubDetails = (ContentSubDetails) object;
                if (contentSubDetails.getTblSection() == tblSection
                        && contentSubDetails.getTblRow() == tblRow) {
                    contentSubDetails.setChecked(checked);
                    if (checked) {
                        contentSubDetails.setCountOfOrder(countOfOrder);
                    }
                    calculateTotal(contentSubDetails);
                    notifyItemChanged(i);
                    return i;
                }
            }
        }
        return -1;
    }

    private void calculateTotal(ContentSubDetails contentSubDetails) {
        float price = Utils.parseFloat(contentSubDetails.getPrice()) * contentSubDetails.getCountOfOrder();
        if (contentSubDetails.isChecked()) {
            total += price;
        } else {
            total -= price;
            contentSubDetails.setCountOfOrder(0);
        }
        notifyItemChanged(0);
    }

    public void calculateTotalAndNotify(ContentSubDetails contentSubDetails) {
        calculateTotal(contentSubDetails);
        notifyItemChanged(items.indexOf(contentSubDetails));
    }

    public void setStrTotal(String strTotal) {
        this.strTotal = strTotal;
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    private boolean loadRubyOrNormalText(FuriganaView fv, String rubyText,
                                         TextView tv, String text) {
        fv.setIsKnownPronounceMeaning(Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
        fv.resetText();
        tv.setText("");
        if (TextUtils.isEmpty(rubyText) && TextUtils.isEmpty(text)) {
            fv.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            return false;
        }
        if (Constant.MAKE_RUBY_TEXT && !TextUtils.isEmpty(rubyText)) {
            if (studyLang == EnumLanguage.ENGLISH.getIdApi()) {
                fv.setTutor(true);
            } else {
                fv.setTutor(studyRole == Constant.STUDY_ROLE_TUTOR);
            }
            fv.setShowMeaningJPCN(!Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
            fv.setJText(rubyText);
            fv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            return true;
        }
        fv.setVisibility(View.GONE);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        return false;
    }

    /**
     * Inner View Holder classes
     */

    class ContentTitleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fv_title)
        FuriganaView fvTitle;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_total)
        TextView tvTotal;


        ContentTitleHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Content content) {
            loadRubyOrNormalText(
                    fvTitle, content.getContentTitleRubyText(),
                    tvTitle, content.getContentTitle()
            );
            tvTotal.setText(strTotal + ": " + decimalFormat.format(total));
        }
    }

    class ContentSubTitleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fv_sub_title)
        FuriganaView fvSubTitle;
        @BindView(R.id.tv_sub_title)
        TextView tvSubTitle;

        ContentSubTitleHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(ContentMain contentMain) {
            loadRubyOrNormalText(
                    fvSubTitle, contentMain.getContentSubTitleRubyText(),
                    tvSubTitle, contentMain.getContentSubTitle()
            );
        }
    }

    class ContentSubDetailsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_item)
        View vItem;
        @BindView(R.id.tv_count_of_order)
        TextView tvCountOfOrder;
        @BindView(R.id.cb_item)
        CheckBox cbItem;
        @BindView(R.id.fv_name)
        FuriganaView fvName;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_desc)
        TextView tvDesc;

        private ContentSubDetails contentSubDetails;
        private boolean flagLock;

        ContentSubDetailsHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(ContentSubDetails contentSubDetails) {
            this.contentSubDetails = contentSubDetails;

            String countOfOrder = contentSubDetails.getCountOfOrder() > 0 ? String.valueOf(contentSubDetails.getCountOfOrder()) : "";
            tvCountOfOrder.setText(countOfOrder);
            flagLock = true;
            cbItem.setChecked(contentSubDetails.isChecked());
            flagLock = false;
            loadRubyOrNormalText(
                    fvName, contentSubDetails.getNameRubyText(),
                    tvName, contentSubDetails.getName()
            );
            tvPrice.setText(decimalFormat.format(Utils.parseFloat(contentSubDetails.getPrice())));
            if (TextUtils.isEmpty(contentSubDetails.getDesc())) {
                tvDesc.setVisibility(View.GONE);
            } else {
                tvDesc.setText(contentSubDetails.getDesc());
                tvDesc.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }

        @OnCheckedChanged(R.id.cb_item)
        void onCheckedChanged(CompoundButton button, boolean checked) {
            vItem.setBackgroundResource(checked ? R.color.color_play_background : R.color.colorBackground);
            if (!flagLock) {
                contentSubDetails.setChecked(checked);
                if (listener != null) {
                    listener.onClick(button, contentSubDetails);
                }
            }
        }

        @OnClick(R.id.v_item)
        void onClick() {
            cbItem.setChecked(!cbItem.isChecked());
        }
    }
}
