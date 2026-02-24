# 16KB 메모리 페이지 크기 지원 (Google Play 요구사항)

2026년 5월 31일부터 Android 15(API 35) 이상을 타겟하는 앱은 16KB 메모리 페이지 크기 지원이 필요합니다.

## 적용된 변경 사항

- **app/build.gradle**
  - `packaging.jniLibs.useLegacyPackaging = true`: 서드파티 .so가 4KB 정렬일 때 설치 이슈 우회
  - `ndkVersion '28.0.13004108'`: NDK r28 사용 (16KB 정렬 기본)

## 빌드 후 검증

APK가 16KB 정렬을 만족하는지 확인:

```bash
# SDK build-tools 35.0.0 이상 필요
$ANDROID_HOME/build-tools/36.0.0/zipalign -c -P 16 -v 4 app/build/outputs/apk/araplayer/release/app-araplayer-release.apk
```

출력 마지막에 `Verification successful` 이 나오면 통과입니다.

## 16KB 환경에서 테스트

1. **에뮬레이터**: SDK Manager에서 Android 15+ **16 KB Page Size** 시스템 이미지 설치  
   (Google APIs Experimental 16 KB Page Size Intel x86_64 / ARM 64 v8a)
2. AVD를 16KB 이미지로 생성 후 araplayer 설치·실행
3. 기기에서 페이지 크기 확인:
   ```bash
   adb shell getconf PAGE_SIZE
   ```
   `16384` 이면 16KB 환경입니다.
4. 앱 주요 시나리오(재생, 자막, 설정 등) 동작 확인

## 참고

- [Support 16 KB page sizes | Android Developers](https://developer.android.com/guide/practices/page-sizes)
- Play Console에서 “개발자님의 최신 프로덕션 버전은 16KB 메모리 페이지 크기를 지원하지 않습니다” 경고는, 위 설정으로 빌드한 새 AAB를 업로드하면 해소됩니다.
