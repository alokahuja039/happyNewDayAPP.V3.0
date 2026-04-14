# Keep app classes
-keep class com.habittracker.app.** { *; }

# Keep Gson for JSON serialisation
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
