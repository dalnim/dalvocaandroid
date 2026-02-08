# 키스토어(.jks) 정리

다음에 빌드·배포할 때 헷갈리지 않도록 정리한 문서입니다.

---

## 경과

1. **처음:** 아라플레이어가 Dropbox에 있던 `realease_key_dalread.jks`를 build.gradle에서 참조해서 사용함.
2. **권한 문제:** Dropbox 경로 접근 시 `Operation not permitted` 에러 발생 → **dalread.jks**를 프로젝트 소스 루트로 복사해 옴. build.gradle 경로를 `${rootProject.projectDir}/realease_key_dalread.jks` 로 변경.
3. **구글 플레이:** 예전부터 구글 플레이에 올릴 때는 **realease_key_araplayer.jks**로 서명해서 올림 (Android Studio "Generate Signed Bundle/APK"에서 해당 파일 선택).
4. **권한 문제:** araplayer.jks도 Dropbox에 두었다가 접근 문제로 **araplayer.jks**를 프로젝트 소스 루트로 옮김.
5. **현재:** 구글 플레이에 올리는 건 **realease_key_araplayer.jks**로 서명한 빌드로 올림.

헷갈리는건 realease_key_dalread.jks와 realease_key_araplayer.jks의 차이를 모르겠음.
일단 구글플레이어에는 realease_key_araplayer.jks로 Generate Signed App Bundle(aab)를 만들어올렸음.
---

## 현재 파일 위치

- **realease_key_dalread.jks** – 프로젝트 루트 (`dalvocaandroid/`)
- **realease_key_araplayer.jks** – 프로젝트 루트 (`dalvocaandroid/`)

둘 다 `.gitignore`에 `*.jks`로 포함되어 있어 **Git 레포에는 올라가지 않음.**

---

## build.gradle과의 관계

- **지금 build.gradle:** `signingConfigs.config`가 **realease_key_dalread.jks**를 가리킴.
- **release 빌드:** 모든 플레이버(araplayer, dalvoca, 아라한자, 아라한글 등)의 release가 이 **dalread.jks**로 서명됨.
- **구글 플레이에 올리는 araplayer:** 실제로는 **realease_key_araplayer.jks**로 서명한 빌드를 올려야 함 (이미 그렇게 올린 상태).

따라서 **araplayer를 Gradle로 빌드해서 구글 플레이에 올리려면**, araplayer release만 **araplayer.jks**를 쓰도록 build.gradle에서 서명 설정을 분리해 두는 것이 맞음. (아직 반영 안 되어 있으면 나중에 수정 필요.)

---

## 요약

| 용도 | 사용하는 키스토어 |
|------|-------------------|
| 구글 플레이에 올리는 **araplayer** | **realease_key_araplayer.jks** |
| 그 외 플레이버(dalvoca 등) release / 로컬 테스트 | **realease_key_dalread.jks** (현재 build.gradle 기본값) |

키스토어는 프로젝트 루트에 두고, Git에는 올리지 않음.
