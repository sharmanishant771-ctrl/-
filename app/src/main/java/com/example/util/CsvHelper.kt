package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.Personnel
import com.example.data.model.PersonnelCategory
import java.io.File
import java.io.FileOutputStream

object CsvHelper {

    // Standard UTF-8 BOM so Microsoft Excel and WPS Office display Hindi characters properly
    private const val UTF8_BOM = "\uFEFF"

    val SAMPLE_CSV_TEMPLATE = buildString {
        append(UTF8_BOM)
        appendLine("नाम,पद,थाना,वर्ग,मोबाइल नंबर,CUG नंबर,ड्यूटी कक्ष,शिफ्ट,गोपनीय टिप्पणी")
        appendLine("सुरेश कुमार,कम्प्यूटर ऑपरेटर (CCTNS),थाना कोतवाली,CCTNS_OPERATOR,9838011223,9454401200,सीसीटीएनएस कक्ष 2,प्रातः 8 से सायं 8,एफआईआर व ऑनलाइन जीडी प्रविष्टि")
        appendLine("महेश यादव,थाना प्रभारी (SHO),थाना सदर,SHO_INCHARGE,9454403401,9454403400,थाना कार्यालय,24x7,थाना प्रशासनिक व सुरक्षा")
        appendLine("आलोक सक्सेना,सीसीटीएनएस तकनीकी सहायक,जिला सीसीटीएनएस सेल,CCTNS_STAFF,9795123456,,सर्वर रूम,ऑन-कॉल,वीपीएन व हार्डवेयर मेंटेनेंस")
        appendLine("संजय सिंह,उप-निरीक्षक (SI),थाना सिविल लाइंस,OFFICER,9454402302,,फील्ड ड्यूटी,सामान्य,विवेचना एवं गश्त")
    }

    /**
     * Parses CSV text rows into a list of Personnel objects
     */
    fun parseCsv(csvText: String): List<Personnel> {
        val result = mutableListOf<Personnel>()
        if (csvText.isBlank()) return result

        val lines = csvText.lines()
            .map { it.trim().removePrefix("\uFEFF") }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) return result

        // Detect if first line is a header
        val firstLine = lines.first()
        val hasHeader = firstLine.contains("नाम", ignoreCase = true) ||
                firstLine.contains("name", ignoreCase = true) ||
                firstLine.contains("मोबाइल", ignoreCase = true) ||
                firstLine.contains("mobile", ignoreCase = true)

        val dataLines = if (hasHeader) lines.drop(1) else lines

        for (line in dataLines) {
            val tokens = parseCsvLine(line)
            if (tokens.isEmpty()) continue

            val name = tokens.getOrNull(0)?.trim() ?: ""
            val designation = tokens.getOrNull(1)?.trim() ?: "कर्मचारी"
            val thana = tokens.getOrNull(2)?.trim() ?: "थाना कोतवाली"
            val categoryStr = tokens.getOrNull(3)?.trim() ?: ""
            val rawMobile = tokens.getOrNull(4)?.trim() ?: ""
            val cug = tokens.getOrNull(5)?.trim() ?: ""
            val duty = tokens.getOrNull(6)?.trim() ?: ""
            val shift = tokens.getOrNull(7)?.trim() ?: ""
            val notes = tokens.getOrNull(8)?.trim() ?: ""

            val cleanMobile = CallHelper.cleanPhoneNumber(rawMobile)

            // Must have a name and mobile to be valid
            if (name.isNotBlank() && cleanMobile.isNotBlank()) {
                val category = deduceCategory(categoryStr, designation)
                result.add(
                    Personnel(
                        name = name,
                        designation = designation,
                        category = category,
                        thanaName = thana,
                        mobileNumber = cleanMobile,
                        cugNumber = CallHelper.cleanPhoneNumber(cug),
                        dutyLocation = duty,
                        shiftTiming = shift,
                        notes = notes,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
        return result
    }

    private fun deduceCategory(categoryStr: String, designation: String): PersonnelCategory {
        if (categoryStr.isNotBlank()) {
            return PersonnelCategory.fromString(categoryStr)
        }
        val lowerDesig = designation.lowercase()
        return when {
            lowerDesig.contains("cctns") || lowerDesig.contains("ऑपरेटर") || lowerDesig.contains("कम्प्यूटर") -> PersonnelCategory.CCTNS_OPERATOR
            lowerDesig.contains("तकनीकी") || lowerDesig.contains("टेक्नीशियन") || lowerDesig.contains("इंजीनियर") || lowerDesig.contains("हार्डवेयर") -> PersonnelCategory.CCTNS_STAFF
            lowerDesig.contains("sho") || lowerDesig.contains("प्रभारी निरीक्षक") || lowerDesig.contains("थानाध्यक्ष") || lowerDesig.contains("थाना प्रभारी") -> PersonnelCategory.SHO_INCHARGE
            lowerDesig.contains("कंट्रोल") || lowerDesig.contains("control") || lowerDesig.contains("सेल") -> PersonnelCategory.CONTROL_ROOM
            lowerDesig.contains("अधिकारी") || lowerDesig.contains("उप-निरीक्षक") || lowerDesig.contains("si") || lowerDesig.contains("co") || lowerDesig.contains("dsp") -> PersonnelCategory.OFFICER
            else -> PersonnelCategory.STATION_STAFF
        }
    }

    /**
     * Splits a CSV line respecting quoted columns
     */
    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        val currentToken = StringBuilder()

        for (ch in line) {
            when {
                ch == '\"' -> {
                    inQuotes = !inQuotes
                }
                ch == ',' && !inQuotes -> {
                    tokens.add(currentToken.toString().trim())
                    currentToken.clear()
                }
                else -> {
                    currentToken.append(ch)
                }
            }
        }
        tokens.add(currentToken.toString().trim())
        return tokens
    }

    /**
     * Exports entire list of personnel to CSV format
     */
    fun exportPersonnelToCsv(personnelList: List<Personnel>): String {
        return buildString {
            append(UTF8_BOM)
            appendLine("नाम,पद,थाना,वर्ग,मोबाइल नंबर,CUG नंबर,ड्यूटी कक्ष,शिफ्ट,गोपनीय टिप्पणी")
            for (p in personnelList) {
                appendLine("\"${p.name}\",\"${p.designation}\",\"${p.thanaName}\",\"${p.category.name}\",\"${p.mobileNumber}\",\"${p.cugNumber}\",\"${p.dutyLocation}\",\"${p.shiftTiming}\",\"${p.notes}\"")
            }
        }
    }

    /**
     * Shares CSV Template or Exported CSV via Android Share Sheet
     */
    fun shareCsvContent(context: Context, csvText: String, filename: String, chooserTitle: String) {
        try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                out.write(csvText.toByteArray(Charsets.UTF_8))
            }
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, filename)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            // Text share fallback
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, csvText)
                putExtra(Intent.EXTRA_SUBJECT, filename)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }
}
