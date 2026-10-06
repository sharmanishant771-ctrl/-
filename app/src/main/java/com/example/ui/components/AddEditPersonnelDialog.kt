package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Personnel
import com.example.data.model.PersonnelCategory

@Composable
fun AddEditPersonnelDialog(
    initialPersonnel: Personnel?,
    thanaSuggestions: List<String>,
    onDismiss: () -> Unit,
    onSave: (Personnel) -> Unit
) {
    val isEdit = initialPersonnel != null

    var name by remember { mutableStateOf(initialPersonnel?.name ?: "") }
    var designation by remember { mutableStateOf(initialPersonnel?.designation ?: "") }
    var selectedCategory by remember { mutableStateOf(initialPersonnel?.category ?: PersonnelCategory.CCTNS_OPERATOR) }
    var thanaName by remember { mutableStateOf(initialPersonnel?.thanaName ?: "थाना कोतवाली") }
    var mobileNumber by remember { mutableStateOf(initialPersonnel?.mobileNumber ?: "") }
    var cugNumber by remember { mutableStateOf(initialPersonnel?.cugNumber ?: "") }
    var dutyLocation by remember { mutableStateOf(initialPersonnel?.dutyLocation ?: "") }
    var shiftTiming by remember { mutableStateOf(initialPersonnel?.shiftTiming ?: "") }
    var notes by remember { mutableStateOf(initialPersonnel?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val quickDesignations = listOf(
        "कम्प्यूटर ऑपरेटर (CCTNS)",
        "सीसीटीएनएस ऑपरेटर",
        "सीसीटीएनएस तकनीकी सहायक",
        "थाना प्रभारी (SHO)",
        "थानाध्यक्ष (SO)",
        "वरिष्ठ उप-निरीक्षक (SSI)",
        "उप-निरीक्षक (SI)",
        "हेड मोहर्रिर",
        "कांस्टेबल / आरक्षी",
        "प्रभारी सीसीटीएनएस सेल"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "कर्मचारी विवरण संपादित करें" else "नया कर्मचारी / अधिकारी जोड़ें",
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
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("नाम * (उदा. का. कुलदीप सिंह)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Designation
                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it; errorMessage = null },
                    label = { Text("पद * (Designation)") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_designation")
                )

                // Quick Designation Chips
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickDesignations.take(6).forEach { d ->
                        SuggestionChip(
                            onClick = {
                                designation = d
                                if (d.contains("ऑपरेटर") || d.contains("कम्प्यूटर")) selectedCategory = PersonnelCategory.CCTNS_OPERATOR
                                else if (d.contains("तकनीकी")) selectedCategory = PersonnelCategory.CCTNS_STAFF
                                else if (d.contains("प्रभारी") || d.contains("SHO") || d.contains("SO")) selectedCategory = PersonnelCategory.SHO_INCHARGE
                            },
                            label = { Text(d, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category selector chips
                Text(text = "वर्ग (Category):", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        PersonnelCategory.CCTNS_OPERATOR,
                        PersonnelCategory.CCTNS_STAFF,
                        PersonnelCategory.SHO_INCHARGE,
                        PersonnelCategory.OFFICER,
                        PersonnelCategory.CONTROL_ROOM,
                        PersonnelCategory.STATION_STAFF
                    ).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.badgeLabel, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Thana Name
                OutlinedTextField(
                    value = thanaName,
                    onValueChange = { thanaName = it; errorMessage = null },
                    label = { Text("थाना / कार्यालय * (उदा. थाना कोतवाली)") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_thana")
                )

                // Thana Suggestions
                if (thanaSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        thanaSuggestions.take(5).forEach { th ->
                            SuggestionChip(
                                onClick = { thanaName = th },
                                label = { Text(th, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile Number (Mandatory)
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.length <= 15) {
                            mobileNumber = it
                            errorMessage = null
                        }
                    },
                    label = { Text("मोबाइल नंबर * (10 अंक)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_mobile")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CUG Number (Optional)
                OutlinedTextField(
                    value = cugNumber,
                    onValueChange = { if (it.length <= 15) cugNumber = it },
                    label = { Text("CUG / सरकारी नंबर (वैकल्पिक)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_cug")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Duty Location / Desk
                OutlinedTextField(
                    value = dutyLocation,
                    onValueChange = { dutyLocation = it },
                    label = { Text("ड्यूटी कक्ष / डेस्क (उदा. सीसीटीएनएस कक्ष 2)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Shift
                OutlinedTextField(
                    value = shiftTiming,
                    onValueChange = { shiftTiming = it },
                    label = { Text("शिफ्ट / समय (उदा. 8:00 AM - 8:00 PM)") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("गोपनीय टिप्पणी / नोट्स") },
                    leadingIcon = { Icon(Icons.Default.Note, contentDescription = null) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "कृपया कर्मचारी/अधिकारी का नाम दर्ज करें"
                        return@Button
                    }
                    if (designation.isBlank()) {
                        errorMessage = "कृपया पद दर्ज करें"
                        return@Button
                    }
                    if (thanaName.isBlank()) {
                        errorMessage = "कृपया थाना दर्ज करें"
                        return@Button
                    }
                    val cleanPhone = mobileNumber.filter { it.isDigit() }
                    if (cleanPhone.length < 10) {
                        errorMessage = "कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें"
                        return@Button
                    }

                    val updated = Personnel(
                        id = initialPersonnel?.id ?: 0L,
                        name = name.trim(),
                        designation = designation.trim(),
                        category = selectedCategory,
                        thanaName = thanaName.trim(),
                        mobileNumber = cleanPhone,
                        cugNumber = cugNumber.filter { it.isDigit() },
                        dutyLocation = dutyLocation.trim(),
                        shiftTiming = shiftTiming.trim(),
                        notes = notes.trim(),
                        isFavorite = initialPersonnel?.isFavorite ?: false,
                        createdAt = initialPersonnel?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(updated)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_personnel_button")
            ) {
                Text(if (isEdit) "अपडेट करें" else "सुरक्षित करें")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("रद्द करें")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
