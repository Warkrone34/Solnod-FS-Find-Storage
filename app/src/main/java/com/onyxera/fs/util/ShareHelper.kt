package com.onyxera.fs.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.onyxera.fs.data.MaterialEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Akıllı Medya Paylaşım Modülü.
 * Solnod operasyonel standartlarında kademeli WhatsApp ve sistem paylaşımı.
 */
object ShareHelper {

    fun shareMaterial(context: Context, item: MaterialEntity) {
        try {
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("tr", "TR"))
            val recordDate = dateFormat.format(Date(item.timestamp))

            val currencySymbol = SolnodConstants.getCurrencySymbol(item.currency)
            val priceText = if (item.price == 0.0) "Belirtilmedi" else "${item.price} $currencySymbol"
            val weightText = if (item.weightKg == 0.0) "Belirtilmedi" else "${item.weightKg} kg"

            val dimText = if (item.width == 0.0 && item.height == 0.0 && item.length == 0.0) {
                "Belirtilmedi"
            } else {
                "${if (item.width == 0.0) "-" else item.width} x ${if (item.height == 0.0) "-" else item.height} x ${if (item.length == 0.0) "-" else item.length}"
            }

            // Solnod Kurumsal Rapor Metni
            val shareText = """
                ⚓ Solnod F&S Malzeme Takip Sistemi ⚓
                -----------------------------------
                Gemi/Proje: ${item.shipName.ifEmpty { "Belirtilmedi" }}
                Malzeme Adı: ${item.materialName.ifEmpty { "Belirtilmedi" }}
                Açıklama: ${item.description.ifEmpty { "Belirtilmedi" }}
                Fiyat: $priceText
                Ağırlık: $weightText
                Boyut (G/E/B): $dimText
                Alınan Tarih: ${item.receivedDate.ifEmpty { "Belirtilmedi" }}
                Gönderilen Tarih: ${item.sentDate.ifEmpty { "Belirtilmedi" }}
                Kayıt Tarihi: $recordDate
                -----------------------------------
                ${SolnodConstants.COMPANY_NAME}
            """.trimIndent()

            val uriList = ArrayList<Uri>()
            item.photoUris.forEach { uriString ->
                if (uriString.isNotBlank()) {
                    uriList.add(Uri.parse(uriString))
                }
            }

            // Metni panoya kopyala
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Solnod F&S Rapor", shareText)
            clipboard.setPrimaryClip(clip)

            val shareIntent = if (uriList.size > 1) {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/jpeg"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)

                    if (uriList.size > 4) {
                        val textList = ArrayList<CharSequence>()
                        for (i in uriList.indices) {
                            if (i < 4) {
                                textList.add(shareText)
                            } else {
                                textList.add("")
                            }
                        }
                        putCharSequenceArrayListExtra(Intent.EXTRA_TEXT, textList)
                        Toast.makeText(context, "Kademeli paylaşım hazır. Metin ayrıca panoya kopyalandı.", Toast.LENGTH_LONG).show()
                    } else {
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else if (uriList.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uriList.first())
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
            }

            context.startActivity(Intent.createChooser(shareIntent, "Solnod F&S Raporunu Paylaş"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Paylaşım sırasında teknik bir hata oluştu.", Toast.LENGTH_SHORT).show()
        }
    }
}