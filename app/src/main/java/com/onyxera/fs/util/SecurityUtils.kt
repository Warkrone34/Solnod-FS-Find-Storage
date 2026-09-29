package com.onyxera.fs.util

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.view.WindowManager
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.security.MessageDigest
import java.util.Locale

/**
 * Solnod F&S Kurumsal Güvenlik ve Tersine Mühendislik Koruması Modülü.
 * 
 * Özellikler:
 * 1. Kriptografik SHA-256 Özetleme (Metin, Bayt ve Dosya Bütünlüğü)
 * 2. APK İmza / Sertifika Doğrulama (Anti-Tamper & Yeniden Paketleme Engelleme)
 * 3. Root / Yetkisiz Erişim Tespiti
 * 4. Dinamik Hata Ayıklama (Debugger) ve Hook (Frida/Xposed) Tespiti
 * 5. Emülatör / Sandbox Ortamı Tespiti
 * 6. Ekran Görüntüsü ve Casus Ekran Kaydı Engelleme (FLAG_SECURE)
 */
object SecurityUtils {

    // --- 1. SHA-256 KRİPTOGRAFİK BÜTÜNLÜK İŞLEMLERİ ---

    /**
     * Metinsel bir girdinin SHA-256 karma (hash) değerini üretir.
     */
    fun sha256(input: String): String {
        return sha256(input.toByteArray(Charsets.UTF_8))
    }

    /**
     * Bayt dizisinin SHA-256 karma (hash) değerini üretir.
     */
    fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Bir dosyanın (örn. SQLite veritabanı veya dışa aktarılan Excel) SHA-256 bütünlük özetini hesaplar.
     */
    fun sha256(file: File): String {
        if (!file.exists() || !file.isFile) return ""
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    // --- 2. APK İMZA / SERTİFİKA DOĞRULAMA (ANTI-TAMPERING) ---

    /**
     * Uygulamanın imzalandığı sertifikanın SHA-256 parmak izini okur.
     * Uygulama tersine mühendislikle bozulup başka bir anahtarla yeniden imzalandığında
     * bu değer değişir ve manipülasyon tespit edilir.
     */
    @Suppress("DEPRECATION")
    fun getAppSignatureSha256(context: Context): String {
        return try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                packageInfo.signatures
            }

            if (!signatures.isNullOrEmpty()) {
                val certBytes = signatures[0].toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(certBytes)
                digest.joinToString(":") { "%02X".format(it) }
            } else {
                "UNKNOWN_SIGNATURE"
            }
        } catch (e: Exception) {
            "ERROR_${e.javaClass.simpleName}"
        }
    }

    /**
     * Çalışan APK'nın imza SHA-256 değerini beklenen orijinal geliştirici sertifikasıyla karşılaştırır.
     */
    fun verifyAppSignature(context: Context, expectedSha256: String): Boolean {
        if (expectedSha256.isBlank()) return true // Henüz kilit konulmamışsa izin ver
        val currentSha = getAppSignatureSha256(context).replace(":", "").uppercase(Locale.ROOT)
        val expectedClean = expectedSha256.replace(":", "").uppercase(Locale.ROOT)
        return currentSha == expectedClean
    }

    // --- 3. TERSİNE MÜHENDİSLİK & GÜVENLİK TEHDİDİ TESPİTİ ---

    /**
     * Cihazda Root (superuser) yetkisi veya kalıntıları olup olmadığını denetler.
     */
    fun isRooted(): Boolean {
        // 1. Build Tags kontrolü
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // 2. Bilinen superuser ikili (binary) yolları
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }

        // 3. 'which su' komut kontrolü
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = BufferedReader(FileReader("/proc/version"))
            reader.close()
            process.inputStream.read() != -1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Uygulamanın bir emülatör veya sanal ortamda (Sandbox) çalıştırılıp çalıştırılmadığını tespit eder.
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }

    /**
     * Aktif bir hata ayıklayıcı (Debugger) bağlanıp bağlanmadığını kontrol eder.
     */
    fun isDebuggerAttached(context: Context): Boolean {
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger() || (!isDebuggable && isRunningUnderTracer())
    }

    /**
     * /proc/self/status dosyasını inceleyerek uygulamanın gdb veya lldb gibi bir tracer altında olup olmadığını kontrol eder.
     */
    private fun isRunningUnderTracer(): Boolean {
        return try {
            val statusFile = File("/proc/self/status")
            if (statusFile.exists()) {
                BufferedReader(FileReader(statusFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line!!.startsWith("TracerPid:")) {
                            val tracerPid = line!!.substring("TracerPid:".length).trim().toIntOrNull() ?: 0
                            return tracerPid != 0
                        }
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Frida, Xposed veya Substrate gibi dinamik bellek kancalama (Hooking) araçlarını tespit eder.
     */
    fun isHookingDetected(): Boolean {
        // 1. Bilinen kütüphane sınıfları kontrolü
        val hookClasses = arrayOf(
            "de.robv.android.xposed.XposedBridge",
            "com.saurik.substrate.MS\$MethodHook"
        )
        for (className in hookClasses) {
            try {
                Class.forName(className)
                return true
            } catch (ignored: ClassNotFoundException) {
                // Sınıf yüklü değil, normal
            }
        }

        // 2. /proc/self/maps bellek haritası analizi (Frida soket veya kütüphane enjeksiyonu)
        return try {
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                BufferedReader(FileReader(mapsFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val lower = line!!.lowercase(Locale.ROOT)
                        if (lower.contains("frida-agent") || lower.contains("frida-gadget") || lower.contains("xposed")) {
                            return true
                        }
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    // --- 4. EKRAN GİZLİLİĞİ VE CASUS YAZILIM KORUMASI ---

    /**
     * Ekran görüntüsü alma, ekran videosu kaydetme ve Son Uygulamalar (Recent Apps)
     * görünümünde veri sızmasını engellemek için FLAG_SECURE uygular.
     */
    fun applyScreenSecurity(activity: Activity, enableSecureMode: Boolean) {
        if (enableSecureMode) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    // --- 5. TOPLU GÜVENLİK RAPORU ---

    data class SecurityStatusReport(
        val isRooted: Boolean,
        val isEmulator: Boolean,
        val isDebuggerAttached: Boolean,
        val isHookDetected: Boolean,
        val certificateSha256: String,
        val isEnvironmentSecure: Boolean
    )

    /**
     * Sistemin anlık güvenlik durumunu değerlendirir ve rapor döner.
     */
    fun evaluateSecurity(context: Context): SecurityStatusReport {
        val rooted = isRooted()
        val emulator = isEmulator()
        val debugger = isDebuggerAttached(context)
        val hook = isHookingDetected()
        val certSha = getAppSignatureSha256(context)

        // Güvenli ortam: Root yok, debugger bağlı değil, hook araçları yok
        val isSecure = !rooted && !debugger && !hook

        return SecurityStatusReport(
            isRooted = rooted,
            isEmulator = emulator,
            isDebuggerAttached = debugger,
            isHookDetected = hook,
            certificateSha256 = certSha,
            isEnvironmentSecure = isSecure
        )
    }
}
