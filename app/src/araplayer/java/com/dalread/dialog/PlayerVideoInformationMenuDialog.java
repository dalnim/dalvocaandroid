package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerVideoInformationMenuBinding;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.FileUtil;
import com.dalread.util.UserUtil;

import org.jetbrains.annotations.NotNull;

public class PlayerVideoInformationMenuDialog extends BasePlayerDialog implements View.OnClickListener{
    private com.dalread.listener.OnClickListener listener;
    private DialogPlayerVideoInformationMenuBinding binding;
    private PlayerFileModel playerFileModel;
    @Override
    protected View getContentView() {
        binding = DialogPlayerVideoInformationMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerVideoInformationMenuDialog(@NonNull Context context,
                                            PlayerFileModel playerFileModel,
                                            VideoInformationModel videoInformationModel,
                                            com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.playerFileModel = playerFileModel;
        int visibilityMetadata = playerFileModel.isHasTMDB() ? View.VISIBLE : View.GONE;
        binding.llClearMetadata.setVisibility(visibilityMetadata);
        int visibilityHasRuby = playerFileModel.isHasSubRuby(context) ? View.VISIBLE : View.GONE;

        binding.llWordList.setVisibility(visibilityHasRuby);
        binding.llSubtitleList.setVisibility(visibilityHasRuby);
//        binding.llEditSubtitle.setVisibility(visibilityHasRuby);
        binding.llQuiz.setVisibility(visibilityHasRuby);

        hideMenusOnReleaseMode(context);
        hideMenuForMusicApp();
    }

    private void hideMenuForMusicApp() {
        if (FileUtil.isMusicApp()) {
            binding.llChooseMetadata.setVisibility(View.GONE);
            binding.llClearMetadata.setVisibility(View.GONE);
            binding.llEditSubtitle.setVisibility(View.GONE);
            binding.llBookmarkList.setVisibility(View.GONE);
            binding.llThumbnailList.setVisibility(View.GONE);

//            binding.llTtsTitle.setVisibility(View.VISIBLE);
//            binding.llDisplayTitle.setVisibility(View.VISIBLE);
//            binding.llTtsArtist.setVisibility(View.VISIBLE);
//            binding.llArtist.setVisibility(View.VISIBLE);
//            binding.llAlbum.setVisibility(View.VISIBLE);
        } else {
            binding.llTtsTitle.setVisibility(View.GONE);
            binding.llDisplayTitle.setVisibility(View.GONE);
            binding.llTtsArtist.setVisibility(View.GONE);
            binding.llArtist.setVisibility(View.GONE);
            binding.llAlbum.setVisibility(View.GONE);
        }
    }

    private void hideMenusOnReleaseMode(@NotNull Context context) {
        if (UserUtil.isDebugOrAdminUser(context)) {
            binding.tvBookmarkList.setVisibility(View.VISIBLE);
            binding.llEditSubtitle.setVisibility(View.VISIBLE);
        } else {
            binding.tvBookmarkList.setVisibility(View.GONE);
            binding.llEditSubtitle.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.tvChooseMetadata.setOnClickListener(this);
        binding.tvChooseMetadata.setOnClickListener(this);
        binding.tvChooseMetadata.setOnClickListener(this);
        binding.tvClearMetadata.setOnClickListener(this);
        binding.tvWordList.setOnClickListener(this);
        binding.tvSubtitleList.setOnClickListener(this);
        binding.llEditSubtitle.setOnClickListener(this);
        binding.tvBookmarkList.setOnClickListener(this);
        binding.tvQuiz.setOnClickListener(this);
        binding.tvThumbnailList.setOnClickListener(this);
        binding.tvOption.setOnClickListener(this);
        binding.llMemo.setOnClickListener(this);
        binding.llTtsTitle.setOnClickListener(this);
        binding.llDisplayTitle.setOnClickListener(this);
        binding.llTtsArtist.setOnClickListener(this);
        binding.llArtist.setOnClickListener(this);
        binding.llAlbum.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, null);
        }
    }

//    @OnClick({R.id.tvChooseMetadata, R.id.tv_clear_metadata,
//            R.id.tv_word_list, R.id.tv_subtitle_list, R.id.llEditSubtitle,
//            R.id.tv_bookmark_list, R.id.tv_quiz, R.id.tv_thumbnail_list,
//            R.id.tv_option, R.id.ll_memo, R.id.llTtsTitle,
//            R.id.llDisplayTitle, R.id.llTtsArtist, R.id.llArtist, R.id.llAlbum,
//            R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//           listener.onClick(view, null);
//        }
//
//    }
}
