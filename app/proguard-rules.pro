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
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn java.lang.reflect.Method
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.stream.** { *; }

-keep class com.moobasoft.helloeigo.rest.models.** { *; }
-keepclassmembers class com.moobasoft.helloeigo.rest.models.** { *; }
-keep class com.moobasoft.helloeigo.rest.requests.** { *; }
-keepclassmembers class com.moobasoft.helloeigo.rest.requests.** { *; }
-keep class com.moobasoft.helloeigo.rest.errors.** { *; }
-keepclassmembers class com.moobasoft.helloeigo.rest.errors.** { *; }

# Hax
-keepattributes InnerClasses,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

-dontwarn okio.**