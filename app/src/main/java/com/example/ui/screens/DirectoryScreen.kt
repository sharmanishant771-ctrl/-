package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PersonnelCategory
import com.example.ui.components.AdminAuthDialog
import com.example.ui.components.AddEditPersonnelDialog
import com.example.ui.components.BulkImportDialog
import com.example.ui.components.CsvTemplateDialog
import com.example.ui.components.PersonnelCard
import com.example.ui.components.PersonnelDetailSheet
import com.example.ui.components.SecurityLockDialog
import com.example.ui.components.SecuritySettingsDialog
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.SurveillanceCyan
import com.example.ui.viewmodel.DirectoryViewModel
import com.example.util.CallHelper
import com.example.util.CsvHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    viewModel: DirectoryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect UI state
    val filteredPersonnel by viewModel.filteredPersonnel.collectAsStateWithLifecycle()
    val allPersonnel by viewModel.allPersonnel.collectAsStateWithLifecycle()
    val thanaList by viewModel.thanaList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedThana by viewModel.selectedThana.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
    val isSecurityEnabled by viewModel.isSecurityEnabled.collectAsStateWithLifecycle()
    val isAdminActive by viewModel.isAdminActive.collectAsStateWithLifecycle()
    val loggedInPersonnel by viewModel.loggedInPersonnel.collectAsStateWithLifecycle()

    val selectedPersonnelForDetail by viewModel.selectedPersonnelForDetail.collectAsStateWithLifecycle()
    val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
    val editingPersonnel by viewModel.editingPersonnel.collectAsStateWithLifecycle()
    val showBulkImportDialog by viewModel.showBulkImportDialog.collectAsStateWithLifecycle()
    val showSecurityLockDialog by viewModel.showSecurityLockDialog.collectAsStateWithLifecycle()
    val showSecuritySettingsDialog by viewModel.showSecuritySettingsDialog.collectAsStateWithLifecycle()
    val showCsvTemplateDialog by viewModel.showCsvTemplateDialog.collectAsStateWithLifecycle()
    val showAdminAuthDialog by viewModel.showAdminAuthDialog.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = PoliceGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "जनपद अमेठी - CCTNS डायरेक्टरी",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        // Logged-in officer label
                        if (loggedInPersonnel != null) {
                            Text(
                                text = "${loggedInPersonnel?.name} • ${loggedInPersonnel?.thanaName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Admin status indicator / switch
                    if (isAdminActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = com.example.ui.theme.PoliceRed.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PoliceGold),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = if (loggedInPersonnel?.mobileNumber == "9807583096") "★ मुख्य एडमिन" else "एडमिन",
                                color = PoliceGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Admin auth icon
                    IconButton(
                        onClick = {
                            if (isAdminActive) {
                                viewModel.lockAdminMode()
                            } else {
                                viewModel.requestAdminAction { }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Mode",
                            tint = if (isAdminActive) PoliceGold else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }

                    // Quick Lock / Unlock Icon
                    IconButton(
                        onClick = {
                            if (isUnlocked) viewModel.lockApp() else viewModel.promptUnlock()
                        }
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = if (isUnlocked) "Lock App" else "Unlock App",
                            tint = if (isUnlocked) ActiveGreen else MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Overflow Menu
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("एक्सेल फॉर्मेट टेम्पलेट") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openCsvTemplateDialog()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("बल्क में नंबर जोड़ें (Excel)") },
                            leadingIcon = { Icon(Icons.Default.Upload, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openBulkImportDialog()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("डायरेक्टरी CSV शेयर करें") },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                val csv = CsvHelper.exportPersonnelToCsv(allPersonnel)
                                CsvHelper.shareCsvContent(
                                    context = context,
                                    csvText = csv,
                                    filename = "District_Police_CCTV_Directory.csv",
                                    chooserTitle = "पुलिस डायरेक्टरी निर्यात करें"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("सुरक्षा सेटिंग्स") },
                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.openSecuritySettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("डिफ़ॉल्ट डेटा रीसेट") },
                            leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.resetToDefaultData()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("लॉगआउट") },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.logout()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddPersonnelDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("कर्मचारी जोड़ें", fontWeight = FontWeight.Bold) },
                containerColor = com.example.ui.theme.PoliceRed,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_personnel_fab")
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Police Dual Red & Blue Ribbon (लाल - नीला पुलिस रिबन)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(com.example.ui.theme.PoliceRed)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(com.example.ui.theme.PoliceBlue)
                )
            }

            // Emergency Speed-Dial Bar
            EmergencyContactsBar()

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("नाम, पद, थाना या मोबाइल नंबर से खोजें...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("directory_search_input")
            )

            // Category Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Category
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.ALL && !favoritesOnly,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.ALL)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("सभी (${allPersonnel.size})", fontSize = 12.sp) }
                )

                // CCTNS Operators Chip
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.CCTNS_OPERATOR,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.CCTNS_OPERATOR)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("💻 CCTNS ऑपरेटर", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SurveillanceCyan.copy(alpha = 0.2f),
                        selectedLabelColor = SurveillanceCyan
                    )
                )

                // CCTNS Tech Staff
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.CCTNS_STAFF,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.CCTNS_STAFF)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("🛠️ तकनीकी स्टाफ", fontSize = 12.sp) }
                )

                // SHO Incharge
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.SHO_INCHARGE,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.SHO_INCHARGE)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("👮 थाना प्रभारी", fontSize = 12.sp) }
                )

                // Officers
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.OFFICER,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.OFFICER)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("⭐ अधिकारी गण", fontSize = 12.sp) }
                )

                // Control Room
                FilterChip(
                    selected = selectedCategory == PersonnelCategory.CONTROL_ROOM,
                    onClick = {
                        viewModel.onCategorySelect(PersonnelCategory.CONTROL_ROOM)
                        if (favoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    label = { Text("🖥️ कंट्रोल रूम", fontSize = 12.sp) }
                )

                // Favorites Only
                FilterChip(
                    selected = favoritesOnly,
                    onClick = { viewModel.toggleFavoritesOnly() },
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = PoliceGold) },
                    label = { Text("पसंदीदा", fontSize = 12.sp) }
                )
            }

            // Thana Selector Chips Row
            if (thanaList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedThana == null,
                        onClick = { viewModel.onThanaSelect(null) },
                        label = { Text("सभी थाने", fontSize = 11.sp) }
                    )

                    thanaList.forEach { thana ->
                        FilterChip(
                            selected = selectedThana == thana,
                            onClick = {
                                viewModel.onThanaSelect(if (selectedThana == thana) null else thana)
                            },
                            label = { Text(thana, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Result summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "कुल परिणाम: ${filteredPersonnel.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (searchQuery.isNotBlank() || selectedThana != null || selectedCategory != PersonnelCategory.ALL || favoritesOnly) {
                    Text(
                        text = "फ़िल्टर हटाएं",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // Personnel List
            if (filteredPersonnel.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "कोई कर्मचारी/अधिकारी नहीं मिला",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "कृपया खोज शब्द या थाना फ़िल्टर बदलकर देखें",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = filteredPersonnel,
                        key = { it.id }
                    ) { personnel ->
                        PersonnelCard(
                            personnel = personnel,
                            isUnlocked = isUnlocked,
                            onClick = { viewModel.openPersonnelDetail(personnel) },
                            onCallClick = {
                                if (isUnlocked) {
                                    CallHelper.initiateCall(context, personnel.mobileNumber)
                                } else {
                                    viewModel.promptUnlock()
                                }
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(personnel) }
                        )
                    }

                    // Space for FAB
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Detail Profile Sheet
    if (selectedPersonnelForDetail != null) {
        PersonnelDetailSheet(
            personnel = selectedPersonnelForDetail!!,
            isUnlocked = isUnlocked,
            isAdminActive = isAdminActive,
            onDismiss = { viewModel.dismissPersonnelDetail() },
            onCall = {
                CallHelper.initiateCall(context, selectedPersonnelForDetail!!.mobileNumber)
            },
            onFavoriteToggle = {
                viewModel.toggleFavorite(selectedPersonnelForDetail!!)
            },
            onEdit = {
                val current = selectedPersonnelForDetail!!
                viewModel.dismissPersonnelDetail()
                viewModel.openEditPersonnelDialog(current)
            },
            onDelete = {
                val current = selectedPersonnelForDetail!!
                viewModel.deletePersonnel(current)
            },
            onUnlockPrompt = { viewModel.promptUnlock() }
        )
    }

    // Add / Edit Personnel Dialog
    if (showAddEditDialog) {
        AddEditPersonnelDialog(
            initialPersonnel = editingPersonnel,
            thanaSuggestions = thanaList,
            onDismiss = { viewModel.dismissAddEditDialog() },
            onSave = { updated -> viewModel.savePersonnel(updated) }
        )
    }

    // Bulk Import Dialog
    if (showBulkImportDialog) {
        BulkImportDialog(
            onDismiss = { viewModel.dismissBulkImportDialog() },
            onOpenTemplate = { viewModel.openCsvTemplateDialog() },
            onImport = { csv, callback -> viewModel.importPersonnelBulk(csv, callback) }
        )
    }

    // Excel CSV Format Template Dialog
    if (showCsvTemplateDialog) {
        CsvTemplateDialog(
            onDismiss = { viewModel.dismissCsvTemplateDialog() }
        )
    }

    // Security Lock / PIN Dialog
    if (showSecurityLockDialog) {
        SecurityLockDialog(
            onDismiss = { viewModel.dismissUnlockDialog() },
            onVerify = { pin -> viewModel.verifyAndUnlock(pin) }
        )
    }

    // Security Settings Dialog
    if (showSecuritySettingsDialog) {
        SecuritySettingsDialog(
            isSecurityEnabled = isSecurityEnabled,
            isAdminActive = isAdminActive,
            onDismiss = { viewModel.dismissSecuritySettings() },
            onToggleSecurity = { viewModel.setSecurityEnabled(it) },
            onChangePin = { old, new -> viewModel.changePin(old, new) },
            onChangeAdminPin = { old, new -> viewModel.changeAdminPin(old, new) }
        )
    }

    // Admin Auth Dialog
    if (showAdminAuthDialog) {
        AdminAuthDialog(
            onDismiss = { viewModel.dismissAdminAuthDialog() },
            onVerify = { pin -> viewModel.verifyAdminPin(pin) }
        )
    }
}
