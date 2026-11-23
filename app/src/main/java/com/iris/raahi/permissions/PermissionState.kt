package com.iris.raahi.permissions

data class PermissionResult(val camera: Boolean, val microphone: Boolean, val location: Boolean)

object PermissionPolicy {
    fun summarize(result: PermissionResult): String = buildList {
        if (!result.camera) add("camera")
        if (!result.microphone) add("microphone")
        if (!result.location) add("location")
    }.let { missing ->
        if (missing.isEmpty()) "All requested permissions are granted."
        else "Permission needed for ${missing.joinToString()}. The feature remains unavailable until it is granted."
    }
}
