package com.dalread.database.sqlite.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.AmkiItem;
import com.dalread.model.RubyTextModel;
import com.dalread.model.SubModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;
//이건 자막의 DIC테이블의 스키마를 담는다.
public class DicModel implements AmkiItem, Parcelable, IVocaFullPlayTTSItem {
    private int id = -1; //id는 테이블의 id이기 때문에 바꾸면 안된다. 그럼 테이블에 업데이트할때 id를 사용할수가 없다.
    private int vocaType = -1;
    private int vocaId = -1;
    private int vocaTypeBase = -1;
    private int vocaIdBase = -1;
    private String vocaDisplay;
    private String vocaTTS;

    private String pronounce;
    private int vocaKnow;
    private String meaning;
    private String meaningTTS;
    private String meaningEng;
    private String jmdictMeaning; //JM Dictionary (meaning for Japanese language, usually Spanish or German)
    private String jmdictMeaningEng; //JM Dictionary (English meaning for Japanese language)
    private int bookmark;
    private int vocaKnowPronounce;
    private int wordLevel;
    private int frequency;
    private String path; //voice file명이 들어감. 실제 녹음을 했는지와는 상관없음. 녹음을 했으면 아래에 recordedPath는 voice File 풀 경로가 들어감.
    private boolean checked;  //What is this for? 자막리스트에서 선택된 자막여부
    private boolean playing;
    private int index; //MergeUtil의 generateMeaning에서 Index는 1부터 시작하고 position은 0부터 다시 넣는다. id는 테이블에 있는 id를 가져온다.
    private String baseRuby;
    private String vocaDisplayRuby;
    private String rubyIds;
    private String rubyTypes;
    private long startTime;
    private long endTime;
    private int position; //Index와 차이점은?
    private int repeat; //Repeat count for TTS or recorded voices.
    private long startTimeOriginal;
    private long endTimeOriginal;
    // https://github.com/dalnim/IssueOnly/issues/118#issuecomment-665396347
    // red/pink word's count
    private int difficultWordsCount;
    private int langStudy;
    private String wordIds; //DifficultWords
    // check played record or TSS
    private boolean isPlayRecordOrTSS;
    private int used; //특수 문자를 가진 자막을 숨기거나 삭제할려고 필터링할때 쓴다. SHOW, HIDE_MANUAL, HIDE_AUTO3가지가 있다.
    private String meaningWords;
    // using get data in meaning adapter from subtitle_id on SUBTITLE_WORDLIST table
    private String subtitleWordlistId;
    private String subtitleOriginal;
//    private int vocaIdServer;
    private HashMap<Integer, RubyTextModel> listRubyTextModel = new HashMap<>(); //This has voca's info(KNOW, KNOWPRONOUNCE etc). Used to have difficult words, but not this has all words.
    private String meaningDetailed;
    private String memo;
    private String hanja; //Japanese or Chinese words can have this value. (It's for pronounce of Hanja(Korean Chinese))
    private String posAll; //Part of Speech, Only Japanese has value
    private String recordedPath; //녹음한 voice File 풀 경로가 들어감. 위의 path는 voice file명이 들어가고 녹음여부와는 상관없음.
    private boolean isListeningRecord;
    public DicModel() {
    }

    public DicModel(int id, String vocaDisplay, String vocaTTS, int vocaType, int vocaId, int vocaTypeBase, int vocaIdBase, String pronounce, int vocaKnow, String meaning, String meaningTTS, String meaningEng, String jmdictMeaning, String jmdictMeaningEng, int bookmark, int vocaKnowPronounce, int wordLevel, int frequency) {
        this.id = id;
        this.vocaType = vocaType;
        this.vocaId = vocaId;
        this.vocaTypeBase = vocaTypeBase;
        this.vocaIdBase = vocaIdBase;
        this.vocaDisplay = vocaDisplay;
        this.vocaTTS = vocaTTS;
        this.pronounce = pronounce;
        this.vocaKnow = vocaKnow;
        this.meaning = meaning;
        this.meaningTTS = meaningTTS;
        this.meaningEng = meaningEng;
        this.jmdictMeaning = jmdictMeaning;
        this.jmdictMeaningEng = jmdictMeaningEng;
        this.bookmark = bookmark;
        this.vocaKnowPronounce = vocaKnowPronounce;
        this.wordLevel = wordLevel;
        this.frequency = frequency;
    }

    public DicModel(VocaStudyChat voca) {
        this.vocaId = voca.getVocaId();
        this.vocaKnow = voca.getVocaKnow();
        this.vocaKnowPronounce = voca.getAmkiKnowPronounce();
    }

    public DicModel(SubModel subModel) {
        this.id = (int) subModel.getId();
        this.vocaDisplay = subModel.getContent();
        this.vocaDisplayRuby = subModel.getContentRuby();
        this.path = subModel.getPath();
        this.startTime = subModel.getStart();
        this.endTime = subModel.getEnd();
        this.startTimeOriginal = subModel.getStartTimeOriginal();
        this.endTimeOriginal = subModel.getEndTimeOriginal();
    }

    public DicModel(VocaStudyChatExam voca) {
        this.vocaId = voca.getVocaId();
        this.vocaKnow = voca.getVocaKnow();
        this.vocaKnowPronounce = voca.getVocaKnowPronounce();
        this.bookmark = voca.getBookmark();
    }

    public DicModel(DicModel item) {
        this.id = item.getId();
        this.langStudy = item.getLangStudy();
        this.vocaType = item.getVocaType();
        this.vocaId = item.getVocaId();
        this.vocaTypeBase = item.getVocaTypeBase();
        this.vocaIdBase = item.getVocaIdBase();
//        this.vocaIdServer = item.getVocaIdServer();
        this.vocaDisplay = item.getVocaDisplay();
        this.vocaDisplayRuby = item.getVocaDisplayRuby();
        this.meaning = item.getMeaning();
        this.meaningTTS = item.getMeaningTTS();
        this.meaningEng = item.getMeaningEng();
        this.jmdictMeaning = item.getJmdictMeaning();
        this.jmdictMeaningEng = item.getJmdictMeaningEng();
        this.startTime = item.getStartTime();
        this.endTime = item.getEndTime();
        this.bookmark = item.getBookmark();
        this.repeat = item.getRepeat();
        this.startTimeOriginal = item.getStartTimeOriginal();
        this.endTimeOriginal = item.getEndTimeOriginal();
        this.subtitleOriginal = item.getSubtitleOriginal();
        this.used = item.getUsed();
        this.vocaKnowPronounce = item.getVocaKnowPronounce();
        this.vocaKnow = item.getVocaKnow();
        this.listRubyTextModel = item.getListRubyTextModel();
        this.meaningDetailed = item.getMeaningDetailed();
        this.memo = item.getMemo();
        this.hanja = item.getHanja();
        this.posAll = item.getPosAll();
    }

    protected DicModel(Parcel in) {
        id = in.readInt();
        vocaDisplay = in.readString();
        vocaTTS = in.readString();
        vocaId = in.readInt();
        pronounce = in.readString();
        vocaKnow = in.readInt();
        meaning = in.readString();
        meaningTTS = in.readString();
        meaningEng = in.readString();
        jmdictMeaning = in.readString();
        jmdictMeaningEng = in.readString();
        bookmark = in.readInt();
        vocaKnowPronounce = in.readInt();
        wordLevel = in.readInt();
        frequency = in.readInt();
        path = in.readString();
        vocaType = in.readInt();
        checked = in.readByte() != 0;
        playing = in.readByte() != 0;
        index = in.readInt();
        baseRuby = in.readString();
        vocaDisplayRuby = in.readString();
        rubyIds = in.readString();
        rubyTypes = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        position = in.readInt();
        repeat = in.readInt();
        startTimeOriginal = in.readLong();
        endTimeOriginal = in.readLong();
        difficultWordsCount = in.readInt();
        langStudy = in.readInt();
        wordIds = in.readString();
        isPlayRecordOrTSS = in.readByte() != 0;
        used = in.readInt();
        meaningWords = in.readString();
        subtitleWordlistId = in.readString();
        subtitleOriginal = in.readString();
//        vocaIdServer = in.readInt();
        meaningDetailed = in.readString();
        memo = in.readString();
        hanja = in.readString();
        posAll = in.readString();
        recordedPath = in.readString();
        isListeningRecord = in.readByte() != 0;
    }

    public static final Creator<DicModel> CREATOR = new Creator<DicModel>() {
        @Override
        public DicModel createFromParcel(Parcel in) {
            return new DicModel(in);
        }

        @Override
        public DicModel[] newArray(int size) {
            return new DicModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVocaDisplay() {
        return vocaDisplay == null ? "" : vocaDisplay.trim();
    }

    public String getTextOfVocaDiaplayMeaning() {
        String vocaDisplay = getVocaDisplay();
        String meaning = getMeaning();
        String result = "";
        if (!vocaDisplay.equals("") && !meaning.equals("")) {
            result = vocaDisplay + "\n" + meaning;
        } else if (!vocaDisplay.equals("")) {
            result = vocaDisplay;
        } else if (!meaning.equals("")) {
            result = meaning;
        }
        return result;
    }
    public void setVocaDisplay(String vocaDisplay) {
        this.vocaDisplay = vocaDisplay;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getVocaIdBase() {
        return vocaIdBase;
    }

    public void setVocaIdBase(int vocaIdBase) {
        this.vocaIdBase = vocaIdBase;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public int getVocaTypeBase() {
        return vocaTypeBase;
    }

    public void setVocaTypeBase(int vocaTypeBase) {
        this.vocaTypeBase = vocaTypeBase;
    }


    @Override
    public int getAmkiId() {
        return getVocaId();
    }

    @Override
    public int getAmkiType() {
        return getVocaType();
    }

    @Override
    public int getAmkiKnow() {
        return getVocaKnow();
    }

    @Override
    public int getAmkiKnowPronounce() {
        return getVocaKnowPronounce();
    }

    @Override
    public String getAmkiEvaluationGrade() {
        return null;
    }

    @Override
    public String getAmki() {
        return getVocaDisplay();
    }

    public String getMeaning() {
        return meaning == null ? "" : meaning.trim();
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMeaningTTS() {
        return meaningTTS;
    }

    public void setMeaningTTS(String meaningTTS) {
        this.meaningTTS = meaningTTS;
    }

    public String getMeaningEng() {
        return meaningEng;
    }

    public void setMeaningEng(String meaningEng) {
        this.meaningEng = meaningEng;
    }

    public String getJmdictMeaning() {
        return jmdictMeaning;
    }

    public void setJmdictMeaning(String jmdictMeaning) {
        this.jmdictMeaning = jmdictMeaning;
    }

    public String getJmdictMeaningEng() {
        return jmdictMeaningEng;
    }

    public void setJmdictMeaningEng(String jmdictMeaningEng) {
        this.jmdictMeaningEng = jmdictMeaningEng;
    }

    public boolean isBookmark() {
        return getBookmark() > 0;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public void setBookmark(boolean isBookmark) {
        this.bookmark = isBookmark ? 1 : 0;
    }

    public void swapBookmark() {
        setBookmark(!isBookmark());
    }

    @Override
    public void swapVIBookmark() {
        swapBookmark();
    }

    public int getKnowPronounceBase() {
        return vocaKnowPronounce;
    }

    public int getVocaKnowPronounce() {
        return vocaKnowPronounce == 0 ? Constant.VOCA_KNOW.VOCA_KNOW_KNOWN : vocaKnowPronounce;
    }

    public void setVocaKnowPronounce(int vocaKnowPronounce) {
        this.vocaKnowPronounce = vocaKnowPronounce;
    }

    public int getWordLevel() {
        return wordLevel;
    }

    public void setWordLevel(int wordLevel) {
        this.wordLevel = wordLevel;
    }

    public String getVocaTTS() {
        return vocaTTS == null ? "" : vocaTTS;
    }

    public void setVocaTTS(String vocaTTS) {
        this.vocaTTS = vocaTTS;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isVocaKnow_Known() {
        return getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public boolean isVocaPronounce_Known() {
        return getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public boolean isVocaPronounce_Unknown() {
        return getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPosAll() {
        return posAll;
    }

    public void setPosAll(String posAll) {
        this.posAll = posAll;
    }

    @Override
    public String toString() {
        return "DicModel{" +
                "id=" + id +
                ", vocaDisplay='" + vocaDisplay + '\'' +
                ", vocaTTS='" + vocaTTS + '\'' +
                ", vocaId=" + vocaId +
                ", pronounce='" + pronounce + '\'' +
                ", know=" + vocaKnow +
                ", meaning='" + meaning + '\'' +
                ", meaningTTS='" + meaningTTS + '\'' +
                ", meaningEng='" + meaningEng + '\'' +
                ", jmdictMeaning='" + jmdictMeaning + '\'' +
                ", jmdictMeaningEng='" + jmdictMeaningEng + '\'' +
                ", bookmark=" + bookmark +
                ", knowPronounce=" + vocaKnowPronounce +
                ", wordLevel=" + wordLevel +
                ", frequency=" + frequency +
                ", path='" + path + '\'' +
                ", type=" + vocaType +
                ", checked=" + checked +
                ", playing=" + playing +
                ", index=" + index +
                ", ids='" + rubyIds + '\'' +
                ", types='" + rubyTypes + '\'' +
                ", starTime=" + startTime + '\'' +
                ", endTime=" + endTime + '\'' +
                ", langStudy=" + langStudy +
//                ", vocaIdServer=" + vocaIdServer +
                '}';
    }

    @Override
    public boolean isVIChecked() {
        return checked;
    }

    @Override
    public void setVIChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isVIPlaying() {
        return playing;
    }

    @Override
    public void setVIPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public boolean isVIRecording() {
        return false;
    }

    @Override
    public void setVIRecording(boolean recording) {

    }

    @Override
    public String getVIPath() {
        return path;
    }

    @Override
    public void setVIPath(String path) {
        this.path = path;
    }

    @Override
    public void setVIIndex(Integer index) {
        getIndex();
    }

    @Override
    public boolean hasVIVoiceFile() {
        if (recordedPath == null)
            return false;
        if (recordedPath.trim().equals(""))
            return false;
        return true;
    }

    @Override
    public int getVIVoiceFileVersion() {
        return 0;
    }

    @Override
    public String getPersonAB() {
        return "";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBaseRuby() {
        return baseRuby;
    }

    public void setBaseRuby(String baseRuby) {
        this.baseRuby = baseRuby;
    }

    public String getVocaDisplayRuby() {
        return vocaDisplayRuby == null ? "" : vocaDisplayRuby;
    }

    public void setVocaDisplayRuby(String vocaDisplayRuby) {
        this.vocaDisplayRuby = vocaDisplayRuby;
    }

    public String getTextDisplay(boolean isStudyLang, boolean isTongueLang) {
        String text = Constant.BASE_BLANK;
        if (isStudyLang && !Utils.isEmpty(getVocaDisplayRuby())) {
            text += getVocaDisplayRuby();
        }
        if (isTongueLang && !Utils.isEmpty(getMeaning())) {
            if (!Utils.isEmpty(text)) {
                text += Constant.RUBY.KEY.BREAK_BR_START;
            }
            text += getMeaning();
        }
        return text;
    }

    //Dalnim add
    public String getTextDisplayByLang(boolean isStudyLang, boolean isTongueLang, boolean isDisplaySutydLangFirst) {
        StringJoiner sjText = new StringJoiner(Constant.RUBY.KEY.BREAK_BR_START);
        if (isDisplaySutydLangFirst) {
            if (isStudyLang && !Utils.isEmpty(getVocaDisplayRuby())) {
                sjText.add(getVocaDisplayRuby());
            }
            if (isTongueLang && !Utils.isEmpty(getMeaning())) {
                sjText.add(getMeaning());
            }
        } else {
            if (isTongueLang && !Utils.isEmpty(getMeaning())) {
                sjText.add(getMeaning());
            }
            if (isStudyLang && !Utils.isEmpty(getVocaDisplayRuby())) {
                sjText.add(getVocaDisplayRuby());
            }
        }
        return sjText.toString();
    }

    public String getTextDisplay(boolean isShowRuby, boolean isStudyLang, boolean isTongueLang) {
        return isShowRuby ? getTextDisplay(isStudyLang, isTongueLang) : getVocaDisplay();
    }

    //Dalnim add
    public String getTextDisplayRubyByLang(boolean isShowRuby, boolean isStudyLang, boolean isTongueLang, boolean isDisplaySutydLangFirst) {
        return isShowRuby ? getTextDisplayByLang(isStudyLang, isTongueLang, isDisplaySutydLangFirst) : getVocaDisplay();
    }

    public String getRubyIds() {
        return rubyIds;
    }

    public void setRubyIds(String rubyIds) {
        this.rubyIds = rubyIds;
    }

    public String getRubyTypes() {
        return rubyTypes;
    }

    public void setRubyTypes(String rubyTypes) {
        this.rubyTypes = rubyTypes;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DicModel that = (DicModel) o;
        return Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int getRepeat() {
        return repeat;
    }

    public boolean isRepeatOne() {
        return repeat == Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.REPEAT_COUNT_ONE;
    }

    private void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public long getStartTimeOriginal() {
        return startTimeOriginal;
    }

    public void setStartTimeOriginal(long startTimeOriginal) {
        this.startTimeOriginal = startTimeOriginal;
    }

    public long getEndTimeOriginal() {
        return endTimeOriginal;
    }

    public void setEndTimeOriginal(long endTimeOriginal) {
        this.endTimeOriginal = endTimeOriginal;
    }

    public int getDifficultWordsCount() {
        return difficultWordsCount;
    }

    public void setDifficultWordsCount(int difficultWordsCount) {
        this.difficultWordsCount = difficultWordsCount;
    }

    public int getLangStudy() {
        return langStudy;
    }

    public String getLangStudyString() {
        return String.valueOf(langStudy);
    }

    public void setLangStudy(int langStudy) {
        this.langStudy = langStudy;
    }

    public String getWordsIds() {
        return wordIds;
    }

    public void setWordIds(String wordIds) {
        this.wordIds = wordIds;
    }

    public boolean isPlayRecordOrTSS() {
        return isPlayRecordOrTSS;
    }

    public void setPlayRecordOrTSS(boolean playRecordOrTSS) {
        isPlayRecordOrTSS = playRecordOrTSS;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public boolean isShowUsed() {
        return used == Constant.PLAYER.SUB_TITLE.USED.SHOW;
    }

    public String getMeaningWords() {
        return meaningWords;
    }

    public void setMeaningWords(String meaningWords) {
        this.meaningWords = meaningWords;
    }

    public String getSubtitleWordlistId() {
        return subtitleWordlistId;
    }

    public void setSubtitleWordlistId(String subtitleWordlistId) {
        this.subtitleWordlistId = subtitleWordlistId;
    }

    public String getVocaDisplay(boolean isShowRuby) {
        return isShowRuby ? getVocaDisplayRuby() : getVocaDisplay();
    }

    public boolean checkTime(long start, long end) {
        return start >= getStartTime() && end <= getEndTime();
    }

    public boolean checkTime(long start, long end, DicModel nextDicModel) {
        return start >= getStartTime() && end <= getEndTime() && start < (nextDicModel == null ? end : nextDicModel.getStartTime());
    }

    public void clearCache() {
        this.startTime = startTimeOriginal;
        this.endTime = endTimeOriginal;
        this.bookmark = Constant.INT_BOOLEAN.FASLE;
        this.used = Constant.INT_BOOLEAN.TRUE;
    }

    public String getSubtitleOriginal() {
        return subtitleOriginal;
    }

    public void setSubtitleOriginal(String subtitleOriginal) {
        this.subtitleOriginal = subtitleOriginal;
    }

//    public int getVocaIdServer() {
//        return vocaIdServer;
//    }
//
//    public void setVocaIdServer(int vocaIdServer) {
//        if (vocaIdServer == 0) {
//            vocaIdServer = -1;
//        }
//        this.vocaIdServer = vocaIdServer;
//    }

    public HashMap<Integer, RubyTextModel> getListRubyTextModel() {
        return listRubyTextModel;
    }

    public void setListRubyTextModel(HashMap<Integer, RubyTextModel> listRubyTextModel) {
        this.listRubyTextModel = listRubyTextModel;
    }

    public String getMeaningDetailed() {
        return meaningDetailed;
    }

    public void setMeaningDetailed(String meaningDetailed) {
        this.meaningDetailed = meaningDetailed;
    }

    public String getMemo() {
        return memo == null ? "" : memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getHanja() {
        return hanja == null ? "" : hanja;
    }

    public void setHanja(String hanja) {
        this.hanja = hanja;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(vocaDisplay);
        dest.writeString(vocaTTS);
        dest.writeInt(vocaId);
        dest.writeString(pronounce);
        dest.writeInt(vocaKnow);
        dest.writeString(meaning);
        dest.writeString(meaningTTS);
        dest.writeString(meaningEng);
        dest.writeString(jmdictMeaning);
        dest.writeString(jmdictMeaningEng);
        dest.writeInt(bookmark);
        dest.writeInt(vocaKnowPronounce);
        dest.writeInt(wordLevel);
        dest.writeInt(frequency);
        dest.writeString(path);
        dest.writeInt(vocaType);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (playing ? 1 : 0));
        dest.writeInt(index);
        dest.writeString(baseRuby);
        dest.writeString(vocaDisplayRuby);
        dest.writeString(rubyIds);
        dest.writeString(rubyTypes);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeInt(position);
        dest.writeInt(repeat);
        dest.writeLong(startTimeOriginal);
        dest.writeLong(endTimeOriginal);
        dest.writeInt(difficultWordsCount);
        dest.writeInt(langStudy);
        dest.writeString(wordIds);
        dest.writeByte((byte) (isPlayRecordOrTSS ? 1 : 0));
        dest.writeInt(used);
        dest.writeString(meaningWords);
        dest.writeString(subtitleWordlistId);
        dest.writeString(subtitleOriginal);
//        dest.writeInt(vocaIdServer);
        dest.writeString(meaningDetailed);
        dest.writeString(memo);
        dest.writeString(hanja);
        dest.writeString(posAll);
        dest.writeString(recordedPath);
        dest.writeByte((byte) (isListeningRecord ? 1 : 0));
    }


    @Override
    public Integer getVIId() {
        return id;
    }

    @Override
    public String getVIVoca() {
        return vocaDisplay == null || vocaDisplay.trim().length() == 0 ? "": vocaDisplay;
    }

    @Override
    public Integer getVIVocaKnow() {
        return vocaKnow;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return vocaKnowPronounce;
    }

    @Override
    public String getVIPronounce() {
        return pronounce;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguageString) {
        return meaning;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguageString) {
        return meaningDetailed;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguageString) {
        return getMeaningTTS();
    }

    @Override
    public String getVIMeaningEng() {
        return getMeaningEng();
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return "";
    }

    @Override
    public String getVIMeaningEngTts() {
        return getMeaningEng();
    }

    @Override
    public Integer getVIBookmark() {
        return bookmark;
    }

    @Override
    public boolean isVIBookmark() {
        return isBookmark();
    }

    @Override
    public void setVIId(Integer value) {
        id = value;
    }

    @Override
    public void setVIVoca(String value) {
        vocaDisplay = value;
    }

    @Override
    public String getVIVocaTTS() {
        return vocaTTS;
    }

    @Override
    public void setVIVocaTTS(String value) {
        vocaTTS = value;
    }

    @Override
    public void setVIPronounce(String value) {
        pronounce = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguageString, String value) {
        meaning = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguageString, String value) {
        meaningDetailed = value;
    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguageString, String value) {

    }

    @Override
    public void setVIMeaningEng(String value) {

    }

    @Override
    public void setVIMeaningEngDetailed(String value) {

    }

    @Override
    public void setVIMeaningEngTts(String value) {

    }

    @Override
    public void setVIVocaKnow(Integer value) {
        vocaKnow = value;
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        vocaKnowPronounce = value;
    }

    @Override
    public void setVIBookmark(Integer value) {
        bookmark = value;
    }

    @Override
    public Integer getVIVocaType() {
        return vocaType;
    }

    @Override
    public Integer getVIVocaId() {
        return vocaId;
    }

    @Override
    public void setVIVocaType(Integer value) {
        vocaType = value;
    }

    @Override
    public void setVIVocaId(Integer value) {
        vocaId = value;
    }

    @Override
    public Integer getVIIndex() {
        return index;
    }

    @Override
    public String getVIPosAll() {
        return posAll;
    }

    @Override
    public Integer getVIVocaTypeBase() {
        return vocaTypeBase;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return vocaIdBase;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {
        vocaTypeBase = value;
    }

    @Override
    public void setVIVocaIdBase(Integer value) {
        vocaIdBase = value;
    }

    public String getRecordedPath() {
        return recordedPath == null ? "" : recordedPath;
    }

    public void setRecordedPath(String recordedPath) {
        this.recordedPath = recordedPath;
    }

    public boolean isListeningRecord() {
        return isListeningRecord;
    }

    public void setListeningRecord(boolean listeningRecord) {
        isListeningRecord = listeningRecord;
    }

    @Override
    public int getVIRepeatCount() {
        return getRepeat();
    }
    @Override
    public void setVIRepeatCount(int value) {
        setRepeat(value);
    }

    @Override
    public int getVIDifficultWordsCount() {
        return getDifficultWordsCount();
    }
    @Override
    public void setVIDifficultWordsCount(int value) {
        setDifficultWordsCount(value);
    }
}
