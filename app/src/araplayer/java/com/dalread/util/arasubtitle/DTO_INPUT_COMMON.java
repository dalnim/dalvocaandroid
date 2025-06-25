package com.dalread.util.arasubtitle;

public class DTO_INPUT_COMMON  {
    protected Integer UID = Constants.UID_DEFAULT;

    protected String USER_NAME = "";

    protected String langDisplay = Constants.LANG_KO;
    protected String studyLang = Constants.LANG_EN;
    protected Integer studyLangCode = Constants.LANGCODE_EN;
    protected Integer langDisplayCode = Constants.LANGCODE_KO;


    protected String LANG_STUDY = Constants.LANG_EN;
    protected String LANG_MEANING = Constants.LANG_KO;
    protected Integer LANG_STUDY_CODE = Constants.LANGCODE_EN;
    protected Integer LANG_MEANING_CODE = Constants.LANGCODE_KO;
    protected Integer LANG_MENU_DIPSPLAY_CODE = Constants.LANGCODE_NONE;

    public void setUID(Integer value) {
        UID = (value == null) ? Constants.UID_DEFAULT : value;
    }
    public void setLangDisplay(String strOne) {
        this.langDisplay = strOne;
        this.langDisplayCode = convertLanguageNameToCode(strOne);
        this.LANG_MEANING = this.langDisplay;
        this.LANG_MEANING_CODE = this.langDisplayCode;

    }
    public void setStudyLang(String strOne) {
        this.studyLang = strOne;
        this.studyLangCode = convertLanguageNameToCode(strOne);
        this.LANG_STUDY = this.studyLang;
        this.LANG_STUDY_CODE = this.studyLangCode;
    }
    public void setLANG_STUDY(String strOne) {
        this.LANG_STUDY = strOne;
        this.LANG_STUDY_CODE = convertLanguageNameToCode(strOne);
    }
    public void setLANG_MEANING(String strOne) {
        this.LANG_MEANING = strOne;
        this.LANG_MEANING_CODE = convertLanguageNameToCode(strOne);
        this.LANG_MENU_DIPSPLAY_CODE = this.LANG_MEANING_CODE;
    }

    public void setLANG_STUDY_CODE(Integer value) {
        this.LANG_STUDY_CODE = value;
        this.LANG_STUDY = convertCodeToLanguageName(value);
        this.studyLang = this.LANG_STUDY;
    }

    public void setLANG_MEANING_CODE(Integer value) {
        this.LANG_MEANING_CODE = value;
        this.LANG_MENU_DIPSPLAY_CODE = value;

        this.LANG_MEANING = convertCodeToLanguageName(value);
        this.langDisplay = this.LANG_MEANING;
    }

    public String convertCodeToLanguageName(Integer value) {
        String result = Constants.LANG_EN;
        if (value == Constants.LANGCODE_AR) {
            result =Constants.LANG_AR;
        } else if (value == Constants.LANGCODE_BN) {
            result =Constants.LANG_BN;
        } else if (value == Constants.LANGCODE_CH_S) {
            result = Constants.LANG_CH_S;
        } else if (value == Constants.LANGCODE_CH_T) {
            result =Constants.LANG_CH_T;
        } else if (value == Constants.LANGCODE_CS) {
            result =Constants.LANG_CS;
        } else if (value == Constants.LANGCODE_DA) {
            result =Constants.LANG_DA;
        } else if (value == Constants.LANGCODE_DE) {
            result =Constants.LANG_DE;
        } else if (value == Constants.LANGCODE_EL) {
            result =Constants.LANG_EL;
        } else if (value == Constants.LANGCODE_EN) {
            result =Constants.LANG_EN;
        } else if (value == Constants.LANGCODE_ES) {
            result =Constants.LANG_ES;
        } else if (value == Constants.LANGCODE_FI) {
            result =Constants.LANG_FI;
        } else if (value == Constants.LANGCODE_FR) {
            result =Constants.LANG_FR;
        } else if (value == Constants.LANGCODE_HE) {
            result =Constants.LANG_HE;
        } else if (value == Constants.LANGCODE_HI) {
            result =Constants.LANG_HI;
        } else if (value == Constants.LANGCODE_HR) {
            result =Constants.LANG_HR;
        } else if (value == Constants.LANGCODE_HU) {
            result =Constants.LANG_HU;
        } else if (value == Constants.LANGCODE_ID) {
            result =Constants.LANG_ID;
        } else if (value == Constants.LANGCODE_IT) {
            result =Constants.LANG_IT;
        } else if (value == Constants.LANGCODE_JP) {
            result =Constants.LANG_JP;
        } else if (value == Constants.LANGCODE_KO) {
            result =Constants.LANG_KO;
        } else if (value == Constants.LANGCODE_NL) {
            result =Constants.LANG_NL;
        } else if (value == Constants.LANGCODE_NO) {
            result =Constants.LANG_NO;
        } else if (value == Constants.LANGCODE_PL) {
            result =Constants.LANG_PL;
        } else if (value == Constants.LANGCODE_PT) {
            result =Constants.LANG_PT;
        } else if (value == Constants.LANGCODE_RO) {
            result =Constants.LANG_RO;
        } else if (value == Constants.LANGCODE_RU) {
            result =Constants.LANG_RU;
        } else if (value == Constants.LANGCODE_SK) {
            result =Constants.LANG_SK;
        } else if (value == Constants.LANGCODE_SV) {
            result =Constants.LANG_SV;
        } else if (value == Constants.LANGCODE_TH) {
            result =Constants.LANG_TH;
        } else if (value == Constants.LANGCODE_TR) {
            result =Constants.LANG_TR;
        } else if (value == Constants.LANGCODE_UK) {
            result =Constants.LANG_UK;
        } else if (value == Constants.LANGCODE_VI) {
            result =Constants.LANG_VI;
        } else if (value == Constants.LANGCODE_HANJA) {
            result =Constants.LANG_HANJA;
        }
        return result;
    }
    public Integer convertLanguageNameToCode(String value) {
        int result = Constants.LANGCODE_EN;
        try {
            if (value.equals(Constants.LANG_AR)) {
                result = Constants.LANGCODE_AR;
            } else if (value.equals(Constants.LANG_BN)) {
                result = Constants.LANGCODE_BN;
            } else if (value.equals(Constants.LANG_CH_S)) {
                result = Constants.LANGCODE_CH_S;
            } else if (value.equals(Constants.LANG_CH_T)) {
                result = Constants.LANGCODE_CH_T;
            } else if (value.equals(Constants.LANG_CS)) {
                result = Constants.LANGCODE_CS;
            } else if (value.equals(Constants.LANG_DA)) {
                result = Constants.LANGCODE_DA;
            } else if (value.equals(Constants.LANG_DE)) {
                result = Constants.LANGCODE_DE;
            } else if (value.equals(Constants.LANG_EL)) {
                result = Constants.LANGCODE_EL;
            } else if (value.equals(Constants.LANG_EN)) {
                result = Constants.LANGCODE_EN;
            } else if (value.equals(Constants.LANG_ES)) {
                result = Constants.LANGCODE_ES;
            } else if (value.equals(Constants.LANG_FI)) {
                result = Constants.LANGCODE_FI;
            } else if (value.equals(Constants.LANG_FR)) {
                result = Constants.LANGCODE_FR;
            } else if (value.equals(Constants.LANG_HE)) {
                result = Constants.LANGCODE_HE;
            } else if (value.equals(Constants.LANG_HI)) {
                result = Constants.LANGCODE_HI;
            } else if (value.equals(Constants.LANG_HR)) {
                result = Constants.LANGCODE_HR;
            } else if (value.equals(Constants.LANG_HU)) {
                result = Constants.LANGCODE_HU;
            } else if (value.equals(Constants.LANG_ID)) {
                result = Constants.LANGCODE_ID;
            } else if (value.equals(Constants.LANG_IT)) {
                result = Constants.LANGCODE_IT;
            } else if (value.equals(Constants.LANG_JP)) {
                result = Constants.LANGCODE_JP;
            } else if (value.equals(Constants.LANG_KO)) {
                result = Constants.LANGCODE_KO;
            } else if (value.equals(Constants.LANG_NL)) {
                result = Constants.LANGCODE_NL;
            } else if (value.equals(Constants.LANG_NO)) {
                result = Constants.LANGCODE_NO;
            } else if (value.equals(Constants.LANG_PL)) {
                result = Constants.LANGCODE_PL;
            } else if (value.equals(Constants.LANG_PT)) {
                result = Constants.LANGCODE_PT;
            } else if (value.equals(Constants.LANG_RO)) {
                result = Constants.LANGCODE_RO;
            } else if (value.equals(Constants.LANG_RU)) {
                result = Constants.LANGCODE_RU;
            } else if (value.equals(Constants.LANG_SK)) {
                result = Constants.LANGCODE_SK;
            } else if (value.equals(Constants.LANG_SV)) {
                result = Constants.LANGCODE_SV;
            } else if (value.equals(Constants.LANG_TH)) {
                result = Constants.LANGCODE_TH;
            } else if (value.equals(Constants.LANG_TR)) {
                result = Constants.LANGCODE_TR;
            } else if (value.equals(Constants.LANG_UK)) {
                result = Constants.LANGCODE_UK;
            } else if (value.equals(Constants.LANG_VI)) {
                result = Constants.LANGCODE_VI;
            } else if (value.equals(Constants.LANG_HANJA)) {
                result = Constants.LANGCODE_HANJA;
            }







        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer getUID() {
        return UID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getLangDisplay() {
        return langDisplay;
    }

    public String getStudyLang() {
        return studyLang;
    }

    public Integer getStudyLangCode() {
        return studyLangCode;
    }

    public void setStudyLangCode(Integer studyLangCode) {
        this.studyLangCode = studyLangCode;
    }

    public Integer getLangDisplayCode() {
        return langDisplayCode;
    }

    public void setLangDisplayCode(Integer langDisplayCode) {
        this.langDisplayCode = langDisplayCode;
    }

    public String getLANG_STUDY() {
        return LANG_STUDY;
    }

    public String getLANG_MEANING() {
        return LANG_MEANING;
    }

    public Integer getLANG_STUDY_CODE() {
        return LANG_STUDY_CODE;
    }

    public Integer getLANG_MEANING_CODE() {
        return LANG_MEANING_CODE;
    }

    public Integer getLANG_MENU_DIPSPLAY_CODE() {
        return LANG_MENU_DIPSPLAY_CODE;
    }

    public void setLANG_MENU_DIPSPLAY_CODE(Integer LANG_MENU_DIPSPLAY_CODE) {
        this.LANG_MENU_DIPSPLAY_CODE = LANG_MENU_DIPSPLAY_CODE;
    }
}