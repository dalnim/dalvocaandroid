package com.dalread.util.arasubtitle;

public class WordMorpheme {

//    private static final int META_DATA_SIZE = 4;

    private final String word;
    private String wordBaseForm = "";
    private String pronounciation = "";			//발음이 여러개 있을때는 대표로 한개만 한다.
    private String pronounciationFromDic = "";			//사전에 발음이 있고 형태소분석기의 발음과 다를 경우에 사전의 것을 적재
    private String pronounciationOfWordBaseForm = "";
    private String posAll;
    private String partOfSpeechLevel1 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel1Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String partOfSpeechLevel2 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel2Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String partOfSpeechLevel3 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel3Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String partOfSpeechLevel4 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel4Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String partOfSpeechLevel5 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel5Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String partOfSpeechLevel6 = "";			//각 형태소 분석기에서 나온 POS명칭을 통일시킨 명칭
    private String partOfSpeechLevel6Ori = "";		//각 형태소 분석기에서 나온 POS명칭 그대로
    private String meaning = "";
    private String wordWithConjugation = "";		//일본어의 경우에는 활용형도 같이 저장한다. (예... 行きました)
    private int position = 0;
    private boolean known = true;
    private int frequency = 1; 							//단어가 나타난 빈도.

    public WordMorpheme(String word, String pronounciation, String wordBaseForm, String posAll, String partOfSpeech1) {
        this.word = word;
        if (!pronounciation.equals("*")) {
            this.pronounciation = pronounciation;
        }
        this.wordBaseForm = wordBaseForm;
        this.posAll = posAll;
        this.partOfSpeechLevel1 = partOfSpeech1;
        this.partOfSpeechLevel1Ori = partOfSpeech1;
    }

    public String getWordBaseForm() {
        return wordBaseForm;
    }

    public void setWordBaseForm(String wordBaseForm) {
        this.wordBaseForm = wordBaseForm;
    }

    public String getPronounciation() {
        return pronounciation;
    }

    public void setPronounciation(String pronounciation) {
        this.pronounciation = pronounciation;
    }

    public String getPronounciationFromDic() {
        return pronounciationFromDic;
    }

    public void setPronounciationFromDic(String pronounciationFromDic) {
        this.pronounciationFromDic = pronounciationFromDic;
    }

    public String getPronounciationOfWordBaseForm() {
        return pronounciationOfWordBaseForm;
    }

    public void setPronounciationOfWordBaseForm(String pronounciationOfWordBaseForm) {
        this.pronounciationOfWordBaseForm = pronounciationOfWordBaseForm;
    }

    public String getPartOfSpeechLevel1() {
        return partOfSpeechLevel1;
    }

    public String getPartOfSpeechLevel1Ori() {
        return partOfSpeechLevel1Ori;
    }

    public void setPartOfSpeechLevel1Ori(String partOfSpeechLevel1Ori) {
        this.partOfSpeechLevel1Ori = partOfSpeechLevel1Ori;
        this.partOfSpeechLevel1 = partOfSpeechLevel1Ori;
    }

    public String getPartOfSpeechLevel2() {
        return partOfSpeechLevel2;
    }

//	public void setPartOfSpeechLevel2(String partOfSpeechLevel2) {
//		this.partOfSpeechLevel2 = partOfSpeechLevel2;
//	}

    public String getPartOfSpeechLevel2Ori() {
        return partOfSpeechLevel2Ori;
    }

    public void setPartOfSpeechLevel2Ori(String partOfSpeechLevel2Ori) {
        this.partOfSpeechLevel2Ori = partOfSpeechLevel2Ori;
        this.partOfSpeechLevel2 = partOfSpeechLevel2Ori;
    }

    public String getPartOfSpeechLevel3() {
        return partOfSpeechLevel3;
    }

//	public void setPartOfSpeechLevel3(String partOfSpeechLevel3) {
//		this.partOfSpeechLevel3 = partOfSpeechLevel3;
//	}

    public String getPartOfSpeechLevel3Ori() {
        return partOfSpeechLevel3Ori;
    }

    public void setPartOfSpeechLevel3Ori(String partOfSpeechLevel3Ori) {
        this.partOfSpeechLevel3Ori = partOfSpeechLevel3Ori;
        this.partOfSpeechLevel3 = partOfSpeechLevel3Ori;
    }

    public String getPartOfSpeechLevel4() {
        return partOfSpeechLevel4;
    }

    public String getPartOfSpeechLevel4Ori() {
        return partOfSpeechLevel4Ori;
    }

    public void setPartOfSpeechLevel4Ori(String partOfSpeechLevel4Ori) {
        this.partOfSpeechLevel4Ori = partOfSpeechLevel4Ori;
        this.partOfSpeechLevel4 = partOfSpeechLevel4Ori;
    }

    public String getPartOfSpeechLevel5() {
        return partOfSpeechLevel5;
    }

    public String getPartOfSpeechLevel5Ori() {
        return partOfSpeechLevel5Ori;
    }

    public void setPartOfSpeechLevel5Ori(String partOfSpeechLevel5Ori) {
        this.partOfSpeechLevel5Ori = partOfSpeechLevel5Ori;
        this.partOfSpeechLevel5 = partOfSpeechLevel5Ori;
    }

    public String getPartOfSpeechLevel6() {
        return partOfSpeechLevel6;
    }

    public String getPartOfSpeechLevel6Ori() {
        return partOfSpeechLevel4Ori;
    }

    public void setPartOfSpeechLevel6Ori(String partOfSpeechLevel6Ori) {
        this.partOfSpeechLevel6Ori = partOfSpeechLevel6Ori;
        this.partOfSpeechLevel6 = partOfSpeechLevel6Ori;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setWordWithConjugation(String wordWithConjugation) {
        this.wordWithConjugation = wordWithConjugation;
    }

    public String getWordWithConjugation() {
        return this.wordWithConjugation;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public String getWord() {
        return word;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increaseFrequency() {
        frequency = frequency + 1;
    }
    @Override
    public String toString() {
        return "wordMorpheme{" +
                "word='" + word + '\'' +
                ", wordBaseForm='" + wordBaseForm + '\'' +
                ", pronounciation='" + pronounciation + '\'' +
                ", posAll='" + posAll + '\'' +
                '}';
    }

    public String getAllPOS() {
        String strAllPOS= partOfSpeechLevel1Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel2Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel3Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel4Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel5Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel6Ori;
        if (strAllPOS.equals("_____")) {
            strAllPOS = "";
        }
        return strAllPOS;
    }

    public String getAllPOSWithBaseForm() {
        return partOfSpeechLevel1Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel2Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel3Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel4Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + partOfSpeechLevel5Ori + Constants.POS_SEPERATOR_UNDERSCORE
                + "基本形";

    }
}
