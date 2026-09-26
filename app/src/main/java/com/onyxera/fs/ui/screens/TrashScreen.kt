package com.onyxera.fs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.viewmodel.MaterialViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Geri Dönüşüm / Çöp Kutusu Ekranı.
 * GÜNCELLEME: UX iyileştirmesi yapılarak "Geri Getir" ve "Kalıcı Sil" butonları
 * belirginleştirildi ve yanlış tıklamaları önlemek için metin destekli butonlara çevrildi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: MaterialViewModel,
    onNavigateBack: () -> Unit
) {
    val trashedItems by viewModel.trashedMaterials.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Çöp Kutusu", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri Dön")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                // GÜNCELLENDİ: Güvenli alan (SafeArea) eklendi
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (trashedItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Çöp kutusu boş.\nSilinen öğeler 15 gün boyunca burada tutulur.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "Bu listedeki öğeler 15 gün sonra kalıcı olarak silinecektir.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trashedItems, key = { it.id }) { item ->
                        TrashCard(
                            item = item,
                            onRestore = { viewModel.restoreFromTrash(listOf(item)) },
                            onDeletePermanently = { viewModel.deletePermanently(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrashCard(item: MaterialEntity, onRestore: () -> Unit, onDeletePermanently: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("tr", "TR"))
    val deleteDateString = if (item.deletedTimestamp > 0L) dateFormat.format(Date(item.deletedTimestamp)) else "Bilinmiyor"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.materialName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Gemi: ${item.shipName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Silinme: $deleteDateString", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GÜNCELLENDİ: "Geri Getir" ve "Kalıcı Sil" butonları UX kurallarına göre yeniden düzenlendi.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDeletePermanently,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "Kalıcı Sil", modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                FilledTonalButton(
                    onClick = onRestore,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = "Geri Getir", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Geri Getir", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}