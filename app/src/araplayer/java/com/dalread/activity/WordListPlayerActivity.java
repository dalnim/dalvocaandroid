package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dalread.R;
import com.dalread.adapter.WordListPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.VocaListPlayerActivity;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.WordListHeaderModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.Collections;

public class WordListPlayerActivity extends VocaListPlayerActivity implements OnAsyncTaskListener {
    public static Intent createIntent(Context context, PlayerFileModel playerFileModel) {
        Intent intent = new Intent(context, WordListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        return intent;
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void onHeaderTextRightClick() {
        showWordListPlayerDialog(true);
    }

    protected void initRecycleView() {
        super.initRecycleView();
        vocaListPlayerAdapter = new WordListPlayerAdapter(this, getSubDatabase(), sharedPreferences.getDisplayPronunciation(), onRecycleClickListener, onDoubleClickListener);
        binding.rvList.setAdapter(vocaListPlayerAdapter);
    }

    protected void generateAllData() {
        totalItems.clear();
        totalItems.addAll(getSubDatabase().getDics());
    }

    protected void initToolbar() {
        super.initToolbar();
        binding.header.setTitle(R.string.word_list_title);
    }


    @SuppressLint("StringFormatInvalid")
    protected void generateData() {
        resetData();
        if (Utils.isEmpty(totalItems))
            return;


        sortTotalItems();

        if (currentBottomNavigationId == R.id.nav_grade) {
            for (Object obj : totalItems) {
                final DicModel item = (DicModel) obj;
                allVocaIds += "," + item.getVocaId();
                allVocaTypes += "," + item.getVocaType();
                if (VocaKnow.isKnown(item)) {
                    known.add(item);
                } else if (VocaKnow.isAmkiGrade1(item)) {
                    grade1.add(item);
                } else if (VocaKnow.isAmkiGrade2(item)) {
                    grade2.add(item);
                } else if (VocaKnow.isUnknown(item)) {
                    unknown.add(item);
                } else {
                    notRated.add(item);
                }
            }

            if (!TextUtils.isEmpty(allVocaIds) && allVocaIds.startsWith(",")) {
                allVocaIds = allVocaIds.substring(1);
            }
            if (!TextUtils.isEmpty(allVocaTypes) && allVocaTypes.startsWith(",")) {
                allVocaTypes = allVocaTypes.substring(1);
            }

            grade1 = checkData(grade1);
            int indexFrom = 1;
            if (grade1.size() > 0) {
                resetVocaIndexForWordList(grade1, indexFrom);
                indexFrom += grade1.size();
                grade1.add(0, new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, getString(R.string.format_size_primary_targets, grade1.size())));
                items.addAll(grade1);
            }
            grade2 = checkData(grade2);
            if (grade2.size() > 0) {
                resetVocaIndexForWordList(grade2, indexFrom);
                indexFrom += grade2.size();
                grade2.add(0, new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, getString(R.string.format_size_secondary_targets, grade2.size())));
                items.addAll(grade2);
            }
            notRated = checkData(notRated);
            if (notRated.size() > 0) {
                resetVocaIndexForWordList(notRated, indexFrom);
                indexFrom += notRated.size();
                notRated.add(0, new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED, getString(R.string.format_size_not_determined, notRated.size())));
                items.addAll(notRated);
            }
            unknown = checkData(unknown);
            if (unknown.size() > 0) {
                resetVocaIndexForWordList(unknown, indexFrom);
                indexFrom += unknown.size();
                unknown.add(0, new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, getString(R.string.format_size_unknown_phrases, unknown.size())));
                items.addAll(unknown);
            }
            known = checkData(known);
            if (known.size() > 0) {
                resetVocaIndexForWordList(known, indexFrom);
                indexFrom += known.size();
                known.add(0, new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, getString(R.string.format_size_known_phrases, known.size())));
                items.addAll(known);
            }
        } else {
            final ArrayList<Object> tmp = checkData(totalItems);
            resetVocaIndexForWordList(tmp, 1);
            if (!Utils.isEmpty(tmp)) {
                items.add(new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_NULL, getString(R.string.format_size_words, tmp.size())));
                items.addAll(tmp);
            }
        }
    }

    private void resetVocaIndexForWordList(ArrayList<Object> items, int indexFrom) {
        for (int i = 0; i < items.size(); i++) {
            Object data = items.get(i);
            if (data instanceof DicModel) {
                DicModel dicModel = (DicModel) data;
                dicModel.setIndex(indexFrom + i);
            }
        }

    }
    private void sortTotalItems() {
        if (currentBottomNavigationId == R.id.nav_frequency) {
            Collections.sort(totalItems, (o1, o2) -> {
                final DicModel d1 = (DicModel) o1;
                final DicModel d2 = (DicModel) o2;
                return d2.getFrequency() - d1.getFrequency();
            });
        } else {
            Collections.sort(totalItems, (o1, o2) -> {
                final DicModel d1 = (DicModel) o1;
                final DicModel d2 = (DicModel) o2;
                return d1.getVocaDisplay().toLowerCase().compareTo(d2.getVocaDisplay().toLowerCase());
            });
        }
    }

    protected void handleItemClick(DicModel model) {
        DLog.d(getLogTag(), "handleItemClick - model=" + model.toString());
        openNewScreen(
                WordInfoActivity.createIntent(this, model)
        );
    }

    @Override
    protected void changeValue(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
        int nextChangeValuePos = -1;
        for (int i = 0; i < items.size(); i++) {
            Object data = items.get(i);
            if (data instanceof DicModel) {
                DicModel dicModel = (DicModel) data;
                if ((Voca.isSameVoca(iVocaBasicItem, dicModel)) && (Voca.isShow4Buttons(dicModel.getVocaKnow()))) {
                    nextChangeValuePos = i+1;
                    if (nextChangeValuePos >= items.size())
                        nextChangeValuePos = items.size() - 1;
                    break;
                }
            }
        }

        if (nextChangeValuePos >= 0) {
            int finalNextChangeValuePos = nextChangeValuePos;
            binding.rvList.post(() -> binding.rvList.scrollToPosition(finalNextChangeValuePos));
        }
        super.changeValue(iVocaBasicItem, newVocaKnow);
    }
}
