package com.dalread.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.model.AmkiGradeHeader;
import com.dalread.util.BaseVoca;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmkiGradeHeaderHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_grade_number)
    TextView tvGradeNumber;
    @BindView(R.id.ic_2)
    ImageView ic2;
    @BindView(R.id.ic_3)
    ImageView ic3;
    @BindView(R.id.ic_4)
    ImageView ic4;
    @BindView(R.id.tv_grade_text)
    TextView tvGradeText;
    @BindView(R.id.ic_play_all)
    View icPlayAll;

    public AmkiGradeHeaderHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        icPlayAll.setVisibility(View.GONE);
    }

    public void bind(AmkiGradeHeader header) {
        tvGradeNumber.setBackgroundResource(header.getPrimaryIconBackground());
        tvGradeNumber.setText(header.getPrimaryIconText());
        if (header.getSecondaryIcon() == 0) {
            ic2.setVisibility(View.GONE);
        } else {
            ic2.setBackgroundResource(header.getSecondaryIcon());
            ic2.setVisibility(View.VISIBLE);
        }
        if (BaseVoca.isDalvocaApp()) {
            if (header.getThirdIcon() == 0) {
                ic3.setVisibility(View.GONE);
            } else {
                ic3.setBackgroundResource(header.getThirdIcon());
                ic3.setVisibility(View.VISIBLE);
            }
            if (header.getFourthIcon() == 0) {
                ic4.setVisibility(View.GONE);
            } else {
                ic4.setBackgroundResource(header.getFourthIcon());
                ic4.setVisibility(View.VISIBLE);
            }
        } else {
            ic3.setVisibility(View.GONE);
            ic4.setVisibility(View.GONE);
        }
        tvGradeText.setText(header.getHeaderText());
    }
}
