package com.example.util

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.data.model.Personnel

object CallHelper {

    /**
     * Cleans phone number to numeric digits
     */
    fun cleanPhoneNumber(raw: String): String {
        return raw.filter { it.isDigit() }
    }

    /**
     * Initiates direct call if permission is granted, otherwise opens phone dialer
     */
    fun initiateCall(context: Context, rawNumber: String) {
        val number = cleanPhoneNumber(rawNumber)
        if (number.isBlank()) {
            Toast.makeText(context, "मोबाइल नंबर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val hasCallPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            if (hasCallPermission) {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$number")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
            } else {
                // Fallback to instant dialer
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
            }
        } catch (e: Exception) {
            // Safe fallback
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "कॉल करने में त्रुटि: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Opens system dialer directly
     */
    fun openDialer(context: Context, rawNumber: String) {
        val number = cleanPhoneNumber(rawNumber)
        if (number.isBlank()) {
            Toast.makeText(context, "मोबाइल नंबर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$number")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "डायलर खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens WhatsApp chat
     */
    fun openWhatsApp(context: Context, rawNumber: String) {
        val number = cleanPhoneNumber(rawNumber)
        if (number.isBlank()) {
            Toast.makeText(context, "मोबाइल नंबर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }
        val formattedNumber = if (number.length == 10) "91$number" else number
        try {
            val uri = Uri.parse("https://wa.me/$formattedNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens SMS app
     */
    fun sendSms(context: Context, rawNumber: String, defaultMessage: String = "") {
        val number = cleanPhoneNumber(rawNumber)
        if (number.isBlank()) {
            Toast.makeText(context, "मोबाइल नंबर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$number")
                if (defaultMessage.isNotBlank()) {
                    putExtra("sms_body", defaultMessage)
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "एसएमएस भेजने में असमर्थ", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Copies phone number to clipboard
     */
    fun copyToClipboard(context: Context, text: String, label: String = "मोबाइल नंबर") {
        if (text.isBlank()) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label कॉपी कर लिया गया", Toast.LENGTH_SHORT).show()
    }

    /**
     * Shares contact details via standard share sheet
     */
    fun shareContact(context: Context, personnel: Personnel) {
        val shareText = buildString {
            appendLine("★ जनपद अमेठी पुलिस CCTNS डायरेक्टरी ★")
            appendLine("नाम: ${personnel.name}")
            appendLine("पद: ${personnel.designation}")
            appendLine("थाना: ${personnel.thanaName}")
            appendLine("मोबाइल: ${personnel.mobileNumber}")
            if (personnel.cugNumber.isNotBlank()) {
                appendLine("CUG नंबर: ${personnel.cugNumber}")
            }
            if (personnel.dutyLocation.isNotBlank()) {
                appendLine("ड्यूटी कक्ष: ${personnel.dutyLocation}")
            }
            if (personnel.shiftTiming.isNotBlank()) {
                appendLine("शिफ्ट: ${personnel.shiftTiming}")
            }
        }
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "${personnel.name} - ${personnel.designation}")
                putExtra(Intent.EXTRA_TEXT, shareText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "संपर्क विवरण साझा करें"))
        } catch (e: Exception) {
            Toast.makeText(context, "साझा करने में असमर्थ", Toast.LENGTH_SHORT).show()
        }
    }
}
