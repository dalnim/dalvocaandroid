package com.dalread.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.EnumFlavor;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumUserType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.DicSentenceSubDatabase;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnVoiceFileInfoDownloadListener;
import com.dalread.model.AmkiGradeHeader;
import com.dalread.model.AmkiItem;
import com.dalread.model.BackupRecording;
import com.dalread.model.GRAMMAR;
import com.dalread.model.Lesson;
import com.dalread.model.READING;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.TBL_MESSAGE;
import com.dalread.model.User;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaDetailInfo;
import com.dalread.model.VocaDownload;
import com.dalread.model.VocaFeedback;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.model.VocaMemorize;
import com.dalread.model.VocaPractice;
import com.dalread.model.VocaStudy;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyHistory;

import net.crizin.KoreanRomanizer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;

public abstract class BaseVoca {

    public static void executeRealmTransaction(Realm.Transaction transaction) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void executeRealmTransactionAsync(Realm.Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();
        Realm.getDefaultInstance().executeTransactionAsync(transaction, realm::close, error -> realm.close());
    }

    public static void executeRealmTransactionAsync(Realm.Transaction transaction, Realm.Transaction.OnSuccess onSuccessListener, Realm.Transaction.OnError onErrorListener) {
        Realm realm = Realm.getDefaultInstance();
        Realm.getDefaultInstance().executeTransactionAsync(
                transaction,
                () -> {
                    realm.close();
                    if (onSuccessListener != null) {
                        onSuccessListener.onSuccess();
                    }
                },
                error -> {
                    realm.close();
                    if (onErrorListener != null) {
                        onErrorListener.onError(error);
                    }
                }
        );
    }

    public static Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public static String getMeaningTTS(IVocaFullPlayTTSItem item, Context context) {
        return TextUtils.isEmpty(item.getVIMeaningTts(LanguageUtil.getMotherTongueLanguage(context))) ? item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) : item.getVIMeaningTts(LanguageUtil.getMotherTongueLanguage(context));
    }

    public static String getVocaTTS(IVocaFullPlayTTSItem item) {
        return TextUtils.isEmpty(item.getVIVocaTTS()) ? item.getVIVoca() : item.getVIVocaTTS();
    }

    public static String getOutputRecordingFileName(int studyLangId, int vocaType, int vocaId, int uid) {
        return studyLangId + "_" + vocaType + "_" + vocaId + "_" + uid + "." + Constant.FILE.EXTENTION_SPEAKING;
    }

    public static String getMyOutputRecordingFileName(Context context, IVocaCoreItem voca) {
//        int studyLang = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage()).getIdApi();
        return getOutputRecordingFileName(LanguageUtil.getStudyLanguageCode(context), voca.getVIVocaType(), voca.getVIVocaId(), UserUtil.getUserID(context));
    }

    public static VocaDownload getLocalVoiceFileInfo(String fileName) {
        VocaDownload vocaDownload = null;
        List<VocaDownload> vocaDownloadList = new ArrayList<>();
        BaseVoca.executeRealmTransaction(realm -> {
            vocaDownloadList.addAll((ArrayList<VocaDownload>)realm.copyFromRealm(realm.where(VocaDownload.class).equalTo("name", fileName).findAll()));
        });
        if (!Utils.isEmpty(vocaDownloadList)) {
            vocaDownload = vocaDownloadList.get(0);
        }
        return vocaDownload;
    }

    public static File getOutputFolderInApp(Context context, String type) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        String appPath = BaseStorageUtil.getAppPath(context);
        String folderPath = appPath + File.separator + Constant.FOLDER_IN_APP.ROOT + File.separator + type;
        if (TextUtils.isEmpty(type)) {
            folderPath = appPath + File.separator + Constant.FOLDER_IN_APP.ROOT + File.separator;
        }
        File mediaStorageDir = new File(folderPath);

//        String folderPath = Constant.FOLDER_ARAONE.APP_ROOT + File.separator + type;
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), folderPath);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return null;
        }
        return mediaStorageDir;
    }

    public static File getVoiceFileInApp(Context context, String fileName) {
        return new File(getVoiceFolderInApp(context), BaseFileUtil.getVoiceSubFolderPath(fileName) + fileName);
//        return getOutputFolderInApp(context, Constant.FOLDER_ARAONE.VOICE);
    }

    public static File getVoiceFolderInApp(Context context) {
        return getOutputFolderInApp(context, Constant.FILE.FOLDER_VOICE);
//        return getOutputFolderInApp(context, Constant.FOLDER_ARAONE.VOICE);
    }

    public static File getOutputFolderOnLocal(Context context, String type) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        if (TextUtils.isEmpty(type)) {
            type = Constant.FOLDER_ARAONE.ROOT;
        } else {
            type = Constant.FOLDER_ARAONE.ROOT + File.separator + type;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), type);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return null;
        }

        return mediaStorageDir;
    }

    public static File getTempFolderOnLocal(Context context) {
        return getOutputFolderOnLocal(context, Constant.FILE.FOLDER_TEMP);
    }

    public static File getSoundFolderOnLocal(Context context) {
        return getOutputFolderOnLocal(context, Constant.FILE.FOLDER_SOUND);
    }


    /*
     * @deprecated Replaced by {@link #getVoiceFolderInApp(Context context)}
     * 근데 getVoiceFolderInApp이걸 쓰면 경로가 다르게 나온다.
     */
    @Deprecated
    public static File getVoiceFolderOnLocal(Context context) {
        return getOutputFolderOnLocal(context, Constant.FILE.FOLDER_VOICE);
    }

    public static File getVoicePracticeFolderOnLocal(Context context) {
        return getOutputFolderOnLocal(context, Constant.FILE.FOLDER_PRACTICE_SPEAKING);
    }

//    public static String getVoiceSubFolderPath(String fileName) {
//        try {
//            String[] split = fileName
//                    .split("\\.")[0]
//                    .split("_");
//            return split[0] + "/" + split[3] + "/";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public static String getVoiceFolderPathOnFirebase(String fileName) {
        return Constant.FILE.FOLDER_VOICE + File.separator + BaseFileUtil.getVoiceSubFolderPath(fileName);
    }

    public static String getPracticeSpeakingFolderPathOnFirebase(String fileName) {
        return Constant.FILE.FOLDER_PRACTICE_SPEAKING + File.separator + BaseFileUtil.getVoiceSubFolderPath(fileName);
    }


    /*
     * @deprecated Replaced by {@link #getVoiceFileInApp(Context context, String fileName)}
     */
    @Deprecated
    public static File getVoiceFileOnLocal(Context context, String fileName) {
        return getVoiceFileOnLocal(getVoiceFolderOnLocal(context), fileName);
    }

    //result /storage/emulated/0/Android/data/com.dalnimsoft.araconv/files/araone/voice/13/1510/13_2_266_1510.m4a
    /*
     * @deprecated Replaced by {@link #getVoiceFileInApp(Context context, String fileName)}
     */
    @Deprecated
    public static File getVoiceFileOnLocal(File voiceFolder, String fileName) {
        if (voiceFolder != null) {
            File folder = new File(voiceFolder, BaseFileUtil.getVoiceSubFolderPath(fileName));// (voiceFolder.getPath() + File.separator + getVoiceSubFolderPath(fileName));
            if (folder.exists() || folder.mkdirs()) {
                return new File(folder, fileName);
            }
        }
        return null;
    }

//    public static File getVoiceFileOnLocal(Context context, PlayerFileModel playerFileModel, File voiceFolder, String fileName, int vocaId) {
//        if (voiceFolder != null) {
//            String folderPath;
//            if (vocaId >= 0) {
//                folderPath = voiceFolder.getPath() + File.separator + getVoiceSubFolderPath(fileName);
//            } else {
//                String videoPath;
//                if (playerFileModel.isLocal()) {
//                    videoPath = BaseStorageUtil.getLocalPath();
//                } else {
//                    videoPath = BaseStorageUtil.getNetworkPath(playerFileModel);
//                }
//                videoPath += playerFileModel.getPath();
//                folderPath = BaseStorageUtil.getFolderZip(context) + videoPath + File.separator + Constant.FILE.FOLDER_VOICE;
//            }
//            File folder = new File(folderPath);
//            if (folder.exists() || folder.mkdirs()) {
//                return new File(folder, fileName);
//            }
//        }
//        return null;
//    }

    public static String getIntroductionFilePathOnFirebase(int uid) {
        return Constant.FILE.FOLDER_INTRODUCTION + "/" + Constant.FILE.PREFIX_INTRODUCTION + uid + "." + Constant.FILE.EXTENTION_SPEAKING;
    }

    public static File getIntroductionFileOnLocal(Context context, int uid) {
        File folder = getOutputFolderOnLocal(context, Constant.FILE.FOLDER_INTRODUCTION);
        if (folder != null) {
            if (folder.exists() || folder.mkdirs()) {
                String fileName = Constant.FILE.PREFIX_INTRODUCTION + uid + "." + Constant.FILE.EXTENTION_SPEAKING;
                return new File(folder, fileName);
            }
        }
        return null;
    }

    public static void copyFile(File src, File dst) throws IOException {
        if (src == null || dst == null) return;
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    /*
     * @deprecated Replaced by {@link #getTTSSpeedInSetting()()} in AbstractStudyLang class
     */
    @Deprecated
    public static int getTTSSpeed(EnumLanguage studyLanguage, SharedPreferencesDB sharedPreferences) {
        if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
            return sharedPreferences.getSettingTTSSpeedChinese();
        } else if (studyLanguage == EnumLanguage.ENGLISH) {
            return sharedPreferences.getSettingTTSSpeedEnglish();
        } else if (studyLanguage == EnumLanguage.JAPANESE) {
            return sharedPreferences.getSettingTTSSpeedJapanese();
        } else if (studyLanguage == EnumLanguage.KOREAN) {
            return sharedPreferences.getSettingTTSSpeedKorean();
        } else if (studyLanguage == EnumLanguage.HANJA) {
            return sharedPreferences.getSettingTTSSpeedHanja();
        }
        return 0;
    }

    public static MediaRecorder setupMediaRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Don't use HE_AAC, it records metal grinding noise too.
        recorder.setAudioEncodingBitRate(48000); //Same as iOS
        recorder.setAudioSamplingRate(16000); //Same as iOS
        return recorder;
    }

    public static void deleteAllRealmData() {
        executeRealmTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(VocaStudy.class);
                realm.delete(VocaFeedback.class);
                realm.delete(VocaMemorize.class);
                realm.delete(VocaStudyHistory.class);
            }
        });
    }

    public static boolean checkStudyLanguageJPCN(SharedPreferencesDB mSharedPref) {
        final String studyLang = mSharedPref.getStudyLanguage();
        DLog.e("checkStudyLanguageJPCN", "studyLang=" + studyLang);
        return studyLang.equalsIgnoreCase(EnumLanguage.JAPANESE.getFormatApi()) ||
                studyLang.equalsIgnoreCase(EnumLanguage.CHINESE_SIMPLIFIED.getFormatApi()) ||
                studyLang.equalsIgnoreCase(EnumLanguage.CHINESE_TRADITIONAL.getFormatApi());
    }

    public static boolean checkStudyLanguageJPCN(String studyLang) {
        if (TextUtils.isEmpty(studyLang)) return false;
        return studyLang.equalsIgnoreCase(String.valueOf(EnumLanguage.JAPANESE.getIdApi())) ||
                studyLang.equalsIgnoreCase(String.valueOf(EnumLanguage.CHINESE_SIMPLIFIED.getIdApi())) ||
                studyLang.equalsIgnoreCase(String.valueOf(EnumLanguage.CHINESE_TRADITIONAL.getIdApi()));
    }

    public static boolean checkStudyLanguageJPCN(EnumLanguage studyLang) {
        return studyLang == EnumLanguage.JAPANESE ||
                studyLang == EnumLanguage.CHINESE_SIMPLIFIED ||
                studyLang == EnumLanguage.CHINESE_TRADITIONAL;
    }

    public static String insert(String str, int index, String value) {
        return str.substring(0, index) + value + str.substring(index);
    }

    public static String[] getRepeatCountValues(int min, int max) {
        int count = max - min + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + min);
        }
        return result;
    }

    public static @DrawableRes int getAvatarResource(int sex, int age) {
        if (sex == Constant.SEX.MALE)
            return age == Constant.AGE.TEENAGER ? R.drawable.ic_avatar_teenager_male : R.drawable.ic_avatar_adult_male;
        return age == Constant.AGE.TEENAGER ? R.drawable.ic_avatar_teenager_female : R.drawable.ic_avatar_adult_female;
    }
    public static @DrawableRes int getAvatarResource(int sex) {
        return sex == Constant.SEX.MALE ? R.drawable.ic_avatar_male_2 : R.drawable.ic_avatar_female_2;
    }

    public static String getAppVersion() {
        String appVersion = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            int i = appVersion.lastIndexOf(".");
            appVersion = appVersion.substring(0, i);
        }
        return appVersion;
    }

    public static String getVocaDisplay(IVocaFullPlayTTSItem item) {
        String content = item.getVIVoca();
        if (Utils.isEmpty(content)) {
            content = Constant.BASE_BLANK;
        }
        int startIndex = content.indexOf(Constant.RUBY.CHARACTER_U2028);
        if (startIndex > 0) {
            content = insert(content, startIndex, Constant.RUBY.BREAK_CHARACTER);
            content = content.replaceAll(Constant.RUBY.CHARACTER_U2028, Constant.BASE_BLANK);
        }
        return content;
    }

    public static void setNormalTextView(TextView tv) {
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);
        tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    public static void setItalicAndStrikeThroughTextView(TextView tv) {
        tv.setTypeface(tv.getTypeface(), Typeface.ITALIC);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void blinkView(View view) {
        view.setBackgroundResource(R.color.color_blink);
        view.postDelayed(() -> view.setBackground(null), Constant.STOP_BLINK_DELAY_TIME);
    }

    public static String getUserDisplayName(SharedPreferencesDB sharedPreferences, User user) {
        String name = (sharedPreferences.getUserType() == EnumUserType.SYSTEM_MANAGER.getType() || user.isShareMyRecording()) ? user.getName() : "";
        if (!TextUtils.isEmpty(user.getOponentNameByMe())) {
            name = "(" + user.getOponentNameByMe() + ") " + name;
        }
        return name;
    }

    public static void updateMicSpeakerVisibility(IVocaFullPlayTTSItem isRecordingVoca, IVocaFullPlayTTSItem voca, ImageView ivSpeaker, ImageView ivMic, boolean isHideSpeakerIfDontHaveMyRecored) {
        if (isRecordingVoca == null) {
            ivSpeaker.setVisibility(View.VISIBLE);
            ivMic.setVisibility(View.VISIBLE);
            if (voca.hasVIVoiceFile()) {
                BaseVoca.updateIconSpeaker(ivSpeaker, voca);
            } else {
                if (isHideSpeakerIfDontHaveMyRecored)
                    ivSpeaker.setVisibility(View.INVISIBLE);
            }
            BaseVoca.updateIconMic(ivMic, voca);
        } else {
            ivSpeaker.setVisibility(View.INVISIBLE);
            if (BaseVoca.isSameVoca(voca, isRecordingVoca)) {
                ivMic.setVisibility(View.VISIBLE);
                BaseVoca.updateIconMic(ivMic, voca);
            } else {
                ivMic.setVisibility(View.INVISIBLE);
            }
        }
    }
    public static void updateIconSpeaker(ImageView iv, IVocaFullPlayTTSItem voca) {
        if (iv == null)
            return;
        if (voca.isVIPlaying()) {
            iv.setImageResource(R.drawable.ic_volume_up_outline_48dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_play));
        } else if (voca.hasVIVoiceFile()) {
            iv.setImageResource(R.drawable.ic_volume_up_outline_48dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_speaker_has_record_file));
        } else {
            iv.setImageResource(R.drawable.ic_volume_up_outline_48dp);
//            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_stop));
            iv.setColorFilter(null);
//            updateSpeakerIconStop(iv);
        }
    }
    public static void updateIconSpeaker2(ImageView iv, IVocaFullPlayTTSItem voca) {
        if (iv == null)
            return;
        if (voca.isVIPlaying()) {
            iv.setImageResource(R.drawable.ic_speaker_2);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_play));
        } else if (voca.hasVIVoiceFile()) {
            iv.setImageResource(R.drawable.ic_speaker_2);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_speaker_has_record_file));
        } else {
            iv.setImageResource(R.drawable.ic_speaker_2);
            iv.setColorFilter(null);
        }
    }

    /*
     * @deprecated Replaced by {@link #updateIconSpeaker(ImageView iv, IVocaFullPlayTTSItem voca)}
     */
    @Deprecated
    public static void updateIconSpeaker(ImageView iv, boolean isSpeaking) {
        if (iv == null)
            return;
        if (isSpeaking) {
            iv.setImageResource(R.drawable.ic_volume_up_48dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_play));
        } else {
            iv.setImageResource(R.drawable.ic_volume_up_48dp);
            iv.setColorFilter(null);
        }
    }

    public static void updateIconMic(ImageView iv, IVocaFullPlayTTSItem voca) {
        if (iv == null)
            return;
        if (voca.isVIRecording()) {
            iv.setImageResource(R.drawable.ic_mic_black_24dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_mic_recording));
        } else if (voca.hasVIVoiceFile()){
            iv.setImageResource(R.drawable.ic_mic_black_24dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_mic_has_record_file));
        } else {
            iv.setImageResource(R.drawable.ic_mic_none_black_24dp);
        }
    }

    /*
     * @deprecated Replaced by {@link #updateIconMic(ImageView iv, IVocaFullPlayTTSItem voca)}
     */
    @Deprecated
    public static void updateIconMic(ImageView iv, boolean isRecording) {
        if (iv == null)
            return;
        if (isRecording) {
            iv.setImageResource(R.drawable.ic_mic_black_24dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_play));
        } else {
            iv.setImageResource(R.drawable.ic_mic_none_black_24dp);
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_black_speaker_icon));
        }
    }

    public static void updateSpeakerIconStop(ImageView iv) {
        iv.setImageResource(R.drawable.ic_volume_up_outline_48dp);
        iv.setColorFilter(null);
    }

    public static void updateIconPlayBackupRecording(ImageView iv, BackupRecording backupRecording) {
        iv.setImageResource(R.drawable.ic_volume_up_48dp);
        if (backupRecording.isPlaying()) {
            iv.setColorFilter(ContextCompat.getColor(iv.getContext(), R.color.color_play));
        } else {
            iv.setColorFilter(null);
        }
    }

    public static void updateTextViewEvaluateGradeStyle(ImageView iv, TextView tv, String evaluateGrade, String evaluateGradeTutors) {
        String grade = TextUtils.isEmpty(evaluateGrade) ? evaluateGradeTutors : evaluateGrade;
        switch (grade) {
            case Constant.EVALUATE.GRADE_A:
                iv.setImageResource(R.drawable.ic_tutor_eval_a);
                break;
            case Constant.EVALUATE.GRADE_B:
                iv.setImageResource(R.drawable.ic_tutor_eval_b);
                break;
            case Constant.EVALUATE.GRADE_C:
                iv.setImageResource(R.drawable.ic_tutor_eval_c);
                break;
            default:
                iv.setImageDrawable(null);
                break;
        }
        iv.setBackgroundResource(R.drawable.btn_no_border_grey1_background);
        iv.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
    }

//    public static void updateTextViewStyleByVocaKnow(TextView tv, int vocaKnow) {
//        if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_KNOWN);
//            tv.setBackgroundResource(R.drawable.bg_circle_light_blue);
//        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_A);
//            tv.setBackgroundResource(R.drawable.bg_circle_orange);
//        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_B);
//            tv.setBackgroundResource(R.drawable.bg_circle_orange);
//        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_UNKNOWN);
//            tv.setBackgroundResource(R.drawable.bg_circle_grey);
//        } else {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_NOT_RATED);
//            tv.setBackgroundResource(R.drawable.bg_circle_border);
//        }
//    }

//    public static void updateTextViewStyleByVocaKnow(ImageView iv, TextView tv, int vocaKnow) {
//        updateTextViewStyleByVocaKnow(tv, vocaKnow);
//        tv.setVisibility(View.VISIBLE);
//        iv.setVisibility(View.GONE);
//    }

    public static String changeVocaWithSmartAsterisk(String input, int vocaKnow) {
        String output;
        if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            output = changeVocaWithAllAsteriskExceptSomeFirstChars(input, 1);
        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
            output = changeVocaWithAllAsteriskExceptSomeFirstChars(input, 2);
        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
            output = changeVocaExceptSomeLastCharsWithAsterisk(input, 2);
        } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
            output = changeVocaExceptSomeLastCharsWithAsterisk(input, 1);
        } else {
            // if it's "?" replace all characters *** to force him to choose ! X 1 2...
            output = changeVocaWithAllAsteriskExceptSomeFirstChars(input, 0);
        }
        return output;
    }

    public static String changeVocaWithAllAsteriskExceptSomeFirstChars(String input, int some) {
        StringBuilder output = new StringBuilder();
        if (!TextUtils.isEmpty(input)) {
            Pattern pattern = Pattern.compile(Constant.PATTERN_SPECIAL_CHARACTERS);
            String[] splitInput = input.split(" ");
            if (splitInput.length > 0) {
                for (String aSplitInput : splitInput) {

//                        output.append(aSplitInput.substring(0, some));
                    int countOfPattern = 0;
                    for (int i = 0; i < aSplitInput.length(); i++) {
                        String aChar = aSplitInput.substring(i, i + 1);
                        if (pattern.matcher(aChar).matches()) {
                            countOfPattern++;
                            output.append(aChar);
                        } else {
                            if ((i - countOfPattern) >= some) {
                                output.append("*");
                            } else {
                                output.append(aChar);
                            }
                        }
                    }

                    output.append(" ");

//                    if (aSplitInput.length() > some) {
//                        output.append(aSplitInput.substring(0, some));
//                        for (int i = some; i < aSplitInput.length(); i++) {
//                            String aChar = aSplitInput.substring(i, i + 1);
//                            if (pattern.matcher(aChar).matches()) {
//                                output.append(aChar);
//                            } else {
//                                output.append("*");
//                            }
//                        }
//                    } else {
//                        output.append(aSplitInput);
//                    }
//                    output.append(" ");
                }
            }
        }
        return output.toString().trim();
    }

    public static String changeVocaExceptSomeLastCharsWithAsterisk(String input, int some) {
        StringBuilder output = new StringBuilder();
        if (!TextUtils.isEmpty(input)) {
            int minLetterToShow = 3;  //don't replace to * if character's count is less than this number.
            Pattern pattern = Pattern.compile(Constant.PATTERN_SPECIAL_CHARACTERS);
            String[] splitInput = input.split(" ");
            if (splitInput.length > 0) {
                for (String aSplitInput : splitInput) {
                    if (aSplitInput.length() <= minLetterToShow) {
                        output.append(aSplitInput);
                        output.append(" ");
                        continue;
                    }
                    int countaSplitInputDecreased = aSplitInput.length();
                    StringBuilder childOutput = new StringBuilder();
                    int count = 0;
                    for (int i = aSplitInput.length() - 1; i >= 0; i--) {
                        countaSplitInputDecreased--;
                        String aChar = aSplitInput.substring(i, i + 1);
                        if (count == some || pattern.matcher(aChar).matches()) {
                            childOutput.insert(0, aChar);
                        } else {
                            if (minLetterToShow > countaSplitInputDecreased) {
                                childOutput.insert(0, aChar);
                            } else {
                                childOutput.insert(0, "*");
                            }
                            count++;
                        }
                    }
                    output.append(childOutput).append(" ");
                }
            }
        }
        return output.toString().trim();
    }

    public static String changeVocaWithAllAsterisk(String input) {
        StringBuilder output = new StringBuilder();
        if (!TextUtils.isEmpty(input)) {
            Pattern pattern = Pattern.compile(Constant.PATTERN_SPECIAL_CHARACTERS);
            String[] splitInput = input.split(" ");
            if (splitInput.length > 0) {
                for (String aSplitInput : splitInput) {
                    for (int i = 0; i < aSplitInput.length(); i++) {
                        String aChar = aSplitInput.substring(i, i + 1);
                        if (pattern.matcher(aChar).matches()) {
                            output.append(aChar);
                        } else {
                            output.append("*");
                        }
                    }
                    output.append(" ");
                }
            }
        }
        return output.toString().trim();
    }

    public static boolean isShow4Buttons(int vocaKnow) {
        return vocaKnow != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                && vocaKnow != Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1
                && vocaKnow != Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2
                && vocaKnow != Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public static LinkedHashMap<AmkiGradeHeader, List<AmkiItem>> groupVocaListByGrade(Context context, List<AmkiItem> vocas) {
        LinkedHashMap<AmkiGradeHeader, List<AmkiItem>> map = new LinkedHashMap<>();
        if (vocas != null && !vocas.isEmpty()) {
            List<AmkiItem> grade1 = new ArrayList<>();
            List<AmkiItem> grade2 = new ArrayList<>();
            List<AmkiItem> notDetermined = new ArrayList<>();
            List<AmkiItem> unknown = new ArrayList<>();
            List<AmkiItem> halfKnown = new ArrayList<>();
            List<AmkiItem> known = new ArrayList<>();
            for (AmkiItem voca : vocas) {
                int vocaKnow = voca.getAmkiKnow();
                if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    if (voca.getAmkiKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                            && voca.getAmkiEvaluationGrade().equals(Constant.EVALUATE.GRADE_A)) {
                        known.add(voca);
                    } else {
                        halfKnown.add(voca);
                    }
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                    grade1.add(voca);
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                    grade2.add(voca);
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                    unknown.add(voca);
                } else {
                    notDetermined.add(voca);
                }
            }
            AmkiGradeHeader header;
            int index = 0;
            if (!grade1.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_amki_grade_1);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_A);
                header.setItemCount(grade1.size());
                header.setHeaderText("(" + grade1.size() + ") " + context.getString(R.string.target_1));
                map.put(header, grade1);
                for (AmkiItem voca : grade1) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
            if (!grade2.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_amki_grade_2);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_B);
                header.setItemCount(grade2.size());
                header.setHeaderText("(" + grade2.size() + ") " + context.getString(R.string.target_2));
                map.put(header, grade2);
                for (AmkiItem voca : grade2) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
            if (!notDetermined.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_not_determined);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_NOT_RATED);
                header.setItemCount(notDetermined.size());
                header.setHeaderText("(" + notDetermined.size() + ") " + context.getString(R.string.not_determined));
                map.put(header, notDetermined);
                for (AmkiItem voca : notDetermined) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
            if (!unknown.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_unknown);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_UNKNOWN);
                header.setItemCount(unknown.size());
                header.setHeaderText("(" + unknown.size() + ") " + context.getString(R.string.unknown_phrases));
                map.put(header, unknown);
                for (AmkiItem voca : unknown) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
            if (!halfKnown.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_known);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_KNOWN);
                header.setSecondaryIcon(R.drawable.ic_unknown_pronounce);
                header.setThirdIcon(R.drawable.ic_tutor_eval_b);
                header.setFourthIcon(R.drawable.ic_tutor_eval_c);
                header.setItemCount(halfKnown.size());
                header.setHeaderText("(" + halfKnown.size() + ") " + context.getString(R.string.half_known_phrases));
                map.put(header, halfKnown);
                for (AmkiItem voca : halfKnown) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
            if (!known.isEmpty()) {
                header = new AmkiGradeHeader();
                header.setPrimaryIconBackground(R.drawable.bg_circle_voca_know_known);
                header.setPrimaryIconText(Constant.AMKI_GRADE.DISPLAY_KNOWN);
                header.setSecondaryIcon(R.drawable.ic_tutor_eval_a);
                header.setItemCount(known.size());
                header.setHeaderText("(" + known.size() + ") " + context.getString(R.string.no_need_to_practice));
                map.put(header, known);
                for (AmkiItem voca : known) {
                    if (voca instanceof VocaStudyChat) {
                        ((VocaStudyChat) voca).setIndex(++index);
                    }
                }
            }
        }
        return map;
    }

    public static List<Object> parseAdapterData(LinkedHashMap<AmkiGradeHeader, List<AmkiItem>> map) {
        List<Object> list = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            int section = -1;
            for (AmkiGradeHeader header : map.keySet()) {
                List<AmkiItem> vocas = map.get(header);
                if (vocas != null && !vocas.isEmpty()) {
                    list.add(header);
                    section++;
                    for (int i = 0; i < vocas.size(); i++) {
                        AmkiItem voca = vocas.get(i);
                        list.add(voca);
                        if (voca instanceof VocaStudyChat) {
                            VocaStudyChat vocaStudyChat = (VocaStudyChat) voca;
                            vocaStudyChat.setTblSection(section);
                            vocaStudyChat.setTblRow(i);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static int getKnowByGrade(int grade) {
        return grade == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? Constant.VOCA_KNOW.VOCA_KNOW_KNOWN : Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public static String[] getReadCountValues() {
        int count = Constant.READ_COUNT_MAX - Constant.READ_COUNT_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.READ_COUNT_MIN);
        }
        return result;
    }

    public static String[] getCountNotRatedWordsOnlyToDisplayInSubtitle() {
        int maxCount = 11;
        String[] result = new String[maxCount];
        for (int i = 0; i < maxCount; i++) {
            result[i] = String.valueOf(i);
        }
        return result;
    }

    public static void updateTextViewDifficultLabelForSubtitle(Context context, TextView tv, int count, int all) {
        if (all <= 0) {
            tv.setVisibility(View.GONE);
            return;
        }
        int difficult = (int) ((count / (double) all) * 100);
        DLog.d("", "count=" + count + " - all=" + all + " - difficult=" + difficult);
        int strId, color;
        if (difficult > 90) {
            strId = R.string.very_easy;
            color = R.color.color_file_dificulty_very_easy;
        } else if (difficult > 80) {
            strId = R.string.easy;
            color = R.color.color_file_dificulty_easy;
        } else if (difficult > 70) {
            strId = R.string.good;
            color = R.color.color_file_dificulty_good;
        } else if (difficult > 60) {
            strId = R.string.hard;
            color = R.color.color_file_dificulty_hard;
        } else {
            strId = R.string.very_hard;
            color = R.color.color_file_dificulty_very_hard;
        }
        tv.setVisibility(View.VISIBLE);
        tv.setTextColor(ContextCompat.getColor(context, color));
        tv.setText(strId);
    }

    public static int getLessonWidgetBackground(long startTimeSeconds, long finishTimeSeconds, boolean isStarted, boolean isFinished) {
        long now = System.currentTimeMillis();
        long startDateMillis = DateUtils.secondsToMillis(startTimeSeconds);
        long finishDateMillis = DateUtils.secondsToMillis(finishTimeSeconds);
        if (now > finishDateMillis || isFinished)
            return R.drawable.bg_border_16dp_grey; // past lessons
        if (now > startDateMillis)
            return isStarted
                    ? R.drawable.bg_border_16dp_green // studying
                    : R.drawable.bg_border_16dp_red; // not started on time
        if (DateUtils.isToday(new Date(startDateMillis)))
            return R.drawable.bg_border_16dp_orange; // upcoming today lessons
        return R.drawable.bg_border_16dp_white; // normal case
    }

    public static String localeToEmoji(Locale locale) {
        String countryCode = locale.getCountry();
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }

    public static String localeToEmoji(int idApi) {
        return localeToEmoji(EnumLanguage.findByIdApi(idApi).getLocale());
    }

//    public static int changeKnowFromDoubleClick(int know) {
//        if (know == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            return Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
//        }
//        return Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//    }

//    public static void updateTextViewSubtitleKnowValue(TextView tv, int know) {
//        if (know >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_KNOWN);
//            tv.setBackgroundResource(R.drawable.bg_circle_light_blue);
//        } else if (know == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_A);
//            tv.setBackgroundResource(R.drawable.bg_circle_orange);
//        } else if (know == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_B);
//            tv.setBackgroundResource(R.drawable.bg_circle_orange);
//        } else if (know == Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED) {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_QUESTION);
//            tv.setBackgroundResource(R.drawable.bg_circle_grey);
//        } else {
//            tv.setText(Constant.AMKI_GRADE.DISPLAY_UNKNOWN);
//            tv.setBackgroundResource(R.drawable.bg_circle_grey);
//        }
//    }

    public static String[] getCountryNameList(Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        ArrayList<String> countries = new ArrayList<>();
        for (Locale locale : Constant.LOCALES) {
            countries.add(locale.getDisplayCountry(currentLocale));
        }
        Collections.sort(countries);
        return countries.toArray(new String[0]);
    }

    public static String getCountryName(Context context, String countryCode) {
        if (!TextUtils.isEmpty(countryCode)) {
            Locale currentLocale = context.getResources().getConfiguration().locale;
            for (Locale locale : Constant.LOCALES) {
                if (locale.getCountry().equals(countryCode)) {
                    return locale.getDisplayCountry(currentLocale);
                }
            }
        }
        return countryCode;
    }

    public static String getCountryCode(Context context, String countryName) {
        if (!TextUtils.isEmpty(countryName)) {
            Locale currentLocale = context.getResources().getConfiguration().locale;
            for (Locale locale : Constant.LOCALES) {
                if (locale.getDisplayCountry(currentLocale).equals(countryName)) {
                    return locale.getCountry();
                }
            }
        }
        return countryName;
    }

    public static String getUserDisplayLocation(Context context, User user) {
        String location = getCountryName(context, user.getNation());
        if (!TextUtils.isEmpty(user.getCity())) {
            if (!TextUtils.isEmpty(location)) {
                location += ", ";
            }
            location += user.getCity();
        }
        return location;
    }

    public static byte[] backupObject(Object obj) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(byteStream).writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteStream.toByteArray();
    }

    public static Object restoreObject(byte[] data) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        try {
            return new ObjectInputStream(byteStream).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object cloneObject(Object obj) {
        return restoreObject(backupObject(obj));
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    public static File getBackupFileOnLocal(File voiceFolder, int studyLang, int uid, String fileName) {
        if (voiceFolder != null) {
            File folder = new File(
                    voiceFolder.getPath()
                            + "/" + Constant.FILE.FOLDER_BACKUP
                            + "/" + studyLang
                            + "/" + uid
            );
            if (folder.exists() || folder.mkdirs()) {
                return new File(folder, fileName);
            }
        }
        return null;
    }

    public static String getBackupPathOnFirebase(int studyLang, int uid) {
        return Constant.FILE.FOLDER_BACKUP_SPEAKING
                + "/" + studyLang
                + "/" + uid
                + "/";
    }

    public static String getBackupFileName(int studyLang, int vocaType, int vocaId, int uid, int version, long creationMillis) {
        return getBackupFileNamePrefix(studyLang, vocaType, vocaId, uid)
                + version
                + "_" + DateUtils.getDateStringFormat().format(new Date(creationMillis))
                + "." + Constant.FILE.EXTENTION_SPEAKING;
    }

    public static String getBackupFileNamePrefix(int studyLang, int vocaType, int vocaId, int uid) {
        return studyLang
                + "_" + vocaType
                + "_" + vocaId
                + "_" + uid
                + "_" + Constant.FILE.FOLDER_BACKUP.toUpperCase()
                + "_";
    }

    public static VocaStudy createVocaStudy(VocaDetailInfo vocaDetailInfo) {
        VocaStudy vocaStudy = new VocaStudy();
        vocaStudy.setVocaId(vocaDetailInfo.getVocaId());
        vocaStudy.setVoca(vocaDetailInfo.getVoca());
        vocaStudy.setVocaDisplay(vocaDetailInfo.getVocaDisplay());
        vocaStudy.setVocaTTS(vocaDetailInfo.getVocaTTS());
        vocaStudy.setPronounce(vocaDetailInfo.getPronounce());
        vocaStudy.setMeaning(vocaDetailInfo.getMeaning());
        vocaStudy.setMeaningEnglish(vocaDetailInfo.getMeaningEnglish());
        vocaStudy.setMeaningTTS(vocaDetailInfo.getMeaningTTS());
        vocaStudy.setType(vocaDetailInfo.getVocaType());
        vocaStudy.setPath(vocaDetailInfo.getVIPath());
        vocaStudy.setHasVoiceFile(vocaDetailInfo.hasVoiceFile());
        return vocaStudy;
    }

    public static Set<Character.UnicodeBlock> getChineseUnicodeBlocks() {
        return new HashSet<Character.UnicodeBlock>() {
            {
                add(Character.UnicodeBlock.CJK_COMPATIBILITY);
                add(Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS);
                add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
                add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
                add(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
                add(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
                add(Character.UnicodeBlock.KANGXI_RADICALS);
                add(Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
            }
        };
    }

    public static boolean containTwoChineseCharacters(String voca) {
        Set<Character.UnicodeBlock> chineseUnicodeBlocks = getChineseUnicodeBlocks();
        char[] vocaArray = voca.toCharArray();
        int count = 0;
        for (char c : vocaArray) {
            if (chineseUnicodeBlocks.contains(Character.UnicodeBlock.of(c))
                    && ++count == 2) {
                return true;
            }
        }
        return false;
    }

    public static String getStarForText(String text) {
        String result = "";
        int count = text.length();
        for (int i = 0; i < count; i++) {
            if (Character.isAlphabetic(text.codePointAt(i))) {
                result += "*";
            } else {
                result += text.charAt(i);
            }
        }
        return result;
    }

    public static void splitVocaStudyHandWriting(String voca, List<String> splitList, List<Integer> posList) {
        char[] vocaArray = voca.toCharArray();
        int count = vocaArray.length;
        for (int i = 0; i < count; i++) {
            char c = vocaArray[i];
            if (Character.isLetter(c)) {
                splitList.add(String.valueOf(c));
                posList.add(i);
            }
        }
    }

    public static List<String> splitVocaStudy(String voca) {
        ArrayList<String> result = new ArrayList<>();
        String[] splited = voca.contains(" ") ? voca.split(" ") : voca.split("(?!^)");
        boolean skip;
        for (String s : splited) {
            skip = s.length() == 1 && !Character.isLetter(s.charAt(0));
            if (!skip) {
                result.add(s);
            }
        }
        return result;
    }

    public static int calculateNoOfColumns(Context context, int itemWidth, int marginSize) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        int scaleWidth = (int) Math.ceil(itemWidth * configuration.fontScale);
        return (displayMetrics.widthPixels - marginSize) / scaleWidth;
    }

    public static String getMeaningDetailed(VocaDetailInfo voca) {
        return TextUtils.isEmpty(voca.getMeaningDetailed()) ? voca.getMeaningEngDetailed() : voca.getMeaningDetailed();
    }

    public static int getBookType(boolean isUserBook) {
        return isUserBook ? Constant.API_VALUE.VALUE_USER_BOOK : Constant.API_VALUE.VALUE_SERVER_BOOK;
    }

    public static boolean hasAllRecordedFiles(File practiceFolder, ArrayList<VocaPractice> vocas) {
        File file;
        for (VocaPractice voca : vocas) {
            file = getVoiceFileOnLocal(practiceFolder, voca.getVIPath());
            if (file == null || !file.exists())
                return false;
        }
        return true;
    }

    public static String[] getMinuteValues() {
        String[] result = new String[60];
        for (int i = 0; i < 60; i++) {
            result[i] = String.valueOf(i + 1);
        }
        return result;
    }

    public static String[] getRepeatCountValues(Context context) {
        final int sizeText = 3;
        final String[] lists = getRepeatCountValues(Constant.READ_COUNT_MIN, Constant.READ_COUNT_MAX);
        String[] result = new String[lists.length + sizeText];
        result[0] = context.getString(R.string.smart_fewer, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER_PLUS);
        result[1] = context.getString(R.string.smart_few, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW_PLUS);
        result[2] = context.getString(R.string.smart_many, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY_PLUS);
        int count = 3;
        for (String s : lists) {
            result[count] = s;
            count++;
        }
        return result;
    }

    public static String[] getRepeatCountValues() {
        return getRepeatCountValues(Constant.READ_COUNT_MIN, Constant.READ_COUNT_MAX);
    }

    public static String[] getRepeatTopicsValues() {
        int count = Constant.REPEAT_TOPICS_MAX - Constant.REPEAT_TOPICS_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.REPEAT_TOPICS_MIN);
        }
        return result;
    }

    public static String[] getPhrasesToStudyValues() {
        int count = Constant.COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_MAX - Constant.COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_MIN);
        }
        return result;
    }

    public static String getEvaluateText(Context context, String evaluateGrade) {
        if (Constant.EVALUATE.GRADE_A.equals(evaluateGrade))
            return context.getString(R.string.speaking_excellent);
        if (Constant.EVALUATE.GRADE_B.equals(evaluateGrade))
            return context.getString(R.string.speaking_good);
        if (Constant.EVALUATE.GRADE_C.equals(evaluateGrade))
            return context.getString(R.string.speaking_not_bad);
        return "";
    }

    public static boolean isUserBook(@NonNull VocaBook book) {
        return book.getStudentId() > 0;
    }

    private static String getContentTextForLessonNotification(Context context, Lesson lesson, boolean isStartTime) {
        if (isStartTime)
            return context.getString(R.string.tpl_lesson_start, DateUtils.getTimeToHourFormat().format(new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()))));
        return context.getString(R.string.tpl_lesson_finish, DateUtils.getTimeToHourFormat().format(new Date(DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS()))));
    }

    public static void createLessonNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_lesson_name);
            String description = context.getString(R.string.notification_channel_lesson_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constant.NOTIFICATION_CHANNEL_LESSON_ID, name, importance);
            channel.setDescription(description);
//            channel.setVibrationPattern(Constant.VIBRATION_PATTERN);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createLessonNotification(Context context, Lesson lesson, boolean isStartTime) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constant.NOTIFICATION_CHANNEL_LESSON_ID)
                .setSmallIcon(R.drawable.ic_edit_black_24dp)
                .setContentTitle(lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR ? lesson.getStudentName() : lesson.getTutorName())
                .setContentText(getContentTextForLessonNotification(context, lesson, isStartTime))
//                .setVibrate(Constant.VIBRATION_PATTERN)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(lesson.getId(), builder.build());
    }

    public static ArrayList<SERVER_VOCABOOKS> getSortedBookList(final String langStudy) {
        final ArrayList<SERVER_VOCABOOKS> sortedVocabooks = new ArrayList<>();
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                addBooksToSortedList(realm, sortedVocabooks, getBooksByParentId(realm, "0", langStudy), langStudy);
            }
        });
        return sortedVocabooks;
    }

    public static void addBooksToSortedList(Realm realm, ArrayList<SERVER_VOCABOOKS> sortedVocabooks, RealmResults<SERVER_VOCABOOKS> serverVocabooks, String langStudy) {
        for (SERVER_VOCABOOKS serverVocabook : serverVocabooks) {
            if (Utils.parseInt(serverVocabook.getVOCA_COUNT()) > 0) {
                sortedVocabooks.add(realm.copyFromRealm(serverVocabook));
                DLog.i("sortedVocabooks", serverVocabook.getNAME_ENG());
            } else {
                addBooksToSortedList(realm, sortedVocabooks, getBooksByParentId(realm, serverVocabook.getID(), langStudy), langStudy);
            }
        }
    }

    public static RealmResults<SERVER_VOCABOOKS> getBooksByParentId(Realm realm, String parentId, String langStudy) {
        return realm.where(SERVER_VOCABOOKS.class)
                .equalTo("PARENT_ID", parentId)
                .equalTo("USE_VOCABOOK", "1")
                .equalTo("LANG_STUDY", langStudy)
                .sort("DISP_ORDER")
                .findAll();
    }

    public static String[] getOrderValues() {
        int count = Constant.ORDER_MAX - Constant.ORDER_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.ORDER_MIN);
        }
        return result;
    }

    public static String getHeaderBookName(final SERVER_VOCABOOKS serverVocabook, final EnumLanguage enumDisplayLanguage, final EnumLanguage enumStudyLanguage) {
        String bookName = serverVocabook.getName(enumDisplayLanguage);
        if (enumDisplayLanguage != enumStudyLanguage) {
            bookName += " (" + serverVocabook.getName(enumStudyLanguage) + ")";
        }
        final String[] parentBookName = new String[1];
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                SERVER_VOCABOOKS parentBook = realm.where(SERVER_VOCABOOKS.class)
                        .equalTo("ID", serverVocabook.getPARENT_ID())
                        .findFirst();
                if (parentBook != null) {
                    String n = parentBook.getName(enumDisplayLanguage);
                    if (enumDisplayLanguage != enumStudyLanguage) {
                        n += " (" + parentBook.getName(enumStudyLanguage) + ")";
                    }
                    parentBookName[0] = n;
                }
            }
        });
        if (!TextUtils.isEmpty(parentBookName[0])) {
            bookName = parentBookName[0] + " : " + bookName;
        }
        return bookName;
    }

    public static List<Object> getMessageListForLesson(final EnumLanguage enumStudyLanguage, final EnumLanguage enumDisplayLanguage) {
        return getMessageListForLessonByStudyRole(enumStudyLanguage, enumDisplayLanguage, -1);
    }

    public static List<Object> getMessageListForLessonByStudyRole(final EnumLanguage enumStudyLanguage, final EnumLanguage enumDisplayLanguage, final int studyRole) {
        final List<Object> messages = new ArrayList<>();
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                RealmResults<TBL_MESSAGE> tblMessages;
                if (studyRole > -1) {
                    tblMessages = realm.where(TBL_MESSAGE.class)
                            .equalTo("USE_STUDYMODE", 1)
                            .equalTo("STUDY_ROLE", studyRole)
                            .sort("DISP_ORDER_STUDYMODE")
                            .findAll();
                } else {
                    tblMessages = realm.where(TBL_MESSAGE.class)
                            .equalTo("USE_STUDYMODE", 1)
                            .sort("DISP_ORDER_STUDYMODE")
                            .findAll();
                }
                for (TBL_MESSAGE message : tblMessages) {
                    TBL_MESSAGE message1 = realm.copyFromRealm(message);
                    String meaningStudy = message1.getMeaning(enumStudyLanguage);
                    message1.setMeaningStudy(meaningStudy);
                    String meaningDisplay = message1.getMeaning(enumDisplayLanguage);
                    DLog.i("Message", meaningDisplay);
                    messages.add(message1);
                }
            }
        });
        return messages;
    }

    public static boolean isMainStudent(User user) {
        return user.getStudyRole() == Constant.STUDY_ROLE_STUDENT && user.isStudyRoleMain();
    }

    public static boolean isMainTutor(User user) {
        return user.getStudyRole() == Constant.STUDY_ROLE_TUTOR && user.isStudyRoleMain();
    }

    public static File getChatFolderOnLocal(Context context) {
        return getOutputFolderOnLocal(context, Constant.FILE.FOLDER_CHAT);
    }

    public static String getUserStudyRoleTitle(Context context, User user, Lesson lesson, boolean useMain) {
        int id;
        String name = null;
        if (user == null || user.getStudyRole() == Constant.STUDY_ROLE_OBSERVER) {
            return lesson.getStudentName() + " / " + lesson.getTutorName();
        } else if (user.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
            if (useMain && user.isStudyRoleMain()) {
                id = R.string.main_student;
            } else {
                id = R.string.student_format;
                name = lesson.getTutorName();
            }
        } else {
            if (useMain && user.isStudyRoleMain()) {
                id = R.string.main_tutor;
            } else {
                id = R.string.tutor_format;
                name = lesson.getStudentName();
            }
        }
        return context.getString(id, name);
    }

    public static String getUserStudyRoleTitle(Context context, User user, boolean useMain) {
        int id;
        if (user == null) {
            id = R.string.observer;
        } else if (user.getStudyRole() == Constant.STUDY_ROLE_STUDENT) {
            if (useMain && user.isStudyRoleMain()) {
                id = R.string.main_student;
            } else {
                id = R.string.student;
            }
        } else if (user.getStudyRole() == Constant.STUDY_ROLE_TUTOR) {
            if (useMain && user.isStudyRoleMain()) {
                id = R.string.main_tutor;
            } else {
                id = R.string.tutor;
            }
        } else {
            id = R.string.observer;
        }
        return context.getString(id);
    }

    public static List<GRAMMAR> getGrammarList(int parentId, EnumLanguage enumStudyLanguage, EnumLanguage enumDisplayLanguage) {
        List<GRAMMAR> grammarList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            RealmResults<GRAMMAR> grammars = getGrammarsByParentId(realm, parentId, enumStudyLanguage.getIdApi());
            for (GRAMMAR grammar : grammars) {
                GRAMMAR grammar1 = realm.copyFromRealm(grammar);
                grammar1.setDISPLAY_LANG(enumDisplayLanguage.getIdApi());
                grammarList.add(grammar1);
            }
        });
        return grammarList;
    }


    public static List<GRAMMAR> getSortedGrammarList(EnumLanguage enumStudyLanguage, EnumLanguage enumDisplayLanguage) {
        List<GRAMMAR> sortedGrammars = new ArrayList<>();
        int langStudy = enumStudyLanguage.getIdApi();
        int displayLang = enumDisplayLanguage.getIdApi();
        executeRealmTransaction(realm -> addGrammarsToSortedList(realm, sortedGrammars, getGrammarsByParentId(realm, 0, langStudy), langStudy, displayLang));
        return sortedGrammars;
    }

    private static void addGrammarsToSortedList(Realm realm, List<GRAMMAR> sortedGrammars, RealmResults<GRAMMAR> grammars, int langStudy, int displayLang) {
        for (GRAMMAR grammar : grammars) {
            if (grammar.getHAS_SUB_LIST() == 0) {
                GRAMMAR g = realm.copyFromRealm(grammar);
                g.setDISPLAY_LANG(displayLang);
                sortedGrammars.add(g);
            } else {
                addGrammarsToSortedList(realm, sortedGrammars, getGrammarsByParentId(realm, grammar.getID(), langStudy), langStudy, displayLang);
            }
        }
    }

    private static RealmResults<GRAMMAR> getGrammarsByParentId(Realm realm, int parentId, int langStudy) {
        return realm.where(GRAMMAR.class)
                .equalTo("PARENT_ID", parentId)
                .equalTo("LANG_STUDY", langStudy)
                .equalTo("USED", 1)
                .sort("DISP_ORDER")
                .findAll();
    }

    private static void addReadingsToSortedList(Realm realm, List<READING> sortedReadings, RealmResults<READING> readings, int langStudy, int displayLang) {
        for (READING reading : readings) {
            if (reading.getHAS_SUB_LIST() == 0) {
                READING g = realm.copyFromRealm(reading);
                g.setDISPLAY_LANG(displayLang);
                sortedReadings.add(g);
            } else {
                addReadingsToSortedList(realm, sortedReadings, getReadingsByParentId(realm, reading.getID(), langStudy), langStudy, displayLang);
            }
        }
    }

    public static List<READING> getSortedReadingList(EnumLanguage enumStudyLanguage, EnumLanguage enumDisplayLanguage) {
        List<READING> sortedReadings = new ArrayList<>();
        int langStudy = enumStudyLanguage.getIdApi();
        int displayLang = enumDisplayLanguage.getIdApi();
        executeRealmTransaction(realm -> addReadingsToSortedList(realm, sortedReadings, getReadingsByParentId(realm, 0, langStudy), langStudy, displayLang));
        return sortedReadings;
    }

    public static List<READING> getReadingList(int parentId, EnumLanguage enumStudyLanguage, EnumLanguage enumDisplayLanguage) {
        List<READING> readingList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            RealmResults<READING> readings = getReadingsByParentId(realm, parentId, enumStudyLanguage.getIdApi());
            for (READING reading : readings) {
                READING reading1 = realm.copyFromRealm(reading);
                reading1.setDISPLAY_LANG(enumDisplayLanguage.getIdApi());
                readingList.add(reading1);
            }
        });
        return readingList;
    }

    private static RealmResults<READING> getReadingsByParentId(Realm realm, int parentId, int langStudy) {
        return realm.where(READING.class)
                .equalTo("PARENT_ID", parentId)
                .equalTo("LANG_STUDY", langStudy)
                .equalTo("USED", 1)
                .sort("DISP_ORDER")
                .findAll();
    }

    public static String md5(String input) {
        if (!TextUtils.isEmpty(input)) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] array = md.digest(input.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte anArray : array) {
                    sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getChatRecordingDowloadURL(int chatRoomId, int senderId, String hashCode) {
        return Constant.FILE.FOLDER_CHAT
                + "/" + chatRoomId
                + "/" + senderId
                + "/" + Constant.FILE.FOLDER_VOICE
                + "/" + hashCode
                + "." + Constant.FILE.EXTENTION_SPEAKING;
    }

    public static String getChatImageDowloadURL(int chatRoomId, int senderId, String hashCode) {
        return Constant.FILE.FOLDER_CHAT
                + "/" + chatRoomId
                + "/" + senderId
                + "/" + Constant.FILE.FOLDER_IMAGE
                + "/" + hashCode
                + "." + Constant.FILE.EXTENTION_IMAGE;
    }

    public static File getChatFileOnLocal(File folder, String downloadURL) {
        if (folder == null || downloadURL == null)
            return null;

        int pos = downloadURL.lastIndexOf("/");
        File subFolder = new File(folder + downloadURL.substring(0, pos).replace(Constant.FILE.FOLDER_CHAT, ""));
        if (!subFolder.exists() && !subFolder.mkdirs())
            return null;

        return new File(subFolder, downloadURL.substring(pos + 1));
    }

    public static void startChooseImageIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, Constant.REQUEST_CODE.SELECT_PHOTO);
    }

    public static void startCameraIntent(Activity activity, Uri photoUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(intent, Constant.REQUEST_CODE.CAMERA);
    }

    public static String addSuffixThumbnail(String imagePath) {
        if (TextUtils.isEmpty(imagePath))
            return "";
        int pos = imagePath.lastIndexOf(".");
        return imagePath.substring(0, pos) + Constant.FILE.SUFFIX_THUMBNAIL + imagePath.substring(pos);
    }

    public static String getVoiceFileDuration(String path) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mmr.release();
            return duration;
        } catch (Exception e) {
            // Handle the error
            return null;
        }
    }

    public static String generateVoiceRoomCall(int id) {
        return Constant.JITSI.ROOM + id;
    }

    public static ArrayList<Integer> getParentBookIds(int bookId) {
        ArrayList<Integer> parentBookIds = new ArrayList<>();
        parentBookIds.add(bookId);
        executeRealmTransaction(realm -> {
            SERVER_VOCABOOKS book = realm.where(SERVER_VOCABOOKS.class)
                    .equalTo("ID", String.valueOf(bookId))
                    .findFirst();
            if (book != null) {
                String strParentId = book.getPARENT_ID();
                int parentId = Utils.parseInt(strParentId);
                while (parentId > 0) {
                    parentBookIds.add(parentId);
                    SERVER_VOCABOOKS parentBook = realm.where(SERVER_VOCABOOKS.class)
                            .equalTo("ID", strParentId)
                            .findFirst();
                    if (parentBook == null) {
                        parentId = 0;
                    } else {
                        strParentId = parentBook.getPARENT_ID();
                        parentId = Utils.parseInt(strParentId);
                    }
                }
            }
        });
        return parentBookIds;
    }

    public static void setStudyLanguage(SharedPreferencesDB sharedPreferences, EnumLanguage language) {
        sharedPreferences.setStudyLanguage(language.getFormatApi());
    }

    public static void setMotherTongueLanguage(SharedPreferencesDB sharedPreferences, EnumLanguage motherTongue) {
        sharedPreferences.setMotherTongueLanguage(motherTongue.getFormatApi());
    }

    public static void setMenuLanguage(SharedPreferencesDB sharedPreferences, EnumLanguage language) {
        sharedPreferences.setMenuLanguage(language.getFormatApi());
    }

    public static void deleteVocaVersion() {
        executeRealmTransaction(realm -> realm.delete(VocaDownload.class));
    }

    /*
     * @deprecated use same method in AppFlavorUtil
     */
    @Deprecated
    public static boolean isDalvocaApp() {
        return "com.dalnimsoft.dalvoca".equals(BuildConfig.APPLICATION_ID);
    }

    /*
     * @deprecated use same method in AppFlavorUtil
     */
    @Deprecated
    public static boolean isAraPlayerTestApp() {
        return "com.dalnimsoft.araplayer_test".equals(BuildConfig.APPLICATION_ID);
    }

    /*
     * @deprecated use same method in AppFlavorUtil
     */
    @Deprecated
    public static boolean isAraHanjaDebugMode() {
        return BuildConfig.DEBUG && BuildConfig.FLAVOR.equals(EnumFlavor.ARAHANJA.getName());
    }

    public static List<Integer> getRandom(int totalSize, int size, int currentItem) {
        Random random = new Random();
        List<Integer> generated = new ArrayList<>();
        generated.add(currentItem);
        for (int i = 0; i < totalSize; i++) {
            int next = random.nextInt(totalSize);
            if (!generated.contains(next)) {
                generated.add(next);
            } else {
                i--;
            }
            if (generated.size() >= size)
                return generated;
        }
        return generated;
    }

    public static boolean isSameVoca(IVocaCoreItem voca1, IVocaCoreItem voca2) {
        return voca1.getVIVocaType().equals(voca2.getVIVocaType()) && voca1.getVIVocaId().equals(voca2.getVIVocaId());
    }

    public static boolean isBookmark(IVocaBasicItem voca) {
        return voca.isVIBookmark();
    }

    //Dalnim add
    public static boolean isIndexInsideList(List<?> list, int index) {
        boolean blnResult = false;
        if (!Utils.isEmpty(list)) {
            if ((0 <= index) && (index < list.size())) {
                blnResult = true;
            }
        }
        return blnResult;
    }

    public static void openCopyDialog(Context context, IVocaBasicItem vocaBasicItem) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_word_pronounce);
        if (BaseVocaKnow.isVocaTypeWord(vocaBasicItem)) {
            displayOptions = Arrays.stream(displayOptions).limit(displayOptions.length - 2).toArray(String[]::new);
        }
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                0,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        String strToCopy = "";
                        switch (which) {
                            case 0:
                                strToCopy = vocaBasicItem.getVIVoca();
                                break;
                            case 1:
                                strToCopy = "(" + vocaBasicItem.getVIVoca() + ")";
                                break;
                            case 2:
                                strToCopy = vocaBasicItem.getVIVoca() + "(" + vocaBasicItem.getVIPronounce() + ")";
                                break;
                            case 3:
                                strToCopy = vocaBasicItem.getVIPronounce() + "(" + vocaBasicItem.getVIVoca() + ")";
                                break;
                            case 4:
                            case 5:
                                String meaningWithNewLine = Utils.isEmpty(vocaBasicItem.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context))) ? "" : "\n" + vocaBasicItem.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
                                if (which == 4) {
                                    strToCopy = vocaBasicItem.getVIVoca() + "(" + vocaBasicItem.getVIPronounce() + ")" + meaningWithNewLine;
                                } else {
                                    strToCopy = vocaBasicItem.getVIPronounce() + "(" + vocaBasicItem.getVIVoca() + ")" + meaningWithNewLine;
                                }
                                break;
                        }

                        String toastText = getMessageInToastToShow(context, strToCopy);
                        Utils.copyToClipboard(context, strToCopy, toastText);
                    }



                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    public static String getMessageInToastToShow(Context context, String strToCopy) {
        int maxStringLength = 60;
        return StringUtils.getSubstringWithMoreText(context.getString(R.string.copied) + "\n" + strToCopy, maxStringLength);
    }

    public static void setIsPlayingTTS(List<? extends IVocaFullPlayTTSItem> itemList, IVocaFullPlayTTSItem item, boolean playing) {
        for(IVocaFullPlayTTSItem iVocaFullPlayTTSItem : itemList) {
            if (isSameVoca(item, iVocaFullPlayTTSItem)) {
                iVocaFullPlayTTSItem.setVIPlaying(playing);
            } else {
                iVocaFullPlayTTSItem.setVIPlaying(false);
            }
        }
    }

    public static void setIsRecordingTTS(List<? extends IVocaFullPlayTTSItem> itemList, IVocaFullPlayTTSItem item, boolean recording) {
        for(IVocaFullPlayTTSItem iVocaFullPlayTTSItem : itemList) {
            if (isSameVoca(item, iVocaFullPlayTTSItem)) {
                iVocaFullPlayTTSItem.setVIRecording(recording);
            } else {
                iVocaFullPlayTTSItem.setVIRecording(false);
            }
        }
    }

    public static void resetIsRecordingVoca(List<? extends IVocaFullPlayTTSItem> itemList) {
        itemList.stream().forEach(e -> e.setVIRecording(false));
    }

    public static void resetIsPlayingTTS(List<? extends IVocaFullPlayTTSItem> itemList) {
        itemList.stream().forEach(e -> e.setVIPlaying(false));
    }

    public static void resetVocaList(List<? extends IVocaFullPlayTTSItem> itemList) {
        if (Utils.isEmpty(itemList))
            return;

        for (int i = 0; i < itemList.size(); i++) {
            IVocaFullPlayTTSItem item = itemList.get(i);
            item.setVIIndex(i + 1);
            item.setVIRecording(false);
            item.setVIPlaying(false);
        }
    }

    public static int udpateVoiceFileInVocaList(Context context, List<? extends IVocaFullPlayTTSItem> itemList) {
        int cntOfVoiceFiles = 0;
        if (!Utils.isEmpty(itemList)) {
            int studyLangCode = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage()).getIdApi();
            int uid = SharedPreferencesDB.getInstance(context).getRealUid();
            for (int i = 0; i < itemList.size(); i++) {
                if (Loading.isShowing()) {
                    Loading.setProgress((i + 1) * 100 / itemList.size());
                }
                IVocaFullPlayTTSItem item = itemList.get(i);
                String voiceFileName = BaseVoca.getOutputRecordingFileName(studyLangCode, item.getVIVocaType(), item.getVIVocaId(), uid);
                if ((voiceFileName != null) && (BaseFileUtil.isVoiceFileExistInVoiceFolder(context, voiceFileName))) {
                    item.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
                    cntOfVoiceFiles++;
                } else {
                    item.setVIVoiceFile(Constant.INT_BOOLEAN.FASLE);
                }
            }
        }
        return cntOfVoiceFiles;
    }

    public static void resetIsPlayingTTS(IVocaFullPlayTTSItem item) {
        item.setVIPlaying(false);
    }
    public static void setIsPlayingTTS(IVocaFullPlayTTSItem item, boolean playing) {
        item.setVIPlaying(playing);
    }

    public static String[] getMaxHomeworkValues() {
        int count = Constant.MAX_HOMEWORK_MAX - Constant.MAX_HOMEWORK_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.MAX_HOMEWORK_MIN);
        }
        return result;
    }

    public static String wrapMeaningEnglish(IVocaBasicItem voca) {
        return wrapText(voca.getVIMeaningEng());
    }

    public static String wrapMeaningEnglishDetailed(IVocaBasicItem voca) {
        return wrapText(voca.getVIMeaningEngDetailed());
    }
    public static String wrapText(String text) {
        String result = "";
        if (!Utils.isEmpty(text)) {
            result = "(" + text + ")";
        }
        return result;
    }

    public static void displayShortOrLongPronounce(Context context, IVocaFullPlayTTSItem voca, TextView tvShortPronounce, TextView tvLongPronounce) {
        tvShortPronounce.setVisibility(View.GONE);
        tvLongPronounce.setVisibility(View.GONE);
        boolean isShowPronounce = SharedPreferencesDB.getInstance(context).getDisplayPronunciation();
        if (isShowPronounce && BaseVocaKnow.isUnknownAndLess(voca)) {
            String pronounce = voca.getVIPronounce();
            int maxLengthForShortPronounce = 15;
            if (LanguageUtil.isStudyLangKorean(context) && Utils.isEmpty(pronounce)) {
                pronounce = KoreanRomanizer.romanize(voca.getVIVoca());
            }
            if (!Utils.isEmpty(pronounce)) {
                pronounce = "[" + pronounce + "]";
                if (pronounce.length() < maxLengthForShortPronounce) {
                    tvShortPronounce.setVisibility(View.VISIBLE);
                    tvShortPronounce.setText(pronounce);
                } else {
                    tvLongPronounce.setVisibility(View.VISIBLE);
                    tvLongPronounce.setText(pronounce);
                }
            }
        }
    }
    public static String getVocaWithPronounce(Context context, IVocaFullPlayTTSItem item) {
        String result = "";
        String voca = item.getVIVoca();
        String pronounce = item.getVIPronounce();
        if (Utils.isEmpty(pronounce)) {
            result = voca;
        } else {
            result = voca + " [" + pronounce + "]";
        }
        return result;
    }
    public static String getMeaningOrEnglishMeaning(Context context, IVocaFullPlayTTSItem voca) {
        String result = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
        if (Utils.isEmpty(result)) {
            result = voca.getVIMeaning(EnumLanguage.ENGLISH);
            if (!Utils.isEmpty(result)) {
                result = "(" + result + ")";
            }
        }
        return result;
    }

    public static String getMeaningDetailedOrEnglishMeaningDetailed(Context context, IVocaFullPlayTTSItem voca) {
        String result = voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(context));
        if (Utils.isEmpty(result)) {
            result = voca.getVIMeaningDetailed(EnumLanguage.ENGLISH);
            if (!Utils.isEmpty(result)) {
                result = "(" + result + ")";
            }
        }
        return result;
    }

    public static OnVoiceFileInfoDownloadListener onVoiceFileInfoDownloadListener = new OnVoiceFileInfoDownloadListener() {
        @Override
        public void onSuccess(VocaDownload vocaDownload, String voiceFileName) {
            if ((vocaDownload != null) && (!Utils.isEmpty(vocaDownload.getName()))) {
                VocaDownload vocaDownloadLocal = BaseVoca.getLocalVoiceFileInfo(vocaDownload.getName());
                if ((vocaDownloadLocal != null) && (!Utils.isEmpty(vocaDownloadLocal.getName()))) {
                    if (vocaDownload.getName().equals(vocaDownloadLocal.getName()) && vocaDownload.getVersion() > vocaDownloadLocal.getVersion()) {
                        udpateLocalDB(voiceFileName, vocaDownload.getVersion());
                    }
                } else {
                    udpateLocalDB(voiceFileName, vocaDownload.getVersion());
                }
            } else {
                udpateLocalDB(voiceFileName, 0);
            }
        }

        @Override
        public void onFailure(String voiceFileName) {
            udpateLocalDB(voiceFileName, 0);
        }

        private void udpateLocalDB(String voiceFileName, int version) {
            BaseVoca.executeRealmTransaction(realm -> {
                VocaDownload vocaDownloadInDB = realm.where(VocaDownload.class).equalTo(Constant.REALMDB.KEY_name, voiceFileName).findFirst();
                if (vocaDownloadInDB == null) {
                    vocaDownloadInDB = new VocaDownload();
                    vocaDownloadInDB.setName(voiceFileName);
                    vocaDownloadInDB.setFileSize(1); //TODO : Need to get real voice file size
                }
                vocaDownloadInDB.setVersion(version);
                realm.insertOrUpdate(vocaDownloadInDB);
            });
        }
    };

    public static void updateVocaKnowAndVocaKnowpronounceAndBookmark(DicSentenceSubDatabase subDatabase, VocaKnowAndBookmarkList vocaKnowAndBookmarkList) {
        subDatabase.resetVocaKnowAndBookmark();
        subDatabase.updateVocaKnowAndVocaKnowpronounce(vocaKnowAndBookmarkList.getVocaKnowKnowpronounceList());
        subDatabase.updateVocaBookmark(vocaKnowAndBookmarkList.getVocaBookmarkList());
    }
}
