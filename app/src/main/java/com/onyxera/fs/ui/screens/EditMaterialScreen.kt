package com.onyxera.fs.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.util.CameraHelper
import com.onyxera.fs.util.SpeechHelper
import com.onyxera.fs.data.ShipEntity
import com.onyxera.fs.ui.components.CreateShipDialog
import com.onyxera.fs.viewmodel.EditMaterialViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialScreen(
    materialToEdit: MaterialEntity,
    viewModel: EditMaterialViewModel,
    ships: List<ShipEntity> = emptyList(),
    onAddNewShip: ((name: String, details: String, onCreated: (ShipEntity) -> Unit) -> Unit)? = null,
    speechHelper: SpeechHelper,
    cameraHelper: CameraHelper,
    onSave: (MaterialEntity) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(materialToEdit) {
        viewModel.loadMaterial(materialToEdit)
    }

    val shipName by viewModel.shipName.collectAsState()
    val materialName by viewModel.materialName.collectAsState()
    val description by viewModel.description.collectAsState()
    val receivedDate by viewModel.receivedDate.collectAsState()
    val sentDate by viewModel.sentDate.collectAsState()
    val priceStr by viewModel.priceStr.collectAsState()
    val currentCurrency by viewModel.currency.collectAsState()
    val weightStr by viewModel.weightStr.collectAsState()
    val widthStr by viewModel.widthStr.collectAsState()
    val heightStr by viewModel.heightStr.collectAsState()
    val lengthStr by viewModel.lengthStr.collectAsState()
    val photoUris by viewModel.photoUris.collectAsState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var fullScreenImageUri by remember { mutableStateOf<Uri?>(null) }

    var showCameraRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsRedirectDialog by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            // ÇÖZÜM: Karmaşık bulut/depolama mantığı iptal edildi, sadece yerel galeriye kaydediliyor.
            cameraHelper.savePhotoToPublicGallery(tempCameraUri!!)
            viewModel.addPhotoUri(tempCameraUri!!)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            tempCameraUri = cameraHelper.createTempPhotoUri()
            tempCameraUri?.let { takePictureLauncher.launch(it) }
        } else {
            showSettingsRedirectDialog = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        uris.forEach { uri -> viewModel.addPhotoUri(uri) }
    }

    var activeSpeechField by remember { mutableStateOf<String?>(null) }
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            speechHelper.startListening(
                onResult = { text ->
                    if (activeSpeechField == "name") {
                        viewModel.updateMaterialName(text)
                    } else if (activeSpeechField == "description") {
                        viewModel.updateDescription(text)
                    }
                    activeSpeechField = null
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    activeSpeechField = null
                }
            )
        } else {
            showSettingsRedirectDialog = true
            activeSpeechField = null
        }
    }

    if (showSettingsRedirectDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsRedirectDialog = false },
            title = { Text("Erişim İzni Gerekiyor") },
            text = { Text("Solnod F&S uygulamasının düzgün çalışabilmesi için cihaz ayarlarından uygulamanın izinlerini (Kamera/Mikrofon) açmanız gerekmektedir.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsRedirectDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Ayarlara Git", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsRedirectDialog = false }) { Text("İptal") }
            }
        )
    }

    if (showCameraRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showCameraRationaleDialog = false },
            title = { Text("Kamera Erişimi") },
            text = { Text("Solnod F&S sistemi, sahadaki malzemeleri sisteme anında kaydedebilmeniz için cihazınızın kamerasına erişmek istiyor.") },
            confirmButton = {
                Button(onClick = {
                    showCameraRationaleDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("İzin Ver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraRationaleDialog = false }) { Text("Daha Sonra") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaydı Düzenle", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "İptal Et")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (materialName.isBlank()) {
                        Toast.makeText(context, "Lütfen bir malzeme adı giriniz.", Toast.LENGTH_SHORT).show()
                    } else {
                        onSave(viewModel.buildUpdatedEntity())
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = { Icon(Icons.Default.Save, contentDescription = "Değişiklikleri Kaydet") },
                text = { Text("Değişiklikleri Kaydet", fontWeight = FontWeight.Bold) },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp, pressedElevation = 16.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.navigationBarsPadding()
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PermMedia, contentDescription = "Medya", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Medya Güncelleme (${photoUris.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (photoUris.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(photoUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(uri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Fotoğraf",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { fullScreenImageUri = uri }
                                    )
                                    IconButton(
                                        onClick = { viewModel.removePhotoUri(uri) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(26.dp)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.7f), shape = CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Sil", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                if (isGranted) {
                                    tempCameraUri = cameraHelper.createTempPhotoUri()
                                    tempCameraUri?.let { takePictureLauncher.launch(it) }
                                } else {
                                    showCameraRationaleDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kamera", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Galeri", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            var expandedShip by remember { mutableStateOf(false) }
            var showInlineShipDialog by remember { mutableStateOf(false) }

            if (showInlineShipDialog) {
                CreateShipDialog(
                    onDismiss = { showInlineShipDialog = false },
                    onConfirm = { name, details ->
                        showInlineShipDialog = false
                        if (onAddNewShip != null) {
                            onAddNewShip(name, details) { createdShip ->
                                viewModel.updateShipName(createdShip.name)
                            }
                        } else {
                            viewModel.updateShipName(name)
                        }
                    }
                )
            }

            ExposedDropdownMenuBox(
                expanded = expandedShip,
                onExpandedChange = { expandedShip = !expandedShip }
            ) {
                OutlinedTextField(
                    value = shipName,
                    onValueChange = { viewModel.updateShipName(it) },
                    label = { Text("Gemi / Proje Adı") },
                    placeholder = { Text("Listeden seçin veya yeni ekleyin") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedShip) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedShip,
                    onDismissRequest = { expandedShip = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Yeni Gemi / Proje Ekle...",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        onClick = {
                            expandedShip = false
                            showInlineShipDialog = true
                        }
                    )

                    if (ships.isNotEmpty()) {
                        HorizontalDivider()
                    }

                    ships.forEach { ship ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(ship.name, fontWeight = FontWeight.SemiBold)
                                    if (ship.details.isNotBlank()) {
                                        Text(
                                            ship.details,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.updateShipName(ship.name)
                                expandedShip = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = materialName,
                onValueChange = { viewModel.updateMaterialName(it) },
                label = { Text("Malzeme Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        activeSpeechField = "name"
                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }) {
                        Icon(
                            imageVector = if (activeSpeechField == "name") Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Sesle Yazdır",
                            tint = if (activeSpeechField == "name") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DatePickerFieldEdit(label = "Alınan Tarih", selectedDate = receivedDate, onDateSelected = { viewModel.updateReceivedDate(it) }, modifier = Modifier.weight(1f))
                DatePickerFieldEdit(label = "Gönderilen Tarih", selectedDate = sentDate, onDateSelected = { viewModel.updateSentDate(it) }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Detaylı Açıklama") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        activeSpeechField = "description"
                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }) {
                        Icon(
                            imageVector = if (activeSpeechField == "description") Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Sesle Yazdır",
                            tint = if (activeSpeechField == "description") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            var currencyExpanded by remember { mutableStateOf(false) }
            val currencies = com.onyxera.fs.util.SolnodConstants.CURRENCIES

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { viewModel.updatePrice(it) },
                    label = { Text("Fiyat") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Box {
                            TextButton(onClick = { currencyExpanded = true }) {
                                Text(currentCurrency, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            DropdownMenu(expanded = currencyExpanded, onDismissRequest = { currencyExpanded = false }) {
                                currencies.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(currency) },
                                        onClick = {
                                            viewModel.updateCurrency(currency)
                                            currencyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                OutlinedTextField(value = weightStr, onValueChange = { viewModel.updateWeight(it) }, label = { Text("Ağırlık (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp))
            }

            Text("Ebatlar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = widthStr, onValueChange = { viewModel.updateWidth(it) }, label = { Text("Genişlik") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = lengthStr, onValueChange = { viewModel.updateLength(it) }, label = { Text("Boy") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = heightStr, onValueChange = { viewModel.updateHeight(it) }, label = { Text("Yükseklik") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (fullScreenImageUri != null) {
        Dialog(
            onDismissRequest = { fullScreenImageUri = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fullScreenImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Tam Ekran Görsel",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { fullScreenImageUri = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFieldEdit(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.clickable { showDialog = true }) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            placeholder = { Text("Seçiniz") },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Tarih Seç") },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR")).format(Date(millis))
                        onDateSelected(formattedDate)
                    }
                    showDialog = false
                }) {
                    Text("Onayla", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}