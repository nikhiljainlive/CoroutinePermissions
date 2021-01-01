package com.nikhiljain.coroutinepermissions

data class PermissionDialogModel(
    val title: String,
    val message: String,
    val positiveButtonText: String = "Allow",
    val negativeButtonText: String = "Deny",
)