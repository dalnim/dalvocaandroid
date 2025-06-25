package com.dalread.util.arasubtitle;

public class VO_COMMON_LANG_STUDY_CODE  {
    protected Integer LANG_STUDY;
    protected Integer LANG_STUDY_CODE;
    protected Integer LANG_DISPLAY;
    protected Integer LANG_NATIVE;
    protected Integer LANG_MEANING_CODE;

    public void setLANG_STUDY(Integer intOne) {
        this.LANG_STUDY = intOne;
        this.LANG_STUDY_CODE = intOne;
    }
    public void setLANG_NATIVE(Integer intOne) {
        this.LANG_NATIVE = intOne;
        this.LANG_MEANING_CODE = intOne;
    }
    public void setLANG_DISPLAY(Integer intOne) {
        this.LANG_DISPLAY = intOne;
        this.LANG_MEANING_CODE = intOne;
    }
//	public void setLANG_NATIVE(int intOne) {
//		this.LANG_NATIVE = intOne;
//		this.LANG_MEANING_CODE = intOne;
//	}
//	public void setLANG_STUDY(int intOne) {
//		this.LANG_STUDY = intOne;
//		this.LANG_STUDY_CODE = intOne;
//	}
}