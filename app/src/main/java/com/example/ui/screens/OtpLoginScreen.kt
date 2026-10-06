package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Personnel
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.PoliceBlue
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.PoliceRed

@Composable
fun OtpLoginScreen(
    mobileInput: String,
    onMobileChange: (String) -> Unit,
    isOtpSent: Boolean,
    generatedOtp: String,
    pendingPersonnel: Personnel?,
    loginError: String?,
    onSendOtp: () -> Unit,
    onVerifyOtp: (String) -> Boolean,
    onResendOtp: () -> Unit,
    onBackToMobile: () -> Unit,
    onQuickSelectMobile: (String) -> Unit
) {
    var otpInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Police Dual Red & Blue Emblem (लाल और नीला पुलिस प्रतीक)
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PoliceRed, PoliceNavy900)
                        )
                    )
                    .border(2.5.dp, PoliceGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = PoliceGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Police Red & Blue Ribbon Tag
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .height(5.dp)
                    .width(140.dp)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxSize().background(PoliceRed))
                Box(modifier = Modifier.weight(1f).fillMaxSize().background(PoliceBlue))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "जनपद अमेठी पुलिस",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = PoliceNavy900,
                textAlign = TextAlign.Center
            )

            Text(
                text = "CCTNS कर्मचारी एवं अधिकारी डायरेक्टरी",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PoliceRed
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PoliceRed.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isOtpSent) {
                        // PHASE 1: ENTER MOBILE NUMBER
                        Text(
                            text = "अधिकृत मोबाइल नंबर दर्ज करें",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PoliceNavy900
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "केवल जनपद अमेठी के थानों में नियुक्त पुलिस अधिकारी एवं CCTNS ऑपरेटर ही लॉगिन कर सकते हैं।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = mobileInput,
                            onValueChange = {
                                if (it.length <= 10) onMobileChange(it)
                            },
                            label = { Text("10-अंकीय मोबाइल नंबर") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PoliceRed)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onSendOtp() }
                            ),
                            singleLine = true,
                            isError = loginError != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_mobile_input")
                        )

                        if (loginError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = loginError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onSendOtp,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PoliceNavy900,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("send_otp_button")
                        ) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = PoliceGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ओटीपी प्राप्त करें (Send OTP)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Divider(color = PoliceRed.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick demo buttons for easy verification (Amethi police stations)
                        Text(
                            text = "जनपद अमेठी त्वरित परीक्षण नंबर:",
                            style = MaterialTheme.typography.labelSmall,
                            color = PoliceNavy800,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val sampleOfficers = listOf(
                            Pair("9807583096", "★ मुख्य व्यवस्थापक / सुपर एडमिन (अमेठी)"),
                            Pair("9454400248", "निरीक्षक विजय सिंह (नोडल CCTNS अमेठी)"),
                            Pair("9454403751", "निरीक्षक राहुल कुमार (SHO गौरीगंज)"),
                            Pair("9454403752", "निरीक्षक अरुण द्विवेदी (SHO अमेठी)"),
                            Pair("9415011223", "का. कुलदीप सिंह (CCTNS ऑपरेटर गौरीगंज)")
                        )

                        sampleOfficers.forEach { (phone, title) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, PoliceBlue.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onQuickSelectMobile(phone) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text(text = phone, fontSize = 11.sp, color = PoliceRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                    } else {
                        // PHASE 2: VERIFY OTP
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = onBackToMobile) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PoliceNavy900)
                            }
                            Text(
                                text = "ओटीपी कोड सत्यापन",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PoliceNavy900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Officer recognized details
                        if (pendingPersonnel != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE8EEF8)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PoliceBlue.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "पंजीकृत अधिकारी/कर्मचारी सत्यापित:", style = MaterialTheme.typography.labelSmall, color = PoliceNavy900, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = pendingPersonnel.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = "${pendingPersonnel.designation} (${pendingPersonnel.thanaName})", fontSize = 13.sp, color = PoliceRed, fontWeight = FontWeight.SemiBold)
                                    Text(text = "मो: ${pendingPersonnel.mobileNumber}", fontSize = 12.sp, color = PoliceBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulated SMS Banner with code for convenient verification
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PoliceGold.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PoliceGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = null, tint = PoliceGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "विभागीय संदेश (SMS OTP)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoliceNavy900)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "कोड: $generatedOtp",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = PoliceRed
                                )
                                TextButton(
                                    onClick = { otpInput = generatedOtp }
                                ) {
                                    Text("स्वतः भरें (Auto-Fill Code)", fontSize = 12.sp, color = PoliceNavy900, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = {
                                if (it.length <= 6) otpInput = it
                            },
                            label = { Text("6-अंकीय ओटीपी दर्ज करें") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (otpInput.isNotBlank()) onVerifyOtp(otpInput)
                                }
                            ),
                            singleLine = true,
                            isError = loginError != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_otp_input")
                        )

                        if (loginError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = loginError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (otpInput.isNotBlank()) onVerifyOtp(otpInput)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PoliceRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_otp_button")
                        ) {
                            Text("सत्यापित करें व लॉगिन करें", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onResendOtp) {
                                Text("पुनः ओटीपी भेजें", fontSize = 12.sp, color = PoliceNavy900)
                            }
                            TextButton(onClick = onBackToMobile) {
                                Text("नंबर बदलें", fontSize = 12.sp, color = PoliceRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
