-printmapping out.map
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,EnclosingMethod,SourceFile,LineNumberTable,Signature,InnerClasses,Deprecated,Exceptions,RuntimeVisibleParameterAnnotations
-optimizations !class/unboxing/enum
-dontnote

# Preserve enumeration classes.
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve all .class method names.
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Kotlin serialization
-dontwarn kotlinx.serialization
-dontwarn java.lang.invoke.StringConcatFactory

# Preserve Parcelable classes
-keep @kotlinx.parcelize.Parcelize class *

# GSON x AGP 8.0
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-if class *
-keepclasseswithmembers class <1> {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}

# OKHTTP
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Annotations
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
