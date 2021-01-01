package com.nikhiljain.sample

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nikhiljain.coroutinepermissions.CoroutinePermissions
import kotlinx.coroutines.*

class SampleActivity : AppCompatActivity() {
    private lateinit var coroutinePermissions : CoroutinePermissions
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutinePermissions = CoroutinePermissions.of(this)

//        askPermissions()
    }

    private fun askPermissions() {
        coroutineScope.launch {
            val permissions = arrayOf(
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            try {
                val isGranted = coroutinePermissions.ensureSingle(permission)

                if (isGranted)
                    Toast.makeText(
                        this@SampleActivity,
                        "Permission is granted",
                        Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(
                        this@SampleActivity,
                        "Permission is denied, you can't use this feature.",
                        Toast.LENGTH_SHORT
                    ).show()
            } catch (exception: Exception) {
                Toast.makeText(
                    this@SampleActivity,
                    exception.localizedMessage ?: "Something went wrong!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val (isGranted, missingPermissions) =
                coroutinePermissions
                    .ensureMultiple(permissions)
            if (isGranted)
                Toast.makeText(
                    this@SampleActivity,
                    "Permission is granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            else
                Toast.makeText(
                    this@SampleActivity,
                    "$missingPermissions permissions are not granted, Please enable it " +
                            "in settings to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}