package com.onyxera.fs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dinamik Gemi ve Proje Tanımlama İletişim Kutusu (Dialog).
 * Kullanıcının kurumsal veya operasyonel gemi/proje adı ve ek detaylarını sisteme kaydetmesini sağlar.
 */
@Composable
fun CreateShipDialog(
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String, details: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var details by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.DirectionsBoat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Yeni Gemi / Proje Tanımla",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Malzeme kaydı oluşturmak için hedef gemi veya proje adını ve varsa ek operasyonel detaylarını giriniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Gemi / Proje Adı *") },
                    placeholder = { Text("Örn: Solnod Transporter") },
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Gemi / Proje adı boş bırakılamaz.", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Zorunlu alan")
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsBoat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Ek Detaylar (Opsiyonel)") },
                    placeholder = { Text("IMO No, Çağrı İşareti, Tersane, Bayrak...") },
                    minLines = 2,
                    maxLines = 4,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(name.trim(), details.trim())
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Kaydet ve İlerle", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Vazgeç")
            }
        }
    )
}
