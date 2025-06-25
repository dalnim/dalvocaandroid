package com.dalread.model;

import com.dalread.R;
import com.dalread.util.Constant;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListenComprehensionModel extends RealmObject {
    @PrimaryKey
    private long id;
    private int type;
    private int index;
    private int count; //Don't use count any more, it's always 1 now.

    public ListenComprehensionModel() {
        this(System.currentTimeMillis(), 0, 0, 1);
    }

    public ListenComprehensionModel(long id, int type, int index, int count) {
        this.id = id;
        this.type = type;
        this.index = index;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTitle() {
        switch (type) {
            case Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE:
                return R.string.listen_comprehension_hide_the_subtitle;
            case Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_THE_SUBTITLE:
                return R.string.listen_comprehension_show_the_subtitle;
            default:
                return R.string.listen_comprehension_show_difficult_words_only;
        }
    }

    public boolean isHideTheSubtitle() {
        return type == Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE;
    }

    @Override
    public String toString() {
        return "ListenComprehensionModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", index=" + index +
                ", count=" + count +
                '}';
    }
}
