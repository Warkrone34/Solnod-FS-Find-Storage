package com.onyxera.fs.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.onyxera.fs.data.MaterialEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mevcut bir operasyon kaydının düzeltilmesi (Edit) için kullanılan ViewModel.
 * Veritabanından gelen mevcut Entity'yi parçalayarak UI formuna (State'lere) doldurur,
 * yapılan değişiklikleri toplar ve aynı ID ile güncellenmiş yeni bir Entity oluşturur.
 * GÜNCELLEME: Gerçek para birimi (Currency) desteği eklendi.
 */
class EditMaterialViewModel : ViewModel() {

    private var currentId: Int = 0
    private var originalTimestamp: Long = 0L

    private val _shipName = MutableStateFlow("")
    val shipName: StateFlow<String> = _shipName.asStateFlow()

    private val _materialName = MutableStateFlow("")
    val materialName: StateFlow<String> = _materialName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _receivedDate = MutableStateFlow("")
    val receivedDate: StateFlow<String> = _receivedDate.asStateFlow()

    private val _sentDate = MutableStateFlow("")
    val sentDate: StateFlow<String> = _sentDate.asStateFlow()

    private val _priceStr = MutableStateFlow("")
    val priceStr: StateFlow<String> = _priceStr.asStateFlow()

    // YENİ: Para birimini tutacak State (Varsayılan: TL)
    private val _currency = MutableStateFlow("TL")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _weightStr = MutableStateFlow("")
    val weightStr: StateFlow<String> = _weightStr.asStateFlow()

    private val _widthStr = MutableStateFlow("")
    val widthStr: StateFlow<String> = _widthStr.asStateFlow()

    private val _heightStr = MutableStateFlow("")
    val heightStr: StateFlow<String> = _heightStr.asStateFlow()

    private val _lengthStr = MutableStateFlow("")
    val lengthStr: StateFlow<String> = _lengthStr.asStateFlow()

    private val _photoUris = MutableStateFlow<List<Uri>>(emptyList())
    val photoUris: StateFlow<List<Uri>> = _photoUris.asStateFlow()

    /**
     * Düzeltme ekranı açıldığında mevcut verileri forma doldurur.
     */
    fun loadMaterial(entity: MaterialEntity) {
        currentId = entity.id
        originalTimestamp = entity.timestamp // Orijinal kayıt tarihi korunur

        _shipName.value = entity.shipName
        _materialName.value = entity.materialName
        _description.value = entity.description
        _receivedDate.value = entity.receivedDate
        _sentDate.value = entity.sentDate

        _priceStr.value = if (entity.price == 0.0) "" else entity.price.toString()
        _currency.value = entity.currency // YENİ: Veritabanındaki para birimi UI'a yüklenir

        _weightStr.value = if (entity.weightKg == 0.0) "" else entity.weightKg.toString()
        _widthStr.value = if (entity.width == 0.0) "" else entity.width.toString()
        _heightStr.value = if (entity.height == 0.0) "" else entity.height.toString()
        _lengthStr.value = if (entity.length == 0.0) "" else entity.length.toString()

        val uris = entity.photoUris.filter { it.isNotBlank() }.map { Uri.parse(it) }
        _photoUris.value = uris
    }

    fun updateShipName(value: String) { _shipName.value = com.onyxera.fs.util.InputValidator.limitText(value, com.onyxera.fs.util.InputValidator.MAX_SHIP_NAME_LENGTH) }
    fun updateMaterialName(value: String) { _materialName.value = com.onyxera.fs.util.InputValidator.limitText(value, com.onyxera.fs.util.InputValidator.MAX_MATERIAL_NAME_LENGTH) }
    fun updateDescription(value: String) { _description.value = com.onyxera.fs.util.InputValidator.limitText(value, com.onyxera.fs.util.InputValidator.MAX_DESCRIPTION_LENGTH) }
    fun updateReceivedDate(value: String) { _receivedDate.value = com.onyxera.fs.util.InputValidator.limitText(value, com.onyxera.fs.util.InputValidator.MAX_DATE_LENGTH) }
    fun updateSentDate(value: String) { _sentDate.value = com.onyxera.fs.util.InputValidator.limitText(value, com.onyxera.fs.util.InputValidator.MAX_DATE_LENGTH) }
    fun updatePrice(value: String) { _priceStr.value = com.onyxera.fs.util.InputValidator.filterDecimalInput(value) }

    // YENİ: Arayüzden güncellenen para birimini yakalar
    fun updateCurrency(value: String) { _currency.value = value }

    fun updateWeight(value: String) { _weightStr.value = com.onyxera.fs.util.InputValidator.filterDecimalInput(value) }
    fun updateWidth(value: String) { _widthStr.value = com.onyxera.fs.util.InputValidator.filterDecimalInput(value) }
    fun updateHeight(value: String) { _heightStr.value = com.onyxera.fs.util.InputValidator.filterDecimalInput(value) }
    fun updateLength(value: String) { _lengthStr.value = com.onyxera.fs.util.InputValidator.filterDecimalInput(value) }

    fun addPhotoUri(uri: Uri) {
        val currentList = _photoUris.value.toMutableList()
        if (!currentList.contains(uri)) {
            currentList.add(uri)
            _photoUris.value = currentList
        }
    }

    fun removePhotoUri(uri: Uri) {
        val currentList = _photoUris.value.toMutableList()
        currentList.remove(uri)
        _photoUris.value = currentList
    }

    /**
     * Düzeltilmiş verileri toplayarak Room veritabanındaki Update işlemi için Entity oluşturur.
     * ID değerinin aynı kalması, Room'un yeni kayıt oluşturmak yerine eskisini ezmesini sağlar.
     */
    fun buildUpdatedEntity(): MaterialEntity {
        val finalPrice = _priceStr.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val finalWeight = _weightStr.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val finalWidth = _widthStr.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val finalHeight = _heightStr.value.replace(",", ".").toDoubleOrNull() ?: 0.0
        val finalLength = _lengthStr.value.replace(",", ".").toDoubleOrNull() ?: 0.0

        return MaterialEntity(
            id = currentId, // Mevcut ID korunur
            shipName = _shipName.value.ifBlank { "Belirtilmedi" },
            materialName = _materialName.value.ifBlank { "Bilinmeyen Malzeme" },
            photoUris = _photoUris.value.map { it.toString() },
            description = _description.value,
            receivedDate = _receivedDate.value.ifBlank { "Belirtilmedi" },
            sentDate = _sentDate.value.ifBlank { "Belirtilmedi" },
            price = finalPrice,
            currency = _currency.value, // YENİ: Güncellenen para birimi veritabanına atılır
            weightKg = finalWeight,
            width = finalWidth,
            height = finalHeight,
            length = finalLength,
            timestamp = originalTimestamp // Orijinal kayıt tarihi korunur
        )
    }

    fun resetData() {
        currentId = 0
        originalTimestamp = 0L
        _shipName.value = ""
        _materialName.value = ""
        _description.value = ""
        _receivedDate.value = ""
        _sentDate.value = ""
        _priceStr.value = ""
        _currency.value = "TL" // YENİ: Temizlerken TL'ye döner
        _weightStr.value = ""
        _widthStr.value = ""
        _heightStr.value = ""
        _lengthStr.value = ""
        _photoUris.value = emptyList()
    }
}