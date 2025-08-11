#include <jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <sys/sysinfo.h>
#include <string.h>
#include "whisper.h"
#include "ggml.h"

#define UNUSED(x) (void)(x)
#define TAG "JNI"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)

static inline int min(int a, int b) {
    return (a < b) ? a : b;
}

static inline int max(int a, int b) {
    return (a > b) ? a : b;
}

// 실시간 콜백을 위한 구조체
struct whisper_callback_context {
    JNIEnv *env;
    jobject callback;
    jmethodID onSegmentMethod;
    jmethodID onProgressMethod;
};

// 실시간 세그먼트 콜백 함수
static void our_new_segment_callback(struct whisper_context *ctx, struct whisper_state *state, int n_new, void *user_data) {
    struct whisper_callback_context *callback_ctx = (struct whisper_callback_context *)user_data;
    JNIEnv *env = callback_ctx->env;
    jobject callback = callback_ctx->callback;
    jmethodID onSegmentMethod = callback_ctx->onSegmentMethod;
    
    if (callback != NULL && onSegmentMethod != NULL) {
        // 새로운 세그먼트가 추가될 때마다 Java 콜백 호출
        for (int i = 0; i < n_new; i++) {
            int segment_index = whisper_full_n_segments(ctx) - n_new + i;
            const char *text = whisper_full_get_segment_text(ctx, segment_index);
            long t0 = whisper_full_get_segment_t0(ctx, segment_index);
            long t1 = whisper_full_get_segment_t1(ctx, segment_index);
            
            // Java 콜백 메서드 호출
            (*env)->CallVoidMethod(env, callback, onSegmentMethod, 
                                  (*env)->NewStringUTF(env, text), 
                                  (jlong)t0, (jlong)t1);
        }
    }
}

// 진행률 콜백 함수
static void our_progress_callback(struct whisper_context *ctx, struct whisper_state *state, int progress, void *user_data) {
    struct whisper_callback_context *callback_ctx = (struct whisper_callback_context *)user_data;
    JNIEnv *env = callback_ctx->env;
    jobject callback = callback_ctx->callback;
    jmethodID onProgressMethod = callback_ctx->onProgressMethod;
    
    if (callback != NULL && onProgressMethod != NULL) {
        // 진행률을 Java로 전달
        (*env)->CallVoidMethod(env, callback, onProgressMethod, progress);
    }
}

struct input_stream_context {
    size_t offset;
    JNIEnv * env;
    jobject thiz;
    jobject input_stream;

    jmethodID mid_available;
    jmethodID mid_read;
};

size_t inputStreamRead(void * ctx, void * output, size_t read_size) {
    struct input_stream_context* is = (struct input_stream_context*)ctx;

    jint avail_size = (*is->env)->CallIntMethod(is->env, is->input_stream, is->mid_available);
    jint size_to_copy = read_size < avail_size ? (jint)read_size : avail_size;

    jbyteArray byte_array = (*is->env)->NewByteArray(is->env, size_to_copy);

    jint n_read = (*is->env)->CallIntMethod(is->env, is->input_stream, is->mid_read, byte_array, 0, size_to_copy);

    if (size_to_copy != read_size || size_to_copy != n_read) {
        LOGI("Insufficient Read: Req=%zu, ToCopy=%d, Available=%d", read_size, size_to_copy, avail_size);
    }

    jbyte* byte_array_elements = (*is->env)->GetByteArrayElements(is->env, byte_array, NULL);
    memcpy(output, byte_array_elements, size_to_copy);
    (*is->env)->ReleaseByteArrayElements(is->env, byte_array, byte_array_elements, JNI_ABORT);

    (*is->env)->DeleteLocalRef(is->env, byte_array);

    is->offset += size_to_copy;

    return size_to_copy;
}
bool inputStreamEof(void * ctx) {
    struct input_stream_context* is = (struct input_stream_context*)ctx;

    jint result = (*is->env)->CallIntMethod(is->env, is->input_stream, is->mid_available);
    return result <= 0;
}
void inputStreamClose(void * ctx) {

}

JNIEXPORT jlong JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_initContextFromInputStream(
        JNIEnv *env, jobject thiz, jobject input_stream) {
    UNUSED(thiz);

    struct whisper_context *context = NULL;
    struct whisper_model_loader loader = {};
    struct input_stream_context inp_ctx = {};

    inp_ctx.offset = 0;
    inp_ctx.env = env;
    inp_ctx.thiz = thiz;
    inp_ctx.input_stream = input_stream;

    jclass cls = (*env)->GetObjectClass(env, input_stream);
    inp_ctx.mid_available = (*env)->GetMethodID(env, cls, "available", "()I");
    inp_ctx.mid_read = (*env)->GetMethodID(env, cls, "read", "([BII)I");

    loader.context = &inp_ctx;
    loader.read = inputStreamRead;
    loader.eof = inputStreamEof;
    loader.close = inputStreamClose;

    loader.eof(loader.context);

    struct whisper_context_params params = {};
    context = whisper_init_with_params(&loader, params);
    return (jlong) context;
}

static size_t asset_read(void *ctx, void *output, size_t read_size) {
    return AAsset_read((AAsset *) ctx, output, read_size);
}

static bool asset_is_eof(void *ctx) {
    return AAsset_read((AAsset *) ctx, NULL, 0) == 0;
}

static void asset_close(void *ctx) {
    AAsset_close((AAsset *) ctx);
}

static struct whisper_context *whisper_init_from_asset(
        JNIEnv *env,
        jobject assetManager,
        const char *asset_path_str) {
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    AAsset *asset = AAssetManager_open(mgr, asset_path_str, AASSET_MODE_STREAMING);
    if (asset == NULL) {
        return NULL;
    }

    struct whisper_model_loader loader = {};
    loader.context = asset;
    loader.read = asset_read;
    loader.eof = asset_is_eof;
    loader.close = asset_close;

    struct whisper_context_params params = {};
    return whisper_init_with_params(&loader, params);
}

JNIEXPORT jlong JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_initContextFromAsset(
        JNIEnv *env, jobject thiz, jobject assetManager, jstring asset_path_str) {
    UNUSED(thiz);

    struct whisper_context *context = NULL;
    const char *asset_path = (*env)->GetStringUTFChars(env, asset_path_str, 0);

    context = whisper_init_from_asset(env, assetManager, asset_path);

    (*env)->ReleaseStringUTFChars(env, asset_path_str, asset_path);

    return (jlong) context;
}

JNIEXPORT jlong JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_initContext(
        JNIEnv *env, jobject thiz, jstring model_path_str) {
    UNUSED(thiz);

    struct whisper_context *context = NULL;
    const char *model_path = (*env)->GetStringUTFChars(env, model_path_str, 0);

    struct whisper_context_params params = {};
    context = whisper_init_from_file_with_params(model_path, params);

    (*env)->ReleaseStringUTFChars(env, model_path_str, model_path);

    return (jlong) context;
}

JNIEXPORT void JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_freeContext(
        JNIEnv *env, jobject thiz, jlong context_ptr) {
    UNUSED(env);
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    if (context != NULL) {
        whisper_free(context);
    }
}

JNIEXPORT void JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_fullTranscribe(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint num_threads, jfloatArray audio_data) {
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    jsize audio_length = (*env)->GetArrayLength(env, audio_data);
    jfloat* audio_elements = (*env)->GetFloatArrayElements(env, audio_data, NULL);

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.n_threads = num_threads;
    params.print_realtime = false;
    params.print_progress = false;
    params.print_timestamps = true;
    params.print_special = false;
    params.translate = false;
    params.language = "en";
    params.offset_ms = 0;
    params.no_context = true;
    params.single_segment = false;
    
    // 더 빠른 처리를 위한 설정
    params.n_max_text_ctx = 64;  // 컨텍스트 크기 제한
    params.max_len = 448;      // 최대 길이 제한

    LOGI("Starting whisper_full with %d threads, audio length: %d", num_threads, audio_length);
    
    int result = whisper_full(context, params, audio_elements, audio_length);
    
    LOGI("whisper_full completed with result: %d", result);

    (*env)->ReleaseFloatArrayElements(env, audio_data, audio_elements, JNI_ABORT);

    if (result != 0) {
        LOGW("whisper_full failed: %d", result);
    }
}

// 실시간 콜백을 사용하는 새로운 전사 함수
JNIEXPORT void JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_fullTranscribeWithCallback(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint num_threads, jfloatArray audio_data, jobject callback) {
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    jsize audio_length = (*env)->GetArrayLength(env, audio_data);
    jfloat* audio_elements = (*env)->GetFloatArrayElements(env, audio_data, NULL);

    // 콜백 컨텍스트 설정
    struct whisper_callback_context callback_ctx = {};
    callback_ctx.env = env;
    callback_ctx.callback = (*env)->NewGlobalRef(env, callback);
    
    // Java 콜백 클래스에서 메서드 찾기
    jclass callback_class = (*env)->GetObjectClass(env, callback);
    callback_ctx.onSegmentMethod = (*env)->GetMethodID(env, callback_class, "onSegment", "(Ljava/lang/String;JJ)V");
    callback_ctx.onProgressMethod = (*env)->GetMethodID(env, callback_class, "onProgress", "(I)V");

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.n_threads = num_threads;
    params.print_realtime = false;
    params.print_progress = false;
    params.print_timestamps = true;
    params.print_special = false;
    params.translate = false;
    params.language = "en";
    params.offset_ms = 0;
    params.no_context = true;
    params.single_segment = false;
    
    // 콜백 함수 설정
    params.new_segment_callback = our_new_segment_callback;
    params.new_segment_callback_user_data = &callback_ctx;
    params.progress_callback = our_progress_callback;
    params.progress_callback_user_data = &callback_ctx;

    LOGI("Starting whisper_full with callback, %d threads, audio length: %d", num_threads, audio_length);
    
    int result = whisper_full(context, params, audio_elements, audio_length);
    
    LOGI("whisper_full with callback completed with result: %d", result);

    // 전역 참조 해제
    (*env)->DeleteGlobalRef(env, callback_ctx.callback);

    (*env)->ReleaseFloatArrayElements(env, audio_data, audio_elements, JNI_ABORT);

    if (result != 0) {
        LOGW("whisper_full failed: %d", result);
    }
}

JNIEXPORT jint JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_getTextSegmentCount(
        JNIEnv *env, jobject thiz, jlong context_ptr) {
    UNUSED(env);
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    return whisper_full_n_segments(context);
}

JNIEXPORT jstring JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_getTextSegment(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint index) {
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    const char *text = whisper_full_get_segment_text(context, index);
    return (*env)->NewStringUTF(env, text);
}

JNIEXPORT jlong JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_getTextSegmentT0(JNIEnv *env, jobject thiz,jlong context_ptr, jint index) {
    UNUSED(env);
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    return whisper_full_get_segment_t0(context, index);
}

JNIEXPORT jlong JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_getTextSegmentT1(JNIEnv *env, jobject thiz,jlong context_ptr, jint index) {
    UNUSED(env);
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    return whisper_full_get_segment_t1(context, index);
}

JNIEXPORT jstring JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_getSystemInfo(
        JNIEnv *env, jobject thiz
) {
    UNUSED(env);
    UNUSED(thiz);

    return (*env)->NewStringUTF(env, whisper_print_system_info());
}

JNIEXPORT jstring JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_benchMemcpy(JNIEnv *env, jobject thiz,
                                                                     jint n_threads) {
    UNUSED(env);
    UNUSED(thiz);

    return (*env)->NewStringUTF(env, whisper_bench_memcpy(n_threads));
}

JNIEXPORT jstring JNICALL
Java_com_araonesoft_dalstttest_WhisperLib_benchGgmlMulMat(JNIEnv *env, jobject thiz,
                                                                             jint n_threads) {
    UNUSED(env);
    UNUSED(thiz);

    return (*env)->NewStringUTF(env, whisper_bench_ggml_mul_mat(n_threads));
}

