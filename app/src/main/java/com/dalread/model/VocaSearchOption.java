package com.dalread.model;

public class VocaSearchOption {

    private String text;
    private String where;
    private int useRegex;
    private int bookId;
    private int vocaType;
    private int startNo;
    private int countRecord;
    private boolean startWith;
    private boolean endWith;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public int getUseRegex() {
        return useRegex;
    }

    public void setUseRegex(int useRegex) {
        this.useRegex = useRegex;
    }

    public String getBookId() {
        return String.valueOf(bookId);
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public int getStartNo() {
        return startNo;
    }

    public void setStartNo(int startNo) {
        this.startNo = startNo;
    }

    public int getCountRecord() {
        return countRecord;
    }

    public void setCountRecord(int countRecord) {
        this.countRecord = countRecord;
    }

    public boolean isStartWith() {
        return startWith;
    }

    public void setStartWith(boolean startWith) {
        this.startWith = startWith;
    }

    public boolean isEndWith() {
        return endWith;
    }

    public void setEndWith(boolean endWith) {
        this.endWith = endWith;
    }

    public boolean useLike() {
        return text != null && (text.contains("?") || text.contains("*"));
    }
}
