package com.onyxera.fs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onyxera.fs.repository.MaterialRepository

/**
 * ViewModel sınıflarını güvenli bir şekilde başlatan Fabrika (Factory) sınıfı.
 * MaterialViewModel'in ihtiyaç duyduğu MaterialRepository bağımlılığının güvenli
 * bir şekilde enjekte edilmesini (Dependency Injection) sağlar.
 */
class MaterialViewModelFactory(private val repository: MaterialRepository) : ViewModelProvider.Factory {

    /**
     * İstenen ViewModel sınıfını kontrol eder ve oluşturur.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // İstenen sınıfın doğru ViewModel olup olmadığını kontrol ederek Tip Güvenliğini sağlarız.
        if (modelClass.isAssignableFrom(MaterialViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaterialViewModel(repository) as T
        }
        // Kötü niyetli veya hatalı bir sınıf talebinde uygulamanın kontrollü ve güvenli çökmesini sağlar.
        throw IllegalArgumentException("Bilinmeyen ViewModel Sınıfı. Bellek veya Güvenlik İhlali.")
    }
}