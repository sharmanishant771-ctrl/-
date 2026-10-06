package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.CryptoManager
import com.example.util.CsvHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CCTNS डायरेक्टरी", appName)
  }

  @Test
  fun `crypto encrypt and decrypt phone number`() {
    val phone = "9454400101"
    val encrypted = CryptoManager.encrypt(phone)
    val decrypted = CryptoManager.decrypt(encrypted)
    assertEquals(phone, decrypted)
  }

  @Test
  fun `csv parser parses template rows properly`() {
    val list = CsvHelper.parseCsv(CsvHelper.SAMPLE_CSV_TEMPLATE)
    assertTrue(list.isNotEmpty())
    val first = list.first()
    assertEquals("सुरेश कुमार", first.name)
    assertEquals("9838011223", first.mobileNumber)
  }
}
