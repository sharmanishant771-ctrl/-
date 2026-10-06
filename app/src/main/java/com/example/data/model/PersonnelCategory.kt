package com.example.data.model

enum class PersonnelCategory(
    val hindiTitle: String,
    val englishTitle: String,
    val badgeLabel: String
) {
    ALL(
        hindiTitle = "सभी",
        englishTitle = "All",
        badgeLabel = "सभी"
    ),
    CCTNS_OPERATOR(
        hindiTitle = "सीसीटीएनएस ऑपरेटर",
        englishTitle = "CCTNS Operator",
        badgeLabel = "💻 CCTNS ऑपरेटर"
    ),
    CCTNS_STAFF(
        hindiTitle = "सीसीटीएनएस तकनीकी/डेटा कर्मचारी",
        englishTitle = "CCTNS Tech Staff",
        badgeLabel = "🛠️ तकनीकी स्टाफ"
    ),
    SHO_INCHARGE(
        hindiTitle = "थाना प्रभारी (SHO/SO)",
        englishTitle = "Station In-charge",
        badgeLabel = "👮 थाना प्रभारी"
    ),
    OFFICER(
        hindiTitle = "उच्चाधिकारी (नोडल/CO/ASP/SP)",
        englishTitle = "Officer",
        badgeLabel = "⭐ अधिकारी गण"
    ),
    CONTROL_ROOM(
        hindiTitle = "जिला सीसीटीएनएस/कंट्रोल रूम",
        englishTitle = "CCTNS Control Cell",
        badgeLabel = "🖥️ सीसीटीएनएस सेल"
    ),
    STATION_STAFF(
        hindiTitle = "थाने के अन्य कर्मचारी (मुंशी/कांस्टेबल)",
        englishTitle = "Station Staff",
        badgeLabel = "📋 थाना कर्मचारी"
    );

    companion object {
        fun fromString(value: String?): PersonnelCategory {
            if (value.isNullOrBlank()) return STATION_STAFF
            val normalized = value.trim()
            return entries.firstOrNull {
                it.name.equals(normalized, ignoreCase = true) ||
                it.hindiTitle.contains(normalized, ignoreCase = true) ||
                it.englishTitle.contains(normalized, ignoreCase = true)
            } ?: when {
                normalized.contains("CCTV", ignoreCase = true) -> CCTNS_OPERATOR
                normalized.contains("ऑपरेटर", ignoreCase = true) -> CCTNS_OPERATOR
                normalized.contains("तकनीक", ignoreCase = true) -> CCTNS_STAFF
                normalized.contains("SHO", ignoreCase = true) || normalized.contains("प्रभारी", ignoreCase = true) -> SHO_INCHARGE
                normalized.contains("अधिकारी", ignoreCase = true) -> OFFICER
                normalized.contains("कंट्रोल", ignoreCase = true) -> CONTROL_ROOM
                else -> STATION_STAFF
            }
        }
    }
}
