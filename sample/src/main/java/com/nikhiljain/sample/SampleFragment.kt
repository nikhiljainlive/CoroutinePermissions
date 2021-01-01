package com.nikhiljain.sample

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nikhiljain.coroutinepermissions.CoroutinePermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SampleFragment : Fragment(R.layout.fragment_sample) {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermissions()
    }

    private fun askPermissions() {
        val coroutinePermissions = CoroutinePermissions.of(this)

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
                        requireContext(),
                        "Permission is granted",
                        Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(
                        requireContext(),
                        "Permission is denied, you can't use this feature.",
                        Toast.LENGTH_SHORT
                    ).show()
            } catch (exception: Exception) {
                Toast.makeText(
                    requireContext(),
                    exception.localizedMessage ?: "Something went wrong!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val (isGranted, missingPermissions) = coroutinePermissions.ensureMultiple(permissions)
            if (isGranted)
                Toast.makeText(
                    requireContext(),
                    "Permission is granted",
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    requireContext(),
                    "$missingPermissions permissions are not granted, Please enable it " +
                            "in settings to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SampleFragment()
    }
}