package com.nikhiljain.coroutinepermissions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface CoroutinePermissions {
    suspend fun ensureMultiple(permissions: Array<String>): Pair<Boolean, List<String>>
    suspend fun ensureSingle(
        permission: String,
        model: PermissionDialogModel = PermissionDialogModel()
    ): Boolean

    companion object {
        fun of(activity: AppCompatActivity): CoroutinePermissions =
            CoroutinePermissionsImpl(activity = activity)

        fun of(fragment: Fragment): CoroutinePermissions =
            CoroutinePermissionsImpl(fragment = fragment)
    }
}