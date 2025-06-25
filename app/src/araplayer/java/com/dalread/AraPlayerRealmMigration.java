package com.dalread;

import com.dalread.model.PlaylistModel;
import com.dalread.model.VideoModel;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class AraPlayerRealmMigration  implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion < 2) {
            // VideoModel에 unused 필드를 추가합니다.
            RealmObjectSchema videoModelSchema = schema.get(VideoModel.class.getSimpleName());
            if (videoModelSchema != null) {
                videoModelSchema.addField(VideoModel.FIELD_UNUSED, int.class);
                videoModelSchema.addField(VideoModel.FIELD_FAVORITE, int.class);
            }

            // PlaylistModel의 구조를 변경합니다.
            RealmObjectSchema playlistModelSchema = schema.get(PlaylistModel.class.getSimpleName());
            if (playlistModelSchema != null) {
                if (playlistModelSchema != null) {
                    playlistModelSchema
                            //FIELD_PLAYLIST_ID는 그대로 쓰자. 귀찮다.
//                            .removeField(PlaylistModel.FIELD_PLAYLIST_ID) // 기존 PrimaryKey 필드를 삭제합니다.
//                            .addField(PlaylistModel.FIELD_ID, int.class, FieldAttribute.PRIMARY_KEY) // 새로운 PrimaryKey 필드를 추가합니다.
                            .addField(PlaylistModel.FIELD_IS_SELECTED, boolean.class)
                            .addRealmListField(PlaylistModel.FIELD_FILE_PATHS, String.class)
                            .addField(PlaylistModel.FIELD_CATEGORY1, int.class)
                            .addField(PlaylistModel.FIELD_CATEGORY2, int.class)
                            .addField(PlaylistModel.FIELD_CATEGORY3, int.class)
                            .addField(PlaylistModel.FIELD_FAVORITE, int.class)
                            .addField(PlaylistModel.FIELD_IS_AUTO_CREATED, boolean.class)
                            .addField(PlaylistModel.FIELD_BOOKMARK, int.class)
                            .addField(PlaylistModel.FIELD_UNUSED, int.class);
                }
            }
            oldVersion++; //차기 버전이 나올때 사용하기 위해서 만들어둠.
        }
    }
}