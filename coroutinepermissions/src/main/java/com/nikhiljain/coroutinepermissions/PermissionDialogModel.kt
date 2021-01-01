package com.nikhiljain.coroutinepermissions

data class PermissionDialogModel(
    val title: String = "This app feature requires the runtime permission",
    val message: String = "In order to enjoy this feature, runtime permissions are " +
            "required otherwise you won't be able to enjoy this app feature",
    val positiveButtonText: String = "Allow",
    val negativeButtonText: String = "Cancel",
)