package com.onyxera.fs.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onyxera.fs.util.SecurityUtils

/**
 * Solnod F&S Güvenlik ve Bütünlük Denetim Paneli.
 * Cihazın güvenlik durumunu, SHA-256 parmak izini ve tersine mühendislik korumalarını gösterir.
 */
@Composable
fun SecurityAuditDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val securityReport = SecurityUtils.evaluateSecurity(context)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = if (securityReport.isEnvironmentSecure) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Güvenlik & Bütünlük Denetimi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Solnod F&S, sahadaki hassas lojistik ve fiyatlandırma verilerini korumak için SHA-256 ve tersine mühendislik önlemleriyle donatılmıştır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                // 1. SHA-256 Sertifika Parmak İzi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("APK İmza SHA-256 Özeti", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = securityReport.certificateSha256,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("SHA-256", securityReport.certificateSha256)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "SHA-256 parmak izi panoya kopyalandı.", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kopyala", fontSize = 12.sp)
                        }
                    }
                }

                // 2. Güvenlik Denetim Listesi
                SecurityAuditRow(
                    icon = Icons.Default.Shield,
                    title = "Root / Yetkisiz Erişim",
                    statusText = if (securityReport.isRooted) "Tehdit: Cihaz Rootlu" else "Güvenli (Root Yok)",
                    isSafe = !securityReport.isRooted
                )

                SecurityAuditRow(
                    icon = Icons.Default.BugReport,
                    title = "Debugger & Bellek Kancalama",
                    statusText = if (securityReport.isDebuggerAttached || securityReport.isHookDetected) "Uyarı: Müdahale Algılandı" else "Güvenli (Frida/Xposed Yok)",
                    isSafe = !securityReport.isDebuggerAttached && !securityReport.isHookDetected
                )

                SecurityAuditRow(
                    icon = Icons.Default.Lock,
                    title = "Ağ Trafiği (TLS / HTTPS)",
                    statusText = "Cleartext HTTP Engellendi (Zorunlu TLS)",
                    isSafe = true
                )

                SecurityAuditRow(
                    icon = Icons.Default.Code,
                    title = "R8 / ProGuard Obfuscation",
                    statusText = "Kod Karıştırma & Log Ayıklama Aktif",
                    isSafe = true
                )

                SecurityAuditRow(
                    icon = Icons.Default.ScreenLockPortrait,
                    title = "Ekran Gizliliği (FLAG_SECURE)",
                    statusText = "Ekran Görüntüsü & Kayıt Koruması",
                    isSafe = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Anladım", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun SecurityAuditRow(
    icon: ImageVector,
    title: String,
    statusText: String,
    isSafe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSafe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSafe) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
            )
        }
        Icon(
            imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isSafe) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
    }
}
