package com.nikhiljain.coroutinepermissions

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class CoroutinePermissionsImpl(
    private val activity: FragmentActivity? = null,
    private val fragment: Fragment? = null
) : CoroutinePermissions, LifecycleObserver {
    private var singlePermissionLauncher: ActivityResultLauncher<String>? = null
    private var singlePermissionResultCallback: ((Boolean) -> Unit)? = null

    private var multiplePermissionsLauncher: ActivityResultLauncher<Array<out String>>? = null
    private var multiplePermissionsResultCallback: ((Pair<Boolean, List<String>>) -> Unit)? = null

    init {
        if (activity == null && fragment == null)
            throw InstantiationError("Either activity or fragment should be passed")

        activity?.lifecycle?.let { checkProperLifecycle(it) }
        fragment?.lifecycle?.let { checkProperLifecycle(it) }
        fragment?.lifecycle?.addObserver(this)
        activity?.lifecycle?.addObserver(this)
    }

    private fun checkProperLifecycle(lifecycle: Lifecycle) {
        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            ("Coroutine Permissions object should be instantiated in CREATED lifecycle state")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onActivityCreated() {
        registerSinglePermissionResultCallback()
        registerMultiplePermissionsResultCallback()
    }

    private fun registerSinglePermissionResultCallback() {
        activity?.let {
            singlePermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted -> singlePermissionResultCallback?.invoke(isGranted) }
        }
        fragment?.let {
            singlePermissionLauncher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted -> singlePermissionResultCallback?.invoke(isGranted) }
        }
    }

    private fun registerMultiplePermissionsResultCallback() {
        var areAllGranted = true
        val missingPermissions = mutableListOf<String>()

        activity?.let {
            multiplePermissionsLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { map ->
                map.entries.forEach { (permission, isGranted) ->
                    if (!isGranted) {
                        missingPermissions.add(permission)
                        areAllGranted = isGranted
                    }
                }

                multiplePermissionsResultCallback?.invoke(areAllGranted to missingPermissions)
            }
        }

        fragment?.let {
            multiplePermissionsLauncher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { map ->
                map.entries.forEach { (permission, isGranted) ->
                    if (!isGranted) {
                        missingPermissions.add(permission)
                        areAllGranted = isGranted
                    }
                }

                multiplePermissionsResultCallback?.invoke(areAllGranted to missingPermissions)
            }
        }
    }

    override suspend fun ensureSingle(
        permission: String,
        model: PermissionDialogModel
    ): Boolean = suspendCoroutine { continuation ->
        if (isBelowMarshmallow()) {
            continuation.resume(true)
            return@suspendCoroutine
        }

        fun requestPermission() = singlePermissionLauncher?.launch(permission)

        try {
            when {
                isPermissionGranted(permission) -> continuation.resume(true)

                fragment?.shouldShowRequestPermissionRationale(permission) ?: false -> {
                    showPermissionDialog(model,
                        positiveButtonAction = {
                            requestPermission()
                        }, negativeButtonAction = {
                            continuation.resume(false)
                        })
                }

                activity?.shouldShowRequestPermissionRationale(permission) ?: false -> {
                    showPermissionDialog(model,
                        positiveButtonAction = {
                            requestPermission()
                        }, negativeButtonAction = {
                            continuation.resume(false)
                        })
                }

                else -> {
                    requestPermission()
                }
            }
            singlePermissionResultCallback = { permissionResult ->
                continuation.resume(permissionResult)
            }
        } catch (exception: Exception) {
            continuation.resumeWithException(exception)
        }
    }

    override suspend fun ensureMultiple(permissions: Array<String>): Pair<Boolean, List<String>> =
        suspendCoroutine { continuation ->
            if (isBelowMarshmallow()) {
                continuation.resume(true to emptyList())
                return@suspendCoroutine
            }

            try {
                multiplePermissionsLauncher?.launch(permissions)
                multiplePermissionsResultCallback = { permissionResult ->
                    continuation.resume(permissionResult)
                }
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }
        }

    private fun showPermissionDialog(
        model: PermissionDialogModel,
        positiveButtonAction: (() -> Unit),
        negativeButtonAction: (() -> Unit),
    ) {
        val context = activity ?: fragment?.context ?: return
        val dialog = AlertDialog.Builder(context)
            .setTitle(model.title)
            .setMessage(model.message)
            .setCancelable(false)
            .setPositiveButton(model.positiveButtonText) { _, _ ->
                positiveButtonAction()
            }.setNegativeButton(model.negativeButtonText) { _, _ ->
                negativeButtonAction()
            }.create()
        dialog.setOnShowListener {
            val positiveColor = ContextCompat.getColor(context, R.color.coroutine_permissions_button_color)
            val disabledColor = ContextCompat.getColor(context, R.color.coroutine_permissions_button_state_disabled)
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(disabledColor)
        }
        dialog.show()
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val context = activity ?: fragment?.context ?: return false
        return ContextCompat.checkSelfPermission(
            context,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isBelowMarshmallow() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
}