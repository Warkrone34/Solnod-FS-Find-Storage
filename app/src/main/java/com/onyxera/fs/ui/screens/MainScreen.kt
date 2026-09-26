package com.onyxera.fs.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.util.ShareHelper
import com.onyxera.fs.util.SolnodConstants
import com.onyxera.fs.viewmodel.MaterialViewModel
import com.onyxera.fs.viewmodel.SortType
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

import com.onyxera.fs.ui.components.AddChoiceBottomSheet
import com.onyxera.fs.ui.components.CreateShipDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MaterialViewModel,
    onAddClick: (String?) -> Unit,
    onItemClick: (MaterialEntity) -> Unit,
    onSettingsClick: () -> Unit
) {
    val materials by viewModel.materials.collectAsState()
    val ships by viewModel.allShips.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val selectedShip by viewModel.selectedShip.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val selectedItems = remember { mutableStateListOf<MaterialEntity>() }

    var showAddChoiceSheet by remember { mutableStateOf(false) }
    var showAddShipDialog by remember { mutableStateOf(false) }

    // Kullanıcının tanımladığı gemiler ile veritabanındaki dinamik gemi isimlerinin birleşimi
    val shipOptions = remember(ships, materials) {
        (ships.map { it.name } + materials.map { it.shipName })
            .filter { it.isNotBlank() && it != "Belirtilmedi" }
            .distinct()
    }

    if (showAddChoiceSheet) {
        AddChoiceBottomSheet(
            onDismiss = { showAddChoiceSheet = false },
            onAddMaterialClick = {
                onAddClick(null)
            },
            onAddShipClick = {
                showAddShipDialog = true
            }
        )
    }

    if (showAddShipDialog) {
        CreateShipDialog(
            onDismiss = { showAddShipDialog = false },
            onConfirm = { name, details ->
                viewModel.insertShip(name, details) { newShip ->
                    showAddShipDialog = false
                    onAddClick(newShip.name)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (selectedItems.isEmpty()) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBoat,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Solnod F&S",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Ayarlar", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            } else {
                TopAppBar(
                    title = { Text(text = "${selectedItems.size} Öğe Seçildi", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { selectedItems.clear() }) { Icon(Icons.Default.Close, contentDescription = "İptal Et") }
                    },
                    actions = {
                        IconButton(onClick = {
                            val itemsToExport = selectedItems.toList()
                            exportToExcel(context, itemsToExport)
                            selectedItems.clear()
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Excel Olarak İndir", tint = MaterialTheme.colorScheme.onSurface)
                        }

                        IconButton(onClick = {
                            val itemsToTrash = selectedItems.toList()
                            val count = itemsToTrash.size
                            viewModel.moveToTrash(itemsToTrash)
                            selectedItems.clear()

                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "$count kayıt çöp kutusuna taşındı.",
                                    actionLabel = "GERİ AL",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.restoreFromTrash(itemsToTrash)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Çöpe Taşı", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, titleContentColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (ships.isEmpty()) {
                        showAddShipDialog = true
                    } else {
                        showAddChoiceSheet = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Yeni Kayıt Ekle", modifier = Modifier.size(28.dp))
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(com.onyxera.fs.util.InputValidator.limitText(it, com.onyxera.fs.util.InputValidator.MAX_SEARCH_LENGTH)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Gemi, malzeme veya açıklama ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara", tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) { Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = MaterialTheme.colorScheme.outline) }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline),
                singleLine = true
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    var shipMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        FilterChip(
                            selected = selectedShip != null,
                            onClick = { shipMenuExpanded = true },
                            label = { Text(selectedShip ?: "Tüm Gemiler") },
                            leadingIcon = if (selectedShip != null) {
                                { Icon(Icons.Default.DirectionsBoat, Icons.Default.DirectionsBoat.name, Modifier.size(18.dp)) }
                            } else null,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, Modifier.size(18.dp)) }
                        )
                        DropdownMenu(expanded = shipMenuExpanded, onDismissRequest = { shipMenuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Tüm Gemiler", fontWeight = if (selectedShip == null) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { viewModel.updateSelectedShip(null); shipMenuExpanded = false }
                            )
                            if (shipOptions.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Henüz gemi tanımlanmadı", color = MaterialTheme.colorScheme.outline) },
                                    onClick = { },
                                    enabled = false
                                )
                            } else {
                                shipOptions.forEach { ship ->
                                    DropdownMenuItem(
                                        text = { Text(ship, fontWeight = if (selectedShip == ship) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = { viewModel.updateSelectedShip(ship); shipMenuExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    FilterChip(
                        selected = sortType == SortType.DATE_DESC,
                        onClick = { viewModel.updateSortType(SortType.DATE_DESC) },
                        label = { Text("En Yeni") },
                        leadingIcon = { Icon(Icons.Default.History, null, Modifier.size(18.dp)) }
                    )
                }

                item {
                    FilterChip(
                        selected = sortType == SortType.PRICE_DESC,
                        onClick = { viewModel.updateSortType(SortType.PRICE_DESC) },
                        label = { Text("En Pahalı") },
                        leadingIcon = { Icon(Icons.Default.Payments, null, Modifier.size(18.dp)) }
                    )
                }

                item {
                    FilterChip(
                        selected = sortType == SortType.WEIGHT_DESC,
                        onClick = { viewModel.updateSortType(SortType.WEIGHT_DESC) },
                        label = { Text("En Ağır") },
                        leadingIcon = { Icon(Icons.Default.Scale, null, Modifier.size(18.dp)) }
                    )
                }

                item {
                    FilterChip(
                        selected = sortType == SortType.SHIP_NAME,
                        onClick = { viewModel.updateSortType(SortType.SHIP_NAME) },
                        label = { Text("Gemi Adı (A-Z)") }
                    )
                }
            }

            if (materials.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedShip != null) "Aramanıza uygun malzeme bulunamadı." else "Henüz kayıtlı malzeme yok.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Yeni bir operasyon malzemesi kaydetmek için aşağıdaki + butonuna dokunun.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(materials, key = { it.id }) { item ->
                        val isSelected = selectedItems.contains(item)
                        MaterialCard(
                            item = item,
                            isSelected = isSelected,
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    if (isSelected) selectedItems.remove(item) else selectedItems.add(item)
                                } else {
                                    onItemClick(item)
                                }
                            },
                            onLongClick = {
                                if (isSelected) selectedItems.remove(item) else selectedItems.add(item)
                            },
                            onShareClick = { ShareHelper.shareMaterial(context, item) }
                        )
                    }
                }
            }
        }
    }
}

fun exportToExcel(context: Context, items: List<MaterialEntity>) {
    try {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_HHmm", Locale("tr", "TR"))
        val fileName = "Solnod_FS_Rapor_${dateFormat.format(Date())}.csv"

        val exportFile = File(context.cacheDir, fileName)
        val fileOutputStream = FileOutputStream(exportFile)

        // BOM (Byte Order Mark) - Türkçe karakterlerin (Ş, Ğ, İ) Excel'de bozulmasını önler
        fileOutputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

        val headers = "Gemi Adı;Malzeme Adı;Fiyat;Ağırlık (kg);Ebatlar (G/E/B);Alınan Tarih;Gönderilen Tarih;Açıklama\n"
        fileOutputStream.write(headers.toByteArray())

        items.forEach { item ->
            val shipName = "\"${item.shipName.replace("\"", "\"\"").replace(";", ",")}\""
            val matName = "\"${item.materialName.replace("\"", "\"\"").replace(";", ",")}\""
            val desc = "\"${item.description.replace("\"", "\"\"").replace("\n", " ").replace(";", ",")}\""
            val dims = "\"${item.width}x${item.height}x${item.length}\""

            val rDate = "\" ${item.receivedDate}\""
            val sDate = "\" ${item.sentDate}\""

            val symbol = SolnodConstants.getCurrencySymbol(item.currency)
            val priceFormat = "\"${item.price} $symbol\""

            val row = "$shipName;$matName;$priceFormat;${item.weightKg};$dims;$rDate;$sDate;$desc\n"
            fileOutputStream.write(row.toByteArray())
        }

        fileOutputStream.flush()
        fileOutputStream.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", exportFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Excel Raporunu Aktar"))
        Toast.makeText(context, "Sütunlu Rapor başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Rapor oluşturulurken hata oluştu!", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaterialCard(
    item: MaterialEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale("tr", "TR"))
    val dateString = dateFormat.format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                if (item.photoUris.isNotEmpty() && item.photoUris.first().isNotBlank()) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(item.photoUris.first()).crossfade(true).build(), contentDescription = "Malzeme Önizleme", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.ImageNotSupported, contentDescription = "Görsel Yok", tint = MaterialTheme.colorScheme.outline)
                }
                if (isSelected) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Seçili", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.materialName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBoat, contentDescription = null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = item.shipName, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateString, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (item.price > 0.0) {
                            val symbol = SolnodConstants.getCurrencySymbol(item.currency)
                            Text(text = "${item.price} $symbol", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(text = "${item.weightKg} kg", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            if (!isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onShareClick) { Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = MaterialTheme.colorScheme.tertiary) }
            }
        }
    }
}