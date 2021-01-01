package com.nikhiljain.coroutinepermissions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface CoroutinePermissions {
    suspend fun ensureMultiple(permissions: Array<String>): Pair<Boolean, List<String>>
    suspend fun ensureSingle(
        permission: String,
        model: PermissionDialogModel
    ): Boolean

    companion object {
        fun of(activity: FragmentActivity): CoroutinePermissions =
            CoroutinePermissionsImpl(activity = activity)

        fun of(fragment: Fragment): CoroutinePermissions =
            CoroutinePermissionsImpl(fragment = fragment)
    }
}