package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.database.RealmManagerPlaylistModel;
import com.dalread.util.Constant;
import com.dalread.util.StorageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class PlaylistModel extends RealmObject implements Parcelable, IPlaylistDisplay {
    public static final String FIELD_PLAYLIST_ID = "playListId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_FILE_PATHS = "filePaths";

    public static final String FIELD_IS_SELECTED = "isSelected";
    public static final String FIELD_CATEGORY1 = "category1";
    public static final String FIELD_CATEGORY2 = "category2";
    public static final String FIELD_CATEGORY3 = "category3";
    public static final String FIELD_FAVORITE = "favorite";
    public static final String FIELD_IS_AUTO_CREATED = "isAutoCreated";
    public static final String FIELD_BOOKMARK = "bookmark";
    public static final String FIELD_UNUSED = "unused";
//    public static final String FIELD_TOTAL_SONGS = "totalSongs";


    @PrimaryKey
    private long playListId;
    private String name;
    //버전 2에서 추가
    //------
    private boolean isSelected;
    private RealmList<String> filePaths; // 릴리즈 하기 전에 이름을 filePaths로 변경
    private int category1; //나중에 영화 구분할때 사용할려고
    private int category2;
    private int category3;
    private int favorite; // 플레이 리스트에서 선호도가 높은 항목에 대해 사용할 예정
    private boolean isAutoCreated; // 나중에 플레이 리스트를 자동으로 생성한 항목에 사용할 예정
    private int bookmark = Constant.INT_BOOLEAN.FASLE;
    private int unused = 0; // 1이상이면 안쓴다.
    //------

    @Ignore
    private int totalSongs;

    public PlaylistModel() {
        filePaths = new RealmList<>();
    }

    public PlaylistModel(long playListId, String name) {
        this.playListId = playListId;
        this.name = name;
    }

    protected PlaylistModel(Parcel in) {
        playListId = in.readLong();
        name = in.readString();
        totalSongs = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(playListId);
        dest.writeString(name);
        dest.writeInt(totalSongs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaylistModel> CREATOR = new Creator<PlaylistModel>() {
        @Override
        public PlaylistModel createFromParcel(Parcel in) {
            return new PlaylistModel(in);
        }

        @Override
        public PlaylistModel[] newArray(int size) {
            return new PlaylistModel[size];
        }
    };

    public long getPlayListId() {
        return playListId;
    }

    public void setPlayListId(long playListId) {
        this.playListId = playListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public RealmList<String> getFilePaths() {
        return filePaths;
    }
    public int getFilePathCount() {
        return (filePaths != null) ? filePaths.size() : 0;
    }
    public void setFilePaths(RealmList<String> filePaths) {
        this.filePaths = filePaths;
    }

    // 영화 추가 메소드
    public void addFilePath(final String path) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            if (!filePaths.contains(path)) {
                filePaths.add(path);
            }
        });
    }

    // 영화 제거 메소드
    public void removeFilePath(final String path) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            int index = filePaths.indexOf(path);
            if (index != -1) {
                filePaths.remove(index);
            }
        });
    }
    //실제 파일이 존재하지 않는거는 filePaths에서 제거해준다.
    public void refreshFilePaths() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            List<String> pathsToRemove = new ArrayList<>();
            for (String path : filePaths) {
                if (!StorageUtil.isFileExist(path)) {
                    pathsToRemove.add(path);
                }
            }
            filePaths.removeAll(pathsToRemove);
        });
    }

    // 여러 영화 경로를 추가하는 메소드
    public void addFilePaths(final List<String> paths) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            for (String path : paths) {
                if (!filePaths.contains(path)) {
                    filePaths.add(path);
                }
            }
        });
    }

    private RealmList<String> getFilePathsFromRealm() {
        Realm realm = Realm.getDefaultInstance();
        PlaylistModel playlist = realm.where(PlaylistModel.class)
                .equalTo(FIELD_PLAYLIST_ID, this.playListId) // 현재 객체의 ID를 기준으로 검색
                .findFirst();

        return playlist != null ? playlist.getFilePaths() : null; // PlaylistModel이 null일 경우 처리
    }

    // 새 메서드: videoPaths를 Map 형태로 반환
    public Map<String, Boolean> getFilePathsAsMap() {
        RealmList<String> paths = getFilePathsFromRealm();

        Map<String, Boolean> map = new HashMap<>();
        if (paths != null) {
            for (String path : paths) {
                map.put(path, true); // 각 경로를 키로 하고, true 값을 설정
            }
        }
        return map;
    }

    // 새 메서드: videoPaths를 List 형태로 반환
    public List<String> getFilePathsAsList() {
        RealmList<String> paths = getFilePathsFromRealm();

        List<String> list = new ArrayList<>();
        if (paths != null) {
            list.addAll(paths); // RealmList를 일반 List로 변환
        }
        return list;
    }

    // 새 플레이리스트 생성 메소드
    public static PlaylistModel createNewPlaylist(String name) {
        Realm realm = Realm.getDefaultInstance();
        PlaylistModel existingPlaylist = RealmManagerPlaylistModel.getPlaylistModelByName(name);
        if (existingPlaylist != null) {
            return null;
        }
        int nextId = RealmManagerPlaylistModel.getNextPlaylistId();
        PlaylistModel newPlaylist = new PlaylistModel(nextId, name);
        realm.executeTransaction(r -> r.insert(newPlaylist));
        return newPlaylist;
    }
}
