# Whisper.cpp를 araplayer에서 사용하기 위한 전체 과정

## 1단계: 프로젝트 구조 설정
- [O] app/src/araplayer/cpp/whisper.cpp/ 디렉토리 생성
- [ ] whisper.cpp 소스코드 다운로드 (git에 추가하지 않음)
- [ ] araplayer 전용 CMakeLists.txt 생성

## 2단계: Android 빌드 설정
- [ ] app/build.gradle에 araplayer 플레이버 전용 CMake 설정 추가
- [ ] NDK 빌드 설정으로 whisper.cpp 컴파일
- [ ] 필요한 라이브러리 링크 (OpenBLAS, BLIS 등)

## 3단계: JNI 인터페이스 생성
- [ ] app/src/araplayer/java/com/dalread/util/WhisperJNI.java 생성
- [ ] C++ 함수들을 Java에서 호출할 수 있는 네이티브 메서드 정의
- [ ] 모델 로드, 오디오 처리, 음성 인식 함수 래핑

## 4단계: WhisperTranscriber 클래스 생성
- [ ] 기존 VoskTranscriber와 유사한 구조로 WhisperTranscriber.java 생성
- [ ] whisper.cpp 모델 초기화 및 관리
- [ ] 오디오 파일을 16kHz PCM으로 변환
- [ ] 음성 인식 결과 처리

## 5단계: 기존 코드 수정
- [ ] MediaInformationActivity.java에서 Vosk 대신 Whisper 사용
- [ ] 모델 파일 경로 변경 (ggml-base.en-q8_0.bin)
- [ ] 초기화 및 인식 로직 수정

## 6단계: 모델 파일 관리
- [O] 영어 모델 파일 다운로드 (ggml-base.en-q8_0.bin ~78MB)
- [O] app/src/araplayer/assets/models/에 배치
- [O] .gitignore에 모델 파일 제외 설정

## 7단계: 성능 최적화
- [ ] 메모리 사용량 최적화
- [ ] 멀티스레딩 설정
- [ ] GPU 가속 옵션 (가능한 경우)
