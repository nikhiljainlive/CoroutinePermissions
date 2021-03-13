[![](https://jitpack.io/v/nikhiljainlive/CoroutinePermissions.svg)](https://jitpack.io/#nikhiljainlive/CoroutinePermissions)

You can raise PR for any contribution and optimization. :slightly_smiling_face:

# CoroutinePermissions

An Android Library for handling runtime permissions with the help of Kotlin Coroutines.

## Usage

Create an instance of `CoroutinePermissions` :

In your Activity/Fragment Class in `onCreate` lifecycle method :

Kotlin
```kotlin
val coroutinePermissions = CoroutinePermissions.of(this)
```

Here in `CoroutinePermissions.of(this)`, **this** refers to either FragmentActivity or Fragment if being called in FragmentActivity or Fragment respectively.

**NOTE:** `CoroutinePermissions.of(this)` must be called in the `CREATED` lifecycle state of FragmentActivity or Fragment otherwise the call will throw an `kotlin.IllegalStateException: Coroutine Permissions object should be instantiated in CREATED lifecycle state`.

Request Single Permission : let's take an example of fine location permission -

Kotlin
```kotlin
val permissionDialogModel = PermissionDialogModel(
            title= "Location Permissions Required",
            message= "Location permissions are required to use this app feature",
            positiveButtonText= "Allow",
            negativeButtonText= "Deny"
        )

launch {
    // CoroutineScope
    
    val isGranted = coroutinePermissions.ensureSingle(Manifest.permission.ACCESS_FINE_LOCATION, permissionDialogModel)

    if (isGranted) {
        // do something when permission is granted
    } else {
       // do something when permission is denied
    }
}
```

Here, you launch the `ensureSingle` suspend function of `CoroutinePermissions` and pass the permission you want to request and an instance of model class.You need to pass this model class as the library internally manages the showing of rational dialog when required. 
So, you need to pass the `title`, `message`, `positiveButtonText` and `negativeButtonText` which will appear in the dialog. When the positiveButton is clicked then the permission is again requested and when negativeButton is clicked, the result returns false for the `ensureSingle` method call.

**Note** : Notice that you don't have to pass the request code with the method call as the library uses the new AndroidX Activity and Fragment Result APIs so it is managed ineternally.

You can customize the color for both `positiveButtonText` and `negativeButtonText` by overriding `coroutine_permissions_button_color` and `coroutine_permissions_button_state_disabled` in your **app** `colors.xml` resource file.

`res/values/colors.xml`
```xml
<resources>
    <color name="coroutine_permissions_button_color">your_color_for_positive_button</color>
    <color name="coroutine_permissions_button_state_disabled">your_color_for_negative_button</color>
</resources>
```

Request Multiple Permissions : let's request CAMERA, WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE -

Kotlin
```kotlin
launch {
    // CoroutineScope
    
    val permissions = arrayOf(
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    val (isGranted, deniedPermissions) = coroutinePermissions.ensureMultiple(permissions)
    if (isGranted) {
        // do something when permission is granted
    } else {
       // do something when permission is denied
    }
    
    // do something with denied permissions
}
```

**Note** : If all of the permissions are granted from the `ensureMultiple` method call then only `isGranted` becomes `true` otherwise `false`. The `deniedPermissions` array returns the permissions denied.

## Status

This library is still beta, so contributions are welcome.

## Benefits

- No need to manage the request code for the permissions. :tada:

- No callbacks. :tada:
