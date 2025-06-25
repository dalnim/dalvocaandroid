package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.BaseViewHolder;
import com.dalread.base.EnumLanguage;
import com.dalread.listener.OnClickListener;
import com.dalread.network.OpenSubtitleApi;
import com.dalread.util.AraStringUtils;
import com.dalread.util.Voca;

import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenSubtitlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OpenSubtitleApi.OSSubtitleResponse> openSubtitleItems;
    private int selectedItemPosition = -1;
    private OnClickListener onClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OpenSubtitlesHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_open_subtitles, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OpenSubtitlesHolder) {
            ((OpenSubtitlesHolder) holder).bind(openSubtitleItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return openSubtitleItems == null ? 0 : openSubtitleItems.size();
    }

    private void selectItem(int position) {
        int oldPosition = selectedItemPosition;
        selectedItemPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(position);
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
    }

    public void setOpenSubtitleItems(List<OpenSubtitleApi.OSSubtitleResponse> openSubtitleItems2) {
        this.openSubtitleItems = openSubtitleItems2;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class OpenSubtitlesHolder extends BaseViewHolder {

        @BindView(R.id.tv_lang)
        TextView tvLang;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_add_date)
        TextView tvAddDate;
        @BindView(R.id.tv_language_name)
        TextView tvLanguageName;
        @BindView(R.id.tv_download_count)
        TextView tvDownloadCount;
        @BindView(R.id.ic_check)
        ImageView icCheck;

        @BindString(R.string.tpl_add_date)
        String tplAddDate;
        @BindString(R.string.tpl_download_count)
        String tplDownloadCount;
        @BindString(R.string.tpl_language_name)
        String tplLanguageName;
        private OpenSubtitleApi.OSSubtitleResponse openSubtitleItem;

        public OpenSubtitlesHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(OpenSubtitleApi.OSSubtitleResponse openSubtitleItem) {
            this.openSubtitleItem = openSubtitleItem;

            String text;
            //
            EnumLanguage enumLanguage = EnumLanguage.findByFormatOs(openSubtitleItem.getAttributes().language);
            Locale locale = enumLanguage.getLocale();
            text = Voca.localeToEmoji(locale);
            tvLang.setText(text);

            //OpenSubtitles.org는 자막 확장자가 나왔었는데, com싸이트의 결과물은 확장자가 안나온다.
            tvName.setText(AraStringUtils.getSubtitleNameWithBoldExtension(openSubtitleItem.getAttributes().files.get(0).getFile_name()));
            //
            text = String.format(tplAddDate, openSubtitleItem.getAttributes().upload_date);
            tvAddDate.setText(text);
            //
            text = String.format(tplLanguageName, openSubtitleItem.getAttributes().language);
            tvLanguageName.setText(text);
            //
            text = String.format(tplDownloadCount, openSubtitleItem.getAttributes().download_count);
            tvDownloadCount.setText(text);
            //
            icCheck.setVisibility(getPositionForAdapter() == selectedItemPosition ? View.VISIBLE : View.GONE);
        }


        @OnClick(R.id.v_item)
        void onClick(View view) {
            selectItem(getPositionForAdapter());

            if (onClickListener != null) {
                onClickListener.onClick(view, openSubtitleItem);
            }
        }
    }
}
