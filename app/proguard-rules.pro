# ============================================================================
# Solnod F&S (Find & Storage) - Tersine Mühendislik ve Kod Karıştırma Kuralları
# ============================================================================

# --- 1. KOD KARIŞTIRMA VE GİZLEME (OBFUSCATION & REPACKAGING) ---
# Sınıf ve paket hiyerarşisini düzleştirip karıştırır
-repackageclasses ''
-allowaccessmodification

# Hata ayıklama meta verilerini ve kaynak dosya isimlerini gizle
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod

# --- 2. HATA AYIKLAMA LOGLARINI DERLEMEDE SİLME (LOG STRIPPING) ---
# Üretim paketinde hassas operasyonel verilerin logcat üzerinden sızmasını önler
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# --- 3. GÜVENLİK VE YARDIMCI MODÜLLER ---
-keepclassmembers class com.onyxera.fs.util.SecurityUtils {
    public static *;
}
-keepclassmembers class com.onyxera.fs.util.InputValidator {
    public static *;
}
-keepclassmembers class com.onyxera.fs.util.SolnodConstants {
    public static *;
}

# --- 4. ROOM VERİTABANI KORUMA KURALLARI ---
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.TypeConverter class * { *; }
-keep class com.onyxera.fs.data.** { *; }

# --- 5. JETPACK COMPOSE & MATERIAL 3 ---
-keep class androidx.compose.material.icons.** { *; }
-dontwarn androidx.compose.**

# --- 6. COIL GÖRSEL YÜKLEYİCİ ---
-keep class coil.** { *; }
-dontwarn coil.**

# --- 7. KOTLIN COROUTINES ---
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**