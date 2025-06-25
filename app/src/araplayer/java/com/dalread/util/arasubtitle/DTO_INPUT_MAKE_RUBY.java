package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO_INPUT_MAKE_RUBY extends DTO_INPUT_COMMON_LESSON {
    protected String comment = ""; //DalRead App이 이걸로 부른다. 나중에는 INPUT_TEXT로 쓰게 바꿀것
    protected String INPUT_TEXT = "";

    protected List<String> LIST_INPUT_TEXT = new ArrayList<String>();

    //입력된 자막을 번역할수 있으면 한다.
    protected Integer TRANSLATE_INPUT_TEXT = Constants.IS_NO;

    //이미 NLP 분석한건 다시 분석하지 않는다.한번분석한건 다시 분석안하고 또 더중요한것은 분석이 잘못된것은 손으로 수정해놓은것을 쓸려고.
    //자막 + 미리분석한 자막에 있는 단어들
//    protected Map<String, List<DTO_VOCA_POS>> MAP_VOCA_ALREADY_NLP_PARSED = new HashMap<String, List<DTO_VOCA_POS>>();

    Map<String, List<String>> MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG = new HashMap<String, List<String>>();

    protected Integer RUBY_OUTPUT_TYPE = Constants.RUBY_OUTPUT_TYPE_EPUB;

    protected String EPUBFILENAMETOBE = "";
    protected String EPUB_FILE_NAME_TO_BE = "";

    protected String WithFixedName = "";
    protected String WITH_FIXED_NAME = "";

    protected Integer USE_SERVER_TRANSLATTION = Constants.IS_NO;

    protected String CLIENT_TYPE = "";

    protected String appName = "";
    protected String APP_NAME = "";

    public void setappName(String strOne) {
        this.appName = strOne.trim();
        this.APP_NAME = strOne.trim();
    }

    public void setcomment(String value) {
        comment = (value == null) ? "" : value.trim();
        setINPUT_TEXT(comment);
    }

    public void setEPUBFILENAMETOBE(String value) {
        EPUBFILENAMETOBE = (value == null) ? "" : value.trim();
        EPUB_FILE_NAME_TO_BE = EPUBFILENAMETOBE;
    }
    public void setWithFixedName(String value) {
        WithFixedName = (value == null) ? "" : value.trim();
        WITH_FIXED_NAME = WithFixedName;
    }
    public void setINPUT_TEXT(String value) {
        INPUT_TEXT = (value == null) ? "" : value.trim();
        LIST_INPUT_TEXT.add(INPUT_TEXT);
        MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG.put(Constants.KEY_FOR_TEXT_TO_EXTRACT, LIST_INPUT_TEXT); //key가 파일명인데 DalRead, OCR앱등은 key를 epub파일명으로 translateInputFileDTO에서 넣고 다시 업데이트 한다.(Web에서 부를때는 dalnim이라는 키를 넣어주어야 한다.
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getINPUT_TEXT() {
        return INPUT_TEXT;
    }

    public List<String> getLIST_INPUT_TEXT() {
        return LIST_INPUT_TEXT;
    }

    public void setLIST_INPUT_TEXT(List<String> LIST_INPUT_TEXT) {
        this.LIST_INPUT_TEXT = LIST_INPUT_TEXT;
    }

    public Integer getTRANSLATE_INPUT_TEXT() {
        return TRANSLATE_INPUT_TEXT;
    }

    public void setTRANSLATE_INPUT_TEXT(Integer TRANSLATE_INPUT_TEXT) {
        this.TRANSLATE_INPUT_TEXT = TRANSLATE_INPUT_TEXT;
    }

    public Map<String, List<String>> getMAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG() {
        return MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG;
    }

    public void setMAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG(Map<String, List<String>> MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG) {
        this.MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG = MAP_ALL_INPUT_TEXT_WITHOUT_HTML_TAG;
    }

    public Integer getRUBY_OUTPUT_TYPE() {
        return RUBY_OUTPUT_TYPE;
    }

    public void setRUBY_OUTPUT_TYPE(Integer RUBY_OUTPUT_TYPE) {
        this.RUBY_OUTPUT_TYPE = RUBY_OUTPUT_TYPE;
    }

    public String getEPUBFILENAMETOBE() {
        return EPUBFILENAMETOBE;
    }

    public String getEPUB_FILE_NAME_TO_BE() {
        return EPUB_FILE_NAME_TO_BE;
    }

    public void setEPUB_FILE_NAME_TO_BE(String EPUB_FILE_NAME_TO_BE) {
        this.EPUB_FILE_NAME_TO_BE = EPUB_FILE_NAME_TO_BE;
    }

    public String getWithFixedName() {
        return WithFixedName;
    }

    public String getWITH_FIXED_NAME() {
        return WITH_FIXED_NAME;
    }

    public void setWITH_FIXED_NAME(String WITH_FIXED_NAME) {
        this.WITH_FIXED_NAME = WITH_FIXED_NAME;
    }

    public Integer getUSE_SERVER_TRANSLATTION() {
        return USE_SERVER_TRANSLATTION;
    }

    public void setUSE_SERVER_TRANSLATTION(Integer USE_SERVER_TRANSLATTION) {
        this.USE_SERVER_TRANSLATTION = USE_SERVER_TRANSLATTION;
    }

    public String getCLIENT_TYPE() {
        return CLIENT_TYPE;
    }

    public void setCLIENT_TYPE(String CLIENT_TYPE) {
        this.CLIENT_TYPE = CLIENT_TYPE;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAPP_NAME() {
        return APP_NAME;
    }

    public void setAPP_NAME(String APP_NAME) {
        this.APP_NAME = APP_NAME;
    }
//	@Getter(AccessLevel.NONE)
//	protected String comment = "";
//	protected String COMMENT = "";

//	public void setcomment(String value) {
//		comment = (value == null) ? "" : value.trim();
//		COMMENT = comment;
//		LIST_COMMENT.add(comment);
//		MAP_ALL_TEXT_WITHOUT_HTML_TAG.put("", LIST_COMMENT);
//	}
//
//	public void setCOMMENT(String value) {
//		COMMENT = (value == null) ? "" : value.trim();
//		LIST_COMMENT.add(COMMENT);
//		MAP_ALL_TEXT_WITHOUT_HTML_TAG.put("", LIST_COMMENT);
//	}
}
