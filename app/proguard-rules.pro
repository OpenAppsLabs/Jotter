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
#-renamesourcefileattribute SourceFile

# --- GSON / R8 RULES FOR DATA MODELS ---

# 1. Keep Data Models so field names match JSON keys
-keep class com.openappslabs.jotter.data.model.** { *; }

# 2. CRITICAL: Keep Generic Signatures.
# Without this, R8 strips the type 'List<Note>' down to just 'List'.
# Gson then deserializes 'notes' as a List of LinkedTreeMaps (Maps) instead of Note objects.
# This causes a ClassCastException when the app tries to use the data.
-keepattributes Signature

# 3. Keep Annotations (good practice for Gson, though you aren't heavily using them yet)
-keepattributes *Annotation*

# 4. Prevent warnings if Gson uses Unsafe (common in some versions)
-dontwarn sun.misc.Unsafe