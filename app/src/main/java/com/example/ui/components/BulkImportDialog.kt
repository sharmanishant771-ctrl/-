package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActiveGreen
import com.example.util.CsvHelper

@Composable
fun BulkImportDialog(
    onDismiss: () -> Unit,
    onOpenTemplate: () -> Unit,
    onImport: (String, (Int) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var csvText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var previewCount by remember { mutableStateOf(0) }

    // File picker launcher for CSV files
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = stream.bufferedReader(Charsets.UTF_8).readText()
                    csvText = text
                    val parsed = CsvHelper.parseCsv(text)
                    previewCount = parsed.size
                    statusMessage = "फ़ाइल से ${parsed.size} रिकॉर्ड लोड हुए"
                }
            } catch (e: Exception) {
                statusMessage = "फ़ाइल पढ़ने में त्रुटि: ${e.localizedMessage}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "बल्क में नंबर इंपोर्ट करें (Excel / CSV)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "आप एक्सेल से कॉपी किया गया डेटा यहां पेस्ट कर सकते हैं या .csv फ़ाइल चुन सकते हैं।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons: Template & File Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onOpenTemplate,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("एक्सेल फॉर्मेट", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { filePicker.launch("text/*") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("फ़ाइल चुनें", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Text field to paste CSV rows
                OutlinedTextField(
                    value = csvText,
                    onValueChange = {
                        csvText = it
                        val parsed = CsvHelper.parseCsv(it)
                        previewCount = parsed.size
                        statusMessage = if (parsed.isNotEmpty()) "${parsed.size} कर्मचारी रिकॉर्ड पहचाने गए" else null
                    },
                    label = { Text("CSV डेटा या एक्सेल पंक्तियां पेस्ट करें") },
                    placeholder = { Text("नाम,पद,थाना,वर्ग,मोबाइल नंबर...\nराम,ऑपरेटर,कोतवाली,CCTV_OPERATOR,9838012345...") },
                    minLines = 5,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bulk_csv_input")
                )

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage ?: "",
                        color = if (previewCount > 0) ActiveGreen else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (csvText.isNotBlank()) {
                        onImport(csvText) { count ->
                            // Handled by ViewModel
                        }
                    } else {
                        statusMessage = "कृपया पहले डेटा पेस्ट करें या फ़ाइल चुनें"
                    }
                },
                enabled = csvText.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("bulk_import_confirm_button")
            ) {
                Text("इंपोर्ट करें ($previewCount)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
