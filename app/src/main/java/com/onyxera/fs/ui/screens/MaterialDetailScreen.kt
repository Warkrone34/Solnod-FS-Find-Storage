package com.onyxera.fs.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.util.ShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MaterialDetailScreen(
    item: MaterialEntity,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val validPhotos = item.photoUris.filter { it.isNotBlank() }
    val pagerState = rememberPagerState(pageCount = { if (validPhotos.isEmpty()) 1 else validPhotos.size })

    var fullScreenInitialPage by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Malzeme Detayı", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri Dön")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Kaydı Düzenle", tint = MaterialTheme.colorScheme.onPrimary)
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
            // ÇÖZÜM: ExtendedFloatingActionButton (Yazılı büyük buton) yerine,
            // sadece ikon içeren standart FloatingActionButton (Küçük dairesel buton) kullanıldı.
            FloatingActionButton(
                onClick = { ShareHelper.shareMaterial(context, item) },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Share, contentDescription = "Paylaş")
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Medya Görüntüleyici
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (validPhotos.isNotEmpty()) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(validPhotos[page])
                                .crossfade(true)
                                .build(),
                            contentDescription = "Malzeme Fotoğrafı",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { fullScreenInitialPage = page }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} / ${validPhotos.size}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ImageNotSupported, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Görsel Bulunamadı", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // Veri Detayları
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = item.materialName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow("Gemi / Proje", item.shipName)
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        DetailRow("Açıklama", item.description.ifEmpty { "Belirtilmedi" })
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) { DetailRow("Alınan Tarih", item.receivedDate) }
                            Column(modifier = Modifier.weight(1f)) { DetailRow("Gönderilen Tarih", item.sentDate) }
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val currencySymbol = com.onyxera.fs.util.SolnodConstants.getCurrencySymbol(item.currency)
                            Column(modifier = Modifier.weight(1f)) { DetailRow("Fiyat", "${item.price} $currencySymbol") }
                            Column(modifier = Modifier.weight(1f)) { DetailRow("Ağırlık", "${item.weightKg} kg") }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        DetailRow("Ebatlar (G x E x B)", "${item.width} x ${item.height} x ${item.length}")
                    }
                }

                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("tr", "TR"))
                Text(text = "Sisteme Kayıt: ${dateFormat.format(Date(item.timestamp))}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (fullScreenInitialPage != null) {
        val fullScreenPagerState = rememberPagerState(
            initialPage = fullScreenInitialPage!!,
            pageCount = { validPhotos.size }
        )

        Dialog(
            onDismissRequest = { fullScreenInitialPage = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

                HorizontalPager(state = fullScreenPagerState, modifier = Modifier.fillMaxSize()) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(validPhotos[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = "Tam Ekran Görsel",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = { fullScreenInitialPage = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                Text(
                    text = "${fullScreenPagerState.currentPage + 1} / ${validPhotos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}