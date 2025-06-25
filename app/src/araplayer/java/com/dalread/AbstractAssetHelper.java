package com.dalread;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;

public abstract class AbstractAssetHelper {
    protected int currentDicDbVer = 0; //사전 SQLite db
    protected int newDicDbVer = 1; //newDicDbVer는 최소 1이어야 한다. AbstractAssetHelper을 상속받는 클래스마다 값이 다르다. 여기 말고 상속받는 클래스의 setNewDicDbVer에서 이 값을 올려주면 기존 DB의 유저데이타는 백업받고 새로운 DB로 갈아치운다.
    protected final String TAG = "AbstractAssetHelper";
    protected SharedPreferencesDB mSharedPref;
    protected Context context;
    protected String dicZipDbFileNameInAsset; //Asset에 있는 압축한 사전 sqlite파일
    protected String dicZipDbDestPathWithFileName; //압축파일을 대상 폴더에 복사할때 사용하는이름 압축해제하면 안쓴다.
    protected String dicDbDestPathWithFileName; //압축해제된 사전 DB

    public AbstractAssetHelper(SharedPreferencesDB mSharedPref, Context context) {
        this.context = context;
        this.mSharedPref = mSharedPref;
        setNewDicDbVer();
        getStoreDbVer();
        initFileNames();
    }
    private void getStoreDbVer() {
        currentDicDbVer = mSharedPref.getDatabaseVersion();
    }

    protected void incrementAndStoreDbVer() {
        //그냥 currentDicDbVer+1을 하면, newDicDbVer이 5이고, 처음 설치하는 유저는 앱을 실행할때마다 currentDicDbVer이 0에서 5가 될때까지 계속 DB를 복사해야 한다.
        mSharedPref.setDatabaseVersion(newDicDbVer);
    }
    protected abstract void setNewDicDbVer();
    protected abstract void initFileNames();
    protected abstract void copyAsset(boolean isOverwrite);

    protected abstract void restoreUserDataAndCopyAsset();
    protected abstract void removeOldFile();
    public void manageAsset() {
        if (currentDicDbVer != newDicDbVer) {
            //최초 설치시나, DB버전이 올라가면 기존 유저가 Bookmark등을 가지고 있을때 복원한다. (restoreUserDataAndCopyAsset에서 sqlite가 없으면 최초 설치로 본다)
            restoreUserDataAndCopyAsset();
        }
        //원래는 새로운 DB가 들어왔을때만 버전을 올려둬야 하지만, 그냥 할때마다 새로운 버전을 저장한다.(코드 중복 줄이기 위해)
        incrementAndStoreDbVer();
    }
}
