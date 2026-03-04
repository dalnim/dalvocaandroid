package com.dalread.model;

/**
 * 플레이리스트 행 표시용 공통 인터페이스.
 * Realm PlaylistModel과 SQLite MultiPlayerPlaylistModel 둘 다 구현하여
 * 메인(Realm)과 멀티플레이어(SQLite)에서 동일한 어댑터를 사용할 수 있게 함.
 */
public interface IPlaylistDisplay {
    long getPlayListId();
    String getName();
    int getFilePathCount();
}
