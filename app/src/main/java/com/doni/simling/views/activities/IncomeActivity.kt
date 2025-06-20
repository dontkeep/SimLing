package com.doni.simling.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.databinding.ActivityIncomeBinding
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.DateHelper.formatDateTime
import com.doni.simling.helper.DateHelper.getCurrentMonth
import com.doni.simling.helper.DateHelper.getCurrentYear
import com.doni.simling.helper.MonthYearPickerDialog
import com.doni.simling.helper.Resource
import com.doni.simling.helper.formatCurrency
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_ADMIN
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_WARGA
import com.doni.simling.viewmodels.FundViewModel
import com.doni.simling.views.adapters.IncomeAdapter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class IncomeActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

    private lateinit var binding: ActivityIncomeBinding
    private val fundViewModel: FundViewModel by viewModels()
    private lateinit var fundAdapter: IncomeAdapter

    private var selectedMonth: String = getCurrentMonth()
    private var selectedYear: String = getCurrentYear()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showExportOptionsDialog()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Jika di Android 11+ dan izin ditolak, jelaskan pentingnya izin
                AlertDialog.Builder(this)
                    .setTitle("Izin Diperlukan")
                    .setMessage("Aplikasi memerlukan izin penyimpanan untuk membuat laporan. Silakan berikan izin melalui pengaturan.")
                    .setPositiveButton("Buka Pengaturan") { _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                            startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:$packageName")
                            }
                            startActivity(intent)
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupRoleSpecificUI()

        binding.icCalendar.setOnClickListener {
            showMonthYearPickerDialog()
        }

        binding.addIncomeBtn.setOnClickListener {
            val intent = Intent(this, AddIncomeActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.printBtn.setOnClickListener {
            checkStoragePermission()
        }

        observeViewModel(getCurrentMonth(), getCurrentYear())
    }

    private fun setupRecyclerView() {
        fundAdapter = IncomeAdapter { incomeItem ->
            val intent = Intent(this, DetailIncomeActivity::class.java).apply {
                putExtra("FUND_ID", incomeItem.id)
            }
            startActivity(intent)
        }

        binding.rvFunds.apply {
            adapter = fundAdapter
            layoutManager = LinearLayoutManager(this@IncomeActivity)
        }
    }

    private fun observeViewModel(month: String, year: String) {
        lifecycleScope.launch {
            fundViewModel.getAllIncomePaging(month, year).collect {
                fundAdapter.submitData(it)
            }
        }
    }

    private fun setupRoleSpecificUI() {
        val role = roleManager.getRole()
        when (role) {
            ROLE_ADMIN -> {
                binding.addIncomeBtn.visibility = View.GONE
                binding.printBtn.visibility = View.VISIBLE
            }
            ROLE_WARGA -> {
                binding.addIncomeBtn.visibility = View.VISIBLE
                binding.tvRole.visibility = View.GONE
                binding.printBtn.visibility = View.GONE
            }
        }
    }

    private fun showMonthYearPickerDialog() {
        val dialog = MonthYearPickerDialog(this) { month, year ->
            selectedMonth = String.format("%02d", month + 1)
            selectedYear = year.toString()

            observeViewModel(selectedMonth, selectedYear)
        }

        dialog.show()
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Untuk Android 11 (API 30) ke atas
            if (Environment.isExternalStorageManager()) {
                showExportOptionsDialog()
            } else {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showExportOptionsDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showExportOptionsDialog() {
        val options = arrayOf("Export as PDF", "Export as Excel")
        AlertDialog.Builder(this)
            .setTitle("Export Report")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> exportAsPdf()
                    1 -> exportAsExcel()
                }
            }
            .show()
    }

    private fun exportAsPdf() {
        lifecycleScope.launch {
            val fileName = "Laporan_Pemasukan_Kas_${selectedMonth}_${selectedYear}-${DateHelper.getCurrentDateTime()}.pdf"
            val pdfFile = File(getExternalFilesDir(null), fileName)

            fundViewModel.getAllIncomeForExport(selectedMonth, selectedYear).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val fundsList = resource.data ?: emptyList()
                        val totalIncome = fundsList.sumOf { it.amount ?: 0 } // Hitung total pemasukan

                        try {
                            PdfWriter(pdfFile).use { writer ->
                                val pdf = PdfDocument(writer)
                                Document(pdf).use { document ->
                                    // Judul Laporan
                                    document.add(
                                        Paragraph("Laporan Pemasukan Kas Bulanan")
                                            .setTextAlignment(TextAlignment.CENTER)
                                            .setFontSize(18f)
                                    )

                                    // Periode Bulan Tahun
                                    document.add(
                                        Paragraph("Bulan: $selectedMonth-$selectedYear")
                                            .setTextAlignment(TextAlignment.CENTER)
                                            .setFontSize(14f)
                                    )

                                    // Create table dengan 5 kolom
                                    val table = Table(5)
                                    table.setWidth(UnitValue.createPercentValue(100f))

                                    // Add headers
                                    table.addHeaderCell("Tanggal")
                                    table.addHeaderCell("Nama")
                                    table.addHeaderCell("Blok")
                                    table.addHeaderCell("Status")
                                    table.addHeaderCell("Jumlah")

                                    // Add data
                                    fundsList.forEach { item ->
                                        table.addCell(formatDateTime(item.createdAt ?: ""))
                                        table.addCell(item.userName ?: "")
                                        table.addCell(item.block ?: "")
                                        table.addCell(item.status ?: "")
                                        table.addCell(formatCurrency(item.amount ?: 0))
                                    }

                                    document.add(table)

                                    // Tambahkan total pemasukan
                                    document.add(
                                        Paragraph("Total Pemasukan: ${formatCurrency(totalIncome)}")
                                            .setTextAlignment(TextAlignment.RIGHT)
                                            .setFontSize(14f)
                                            .setBold()
                                            .setMarginTop(10f)
                                    )
                                }
                            }
                            showExportSuccessDialog(pdfFile)
                        } catch (e: Exception) {
                            Toast.makeText(this@IncomeActivity, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@IncomeActivity, "Gagal mengambil data: ${resource.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        // Tampilkan loading indicator jika perlu
                    }
                }
            }
        }
    }

    private fun exportAsExcel() {
        lifecycleScope.launch {
            val fileName = "Laporan_Pemasukan_Kas_${selectedMonth}_${selectedYear}-${DateHelper.getCurrentDateTime()}.csv"
            val csvFile = File(getExternalFilesDir(null), fileName)

            fundViewModel.getAllIncomeForExport(selectedMonth, selectedYear).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val fundsList = resource.data ?: emptyList()
                        val totalIncome = fundsList.sumOf { it.amount ?: 0 } // Hitung total pemasukan

                        try {
                            val csvContent = StringBuilder()

                            // Add headers
                            csvContent.append("Tanggal,Nama,Blok,Status,Jumlah\n")

                            // Add data
                            fundsList.forEach { item ->
                                csvContent.append("\"${formatDateTime(item.createdAt ?: "")}\",")
                                csvContent.append("\"${item.userName ?: ""}\",")
                                csvContent.append("\"${item.block ?: ""}\",")
                                csvContent.append("\"${item.status ?: ""}\",")
                                csvContent.append("\"${item.amount ?: 0}\"\n")
                            }

                            // Tambahkan baris total
                            csvContent.append("\n")
                            csvContent.append("\"\",\"\",\"\",\"Total Pemasukan\",\"${formatCurrency(totalIncome)}\"")

                            // Write to file
                            FileOutputStream(csvFile).use { fos ->
                                fos.write(csvContent.toString().toByteArray(Charsets.UTF_8))
                            }

                            showExportSuccessDialog(csvFile)
                        } catch (e: Exception) {
                            Toast.makeText(this@IncomeActivity, "Gagal membuat CSV: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@IncomeActivity, "Gagal mengambil data: ${resource.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        // Tampilkan loading indicator jika perlu
                    }
                }
            }
        }
    }

    private fun showExportSuccessDialog(file: File) {
        val builder = AlertDialog.Builder(this)
            .setTitle("Export Berhasil")
            .setMessage("Laporan berhasil disimpan di: ${file.absolutePath}")
            .setPositiveButton("Buka") { _, _ ->
                openExportedFile(file)
            }
            .setNegativeButton("OK", null)

        // Add share button for alternative way to access the file
        builder.setNeutralButton("Bagikan") { _, _ ->
            shareExportedFile(file)
        }

        builder.show()
    }

    private fun openExportedFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Create a chooser to let user select which app to use
            val chooser = Intent.createChooser(intent, "Buka dengan...")

            // Verify that there's at least one app that can handle the intent
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "Tidak ada aplikasi yang dapat membuka file ini", Toast.LENGTH_SHORT).show()
                shareExportedFile(file) // Fallback to sharing if no app can open directly
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka file: ${e.message}", Toast.LENGTH_SHORT).show()
            shareExportedFile(file) // Fallback to sharing
        }
    }

    private fun shareExportedFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = getMimeType(file)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Bagikan via"))
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membagikan file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(file: File): String {
        return when {
            file.name.endsWith(".pdf") -> "application/pdf"
            file.name.endsWith(".csv") -> "text/csv"
            else -> "*/*"
        }
    }
}