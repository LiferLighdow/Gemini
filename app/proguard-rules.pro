# Keep JavaScript interface methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep the bridge class
-keep class com.liferlighdow.gemini.WebAppInterface {
    @android.webkit.JavascriptInterface <methods>;
}

# Credentials API and Google Identity rules
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# R8/Proguard rules for Compose
-keepclassmembers class androidx.compose.runtime.Recomposer {
    private void readObject(java.io.ObjectInputStream);
}
