package com.tnrylmz.cihaz_bilgisi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.tnrylmz.cihaz_bilgisi.adapter.DeviceInfoCategoryAdapter
import com.tnrylmz.cihaz_bilgisi.utils.DeviceInfoCollector

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceInfoCategoryAdapter
    private lateinit var deviceInfoCollector: DeviceInfoCollector
    
    private val PERMISSION_REQUEST_CODE = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupToolbar()
        setupRecyclerView()
        requestPermissions()
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }
    
    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        deviceInfoCollector = DeviceInfoCollector(this)
        loadDeviceInfo()
    }
    
    private fun loadDeviceInfo() {
        val categories = deviceInfoCollector.collectAllDeviceInfo()
        adapter = DeviceInfoCategoryAdapter(categories.toMutableList())
        recyclerView.adapter = adapter
    }
    
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var permissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false
                    break
                }
            }
            
            if (!permissionsGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
            
            // Reload device info after permission request
            loadDeviceInfo()
        }
    }
}