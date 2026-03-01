# AB 루프 저장 로직: 맥 vs 안드로이드 비교

맥 `desktop_multi_player/mac_multi_player` 의 AB 저장 흐름을 기준으로 안드로이드에 있던 차이와 반영한 수정을 정리했다.

---

## 맥 쪽 AB 저장 흐름 (요약)

- **진입점**: `VideoGridViewModel.updateScreenABLoopOnly(screenId, videoPath, abLoopCollection)`
  - AB 생성/수정/삭제 시 `abLoopCollections` 변경에 따라 호출된다.

1. **current_screens**  
   `screenStateRepository.updateABLoop(screenId, abLoopCollection)`  
   → `UPDATE current_screens SET ab_loop_json=?, use_ab=? WHERE screen_id=?`

2. **video_meta**  
   - 루프 있음: `playlistStore.updateABLoopCollection(for: videoPath, collection)`  
     → `VideoMetaRepository.updateABLoopCollection`  
     → `UPDATE video_meta SET ab_loop_json=?, use_ab=? WHERE file_path=?`
   - 루프 없음: `playlistStore.clearABLoopCollection(for: videoPath)`  
     → 동일 테이블에 빈 컬렉션으로 업데이트

3. **screens_in_stored_layout** (저장된 레이아웃에서 로드된 경우만)  
   `if let layoutId = loadedLayoutId`  
   → `storedLayoutRepository.updateScreenABLoop(layoutId, screenId, abLoopCollection)`  
   → `UPDATE screens_in_stored_layout SET ab_loop_json=?, use_ab=? WHERE layout_id=? AND screen_id=?`

---

## 안드로이드에서 맥과 비교해 빠져 있던 부분

| 항목 | 맥 | 안드로이드 (수정 전) | 비고 |
|------|----|----------------------|------|
| current_screens 갱신 | ✅ updateABLoop(screenId, …) | ✅ updateABRepeat → updateTableById(CURRENT_SCREENS, …) | 동일 |
| video_meta 갱신 | ✅ updateABLoopCollection / clearABLoopCollection | ✅ updateTableByFilePath(VIDEO_META, …) | 동일 |
| **screens_in_stored_layout 갱신** | ✅ loadedLayoutId 있을 때 updateScreenABLoop(layoutId, screenId, …) | ❌ **없음** | 여기만 누락 |
| **loadedLayoutId 보관** | ✅ ViewModel에 loadedLayoutId 유지 | ❌ **없음** | 저장된 레이아웃 로드 시 id 미전달·미보관 |
| 저장된 레이아웃 로드 시 layout_id 전달 | (앱 내부에서 유지) | ❌ Intent에 layout id 미전달 | 결과에 id 추가 필요 |

즉, **“저장된 레이아웃에서 로드된 상태에서 AB를 바꿀 때, 그 레이아웃의 screens_in_stored_layout도 함께 갱신”** 하는 부분이 안드로이드에만 없었다.

---

## 안드로이드에 반영한 수정

1. **MultiPlayerDatabase**
   - `updateABRepeat(MultiPlayerVideoModel model, Integer loadedLayoutId)` 추가.
   - `loadedLayoutId != null` 이면  
     `updateScreenABLoopInStoredLayout(layoutId, screenId, ab_loop_json, use_ab)` 호출  
     → `UPDATE screens_in_stored_layout SET ab_loop_json=?, use_ab=? WHERE layout_id=? AND screen_id=?`
   - 기존 `updateABRepeat(model)` 는 `updateABRepeat(model, null)` 로 위임 (호환 유지).

2. **MultiplePlayerActivity**
   - `loadedStoredLayoutId` (Integer) 필드 추가.
   - 저장된 레이아웃 로드 결과 수신 시  
     `KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID` 로 layout id를 받아 `loadedStoredLayoutId` 에 저장.
   - 다음 경우에 `loadedStoredLayoutId = null` 로 초기화:
     - 비디오 선택(파일/멀티 선택)으로 그리드 갱신 시 (`updateVideosAllFragments`),
     - “전체 닫기” 시.
   - `getLoadedStoredLayoutId()` 로 프래그먼트에 전달.

3. **MultiPlayerStoredVideosActivity**
   - 저장된 레이아웃 로드 후 결과 Intent에  
     `KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID` 로 `list.get(0).getSTORED_ID()` 추가.

4. **MultiplePlayerFragment**
   - `updateABRepeatInDb()` 에서  
     `multiPlayerDatabase.updateABRepeat(model, activity.getLoadedStoredLayoutId())` 호출하도록 변경.

이제 AB 저장 시 맥과 동일하게 다음 세 곳이 갱신된다.

- **current_screens** (해당 스크린)
- **video_meta** (해당 file_path)
- **screens_in_stored_layout** (저장된 레이아웃에서 로드된 경우에만, 해당 layout_id + screen_id)

---

## 참고: 맥 코드 위치

- `AraMultiPlayer/Core/ViewModel/VideoGridViewModel.swift`  
  - `updateScreenABLoopOnly`, `saveScreenStateToDB`
- `AraMultiPlayer/Core/Database/PlayerScreenRepository.swift`  
  - `updateABLoop(screenId:, abLoopCollection:)`
- `AraMultiPlayer/Core/Database/VideoMetaRepository.swift`  
  - `updateABLoopCollection(for:path, collection:)`, `clearABLoopCollection(for:)`
- `AraMultiPlayer/Core/Database/StoredLayoutRepository.swift`  
  - `updateScreenABLoop(layoutId:, screenId:, abLoopCollection:)`
