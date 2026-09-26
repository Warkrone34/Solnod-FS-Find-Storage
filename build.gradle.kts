// Proje seviyesindeki yapılandırma.
// Eklentileri burada 'apply false' ile tanımlıyoruz. Bu, eklentinin versiyonunun projeye tanıtıldığı ama anında aktif edilmediği anlamına gelir.
// Gerçek aktivasyon 'app' modülünde yapılır. Hatayı çözen ana hamlelerden biri budur.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
}