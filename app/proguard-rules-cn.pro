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

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dontobfuscate
-forceprocessing
-optimizationpasses 5

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


-keep class com.dalnimsoft.dalreadwebchineselite.model.** { *; }
-keep class com.dalnimsoft.dalreadwebchineselite.network.** { *; }
-dontwarn org.json.**
# Billing
-keep class com.android.vending.billing.**
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


