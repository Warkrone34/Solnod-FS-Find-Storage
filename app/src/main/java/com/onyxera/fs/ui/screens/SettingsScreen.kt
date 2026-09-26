package com.onyxera.fs.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DirectionsBoat
import com.onyxera.fs.ui.components.CreateShipDialog
import com.onyxera.fs.ui.components.ShipManagerDialog
import com.onyxera.fs.ui.theme.AppThemeColor
import com.onyxera.fs.ui.theme.AppThemeMode
import com.onyxera.fs.util.SolnodConstants
import com.onyxera.fs.viewmodel.MaterialViewModel
import kotlinx.coroutines.launch

/**
 * Solnod Kurumsal Ayarlar, Tema ve Güvenlik Ekranı.
 * Açık/Koyu mod (sistem varsayılanı destekli) ve değiştirilebilir kurumsal renk paletleri.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MaterialViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTrash: () -> Unit,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    themeColor: AppThemeColor,
    onThemeColorChange: (AppThemeColor) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val ships by viewModel.allShips.collectAsState()
    var expandedShip by remember { mutableStateOf(false) }
    var selectedExportShip by remember { mutableStateOf("") }

    // ships güncellendiğinde seçili gemiyi ilk gemi yap (eğer henüz seçilmediyse)
    LaunchedEffect(ships) {
        if (selectedExportShip.isBlank() && ships.isNotEmpty()) {
            selectedExportShip = ships.first().name
        }
    }

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showShipManagerDialog by remember { mutableStateOf(false) }
    var showCreateShipDialog by remember { mutableStateOf(false) }

    if (showShipManagerDialog) {
        ShipManagerDialog(
            ships = ships,
            onDismiss = { showShipManagerDialog = false },
            onDeleteShip = { ship -> viewModel.deleteShip(ship) },
            onAddNewShip = { showCreateShipDialog = true }
        )
    }

    if (showCreateShipDialog) {
        CreateShipDialog(
            onDismiss = { showCreateShipDialog = false },
            onConfirm = { name, details ->
                viewModel.insertShip(name, details)
                showCreateShipDialog = false
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Veri Güvenliği Politikası",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "${SolnodConstants.COMPANY_NAME} operasyon standartları gereği, girdiğiniz tüm veriler (fotoğraf, ölçü, fiyat ve kilo) izole bir ortamda, yalnızca cihazınızın yerel veritabanında (Room DB) saklanır.\n\n" +
                                "1. Veri Toplama ve Kullanım:\n" +
                                "Uygulamamız, operasyonel envanter ve malzeme takibi amacıyla yalnızca kullanıcı tarafından manuel girilen veya kamerayla çekilen verileri işler. Arka planda gizli veri toplanmaz.\n\n" +
                                "2. Yerel Depolama:\n" +
                                "Kaydettiğiniz medya dosyaları güvenlik amacıyla yalnızca cihazın dahili hafızasına ve seçilirse Fotoğraflar galerisine aktarılır. Buluta izinsiz veri sızdırılmaz.\n\n" +
                                "3. Dışa Aktarım ve Paylaşım:\n" +
                                "Verileriniz yalnızca 'Paylaş' veya 'Excel Aktar' işlevleri üzerinden kullanıcı iradesiyle dışa aktarılır.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("Okudum, Onaylıyorum", fontWeight = FontWeight.Bold) }
            }
        )
    }

    // EXCEL RAPOR DIŞA AKTARMA (EXPORT) MENÜSÜ
    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Excel Raporu Oluştur", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Text("Veritabanındaki kayıtları nasıl filtrelemek istiyorsunuz?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // 1. TÜMÜNÜ AKTAR
                Button(
                    onClick = {
                        showExportSheet = false
                        coroutineScope.launch {
                            val allData = viewModel.getExportDataAll()
                            if (allData.isNotEmpty()) {
                                exportToExcel(context, allData)
                            } else {
                                Toast.makeText(context, "Aktarılacak veri bulunamadı.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Tüm Kayıtları Aktar", fontWeight = FontWeight.Bold) }

                // 2. GEMİYE GÖRE AKTAR
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expandedShip,
                        onExpandedChange = { expandedShip = !expandedShip },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = if (selectedExportShip.isBlank()) "Gemi seçin..." else selectedExportShip,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedShip) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                        ExposedDropdownMenu(expanded = expandedShip, onDismissRequest = { expandedShip = false }) {
                            if (ships.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Henüz gemi tanımlanmamış", color = MaterialTheme.colorScheme.outline) },
                                    onClick = { },
                                    enabled = false
                                )
                            } else {
                                ships.forEach { ship ->
                                    DropdownMenuItem(
                                        text = { Text(ship.name, fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            selectedExportShip = ship.name
                                            expandedShip = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedExportShip.isBlank()) {
                                Toast.makeText(context, "Lütfen önce bir gemi seçin.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            showExportSheet = false
                            coroutineScope.launch {
                                val shipData = viewModel.getExportDataByShip(selectedExportShip)
                                if (shipData.isNotEmpty()) {
                                    exportToExcel(context, shipData)
                                } else {
                                    Toast.makeText(context, "$selectedExportShip gemisi için veri bulunamadı.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Aktar", fontWeight = FontWeight.Bold) }
                }

                // 3. AYA GÖRE AKTAR
                Button(
                    onClick = {
                        showExportSheet = false
                        coroutineScope.launch {
                            val monthData = viewModel.getExportDataByCurrentMonth()
                            if (monthData.isNotEmpty()) {
                                exportToExcel(context, monthData)
                            } else {
                                Toast.makeText(context, "Bu aya ait veri bulunamadı.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Sadece Bu Ayı Aktar", fontWeight = FontWeight.Bold) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sistem Ayarları", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri Dön") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle(title = "Arayüz ve Tema Tercihleri")

            // TEMA MODU SEÇİMİ (Sistem Varsayılanı / Açık / Koyu)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Görünüm Modu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    AppThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onThemeModeChange(mode) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChange(mode) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (themeMode == mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // TEMA RENK PALETİ SEÇİMİ
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Kurumsal Renk Paleti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    AppThemeColor.entries.forEach { colorOption ->
                        val isSelected = themeColor == colorOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable { onThemeColorChange(colorOption) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(colorOption.previewColor)
                                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = colorOption.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Seçili", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

            SectionTitle(title = "Veri Yönetimi ve Raporlama")

            SettingItemClickable(
                icon = Icons.Default.DirectionsBoat,
                title = "Gemi ve Proje Yönetimi",
                description = "Kayıtlı gemileri görüntüle, yeni gemi tanımla veya listeden kaldır.",
                onClick = { showShipManagerDialog = true }
            )
            SettingItemClickable(icon = Icons.Default.Download, title = "Gelişmiş Excel Raporu (.csv)", description = "Sistemdeki verileri Gemi ve Ay bazlı filtreleyerek tabloya dökün.", onClick = { showExportSheet = true })
            SettingItemClickable(icon = Icons.Default.DeleteSweep, title = "Çöp Kutusu", description = "Silinen operasyon kayıtlarını kurtar veya kalıcı olarak temizle.", onClick = onNavigateToTrash)

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

            SectionTitle(title = "Sistem ve Güvenlik")
            SettingItemClickable(icon = Icons.Default.Lock, title = "Gizlilik Sözleşmesi", description = "Uygulama veri işleme standartlarını görüntüle.", onClick = { showPrivacyDialog = true })
            SettingItemClickable(icon = Icons.Default.Info, title = "Cihaz İzinleri", description = "Donanım (Kamera, Mikrofon) erişim yetkilerini (Fallback) yönet.", onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            })

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = SolnodConstants.COMPANY_NAME.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Find & Storage (F&S) • Version 2.0.0\nSolnod Operasyonel Envanter Çözümü", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title.uppercase(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
}

@Composable
fun SettingItemClickable(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
        }
        TextButton(onClick = onClick, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) { Text("İncele", fontWeight = FontWeight.Bold) }
    }
}