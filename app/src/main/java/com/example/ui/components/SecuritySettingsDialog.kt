package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.PoliceGold

@Composable
fun SecuritySettingsDialog(
    isSecurityEnabled: Boolean,
    isAdminActive: Boolean,
    onDismiss: () -> Unit,
    onToggleSecurity: (Boolean) -> Unit,
    onChangePin: (String, String) -> Boolean,
    onChangeAdminPin: (String, String) -> Boolean
) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf<String?>(null) }

    var oldAdminPin by remember { mutableStateOf("") }
    var newAdminPin by remember { mutableStateOf("") }
    var adminPinMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "सुरक्षा एवं एक्सेस सेटिंग्स",
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
                // Toggle security
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("पिन सुरक्षा सक्रिय रखें", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "नंबर छिपाएं व अनधिकृत कॉलिंग रोकें",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isSecurityEnabled,
                        onCheckedChange = onToggleSecurity
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Change Departmental PIN
                Text("विभागीय पिन बदलें (Current Default: 1120)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = oldPin,
                    onValueChange = { oldPin = it },
                    label = { Text("पुराना पिन") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it },
                    label = { Text("नया पिन") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        if (oldPin.isNotBlank() && newPin.isNotBlank()) {
                            val ok = onChangePin(oldPin, newPin)
                            pinMessage = if (ok) "पिन सफलतापूर्वक बदल गया" else "पुराना पिन गलत है"
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("विभागीय पिन अपडेट करें")
                }

                if (pinMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pinMessage ?: "",
                        color = if (pinMessage?.contains("सफल") == true) ActiveGreen else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isAdminActive) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Change Admin Master PIN
                    Text("एडमिन मास्टर पिन बदलें (Current Default: 8899)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoliceGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = oldAdminPin,
                        onValueChange = { oldAdminPin = it },
                        label = { Text("वर्तमान एडमिन पिन") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newAdminPin,
                        onValueChange = { newAdminPin = it },
                        label = { Text("नया एडमिन पिन") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (oldAdminPin.isNotBlank() && newAdminPin.isNotBlank()) {
                                val ok = onChangeAdminPin(oldAdminPin, newAdminPin)
                                adminPinMessage = if (ok) "एडमिन पिन अपडेट हो गया" else "वर्तमान पिन अमान्य है"
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("एडमिन पिन अपडेट करें")
                    }

                    if (adminPinMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = adminPinMessage ?: "",
                            color = if (adminPinMessage?.contains("अपडेट") == true) ActiveGreen else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("पूर्ण")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
