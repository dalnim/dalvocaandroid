package com.dalread.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.DalPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseMainPlayerFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.VideoModelQuery;
import com.dalread.databinding.FragmentQuizListPlayerBinding;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.StorageUtil;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class MainPlayerQuizListFragment extends BaseMainPlayerFragment implements OnAsyncTaskListener {

    @BindView(R.id.tv_max_quiz_count) TextView tvMaxQuizCount;
    @BindView(R.id.tv_quiz_type) TextView tvQuizType;
    @BindView(R.id.rvContent) RecyclerView rvContent;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;

    private int maxQuizCountIndex, quizTypeIndex;
    private String[] maxQuizCountRange, quizTypeRange;
    private SingleChoiceDialog singleChoiceDialog;

    private List<PlayerFileModel> playerFileModels = new ArrayList<>();
    private DalPlayerAdapter adapter;

    private final int TYPE_INIT_DATA = 0;
    private List<VideoModel> videoModels = new ArrayList<>();
    private EnumLanguage studyLanguage, tongueLanguage;

    private FragmentQuizListPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentQuizListPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        singleChoiceDialog = new SingleChoiceDialog(activity);
        //Todo : need to get MediaType Music or Video
        adapter = new DalPlayerAdapter(getActivity(), playerFileModels, Constant.AppMediaType.VIDEO, onItemClickListener);
        adapter.setShowIconInformation(false);
        rvContent.setAdapter(adapter);
        adapter.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        rvContent.setLayoutManager(new CenterLayoutManager(getActivity()));
        rvContent.addItemDecoration(new SeparatorDecoration(getActivity(), clDivider, dividerHeight));
    }

    @Override
    public void initData() {
        maxQuizCountIndex = sharedPreferences.getPlayerMaxQuizCount();
        quizTypeIndex = sharedPreferences.getPlayerQuizType();
        maxQuizCountRange = Constant.PLAYER.QUIZ.MAX_QUIZ_COUNT_RANGE;
        studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        tongueLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        quizTypeRange = new String[]{
                studyLanguage.getFormatApi() + " - " + tongueLanguage.getFormatApi(),
                tongueLanguage.getFormatApi() + " - " + studyLanguage.getFormatApi(),
                activity.getString(R.string.quiz_type_random)
        };
        updateUIMaxQuizCount();
        updateUIQuizType();
        getVideoModels();
        activity.callAsyncTask(this, TYPE_INIT_DATA, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @OnClick({R.id.v_max_quiz_count, R.id.v_quiz_type})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_max_quiz_count:
                showMaxQuizCountDialog();
                break;
            case R.id.v_quiz_type:
                showQuizTypeDialog();
                break;
        }
    }

    private void updateUIMaxQuizCount() {
        activity.runOnUiThread(() -> tvMaxQuizCount.setText(maxQuizCountRange[maxQuizCountIndex]));
    }

    private void updateUIQuizType() {
        activity.runOnUiThread(() -> tvQuizType.setText(quizTypeRange[quizTypeIndex]));
    }

    private OnClickListener onItemClickListener = (view, object) -> {
        final PlayerFileModel playerFileModel = (PlayerFileModel) object;
        if (view.getId() == R.id.izbVideoThumbnail) {
            ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(activity, playerFileModel);
            zoomedPhotoDialog.loadThumbnailFromFileAndShow(playerFileModel, ((ImageView) view).getDrawable());
        } else {

            if (StorageUtil.isSubDatabaseFileExist(playerFileModel.getPath())) {
                activity.openPracticeConversationActivityWithLocalData(playerFileModel);
//                activity.openQuizPlayerScreenWithLocalData(playerFileModel);
            } else {
                activity.openQuizPlayerScreenWithServerData();
            }
        }
    };

    private void showMaxQuizCountDialog() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose,
                maxQuizCountRange,
                maxQuizCountIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != maxQuizCountIndex) {
                            sharedPreferences.setPlayerMaxQuizCount(maxQuizCountIndex = which);
                            updateUIMaxQuizCount();
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }


    private void showQuizTypeDialog() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose,
                quizTypeRange,
                quizTypeIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != quizTypeIndex) {
                            sharedPreferences.setPlayerQuizType(quizTypeIndex = which);
                            updateUIQuizType();
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    @Override
    public void onInitAsyncTask() {
        Loading.show(activity);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        if (searchType == TYPE_INIT_DATA) {
            return getAllVideoFile();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        if (searchType == TYPE_INIT_DATA) {
            finishGetAllVideoFile(resultData);
        }
        Loading.hide();
    }

    private List<PlayerFileModel> getAllVideoFile() {
        List<PlayerFileModel> files = new ArrayList<>();
        for (VideoModel v : videoModels) {
//            if (!Utils.isEmpty(v.getSubPath1())
//                    && StorageUtil.checkSubDatabaseFile(activity, v.getSubPath1())) {
//            if (StorageUtil.isSubDatabaseFileExist(v.getPath())) {
            if (v.getVocaKnowAll() > 0) {
                files.add(new PlayerFileModel(v));
            }
        }
        return StorageUtil.sortFiles(files);
    }

    private void finishGetAllVideoFile(Object data) {
        playerFileModels.clear();
        //Don't delete this code. We will use this menu later "암기 대상으로 퀴즈 만들기 From difficult words"
//        playerFileModels.add(new PlayerFileModel(activity.getString(R.string.from_difficult_words), PlayerFileModel.DirectoryType.NONE));
        playerFileModels.addAll((Collection<? extends PlayerFileModel>) data);
        adapter.setData(playerFileModels);
    }

//    private void showQuizSourceDialog(Object data) {
//        final PlayerFileModel item = (PlayerFileModel) data;
//        if (Utils.isEmpty(item.getSubPath())) {
//            activity.openQuizPlayerScreen(item);
//            return;
//        }
//        final PlayerShowQuizSourceDialog dialog = new PlayerShowQuizSourceDialog(activity, (view, object) -> {
//            switch (view.getId()) {
//                case R.id.tv_solve_a_quiz:
//                    activity.openQuizPlayerScreen(item);
//                    break;
//                case R.id.tv_word_list:
//                    openWordList(item);
//                    break;
//                case R.id.tv_subtitle_list:
//                    openDialogList(item);
//                    break;
//            }
//        });
//        dialog.show();
//    }

    private void getVideoModels() {
        videoModels.clear();
        List<VideoModel> tmp = VideoModelQuery.getAllByQuiz(Voca.getRealm());
        if (tmp != null) {
            videoModels.addAll(tmp);
        }
    }

    private void openWordList(PlayerFileModel item) {
        Intent intent = new Intent(activity, WordListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, item);
        startActivity(intent);
    }
    private void openDialogList(PlayerFileModel item) {
        Intent intent = new Intent(activity, DialogueListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, item);
        startActivity(intent);
    }
}
