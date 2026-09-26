# ⚓ Solnod F&S (Find & Storage)

[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-brightgreen.svg?style=flat-square&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4.svg?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Database-Room%20DB%20(Offline--First)-orange.svg?style=flat-square)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg?style=flat-square)](LICENSE)

> **Solnod Maritime & Logistics Group** operasyon standartlarına uygun; satın alma ve teknik ikmal personeli için gemi/proje malzemelerinin fotoğraflanması, boyutsal ölçü girişleri, hedef gemi tanımlama ve atamaları, dövizli maliyet takibi ve teslimat süreçlerini uçtan uca dijitalleştiren modern, güvenli ve çevrimdışı (offline-first) mobil uygulama.

---

## 📖 Ne İşe Yarar? (Uygulama Analizi)

Denizcilik, tersane ve teknik lojistik operasyonlarında sahada satın alınan veya tedarikçilerden teslim alınan yedek parça, avadanlık ve makine ekipmanlarının takibi sıklıkla kağıt üzerinde veya düzensiz mesajlaşma gruplarında kaybolmaktadır.

**Solnod F&S**, sahada çalışan satın alma ve teknik ikmal personelinin:
1. Malzemeyi tedarik anında yüksek çözünürlüklü fotoğraflamasını,
2. En, boy, yükseklik ve ağırlık (kg) ölçülerini hatasız girmesini,
3. Dinamik gemi ve proje yönetimi ile filodaki gemileri/projeleri tanımlayıp atamasını,
4. Çift tarih mekanizmasıyla (Tedarikçiden Alınan Tarih & Gemiye Gönderilen Tarih) lojistik sürecini adım adım izlemesini,
5. Çoklu para birimi desteğiyle (TL, USD, EUR, GBP, JPY, Dinar) maliyet takibi yapmasını,
6. WhatsApp üzerinden sahaya veya armatöre fotoğraflı operasyonel rapor göndermesini,
7. Muhasebe ve satın alma departmanı için Türkçe karakter korumalı (UTF-8 BOM) **Excel (.csv)** tabloları üretmesini

sağlayan kapsamlı ve güvenli bir operasyon platformudur.

---

## ✨ Temel Özellikler

### 🚢 1. Dinamik Gemi ve Proje Yönetimi
- **Kullanıcı Tanımlı Mimari**: Sabit gemi isimleri yerine kullanıcıya tam özgürlük sunar.
- **Detaylı Tanımlama**: Gemi/proje adı ile birlikte IMO No, Çağrı İşareti, Tersane, Bayrak gibi ek operasyonel detaylar kaydedilebilir.
- **Akıllı Akış**: Sistemde henüz gemi tanımlı değilse `+` butonuna basıldığında kullanıcı doğrudan gemi tanımlama formuna yönlendirilir ve kayıt tamamlandığında gemi otomatik seçilerek malzeme ekleme formuna aktarılır.
- **Hızlı Erişim**: Malzeme ekleme ve düzenleme ekranlarındaki açılır menüden tek dokunuşla yeni gemi/proje eklenebilir.
- **Gemi & Proje Yönetim Paneli**: Ayarlar ekranından kayıtlı tüm gemiler görüntülenebilir ve yönetilebilir.

### 📸 2. Akıllı Medya ve Galeri Entegrasyonu
- Sistem kamera ve PhotoPicker arayüzleri ile sıfır gecikmeli, %100 16 KB sayfa boyutu uyumlu hafif medya motoru.
- Çoklu fotoğraf çekimi ve galeriden görsel yükleme desteği.
- Cihaz galerisinde `Pictures/FS_Materials` altına otomatik ve güvenli senkronizasyon.
- Tam ekran (pinch-to-zoom / slide) interaktif görsel görüntüleyici.

### 🎙️ 3. Sesli Veri Girişi (Speech-to-Text)
- Saha koşullarında eldiven kullanımı veya ağır çalışma şartlarında eller serbest veri girişi.
- `SpeechRecognizer` API entegrasyonu ile malzeme adı ve detaylı açıklama alanlarını sesle otomatik doldurma.

### 📐 4. Boyutsal Ölçü ve Ağırlık Girişi
- En, Boy, Yükseklik ve Ağırlık (kg) için anlık veri giriş kontrolleri (`InputValidator`).
- Sevkiyat öncesi kargo ve vinç planlamasını kolaylaştıran yapı.

### 💰 5. Çoklu Döviz & Finans Takibi
- TL (₺), USD ($), EUR (€), GBP (£), JPY (¥), Dinar (د.ك) desteği.
- Kartlarda, detay ekranında, paylaşım metinlerinde ve Excel tablolarında tam senkronize döviz sembolü gösterimi.

### 🎨 6. Kişiselleştirilebilir Tema & Dinamik Mod
- **Sistem Varsayılanı (Otomatik)**: Uygulama ilk açıldığında doğrudan cihazın sistem açık/koyu temasını algılar ve dinamik olarak uyum sağlar.
- **Değiştirilebilir Kurumsal Renk Paletleri**:
  - 🔵 **Solnod Lacivert (Varsayılan)**: Kurumsal marine laciverdi ve altın aksanlar.
  - 🟢 **Okyanus Zümrüt**: Petrol yeşili, deniz köpüğü ve amber.
  - ⚪ **Kuzey Çeliği**: Kutup grisi, safir ve arduvaz mavisi.
  - 🟠 **Liman Gün Batımı**: Sıcak pişmiş toprak, bronz ve antrasit.
  - 🎨 **Dinamik (Material You)**: Android 12+ cihaz duvar kağıdı renkleriyle entegre olan dinamik palet.

### 📊 7. Gelişmiş Excel / CSV Raporlama
- Microsoft Excel ve Google Sheets ile %100 uyumlu UTF-8 BOM desteği (Türkçe Ş, Ğ, İ harfleri bozulmaz).
- 3 farklı rapor filtreleme modu:
  - **Tüm Kayıtları Aktar**
  - **Seçili Gemiye Göre Filtreli Aktar**
  - **İçinde Bulunulan Aya Göre Aktar**

### 💬 8. Akıllı WhatsApp Kademeli Paylaşım
- Android çoklu görsel gönderim limitlerine takılmayan akıllı algoritma:
  - 4 ve altı fotoğrafta standart metinli gönderim.
  - 4+ fotoğrafta metin karmaşasını önleyen kademeli (staged) intent yönetimi ve panoya otomatik kopyalama güvencesi.

### 🗑️ 9. Güvenli Çöp Kutusu (Soft Delete)
- Yanlışlıkla silinen kayıtlar için anında **Snackbar Geri Al** desteği.
- Ayrı Çöp Kutusu ekranından silinmiş malzemeleri inceleme, tek dokunuşla geri yükleme veya kalıcı olarak yok etme.
- **15 Günlük Otomatik Temizlik**: 15 günü geçmiş silinmiş kayıtlar veritabanını şişirmemek adına arka planda otomatik olarak kalıcı temizlenir.

### 🔒 10. Tam İzolasyon & Güvenlik
- Sıfır izinsiz bulut aktarımı. Veriler yerel SQLite/Room veritabanında (`solnod_fs_database`) izole alanda saklanır.

---

## 🏗️ Mimari & Teknoloji Yığını

```
com.onyxera.fs
├── data/               # Room Database, DAO, Entity, Type Converters
│   ├── FSDatabase.kt
│   ├── MaterialDao.kt
│   ├── MaterialEntity.kt
│   ├── ShipDao.kt
│   ├── ShipEntity.kt
│   └── Converters.kt
├── repository/         # Single Source of Truth Veri Katmanı
│   └── MaterialRepository.kt
├── viewmodel/          # MVVM StateFlow Yönetimi
│   ├── MaterialViewModel.kt
│   ├── AddMaterialViewModel.kt
│   ├── EditMaterialViewModel.kt
│   └── MaterialViewModelFactory.kt
├── ui/
│   ├── components/     # Modüler Arayüz Bileşenleri ve Diyaloglar
│   │   ├── AddChoiceBottomSheet.kt
│   │   ├── CreateShipDialog.kt
│   │   └── ShipManagerDialog.kt
│   ├── screens/        # Jetpack Compose Ekranları
│   │   ├── MainScreen.kt
│   │   ├── AddMaterialScreen.kt
│   │   ├── EditMaterialScreen.kt
│   │   ├── MaterialDetailScreen.kt
│   │   ├── SettingsScreen.kt
│   │   └── TrashScreen.kt
│   └── theme/          # Renk Paletleri, Tipografi ve Dinamik Tema
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── util/               # Yardımcı Modüller
    ├── CameraHelper.kt
    ├── SpeechHelper.kt
    ├── ShareHelper.kt
    ├── InputValidator.kt
    └── SolnodConstants.kt
```

- **Dil:** Kotlin 1.9
- **Arayüz:** Jetpack Compose + Material 3
- **Veritabanı:** Room 2.6.1 + Coroutines / Flow
- **Görsel Yükleme:** Coil Compose 2.6.0
- **Asenkron İşlemler:** Kotlin Coroutines & StateFlow
- **Minimum SDK:** Android 8.0 (API 26)
- **Hedef SDK:** Android 14 (API 34)

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Android Studio Hedgehog | Iguana | Jellyfish veya üzeri
- JDK 17
- Android SDK (API 34)

### Adımlar

1. Depoyu klonlayın:
   ```bash
   git clone https://github.com/Warkrone34/Solnod-FS-Find-Storage.git
   cd Solnod-FS-Find-Storage
   ```

2. Projeyi derleyin ve birim testlerini çalıştırın:
   ```bash
   ./gradlew test
   ./gradlew assembleDebug
   ```

3. Cihaza veya emülatöre yükleyin:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 Lisans

Bu proje **MIT Lisansı** altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakabilirsiniz.
