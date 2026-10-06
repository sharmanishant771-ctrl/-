package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Personnel
import com.example.data.model.PersonnelCategory
import com.example.data.repository.PersonnelRepository
import com.example.security.SecurityPreferences
import com.example.util.CallHelper
import com.example.util.CsvHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DirectoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PersonnelRepository
    private val securityPrefs = SecurityPreferences(application)

    // OTP Login Authentication state
    private val _isLoggedIn = MutableStateFlow(securityPrefs.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loggedInPersonnel = MutableStateFlow<Personnel?>(null)
    val loggedInPersonnel: StateFlow<Personnel?> = _loggedInPersonnel.asStateFlow()

    private val _otpMobileInput = MutableStateFlow("")
    val otpMobileInput: StateFlow<String> = _otpMobileInput.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    private val _pendingPersonnel = MutableStateFlow<Personnel?>(null)
    val pendingPersonnel: StateFlow<Personnel?> = _pendingPersonnel.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Admin authorization state (केवल एडमिन को ही जोड़ने / बदलने की अनुमति)
    private val _isAdminActive = MutableStateFlow(securityPrefs.isAdminActive())
    val isAdminActive: StateFlow<Boolean> = _isAdminActive.asStateFlow()

    private val _showAdminAuthDialog = MutableStateFlow(false)
    val showAdminAuthDialog: StateFlow<Boolean> = _showAdminAuthDialog.asStateFlow()

    private var pendingAdminAction: (() -> Unit)? = null

    init {
        val database = AppDatabase.getDatabase(application)
        val syncService = com.example.data.firestore.FirestoreSyncService(application, database.personnelDao())
        repository = PersonnelRepository(database.personnelDao(), syncService)
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            // Restore logged in user info if already logged in
            if (securityPrefs.isLoggedIn()) {
                val saved = securityPrefs.getLoggedUser()
                val mobile = saved["mobile"] ?: ""
                if (mobile.isNotBlank()) {
                    val p = repository.findPersonnelByMobile(mobile)
                    _loggedInPersonnel.value = p
                    if (securityPrefs.isMasterAdmin(mobile)) {
                        _isAdminActive.value = true
                        securityPrefs.setAdminActive(true)
                    }
                }
            }
        }
    }

    // State flows for search and filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedThana = MutableStateFlow<String?>(null)
    val selectedThana: StateFlow<String?> = _selectedThana.asStateFlow()

    private val _selectedCategory = MutableStateFlow(PersonnelCategory.ALL)
    val selectedCategory: StateFlow<PersonnelCategory> = _selectedCategory.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    // Access control & Security state
    private val _isUnlocked = MutableStateFlow(!securityPrefs.isSecurityEnabled() || securityPrefs.isLoggedIn())
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _isSecurityEnabled = MutableStateFlow(securityPrefs.isSecurityEnabled())
    val isSecurityEnabled: StateFlow<Boolean> = _isSecurityEnabled.asStateFlow()

    // Dialog & UI view states
    private val _selectedPersonnelForDetail = MutableStateFlow<Personnel?>(null)
    val selectedPersonnelForDetail: StateFlow<Personnel?> = _selectedPersonnelForDetail.asStateFlow()

    private val _showAddEditDialog = MutableStateFlow(false)
    val showAddEditDialog: StateFlow<Boolean> = _showAddEditDialog.asStateFlow()

    private val _editingPersonnel = MutableStateFlow<Personnel?>(null)
    val editingPersonnel: StateFlow<Personnel?> = _editingPersonnel.asStateFlow()

    private val _showBulkImportDialog = MutableStateFlow(false)
    val showBulkImportDialog: StateFlow<Boolean> = _showBulkImportDialog.asStateFlow()

    private val _showSecurityLockDialog = MutableStateFlow(false)
    val showSecurityLockDialog: StateFlow<Boolean> = _showSecurityLockDialog.asStateFlow()

    private val _showSecuritySettingsDialog = MutableStateFlow(false)
    val showSecuritySettingsDialog: StateFlow<Boolean> = _showSecuritySettingsDialog.asStateFlow()

    private val _showCsvTemplateDialog = MutableStateFlow(false)
    val showCsvTemplateDialog: StateFlow<Boolean> = _showCsvTemplateDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Distinct list of Thana names from database
    val thanaList: StateFlow<List<String>> = repository.allThanaNames.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered personnel list combining search query, thana, category, favorites
    val filteredPersonnel: StateFlow<List<Personnel>> = combine(
        repository.allPersonnel,
        _searchQuery,
        _selectedThana,
        _selectedCategory,
        _favoritesOnly
    ) { list, query, thana, category, favOnly ->
        list.filter { p ->
            val matchesQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.designation.contains(query, ignoreCase = true) ||
                    p.thanaName.contains(query, ignoreCase = true) ||
                    p.mobileNumber.contains(query, ignoreCase = true) ||
                    p.dutyLocation.contains(query, ignoreCase = true)

            val matchesThana = thana == null || p.thanaName.equals(thana, ignoreCase = true)

            val matchesCategory = category == PersonnelCategory.ALL || p.category == category

            val matchesFav = !favOnly || p.isFavorite

            matchesQuery && matchesThana && matchesCategory && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All personnel list for export / stats
    val allPersonnel: StateFlow<List<Personnel>> = repository.allPersonnel.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search & Filter actions
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onThanaSelect(thana: String?) {
        _selectedThana.value = thana
    }

    fun onCategorySelect(category: PersonnelCategory) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedThana.value = null
        _selectedCategory.value = PersonnelCategory.ALL
        _favoritesOnly.value = false
    }

    // OTP Authentication Workflow
    fun onOtpMobileChange(number: String) {
        _otpMobileInput.value = number
        _loginError.value = null
    }

    fun sendOtp(onSuccess: (() -> Unit)? = null) {
        val clean = CallHelper.cleanPhoneNumber(_otpMobileInput.value)
        if (clean.length < 10) {
            _loginError.value = "कृपया 10 अंकों का वैध मोबाइल नंबर दर्ज करें"
            return
        }

        viewModelScope.launch {
            val personnel = repository.findPersonnelByMobile(clean)
            if (personnel == null) {
                _loginError.value = "यह मोबाइल नंबर जनपद अमेठी पुलिस/सीसीटीएनएस डायरेक्टरी में पंजीकृत नहीं है। केवल अधिकृत कर्मचारी ही लॉगिन कर सकते हैं।"
            } else {
                _loginError.value = null
                _pendingPersonnel.value = personnel
                // Generate a realistic 6-digit OTP
                val code = ((100000..999999).random()).toString()
                _generatedOtp.value = code
                _isOtpSent.value = true
                _snackbarMessage.value = "ओटीपी कोड ${personnel.name} के पंजीकृत नंबर पर भेजा गया: $code"
                onSuccess?.invoke()
            }
        }
    }

    fun verifyOtp(enteredOtp: String): Boolean {
        if (enteredOtp.trim() == _generatedOtp.value) {
            val personnel = _pendingPersonnel.value
            _isLoggedIn.value = true
            _loggedInPersonnel.value = personnel
            _isUnlocked.value = true
            _isOtpSent.value = false
            _loginError.value = null

            val isMaster = securityPrefs.isMasterAdmin(personnel?.mobileNumber)
            if (isMaster) {
                _isAdminActive.value = true
                securityPrefs.setAdminActive(true)
            }

            securityPrefs.setLoggedIn(
                loggedIn = true,
                mobile = personnel?.mobileNumber,
                name = personnel?.name,
                designation = personnel?.designation,
                thana = personnel?.thanaName
            )
            _snackbarMessage.value = if (isMaster)
                "मुख्य व्यवस्थापक अधिकृत! (9807583096) - समस्त डेटा प्रबंधन का पूर्ण नियंत्रण प्राप्त"
            else
                "सत्यापन सफल! स्वागत है: ${personnel?.name ?: ""}"
            return true
        } else {
            _loginError.value = "गलत ओटीपी कोड। कृपया सही 6-अंकीय कोड दर्ज करें。"
            return false
        }
    }

    fun resendOtp() {
        val code = ((100000..999999).random()).toString()
        _generatedOtp.value = code
        _snackbarMessage.value = "नया ओटीपी कोड भेजा गया: $code"
    }

    fun logout() {
        securityPrefs.setLoggedIn(false)
        _isLoggedIn.value = false
        _loggedInPersonnel.value = null
        _isOtpSent.value = false
        _generatedOtp.value = ""
        _otpMobileInput.value = ""
        _pendingPersonnel.value = null
        _isAdminActive.value = false
        securityPrefs.setAdminActive(false)
        _snackbarMessage.value = "लॉगआउट हो गए हैं"
    }

    fun quickSelectLoginUser(personnel: Personnel) {
        _otpMobileInput.value = personnel.mobileNumber
        _loginError.value = null
    }

    // Admin Authorization Workflow (केवल एडमिन को संपादन / जोड़ने की अनुमति)
    fun requestAdminAction(action: () -> Unit) {
        val isMaster = securityPrefs.isMasterAdmin(_loggedInPersonnel.value?.mobileNumber)
        if (_isAdminActive.value || isMaster) {
            _isAdminActive.value = true
            action()
        } else {
            pendingAdminAction = action
            _showAdminAuthDialog.value = true
        }
    }

    fun dismissAdminAuthDialog() {
        _showAdminAuthDialog.value = false
        pendingAdminAction = null
    }

    fun verifyAdminPin(pin: String): Boolean {
        if (securityPrefs.verifyAdminPin(pin)) {
            _isAdminActive.value = true
            securityPrefs.setAdminActive(true)
            _showAdminAuthDialog.value = false
            _snackbarMessage.value = "एडमिन अधिकृत! संपादन एवं प्रबंधन की अनुमति सक्रिय"
            pendingAdminAction?.invoke()
            pendingAdminAction = null
            return true
        } else {
            _snackbarMessage.value = "अमान्य एडमिन पिन/पासवर्ड"
            return false
        }
    }

    fun lockAdminMode() {
        _isAdminActive.value = false
        securityPrefs.setAdminActive(false)
        _snackbarMessage.value = "एडमिन मोड बंद किया गया"
    }

    fun changeAdminPin(oldPin: String, newPin: String): Boolean {
        if (securityPrefs.verifyAdminPin(oldPin)) {
            securityPrefs.setAdminPin(newPin)
            _snackbarMessage.value = "एडमिन पासवर्ड सफलतापूर्वक अपडेट हो गया"
            return true
        }
        return false
    }

    // Security Actions
    fun promptUnlock() {
        _showSecurityLockDialog.value = true
    }

    fun dismissUnlockDialog() {
        _showSecurityLockDialog.value = false
    }

    fun verifyAndUnlock(pin: String): Boolean {
        val isValid = securityPrefs.verifyPin(pin)
        if (isValid) {
            _isUnlocked.value = true
            _showSecurityLockDialog.value = false
            _snackbarMessage.value = "सुरक्षा अधिकृत: नंबर अनलॉक हो गए हैं"
        }
        return isValid
    }

    fun lockApp() {
        if (_isSecurityEnabled.value) {
            _isUnlocked.value = false
            _snackbarMessage.value = "ऐप लॉक कर दिया गया है"
        }
    }

    fun openSecuritySettings() {
        _showSecuritySettingsDialog.value = true
    }

    fun dismissSecuritySettings() {
        _showSecuritySettingsDialog.value = false
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (securityPrefs.verifyPin(oldPin)) {
            securityPrefs.setPin(newPin)
            _snackbarMessage.value = "सुरक्षा पिन सफलतापूर्वक बदल दिया गया"
            return true
        }
        return false
    }

    fun setSecurityEnabled(enabled: Boolean) {
        securityPrefs.setSecurityEnabled(enabled)
        _isSecurityEnabled.value = enabled
        if (!enabled) {
            _isUnlocked.value = true
        } else {
            _isUnlocked.value = false
        }
        _snackbarMessage.value = if (enabled) "पिन सुरक्षा सक्रिय कर दी गई" else "पिन सुरक्षा निष्क्रिय कर दी गई"
    }

    // Detail Sheet Actions
    fun openPersonnelDetail(personnel: Personnel) {
        _selectedPersonnelForDetail.value = personnel
    }

    fun dismissPersonnelDetail() {
        _selectedPersonnelForDetail.value = null
    }

    // Add / Edit Personnel Actions (Admin Protected)
    fun openAddPersonnelDialog() {
        requestAdminAction {
            _editingPersonnel.value = null
            _showAddEditDialog.value = true
        }
    }

    fun openEditPersonnelDialog(personnel: Personnel) {
        requestAdminAction {
            _editingPersonnel.value = personnel
            _showAddEditDialog.value = true
        }
    }

    fun dismissAddEditDialog() {
        _showAddEditDialog.value = false
        _editingPersonnel.value = null
    }

    fun savePersonnel(personnel: Personnel) {
        if (!_isAdminActive.value) {
            requestAdminAction { savePersonnel(personnel) }
            return
        }
        viewModelScope.launch {
            if (personnel.id == 0L) {
                repository.insert(personnel)
                _snackbarMessage.value = "${personnel.name} को सफलतापूर्वक जोड़ दिया गया"
            } else {
                repository.update(personnel)
                _snackbarMessage.value = "${personnel.name} का विवरण अपडेट हो गया"
                // Update active detail view if open
                if (_selectedPersonnelForDetail.value?.id == personnel.id) {
                    _selectedPersonnelForDetail.value = personnel
                }
            }
            _showAddEditDialog.value = false
            _editingPersonnel.value = null
        }
    }

    fun deletePersonnel(personnel: Personnel) {
        if (!_isAdminActive.value) {
            requestAdminAction { deletePersonnel(personnel) }
            return
        }
        viewModelScope.launch {
            repository.deleteById(personnel.id)
            if (_selectedPersonnelForDetail.value?.id == personnel.id) {
                _selectedPersonnelForDetail.value = null
            }
            _snackbarMessage.value = "${personnel.name} को हटा दिया गया"
        }
    }

    fun toggleFavorite(personnel: Personnel) {
        viewModelScope.launch {
            val newFav = !personnel.isFavorite
            repository.setFavorite(personnel.id, newFav)
            val updated = personnel.copy(isFavorite = newFav)
            if (_selectedPersonnelForDetail.value?.id == personnel.id) {
                _selectedPersonnelForDetail.value = updated
            }
        }
    }

    // Bulk Import Actions (Admin Protected)
    fun openBulkImportDialog() {
        requestAdminAction {
            _showBulkImportDialog.value = true
        }
    }

    fun dismissBulkImportDialog() {
        _showBulkImportDialog.value = false
    }

    fun importPersonnelBulk(csvText: String, onComplete: (Int) -> Unit) {
        if (!_isAdminActive.value) {
            requestAdminAction { importPersonnelBulk(csvText, onComplete) }
            return
        }
        viewModelScope.launch {
            val parsedList = CsvHelper.parseCsv(csvText)
            if (parsedList.isNotEmpty()) {
                repository.insertBulk(parsedList)
                _snackbarMessage.value = "${parsedList.size} कर्मचारियों के नंबर सफलतापूर्वक जोड़े गए!"
                _showBulkImportDialog.value = false
                onComplete(parsedList.size)
            } else {
                _snackbarMessage.value = "कोई मान्य रिकॉर्ड नहीं मिला। कृपया फॉर्मेट जांचें।"
                onComplete(0)
            }
        }
    }

    // Template Dialog Actions
    fun openCsvTemplateDialog() {
        _showCsvTemplateDialog.value = true
    }

    fun dismissCsvTemplateDialog() {
        _showCsvTemplateDialog.value = false
    }

    fun resetToDefaultData() {
        requestAdminAction {
            viewModelScope.launch {
                repository.resetToSampleData()
                _snackbarMessage.value = "डिफ़ॉल्ट जनपद अमेठी पुलिस CCTNS डायरेक्टरी रीसेट हो गई"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
