# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/nguyentuanvu/Documents/Android/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging*/
-allowaccessmodification

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# standard, except v4.app.Fragment, its required when app uses Fragments

-keep public class * extends android.app.Activity
-keep public class * extends android.support.v7.app.ActionBarActivity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
}

-keep class com.squareup.** { *; }
-dontwarn com.squareup.**
-keep class okio.Okio.** { *; }
-dontwarn okio.Okio.**
-keep class java.nio.** { *; }
-dontwarn java.nio.**
-keep class org.codehaus.mojo.** { *; }
-dontwarn org.codehaus.mojo.**
-keep class de.greenrobot.event.** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
# From missing_rules.txt
-dontwarn java.awt.Component


# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
   @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Retrofit 2.0
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}


-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
-keepclassmembers class com.dalread.model** { <fields>; }
-keep class com.dalread.model** { <fields>; }

-dontwarn org.json.**
# Billing
-keep class com.android.vending.billing.**
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Jitsi
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep,includedescriptorclasses class com.facebook.react.bridge.** { *; }

# okhttp

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okio.** { *; }
-dontwarn okio.**

# WebRTC

-keep class org.webrtc.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid

# Jisti Meet SDK

-keep class org.jitsi.meet.** { *; }
-keep class org.jitsi.meet.sdk.** { *; }

# We added the following when we switched minifyEnabled on. Probably because we
# ran the app and hit problems...

-keep class com.facebook.react.bridge.CatalystInstanceImpl { *; }
-keep class com.facebook.react.bridge.ExecutorToken { *; }
-keep class com.facebook.react.bridge.JavaScriptExecutor { *; }
-keep class com.facebook.react.bridge.ModuleRegistryHolder { *; }
-keep class com.facebook.react.bridge.ReadableType { *; }
-keep class com.facebook.react.bridge.queue.NativeRunnable { *; }
-keep class com.facebook.react.devsupport.** { *; }

-dontwarn com.facebook.react.devsupport.**
-dontwarn com.google.appengine.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.servlet.**

# ^^^ We added the above when we switched minifyEnabled on.

# Rule to avoid build errors related to SVGs.
-keep public class com.horcrux.svg.** {*;}

# FFmpegMediaMetadataRetriever
-keep class wseemann.media.** { *; }

# Without this, AraKoica can't update sqlite data from server contents.
-keep class com.dalread.database.** {*;}

-keepclassmembers class com.dalread.network.ChatCompletionResponse { *; }
-keepclassmembers class com.dalread.network.ChatCompletionResponse$Message { *; }
-keepclassmembers class com.dalread.network.ChatCompletionResponse$Choice { *; }
-keepclassmembers class com.dalread.network.ChatCompletionResponse$Usage { *; }
-keepclassmembers class com.dalread.network.ChatGPTRequest { *; }
-keepclassmembers class com.dalread.network.ChatGPTRequest$Message { *; }
-keepclassmembers class com.dalread.network.ChatGPTRequest$Message$Role { *; }

-keep class com.dalread.network.OpenSubtitleApi$* {*;}
-keep class com.dalread.network.RequestBodyOSDownloadSubtitle {*;}

# 다음에러때문에 추가함. 디버그 모드는 되는데, 릴리즈 모드에서는 컴파일이 안되었음.
# Missing classes detected while running R8. Please add the missing classes or apply additional keep rules that are generated in /Users/dalnimbest/Documents/workspace/dalvocaandroid/app/build/outputs/mapping/araplayerRelease/missing_rules.txt.
-dontwarn com.google.android.exoplayer2.source.rtsp.RtspMessageChannel$MessageParser$ReadingState
