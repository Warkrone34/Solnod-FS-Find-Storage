package com.onyxera.fs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.repository.MaterialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar // EKLENDİ: Tarih filtrelemesi için

/**
 * Sıralama Seçenekleri (Solnod Operasyon Standartları)
 */
enum class SortType { DATE_DESC, PRICE_DESC, WEIGHT_DESC, SHIP_NAME }

/**
 * Ana Ekran İş Mantığı Katmanı.
 * GÜNCELLEME: Gelişmiş Excel Raporları (.csv) için Gemi ve Ay bazlı
 * özel filtreleme ve veri çekme asenkron fonksiyonları eklendi.
 */
class MaterialViewModel(private val repository: MaterialRepository) : ViewModel() {

    // Arama Sorgusu State'i
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Sıralama Tipi State'i (Varsayılan: En Yeni)
    private val _sortType = MutableStateFlow(SortType.DATE_DESC)
    val sortType = _sortType.asStateFlow()

    // Gemi Filtresi State'i (null ise hepsi)
    private val _selectedShip = MutableStateFlow<String?>(null)
    val selectedShip = _selectedShip.asStateFlow()

    // Dinamik Gemi Listesi (Kullanıcının tanımladığı gemiler)
    val allShips: StateFlow<List<com.onyxera.fs.data.ShipEntity>> = repository.allShips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // ViewModel her başlatıldığında arka planda 15 günü geçmiş çöpleri kalıcı olarak siler.
        autoCleanTrash()
    }

    // --- State Güncelleme Fonksiyonları ---
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateSortType(type: SortType) { _sortType.value = type }
    fun updateSelectedShip(ship: String?) { _selectedShip.value = ship }

    // --- Gemi Yönetimi Fonksiyonları ---
    fun insertShip(name: String, details: String = "", onComplete: ((com.onyxera.fs.data.ShipEntity) -> Unit)? = null) {
        viewModelScope.launch {
            val entity = com.onyxera.fs.data.ShipEntity(name = name.trim(), details = details.trim())
            val id = repository.insertShip(entity)
            val created = entity.copy(id = id.toInt())
            onComplete?.invoke(created)
        }
    }

    fun deleteShip(ship: com.onyxera.fs.data.ShipEntity) {
        viewModelScope.launch {
            repository.deleteShip(ship)
            // Eğer silinen gemi şu an filtrede seçiliyse filtreyi sıfırla
            if (_selectedShip.value == ship.name) {
                _selectedShip.value = null
            }
        }
    }

    /**
     * ANA VERİ AKIŞI:
     * Veritabanı, Arama, Sıralama ve Gemi Filtresi değiştiğinde otomatik tetiklenir.
     */
    val materials: StateFlow<List<MaterialEntity>> = combine(
        repository.activeMaterials,
        _searchQuery,
        _sortType,
        _selectedShip
    ) { list, query, sort, ship ->

        // OPTİMİZASYON: Ağır filtreleme ve sıralama işlemini arka plana (CPU işçilerine) atıyoruz.
        withContext(Dispatchers.Default) {
            var filteredList = list

            // 1. Gemi Filtreleme
            if (ship != null) {
                filteredList = filteredList.filter { it.shipName == ship }
            }

            // 2. Arama Filtreleme
            if (query.isNotBlank()) {
                filteredList = filteredList.filter {
                    it.materialName.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.shipName.contains(query, ignoreCase = true)
                }
            }

            // 3. Sıralama Uygulama
            when (sort) {
                SortType.DATE_DESC -> filteredList.sortedByDescending { it.timestamp }
                SortType.PRICE_DESC -> filteredList.sortedByDescending { it.price }
                SortType.WEIGHT_DESC -> filteredList.sortedByDescending { it.weightKg }
                SortType.SHIP_NAME -> filteredList.sortedBy { it.shipName }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- EXPORT (EXCEL) İÇİN ÖZEL VERİ ÇEKME FONKSİYONLARI ---

    // 1. Tüm Kayıtları Getir
    suspend fun getExportDataAll(): List<MaterialEntity> {
        // Flow'un sadece anlık durumunu (first) alıp UI'a dönüyoruz
        return repository.activeMaterials.first()
    }

    // 2. Sadece Belirli Bir Geminin Kayıtlarını Getir
    suspend fun getExportDataByShip(shipName: String): List<MaterialEntity> {
        return repository.activeMaterials.first().filter { it.shipName == shipName }
    }

    // 3. Sadece İçinde Bulunulan Ayın Kayıtlarını Getir
    suspend fun getExportDataByCurrentMonth(): List<MaterialEntity> {
        val allActive = repository.activeMaterials.first()
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return allActive.filter { item ->
            val itemCalendar = Calendar.getInstance().apply { timeInMillis = item.timestamp }
            itemCalendar.get(Calendar.MONTH) == currentMonth && itemCalendar.get(Calendar.YEAR) == currentYear
        }
    }

    // --- Veri Operasyonları ---

    // Çöp Kutusu (Trash) ekranı için veriler
    val trashedMaterials: StateFlow<List<MaterialEntity>> = repository.deletedMaterials
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun insertDirectly(entity: MaterialEntity) = viewModelScope.launch { repository.insert(entity) }
    fun updateDirectly(entity: MaterialEntity) = viewModelScope.launch { repository.update(entity) }

    fun moveToTrash(items: List<MaterialEntity>) {
        viewModelScope.launch {
            val ids = items.map { it.id }
            repository.moveToTrash(ids)
        }
    }

    fun restoreFromTrash(items: List<MaterialEntity>) {
        viewModelScope.launch {
            val ids = items.map { it.id }
            repository.restoreFromTrash(ids)
        }
    }

    fun deletePermanently(item: MaterialEntity) {
        viewModelScope.launch { repository.deletePermanently(item) }
    }

    /**
     * 15 GÜNLÜK OTOMATİK TEMİZLEYİCİ
     */
    private fun autoCleanTrash() {
        viewModelScope.launch {
            val fifteenDaysInMillis = 15L * 24 * 60 * 60 * 1000
            val threshold = System.currentTimeMillis() - fifteenDaysInMillis
            repository.clearOldTrash(threshold)
        }
    }
}