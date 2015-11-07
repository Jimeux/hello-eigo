# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Retrolambda
-dontwarn java.lang.invoke.*

# Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.stream.** { *; }

-keep class com.moobasoft.damego.rest.models.** { *; }
-keepclassmembers class com.moobasoft.damego.rest.models.** { *; }
-keep class com.moobasoft.damego.rest.requests.** { *; }
-keepclassmembers class com.moobasoft.damego.rest.requests.** { *; }

# Hax
-keepattributes InnerClasses,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

-dontwarn okio.**