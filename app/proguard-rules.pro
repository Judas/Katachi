# GLOBAL
-printmapping out.map
-keepparameternames
-keepattributes *Annotation*,EnclosingMethod,SourceFile,LineNumberTable,Signature,InnerClasses,Deprecated,Exceptions,RuntimeVisibleParameterAnnotations
-renamesourcefileattribute SourceFile
-optimizations !class/unboxing/enum

-dontnote
-dontwarn **

# Preserve enumeration classes.
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
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

# PARCELABLE
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# NoSuchMethodError while calling parse(String, ParsePosition) on Xiaomi devices (either Android 4, 5 or 6)
-keepnames class org.apache.** { *; }

# GSON
-dontwarn sun.misc.**
# Prevent R8 from leaving Data object members always null
-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
