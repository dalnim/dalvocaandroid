package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.StudyWritingEnglishAdapter;
import com.dalread.base.BaseVocaStudyFragment;
import com.dalread.component.ItemOffsetDecoration;
import com.dalread.listener.OnWordClickListener;
import com.dalread.model.VocaStudy;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class StudyChoosingFragment extends BaseVocaStudyFragment {

    @BindView(R.id.tvVoca)
    TextView tvVoca;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.tv_practice)
    TextView tvPractice;

    @BindDimen(R.dimen.study_english_small_box_width)
    int smallBoxWidth;
    @BindDimen(R.dimen.study_english_small_box_offset)
    int smallBoxOffset;

    @BindString(R.string.tpl_practice_writing)
    String tplPraticeWriting;

    private VocaStudy voca;
    private String vocaDisplay;
    private List<String> splitedVoca;
    private List<String> shuffleSplitedVoca;
    private StudyWritingEnglishAdapter adapter;
    private String strVoca;
    private int posVoca;
    private int practiceCount;
    private Vibrator vibrator;
    private long[] vibratePattern;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_study_choosing;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();
        initVibrator();
    }

    private void initData() {
        voca = (VocaStudy) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        vocaDisplay = BaseVoca.getVocaDisplay(voca);
        splitedVoca = BaseVoca.splitVocaStudy(vocaDisplay);
        Collections.shuffle(shuffleSplitedVoca = new ArrayList<>(splitedVoca));
        strVoca = "";
        posVoca = 0;
        practiceCount = Constant.PRACTICE_COUNT_MAX;
    }

    private void initLayout() {
        tvVoca.setText(R.string.msg_making_sentence);
        int noOfColumns = BaseVoca.calculateNoOfColumns(getContext(), smallBoxWidth + smallBoxOffset, 0);
        rvVoca.setLayoutManager(new GridLayoutManager(getContext(), noOfColumns));
        rvVoca.addItemDecoration(new ItemOffsetDecoration(smallBoxOffset));
        rvVoca.setAdapter(adapter = new StudyWritingEnglishAdapter(shuffleSplitedVoca, onWordClickListener));
        tvPractice.setText(String.format(tplPraticeWriting, practiceCount));
    }

    private void initVibrator() {
        if (activity != null) {
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibratePattern = new long[]{0, 1000};
        }
    }

    @OnClick(R.id.ic_refresh)
    void onClick(View view) {
        practiceCount = Constant.PRACTICE_COUNT_MAX;
        refreshData();
        refreshLayout();
    }

    private void refreshData() {
        Collections.shuffle(shuffleSplitedVoca = new ArrayList<>(splitedVoca));
        strVoca = "";
        posVoca = 0;
    }

    private void refreshLayout() {
        tvVoca.setText(R.string.msg_making_sentence);
        adapter.setData(shuffleSplitedVoca);
        adapter.notifyDataSetChanged();
        tvPractice.setText(String.format(tplPraticeWriting, practiceCount));
    }

    private OnWordClickListener onWordClickListener = new OnWordClickListener() {

        @Override
        public void onClick(String word, int pos) {
            if (posVoca >= splitedVoca.size()) {
                return;
            }
            if (activity != null) {
                activity.displayStar();
            }
            if (word.equals(splitedVoca.get(posVoca))) {
                if (!strVoca.isEmpty() && vocaDisplay.contains(" ")) {
                    strVoca += " ";
                }
                strVoca += word;
                tvVoca.setText(strVoca);
                shuffleSplitedVoca.set(pos, "");
                adapter.notifyItemChanged(pos);
                if (++posVoca == splitedVoca.size()) {
                    rvVoca.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (--practiceCount > 0) {
                                refreshData();
                                refreshLayout();
                            } else if (activity != null) {
                                activity.finishWriting();
                            }
                        }
                    }, 500);
                }
            } else if (vibrator != null && vibrator.hasVibrator()) {
//                vibrator.vibrate(vibratePattern, -1);
            }
        }
    };
}
