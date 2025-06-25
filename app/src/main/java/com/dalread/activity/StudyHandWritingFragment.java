package com.dalread.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaStudyFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.FragmentStudyHandWritingBinding;
import com.dalread.model.VocaStudy;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.ToastUtil;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.OnClick;

public class StudyHandWritingFragment extends BaseVocaStudyFragment {
    @BindString(R.string.tpl_practice_writing)
    String tplPraticeWriting;

    private VocaStudy voca;
    private String vocaDisplay;
    private List<String> splitList;
    private List<Integer> posList;
    private String[] hanajList;
    private int pos;
    private boolean visible;
    private int practiceCount;

    private FragmentStudyHandWritingBinding binding;
    protected View getContentView() {
        binding = FragmentStudyHandWritingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }
//
//    @Override
//    protected int getContentViewId() {
//        return R.layout.fragment_study_hand_writing;
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preInitData();
        initData();
        initLayout();
        initListener();
        getHanajInfo();
    }

    private void preInitData() {
        voca = (VocaStudy) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        vocaDisplay = BaseVoca.getVocaDisplay(voca);
        BaseVoca.splitVocaStudyHandWriting(vocaDisplay, splitList = new ArrayList<>(), posList = new ArrayList<>());
    }

    private void initData() {
        pos = 0;
        visible = true;
        practiceCount = Constant.PRACTICE_COUNT_MAX;
    }

    private void initLayout() {
        binding.tvVoca.setTypeface(Typeface.createFromAsset(activity.getAssets(), Constant.FONT.KANJI_STROKE_ORDERS));
        binding.fdvVoca.setPaintColor(Color.RED);
        binding.fdvVoca.setPaintWidthDp(8);
        updateSpanText();
        updateBackgroundText();
        updateIconLeftRight();
        updatePracticeCount();
    }

    private void initListener() {
        binding.fdvVoca.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {

            @Override
            public void onUndoCountChanged(int undoCount) {
                if (undoCount == 0) {
                    binding.ivUndo.setColorFilter(BaseBindUtils.getDisableColor());
                } else {
                    binding.ivUndo.setColorFilter(BaseBindUtils.getEnableColor());
                }
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
            }
        });
        binding.fdvVoca.setOnPathDrawnListener(new PathDrawnListener() {

            @Override
            public void onPathStart() {
                activity.displayStar();
            }

            @Override
            public void onNewPathDrawn() {
            }
        });
    }

    @OnClick({R.id.ivUndo, R.id.ivEye, R.id.icRefresh, R.id.ivArrowLeft, R.id.ivArrowRight, R.id.tvHanja})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivUndo:
                binding.fdvVoca.undoLast();
                break;
            case R.id.ivEye:
                visible = !visible;
                updateSpanText();
                updateBackgroundText();
                break;
            case R.id.ivRefresh:
                binding.fdvVoca.undoAll();
                break;
            case R.id.ivArrowLeft:
                if (!isFirstWord()) {
                    pos--;
                    updateLayout();
                }
                break;
            case R.id.ivArrowRight:
                if (isLastWord()) {
                    if (--practiceCount > 0) {
                        pos = 0;
                        ToastUtil.getInstance(getContext()).show(R.string.msg_back_to_beginning);
                        updatePracticeCount();
                    } else if (activity != null) {
                        activity.finishWriting();
                    }
                } else {
                    pos++;
                }
                updateLayout();
                break;
            case R.id.tvHanja:
                if (visible) {
                    useTextInBracket();
                }
                break;
            default:
                break;
        }
    }

    private void updateLayout() {
        updateSpanText();
        updateBackgroundText();
        updateHanajText();
        updateIconLeftRight();
        binding.fdvVoca.undoAll();
    }

    private void updateSpanText() {
        String voca = vocaDisplay;
        int start = posList.get(pos);
        if (!visible) {
            voca = voca.substring(0, start) + "*" + (start < voca.length() - 1 ? voca.substring(start + 1) : "");
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(voca);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSentence.setText(spannableStringBuilder);
    }

    private void updateBackgroundText() {
        if (visible) {
            binding.tvVoca.setText(splitList.get(pos));
            binding.ivEye.setImageResource(R.drawable.ic_visibility_black_24dp);
        } else {
            binding.tvVoca.setText("");
            binding.ivEye.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }
    }

    private void updateHanajText() {
        if (hanajList != null) {
            for (String hanaj : hanajList) {
                if (hanaj.contains(splitList.get(pos))) {
                    binding.tvHanja.setText(hanaj);
                    binding.tvHanja.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
        binding.tvHanja.setText("");
        binding.tvHanja.setVisibility(View.GONE);
    }

    private void updatePracticeCount() {
        binding.tvPractice.setText(String.format(tplPraticeWriting, practiceCount));
    }

    private void useTextInBracket() {
        String hanajText = binding.tvHanja.getText().toString();
        int i = hanajText.indexOf("(");
        if (i > -1) {
            int ii = hanajText.indexOf(")");
            if (ii > i) {
                String t1 = splitList.get(pos);
                String t2 = binding.tvVoca.getText().toString();
                if (t1.equals(t2)) {
                    binding.tvVoca.setText(hanajText.substring(i + 1, ii));
                } else {
                    binding.tvVoca.setText(t1);
                }
                binding.fdvVoca.undoAll();
            }
        }
    }

    private void updateIconLeftRight() {
        if (isFirstWord()) {
            binding.ivArrowLeft.setColorFilter(BaseBindUtils.getDisableColor());
        } else {
            binding.ivArrowLeft.setColorFilter(BaseBindUtils.getEnableColor());
        }
    }

    private boolean isFirstWord() {
        return pos == 0;
    }

    private boolean isLastWord() {
        return pos == splitList.size() - 1;
    }

    private void getHanajInfo() {
        String displayLang = sharedPreferences.getMotherTongueLanguage();
        if (EnumLanguage.KOREAN.getFormatApi().equals(displayLang)) {
            int uid = sharedPreferences.getRealUid();
            if (uid > 0) {
                application.getDalAiImpl().getHanajInfoDalVoca(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        displayLang,
                        vocaDisplay,
                        new DalApiListener<String>() {

                            @Override
                            public void onSuccess(String response) {
                                hanajList = response.split("<br>");
                                updateHanajText();
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            }
        }
    }
}
