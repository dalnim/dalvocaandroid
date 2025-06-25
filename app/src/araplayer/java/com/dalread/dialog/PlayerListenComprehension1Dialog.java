package com.dalread.dialog;

import android.content.DialogInterface;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.adapter.ListenComprehensionAdapter;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.BasePlayerDialog;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.ListenComprehensionQuery;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.DialogPlayerListenComprehension1Binding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.ListenComprehensionModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Collections;
import java.util.List;

public class PlayerListenComprehension1Dialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private BasePlayerActivity activity;
    private OnClickDialogListener listener;
    private DicModel dicModel;
    private ListenComprehensionAdapter adapter;
    private List<ListenComprehensionModel> list;
    private SingleChoiceDialog singleChoiceDialog;
    private int playSubtitlesAtOnce;
    private int playSubtitlesAtOnceIndex;
    private int playSubtitlesPlayParts;
    private int playSubtitlesPlayPartsIndex;
    private boolean isSkipPlayingNoSubtitlePart;
    private boolean isPlayOnlyDialogsInListenComprehension;
    private final String[] readCountValuesAtOnce = Voca.getPlaySubtitlesAtOnceListenComprehensionValues();
    private final String[] readCountValuesPlayParts = Voca.getPlaySubtitlesPlayPartsListenComprehensionValues();

    protected BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;

    private DialogPlayerListenComprehension1Binding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerListenComprehension1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerListenComprehension1Dialog(BasePlayerActivity activity, DicModel dicModel, OnClickDialogListener listener) {
        super(activity);
        setDialogSizeWider(true);
        this.activity = activity;
        this.dicModel = dicModel;
        this.listener = listener;
//        setOnDismissListener(this);

        singleChoiceDialog = new SingleChoiceDialog(activity);

        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();

        initView();
        setIsSkipPlayingNoSubtitlePart(false);
    }

    public void initView() {
//        adapter = new ListenComprehensionAdapter(this, binding.rvList, onItemClickListener);
        adapter = new ListenComprehensionAdapter(activity, binding.rvList, (view, object) -> {
//            showRepeatCountDialog((ListenComprehensionModel) object);
            deletePlayingType((ListenComprehensionModel) object);
        });
        binding.rvList.setOnItemMoveListener(onItemMoveListener);
        binding.rvList.setAdapter(adapter);
        binding.rvList.setLayoutManager(new CenterLayoutManager(activity));
//        binding.rvList.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
        binding.rvList.addItemDecoration(new SeparatorDecoration(activity, ContextCompat.getColor(getContext(), R.color.colorLine), BaseBindUtils.getDividerHeightRes()));
        binding.rvList.setItemViewSwipeEnabled(false);
        singleChoiceDialog = new SingleChoiceDialog(activity);
//        playSubtitlesAtOnce = sharedPreferences.getPlayerListenComprehensionPlaySubtitlesAtOnce();
        playSubtitlesAtOnceIndex = sharedPreferences.getPlayerListenComprehensionPlaySubtitlesAtOnceIndex();
        playSubtitlesAtOnce = Utils.parseInt(readCountValuesAtOnce[playSubtitlesAtOnceIndex]);
        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesAtOnce(playSubtitlesAtOnce);

        playSubtitlesPlayPartsIndex = sharedPreferences.getPlayerListenComprehensionPlaySubtitlesPlayPartsIndex();
        playSubtitlesPlayParts = Utils.parseInt(readCountValuesPlayParts[playSubtitlesPlayPartsIndex]);
        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesPlayParts(playSubtitlesPlayParts);

        isPlayOnlyDialogsInListenComprehension = sharedPreferences.getPlayOnlyDialogsInListenComprehension();
//        sc_play_only_dialogs.setChecked(isPlayOnlyDialogsInListenComprehension);
        updatePlaySubtitlesAtOnce();
        updatePlaySubtitlesPlayParts();
        initData();
    }

    public void initData() {
        Loading.show(activity);
        list = ListenComprehensionQuery.getAll(Voca.getRealm());
        adapter.notifyDataSetChanged(list);
        Loading.hide();
    }

    @Override
    protected void initOnClickListener() {
        binding.llPlaySubtitlesAtOnce.setOnClickListener(this);
        binding.llPlaySubtitlesPlayParts.setOnClickListener(this);
        binding.llPlayingTypeHideTheSubtitle.setOnClickListener(this);
        binding.scSkipPlayingNoSubtitlePart.setOnClickListener(this);
        binding.llPlayingTypeShowTheSubtitle.setOnClickListener(this);
        binding.llPlayingTypeShowDifficultWordsOnly.setOnClickListener(this);
        binding.tvOK.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_subtitles_at_once:
                showPlaySubtitlesAtOnceDialog();
                break;
            case R.id.ll_play_subtitles_play_parts:
                showPlaySubtitlesPlayPartsDialog();
                break;
            case R.id.scSkipPlayingNoSubtitlePart:
                isSkipPlayingNoSubtitlePart = !isSkipPlayingNoSubtitlePart;
                listener.onClick(v, isSkipPlayingNoSubtitlePart);
                break;
            case R.id.ll_playing_type_hide_the_subtitle:
                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE);
                break;
            case R.id.ll_playing_type_show_the_subtitle:
                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_THE_SUBTITLE);
                break;
            case R.id.ll_playing_type_show_difficult_words_only:
                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_DIFFICULT_WORDS_ONLY);
                break;
            case R.id.tvOK:
            case R.id.tvCancel:
                if (listener != null) {
                    listener.onClick(v, dicModel);
                }
                dismiss();
                break;
        }
    }


//    @OnClick({
//            R.id.ll_play_subtitles_at_once, R.id.ll_play_subtitles_play_parts, R.id.ll_playing_type_hide_the_subtitle, R.id.scSkipPlayingNoSubtitlePart,
//            R.id.ll_playing_type_show_the_subtitle, R.id.ll_playing_type_show_difficult_words_only, R.id.tvOK, R.id.tvCancel
//    })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ll_play_subtitles_at_once:
//                showPlaySubtitlesAtOnceDialog();
//                break;
//            case R.id.ll_play_subtitles_play_parts:
//                showPlaySubtitlesPlayPartsDialog();
//                break;
//            case R.id.scSkipPlayingNoSubtitlePart:
//                isSkipPlayingNoSubtitlePart = !isSkipPlayingNoSubtitlePart;
//                listener.onClick(view, isSkipPlayingNoSubtitlePart);
//                break;
//            case R.id.ll_playing_type_hide_the_subtitle:
//                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE);
//                break;
//            case R.id.ll_playing_type_show_the_subtitle:
//                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_THE_SUBTITLE);
//                break;
//            case R.id.ll_playing_type_show_difficult_words_only:
//                addPlayingType(Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_DIFFICULT_WORDS_ONLY);
//                break;
//            case R.id.tvOK:
//            case R.id.tvCancel:
//                if (listener != null) {
//                    listener.onClick(view, dicModel);
//                }
//                dismiss();
//                break;
//        }
//    }

//    private OnClickListener onItemClickListener = (view, object) -> {
//        showRepeatCountDialog((ListenComprehensionModel) object);
//    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            if (viewHolder.getItemViewType() != viewHolder1.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = viewHolder.getAdapterPosition() - binding.rvList.getHeaderCount();
            int toPosition = viewHolder1.getAdapterPosition() - binding.rvList.getHeaderCount();

            swapPositionData(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        }
    };

    private void swapPositionData(int fromPosition, int toPosition) {
        //Don't want to swap first item.
        if ((fromPosition == 0) || (toPosition == 0))
            return;

        activity.runOnUiThread(() -> {
            Loading.show(activity);
            Collections.swap(list, fromPosition, toPosition);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setIndex(i);
                ListenComprehensionQuery.update(Voca.getRealm(), list.get(i));
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            Loading.hide();
        });
    }

    //Don't use this dialog any more But don't delete the code
    private void showRepeatCountDialog(ListenComprehensionModel item) {
        final String[] readCountValues = Voca.getRepeatCountListenComprehensionValues();
        singleChoiceDialog.show(
                R.string.repeat_count,
                readCountValues,
                item.getCount(),
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        if (which == item.getCount() && which > 0) return;
                        if (which <= 0) {
                            list.remove(item);
                            ListenComprehensionQuery.deleteById(Voca.getRealm(), item.getId());
                        } else {
                            item.setCount(which);
                            ListenComprehensionQuery.update(Voca.getRealm(), item);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void deletePlayingType(ListenComprehensionModel item) {
        list.remove(item);
        ListenComprehensionQuery.deleteById(Voca.getRealm(), item.getId());
        adapter.notifyDataSetChanged();
    }

    private void addPlayingType(int type) {
        final ListenComprehensionModel item = new ListenComprehensionModel();
        item.setType(type);
        item.setIndex(ListenComprehensionQuery.createIndex(Voca.getRealm()));
        ListenComprehensionQuery.add(Voca.getRealm(), item);
        initData();
    }

    private void showPlaySubtitlesAtOnceDialog() {
        singleChoiceDialog.show(
                R.string.play_subtitles_at_once,
                readCountValuesAtOnce,
                playSubtitlesAtOnceIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        playSubtitlesAtOnceIndex = which;
                        playSubtitlesAtOnce = Utils.parseInt(readCountValuesAtOnce[which]);

                        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesAtOnce(playSubtitlesAtOnce);
                        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesAtOnceIndex(playSubtitlesAtOnceIndex);
                        updatePlaySubtitlesAtOnce();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void showPlaySubtitlesPlayPartsDialog() {
        singleChoiceDialog.show(
                R.string.play_subtitles_play_parts,
                readCountValuesPlayParts,
                playSubtitlesPlayPartsIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        playSubtitlesPlayPartsIndex = which;
                        playSubtitlesPlayParts = Utils.parseInt(readCountValuesPlayParts[which]);

                        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesPlayParts(playSubtitlesPlayParts);
                        sharedPreferences.setPlayerListenComprehensionPlaySubtitlesPlayPartsIndex(playSubtitlesPlayPartsIndex);
                        updatePlaySubtitlesPlayParts();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void updatePlaySubtitlesAtOnce() {
        activity.runOnUiThread(() -> {
            binding.tvPlaySubtitlesAtOnce.setText(String.valueOf(playSubtitlesAtOnce));
        });
    }

    private void updatePlaySubtitlesPlayParts() {
        activity.runOnUiThread(() -> {
            binding.tvPlaySubtitlesPlayParts.setText(String.valueOf(playSubtitlesPlayParts));
        });
    }
    public void setIsSkipPlayingNoSubtitlePart(boolean isSkipPlayingNoSubtitlePart) {
        this.isSkipPlayingNoSubtitlePart = isSkipPlayingNoSubtitlePart;
        binding.scSkipPlayingNoSubtitlePart.setChecked(isSkipPlayingNoSubtitlePart);
    }
    @Override
    public void onDismiss(DialogInterface dialogInterface) {
//        if (listener != null) {
//            listener.onDismiss(view, dicModel);
//        }
    }
}
