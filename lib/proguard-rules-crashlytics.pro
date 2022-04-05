##---------------Begin: proguard configuration for Crashlytics  ----------
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

##---------------End: proguard configuration for Crashlytics  ----------