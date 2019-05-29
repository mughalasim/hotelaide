# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# FILE MAPPING======================================================================================
-verbose

#For retrofit issues
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keep class retrofit.** { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# GILDE ============================================================================================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# FACEBOOK =========================================================================================
-keepattributes *Annotation*
-keep class com.facebook.** {
   *;
}

# PDF VIEWER =======================================================================================
-keep class com.shockwave.**


# FIREBASE DATABASE MODELS =========================================================================
-keepattributes Signature
-keepclassmembers class com.hotelaide.models.** {
  *;
}

# GOOGLE ADS =======================================================================================
-dontwarn com.google.android.gms.**

# IGNORE ALL WARNING ===============================================================================
-ignorewarnings

# YOYO ANIMATION ===================================================================================
-keep class com.daimajia.* { *; }
-keep interface com.daimajia.* { *; }
-keep public class com.daimajia.* { *; }
-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }
-keep class com.nineoldandroids.* { *; }
-keep interface com.nineoldandroids.* { *; }
-keep public class com.nineoldandroids.* { *; }

# PRETY TIME FORMATTER =============================================================================
-keep class org.ocpsoft.prettytime.i18n.**