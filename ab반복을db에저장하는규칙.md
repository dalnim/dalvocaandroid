# AB 반복을 DB에 저장하는 규칙

멀티플레이어에서 AB 반복(ab_loop_json)을 current_screens, video_meta, screens_in_stored_layout에 어떻게 반영하는지 정리한 규칙이다. 맥/아이폰과 동일하게 적용한다.

---

## 1. AB 생성/수정/삭제 시 (current_screens에서)

- **current_screens**: 해당 스크린 행만 갱신한다. (다른 스크린은 건드리지 않는다.)
- **video_meta**: 항상 해당 파일 경로(file_path) 행의 ab_loop_json(및 use_ab)을 업데이트한다.
- **screens_in_stored_layout**:  
  현재 그리드가 **저장된 레이아웃에서 로드된 상태**인 경우(loadedLayoutId != null)에만, 해당 레이아웃의 해당 스크린 행을 함께 업데이트한다.

---

## 2. 앱을 열 때

- **current_screens에서만** 스크린별 파일 매핑과 상태(last_time, ab_loop_json 등)를 가져온다.
- **video_meta에서는 가져오지 않는다.**

---

## 3. 스크린에서 Close 할 때

- **current_screens**에서 해당 스크린(screen_id) 행만 **삭제**한다.
- **video_meta는 수정하지 않는다.**

---

## 4. 스크린에서 비디오를 열 때 (파일 선택해서 열 때)

- **video_meta**에서 해당 file_path 행을 조회해 ab_loop_json, last_time, resize_mode 등을 **가져온다.**
- 가져온 값으로 해당 스크린의 current_screens 행을 채운다(INSERT 또는 UPDATE).

---

## 5. screens_in_stored_layout에서 레이아웃 로드할 때

- **current_screens**를 비운 뒤, **screens_in_stored_layout**의 해당 레이아웃 스크린 행들을 **current_screens에 복사**해서 넣는다.
- 이때 **video_meta는 수정하지 않는다.**

---

## 6. current_screens가 stored layout에서 온 건지 여부

- **loadedLayoutId** (또는 이에 대응하는 플래그)를 두어,  
  현재 그리드가 **저장된 레이아웃에서 로드된 상태**인지 구분한다.
- 이 값이 있을 때만 AB 변경 시 screens_in_stored_layout도 함께 업데이트한다. (위 1번)

---

## 요약 표

| 시점 | current_screens | video_meta | screens_in_stored_layout |
|------|-----------------|------------|---------------------------|
| AB 생성/수정/삭제 | 해당 스크린만 갱신 | 항상 해당 file_path 행 갱신 | loadedLayoutId 있을 때만 해당 스크린 갱신 |
| 앱 열 때 | 여기서만 로드 | 사용 안 함 | 사용 안 함 |
| 스크린 Close | 해당 행 삭제 | 수정 안 함 | 수정 안 함 |
| 스크린에 비디오 열 때 | video_meta 값으로 채움 | 여기서 읽기만 | 수정 안 함 |
| stored layout 로드 | 레이아웃 데이터로 채움 | 수정 안 함 | 읽기만(복사 소스) |
