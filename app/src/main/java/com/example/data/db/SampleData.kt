package com.example.data.db

import com.example.data.entity.PolicePersonnelEntity
import com.example.security.CryptoManager

object SampleData {
    fun getMasterAdminEntity(): PolicePersonnelEntity {
        return PolicePersonnelEntity(
            name = "मुख्य व्यवस्थापक (एडमिन)",
            designation = "सुपर एडमिन (जिला CCTNS प्रभारी, अमेठी)",
            category = "CONTROL_ROOM",
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            encryptedMobileNumber = CryptoManager.encrypt("9807583096"),
            encryptedCugNumber = CryptoManager.encrypt("9454400247"),
            dutyLocation = "कंट्रोल रूम व एडमिन डेस्क, गौरीगंज",
            shiftTiming = "24x7 मुख्य व्यवस्थापक",
            email = "admin.cctns@amethi.police.gov.in",
            isFavorite = true,
            encryptedNotes = CryptoManager.encrypt("स्थायी मुख्य व्यवस्थापक (Master Admin 9807583096) - समस्त डेटा प्रबंधन व नियंत्रण का पूर्ण अधिकार")
        )
    }

    fun getMasterAdminPersonnel(): com.example.data.model.Personnel {
        return com.example.data.model.Personnel(
            id = 1L,
            name = "मुख्य व्यवस्थापक (एडमिन)",
            designation = "सुपर एडमिन (जिला CCTNS प्रभारी, अमेठी)",
            category = com.example.data.model.PersonnelCategory.CONTROL_ROOM,
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            mobileNumber = "9807583096",
            cugNumber = "9454400247",
            dutyLocation = "कंट्रोल रूम व एडमिन डेस्क, गौरीगंज",
            shiftTiming = "24x7 मुख्य व्यवस्थापक",
            email = "admin.cctns@amethi.police.gov.in",
            isFavorite = true,
            notes = "स्थायी मुख्य व्यवस्थापक (Master Admin 9807583096) - समस्त डेटा प्रबंधन व नियंत्रण का पूर्ण अधिकार",
            createdAt = System.currentTimeMillis()
        )
    }

    fun getInitialPersonnel(): List<PolicePersonnelEntity> {
        val list = mutableListOf<PolicePersonnelEntity>()

        fun add(
            name: String,
            designation: String,
            category: String,
            thanaName: String,
            mobile: String,
            cug: String? = null,
            dutyLocation: String? = null,
            shiftTiming: String? = null,
            email: String? = null,
            isFavorite: Boolean = false,
            notes: String? = null
        ) {
            list.add(
                PolicePersonnelEntity(
                    name = name,
                    designation = designation,
                    category = category,
                    thanaName = thanaName,
                    encryptedMobileNumber = CryptoManager.encrypt(mobile),
                    encryptedCugNumber = if (cug != null) CryptoManager.encrypt(cug) else null,
                    dutyLocation = dutyLocation,
                    shiftTiming = shiftTiming,
                    email = email,
                    isFavorite = isFavorite,
                    encryptedNotes = if (notes != null) CryptoManager.encrypt(notes) else null
                )
            )
        }

        // ==========================================
        // जिला अमेठी (Amethi) - समस्त थाने एवं CCTNS विंग
        // ==========================================

        // 0. मुख्य व्यवस्थापक (Chief Master Admin - 9807583096)
        add(
            name = "मुख्य व्यवस्थापक (एडमिन)",
            designation = "सुपर एडमिन (जिला CCTNS प्रभारी, अमेठी)",
            category = "CONTROL_ROOM",
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            mobile = "9807583096",
            cug = "9454400247",
            dutyLocation = "कंट्रोल रूम व एडमिन डेस्क, गौरीगंज",
            shiftTiming = "24x7 मुख्य व्यवस्थापक",
            email = "admin.cctns@amethi.police.gov.in",
            isFavorite = true,
            notes = "स्थायी मुख्य व्यवस्थापक (Master Admin 9807583096) - समस्त डेटा प्रबंधन व नियंत्रण का पूर्ण अधिकार"
        )

        // 1. जिला सीसीटीएनएस सेल (पुलिस कार्यालय / एसपी ऑफिस गौरीगंज, अमेठी)
        add(
            name = "निरीक्षक विजय प्रताप सिंह",
            designation = "जिला नोडल अधिकारी (CCTNS एवं तकनीकी विंग)",
            category = "CONTROL_ROOM",
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            mobile = "9454400248",
            cug = "9454400247",
            dutyLocation = "कमांड सेंटर, पुलिस लाइन गौरीगंज",
            shiftTiming = "24x7 CCTNS नोडल प्रभारी",
            email = "cctns-amethi@up.gov.in",
            isFavorite = true,
            notes = "जनपद अमेठी के समस्त 15 थानों की CCTNS प्रविष्टि, ऑनलाइन एफआईआर व तकनीकी समीक्षा"
        )
        add(
            name = "हे.कां. रमेश चंद्र यादव",
            designation = "मुख्य CCTNS ऑपरेटर (सर्वर एवं वीपीएन प्रभारी)",
            category = "CCTNS_OPERATOR",
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            mobile = "9838012345",
            cug = "9454400249",
            dutyLocation = "CCTNS सर्वर रूम, गौरीगंज",
            shiftTiming = "प्रातः 08:00 से सायं 04:00",
            isFavorite = true,
            notes = "स्टेट डेटा सेंटर (SDC) सिंक, थानों की कनेक्टिविटी व ऑनलाइन जीडी निगरानी"
        )
        add(
            name = "श्री अनुज सक्सेना",
            designation = "CCTNS तकनीकी इंजीनियर (हार्डवेयर व नेटवर्क)",
            category = "CCTNS_STAFF",
            thanaName = "जिला सीसीटीएनएस सेल (एसपी ऑफिस)",
            mobile = "9795123456",
            dutyLocation = "तकनीकी सेल, गौरीगंज",
            shiftTiming = "ऑन-कॉल आपातकालीन सेवा",
            isFavorite = true,
            notes = "समस्त थानों के कम्प्यूटर, प्रिंटर, स्कैनर व डोंगल मेंटेनेंस"
        )

        // 2. थाना गौरीगंज (जिला मुख्यालय, अमेठी)
        add(
            name = "निरीक्षक राहुल कुमार",
            designation = "प्रभारी निरीक्षक (SHO गौरीगंज)",
            category = "SHO_INCHARGE",
            thanaName = "थाना गौरीगंज",
            mobile = "9454403751",
            cug = "9454403751",
            dutyLocation = "थाना गौरीगंज कार्यालय",
            shiftTiming = "24x7 सामान्य",
            isFavorite = true,
            notes = "जिला मुख्यालय थाना विधि-व्यवस्था व CCTNS पर्यवेक्षण"
        )
        add(
            name = "का. कुलदीप सिंह",
            designation = "कम्प्यूटर ऑपरेटर (CCTNS ऑपरेटर)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना गौरीगंज",
            mobile = "9415011223",
            dutyLocation = "CCTNS कक्ष, थाना गौरीगंज",
            shiftTiming = "प्रातः 08:00 से रात्रि 08:00",
            isFavorite = true,
            notes = "दैनिक जीडी, ऑनलाइन एफआईआर, चार्जशीट प्रविष्टि व केस डायरी इंडेक्स"
        )
        add(
            name = "हे.कां. दीपक वर्मा",
            designation = "CCTNS सहायक ऑपरेटर व मुंशी",
            category = "CCTNS_STAFF",
            thanaName = "थाना गौरीगंज",
            mobile = "9621334455",
            dutyLocation = "कम्प्यूटर हेल्पडेस्क",
            shiftTiming = "रोटेशन शिफ्ट",
            notes = "वारंट व समन प्रविष्टि, सीसीटीएनएस अभिलेख संधारण"
        )

        // 3. थाना अमेठी
        add(
            name = "निरीक्षक अरुण कुमार द्विवेदी",
            designation = "प्रभारी निरीक्षक (SHO अमेठी)",
            category = "SHO_INCHARGE",
            thanaName = "थाना अमेठी",
            mobile = "9454403752",
            cug = "9454403752",
            dutyLocation = "थाना अमेठी कार्यालय",
            shiftTiming = "24x7 सामान्य",
            isFavorite = true,
            notes = "थाना अमेठी क्षेत्राधिकार कानून व्यवस्था"
        )
        add(
            name = "का. विपिन यादव",
            designation = "कम्प्यूटर ऑपरेटर (CCTNS ऑपरेटर)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना अमेठी",
            mobile = "9918234567",
            dutyLocation = "CCTNS कक्ष, थाना अमेठी",
            shiftTiming = "प्रातः 09:00 से सायं 09:00",
            isFavorite = true,
            notes = "एफआईआर, जीडी प्रविष्टि, गिरफ्तारी मेमो व चार्जशीट अपलोड"
        )
        add(
            name = "उ.नि. सत्येंद्र प्रताप",
            designation = "वरिष्ठ उप-निरीक्षक (SSI अमेठी)",
            category = "OFFICER",
            thanaName = "थाना अमेठी",
            mobile = "9454403753",
            dutyLocation = "थाना अमेठी",
            shiftTiming = "क्षेत्र गश्त व विवेचना",
            notes = "विवेचनाओं की ऑनलाइन केस डायरी समीक्षा"
        )

        // 4. थाना मुसाफिरखाना
        add(
            name = "निरीक्षक विनोद कुमार सिंह",
            designation = "प्रभारी निरीक्षक (SHO मुसाफिरखाना)",
            category = "SHO_INCHARGE",
            thanaName = "थाना मुसाफिरखाना",
            mobile = "9454403754",
            cug = "9454403754",
            dutyLocation = "थाना मुसाफिरखाना कार्यालय",
            shiftTiming = "24x7 सामान्य",
            isFavorite = true,
            notes = "मुसाफिरखाना सर्किल अपराध नियंत्रण"
        )
        add(
            name = "का. पंकज तिवारी",
            designation = "CCTNS ऑपरेटर (मुसाफिरखाना)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना मुसाफिरखाना",
            mobile = "9839445566",
            dutyLocation = "CCTNS कम्प्यूटर डेस्क",
            shiftTiming = "प्रातः 08:00 से रात्रि 08:00",
            notes = "ऑनलाइन रिपोर्टिंग व CCTNS पोर्टल डेटा सिंक"
        )

        // 5. थाना जगदीशपुर
        add(
            name = "निरीक्षक राकेश कुमार सिंह",
            designation = "प्रभारी निरीक्षक (SHO जगदीशपुर)",
            category = "SHO_INCHARGE",
            thanaName = "थाना जगदीशपुर",
            mobile = "9454403755",
            cug = "9454403755",
            dutyLocation = "थाना जगदीशपुर",
            shiftTiming = "24x7 सामान्य",
            isFavorite = true,
            notes = "औद्योगिक क्षेत्र जगदीशपुर सुरक्षा व विधि व्यवस्था"
        )
        add(
            name = "का. अमित कुमार मौर्य",
            designation = "CCTNS कम्प्यूटर ऑपरेटर (जगदीशपुर)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना जगदीशपुर",
            mobile = "9559876543",
            dutyLocation = "CCTNS डेस्क, कमरा नं 1",
            shiftTiming = "प्रातः 09:00 से सायं 09:00",
            notes = "औद्योगिक क्षेत्र मामले व नियमित CCTNS प्रविष्टि"
        )

        // 6. थाना मोहनगंज
        add(
            name = "उ.नि. ज्ञानेंद्र सिंह",
            designation = "थानाध्यक्ष (SO मोहनगंज)",
            category = "SHO_INCHARGE",
            thanaName = "थाना मोहनगंज",
            mobile = "9454403756",
            cug = "9454403756",
            dutyLocation = "थाना मोहनगंज",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना मोहनगंज क्षेत्र कानून व्यवस्था"
        )
        add(
            name = "का. सुनील कुमार",
            designation = "CCTNS ऑपरेटर (मोहनगंज)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना मोहनगंज",
            mobile = "9415778899",
            dutyLocation = "CCTNS कक्ष",
            shiftTiming = "दिन की ड्यूटी",
            notes = "दैनिक जीडी, ऑनलाइन एफआईआर प्रविष्टि"
        )

        // 7. थाना जामो
        add(
            name = "उ.नि. विनोद यादव",
            designation = "थानाध्यक्ष (SO जामो)",
            category = "SHO_INCHARGE",
            thanaName = "थाना जामो",
            mobile = "9454403757",
            cug = "9454403757",
            dutyLocation = "थाना जामो",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना जामो विधि व्यवस्था"
        )
        add(
            name = "का. शशिकांत राय",
            designation = "CCTNS ऑपरेटर (जामो)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना जामो",
            mobile = "9616009988",
            dutyLocation = "CCTNS कम्प्यूटर रूम",
            shiftTiming = "प्रातः 08:00 से रात्रि 08:00",
            notes = "एफआईआर, चार्जशीट व ऑनलाइन जीडी"
        )

        // 8. थाना कमरौली
        add(
            name = "निरीक्षक धीरेंद्र यादव",
            designation = "प्रभारी निरीक्षक (SHO कमरौली)",
            category = "SHO_INCHARGE",
            thanaName = "थाना कमरौली",
            mobile = "9454403758",
            cug = "9454403758",
            dutyLocation = "थाना कमरौली",
            shiftTiming = "24x7 सामान्य",
            notes = "हाईवे व औद्योगिक बेल्ट सुरक्षा"
        )
        add(
            name = "का. मनीष पाल",
            designation = "CCTNS ऑपरेटर (कमरौली)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना कमरौली",
            mobile = "9721443322",
            dutyLocation = "CCTNS कक्ष",
            shiftTiming = "सामान्य ड्यूटी",
            notes = "दैनिक अभिलेख व CCTNS पोर्टल डेटा"
        )

        // 9. थाना मुंशीगंज
        add(
            name = "उ.नि. शिवाकांत त्रिपाठी",
            designation = "थानाध्यक्ष (SO मुंशीगंज)",
            category = "SHO_INCHARGE",
            thanaName = "थाना मुंशीगंज",
            mobile = "9454403759",
            cug = "9454403759",
            dutyLocation = "थाना मुंशीगंज",
            shiftTiming = "24x7 सामान्य",
            notes = "एचएएल व मुंशीगंज क्षेत्र"
        )
        add(
            name = "का. सूरज भान",
            designation = "CCTNS ऑपरेटर (मुंशीगंज)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना मुंशीगंज",
            mobile = "9935443322",
            dutyLocation = "CCTNS विंग",
            shiftTiming = "प्रातः 09:00 से सायं 06:00",
            notes = "सत्यापन रिपोर्ट व जीडी प्रविष्टि"
        )

        // 10. थाना पीपरपुर
        add(
            name = "उ.नि. रामराज कुशवाहा",
            designation = "थानाध्यक्ष (SO पीपरपुर)",
            category = "SHO_INCHARGE",
            thanaName = "थाना पीपरपुर",
            mobile = "9454403760",
            cug = "9454403760",
            dutyLocation = "थाना पीपरपुर",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना पीपरपुर सीमावर्ती क्षेत्र"
        )
        add(
            name = "का. अभिषेक सिंह",
            designation = "CCTNS ऑपरेटर (पीपरपुर)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना पीपरपुर",
            mobile = "9839112233",
            dutyLocation = "CCTNS डेस्क",
            shiftTiming = "रोटेशन शिफ्ट",
            notes = "ऑनलाइन रिपोर्टिंग व एफआईआर"
        )

        // 11. थाना संग्रामपुर
        add(
            name = "उ.नि. अंगद सिंह",
            designation = "थानाध्यक्ष (SO संग्रामपुर)",
            category = "SHO_INCHARGE",
            thanaName = "थाना संग्रामपुर",
            mobile = "9454403761",
            cug = "9454403761",
            dutyLocation = "थाना संग्रामपुर",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना संग्रामपुर क्षेत्र विधि व्यवस्था"
        )
        add(
            name = "का. राहुल कुमार वर्मा",
            designation = "CCTNS ऑपरेटर (संग्रामपुर)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना संग्रामपुर",
            mobile = "9554112244",
            dutyLocation = "CCTNS कक्ष",
            shiftTiming = "दिन की शिफ्ट",
            notes = "केस डिस्पोजल व ऑनलाइन जीडी"
        )

        // 12. थाना बाजार शुकुल (शुकुलबाजार)
        add(
            name = "उ.नि. दयाशंकर मिश्रा",
            designation = "थानाध्यक्ष (SO बाजार शुकुल)",
            category = "SHO_INCHARGE",
            thanaName = "थाना बाजार शुकुल",
            mobile = "9454403762",
            cug = "9454403762",
            dutyLocation = "थाना बाजार शुकुल",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना बाजार शुकुल कानून व्यवस्था"
        )
        add(
            name = "का. अवधेश चौरसिया",
            designation = "CCTNS ऑपरेटर (बाजार शुकुल)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना बाजार शुकुल",
            mobile = "9450112233",
            dutyLocation = "CCTNS डेस्क",
            shiftTiming = "सामान्य ड्यूटी",
            notes = "चार्जशीट प्रविष्टि व ऑनलाइन एफआईआर"
        )

        // 13. थाना शिवरतनगंज
        add(
            name = "उ.नि. अमरेंद्र सिंह",
            designation = "थानाध्यक्ष (SO शिवरतनगंज)",
            category = "SHO_INCHARGE",
            thanaName = "थाना शिवरतनगंज",
            mobile = "9454403763",
            cug = "9454403763",
            dutyLocation = "थाना शिवरतनगंज",
            shiftTiming = "24x7 सामान्य",
            notes = "थाना शिवरतनगंज क्षेत्र"
        )
        add(
            name = "का. विकास पटेल",
            designation = "CCTNS ऑपरेटर (शिवरतनगंज)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना शिवरतनगंज",
            mobile = "9889223344",
            dutyLocation = "CCTNS डेस्क",
            shiftTiming = "प्रातः 08:00 से रात्रि 08:00",
            notes = "दैनिक जीडी व समन-वारंट प्रविष्टि"
        )

        // 14. थाना फुरसतगंज
        add(
            name = "उ.नि. प्रवीण कुमार गौतम",
            designation = "थानाध्यक्ष (SO फुरसतगंज)",
            category = "SHO_INCHARGE",
            thanaName = "थाना फुरसतगंज",
            mobile = "9454403764",
            cug = "9454403764",
            dutyLocation = "थाना फुरसतगंज",
            shiftTiming = "24x7 सामान्य",
            notes = "एयरफील्ड व फुरसतगंज क्षेत्र"
        )
        add(
            name = "का. प्रदीप यादव",
            designation = "CCTNS ऑपरेटर (फुरसतगंज)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना फुरसतगंज",
            mobile = "9794334455",
            dutyLocation = "CCTNS कम्प्यूटर डेस्क",
            shiftTiming = "दिन की ड्यूटी",
            notes = "ऑनलाइन एफआईआर व सत्यापन कार्य"
        )

        // 15. थाना जायस
        add(
            name = "निरीक्षक अमर सिंह",
            designation = "प्रभारी निरीक्षक (SHO जायस)",
            category = "SHO_INCHARGE",
            thanaName = "थाना जायस",
            mobile = "9454403765",
            cug = "9454403765",
            dutyLocation = "थाना जायस",
            shiftTiming = "24x7 सामान्य",
            notes = "कस्बा जायस व आसपास का क्षेत्र"
        )
        add(
            name = "का. अंकित शुक्ला",
            designation = "CCTNS ऑपरेटर (जायस)",
            category = "CCTNS_OPERATOR",
            thanaName = "थाना जायस",
            mobile = "9628556677",
            dutyLocation = "CCTNS कक्ष",
            shiftTiming = "प्रातः 09:00 से सायं 09:00",
            notes = "केस डायरी व CCTNS पोर्टल रिकॉर्ड"
        )

        // 16. थाना महिला (गौरीगंज, अमेठी)
        add(
            name = "उ.नि. रीता चौधरी",
            designation = "थाना प्रभारी (महिला थाना अमेठी)",
            category = "SHO_INCHARGE",
            thanaName = "महिला थाना (अमेठी)",
            mobile = "9454403766",
            cug = "9454403766",
            dutyLocation = "महिला थाना, गौरीगंज",
            shiftTiming = "24x7 सामान्य",
            isFavorite = true,
            notes = "महिला सुरक्षा हेल्पडेस्क व परामर्श केंद्र"
        )
        add(
            name = "म.कां. नीलम सिंह",
            designation = "महिला CCTNS ऑपरेटर व हेल्पडेस्क",
            category = "CCTNS_OPERATOR",
            thanaName = "महिला थाना (अमेठी)",
            mobile = "9454404502",
            dutyLocation = "कम्प्यूटर कक्ष, महिला थाना",
            shiftTiming = "प्रातः 09:00 से सायं 06:00",
            notes = "महिला हेल्पडेस्क शिकायतें व CCTNS पोर्टल ऑनलाइन फीडिंग"
        )

        // 17. साइबर क्राइम थाना / सेल (अमेठी)
        add(
            name = "निरीक्षक आनंद प्रकाश",
            designation = "प्रभारी साइबर क्राइम सेल (अमेठी)",
            category = "OFFICER",
            thanaName = "साइबर क्राइम सेल (अमेठी)",
            mobile = "9454405601",
            cug = "9454405600",
            dutyLocation = "पुलिस लाइन गौरीगंज",
            shiftTiming = "24x7 तकनीकी जांच",
            isFavorite = true,
            notes = "डिजिटल फॉरेन्सिक, NCRP व वित्तीय साइबर फ्रॉड रिकवरी"
        )
        add(
            name = "का. गौरव बाजपेयी",
            designation = "CCTNS व साइबर तकनीकी विश्लेषक",
            category = "CCTNS_STAFF",
            thanaName = "साइबर क्राइम सेल (अमेठी)",
            mobile = "9721443322",
            dutyLocation = "साइबर लैब, गौरीगंज",
            shiftTiming = "सामान्य ड्यूटी",
            notes = "आईसीजीएस (ICJS) व CCTNS डिजिटल समन्वय"
        )

        // 18. यातायात पुलिस लाइन (अमेठी)
        add(
            name = "यातायात निरीक्षक महेंद्र पाल",
            designation = "प्रभारी यातायात (अमेठी)",
            category = "OFFICER",
            thanaName = "यातायात पुलिस (अमेठी)",
            mobile = "9454406701",
            cug = "9454406700",
            dutyLocation = "ट्रैफिक पुलिस लाइन, गौरीगंज",
            shiftTiming = "यातायात प्रबंधन",
            notes = "ई-चालान एवं CCTNS वाहन डाटा लिंकेज"
        )

        return list
    }
}
